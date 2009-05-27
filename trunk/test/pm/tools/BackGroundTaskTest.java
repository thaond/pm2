package pm.tools;

import junit.framework.TestCase;
import pm.action.ILongTask;
import pm.util.PMDate;
import pm.util.enumlist.ITaskDetail;
import pm.util.enumlist.SyncStatus;
import pm.util.enumlist.TASKNAME;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Thiyagu
 * @version $Id: BackGroundTaskTest.java,v 1.1 2007/12/30 15:18:01 tpalanis Exp $
 * @since 20-Dec-2007
 */
public class BackGroundTaskTest extends TestCase {

    public void testVerifyResiteredTasks() {
        List<ITaskDetail> iTaskNameList = new BackGroundTask().registeredTasks;
        assertEquals(5, iTaskNameList.size());
        assertTrue(iTaskNameList.contains(TASKNAME.CORPACTIONDOWNLOAD));
        assertTrue(iTaskNameList.contains(TASKNAME.CORPACTIONSYNC));
        assertTrue(iTaskNameList.contains(TASKNAME.EODDOWNLOAD));
        assertTrue(iTaskNameList.contains(TASKNAME.MARKETHOLIDAYDOWNLOAD));
        assertTrue(iTaskNameList.contains(TASKNAME.STOCKLISTDOWNLOAD));
    }

    public void testRun() throws InterruptedException {

        final int runTimes = 2;
        final List<ITaskDetail> runTasks = new ArrayList<ITaskDetail>();
        BackGroundTask task = new BackGroundTask() {
            List<ITaskDetail> getRegisteredTasks() {
                List<ITaskDetail> tasks = new ArrayList<ITaskDetail>();
                tasks.add(TestTasks.TaskNotReadyToRun);
                tasks.add(TestTasks.TaskReadyToRun);
                tasks.add(TestTasks.TaskReadyToRun2);
                return tasks;
            }

            int count = 0;

            boolean continueRunning() {
                count++;
                return count <= runTimes;
            }

            int sleepTime() {
                return 10;
            }

            void startTask(ITaskDetail registeredTask) {
                runTasks.add(registeredTask);
            }
        };

        Thread thread = new Thread(task);
        thread.start();
        thread.join();
        assertEquals(runTimes, TestTasks.TaskNotReadyToRun.statusCheckCount);
        assertEquals(runTimes, TestTasks.TaskReadyToRun.statusCheckCount);
        assertEquals(runTimes, TestTasks.TaskReadyToRun2.statusCheckCount);

        int readyToRunTasksCount = 2;

        assertEquals(readyToRunTasksCount * runTimes, runTasks.size());

        TestTasks[] runnableTasks = {TestTasks.TaskReadyToRun, TestTasks.TaskReadyToRun2};
        for (int i = 0; i < runTasks.size(); i++) {
            assertEquals(runnableTasks[i % 2], runTasks.get(i));
        }
    }
}

class TestTasks implements ITaskDetail {

    static TestTasks TaskNotReadyToRun = new TestTasks(false);
    static TestTasks TaskReadyToRun = new TestTasks(true);
    static TestTasks TaskReadyToRun2 = new TestTasks(true);

    int statusCheckCount = 0;

    TestTasks(boolean startRunning) {
        this.startRunning = startRunning;
    }

    private boolean startRunning;


    public ILongTask getTask(ThreadPoolExecutor executor) {
        return null;
    }

    public SyncStatus getSyncStat() {
        return SyncStatus.InSync;
    }

    public PMDate getLastRunDate() {
        return null;
    }

    public boolean getLastRunStatus() {
        return false;
    }

    public void setLastRunDetails(PMDate date, boolean status) {

    }

    public boolean canStartSync(boolean networkAvailable) {
        statusCheckCount++;
        return startRunning;
    }

    public void incAttemptCount() {

    }
}
 