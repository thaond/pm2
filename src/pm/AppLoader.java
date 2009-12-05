/*
 * Created on 25-Feb-2005
 *
 */
package pm;

import org.apache.log4j.*;
import pm.action.LimitAlert;
import pm.action.TaskManager;
import pm.dao.derby.DBManager;
import pm.net.eod.EODDownloadManager;
import pm.net.nse.CorpActionDownloadManager;
import pm.net.nse.CorpResultDownloadManager;
import pm.tools.BhavToPMConverter;
import pm.tools.EODScheduler;
import pm.tools.LoadTransData;
import pm.ui.PortfolioManager;
import pm.util.AppConfig;
import pm.util.BusinessLogger;
import pm.util.enumlist.AppConfigWrapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author thiyagu1
 */
public class AppLoader {

    public static void initLogger() {
        PatternLayout layout = new PatternLayout(AppConfig.Log_Pattern.Value);
        try {
            Appender appender = new RollingFileAppender(layout, AppConfigWrapper.logFileDir.Value + "/Log_pm.log");
            Logger rootLogger = Logger.getRootLogger();
            rootLogger.addAppender(appender);
            rootLogger.setLevel(Level.toLevel(AppConfig.Log_Level.Value));
            rootLogger.info("Logger Init...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Logger logger = Logger.getLogger(AppLoader.class);

    public static void main(String[] args) throws Exception {
        initLogger();
        if (args.length < 1) {
            String message = "Usage: pm.AppLoader ui | scheduler | download | alert | bhav2pm | checkSymbolChange | restorelog | downloadCompResult | downloadCompAction | execStmt | loadIciciTransaction";
            logger.error(message);
            System.out.println(message);
            System.out.println("Terminating...");
            logger.error("Terminating...");
            System.exit(0);
        }
        if (args.length > 1 && args[1].equals("debug")) {
            initConsoleLogger();
        }
        if (args[0].equalsIgnoreCase("ui")) {
            PortfolioManager.getInstance();
        } else if (args[0].equalsIgnoreCase("scheduler")) {
            Thread thread = new Thread(new EODScheduler(), "Downloader");
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
            }
        } else if (args[0].equalsIgnoreCase("download")) {
            new EODDownloadManager(TaskManager.getExecutor());
        } else if (args[0].equalsIgnoreCase("alert")) {
            Thread thread = new LimitAlert();
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
            }
        } else if (args[0].equalsIgnoreCase("bhav2pm")) {
            logger.info("Starting Bhav To PM converter");
            try {
                new BhavToPMConverter().processData();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else if (args[0].equalsIgnoreCase("checkSymbolChange")) {
            logger.info("Starting checkSymbolChange");
//			NSETradingSymbol.updateSymbolChange();          //TODO make it working
        } else if (args[0].equalsIgnoreCase("restorelog")) {
            logger.info("Starting restorelog");
            new LoadTransData().loadLogData(BusinessLogger.getTransLogFilePath(), BusinessLogger.getCompActLogFilePath(), true, true);
        } else if (args[0].equalsIgnoreCase("downloadCompResult")) {
            logger.info("Starting Company Result download");
            new CorpResultDownloadManager(TaskManager.getExecutor());
        } else if (args[0].equalsIgnoreCase("downloadCompAction")) {
            logger.info("Starting Company Action download");
            new CorpActionDownloadManager(TaskManager.getExecutor());
        } else if (args[0].equalsIgnoreCase("execStmt") && args.length >= 2) {
            executeStmt(args[1]);
        } else if (args[0].equalsIgnoreCase("loadIciciTransaction")) {
            new pm.net.icici.ICICICSVTransactionLoader().load();
        }
    }

    private static void executeStmt(String sqlStmt) throws SQLException {
        System.out.println("Executing " + sqlStmt);
        Connection conn = DBManager.createNewConnection();
        Statement stmt = conn.createStatement();
        stmt.execute(sqlStmt);
        conn.close();
    }

    public synchronized static void initConsoleLogger() {
        PatternLayout layout = new PatternLayout(AppConfig.Log_Pattern.Value);
        Appender appender = new ConsoleAppender(layout);
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.addAppender(appender);
        rootLogger.setLevel(Level.toLevel("Info"));
        rootLogger.info("Logger Init...");
    }

}
