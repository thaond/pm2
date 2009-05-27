package pm.net.eod;

import pm.dao.ibatis.dao.DAOManager;
import pm.net.AbstractDownloader;
import pm.vo.StockVO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IndexQuoteTaskManager {

    static Set<String> NSE_INDEX = new HashSet<String>();

    static {
        NSE_INDEX.add("^NSEI");
    }


    public void loadDownloaders(EODDownloadManager downloadManager) {
        for (StockVO stockVO : getIndexStocks()) {
            downloadManager.addTask(getIndexQuoteDownloader(stockVO.getStockCode(), downloadManager));
        }
    }

    List<StockVO> getIndexStocks() {
        return DAOManager.getStockDAO().getIndexList();
    }

    AbstractDownloader getIndexQuoteDownloader(String indexCode, EODDownloadManager downloadManager) {
        return new IndexQuoteDownloader(indexCode, downloadManager);
    }

}
