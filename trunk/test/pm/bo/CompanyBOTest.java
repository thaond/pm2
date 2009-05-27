/*
 * Created on 15-Feb-2005
 *
 */
package pm.bo;

import pm.dao.ibatis.dao.*;
import pm.util.AppConst;
import pm.util.Helper;
import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.*;

import java.util.*;

/**
 * @author thiyagu1
 */
public class CompanyBOTest extends PMDBTestCase {
    private String STOCKCODE = "CODE1";
    private String DEMERGEDENTITY1 = "CODE1A";
    private String DEMERGEDENTITY2 = "CODE1B";
    private final PMDate EXDATE = new PMDate(2, 2, 2006);
    private final PMDate RECORDDATE = EXDATE.previous();


    public CompanyBOTest(String string) {
        super(string, "CompanyActionTestData.xml");
    }

    public void testDoActionForDivident() {

        List<TradeVO> holdingDetails = DAOManager.getTransactionDAO().getHoldingDetails(null, null, STOCKCODE, false);
        TradeVO tradeVO = holdingDetails.get(0);
        CompanyBO companyBO = new CompanyBO() {
            boolean isFutureAction(CompanyActionVO actionVO) {
                return false;
            }

            boolean isDuplicateAction(CompanyActionVO actionVO) {
                return false;
            }
        };
        float expectedDivident = tradeVO.getDivident() + tradeVO.getQty() * 12f;
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, tradeVO.getPurchaseDate().next(), tradeVO.getStockCode(), 12f, 1);
        companyBO.doAction(actionVO);
        holdingDetails = DAOManager.getTransactionDAO().getHoldingDetails(null, null, STOCKCODE, false);
        tradeVO = holdingDetails.get(0);
        assertEquals(expectedDivident, tradeVO.getDivident());

