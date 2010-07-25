package pm.dao.ibatis.dao;

import pm.util.PMDate;
import pm.vo.EquityQuote;
import pm.vo.StockVO;

import java.util.List;
import java.util.Map;

/**
 * Date: 01-Aug-2006
 * Time: 23:20:58
 */
public interface IQuoteDAO {

    public void insertQuote(EquityQuote quoteVO);

    public boolean updateQuote(EquityQuote quoteVO);

    public void insertQuotes(List<EquityQuote> quoteVOs);

    public void updateQuotes(List<EquityQuote> quoteVOs);

    public EquityQuote getQuote(String stockCode);

    public EquityQuote getQuote(String stockCode, PMDate date);

    public List<EquityQuote> getQuotes(String stockCode);

    public List<EquityQuote> getQuotes(PMDate date);

    public List<EquityQuote> getQuotes(String stockCode, PMDate frmDate, PMDate toDate);

    EquityQuote getQuote(StockVO stockVO);

    Map<StockVO, List<EquityQuote>> quotes(PMDate stDate, PMDate enDate);

    EquityQuote quote(StockVO stockVO, PMDate date);

    void updateStockId(int fromStockId, int toStockId);

    void updateAdjustedClose(String stockCode, PMDate exDate, float ratio);
}
