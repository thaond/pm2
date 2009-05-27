package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import org.apache.log4j.Logger;
import pm.util.PMDate;
import pm.vo.QuoteVO;
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

    public List<QuoteVO> getQuotes(String stockCode) {
        return (List<QuoteVO>) super.queryForList("getQuote", stockCode);
    }

    public void insertQuote(QuoteVO quoteVO) {
        logger.info(quoteVO);
        super.insert("insertQuote", quoteVO);
    }

    public boolean updateQuote(QuoteVO quoteVO) {
        int rowCount = super.update("updateQuote", quoteVO);
        boolean flag = rowCount == 1;
        if (!flag) {
            logger.error("Update Quote, updated more than 1 row : " + quoteVO);
        }
        return flag;
    }

    public void insertQuotes(List<QuoteVO> quoteVOs) {
        super.startBatch();
        for (QuoteVO quoteVO : quoteVOs) {
            insertQuote(quoteVO);
        }
        super.executeBatch();
    }

    public void updateQuotes(List<QuoteVO> quoteVOs) {
        super.startBatch();
        for (QuoteVO quoteVO : quoteVOs) {
            updateQuote(quoteVO);
        }
        super.executeBatch();
    }

    public List<QuoteVO> getQuotes(PMDate date) {
        return (List<QuoteVO>) super.queryForList("getQuoteForDate", date.getIntVal());
    }

    public List<QuoteVO> getQuotes(String stockCode, PMDate frmDate, PMDate toDate) {
        Map paramMap = map(stockCode, frmDate, toDate);
        return (List<QuoteVO>) super.queryForList("getQuoteForStockDateRange", paramMap);
    }

    private Map map(String stockCode, PMDate frmDate, PMDate toDate) {
        Map paramMap = new HashMap();
        if (stockCode != null) paramMap.put("StockCode", stockCode);
        if (frmDate != null) paramMap.put("fromDate", frmDate.getIntVal());
        if (toDate != null) paramMap.put("toDate", toDate.getIntVal());
        return paramMap;
    }

    public QuoteVO getQuote(StockVO stockVO) {
        return (QuoteVO) super.queryForObject("getLastQuoteForID", stockVO.getId());
    }

    public Map<StockVO, List<QuoteVO>> quotes(PMDate stDate, PMDate enDate) {
        Map paramMap = map(null, stDate, enDate);
        List<QuoteVO> quotes = (List<QuoteVO>) super.queryForList("getQuoteForDateRange", paramMap);
        Map<StockVO, List<QuoteVO>> quotesMap = new HashMap<StockVO, List<QuoteVO>>();
        for (QuoteVO quote : quotes) {
            List<QuoteVO> quoteList = quotesMap.get(quote.getStockVO());
            if (quoteList == null) {
                quoteList = new ArrayList<QuoteVO>();
                quotesMap.put(quote.getStockVO(), quoteList);
            }
            quoteList.add(quote);
        }
        return quotesMap;
    }

    public QuoteVO quote(StockVO stockVO, PMDate date) {
        Map paramMap = new HashMap();
        paramMap.put("StockID", stockVO.getId());
        paramMap.put("PMDate", date.getIntVal());
        return (QuoteVO) super.queryForObject("getQuoteForStockIDDate", paramMap);

    }

    public void updateStockId(int fromStockId, int toStockId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("from", fromStockId);
        params.put("to", toStockId);
        params.put("table", "QUOTE");
        super.update("updateStockID", params);
    }

    public QuoteVO getQuote(String stockCode) {
        return (QuoteVO) super.queryForObject("getLastQuote", stockCode);
    }

    public QuoteVO getQuote(String stockCode, PMDate date) {
        Map paramMap = new HashMap();
        paramMap.put("StockCode", stockCode);
        paramMap.put("PMDate", date.getIntVal());
        return (QuoteVO) super.queryForObject("getQuoteForStockDate", paramMap);
    }

}
