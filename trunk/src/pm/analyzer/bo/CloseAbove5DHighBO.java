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
public class CloseAbove5DHighBO extends AbsStockAnalyzerBO {

    public boolean markData(QuoteIterator quoteIterator, PMDate stDate, PMDate enDate, boolean positive, boolean negative) {
        boolean retFlag = false;
        if (!quoteIterator.movePtrToDate(stDate)) return false;

        for (; quoteIterator.hasNext();) {
            EquityQuote quoteVO = quoteIterator.next();
            if (quoteVO.after(enDate)) break;
            double high5d = 0;
            for (int i = -6; i < -1; i++) {
                EquityQuote prevQuote = quoteIterator.getItemFrmCurrPos(i);
                if (prevQuote == null) {
                    high5d = 0;
                    break;
                }
                if (high5d < prevQuote.getHigh()) high5d = prevQuote.getHigh();
            }

            if (high5d == 0) continue;
            double close = quoteVO.getLastPrice();
            if (close > high5d) {
                retFlag = true;
                quoteVO.addPickDetail(ANALYZER_LIST.CloseAbove5DHigh.getPosDisplay());
                high5d = quoteVO.getHigh();
                quoteVO.updateScoreCard(ANALYZER_LIST.CloseAbove5DHigh.getWeightage());
            }
        }
        return retFlag;
    }
}
