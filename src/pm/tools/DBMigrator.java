package pm.tools;

import pm.dao.ibatis.dao.*;
import pm.util.AppConst;
import pm.vo.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thiyagu
 * @version $Id: DBMigrator.java,v 1.1 2008/01/02 11:49:14 tpalanis Exp $
 * @since 02-Jan-2008
 */
public class DBMigrator {

    public static void main(String[] args) {
        new DBMigrator().migrate();
    }

    void migrate() {
        migrateWatchlist();
        migrateStoploss();
        migrateFundTransaction();
    }

    private void migrateFundTransaction() {
        List<PortfolioDetailsVO> oldPortfolios = DAOManagerOldDB.getAccountDAO().getPorfolioList();
        List<TradingAccountVO> oldTradingAcs = DAOManagerOldDB.getAccountDAO().getTradingAccList();
        List<PortfolioDetailsVO> newPortfolios = DAOManager.getAccountDAO().getPorfolioList();
        List<TradingAccountVO> newTradingAcs = DAOManager.getAccountDAO().getTradingAccList();
        IFundTransactionDAO oldDAO = DAOManagerOldDB.fundTransactionDAO();
        IFundTransactionDAO newDAO = DAOManager.fundTransactionDAO();

        Map<String, TradingAccountVO> tradingAcMap = new HashMap<String, TradingAccountVO>();
        for (TradingAccountVO accountVO : newTradingAcs) {
            tradingAcMap.put(accountVO.getName(), accountVO);
        }
        Map<String, PortfolioDetailsVO> portfolioAcMap = new HashMap<String, PortfolioDetailsVO>();
        for (PortfolioDetailsVO accountVO : newPortfolios) {
            portfolioAcMap.put(accountVO.getName(), accountVO);
        }

        for (PortfolioDetailsVO portfolio : oldPortfolios) {
            for (TradingAccountVO tradingAc : oldTradingAcs) {
                List<FundTransactionVO> transactionVOs = oldDAO.get(tradingAc, portfolio);
                TradingAccountVO newTradingAc = tradingAcMap.get(tradingAc.getName());
                PortfolioDetailsVO newPortfolio = portfolioAcMap.get(portfolio.getName());
                for (FundTransactionVO transactionVO : transactionVOs) {
                    AppConst.FUND_TRANSACTION_REASON reason = transactionVO.getTransactionReason();
                    if (reason == AppConst.FUND_TRANSACTION_REASON.StockBuy
                            || reason == AppConst.FUND_TRANSACTION_REASON.StockSell
                            || reason == AppConst.FUND_TRANSACTION_REASON.IPOApply
                            || reason == AppConst.FUND_TRANSACTION_REASON.IPORefund)
                        continue;
                    transactionVO.setPortfolio(newPortfolio);
                    transactionVO.setTradingAccount(newTradingAc);
                    newDAO.perform(transactionVO);
                }
            }
        }
    }

    private void migrateStoploss() {
        List<PortfolioDetailsVO> portfolios = DAOManager.getAccountDAO().getPorfolioList();
        IPortfolioDAO oldDAO = DAOManagerOldDB.getPortfolioDAO();
        IPortfolioDAO newDAO = DAOManager.getPortfolioDAO();
        for (PortfolioDetailsVO portfolio : portfolios) {
            List<StopLossVO> slVOs = oldDAO.getStopLoss(portfolio.getName());
            newDAO.insertStopLossVOs(slVOs);
        }
    }

    private void migrateWatchlist() {
        IWatchlistDAO oldDAO = DAOManagerOldDB.getWatchlistDAO();
        IWatchlistDAO newDAO = DAOManager.getWatchlistDAO();
        List<WatchlistDetailsVO> watchlistGroup = oldDAO.getWatchlistGroup();
        Map<String, List<WatchlistVO>> watchListMap = new HashMap<String, List<WatchlistVO>>();
        for (WatchlistDetailsVO watchlistDetailsVO : watchlistGroup) {
            List<WatchlistVO> watchlist = oldDAO.getWatchlistVos(watchlistDetailsVO.getId());
            newDAO.insertWatchlistGroup(watchlistDetailsVO);
            watchListMap.put(watchlistDetailsVO.getName(), watchlist);
        }
        List<WatchlistDetailsVO> newWatchlistGroup = newDAO.getWatchlistGroup();

        for (WatchlistDetailsVO watchlistDetailsVO : newWatchlistGroup) {
            List<WatchlistVO> watchList = watchListMap.get(watchlistDetailsVO.getName());
            for (WatchlistVO watchlistVO : watchList) {
                watchlistVO.setWatchlistGroupId(watchlistDetailsVO.getId());
            }
            newDAO.insertWatchlistVOs(watchList);
        }
    }
}
