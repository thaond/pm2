package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
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
 * Time: 6:05:28 PM
 */
public class TransactionDAO extends SqlMapDaoTemplate implements ITransactionDAO {
    private static final String TRADING_ACCNAME = "tradingACCName";
    private static final String PORTFOLIO_NAME = "portfolioName";
    private static final String STOCK_CODE = "stockCode";
    private static final String DAY_TRADING = "dayTrading";

    public TransactionDAO(DaoManager daoManager) {
        super(daoManager);
    }

    //TODO inssertTransaction to insert only buy transaction
    public int insertTransaction(TransactionVO transactionVO) {
        int retVal = -1;
        switch (transactionVO.getAction()) {
            case Buy:
                retVal = (Integer) super.insert("insertBuyTransaction", transactionVO);
                break;
            case IPO:
                retVal = (Integer) super.insert("insertBuyTransaction", transactionVO);
                break;
            //TODO need this only for auto loading from log file???
//                throw new RuntimeException("2DB Deprecated");
            case Sell:
                retVal = insertSellTransaction(transactionVO);
                break;
        }
        return retVal;
    }

    private int insertSellTransaction(TransactionVO transactionVO) {
        int retVal;
        retVal = (Integer) super.insert("insertSellTransaction", transactionVO);
        return retVal;
    }

    public List<TradeVO> getCompletedTradeDetails(String tradingAccName, String portfolioName, String stockCode, boolean incDayTrading) {
        HashMap hmParameters = addToMap(tradingAccName, portfolioName, stockCode);
        if (!incDayTrading) {
            hmParameters.put(DAY_TRADING, 0);
        }
        return super.queryForList("getCompletedTradeDetails", hmParameters);
    }

    private HashMap addToMap(String tradingAccName, String portfolioName, String stockCode) {
        HashMap hmParameters = new HashMap();
        if (tradingAccName != null) {
            hmParameters.put(TRADING_ACCNAME, tradingAccName);
        }
        if (portfolioName != null) {
            hmParameters.put(PORTFOLIO_NAME, portfolioName);
        }
        if (stockCode != null) {
            hmParameters.put(STOCK_CODE, stockCode);
        }
        return hmParameters;
    }

    public List<TradeVO> getHoldingDetails(String tradingAccName, String portfolioName, boolean dayTrading) {
        return getHoldingDetails(tradingAccName, portfolioName, null, dayTrading);
    }

    private HashMap addToMap(String tradingAccName, String portfolioName, boolean dayTrading) {
        HashMap hmParameters = addToMap(tradingAccName, portfolioName, null);
        hmParameters.put(DAY_TRADING, (dayTrading ? 1 : 0));
        return hmParameters;
    }

    public List<TradeVO> getHoldingDetails(String tradingAccName, String portfolioName, String stockCode, boolean dayTrading) {
        HashMap hmParameters = addToMap(tradingAccName, portfolioName, dayTrading);
        if (stockCode != null) {
            hmParameters.put(STOCK_CODE, stockCode);
        }
        return super.queryForList("getHoldingDetails", hmParameters);
    }

    public void insertSaleTransaction(TransactionVO transVO, HashMap<Integer, Float> tradeMap) {
        int saleID = insertSellTransaction(transVO);
        for (Integer buyID : tradeMap.keySet()) {
            float saleQty = tradeMap.get(buyID);
            insertTrade(buyID, saleID, saleQty);
        }
    }

    public List<TransactionVO> getTransactionList(String tradingAccName, String portfolioName) {
        return executeGetTransactionList(addToMap(tradingAccName, portfolioName, null));
    }

    private List executeGetTransactionList(HashMap hmParameters) {
        List transactionList = super.queryForList("getBuyTransactionList", hmParameters);
        transactionList.addAll(super.queryForList("getSellTransactionList", hmParameters));
        return transactionList;
    }

    public List<TransactionVO> getTransactionList(String tradingAccName, String portfolioName, String stockCode, boolean forDayTrading) {
        HashMap hmParameters = addToMap(tradingAccName, portfolioName, null);
        if (stockCode != null) {
            hmParameters.put(STOCK_CODE, stockCode);
        }
        hmParameters.put(DAY_TRADING, forDayTrading);
        return executeGetTransactionList(hmParameters);
    }

