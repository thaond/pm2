package pm.dao.ibatis.dao;

import pm.bo.TradingBO;
import pm.util.AppConst;
import static pm.util.AppConst.COMPANY_ACTION_TYPE.Divident;
import static pm.util.AppConst.TRADINGTYPE.Buy;
import static pm.util.AppConst.TRADINGTYPE.Sell;
import pm.util.PMDate;
import pm.vo.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TransactionDAO Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>08/12/2006</pre>
 */
public class TransactionDAOTest extends PMDBCompositeDataSetTestCase {
    public TransactionDAOTest(String name) {
        super(name, "EmptyData.xml", "TransactionData.xml");
    }

    public void testGetTransactionById() throws Exception {
        TransactionVO transaction = new TransactionVO(new PMDate(1, 1, 2006), "CODE1", Buy, 10f, 100f, 115f,
                "PortfolioName", "TradingACCName", false);
        int id = 1;
        TransactionVO transVO = DAOManager.getTransactionDAO().getTransaction(id, Buy);
        assertEquals(transaction, transVO);

        transaction = new TransactionVO(new PMDate(5, 2, 2006), "CODE1", Sell, 5f, 220f, 125f,
                "PortfolioName", "TradingACCName", false);
        transVO = DAOManager.getTransactionDAO().getTransaction(id, Sell);
        assertEquals(transaction, transVO);

    }

    public void testGetTransaction() throws Exception {
/*
        <BUYTRANSACTION ID="1" STOCKID="1" TDATE="20060101" QTY="10.0" PRICE="100.0" DELIVERYTYPE="0" PORTFOLIOID="1" TRADINGACCID="1" BROKERAGE="115.0"/>
        <SELLTRANSACTION ID="1" TDATE="20060205" QTY="5.0" PRICE="220.0" BROKERAGE="125.0"/>
        <TRADE BUYID="1" SELLID="1" QTY="5.0"/>
*/

        String stockCode = "CODE1";
        String portfolioName = "PortfolioName";
        String tradingAcName = "TradingACCName";
        TransactionVO buyTransaction = new TransactionVO(new PMDate(1, 1, 2006), stockCode, Buy, 10f, 100f, 115f, portfolioName, tradingAcName, false);
        TransactionVO sellTransaction = new TransactionVO(new PMDate(5, 2, 2006), stockCode, Sell, 5f, 220f, 125f, portfolioName, tradingAcName, false);

        List<TransactionVO> transactionVOs = DAOManager.getTransactionDAO().getTransactionList(null, null);
        assertTrue(transactionVOs.contains(buyTransaction));
        assertTrue(transactionVOs.contains(sellTransaction));
    }

    public void testGetTransactionToFilterByAccount() throws Exception {
/*
        <BUYTRANSACTION ID="10" STOCKID="1" TDATE="20060109" QTY="20.0" PRICE="180.0" DELIVERYTYPE="0" PORTFOLIOID="2" TRADINGACCID="1" BROKERAGE="915.0"/>
        <BUYTRANSACTION ID="11" STOCKID="1" TDATE="20060109" QTY="20.0" PRICE="180.0" DELIVERYTYPE="0" PORTFOLIOID="1" TRADINGACCID="2" BROKERAGE="915.0"/>
        <SELLTRANSACTION ID="10" TDATE="20061008" QTY="10.0" PRICE="1020.0" BROKERAGE="925.0"/>
        <SELLTRANSACTION ID="11" TDATE="20061008" QTY="10.0" PRICE="1020.0" BROKERAGE="925.0"/>
        <TRADE BUYID="10" SELLID="10" QTY="10.0"/>
        <TRADE BUYID="11" SELLID="11" QTY="10.0"/>
        <BUYTRANSACTION ID="15" STOCKID="1" TDATE="20060110" QTY="20.0" PRICE="280.0" DELIVERYTYPE="1" PORTFOLIOID="2" TRADINGACCID="1" BROKERAGE="915.0"/>
        <SELLTRANSACTION ID="13" TDATE="20060110" QTY="10.0" PRICE="1020.0" BROKERAGE="925.0"/>
        <TRADE BUYID="15" SELLID="13" QTY="10.0"/>
        <BUYTRANSACTION ID="10" STOCKID="1" TDATE="20060109" QTY="20.0" PRICE="180.0" DELIVERYTYPE="0" PORTFOLIOID="2" TRADINGACCID="1" BROKERAGE="915.0"/>
        <SELLTRANSACTION ID="10" TDATE="20061008" QTY="10.0" PRICE="1020.0" BROKERAGE="925.0"/>
        <TRADE BUYID="10" SELLID="10" QTY="10.0"/>
*/
        String stockCode = "CODE1";
        String portfolioName = "PortfolioName";
        String portfolioName2 = "PortfolioName2";
        String tradingAcName = "TradingACCName";
        String tradingAcName2 = "TradingACCName2";
        TransactionVO buyTransaction = new TransactionVO(new PMDate(9, 1, 2006), stockCode, Buy, 20f, 180f, 915f, portfolioName2, tradingAcName, false);
        TransactionVO sellTransaction = new TransactionVO(new PMDate(8, 10, 2006), stockCode, Sell, 10f, 1020f, 925f, portfolioName2, tradingAcName, false);
        List<TransactionVO> transactionVOs = DAOManager.getTransactionDAO().getTransactionList(tradingAcName, portfolioName2);
        assertEquals(4, transactionVOs.size());
        assertTrue(transactionVOs.contains(buyTransaction));
        assertTrue(transactionVOs.contains(sellTransaction));

        buyTransaction = new TransactionVO(new PMDate(9, 1, 2006), stockCode, Buy, 20f, 180f, 915f, portfolioName, tradingAcName2, false);
        sellTransaction = new TransactionVO(new PMDate(8, 10, 2006), stockCode, Sell, 10f, 1020f, 925f, portfolioName, tradingAcName2, false);
        transactionVOs = DAOManager.getTransactionDAO().getTransactionList(tradingAcName2, portfolioName);
        assertEquals(2, transactionVOs.size());
        assertTrue(transactionVOs.contains(buyTransaction));
        assertTrue(transactionVOs.contains(sellTransaction));
    }

