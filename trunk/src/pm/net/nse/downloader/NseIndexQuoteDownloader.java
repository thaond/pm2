package pm.net.nse.downloader;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import pm.bo.QuoteBO;
import pm.net.HTTPHelper;
import pm.net.eod.EODDownloadManager;
import pm.net.eod.IndexQuoteDownloader;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.EquityQuote;
import pm.vo.StockVO;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NseIndexQuoteDownloader extends IndexQuoteDownloader {

    private static String URL = "http://www.nseindia.com/marketinfo/indices/histdata/historicalindices.jsp";
    private static Logger logger = Logger.getLogger(NseIndexQuoteDownloader.class);
    private static String NSEINDIA_URL = "http://www.nseindia.com";

    public NseIndexQuoteDownloader(EODDownloadManager manager, StockVO stockVO) {
        super(manager, stockVO);
    }

    public void downloadData(PMDate stDate, PMDate enDate) {
        try {
            String postData = postData(stDate, enDate, stockVO.getCompanyName());
            List<EquityQuote> quotes = parse(getData(postData));
            store(quotes);
        } catch (ParserException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
    }

    Reader getData(String postData) throws ParserException, IOException {
        String csvURL = findCsvURL(postData);
        if (csvURL != null) {
            waitForServerToCreateFile(csvURL);
            return downloadCSV(csvURL);
        }
        return null;
    }

    private void waitForServerToCreateFile(String csvURL) {
        int count = 0;
        while (!HTTPHelper.isExists(csvURL) && count < 120) {
            count++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            logger.info("waiting for url " + csvURL);
        }
    }

    String findCsvURL(String postData) throws IOException, ParserException {
        String data = getHistoricalDataPageContent(postData);
        Parser parser = new Parser();
        parser.setInputHTML(data);
        NodeFilter filter = new NodeClassFilter(LinkTag.class);
        NodeList list = parser.extractAllNodesThatMatch(filter);
        for (Node node : list.toNodeArray()) {
            String linkStr = ((LinkTag) node).getLink();
            if (linkStr.endsWith(".csv")) {
                linkStr = linkStr.replaceAll(" ", "%20");
                return NSEINDIA_URL + linkStr;
            }
        }
        return null;
    }

    String getHistoricalDataPageContent(String postData) throws IOException {
        return new HTTPHelper().getContentUsingPost(URL, postData, new HashMap<String, String>());
    }

    private Reader downloadCSV(String linkStr) throws IOException {
        return new InputStreamReader(new HTTPHelper().getDataStream(linkStr));
    }

    void store(List<EquityQuote> quotes) {
        if (!quotes.isEmpty()) {
            new QuoteBO().saveIndexQuotes(stockVO.getStockCode(), quotes);
        }
    }

    List<EquityQuote> parse(Reader reader) throws IOException {
        List<EquityQuote> quoteVOs = new ArrayList<EquityQuote>();
        CSVReader csvReader = new CSVReader(reader, ',', '"', 1);
        List<String[]> list = csvReader.readAll();
        for (String[] values : list) {
            PMDate date = PMDateFormatter.parseDD_Mmm_YYYY(values[0]);
            float open = parseFloatWithErrorMasking(values[1]);
            float high = parseFloatWithErrorMasking(values[2]);
            float low = parseFloatWithErrorMasking(values[3]);
            float close = parseFloatWithErrorMasking(values[4]);
            float volume = values.length >= 6 ? parseFloatWithErrorMasking(values[5]) : 0f;
            quoteVOs.add(new EquityQuote(stockVO.getStockCode(), date, open, high, low, close, volume, 0f, 0f, 0f));
        }
        return quoteVOs;
    }

    private float parseFloatWithErrorMasking(String value) {
        float floatValue = 0;
        try {
            floatValue = Float.parseFloat(value);
        } catch (NumberFormatException e) {

        }
        return floatValue;
    }

    String postData(PMDate stDate, PMDate enDate, String companyName) {
        return "FromDate=" + encode(PMDateFormatter.formatWithDelimter(stDate, '-')) +
                "&IndexType=" + encode(companyName) +
                "&Indicesdata=" + encode("Get Details") +
                "&ToDate=" + encode(PMDateFormatter.formatWithDelimter(enDate, '-')) +
                "&check=new";
    }

    private String encode(String s) {
        try {
            return HTTPHelper.encode(s);
        } catch (UnsupportedEncodingException e) {
            logger.error(e, e);
        }
        return null;
    }
}
