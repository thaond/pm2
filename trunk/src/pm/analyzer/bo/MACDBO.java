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
import pm.vo.EquityQuote;

/**
 * @author thiyagu
 *         <p/>
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MACDBO extends AbsStockAnalyzerBO {

    private int _MAX = 26;
    private int _FACTOR = 9;
    private int _FAST = 12;
    private int _SLOW = 26;
    private int _SIGNAL = 9;

    public boolean markData(QuoteIterator quoteIterator, PMDate stDate, PMDate enDate, boolean positive, boolean negative) {
        boolean retFlag = false;

        if (!quoteIterator.movePtrToDate(stDate)) return false;
        double[] signal = getSignal(quoteIterator, stDate, enDate);
        if (signal == null) return false;
        if (!quoteIterator.movePtrToDate(stDate)) return false;
        for (int i = 0; quoteIterator.hasNext(); i++) {
            EquityQuote quoteVO = quoteIterator.next();

            if (quoteVO.after(enDate)) break;
            if (signal[i] * signal[i + 1] < 0) {
                if (signal[i + 1] > 0) {
                    if (positive) {
                        retFlag = true;
                        quoteVO.addPickDetail(ANALYZER_LIST.MACD.getPosDisplay());
                        quoteVO.updateScoreCard(ANALYZER_LIST.MACD.getWeightage());
                    }
                } else {
                    if (negative) {
                        retFlag = true;
                        quoteVO.addPickDetail(ANALYZER_LIST.MACD.getNegDisplay());
                        quoteVO.updateScoreCard(-ANALYZER_LIST.MACD.getWeightage());
                    }
                }
            }
        }

        return retFlag;
    }

    private double[] getSignal(QuoteIterator quoteIterator, PMDate stDate, PMDate enDate) {

        if (quoteIterator.movePtrToFirst() < _MAX * 2) return null;

        double fastS = 2.0 / (1.0 + (double) _FAST);
        double fastD = 1 - fastS;

        double slowS = 2.0 / (1.0 + (double) _SLOW);
        double slowD = 1 - slowS;

        double signalS = 2.0 / (1.0 + (double) _SIGNAL);
        double signalD = 1 - signalS;

        double mAVG = 0.0;
        for (int i = 0; i < _MAX; i++) {
            mAVG += quoteIterator.next().getLastPrice();
        }
        mAVG /= _MAX;
        //mAVG = ((HQuoteVO)histDataItem.elementAt(0)).getClose();
        //System.out.println(((HQuoteVO)histDataItem.elementAt(0)).getDate());
        double prevClose = 0;
        double N = mAVG, M = mAVG, N_M = 0.0, S = 0.0;
        for (; quoteIterator.hasNext();) {
            EquityQuote quoteVO = quoteIterator.next();
            if (!quoteVO.getDate().before(stDate)) break;
            double close = quoteVO.getLastPrice();
            N = close * fastS + N * fastD;
            M = close * slowS + M * slowD;
            N_M = N - M;
            if (S != 0)
                S = N_M * signalS + S * signalD;
            else
                S = N_M;
            prevClose = close;
        }
        double[] retVal = new double[(new DateIterator(stDate, enDate)).size() + 1];
        int count = 0;
        double cross = ((S * signalD) + (N * fastD * signalS) - (M * slowD * signalS) - (N * fastD) + (M * slowD)) / (fastS - slowS - (fastS * signalS) + (slowS * signalS));
        retVal[count++] = prevClose - cross;
        quoteIterator.movePtr(-1);
        for (; quoteIterator.hasNext();) {
            EquityQuote quoteVO = quoteIterator.next();
            //System.out.println(quoteVO.getDate());
            if (quoteVO.after(enDate)) break;
            double close = quoteVO.getLastPrice();
            N = close * fastS + N * fastD;
            M = close * slowS + M * slowD;
            N_M = N - M;
            S = N_M * signalS + S * signalD;
            cross = ((S * signalD) + (N * fastD * signalS) - (M * slowD * signalS) - (N * fastD) + (M * slowD)) / (fastS - slowS - (fastS * signalS) + (slowS * signalS));
            retVal[count++] = close - cross;
        }
        return retVal;
    }
}