    public List<TransactionVO> getTransactionList(String tradingAccName, String portfolioName, AppConst.TRADINGTYPE actionType) {
        HashMap hmParameters = addToMap(tradingAccName, portfolioName, null);
        if (actionType == AppConst.TRADINGTYPE.Buy) {
            return super.queryForList("getBuyTransactionList", hmParameters);
        }
        if (actionType == AppConst.TRADINGTYPE.Sell) {
            return super.queryForList("getSellTransactionList", hmParameters);
        }
        return null;
    }

    public List<TransactionVO> getTransactionList(String tradingAccName, String portfolioName, String stockCode, AppConst.TRADINGTYPE actionType) {
        HashMap hmParameters = addToMap(tradingAccName, portfolioName, stockCode);
        if (actionType == AppConst.TRADINGTYPE.Buy) {
            return super.queryForList("getBuyTransactionList", hmParameters);
        }
        if (actionType == AppConst.TRADINGTYPE.Sell) {
            return super.queryForList("getSellTransactionList", hmParameters);
        }
        return null;
    }

    public List<TradeVO> getTradeDetails(String tradingAccName, String portfolioName, boolean incDayTrading) {
        List<TradeVO> tradeDetails = getHoldingDetails(tradingAccName, portfolioName, false);
        if (incDayTrading) {
            tradeDetails.addAll(getHoldingDetails(tradingAccName, portfolioName, true));
        }
        tradeDetails.addAll(getCompletedTradeDetails(tradingAccName, portfolioName, null, incDayTrading));
        return tradeDetails;
    }

    public List<TradeVO> getTradeDetails(String tradingAccName, String portfolioName, String stockCode, boolean incDayTrading) {
        List<TradeVO> tradeDetails = getHoldingDetails(tradingAccName, portfolioName, stockCode, false);
        if (incDayTrading) {
            tradeDetails.addAll(getHoldingDetails(tradingAccName, portfolioName, stockCode, true));
        }
        tradeDetails.addAll(getCompletedTradeDetails(tradingAccName, portfolioName, stockCode, incDayTrading));
        return tradeDetails;
    }

    public TransactionVO getTransaction(int id, AppConst.TRADINGTYPE actionType) {
        if (actionType == AppConst.TRADINGTYPE.Buy) {
            return (TransactionVO) super.queryForObject("getBuyTransactionByID", id);
        }
        if (actionType == AppConst.TRADINGTYPE.Sell) {
            return (TransactionVO) super.queryForObject("getSellTransactionByID", id);
        }
        return null;
    }

    public Map<Integer, TransactionMapping> getTransactionMapping() {
        return super.queryForMap("getTransactionMapping", null, "id");
    }

    public void updateTransaction(TransactionVO transactionVO) {
        if (transactionVO.getAction() == AppConst.TRADINGTYPE.Buy) {
            super.update("updateBuyTransaction", transactionVO);
        }
        if (transactionVO.getAction() == AppConst.TRADINGTYPE.Sell) {
            super.update("updateSellTransaction", transactionVO);
        }
    }

    public void updateTrade(TransactionMapping mapping) {
        super.update("updateTrade", mapping);
    }

    public void updateStockId(int fromStockId, int toStockId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("from", fromStockId);
        params.put("to", toStockId);
        params.put("table", "BUYTRANSACTION");
        super.update("updateStockID", params);
    }

    public void updateOrInsertICICITransaction(ICICITransaction transaction) {
        super.insert("insertICICICodeIfMissing", transaction.getIciciCode());
        if (super.update("updateICICITransaction", transaction) == 0) {
            super.insert("insertICICITransaction", transaction);
        }
    }

    public List<ICICITransaction> iciciTransactions() {
        return super.queryForList("getICICITransaction");
    }

    private void insertTrade(Integer buyID, Integer saleID, Float saleQty) {
        Map values = new HashMap();
        values.put("BUYID", buyID);
        values.put("SELLID", saleID);
        values.put("QTY", saleQty);
        super.insert("insertTrade", values);
    }
}