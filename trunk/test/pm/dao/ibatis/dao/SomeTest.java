package pm.dao.ibatis.dao;

import pm.util.AppConst;
import pm.util.PMDate;
import pm.vo.TradeVO;
import pm.vo.TransactionVO;

import java.util.List;

/**
 * Date: Nov 15, 2006
 * Time: 8:48:06 PM
 */
public class SomeTest extends PMDBCompositeDataSetTestCase {

    public SomeTest(String name) {
        super(name, "EmptyData.xml", "SomeTestData.xml");
    }

    public void testGetHoldingDetailsForStockWithoutAnySellTransaction() {
        String stockCode = "CODE3";
        String portfolioName = "PortfolioName";
        String tradingAcName = "TradingACCName";
        TransactionVO buyTransaction = new TransactionVO(new PMDate(9, 1, 2006), stockCode, AppConst.TRADINGTYPE.Buy, 20f, 180f, 915f, portfolioName, tradingAcName, false);
        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        int buyID = transactionDAO.insertTransaction(buyTransaction);
        List<TradeVO> holdingVOList = transactionDAO.getHoldingDetails(tradingAcName, portfolioName, stockCode, false);
        assertEquals(1, holdingVOList.size());
        assertEquals(buyID, holdingVOList.get(0).getBuyId());
    }
}
