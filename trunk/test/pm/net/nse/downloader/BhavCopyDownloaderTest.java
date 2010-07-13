package pm.net.nse.downloader;

import junit.framework.Assert;
import org.junit.Test;
import pm.util.PMDate;

public class BhavCopyDownloaderTest {

    @Test
    public void testGetFilePath() throws Exception {
        Assert.assertEquals("./SampleData/Input/cm04JAN2010bhav.csv.zip", new BhavCopyDownloader(new PMDate(4, 1, 2010).getJavaDate(), null).getFilePath());
    }

    @Test
    public void testGetRelativeURL() throws Exception {
        Assert.assertEquals("http://www.nseindia.com/content/historical/EQUITIES/2010/JAN/cm04JAN2010bhav.csv.zip", new BhavCopyDownloader(new PMDate(4, 1, 2010).getJavaDate(), null).getURL());
    }

}
