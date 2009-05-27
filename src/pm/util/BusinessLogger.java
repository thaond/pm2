/*
 * Created on 03-Feb-2005
 *
 */
package pm.util;

import org.apache.log4j.Logger;
import pm.util.enumlist.AppConfigWrapper;
import pm.util.enumlist.IPOAction;
import pm.vo.CompanyActionVO;
import pm.vo.IPOVO;
import pm.vo.TransactionVO;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class BusinessLogger {
    public static final String TRANSACTION_LOG_FILE = "Log_Transaction.log";

    private static final String IPO_TRANSACTION_LOG_FILE = "Log_IPO_Transaction.log";

    public static final String COMPANY_ACTION_LOG_FILE = "Log_CompanyAction.log";

    private static final String SYMBOL_CHANGE_LOG_FILE = "Log_SymbolChange.log";

    private static Logger logger = Logger.getLogger(BusinessLogger.class);
    private static final String SYMBOLCHANGE_DELIMITOR = ",";

    public static String getSymbolChangeLogFilePath() {
        return AppConfigWrapper.logFileDir.Value + "/" + SYMBOL_CHANGE_LOG_FILE;
    }

    public static String getTransLogFilePath() {
        return AppConfigWrapper.logFileDir.Value + "/" + TRANSACTION_LOG_FILE;
    }

    public static String getIPOTransLogFilePath() {
        return AppConfigWrapper.logFileDir.Value + "/" + IPO_TRANSACTION_LOG_FILE;
    }

    public static String getCompActLogFilePath() {
        return AppConfigWrapper.logFileDir.Value + "/" + COMPANY_ACTION_LOG_FILE;
    }

    public static boolean logTransaction(CompanyActionVO actionVO) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(
                    getCompActLogFilePath(), true));
            pw.println(actionVO.toWrite());
            pw.close();
        } catch (IOException e) {
            logger.error(e, e);
            return false;
        }
        return true;
    }

    public static boolean logTransaction(TransactionVO transVO) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(
                    getTransLogFilePath(), true));
            pw.println(transVO.getDetails());
            pw.close();
            return true;
        } catch (IOException e) {
            logger.error(e, e);
            return false;
        }
    }

    public static Vector<TransactionVO> getTransactionLogs() {
        Vector<TransactionVO> logs = new Vector<TransactionVO>();
        File inpFile = new File(BusinessLogger.getTransLogFilePath());
        if (inpFile.exists()) {
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(inpFile));
                String line;
                while ((line = br.readLine()) != null) {
                    logs.add(new TransactionVO(line));
                }
                br.close();
            } catch (FileNotFoundException e) {
                logger.error(e, e);
            } catch (IOException e) {
                logger.error(e, e);
            } catch (Exception e) {
                logger.error(e, e);
            }
        }
        return logs;

    }

    public static void logSymbolChange(String oldStockCode, String newStockCode) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(
                    getSymbolChangeLogFilePath(), true));
            pw.println(oldStockCode + SYMBOLCHANGE_DELIMITOR + newStockCode);
            pw.close();
        } catch (IOException e) {
            logger.error(e, e);
        }
    }

    public static Map getSymbolChanges() {
        Map retVal = new HashMap();
        try {
            BufferedReader br = new BufferedReader(new FileReader(getSymbolChangeLogFilePath()));
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer stk = new StringTokenizer(line, SYMBOLCHANGE_DELIMITOR);
                String oldStockCode = stk.nextToken();
                String newStockCode = stk.nextToken();
                retVal.put(oldStockCode, newStockCode);
            }
            br.close();
            return retVal;
        } catch (FileNotFoundException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
        return null;
    }

    public static void logTransaction(IPOAction action, IPOVO ipovo) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(
                    getIPOTransLogFilePath(), true));
            StringBuffer sb = new StringBuffer();
            sb.append(action).append(AppConst.DELIMITER_COMMA);
            sb.append(ipovo.toWrite());
            pw.println(sb.toString());
            pw.close();
        } catch (IOException e) {
            logger.error(e, e);
        }
    }

    public static synchronized void recordMsg(String msg) {
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
