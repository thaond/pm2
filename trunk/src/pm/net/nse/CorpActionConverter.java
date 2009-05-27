package pm.net.nse;

import org.apache.log4j.Logger;
import pm.util.AppConst.COMPANY_ACTION_TYPE;
import pm.util.BusinessLogger;
import pm.util.NumberExtractor;
import pm.util.PMDate;
import pm.util.enumlist.AppConfigWrapper;
import pm.vo.CompanyActionVO;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

public class CorpActionConverter {

    public static final String FV_STRING = "FV";

    public static final String F_V_STRING = "F.V";

    public static final String SPLT_STRING = "SPLT";

    public static final String DIV_STRING = "DIV";

    public static final String SPL_STRING = "SPL";

    public static final String INT_STRING = "INT";

    public static final String BONUS_STRING = "BON";


    private static final Logger logger = Logger.getLogger(CorpActionConverter.class);

    public Vector<CompanyActionVO> processCorpAction(String stockCode, Hashtable<PMDate, String> actionDetails) {
        Vector<CompanyActionVO> retVal = new Vector<CompanyActionVO>();
        for (PMDate date : actionDetails.keySet()) {
            Vector<CompanyActionVO> actionList = convertToActionVO(stockCode,
                    date, actionDetails.get(date));
            retVal.addAll(actionList);
        }
        return retVal;
    }

    //TODO Processing RIGHTS
    //TODO Process actions items by items (delimitor /) and record not processed items

    Vector<CompanyActionVO> convertToActionVO(String stockCode, PMDate date,
                                              String rawActionLine) {
        Vector<CompanyActionVO> actionList = new Vector<CompanyActionVO>();
        boolean flagProcessed = false;
        if (rawActionLine.indexOf(DIV_STRING) != -1) {
            flagProcessed = true;
            validateAndAddAction("Divident", stockCode, date, rawActionLine,
                    actionList, processDivident(stockCode, date, rawActionLine));
        }
        if (rawActionLine.indexOf(FV_STRING) != -1
                || rawActionLine.indexOf(F_V_STRING) != -1
                || rawActionLine.indexOf(SPLT_STRING) != -1) {
            flagProcessed = true;
            validateAndAddAction("Split", stockCode, date, rawActionLine,
                    actionList, processSplit(stockCode, date, rawActionLine));
        }
        if (rawActionLine.indexOf(BONUS_STRING) != -1) {
            flagProcessed = true;
            validateAndAddAction("Bonus", stockCode, date, rawActionLine,
                    actionList, processBonus(stockCode, date, rawActionLine));
        }
        if (!flagProcessed) {
            if ((rawActionLine.indexOf("AGM") != -1 || rawActionLine.indexOf("INTEREST") != -1 ||
                    rawActionLine.indexOf("BOOK CLOSURE") != -1 || rawActionLine.indexOf("EGM") != -1)
                    && rawActionLine.indexOf('/') == -1) {
                //do nothing
            } else {
                recordAlert("UNKNOWN", stockCode, date, rawActionLine);
            }
        }

        return actionList;
    }

    private void validateAndAddAction(String msg, String stockCode,
                                      PMDate date, String rawActionLine,
                                      Vector<CompanyActionVO> actionList, CompanyActionVO actionVO) {
        if (actionVO != null) {
            actionList.add(actionVO);
        } else {
            recordAlert(msg, stockCode, date, rawActionLine);
        }
    }

    protected void recordAlert(String msg, String stockCode, PMDate date,
                               String rawActionLine) {
        String errorLine = "Converter Error : - " + msg + " : " + stockCode + " [" + date + "] "
                + rawActionLine;
        BusinessLogger.recordMsg(errorLine);
    }

    public void recordMsg(String msg) {
        synchronized (CorpActionConverter.class) {
            try {
                PrintWriter pw = new PrintWriter(new FileWriter(AppConfigWrapper.logFileDir.Value + "/CorpActionConverterAlert.log", true));
                pw.println(msg);
                pw.close();
            } catch (IOException e) {
                logger.error(e, e);
                logger.error(msg);
            }
        }
    }

