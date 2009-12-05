package pm.bo;

import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.ITransactionDAO;
import pm.dao.ibatis.dao.PMDBTestCase;
import pm.util.AppConst;
import pm.util.ApplicationException;
import pm.util.Helper;
import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.*;

import java.util.List;
import java.util.Vector;

/**
 * TradingBO Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>08/14/2006</pre>
 */
public class TradingBOTest extends PMDBTestCase {

    private static final String portfolioName1 = "PortForTradingBO1";
    private static final String tradingAcName1 = "TradForTradingBO1";
    private static final String stockCode1 = "CODE10";
    private final PMDate buyDate1 = new PMDate(1, 1, 2000);
    private final float buyQty = 1000.5f;
    private final float buyPrice = 2012.05f;
    private final float buyBrokerage = 842.56f;


    public TradingBOTest(String name) {
        super(name, "TestData.xml");
    }

    public void testPerformBuy() throws Exception {
        TransactionVO transVO = doBuy(buyDate1, stockCode1, buyQty, buyPrice, buyBrokerage, portfolioName1, tradingAcName1, false);
        assertTrue(transVO.getId() != 0);
        DAOManager.getTransactionDAO().getTransactionList(null, null).contains(transVO);
    }

    public void testIsDuplicate() throws Exception {
        TransactionVO existingTransaction = new TransactionVO(new PMDate(1, 1, 2006), "CODE1", AppConst.TRADINGTYPE.Buy, 10f, 100f, 115f, "PortfolioName", "TradingACCName", false);
        TradingBO tradingBO = new TradingBO();
        assertTrue(tradingBO.isDuplicate(existingTransaction));
        TransactionVO newTransacction = new TransactionVO(new PMDate(1, 1, 2006), "CODE1", AppConst.TRADINGTYPE.Sell, 10f, 100f, 115f, "PortfolioName", "TradingACCName", false);
        assertFalse(tradingBO.isDuplicate(newTransacction));
    }

    public void testFundTransaction() {
        TransactionVO transVO = new TransactionVO(buyDate1, stockCode1, AppConst.TRADINGTYPE.Buy, buyQty, buyPrice, buyBrokerage, portfolioName1, tradingAcName1, false);
        FundTransactionVO fundTransactionVO = new TradingBO().fundTransaction(transVO);
        TradingAccountVO trading = DAOManager.getAccountDAO().tradingAcc(tradingAcName1);
        PortfolioDetailsVO portfolioDetailsVO = DAOManager.getAccountDAO().portfolio(portfolioName1);
        FundTransactionVO expected = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Debit, AppConst.FUND_TRANSACTION_REASON.StockBuy, buyDate1, transVO.getValue(), trading, portfolioDetailsVO, transVO.getAbsDetails());
        assertEquals(expected, fundTransactionVO);

