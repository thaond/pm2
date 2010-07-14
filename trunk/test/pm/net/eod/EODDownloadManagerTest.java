package pm.net.eod;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import pm.dao.ibatis.dao.IDateDAO;
import pm.net.nse.downloader.BhavCopyDownloader;
import pm.net.nse.downloader.DeliveryPositionDownloader;
import pm.net.nse.downloader.FandODownloader;
import pm.util.PMDate;
import pm.vo.StockVO;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
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
        List<Boolean> processStatus = new Vector<Boolean>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(15, 15, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        EODDownloadManager downloadManager = create(processStatus, executor);

        executor.execute(downloadManager);
        while (!downloadManager.isTaskCompleted()) {
            Thread.sleep(1000);
        }

        executor.shutdown();
        assertTrue(processStatus.get(0));
        assertTrue(processStatus.get(1));
        assertEquals(100, downloadManager.getProgress());
        assertTrue(downloadManager.isTaskCompleted());

    }

    private EODDownloadManager create(final List<Boolean> processStatus, final ThreadPoolExecutor executor) {
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
            void loadFandODownloaders() {
                this.addTask(new FandODownloader(null, this) {
                    @Override
                    public void run() {
                        manager.taskCompleted(this);
                    }
                });
            }

            @Override
            void processEODData() {
                processStatus.add(true);
            }

            @Override
            void shutdown() {
                processStatus.add(true);
            }
        };
        return downloadManager;
    }


}
