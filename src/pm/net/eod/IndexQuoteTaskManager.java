package pm.net.eod;

import pm.dao.ibatis.dao.DAOManager;
import pm.net.AbstractDownloader;
import pm.net.nse.downloader.NseIndexQuoteDownloader;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.StockVO;

import java.util.List;

public class IndexQuoteTaskManager {

    public void loadDownloaders(EODDownloadManager downloadManager) {
        for (StockVO stockVO : getIndexStocks()) {
            downloadManager.addTask(getIndexQuoteDownloader(stockVO, downloadManager));
        }
    }

    List<StockVO> getIndexStocks() {
        return DAOManager.getStockDAO().getIndexList();
    }

    AbstractDownloader getIndexQuoteDownloader(StockVO stockVO, EODDownloadManager downloadManager) {
        AbstractDownloader downloader = null;

        if (stockVO.getSeries() == SERIESTYPE.index)
            downloader = new YahooQuoteDownloadHandler(stockVO, downloadManager);
        else if (stockVO.getSeries() == SERIESTYPE.nseindex)
            downloader = new NseIndexQuoteDownloader(downloadManager, stockVO);

        return downloader;
    }

}
