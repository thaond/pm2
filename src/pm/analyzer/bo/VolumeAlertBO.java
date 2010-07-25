package pm.analyzer.bo;

import pm.util.AppConst.ANALYZER_LIST;
import pm.util.PMDate;
import pm.util.QuoteIterator;
import pm.vo.EquityQuote;

public class VolumeAlertBO extends AbsStockAnalyzerBO {

    private static int _AVGDAYS = 20;
    private static String FACTOR = "Diff%";

    public VolumeAlertBO() {
        factors.put(FACTOR, 75f);
    }

    public boolean markData(QuoteIterator quoteIterator, PMDate stDate, PMDate enDate, boolean positive, boolean negative) {
        boolean retFlag = false;
        if (!quoteIterator.movePtrToDate(stDate)) return false;
        for (; quoteIterator.hasNext();) {
            EquityQuote quoteVO = quoteIterator.next();
            if (quoteVO.after(enDate)) break;
            float volumeTot = 0;
            for (int i = -_AVGDAYS - 1; i < -1; i++) {
                EquityQuote prevQuote = quoteIterator.getItemFrmCurrPos(i);
                if (prevQuote == null) {
                    volumeTot = 0;
                    break;
                }
                volumeTot += prevQuote.getPerDeliveryQty();
            }

            if (volumeTot == 0) continue;
            float movAvg = volumeTot / _AVGDAYS;
            float factor = factors.get(FACTOR) / 100f;
            if (positive && quoteVO.getVolume() >= (movAvg * (1.0 + factor))) {
                quoteVO.addPickDetail(ANALYZER_LIST.VolumeAlert.getPosDisplay());
                quoteVO.updateScoreCard(ANALYZER_LIST.VolumeAlert.getWeightage());
                retFlag = true;
            }
            if (negative && quoteVO.getVolume() <= (movAvg * (1.0 - factor))) {
                quoteVO.addPickDetail(ANALYZER_LIST.VolumeAlert.getNegDisplay());
                quoteVO.updateScoreCard(-ANALYZER_LIST.VolumeAlert.getWeightage());
                retFlag = true;
            }
        }
        return retFlag;
    }

}
