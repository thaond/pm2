package pm.net.nse.downloader;

import org.junit.Test;
import pm.util.PMDate;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static pm.net.nse.downloader.BhavCopyDownloader.getURL;

public class BhavCopyDownloaderTest {

    @Test
    public void testGetURL() throws Exception {
        final String url_before_zip = "http://www.nseindia.com/content/historical/EQUITIES/2009/OCT/cm05OCT2009bhav.csv";
        assertEquals(url_before_zip, getURL(new PMDate(5, 10, 2009).getJavaDate()));
        final String url_after_zip_introduced = "http://www.nseindia.com/content/historical/EQUITIES/2009/DEC/cm01DEC2009bhav.csv.zip";
        assertEquals(url_after_zip_introduced, getURL(new PMDate(1, 12, 2009).getJavaDate()));
    }

    @Test
    public void testGetFilePath() throws Exception {
        String filePath = BhavCopyDownloader.getFilePath(new PMDate(5, 10, 2009).getJavaDate());
        assertTrue(filePath.endsWith("cm05OCT2009bhav.csv"));
        filePath = BhavCopyDownloader.getFilePath(new PMDate(5, 12, 2009).getJavaDate());
        assertTrue(filePath.endsWith("cm05DEC2009bhav.csv.zip"));
    }
}
