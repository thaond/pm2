package pm.util.enumlist;

import pm.action.ILongTask;
import pm.net.nse.CorpActionDownloadManager;
import pm.net.nse.CorpResultDownloadManager;
import pm.net.nse.StockListDownloader;
import pm.tools.BackGroundTask;
import pm.tools.CorpActionSynchronizer;
import pm.tools.ICICITransactionSynchronizer;
import pm.util.PMDate;
import pm.util.taskdetail.DefaultTaskDetail;
import pm.util.taskdetail.EodDownload;
import pm.util.taskdetail.MarketHolidayDownload;

import java.util.concurrent.ThreadPoolExecutor;

public enum TASKNAME implements ITaskDetail {

    CORPACTIONDOWNLOAD(new DefaultTaskDetail(CorpActionDownloadManager.class, 7, true)),
    EODDOWNLOAD(new EodDownload()),
    CORPRESULTDOWNLOAD(new DefaultTaskDetail(CorpResultDownloadManager.class, 30, true)),
    STOCKLISTDOWNLOAD(new DefaultTaskDetail(StockListDownloader.class, 7, true)),
    MARKETHOLIDAYDOWNLOAD(new MarketHolidayDownload()),
    BACKGROUND(new DefaultTaskDetail(BackGroundTask.class, 0, false)),
    CORPACTIONSYNC(new DefaultTaskDetail(CorpActionSynchronizer.class, 1, false)),
    ICICITRANSACTIONSYNC(new DefaultTaskDetail(ICICITransactionSynchronizer.class, 1, true));

    private final ITaskDetail taskDetail;

    TASKNAME(ITaskDetail defaultTaskDetail) {
        taskDetail = defaultTaskDetail;
    }

    public ILongTask getTask(ThreadPoolExecutor executor) {
        return taskDetail.getTask(executor);
    }

    public SyncStatus getSyncStat() {
        return taskDetail.getSyncStat();
    }

    public PMDate getLastRunDate() {
        return taskDetail.getLastRunDate();
    }

    public boolean getLastRunStatus() {
        return taskDetail.getLastRunStatus();
    }

    public void setLastRunDetails(PMDate date, boolean status) {
        taskDetail.setLastRunDetails(date, status);
    }

    public boolean canStartSync(boolean networkAvailable) {
        return taskDetail.canStartSync(networkAvailable);
    }

    public void incAttemptCount() {
        taskDetail.incAttemptCount();
    }
}
