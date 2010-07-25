/*
 * Created on Oct 14, 2004
 *
 */
package pm.bo;

import org.apache.log4j.Logger;
import pm.action.QuoteManager;
import pm.dao.CompanyDAO;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.ICompanyActionDAO;
import pm.dao.ibatis.dao.IStockDAO;
import pm.dao.ibatis.dao.ITransactionDAO;
import pm.util.AppConst;
import pm.util.AppConst.COMPANY_ACTION_TYPE;
import pm.util.AppConst.CORP_RESULT_TIMELINE;
import pm.util.BusinessLogger;
import pm.util.PMDate;
import pm.vo.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

import static java.lang.Math.*;

public class CompanyBO {

    private static final Logger logger = Logger.getLogger(CompanyBO.class);

    //TODO do away with actionApplied field
    private boolean actionApplied = false;

    // TODO Round off divident , move divident calculation to trading ac specific

    public boolean doAction(CompanyActionVO actionVO) { //TODO remove return value
        logger.info("Performing company action : " + actionVO);
        if (isDuplicateAction(actionVO)) {
            logger.info("Duplicate action entered : " + actionVO);
            return false;
        }
        if (isFutureAction(actionVO)) {
            logger.info("Future action entered : " + actionVO);
            return false;
        }
        actionApplied = false;
        BusinessLogger.logTransaction(actionVO);
        return performAction(actionVO);
    }

    public boolean doActionAfterNormalize(CompanyActionVO actionVO) {
        doNormalization(actionVO);
        return doAction(actionVO);
    }

    private boolean performAction(CompanyActionVO actionVO) {
        boolean retVal = false;
        switch (actionVO.getAction()) {
            case Bonus:
                retVal = doBonus(actionVO);
                break;
            case Demerger:
                retVal = doDemerger(actionVO);
                break;
            case Divident:
                retVal = saveActionInfo(actionVO);
                break;
            case Split:
                retVal = doSplit(actionVO);
                break;
            case Merger:
                retVal = doMerger(actionVO);

        }
        return retVal;
    }

    private boolean doMerger(CompanyActionVO actionVO) {
        /**
         * Save company action
         * Identify all partial holdings
         * Detach partial holdings and partial sale; make all of them full holdings
         * For all full holdings, Identify the ratio, adjust purchase quantity, update stock code to parent entity
         */
        new StockMasterBO().insertMissingStockCodes(new HashSet<String>(Arrays.asList(actionVO.getParentEntity())));

        actionVO.setId(getDAO().insertCompanyAction(actionVO));
        float newStockPerExistingShare = actionVO.getDsbValue() / actionVO.getBase();

        PMDate previousToExDate = actionVO.getExDate().previous();

        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        ConsolidatedTransactionDetails ctd = new ConsolidatedTransactionDetails(actionVO, previousToExDate, transactionDAO).invoke();
        NewQtyHelper newQtyHelper = new NewQtyHelper(newStockPerExistingShare, ctd.getBuyTransactions(), ctd.getBuyIDHoldingQtyOnRecordDate());

        for (TransactionVO buyTransaction : ctd.getBuyTransactions()) {
            Float holdingQty = ctd.getBuyIDHoldingQtyOnRecordDate().get(buyTransaction.getId());
            if (holdingQty == null) {
                continue;
            }

            Float newQtyForTransaction = newQtyHelper.findNewQty(buyTransaction.getTradingAc(), holdingQty);

            if (holdingQty != buyTransaction.getQty()) {
                TransactionVO holdingTransaction = createSeparateBuyTransactionForHoldingAndUpdateOrginalToRepresentSoldQty(buyTransaction, holdingQty);
                performMerger(actionVO, newQtyForTransaction, holdingTransaction);
                persistNewTransactionWithCompanyActionAndSoldOutTransaction(actionVO, previousToExDate, transactionDAO, ctd, buyTransaction, holdingTransaction);
            } else {
                performMerger(actionVO, newQtyForTransaction, buyTransaction);
                persistTransactionWithCompanyAction(actionVO, transactionDAO, buyTransaction);
            }
        }
        return true;
    }

    private void persistNewTransactionWithCompanyActionAndSoldOutTransaction(CompanyActionVO actionVO, PMDate previousToExDate,
                                                                             ITransactionDAO transactionDAO,
                                                                             ConsolidatedTransactionDetails ctd,
                                                                             TransactionVO buyTransaction, TransactionVO holdingTransaction) {

        try {
            DAOManager.getDaoManager().startTransaction();
            int newBuyId = transactionDAO.insertTransaction(holdingTransaction);
            updateWithNewBuyIDsForTransactionDoneAfterRecordDate(previousToExDate, transactionDAO, ctd, buyTransaction.getId(), newBuyId);
            transactionDAO.updateTransaction(buyTransaction);
            DAOManager.getCompanyActionDAO().insertActionMapping(actionVO.getAction(), new ActionMapping(actionVO.getId(), newBuyId));
            DAOManager.getDaoManager().commitTransaction();
        } finally {
            DAOManager.getDaoManager().endTransaction();
        }
    }

    private void persistTransactionWithCompanyAction(CompanyActionVO actionVO, ITransactionDAO transactionDAO, TransactionVO buyTransaction) {
        try {
            DAOManager.getDaoManager().startTransaction();
            transactionDAO.updateTransaction(buyTransaction);
            DAOManager.getCompanyActionDAO().insertActionMapping(actionVO.getAction(), new ActionMapping(actionVO.getId(), buyTransaction.getId()));
            DAOManager.getDaoManager().commitTransaction();
        } finally {
            DAOManager.getDaoManager().endTransaction();
        }
    }

