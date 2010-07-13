package pm.net.eod;

import org.apache.log4j.Logger;
import pm.action.ILongTask;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IDateDAO;
import pm.net.AbstractDownloader;
import pm.tools.BhavToPMConverter;
import pm.ui.PortfolioManager;
import pm.util.PMDate;
import pm.util.enumlist.TASKNAME;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

public class EODDownloadManager implements ILongTask {

    private static Logger logger = Logger.getLogger(EODDownloadManager.class);
    private static int _MAXTASKPERCENTAGE = 100;
    private static int _WEIGHTAGEFORBGPROCESS = 25;

    private ThreadPoolExecutor executor;

    private boolean completedFlag = false;

    private boolean initComplete = false;
    private boolean stopFlag = false;
    private boolean bgCompleted = false;
    private int completedTask = 0;

    private boolean flagStartBhavConverter = true;

    private List<AbstractDownloader> downloaderList = new Vector<AbstractDownloader>();
    private AtomicLong quoteDownloaderCount = new AtomicLong();
    private AtomicLong indexQuoteDownloaderCount = new AtomicLong();

    public EODDownloadManager(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    synchronized public void taskCompleted(Runnable downloader) {

        completedTask++;

        if (downloader instanceof IndexQuoteDownloader) {
            indexQuoteDownloaderCount.decrementAndGet();
        } else {
            quoteDownloaderCount.decrementAndGet();
        }

        if (flagStartBhavConverter && quoteDownloaderCount.get() == 0) {
            flagStartBhavConverter = false;
            if (!stopFlag) {
                processEODData();
                bgCompleted = true;
            }
        }
        completedFlag = (initComplete && completedTask == downloaderList.size());
        if (completedFlag) {
            shutdown();
        }
    }

    void processEODData() {
        new BhavToPMConverter().processData();
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
        int taskCompletedRatio = (int) ((float) completedTask / (float) downloaderList.size() * (float) _MAXTASKPERCENTAGE);
        if (!bgCompleted) {
            taskCompletedRatio -= _WEIGHTAGEFORBGPROCESS;
        }
        return taskCompletedRatio;
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
        loadFandODownloaders();
        loadIndexDownloaders();
        initComplete = true;
    }

    void loadFandODownloaders() {
        new NSEFOTaskManager().loadDownloaders(this);
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
        if (downloader instanceof IndexQuoteDownloader) {
            indexQuoteDownloaderCount.incrementAndGet();
        } else {
            quoteDownloaderCount.incrementAndGet();
        }
        downloaderList.add(downloader);
        executor.execute(downloader);
    }
}
