/*
 * Created on 17-Feb-2005
 *
 */
package pm.net;

import pm.util.AppConfig;
import pm.util.AppConst.enumQServer;
import pm.vo.EquityQuote;


/**
 * @author thiyagu1
 */
public class LiveQuoteLoader {

    private static WorkerThread[] workerThread;

    /**
     * @param stockCode
     * @return
     */
    public static EquityQuote[] getQuote(String[] stockCode) {
        Class QServerClass = enumQServer.valueOf(AppConfig.quoteServer.Value).getQClass();
        workerThread = new WorkerThread[stockCode.length];
        EquityQuote[] quoteVOs = new EquityQuote[stockCode.length];
        try {
            for (int i = 0; i < stockCode.length; i++) {
                if (stockCode[i].startsWith("^"))  // TODO broken because index doesn't start with ^
                    workerThread[i] = new WorkerThread(new YahooQuoteDownloader(), stockCode[i].substring(1));
                else
                    workerThread[i] = new WorkerThread((AbstractQuoteDownloader) QServerClass.newInstance(), stockCode[i]);
                workerThread[i].start();
            }
            for (int i = 0; i < workerThread.length; i++) {
                try {
                    workerThread[i].join();
                    quoteVOs[i] = workerThread[i].getQuote();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        workerThread = null;
        return quoteVOs;
    }

    public static void stopProcess() {
        if (workerThread == null) return;
        for (int i = 0; i < workerThread.length; i++) {
            if (workerThread[i].isAlive())
                workerThread[i].stopProcessing();
        }
    }

    public static String getQuotePage(String stockCode) {
        Class QServerClass = enumQServer.valueOf(AppConfig.quoteServer.Value).getQClass();
        try {
            return ((AbstractQuoteDownloader) QServerClass.newInstance()).getQuotePage(stockCode);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
