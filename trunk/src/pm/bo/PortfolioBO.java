/*
 * Created on Oct 13, 2004
 *
 */
package pm.bo;

import org.apache.log4j.Logger;
import pm.action.QuoteManager;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IPortfolioDAO;
import pm.dao.ibatis.dao.ITransactionDAO;
import pm.util.AppConst.REPORT_TYPE;
import pm.util.DateIterator;
import pm.util.PMDate;
import pm.vo.*;

import java.util.*;

/**
 * @author thiyagu1
 */
public class PortfolioBO {

    private static final String DAYTRADING = "DayTrading";
    private static Logger logger = Logger.getLogger(PortfolioBO.class);

    /**
     * This method returns Stockwise consolidated Trade details with latest quote
     *
     * @param tradingAc
     * @param portfolio
     */
    public Vector<ConsolidatedTradeVO> getPortfolioView(String tradingAc, String portfolio, String reportType) {
        boolean AllTransFlag = reportType.equals(REPORT_TYPE.All.name());
        Hashtable<String, Vector<TradeVO>> overallTransDetails = getStockwiseTradeDetails(tradingAc, portfolio, false);

        Vector<ConsolidatedTradeVO> consolidatedList = groupTradeVOByStock(overallTransDetails, AllTransFlag);
        //getting stock code list
        String[] stockCodes = new String[consolidatedList.size()];
        for (int i = 0; i < consolidatedList.size(); i++) {
            stockCodes[i] = consolidatedList.elementAt(i).getStockCode();
        }
        //getting live quote
        logger.info(Calendar.getInstance().getTime());
        QuoteVO[] quoteVOs = QuoteManager.getLiveQuote(stockCodes);
        logger.info(Calendar.getInstance().getTime());
        //setting live quote to the report object
        for (int i = 0; i < consolidatedList.size(); i++) {
            consolidatedList.elementAt(i).setCurrQuote(quoteVOs[i]);
        }
//		consolidatedList = alterQuoteForRelianceStocks(consolidatedList);
        //setting day trading report to get consolidated report.
        if (AllTransFlag) {
            consolidatedList.add(getDayTradingReport(tradingAc, portfolio));
        }
        return consolidatedList;
    }

    private static Vector<ConsolidatedTradeVO> groupTradeVOByStock(Hashtable<String, Vector<TradeVO>> overallTransDetails, boolean flagIncNonHolding) {
        Vector<ConsolidatedTradeVO> consolidatedList = new Vector<ConsolidatedTradeVO>();
        TreeSet<String> treeSet = new TreeSet<String>(overallTransDetails.keySet());
        for (String stockCode : treeSet) {
            Vector transDetails = overallTransDetails.get(stockCode);
            float divident, totProfLoss, totCost, qty;
            divident = totProfLoss = totCost = qty = 0;
            for (int i = 0; i < transDetails.size(); i++) {
                TradeVO stockVO = (TradeVO) transDetails.elementAt(i);
                divident += stockVO.getDivident();
                if (stockVO.isHolding()) {
                    qty += stockVO.getQty();
                    totCost += stockVO.getPurchasePrice() * stockVO.getQty() + stockVO.getBrokerage();
                } else {
                    float profLoss = (stockVO.getSalePrice() - stockVO.getPurchasePrice())
                            * stockVO.getQty() - stockVO.getBrokerage();
                    totProfLoss += profLoss;
                }
            }
            if (flagIncNonHolding || qty != 0) {
                ConsolidatedTradeVO reportVO = new ConsolidatedTradeVO(stockCode, qty, totCost, divident, totProfLoss);
                consolidatedList.add(reportVO);
            }
        }
        return consolidatedList;
    }

    private ConsolidatedTradeVO getDayTradingReport(String tradingAc, String portfolio) {
        float totProfit = 0;
        Vector<TradeVO> dayTradeTransactionDetails = getDayTradeTransactionDetails(tradingAc, portfolio);
        for (TradeVO tradeVO : dayTradeTransactionDetails) {
            totProfit += tradeVO.getProfitLoss();
        }
        ConsolidatedTradeVO tradeVO = new ConsolidatedTradeVO(DAYTRADING, 0, 0, 0, totProfit);
        tradeVO.setCurrQuote(new QuoteVO(DAYTRADING));
        return tradeVO;
    }

    public Hashtable<String, Vector<TradeVO>> getStockwiseTradeDetails(String tradingAc, String portfolio, boolean dayTrading) {
        List<TradeVO> tradeDetails = getTradeDetails(tradingAc, portfolio, dayTrading);
        Hashtable<String, Vector<TradeVO>> tradeGroupByStockCode = new Hashtable<String, Vector<TradeVO>>();
        for (TradeVO tradeVO : tradeDetails) {
            Vector<TradeVO> tradeVOs = tradeGroupByStockCode.remove(tradeVO.getStockCode());
            if (tradeVOs == null) {
                tradeVOs = new Vector<TradeVO>();
            }
            tradeVOs.add(tradeVO);
            tradeGroupByStockCode.put(tradeVO.getStockCode(), tradeVOs);
        }
        return tradeGroupByStockCode;
    }

