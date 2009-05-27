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
public class FlashQuoteBO extends AbsStockAnalyzerBO {

    public boolean markData(QuoteIterator quoteIterator, PMDate stDate, PMDate enDate, boolean positive, boolean negative) {
        boolean retFlag = false;
        if (!quoteIterator.movePtrToDate(stDate)) return false;
        for (; quoteIterator.hasNext();) {
            QuoteVO quoteVO = quoteIterator.next();
            if (quoteVO.after(enDate)) break;
            if (quoteVO.getHigh() > (quoteVO.getPrevClose() * 1.1)) {
                retFlag = true;
                quoteVO.addPickDetail(ANALYZER_LIST.FlashQuote.getPosDisplay());
                quoteVO.updateScoreCard(ANALYZER_LIST.FlashQuote.getWeightage());
            }
        }
        return retFlag;
    }
}
