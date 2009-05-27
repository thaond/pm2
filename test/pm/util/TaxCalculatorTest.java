package pm.util;

import junit.framework.TestCase;
import pm.util.AppConst.TRADINGTYPE;
import pm.vo.TradeVO;
import pm.vo.TransactionVO;

import java.util.Vector;

public class TaxCalculatorTest extends TestCase {

    /*
      * Test method for 'pm.util.TaxCalculator.calculateBrokerage(TransactionVO)'
      */
    public void testCalculateBrokerageForDeliveryTrading() {

        TransactionVO transactionVO = new TransactionVO(new PMDate(25, 5, 2005), "TCS", TRADINGTYPE.Buy, 10f, 1237.68f, 0, "Thiyagu", "ICICI_DIRECT", false);
        BrokerageVO calculateBrokerage = new TaxCalculator().calculateBrokerage(transactionVO);
        assertEquals(92.83f, calculateBrokerage.getBrokerage());
        assertEquals(9.47f, calculateBrokerage.getServiceTax());
        assertEquals(9.28f, calculateBrokerage.getStt());

        transactionVO = new TransactionVO(new PMDate(8, 4, 2005), "UCAFUE", TRADINGTYPE.Sell, 20f, 200f, 0, "Thiyagu", "ICICI_DIRECT", false);
        calculateBrokerage = new TaxCalculator().calculateBrokerage(transactionVO);
        assertEquals(30.0f, calculateBrokerage.getBrokerage());
        assertEquals(3.06f, calculateBrokerage.getServiceTax());
        assertEquals(3.0f, calculateBrokerage.getStt());
        assertEquals(36.06f, calculateBrokerage.getTotal());

        transactionVO = new TransactionVO(new PMDate(8, 4, 2005), "TEST MINIMUM Brokerage", TRADINGTYPE.Buy, 20f, 100f, 0, "Thiyagu", "ICICI_DIRECT", false);
        calculateBrokerage = new TaxCalculator().calculateBrokerage(transactionVO);
        assertEquals(25.0f, calculateBrokerage.getBrokerage());
        assertEquals(2.55f, calculateBrokerage.getServiceTax());
        assertEquals(1.5f, calculateBrokerage.getStt());
        assertEquals(29.05f, calculateBrokerage.getTotal());

        transactionVO = new TransactionVO(new PMDate(3, 6, 2005), "ITC", TRADINGTYPE.Buy, 15f, 1560.95f, 0, "Thiyagu", "ICICI_DIRECT", false);
        calculateBrokerage = new TaxCalculator().calculateBrokerage(transactionVO);
        assertEquals(175.61f, calculateBrokerage.getBrokerage());
        assertEquals(17.91f, calculateBrokerage.getServiceTax());
        assertEquals(23.41f, calculateBrokerage.getStt());
        assertEquals(216.93f, calculateBrokerage.getTotal());

        transactionVO = new TransactionVO(new PMDate(22, 3, 2006), "TVTNET", TRADINGTYPE.Sell, 50f, 95.9f, 0, "Thiyagu", "ICICI_DIRECT", false);
        calculateBrokerage = new TaxCalculator().calculateBrokerage(transactionVO);
        assertEquals(35.96f, calculateBrokerage.getBrokerage());
        assertEquals(3.67f, calculateBrokerage.getServiceTax());
        assertEquals(4.8f, calculateBrokerage.getStt());
        assertEquals(44.43f, calculateBrokerage.getTotal());
    }

