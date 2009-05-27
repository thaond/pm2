package pm.net;

import junit.framework.TestCase;
import pm.util.PMDate;
import pm.vo.SymbolChange;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

/**
 * @author Thiyagu
 * @since 22-Jun-2007
 */
public class NSESymbolChangeDownloaderTest extends TestCase {


    public void testParseData() {
        NSESymbolChangeDownloader downloader = new NSESymbolChangeDownloader() {
            Reader getData() {
                return new StringReader(sampleData());
            }
        };

        List<SymbolChange> list = downloader.download();
        assertEquals(4, list.size());
        SymbolChange symbolChange = list.get(0);
        assertEquals("BIRLA3M", symbolChange.getOldCode());
        assertEquals("3MINDIA", symbolChange.getNewCode());
        assertEquals(new PMDate(15, 6, 2004), symbolChange.getFromDate());
        symbolChange = list.get(3);
        assertEquals("ADANIEXPO", symbolChange.getOldCode());
        assertEquals("ADANIENT", symbolChange.getNewCode());
        assertEquals(new PMDate(20, 9, 2006), symbolChange.getFromDate());
    }

    private String sampleData() {
        return "SYMB_COMPANY_NAME, SM_KEY_SYMBOL, SM_NEW_SYMBOL, SM_APPLICABLE_FROM\n" +
                "3M India Limited,BIRLA3M,3MINDIA,15-JUN-2004\n" +
                "Aban Offshore Ltd.,ABANLLOYD,ABANLOYD,17-JAN-2005\n" +
                "Aban Offshore Ltd.,ABANLOYD,ABAN,18-AUG-2006\n" +
                "Adani Enterprises Limited,ADANIEXPO,ADANIENT,20-SEP-2006\n";
    }
}
