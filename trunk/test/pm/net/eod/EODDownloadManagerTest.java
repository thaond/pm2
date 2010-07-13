package pm.net.eod;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import pm.dao.ibatis.dao.IDateDAO;
import pm.net.nse.downloader.BhavCopyDownloader;
import pm.net.nse.downloader.DeliveryPositionDownloader;
import pm.util.PMDate;
import pm.vo.StockVO;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EODDownloadManagerTest extends MockObjectTestCase {

    private Mock dateDAOMock = mock(IDateDAO.class);

    public void testGetStatusForSuccessfullEODOnMarketDay() throws Exception {
        final PMDate date = new PMDate();
        dateDAOMock.expects(once()).method("nextQuoteDate").withNoArguments().will(returnValue(date.next()));
        assertTrue(downloadManager().getStatus());
    }

    private EODDownloadManager downloadManager() {
        EODDownloadManager downloadManager = new EODDownloadManager(null) {
            IDateDAO dateDAO() {
                return (IDateDAO) dateDAOMock.proxy();
            }
        };
        return downloadManager;
    }

    public void testGetStatusForFailureEODOnMarketDay() throws Exception {
        final PMDate date = new PMDate();
        dateDAOMock.expects(once()).method("nextQuoteDate").withNoArguments().will(returnValue(date));
        assertFalse(downloadManager().getStatus());
    }

    public void testGetStatusForFailureLastMarketDayEODOnMarketHoliday() throws Exception {
        final PMDate date = new PMDate();
        dateDAOMock.expects(once()).method("nextQuoteDate").withNoArguments().will(returnValue(date.previous()));
        assertFalse(downloadManager().getStatus());
    }

    public void testAddingTasksAndHandlingCompletion() throws Exception {
        final boolean[] processStatus = {false, false};
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        EODDownloadManager downloadManager = new EODDownloadManager(executor) {
            @Override
            void loadIndexDownloaders() {
                this.addTask(new YahooQuoteDownloadHandler(new StockVO(), this) {
                    @Override
                    public void run() {
                        manager.taskCompleted(this);
                    }
                });
            }

            @Override
            void loadDeliveryPostDownloaders() {
                this.addTask(new DeliveryPositionDownloader(null, this) {
                    @Override
                    public void run() {
                        manager.taskCompleted(this);
                    }
                });
            }

            @Override
            void loadBhavCopyDownloaders() {
                this.addTask(new BhavCopyDownloader(null, this) {
                    @Override
                    public void run() {
                        manager.taskCompleted(this);
                    }
                });
            }

            @Override
            void processEODData() {
                processStatus[0] = true;
            }

            @Override
            void shutdown() {
                processStatus[1] = true;
            }
        };

        downloadManager.run();
        executor.shutdown();
        assertTrue(processStatus[0]);
        assertTrue(processStatus[1]);
        assertEquals(100, downloadManager.getProgress());
        assertTrue(downloadManager.isTaskCompleted());

    }
}
