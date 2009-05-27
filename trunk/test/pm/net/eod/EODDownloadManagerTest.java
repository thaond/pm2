package pm.net.eod;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import pm.dao.ibatis.dao.IDateDAO;
import pm.util.PMDate;

public class EODDownloadManagerTest extends MockObjectTestCase {

    private Mock dateDAOMock = mock(IDateDAO.class);

    public void testGetStatusForSuccessfullEODOnMarketDay() throws Exception {
        final PMDate date = new PMDate();
        dateDAOMock.expects(once()).method("nextQuoteDate").withNoArguments().will(returnValue(date.next()));
        assertTrue(downloadManager().getStatus());
    }

    private EODDownloadManager downloadManager() {
        EODDownloadManager downloadManager = new EODDownloadManager(null) {
            IDateDAO dateDAO() {
                return (IDateDAO) dateDAOMock.proxy();
            }
        };
        return downloadManager;
    }

    public void testGetStatusForFailureEODOnMarketDay() throws Exception {
        final PMDate date = new PMDate();
        dateDAOMock.expects(once()).method("nextQuoteDate").withNoArguments().will(returnValue(date));
        assertFalse(downloadManager().getStatus());
    }

    public void testGetStatusForFailureLastMarketDayEODOnMarketHoliday() throws Exception {
        final PMDate date = new PMDate();
        dateDAOMock.expects(once()).method("nextQuoteDate").withNoArguments().will(returnValue(date.previous()));
        assertFalse(downloadManager().getStatus());
    }

}