        transVO = new TransactionVO(buyDate1, stockCode1, AppConst.TRADINGTYPE.Sell, buyQty, buyPrice, buyBrokerage, portfolioName1, tradingAcName1, false);
        fundTransactionVO = new TradingBO().fundTransaction(transVO);
        expected = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Credit, AppConst.FUND_TRANSACTION_REASON.StockSell, buyDate1, transVO.getValue(), trading, portfolioDetailsVO, transVO.getAbsDetails());
        assertEquals(expected, fundTransactionVO);

        transVO = new TransactionVO(buyDate1, stockCode1, AppConst.TRADINGTYPE.IPO, buyQty, buyPrice, buyBrokerage, portfolioName1, tradingAcName1, false);
        fundTransactionVO = new TradingBO().fundTransaction(transVO);
        expected = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Debit, AppConst.FUND_TRANSACTION_REASON.StockBuy, buyDate1, transVO.getValue(), trading, portfolioDetailsVO, transVO.getAbsDetails());
        assertEquals(expected, fundTransactionVO);
    }

    public void testDoBuyDoesFundTransaction() {
        float balance = DAOManager.fundTransactionDAO().balance(null, null);
        TransactionVO transVO = doBuy(buyDate1, stockCode1, buyQty, buyPrice, buyBrokerage, portfolioName1, tradingAcName1, false);
        float newBalance = DAOManager.fundTransactionDAO().balance(null, null);
        assertEquals(balance - transVO.getValue(), newBalance);
    }

    public void testDoSellDoesFundTransaction() {
        float balance = DAOManager.fundTransactionDAO().balance(null, null);
        TransactionVO transVO = doBuy(buyDate1, stockCode1, buyQty, buyPrice, buyBrokerage, portfolioName1, tradingAcName1, false);
        TransactionVO sellTrans = new TransactionVO(buyDate1, stockCode1, AppConst.TRADINGTYPE.Sell, buyQty, buyPrice + 100, buyBrokerage, portfolioName1, tradingAcName1, false);
        new TradingBO().doSell(sellTrans);
        float newBalance = DAOManager.fundTransactionDAO().balance(null, null);
        assertEquals(balance - transVO.getValue() + sellTrans.getValue(), newBalance);
    }

    public void testPerformSellToSellPartialFromFirstAvailable() throws Exception {
        setUp();
        PMDate buyDate2 = new PMDate(1, 1, 2000);
        String stockCode = "CODE11";
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;
        String portfolio = "PortForTradingBO2";
        String tradingAc = "TradForTradingBO2";
        doBuy(buyDate2, stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        PMDate buyDate1 = new PMDate(31, 12, 1999);
        doBuy(buyDate1, stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        float salePrice = 4012.05f;
        float saleBrok = 123.23f;
        float saleQty = 100.5f;
        PMDate saleDate = new PMDate(2, 1, 2000);
        TransactionVO transVO = new TransactionVO(saleDate, stockCode, AppConst.TRADINGTYPE.Sell, saleQty, salePrice, saleBrok, portfolio, tradingAc, false);
        TradingBO tradingBO = new TradingBO();
        tradingBO.doSell(transVO);
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        List<TradeVO> tradeDetails = dao.getCompletedTradeDetails(tradingAc, portfolio, null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(1, tradeDetails.size());
        TradeVO tradeVO = tradeDetails.get(0);
        assertEquals(buyDate1, tradeVO.getPurchaseDate());
        assertEquals(stockCode, tradeVO.getStockCode());
        assertEquals(saleQty, tradeVO.getQty());
        assertEquals(price, tradeVO.getPurchasePrice());
        assertEquals(Helper.getRoundedOffValue(brokerage / qty * saleQty + saleBrok, 2), Helper.getRoundedOffValue(tradeVO.getBrokerage(), 2));
        assertEquals(saleDate, tradeVO.getSaleDate());
        assertEquals(salePrice, tradeVO.getSalePrice());
        assertEquals(portfolio, tradeVO.getPortfolio());
        assertEquals(tradingAc, tradeVO.getTradingAc());
        List<TradeVO> holdingDetails = dao.getHoldingDetails(tradingAc, portfolio, stockCode, false);
        assertEquals(2, holdingDetails.size());
        tradeVO = holdingDetails.get(0);
        assertEquals(buyDate1, tradeVO.getPurchaseDate());
        assertEquals(stockCode, tradeVO.getStockCode());
        assertEquals(qty - saleQty, tradeVO.getQty());
        assertEquals(price, tradeVO.getPurchasePrice());
        assertEquals(Helper.getRoundedOffValue(brokerage / qty * (qty - saleQty), 2), Helper.getRoundedOffValue(tradeVO.getBrokerage(), 2));
        assertTrue(tradeVO.isHolding());
        tradeVO = holdingDetails.get(1);
        assertEquals(buyDate2, tradeVO.getPurchaseDate());
        assertEquals(stockCode, tradeVO.getStockCode());
        assertEquals(qty, tradeVO.getQty());
        assertEquals(price, tradeVO.getPurchasePrice());
        assertEquals(brokerage, tradeVO.getBrokerage());
        assertTrue(tradeVO.isHolding());
    }

    public void testPerformSellToUseSameCompanyStock() throws Exception {
        setUp();
        PMDate date = new PMDate(1, 1, 2000);
        String stockCode = "CODE11";
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;
        String portfolio = "PortForTradingBO2";
        String tradingAc = "TradForTradingBO2";
        doBuy(date, stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        float salePrice = 4012.05f;
        float saleBrok = 123.23f;
        float saleQty = 100.5f;
        PMDate saleDate = new PMDate(2, 1, 2000);
        TransactionVO transVO = new TransactionVO(saleDate, "CODE12", AppConst.TRADINGTYPE.Sell, saleQty, salePrice, saleBrok, portfolio, tradingAc, false);
        TradingBO tradingBO = new TradingBO();
        try {
            tradingBO.doSell(transVO);
            fail("Should fail, there is not enough qty to sell");
        } catch (ApplicationException e) {
        }
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        List<TradeVO> tradeDetails = dao.getCompletedTradeDetails(tradingAc, portfolio, null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(0, tradeDetails.size());
    }

    public void testPerformSellToUseStockFromSameAccount() throws Exception {
        setUp();
        PMDate date = new PMDate(1, 1, 2000);
        String stockCode = "CODE11";
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;

        doBuy(date, stockCode, qty, price, brokerage, "PortForTradingBO1", "TradForTradingBO2", false);
        doBuy(date, stockCode, qty, price, brokerage, "PortForTradingBO2", "TradForTradingBO1", false);
        float salePrice = 4012.05f;
        float saleBrok = 123.23f;
        float saleQty = qty;
        PMDate saleDate = new PMDate(2, 1, 2000);
        TransactionVO transVO = new TransactionVO(saleDate, stockCode, AppConst.TRADINGTYPE.Sell, saleQty, salePrice, saleBrok, "PortForTradingBO2", "TradForTradingBO2", false);
        TradingBO tradingBO = new TradingBO();
        try {
            tradingBO.doSell(transVO);
            fail("Should fail, there is not enough qty to sell");
        } catch (ApplicationException e) {
        }
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        List<TradeVO> tradeDetails = dao.getCompletedTradeDetails("TradForTradingBO2", "PortForTradingBO2", null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(0, tradeDetails.size());
        doBuy(saleDate, stockCode, qty, price, brokerage, "PortForTradingBO2", "TradForTradingBO2", false);
        tradingBO.doSell(transVO);
        tradeDetails = dao.getCompletedTradeDetails("TradForTradingBO2", "PortForTradingBO2", null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(1, tradeDetails.size());
    }

    public void testPerformSellToSellAllFromFirstAvailable() throws Exception {
        setUp();
        PMDate date = new PMDate(1, 1, 2000);
        String stockCode = "CODE11";
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;
        String portfolio = "PortForTradingBO2";
        String tradingAc = "TradForTradingBO2";
        doBuy(date, stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        float salePrice = 4012.05f;
        float saleBrok = 123.23f;
        float saleQty = qty;
        PMDate saleDate = new PMDate(2, 1, 2000);
        TransactionVO transVO = new TransactionVO(saleDate, stockCode, AppConst.TRADINGTYPE.Sell, saleQty, salePrice, saleBrok, portfolio, tradingAc, false);
        TradingBO tradingBO = new TradingBO();
        tradingBO.doSell(transVO);
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        List<TradeVO> tradeDetails = dao.getCompletedTradeDetails(tradingAc, portfolio, null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(1, tradeDetails.size());
        TradeVO tradeVO = tradeDetails.get(0);
        assertEquals(date, tradeVO.getPurchaseDate());
        assertEquals(stockCode, tradeVO.getStockCode());
        assertEquals(saleQty, tradeVO.getQty());
        assertEquals(price, tradeVO.getPurchasePrice());
        assertEquals(Helper.getRoundedOffValue(brokerage + saleBrok, 2), Helper.getRoundedOffValue(tradeVO.getBrokerage(), 2));
        assertEquals(saleDate, tradeVO.getSaleDate());
        assertEquals(salePrice, tradeVO.getSalePrice());
        assertEquals(portfolio, tradeVO.getPortfolio());
        assertEquals(tradingAc, tradeVO.getTradingAc());
        List<TradeVO> holdingDetails = dao.getHoldingDetails(tradingAc, portfolio, stockCode, false);
        assertEquals(0, holdingDetails.size());
    }

    public void testPerformSellToUseOnlyWhichAreBoughtBefore() throws Exception {
        setUp();
        PMDate date = new PMDate(1, 1, 2000);
        String stockCode = "CODE11";
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;
        String portfolio = "PortForTradingBO2";
        String tradingAc = "TradForTradingBO2";
        doBuy(date, stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        doBuy(new PMDate(3, 1, 2000), stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        float salePrice = 4012.05f;
        float saleBrok = 123.23f;
        float saleQty = qty + 100;
        PMDate saleDate = new PMDate(2, 1, 2000);
        TransactionVO transVO = new TransactionVO(saleDate, stockCode, AppConst.TRADINGTYPE.Sell, saleQty, salePrice, saleBrok, portfolio, tradingAc, false);
        TradingBO tradingBO = new TradingBO();
        try {
            tradingBO.doSell(transVO);
            fail("Should fail, there is not enough qty to sell");
        } catch (ApplicationException e) {
        }
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        List<TradeVO> tradeDetails = dao.getCompletedTradeDetails(tradingAc, portfolio, null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(0, tradeDetails.size());
        transVO = new TransactionVO(saleDate, stockCode, AppConst.TRADINGTYPE.Sell, qty, salePrice, saleBrok, portfolio, tradingAc, false);
        tradingBO.doSell(transVO);
        tradeDetails = dao.getCompletedTradeDetails(tradingAc, portfolio, null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(1, tradeDetails.size());
    }

    public void testPerformSellToUseOnlyWhichAreBoughtOnSameAsSaleDate() throws Exception {
        setUp();
        PMDate date = new PMDate(1, 1, 2000);
        String stockCode = "CODE11";
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;
        String portfolio = "PortForTradingBO2";
        String tradingAc = "TradForTradingBO2";
        doBuy(date, stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        float salePrice = 4012.05f;
        float saleBrok = 123.23f;
        float saleQty = qty;
        PMDate saleDate = date;
        TransactionVO transVO = new TransactionVO(saleDate, stockCode, AppConst.TRADINGTYPE.Sell, saleQty, salePrice, saleBrok, portfolio, tradingAc, false);
        TradingBO tradingBO = new TradingBO();
        tradingBO.doSell(transVO);
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        List<TradeVO> tradeDetails = dao.getCompletedTradeDetails(tradingAc, portfolio, null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(1, tradeDetails.size());
    }

    public void testPerformSellToSellAllFromFirstAndPartialFromSecond() throws Exception {
        setUp();
        PMDate date1 = new PMDate(1, 1, 2000);
        String stockCode = "CODE11";
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;
        String portfolio = "PortForTradingBO2";
        String tradingAc = "TradForTradingBO2";
        doBuy(date1, stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        PMDate date2 = new PMDate(2, 1, 2000);
        doBuy(date2, stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        float salePrice = 4012.05f;
        float saleBrok = 123.23f;
        float saleQty = qty + 100;
        PMDate saleDate = date2;
        TransactionVO transVO = new TransactionVO(saleDate, stockCode, AppConst.TRADINGTYPE.Sell, saleQty, salePrice, saleBrok, portfolio, tradingAc, false);
        TradingBO tradingBO = new TradingBO();
        tradingBO.doSell(transVO);
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        List<TradeVO> tradeDetails = dao.getCompletedTradeDetails(tradingAc, portfolio, null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(2, tradeDetails.size());
        boolean date1Flag = false, date2Flag = false;
        for (TradeVO tradeDetail : tradeDetails) {
            if (tradeDetail.getPurchaseDate().equals(date1)) {
                date1Flag = true;
                if (tradeDetail.getQty() != qty) {
                    fail();
                }
            }
            if (tradeDetail.getPurchaseDate().equals(date2)) {
                date2Flag = true;
                if (tradeDetail.getQty() != 100) {
                    fail();
                }
            }
        }
        assertTrue(date1Flag);
        assertTrue(date2Flag);

    }

    public void testPerformSellToSellAllFromFirstAndSecond() throws Exception {
        setUp();
        PMDate date1 = new PMDate(1, 1, 2000);
        String stockCode = "CODE11";
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;
        String portfolio = "PortForTradingBO2";
        String tradingAc = "TradForTradingBO2";
        doBuy(date1, stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        PMDate date2 = new PMDate(2, 1, 2000);
        doBuy(date2, stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        doBuy(date2, stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        float salePrice = 4012.05f;
        float saleBrok = 123.23f;
        float saleQty = qty + qty;
        PMDate saleDate = date2;
        TransactionVO transVO = new TransactionVO(saleDate, stockCode, AppConst.TRADINGTYPE.Sell, saleQty, salePrice, saleBrok, portfolio, tradingAc, false);
        TradingBO tradingBO = new TradingBO();
        tradingBO.doSell(transVO);
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        List<TradeVO> tradeDetails = dao.getCompletedTradeDetails(tradingAc, portfolio, null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(2, tradeDetails.size());
        boolean date1Flag = false, date2Flag = false;
        for (TradeVO tradeDetail : tradeDetails) {
            if (tradeDetail.getPurchaseDate().equals(date1)) {
                date1Flag = true;
            }
            if (tradeDetail.getPurchaseDate().equals(date2)) {
                date2Flag = true;
            }
            if (tradeDetail.getQty() != qty) {
                fail();
            }
        }
        assertTrue(date1Flag);
        assertTrue(date2Flag);
    }

    public void testPerformSellToTrySellingMoreThanAvailableQty() throws Exception {
        setUp();
        PMDate date = new PMDate(1, 1, 2000);
        String stockCode = "CODE11";
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;
        String portfolio = "PortForTradingBO2";
        String tradingAc = "TradForTradingBO2";
        doBuy(date, stockCode, qty, price, brokerage, portfolio, tradingAc, false);
        float salePrice = 4012.05f;
        float saleBrok = 123.23f;
        float saleQty = qty + 100;
        PMDate saleDate = date;
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        List<TransactionVO> transactionList = dao.getTransactionList(tradingAc, portfolio);
        int initialSize = transactionList.size();
        TransactionVO transVO = new TransactionVO(saleDate, stockCode, AppConst.TRADINGTYPE.Sell, saleQty, salePrice, saleBrok, portfolio, tradingAc, false);
        TradingBO tradingBO = new TradingBO();
        try {
            tradingBO.doSell(transVO);
            fail("Should fail, there is not enough qty to sell");
        } catch (ApplicationException e) {
        }
        List<TradeVO> tradeDetails = dao.getCompletedTradeDetails(tradingAc, portfolio, null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(0, tradeDetails.size());
        transactionList = dao.getTransactionList(tradingAc, portfolio);
        assertEquals(initialSize, transactionList.size());
    }

    public void testPerformSellToTrySellingWithoutAvailable() throws Exception {
        setUp();
        PMDate date = new PMDate(1, 1, 2000);
        String stockCode = "CODE11";
        float qty = 1000.5f;
        String portfolio = "PortForTradingBO2";
        String tradingAc = "TradForTradingBO2";
        float salePrice = 4012.05f;
        float saleBrok = 123.23f;
        float saleQty = qty + 100;
        PMDate saleDate = date;
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        List<TransactionVO> transactionList = dao.getTransactionList(tradingAc, portfolio);
        int initialSize = transactionList.size();
        TransactionVO transVO = new TransactionVO(saleDate, stockCode, AppConst.TRADINGTYPE.Sell, saleQty, salePrice, saleBrok, portfolio, tradingAc, false);
        TradingBO tradingBO = new TradingBO();
        try {
            tradingBO.doSell(transVO);
            fail("Should fail, there is not enough qty to sell");
        } catch (ApplicationException e) {
        }
        List<TradeVO> tradeDetails = dao.getCompletedTradeDetails(tradingAc, portfolio, null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(0, tradeDetails.size());
        transactionList = dao.getTransactionList(tradingAc, portfolio);
        assertEquals(initialSize, transactionList.size());

    }

    public void testPerformSellToSellBasedOnDeliveryTrade() throws Exception {
        performTransaction(false);
    }

    public void testPerformSellToSellBasedOnDayTrade() throws Exception {
        performTransaction(true);
    }

    private void performTransaction(boolean dayTrade) throws Exception {
        setUp();
        String stockCode = "CODE11";
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;
        String portfolio = "PortForTradingBO2";
        String tradingAc = "TradForTradingBO2";
        PMDate dayTradeDate = new PMDate(1, 1, 2000);
        doBuy(dayTradeDate, stockCode, qty, price, brokerage, portfolio, tradingAc, dayTrade);
        doBuy(new PMDate(5, 1, 2000), stockCode, qty, price, brokerage, portfolio, tradingAc, !dayTrade);
        float salePrice = 4012.05f;
        float saleBrok = 123.23f;
        float saleQty = qty;
        PMDate saleDate = new PMDate(6, 1, 2000);
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        TransactionVO transVO = new TransactionVO(saleDate, stockCode, AppConst.TRADINGTYPE.Sell, saleQty, salePrice, saleBrok, portfolio, tradingAc, !dayTrade);
        TradingBO tradingBO = new TradingBO();
        tradingBO.doSell(transVO);
        List<TradeVO> tradeDetails = dao.getCompletedTradeDetails(tradingAc, portfolio, null, true);
        filterByStockCode(tradeDetails, stockCode);
        assertEquals(1, tradeDetails.size());
        List<TradeVO> holdingDetails = dao.getHoldingDetails(tradingAc, portfolio, dayTrade);
        assertEquals(1, holdingDetails.size());
        assertEquals(dayTradeDate, holdingDetails.get(0).getPurchaseDate());
        assertEquals(0, dao.getHoldingDetails(tradingAc, portfolio, !dayTrade).size());
    }

    public void testDoIPO() throws Exception {
        //nothing special now
    }

    public void testPerformSellToRemoveStopLossEntry() throws Exception {
        setUp();
        PMDate date = new PMDate(1, 1, 2000);
        String stockCode = "CODE15";
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;

        String portfolioName = "PortForTradingBO1";
        String tradingAcName = "TradForTradingBO1";
        doBuy(date, stockCode, qty, price, brokerage, portfolioName, tradingAcName, false);

        PortfolioBO portfolioBO = new PortfolioBO();
        StopLossVO slvo = new StopLossVO(stockCode, 100, 110, 200, 300, portfolioName);
        List<StopLossVO> slVOs = new Vector<StopLossVO>();
        slVOs.add(slvo);
        DAOManager.getPortfolioDAO().insertStopLossVOs(slVOs);
        List<StopLossVO> slList = portfolioBO.getStopLossDetails(portfolioName);
        assertEquals(1, slList.size());
        assertTrue(slList.contains(slvo));

        TransactionVO transVO = new TransactionVO(new PMDate(2, 1, 2000), stockCode, AppConst.TRADINGTYPE.Sell, qty, 4012.05f, 123.23f, portfolioName, tradingAcName, false);
        TradingBO tradingBO = new TradingBO();
        tradingBO.doSell(transVO);

        slList = portfolioBO.getStopLossDetails(portfolioName);
        assertEquals(0, slList.size());
    }

    public void testPerformSellNotToRemoveStopLossIfHoldingExists() throws Exception {
        setUp();
        PMDate date = new PMDate(1, 1, 2000);
        String stockCode = "CODE15";
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;

        String portfolioName = "PortForTradingBO1";
        String tradingAcName = "TradForTradingBO1";
        doBuy(date, stockCode, qty, price, brokerage, portfolioName, tradingAcName, false);

        PortfolioBO portfolioBO = new PortfolioBO();
        StopLossVO slvo = new StopLossVO(stockCode, 100, 110, 200, 300, portfolioName);
        List<StopLossVO> slVOs = new Vector<StopLossVO>();
        slVOs.add(slvo);
        DAOManager.getPortfolioDAO().insertStopLossVOs(slVOs);
        List<StopLossVO> slList = portfolioBO.getStopLossDetails(portfolioName);
        assertEquals(1, slList.size());
        assertTrue(slList.contains(slvo));

        TransactionVO transVO = new TransactionVO(new PMDate(2, 1, 2000), stockCode, AppConst.TRADINGTYPE.Sell, qty - 1, 4012.05f, 123.23f, portfolioName, tradingAcName, false);
        TradingBO tradingBO = new TradingBO();
        tradingBO.doSell(transVO);

        slList = portfolioBO.getStopLossDetails(portfolioName);
        assertEquals(1, slList.size());
        assertTrue(slList.contains(slvo));
    }

    public void testDoBuyAppliesDivident() throws Exception {

        String stockCode = "MyCode";
        StockVO stockVO = new StockVO(stockCode, stockCode, 10f, SERIESTYPE.equity, 10f, (short) 10, "", new PMDate(1, 1, 2006), true);
        DAOManager.getStockDAO().insertStock(stockVO);
        DAOManager.getCompanyActionDAO().insertCompanyAction(new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(5, 1, 2008), stockCode, 10f, 0f));

        PMDate date = new PMDate(1, 1, 2008);
        float qty = 1000.5f;
        float price = 2012.05f;
        float brokerage = 842.56f;

        String portfolioName = "PortForTradingBO1";
        String tradingAcName = "TradForTradingBO1";
        doBuy(date, stockCode, qty, price, brokerage, portfolioName, tradingAcName, false);

//        DAOManager.getTransactionDAO().getTransaction();


    }

    private void filterByStockCode(List<TradeVO> tradeDetails, String stockCode) {
        for (int i = tradeDetails.size() - 1; i > 0; i--) {
            TradeVO tradeVO = tradeDetails.get(i);
            if (!tradeVO.getStockCode().equals(stockCode)) {
                tradeDetails.remove(i);
            }
        }
    }

    private TransactionVO doBuy(PMDate date, String stockCode, float qty, float price, float brokerage, String portfolio, String tradingAc, boolean dayTrading) {
        TransactionVO transVO = new TransactionVO(date, stockCode, AppConst.TRADINGTYPE.Buy, qty, price, brokerage, portfolio, tradingAc, dayTrading);
        TradingBO tradingBO = new TradingBO();
        tradingBO.doBuy(transVO);
        return transVO;
    }

}