    public void testInsertGetTransaction() throws Exception {
        setUp();
        PMDate buyDate = new PMDate(4, 1, 2000);
        String stockCode = "CODE2";
        String portfolioName = "PortfolioName2";
        String tradingAcName = "TradingACCName2";
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        float qty = 100f;
        int buyId = dao.insertTransaction(new TransactionVO(buyDate, stockCode, Buy, qty, 1000f, 150f, portfolioName, tradingAcName, false));
        PMDate sellDate = new PMDate(5, 1, 2000);
        TransactionVO saleTrans = new TransactionVO(sellDate, stockCode, Sell, qty, 1200f, 250f, portfolioName, tradingAcName, false);
        HashMap<Integer, Float> hashMap = new HashMap<Integer, Float>();
        hashMap.put(buyId, qty);
        dao.insertSaleTransaction(saleTrans, hashMap);

        List<TransactionVO> transactionVOs = DAOManager.getTransactionDAO().getTransactionList(tradingAcName, portfolioName);
        TransactionVO transactionVO = getTransactionForStockCode(transactionVOs, buyDate, Buy, false);
        assertNotNull(transactionVO);
        assertEquals(100f, transactionVO.getQty());
        assertEquals(1000f, transactionVO.getPrice());
        assertEquals(150f, transactionVO.getBrokerage());
        assertEquals(stockCode, transactionVO.getStockCode());
        assertEquals(portfolioName, transactionVO.getPortfolio());
        assertEquals(tradingAcName, transactionVO.getTradingAc());

        transactionVO = getTransactionForStockCode(transactionVOs, sellDate, Sell, false);
        assertNotNull(transactionVO);
        assertEquals(100f, transactionVO.getQty());
        assertEquals(1200f, transactionVO.getPrice());
        assertEquals(250f, transactionVO.getBrokerage());
    }

    public void testInsertingDayTrading() throws Exception {
        PMDate buyDate = new PMDate(4, 1, 2000);
        String stockCode = "CODE2";
        String portfolioName = "PortfolioName2";
        String tradingAcName = "TradingACCName2";
        float qty = 1010f;
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        int buyId = dao.insertTransaction(new TransactionVO(buyDate, stockCode, Buy, qty, 1000f, 150f, portfolioName, tradingAcName, true));
        PMDate sellDate = new PMDate(5, 1, 2000);

        TransactionVO saleTrans = new TransactionVO(sellDate, stockCode, Sell, qty, 1200f, 250f, portfolioName, tradingAcName, true);
        HashMap<Integer, Float> hashMap = new HashMap<Integer, Float>();
        hashMap.put(buyId, qty);
        dao.insertSaleTransaction(saleTrans, hashMap);

        List<TransactionVO> transactionVOs = DAOManager.getTransactionDAO().getTransactionList(tradingAcName, portfolioName);
        TransactionVO transactionVO = getTransactionForStockCode(transactionVOs, buyDate, Buy, true);
        assertNotNull(transactionVO);
        assertEquals(1010f, transactionVO.getQty());
        assertEquals(1000f, transactionVO.getPrice());
        assertEquals(150f, transactionVO.getBrokerage());
        assertEquals(stockCode, transactionVO.getStockCode());
        assertEquals(portfolioName, transactionVO.getPortfolio());
        assertEquals(tradingAcName, transactionVO.getTradingAc());

        transactionVO = getTransactionForStockCode(transactionVOs, sellDate, Sell, true);
        assertNotNull(transactionVO);
        assertEquals(1010f, transactionVO.getQty());
        assertEquals(1200f, transactionVO.getPrice());
        assertEquals(250f, transactionVO.getBrokerage());

    }


    private TransactionVO getTransactionForStockCode(List<TransactionVO> transactionVOs, PMDate date, AppConst.TRADINGTYPE tradingType, boolean dayTrading) {
        for (TransactionVO transactionVO : transactionVOs) {
            if (transactionVO.getAction() == tradingType && transactionVO.getDate().equals(date)) {
                return transactionVO;
            }
        }
        return null;
    }

    public void testGetCompletedTradeDetailsToIncDayTrading() throws Exception {
        String tradingAc = "TradingACCName";
        String portfolio = "PortfolioName";
        List<TradeVO> tradeDetails = DAOManager.getTransactionDAO().getCompletedTradeDetails(tradingAc, portfolio, null, true);
        assertEquals(12, tradeDetails.size());
        TradeVO tradeVO = new TradeVO("CODE1", new PMDate(1, 1, 2006), 5f, 100f, 57.5f + 125f, tradingAc, portfolio);
        tradeVO.setSaleDate(new PMDate(5, 2, 2006));
        tradeVO.setSalePrice(220f);
        tradeVO.setDivident(50f);
        tradeVO.setBuyId(1);
        tradeVO.setSellId(1);
        assertTrue(tradeDetails.contains(tradeVO));
/*
        <BUYTRANSACTION ID="15" STOCKID="1" TDATE="20060110" QTY="20.0" PRICE="280.0" DELIVERYTYPE="1" PORTFOLIOID="2" TRADINGACCID="1" BROKERAGE="915.0"/>
        <SELLTRANSACTION ID="13" TDATE="20060110" QTY="10.0" PRICE="1020.0" BROKERAGE="925.0"/>
        <TRADE BUYID="15" SELLID="13" QTY="10.0"/>
*/
        tradingAc = "TradingACCName";
        portfolio = "PortfolioName2";
        tradeDetails = DAOManager.getTransactionDAO().getCompletedTradeDetails(tradingAc, portfolio, null, true);
        assertEquals(2, tradeDetails.size());
        TradeVO daytradeVO = new TradeVO("CODE1", new PMDate(10, 1, 2006), 10f, 280f, 457.5f + 925f, tradingAc, portfolio);
        daytradeVO.setSaleDate(new PMDate(10, 1, 2006));
        daytradeVO.setSalePrice(1020f);
        daytradeVO.setBuyId(15);
        daytradeVO.setSellId(13);
        daytradeVO.setDayTrading(true);
        assertTrue(tradeDetails.contains(daytradeVO));

        TradeVO deliveryTradeVO = new TradeVO("CODE1", new PMDate(9, 1, 2006), 10f, 180f, 457.5f + 925f, tradingAc, portfolio);
        deliveryTradeVO.setSaleDate(new PMDate(8, 10, 2006));
        deliveryTradeVO.setSalePrice(1020f);
        deliveryTradeVO.setBuyId(10);
        deliveryTradeVO.setSellId(10);
        deliveryTradeVO.setDivident(50f);
        deliveryTradeVO.setDayTrading(false);
        assertTrue(tradeDetails.contains(deliveryTradeVO));

    }

