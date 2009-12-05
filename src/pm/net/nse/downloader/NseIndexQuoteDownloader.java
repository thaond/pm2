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
import pm.vo.QuoteVO;
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
            List<QuoteVO> quotes = parse(getData(postData));
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
            waitForServerToCreateFile();
            return downloadCSV(csvURL);
        }
        return null;
    }

    private void waitForServerToCreateFile() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

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

    void store(List<QuoteVO> quotes) {
        if (!quotes.isEmpty()) {
            new QuoteBO().saveIndexQuotes(stockVO.getStockCode(), quotes);
        }
    }

    List<QuoteVO> parse(Reader reader) throws IOException {
        List<QuoteVO> quoteVOs = new ArrayList<QuoteVO>();
        CSVReader csvReader = new CSVReader(reader, ',', '"', 1);
        List<String[]> list = csvReader.readAll();
        for (String[] values : list) {
            PMDate date = PMDateFormatter.parseDD_Mmm_YYYY(values[0]);
            float open = Float.parseFloat(values[1]);
            float high = Float.parseFloat(values[2]);
            float low = Float.parseFloat(values[3]);
            float close = Float.parseFloat(values[4]);
            float volume = values.length >= 6 ? Float.parseFloat(values[5]) : 0f;
            quoteVOs.add(new QuoteVO(stockVO.getStockCode(), date, open, high, low, close, volume, 0f, 0f, 0f));
        }
        return quoteVOs;
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
