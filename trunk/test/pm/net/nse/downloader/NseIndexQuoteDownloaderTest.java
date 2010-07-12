package pm.net.nse.downloader;

import org.htmlparser.util.ParserException;
import org.junit.Test;
import pm.net.eod.EODDownloadManager;
import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.QuoteVO;
import pm.vo.StockVO;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class NseIndexQuoteDownloaderTest {

    @Test
    public void postData() {
        String postData = new NseIndexQuoteDownloader(null, null).postData(new PMDate(5, 8, 2009), new PMDate(12, 8, 2009), "S&P CNX NIFTY");
        String expected = "FromDate=05-08-2009&IndexType=S%26P+CNX+NIFTY&Indicesdata=Get+Details&ToDate=12-08-2009&check=new";
        assertEquals(expected, postData);
    }

    @Test
    public void parse() throws IOException {
        StockVO stockVO = new StockVO("NIFTY", "S&P CNX NIFTY", 0f, SERIESTYPE.nseindex, 0f, (short) 0, "", new PMDate(), true);
        List<QuoteVO> quotes = new NseIndexQuoteDownloader(null, stockVO).parse(new StringReader(getCSVContent()));
        verifyQuotes(quotes, false);
    }

    @Test
    public void parseToHandleWithoutTradedColumn() throws IOException {
        StockVO stockVO = new StockVO("NIFTY", "S&P CNX NIFTY", 0f, SERIESTYPE.nseindex, 0f, (short) 0, "", new PMDate(), true);
        List<QuoteVO> quotes = new NseIndexQuoteDownloader(null, stockVO).parse(new StringReader(getCSVContentWithoutVolume()));
        verifyQuotes(quotes, true);
    }

    @Test
    public void runToHandleException() {
        StockVO stockVO = new StockVO("NIFTY", "S&P CNX NIFTY", 0f, SERIESTYPE.nseindex, 0f, (short) 0, "", new PMDate(), true);
        NseIndexQuoteDownloader downloader = new NseIndexQuoteDownloader(new EODDownloadManager(null), stockVO) {
            @Override
            Reader getData(String postData) throws ParserException, IOException {
                throw new NullPointerException();
            }
        };
        downloader.run();
        assertTrue(downloader.hasError());
    }

    private String getCSVContentWithoutVolume() {
        return "\"Date\",\"Open\",\"High\",\"Low\",\"Close\"\n" +
                "\"05-Aug-2009\",\"     4680.95\",\"     4717.20\",\"     4629.85\",\"     4694.15\"\n" +
                "\"06-Aug-2009\",\"     4694.35\",\"     4718.15\",\"     4559.20\",\"     4585.50\"\n" +
                "\"07-Aug-2009\",\"     4591.90\",\"     4591.90\",\"     4463.95\",\"     4481.40\"\n" +
                "\"10-Aug-2009\",\"     4486.50\",\"     4562.50\",\"     4399.85\",\"     4437.65\"\n" +
                "\"11-Aug-2009\",\"     4435.00\",\"     4510.80\",\"     4398.90\",\"     4471.35\"\n" +
                "\"12-Aug-2009\",\"     4473.80\",\"     4473.80\",\"     4359.40\",\"     4457.50\"\n";
    }

    private String getCSVContent() {
        return "\"Date\",\"Open\",\"High\",\"Low\",\"Close\",\"Shares Traded\",\"Turnover (Rs. Cr)\"\n" +
                "\"05-Aug-2009\",\"     4680.95\",\"     4717.20\",\"     4629.85\",\"     4694.15\",\"      244640641\",\"         9312.86\"\n" +
                "\"06-Aug-2009\",\"     4694.35\",\"     4718.15\",\"     4559.20\",\"     4585.50\",\"      278150681\",\"        10973.92\"\n" +
                "\"07-Aug-2009\",\"     4591.90\",\"     4591.90\",\"     4463.95\",\"     4481.40\",\"      266871982\",\"         9772.77\"\n" +
                "\"10-Aug-2009\",\"     4486.50\",\"     4562.50\",\"     4399.85\",\"     4437.65\",\"      284079082\",\"        10028.43\"\n" +
                "\"11-Aug-2009\",\"     4435.00\",\"     4510.80\",\"     4398.90\",\"     4471.35\",\"      250129719\",\"         9082.78\"\n" +
                "\"12-Aug-2009\",\"     4473.80\",\"     4473.80\",\"     4359.40\",\"     4457.50\",\"      274149382\",\"         9375.42\"";
    }

    private void verifyQuotes(List<QuoteVO> quotes, boolean noVolume) {
        assertEquals(6, quotes.size());
        QuoteVO quote = quotes.get(0);
        assertEquals(new PMDate(5, 8, 2009), quote.getDate());
        assertEquals(4680.95f, quote.getOpen());
        assertEquals(4717.20f, quote.getHigh());
        assertEquals(4629.85f, quote.getLow());
        assertEquals(4694.15f, quote.getClose());
        if (noVolume) {
            assertEquals(0f, quote.getVolume());
        } else {
            assertEquals(244640641f, quote.getVolume());
        }
    }
}
