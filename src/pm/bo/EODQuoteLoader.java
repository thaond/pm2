/*
 * Created on 23-Feb-2005
 *
 */
package pm.bo;

import static pm.dao.ibatis.dao.DAOManager.getQuoteDAO;
import pm.dao.ibatis.dao.IQuoteDAO;
import pm.util.PMDate;
import pm.vo.QuoteVO;
import pm.vo.StockVO;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class EODQuoteLoader {

    public static QuoteVO[] getLastQuote(String[] stockCodes) {
        return new QuoteBO().getQuote(stockCodes);
    }

    public static QuoteVO getLastQuote(String stockCode) {
        return getQuoteDAO().getQuote(stockCode);
    }

    public static QuoteVO getLastQuote(StockVO stockCode) {
        return getQuoteDAO().getQuote(stockCode);
    }

    public static Vector<QuoteVO[]> getQuote(PMDate frmDate, PMDate toDate, String[] stockList) {
        return new QuoteBO().getQuotes(frmDate, toDate, stockList);
    }

    public static QuoteVO[] getQuote(PMDate frmDate, PMDate toDate, String stockCode) {
        return new QuoteBO().getQuotes(stockCode, frmDate, toDate);
    }

    public static Hashtable<String, QuoteVO> getQuote(PMDate date, Set setStkList) {
        Hashtable<String, QuoteVO> retVal = new Hashtable<String, QuoteVO>();
        IQuoteDAO quoteDAO = dao();
        for (Object object : setStkList) {
            String stockCode = object.toString();
            QuoteVO quote = quoteDAO.getQuote(stockCode, date);
            if (quote != null) {
                retVal.put(stockCode, quote);
            }
        }
        return retVal;
    }

    public static QuoteVO getQuote(StockVO stockVO, PMDate date) {
        return dao().quote(stockVO, date);
    }

    private static IQuoteDAO dao() {
        return getQuoteDAO();
    }

    public static List<QuoteVO> getQuote(PMDate date) {
        return dao().getQuotes(date);
    }
}
