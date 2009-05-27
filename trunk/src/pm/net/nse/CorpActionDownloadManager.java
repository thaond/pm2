package pm.net.nse;

import org.apache.log4j.Logger;
import pm.dao.CompanyDAO;
import pm.net.AbstractDownloader;
import pm.net.nse.downloader.CorpActionDownloader;
import pm.tools.CorpActionSynchronizer;
import pm.util.AppConfig;
import pm.util.PMDate;
import pm.util.enumlist.TASKNAME;
import pm.vo.CompanyActionVO;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;

public class CorpActionDownloadManager extends AbstractStockDownloadManager {

    private static Logger logger = Logger.getLogger(CorpActionDownloadManager.class);

    private ThreadPoolExecutor executor;
    private Vector<CorpActionDownloader> downloaderList = new Vector<CorpActionDownloader>();
    protected Hashtable<PMDate, Vector<CompanyActionVO>> htConsolidatedCorpAction = new Hashtable<PMDate, Vector<CompanyActionVO>>();

    public CorpActionDownloadManager(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    AppConfig getErrorListConfig() {
        return AppConfig.corpActionDownloadErrorList;
    }

    @Override
    synchronized public void taskCompleted(AbstractDownloader completedTask) {
        CorpActionDownloader task = (CorpActionDownloader) completedTask;
        completedTaskCount++;
        if (logger.isInfoEnabled()) {
            logger.info("CorpAction download completed for " + task.getStockCode() + " completed : " + task.isCompleted());
            logger.info(completedTaskCount + " tasks completed out of " + totalTaskCount);
        }
        if (completedTask.isCompleted()) {
            performConsolidation(task);
        } else {
            addToInCompleteList(task.getStockCode());
        }

        if (completedTaskCount >= totalTaskCount) {
            shutDownManager();
            taskCompleted = true;
        }
    }

    void shutDownManager() {
        logger.info("Shutdown initiated in CorpActionDownloadManager");
        getCompanyDAO().writeConsolidatedActionData(htConsolidatedCorpAction);
        storeIncompleteList();
        if (!stop) {
            getSynchronizer().applyCorpAction();
        } else {
            error = true;
        }
        saveStatus();
        logger.info("Shutdown Complete");
    }

    CorpActionSynchronizer getSynchronizer() {
        return new CorpActionSynchronizer();
    }

    CompanyDAO getCompanyDAO() {
        return new CompanyDAO();
    }

    void performConsolidation(CorpActionDownloader completedTask) {
        Vector<CompanyActionVO> corpActions = completedTask.getCorpActions();
        for (CompanyActionVO actionVO : corpActions) {
            if (htConsolidatedCorpAction.containsKey(actionVO.getExDate())) {
                htConsolidatedCorpAction.get(actionVO.getExDate()).add(actionVO);
            } else {
                Vector<CompanyActionVO> consolidatedVector = new Vector<CompanyActionVO>();
                consolidatedVector.add(actionVO);
                htConsolidatedCorpAction.put(actionVO.getExDate(), consolidatedVector);
            }
        }
    }

    public void run() {
        Vector<String> stockList = getStockList();
        if (!flagCompleteListLoaded) {
            htConsolidatedCorpAction = getCompanyDAO().getConsolidatedActionData();
        }

        for (String stockCode : stockList) {
            CorpActionDownloader corpActionDownloader = createDownloader(stockCode);
            downloaderList.add(corpActionDownloader);
            executor.execute(corpActionDownloader);
            totalTaskCount++;
        }
        initComplete = true;
    }

    CorpActionDownloader createDownloader(String stockCode) {
        return new CorpActionDownloader(stockCode, this);
    }

    public void stop() {
        stop = true;
        for (CorpActionDownloader downloader : downloaderList) {
            downloader.stop();
        }
    }

    public TASKNAME getTaskName() {
        return TASKNAME.CORPACTIONDOWNLOAD;
    }

    public boolean isIndeterminate() {
        return false;
    }

}
