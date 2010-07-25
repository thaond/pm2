package pm.net.eod;

import builder.StockBuilder;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import pm.dao.ibatis.dao.IQuoteDAO;
import pm.util.PMDate;
import pm.vo.EquityQuote;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thiyagu
 * @version $Id: IndexQuoteDownloaderTest.java,v 1.3 2007/12/15 16:10:43 tpalanis Exp $
 * @since 21-Oct-2007
 */
public class YahooQuoteDownloadHandlerTest extends MockObjectTestCase {

    public void testDownloadLiveQuoteGettingaCurrentDaysQuote() {
        final PMDate currDate = new PMDate();
        final EquityQuote quote = new EquityQuote("STOCK", currDate, 0f, 0f, 0, 0f, 0f, 0f, 0f, 0f);
        final Mock mockQuoteDAO = mock(IQuoteDAO.class);
        mockQuoteDAO.expects(once()).method("insertQuote").with(eq(quote));
        YahooQuoteDownloadHandler quoteDownloader = new YahooQuoteDownloadHandler(null, null) {
            EquityQuote getQuote(String indexCode) throws Exception {
                return quote;
            }

            IQuoteDAO quoteDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }

        };
        quoteDownloader.downloadLiveQuote("", currDate);
    }

    public void testDownloadLiveQuoteGettingaPreviousDaysQuote() {
        final PMDate currDate = new PMDate();
        final EquityQuote quote = new EquityQuote("STOCK", currDate.previous(), 0f, 0f, 0, 0f, 0f, 0f, 0f, 0f);
        final Mock mockQuoteDAO = mock(IQuoteDAO.class);

        YahooQuoteDownloadHandler quoteDownloader = new YahooQuoteDownloadHandler(null, null) {
            EquityQuote getQuote(String indexCode) throws Exception {
                return quote;
            }

            IQuoteDAO quoteDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }
        };

        quoteDownloader.downloadLiveQuote("", currDate);
    }

    public void testDownloadDateToDownloadLiveQuote() {
        final List<PMDate> callList = new ArrayList<PMDate>();
        final PMDate pmDate = new PMDate();
        YahooQuoteDownloadHandler quoteDownloader = new YahooQuoteDownloadHandler(new StockBuilder().build(), null) {
            PMDate findStartDate(String indexCode) {
                return pmDate;
            }

            boolean isMarketClosed() {
                return true;
            }

            void downloadLiveQuote(String indexCode, PMDate date) {
                callList.add(date);
            }
        };
        quoteDownloader.downloadData(pmDate, new PMDate());
        assertEquals(1, callList.size());
        assertTrue(callList.contains(pmDate));
    }

    public void testDownloadDateNotToDownloadLiveQuoteIfMarketOpen() {
        final PMDate pmDate = new PMDate();
        YahooQuoteDownloadHandler quoteDownloader = new YahooQuoteDownloadHandler(new StockBuilder().build(), null) {
            PMDate findStartDate(String indexCode) {
                return pmDate;
            }

            boolean isMarketClosed() {
                return false;
            }

            void downloadLiveQuote(String indexCode, PMDate date) {
                fail("Should not download quote when market open");
            }

            boolean downloadHistoricQuote(String indexCode, PMDate stDate, PMDate enDate) {
                fail("Should not historic download quote");
                return false;
            }
        };
        quoteDownloader.downloadData(pmDate, new PMDate());
    }
}
