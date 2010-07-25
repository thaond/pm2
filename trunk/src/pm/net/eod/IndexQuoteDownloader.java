package pm.net.eod;

import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IQuoteDAO;
import pm.net.AbstractDownloader;
import pm.util.PMDate;
import pm.vo.EquityQuote;
import pm.vo.StockVO;

public abstract class IndexQuoteDownloader extends AbstractDownloader {
    protected EODDownloadManager manager;
    protected final StockVO stockVO;

    public IndexQuoteDownloader(EODDownloadManager manager, StockVO stockVO) {
        this.manager = manager;
        this.stockVO = stockVO;
    }

    public void run() {
        try {
            if (!stop) {
                try {
                    downloadData(findStartDate(stockVO.getStockCode()), new PMDate());
                } catch (Exception e) {
                    logger.error(e, e);
                    error = true;
                }
            }
            completed = true;
        } finally {
            manager.taskCompleted(this);
        }
    }

    PMDate findStartDate(String indexCode) {
        EquityQuote iQuoteVO = quoteDAO().getQuote(indexCode);
        if ((iQuoteVO != null) && (iQuoteVO.getDate() != null)) {
            return iQuoteVO.getDate();
        } else {
            return PMDate.START_DATE;
        }
    }

    IQuoteDAO quoteDAO() {
        return DAOManager.getQuoteDAO();
    }

    public abstract void downloadData(PMDate stDate, PMDate enDate);

    public String getIndexCode() {
        return stockVO.getStockCode();
    }
}