    private void performMerger(CompanyActionVO actionVO, float newQtyForTransaction, TransactionVO transactionVO) {
        transactionVO.setPrice(transactionVO.getPrice() * transactionVO.getQty() / newQtyForTransaction);
        transactionVO.setQty(newQtyForTransaction);
        transactionVO.setStockCode(actionVO.getParentEntity());
    }

    private void updateWithNewBuyIDsForTransactionDoneAfterRecordDate(PMDate previousToExDate, ITransactionDAO transactionDAO,
                                                                      ConsolidatedTransactionDetails ctd,
                                                                      Integer oldBuyId, int newBuyId) {
        Map<Integer, TradeVO> tradeMapOnID = ctd.getTradeMapOnID();
        Map<Integer, TransactionMapping> mapTransactionMapping = ctd.getMapTransactionMapping();
        List<Integer> tradeIDs = ctd.getBuyIDTradeList().get(oldBuyId);
        if (tradeIDs != null) {
            for (Integer tradeID : tradeIDs) {
                TransactionMapping mapping = mapTransactionMapping.get(tradeID);
                TradeVO tradeVO = tradeMapOnID.get(mapping.getId());
                if (tradeVO.isHolding(previousToExDate)) {
                    mapping.setBuyId(newBuyId);
                    transactionDAO.updateTrade(mapping);
                }
            }
        }
    }

    private TransactionVO createSeparateBuyTransactionForHoldingAndUpdateOrginalToRepresentSoldQty(TransactionVO buyTransaction, Float holdingQty) {
        TransactionVO holdingTransaction = (TransactionVO) buyTransaction.clone();
        float actualQty = buyTransaction.getQty();
        float soldQty = buyTransaction.getQty() - holdingQty;
        buyTransaction.setQty(soldQty);
        buyTransaction.setBrokerage(buyTransaction.getBrokerage() / actualQty * soldQty);
        holdingTransaction.setQty(holdingQty);
        holdingTransaction.setBrokerage(holdingTransaction.getBrokerage() / actualQty * holdingQty);
        return holdingTransaction;
    }

    boolean isFutureAction(CompanyActionVO actionVO) {
        return actionVO.getExDate().after(new PMDate());
    }

    boolean saveActionInfo(CompanyActionVO actionVO) {
        getDAO().insertCompanyAction(actionVO);
        return true; //TODO refactor
    }

    boolean isDuplicateAction(CompanyActionVO actionVO) {
        for (CompanyActionVO logActionVO : getDAO().getCompanyAction(
                actionVO.getStockCode())) {
            if (logActionVO.equals(actionVO) || isEqualAfterNormalization(logActionVO, actionVO)) {
                return true;
            }
        }
        return false;
    }

    boolean isEqualAfterNormalization(CompanyActionVO logActionVO, CompanyActionVO actionVO) {
        if (actionVO.getAction() != COMPANY_ACTION_TYPE.Divident ||
                actionVO.isPercentageValue()) {
            return false;
        }
        CompanyActionVO clonedVO = actionVO.clone();
        doNormalization(clonedVO);
        return logActionVO.equals(clonedVO);
    }

    //

    void doNormalization(CompanyActionVO actionVO) {
        if (actionVO.getAction() == COMPANY_ACTION_TYPE.Divident) {
            String stockCode = actionVO.getStockCode();
            PMDate previousToExDate = actionVO.getExDate().previous();
            float faceValue = faceValueOn(stockCode, previousToExDate);
            actionVO.normalize(faceValue);
        }
    }

    float faceValueOn(String stockCode, PMDate date) {
        StockVO stockVO = DAOManager.getStockDAO().getStock(stockCode);
        float faceValue = stockVO.getFaceValue();
        List<CompanyActionVO> list = DAOManager.getCompanyActionDAO().getCompanyAction(stockCode);
        for (CompanyActionVO actionVO : list) {
            if (actionVO.getAction() == COMPANY_ACTION_TYPE.Split && actionVO.getExDate().after(date)) {
                faceValue *= actionVO.getBase() / actionVO.getDsbValue();
            }
        }
        return faceValue;
    }

    //TODO clear split base and DSB value confusion
    boolean doSplit(CompanyActionVO actionVO) {
        actionVO.setId(DAOManager.getCompanyActionDAO().insertCompanyAction(actionVO));
        float splitPerShare = actionVO.getBase() / actionVO.getDsbValue();
        PMDate previousToExDate = actionVO.getExDate().previous();

        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        ConsolidatedTransactionDetails ctd = new ConsolidatedTransactionDetails(actionVO, previousToExDate, transactionDAO).invoke();

        for (TransactionVO buyTransaction : ctd.getBuyTransactions()) {
            Float holdingQty = ctd.getBuyIDHoldingQtyOnRecordDate().get(buyTransaction.getId());
            if (holdingQty != null) {
                if (holdingQty != buyTransaction.getQty()) {
                    TransactionVO holdingTransaction = createSeparateBuyTransactionForHoldingAndUpdateOrginalToRepresentSoldQty(buyTransaction, holdingQty);
                    performSplit(holdingTransaction, splitPerShare);
                    try {
                        DAOManager.getDaoManager().startTransaction();
                        int newBuyId = transactionDAO.insertTransaction(holdingTransaction);
                        updateWithNewBuyIDsForTransactionDoneAfterRecordDate(previousToExDate, transactionDAO, ctd, buyTransaction.getId(), newBuyId);

                        transactionDAO.updateTransaction(buyTransaction);
                        DAOManager.getCompanyActionDAO().insertActionMapping(actionVO.getAction(), new ActionMapping(actionVO.getId(), newBuyId));
                        DAOManager.getDaoManager().commitTransaction();
                    } finally {
                        DAOManager.getDaoManager().endTransaction();
                    }
                } else {
                    performSplit(buyTransaction, splitPerShare);
                    persistTransactionWithCompanyAction(actionVO, transactionDAO, buyTransaction);
                }
            }
        }

        updateStockMaster(actionVO);
        DAOManager.getQuoteDAO().updateAdjustedClose(actionVO.getStockCode(), actionVO.getExDate(), actionVO.getDsbValue() / actionVO.getBase());
        return true; //TODO refactor this

    }


