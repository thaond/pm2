package pm.net.nse;

import junit.framework.TestCase;
import pm.util.ApplicationException;
import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.StockVO;

public class StockListDownloaderTest extends TestCase {


    public void testBuildStockVO() throws ApplicationException {
        StockVO stockVO = new StockListDownloader().buildStockVO("3IINFOTECH,3i Infotech Limited,EQ,22-APR-2005,10,1,INE748C01020,10");
        StockVO expected = new StockVO("3IINFOTECH", "3i Infotech Limited", 10f, SERIESTYPE.equity, 10f, (short) 1, "INE748C01020", new PMDate(22, 4, 2005), true);
        assertEquals(expected, stockVO);
    }
}
