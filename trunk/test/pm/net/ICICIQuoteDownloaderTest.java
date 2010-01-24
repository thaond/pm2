package pm.net;

import junit.framework.TestCase;
import pm.vo.QuoteVO;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

/**
 * @author Thiyagu
 * @since 12-Aug-2007
 */
public class ICICIQuoteDownloaderTest extends TestCase {

    public void testParseData() throws IOException, ParseException {
        String data = "ICICI direct.com :: Trading :: Latest Quote\n" +
                "Symbol Code :\n" +
                "Best 5 Bids/Offers\n" +
                "LATEST QUOTE\n" +
                "STOCK NAME\n" +
                ":\n" +
                "RELIANCE IND\n" +
                "NSE\n" +
                "BSE\n" +
                "NSE\n" +
                "BSE\n" +
                "DATE\n" +
                "10-Aug-2007\n" +
                "10-Aug-2007\n" +
                "LAST TRADED TIME\n" +
                "15:29:59\n" +
                "15:29:49\n" +
                "LAST TRADE PRICE\n" +
                "1,818.00\n" +
                "1,819.10\n" +
                "BEST BID PRICE\n" +
                "1,817.00\n" +
                "1,819.10\n" +
                "DAY OPEN\n" +
                "1,820.00\n" +
                "1,805.00\n" +
                "BEST OFFER PRICE\n" +
                "1,818.00\n" +
                "1,819.80\n" +
                "DAY HIGH\n" +
                "1,820.00\n" +
                "1,820.90\n" +
                "BEST BID QTY\n" +
                "31\n" +
                "328\n" +
                "DAY LOW\n" +
                "1,776.20\n" +
                "1,778.00\n" +
                "BEST OFFER QTY\n" +
                "4,020\n" +
                "1\n" +
                "PREVIOUS DAY CLOSE\n" +
                "1,809.05\n" +
                "1,842.00\n" +
                "52 WEEK HIGH\n" +
                "1,948.50\n" +
                "1,948.00\n" +
                "% CHANGE\n" +
                "-1.50\n" +
                "-1.24\n" +
                "52 WEEK LOW\n" +
                "967.20\n" +
                "967.05\n" +
                "DAY VOLUME\n" +
                "3,362,877\n" +
                "918,042\n" +
                "|X| CLOSE\n" +
                "Minimum Browser Requirement: You must have Internet Explorer 5.5 & above or Netscape Communicator 4.7 & above.\n" +
                "Copyright 2007.All rights Reserved. ICICI Brokerage Services Ltd\n" +
                " trademark registration in respect of the concerned mark has been applied for by ICICI Bank Limited\n" +
                "NSE SEBI Registration Number :- INB 230773037 | BSE SEBI Registration Number :- INB 010773035\n" +
                "NSE SEBI Registration Number Derivatives :- INF 230773037.\n" +
                "ICICI Comm Trade Limited\n" +
                "NCDEX Membership No.00034 | MCX Membership No.16065";

        StringReader reader = new StringReader(data);
        QuoteVO quoteVO = new ICICIQuoteDownloader().parseData("RELIND", reader);
        assertEquals(1818f, quoteVO.getLastPrice());
        assertEquals(1820f, quoteVO.getOpen());
        assertEquals(1820f, quoteVO.getHigh());
        assertEquals(1776.2f, quoteVO.getLow());
        assertEquals(1809.05f, quoteVO.getPrevClose());
        assertEquals(3362877f, quoteVO.getVolume());
    }


}
