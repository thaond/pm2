package pm.net.nse;

import org.apache.log4j.Logger;
import pm.action.ILongTask;
import pm.net.AbstractDownloader;
import pm.util.AppConfig;
import pm.util.Helper;
import pm.util.PMDate;

import java.util.StringTokenizer;
import java.util.Vector;

public abstract class AbstractStockDownloadManager implements ILongTask {

    private static final int _MAXTASKPERCENTAGE = 100;


    protected static Logger logger = Logger.getLogger(AbstractStockDownloadManager.class);


    protected boolean flagCompleteListLoaded = false;
    protected Vector<String> inCompleteList = new Vector<String>();


    protected int totalTaskCount = 0;


    protected volatile int completedTaskCount = 0;


    protected volatile boolean taskCompleted = false;


    protected boolean initComplete = false;

    protected boolean error = false;

    protected boolean stop = false;

    abstract public void taskCompleted(AbstractDownloader completedTask);

    protected Vector<String> getCompleteList() {
        flagCompleteListLoaded = true;
        return Helper.getStockMasterList();
    }

    protected Vector<String> loadInComleteList() {
        Vector<String> retVal = new Vector<String>();
        StringTokenizer stk = new StringTokenizer(getErrorListConfig().Value, ",");
        while (stk.hasMoreTokens()) {
            retVal.add(stk.nextToken());
        }
        return retVal;
    }

    abstract AppConfig getErrorListConfig();

    protected void storeIncompleteList() {
        logger.info("Storing incomplete list");
        StringBuffer sb = new StringBuffer();
        for (String stockCode : inCompleteList) {
            sb.append(stockCode).append(",");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
            error = true;
        }
        getErrorListConfig().Value = sb.toString();
        AppConfig.saveConfigDetails();
    }

    protected Vector<String> getStockList() {
        PMDate lastRunDate = getLastRunDate();
        Vector<String> list;
        if (!lastRunDate.getDateAddingDays(6).before(new PMDate())) {
            list = loadInComleteList();
            if (list.isEmpty()) {
                list = getCompleteList();
            }
        } else {
            list = getCompleteList();
        }
        return list;
    }

    PMDate getLastRunDate() {
        return getTaskName().getLastRunDate();
    }

    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    public int getProgress() {
        int progress = (int) ((float) completedTaskCount / (float) totalTaskCount * (float) _MAXTASKPERCENTAGE);
        return progress;

    }

    public int getTaskLength() {
        return _MAXTASKPERCENTAGE;
    }

    public boolean isInitComplete() {
        return initComplete;
    }

    protected void addToInCompleteList(String stockCode) {
        if (!inCompleteList.contains(stockCode)) {
            inCompleteList.add(stockCode);
        }
    }

    protected void saveStatus() {
        getTaskName().setLastRunDetails(new PMDate(), !error);
    }


}
