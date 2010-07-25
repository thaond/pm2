/*
 * Created on Jan 6, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package pm.analyzer.bo;

import pm.util.AppConst.ANALYZER_LIST;
import pm.util.PMDate;
import pm.util.QuoteIterator;
import pm.vo.EquityQuote;

/**
 * @author thiyagu
 *         <p/>
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CloseBelow5DLowBO extends AbsStockAnalyzerBO {

    public boolean markData(QuoteIterator quoteIterator, PMDate stDate, PMDate enDate, boolean positive, boolean negative) {
        boolean retFlag = false;
        if (!quoteIterator.movePtrToDate(stDate)) return false;

        for (; quoteIterator.hasNext();) {
            EquityQuote quoteVO = quoteIterator.next();
            if (quoteVO.after(enDate)) break;
            double low5d = 0;
            for (int i = -6; i < -1; i++) {
                EquityQuote prevQuote = quoteIterator.getItemFrmCurrPos(i);
                if (prevQuote == null) {
                    low5d = 0;
                    break;
                }
                if (low5d > prevQuote.getLow()) low5d = prevQuote.getLow();
            }

            if (low5d == 0) continue;

            double close = quoteVO.getLastPrice();
            if (close < low5d) {
                retFlag = true;
                quoteVO.addPickDetail(ANALYZER_LIST.CloseBelow5DLow.getNegDisplay());
                low5d = quoteVO.getLow();
                quoteVO.updateScoreCard(ANALYZER_LIST.CloseBelow5DLow.getWeightage());
            }
        }
        return retFlag;
    }
}
