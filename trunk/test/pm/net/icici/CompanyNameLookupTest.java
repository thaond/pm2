package pm.net.icici;

import junit.framework.TestCase;
import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.StockVO;

import java.util.ArrayList;

public class CompanyNameLookupTest extends TestCase {

    public void testFindNseMapping() {
        final StockVO stockVO = new StockVO("RELIANCE", "Reliance Industries Limited", 10f, SERIESTYPE.equity, 10f, (short) 1, "123456", new PMDate(), true);
        ArrayList<StockVO> list = new ArrayList<StockVO>();
        list.add(stockVO);
        assertEquals(stockVO, new CompanyNameLookup(null).findNseMapping("Reliance Industries Limited", list));
    }

    public void testFindNseMappingWithPartialMatch() {
        final StockVO stockVO = new StockVO("RELIANCE", "Reliance Industries Limited", 10f, SERIESTYPE.equity, 10f, (short) 1, "123456", new PMDate(), true);
        ArrayList<StockVO> list = new ArrayList<StockVO>();
        list.add(stockVO);
        assertEquals(stockVO, new CompanyNameLookup(null).findNseMapping("Reliance Industries Ltd.", list));
    }

    public void testFindNseMappingWithPartialMatch2() {
        final StockVO stockVO = new StockVO("RELIANCE", "Reliance", 10f, SERIESTYPE.equity, 10f, (short) 1, "123456", new PMDate(), true);
        ArrayList<StockVO> list = new ArrayList<StockVO>();
        list.add(stockVO);
        assertEquals(stockVO, new CompanyNameLookup(null).findNseMapping("Reliance Industries Ltd.", list));
    }

    public void testFindNseMappingWithNoMatch() {
        final StockVO stockVO = new StockVO("RELIANCE", "RIL", 10f, SERIESTYPE.equity, 10f, (short) 1, "123456", new PMDate(), true);
        ArrayList<StockVO> list = new ArrayList<StockVO>();
        list.add(stockVO);
        assertNull(new CompanyNameLookup(null).findNseMapping("Reliance Industries Ltd.", list));
    }

    public void testFindNseMappingWithPartialMatchOnICICICode() {
        final StockVO stockVO = new StockVO("RELIANCE", "Reliance Industries Limited", 10f, SERIESTYPE.equity, 10f, (short) 1, "123456", new PMDate(), true);
        ArrayList<StockVO> list = new ArrayList<StockVO>();
        list.add(stockVO);
        assertEquals(stockVO, new CompanyNameLookup(null).findNseMapping("Reliance Industries", list));
    }

    public void testFindNseMappingWithMultipleMatch() {
        final StockVO stockVO1 = new StockVO("RIL", "Reliance Industries Limited", 10f, SERIESTYPE.equity, 10f, (short) 1, "123456", new PMDate(), true);
        final StockVO stockVO2 = new StockVO("RCOM", "Reliance Communication Limited", 10f, SERIESTYPE.equity, 10f, (short) 1, "123456", new PMDate(), true);
        ArrayList<StockVO> list = new ArrayList<StockVO>();
        list.add(stockVO1);
        list.add(stockVO2);
        assertNull(new CompanyNameLookup(null).findNseMapping("Reliance", list));
    }

}