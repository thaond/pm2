package pm.net.nse.downloader;

import static junit.framework.Assert.assertEquals;
import org.htmlparser.util.ParserException;
import org.junit.Test;
import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.QuoteVO;
import pm.vo.StockVO;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NseIndexQuoteDownloaderTest {

    @Test
    public void postData() {
        String postData = new NseIndexQuoteDownloader(null, null).postData(new PMDate(5, 8, 2009), new PMDate(12, 8, 2009), "S&P CNX NIFTY");
        String expected = "FromDate=05-08-2009&IndexType=S%26P+CNX+NIFTY&Indicesdata=Get+Details&ToDate=12-08-2009&check=new";
        assertEquals(expected, postData);
    }

    @Test
    public void parse() {
        StockVO stockVO = new StockVO("^NIFTY", "S&P CNX NIFTY", 0f, SERIESTYPE.nseindex, 0f, (short) 0, "", new PMDate(), true);
        List<QuoteVO> quotes = new NseIndexQuoteDownloader(null, stockVO).parse(new StringReader(getContent()));
        verifyQuotes(quotes);
    }

    private void verifyQuotes(List<QuoteVO> quotes) {
        assertEquals(6, quotes.size());
        QuoteVO quote = quotes.get(0);
        assertEquals(new PMDate(5, 8, 2009), quote.getDate());
        assertEquals(4680.95f, quote.getOpen());
        assertEquals(4717.20f, quote.getHigh());
        assertEquals(4629.85f, quote.getLow());
        assertEquals(4694.15f, quote.getClose());
        assertEquals(244640641f, quote.getVolume());
    }

    @Test
    public void downloadData() {
        StockVO stockVO = new StockVO("^NIFTY", "S&P CNX NIFTY", 0f, SERIESTYPE.nseindex, 0f, (short) 0, "", new PMDate(), true);
        final List<QuoteVO> quoteVOs = new ArrayList<QuoteVO>();
        PMDate stDate = new PMDate(1, 1, 2009);
        PMDate enDate = new PMDate(2, 1, 2009);
        NseIndexQuoteDownloader downloader = new NseIndexQuoteDownloader(null, stockVO) {
            @Override
            Reader getData(String postData) throws ParserException {
                assertEquals("FromDate=01-01-2009&IndexType=S%26P+CNX+NIFTY&Indicesdata=Get+Details&ToDate=02-01-2009&check=new", postData);
                return new StringReader(getContent());
            }

            @Override
            void store(List<QuoteVO> quotes) {
                quoteVOs.addAll(quotes);
            }
        };
        downloader.downloadData(stDate, enDate);
        verifyQuotes(quoteVOs);
    }

    @Test
    public void getPaginatedEnDate() {
        NseIndexQuoteDownloader downloader = new NseIndexQuoteDownloader(null, null);
        PMDate stDate = new PMDate(1, 1, 2009);
        assertEquals(stDate.getDateAddingDays(100), downloader.getPaginatedEnDate(stDate, stDate.getDateAddingDays(100), 100));
        assertEquals(stDate.getDateAddingDays(90), downloader.getPaginatedEnDate(stDate, stDate.getDateAddingDays(90), 100));
        assertEquals(stDate.getDateAddingDays(100), downloader.getPaginatedEnDate(stDate, stDate.getDateAddingDays(101), 100));
    }

    @Test
    public void downloadDataToPaginate() {
        final List<List<PMDate>> pageDates = new ArrayList<List<PMDate>>();
        StockVO stockVO = new StockVO("^NIFTY", "S&P CNX NIFTY", 0f, SERIESTYPE.nseindex, 0f, (short) 0, "", new PMDate(), true);
        NseIndexQuoteDownloader downloader = new NseIndexQuoteDownloader(null, stockVO) {
            @Override
            void process(PMDate stDate, PMDate newEnDate) throws ParserException {
                pageDates.add(Arrays.asList(stDate, newEnDate));
            }
        };
        PMDate stDate = new PMDate(1, 1, 2009);
        PMDate enDate = stDate.getDateAddingDays(300);
        downloader.downloadData(stDate, enDate);

        assertEquals(4, pageDates.size());
        assertEquals(Arrays.asList(stDate, stDate.getDateAddingDays(99)), pageDates.get(0));
        assertEquals(Arrays.asList(stDate.getDateAddingDays(100), stDate.getDateAddingDays(199)), pageDates.get(1));
        assertEquals(Arrays.asList(stDate.getDateAddingDays(200), stDate.getDateAddingDays(299)), pageDates.get(2));
        assertEquals(Arrays.asList(stDate.getDateAddingDays(300), stDate.getDateAddingDays(300)), pageDates.get(3));
    }

    private String getContent() {
        return "NSE - Historical Data of NSE Indices\n" +
                "Home > Indices > Statistics > Historical Data of NSE Indices\n" +
                "Historical Data for S&P CNX NIFTY\n" +
                "For the period 05-08-2009 to 12-08-2009\n" +
                "Date\n" +
                "Open\n" +
                "High\n" +
                "Low\n" +
                "Close\n" +
                "Shares Traded\n" +
                "Turnover\n" +
                "(Rs. Cr)\n" +
                "05-Aug-2009\n" +
                "4680.95\n" +
                "4717.20\n" +
                "4629.85\n" +
                "4694.15\n" +
                "244640641\n" +
                "9312.86\n" +
                "06-Aug-2009\n" +
                "4694.35\n" +
                "4718.15\n" +
                "4559.20\n" +
                "4585.50\n" +
                "278150681\n" +
                "10973.92\n" +
                "07-Aug-2009\n" +
                "4591.90\n" +
                "4591.90\n" +
                "4463.95\n" +
                "4481.40\n" +
                "266871982\n" +
                "9772.77\n" +
                "10-Aug-2009\n" +
                "4486.50\n" +
                "4562.50\n" +
                "4399.85\n" +
                "4437.65\n" +
                "284079082\n" +
                "10028.43\n" +
                "11-Aug-2009\n" +
                "4435.00\n" +
                "4510.80\n" +
                "4398.90\n" +
                "4471.35\n" +
                "250129719\n" +
                "9082.78\n" +
                "12-Aug-2009\n" +
                "4473.80\n" +
                "4473.80\n" +
                "4359.40\n" +
                "4457.50\n" +
                "274149382\n" +
                "9375.42\n" +
                "Download file in csv format\n" +
                "Another Search\n" +
                "Top";
    }
}
