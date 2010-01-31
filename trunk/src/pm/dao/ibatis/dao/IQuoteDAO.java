package pm.dao.ibatis.dao;

import pm.util.PMDate;
import pm.vo.QuoteVO;
import pm.vo.StockVO;

import java.util.List;
import java.util.Map;

/**
 * Date: 01-Aug-2006
 * Time: 23:20:58
 */
public interface IQuoteDAO {

    public void insertQuote(QuoteVO quoteVO);

    public boolean updateQuote(QuoteVO quoteVO);

    public void insertQuotes(List<QuoteVO> quoteVOs);

    public void updateQuotes(List<QuoteVO> quoteVOs);

    public QuoteVO getQuote(String stockCode);

    public QuoteVO getQuote(String stockCode, PMDate date);

    public List<QuoteVO> getQuotes(String stockCode);

    public List<QuoteVO> getQuotes(PMDate date);

    public List<QuoteVO> getQuotes(String stockCode, PMDate frmDate, PMDate toDate);

    QuoteVO getQuote(StockVO stockVO);

    Map<StockVO, List<QuoteVO>> quotes(PMDate stDate, PMDate enDate);

    QuoteVO quote(StockVO stockVO, PMDate date);

    void updateStockId(int fromStockId, int toStockId);

    void updateAdjustedClose(String stockCode, PMDate exDate, float ratio);
}