    public void testGetCompletedTradeDetailsForIndividualStock() throws Exception {
        String stockCode = "CODE2";
        List<TradeVO> tradeDetails = DAOManager.getTransactionDAO().getCompletedTradeDetails(null, null, stockCode, true);
        assertEquals(1, tradeDetails.size());
        for (TradeVO tradeVO : tradeDetails) {
            if (!tradeVO.getStockCode().equals(stockCode)) {
                fail("should not fetch other stock details");
            }
        }

    }

    public void testGetCompletedTradeDetailsToHaveDividentInformation() throws Exception {
        String stockCode = "CODE1";
        List<TradeVO> tradeDetails = DAOManager.getTransactionDAO().getCompletedTradeDetails(null, null, stockCode, false);
        String tradingAc = "TradingACCName";
        String portfolio = "PortfolioName";
        TradeVO tradeVO = new TradeVO("CODE1", new PMDate(1, 1, 2006), 5f, 100f, 57.5f + 125f, tradingAc, portfolio);
        tradeVO.setSaleDate(new PMDate(5, 2, 2006));
        tradeVO.setSalePrice(220f);
        tradeVO.setDivident(50f);
        tradeVO.setBuyId(1);
        tradeVO.setSellId(1);
        assertTrue(tradeDetails.contains(tradeVO));
        tradeVO = new TradeVO("CODE1", new PMDate(1, 1, 2006), 5f, 100f, 57.5f + 225f, tradingAc, portfolio);
        tradeVO.setSaleDate(new PMDate(6, 3, 2006));
        tradeVO.setSalePrice(320f);
        tradeVO.setDivident(50f);
        tradeVO.setBuyId(1);
        tradeVO.setSellId(2);
        assertTrue(tradeDetails.contains(tradeVO));
    }


    public void testGetCompletedTradeDetailsForOnlyDeliveryTrading() throws Exception {
/*
        <BUYTRANSACTION ID="10" STOCKID="1" TDATE="20060109" QTY="20.0" PRICE="180.0" DELIVERYTYPE="0" PORTFOLIOID="2" TRADINGACCID="1" BROKERAGE="915.0"/>
        <SELLTRANSACTION ID="10" TDATE="20061008" QTY="10.0" PRICE="1020.0" BROKERAGE="925.0"/>
        <TRADE BUYID="10" SELLID="10" QTY="10.0"/>
*/
        String tradingAc = "TradingACCName";
        String portfolio = "PortfolioName2";
        List<TradeVO> tradeDetails = DAOManager.getTransactionDAO().getCompletedTradeDetails(tradingAc, portfolio, null, false);
        assertEquals(1, tradeDetails.size());
        TradeVO tradeVO = new TradeVO("CODE1", new PMDate(9, 1, 2006), 10f, 180f, 457.5f + 925f, tradingAc, portfolio);
        tradeVO.setSaleDate(new PMDate(8, 10, 2006));
        tradeVO.setSalePrice(1020f);
        tradeVO.setDivident(50f);
        tradeVO.setBuyId(10);
        tradeVO.setSellId(10);
        assertTrue(tradeDetails.contains(tradeVO));

    }

    public void testGetHoldingDetailsForDeliveryBaseTrading() throws Exception {
        String tradingAc = "TradingACCName";
        String portfolio = "PortfolioName";
        List<TradeVO> tradeDetails = DAOManager.getTransactionDAO().getHoldingDetails(tradingAc, portfolio, false);
        assertEquals(6, tradeDetails.size());
        TradeVO tradeVO1 = new TradeVO("CODE1", new PMDate(8, 1, 2006), 10f, 170f, 407.5f, tradingAc, portfolio);
        tradeVO1.setDayTrading(false);
        tradeVO1.setDivident(50f);
        tradeVO1.setBuyId(8);
        TradeVO tradeVO2 = new TradeVO("CODE1", new PMDate(9, 1, 2006), 20f, 180f, 915f, tradingAc, portfolio);
        tradeVO2.setDivident(100f);
        tradeVO2.setBuyId(9);
        TradeVO tradeVO4 = new TradeVO("CODE2", new PMDate(9, 1, 2006), 20f, 180f, 915f, tradingAc, portfolio);
        tradeVO4.setBuyId(13);
        TradeVO tradeVO3 = new TradeVO("CODE2", new PMDate(9, 1, 2006), 10f, 180f, 457.5f, tradingAc, portfolio);
        tradeVO3.setBuyId(12);
        assertTrue(tradeDetails.contains(tradeVO1));
        assertTrue(tradeDetails.contains(tradeVO2));
        assertTrue(tradeDetails.contains(tradeVO3));
        assertTrue(tradeDetails.contains(tradeVO4));
    }

    public void testGetHoldingDetailsForStock() throws Exception {
        String tradingAc = "TradingACCName";
        String portfolio = "PortfolioName";
        List<TradeVO> tradeDetails = DAOManager.getTransactionDAO().getHoldingDetails(tradingAc, portfolio, "CODE2", false);
        assertEquals(2, tradeDetails.size());
        TradeVO tradeVO3 = new TradeVO("CODE2", new PMDate(9, 1, 2006), 20f, 180f, 915f, tradingAc, portfolio);
        tradeVO3.setBuyId(13);
        assertTrue(tradeDetails.contains(tradeVO3));
    }

    public void testGetHoldingDetailsForDayTrading() throws Exception {
        String tradingAc = "TradingACCName";
        String portfolio = "PortfolioName";
        List<TradeVO> tradeDetails = DAOManager.getTransactionDAO().getHoldingDetails(tradingAc, portfolio, true);
        assertEquals(1, tradeDetails.size());
        TradeVO tradeVO3 = new TradeVO("CODE1", new PMDate(9, 1, 2006), 20f, 180f, 915f, tradingAc, portfolio);
        tradeVO3.setDayTrading(true);
        tradeVO3.setBuyId(14);
        assertTrue(tradeDetails.contains(tradeVO3));
    }