    private void updateStockMaster(CompanyActionVO actionVO) {
        IStockDAO stockDAO = DAOManager.getStockDAO();
        StockVO stockVO = stockDAO.getStock(actionVO.getStockCode());
        if (stockVO.getFaceValue() == actionVO.getBase()) {
            stockVO.setFaceValue((short) actionVO.getDsbValue()); //TODO potential issue
            stockDAO.updateStock(stockVO);
        } else {
            //update all dividents
            getDAO().resetDividents(stockVO);
        }
    }

    boolean doDemerger(CompanyActionVO actionVO) {
        if (actionVO.getDsbValue() != actionVO.getBase()) {
            throw new RuntimeException("For demerger Base and DSB should be equal");
        }
        checkDemergedEntityHasOldEntity(actionVO);
        insertNewEntityCodes(actionVO);

        actionVO.setId(DAOManager.getCompanyActionDAO().insertCompanyAction(actionVO));
        PMDate previousToExDate = actionVO.getExDate().previous();
        ITransactionDAO transactionDAO = DAOManager.getTransactionDAO();
        List<TransactionVO> buyTransactions = transactionDAO.getTransactionList(null, null, AppConst.TRADINGTYPE.Buy);
        List<TradeVO> tradeDetails = transactionDAO.getTradeDetails(null, null, actionVO.getStockCode(), false);
        Map<Integer, TransactionMapping> mapTransactionMapping = transactionDAO.getTransactionMapping();
        Map<Integer, Float> buyIDHoldingQtyOnRecordDate = new HashMap<Integer, Float>();
        Map<Integer, List<Integer>> buyIDTradeList = new HashMap<Integer, List<Integer>>();
        groupByBuyID(tradeDetails, previousToExDate, buyIDHoldingQtyOnRecordDate, buyIDTradeList);
        ICompanyActionDAO actionDAO = DAOManager.getCompanyActionDAO();

        for (TransactionVO buyTransaction : buyTransactions) {
            Float holdingQty = buyIDHoldingQtyOnRecordDate.get(buyTransaction.getId());
            if (holdingQty != null) {
                if (holdingQty != buyTransaction.getQty()) {
                    TransactionVO holdingTransaction = createSeparateBuyTransactionForHoldingAndUpdateOrginalToRepresentSoldQty(buyTransaction, holdingQty);
                    List<TransactionVO> childEntity = creareBuyTransactionForNewEntity(holdingTransaction, actionVO);
                    adjustBookValueOfBaseCompany(holdingTransaction, actionVO);
                    try {
                        DAOManager.getDaoManager().startTransaction();
                        transactionDAO.updateTransaction(buyTransaction);
                        int newBuyId = transactionDAO.insertTransaction(holdingTransaction);
                        List<Integer> tradeIDs = buyIDTradeList.get(buyTransaction.getId());
                        if (tradeIDs != null) {
                            for (Integer tradeID : tradeIDs) {
                                TransactionMapping mapping = mapTransactionMapping.get(tradeID);
                                mapping.setBuyId(newBuyId);
                                transactionDAO.updateTrade(mapping);
                            }
                        }
                        actionDAO.insertActionMapping(actionVO.getAction(), new ActionMapping(actionVO.getId(), newBuyId));
                        for (TransactionVO transactionVO : childEntity) {
//                            stockDAO.insertStock(new StockVO(transactionVO.getStock()));
                            int childCompanyId = transactionDAO.insertTransaction(transactionVO);
//                            actionDAO.insertActionMapping(actionVO.getAction(), new ActionMapping(actionVO.getId(), childCompanyId));
                        }
                        DAOManager.getDaoManager().commitTransaction();
                    } finally {
                        DAOManager.getDaoManager().endTransaction();
                    }
                } else {
                    List<TransactionVO> childEntity = creareBuyTransactionForNewEntity(buyTransaction, actionVO);
                    adjustBookValueOfBaseCompany(buyTransaction, actionVO);
                    try {
                        DAOManager.getDaoManager().startTransaction();
                        transactionDAO.updateTransaction(buyTransaction);
                        for (TransactionVO transactionVO : childEntity) {
//                            stockDAO.insertStock(new StockVO(transactionVO.getStock()));
                            int childCompanyId = transactionDAO.insertTransaction(transactionVO);
                        }
                        DAOManager.getDaoManager().commitTransaction();
                    } finally {
                        DAOManager.getDaoManager().endTransaction();
                    }
                }
            }
        }
        return true; //TODO refactor this

    }

    void groupByBuyID(List<TradeVO> tradeDetails, PMDate previousToExDate, Map<Integer, Float> buyIDHoldingQtyOnRecordDate, Map<Integer, List<Integer>> buyIDTradeList) {
        for (TradeVO tradeVO : tradeDetails) {
            if (tradeVO.isHolding(previousToExDate)) {
                Float qty = buyIDHoldingQtyOnRecordDate.get(tradeVO.getBuyId());
                qty = ((qty == null) ? 0 : qty) + tradeVO.getQty();
                buyIDHoldingQtyOnRecordDate.put(tradeVO.getBuyId(), qty);
                if (tradeVO.getId() != 0) {
                    List<Integer> tradeList = buyIDTradeList.get(tradeVO.getBuyId());
                    if (tradeList == null) {
                        tradeList = new Vector<Integer>();
                    }
                    tradeList.add(tradeVO.getId());
                    buyIDTradeList.put(tradeVO.getBuyId(), tradeList);
                }
            }
        }
    }

