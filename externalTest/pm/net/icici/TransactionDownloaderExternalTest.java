package pm.net.icici;

import junit.framework.TestCase;
import pm.util.AppConst;
import pm.util.PMDate;
import pm.vo.ICICITransaction;
import pm.vo.TransactionVO;

import java.util.List;

public class TransactionDownloaderExternalTest extends TestCase {

    public void testDownload() {
        List<ICICITransaction> transactions = new TransactionDownloader(new PMDate(1, 6, 2008), new PMDate(23, 6, 2008)).download();
        assertEquals(5, transactions.size());
        TransactionVO ranbaxyBuy = new ICICITransaction(new PMDate(11, 6, 2008), "RANLAB", AppConst.TRADINGTYPE.Buy, 30f, 571f, 165.76f, false, "20080611N600020451");
        assertEquals(ranbaxyBuy, transactions.get(0));
        TransactionVO suntvSell = new ICICITransaction(new PMDate(10, 6, 2008), "SUNTV", AppConst.TRADINGTYPE.Sell, 25f, 338.1f, 18.97f, true, "20080610N900008911");
        assertEquals(suntvSell, transactions.get(1));
    }

}
