package pm.util.taskdetail;

import junit.framework.TestCase;
import pm.action.ILongTask;
import pm.util.PMDate;
import pm.util.enumlist.SyncStatus;
import pm.util.enumlist.TASKNAME;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Thiyagu
 * @version $Id: DefaultTaskDetailTest.java,v 1.1 2007/12/30 15:18:02 tpalanis Exp $
 * @since 30-Dec-2007
 */
public class DefaultTaskDetailTest extends TestCase {

    public void testName() {
        Class testClass = TestLongTaskWithDefaultConstructor.class;
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(testClass, 0, false);
        assertEquals(testClass.getSimpleName().toUpperCase(), taskDetail.name());
    }

    public void testCanStartSync() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(null, 0, true) {
            boolean networkDependencyCheck(boolean networkAvailable) {
                return true;
            }

            boolean isMustSync() {
                return true;
            }

            boolean canRunToday() {
                return true;
            }
        };
        assertTrue(taskDetail.canStartSync(true));
    }

    public void testCanStartSync_ForNetWorkDependency() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(null, 0, true) {
            boolean isMustSync() {
                return true;
            }

            boolean canRunToday() {
                return true;
            }
        };
        assertTrue(taskDetail.canStartSync(true));
        assertFalse(taskDetail.canStartSync(false));
    }

    public void testCanStartSync_MustSyncState() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(null, 0, false) {
            boolean canRunToday() {
                return true;
            }

            public SyncStatus getSyncStat() {
                return SyncStatus.MustSync;
            }
        };
        assertTrue(taskDetail.canStartSync(true));

        DefaultTaskDetail taskDetailNotInMustSync = new DefaultTaskDetail(null, 0, false) {
            boolean canRunToday() {
                return true;
            }

            public SyncStatus getSyncStat() {
                return SyncStatus.CanSync;
            }
        };
        assertFalse(taskDetailNotInMustSync.canStartSync(true));
    }

    public void testNetworkDependencyCheck() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(null, 0, true);
        assertTrue(taskDetail.networkDependencyCheck(true));
        assertFalse(taskDetail.networkDependencyCheck(false));

        DefaultTaskDetail taskDetailNotNeedNW = new DefaultTaskDetail(null, 0, false);
        assertTrue(taskDetailNotNeedNW.networkDependencyCheck(true));
        assertTrue(taskDetailNotNeedNW.networkDependencyCheck(false));
    }

    public void testIsAttemptLeftForDayToIgnorePreviousDaysAttempt() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(null, 0, true) {
            {
                attemptCount = MAX_ATTEMPT;
            }

            public PMDate getLastRunDate() {
                return new PMDate().previous();
            }
        };
        assertTrue(taskDetail.isAttemptLeftForDay());
    }

    public void testIsAttemptLeftForDayToAllowTillMaxAttempt() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(null, 0, true) {
            {
                attemptCount = MAX_ATTEMPT - 1;
            }

            public PMDate getLastRunDate() {
                return new PMDate();
            }
        };
        assertTrue(taskDetail.isAttemptLeftForDay());
        taskDetail.incAttemptCount();
        assertFalse(taskDetail.isAttemptLeftForDay());
    }

    public void testIsAttemptLeftForDayToResetPreviousDayAttemptCount() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(null, 0, true) {
            public PMDate getLastRunDate() {
                return new PMDate().previous();
            }

            boolean isAttemptLeftForDay() {
                attemptCount = MAX_ATTEMPT;
                super.isAttemptLeftForDay();
                assertEquals(0, attemptCount);
                return true;
            }
        };
        taskDetail.isAttemptLeftForDay();
    }

    public void testGetSyncStatForLastFailedAttempt() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(null, 0, true) {
            public boolean getLastRunStatus() {
                return false;
            }

            public PMDate getLastRunDate() {
                fail("Should not check date");
                return null;
            }
        };
        assertEquals(SyncStatus.MustSync, taskDetail.getSyncStat());
    }

    public void testGetSyncStatForSuccessfulAttemptToday() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(null, 0, true) {
            public boolean getLastRunStatus() {
                return true;
            }

            public PMDate getLastRunDate() {
                return new PMDate();
            }
        };
        assertEquals(SyncStatus.InSync, taskDetail.getSyncStat());
    }

    public void testGetSyncStatForTimeLeftBeforeNextAttempt() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(null, 2, true) {
            public boolean getLastRunStatus() {
                return true;
            }

            public PMDate getLastRunDate() {
                return new PMDate().previous();
            }

        };
        assertEquals(SyncStatus.CanSync, taskDetail.getSyncStat());
    }

    public void testGetSyncStatForNextAttemptDay() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(null, 1, true) {
            public boolean getLastRunStatus() {
                return true;
            }

            public PMDate getLastRunDate() {
                return new PMDate().previous();
            }

        };
        assertEquals(SyncStatus.MustSync, taskDetail.getSyncStat());
    }

    public void testGetSyncStatForInSync() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(null, 0, true) {
            public boolean getLastRunStatus() {
                return true;
            }

            public PMDate getLastRunDate() {
                return new PMDate();
            }

        };
        assertEquals(SyncStatus.InSync, taskDetail.getSyncStat());
    }

    public void testValidTimeToRunDefaultedToAnyTime() {
        assertTrue(new DefaultTaskDetail(null, 0, true).vaildTimeToRun());
    }

    public void testGetTask_nextRunDate() {
        final int noOfDays = 10;
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(TestLongTaskWithDefaultConstructor.class, noOfDays, false) {
            public PMDate getLastRunDate() {
                return PMDate.today();
            }
        };

        assertEquals(PMDate.today().getDateAddingDays(noOfDays), taskDetail.nextRunDate());
    }

    public void testGetTask_ToUseDefaultConstructor() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(TestLongTaskWithDefaultConstructor.class, 0, false);
        ILongTask iLongTask = taskDetail.getTask(null);
        assertTrue(iLongTask instanceof TestLongTaskWithDefaultConstructor);
    }

    public void testGetTask_ToUseConstructorWithExecutor() {
        DefaultTaskDetail taskDetail = new DefaultTaskDetail(TestLongTaskWithNoDefaultConstructor.class, 0, false);
        ILongTask iLongTask = taskDetail.getTask(null);
        assertTrue(iLongTask instanceof TestLongTaskWithNoDefaultConstructor);
        assertTrue(((TestLongTaskWithNoDefaultConstructor) iLongTask).isConstructorCalled());
    }
}

class TestLongTaskWithDefaultConstructor implements ILongTask {

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

    }

    public TASKNAME getTaskName() {
        return null;
    }

    public boolean isInitComplete() {
        return false;
    }

    public boolean isIndeterminate() {
        return false;
    }

    public void run() {

    }
}

class TestLongTaskWithNoDefaultConstructor extends TestLongTaskWithDefaultConstructor {
    boolean constructorCalled = false;

    public TestLongTaskWithNoDefaultConstructor(ThreadPoolExecutor executor) {
        constructorCalled = true;
    }

    public boolean isConstructorCalled() {
        return constructorCalled;
    }
}