    private void insertNewEntityCodes(CompanyActionVO actionVO) {
        Set stockList = new HashSet();
        for (DemergerVO demergerVO : actionVO.getDemergerData()) {
            stockList.add(demergerVO.getNewStockCode());
        }
        new StockMasterBO().insertMissingStockCodes(stockList);
    }

    private List<TransactionVO> creareBuyTransactionForNewEntity(TransactionVO holdingTransaction, CompanyActionVO actionVO) {
        List<TransactionVO> newTransList = new Vector<TransactionVO>();
        for (DemergerVO demergerVO : actionVO.getDemergerData()) {
            if (!demergerVO.getNewStockCode().equals(actionVO.getStockCode())) {
                TransactionVO newTrans = (TransactionVO) holdingTransaction.clone();
                float factor = demergerVO.getBookValueRatio() / 100f;
                newTrans.setPrice(newTrans.getPrice() * factor);
                newTrans.setBrokerage(newTrans.getBrokerage() * factor);
                newTrans.setStockCode(demergerVO.getNewStockCode());
                newTransList.add(newTrans);
            }
        }
        return newTransList;
    }

    private void adjustBookValueOfBaseCompany(TransactionVO holdingTransaction, CompanyActionVO actionVO) {
        for (DemergerVO demergerVO : actionVO.getDemergerData()) {
            if (demergerVO.getNewStockCode().equals(holdingTransaction.getStockCode())) {
                float factor = demergerVO.getBookValueRatio() / 100f;
                holdingTransaction.setPrice(holdingTransaction.getPrice() * factor);
                holdingTransaction.setBrokerage(holdingTransaction.getBrokerage() * factor);
            }
        }
    }

    void checkDemergedEntityHasOldEntity(CompanyActionVO actionVO) {
        for (DemergerVO demergerVO : actionVO.getDemergerData()) {
            if (demergerVO.getNewStockCode().equals(actionVO.getStockCode())) {
                return;
            }
        }
        throw new RuntimeException("Demerged entity should has old entity in the list");
    }

    private Map<Integer, TransactionMapping> getMapping(List<TransactionMapping> transactionMappingList) {
        Map<Integer, TransactionMapping> transactionMap = new HashMap<Integer, TransactionMapping>();
        for (TransactionMapping mapping : transactionMappingList) {
            transactionMap.put(mapping.getId(), mapping);
        }
        return transactionMap;
    }

    void performSplit(TransactionVO transactionVO, float splitPerShare) {
        transactionVO.setQty(transactionVO.getQty() * splitPerShare);
        transactionVO.setPrice(transactionVO.getPrice() / splitPerShare);
    }

    Hashtable<String, Vector<TradeVO>> performDemerger(
            List<TradeVO> transDetails, CompanyActionVO actionVO) {
        boolean actionPerformed = false;
        PMDate previousToExDate = actionVO.getExDate().previous();
        Hashtable<String, Vector<TradeVO>> demergedDetails = new Hashtable<String, Vector<TradeVO>>();
        boolean isOldEntityPresent = false;
        for (DemergerVO demergerVO : actionVO.getDemergerData()) {
            if (!demergerVO.isComplete()) {
                continue;
            }
            demergedDetails.put(demergerVO.getNewStockCode(),
                    new Vector<TradeVO>());
            if (!isOldEntityPresent
                    && demergerVO.getNewStockCode().equals(
                    actionVO.getStockCode())) {
                isOldEntityPresent = true;
            }
        }
        if (!isOldEntityPresent) {
            demergedDetails.put(actionVO.getStockCode(), new Vector<TradeVO>());
        }

        for (TradeVO tradeVO : transDetails) {
            if (tradeVO.isHolding(previousToExDate)) {
                for (DemergerVO demergerVO : actionVO.getDemergerData()) {
                    if (!demergerVO.isComplete()) {
                        continue;
                    }
                    actionPerformed = true;
                    TradeVO newTradeVO = (TradeVO) tradeVO.clone();
                    newTradeVO.setStock(demergerVO.getNewStockCode());
                    BigDecimal per = new BigDecimal(demergerVO
                            .getBookValueRatio() / 100f);
                    BigDecimal newBrok = new BigDecimal(tradeVO.getBrokerage())
                            .multiply(per)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                    newTradeVO.setBrokerage(newBrok.floatValue());
                    BigDecimal newPP = new BigDecimal(tradeVO
                            .getPurchasePrice()).multiply(per).setScale(2,
                            BigDecimal.ROUND_HALF_UP);
                    newTradeVO.setPurchasePrice(newPP.floatValue());
                    demergedDetails.get(newTradeVO.getStockCode()).add(
                            newTradeVO);
                }
            } else {
                demergedDetails.get(tradeVO.getStockCode()).add(tradeVO);
            }
        }
        if (!actionPerformed) {
            return null;
        }

        Hashtable<String, Vector<TradeVO>> retVal = new Hashtable<String, Vector<TradeVO>>();
        for (String stockCode : demergedDetails.keySet()) {
            if (!stockCode.equals(actionVO.getStockCode())
                    && demergedDetails.get(stockCode).isEmpty()) {
                continue;
            }
            retVal.put(stockCode, demergedDetails.get(stockCode));
        }

        return retVal;
    }

