package pm.net.icici;

import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.PMDBTestCase;
import pm.util.AppConst;
import pm.util.PMDate;
import pm.vo.ICICITransaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionDownloaderTest extends PMDBTestCase {

    public TransactionDownloaderTest(String string) {
        super(string, "EmptyData.xml");
    }

    public void testSync() {

        final List<ICICITransaction> transactions = new ArrayList<ICICITransaction>();
        ICICITransaction ranbaxyBuy = new ICICITransaction(new PMDate(11, 6, 2008), "RANLAB", AppConst.TRADINGTYPE.Buy, 30f, 571f, 165.76f, false, "20080611N600020451");
        ICICITransaction suntvSell = new ICICITransaction(new PMDate(10, 6, 2008), "SUNTV", AppConst.TRADINGTYPE.Sell, 25f, 338.1f, 18.97f, true, "20080610N900008911");
        transactions.add(ranbaxyBuy);
        transactions.add(suntvSell);

        final PMDate date = PMDate.today();

        TransactionDownloader downloader = new TransactionDownloader(date, date) {
            @Override
            List<ICICITransaction> download() {
                return transactions;
            }
        };

        downloader.sync();
        List<ICICITransaction> savedTransactions = DAOManager.getTransactionDAO().iciciTransactions();
        assertEquals(transactions.size(), savedTransactions.size());
        assertTrue(savedTransactions.contains(ranbaxyBuy));
        assertTrue(savedTransactions.contains(suntvSell));
    }

}
