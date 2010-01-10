package pm.bo;

import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.PMDBTestCase;
import pm.util.AppConst;
import pm.util.PMDate;
import pm.util.enumlist.ICICITransactionStatus;
import pm.vo.ICICICodeMapping;
import pm.vo.ICICITransaction;
import pm.vo.TransactionVO;

public class IciciTradeBOTest extends PMDBTestCase {

    public IciciTradeBOTest(String string) {
        super(string);
    }

    public void testSyncWithPMTrade() throws Exception {
        ICICICodeMapping iciciCodeMapping = DAOManager.getStockDAO().iciciCodeMappings().get(0);
        TransactionVO buyTransaction = new TransactionVO(new PMDate(), "CODE1", AppConst.TRADINGTYPE.Buy, 14f, 23.2f, 12.07f, "PortfolioName", "ICICI_Direct", true);
        new TradingBO().doTrading(buyTransaction);
        ICICITransaction iciciTransaction = new ICICITransaction(buyTransaction.getDate(), iciciCodeMapping.getIciciCode(), buyTransaction.getAction(), buyTransaction.getQty(),
                buyTransaction.getPrice(), buyTransaction.getBrokerage(), buyTransaction.isDayTrading(), "Something");
        DAOManager.getTransactionDAO().updateOrInsertICICITransaction(iciciTransaction);
        ICICITransaction savedTransaction = DAOManager.getTransactionDAO().iciciTransactions().get(0);

        assertEquals(ICICITransactionStatus.Pending, savedTransaction.getStatus());
        new IciciTradeBO().syncWithPMTrade();
        ICICITransaction updatedTransaction = DAOManager.getTransactionDAO().iciciTransactions().get(0);
        assertEquals(buyTransaction.getPortfolio(), updatedTransaction.getPortfolio());
        assertEquals(ICICITransactionStatus.AutoMapped, updatedTransaction.getStatus());
    }
}