    StockMasterBO getStockMasterBO() {
        return new StockMasterBO();
    }

    List<TradingAccountVO> getTradingAcVOList() {
        return DAOManager.getAccountDAO().getTradingAccList();
    }

    List<PortfolioDetailsVO> getPortfolioList() {
        return DAOManager.getAccountDAO().getPorfolioList();
    }

    boolean doBonus(CompanyActionVO actionVO) {
        actionVO.setId(getDAO().insertCompanyAction(actionVO));
        if (actionVO.getId() == -1) {
            return false;
        }
        List<PortfolioDetailsVO> portfolioList = getPortfolioList();
        List<TradingAccountVO> tradingAcList = getTradingAcVOList();
        PMDate previousToExDate = actionVO.getExDate().previous();
        boolean retVal = true;
        for (Account tradingAc : tradingAcList) {

            float totalQtyInTradingAc = getHoldingQtyInTradingAc(actionVO
                    .getStockCode(), portfolioList, previousToExDate, tradingAc);
            if (totalQtyInTradingAc == 0) {
                continue;
            }

            actionApplied = true;
            float bonusForTradingAc = (float) floor(actionVO.getDsbValue()
                    / actionVO.getBase() * totalQtyInTradingAc);
            float bonusPerShare = bonusForTradingAc / totalQtyInTradingAc;

            for (PortfolioDetailsVO portfolioVO : portfolioList) {

                float holdingQty = getHoldingQty(actionVO.getStockCode(),
                        previousToExDate, tradingAc, portfolioVO);

                float bonusNotRounded = holdingQty * bonusPerShare;
                float bonusQty = 0f;
                if (abs(IEEEremainder(bonusNotRounded, 1)) == 0.5) {
                    // If both r equal & bonus is not even then First will get
                    // extra
                    bonusQty = round(bonusNotRounded);
                } else {
                    bonusQty = round(bonusNotRounded
                            / actionVO.getDsbValue())
                            * actionVO.getDsbValue();
                }

                if (bonusQty > bonusForTradingAc) {
                    bonusQty = bonusForTradingAc;
                }
                bonusForTradingAc -= bonusQty;
                retVal = retVal
                        && performBonus(actionVO, previousToExDate, tradingAc,
                        bonusQty, portfolioVO);
            }
        }
        DAOManager.getQuoteDAO().updateAdjustedClose(actionVO.getStockCode(), actionVO.getExDate(), actionVO.getBase() / (actionVO.getDsbValue() + actionVO.getBase()));
        return retVal;
    }

    boolean performBonus(CompanyActionVO actionVO, PMDate previousToExDate,
                         Account tradingAc, float bonusQty,
                         PortfolioDetailsVO portfolioVO) {
        if (bonusQty > 0) {
            TransactionVO transactionVO = new TransactionVO(actionVO.getExDate(), actionVO.getStockCode(), AppConst.TRADINGTYPE.Buy, bonusQty, 0f, 0f,
                    portfolioVO.getName(), tradingAc.getName(), false);
            try {
                DAOManager.getDaoManager().startTransaction();
                int transactionId = DAOManager.getTransactionDAO().insertTransaction(transactionVO);
                getDAO().insertActionMapping(actionVO.getAction(), new ActionMapping(actionVO.getId(), transactionId));
                DAOManager.getDaoManager().commitTransaction();
            } finally {
                DAOManager.getDaoManager().endTransaction();
            }
        }
        return true;
    }

    float getHoldingQtyInTradingAc(String stockCode,
                                   List<PortfolioDetailsVO> portfolioList, PMDate previousToExDate,
                                   Account tradingAc) {
        float totalQtyInTradingAc = 0f;
        for (PortfolioDetailsVO portfolioVO : portfolioList) {
            totalQtyInTradingAc += getHoldingQty(stockCode, previousToExDate,
                    tradingAc, portfolioVO);
        }
        return totalQtyInTradingAc;
    }

    float getHoldingQty(String stockCode, PMDate previousToExDate,
                        Account tradingAc, PortfolioDetailsVO portfolioVO) {
        float total = 0;
        List<TradeVO> transactionDetails = getTransactionDetails(stockCode,
                tradingAc, portfolioVO);
        for (TradeVO tradeVO : transactionDetails) {
            if (tradeVO.isHolding(previousToExDate)) {
                total += tradeVO.getQty();
            }
        }
        return total;
    }

    List<TradeVO> getTransactionDetails(String stockCode,
                                        Account tradingAc, PortfolioDetailsVO portfolioVO) {
        return DAOManager.getTransactionDAO().getTradeDetails(tradingAc.getName(), portfolioVO.getName(), stockCode, false);
    }

    void applyCompanyAction(String stockCode,
                            Vector<CorporateResultsVO> financialData) {
        List<CompanyActionVO> actionInfo = getDAO().getCompanyAction(stockCode);
        for (CompanyActionVO actionVO : actionInfo) {
            if (actionVO.getAction() == COMPANY_ACTION_TYPE.Divident) {
                continue;
            }
            for (CorporateResultsVO resultsVO : financialData) {
                if (resultsVO.getEndDate().before(actionVO.getExDate())) {
                    resultsVO.applyPriceFactor(actionVO.getPriceFactor());
                }
            }
        }

    }

    ICompanyActionDAO getDAO() {
        return DAOManager.getCompanyActionDAO();
    }