    public List<TradeVO> getTradeDetails(String tradingAc, String portfolio, boolean dayTrading) {
        return getTransactionDetails(null, tradingAc, portfolio, dayTrading);
    }

    private ITransactionDAO getDAO() {
        return DAOManager.getTransactionDAO();
    }

    public List<TradeVO> getTransactionDetails(String stockCode, String tradingAc, String portfolio, boolean dayTrading) {
        if (tradingAc.equals(REPORT_TYPE.All.toString())) {
            tradingAc = null;
        }
        if (portfolio.equals(REPORT_TYPE.All.toString())) {
            portfolio = null;
        }
        return getDAO().getTradeDetails(tradingAc, portfolio, stockCode, dayTrading);
    }

    public Vector<TradeVO> getDayTradeTransactionDetails(String tradingAc, String portfolio) { //TODO refactor this method
        List<TradeVO> tradeDetails = getTransactionDetails(null, tradingAc, portfolio, true);
        Vector<TradeVO> dayTradingDetails = new Vector<TradeVO>();
        for (TradeVO tradeVO : tradeDetails) {
            if (tradeVO.isDayTrading()) {
                dayTradingDetails.add(tradeVO);
            }
        }
        return dayTradingDetails;
    }

    public Vector<PerformanceVO> getPortfolioPerformance(String tradingAc, String portfolio, PMDate frmDate, PMDate toDate) {
        Hashtable<String, Vector<TradeVO>> overallTransDetails = getStockwiseTradeDetails(tradingAc, portfolio, true);
        return calcualteDailyPerformance(overallTransDetails, frmDate, toDate);
    }

    static Vector<PerformanceVO> calcualteDailyPerformance(Hashtable<String, Vector<TradeVO>> overallTransDetails, PMDate frmDate, PMDate toDate) {       //TODO F2DB
//	TODO THIS METHOD needs to be changed to process stockwise instead of datewise
        Set stkKeySet = overallTransDetails.keySet();
        Vector<PerformanceVO> vPerDetails = new Vector<PerformanceVO>();
        DateIterator iterator = new DateIterator(frmDate, toDate);
        PortfolioBO portfolioBO = new PortfolioBO();
        for (; iterator.hasNext();) {
//			Loading Historic Quote for the stocks which has transaction
            PMDate date = iterator.next();
            Hashtable<String, QuoteVO> htDailyData = portfolioBO.getQuote(stkKeySet, date);
            if (htDailyData.size() == 0) {
                continue;
            }
//			finding that days holding			
            Vector<HoldingVO> daysHoldingDetail = findDaysHoldingDetails(date, overallTransDetails);
            float cost, marketValue, profitLoss;
            cost = marketValue = profitLoss = 0f;
//			Calculating current days value			
            for (HoldingVO holdingVO : daysHoldingDetail) {
                if (!holdingVO.getTicker().equals(DAYTRADING)) {
                    QuoteVO hQuoteVO = htDailyData.get(holdingVO.getTicker());
                    if (hQuoteVO != null) {
                        marketValue += holdingVO.getQty() * hQuoteVO.getLastPrice();
                    }
                }
                cost += holdingVO.getTotalCost();
                profitLoss += holdingVO.getProfitLossIncDev();
            }
            PerformanceVO performanceVO = new PerformanceVO(date, cost, marketValue, profitLoss);
            vPerDetails.add(performanceVO);
        }
        return vPerDetails;
    }

    Hashtable<String, QuoteVO> getQuote(Set stkKeySet, PMDate date) {
        return EODQuoteLoader.getQuote(date, stkKeySet);
    }

    private static Vector<HoldingVO> findDaysHoldingDetails(PMDate date, Hashtable<String, Vector<TradeVO>> overallTransDetails) {
        Vector<HoldingVO> dayDetails = new Vector<HoldingVO>();
        for (String stkCode : overallTransDetails.keySet()) {
            Vector<TradeVO> transDetails = overallTransDetails.get(stkCode);
            float qty, totCost, profLoss, divident;
            qty = totCost = profLoss = divident = 0;
            for (int i = 0; i < transDetails.size(); i++) {
                TradeVO stockVO = transDetails.elementAt(i);
                if (!stockVO.getPurchaseDate().after(date)) {
                    if (stockVO.isHolding() || stockVO.getSaleDate().after(date)) {
                        qty += stockVO.getQty();
                        totCost += stockVO.getPurchasePrice() * stockVO.getQty() + stockVO.getBrokerage();
                    } else {
                        profLoss += (stockVO.getSalePrice() - stockVO.getPurchasePrice())
                                * stockVO.getQty() - stockVO.getBrokerage();
                    }
                    divident += stockVO.getDivident();
                }
            }
            if (qty != 0 || profLoss != 0) {
                HoldingVO holdingVO = new HoldingVO(stkCode, qty, totCost, profLoss, divident);
                dayDetails.add(holdingVO);
            }
        }
        return dayDetails;
    }

