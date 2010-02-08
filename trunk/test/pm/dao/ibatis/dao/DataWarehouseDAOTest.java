package pm.dao.ibatis.dao;

import pm.TestHelper;
import pm.util.PMDate;
import pm.vo.EODStatistics;
import pm.vo.StockVO;

import java.sql.SQLException;
import java.util.List;

public class DataWarehouseDAOTest extends PMDBCompositeDataSetTestCase {
    public DataWarehouseDAOTest(String string) {
        super(string, "TestData.xml");
    }


    public void testFetchEodStatics() throws Exception {
//   <EODSTATISTICS STOCKID="1" DATEID="20060101" HIGH5D="5.0" HIGH20D="20.0" HIGH52W="52.0" HIGHLIFETIME="1000.0"
//     LOW5D="3.0" LOW20D="2.0" LOW52W="1.0" LOWLIFETIME="0.5" MOVAVG10D="10.01" MOVAVG50D="50.05" MOVAVG200D="200.20"/>

        StockVO stockVO = DAOManager.getStockDAO().getStock("CODE1");
        System.out.println("------" + stockVO.toPrint());
        PMDate pmDate = new PMDate(20060101);
        EODStatistics eodStatistics = DAOManager.getDataWarehouseDAO().fetchEodStatics(pmDate, stockVO);

        assertEquals(stockVO, eodStatistics.getStock());
        assertEquals(pmDate, eodStatistics.getDate());
        assertEquals(5f, eodStatistics.getHigh5D(), .01);
        assertEquals(20f, eodStatistics.getHigh20D(), .001);
        assertEquals(52f, eodStatistics.getHigh52Week(), .001);
        assertEquals(1000f, eodStatistics.getHighLifeTime(), .001);
        assertEquals(3f, eodStatistics.getLow5D(), .001);
        assertEquals(2f, eodStatistics.getLow20D(), .001);
        assertEquals(1f, eodStatistics.getLow52Week(), .001);
        assertEquals(0.5f, eodStatistics.getLowLifeTime(), .001);
    }

    public void testFetchEodStaticsForDifferentDays() throws Exception {

        StockVO stockVO = DAOManager.getStockDAO().getStock("CODE1");
        PMDate pmDate = new PMDate(20060101);
        EODStatistics eodStatistics = DAOManager.getDataWarehouseDAO().fetchEodStatics(pmDate, stockVO);
        assertEquals(stockVO, eodStatistics.getStock());
        assertEquals(pmDate, eodStatistics.getDate());
        assertEquals(5f, eodStatistics.getHigh5D(), .01);

        pmDate = new PMDate(20060102);
        eodStatistics = DAOManager.getDataWarehouseDAO().fetchEodStatics(pmDate, stockVO);
        assertEquals(stockVO, eodStatistics.getStock());
        assertEquals(pmDate, eodStatistics.getDate());
        assertEquals(6f, eodStatistics.getHigh5D(), .01);
    }

    public void testFetchEodStaticsForDifferentStocks() throws Exception {

        StockVO stockVO = DAOManager.getStockDAO().getStock("CODE1");
        PMDate pmDate = new PMDate(20060101);
        EODStatistics eodStatistics = DAOManager.getDataWarehouseDAO().fetchEodStatics(pmDate, stockVO);
        assertEquals(stockVO, eodStatistics.getStock());
        assertEquals(pmDate, eodStatistics.getDate());
        assertEquals(5f, eodStatistics.getHigh5D(), .01);

        stockVO = DAOManager.getStockDAO().getStock("CODE2");
        pmDate = new PMDate(20060101);
        eodStatistics = DAOManager.getDataWarehouseDAO().fetchEodStatics(pmDate, stockVO);
        assertEquals(stockVO, eodStatistics.getStock());
        assertEquals(pmDate, eodStatistics.getDate());
        assertEquals(15f, eodStatistics.getHigh5D(), .01);
    }

    public void testUpdateEodStatistics() throws SQLException {
        String stockCode1 = "STK1";
        String stockCode2 = "STK2";
        TestHelper.insertStocks(stockCode1, stockCode2);
        PMDate quoteStartDate = new PMDate(20090101);
        List<PMDate> pmDates = TestHelper.insertWeekDays(quoteStartDate.getIntVal(), 20090120);
        TestHelper.insertQuotes(stockCode1, pmDates, 100, 1);
        TestHelper.insertQuotes(stockCode2, pmDates, 1000, -1);

        for (PMDate pmDate : pmDates) {
            DAOManager.getDateDAO().setNSEQuoteStatusFor(pmDate);
        }

        StockVO stockVO = DAOManager.getStockDAO().getStock(stockCode1);
        StockVO stockVO2 = DAOManager.getStockDAO().getStock(stockCode2);
        PMDate statDate = new PMDate(20090116);

        IDataWarehouseDAO warehouseDAO = DAOManager.getDataWarehouseDAO();
        warehouseDAO.updateEodStatistics();
        EODStatistics eodStatistics = warehouseDAO.fetchEodStatics(statDate, stockVO);

        assertEquals(111, eodStatistics.getHigh5D(), .01);
        assertEquals(107, eodStatistics.getLow5D(), .001);
        assertEquals(106.5, eodStatistics.getMoving10DAverage(), .001);

        eodStatistics = warehouseDAO.fetchEodStatics(statDate, stockVO2);
        assertEquals(993, eodStatistics.getHigh5D(), .001);
        assertEquals(989, eodStatistics.getLow5D(), .001);
        assertEquals(993.5, eodStatistics.getMoving10DAverage(), .001);

        assertNotNull(warehouseDAO.fetchEodStatics(statDate.next(), stockVO));
        assertNotNull(warehouseDAO.fetchEodStatics(quoteStartDate, stockVO));

    }


}