    public List<EquityQuote> applyCompanyAction(String stockCode, List<EquityQuote> quoteVOs) {
        List<CompanyActionVO> actionInfo = getDAO().getCompanyAction(stockCode);
        for (CompanyActionVO actionVO : actionInfo) {
            if (actionVO.getAction() == COMPANY_ACTION_TYPE.Divident) {
                continue;
            }

            float priceFactor = actionVO.getPriceFactor();
            for (EquityQuote quoteVO : quoteVOs) {
                if (quoteVO.before(actionVO.getExDate())) {
                    quoteVO.applyPriceFactor(priceFactor);
                } else if (quoteVO.dateEquals(actionVO.getExDate())) {
                    quoteVO.applyPreviousDayPriceFactor(priceFactor);
                }
            }
        }
        return quoteVOs;
    }

    public Vector<CorpResultVO> getFinResult(String stockCode,
                                             CORP_RESULT_TIMELINE corp_result_timeline) {
        List<CorpResultVO> finData = getFinData(stockCode);
        Vector<CorpResultVO> retVal = new Vector<CorpResultVO>();
        for (CorpResultVO resultVO : finData) {
            if (resultVO.getTimeline() == corp_result_timeline) {
                sortElimateDuplicateAdd(retVal, resultVO);

            }
        }

        return normalizeResultsToCurrentEquityBase(retVal);
    }

    public List<CorpResultVO> getFinData(String stockCode) {
        StockVO stockVO = DAOManager.getStockDAO().getStock(stockCode);
        return DAOManager.companyResultDAO().get(stockVO.getId());
    }

    void sortElimateDuplicateAdd(Vector<CorpResultVO> retVal,
                                 CorpResultVO newResultVO) {
        boolean isAdded = false;
        for (int i = 0; i < retVal.size(); i++) {
            CorpResultVO resultVO = retVal.elementAt(i);
            if (resultVO.getStartDate().after(newResultVO.getStartDate())) {
                retVal.add(i, newResultVO);
                isAdded = true;
                break;
            } else if (resultVO.getStartDate().equals(
                    newResultVO.getStartDate())) {
                if (!resultVO.isConsolidatedFlag()
                        && newResultVO.isConsolidatedFlag()) {
                    retVal.remove(i);
                    retVal.add(i, newResultVO);
                    isAdded = true;
                    break;
                } else {
                    isAdded = true;
                    break;
                }
            }
        }
        if (!isAdded) {
            retVal.add(newResultVO);
        }
    }

    Vector<CorpResultVO> normalizeResultsToCurrentEquityBase(
            Vector<CorpResultVO> processedFinData) {

        if (processedFinData.isEmpty()) {
            return processedFinData;
        }

        CorpResultVO lastResultVO = null;
        for (CorpResultVO resultVO : processedFinData) {
            if (lastResultVO == null) {
                lastResultVO = resultVO;
            } else if (lastResultVO.getEndDate().before(resultVO.getEndDate())) {
                lastResultVO = resultVO;
            }
        }
        assert lastResultVO != null;
        float lastFaceValue = lastResultVO.getFaceValue();
        float lastEquityBase = lastResultVO.getPaidUpEquityShareCapital();

        for (CorpResultVO resultVO : processedFinData) {
            float faceValueNorm = lastFaceValue / resultVO.getFaceValue();
            float equityBaseNorm = resultVO.getPaidUpEquityShareCapital()
                    / lastEquityBase;
            float totNorm = faceValueNorm * equityBaseNorm;
            resultVO.setEps(resultVO.getEps() * totNorm);
        }
        return processedFinData;
    }

    public Vector<CompanyPerfVO> getCompanyPerformance(String stockCode,
                                                       CORP_RESULT_TIMELINE timeline) {
        Vector<CompanyPerfVO> retVal = new Vector<CompanyPerfVO>();
        Vector<CorpResultVO> finResult = getFinResult(stockCode, timeline);

        if (finResult.isEmpty()) {
            return retVal;
        }
        List<EquityQuote> quoteVOs = DAOManager.getQuoteDAO().getQuotes(stockCode, finResult.firstElement()
                .getStartDate(), finResult.lastElement().getEndDate());
        applyCompanyAction(stockCode, quoteVOs);
        for (CorpResultVO resultVO : finResult) {
            retVal.add(new CompanyPerfVO(resultVO));
        }

        if (quoteVOs.isEmpty()) {
            return retVal;
        }
        int perfIndex = 0;
        EquityQuote quoteVO = new EquityQuote(stockCode);
        quoteVO.setLow(1000000);
        quoteVO.setPrevClose(getAdjustedPrevClose(quoteVOs.get(0)));
        for (int i = 0; i < quoteVOs.size() && perfIndex < retVal.size(); i++) {
            if (quoteVOs.get(i).getDate().after(retVal.get(perfIndex).getEndDate())) {
                retVal.get(perfIndex).setQuote(quoteVO);
                quoteVO = new EquityQuote(stockCode);
                quoteVO.setPrevClose(getAdjustedPrevClose(quoteVOs.get(i)));
                quoteVO.setLow(-1);
                perfIndex++;
            }
            if (quoteVOs.get(i).getLow() < quoteVO.getLow()) {
                quoteVO.setLow(quoteVOs.get(i).getLow());
            }
            if (quoteVOs.get(i).getHigh() > quoteVO.getHigh()) {
                quoteVO.setHigh(quoteVOs.get(i).getHigh());
            }
            if (quoteVOs.get(i).getOpen() > quoteVO.getOpen()) {
                quoteVO.setOpen(quoteVOs.get(i).getOpen());
            }
            if (quoteVOs.get(i).getLastPrice() > quoteVO.getLastPrice()) {
                quoteVO.setLastPrice(quoteVOs.get(i).getLastPrice());
            }
        }
        if (perfIndex < retVal.size()) {
            retVal.get(perfIndex).setQuote(quoteVO);
        }

        for (int i = 1; i < retVal.size(); i++) {
            float prevEps = retVal.get(i - 1).getEps();
            float currEps = retVal.get(i).getEps();
            retVal.get(i).setEpsGrowth((currEps - prevEps) / prevEps * 100);
        }
        return retVal;
    }