        expectedDivident += tradeVO.getQty() * 120f;
        actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, tradeVO.getPurchaseDate().next().next(), tradeVO.getStockCode(), 100f, 1);
        actionVO.setPercentageValue(true);
        companyBO.doAction(actionVO);
        holdingDetails = DAOManager.getTransactionDAO().getHoldingDetails(null, null, STOCKCODE, false);
        tradeVO = holdingDetails.get(0);
        assertEquals(expectedDivident, Helper.getRoundedOffValue(tradeVO.getDivident(), 2));

        actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, tradeVO.getPurchaseDate(), tradeVO.getStockCode(), 100f, 1);
        companyBO.doAction(actionVO);
        holdingDetails = DAOManager.getTransactionDAO().getHoldingDetails(null, null, STOCKCODE, false);
        tradeVO = holdingDetails.get(0);
        assertEquals(expectedDivident, Helper.getRoundedOffValue(tradeVO.getDivident(), 2));

        actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, tradeVO.getPurchaseDate().next(), "CODE2", 100f, 1);
        companyBO.doAction(actionVO);
        holdingDetails = DAOManager.getTransactionDAO().getHoldingDetails(null, null, STOCKCODE, false);
        tradeVO = holdingDetails.get(0);
        assertEquals(expectedDivident, Helper.getRoundedOffValue(tradeVO.getDivident(), 2));

    }

    public void testDoActionForBonus() {
        PMDate exDate = new PMDate(2, 2, 2006);
        String stockCode = STOCKCODE;
        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        List<TradeVO> holdingDetails = transactionDAO.getHoldingDetails(null, null, stockCode, false);
        List<TradeVO> holdingDetailsForStock2 = transactionDAO.getHoldingDetails(null, null, "CODE2", false);
        List<TradeVO> allTrades = transactionDAO.getTradeDetails(null, null, true);
        float totBrok = 0f, totCost = 0f;
        for (TradeVO allTrade : allTrades) {
            totBrok += allTrade.getBrokerage();
            totCost += allTrade.getPurchasePrice() * allTrade.getQty();
        }

        CompanyBO companyBO = new CompanyBO() {
            boolean isFutureAction(CompanyActionVO actionVO) {
                return false;
            }

            boolean isDuplicateAction(CompanyActionVO actionVO) {
                return false;
            }
        };
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Bonus, exDate, stockCode, 1, 1);
        companyBO.doAction(actionVO);
        List<TradeVO> newHoldingDetails = transactionDAO.getHoldingDetails(null, null, stockCode, false);
        assertEquals(holdingDetails.size() + 4, newHoldingDetails.size());
        for (TradeVO holdingDetail : holdingDetails) {
            assertTrue(newHoldingDetails.contains(holdingDetail));
            newHoldingDetails.remove(holdingDetail);
        }
        boolean flagTr1Po1 = false, flagTr2Po1 = false, flagTr1Po2 = false, flagTr2Po2 = false;
        Set<Integer> buyIDs = new HashSet<Integer>();
        for (TradeVO newHoldingDetail : newHoldingDetails) {
            buyIDs.add(newHoldingDetail.getBuyId());
            if (newHoldingDetail.getPurchasePrice() != 0 || !newHoldingDetail.getPurchaseDate().equals(exDate) ||
                    newHoldingDetail.getBrokerage() != 0) {
                fail();
            }

            if (newHoldingDetail.getTradingAc().equals("TradingACCName") && newHoldingDetail.getPortfolio().equals("PortfolioName") &&
                    newHoldingDetail.getQty() == 29f) {
                flagTr1Po1 = true;
            }
            if (newHoldingDetail.getTradingAc().equals("TradingACCName2") && newHoldingDetail.getPortfolio().equals("PortfolioName") &&
                    newHoldingDetail.getQty() == 10f) {
                flagTr2Po1 = true;
            }
            if (newHoldingDetail.getTradingAc().equals("TradingACCName") && newHoldingDetail.getPortfolio().equals("PortfolioName2") &&
                    newHoldingDetail.getQty() == 11f) {
                flagTr1Po2 = true;
            }
            if (newHoldingDetail.getTradingAc().equals("TradingACCName2") && newHoldingDetail.getPortfolio().equals("PortfolioName2") &&
                    newHoldingDetail.getQty() == 14f) {
                flagTr2Po2 = true;
            }

        }

        assertTrue(flagTr1Po1);
        assertTrue(flagTr2Po1);
        assertTrue(flagTr1Po2);
        assertTrue(flagTr2Po2);
        List<ActionMapping> list = DAOManager.getCompanyActionDAO().getActionMapping(AppConst.COMPANY_ACTION_TYPE.Bonus);
        assertEquals(4, list.size());
        for (ActionMapping actionMapping : list) {
            assertTrue(buyIDs.contains(actionMapping.getTransactionId()));
            buyIDs.remove(actionMapping.getTransactionId());
        }

        assertTrue(buyIDs.isEmpty());
        verifyOtherStockTransactionsNotAffected(transactionDAO, holdingDetailsForStock2, "CODE2");
        verifyTotalBrokerageAndCostRemainsSame(transactionDAO, totBrok, totCost);
    }

    public void testDoActionForBonusInvolving2PortfolioInOneTradingAccountWithAccWithHigherQtyGettingRoundOffBenefit() {
        PMDate exDate = new PMDate(2, 2, 2006);
        String stockCode = STOCKCODE;
        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        List<TradeVO> holdingDetails = transactionDAO.getHoldingDetails(null, null, stockCode, false);
        CompanyBO companyBO = new CompanyBO() {
            boolean isFutureAction(CompanyActionVO actionVO) {
                return false;
            }

            boolean isDuplicateAction(CompanyActionVO actionVO) {
                return false;
            }
        };
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Bonus, exDate, stockCode, 1, 2);
        companyBO.doAction(actionVO);
        List<TradeVO> newHoldingDetails = transactionDAO.getHoldingDetails(null, null, stockCode, false);
        for (TradeVO holdingDetail : holdingDetails) {
            assertTrue(newHoldingDetails.remove(holdingDetail));
        }
        boolean flagP1 = false, flagP2 = false;
        for (TradeVO newHoldingDetail : newHoldingDetails) {
            if (newHoldingDetail.getPortfolio().equals("PortfolioName") && newHoldingDetail.getQty() == 15f) {
                flagP1 = true;
            }
            if (newHoldingDetail.getPortfolio().equals("PortfolioName2") && newHoldingDetail.getQty() == 5f) {
                flagP2 = true;
            }
        }
        assertTrue(flagP1);
        assertTrue(flagP2);
    }

    public void testDoSplitModifiesStockFaceValueIfMatches() {
        String stockCode1 = "ToSplit1";
        IStockDAO stockDAO = DAOManager.getStockDAO();
        stockDAO.insertStock(new StockVO(stockCode1, "Desc", 10f, SERIESTYPE.equity, 10f, (short) 1, "123", new PMDate(), true));
        String stockCode2 = "ToSplit2";
        stockDAO.insertStock(new StockVO(stockCode2, "Desc", 10f, SERIESTYPE.equity, 10f, (short) 2, "124", new PMDate(), true));
        new CompanyBO().doAction(new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, new PMDate(), stockCode1, 2, 10));
        assertEquals(2f, stockDAO.getStock(stockCode1).getFaceValue());
        new CompanyBO().doAction(new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, new PMDate(), stockCode2, 2, 5));
        assertEquals(10f, stockDAO.getStock(stockCode2).getFaceValue());
    }


    public void testDoSplit() throws Exception {
        PMDate exDate = new PMDate(2, 2, 2006);
        String stockCode = STOCKCODE;
        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        TransactionVO transactionVO2 = transactionDAO.getTransaction(2, AppConst.TRADINGTYPE.Buy);
        List<TradeVO> allTrades = transactionDAO.getTradeDetails(null, null, true);
        float totBrok = 0f, totCost = 0f;
        for (TradeVO allTrade : allTrades) {
            totBrok += allTrade.getBrokerage();
            totCost += allTrade.getPurchasePrice() * allTrade.getQty();
        }

        List<TradeVO> holdingDetails = transactionDAO.getHoldingDetails(null, null, stockCode, false);
        List<TradeVO> holdingDetailsForStock2 = transactionDAO.getHoldingDetails(null, null, "CODE2", false);
        CompanyBO companyBO = new CompanyBO() {
            boolean isFutureAction(CompanyActionVO actionVO) {
                return false;
            }

            boolean isDuplicateAction(CompanyActionVO actionVO) {
                return false;
            }
        };
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, exDate, stockCode, 1, 2);
        companyBO.doAction(actionVO);
        Map<Integer, TransactionMapping> transactionMappings = transactionDAO.getTransactionMapping();

        verifyTotalBrokerageAndCostRemainsSame(transactionDAO, totBrok, totCost);

        verifyNoOfHoldingTransactions(transactionDAO, stockCode, holdingDetails);

        verifyActionMappingCount(AppConst.COMPANY_ACTION_TYPE.Split, 8);

        verifyParitiallySoldTransactionToHaveOnlySoldQty(transactionDAO, transactionVO2);
        verifyForPartialHoldingSoldTransactionMappingRemainsSame(transactionMappings);

        checkCompleteHoldingTransactions(transactionDAO, transactionMappings);

        verifyNonHoldingNotAltered(transactionDAO, transactionMappings);

        verifyPartialHoldingGotNewTransactionCreated(transactionDAO, transactionMappings, holdingDetails, stockCode);

        verifyOtherStockTransactionsNotAffected(transactionDAO, holdingDetailsForStock2, "CODE2");
        verifyDayTradingNotAffected(transactionDAO);
        verifyForPartialHoldingQtySoldAfterRecordDateGetsMappedToNewlyCreatedTransaction(transactionDAO, transactionMappings);

    }

    private void verifyForPartialHoldingQtySoldAfterRecordDateGetsMappedToNewlyCreatedTransaction(ITransactionDAO transactionDAO, Map<Integer, TransactionMapping> transactionMappings) {
        TransactionMapping mapping = transactionMappings.get(21);
        assertFalse(mapping.getBuyId() == 2);
        assertEquals(3f, mapping.getQty());
        assertEquals(21, mapping.getSellId());
        TransactionVO buyTransaction = transactionDAO.getTransaction(mapping.getBuyId(), AppConst.TRADINGTYPE.Buy);
        assertEquals(10f, buyTransaction.getQty());
    }

    private void verifyForPartialHoldingSoldTransactionMappingRemainsSame(Map<Integer, TransactionMapping> transactionMappings) {
        TransactionMapping mapping = transactionMappings.get(2);
        assertEquals(2, mapping.getBuyId());
        assertEquals(2, mapping.getSellId());
        assertEquals(5f, mapping.getQty());
    }

    private void verifyDayTradingNotAffected(ITransactionDAO transactionDAO) {
        assertEquals(16f, transactionDAO.getTransaction(11, AppConst.TRADINGTYPE.Buy).getQty());
    }

    private void verifyPartialHoldingGotNewTransactionCreated(ITransactionDAO transactionDAO, Map<Integer, TransactionMapping> transactionMappings, List<TradeVO> holdingDetails, String stockCode) {
        Map<Integer, TradeVO> holdingDetailsMapOnBuyId = createMap(holdingDetails);
        assertNotNull(holdingDetailsMapOnBuyId.get(2));
        assertEquals(5f, transactionDAO.getTransaction(2, AppConst.TRADINGTYPE.Sell).getQty());
        assertEquals(5f, transactionDAO.getTransaction(transactionMappings.get(2).getBuyId(), AppConst.TRADINGTYPE.Buy).getQty());
        Map<Integer, TradeVO> newHoldingDetailsMapOnBuyId = createMap(transactionDAO.getHoldingDetails(null, null, stockCode, false));
        assertNull(newHoldingDetailsMapOnBuyId.get(2));
        for (TradeVO holdingDetail : holdingDetails) {
            newHoldingDetailsMapOnBuyId.remove(holdingDetail.getBuyId());
        }
        verifyRemoveHoldingCreatedForExistingTransactionBySplit(newHoldingDetailsMapOnBuyId);
        assertEquals(1, newHoldingDetailsMapOnBuyId.size());
        TradeVO newlyCreatedTransaction = newHoldingDetailsMapOnBuyId.values().iterator().next();
        assertEquals(7f, newlyCreatedTransaction.getQty());
        assertEquals(1f, newlyCreatedTransaction.getPurchasePrice());
        assertEquals(new PMDate(2, 1, 2006), newlyCreatedTransaction.getPurchaseDate());
        assertEquals(40.25f, newlyCreatedTransaction.getBrokerage());
    }

    private void verifyRemoveHoldingCreatedForExistingTransactionBySplit(Map<Integer, TradeVO> newHoldingDetailsMapOnBuyId) {
        TradeVO tradeVO = newHoldingDetailsMapOnBuyId.remove(5);
        assertEquals(10f, tradeVO.getQty());
    }

    private Map<Integer, TradeVO> createMap(List<TradeVO> holdingDetails) {
        HashMap<Integer, TradeVO> mapOnBuyId = new HashMap<Integer, TradeVO>();
        for (TradeVO holdingDetail : holdingDetails) {
            mapOnBuyId.put(holdingDetail.getBuyId(), holdingDetail);
        }
        return mapOnBuyId;
    }

    private void verifyNonHoldingNotAltered(ITransactionDAO transactionDAO, Map<Integer, TransactionMapping> transactionMappings) {
        assertEquals(10f, transactionDAO.getTransaction(1, AppConst.TRADINGTYPE.Sell).getQty());
        TransactionVO buyTransaction = transactionDAO.getTransaction(1, AppConst.TRADINGTYPE.Buy);
        assertEquals(1f, buyTransaction.getPrice());
        assertEquals(10f, buyTransaction.getQty());
        assertEquals(115f, buyTransaction.getBrokerage());
        assertEquals(10f, transactionDAO.getTransaction(transactionMappings.get(1).getBuyId(), AppConst.TRADINGTYPE.Buy).getQty());
    }

    private void checkCompleteHoldingTransactions(ITransactionDAO transactionDAO, Map<Integer, TransactionMapping> transactionMappings) {
        assertEquals(12f, transactionDAO.getTransaction(3, AppConst.TRADINGTYPE.Buy).getQty());
        assertEquals(4f, transactionDAO.getTransaction(4, AppConst.TRADINGTYPE.Buy).getQty());
        assertEquals(20f, transactionDAO.getTransaction(5, AppConst.TRADINGTYPE.Buy).getQty());

        assertEquals(5f, transactionDAO.getTransaction(3, AppConst.TRADINGTYPE.Sell).getQty());
        assertEquals(10f, transactionDAO.getTransaction(5, AppConst.TRADINGTYPE.Sell).getQty());

        assertEquals(12f, transactionDAO.getTransaction(transactionMappings.get(3).getBuyId(), AppConst.TRADINGTYPE.Buy).getQty());
        assertEquals(20f, transactionDAO.getTransaction(transactionMappings.get(4).getBuyId(), AppConst.TRADINGTYPE.Buy).getQty());
    }

    private void verifyActionMappingCount(AppConst.COMPANY_ACTION_TYPE action, int count) {
        ICompanyActionDAO actionDAO = DAOManager.getCompanyActionDAO();
        List<ActionMapping> list = actionDAO.getActionMapping(action);
        assertEquals(count, list.size());
    }

    private void verifyNoOfHoldingTransactions(ITransactionDAO transactionDAO, String stockCode, List<TradeVO> holdingDetails) {
        List<TradeVO> newHoldingDetails = transactionDAO.getHoldingDetails(null, null, stockCode, false);
        assertEquals(holdingDetails.size() + 1, newHoldingDetails.size());
    }

    private void verifyTotalBrokerageAndCostRemainsSame(ITransactionDAO transactionDAO, float totBrok, float totCost) {
        List<TradeVO> allTrades;
        allTrades = transactionDAO.getTradeDetails(null, null, true);
        float newTotBrok = 0f, newTotCost = 0f;
        for (TradeVO allTrade : allTrades) {
            newTotBrok += allTrade.getBrokerage();
            newTotCost += allTrade.getPurchasePrice() * allTrade.getQty();
        }
        assertEquals(Helper.getRoundedOffValue(totBrok, 2), Helper.getRoundedOffValue(newTotBrok, 2));
        assertEquals(Helper.getRoundedOffValue(totCost, 2), Helper.getRoundedOffValue(newTotCost, 2));
    }

    private void verifyOtherStockTransactionsNotAffected(ITransactionDAO transactionDAO, List<TradeVO> holdingDetailsForStock2, String stockCode) {
        List<TradeVO> newHoldingDetailsForStock2 = transactionDAO.getHoldingDetails(null, null, stockCode, false);
        assertEquals(holdingDetailsForStock2.size(), newHoldingDetailsForStock2.size());
        for (TradeVO tradeVO : newHoldingDetailsForStock2) {
            assertTrue(tradeVO.toString(), holdingDetailsForStock2.contains(tradeVO));
        }
    }

    private void verifyParitiallySoldTransactionToHaveOnlySoldQty(ITransactionDAO transactionDAO, TransactionVO transactionVO2) {
        TransactionVO transactionVO2New = transactionDAO.getTransaction(2, AppConst.TRADINGTYPE.Buy);
        transactionVO2.setQty(5);
        transactionVO2.setBrokerage(57.5f);
        assertEquals(transactionVO2, transactionVO2New);
    }

    public void testDoDemerger() {
        /*
         other stocks not affected
         Already sold not affected
         Bought after record date not affected
         Day trades not affected

         Total brokerage + cost remains same
         Stock for each new entity should be equal to base companies holding qty on record date
         base companies stocks sold after record date should get new price, brokerage
         new companies stock count should remain same even after old companies sell transaction
         each new entities cost,brokerage should be in the ratio of base company, total cost should add to base value
         divient paid before should remain only with old company

         For partial holding, new price should reflect only for the new qty
         verify exdate, record date
         verify portfolio name and trading acc name
         verify duplicate actions are not allowed

         */
    }

    public void testDoDemergerDoesNotAllowDuplicateAction() {
        CompanyActionVO actionVO = getDemergerAction(EXDATE, STOCKCODE);
        CompanyBO companyBO = new CompanyBO() {

            boolean isFutureAction(CompanyActionVO actionVO) {
                return false;
            }
        };
        assertTrue(companyBO.doAction(actionVO));
        assertFalse(companyBO.doAction(actionVO));
    }


    public void testDoDemergerPerformsActionBasedOnRecordDate() {
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        TransactionVO beforeRDvo = new TransactionVO(RECORDDATE.previous(), STOCKCODE, AppConst.TRADINGTYPE.Buy, 10, 101, 200, "PortfolioName", "TradingACCName", false);
        int beforeRecordDateID = iTransactionDAO.insertTransaction(beforeRDvo);
        TransactionVO onRDvo = new TransactionVO(RECORDDATE, STOCKCODE, AppConst.TRADINGTYPE.Buy, 10, 102, 200, "PortfolioName", "TradingACCName", false);
        int recordDateID = iTransactionDAO.insertTransaction(onRDvo);
        TransactionVO afterRDvo = new TransactionVO(EXDATE, STOCKCODE, AppConst.TRADINGTYPE.Buy, 10, 103, 200, "PortfolioName", "TradingACCName", false);
        int exDateID = iTransactionDAO.insertTransaction(afterRDvo);
        CompanyActionVO actionVO = getDemergerAction(EXDATE, STOCKCODE);
        performDemerger(actionVO);
        assertNotSame(beforeRDvo.getPrice(), iTransactionDAO.getTransaction(beforeRecordDateID, AppConst.TRADINGTYPE.Buy).getPrice());
        assertNotSame(onRDvo.getPrice(), iTransactionDAO.getTransaction(recordDateID, AppConst.TRADINGTYPE.Buy).getPrice());
        assertEquals(afterRDvo, iTransactionDAO.getTransaction(exDateID, AppConst.TRADINGTYPE.Buy));
    }


    public void testDoDemergerCreatesEntitiesInProperPortfolioAndTradingAccout() {
        CompanyActionVO actionVO = getDemergerAction(EXDATE, STOCKCODE);
        performDemerger(actionVO);
        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        String tradingAcc1 = "TradingACCName";
        String tradingAcc2 = "TradingACCName2";
        String portfolio1 = "PortfolioName";
        String portfolio2 = "PortfolioName2";
        verifyQtyInPortfolioTradingAcc(transactionDAO, tradingAcc1, portfolio1, 29);
        verifyQtyInPortfolioTradingAcc(transactionDAO, tradingAcc1, portfolio2, 11);
        verifyQtyInPortfolioTradingAcc(transactionDAO, tradingAcc2, portfolio1, 10);
        verifyQtyInPortfolioTradingAcc(transactionDAO, tradingAcc2, portfolio2, 14);
    }

    private void verifyQtyInPortfolioTradingAcc(ITransactionDAO transactionDAO, String tradingAcc1, String portfolio1, int qty) {
        assertEquals(qty, getHoldingCountForStockOnDate(transactionDAO, STOCKCODE, RECORDDATE, tradingAcc1, portfolio1));
        assertEquals(qty, getHoldingCountForStockOnDate(transactionDAO, DEMERGEDENTITY1, RECORDDATE, tradingAcc1, portfolio1));
        assertEquals(qty, getHoldingCountForStockOnDate(transactionDAO, DEMERGEDENTITY2, RECORDDATE, tradingAcc1, portfolio1));
    }


    public void testDoDemergerOfParitialHoldingDoesNotAffectDivident() {
        DAOManager.getCompanyActionDAO().insertCompanyAction(new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, EXDATE, STOCKCODE, 200, 1));
        Map<Integer, TradeVO> tradeMap = getTradeDetailsMap(null, null, true);
        float totDivident2 = tradeMap.get(2).getDivident();
        float totDivident21 = tradeMap.get(21).getDivident();
        performDemerger(getDemergerAction(EXDATE, STOCKCODE));
        tradeMap = getTradeDetailsMap(null, null, true);
        assertEquals(totDivident2, tradeMap.get(2).getDivident());
        assertEquals(totDivident21, tradeMap.get(21).getDivident());
    }

    public void testDoDemergerOfParitialHoldingAffectsPriceAndBrokerageOfOnlyHolding() {
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        TransactionVO buyTrans2 = iTransactionDAO.getTransaction(2, AppConst.TRADINGTYPE.Buy);
        Map<Integer, TransactionMapping> transactionMapping = iTransactionDAO.getTransactionMapping();
        assertEquals(2, transactionMapping.get(2).getBuyId());
        assertEquals(2, transactionMapping.get(21).getBuyId());

        performDemerger(getDemergerAction(EXDATE, STOCKCODE));

        transactionMapping = iTransactionDAO.getTransactionMapping();
        assertEquals(2, transactionMapping.get(2).getBuyId());
        TransactionVO truncatedTrans = iTransactionDAO.getTransaction(2, AppConst.TRADINGTYPE.Buy);
        assertEquals(5.0f, truncatedTrans.getQty());
        assertEquals(buyTrans2.getPrice(), truncatedTrans.getPrice());
        assertEquals(buyTrans2.getBrokerage() / 2, truncatedTrans.getBrokerage());

        int buyIdForTrade21 = transactionMapping.get(21).getBuyId();
        assertNotSame(2, buyIdForTrade21);
        TransactionVO newlyCreateBuyTrans = iTransactionDAO.getTransaction(buyIdForTrade21, AppConst.TRADINGTYPE.Buy);
        assertEquals(5.0f, newlyCreateBuyTrans.getQty());
        assertEquals(buyTrans2.getPrice() * 51.2f / 100f, newlyCreateBuyTrans.getPrice());
        assertEquals(buyTrans2.getBrokerage() / 2f * 51.2f / 100f, newlyCreateBuyTrans.getBrokerage());

    }

    public void testDoDemergerLeavesDividentWithTheParentEntity() {
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        float qty = 10;
        float price = 1000;
        float brok = 200;
        String stockCode = "CODE3";
        iTransactionDAO.insertTransaction(new TransactionVO(new PMDate(1, 1, 2004), stockCode, AppConst.TRADINGTYPE.Buy, qty, price, brok, "PortfolioName", "TradingACCName", false));
        DAOManager.getCompanyActionDAO().insertCompanyAction(new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(2, 1, 2004), stockCode, 123, 1));
        CompanyActionVO actionVO = getDemergerAction(new PMDate(2, 1, 2004), stockCode);
        performDemerger(actionVO);
        for (DemergerVO demergerVO : actionVO.getDemergerData()) {
            List<TradeVO> tradeList = iTransactionDAO.getTradeDetails(null, null, demergerVO.getNewStockCode(), true);
            if (demergerVO.getNewStockCode().equals(stockCode)) {
                assertEquals(1230f, tradeList.get(0).getDivident());
            } else {
                assertEquals(0f, tradeList.get(0).getDivident());
            }
        }
    }

    public void testDoDemergerCreatesEntitiesWithPriceBasedOnWeightage() {
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        float qty = 10;
        float price = 1000;
        float brok = 200;
        String stockCode = "CODE3";
        iTransactionDAO.insertTransaction(new TransactionVO(new PMDate(1, 1, 2004), stockCode, AppConst.TRADINGTYPE.Buy, qty, price, brok, "PortfolioName", "TradingACCName", false));
        CompanyActionVO actionVO = getDemergerAction(new PMDate(2, 1, 2004), stockCode);
        performDemerger(actionVO);
        for (DemergerVO demergerVO : actionVO.getDemergerData()) {
            List<TradeVO> tradeList = iTransactionDAO.getTradeDetails(null, null, demergerVO.getNewStockCode(), true);
            assertEquals(1, tradeList.size());
            assertEquals(price * demergerVO.getBookValueRatio() / 100f, tradeList.get(0).getPurchasePrice());
            assertEquals(brok * demergerVO.getBookValueRatio() / 100f, Helper.getRoundedOffValue(tradeList.get(0).getBrokerage(), 2));
            assertEquals(10f, tradeList.get(0).getQty());

        }
    }


    public void testDoDemergerNewEntitiesNotAffectedByParentCompanySellTransactionOfHoldingQty() {
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        int holdingQty = getHoldingCountForStockOnDate(iTransactionDAO, STOCKCODE, EXDATE.previous(), null, null);
        performDemerger(getDemergerAction(EXDATE, STOCKCODE));
        PMDate firstSaleDate = new PMDate(20060210);
        assertNotSame(holdingQty, getHoldingCountForStockOnDate(iTransactionDAO, STOCKCODE, firstSaleDate, null, null));
        assertEquals(holdingQty, getHoldingCountForStockOnDate(iTransactionDAO, DEMERGEDENTITY1, firstSaleDate, null, null));
        assertEquals(holdingQty, getHoldingCountForStockOnDate(iTransactionDAO, DEMERGEDENTITY2, firstSaleDate, null, null));

    }


    public void testDoDemergerUpdatesPurchasePriceAndBrokerageForQtySoldAfterRecordDate() {
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        performDemerger(getDemergerAction(EXDATE, STOCKCODE));
        Map<Integer, TransactionMapping> transactionMapping = iTransactionDAO.getTransactionMapping();
        verifyBrokerageQtyAndPriceForBuy(iTransactionDAO, transactionMapping, 21, 2f, 0.512f, 57.5f, 5f);
        verifyBrokerageQtyAndPriceForBuy(iTransactionDAO, transactionMapping, 3, 3f, 0.512f, 115f, 6f);
        verifyBrokerageQtyAndPriceForBuy(iTransactionDAO, transactionMapping, 4, 5f, 0.512f, 115f, 10f);
    }

    private void performDemerger(CompanyActionVO actionVO) {
        CompanyBO companyBO = getCompanyActionMockingValidation();
        companyBO.doAction(actionVO);
    }

    private void verifyBrokerageQtyAndPriceForBuy(ITransactionDAO iTransactionDAO, Map<Integer, TransactionMapping> transactionMapping, int tradeID, float price, float weightage, float brok, float qty) {
        TransactionVO buyTransaction = iTransactionDAO.getTransaction(transactionMapping.get(tradeID).getBuyId(), AppConst.TRADINGTYPE.Buy);
        verifyPriceQtyBrok(price, weightage, buyTransaction, brok, qty);
    }

    private void verifyPriceQtyBrok(float price, float weightage, TransactionVO buyTransaction, float brok, float qty) {
        assertEquals(Helper.getRoundedOffValue(price * weightage, 2), Helper.getRoundedOffValue(buyTransaction.getPrice(), 2));
        assertEquals(Helper.getRoundedOffValue(brok * weightage, 2), Helper.getRoundedOffValue(buyTransaction.getBrokerage(), 2));
        assertEquals(qty, buyTransaction.getQty());
    }

    public void testDoDemergerCreatesSameQtyForEachNewEntityAsHoldingQty() {
        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        PMDate recordDate = EXDATE.previous();
        int holdingQty = getHoldingCountForStockOnDate(transactionDAO, STOCKCODE, recordDate, null, null);
        CompanyActionVO actionVO = getDemergerAction(EXDATE, STOCKCODE);
        performDemerger(actionVO);
        for (DemergerVO demergerVO : actionVO.getDemergerData()) {
            assertEquals(holdingQty, getHoldingCountForStockOnDate(transactionDAO, demergerVO.getNewStockCode(), recordDate, null, null));
        }

    }

    public void testIsEqualAfterNormalization() {
        final CompanyActionVO logActionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(1, 1, 2005), "STK", 50f, 1f);
        logActionVO.setPercentageValue(true);
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(1, 1, 2005), "STK", 5f, 1f);
        CompanyBO companyBO = new CompanyBO() {
            void doNormalization(CompanyActionVO actionVO) {
                actionVO.setPercentageValue(true);
                actionVO.setDsbValue(50f);
            }
        };
        assertTrue(companyBO.isEqualAfterNormalization(logActionVO, actionVO));

        companyBO = new CompanyBO() {
            void doNormalization(CompanyActionVO actionVO) {
            }
        };

        assertFalse(companyBO.isEqualAfterNormalization(logActionVO, actionVO));
        actionVO.setPercentageValue(true);
        companyBO = new CompanyBO() {
            void doNormalization(CompanyActionVO actionVO) {
                actionVO.setPercentageValue(true);
                actionVO.setDsbValue(50f);
            }
        };
        assertFalse(companyBO.isEqualAfterNormalization(logActionVO, actionVO));
    }


    public void testNormalize() {
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(1, 1, 2005), "CODE1", 240f, 1f);
        CompanyBO companyBO = new CompanyBO();
        companyBO.doNormalization(actionVO);
        assertEquals(100f, actionVO.getDsbValue());
        assertTrue(actionVO.isPercentageValue());
    }

    public void testDoDemergerEnsuresTotalCostAndBrokerageRemainsSame() {
        PMDate exDate = new PMDate(2, 2, 2006);
        String stockCode = STOCKCODE;
        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        List<TradeVO> allTrades = transactionDAO.getTradeDetails(null, null, true);
        float totBrok = 0f, totCost = 0f;
        for (TradeVO allTrade : allTrades) {
            totBrok += allTrade.getBrokerage();
            totCost += allTrade.getPurchasePrice() * allTrade.getQty();
        }
        CompanyActionVO actionVO = getDemergerAction(exDate, stockCode);
        CompanyBO companyBO = getCompanyActionMockingValidation();
        companyBO.doAction(actionVO);
        verifyTotalBrokerageAndCostRemainsSame(transactionDAO, totBrok, totCost);
    }

    public void testDoDemergerNotAffectingAlreadySoldQty() {
        PMDate exDate = new PMDate(2, 2, 2006);
        PMDate recordDate = exDate.previous();
        String stockCode = STOCKCODE;
        Map<Integer, TradeVO> tradeMap = getTradeDetailsMap(null, null, true);
        CompanyActionVO actionVO = getDemergerAction(exDate, stockCode);
        CompanyBO companyBO = getCompanyActionMockingValidation();
        companyBO.doAction(actionVO);
        Map<Integer, TradeVO> newTradeMap = getTradeDetailsMap(null, null, true);
        for (Integer id : tradeMap.keySet()) {
            if (!tradeMap.get(id).isHolding(recordDate)) {
                assertEquals(tradeMap.get(id), newTradeMap.get(id));
            }
        }

    }

    public void testDoDemergerNotAffectingBoughtAfterRecordDate() {
        PMDate exDate = new PMDate(2, 2, 2006);
        PMDate recordDate = exDate.previous();
        String stockCode = STOCKCODE;
        Map<Integer, TradeVO> tradeMap = getTradeDetailsMap(null, null, true);
        CompanyActionVO actionVO = getDemergerAction(exDate, stockCode);
        CompanyBO companyBO = getCompanyActionMockingValidation();
        companyBO.doAction(actionVO);
        Map<Integer, TradeVO> newTradeMap = getTradeDetailsMap(null, null, true);
        for (Integer id : tradeMap.keySet()) {
            if (tradeMap.get(id).getPurchaseDate().after(recordDate)) {
                assertEquals(tradeMap.get(id), newTradeMap.get(id));
            }
        }

    }

    public void testDoDemergerNotAffectingDayTrade() {
        String stockCode = STOCKCODE;
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        PMDate exDate = new PMDate(2, 2, 2006);
        PMDate recordDate = exDate.previous();
        TransactionVO beforeRDvo = new TransactionVO(recordDate.previous(), stockCode, AppConst.TRADINGTYPE.Buy, 10, 101, 200, "PortfolioName", "TradingACCName", true);
        int beforeRecordDateID = iTransactionDAO.insertTransaction(beforeRDvo);
        TransactionVO onRDvo = new TransactionVO(recordDate, stockCode, AppConst.TRADINGTYPE.Buy, 10, 102, 200, "PortfolioName", "TradingACCName", true);
        int recordDateID = iTransactionDAO.insertTransaction(onRDvo);
        TransactionVO afterRDvo = new TransactionVO(exDate, stockCode, AppConst.TRADINGTYPE.Buy, 10, 103, 200, "PortfolioName", "TradingACCName", true);
        int afterRecordDateID = iTransactionDAO.insertTransaction(afterRDvo);
        CompanyActionVO actionVO = getDemergerAction(exDate, stockCode);
        CompanyBO companyBO = getCompanyActionMockingValidation();
        companyBO.doAction(actionVO);
        assertEquals(beforeRDvo, iTransactionDAO.getTransaction(beforeRecordDateID, AppConst.TRADINGTYPE.Buy));
        assertEquals(onRDvo, iTransactionDAO.getTransaction(recordDateID, AppConst.TRADINGTYPE.Buy));
        assertEquals(afterRDvo, iTransactionDAO.getTransaction(afterRecordDateID, AppConst.TRADINGTYPE.Buy));
    }

    private Map<Integer, TradeVO> getTradeDetailsMap(String tradingAccName, String portfolioName, boolean incDayTrading) {
        List<TradeVO> newTradeList = DAOManager.getTransactionDAO().getTradeDetails(tradingAccName, portfolioName, incDayTrading);
        Map<Integer, TradeVO> retVal = new HashMap<Integer, TradeVO>();
        for (TradeVO tradeVO : newTradeList) {
            retVal.put(tradeVO.getId(), tradeVO);
        }
        return retVal;
    }

    public void testDoDemergerNotAffectingOtherStocks() {
        PMDate exDate = new PMDate(2, 2, 2006);
        String stockCode = STOCKCODE;
        String stockCode2 = "CODE2";
        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        List<TradeVO> holdingDetailsForStock2 = transactionDAO.getHoldingDetails(null, null, stockCode2, false);
        CompanyActionVO actionVO = getDemergerAction(exDate, stockCode);
        CompanyBO companyBO = getCompanyActionMockingValidation();
        companyBO.doAction(actionVO);
        List<TradeVO> newHoldingDetailsForStock2 = transactionDAO.getHoldingDetails(null, null, stockCode2, false);
        assertEquals(holdingDetailsForStock2.size(), newHoldingDetailsForStock2.size());
        for (int i = 0; i < holdingDetailsForStock2.size(); i++) {
            assertEquals(holdingDetailsForStock2.get(i), newHoldingDetailsForStock2.get(i));
        }
    }

    private CompanyActionVO getDemergerAction(PMDate exDate, String stockCode) {
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Demerger, exDate, stockCode, 1, 1);
        Vector<DemergerVO> demergerList = new Vector<DemergerVO>();
        demergerList.add(new DemergerVO(stockCode, 51.2f));
        demergerList.add(new DemergerVO(DEMERGEDENTITY1, 8.8f));
        demergerList.add(new DemergerVO(DEMERGEDENTITY2, 40.0f));
        actionVO.setDemergerData(demergerList);
        return actionVO;
    }

    private CompanyBO getCompanyActionMockingValidation() {
        CompanyBO companyBO = new CompanyBO() {
            boolean isFutureAction(CompanyActionVO actionVO) {
                return false;
            }

            boolean isDuplicateAction(CompanyActionVO actionVO) {
                return false;
            }
        };
        return companyBO;
    }


    private int getHoldingCountForStockOnDate(ITransactionDAO transactionDAO, String stockCode, PMDate pmDate, String tradingAccName, String portfolioName) {
        List<TradeVO> allTradesForStock = transactionDAO.getTradeDetails(tradingAccName, portfolioName, stockCode, false);
        int holdingQty = 0;
        for (TradeVO tradeVO : allTradesForStock) {
            if (tradeVO.isHolding(pmDate)) {
                holdingQty += tradeVO.getQty();
            }
        }
        return holdingQty;
    }

    public void testNormalizeDivident() {

        CompanyBO companyBO = new CompanyBO();
        String stockCode = "CODE3";
        CompanyActionVO bonusAction = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Bonus, new PMDate(1, 12, 2003), stockCode, 90, 1);
        companyBO.doAction(bonusAction);
        Vector<DemergerVO> demergerVOs = new Vector<DemergerVO>();
        demergerVOs.add(new DemergerVO(stockCode, 90f));
        demergerVOs.add(new DemergerVO(DEMERGEDENTITY1, 10f));
        CompanyActionVO demergerAction = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Demerger, new PMDate(2, 12, 2003), stockCode, demergerVOs);
        demergerAction.setBase(1f);
        demergerAction.setDsbValue(1f);
        companyBO.doAction(demergerAction);

        companyBO.doAction(new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(1, 1, 2004), stockCode, 95, 1));
        companyBO.doAction(new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(2, 1, 2004), stockCode, 63, 1));
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(3, 1, 2004), stockCode, 21.5f, 1);
        actionVO.setPercentageValue(true);
        companyBO.doAction(actionVO);
        companyBO.doAction(new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(4, 1, 2004), stockCode, 60, 1));
        CompanyActionVO splitAction = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, new PMDate(5, 1, 2004), stockCode, 5, 15);
        companyBO.doAction(splitAction);
        companyBO.doAction(new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(6, 1, 2004), stockCode, 90, 1));
        CompanyActionVO splitAction2 = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, new PMDate(7, 1, 2004), stockCode, 1, 5);
        companyBO.doAction(splitAction2);
        companyBO.normalizeDivident(stockCode);
        List<CompanyActionVO> actionVOs = companyBO.getDAO().getCompanyAction(stockCode);
        for (CompanyActionVO vo : actionVOs) {
            if (vo.getAction() == AppConst.COMPANY_ACTION_TYPE.Divident) {
                assertTrue(vo.isPercentageValue());
            }
            if (vo.getExDate().equals(new PMDate(1, 12, 2003))) {
                assertEquals(bonusAction, vo);
            }
            if (vo.getExDate().equals(new PMDate(2, 12, 2003))) {
                assertEquals(demergerAction, vo);
            }
            if (vo.getExDate().equals(new PMDate(1, 1, 2004))) {
                assertEquals(633.3334f, vo.getDsbValue());
            }
            if (vo.getExDate().equals(new PMDate(2, 1, 2004))) {
                assertEquals(420f, vo.getDsbValue());
            }
            if (vo.getExDate().equals(new PMDate(3, 1, 2004))) {
                assertEquals(21.5f, vo.getDsbValue());
            }
            if (vo.getExDate().equals(new PMDate(4, 1, 2004))) {
                assertEquals(400f, vo.getDsbValue());
            }
            if (vo.getExDate().equals(new PMDate(5, 1, 2004))) {
                assertEquals(splitAction, vo);
            }
            if (vo.getExDate().equals(new PMDate(6, 1, 2004))) {
                assertEquals(1800f, vo.getDsbValue());
            }
            if (vo.getExDate().equals(new PMDate(7, 1, 2004))) {
                assertEquals(splitAction2, vo);
            }
        }
    }

    public void testITCIssue() {

        CompanyActionVO divident = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(18, 7, 2005), "ITC", 31f, 1);
        CompanyActionVO split = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, new PMDate(21, 9, 2005), "ITC", 1f, 10f);
        CompanyActionVO bonus = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Bonus, new PMDate(21, 9, 2005), "ITC", 1f, 2f);

        CompanyBO companyBO = new CompanyBO();

        companyBO.doAction(divident);
        companyBO.doAction(split);
        companyBO.doAction(bonus);
        companyBO.normalizeDivident("ITC");

        List<TradeVO> tradeList = DAOManager.getTransactionDAO().getTradeDetails("TradForTradingBO1", "PortForTradingBO1", false);
        assertEquals(2, tradeList.size());
        TradeVO tradeVO = tradeList.get(0);
        assertEquals(310f, tradeVO.getDivident());

    }

    public void testDCHLIssue() {

        CompanyActionVO divident1 = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(23, 9, 2005), "DCHL", 10f, 1);
        divident1.setPercentageValue(true);
        CompanyActionVO divident2 = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(4, 8, 2006), "DCHL", 10f, 1);
        divident2.setPercentageValue(true);
        CompanyActionVO divident3 = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(1, 2, 2007), "DCHL", 50f, 1);
        divident3.setPercentageValue(true);

        CompanyActionVO split = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, new PMDate(13, 3, 2007), "DCHL", 2f, 10f);

        CompanyBO companyBO = new CompanyBO();

        companyBO.doAction(divident1);
        companyBO.doAction(divident2);
        companyBO.doAction(divident3);
        companyBO.doAction(split);
        companyBO.normalizeDivident("DCHL");

        List<TradeVO> tradeList = DAOManager.getTransactionDAO().getTradeDetails("TradForTradingBO1", "PortfolioName2", "DCHL", false);
        assertEquals(2, tradeList.size());
        TradeVO tradeVO1 = tradeList.get(0);
        assertEquals(50f, tradeVO1.getDivident());
        TradeVO tradeVO2 = tradeList.get(1);
        assertEquals(25f, tradeVO2.getDivident());

    }

    public void testFaceValueOn() {
        CompanyBO companyBO = new CompanyBO();
        assertEquals(20f, companyBO.faceValueOn("CODE4", new PMDate(1, 1, 2004)));
        assertEquals(10f, companyBO.faceValueOn("CODE4", new PMDate(2, 1, 2004)));
        assertEquals(10f, companyBO.faceValueOn("CODE4", new PMDate(1, 2, 2007)));
        assertEquals(1f, companyBO.faceValueOn("CODE4", new PMDate(2, 2, 2007)));

    }

    public void testDoActionForSplitForOnlyBuy() throws Exception {
        PMDate buyDate = new PMDate(3, 1, 2007);
        String stockCode = "CODE4";
        String portfolio = "PortfolioName";
        String tradingAc = "TradingACCName";
        float qty = 12;
        float price = 100;
        float brok = 200;
        TransactionVO transVO = new TransactionVO(buyDate, stockCode, AppConst.TRADINGTYPE.Buy, qty, price, brok, portfolio, tradingAc, false);
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        int transactionID = iTransactionDAO.insertTransaction(transVO);
        CompanyActionVO splitAction = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, buyDate.next(), stockCode, 2f, 10f);
        new CompanyBO().doAction(splitAction);
        TransactionVO updatedTransactionVO = iTransactionDAO.getTransaction(transactionID, AppConst.TRADINGTYPE.Buy);
        assertEquals(qty * 5, updatedTransactionVO.getQty());
        assertEquals(price / 5.0f, updatedTransactionVO.getPrice());
        assertEquals(brok, updatedTransactionVO.getBrokerage());
        List<TradeVO> list = iTransactionDAO.getTradeDetails(tradingAc, portfolio, stockCode, false);
        assertEquals(1, list.size());
        assertEquals(transVO.getQty() * 5, list.get(0).getQty());
        assertEquals(price / 5.0f, list.get(0).getPurchasePrice());
        assertEquals(brok, list.get(0).getBrokerage());
    }

    public void testDoActionForSplitForSellBeforeExDateWithHolding() throws Exception {
        PMDate buyDate = new PMDate(3, 1, 2007);
        String stockCode = "CODE4";
        String portfolio = "PortfolioName";
        String tradingAc = "TradingACCName";
        float qty = 12;
        float soldQty = 6;
        float price = 100;
        float brok = 200;
        float sellBrok = 19;
        CompanyBO companyBO = new CompanyBO();
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        int transactionID = iTransactionDAO.insertTransaction(new TransactionVO(buyDate, stockCode, AppConst.TRADINGTYPE.Buy, qty, price, brok, portfolio, tradingAc, false));
        new TradingBO().doTrading(new TransactionVO(buyDate, stockCode, AppConst.TRADINGTYPE.Sell, soldQty, price, sellBrok, portfolio, tradingAc, false));
        CompanyActionVO splitAction = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, buyDate.next().next(), stockCode, 2f, 10f);
        companyBO.doAction(splitAction);
        float holdingQty = qty - soldQty;
        verifyBuyTransaction(stockCode, portfolio, tradingAc, price, brok, iTransactionDAO, price / 5.0f, soldQty + holdingQty * 5);
        List<TradeVO> list = iTransactionDAO.getTradeDetails(tradingAc, portfolio, stockCode, false);
        assertEquals(2, list.size());
        assertEquals(soldQty + holdingQty * 5, list.get(0).getQty() + list.get(1).getQty());
        assertEquals(price, list.get(1).getPurchasePrice());
        assertEquals(price / 5.0f, list.get(0).getPurchasePrice());
        assertEquals(brok + sellBrok, list.get(0).getBrokerage() + list.get(1).getBrokerage());
    }

    public void testDoActionForSplitForSellAfterExDateWithHolding() throws Exception {
        PMDate buyDate = new PMDate(3, 1, 2007);
        String stockCode = "CODE4";
        String portfolio = "PortfolioName";
        String tradingAc = "TradingACCName";
        float qty = 12;
        float soldQty = 6;
        float price = 100;
        float brok = 200;
        float sellBrok = 19;
        CompanyBO companyBO = new CompanyBO();
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        int transactionID = iTransactionDAO.insertTransaction(new TransactionVO(buyDate, stockCode, AppConst.TRADINGTYPE.Buy, qty, price, brok, portfolio, tradingAc, false));
        new TradingBO().doTrading(new TransactionVO(buyDate.next().next(), stockCode, AppConst.TRADINGTYPE.Sell, soldQty, price, sellBrok, portfolio, tradingAc, false));
        CompanyActionVO splitAction = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, buyDate.next(), stockCode, 2f, 10f);
        companyBO.doAction(splitAction);
        List<TransactionVO> transactionVOs = iTransactionDAO.getTransactionList(tradingAc, portfolio, stockCode, AppConst.TRADINGTYPE.Buy);
        assertEquals(1, transactionVOs.size());
        assertEquals(qty * 5, transactionVOs.get(0).getQty());
        assertEquals(price / 5.0f, transactionVOs.get(0).getPrice());
        assertEquals(brok, transactionVOs.get(0).getBrokerage());
        List<TradeVO> list = iTransactionDAO.getTradeDetails(tradingAc, portfolio, stockCode, false);
        assertEquals(2, list.size());
        float holdigQty = list.get(0).getQty();
        float soldTransQty = list.get(1).getQty();
        assertEquals(soldQty, soldTransQty);
        assertEquals(qty * 5 - soldQty, holdigQty);
        assertEquals(qty * 5, holdigQty + soldTransQty);
        assertEquals(price / 5.0f, list.get(0).getPurchasePrice());
        assertEquals(price / 5.0f, list.get(1).getPurchasePrice());
        assertEquals(brok + sellBrok, list.get(0).getBrokerage() + list.get(1).getBrokerage());
    }

    public void testDoActionForSplitForSellBeforeAfterExDateWithHolding() throws Exception {
        PMDate buyDate = new PMDate(3, 1, 2007);
        String stockCode = "CODE4";
        String portfolio = "PortfolioName";
        String tradingAc = "TradingACCName";
        float qty = 12;
        float soldQtyBeforeSplit = 6;
        float soldQtyAfterSplit = 3;
        float price = 100;
        float brok = 200;
        float sellBrok1 = 19;
        float sellBrok2 = 98;
        CompanyBO companyBO = new CompanyBO();
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        iTransactionDAO.insertTransaction(new TransactionVO(buyDate, stockCode, AppConst.TRADINGTYPE.Buy, qty, price, brok, portfolio, tradingAc, false));
        new TradingBO().doTrading(new TransactionVO(buyDate, stockCode, AppConst.TRADINGTYPE.Sell, soldQtyBeforeSplit, price, sellBrok1, portfolio, tradingAc, false));
        PMDate exDate = buyDate.next().next();
        new TradingBO().doTrading(new TransactionVO(exDate, stockCode, AppConst.TRADINGTYPE.Sell, soldQtyAfterSplit, price, sellBrok2, portfolio, tradingAc, false));
        CompanyActionVO splitAction = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, exDate, stockCode, 2f, 10f);
        companyBO.doAction(splitAction);

        float priceAfterSplit = price / 5.0f;
        float holdingQty = qty - soldQtyBeforeSplit;
        float qtyAfterSplit = holdingQty * 5;
        float finalHoldingQty = qtyAfterSplit - soldQtyAfterSplit;
        float totalQtyAfterSplit = soldQtyBeforeSplit + qtyAfterSplit;

        verifyBuyTransaction(stockCode, portfolio, tradingAc, price, brok, iTransactionDAO, priceAfterSplit, totalQtyAfterSplit);

        List<TradeVO> list = iTransactionDAO.getTradeDetails(tradingAc, portfolio, stockCode, false);
        assertEquals(3, list.size());
        TradeVO holdingVO = list.get(0);
        TradeVO soldBeforeRecordDateVO = list.get(1);
        TradeVO soldAfterRecordDateVO = list.get(2);
        assertTrue(holdingVO.isHolding());
        assertTrue(soldBeforeRecordDateVO.getSaleDate().before(exDate));
        assertFalse(soldAfterRecordDateVO.getSaleDate().before(exDate));

        assertEquals(soldQtyBeforeSplit, soldBeforeRecordDateVO.getQty());
        assertEquals(soldQtyAfterSplit, soldAfterRecordDateVO.getQty());
        assertEquals(finalHoldingQty, holdingVO.getQty());

        assertEquals(totalQtyAfterSplit, holdingVO.getQty() + soldBeforeRecordDateVO.getQty() + soldAfterRecordDateVO.getQty());
        assertEquals(price, soldBeforeRecordDateVO.getPurchasePrice());
        assertEquals(priceAfterSplit, soldAfterRecordDateVO.getPurchasePrice());
        assertEquals(priceAfterSplit, holdingVO.getPurchasePrice());
        assertEquals(brok + sellBrok1 + sellBrok2, holdingVO.getBrokerage() + soldBeforeRecordDateVO.getBrokerage() + soldAfterRecordDateVO.getBrokerage());
    }

    private void verifyBuyTransaction(String stockCode, String portfolio, String tradingAc, float price, float brok, ITransactionDAO iTransactionDAO,
                                      float priceAfterSplit, float totalQtyAfterSplit) {
        List<TransactionVO> transactionVOs = iTransactionDAO.getTransactionList(tradingAc, portfolio, stockCode, AppConst.TRADINGTYPE.Buy);
        assertEquals(2, transactionVOs.size());
        assertEquals(totalQtyAfterSplit, transactionVOs.get(0).getQty() + transactionVOs.get(1).getQty());
        assertEquals(price, transactionVOs.get(0).getPrice());
        assertEquals(priceAfterSplit, transactionVOs.get(1).getPrice());
        assertEquals(brok, transactionVOs.get(0).getBrokerage() + transactionVOs.get(1).getBrokerage());
    }

}