    public void testGetTradeDetailsToGetOnlyDeliveryTrade() throws Exception {
/*
        <BUYTRANSACTION ID="10" STOCKID="1" TDATE="20060109" QTY="20.0" PRICE="180.0" DELIVERYTYPE="0" PORTFOLIOID="2" TRADINGACCID="1" BROKERAGE="915.0"/>
        <SELLTRANSACTION ID="10" TDATE="20061008" QTY="10.0" PRICE="1020.0" BROKERAGE="925.0"/>
        <TRADE BUYID="10" SELLID="10" QTY="10.0"/>
*/
        String tradingAc = "TradingACCName";
        String portfolio = "PortfolioName2";
        List<TradeVO> tradeDetails = DAOManager.getTransactionDAO().getTradeDetails(tradingAc, portfolio, false);
        assertEquals(2, tradeDetails.size());
        TradeVO completedTradeVO = new TradeVO("CODE1", new PMDate(9, 1, 2006), 10f, 180f, 457.5f + 925f, tradingAc, portfolio);
        completedTradeVO.setSaleDate(new PMDate(8, 10, 2006));
        completedTradeVO.setSalePrice(1020f);
        completedTradeVO.setDivident(50f);
        completedTradeVO.setBuyId(10);
        completedTradeVO.setSellId(10);
        TradeVO inCompletedTradeVO = new TradeVO("CODE1", new PMDate(9, 1, 2006), 10f, 180f, 457.5f, tradingAc, portfolio);
        inCompletedTradeVO.setDivident(50f);
        inCompletedTradeVO.setBuyId(10);
        assertTrue(tradeDetails.contains(completedTradeVO));
        assertTrue(tradeDetails.contains(inCompletedTradeVO));

    }

    public void testGetTradeDetailsToGetDeliveryAndDayTrade() throws Exception {
/*
        <BUYTRANSACTION ID="10" STOCKID="1" TDATE="20060109" QTY="20.0" PRICE="180.0" DELIVERYTYPE="0" PORTFOLIOID="2" TRADINGACCID="1" BROKERAGE="915.0"/>
        <SELLTRANSACTION ID="10" TDATE="20061008" QTY="10.0" PRICE="1020.0" BROKERAGE="925.0"/>
        <TRADE BUYID="10" SELLID="10" QTY="10.0"/>
        <BUYTRANSACTION ID="15" STOCKID="1" TDATE="20060110" QTY="20.0" PRICE="280.0" DELIVERYTYPE="1" PORTFOLIOID="2" TRADINGACCID="1" BROKERAGE="915.0"/>
        <SELLTRANSACTION ID="13" TDATE="20060110" QTY="10.0" PRICE="1020.0" BROKERAGE="925.0"/>
        <TRADE BUYID="15" SELLID="13" QTY="10.0"/>
*/
        String tradingAc = "TradingACCName";
        String portfolio = "PortfolioName2";
        List<TradeVO> tradeDetails = DAOManager.getTransactionDAO().getTradeDetails(tradingAc, portfolio, true);
        assertEquals(4, tradeDetails.size());
        TradeVO completedTradeVO = new TradeVO("CODE1", new PMDate(9, 1, 2006), 10f, 180f, 457.5f + 925f, tradingAc, portfolio);
        completedTradeVO.setSaleDate(new PMDate(8, 10, 2006));
        completedTradeVO.setSalePrice(1020f);
        completedTradeVO.setDivident(50f);
        completedTradeVO.setBuyId(10);
        completedTradeVO.setSellId(10);
        TradeVO inCompletedTradeVO = new TradeVO("CODE1", new PMDate(9, 1, 2006), 10f, 180f, 457.5f, tradingAc, portfolio);
        inCompletedTradeVO.setBuyId(10);
        inCompletedTradeVO.setDivident(50f);
        assertTrue(tradeDetails.contains(completedTradeVO));
        assertTrue(tradeDetails.contains(inCompletedTradeVO));

        TradeVO completeDaytradeVO = new TradeVO("CODE1", new PMDate(10, 1, 2006), 10f, 280f, 457.5f + 925f, tradingAc, portfolio);
        completeDaytradeVO.setSaleDate(new PMDate(10, 1, 2006));
        completeDaytradeVO.setSalePrice(1020f);
        completeDaytradeVO.setDayTrading(true);
        completeDaytradeVO.setBuyId(15);
        completeDaytradeVO.setSellId(13);
        assertTrue(tradeDetails.contains(completeDaytradeVO));
        TradeVO inCompleteDaytradeVO = new TradeVO("CODE1", new PMDate(10, 1, 2006), 10f, 280f, 457.5f, tradingAc, portfolio);
        inCompleteDaytradeVO.setDayTrading(true);
        inCompleteDaytradeVO.setBuyId(15);
        assertTrue(tradeDetails.contains(inCompleteDaytradeVO));

    }

    public void testGetTradeDetailsToGetDeliveryAndDayTradeForAllAccountIfAccountNull() throws Exception {

        String tradingAc = null;
        String portfolio = null;
        List<TradeVO> tradeDetails = DAOManager.getTransactionDAO().getTradeDetails(tradingAc, portfolio, true);
        boolean tradingAcc1 = false;
        boolean tradingAcc2 = false;
        boolean portfolio1 = false;
        boolean portfolio2 = false;
        boolean dayTrading = false;
        boolean deliveryTrading = false;
        for (TradeVO tradeVO : tradeDetails) {
            if (tradeVO.getTradingAc().equals("TradingACCName")) {
                tradingAcc1 = true;
            }
            if (tradeVO.getTradingAc().equals("TradingACCName2")) {
                tradingAcc2 = true;
            }
            if (tradeVO.getPortfolio().equals("PortfolioName")) {
                portfolio1 = true;
            }
            if (tradeVO.getPortfolio().equals("PortfolioName2")) {
                portfolio2 = true;
            }
            if (tradeVO.isDayTrading()) {
                dayTrading = true;
            } else {
                deliveryTrading = true;
            }
        }
        assertTrue(tradingAcc1);
        assertTrue(tradingAcc2);
        assertTrue(portfolio1);
        assertTrue(portfolio2);
        assertTrue(dayTrading);
        assertTrue(deliveryTrading);

    }

