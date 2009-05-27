/*
 * Created on Jan 6, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package pm.analyzer.bo;

import pm.util.AppConst.ANALYZER_LIST;
import pm.util.DateIterator;
import pm.util.PMDate;
import pm.util.QuoteIterator;
import pm.vo.QuoteVO;

/**
 * @author thiyagu
 *         <p/>
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CloseAbove52WeekHighBO extends AbsStockAnalyzerBO {

    public boolean markData(QuoteIterator quoteIterator, PMDate stDate, PMDate enDate, boolean positive, boolean negative) {
        boolean retFlag = false;
        if (!quoteIterator.movePtrToDate(stDate)) return false;
        DateIterator dateIterator = new DateIterator(stDate, enDate);
        for (; dateIterator.hasNext();) {
            PMDate date = dateIterator.next();
            if (!quoteIterator.movePtrToDate(date.get52WeeksBefore())) break;

            double high52W = 0;
            for (; quoteIterator.hasNext();) {
                QuoteVO quoteVO = quoteIterator.next();
                if (quoteVO.getDate().equals(date)) {
                    double close = quoteVO.getClose();
                    if (close > high52W) {
                        retFlag = true;
                        quoteVO.addPickDetail(ANALYZER_LIST.CloseAbove52WHigh.getPosDisplay());
                        quoteVO.updateScoreCard(ANALYZER_LIST.CloseAbove52WHigh.getWeightage());
                    }
                    break;
                }
                if (high52W < quoteVO.getClose()) high52W = quoteVO.getClose();
            }
            QuoteVO quoteVO = quoteIterator.next();
        }
        return retFlag;
    }
}