    private float getAdjustedPrevClose(EquityQuote quoteVO) {
        float prevClose = quoteVO.getPrevClose();
        if (prevClose < (quoteVO.getOpen() * 0.8)) {
            prevClose = quoteVO.getOpen();
        }
        return prevClose;
    }

    public void loadFinanceDetails(String stockCode,
                                   List<EODDetailsVO> movAvgData) {
        Vector<CorpResultVO> processedFinData = getFinResult(stockCode,
                CORP_RESULT_TIMELINE.Quaterly);
        Hashtable<PMDate, CorpResultVO> finData = new Hashtable<PMDate, CorpResultVO>();
        for (CorpResultVO resultVO : processedFinData) {
            finData.put(resultVO.getStartDate(), resultVO);
        }
        float lastEPS = 0;
        for (EODDetailsVO avgVO : movAvgData) {
            PMDate stDate = avgVO.getDate().quaterStartDate();
            CorpResultVO corpResultVO = finData.get(stDate);
            if (corpResultVO != null) {
                lastEPS = corpResultVO.getEps() * 4;
                avgVO.setEps(lastEPS);
                avgVO.setPe(avgVO.getClose() / avgVO.getEps());
                // converting quaterly to annual
            } else if (lastEPS != 0) {
                avgVO.setPe(avgVO.getClose() / lastEPS);
            }
        }
    }

    public boolean isActionApplied() {
        return actionApplied;
    }

    public Hashtable<PMDate, Vector<CompanyActionVO>> getConsolidatedActionDataNormalizedToCurrentPrice() {
        Vector<StockVO> stockVOList = getStockMasterBO().getStockList(false);
        Hashtable<String, StockVO> htStockVOs = new Hashtable<String, StockVO>(stockVOList.size());
        for (StockVO stockVO : stockVOList) {
            htStockVOs.put(stockVO.getStockCode(), stockVO);
        }
        Hashtable<PMDate, Vector<CompanyActionVO>> consolidatedActionData = getOldDAO().getConsolidatedActionData();
        Vector<String> actionStockList = new Vector<String>();
        PMDate today = new PMDate();
        for (Vector<CompanyActionVO> actionVOs : consolidatedActionData.values()) {
            for (CompanyActionVO actionVO : actionVOs) {
                if (!(actionVO.getAction() == COMPANY_ACTION_TYPE.Divident) || actionVO.getExDate().before(today)) {
                    continue;
                }
                actionStockList.add(actionVO.getStockCode());
            }
        }
        Hashtable<String, EquityQuote> htLiveQuotes = new Hashtable<String, EquityQuote>();
        EquityQuote[] liveQuote = QuoteManager.getLiveQuote(actionStockList.toArray(new String[actionStockList.size()]));
        for (EquityQuote quoteVO : liveQuote) {
            if (quoteVO != null) {
                htLiveQuotes.put(quoteVO.getStockCode(), quoteVO);
            }
        }

        for (Vector<CompanyActionVO> actionVOs : consolidatedActionData.values()) {
            for (CompanyActionVO actionVO : actionVOs) {
                if (!(actionVO.getAction() == COMPANY_ACTION_TYPE.Divident)) {
                    continue;
                }
                float divident = 0;
                if (actionVO.isPercentageValue()) {
                    StockVO stockVO = htStockVOs.get(actionVO.getStockCode());
                    if (stockVO == null) {
                        continue;
                    }
                    divident = stockVO.getFaceValue() / 100 * actionVO.getDsbValue();
                } else {
                    divident = actionVO.getDsbValue();
                }
                EquityQuote quoteVO = htLiveQuotes.get(actionVO.getStockCode());
                if (quoteVO != null) {
                    float valueAtCurrPrice = divident / quoteVO.getLastPrice() * 100f;
                    actionVO.setValueAtCurrentPrice(valueAtCurrPrice);
                }
            }
        }

        return consolidatedActionData;
    }

    private CompanyDAO getOldDAO() {
        return new CompanyDAO();
    }

    public void normalizeDivident(String stockCode) {
        StockVO stockVO = DAOManager.getStockDAO().getStock(stockCode);
        normalizeDivident(stockVO);
    }

    private void normalizeDivident(StockVO stockVO) {
        ICompanyActionDAO dao = getDAO();
        List<CompanyActionVO> actionList = dao.getCompanyAction(stockVO.getStockCode());
        if (stockVO.getFaceValue() == 0) {
            logger.warn("Not able to do divient normalization for " + stockVO.getStockCode());
            return;
        }
        for (CompanyActionVO actionVO : actionList) {
            float faceValue = getFaceValueAlteredToSplits(stockVO, actionList, actionVO.getExDate().previous());
            if (actionVO.normalize(faceValue)) {
                dao.updateCompanyAction(actionVO);
            }
        }
    }

    private float getFaceValueAlteredToSplits(StockVO stockVO, List<CompanyActionVO> actionList, PMDate exDate) {
        float faceValue = stockVO.getFaceValue();
        for (CompanyActionVO actionVO : actionList) {
            if (actionVO.getAction() == COMPANY_ACTION_TYPE.Split && actionVO.getExDate().after(exDate)) {
                faceValue *= actionVO.getBase() / actionVO.getDsbValue();
            }
        }
        return faceValue;
    }