    public void testGetTradeDetailsToGetOnlyDeliveryForAllAccountIfAccountNull() throws Exception {

        String tradingAc = null;
        String portfolio = null;
        List<TradeVO> tradeDetails = DAOManager.getTransactionDAO().getTradeDetails(tradingAc, portfolio, false);
        boolean tradingAcc1 = false;
        boolean tradingAcc2 = false;
        boolean portfolio1 = false;
        boolean portfolio2 = false;
        boolean dayTrading = false;
        boolean deliveryTrading = false;
        for (TradeVO tradeVO : tradeDetails) {
            if (tradeVO.getTradingAc().equals("TradingACCName")) {
                tradingAcc1 = true;
            }
            if (tradeVO.getTradingAc().equals("TradingACCName2")) {
                tradingAcc2 = true;
            }
            if (tradeVO.getPortfolio().equals("PortfolioName")) {
                portfolio1 = true;
            }
            if (tradeVO.getPortfolio().equals("PortfolioName2")) {
                portfolio2 = true;
            }
            if (tradeVO.isDayTrading()) {
                dayTrading = true;
            } else {
                deliveryTrading = true;
            }
        }
        assertTrue(tradingAcc1);
        assertTrue(tradingAcc2);
        assertTrue(portfolio1);
        assertTrue(portfolio2);
        assertFalse(dayTrading);
        assertTrue(deliveryTrading);
    }

    public void testGetTradeDetailsToGetOnlyForSpecifiedStock() throws Exception {

        String tradingAc = null;
        String portfolio = null;
        String stockCode = "CODE1";
        List<TradeVO> tradeDetails = DAOManager.getTransactionDAO().getTradeDetails(tradingAc, portfolio, stockCode, false);
        assertTrue(tradeDetails.size() > 0);
        for (TradeVO tradeVO : tradeDetails) {
            if (!tradeVO.getStockCode().equals(stockCode)) {
                fail("Should not fetch other stock details");
            }
        }

    }

    public void testGetTransactionMapping() {
        TransactionMapping mapping = new TransactionMapping(1, 1, 5);
        Map<Integer, TransactionMapping> transactionMappings = DAOManager.getTransactionDAO().getTransactionMapping();
        assertTrue(transactionMappings.containsValue(mapping));
    }

    public void testUpdateTransaction() {
        int id = 13;
        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        List<TransactionVO> existingTransList = transactionDAO.getTransactionList(null, null);

        TransactionVO buyTransaction = new TransactionVO(new PMDate(1, 3, 2006), "CODE2", Buy, 1f, 10f, 15f,
                "PortfolioName2", "TradingACCName2", true);
        buyTransaction.setId(id);
        transactionDAO.updateTransaction(buyTransaction);

        TransactionVO transVO = transactionDAO.getTransaction(id, Buy);
        assertEquals(buyTransaction, transVO);

        TransactionVO sellTransaction = transactionDAO.getTransaction(id, Sell);
        sellTransaction.setPrice(12f);
        sellTransaction.setQty(1f);
        sellTransaction.setDate(new PMDate(1, 1, 2005));
        sellTransaction.setBrokerage(1234f);
        transactionDAO.updateTransaction(sellTransaction);

    }

    public void testUpdateTransactionMapping() throws Exception {
        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        Map<Integer, TransactionMapping> currentMappings = transactionDAO.getTransactionMapping();
        int id = 2;
        TransactionMapping mappingToModify = currentMappings.get(id);
        mappingToModify.setBuyId(1);
        mappingToModify.setSellId(1);
        transactionDAO.updateTrade(mappingToModify);
        Map<Integer, TransactionMapping> newMappings = transactionDAO.getTransactionMapping();
        assertEquals(currentMappings.size(), newMappings.size());
        for (Integer tradeID : currentMappings.keySet()) {
            assertEquals(currentMappings.get(tradeID), newMappings.get(tradeID));
        }
    }

    public void testGetTransactionListForPortfolioNameTradingAcNameTransactionType() {
        String stockCode = "CODE1";
        String portfolioName = "PortfolioName";
        String portfolioName2 = "PortfolioName2";
        String tradingAcName = "TradingACCName";
        String tradingAcName2 = "TradingACCName2";
        TransactionVO buyTransaction = new TransactionVO(new PMDate(9, 1, 2006), stockCode, Buy, 20f, 180f, 915f, portfolioName2, tradingAcName, false);
        TransactionVO sellTransaction = new TransactionVO(new PMDate(8, 10, 2006), stockCode, Sell, 10f, 1020f, 925f, portfolioName2, tradingAcName, false);
        List<TransactionVO> transactionVOs = DAOManager.getTransactionDAO().getTransactionList(tradingAcName, portfolioName2, Buy);
        assertEquals(2, transactionVOs.size());
        assertTrue(transactionVOs.contains(buyTransaction));
        assertFalse(transactionVOs.contains(sellTransaction));
        transactionVOs = DAOManager.getTransactionDAO().getTransactionList(tradingAcName, portfolioName2, Sell);
        assertEquals(2, transactionVOs.size());
        assertFalse(transactionVOs.contains(buyTransaction));
        assertTrue(transactionVOs.contains(sellTransaction));

        transactionVOs = DAOManager.getTransactionDAO().getTransactionList(null, null, Buy);
        boolean flagPort1 = false, flagPort2 = false, flagTrad1 = false, flagTrad2 = false;

        for (TransactionVO transactionVO : transactionVOs) {
            if (transactionVO.getTradingAc().equals(tradingAcName)) {
                flagTrad1 = true;
            }
            if (transactionVO.getTradingAc().equals(tradingAcName2)) {
                flagTrad2 = true;
            }
            if (transactionVO.getPortfolio().equals(portfolioName)) {
                flagPort1 = true;
            }
            if (transactionVO.getPortfolio().equals(portfolioName2)) {
                flagPort2 = true;
            }
            assertEquals(Buy, transactionVO.getAction());
        }

        assertTrue(flagPort1);
        assertTrue(flagPort2);
        assertTrue(flagTrad1);
        assertTrue(flagTrad2);

        transactionVOs = DAOManager.getTransactionDAO().getTransactionList(null, null, Sell);
        flagPort1 = flagPort2 = flagTrad1 = flagTrad2 = false;

        for (TransactionVO transactionVO : transactionVOs) {
            if (transactionVO.getTradingAc().equals(tradingAcName)) {
                flagTrad1 = true;
            }
            if (transactionVO.getTradingAc().equals(tradingAcName2)) {
                flagTrad2 = true;
            }
            if (transactionVO.getPortfolio().equals(portfolioName)) {
                flagPort1 = true;
            }
            if (transactionVO.getPortfolio().equals(portfolioName2)) {
                flagPort2 = true;
            }
            assertEquals(Sell, transactionVO.getAction());
        }

        assertTrue(flagPort1);
        assertTrue(flagPort2);
        assertTrue(flagTrad1);
        assertTrue(flagTrad2);

    }

