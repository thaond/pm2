package pm.net.icici;

import junit.framework.TestCase;
import pm.dao.ibatis.dao.DAOManager;
import pm.vo.StockVO;

import java.util.List;

public class SymbolLookupExternalTest extends TestCase {

    public void testLookup() {
        assertEquals("INFTEC", new SymbolLookup(null).lookup("Infosys Technologies"));
    }

    public void testLookupWithMultipleMatchShouldReturnNull() {
        assertNull(new SymbolLookup(null).lookup("Reliance"));
    }

    public void testLookupAll() {
        List<StockVO> stocks = DAOManager.getStockDAO().getStockList(false);
        for (StockVO stockVO : stocks) {
            System.out.println(stockVO.getStockCode() + " - " + stockVO.getCompanyName() + " - " + new SymbolLookup(null).lookup(stockVO.getCompanyName()));
        }
    }
}