    CompanyActionVO processSplit(String stockCode, PMDate date,
                                 String rawActionLine) {
        int st = rawActionLine.indexOf(FV_STRING);
        if (st == -1) {
            st = rawActionLine.indexOf(F_V_STRING);
        }
        if (st == -1) {
            st = rawActionLine.indexOf(SPLT_STRING);
        }
        if (st == -1) {
            return null;
        }
        NumberExtractor extractor = new NumberExtractor(rawActionLine
                .substring(st));
        if (!extractor.hasMoreElements()) {
            return null;
        }
        float currFaceValue = extractor.nextElement();
        if (!extractor.hasMoreElements()) {
            return null;
        }
        float newFaceValue = extractor.nextElement();
        return new CompanyActionVO(COMPANY_ACTION_TYPE.Split, date, stockCode,
                newFaceValue, currFaceValue);
    }

    CompanyActionVO processDivident(String stockCode, PMDate date,
                                    String rawActionLine) {
        int st = rawActionLine.indexOf(DIV_STRING);
        if (st == -1) {
            return null;
        }
        String substring = rawActionLine
                .substring(st);
        NumberExtractor extractor = new NumberExtractor(substring);
        if (extractor.hasMoreElements()) {
            CompanyActionVO actionVO = new CompanyActionVO(
                    COMPANY_ACTION_TYPE.Divident, date, stockCode, extractor
                    .nextElement(), 1f);
            actionVO.setPercentageValue(extractor.isPercentage());
            st = extractor.getIndex();
            if (st < rawActionLine.length()) {
                int retVal = checkForAdditionalDivident(actionVO, substring
                        .substring(st), SPL_STRING);
                if (retVal != -1) st = retVal;
                retVal = checkForAdditionalDivident(actionVO, substring
                        .substring(st), INT_STRING);
                if (retVal != -1) st = retVal;
                st = getNextItem(substring, st); //check for items after /
                if (st != -1 && st < rawActionLine.length()) {
                    checkForAdditionalDivident(actionVO, substring
                            .substring(st), DIV_STRING);
                }
            }
            return actionVO;
        }
        return null;
    }

    int getNextItem(String substring, int index) {
        int slashIndex = substring.indexOf('/', index);
        int plusIndex = substring.indexOf('+', index);
        if (slashIndex != -1 && plusIndex == -1) {
            return slashIndex + 1;
        }
        if (plusIndex != -1 && slashIndex == -1) {
            return plusIndex + 1;
        }
        if (slashIndex == -1 && plusIndex == -1) {
            return -1;
        }
        if (slashIndex < plusIndex) {
            return slashIndex + 1;
        } else {
            return plusIndex + 1;
        }
    }

    int checkForAdditionalDivident(CompanyActionVO actionVO,
                                   String rawActionLine, String otherDividentCode) {
        int st = rawActionLine.indexOf(otherDividentCode);
        if (st == -1) {
            return -1;
        }
        int en = rawActionLine.indexOf('/');
        if (en != -1 && en < st) {
            return -1;
        }

        NumberExtractor extractor = new NumberExtractor(rawActionLine
                .substring(st));
        if (extractor.hasMoreElements()) {
            actionVO.setDsbValue(actionVO.getDsbValue()
                    + extractor.nextElement());
        }
        return extractor.getIndex();
    }

    CompanyActionVO processBonus(String stockCode, PMDate date,
                                 String rawActionLine) {
        int st = rawActionLine.indexOf(BONUS_STRING);
        if (st == -1) {
            return null;
        }
        NumberExtractor extractor = new NumberExtractor(rawActionLine
                .substring(st));
        if (!extractor.hasMoreElements()) {
            return null;
        }
        float bonus = extractor.nextElement();
        if (!extractor.hasMoreElements()) {
            return null;
        }
        float base = extractor.nextElement();
        return new CompanyActionVO(COMPANY_ACTION_TYPE.Bonus, date, stockCode,
                bonus, base);
    }

}
