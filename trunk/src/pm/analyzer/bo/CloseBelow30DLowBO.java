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
import pm.vo.QuoteVO;

/**
 * @author thiyagu
 *         <p/>
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CloseBelow30DLowBO extends AbsStockAnalyzerBO {

    public boolean markData(QuoteIterator quoteIterator, PMDate stDate, PMDate enDate, boolean positive, boolean negative) {
        boolean retFlag = false;
        if (!quoteIterator.movePtrToDate(stDate)) return false;

        for (; quoteIterator.hasNext();) {
            QuoteVO quoteVO = quoteIterator.next();
            if (quoteVO.after(enDate)) break;
            double low30d = 0;
            for (int i = -31; i < -1; i++) {
                QuoteVO prevQuote = quoteIterator.getItemFrmCurrPos(i);
                if (prevQuote == null) {
                    low30d = 0;
                    break;
                }
                if (low30d > prevQuote.getLow()) low30d = prevQuote.getLow();
            }

            if (low30d == 0) continue;

            double close = quoteVO.getLastPrice();
            if (close < low30d) {
                retFlag = true;
                quoteVO.addPickDetail(ANALYZER_LIST.CloseBelow30DLow.getNegDisplay());
                low30d = quoteVO.getLow();
                quoteVO.updateScoreCard(-ANALYZER_LIST.CloseBelow30DLow.getWeightage());
            }
        }
        return retFlag;
    }
}
