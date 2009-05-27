/*
 * Created on 24-Feb-2005
 *
 */
package pm.analyzer.bo;

import junit.framework.TestCase;
import pm.util.PMDate;
import pm.util.QuoteIterator;
import pm.vo.QuoteVO;

/**
 * @author thiyagu1
 */
public class DeliveryPositionBOTest extends TestCase {

    public void test() {
    }

    QuoteIterator iterator = new QuoteIterator(null, null, null) {
        protected void init(PMDate stDate, PMDate enDate, String stockCode) {
            quoteVOs = new QuoteVO[DeliveryPositionBO._AVGDAYS + 5];
            quoteVOs[0] = new QuoteVO(null, new PMDate(1, 1, 2005), 1, 2, 1, 1, 1, 1, 1, DeliveryPositionBO._AVGDAYS + 1);
            for (int i = 1; i < DeliveryPositionBO._AVGDAYS; i++) {
                quoteVOs[i] = new QuoteVO(null, new PMDate(i + 1, 1, 2005), 1, 1, 1, 1, 1, 1, 1, 1);
            }
            Float factor = new DeliveryPositionBO().getFactors().get(DeliveryPositionBO.FACTOR);
            quoteVOs[DeliveryPositionBO._AVGDAYS] = new QuoteVO(null, new PMDate(1, 2, 2005), 1, 1, 1, 1, 1, 1, 1, 1);
            quoteVOs[DeliveryPositionBO._AVGDAYS + 1] = new QuoteVO(null, new PMDate(2, 2, 2005), 1, 2, 1, 2, 1, 1, 1, factor + 2);
            quoteVOs[DeliveryPositionBO._AVGDAYS + 2] = new QuoteVO(null, new PMDate(3, 2, 2005), 1, 2, 1, 2, 1, 1, 1, factor);
            quoteVOs[DeliveryPositionBO._AVGDAYS + 3] = new QuoteVO(null, new PMDate(4, 2, 2005), 1, 2, 1, 2, 1, 1, 1, factor * DeliveryPositionBO._AVGDAYS);
            quoteVOs[DeliveryPositionBO._AVGDAYS + 4] = new QuoteVO(null, new PMDate(5, 2, 2005), 1, 4, 1, 4, 1, 1, 1, 1);
        }
    };

    /*
      * @see TestCase#tearDown()
      */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testMarkData() {
        boolean retVal = new DeliveryPositionBO().markData(iterator, new PMDate(1, 2, 2005), new PMDate(1, 2, 2005), true, true);
        assertFalse(retVal);

        retVal = new DeliveryPositionBO().markData(iterator, new PMDate(1, 2, 2005), new PMDate(6, 2, 2005), true, true);
        assertTrue(retVal);
        iterator.movePtrToDate(new PMDate(1, 2, 2005));
        QuoteVO quoteVO = iterator.next();
        assertEquals("", quoteVO.getPickDetails());
        quoteVO = iterator.next();
        assertEquals("DP+ ", quoteVO.getPickDetails());
        quoteVO = iterator.next();
        assertEquals("", quoteVO.getPickDetails());
        quoteVO = iterator.next();
        assertEquals("DP+ ", quoteVO.getPickDetails());
        quoteVO = iterator.next();
        assertEquals("DP- ", quoteVO.getPickDetails());
    }

}
