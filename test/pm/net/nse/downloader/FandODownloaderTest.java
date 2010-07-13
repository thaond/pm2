package pm.net.nse.downloader;

import org.junit.Test;
import pm.util.PMDate;

import static junit.framework.Assert.assertEquals;

public class FandODownloaderTest {

    @Test
    public void testGetFilePath() throws Exception {
        assertEquals("./SampleData/Input/fo04JAN2010bhav.csv.zip", new FandODownloader(new PMDate(4, 1, 2010).getJavaDate(), null).getFilePath());
    }

    @Test
    public void testGetRelativeURL() throws Exception {
        assertEquals("http://www.nseindia.com/content/historical/DERIVATIVES/2010/JAN/fo04JAN2010bhav.csv.zip", new FandODownloader(new PMDate(4, 1, 2010).getJavaDate(), null).getURL());
    }
}
