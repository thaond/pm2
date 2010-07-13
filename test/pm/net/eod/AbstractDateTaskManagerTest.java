package pm.net.eod;

import junit.framework.TestCase;
import pm.net.AbstractDownloader;
import pm.net.nse.downloader.AbstractFileDownloader;
import pm.util.PMDate;

import java.util.*;

public class AbstractDateTaskManagerTest extends TestCase {

    /*
      * Test method for 'pm.net.eod.AbstractTaskManager.addTask(Executor)'
      */
    public void testAddTaskToProcessFromNextDayTillCurrentDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);
        final PMDate date1 = new PMDate(cal);
        final Vector<PMDate> calList = new Vector<PMDate>();

        AbstractDateTaskManager taskManager = new AbstractDateTaskManager() {
            @Override
            public PMDate getLastCompletedDate() {
                return date1;
            }

            @Override
            protected AbstractFileDownloader getDownloader(Date date, EODDownloadManager manager) {
                calList.add(new PMDate(date));
                return new AbstractFileDownloader(null, null) {

                    @Override
                    protected String getURL() {
                        return null;
                    }

                    @Override
                    public String getFilePath() {
                        return null;
                    }

                    @Override
                    protected String getFileType() {
                        return null;
                    }

                };
            }
        };

        final List downloaders = new ArrayList();
        EODDownloadManager downloadManager = new EODDownloadManager(null) {
            public void addTask(AbstractDownloader downloader) {
                downloaders.add(downloader);
            }
        };
        taskManager.loadDownloaders(downloadManager);
        assertEquals(3, downloaders.size());
        assertEquals(3, calList.size());
        for (PMDate date : calList) {
            cal.add(Calendar.DATE, 1);
            assertEquals(new PMDate(cal), date);
        }

    }

    public void testAddTaskToDoNothingForCurrentDayIsLastProcessing() {
        Calendar cal = Calendar.getInstance();
        final PMDate date1 = new PMDate(cal);

        AbstractDateTaskManager taskManager = new AbstractDateTaskManager() {
            @Override
            public PMDate getLastCompletedDate() {
                return date1;
            }

            @Override
            protected AbstractFileDownloader getDownloader(Date date, EODDownloadManager manager) {
                fail("Should not come herer");
                return null;
            }
        };

        taskManager.loadDownloaders(null);
    }

    public void testAddTaskToProcessCurrentDayIfLastProcessingYDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        final PMDate date1 = new PMDate(cal);
        final Vector<PMDate> calList = new Vector<PMDate>();

        AbstractDateTaskManager taskManager = new AbstractDateTaskManager() {
            @Override
            public PMDate getLastCompletedDate() {
                return date1;
            }

            @Override
            protected AbstractFileDownloader getDownloader(Date date, EODDownloadManager manager) {
                calList.add(new PMDate(date));
                return null;
            }
        };

        final List downloaders = new ArrayList();
        EODDownloadManager downloadManager = new EODDownloadManager(null) {
            public void addTask(AbstractDownloader downloader) {
                downloaders.add(downloader);
            }
        };
        taskManager.loadDownloaders(downloadManager);
        assertEquals(1, downloaders.size());
        assertEquals(1, calList.size());

    }
}
