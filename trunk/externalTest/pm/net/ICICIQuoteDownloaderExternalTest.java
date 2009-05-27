package pm.net;

import junit.framework.TestCase;
import pm.vo.QuoteVO;

/**
 * @author Thiyagu
 * @since 12-Aug-2007
 */
public class ICICIQuoteDownloaderExternalTest extends TestCase {

    public void testDownload() throws Exception {
        if (!HTTPHelper.isNetworkAvailable()) return;
        ICICIQuoteDownloader quoteDownloader = new ICICIQuoteDownloader() {
            String getICICICode(String symbol) {
                return "RELIND";
            }
        };
        QuoteVO quoteVO = quoteDownloader.getQuote("RELIANCE");
        System.out.println(quoteVO);
        assertTrue(quoteVO.getLastPrice() != 0f);
    }
}
