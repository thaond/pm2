/*
 * Created on Oct 13, 2004
 *
 */
package pm.bo;

import com.ibatis.dao.client.DaoManager;
import org.apache.log4j.Logger;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IAccountDAO;
import pm.dao.ibatis.dao.ITransactionDAO;
import pm.util.AppConst;
import pm.util.ApplicationException;
import pm.util.BusinessLogger;
import pm.util.PMDate;
import pm.vo.*;

import java.util.HashMap;
import java.util.List;

public class TradingBO {
    private static Logger logger = Logger.getLogger(TradingBO.class);


    public boolean doTrading(TransactionVO transVO) {
        BusinessLogger.logTransaction(transVO);
        switch (transVO.getAction()) {
            case Buy:
            case IPO:
                return doBuy(transVO);
            case Sell:
                try {
                    return doSell(transVO);
                } catch (ApplicationException e) {
                    logger.error(e, e);
                    throw e;
                }
        }
        return false;
    }

    boolean doSell(TransactionVO transVO) throws ApplicationException {
        DaoManager daoManager = DAOManager.getDaoManager();
        try {
            daoManager.startTransaction();
            ITransactionDAO dao = getDAO();
            List<TradeVO> holdingDetails = dao.getHoldingDetails(transVO.getTradingAc(), transVO.getPortfolio(), transVO.getStockCode(), transVO.isDayTrading());
            HashMap<Integer, Float> tradeMap = new HashMap<Integer, Float>();
            boolean mappingFlag = performBuySellMapping(transVO.getDate(), holdingDetails, transVO.getQty(), tradeMap);
            if (!mappingFlag) {
                throw new ApplicationException("Not enough Qty to sell");
            }
            dao.insertSaleTransaction(transVO, tradeMap);
            doFundTransaction(transVO);
            holdingDetails = dao.getHoldingDetails(transVO.getTradingAc(), transVO.getPortfolio(), transVO.getStockCode(), transVO.isDayTrading());
            if (holdingDetails.isEmpty()) {
                PortfolioDetailsVO portfolio = DAOManager.getAccountDAO().portfolio(transVO.getPortfolio());
                StockVO stockVO = DAOManager.getStockDAO().getStock(transVO.getStockCode());
                DAOManager.getPortfolioDAO().deleteStopLossOf(portfolio.getId(), stockVO.getId());
            }
            daoManager.commitTransaction();
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            daoManager.endTransaction();
        }
        return true;
    }

    void doFundTransaction(TransactionVO transVO) {
        DAOManager.fundTransactionDAO().perform(fundTransaction(transVO));
    }

    FundTransactionVO fundTransaction(TransactionVO transVO) {
        IAccountDAO accountDAO = DAOManager.getAccountDAO();
        TradingAccountVO accountVO = accountDAO.tradingAcc(transVO.getTradingAc());
        PortfolioDetailsVO portfolioDetailsVO = accountDAO.portfolio(transVO.getPortfolio());
        AppConst.FUND_TRANSACTION_TYPE transactionType = isSell(transVO) ? AppConst.FUND_TRANSACTION_TYPE.Credit : AppConst.FUND_TRANSACTION_TYPE.Debit;
        AppConst.FUND_TRANSACTION_REASON transactionReason = isSell(transVO) ? AppConst.FUND_TRANSACTION_REASON.StockSell : AppConst.FUND_TRANSACTION_REASON.StockBuy;
        return new FundTransactionVO(transactionType, transactionReason,
                transVO.getDate(), transVO.getValue(), accountVO, portfolioDetailsVO, transVO.getAbsDetails());
    }

    private boolean isSell(TransactionVO transVO) {
        return transVO.getAction() == AppConst.TRADINGTYPE.Sell;
    }

    boolean performBuySellMapping(PMDate saleDate, List<TradeVO> holdingDetails, float saleQty, HashMap<Integer, Float> tradeMap) {
        for (TradeVO tradeVO : holdingDetails) {
            if (!tradeVO.isHolding(saleDate)) {
                continue;
            }
            float qtyAvailable = tradeVO.getQty();
            float diffQty = saleQty - qtyAvailable;
            if (diffQty <= 0) {
                tradeMap.put(tradeVO.getBuyId(), saleQty);
                saleQty = 0;
            } else {
                tradeMap.put(tradeVO.getBuyId(), qtyAvailable);
                saleQty -= qtyAvailable;
            }

            if (saleQty == 0) {
                break;
            }
        }
        return saleQty <= 0;
    }

    private ITransactionDAO getDAO() {
        return DAOManager.getTransactionDAO();
    }

    boolean doBuy(TransactionVO transVO) {
        DaoManager daoManager = DAOManager.getDaoManager();
        try {
            daoManager.startTransaction();
            getDAO().insertTransaction(transVO);
            doFundTransaction(transVO);
            daoManager.commitTransaction();
        } finally {
            daoManager.endTransaction();
        }
        return true; //TODO Need refactor
    }

    public static boolean saveTradingAc(Account tradingAc) throws Exception {
        DAOManager.getAccountDAO().insertTradingAcc(tradingAc);
        return true; //TODO Need refactor
    }

    public boolean isDuplicate(TransactionVO transVO) {
        List<TransactionVO> transactionList = getDAO().getTransactionList(transVO.getTradingAc(), transVO.getPortfolio(), transVO.getStockCode(), transVO.getAction());
        return transactionList.contains(transVO);
    }
}
