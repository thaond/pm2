/*
 * Created on 23-Feb-2005
 *
 */
package pm.bo;

import pm.dao.ibatis.dao.IQuoteDAO;
import pm.util.PMDate;
import pm.vo.EquityQuote;
import pm.vo.StockVO;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import static pm.dao.ibatis.dao.DAOManager.getQuoteDAO;

public class EODQuoteLoader {

    public static EquityQuote[] getLastQuote(String[] stockCodes) {
        return new QuoteBO().getQuote(stockCodes);
    }

    public static EquityQuote getLastQuote(String stockCode) {
        return getQuoteDAO().getQuote(stockCode);
    }

    public static EquityQuote getLastQuote(StockVO stockCode) {
        return getQuoteDAO().getQuote(stockCode);
    }

    public static Vector<EquityQuote[]> getQuote(PMDate frmDate, PMDate toDate, String[] stockList) {
        return new QuoteBO().getQuotes(frmDate, toDate, stockList);
    }

    public static EquityQuote[] getQuote(PMDate frmDate, PMDate toDate, String stockCode) {
        return new QuoteBO().getQuotes(stockCode, frmDate, toDate);
    }

    public static Hashtable<String, EquityQuote> getQuote(PMDate date, Set setStkList) {
        Hashtable<String, EquityQuote> retVal = new Hashtable<String, EquityQuote>();
        IQuoteDAO quoteDAO = dao();
        for (Object object : setStkList) {
            String stockCode = object.toString();
            EquityQuote quote = quoteDAO.getQuote(stockCode, date);
            if (quote != null) {
                retVal.put(stockCode, quote);
            }
        }
        return retVal;
    }

    public static EquityQuote getQuote(StockVO stockVO, PMDate date) {
        return dao().quote(stockVO, date);
    }

    private static IQuoteDAO dao() {
        return getQuoteDAO();
    }

    public static List<EquityQuote> getQuote(PMDate date) {
        return dao().getQuotes(date);
    }
}
