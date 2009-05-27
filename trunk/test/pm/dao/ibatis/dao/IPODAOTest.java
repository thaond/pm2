package pm.dao.ibatis.dao;

import pm.util.AppConst;
import pm.util.PMDate;
import pm.vo.IPOVO;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradingAccountVO;
import pm.vo.TransactionVO;

import java.util.List;

/**
 * IPODAO Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>08/26/2006</pre>
 */
public class IPODAOTest extends PMDBTestCase {
    public IPODAOTest(String name) {
        super(name, "TestData.xml");
    }


    public void testInsert_GetIPOTransaction() throws Exception {
        IIPODAO ipoDAO = DAOManager.getIPODAO();
        TradingAccountVO tradingAcc = DAOManager.getAccountDAO().getTradingAccList().get(0);
        PortfolioDetailsVO portfolioAcc = DAOManager.getAccountDAO().getPorfolioList().get(1);

        IPOVO ipovo = new IPOVO("IPOCODE1", new PMDate(1, 1, 2005), 125.05f, 1200.1f, 150000, portfolioAcc, tradingAcc);
        ipoDAO.insertIPOApply(ipovo);
        List<IPOVO> ipoTransaction = ipoDAO.getIPOTransaction(portfolioAcc.getId(), tradingAcc.getId());
        assertTrue(ipoTransaction.contains(ipovo));
    }

    public void testUpdateIPO() throws Exception {
        IIPODAO ipoDAO = DAOManager.getIPODAO();
        List<IPOVO> ipoTransaction = ipoDAO.getIPOTransaction(1, 1);
        IPOVO ipovo = ipoTransaction.get(0);
        TransactionVO allotedTransaction = new TransactionVO(new PMDate(1, 1, 2006), "CODE1", AppConst.TRADINGTYPE.Buy, 10f, 100f, 115f,
                "PortfolioName", "TradingACCName", false);
        allotedTransaction.setId(1);
        ipovo.setAllotedTransaction(allotedTransaction);
        ipovo.setRefundAmount(12000);
        ipovo.setRefundedDate(new PMDate(5, 2, 2006));
        ipoDAO.updateIPO(ipovo);
        List<IPOVO> modifiedIPOTransaction = ipoDAO.getIPOTransaction(1, 1);
        assertEquals(ipoTransaction.size(), modifiedIPOTransaction.size());
        assertTrue(modifiedIPOTransaction.get(0).equals(ipovo));
        for (int i = 1; i < modifiedIPOTransaction.size(); i++) {
            assertEquals(ipoTransaction.get(i), modifiedIPOTransaction.get(i));
        }
    }


}
