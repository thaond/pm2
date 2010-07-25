package pm.dao.ibatis.dao;

import pm.util.PMDate;
import pm.vo.EquityQuote;
import pm.vo.StockVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * QuoteDAO Tester.
 *
 * @version 1.0
 * @since <pre>08/01/2006</pre>
 */
public class QuoteDAOTest extends PMDBTestCase {

    public QuoteDAOTest(String name) {
        super(name, "TestData.xml");
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetQuotes() throws Exception {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE1";
        List<EquityQuote> quoteVOs = quoteDAO.getQuotes(stockCode);
        assertEquals(1, quoteVOs.size());
        assertEquals(stockCode, quoteVOs.get(0).getStockCode());
        assertEquals(20060101, quoteVOs.get(0).getDateVal());
        assertEquals(10f, quoteVOs.get(0).getOpen());
        assertEquals(20f, quoteVOs.get(0).getHigh());
        assertEquals(5f, quoteVOs.get(0).getLow());
        assertEquals(15f, quoteVOs.get(0).getClose());
        assertEquals(6f, quoteVOs.get(0).getPrevClose());
        assertEquals(100f, quoteVOs.get(0).getVolume());
        assertEquals(1200f, quoteVOs.get(0).getTradeValue());
        assertEquals(50f, quoteVOs.get(0).getPerDeliveryQty());
    }

    public void testGetQuotesForDay() {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        assertEquals(4, quoteDAO.getQuotes(new PMDate(1, 1, 2006)).size());
        assertEquals(3, quoteDAO.getQuotes(new PMDate(2, 1, 2006)).size());
        assertEquals(2, quoteDAO.getQuotes(new PMDate(3, 1, 2006)).size());

    }

    public void testGetQuoteForStock() {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE2";
        EquityQuote quoteVOs = quoteDAO.getQuote(stockCode);
        assertEquals(stockCode, quoteVOs.getStockCode());
        assertEquals(20060103, quoteVOs.getDateVal());
        stockCode = "CODE1";
        quoteVOs = quoteDAO.getQuote(stockCode);
        assertEquals(stockCode, quoteVOs.getStockCode());
        assertEquals(20060101, quoteVOs.getDateVal());
    }

    public void testGetQuoteForStockID() {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE2";
        StockVO stockVO2 = new StockVO("");
        stockVO2.setId(2);
        PMDate date1 = new PMDate(3, 1, 2006);
        EquityQuote quoteVOs = quoteDAO.quote(stockVO2, date1);
        assertEquals(stockCode, quoteVOs.getStockCode());
        assertEquals(date1, quoteVOs.getDate());
        StockVO stockVO1 = new StockVO("");
        stockVO1.setId(2);
        PMDate date2 = new PMDate(1, 1, 2006);
        quoteVOs = quoteDAO.quote(stockVO1, date2);
        assertEquals(stockCode, quoteVOs.getStockCode());
        assertEquals(date2, quoteVOs.getDate());
        assertNull(quoteDAO.quote(stockVO1, new PMDate(1, 1, 1999)));
    }

    public void testGetQuoteForStockVO() {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE2";
        IStockDAO stockDAO = DAOManager.getStockDAO();
        EquityQuote quoteVO = quoteDAO.getQuote(stockDAO.getStock(stockCode));
        assertEquals(stockCode, quoteVO.getStockCode());
        assertEquals(20060103, quoteVO.getDateVal());
        stockCode = "CODE1";
        quoteVO = quoteDAO.getQuote(stockDAO.getStock(stockCode));
        assertEquals(stockCode, quoteVO.getStockCode());
        assertEquals(20060101, quoteVO.getDateVal());
        StockVO stockVO = stockDAO.getStock(stockCode);
        stockVO.setId(10000);
        quoteVO = quoteDAO.getQuote(stockVO);
        assertNull(quoteVO);
    }

    public void testGetQuoteForStockForDate() {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE2";
        EquityQuote quoteVOs = quoteDAO.getQuote(stockCode, new PMDate(3, 1, 2006));
        assertEquals(stockCode, quoteVOs.getStockCode());
        assertEquals(20060103, quoteVOs.getDateVal());
        assertEquals(50f, quoteVOs.getOpen());
        assertEquals(10f, quoteVOs.getHigh());
        stockCode = "CODE1";
        quoteVOs = quoteDAO.getQuote(stockCode, new PMDate(1, 1, 2006));
        assertEquals(stockCode, quoteVOs.getStockCode());
        assertEquals(20060101, quoteVOs.getDateVal());
    }

    public void testGetQuoteForStockForDateRange() {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE4";
        List<EquityQuote> quoteVOs = quoteDAO.getQuotes(stockCode, new PMDate(1, 1, 2006), new PMDate(2, 1, 2006));
        assertEquals(2, quoteVOs.size());
        assertEquals(20060101, quoteVOs.get(0).getDateVal());
        assertEquals(5f, quoteVOs.get(0).getOpen());
        assertEquals(20060102, quoteVOs.get(1).getDateVal());
        assertEquals(15f, quoteVOs.get(1).getOpen());

        quoteVOs = quoteDAO.getQuotes(stockCode, new PMDate(1, 1, 2005), new PMDate(2, 10, 2005));
        assertEquals(0, quoteVOs.size());

        quoteVOs = quoteDAO.getQuotes(stockCode, new PMDate(1, 1, 2005), new PMDate(2, 10, 2006));
        assertEquals(6, quoteVOs.size());
        assertEquals(20060101, quoteVOs.get(0).getDateVal());
        assertEquals(20060108, quoteVOs.get(5).getDateVal());

        quoteVOs = quoteDAO.getQuotes(stockCode, new PMDate(4, 1, 2006), new PMDate(7, 1, 2006));
        assertEquals(2, quoteVOs.size());
        assertEquals(20060104, quoteVOs.get(0).getDateVal());
        assertEquals(20060105, quoteVOs.get(1).getDateVal());
    }

    public void testGetQuoteForStockForDateRangeSupportingNull() {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE4";
        List<EquityQuote> quoteVOs = quoteDAO.getQuotes(stockCode, null, new PMDate(2, 1, 2006));
        assertEquals(2, quoteVOs.size());
        assertEquals(20060101, quoteVOs.get(0).getDateVal());
        assertEquals(5f, quoteVOs.get(0).getOpen());
        assertEquals(20060102, quoteVOs.get(1).getDateVal());
        assertEquals(15f, quoteVOs.get(1).getOpen());

        quoteVOs = quoteDAO.getQuotes(stockCode, new PMDate(1, 1, 2005), new PMDate(2, 10, 2005));
        assertEquals(0, quoteVOs.size());

        quoteVOs = quoteDAO.getQuotes(stockCode, new PMDate(5, 1, 2006), null);
        assertEquals(2, quoteVOs.size());
        assertEquals(20060105, quoteVOs.get(0).getDateVal());
        assertEquals(20060108, quoteVOs.get(1).getDateVal());

        quoteVOs = quoteDAO.getQuotes(stockCode, null, null);
        assertEquals(6, quoteVOs.size());
        assertEquals(20060101, quoteVOs.get(0).getDateVal());
        assertEquals(20060108, quoteVOs.get(5).getDateVal());
    }

    public void testInsertQuote() throws Exception {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE1";
        EquityQuote quoteVO = new EquityQuote(stockCode, new PMDate(2, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);
        quoteVO.setStockVO(DAOManager.getStockDAO().getStock(stockCode));
        quoteDAO.insertQuote(quoteVO);
        List<EquityQuote> quoteVOs = quoteDAO.getQuotes(stockCode);
        assertEquals(2, quoteVOs.size());
        assertTrue(quoteVOs.contains(quoteVO));
    }

    public void testInsertQuoteToInsertAdjustedClose() throws Exception {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE1";
        PMDate date = new PMDate(2, 1, 2006);
        EquityQuote quoteVO = new EquityQuote(stockCode, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);
        quoteVO.setStockVO(DAOManager.getStockDAO().getStock(stockCode));
        quoteDAO.insertQuote(quoteVO);

        EquityQuote actualQuoteVO = quoteDAO.getQuote(stockCode, date);
        assertEquals(7.5f, actualQuoteVO.getAdjustedClose());
    }

    public void testUpdateAdjustedClose() throws Exception {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE1";
        PMDate date = new PMDate(2, 1, 2006);
        createQuote(quoteDAO, stockCode, date);
        quoteDAO.updateAdjustedClose(stockCode, date.next(), 1f / 5f);
        EquityQuote actualQuoteVO = quoteDAO.getQuote(stockCode, date);
        assertEquals(2f, actualQuoteVO.getAdjustedClose());
    }

    private void createQuote(IQuoteDAO quoteDAO, String stockCode, PMDate date) {
        EquityQuote quoteVO = new EquityQuote(stockCode, date, 10f, 15f, 5f, 10f, 100f, 15f, 1000f, 98f);
        quoteVO.setStockVO(DAOManager.getStockDAO().getStock(stockCode));
        quoteDAO.insertQuote(quoteVO);
    }

    public void testInsertQuotes() throws Exception {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE1";
        EquityQuote quoteVO1 = new EquityQuote(stockCode, new PMDate(2, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);
        EquityQuote quoteVO2 = new EquityQuote(stockCode, new PMDate(3, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);
        EquityQuote quoteVO3 = new EquityQuote(stockCode, new PMDate(4, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);
        EquityQuote quoteVO4 = new EquityQuote("CODE2", new PMDate(2, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);
        Vector<EquityQuote> quoteVOs = new Vector<EquityQuote>();
        quoteVOs.add(quoteVO1);
        quoteVOs.add(quoteVO2);
        quoteVOs.add(quoteVO3);
        quoteVOs.add(quoteVO4);
        quoteDAO.insertQuotes(quoteVOs);
        List<EquityQuote> actualQuoteVOs = quoteDAO.getQuotes(stockCode);
        assertEquals(4, actualQuoteVOs.size());
        assertTrue(quoteVOs.contains(quoteVO1));
        assertTrue(quoteVOs.contains(quoteVO2));
        assertTrue(quoteVOs.contains(quoteVO3));
        actualQuoteVOs = quoteDAO.getQuotes("CODE2");
        assertTrue(quoteVOs.contains(quoteVO4));
    }

    public void testUpdateQuotes() throws Exception {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE1";
        EquityQuote quoteVO = new EquityQuote(stockCode, new PMDate(1, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);
        quoteVO.setStockVO(DAOManager.getStockDAO().getStock(stockCode));
        Vector<EquityQuote> quoteVOs = new Vector<EquityQuote>();
        quoteVOs.add(quoteVO);
        assertTrue(quoteDAO.updateQuote(quoteVO));
        List<EquityQuote> actualQuoteVOs = quoteDAO.getQuotes(stockCode);
        assertEquals(1, actualQuoteVOs.size());
        assertTrue(actualQuoteVOs.contains(quoteVO));
    }

    public void testUpdateQuote() throws Exception {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        String stockCode = "CODE1";
        EquityQuote quoteVO = new EquityQuote(stockCode, new PMDate(1, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);
        quoteVO.setStockVO(DAOManager.getStockDAO().getStock(stockCode));
        assertTrue(quoteDAO.updateQuote(quoteVO));
        List<EquityQuote> quoteVOs = quoteDAO.getQuotes(stockCode);
        assertEquals(1, quoteVOs.size());
        assertTrue(quoteVOs.contains(quoteVO));
    }

    public void testQuotes_PMDate_PMDate() {
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        PMDate stDate = new PMDate(1, 1, 2006);
        PMDate enDate = new PMDate(3, 1, 2006);
        List<PMDate> dates = new ArrayList<PMDate>();
        dates.add(new PMDate(1, 1, 2006));
        dates.add(new PMDate(2, 1, 2006));
        dates.add(new PMDate(3, 1, 2006));
        dates.add(new PMDate(4, 1, 2006));
        dates.add(new PMDate(5, 1, 2006));
        dates.add(new PMDate(8, 1, 2006));
        dates.add(new PMDate(9, 1, 2006));

        Map<StockVO, List<EquityQuote>> map = quoteDAO.quotes(dates.get(0), dates.get(2));
        IStockDAO iStockDAO = DAOManager.getStockDAO();
        verify(map, iStockDAO.getStock("CODE1"), 1, dates, 0);
        verify(map, iStockDAO.getStock("CODE2"), 3, dates, 0);
        verify(map, iStockDAO.getStock("CODE3"), 2, dates, 0);
        verify(map, iStockDAO.getStock("CODE4"), 3, dates, 0);
        assertNull(map.get(iStockDAO.getStock("DELISTED")));

        map = quoteDAO.quotes(dates.get(0), dates.get(0));
        verify(map, iStockDAO.getStock("CODE1"), 1, dates, 0);
        verify(map, iStockDAO.getStock("CODE2"), 1, dates, 0);
        verify(map, iStockDAO.getStock("CODE3"), 1, dates, 0);
        verify(map, iStockDAO.getStock("CODE4"), 1, dates, 0);
        assertNull(map.get(iStockDAO.getStock("DELISTED")));

        map = quoteDAO.quotes(dates.get(2), dates.get(3));
        assertNull(map.get(iStockDAO.getStock("CODE1")));
        verify(map, iStockDAO.getStock("CODE2"), 1, dates, 2);
        assertNull(map.get(iStockDAO.getStock("CODE3")));
        verify(map, iStockDAO.getStock("CODE4"), 2, dates, 2);
        assertNull(map.get(iStockDAO.getStock("DELISTED")));

        map = quoteDAO.quotes(dates.get(3), dates.get(6));
        assertNull(map.get(iStockDAO.getStock("CODE1")));
        assertNull(map.get(iStockDAO.getStock("CODE2")));
        assertNull(map.get(iStockDAO.getStock("CODE3")));
        verify(map, iStockDAO.getStock("CODE4"), 3, dates, 3);
        assertNull(map.get(iStockDAO.getStock("DELISTED")));

    }

    private void verify(Map<StockVO, List<EquityQuote>> map, StockVO stockVO1, int size, List<PMDate> dates, int startingIndex) {
        List<EquityQuote> list = map.get(stockVO1);
        assertEquals(size, list.size());
        for (int i = startingIndex; i < size; i++) {
            verify(dates.get(i), list.get(i), stockVO1);
        }
    }

    private void verify(PMDate stDate, EquityQuote quote, StockVO stockVO2) {
        assertEquals(stDate, quote.getDate());
        assertEquals(stockVO2, quote.getStockVO());
    }

    public void testUpdateStockId() {
        final IQuoteDAO dao = DAOManager.getQuoteDAO();
        assertNull(dao.getQuote("CODE16", new PMDate(8, 1, 2006)));
        dao.updateStockId(17, 16);
        assertNotNull(dao.getQuote("CODE16", new PMDate(8, 1, 2006)));
    }


}
