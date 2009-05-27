package pm.bo;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import pm.dao.ibatis.dao.IPortfolioDAO;
import pm.util.PMDate;
import pm.vo.PortfolioDetailsVO;
import pm.vo.StockVO;
import pm.vo.StopLossVO;
import pm.vo.TradeVO;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * PortfolioBO Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>08/20/2006</pre>
 */
public class PortfolioBOTest extends MockObjectTestCase {

    public void testGetPortfolioView() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetConsolidatedTradeReport() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetDayTradingReport() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetStockwiseTradeDetails() {
        final ArrayList<TradeVO> tradeVOs = new ArrayList<TradeVO>();
        final String tradingAc = "T";
        final String portfolio = "P";
        String stockCode1 = "CODE1";
        TradeVO tradeVO1 = new TradeVO(stockCode1, new PMDate(1, 1, 2006), 10f, 12f, 15f, tradingAc, portfolio);
        tradeVOs.add(tradeVO1);
        String stockCode2 = "CODE2";
        TradeVO tradeVO2 = new TradeVO(stockCode2, new PMDate(1, 1, 2006), 10f, 12f, 15f, tradingAc, portfolio);
        tradeVOs.add(tradeVO2);
        TradeVO tradeVO3 = new TradeVO(stockCode1, new PMDate(2, 1, 2006), 10f, 12f, 15f, tradingAc, portfolio);
        tradeVOs.add(tradeVO3);
        PortfolioBO bo = new PortfolioBO() {
            public List<TradeVO> getTradeDetails(String tradingAc, String portfolio, boolean dayTrading) {
                assertEquals(tradingAc, tradingAc);
                assertEquals(portfolio, portfolio);
                assertTrue(dayTrading);
                return tradeVOs;
            }
        };

        Hashtable<String, Vector<TradeVO>> groupedTradeVOs = bo.getStockwiseTradeDetails(tradingAc, portfolio, true);
        assertEquals(2, groupedTradeVOs.size());
        assertTrue(groupedTradeVOs.keySet().contains(stockCode1));
        assertTrue(groupedTradeVOs.keySet().contains(stockCode2));
        assertTrue(groupedTradeVOs.get(stockCode1).contains(tradeVO1));
        assertTrue(groupedTradeVOs.get(stockCode1).contains(tradeVO3));
        assertTrue(groupedTradeVOs.get(stockCode2).contains(tradeVO2));
        assertEquals(2, groupedTradeVOs.get(stockCode1).size());
        assertEquals(1, groupedTradeVOs.get(stockCode2).size());
    }

    public void testGetDayTradeTransactionDetails() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetPortfolioPerformance() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetQuote() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetStopLossDetailsWithQuote() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetStopLossDetails() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetStopLossDetailsWithQuoteFilterForNonSet() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetAllPortfolioValue() throws Exception {
        //TODO: Test goes here...
    }

    public void testHandleDuplicateStopLoss() {
        final ArrayList<PortfolioDetailsVO> portfolioList = new ArrayList<PortfolioDetailsVO>();
        final String portfolioName1 = "P1";
        final int portfolioID1 = 100;
        final String portfolioName2 = "P2";
        final Mock mockPortfolioDAO = mock(IPortfolioDAO.class);
        final String latestStockCode = "CODE1NEW";
        final int latestStockId = 1;
        final int originalStockId = 2;
        final String originalStockCode = "CODE1";

        StockVO latestStockVO = new StockVO(latestStockId, latestStockCode);
        StockVO originalStockVO = new StockVO(originalStockId, originalStockCode);

        portfolioList.add(new PortfolioDetailsVO(portfolioName1, portfolioID1));
        portfolioList.add(new PortfolioDetailsVO(portfolioName2, 200));

        mockPortfolioDAO.expects(once()).method("getStopLoss").with(eq(portfolioName1), eq(latestStockCode)).will(returnValue(new StopLossVO("")));
        mockPortfolioDAO.expects(once()).method("getStopLoss").with(eq(portfolioName2), eq(latestStockCode)).will(returnValue(null));
        mockPortfolioDAO.expects(once()).method("deleteStopLossOf").with(eq(portfolioID1), eq(originalStockId));
        mockPortfolioDAO.expects(once()).method("updateSLStockId").with(eq(latestStockId), eq(originalStockId));

        PortfolioBO bo = new PortfolioBO() {
            List<PortfolioDetailsVO> getPortfolioList() {
                return portfolioList;
            }

            IPortfolioDAO dao() {
                return (IPortfolioDAO) mockPortfolioDAO.proxy();
            }
        };
        bo.handleDuplicateInStopLoss(latestStockVO, originalStockVO);
    }

}
