package pm.dao.ibatis.dao;

import pm.util.AppConst;
import pm.vo.ICICITransaction;
import pm.vo.TradeVO;
import pm.vo.TransactionMapping;
import pm.vo.TransactionVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: Aug 12, 2006
 * Time: 6:04:14 PM
 */
public interface ITransactionDAO {

    public int insertTransaction(TransactionVO transactionVO);

    public List<TradeVO> getCompletedTradeDetails(String tradingAccName, String portfolioName, String stockCode, boolean incDayTrading);

    public List<TradeVO> getHoldingDetails(String tradingAccName, String portfolioName, boolean forDayTrading);

    public List<TradeVO> getHoldingDetails(String tradingAccName, String portfolioName, String stockCode, boolean forDayTrading);

    public void insertSaleTransaction(TransactionVO transVO, HashMap<Integer, Float> tradeMap);

    public List<TransactionVO> getTransactionList(String tradingAccName, String portfolioName);

    public List<TransactionVO> getTransactionList(String tradingAccName, String portfolioName, String stockCode, boolean forDayTrading);

    public List<TransactionVO> getTransactionList(String tradingAccName, String portfolioName, AppConst.TRADINGTYPE actionType);

    public List<TransactionVO> getTransactionList(String tradingAccName, String portfolioName, String stockCode, AppConst.TRADINGTYPE actionType);

    /* This methods gets list of all the trades (both complete and incomplete)*/
    public List<TradeVO> getTradeDetails(String tradingAccName, String portfolioName, boolean incDayTrading);

    /* This methods gets list of all the trades (both complete and incomplete)*/
    public List<TradeVO> getTradeDetails(String tradingAccName, String portfolioName, String stockCode, boolean incDayTrading);

    public TransactionVO getTransaction(int id, AppConst.TRADINGTYPE actionType);

    public Map<Integer, TransactionMapping> getTransactionMapping();

    public void updateTransaction(TransactionVO transactionVO);

    public void updateTrade(TransactionMapping mapping);

    void updateStockId(int fromStockId, int toStockId);

    void updateOrInsertICICITransaction(ICICITransaction transaction);

    List<ICICITransaction> iciciTransactions();

}
