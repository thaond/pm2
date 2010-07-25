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
public class BuySellBO extends AbsStockAnalyzerBO {

    public boolean markData(QuoteIterator quoteIterator, PMDate stDate, PMDate enDate, boolean positive, boolean negative) {
        boolean retFlag = false;
        for (; quoteIterator.hasNext();) {
            EquityQuote quoteVO = quoteIterator.next();
            if (quoteVO.before(stDate)) continue;
            if (quoteVO.after(enDate)) break;
            if (positive && quoteVO.getLastPrice() > quoteVO.getAvgTradePrice()) {
                retFlag = true;
                quoteVO.addPickDetail(ANALYZER_LIST.BuySell.getPosDisplay());
                quoteVO.updateScoreCard(ANALYZER_LIST.BuySell.getWeightage());
            } else if (negative && quoteVO.getLastPrice() < quoteVO.getAvgTradePrice()) {
                retFlag = true;
                quoteVO.addPickDetail(ANALYZER_LIST.BuySell.getNegDisplay());
                quoteVO.updateScoreCard(-ANALYZER_LIST.BuySell.getWeightage());
            }
        }
        return retFlag;
    }
}
