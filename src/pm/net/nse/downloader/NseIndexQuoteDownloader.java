package pm.net.nse.downloader;

import org.apache.log4j.Logger;
import org.htmlparser.util.ParserException;
import pm.bo.QuoteBO;
import pm.net.HTTPHelper;
import pm.net.eod.EODDownloadManager;
import pm.net.eod.IndexQuoteDownloader;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.QuoteVO;
import pm.vo.StockVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class NseIndexQuoteDownloader extends IndexQuoteDownloader {

    private static String URL = "http://www.nseindia.com/marketinfo/indices/histdata/historicalindices.jsp";
    private static Logger logger = Logger.getLogger(NseIndexQuoteDownloader.class);

    public NseIndexQuoteDownloader(EODDownloadManager manager, StockVO stockVO) {
        super(manager, stockVO);
    }

    public void downloadData(PMDate stDate, PMDate enDate) {
        try {
            while (!stDate.after(enDate)) {
                PMDate newEnDate = getPaginatedEnDate(stDate, enDate, 99);
                process(stDate, newEnDate);
                stDate = newEnDate.next();
            }
        } catch (ParserException e) {
            logger.error(e, e);
        }
    }

    void process(PMDate stDate, PMDate newEnDate) throws ParserException {
        String postData = postData(stDate, newEnDate, stockVO.getCompanyName());
        List<QuoteVO> quotes = parse(getData(postData));
        store(quotes);
    }

    PMDate getPaginatedEnDate(PMDate stDate, PMDate enDate, int pageSize) {
        PMDate newEnDate = enDate;
        if (enDate.after(stDate.getDateAddingDays(pageSize))) {
            newEnDate = stDate.getDateAddingDays(pageSize);
        }
        return newEnDate;
    }

    Reader getData(String postData) throws ParserException {
        return new HTTPHelper().getHTMLContentReaderUsingPost(URL, postData);
    }

    void store(List<QuoteVO> quotes) {
        if (!quotes.isEmpty()) {
            new QuoteBO().saveIndexQuotes(stockVO.getStockCode(), quotes);
        }
    }

    List<QuoteVO> parse(Reader reader) {
        List<QuoteVO> quoteVOs = new ArrayList<QuoteVO>();
        BufferedReader br = new BufferedReader(reader);
        String line;
        try {
            skipTill_Rs_Cr(br);
            while ((line = br.readLine()) != null && !line.startsWith("Download file")) {
                PMDate date = PMDateFormatter.parseDD_Mmm_YYYY(line);
                float open = Float.parseFloat(br.readLine());
                float high = Float.parseFloat(br.readLine());
                float low = Float.parseFloat(br.readLine());
                float close = Float.parseFloat(br.readLine());
                float volume = Float.parseFloat(br.readLine());
                float turnover = Float.parseFloat(br.readLine());
                quoteVOs.add(new QuoteVO(stockVO.getStockCode(), date, open, high, low, close, volume, 0f, 0f, 0f));
            }
        } catch (IOException e) {
            logger.error(e, e);
        }
        return quoteVOs;
    }

    private void skipTill_Rs_Cr(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null && !line.equalsIgnoreCase("(Rs. Cr)")) ;
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
