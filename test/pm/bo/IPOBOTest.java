package pm.bo;

import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.PMDBTestCase;
import pm.util.AppConst;
import pm.util.PMDate;
import pm.util.enumlist.IPOAction;
import pm.vo.*;

import java.util.List;

public class IPOBOTest extends PMDBTestCase {
    public IPOBOTest(String string) {
        super(string, "TestData.xml");
    }

    Account tradingAcc;
    Account portfolioAcc;

    protected void setUp() throws Exception {
        super.setUp();
        tradingAcc = DAOManager.getAccountDAO().getTradingAccList().get(0);
        portfolioAcc = DAOManager.getAccountDAO().getPorfolioList().get(0);
    }

    public void testDoActionForApply() throws Exception {
        float balance = DAOManager.fundTransactionDAO().balance(null, null);
        IPOVO ipovo = new IPOVO("IPOBOCODE1", new PMDate(1, 1, 2001), 135.05f, 1210.1f, 150200, portfolioAcc, tradingAcc);
        int initSize = DAOManager.getIPODAO().getIPOTransaction(portfolioAcc.getId(), tradingAcc.getId()).size();
        new IPOBO().doAction(IPOAction.Apply, ipovo);
        List<IPOVO> transList = DAOManager.getIPODAO().getIPOTransaction(portfolioAcc.getId(), tradingAcc.getId());
        assertEquals(initSize + 1, transList.size());
        assertTrue(transList.contains(ipovo));
        float newBalance = DAOManager.fundTransactionDAO().balance(null, null);
        assertEquals(balance - ipovo.getAppliedAmount(), newBalance);
    }

