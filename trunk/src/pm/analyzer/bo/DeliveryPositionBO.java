/*
 * Created on 23-Feb-2005
 *
 */
package pm.analyzer.bo;

import pm.util.AppConst.ANALYZER_LIST;
import pm.util.PMDate;
import pm.util.QuoteIterator;
import pm.vo.EquityQuote;

/**
 * @author thiyagu1
 */
public class DeliveryPositionBO extends AbsStockAnalyzerBO {

    public static final int _AVGDAYS = 20;
    static String FACTOR = "Diff%";

    public DeliveryPositionBO() {
        factors.put(FACTOR, 25f);
    }

    public boolean markData(QuoteIterator quoteIterator, PMDate stDate, PMDate enDate, boolean positive, boolean negative) {
        float factor = factors.get(FACTOR) / 100f;
        boolean retFlag = false;
        if (!quoteIterator.movePtrToDate(stDate)) return false;
        for (; quoteIterator.hasNext();) {
            EquityQuote quoteVO = quoteIterator.next();
            if (quoteVO.after(enDate)) break;
            float delivPerTot = 0;
            for (int i = -_AVGDAYS - 1; i < -1; i++) {
                EquityQuote prevQuote = quoteIterator.getItemFrmCurrPos(i);
                if (prevQuote == null) {
                    delivPerTot = 0;
                    break;
                }
                delivPerTot += prevQuote.getPerDeliveryQty();
            }

            if (delivPerTot == 0) continue;
            float movAvg = delivPerTot / _AVGDAYS;
            if (positive && quoteVO.getPerDeliveryQty() >= (movAvg * (1.0 + factor))) {
                quoteVO.addPickDetail(ANALYZER_LIST.FlashDeliveryPosition.getPosDisplay());
                quoteVO.updateScoreCard(ANALYZER_LIST.FlashDeliveryPosition.getWeightage());
                retFlag = true;
            }
            if (negative && quoteVO.getPerDeliveryQty() <= (movAvg * (1.0 - factor))) {
                quoteVO.addPickDetail(ANALYZER_LIST.FlashDeliveryPosition.getNegDisplay());
                quoteVO.updateScoreCard(-ANALYZER_LIST.FlashDeliveryPosition.getWeightage());
                retFlag = true;
            }
        }
        return retFlag;
    }

}
