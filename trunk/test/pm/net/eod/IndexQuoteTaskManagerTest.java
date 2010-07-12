package pm.net.eod;

import org.junit.Test;
import pm.net.AbstractDownloader;
import pm.net.nse.downloader.NseIndexQuoteDownloader;
import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.StockVO;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertSame;
import static org.junit.Assert.assertEquals;

public class IndexQuoteTaskManagerTest {

    @Test
    public void testLoadsDownloaderForAllIndexCodes() {

        final AbstractDownloader indexDownloader = new AbstractDownloader() {
            public void run() {
            }
        };
        final List<String> downloaderList = new ArrayList<String>();
        IndexQuoteTaskManager taskManager = new IndexQuoteTaskManager() {
            List<StockVO> getIndexStocks() {
                List<StockVO> indexList = new ArrayList<StockVO>();
                indexList.add(new StockVO("index1"));
                indexList.add(new StockVO("index2"));
                return indexList;
            }

            AbstractDownloader getIndexQuoteDownloader(StockVO stockVO, EODDownloadManager downloadManager) {
                downloaderList.add(stockVO.getStockCode());
                return indexDownloader;
            }
        };

        final List<AbstractDownloader> addedDownloaders = new ArrayList<AbstractDownloader>();
        EODDownloadManager downloadManager = new EODDownloadManager(null) {
            public void addTask(AbstractDownloader downloader) {
                addedDownloaders.add(downloader);
            }
        };
        taskManager.loadDownloaders(downloadManager);
        assertEquals(2, downloaderList.size());
        assertEquals(2, addedDownloaders.size());
    }

    @Test
    public void getIndexQuoteDownloader() {
        StockVO nseStock = new StockVO("NIFTY", "S&P CNX NIFTY", 0f, SERIESTYPE.nseindex, 0f, (short) 0, "", new PMDate(), true);
        AbstractDownloader downloader = new IndexQuoteTaskManager().getIndexQuoteDownloader(nseStock, null);
        assertSame(NseIndexQuoteDownloader.class, downloader.getClass());
        StockVO nonNseStock = new StockVO("NIFTY", "S&P CNX NIFTY", 0f, SERIESTYPE.index, 0f, (short) 0, "", new PMDate(), true);
        downloader = new IndexQuoteTaskManager().getIndexQuoteDownloader(nonNseStock, null);
        assertSame(YahooQuoteDownloadHandler.class, downloader.getClass());
    }

}
