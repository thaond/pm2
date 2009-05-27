package pm.tools;

import org.apache.log4j.Logger;
import pm.AppLoader;
import pm.action.ILongTask;
import pm.bo.CompanyBO;
import pm.bo.PortfolioBO;
import pm.dao.CompanyDAO;
import pm.util.AppConfig;
import pm.util.BusinessLogger;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.util.enumlist.TASKNAME;
import pm.vo.CompanyActionVO;

import java.util.*;

/**
 * This class applys downloaded data into PM
 */
//TODO Company action should be applied automatically on start of every day
//TODO Today's exDate action r not applied

public class CorpActionSynchronizer implements ILongTask {
    private boolean taskCompleted = false;
    private static Logger logger = Logger.getLogger(CorpActionSynchronizer.class);

    public void applyCorpAction() {
        logger.info("Started applying Corp Action");
        CompanyDAO companyDAO = getCompanyDAO();
        Hashtable<PMDate, Vector<CompanyActionVO>> consolidatedActionData = companyDAO.getConsolidatedActionData();
        Set<PMDate> keys = getKeysSortedByDate(consolidatedActionData);
        PMDate currDate = getCurrDate();
        CompanyBO companyBO = getCompanyBO();
        PortfolioBO portfolioBO = new PortfolioBO();

        float totalValue = portfolioBO.getAllPortfolioValue();
        BusinessLogger.recordMsg("Info : value before performing action : " + totalValue);

        for (PMDate date : keys) {
            if (date.after(currDate)) {
                continue;
            }
            applyActions(companyBO, consolidatedActionData.remove(date));
        }
        doPostSyncOperation(companyDAO, consolidatedActionData, currDate, companyBO, portfolioBO, totalValue);
    }

    private void applyActions(CompanyBO companyBO, Vector<CompanyActionVO> actionVOs) {
        for (CompanyActionVO actionVO : actionVOs) {
            applyAction(companyBO, actionVO);
        }
    }

    private Set<PMDate> getKeysSortedByDate(Hashtable<PMDate, Vector<CompanyActionVO>> consolidatedActionData) {
        TreeSet<PMDate> keys = new TreeSet<PMDate>(consolidatedActionData.keySet());
        Enumeration<PMDate> enumKeys = consolidatedActionData.keys();
        while (enumKeys.hasMoreElements()) {
            keys.add(enumKeys.nextElement());
        }
        return keys;
    }

    void doPostSyncOperation(CompanyDAO companyDAO, Hashtable<PMDate, Vector<CompanyActionVO>> consolidatedActionData, PMDate currDate, CompanyBO companyBO, PortfolioBO portfolioBO, float totalValue) {
        companyBO.normalizeDividents();
        float currValue = portfolioBO.getAllPortfolioValue();
        BusinessLogger.recordMsg("Info : value after performing action : " + currValue + " diff : " + (totalValue - currValue));

        AppConfig.saveUpdateConfigDetail(AppConfig.dateCORPACTIONSYNCHRONIZER, PMDateFormatter.formatYYYYMMDD(currDate));
        companyDAO.writeConsolidatedActionData(consolidatedActionData);
        logger.info("Completed applying Corp Action");
    }

    void applyAction(CompanyBO companyBO, CompanyActionVO actionVO) {
        companyBO.doAction(actionVO);
        if (companyBO.isActionApplied()) {
            String errorLine = "Info : " + actionVO.toWrite() + " : changed Portfolio value ";
            BusinessLogger.recordMsg(errorLine);
        }
    }

    PMDate getCurrDate() {
        return new PMDate();
    }

    CompanyBO getCompanyBO() {
        return new CompanyBO();
    }

    CompanyDAO getCompanyDAO() {
        return new CompanyDAO();
    }

    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    public int getProgress() {
        return 0;
    }

    public int getTaskLength() {
        return 0;
    }

    public void stop() {

    }

    public TASKNAME getTaskName() {
        return TASKNAME.CORPACTIONSYNC;
    }

    public boolean isInitComplete() {
        return true;
    }

    public boolean isIndeterminate() {
        return true;
    }

    public void run() {
        try {
            applyCorpAction();
            saveState(true);
        } catch (Exception e) {
            logger.error(e, e);
            saveState(false);
        } finally {
            taskCompleted = true;
        }
    }

    private void saveState(boolean status) {
        getTaskName().setLastRunDetails(getCurrDate(), status);
    }

    public static void main(String[] args) {
        AppLoader.initConsoleLogger();
        new CorpActionSynchronizer().run();
    }
}
