package pm.net;

import junit.framework.TestCase;
import pm.util.PMDate;
import pm.vo.SymbolChange;

import java.util.List;

/**
 * @author Thiyagu
 * @since 22-Jun-2007
 */
public class NSESymbolChangeDownloaderExternalTest extends TestCase {

    public void testDownload() {
        if (!HTTPHelper.isNetworkAvailable()) return;
        NSESymbolChangeDownloader downloader = new NSESymbolChangeDownloader();
        List<SymbolChange> list = downloader.download();
        int countOnVerifiedDate = 172;
        assertTrue(list.size() >= countOnVerifiedDate);
        boolean foundAztec = false;
        PMDate aztecSymbolChangeDate = new PMDate(20, 9, 2006);
        for (SymbolChange symbolChange : list) {
            if (symbolChange.getOldCode().equals("AZTEC") &&
                    symbolChange.getNewCode().equals("AZTECSOFT") &&
                    symbolChange.getFromDate().equals(aztecSymbolChangeDate)) {
                foundAztec = true;
                break;
            }
        }
        assertTrue(foundAztec);
    }
}