    public void testGetTransactionListForPortfolioNameTradingAcNameStockDeliveryTrade() {
        String stockCode = "CODE1";
        String portfolioName = "PortfolioName";
        String tradingAcName = "TradingACCName";
        List<TransactionVO> transList = DAOManager.getTransactionDAO().getTransactionList(tradingAcName, portfolioName, stockCode, false);
        assertEquals(20, transList.size());
        int[] ids = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        verifyTransactionOrderAndType(transList, ids, 0, 9, Buy);
        int[] sellids = {1, 2, 3, 4, 4, 5, 6, 7, 7, 8, 9};
        verifyTransactionOrderAndType(transList, sellids, 9, 20, Sell);
    }

    public void testGetTransactionListForPortfolioNameTradingAcNameStockDayTrade() {
        String stockCode = "CODE1";
        String portfolioName = "PortfolioName2";
        String tradingAcName = "TradingACCName";
        List<TransactionVO> transList = DAOManager.getTransactionDAO().getTransactionList(tradingAcName, portfolioName, stockCode, true);
        assertEquals(2, transList.size());
        int[] ids = {15};
        verifyTransactionOrderAndType(transList, ids, 0, 1, Buy);
        int[] sellids = {13};
        verifyTransactionOrderAndType(transList, sellids, 1, 2, Sell);
    }

    public void testGetTransactionListForSplitedSellTransaction() {

        String portfolioName = "PortfolioName";
        String tradingAcName = "TradingACCName";
        List<TransactionVO> transList = DAOManager.getTransactionDAO().getTransactionList(tradingAcName, portfolioName, Sell);
        Map<Integer, Float> mapBrok = new HashMap<Integer, Float>();
        Map<Integer, Float> mapQty = new HashMap<Integer, Float>();
        for (TransactionVO transactionVO : transList) {
            Float brok = mapBrok.get(transactionVO.getId());
            if (brok == null) {
                mapBrok.put(transactionVO.getId(), transactionVO.getBrokerage());
            } else {
                mapBrok.put(transactionVO.getId(), brok + transactionVO.getBrokerage());
            }
            Float qty = mapQty.get(transactionVO.getId());
            if (qty == null) {
                mapQty.put(transactionVO.getId(), transactionVO.getQty());
            } else {
                mapQty.put(transactionVO.getId(), qty + transactionVO.getQty());
            }
        }

        float[] qty = {5, 5, 5, 15, 10, 20, 30, 10, 10, 10, 10, 10, 10};
        float[] brok = {125, 225, 325, 425, 525, 625, 725, 825, 925, 925, 925, 925};
        for (int i = 1; i < 10; i++) {
            assertEquals("" + i, brok[i - 1], mapBrok.get(i));
            assertEquals("" + i, qty[i - 1], mapQty.get(i));

        }

    }

    private void verifyTransactionOrderAndType(List<TransactionVO> transList, int[] ids, int st, int en, AppConst.TRADINGTYPE transType) {
        int i = 0;
        for (int j = st; j < en; j++) {
            assertEquals(transType, transList.get(j).getAction());
            assertEquals(ids[i++], transList.get(j).getId());
        }
    }

    public void testGetHoldingDetailsForStockWithoutAnySellTransaction() {
        String stockCode = "CODE3";
        String portfolioName = "PortfolioName";
        String tradingAcName = "TradingACCName";
        TransactionVO buyTransaction = new TransactionVO(new PMDate(9, 1, 2006), stockCode, Buy, 20f, 180f, 915f, portfolioName, tradingAcName, false);
        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        int buyID = transactionDAO.insertTransaction(buyTransaction);
        List<TradeVO> holdingVOList = transactionDAO.getHoldingDetails(tradingAcName, portfolioName, stockCode, false);
        assertEquals(1, holdingVOList.size());
        assertEquals(buyID, holdingVOList.get(0).getBuyId());
    }

    public void testGetTrasactionList_Account_StockCode_ActionType() {
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        List<TransactionVO> list = iTransactionDAO.getTransactionList(null, null, "CODE1", Buy);
        assertEquals(13, list.size());
        list = iTransactionDAO.getTransactionList("TradingACCName", "PortfolioName", "CODE1", Buy);
        assertEquals(10, list.size());
        list = iTransactionDAO.getTransactionList("TradingACCName", "PortfolioName", "CODE2", Buy);
        assertEquals(2, list.size());

    }

    public void testUpdateStockId() {
        final ITransactionDAO dao = DAOManager.getTransactionDAO();
        assertEquals("CODE16NEW", dao.getTransaction(502, Buy).getStockCode());
        dao.updateStockId(17, 16);
        assertEquals("CODE16", dao.getTransaction(501, Buy).getStockCode());
        assertEquals("CODE16", dao.getTransaction(502, Buy).getStockCode());
    }

    public void testInsertGetICICITransaction() {
        String iciciCode = "ICICI1";
        ICICITransaction transaction = new ICICITransaction(new PMDate(1, 1, 2006), iciciCode, Buy, 100f, 224.24f, 123.23f, true, "123123123");
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        dao.updateOrInsertICICITransaction(transaction);
        List<ICICITransaction> transactions = dao.iciciTransactions();
        assertEquals(1, transactions.size());
        assertTrue(transactions.contains(transaction));
    }

    public void testInsertICICITransactionShouldInsertICICICodeToMapping() {
        String iciciCode = "ICICI1";
        ICICITransaction transaction = new ICICITransaction(new PMDate(1, 1, 2006), iciciCode, Buy, 100f, 224.24f, 123.23f, true, "123123123");
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        dao.updateOrInsertICICITransaction(transaction);
        assertTrue(DAOManager.getStockDAO().iciciCodeMapping().containsKey("ICICI1"));
    }

    public void testUpdateOrInsertICICITransactionShouldUpdateICICITransaction() {
        String iciciCode = "ICICI1";
        ICICITransaction transaction = new ICICITransaction(new PMDate(1, 1, 2006), iciciCode, Buy, 100f, 224.24f, 0f, true, "123123123");
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        dao.updateOrInsertICICITransaction(transaction);
        assertEquals(1, dao.iciciTransactions().size());
        assertTrue(dao.iciciTransactions().contains(transaction));
        transaction = new ICICITransaction(new PMDate(1, 1, 2006), iciciCode, Buy, 100f, 224.24f, 123.23f, true, "123123123");
        transaction.setPortfolio("PortfolioName");
        dao.updateOrInsertICICITransaction(transaction);
        assertEquals(1, dao.iciciTransactions().size());
        assertTrue(dao.iciciTransactions().contains(transaction));
    }

