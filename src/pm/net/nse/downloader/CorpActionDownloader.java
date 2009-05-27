package pm.net.nse.downloader;

import org.htmlparser.util.ParserException;
import pm.dao.CompanyDAO;
import pm.net.HTTPHelper;
import pm.net.nse.AbstractStockDownloadManager;
import pm.net.nse.CorpActionConverter;
import pm.util.AppConfig;
import pm.util.ApplicationException;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.CompanyActionVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

public class CorpActionDownloader extends AbstractHTMLDownloader {

    protected static String baseURL = "http://www.nseindia.com/marketinfo/companyinfo/eod/action.jsp?symbol=";

    private Vector<CompanyActionVO> corpActions = new Vector<CompanyActionVO>();

    protected String stockCode;

    public CorpActionDownloader(String stockCode,
                                AbstractStockDownloadManager manager) {
        super(manager);
        this.stockCode = stockCode;
    }

    protected void performTask() {
        logger.info("Downloading corp action for " + stockCode);
        try {
            Reader reader = getDataReader();
            if (reader == null) {
                error = true;
                return;
            }
            if (stop) {
                return;
            }
            Hashtable<PMDate, String> actionData = parseData(reader);
            if (actionData.isEmpty()) {
                return;
            }
            logger.info("Downloaded Company Action : " + actionData);
//            actionData = removeOldData(actionData); This is might impact performance, but ensures old actions are reprocessed
            convertDownloadedAction(actionData);
            logger.info("Final Corp Actions : " + corpActions);
        } catch (ParserException e) {
            error = true;
            logger.error(e, e);
        }
    }

    Reader getDataReader() throws ParserException {
        return new HTTPHelper().getHTMLContentReader(this.getURL());
    }

    Hashtable<PMDate, String> removeOldData(Hashtable<PMDate, String> actionData) {
        Hashtable<PMDate, String> retVal = new Hashtable<PMDate, String>();
        PMDate lastRunDate = new PMDate(1, 1, 1990);
        try {
            lastRunDate = PMDateFormatter
                    .parseYYYYMMDD(AppConfig.dateCORPACTIONSYNCHRONIZER.Value);
        } catch (ApplicationException e) {
            logger.info(e, e);
        }
        PMDate stDate = lastRunDate.previous(); // include action performed
        // from last run date
        for (PMDate date : actionData.keySet()) {
            if (date.after(stDate)) {
                retVal.put(date, actionData.get(date));
            }
        }
        logger.info("Company Action after removing old data : " + retVal);
        return retVal;
    }

    void convertDownloadedAction(Hashtable<PMDate, String> actionData) {
        CorpActionConverter converter = getConverter();
        corpActions = converter.processCorpAction(stockCode, actionData);
    }

    CorpActionConverter getConverter() {
        return new CorpActionConverter();
    }

    public Vector<CompanyActionVO> getCorpActions() {
        return corpActions;
    }

    public String getStockCode() {
        return stockCode;
    }

    protected Hashtable<PMDate, String> parseData(Reader reader) {
        Hashtable<PMDate, String> retVal = new Hashtable<PMDate, String>();
        String line;
        BufferedReader br = new BufferedReader(reader);
        try {
            boolean startFlag = false;
            int colCount = 0;
            boolean skipLine = false;
            PMDate date = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("Purpose")) {
                    startFlag = true;
                    continue;
                }
                if (!startFlag) {
                    continue;
                }
                if (line.startsWith("Other Info:")) {
                    break;
                }
                colCount++;
                if (colCount == 1 && !line.equals("EQ")) {
                    skipLine = true;
                }
                if (!skipLine && colCount == 5) {
                    try {
                        date = PMDateFormatter.parseDD_MM_YYYY(line);
                    } catch (ApplicationException e) {
                        logger.warn("Error in Date format in stock "
                                + stockCode + " Line: " + line);
                        skipLine = true;
                    }
                }
                if (colCount == 8) {
                    if (skipLine) {
                        skipLine = false;
                    } else {
                        retVal.put(date, line);
                    }
                    colCount = 0;
                }
            }
        } catch (IOException e) {
            logger.error(e, e);
            error = true;
        }
        return retVal;
    }

    @Override
    public String getURL() {
        try {
            return baseURL + HTTPHelper.encode(stockCode);
        } catch (UnsupportedEncodingException e) {
            logger.error(e, e);
            error = true;
            return null;
        }
    }

    CompanyDAO getCompanyDAO() {
        return new CompanyDAO();
    }
}
