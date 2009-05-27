package pm.net.eod;

import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IQuoteDAO;
import pm.net.AbstractDownloader;
import pm.net.NSE;
import pm.net.YahooQuoteDownloader;
import pm.util.PMDate;
import pm.vo.QuoteVO;

public class IndexQuoteDownloader extends AbstractDownloader {

    private String indexCode;
    private EODDownloadManager manager;


    public IndexQuoteDownloader(String indexCode, EODDownloadManager manager) {
        this.indexCode = indexCode;
        this.manager = manager;
    }

    public void run() {
        if (!stop) {
            if (downloadData(indexCode)) {
                downloadData(indexCode);
            }
        }
        completed = true;
        manager.taskCompleted(this);
    }

    public boolean downloadData(String indexCode) {
        boolean retryFlag = false;
        PMDate stDate = findStartDate(indexCode);
        PMDate enDate = new PMDate();

        logger.info("Downloading Index quote for " + indexCode + "[" + stDate + " to " + enDate + "]");


        if (stDate.equals(enDate)) {
            if (isMarketClosed())
                downloadLiveQuote(indexCode, stDate);
            return retryFlag;
        } else {
            return downloadHistoricQuote(indexCode, stDate, enDate);
        }
    }

    boolean downloadHistoricQuote(String indexCode, PMDate stDate, PMDate enDate) {
        return new YahooHQDownloader().downloadQuote(indexCode, stDate, enDate);
    }

    PMDate findStartDate(String indexCode) {
        QuoteVO iQuoteVO = quoteDAO().getQuote(indexCode);
        if ((iQuoteVO != null) && (iQuoteVO.getDate() != null)) {
            return iQuoteVO.getDate();
        } else {
            return PMDate.START_DATE;
        }
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

    IQuoteDAO quoteDAO() {
        return DAOManager.getQuoteDAO();
    }

    QuoteVO getQuote(String indexCode) throws Exception {
        QuoteVO quoteVO = new YahooQuoteDownloader().getQuote(indexCode);
        quoteVO.setStockCode(indexCode);
        return quoteVO;
    }

    public String getIndexCode() {
        return indexCode;
    }
}