    public void testInsertICICITransactionShouldInsertICICICodeToMappingOnlyIfMissing() {
        String iciciCode = "ICICI1";
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        dao.updateOrInsertICICITransaction(new ICICITransaction(new PMDate(1, 1, 2006), iciciCode, Buy, 100f, 224.24f, 123.23f, true, "123123123"));
        int mappingSize = DAOManager.getStockDAO().iciciCodeMapping().size();
        dao.updateOrInsertICICITransaction(new ICICITransaction(new PMDate(1, 2, 2006), iciciCode, Buy, 100f, 224.24f, 123.23f, true, "123123123"));
        assertEquals(mappingSize, DAOManager.getStockDAO().iciciCodeMapping().size());
    }

    public void testGetICICITransactionLoadsMappedStockCode() {
        String iciciCode = "ICICI2";
        ICICITransaction transaction = new ICICITransaction(new PMDate(1, 1, 2006), iciciCode, Buy, 100f, 224.24f, 123.23f, true, "123123123");
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        dao.updateOrInsertICICITransaction(transaction);
        String stockCode = "CODE2";
        StockVO stockVO = DAOManager.getStockDAO().getStock(stockCode);
        DAOManager.getStockDAO().updateICICICode(stockVO, iciciCode);
        List<ICICITransaction> transactions = dao.iciciTransactions();
        assertEquals(1, transactions.size());
        transaction.setStockCode(stockCode);
        assertTrue(transactions.contains(transaction));
    }

    public void testSoldDuringFinYear() {
        String portfolioName = "PortfolioName";
        String tradingAcName = "TradingACCName";
        doTransaction(new PMDate(31, 1, 2008), new PMDate(31, 3, 2008), false, portfolioName, tradingAcName);
        doTransaction(new PMDate(31, 1, 2008), new PMDate(1, 4, 2008), false, portfolioName, tradingAcName);
        doTransaction(new PMDate(1, 4, 2008), new PMDate(1, 4, 2008), true, portfolioName, tradingAcName);
        doTransaction(new PMDate(31, 5, 2008), new PMDate(31, 7, 2008), false, portfolioName, tradingAcName);
        doTransaction(new PMDate(31, 5, 2008), new PMDate(31, 7, 2009), false, portfolioName, tradingAcName);
        doTransaction(new PMDate(31, 3, 2009), new PMDate(31, 3, 2009), true, portfolioName, tradingAcName);
        doTransaction(new PMDate(1, 4, 2009), new PMDate(31, 4, 2009), false, portfolioName, tradingAcName);
        TradingAccountVO tradingAccount = DAOManager.getAccountDAO().tradingAcc(tradingAcName);
        PortfolioDetailsVO portfolio = DAOManager.getAccountDAO().portfolio(portfolioName);
        FinYear finYear = new FinYear(2008);
        List<TradeVO> tradeVOs = DAOManager.getTransactionDAO().soldDuringFinYear(tradingAccount, portfolio, finYear);
        assertEquals(4, tradeVOs.size());
        for (TradeVO tradeVO : tradeVOs) {
            assertFalse(tradeVO.getSaleDate().before(finYear.startDate()));
            assertFalse(tradeVO.getSaleDate().after(finYear.endDate()));
        }
    }

    public void testSoldDuringFinYearToFilterByTradingAcAndPortfolio() {
        String portfolioName1 = "PortfolioName";
        String portfolioName2 = "PortfolioName2";
        String tradingAcName1 = "TradingACCName";
        String tradingAcName2 = "TradingACCName2";

        doTransaction(new PMDate(31, 5, 2008), new PMDate(31, 7, 2008), false, portfolioName1, tradingAcName1);
        doTransaction(new PMDate(15, 5, 2008), new PMDate(18, 7, 2008), false, portfolioName1, tradingAcName2);
        doTransaction(new PMDate(16, 5, 2008), new PMDate(20, 7, 2008), false, portfolioName2, tradingAcName1);
        doTransaction(new PMDate(31, 4, 2008), new PMDate(31, 6, 2008), false, portfolioName2, tradingAcName2);

        TradingAccountVO tradingAccount1 = DAOManager.getAccountDAO().tradingAcc(tradingAcName1);
        TradingAccountVO tradingAccount2 = DAOManager.getAccountDAO().tradingAcc(tradingAcName2);
        PortfolioDetailsVO portfolio1 = DAOManager.getAccountDAO().portfolio(portfolioName1);
        PortfolioDetailsVO portfolio2 = DAOManager.getAccountDAO().portfolio(portfolioName2);
        FinYear finYear = new FinYear(2008);
        verifyTransactions(finYear, 4, TradingAccountVO.ALL, PortfolioDetailsVO.ALL);
        verifyTransactions(finYear, 2, tradingAccount2, PortfolioDetailsVO.ALL);
        verifyTransactions(finYear, 2, TradingAccountVO.ALL, portfolio1);
        verifyTransactions(finYear, 1, tradingAccount1, portfolio2);
        verifyTransactions(finYear, 1, tradingAccount1, portfolio1);
    }

    public void testGetDividentForFY() throws Exception {
        String portfolioName1 = "PortfolioName";
        String tradingAcName1 = "TradingACCName";

        doTransaction(new PMDate(31, 5, 2006), new PMDate(31, 7, 2008), false, portfolioName1, tradingAcName1);
        doTransaction(new PMDate(2, 4, 2008), new PMDate(31, 7, 2008), false, portfolioName1, tradingAcName1);
        float dividentRate2007 = 10f;
        ICompanyActionDAO companyActionDAO = DAOManager.getCompanyActionDAO();
        companyActionDAO.insertCompanyAction(new CompanyActionVO(Divident, new PMDate(1, 4, 2008), "CODE4", dividentRate2007, 10f));
        float dividentRate2008 = 30f;
        companyActionDAO.insertCompanyAction(new CompanyActionVO(Divident, new PMDate(2, 4, 2008), "CODE4", dividentRate2008, 10f));
        TradingAccountVO tradingAccount1 = DAOManager.getAccountDAO().tradingAcc(tradingAcName1);
        PortfolioDetailsVO portfolio1 = DAOManager.getAccountDAO().portfolio(portfolioName1);
        ITransactionDAO dao = DAOManager.getTransactionDAO();
        float divident = 10f * dividentRate2007 / 100f * 10f;
        assertEquals(divident, dao.getDividentForFY(tradingAccount1, portfolio1, new FinYear(2007)));
        divident = 10f * dividentRate2008 / 100f * 10f;
        assertEquals(divident, dao.getDividentForFY(tradingAccount1, portfolio1, new FinYear(2008)));

    }

