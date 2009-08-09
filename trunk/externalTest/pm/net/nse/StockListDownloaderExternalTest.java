package pm.net.nse;

import junit.framework.TestCase;
import pm.AppLoader;
import pm.net.HTTPHelper;
import pm.vo.StockVO;

import java.util.List;
import java.util.Vector;

public class StockListDownloaderExternalTest extends TestCase {
    final List<StockVO> downloadedList = new Vector<StockVO>();

    public void testRun() {
        if (!HTTPHelper.isNetworkAvailable()) return;
        AppLoader.initConsoleLogger();
        StockListDownloader downloader = new StockListDownloader() {
            void save(Vector<StockVO> stockList) {
                downloadedList.addAll(stockList);
            }
        };
        downloader.run();
        boolean flagFoundReliance = false;
        String relianceStockCode = "RELIANCE";
        for (StockVO stockVO : downloadedList) {
            if (stockVO.getStockCode().equalsIgnoreCase(relianceStockCode)) {
                flagFoundReliance = true;
                break;
            }
        }
        assertTrue(flagFoundReliance);
    }
}
