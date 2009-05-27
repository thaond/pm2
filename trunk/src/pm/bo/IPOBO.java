package pm.bo;

import org.apache.log4j.Logger;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IIPODAO;
import pm.util.AppConst;
import static pm.util.AppConst.COMPANY_NAME_IPO;
import pm.util.BusinessLogger;
import pm.util.PMDate;
import pm.util.enumlist.IPOAction;
import pm.vo.*;

import java.util.Vector;

public class IPOBO {
    private static Logger logger = Logger.getLogger(IPOBO.class);

    public void doAction(IPOAction action, IPOVO ipovo) {
        BusinessLogger.logTransaction(action, ipovo);
        IIPODAO dao = DAOManager.getIPODAO();
        switch (action) {
            case Apply:
                dao.insertIPOApply(ipovo);
                doFundTransaction(action, ipovo);
                break;
            case Allotment:
                doAllotment(ipovo, dao);
                break;
            case Refund:
                dao.updateIPO(ipovo);
                doFundTransaction(action, ipovo);
                break;
        }
    }

    private void doFundTransaction(IPOAction action, IPOVO ipovo) {
        FundTransactionVO fund = fundTransaction(action, ipovo);
        DAOManager.fundTransactionDAO().perform(fund);
    }

    FundTransactionVO fundTransaction(IPOAction action, IPOVO transVO) {
        if (action == IPOAction.Allotment) return null;
        TradingAccountVO accountVO = (TradingAccountVO) transVO.getTradingAcc();
        PortfolioDetailsVO portfolioDetailsVO = (PortfolioDetailsVO) transVO.getPortfolio();
        AppConst.FUND_TRANSACTION_TYPE transactionType = isApply(action) ? AppConst.FUND_TRANSACTION_TYPE.Debit : AppConst.FUND_TRANSACTION_TYPE.Credit;
        AppConst.FUND_TRANSACTION_REASON transactionReason = isApply(action) ? AppConst.FUND_TRANSACTION_REASON.IPOApply : AppConst.FUND_TRANSACTION_REASON.IPORefund;
        PMDate date = isApply(action) ? transVO.getApplyDate() : transVO.getRefundedDate();
        float amount = isApply(action) ? transVO.getAppliedAmount() : transVO.getRefundAmount();
        return new FundTransactionVO(transactionType, transactionReason, date, amount, accountVO, portfolioDetailsVO, transVO.getIpoCode());
    }

    private boolean isApply(IPOAction action) {
        return (action == IPOAction.Apply);
    }

    private void doAllotment(IPOVO ipovo, IIPODAO dao) {
        TransactionVO transVO = ipovo.getAllotedTransaction();
        if (transVO.getQty() == 0) {
            return;
        }
        StockVO stockVO = DAOManager.getStockDAO().getStock(transVO.getStockCode());
        if (stockVO == null) {
            new StockMasterBO().insertNewStock(transVO.getStockCode(), COMPANY_NAME_IPO);
        }
        transVO.setId(DAOManager.getTransactionDAO().insertTransaction(transVO));
        BusinessLogger.logTransaction(transVO); //TODO if IPOLogger works fine we don't need this, this would become duplicate
        dao.updateIPO(ipovo);
    }


    TradingBO getTradingBO() {
        return new TradingBO();
    }

    IIPODAO getDAO() {
        return DAOManager.getIPODAO();
    }

    PortfolioBO getPortfolioBO() {
        return new PortfolioBO();
    }

    public Vector<IPOVO> getIPOTransactionDetailsFor(int tradeAc,
                                                     int portfolio, IPOAction action) {
        Vector<IPOVO> retVal = new Vector<IPOVO>();
        for (IPOVO ipovo : getDAO().getIPOTransaction(portfolio, tradeAc)) {
            switch (action) {
                case Allotment:
                    if (ipovo.isPending() && !ipovo.isAlloted()) {
                        retVal.add(ipovo);
                    }
                    break;
                case Refund:
                    if (ipovo.isPending()) {
                        retVal.add(ipovo);
                    }
                    break;
            }
        }
        return retVal;
    }

    public Vector<IPOVO> getIPOTransactionList(int tradeAc, int portfolio) {
        return new Vector<IPOVO>(getDAO().getIPOTransaction(portfolio, tradeAc));
    }


}
