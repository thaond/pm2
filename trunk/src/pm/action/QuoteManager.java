/*
 * Created on 23-Feb-2005
 *
 */
package pm.action;


import pm.bo.EODQuoteLoader;
import pm.net.LiveQuoteLoader;
import pm.util.AppConfig;
import pm.util.PMDate;
import pm.vo.QuoteVO;
import pm.vo.StockVO;

import java.util.List;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class QuoteManager {

    public static QuoteVO[] getLiveQuote(String[] stockCode) {
        if (Boolean.parseBoolean(AppConfig.liveQuote.Value)) {
            return LiveQuoteLoader.getQuote(stockCode);
        } else {
            return EODQuoteLoader.getLastQuote(stockCode);
        }
    }

    public static Vector<QuoteVO[]> getEODQuote(PMDate frmDate, PMDate toDate, String[] stockList) {
        return EODQuoteLoader.getQuote(frmDate, toDate, stockList);
    }

    public static String getQuotePage(String stockCode) {
        return LiveQuoteLoader.getQuotePage(stockCode);
    }

    public static QuoteVO eodQuote(StockVO stockVO) {
        return EODQuoteLoader.getLastQuote(stockVO);
    }

    public static QuoteVO eodQuote(StockVO stockVO, PMDate date) {
        return EODQuoteLoader.getQuote(stockVO, date);
    }

    public static List<QuoteVO> eodQuotes(PMDate date) {
        return EODQuoteLoader.getQuote(date);
    }
}
