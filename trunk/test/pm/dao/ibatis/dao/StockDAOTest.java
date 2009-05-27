package pm.dao.ibatis.dao;

import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.StockVO;

import java.util.List;
import java.util.Map;

/**
 * StockDAO Tester.
 *
 * @version 1.0
 * @since <pre>07/22/2006</pre>
 */
public class StockDAOTest extends PMDBCompositeDataSetTestCase {
    public StockDAOTest(String name) {
        super(name, "EmptyData.xml", "StockMasterData.xml");
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetStock() throws Exception {
        IStockDAO stockDAO = DAOManager.getStockDAO();
        StockVO stockVO = stockDAO.getStock("CODE4");
        assertEquals("CODE4", stockVO.getStockCode());
        assertEquals("CompanyName4", stockVO.getCompanyName());
        assertEquals(SERIESTYPE.equity, stockVO.getSeries());
        assertEquals(10f, stockVO.getPaidupValue());
        assertEquals(100, stockVO.getMarketLot());
        assertTrue(stockVO.isListed());
        assertEquals(10f, stockVO.getFaceValue());
        assertEquals(new PMDate(1, 1, 2006), stockVO.getDateOfListing());
        assertEquals("ISIN4", stockVO.getIsin());
    }

    public void testGetStockListForOrderByStockCode() {
        IStockDAO stockDAO = DAOManager.getStockDAO();
        List<StockVO> stockList = stockDAO.getStockList(false);
        assertEquals(6, stockList.size());
        assertEquals("CODE1", stockList.get(0).getStockCode());
        assertEquals("CODE3", stockList.get(2).getStockCode());

        stockList = stockDAO.getStockList(true);
        assertEquals(7, stockList.size());
        assertEquals("CODE1", stockList.get(0).getStockCode());
        assertEquals("^INDEX", stockList.get(6).getStockCode());
        assertEquals(SERIESTYPE.index, stockList.get(6).getSeries());

    }

    public void testGetIndexList() {
        IStockDAO stockDAO = DAOManager.getStockDAO();
        List<StockVO> stockList = stockDAO.getIndexList();
        assertEquals(1, stockList.size());
        assertEquals("^INDEX", stockList.get(0).getStockCode());
    }

    public void testInsertICICICode() {
        IStockDAO stockDAO = DAOManager.getStockDAO();
        StockVO stockVO = stockDAO.getStock("CODE2");
        stockDAO.updateICICICode(stockVO, "ICODE2");
        assertEquals("ICODE2", stockDAO.iciciCode("CODE2"));
        stockDAO.updateICICICode(stockDAO.getStock("CODE1"), "ICODE1");
        assertEquals("ICODE2", stockDAO.iciciCode("CODE2"));
        assertEquals("ICODE1", stockDAO.iciciCode("CODE1"));
        assertNull(stockDAO.iciciCode("DELISTED"));
    }

    public void testICICICodeMapping() {
        IStockDAO stockDAO = DAOManager.getStockDAO();
        StockVO stockVO = stockDAO.getStock("CODE2");
        stockDAO.updateICICICode(stockVO, "ICODE2");
        Map<String, String> mapping = stockDAO.iciciCodeMapping();
        assertEquals(2, mapping.size());
        assertEquals("CODE2", mapping.get("ICODE2"));
    }

    public void testYahooCode() {
        IStockDAO stockDAO = DAOManager.getStockDAO();
        assertEquals("YCODE", stockDAO.yahooCode("CODE1"));
        assertNull(stockDAO.yahooCode("CODE2"));
        assertNull(stockDAO.yahooCode("DELISTED"));
    }

    public void testDelete() {
        IStockDAO stockDAO = DAOManager.getStockDAO();
        final StockVO stockVO = stockDAO.getStock("NewCode0");
        assertNotNull(stockVO);
        final int actualSize = stockDAO.getStockList(true).size();
        stockDAO.delete(stockVO.getId());
        assertNull(stockDAO.getStock(stockVO.getStockCode()));
        final List<StockVO> stockList = stockDAO.getStockList(true);
        assertEquals(actualSize - 1, stockList.size());
        assertFalse(stockList.contains(stockVO));
    }

}
