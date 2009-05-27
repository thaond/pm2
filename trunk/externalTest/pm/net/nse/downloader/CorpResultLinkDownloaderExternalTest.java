package pm.net.nse.downloader;

import junit.framework.TestCase;

/**
 * @author Thiyagu
 * @version $Id: CorpResultLinkDownloaderExternalTest.java,v 1.1 2007/12/31 09:34:54 tpalanis Exp $
 * @since 31-Dec-2007
 */
public class CorpResultLinkDownloaderExternalTest extends TestCase {

    public void testPerformTask() {
        CorpResultLinkDownloader downloader = new CorpResultLinkDownloader(null, "RELIANCE");
        downloader.performTask();
        assertFalse(downloader.getLinkList().isEmpty());
    }


}