    public void normalizeDividents() {
        List<StockVO> stockVOs = DAOManager.getStockDAO().getStockList(false);
        for (StockVO stockVO : stockVOs) {
            normalizeDivident(stockVO);
        }
    }

    public Map<String, List<CorpResultVO>> getFinResult(String[] stockCodes) {
        Map<String, List<CorpResultVO>> retVal = new HashMap<String, List<CorpResultVO>>();
        for (String stockCode : stockCodes) {
            retVal.put(stockCode, getFinData(stockCode));
        }
        return retVal;
    }

    class ConsolidatedTransactionDetails {
        private CompanyActionVO actionVO;
        private PMDate previousToExDate;
        private ITransactionDAO transactionDAO;
        private List<TransactionVO> buyTransactions;
        private Map<Integer, TradeVO> tradeMapOnID;
        private Map<Integer, TransactionMapping> mapTransactionMapping;
        private Map<Integer, Float> buyIDHoldingQtyOnRecordDate;
        private Map<Integer, List<Integer>> buyIDTradeList;

        public ConsolidatedTransactionDetails(CompanyActionVO actionVO, PMDate previousToExDate, ITransactionDAO transactionDAO) {
            this.actionVO = actionVO;
            this.previousToExDate = previousToExDate;
            this.transactionDAO = transactionDAO;
        }

        public List<TransactionVO> getBuyTransactions() {
            return buyTransactions;
        }

        public Map<Integer, TradeVO> getTradeMapOnID() {
            return tradeMapOnID;
        }

        public Map<Integer, TransactionMapping> getMapTransactionMapping() {
            return mapTransactionMapping;
        }

        public Map<Integer, Float> getBuyIDHoldingQtyOnRecordDate() {
            return buyIDHoldingQtyOnRecordDate;
        }

        public Map<Integer, List<Integer>> getBuyIDTradeList() {
            return buyIDTradeList;
        }

        public ConsolidatedTransactionDetails invoke() {
            buyTransactions = transactionDAO.getTransactionList(null, null, actionVO.getStockCode(), AppConst.TRADINGTYPE.Buy);
            List<TradeVO> tradeDetails = transactionDAO.getTradeDetails(null, null, actionVO.getStockCode(), false);
            tradeMapOnID = getTradeMapOnID(tradeDetails);
            mapTransactionMapping = transactionDAO.getTransactionMapping();
            buyIDHoldingQtyOnRecordDate = new HashMap<Integer, Float>();
            buyIDTradeList = new HashMap<Integer, List<Integer>>();
            groupByBuyID(tradeDetails, previousToExDate, buyIDHoldingQtyOnRecordDate, buyIDTradeList);
            return this;
        }

        private Map<Integer, TradeVO> getTradeMapOnID(List<TradeVO> tradeDetails) {
            HashMap<Integer, TradeVO> tradeMap = new HashMap<Integer, TradeVO>();
            for (TradeVO tradeDetail : tradeDetails) {
                tradeMap.put(tradeDetail.getId(), tradeDetail);
            }
            return tradeMap;
        }

    }
}

class NewQtyHelper {

    HashMap<String, NewQtyVO> qtyByTradingAccount = new HashMap<String, NewQtyVO>();
    private float newStockPerExistingShare;

    public NewQtyHelper(float newStockPerExistingShare, List<TransactionVO> buyTransactions, Map<Integer, Float> buyIDHoldingQtyOnRecordDate) {
        this.newStockPerExistingShare = newStockPerExistingShare;
        init(newStockPerExistingShare, buyTransactions, buyIDHoldingQtyOnRecordDate);
    }

    public Float findNewQty(String tradingAc, Float holdingQty) {
        return qtyByTradingAccount.get(tradingAc).allocateNewQty(holdingQty);
    }

    private void init(float newStockPerExistingShare, List<TransactionVO> buyTransactions,
                      Map<Integer, Float> buyIDHoldingQtyOnRecordDate) {

        for (TransactionVO buyTransaction : buyTransactions) {
            Float holdingQty = buyIDHoldingQtyOnRecordDate.get(buyTransaction.getId());
            if (holdingQty != null && holdingQty > 0) {
                NewQtyVO newQtyVO = qtyByTradingAccount.get(buyTransaction.getTradingAc());
                if (newQtyVO == null) {
                    newQtyVO = new NewQtyVO();
                }
                newQtyVO.addHolding(holdingQty);
                qtyByTradingAccount.put(buyTransaction.getTradingAc(), newQtyVO);
            }
        }

        for (NewQtyVO newQtyVO : qtyByTradingAccount.values()) {
            newQtyVO.computeNewQty(newStockPerExistingShare);
        }
    }

    private class NewQtyVO {
        Float totalHoldingQty = 0f;
        Float totalNewQty = 0f;

        public void addHolding(Float holdingQty) {
            totalHoldingQty += holdingQty;
        }

        public void computeNewQty(float newStockPerExistingShare) {
            totalNewQty = (float) floor(totalHoldingQty * newStockPerExistingShare);
        }

        public Float allocateNewQty(Float holdingQty) {
            totalHoldingQty -= holdingQty;
            Float newQtyForTransaction = new BigDecimal(newStockPerExistingShare * holdingQty).round(MathContext.DECIMAL32).floatValue();
            if (newQtyForTransaction > totalNewQty || totalHoldingQty == 0) {
                newQtyForTransaction = totalNewQty;
            }
            totalNewQty -= newQtyForTransaction;
            return newQtyForTransaction;
        }
    }
}
