package pm.net.eod;

import pm.net.NSE;
import pm.net.YahooQuoteDownloader;
import pm.util.PMDate;
import pm.vo.QuoteVO;
import pm.vo.StockVO;

public class YahooQuoteDownloadHandler extends IndexQuoteDownloader {

    public YahooQuoteDownloadHandler(StockVO stockVO, EODDownloadManager manager) {
        super(manager, stockVO);
    }

    @Override
    public void downloadData(PMDate stDate, PMDate enDate) {
        logger.info("Downloading Index quote for " + stockVO.getStockCode() + "[" + stDate + " to " + enDate + "]");

        boolean retryFlag = download(stDate, enDate);
        if (retryFlag) {
            download(stDate, enDate);
        }
    }

    private boolean download(PMDate stDate, PMDate enDate) {
        boolean retryFlag = false;
        if (stDate.equals(enDate)) {
            if (isMarketClosed())
                downloadLiveQuote(stockVO.getStockCode(), stDate);
            return retryFlag;
        } else {
            return downloadHistoricQuote(stockVO.getStockCode(), stDate, enDate);
        }
    }

    boolean downloadHistoricQuote(String indexCode, PMDate stDate, PMDate enDate) {
        return new YahooHQDownloader().downloadQuote(indexCode, stDate, enDate);
    }

    boolean isMarketClosed() {
        return !new NSE().isMarketOpen();
    }

    void downloadLiveQuote(String indexCode, PMDate date) {
        try {
            QuoteVO quoteVO = getQuote(indexCode);
            if (date.equals(quoteVO.getDate()))
                quoteDAO().insertQuote(quoteVO);
        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    QuoteVO getQuote(String indexCode) throws Exception {
        QuoteVO quoteVO = new YahooQuoteDownloader().getQuote(indexCode);
        quoteVO.setStockCode(indexCode);
        return quoteVO;
    }

    public String getIndexCode() {
        return stockVO.getStockCode();
    }
}
