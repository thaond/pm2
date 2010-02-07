package pm.dao.derby.function;

import pm.TestHelper;
import pm.dao.derby.DBManager;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IDataWarehouseDAO;
import pm.dao.ibatis.dao.IDateDAO;
import pm.dao.ibatis.dao.PMDBCompositeDataSetTestCase;
import pm.util.PMDate;
import pm.vo.EODStatistics;
import pm.vo.StockVO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class EODStatisticsProcedureTest extends PMDBCompositeDataSetTestCase {

    public EODStatisticsProcedureTest(String string) {
        super(string, "EmptyData.xml");
    }

    public void testFindPriorDate() throws Exception {

        int[] dates = new int[]{20060101, 20060102, 20060103, 20060104, 20060105, 20060108, 20060109, 20060110, 20060111, 20060112, 20060115, 20060116, 20060117};

        IDateDAO dateDAO = DAOManager.getDateDAO();
        for (int date : dates) {
            dateDAO.insertDate(new PMDate(date));
        }

        Connection conn = DBManager.createNewConnection();
        assertEquals(20060102, EODStatisticsProcedure.findPriorDate(20060108, 4, conn));
        assertEquals(20060101, EODStatisticsProcedure.findPriorDate(20060101, 4, conn));
        assertEquals(20060101, EODStatisticsProcedure.findPriorDate(20060105, 4, conn));
        assertEquals(20060104, EODStatisticsProcedure.findPriorDate(20060117, 9, conn));
        assertEquals(20060116, EODStatisticsProcedure.findPriorDate(20060117, 1, conn));
        assertEquals(20060115, EODStatisticsProcedure.findPriorDate(20060117, 2, conn));
    }

    public void testCalcuateDayStatistics() throws SQLException {
        String stockCode1 = "CODE1";
        TestHelper.insertStocks(stockCode1);
        PMDate quoteStartDate = new PMDate(20060101);
        List<PMDate> pmDates = TestHelper.insertWeekDays(quoteStartDate.getIntVal(), 20071231);
        TestHelper.insertQuotes(stockCode1, pmDates, 100, 1);
        Connection conn = DBManager.createNewConnection();

        StockVO stockVO = DAOManager.getStockDAO().getStock(stockCode1);
        PMDate statDate = new PMDate(20070101);
        EODStatisticsProcedure.calculateDayStatistics(statDate.getIntVal(), conn);
        EODStatistics eodStatistics = DAOManager.getDataWarehouseDAO().fetchEodStatics(statDate, stockVO);
        TestHelper.validateStatistics(stockVO, statDate, eodStatistics, quoteStartDate);

        statDate = new PMDate(20070201);
        EODStatisticsProcedure.calculateDayStatistics(statDate.getIntVal(), conn);
        eodStatistics = DAOManager.getDataWarehouseDAO().fetchEodStatics(statDate, stockVO);
        TestHelper.validateStatistics(stockVO, statDate, eodStatistics, quoteStartDate);

    }

    public void testCalcuateDayStatisticsToHandleNotEnoughQuotes() throws SQLException {
        String stockCode1 = "CODE1";
        TestHelper.insertStocks(stockCode1);
        PMDate quoteStartDate = new PMDate(20060101);
        List<PMDate> pmDates = TestHelper.insertWeekDays(quoteStartDate.getIntVal(), 20060103);
        TestHelper.insertQuotes(stockCode1, pmDates, 100, 1);
        Connection conn = DBManager.createNewConnection();

        StockVO stockVO = DAOManager.getStockDAO().getStock(stockCode1);
        PMDate statDate = new PMDate(20060102);
        EODStatisticsProcedure.calculateDayStatistics(statDate.getIntVal(), conn);
        EODStatistics eodStatistics = DAOManager.getDataWarehouseDAO().fetchEodStatics(statDate, stockVO);
        TestHelper.validateStatistics(stockVO, statDate, eodStatistics, quoteStartDate);

    }

    public void testCalcuateDayStatisticsForMultipleStocks() throws SQLException {
        String stockCode1 = "CODE1";
        String stockCode2 = "CODE2";
        TestHelper.insertStocks(stockCode1, stockCode2);
        PMDate quoteStartDate = new PMDate(20060101);
        List<PMDate> pmDates = TestHelper.insertWeekDays(quoteStartDate.getIntVal(), 20060120);
        TestHelper.insertQuotes(stockCode1, pmDates, 100, 1);
        TestHelper.insertQuotes(stockCode2, pmDates, 1000, -1);
        Connection conn = DBManager.createNewConnection();

        StockVO stockVO = DAOManager.getStockDAO().getStock(stockCode1);
        StockVO stockVO2 = DAOManager.getStockDAO().getStock(stockCode2);
        PMDate statDate = new PMDate(20060116);
        EODStatisticsProcedure.calculateDayStatistics(statDate.getIntVal(), conn);
        EODStatistics eodStatistics = DAOManager.getDataWarehouseDAO().fetchEodStatics(statDate, stockVO);
        TestHelper.validateStatistics(stockVO, statDate, eodStatistics, quoteStartDate);

        assertEquals(111, eodStatistics.getHigh5D(), .01);
        assertEquals(111, eodStatistics.getHigh20D(), .001);
        assertEquals(111, eodStatistics.getHigh52Week(), .001);
        assertEquals(111, eodStatistics.getHighLifeTime(), .001);

        assertEquals(107, eodStatistics.getLow5D(), .001);
        assertEquals(100, eodStatistics.getLow20D(), .001);
        assertEquals(100, eodStatistics.getLow52Week(), .001);
        assertEquals(100, eodStatistics.getLowLifeTime(), .001);

        assertEquals(106.5, eodStatistics.getMoving10DAverage(), .001);
        assertEquals(105.5, eodStatistics.getMoving50DAverage(), .001);
        assertEquals(105.5, eodStatistics.getMoving200DAverage(), .001);

        eodStatistics = DAOManager.getDataWarehouseDAO().fetchEodStatics(statDate, stockVO2);
        TestHelper.validateStatistics(stockVO2, statDate, eodStatistics, quoteStartDate);

        assertEquals(993, eodStatistics.getHigh5D(), .001);
        assertEquals(1000, eodStatistics.getHigh20D(), .001);
        assertEquals(1000, eodStatistics.getHigh52Week(), .001);
        assertEquals(1000, eodStatistics.getHighLifeTime(), .001);

        assertEquals(989, eodStatistics.getLow5D(), .001);
        assertEquals(989, eodStatistics.getLow20D(), .001);
        assertEquals(989, eodStatistics.getLow52Week(), .001);
        assertEquals(989, eodStatistics.getLowLifeTime(), .001);

        assertEquals(993.5, eodStatistics.getMoving10DAverage(), .001);
        assertEquals(994.5, eodStatistics.getMoving50DAverage(), .001);
        assertEquals(994.5, eodStatistics.getMoving200DAverage(), .001);
    }

    public void testFindDatesToUpdateStatistics() throws Exception {
        String stockCode1 = "CODE1";
        TestHelper.insertStocks(stockCode1);
        PMDate quoteStartDate = new PMDate(20060101);
        List<PMDate> pmDates = TestHelper.insertWeekDays(quoteStartDate.getIntVal(), 20060105);
        TestHelper.insertQuotes(stockCode1, pmDates, 100, 1);
        Connection conn = DBManager.createNewConnection();
        PMDate statDate = new PMDate(20060101);

        DAOManager.getDateDAO().setNSEQuoteStatusFor(statDate);
        List<Integer> dates = EODStatisticsProcedure.findDatesToUpdateStatistics(conn);
        assertEquals(1, dates.size());
        assertEquals((Integer) statDate.getIntVal(), dates.get(0));
        IDataWarehouseDAO warehouseDAO = DAOManager.getDataWarehouseDAO();
        StockVO stockVO = DAOManager.getStockDAO().getStock(stockCode1);
        assertNull(warehouseDAO.fetchEodStatics(statDate, stockVO));
        EODStatisticsProcedure.calculateDayStatistics(statDate.getIntVal(), conn);
        assertNotNull(warehouseDAO.fetchEodStatics(statDate, stockVO));

        DAOManager.getDateDAO().setNSEQuoteStatusFor(statDate.next());
        DAOManager.getDateDAO().setNSEQuoteStatusFor(statDate.next().next());
        dates = EODStatisticsProcedure.findDatesToUpdateStatistics(conn);
        assertEquals(2, dates.size());
        assertEquals((Integer) statDate.next().getIntVal(), dates.get(0));
        assertEquals((Integer) statDate.next().next().getIntVal(), dates.get(1));
    }

    public void testCalculateStatisticsToCalculateForAllDatesWhichHasNSEQuote() throws Exception {
        String stockCode1 = "CODE1";
        TestHelper.insertStocks(stockCode1);
        PMDate quoteStartDate = new PMDate(20060101);
        List<PMDate> pmDates = TestHelper.insertWeekDays(quoteStartDate.getIntVal(), 20060105);
        TestHelper.insertQuotes(stockCode1, pmDates, 100, 1);

        Connection conn = DBManager.createNewConnection();
        StockVO stockVO = DAOManager.getStockDAO().getStock(stockCode1);
        IDataWarehouseDAO warehouseDAO = DAOManager.getDataWarehouseDAO();
        List<PMDate> datesNSESet = Arrays.asList(pmDates.get(0), pmDates.get(1), pmDates.get(2));

        for (PMDate pmDate : datesNSESet) {
            DAOManager.getDateDAO().setNSEQuoteStatusFor(pmDate);
            assertNull(warehouseDAO.fetchEodStatics(pmDate, stockVO));
        }
        EODStatisticsProcedure.calculateStatistics(conn);

        for (PMDate pmDate : datesNSESet) {
            assertNotNull(warehouseDAO.fetchEodStatics(pmDate, stockVO));
        }

        pmDates.removeAll(datesNSESet);
        for (PMDate pmDate : pmDates) {
            assertNull(warehouseDAO.fetchEodStatics(pmDate, stockVO));
        }
    }
}