    public void testGetDividentForFYFilterByPortfolioAndTradingAcc() throws Exception {
        String portfolioName1 = "PortfolioName";
        String portfolioName2 = "PortfolioName2";
        String tradingAcName1 = "TradingACCName";
        String tradingAcName2 = "TradingACCName2";

        doTransaction(new PMDate(31, 5, 2006), new PMDate(31, 7, 2008), false, portfolioName1, tradingAcName1);
        doTransaction(new PMDate(31, 5, 2006), new PMDate(31, 7, 2008), false, portfolioName2, tradingAcName1);
        doTransaction(new PMDate(31, 5, 2006), new PMDate(31, 7, 2008), false, portfolioName1, tradingAcName2);
        doTransaction(new PMDate(31, 5, 2006), new PMDate(31, 7, 2008), false, portfolioName2, tradingAcName2);

        ICompanyActionDAO companyActionDAO = DAOManager.getCompanyActionDAO();
        companyActionDAO.insertCompanyAction(new CompanyActionVO(Divident, new PMDate(31, 3, 2008), "CODE4", 10f, 10f));
        TradingAccountVO tradingAccount1 = DAOManager.getAccountDAO().tradingAcc(tradingAcName1);
        TradingAccountVO tradingAccount2 = DAOManager.getAccountDAO().tradingAcc(tradingAcName2);
        PortfolioDetailsVO portfolio1 = DAOManager.getAccountDAO().portfolio(portfolioName1);
        PortfolioDetailsVO portfolio2 = DAOManager.getAccountDAO().portfolio(portfolioName2);
        ITransactionDAO dao = DAOManager.getTransactionDAO();

        assertEquals(40f, dao.getDividentForFY(TradingAccountVO.ALL, PortfolioDetailsVO.ALL, new FinYear(2007)));
        assertEquals(20f, dao.getDividentForFY(tradingAccount1, PortfolioDetailsVO.ALL, new FinYear(2007)));
        assertEquals(20f, dao.getDividentForFY(TradingAccountVO.ALL, portfolio2, new FinYear(2007)));
        assertEquals(10f, dao.getDividentForFY(tradingAccount2, portfolio1, new FinYear(2007)));
    }

    public void testGetDividentForFYFilterByPortfolioAndTradingAccForHolding() throws Exception {
        String portfolioName1 = "PortfolioName";
        String portfolioName2 = "PortfolioName2";
        String tradingAcName1 = "TradingACCName";
        String tradingAcName2 = "TradingACCName2";

        TradingBO bo = new TradingBO();
        bo.doTrading(new TransactionVO(new PMDate(31, 5, 2006), "CODE4", Buy, 10f, 10f, 10f, portfolioName1, tradingAcName1, false));
        bo.doTrading(new TransactionVO(new PMDate(31, 5, 2007), "CODE4", Buy, 10f, 10f, 10f, portfolioName1, tradingAcName2, false));
        bo.doTrading(new TransactionVO(new PMDate(2, 4, 2008), "CODE4", Buy, 100f, 10f, 10f, portfolioName2, tradingAcName2, false));


        ICompanyActionDAO companyActionDAO = DAOManager.getCompanyActionDAO();
        companyActionDAO.insertCompanyAction(new CompanyActionVO(Divident, new PMDate(1, 4, 2008), "CODE4", 10f, 10f));
        companyActionDAO.insertCompanyAction(new CompanyActionVO(Divident, new PMDate(2, 4, 2008), "CODE4", 10f, 10f));
        TradingAccountVO tradingAccount1 = DAOManager.getAccountDAO().tradingAcc(tradingAcName1);
        TradingAccountVO tradingAccount2 = DAOManager.getAccountDAO().tradingAcc(tradingAcName2);
        PortfolioDetailsVO portfolio1 = DAOManager.getAccountDAO().portfolio(portfolioName1);
        PortfolioDetailsVO portfolio2 = DAOManager.getAccountDAO().portfolio(portfolioName2);
        ITransactionDAO dao = DAOManager.getTransactionDAO();

        assertEquals(20f, dao.getDividentForFY(TradingAccountVO.ALL, PortfolioDetailsVO.ALL, new FinYear(2007)));
        assertEquals(10f, dao.getDividentForFY(tradingAccount1, PortfolioDetailsVO.ALL, new FinYear(2007)));
        assertEquals(0f, dao.getDividentForFY(TradingAccountVO.ALL, portfolio2, new FinYear(2007)));
        assertEquals(0f, dao.getDividentForFY(tradingAccount2, portfolio2, new FinYear(2007)));
        assertEquals(20f, dao.getDividentForFY(TradingAccountVO.ALL, portfolio1, new FinYear(2008)));
    }

    private void verifyTransactions(FinYear finYear, int count, TradingAccountVO tradingAc, PortfolioDetailsVO portfolio) {
        List<TradeVO> tradeVOs = DAOManager.getTransactionDAO().soldDuringFinYear(tradingAc, portfolio, finYear);
        assertEquals(count, tradeVOs.size());
        for (TradeVO tradeVO : tradeVOs) {
            if (!tradingAc.isAll()) {
                assertEquals(tradingAc.getName(), tradeVO.getTradingAc());
            }
            if (!portfolio.isAll()) {
                assertEquals(portfolio.getName(), tradeVO.getPortfolio());
            }
        }
    }

    private void doTransaction(PMDate buyDate, PMDate sellDate, boolean dayTrading, String portfolio, String tradingAc) {
        TradingBO bo = new TradingBO();
        bo.doTrading(new TransactionVO(buyDate, "CODE4", Buy, 10f, 10f, 10f, portfolio, tradingAc, dayTrading));
        bo.doTrading(new TransactionVO(sellDate, "CODE4", Sell, 10f, 10f, 10f, portfolio, tradingAc, dayTrading));
    }
}
