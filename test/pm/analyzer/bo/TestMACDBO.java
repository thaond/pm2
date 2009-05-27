package pm.analyzer.bo;

import junit.framework.TestCase;
import pm.dao.ibatis.dao.DAOManager;
import pm.util.AppConst;
import pm.util.PMDate;
import pm.util.QuoteIterator;
import pm.vo.QuoteVO;

public class TestMACDBO extends TestCase {

    /*
      * Class under test for boolean markData(QuoteIterator, PMDate, PMDate)
      */
    public void test() {
    }

    public final void _testMarkDataQuoteIteratorPMDatePMDate() {
        PMDate frmDate = new PMDate(10, 3, 2005);
        PMDate toDate = new PMDate(12, 3, 2005);
        PMDate hisFrmDt = DAOManager.getDateDAO().getDate(frmDate, -52);
        QuoteIterator iterator = new QuoteIterator(hisFrmDt, "INFOSYSTCH");
        new MACDBO().markData(iterator, frmDate, toDate, true, true);
        iterator.movePtrToDate(frmDate);
        QuoteVO quoteVO = iterator.next();
        assertEquals("", quoteVO.getPickDetails());
        quoteVO = iterator.next();
        assertEquals(AppConst.ANALYZER_LIST.MACD.getNegDisplay() + " ", quoteVO.getPickDetails());
        quoteVO = iterator.next();
        assertEquals("", quoteVO.getPickDetails());
        frmDate = new PMDate(27, 1, 2005);
        toDate = new PMDate(31, 1, 2005);
        hisFrmDt = DAOManager.getDateDAO().getDate(frmDate, -52);
        iterator = new QuoteIterator(hisFrmDt, "INFOSYSTCH");
        new MACDBO().markData(iterator, frmDate, toDate, true, true);
        iterator.movePtrToDate(frmDate);
        quoteVO = iterator.next();
        assertEquals("", quoteVO.getPickDetails());
        quoteVO = iterator.next();
        assertEquals(AppConst.ANALYZER_LIST.MACD.getPosDisplay() + " ", quoteVO.getPickDetails());
        quoteVO = iterator.next();
        assertEquals("", quoteVO.getPickDetails());
    }
}
