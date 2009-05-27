package pm.tools;

import org.apache.log4j.Logger;
import pm.action.ILongTask;
import pm.action.TaskManager;
import pm.net.HTTPHelper;
import pm.util.enumlist.ITaskDetail;
import pm.util.enumlist.TASKNAME;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thiyagu
 * @since 12-Aug-2007
 */

public class BackGroundTask implements ILongTask {

    private Logger logger = Logger.getLogger(BackGroundTask.class);

    List<ITaskDetail> registeredTasks = new ArrayList<ITaskDetail>();

    {
        registeredTasks.add(TASKNAME.CORPACTIONSYNC);
        registeredTasks.add(TASKNAME.CORPACTIONDOWNLOAD);
        registeredTasks.add(TASKNAME.EODDOWNLOAD);
        registeredTasks.add(TASKNAME.MARKETHOLIDAYDOWNLOAD);
        registeredTasks.add(TASKNAME.STOCKLISTDOWNLOAD);
        registeredTasks.add(TASKNAME.CORPRESULTDOWNLOAD);
    }

    private boolean stopFlag = false;

    public boolean isTaskCompleted() {
        return false;
    }

    public int getProgress() {
        return 0;
    }

    public int getTaskLength() {
        return 0;
    }

    public void stop() {
        stopFlag = true;
    }

    public TASKNAME getTaskName() {
        return TASKNAME.BACKGROUND;
    }

    public boolean isInitComplete() {
        return false;
    }

    public boolean isIndeterminate() {
        return true;
    }

    public void run() {
        logger.info("Starting back ground task");
        while (continueRunning()) {
            doSleep();
            boolean networkAvailable = HTTPHelper.isNetworkAvailable();
            for (ITaskDetail registeredTask : getRegisteredTasks()) {
                if (registeredTask.canStartSync(networkAvailable)) {
                    startTask(registeredTask);
                }
            }
            logger.info("back ground task completed one run");
        }
        logger.info("Back ground task terminating");
    }

    private void doSleep() {
        try {
            Thread.sleep(sleepTime());
        } catch (InterruptedException e) {
            logger.error(e, e);
        }
    }

    void startTask(ITaskDetail registeredTask) {
        TaskManager.start(registeredTask);
    }

    int sleepTime() {
        return 1000 * 60 * 5;
    }

    boolean continueRunning() {
        return !stopFlag;
    }

    List<ITaskDetail> getRegisteredTasks() {
        return registeredTasks;
    }
}
