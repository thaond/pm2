package pm.net.eod;

import org.jmock.cglib.MockObjectTestCase;
import pm.net.AbstractDownloader;
import pm.vo.StockVO;

import java.util.ArrayList;
import java.util.List;

public class IndexQuoteTaskManagerTest extends MockObjectTestCase {

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

            AbstractDownloader getIndexQuoteDownloader(String indexCode, EODDownloadManager downloadManager) {
                downloaderList.add(indexCode);
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
}