    public List<StopLossVO> getStopLossDetailsWithQuote(String portfolioName) {
        //getting live quote
        List<StopLossVO> stopLossVOs = getStopLossDetails(portfolioName);
        loadQuote(stopLossVOs);
        return stopLossVOs;
    }

    private void loadQuote(List<StopLossVO> stopLossVOs) {
        String[] stockList = new String[stopLossVOs.size()];
        int count = 0;
        for (StopLossVO stopLossVO : stopLossVOs) {
            stockList[count++] = stopLossVO.getStockCode();
        }

        logger.info(Calendar.getInstance().getTime());
        QuoteVO[] quoteVOs = QuoteManager.getLiveQuote(stockList);
        logger.info(Calendar.getInstance().getTime());

        count = 0;
        for (StopLossVO stopLossVO : stopLossVOs) {
            stopLossVO.setQuoteVO(quoteVOs[count++]);
        }
    }

    public List<StopLossVO> getStopLossDetails(String portfolioName) {
        List<StopLossVO> slVOs = dao().getStopLoss(portfolioName);
        Set<String> slVOsStockCodes = new HashSet<String>();
        for (StopLossVO slVO : slVOs) {
            slVOsStockCodes.add(slVO.getStockCode());
        }
        List<TradeVO> holdingDetails = DAOManager.getTransactionDAO().getHoldingDetails(null, portfolioName, false);
        for (TradeVO tradeVO : holdingDetails) {
            String stockCode = tradeVO.getStockCode();
            if (!slVOsStockCodes.contains(stockCode)) {
                slVOs.add(new StopLossVO(stockCode));
                slVOsStockCodes.add(stockCode);
            }
        }
        return slVOs;
    }

    public boolean saveStopLoss(PortfolioDetailsVO detailsVO, List<StopLossVO> data) {

        DAOManager.getAccountDAO().updatePortfolio(detailsVO);
        for (StopLossVO slVO : data) {
            slVO.setPortfolioName(detailsVO.getName());
        }
        IPortfolioDAO portfolioDAO = dao();
        portfolioDAO.deleteAllStopLoss(detailsVO);
        portfolioDAO.insertStopLossVOs(data);
        return true; //TODO refactor here
    }

    public Vector<StopLossVO> getStopLossDetailsWithQuoteFilterForNonSet(String portfolioName) {
        List<StopLossVO> stopLossVOComplete = dao().getStopLoss(portfolioName);
        loadQuote(stopLossVOComplete);
        Vector<StopLossVO> retVal = new Vector<StopLossVO>();
        for (StopLossVO slVO : stopLossVOComplete) {
            if (slVO.getQuoteVO().getLastPrice() == 0 || slVO.getStopLoss1() == 0
                    || slVO.getStopLoss2() == 0 || slVO.getTarget1() == 0
                    || slVO.getTarget2() == 0) {
                continue;
            }
            retVal.add(slVO);
        }
        return retVal;
    }

    public float getAllPortfolioValue() {
        PMDate date = DAOManager.getDateDAO().getLastQuoteDate();
        Vector<PerformanceVO> performanceVOs = getPortfolioPerformance(REPORT_TYPE.All.toString(), REPORT_TYPE.All.toString(), date, date);
        if (performanceVOs.size() != 1) {
            return 0f;
        }
        return performanceVOs.elementAt(0).getMarketValue() + performanceVOs.elementAt(0).getProfitLoss() - performanceVOs.elementAt(0).getCost();
    }

    public boolean saveStopLoss(StopLossVO stopLossVO) {
        IPortfolioDAO portfolioDAO = dao();
        portfolioDAO.updateStopLoss(stopLossVO);
        return true; //TODO refactor this
    }

    public PerformanceVO getMarketValue(PortfolioDetailsVO portfolioVO) {
        PMDate currDate = DAOManager.getDateDAO().getLastQuoteDate();
        Vector<PerformanceVO> performance = getPortfolioPerformance(REPORT_TYPE.All.toString(), portfolioVO.getName(), currDate, currDate);
        return performance.get(0);
    }


    public void handleDuplicateInStopLoss(StockVO latestStockVO, StockVO originalStockVO) {

        List<PortfolioDetailsVO> portfolioList = getPortfolioList();
        IPortfolioDAO dao = dao();
        for (PortfolioDetailsVO portfolio : portfolioList) {
            StopLossVO lossVO = dao.getStopLoss(portfolio.getName(), latestStockVO.getStockCode());
            if (lossVO != null) {
                dao.deleteStopLossOf(portfolio.getId(), originalStockVO.getId());
            }
        }
        dao.updateSLStockId(latestStockVO.getId(), originalStockVO.getId());
    }

    List<PortfolioDetailsVO> getPortfolioList() {
        return DAOManager.getAccountDAO().getPorfolioList();
    }

    IPortfolioDAO dao() {
        return DAOManager.getPortfolioDAO();
    }
}

