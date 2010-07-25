package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import org.apache.log4j.Logger;
import pm.util.PMDate;
import pm.vo.EquityQuote;
import pm.vo.StockVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 01-Aug-2006
 * Time: 23:22:37
 */
public class QuoteDAO extends SqlMapDaoTemplate implements IQuoteDAO {

    Logger logger = Logger.getLogger(QuoteDAO.class);

    public QuoteDAO(DaoManager daoManager) {
        super(daoManager);
    }

    public List<EquityQuote> getQuotes(String stockCode) {
        return (List<EquityQuote>) super.queryForList("getQuote", stockCode);
    }

    public void insertQuote(EquityQuote quoteVO) {
        logger.info(quoteVO);
        super.insert("insertQuote", quoteVO);
    }

    public boolean updateQuote(EquityQuote quoteVO) {
        int rowCount = super.update("updateQuote", quoteVO);
        boolean flag = rowCount == 1;
        if (!flag) {
            logger.error("Update Quote, updated more than 1 row : " + quoteVO);
        }
        return flag;
    }

    public void insertQuotes(List<EquityQuote> quoteVOs) {
        super.startBatch();
        for (EquityQuote quoteVO : quoteVOs) {
            insertQuote(quoteVO);
        }
        super.executeBatch();
    }

    public void updateQuotes(List<EquityQuote> quoteVOs) {
        super.startBatch();
        for (EquityQuote quoteVO : quoteVOs) {
            updateQuote(quoteVO);
        }
        super.executeBatch();
    }

    public List<EquityQuote> getQuotes(PMDate date) {
        return (List<EquityQuote>) super.queryForList("getQuoteForDate", date.getIntVal());
    }

    public List<EquityQuote> getQuotes(String stockCode, PMDate frmDate, PMDate toDate) {
        Map paramMap = map(stockCode, frmDate, toDate);
        return (List<EquityQuote>) super.queryForList("getQuoteForStockDateRange", paramMap);
    }

    private Map map(String stockCode, PMDate frmDate, PMDate toDate) {
        Map paramMap = new HashMap();
        if (stockCode != null) paramMap.put("StockCode", stockCode);
        if (frmDate != null) paramMap.put("fromDate", frmDate.getIntVal());
        if (toDate != null) paramMap.put("toDate", toDate.getIntVal());
        return paramMap;
    }

    public EquityQuote getQuote(StockVO stockVO) {
        return (EquityQuote) super.queryForObject("getLastQuoteForID", stockVO.getId());
    }

    public Map<StockVO, List<EquityQuote>> quotes(PMDate stDate, PMDate enDate) {
        Map paramMap = map(null, stDate, enDate);
        List<EquityQuote> quotes = (List<EquityQuote>) super.queryForList("getQuoteForDateRange", paramMap);
        Map<StockVO, List<EquityQuote>> quotesMap = new HashMap<StockVO, List<EquityQuote>>();
        for (EquityQuote quote : quotes) {
            List<EquityQuote> quoteList = quotesMap.get(quote.getStockVO());
            if (quoteList == null) {
                quoteList = new ArrayList<EquityQuote>();
                quotesMap.put(quote.getStockVO(), quoteList);
            }
            quoteList.add(quote);
        }
        return quotesMap;
    }

    public EquityQuote quote(StockVO stockVO, PMDate date) {
        Map paramMap = new HashMap();
        paramMap.put("StockID", stockVO.getId());
        paramMap.put("PMDate", date.getIntVal());
        return (EquityQuote) super.queryForObject("getQuoteForStockIDDate", paramMap);

    }

    public void updateStockId(int fromStockId, int toStockId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("from", fromStockId);
        params.put("to", toStockId);
        params.put("table", "QUOTE");
        super.update("updateStockID", params);
    }

    public void updateAdjustedClose(String stockCode, PMDate exDate, float ratio) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("exDate", exDate);
        params.put("stockCode", stockCode);
        params.put("ratio", ratio);
        super.update("updateAdjustedClose", params);
    }

    public EquityQuote getQuote(String stockCode) {
        return (EquityQuote) super.queryForObject("getLastQuote", stockCode);
    }

    public EquityQuote getQuote(String stockCode, PMDate date) {
        Map paramMap = new HashMap();
        paramMap.put("StockCode", stockCode);
        paramMap.put("PMDate", date.getIntVal());
        return (EquityQuote) super.queryForObject("getQuoteForStockDate", paramMap);
    }

}
