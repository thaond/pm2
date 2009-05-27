package pm.dao.ibatis.dao;

import pm.util.enumlist.BROKERAGETYPE;
import pm.vo.Account;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradingAccountVO;

import java.util.List;

/**
 * AccountDAO Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>08/12/2006</pre>
 */
public class AccountDAOTest extends PMDBTestCase {

    public AccountDAOTest(String string) {
        super(string, "TestData.xml");
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetPorfolioList() throws Exception {
        List<PortfolioDetailsVO> portfolioList = DAOManager.getAccountDAO().getPorfolioList();
        assertTrue(portfolioList.contains(new PortfolioDetailsVO("PortfolioName")));
        assertTrue(portfolioList.contains(new PortfolioDetailsVO("PortfolioName2")));
        assertFalse(portfolioList.get(0).isAlertEnabled());
        assertTrue(portfolioList.get(1).isAlertEnabled());
    }

    public void testUpdatePortfolio() throws Exception {
        IAccountDAO dao = DAOManager.getAccountDAO();
        List<PortfolioDetailsVO> portfolioList = dao.getPorfolioList();
        int originalSize = portfolioList.size();

        portfolioList.get(0).setAlertEnabled(true);
        portfolioList.get(0).setName("MODIFIED");
        dao.updatePortfolio(portfolioList.get(0));
        portfolioList = dao.getPorfolioList();
        int newSize = portfolioList.size();

        assertEquals(originalSize, newSize);
        assertEquals("MODIFIED", portfolioList.get(0).getName());
        assertTrue(portfolioList.get(0).isAlertEnabled());

    }

    public void testInsertPortfolio() throws Exception {
        PortfolioDetailsVO portfolioDetailsVO = new PortfolioDetailsVO("NewPortfolioName2");
        DAOManager.getAccountDAO().insertPortfolio(portfolioDetailsVO);
        try {
            DAOManager.getAccountDAO().insertPortfolio(portfolioDetailsVO);
            fail("Duplicate Portfolio name inserted");
        } catch (Throwable e) {

        }
    }

    public void testGetTradingAccList() throws Exception {
        List<TradingAccountVO> accList = DAOManager.getAccountDAO().getTradingAccList();
        assertTrue(accList.contains(new TradingAccountVO("TradingACCName", BROKERAGETYPE.ICICIDirect)));
        assertTrue(accList.contains(new TradingAccountVO("TradingACCName2", BROKERAGETYPE.HDFC)));
    }

    public void testUpdateTradingAcc() throws Exception {
        IAccountDAO dao = DAOManager.getAccountDAO();
        List<TradingAccountVO> accList = dao.getTradingAccList();
        int originalSize = accList.size();

        accList.get(0).setName("MODIFIED");
        accList.get(0).setBrokeragetype(BROKERAGETYPE.HDFC);
        dao.updateTradingAcc(accList.get(0));
        accList = dao.getTradingAccList();
        int newSize = accList.size();
        assertEquals(originalSize, newSize);

        assertEquals("MODIFIED", accList.get(0).getName());
        assertEquals(BROKERAGETYPE.HDFC, accList.get(0).getBrokeragetype());

    }

    public void testInsertTradingAcc() throws Exception {
        Account tradingAc = new TradingAccountVO("NewTradingACCName2", BROKERAGETYPE.ICICIDirect);
        DAOManager.getAccountDAO().insertTradingAcc(tradingAc);
        try {
            DAOManager.getAccountDAO().insertTradingAcc(tradingAc);
            fail("Duplicate TradingAcc name inserted");
        } catch (Throwable e) {

        }
    }

    public void testGetTradingaccById() {
        TradingAccountVO expectedTradingAcc = new TradingAccountVO("TradForTradingBO1", BROKERAGETYPE.HDFC);
        expectedTradingAcc.setId(3);
        TradingAccountVO accountVO = DAOManager.getAccountDAO().tradingAcc(3);
        assertEquals(expectedTradingAcc, accountVO);
    }

    public void testGetTradingaccByName() {
        String name = "TradForTradingBO1";
        TradingAccountVO expectedTradingAcc = new TradingAccountVO(name, BROKERAGETYPE.HDFC);
        expectedTradingAcc.setId(3);
        TradingAccountVO accountVO = DAOManager.getAccountDAO().tradingAcc(name);
        assertEquals(expectedTradingAcc, accountVO);
    }

    public void testGetPortfolioById() {
        PortfolioDetailsVO expected = new PortfolioDetailsVO("PortForTradingBO3");
        expected.setAlertEnabled(true);
        assertEquals(expected, DAOManager.getAccountDAO().portfolio(5));
    }

    public void testGetPortfolioByName() {
        String name = "PortForTradingBO3";
        PortfolioDetailsVO expected = new PortfolioDetailsVO(name);
        expected.setAlertEnabled(true);
        assertEquals(expected, DAOManager.getAccountDAO().portfolio(name));
    }


}
