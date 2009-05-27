package pm.util.taskdetail;

import junit.framework.TestCase;
import pm.util.PMDate;
import pm.util.enumlist.SyncStatus;

/**
 * @author Thiyagu
 * @version $Id: EodDownloadTest.java,v 1.1 2007/12/30 15:18:02 tpalanis Exp $
 * @since 30-Dec-2007
 */
public class EodDownloadTest extends TestCase {

    public void testIsOnOrAfterValidRunTime() {
        EodDownload eodDownload = new EodDownload();
        assertFalse(eodDownload.isOnOrAfterValidRunTime(16, 30, 16, 20));
        assertFalse(eodDownload.isOnOrAfterValidRunTime(16, 30, 15, 40));
        assertTrue(eodDownload.isOnOrAfterValidRunTime(16, 30, 16, 30));
        assertTrue(eodDownload.isOnOrAfterValidRunTime(16, 30, 16, 40));
        assertTrue(eodDownload.isOnOrAfterValidRunTime(16, 30, 17, 30));
    }

    public void testVaildTimeToRunToIgnoreTimeCheckIfNextRunDateIsInPast() {
        EodDownload taskDetail = new EodDownload() {
            @Override
            protected PMDate nextRunDate() {
                return PMDate.today().previous();
            }

            boolean isOnOrAfterValidRunTime(int runHH, int runMM, int currentHH, int currentMM) {
                fail("Should not check this");
                return false;
            }
        };
        assertTrue(taskDetail.vaildTimeToRun());
    }

    public void testVaildTimeToRunToDoTimeCheckIfNextRunDateIsNotPast() {
        EodDownload taskDetailNotValidTime = new EodDownload() {
            @Override
            protected PMDate nextRunDate() {
                return PMDate.today();
            }

            boolean isOnOrAfterValidRunTime(int runHH, int runMM, int currentHH, int currentMM) {
                return false;
            }
        };
        assertFalse(taskDetailNotValidTime.vaildTimeToRun());

        EodDownload taskDetailWithValidTime = new EodDownload() {
            @Override
            protected PMDate nextRunDate() {
                return PMDate.today();
            }

            boolean isOnOrAfterValidRunTime(int runHH, int runMM, int currentHH, int currentMM) {
                return true;
            }
        };
        assertTrue(taskDetailWithValidTime.vaildTimeToRun());
    }

    public void testGetSyncStat_ForTaskWhichNeedsToRunInLaterPartOfDay() {
        EodDownload task = new EodDownload() {
            public boolean getLastRunStatus() {
                return true;
            }

            @Override
            protected PMDate nextRunDate() {
                return PMDate.today();
            }

            protected boolean vaildTimeToRun() {
                return false;
            }
        };

        assertEquals(SyncStatus.InSync, task.getSyncStat());

    }

    public void testGetSyncStat_ForTaskWhichNeedsToRunInFuture() {
        EodDownload task = new EodDownload() {
            public boolean getLastRunStatus() {
                return true;
            }

            @Override
            protected PMDate nextRunDate() {
                return PMDate.today().next();
            }

            protected boolean vaildTimeToRun() {
                throw new RuntimeException("Should not check");
            }
        };

        assertEquals(SyncStatus.InSync, task.getSyncStat());

    }

    public void testGetSyncStat_ForTaskNeedToRunNow() {
        EodDownload task = new EodDownload() {
            public boolean getLastRunStatus() {
                return true;
            }

            @Override
            protected PMDate nextRunDate() {
                return PMDate.today();
            }

            protected boolean vaildTimeToRun() {
                return true;
            }
        };

        assertEquals(SyncStatus.MustSync, task.getSyncStat());

    }

    public void testGetSyncStat_ForTaskNeedToRun() {
        EodDownload task = new EodDownload() {
            public boolean getLastRunStatus() {
                return true;
            }

            @Override
            protected PMDate nextRunDate() {
                return PMDate.today().previous();
            }

        };

        assertEquals(SyncStatus.MustSync, task.getSyncStat());

    }
}
