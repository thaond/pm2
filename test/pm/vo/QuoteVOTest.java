package pm.vo;

import junit.framework.TestCase;
import pm.util.PMDate;

public class QuoteVOTest extends TestCase {

    /*
      * Test method for 'pm.vo.QuoteVO.applyPriceFactor(float)'
      */
    public void testApplyPriceFactor() {
        QuoteVO quoteVO = new QuoteVO("stockCode", new PMDate(10, 1, 2005), 100f, 120f, 80f, 110f, 20, 90, 1, 1);
        quoteVO.applyPriceFactor(.5f);
        assertEquals(50f, quoteVO.getOpen());
        assertEquals(60f, quoteVO.getHigh());
        assertEquals(40f, quoteVO.getLow());
        assertEquals(55f, quoteVO.getLastPrice());
        assertEquals(40f, quoteVO.getVolume());
        assertEquals(45f, quoteVO.getPrevClose());
        assertEquals(1f, quoteVO.getTradeValue());
        assertEquals(1f, quoteVO.getPerDeliveryQty());

    }

}
