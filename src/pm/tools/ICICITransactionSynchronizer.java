package pm.tools;

import org.apache.log4j.Logger;
import pm.action.ILongTask;
import pm.net.icici.TransactionDownloader;
import pm.util.PMDate;
import pm.util.enumlist.TASKNAME;

import java.util.concurrent.ThreadPoolExecutor;

public class ICICITransactionSynchronizer implements ILongTask {

    static Logger _logger = Logger.getLogger(ICICITransactionSynchronizer.class);

    private boolean error = false;
    private int progress = 0;
    private ThreadPoolExecutor executor;

    public ICICITransactionSynchronizer(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    boolean taskCompleted = false;

    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    public int getProgress() {
        return progress;
    }

    public int getTaskLength() {
        return 100;
    }

    public void stop() {
    }

    public TASKNAME getTaskName() {
        return TASKNAME.ICICITRANSACTIONSYNC;
    }

    public boolean isInitComplete() {
        return true;
    }

    public boolean isIndeterminate() {
        return false;
    }

    public void run() {
        PMDate startDate = getTaskName().getLastRunDate();
        PMDate endDate = PMDate.today().getDateAddingDays(-1);
        boolean status = new TransactionDownloader(startDate, endDate).sync();
        if (status) {

        }
//        if (!error) {
//            getTaskName().setLastRunDetails(PMDate.today(), true);
//        }

        progress = getTaskLength();
        taskCompleted = true;
    }
}
