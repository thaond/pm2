package pm.analyzer;

import org.apache.log4j.Logger;
import pm.util.AppConst.ANALYZER_LIST;
import pm.util.AppConst.STOCK_PICK_TYPE;
import pm.util.PMDate;
import pm.util.QuoteIterator;
import pm.vo.QuoteVO;

public class ScoreCard {

    private static Logger logger = Logger.getLogger(ScoreCard.class);

    public static float getScore(String stockCode, ANALYZER_LIST[] analyzerList, boolean posNeg) {
        StringBuffer sb = new StringBuffer();
        for (ANALYZER_LIST analyzer : analyzerList) {
            if (posNeg) sb.append(analyzer.getPosDisplay());
            else sb.append(analyzer.getNegDisplay());
            sb.append(" ");
        }
        QuoteIterator data = AnalyzeController.getAnalyzedData(stockCode, new PMDate(3, 5, 1999), new PMDate(), analyzerList, STOCK_PICK_TYPE.And, posNeg, !posNeg);
        if (data == null) {
            System.out.print("Error getting analyzed report");
            return 0;
        }
        int occurrence = 0;
        int sucess = 0;
        String picks = sb.toString();
        data.movePtrToFirst();
        for (; data.hasNext();) {
            QuoteVO quoteVO = data.next();
            if (quoteVO.getPickDetails().equals(picks)) {
                occurrence++;
                if (checkSucess(data, quoteVO)) sucess++;
            }
        }
        return (float) sucess / (float) occurrence;
    }

    private static boolean checkSucess(QuoteIterator data, QuoteVO quoteVO) {
        boolean retVal = false;
        data.mark();
        data.movePtrToDate(quoteVO.getDate());
        data.next();
        if (data.hasNext()) {
            QuoteVO nextDay = data.next();
            System.out.println(nextDay.getPerChange());
            if (nextDay.getPerChange() >= 1.0f) retVal = true;
        }
        data.reset();
        return retVal;
    }

    public static void main(String[] args) {
        ANALYZER_LIST[] analyzerList = new ANALYZER_LIST[1];
        analyzerList[0] = ANALYZER_LIST.CloseAbove30DHigh;
        float score = getScore("INFOSYSTCH", analyzerList, true);
        System.out.println(score);
    }

}
