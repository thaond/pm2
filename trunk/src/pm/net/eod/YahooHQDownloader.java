package pm.net.eod;

import org.apache.log4j.Logger;
import pm.bo.QuoteBO;
import pm.net.HTTPHelper;
import pm.util.ApplicationException;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.QuoteVO;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author thiyagu1
 *         This downloads Historic quote from Yahoo site
 */
public class YahooHQDownloader {

    private static String baseURL = "http://ichart.finance.yahoo.com/table.csv?";
    private static final String _DELIMITER = ",";
    private static Logger logger = Logger.getLogger(YahooHQDownloader.class);

    private boolean writeData(String indexCode, Reader reader) {
        boolean retFlag = false;
        try {
            BufferedReader inBr = new BufferedReader(reader);
            String line = inBr.readLine();
            List<QuoteVO> quoteVOs = new Vector<QuoteVO>();
            while ((line = inBr.readLine()) != null) {
                QuoteVO quoteVO = getQuoteVO(line, indexCode);
                if (quoteVO != null) {
                    quoteVOs.add(quoteVO);
                    retFlag = true;
                }
            }
            if (!quoteVOs.isEmpty()) {
                new QuoteBO().saveIndexQuotes(indexCode, quoteVOs);
            }
            inBr.close();
        } catch (FileNotFoundException e) {
            logger.info(e);
        } catch (IOException e) {
            logger.error(e, e);
        }
        return retFlag;
    }

    private QuoteVO getQuoteVO(String line, String indexCode) {
        StringTokenizer stk = new StringTokenizer(line, _DELIMITER);
        PMDate date;
        try {
            date = PMDateFormatter.parseYYYY_MM_DD(stk.nextToken());
        } catch (ApplicationException e) {
            logger.error(e, e);
            return null;
        }
        float open = Float.parseFloat(stk.nextToken());
        float high = Float.parseFloat(stk.nextToken());
        float low = Float.parseFloat(stk.nextToken());
        float close = Float.parseFloat(stk.nextToken());
        float totV = Float.parseFloat(stk.nextToken());
        return new QuoteVO(indexCode, date, open, high, low, close, totV, 0f, 0f, 0f);

    }

    public boolean downloadQuote(String indexCode, PMDate stDate, PMDate enDate) {

        Reader reader = new HTTPHelper().getData(getURL(indexCode, stDate, enDate));

        if (reader == null) {
            logger.info("Error getting yahoo data");
            return false;
        }
        return writeData(indexCode, reader);
    }

    public String getURL(String indexCode, PMDate stDate, PMDate enDate) {
        try {
            StringBuffer sb = new StringBuffer(baseURL);
            sb.append("a=").append(stDate.getMonth() - 1);
            sb.append("&b=").append(stDate.getDate());
            sb.append("&c=").append(stDate.getYear());
            sb.append("&d=").append(enDate.getMonth() - 1);
            sb.append("&e=").append(enDate.getDate());
            sb.append("&f=").append(enDate.getYear());
            sb.append("&s=").append(URLEncoder.encode(indexCode, "UTF-8"));
            sb.append("&y=0&g=d&ignore=.csv");
            String url = sb.toString();
            logger.debug("Yahoo HQ download url " + url);
            return url;
        } catch (UnsupportedEncodingException e) {
            logger.error(e, e);
        }
        return null;
    }

}
