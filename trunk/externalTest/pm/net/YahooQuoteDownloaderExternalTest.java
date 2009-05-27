package pm.net;

import junit.framework.TestCase;
import pm.util.PMDate;
import pm.vo.QuoteVO;

/**
 * @author Thiyagu
 * @since 08-Aug-2007
 */

public class YahooQuoteDownloaderExternalTest extends TestCase {

    public void testGetQuote() throws Exception {
        if (!HTTPHelper.isNetworkAvailable()) return;
        YahooQuoteDownloader quoteDownloader = new YahooQuoteDownloader() {
            String getYahooCode(String symbol) {
                return symbol;
            }
        };
        verifyResult(quoteDownloader, "RELIANCE.NS");
        verifyResult(quoteDownloader, "^BSESN");
    }

    private void verifyResult(YahooQuoteDownloader quoteDownloader, String stockCode) throws Exception {
        QuoteVO quoteVO = quoteDownloader.getQuote(stockCode);
        assertTrue(quoteVO.getLastPrice() != 0);
        assertNotNull(quoteVO.getDate());
        if (quoteVO.getDate().equals(new PMDate())) {
            assertTrue(quoteVO.getOpen() != 0);
            assertTrue(quoteVO.getHigh() != 0);
            assertTrue(quoteVO.getLow() != 0);
        }
    }


}
