package pm.net.icici;

import org.junit.Test;
import pm.util.PMDate;
import pm.vo.ICICITransaction;

import java.io.StringReader;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static pm.util.AppConst.TRADINGTYPE.Buy;
import static pm.util.AppConst.TRADINGTYPE.Sell;

public class ICICICSVTransactionLoaderTest {

    @Test
    public void parse() {
        StringReader reader = new StringReader("22-Jun-09,RELIND  ,Buy  ,30,\"2,024.55\",\"60,736.50\",37.16,20090622N400004027  ,2009112,Rolling  , MarginPLUS   ,NSE  \n" +
                "22-Jun-09,RELIND ,Sell ,30,\"1,970.00\",\"59,100.00\",50.93,20090622N400004028 ,2009112,Rolling ,MarginPLUS ,NSE ");

        List<ICICITransaction> transactions = new ICICICSVTransactionLoader().parse(reader);
        assertEquals(2, transactions.size());
        final ICICITransaction transaction = transactions.get(0);
        assertEquals("RELIND", transaction.getIciciCode());
        assertEquals("20090622N400004027", transaction.getOrderRef());
        assertEquals(new PMDate(22, 6, 2009), transaction.getDate());
        assertEquals(Buy, transaction.getAction());
        assertEquals(30, transaction.getQty(), 0.001f);
        assertEquals(2024.55f, transaction.getPrice(), 0.001f);
        assertEquals(37.16, transaction.getBrokerage(), 0.001f);
        assertTrue(transaction.isDayTrading());
    }

    @Test
    public void parseDeliveryTransaction() {
        StringReader reader = new StringReader("22-Jun-09,RELIND ,Sell ,30,\"1,970.00\",\"59,100.00\",50.93,20090622N400004028 ,2009112,Rolling , IN302679-31502823 ,NSE ");

        List<ICICITransaction> transactions = new ICICICSVTransactionLoader().parse(reader);
        assertEquals(1, transactions.size());
        final ICICITransaction transaction = transactions.get(0);
        assertEquals("RELIND", transaction.getIciciCode());
        assertEquals("20090622N400004028", transaction.getOrderRef());
        assertEquals(new PMDate(22, 6, 2009), transaction.getDate());
        assertEquals(Sell, transaction.getAction());
        assertEquals(30, transaction.getQty(), 0.001f);
        assertEquals(1970.00, transaction.getPrice(), 0.001f);
        assertEquals(50.93, transaction.getBrokerage(), 0.001f);
        assertFalse(transaction.isDayTrading());
    }

    @Test
    public void parseFloat() {
        assertEquals(0f, new ICICICSVTransactionLoader().parseFloat("0"), 0.001f);
        assertEquals(1220f, new ICICICSVTransactionLoader().parseFloat("\"1220\""), 0.001f);
        assertEquals(2024.55f, new ICICICSVTransactionLoader().parseFloat("\"2,024.55\""), 0.001f);
    }

}
