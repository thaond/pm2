/*
 * Created on Oct 29, 2004
 *
 */
package pm.net;

import pm.vo.QuoteVO;

/**
 * @author thiyagu1
 */
public class WorkerThread extends Thread {
    private AbstractQuoteDownloader iQuote;
    private QuoteVO quoteVO;
    private String stockCode;

    public WorkerThread(AbstractQuoteDownloader iQuote, String stockCode) {
        this.iQuote = iQuote;
        this.stockCode = stockCode;
    }

    public void run() {
        try {
            quoteVO = iQuote.getQuote(stockCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public QuoteVO getQuote() {
        return quoteVO;
    }

    public void stopProcessing() {
        iQuote.stopProcessing();
    }

}
