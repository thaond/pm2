package pm.analyzer.bo;

import junit.framework.TestCase;
import pm.util.PMDate;
import pm.util.QuoteIterator;
import pm.vo.EquityQuote;

public class TestCloseAbove30DHighBO extends TestCase {

    public void test() {
    }

    QuoteIterator iterator = new QuoteIterator(null, null, null) {
        protected void init(PMDate stDate, PMDate enDate, String stockCode) {
            quoteVOs = new EquityQuote[34];
            quoteVOs[0] = new EquityQuote(null, new PMDate(1, 1, 2005), 1, 2, 1, 1, 1, 1, 1, 1);
            for (int i = 1; i < 30; i++) {
                quoteVOs[i] = new EquityQuote(null, new PMDate(i + 1, 1, 2005), 1, 1, 1, 1, 1, 1, 1, 1);
            }
            quoteVOs[30] = new EquityQuote(null, new PMDate(1, 2, 2005), 1, 1, 1, 1, 1, 1, 1, 1);
            quoteVOs[31] = new EquityQuote(null, new PMDate(2, 2, 2005), 1, 2, 1, 2, 1, 1, 1, 1);
            quoteVOs[32] = new EquityQuote(null, new PMDate(3, 2, 2005), 1, 2, 1, 2, 1, 1, 1, 1);
            quoteVOs[33] = new EquityQuote(null, new PMDate(4, 2, 2005), 1, 4, 1, 4, 1, 1, 1, 1);
        }

        public EquityQuote next() {
            EquityQuote quoteVO = super.next();
            return quoteVO;
        }
    };

    /*
      * Class under test for boolean markData(QuoteIterator, PMDate, PMDate)
      */
    public final void _testMarkDataQuoteIteratorPMDatePMDate() {
        iterator.mark();
        new CloseAbove30DHighBO().markData(iterator, new PMDate(1, 2, 2005), new PMDate(3, 2, 2005), true, true);
        iterator.reset();
        iterator.movePtr(30);
        EquityQuote quoteVO = iterator.next();
        assertEquals(new PMDate(1, 2, 2005), quoteVO.getDate());
        assertEquals("", quoteVO.getPickDetails());
        quoteVO = iterator.next();
        assertEquals("30H ", quoteVO.getPickDetails());
        quoteVO = iterator.next();
        assertEquals("", quoteVO.getPickDetails());
        quoteVO = iterator.next();
        assertEquals("", quoteVO.getPickDetails());
    }

}
