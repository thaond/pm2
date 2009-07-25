package pm.tools;

import junit.framework.TestCase;
import pm.util.AppConst;
import pm.util.PMDate;
import pm.vo.TransactionVO;

import java.util.TreeMap;
import java.util.Vector;

public class LoadTransDataTest extends TestCase {

    public void testMoveBuysToFirst() {
        TreeMap details = new TreeMap();
        Vector<TransactionVO> transactionVOs = new Vector<TransactionVO>();
        transactionVOs.add(new TransactionVO(new PMDate(1, 1, 2000), "A", AppConst.TRADINGTYPE.Sell, 10f, 10f, 10f, "", "", true));
        transactionVOs.add(new TransactionVO(new PMDate(1, 1, 2000), "A", AppConst.TRADINGTYPE.Buy, 10f, 10f, 10f, "", "", true));
        details.put("Key", transactionVOs);
        new LoadTransData().moveBuysToFirst(details);

        assertEquals(AppConst.TRADINGTYPE.Buy, ((Vector<TransactionVO>) details.get("Key")).get(0).getAction());
        assertEquals(AppConst.TRADINGTYPE.Sell, ((Vector<TransactionVO>) details.get("Key")).get(1).getAction());
    }
}