    public void testFundTransactionForApply() throws Exception {
        PMDate pmDate = new PMDate(1, 1, 2001);
        IPOVO ipovo = new IPOVO("IPOBOCODE1", pmDate, 135.05f, 1210.1f, 150200, portfolioAcc, tradingAcc);
        FundTransactionVO fund = new IPOBO().fundTransaction(IPOAction.Apply, ipovo);
        FundTransactionVO expected = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Debit, AppConst.FUND_TRANSACTION_REASON.IPOApply, pmDate, ipovo.getAppliedAmount(), (TradingAccountVO) tradingAcc, (PortfolioDetailsVO) portfolioAcc, ipovo.getIpoCode());
        assertEquals(expected, fund);
    }

    public void testFundTransactionForRefund() throws Exception {
        PMDate pmDate = new PMDate(1, 1, 2001);
        IPOVO ipovo = new IPOVO("IPOBOCODE1", pmDate, 135.05f, 1210.1f, 150200, portfolioAcc, tradingAcc);
        ipovo.setRefundAmount(1234);
        PMDate refundDate = new PMDate(1, 2, 2001);
        ipovo.setRefundedDate(refundDate);
        FundTransactionVO fund = new IPOBO().fundTransaction(IPOAction.Refund, ipovo);
        FundTransactionVO expected = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Credit, AppConst.FUND_TRANSACTION_REASON.IPORefund, refundDate, 1234f, (TradingAccountVO) tradingAcc, (PortfolioDetailsVO) portfolioAcc, ipovo.getIpoCode());
        assertEquals(expected, fund);
    }

    public void testDoActionForAllotment() throws Exception {
        String ipoCode = "IPOBOCODE2";
        IPOVO ipovo = new IPOVO(ipoCode, new PMDate(1, 1, 2001), 135.05f, 1210.1f, 150200, portfolioAcc, tradingAcc);
        IPOBO ipobo = new IPOBO();
        ipobo.doAction(IPOAction.Apply, ipovo);
        List<IPOVO> transList = DAOManager.getIPODAO().getIPOTransaction(portfolioAcc.getId(), tradingAcc.getId());
        int initSize = transList.size();
        ipovo = findTransactionFor(transList, ipoCode);
        float balance = DAOManager.fundTransactionDAO().balance(null, null);
        TransactionVO transactionVO = new TransactionVO(new PMDate(15, 1, 2001), "CODE2", AppConst.TRADINGTYPE.Buy, 15, 1000, 0, "PortfolioName", "TradingACCName", false);
        ipovo.setAllotedTransaction(transactionVO);
        ipobo.doAction(IPOAction.Allotment, ipovo);
        transList = DAOManager.getIPODAO().getIPOTransaction(portfolioAcc.getId(), tradingAcc.getId());
        assertEquals(initSize, transList.size());
        IPOVO ipovoActual = findTransactionFor(transList, ipoCode);
        assertEquals(ipovo, ipovoActual);
        TransactionVO actualTransactionVO = DAOManager.getTransactionDAO().getTransaction(ipovoActual.getAllotedTransaction().getId(), AppConst.TRADINGTYPE.Buy);
        assertEquals(transactionVO, actualTransactionVO);
        float newBalance = DAOManager.fundTransactionDAO().balance(null, null);
        assertEquals(balance, newBalance);

    }

    public void testDoActionForAllotmentToAddNewStockCode() throws Exception {
        String ipoCode = "IPOBOCODE2";
        IPOVO ipovo = new IPOVO(ipoCode, new PMDate(1, 1, 2001), 135.05f, 1210.1f, 150200, portfolioAcc, tradingAcc);
        IPOBO ipobo = new IPOBO();
        ipobo.doAction(IPOAction.Apply, ipovo);
        List<IPOVO> transList = DAOManager.getIPODAO().getIPOTransaction(portfolioAcc.getId(), tradingAcc.getId());
        int initSize = transList.size();
        ipovo = findTransactionFor(transList, ipoCode);
        TransactionVO transactionVO = new TransactionVO(new PMDate(15, 1, 2001), "IPOSTOCK", AppConst.TRADINGTYPE.Buy, 15, 1000, 0, "PortfolioName", "TradingACCName", false);
        ipovo.setAllotedTransaction(transactionVO);
        ipobo.doAction(IPOAction.Allotment, ipovo);
        transList = DAOManager.getIPODAO().getIPOTransaction(portfolioAcc.getId(), tradingAcc.getId());
        assertEquals(initSize, transList.size());
        IPOVO ipovoActual = findTransactionFor(transList, ipoCode);
        assertEquals(ipovo, ipovoActual);
        TransactionVO actualTransactionVO = DAOManager.getTransactionDAO().getTransaction(ipovoActual.getAllotedTransaction().getId(), AppConst.TRADINGTYPE.Buy);
        assertEquals(transactionVO, actualTransactionVO);
    }

    public void testDoActionForAllotmentSkipAddingTransactionForZeroQty() throws Exception {
        String ipoCode = "IPOBOCODE3";
        IPOVO ipovo = new IPOVO(ipoCode, new PMDate(1, 1, 2001), 135.05f, 1210.1f, 150200, portfolioAcc, tradingAcc);
        IPOBO ipobo = new IPOBO();
        ipobo.doAction(IPOAction.Apply, ipovo);
        List<IPOVO> transList = DAOManager.getIPODAO().getIPOTransaction(portfolioAcc.getId(), tradingAcc.getId());
        int initSize = transList.size();
        ipovo = findTransactionFor(transList, ipoCode);
        TransactionVO transactionVO = new TransactionVO(new PMDate(15, 1, 2001), "IPOSTOCK1", AppConst.TRADINGTYPE.Buy, 0, 1000, 0, "PortfolioName", "TradingACCName", false);
        ipovo.setAllotedTransaction(transactionVO);
        ipobo.doAction(IPOAction.Allotment, ipovo);
        transList = DAOManager.getIPODAO().getIPOTransaction(portfolioAcc.getId(), tradingAcc.getId());
        assertEquals(initSize, transList.size());
        IPOVO ipovoActual = findTransactionFor(transList, ipoCode);
        assertFalse(ipovoActual.isAlloted());
    }

    public void testDoActionForRefund() throws Exception {
        String ipoCode = "IPOBOCODE3";
        IPOVO ipovo = new IPOVO(ipoCode, new PMDate(1, 1, 2001), 135.05f, 1210.1f, 150200, portfolioAcc, tradingAcc);
        IPOBO ipobo = new IPOBO();
        ipobo.doAction(IPOAction.Apply, ipovo);
        List<IPOVO> transList = DAOManager.getIPODAO().getIPOTransaction(portfolioAcc.getId(), tradingAcc.getId());
        int initSize = transList.size();
        ipovo = findTransactionFor(transList, ipoCode);
        TransactionVO transactionVO = new TransactionVO(new PMDate(15, 1, 2001), "CODE2", AppConst.TRADINGTYPE.Buy, 15, 1000, 0, "PortfolioName", "TradingACCName", false);
        ipovo.setAllotedTransaction(transactionVO);
        ipobo.doAction(IPOAction.Allotment, ipovo);
        transList = DAOManager.getIPODAO().getIPOTransaction(portfolioAcc.getId(), tradingAcc.getId());
        assertEquals(initSize, transList.size());
        float balance = DAOManager.fundTransactionDAO().balance(null, null);

        IPOVO ipovoAlloted = findTransactionFor(transList, ipoCode);
        float refundAmount = 1234f;
        ipovoAlloted.setRefundAmount(refundAmount);
        ipovoAlloted.setRefundedDate(new PMDate(1, 2, 2001));
        ipobo.doAction(IPOAction.Refund, ipovoAlloted);
        transList = DAOManager.getIPODAO().getIPOTransaction(portfolioAcc.getId(), tradingAcc.getId());
        assertEquals(initSize, transList.size());
        IPOVO ipovoRefunded = findTransactionFor(transList, ipoCode);
        assertEquals(ipovoAlloted, ipovoRefunded);
        assertEquals(ipovoAlloted.getAllotedTransaction().getId(), ipovoRefunded.getAllotedTransaction().getId());
        float newBalance = DAOManager.fundTransactionDAO().balance(null, null);
        assertEquals(balance + refundAmount, newBalance);

    }


    private IPOVO findTransactionFor(List<IPOVO> transList, String ipoCode) {
        for (IPOVO ipovo : transList) {
            if (ipovo.getIpoCode().equals(ipoCode)) {
                return ipovo;
            }
        }
        return null;
    }
}
