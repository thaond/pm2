package pm.net.eod;

import org.junit.Test;
import pm.dao.ibatis.dao.DAOManager;
import pm.util.PMDate;
import pm.vo.QuoteVO;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;


public class EODDownloadManagerExternalTest {

    @Test
    public void run() throws InterruptedException {
        final PMDate stDate = new PMDate(27, 11, 2009);
        final PMDate enDate = new PMDate(1, 12, 2009);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        EODDownloadManager downloadManager = new EODDownloadManager(executor) {
            @Override
            void loadIndexDownloaders() {

            }

            @Override
            void loadDeliveryPostDownloaders() {

            }

            @Override
            void loadBhavCopyDownloaders() {
                NSEBhavCopyTaskManager manager = new NSEBhavCopyTaskManager() {
                    @Override
                    Calendar getTodaysDate() {
                        return enDate.getCalendar();
                    }

                    @Override
                    public PMDate getLastCompletedDate() {
                        return stDate;
                    }
                };
                manager.loadDownloaders(this);
            }
        };

        new Thread(downloadManager).start();
        while (!downloadManager.isTaskCompleted()) {
            Thread.sleep(1000);
        }
        executor.shutdown();

        List<QuoteVO> quotes = DAOManager.getQuoteDAO().getQuotes("RELIANCE", stDate, enDate);
        assertEquals(1, 063.50f, quotes.get(0).getClose());
        assertEquals(1, 098.00f, quotes.get(1).getClose());
    }

}
