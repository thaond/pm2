/*
 * Created on 15-Feb-2005
 *
 */
package pm.util;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * @author thiyagu1
 */
public enum AppConfig {
    proxyServer, proxyPort, quoteServer,
    alertSleepTime, marketOpenTimeHH, marketOpenTimeMM, marketCloseTimeHH,
    marketCloseTimeMM, mailServer, toMailId, fromMailId, mailSubject,
    EODRunFlag, EODRunHH, EODRunMM,
    liveQuote, useProxy, dataDir,
    dataDownloadDir, Log_Pattern, Log_Level, maxThreadCount,
    corpActionDownloadErrorList, corpResultDownloadErrorList,
    dateLastDeliveryPosition, dateLastBhavCopy,
    dateCORPACTIONSYNCHRONIZER, statusCORPACTIONSYNCHRONIZER,
    dateCORPACTIONDOWNLOADMANAGER, statusCORPACTIONDOWNLOADMANAGER,
    dateCORPRESULTDOWNLOADMANAGER, statusCORPRESULTDOWNLOADMANAGER,
    dateEODDOWNLOADMANAGER, statusEODDOWNLOADMANAGER,
    dateSTOCKLISTDOWNLOADER, statusSTOCKLISTDOWNLOADER,
    dateMARKETHOLIDAYDOWNLOADER, statusMARKETHOLIDAYDOWNLOADER,
    DB_DRIVER, DB_URL, DB_USER, DB_PASSWORD,
    HP_CHART_STOCKCODE, DEFAULT_PORTFOLIO, runBackgroundTask,
    iciciUserName, iciciPasswd, dateICICITRANSACTIONSYNCHRONIZER, statusICICITRANSACTIONSYNCHRONIZER,
    ENABLE_CACHE;

    public String Value = null;
    public static Logger logger = Logger.getLogger(AppConfig.class);

    static {
        loadProperties();
    }

    private static void loadProperties() {
        java.util.Properties prop = new java.util.Properties();
        try {
            prop.load(new FileInputStream("pm.properties"));
            for (AppConfig key : AppConfig.values()) {
                key.Value = prop.getProperty(key.name());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadProperties() {
        loadProperties();
    }

    public static boolean saveConfigDetails() {
        OutputStream outs = null;
        try {
            outs = new FileOutputStream("pm.properties");
            java.util.Properties prop = new java.util.Properties();
            for (AppConfig key : AppConfig.values()) {
                if (key.Value == null) {
                    key.Value = "";
                }
                prop.setProperty(key.name(), key.Value);
            }
            prop.store(outs, "Config file for Portfolio Manager");
            outs.close();
        } catch (FileNotFoundException e) {
            logger.info("Config file Missing");
        } catch (IOException e) {
            logger.fatal(e, e);
            return false;
        }
        return true;
    }

    public static boolean saveUpdateConfigDetail(AppConfig config, String value) {
        config.Value = value;
        return saveConfigDetails();
    }

    public PMDate getDateValue() {
        try {
            return PMDateFormatter.parseYYYYMMDD(Value);
        } catch (ApplicationException e) {
            logger.error(e, e);
            return PMDate.START_DATE;
        }
    }

    public void setValueWithoutSave(PMDate date) {
        Value = PMDateFormatter.formatYYYYMMDD(date);
    }

    public void setValueWithoutSave(int value) {
        Value = Integer.toString(value);
    }

    public boolean getBooleanValue() {
        return Value != null && Value.equalsIgnoreCase("true");
    }

    public void setValueWithoutSave(boolean status) {
        Value = Boolean.toString(status);
    }

    public int getIntValue() {
        return (Value != null) ? Integer.parseInt(Value) : -1;
    }
}
