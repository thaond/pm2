package pm.dao.ibatis.dao;

import pm.vo.PortfolioDetailsVO;
import pm.vo.StopLossVO;

import java.util.List;
import java.util.Vector;

/**
 * PortfolioDAO Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>08/20/2006</pre>
 */
public class PortfolioDAOTest extends PMDBTestCase {
    public PortfolioDAOTest(String name) {
        super(name, "TestData.xml");
    }

    public void testInsert_GetStopLoss() throws Exception {
        String portfolioName = "PortfolioName";
        StopLossVO slVO1 = new StopLossVO("CODE3", 100.05f, 200.25f, 301.21f, 501.00f, portfolioName);
        StopLossVO slVO2 = new StopLossVO("CODE4", 101.05f, 201.25f, 301.21f, 501.00f, portfolioName);
        List<StopLossVO> slVOs = new Vector<StopLossVO>();
        slVOs.add(slVO1);
        slVOs.add(slVO2);
        IPortfolioDAO dao = DAOManager.getPortfolioDAO();
        dao.insertStopLossVOs(slVOs);
        slVOs = dao.getStopLoss(portfolioName);
        assertTrue(slVOs.contains(slVO1));
        assertTrue(slVOs.contains(slVO2));
    }

    public void testUpdateStopLoss() throws Exception {
        String portfolioName = "PortfolioName";
        IPortfolioDAO dao = DAOManager.getPortfolioDAO();
        List<StopLossVO> slVOs = dao.getStopLoss(portfolioName);
        StopLossVO nonModifiedSLVO = getSLVOByStockCode(slVOs, "CODE2");
        StopLossVO stopLossVO = getSLVOByStockCode(slVOs, "CODE1");
        stopLossVO.setStopLoss1(1f);
        stopLossVO.setStopLoss2(0.5f);
        stopLossVO.setTarget1(2f);
        stopLossVO.setTarget2(3f);
        dao.updateStopLoss(stopLossVO);
        slVOs = dao.getStopLoss(portfolioName);
        assertTrue(slVOs.contains(stopLossVO));
        assertTrue(slVOs.contains(nonModifiedSLVO));

    }

    private StopLossVO getSLVOByStockCode(List<StopLossVO> slVOs, String stockCode) {
        for (StopLossVO stopLossVO : slVOs) {
            if (stopLossVO.getStockCode().equals(stockCode)) {
                return stopLossVO;
            }
        }
        return null;
    }

    public void testDeleteAllStopLossForPortfolio() throws Exception {
        IPortfolioDAO dao = DAOManager.getPortfolioDAO();
        assertTrue(dao.getStopLoss("PortfolioName").size() > 0);
        PortfolioDetailsVO detailsVO = new PortfolioDetailsVO();
        detailsVO.setId(1);
        dao.deleteAllStopLoss(detailsVO);
        assertTrue(dao.getStopLoss("PortfolioName").isEmpty());
    }

    public void testDeleteStopLossForPortfolio() throws Exception {
        IPortfolioDAO dao = DAOManager.getPortfolioDAO();
        List<StopLossVO> list = dao.getStopLoss("PortfolioName");
        StopLossVO slvo = new StopLossVO("CODE1", 100.05f, 200.25f, 300.35f, 400.95f, "PortfolioName");
        assertTrue(list.contains(slvo));
        dao.deleteStopLossOf(1, 1);
        list = dao.getStopLoss("PortfolioName");
        assertFalse(list.contains(slvo));
        slvo = new StopLossVO("CODE2", 100.05f, 200.25f, 300.35f, 400.95f, "PortfolioName");
        assertTrue(list.contains(slvo));
        dao.deleteStopLossOf(1, 1);
        list = dao.getStopLoss("PortfolioName");
        assertTrue(list.contains(slvo));
        dao.deleteStopLossOf(1, 3);
        list = dao.getStopLoss("PortfolioName");
        assertTrue(list.contains(slvo));
        dao.deleteStopLossOf(2, 2);
        list = dao.getStopLoss("PortfolioName");
        assertTrue(list.contains(slvo));
        dao.deleteStopLossOf(1, 2);
        list = dao.getStopLoss("PortfolioName");
        assertFalse(list.contains(slvo));
    }

    public void testUpdateSLStockId() {
        IPortfolioDAO dao = DAOManager.getPortfolioDAO();
        assertNull(dao.getStopLoss("PortfolioName", "CODE10"));
        dao.updateSLStockId(17, 10);
        assertNotNull(dao.getStopLoss("PortfolioName", "CODE10"));
    }

}
