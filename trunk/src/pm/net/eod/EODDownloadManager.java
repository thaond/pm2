package pm.net.eod;

import org.apache.log4j.Logger;
import pm.action.ILongTask;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IDateDAO;
import pm.net.AbstractDownloader;
import pm.net.nse.downloader.AbstractFileDownloader;
import pm.net.nse.downloader.BhavCopyDownloader;
import pm.net.nse.downloader.DeliveryPositionDownloader;
import pm.tools.BhavToPMConverter;
import pm.ui.PortfolioManager;
import pm.util.PMDate;
import pm.util.enumlist.TASKNAME;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;

public class EODDownloadManager implements ILongTask {

    private static Logger logger = Logger.getLogger(EODDownloadManager.class);
    private static int _MAXTASKPERCENTAGE = 100;

    private ThreadPoolExecutor executor;

    private boolean completedFlag = false;

    private boolean initComplete = false;
    private boolean stopFlag = false;
    private int totalTask = 0;

    private int completedTask = 0;
    private int countBhavDownloader = 0;
    private int countDeliveryDownloader = 0;
    private int countIndexDownloader = 0;
    private boolean flagStartBhavConverter = true;

    private List<AbstractDownloader> downloaderList = new Vector<AbstractDownloader>();

    public EODDownloadManager(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    synchronized public void taskCompleted(Runnable downloader) {

        completedTask++;
        if (downloader instanceof BhavCopyDownloader) {
            logger.info("BhavCopy downloaded for date : " + ((AbstractFileDownloader) downloader).getDate());
            countBhavDownloader--;
        } else if (downloader instanceof DeliveryPositionDownloader) {
            logger.info("DeliveryPosi downloaded for date : " + ((AbstractFileDownloader) downloader).getDate());
            countDeliveryDownloader--;
        } else if (downloader instanceof YahooQuoteDownloadHandler) {
            logger.info("Index quote downloaded for " + ((YahooQuoteDownloadHandler) downloader).getIndexCode());
            countIndexDownloader--;
        }

        if (flagStartBhavConverter && countBhavDownloader == 0 && countDeliveryDownloader == 0) {
            flagStartBhavConverter = false;
            if (!stopFlag) {
                new BhavToPMConverter().processData();
            }
        }
        completedFlag = (totalTask == completedTask);
        if (completedFlag) {
            shutdown();
        }
    }

    void shutdown() {
        logger.info("Shutdown");
        getTaskName().setLastRunDetails(new PMDate(), getStatus());
        PortfolioManager.resetView();
    }

    boolean getStatus() {
        return hasQuoteTillToday();
    }

    private boolean hasQuoteTillToday() {
        return dateDAO().nextQuoteDate().after(new PMDate());
    }

    IDateDAO dateDAO() {
        return DAOManager.getDateDAO();
    }

    public synchronized boolean isTaskCompleted() {
        return completedFlag;
    }

    public synchronized int getProgress() {
        return (int) ((float) completedTask / (float) totalTask * (float) _MAXTASKPERCENTAGE);
    }

    public synchronized int getTaskLength() {
        return _MAXTASKPERCENTAGE;
    }

    public synchronized void stop() {
        stopFlag = true;
        for (AbstractDownloader downloader : downloaderList) {
            downloader.stop();
        }
    }

    public synchronized void run() {
        logger.info("Downloading EOD Data started");
        loadBhavCopyDownloaders();
        loadDeliveryPostDownloaders();
        loadIndexDownloaders();
        totalTask = countBhavDownloader + countDeliveryDownloader + countIndexDownloader;

        initComplete = true;
    }

    void loadIndexDownloaders() {
        new IndexQuoteTaskManager().loadDownloaders(this);
    }

    void loadDeliveryPostDownloaders() {
        new NSEDeliveryPostTaskManager().loadDownloaders(this);
    }

    void loadBhavCopyDownloaders() {
        new NSEBhavCopyTaskManager().loadDownloaders(this);
    }

    public TASKNAME getTaskName() {
        return TASKNAME.EODDOWNLOAD;
    }

    public boolean isInitComplete() {
        return initComplete;
    }

    public boolean isIndeterminate() {
        return false;
    }

    public void addTask(AbstractDownloader downloader) {
        if (downloader instanceof BhavCopyDownloader) {
            countBhavDownloader++;
        } else if (downloader instanceof DeliveryPositionDownloader) {
            countDeliveryDownloader++;
        } else if (downloader instanceof YahooQuoteDownloadHandler) {
            countIndexDownloader++;
        }
        downloaderList.add(downloader);
        executor.execute(downloader);
    }
}
