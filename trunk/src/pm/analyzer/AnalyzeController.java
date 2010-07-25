/*
 * Created on Dec 13, 2003
 *
 */
package pm.analyzer;

import pm.dao.ibatis.dao.DAOManager;
import pm.util.AppConst.ANALYZER_LIST;
import pm.util.AppConst.STOCK_PICK_TYPE;
import pm.util.PMDate;
import pm.util.QuoteIterator;

import java.util.List;
import java.util.Vector;

/**
 * @author thiyagu
 */
public class AnalyzeController {
    public static Vector<QuoteIterator> getAnalyzedData(PMDate frmDate, PMDate toDate, ANALYZER_LIST[] analyzerList, STOCK_PICK_TYPE funMode, boolean positive, boolean negative) {
        Vector<QuoteIterator> retVal = new Vector<QuoteIterator>();
        PMDate histDate = frmDate.get52WeeksBefore();
        List<QuoteIterator> quoteIterators = QuoteIterator.getIterators(histDate, toDate);
        for (QuoteIterator quoteIterator : quoteIterators) {
            if (analyzeData(toDate, analyzerList, frmDate, positive, negative, funMode, quoteIterator)) {
                retVal.add(quoteIterator);
            }
        }
        return retVal;

    }

    public static QuoteIterator getAnalyzedData(String stockCode, PMDate frmDate, PMDate toDate, ANALYZER_LIST[] analyzerList, STOCK_PICK_TYPE funMode, boolean positive, boolean negative) {
        PMDate histDate = DAOManager.getDateDAO().getDate(frmDate, -52);
        if (histDate == null) {
            return null;
        }
        QuoteIterator quoteIterator = new QuoteIterator(histDate, toDate, stockCode);
        boolean hasPick = analyzeData(toDate, analyzerList, frmDate, positive, negative, funMode, quoteIterator);
        return hasPick ? quoteIterator : null;
    }

    private static boolean analyzeData(PMDate toDate, ANALYZER_LIST[] analyzerList, PMDate frmDate, boolean positive, boolean negative, STOCK_PICK_TYPE funMode, QuoteIterator quoteIterator) {
        boolean isPick = false;
        for (ANALYZER_LIST analyzer : analyzerList) {
            boolean thisBO = analyzer.getAnalyzer().markData(quoteIterator, frmDate, toDate, positive, negative);
            if (funMode == STOCK_PICK_TYPE.And && !thisBO) {
                isPick = false;
                break;
            }
            if (thisBO && !isPick) {
                isPick = true;
            }
        }
        quoteIterator.setDataRange(frmDate, toDate);
        return isPick;
    }

    public static void main(String[] arg) {
        ANALYZER_LIST[] analyzerList = new ANALYZER_LIST[1];
        analyzerList[0] = ANALYZER_LIST.MACD;
//    	Vector<EquityQuote> dataItem = getAnalyzedData("ACC",new PMDate(26,8,2005),new PMDate(26,8,2005),analyzerList,STOCK_PICK_TYPE.And,
//    			true,true);
    }
}