    //----------NOT RUNNING------------------
    public void xtestCalculateBrokerageForDayTrading() {

        TransactionVO transactionVO = new TransactionVO(new PMDate(1, 4, 2005), "INFY", TRADINGTYPE.Buy, 25f, 2231.84f, 0, "Thiyagu", "ICICI_DIRECT", true);
        BrokerageVO calculateBrokerage = new TaxCalculator().calculateBrokerage(transactionVO);
        assertEquals(55.80f, calculateBrokerage.getBrokerage());
        assertEquals(5.69f, calculateBrokerage.getServiceTax());
        assertEquals(0f, calculateBrokerage.getStt());
        assertEquals(61.49f, calculateBrokerage.getTotal());

        transactionVO = new TransactionVO(new PMDate(1, 4, 2005), "INFY", TRADINGTYPE.Sell, 25f, 2245.37f, 0, "Thiyagu", "ICICI_DIRECT", true);
        calculateBrokerage = new TaxCalculator().calculateBrokerage(transactionVO);
        assertEquals(56.13f, calculateBrokerage.getBrokerage());
        assertEquals(5.73f, calculateBrokerage.getServiceTax());
        assertEquals(8.42f, calculateBrokerage.getStt());
        assertEquals(70.28f, calculateBrokerage.getTotal());

        transactionVO = new TransactionVO(new PMDate(8, 4, 2005), "TEST MINIMUM Brokerage", TRADINGTYPE.Buy, 20f, 100f, 0, "Thiyagu", "ICICI_DIRECT", true);
        calculateBrokerage = new TaxCalculator().calculateBrokerage(transactionVO);
        assertEquals(15.0f, calculateBrokerage.getBrokerage());
        assertEquals(1.53f, calculateBrokerage.getServiceTax());
        assertEquals(0f, calculateBrokerage.getStt());
        assertEquals(16.53f, calculateBrokerage.getTotal());

        transactionVO = new TransactionVO(new PMDate(14, 9, 2005), "IPCL", TRADINGTYPE.Buy, 200f, 213.4f, 0, "Thiyagu", "ICICI_DIRECT", true);
        calculateBrokerage = new TaxCalculator().calculateBrokerage(transactionVO);
        assertEquals(42.68f, calculateBrokerage.getBrokerage());
        assertEquals(4.35f, calculateBrokerage.getServiceTax());
        assertEquals(0f, calculateBrokerage.getStt());
        assertEquals(47.03f, calculateBrokerage.getTotal());

        transactionVO = new TransactionVO(new PMDate(8, 8, 2005), "RELIANCE", TRADINGTYPE.Buy, 20f, 726.75f, 0, "Thiyagu", "ICICI_DIRECT", true);
        calculateBrokerage = new TaxCalculator().calculateBrokerage(transactionVO);
        assertEquals(42.68f, calculateBrokerage.getBrokerage());
        assertEquals(4.35f, calculateBrokerage.getServiceTax());
        assertEquals(0f, calculateBrokerage.getStt());
        assertEquals(47.03f, calculateBrokerage.getTotal());

        transactionVO = new TransactionVO(new PMDate(14, 9, 2005), "IPCL", TRADINGTYPE.Sell, 200f, 205.51f, 0, "Thiyagu", "ICICI_DIRECT", true);
        calculateBrokerage = new TaxCalculator().calculateBrokerage(transactionVO);
        assertEquals(41.10f, calculateBrokerage.getBrokerage());
        assertEquals(4.19f, calculateBrokerage.getServiceTax());
        assertEquals(8.22f, calculateBrokerage.getStt());
        assertEquals(53.51f, calculateBrokerage.getTotal());
    }

    public void _testCalculateSTTDeliveryBased() throws Exception {
        PMDate stDate = new PMDate();
        PMDate enDate = new PMDate();

        TradeVO tradeVO = new TradeVO("20050531,100.0,158.99,274.5127,20050930,137.3,310.0,1.0,", false);
        float stt = new TaxCalculator().calculateSTTDeliveryBased(tradeVO, stDate, enDate);
        assertEquals(25.65f, stt);

        tradeVO = new TradeVO("20050915,100.0,186.875,296.68848,20051005,133.35,0.0,1.0,", false);
        stt = new TaxCalculator().calculateSTTDeliveryBased(tradeVO, stDate, enDate);
        assertEquals(32.02f, stt);
    }

    public void testCalculateSTTDayTradeBased() throws Exception {
        TradeVO tradeVO = new TradeVO("IPCL,20050914,200.0,213.45,100.5696,20050914,205.55,0.0,1.0,", true);
        float stt = new TaxCalculator().calculateSTTDayTradeBased(tradeVO);
        assertEquals(8.22f, stt);

        tradeVO = new TradeVO("INFOSYSTCH,20050405,25.0,2227.8,133.76709,20050405,2224.0,0.0,1.0,", true);
        stt = new TaxCalculator().calculateSTTDayTradeBased(tradeVO);
        assertEquals(8.34f, stt);
    }

    public void _testCalculatePLForDeliveryBasedTrade() throws Exception {
        PMDate stDate = new PMDate();
        PMDate enDate = new PMDate();
        Vector<TradeVO> tradeVOs = new Vector<TradeVO>();
        tradeVOs.add(new TradeVO("20050531,100.0,158.99,274.5127,20050930,137.3,310.0,1.0,", false));
        tradeVOs.add(new TradeVO("20050915,100.0,186.875,296.68848,20051005,133.35,0.0,1.0,", false));
        PLVO plvo = new TaxCalculator().calculatePL(tradeVOs, false, stDate, enDate);
        assertEquals(57.67f, plvo.getStt());
        assertEquals(-8035.0312f, plvo.getPl());

    }

}
