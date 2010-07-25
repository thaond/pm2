/*
 * Created on Oct 29, 2004
 *
 */
package pm.net;

import org.apache.log4j.Logger;
import pm.vo.EquityQuote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

/**
 * @author thiyagu1
 */
public abstract class AbstractQuoteDownloader {

    static Logger logger = Logger.getLogger(AbstractQuoteDownloader.class);

    private boolean stopFlag = false;

    public EquityQuote getQuote(String symbol) throws Exception {
        String url = getURL(symbol);
        if (url == null) {
            return new EquityQuote(symbol);
        }
        Reader reader = new HTTPHelper().getHTMLContentReader(url);
        if (reader != null) {
            return parseData(symbol, reader);
        } else {
            logger.error("Error getting Quote Data  from " + url);
            return new EquityQuote(symbol);
        }
    }

    protected abstract EquityQuote parseData(String symbol, Reader reader) throws IOException, ParseException;

    public String getQuotePage(String symbol) {
        String url = getURL(symbol);
        Reader reader = new HTTPHelper().getData(url);
        if (reader != null) {
            BufferedReader inBr = new BufferedReader(reader);
            String line = null;
            StringBuffer sb = new StringBuffer();
            try {
                while ((line = inBr.readLine()) != null) {
                    sb.append(line);
                }
                inBr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        } else {
            System.out.println("Error getting Quote");
            return null;
        }
    }

    public abstract String getURL(String symbol);

    public void stopProcessing() {
        stopFlag = true;
    }

    public boolean isStop() {
        return stopFlag;
    }
}