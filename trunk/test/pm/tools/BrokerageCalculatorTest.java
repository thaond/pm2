package pm.tools;

import junit.framework.TestCase;

public class BrokerageCalculatorTest extends TestCase {

    public void testCalculateBrokerageForDeliveryBasedTrading() throws Exception {
        BrokerageCalculator brokerageCalculator = new BrokerageCalculator();
//        assertEquals(102.85f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(1, 1, 2004), 1000, 12.1f, false));
//        assertEquals(103.28f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(1, 1, 2004), 1000, 12.15f, false));
//        assertEquals(109.98f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(1, 10, 2004), 1000, 12.2f, false));
//        assertEquals(113.03f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(1, 4, 2005), 1000, 12.2f, false));
//        assertEquals(28.77f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(1, 5, 2005), 100, 12.2f, false));
//        assertEquals(182.81f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(20, 4, 2006), 30, 657.7f, false));
//        assertEquals(190.71f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(21, 4, 2006), 30, 657.5f, false));
//        assertEquals(211.02f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Buy, new PMDate(11, 1, 2007), 10, 2182.7f, false));
//        assertEquals(253.91f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(5, 3, 2007), 19, 1382.26f, false));
//        assertEquals(74.70f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(28, 9, 2007), 15, 514.59f, false));
//        assertEquals(650.29f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Buy, new PMDate(6, 2, 2008), 100, 672f, false));

//        assertEquals(55.14f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Buy, new PMDate(9, 5, 2008), 14, 407f, false));

//        assertEquals(100.58f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.HDFC), TRADINGTYPE.Buy, new PMDate(2, 6, 2008), 15, 980f, false));
//        assertEquals(135.67f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.HDFC), TRADINGTYPE.Buy, new PMDate(2, 6, 2008), 10, 1970f, false));
//        assertEquals(312.62f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.HDFC), TRADINGTYPE.Sell, new PMDate(22, 5, 2008), 125, 363f, false));
//        assertEquals(405.46f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.HDFC), TRADINGTYPE.Sell, new PMDate(9, 1, 2008), 250, 236.8f, false));
//
//        assertEquals(63.24f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.IndiaBulls), TRADINGTYPE.Buy, new PMDate(1, 5, 2005), 50, 186.4f, false));
//        assertEquals(67.09f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.IndiaBulls), TRADINGTYPE.Sell, new PMDate(1, 5, 2005), 50, 197.75f, false));
//
//        assertEquals(0f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.IndiaBulls), TRADINGTYPE.IPO, new PMDate(1, 5, 2005), 50, 197.75f, false));
//        assertEquals(0f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.IndiaBulls), TRADINGTYPE.Buy, new PMDate(1, 5, 2005), 0, 197.75f, false));
//        assertEquals(0f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.IndiaBulls), TRADINGTYPE.Buy, new PMDate(1, 5, 2005), 50, 0f, false));
    }

    public void testCalculateBrokerageForDayTrading() throws Exception {
//        BrokerageCalculator brokerageCalculator = new BrokerageCalculator();
//        assertEquals(15f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Buy, new PMDate(14, 10, 2003), 596.75f, 6, true));
//        assertEquals(17.24f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Buy, new PMDate(14, 10, 2003), 383.00f, 30, true));
//        assertEquals(16.71f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(14, 10, 2003), 371.30f, 30, true));
//        assertEquals(20.46f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Buy, new PMDate(1, 10, 2004), 371.30f, 50, true));
//        assertEquals(23.24f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(1, 10, 2004), 371.30f, 50, true));
//        assertEquals(20.46f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Buy, new PMDate(1, 4, 2005), 371.30f, 50, true));
//        assertEquals(24.17f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(1, 4, 2005), 371.30f, 50, true));
////        assertEquals(15f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(1, 5, 2005), 100, 12.2f, true));
//        assertEquals(25.69f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(17, 4, 2006), 30, 657.7f, true));
////        assertEquals(26.08f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(18, 4, 2006), 30, 657.5f, true));
////        assertEquals(73.6f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Buy, new PMDate(31, 10, 2007), 500, 261.9f, true));
//        assertEquals(105.43f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(31, 10, 2007), 500, 259.74f, true));
//        assertEquals(16.85f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Buy, new PMDate(23, 5, 2008), 25, 352.9f, true));
//        assertEquals(19.11f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.ICICIDirect), TRADINGTYPE.Sell, new PMDate(23, 5, 2008), 25, 361.23f, true));
//
//        assertEquals(22.13f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.IndiaBulls), TRADINGTYPE.Buy, new PMDate(1, 5, 2005), 50, 186.4f, true));
//        assertEquals(23.69f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.IndiaBulls), TRADINGTYPE.Sell, new PMDate(1, 5, 2005), 50, 197.75f, true));
//        assertEquals(0f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.IndiaBulls), TRADINGTYPE.Sell, new PMDate(1, 5, 2005), 0f, 197.75f, true));
//        assertEquals(0f, brokerageCalculator.getBrokerage(new TradingAccountVO("", BROKERAGETYPE.IndiaBulls), TRADINGTYPE.Sell, new PMDate(1, 5, 2005), 50, 0f, true));
    }
}
