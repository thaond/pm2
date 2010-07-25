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
public class PositiveMove5DBO extends AbsStockAnalyzerBO {

    public boolean markData(QuoteIterator quoteIterator, PMDate stDate, PMDate enDate, boolean positive, boolean negative) {
        boolean retFlag = false;
        if (!quoteIterator.movePtrToDate(stDate)) return false;

        for (; quoteIterator.hasNext();) {
            EquityQuote quoteVO = quoteIterator.next();
            if (quoteVO.after(enDate)) break;
            boolean positiveMove = true;
            for (int i = -5; i < -1; i++) {
                EquityQuote prevQuote = quoteIterator.getItemFrmCurrPos(i);
                if (prevQuote == null) {
                    positiveMove = false;
                    break;
                }
                if (prevQuote.getLastPrice() < prevQuote.getPrevClose()) {
                    positiveMove = false;
                    break;
                }
            }

            if (!positiveMove) continue;

            if (quoteVO.getLastPrice() > quoteVO.getPrevClose()) {
                quoteVO.addPickDetail(ANALYZER_LIST.PositiveMove5D.getPosDisplay());
                quoteVO.updateScoreCard(ANALYZER_LIST.PositiveMove5D.getWeightage());
                retFlag = true;
            }
        }
        return retFlag;
    }
}
