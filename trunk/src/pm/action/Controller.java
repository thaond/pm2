/*
 * Created on Oct 12, 2004
 *
 */
package pm.action;

import org.apache.log4j.Logger;
import pm.analyzer.AnalyzeController;
import pm.bo.*;
import pm.dao.ibatis.dao.DAOManager;
import pm.datamining.SupportResistanceAnalyzer;
import pm.datamining.vo.SupportResistanceVO;
import pm.report.PortfolioReport;
import pm.util.AppConst;
import pm.util.AppConst.ANALYZER_LIST;
import pm.util.AppConst.CORP_RESULT_TIMELINE;
import pm.util.AppConst.STOCK_PICK_TYPE;
import pm.util.PMDate;
import pm.util.enumlist.IPOAction;
import pm.vo.*;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class Controller {

    private static LimitAlert limitAlertThread = null;
    private static Logger logger = Logger.getLogger(Controller.class);

    public static boolean savePortfolio(String name) throws Exception {
        return new AccountBO().savePortfolio(name);
    }

    public static List<PortfolioDetailsVO> getPortfolioList() {
        return new AccountBO().getPortfolioList();

    }

    public static boolean saveTradingAc(Account tradingAc) throws Exception {
        return TradingBO.saveTradingAc(tradingAc);
    }

    public static List<TradingAccountVO> getTradingAcList() {
        return new AccountBO().getTradingAccountVOs();
    }

    public static Vector getPortfolioView(String tradingAc, String portfolio, String reportType) throws Exception {
        return new PortfolioBO().getPortfolioView(tradingAc, portfolio, reportType);
    }

    public static boolean saveWatchlist(WatchlistVO[] obj, WatchlistDetailsVO wlDetails) {
        return new WatchlistBO().saveWatchlist(obj, wlDetails);
    }

    public static List<WatchlistVO> getWatchlistView(int wlgID) {
        return new WatchlistBO().getWatchlistView(wlgID);
    }

    public static Vector getPortfolioPerformance(String tradeAc, String portfolio, PMDate frmDate, PMDate toDate) {
        return new PortfolioBO().getPortfolioPerformance(tradeAc, portfolio, frmDate, toDate);
    }

    public static Vector getWatchlistPerfReport(int days, int wlgID) throws Exception {
        return new WatchlistBO().getPerfReport(days, wlgID);
    }

    public static Vector<Vector<MovAvgVO>> getMovAvg(String[] stockCodes, int[] days, PMDate stDate, PMDate enDate) {
        return ChartBO.getMovAvgData(stockCodes, days, stDate, enDate);
    }

    public static Hashtable getTransactionDetails(String tradeAc, String portfolio, boolean dayTrading) {
        return new PortfolioBO().getStockwiseTradeDetails(tradeAc, portfolio, dayTrading);
    }

    public static EODChartVO getEODData(PMDate frmDate, PMDate toDate, int[] days, String stockCode, boolean applyCompanyAction, String portfolioName, AppConst.TIMEPERIOD timePeriod) {
        return new ChartBO().getEODChartData(stockCode, days, frmDate, toDate, applyCompanyAction, portfolioName, timePeriod);
    }

    public static Vector getAnalyzedData(PMDate frmDate, PMDate toDate, ANALYZER_LIST[] analyzerList, STOCK_PICK_TYPE funMode, boolean bPositive, boolean bNegative) {
        return AnalyzeController.getAnalyzedData(frmDate, toDate, analyzerList, funMode, bPositive, bNegative);
    }

    public static Vector<WatchlistDetailsVO> getWatchlistNames() {
        return new Vector<WatchlistDetailsVO>(new WatchlistBO().getWatchlistNames());
    }

    public static boolean createWatchlist(WatchlistDetailsVO newName) {
        return new WatchlistBO().createWatchlist(newName);
    }

    public static boolean doTrading(TransactionVO transVO) {
        return new TradingBO().doTrading(transVO);
    }

    public static boolean doCompanyAction(CompanyActionVO actionVO) {
        return new CompanyBO().doActionAfterNormalize(actionVO);
    }

    public static void doCancel() {
        logger.info("Cancel triggered");
    }

    public static List<WatchlistVO> getWatchlist(int wlgID) {
        return DAOManager.getWatchlistDAO().getWatchlistVos(wlgID);
    }

    public static QuoteVO getLastQuote(String code) {
        return EODQuoteLoader.getLastQuote(code);
    }

    public static Object getAnalysisReport(PMDate date) {
        return AnalyzeController.getAnalyzedData(date, date, ANALYZER_LIST.values(), STOCK_PICK_TYPE.Or, true, true);
    }

    public static List<StopLossVO> getStopLoss(String portfolioName) {
        return new PortfolioBO().getStopLossDetailsWithQuote(portfolioName);
    }

    public static boolean saveStopLoss(PortfolioDetailsVO detailsVO, List<StopLossVO> data) {
        return new PortfolioBO().saveStopLoss(detailsVO, data);
    }

    public static boolean saveStopLoss(StopLossVO stopLossVO) {
        return new PortfolioBO().saveStopLoss(stopLossVO);
    }

    public static Object getPortfolioEODReport(String portfolioName) {
        return PortfolioReport.getReport(portfolioName);
    }

    public static Hashtable<PMDate, Vector<CompanyActionVO>> getCompanyActionDetails() {
        return new CompanyBO().getConsolidatedActionDataNormalizedToCurrentPrice();
    }

    public static Map<String, List<CorpResultVO>> getFinancialResult(String[] stockCodes) {
        return new CompanyBO().getFinResult(stockCodes);
    }

    public static Vector<CorpResultVO> getFinancialResult(String stockCode, CORP_RESULT_TIMELINE corp_result_timeline) {
        return new CompanyBO().getFinResult(stockCode, corp_result_timeline);
    }

    public static String getQuotePage(String stockName) {

        return QuoteManager.getQuotePage(stockName);
    }

    public static Vector<SupportResistanceVO> getSupportResistance(String stockCode, float diff, float weight) {
        return SupportResistanceAnalyzer.getAllSupportResistanceZone(stockCode, diff, weight);
    }

    public static boolean isPriceAlertRunning() {
        return limitAlertThread != null && limitAlertThread.isAlive();
    }

    public static boolean togglePriceAlert() {
        if (!isPriceAlertRunning()) {
            logger.info("Starting Alert Thread");
            limitAlertThread = new LimitAlert();
            limitAlertThread.start();
            return true;
        } else {
            logger.info("Stopping Alert Thread");
            if (limitAlertThread != null) {
                limitAlertThread.stopThread();
                limitAlertThread = null;
            }
            return false;
        }

    }

    public static List<TradingAccountVO> getTradingAcVOList() {
        return new AccountBO().getTradingAccountVOs();
    }

    public static Vector<IPOVO> getIPOTransactionDetailsFor(int tradeAc, int portfolio, IPOAction action) {
        return new IPOBO().getIPOTransactionDetailsFor(tradeAc, portfolio, action);
    }

    public static Vector<IPOVO> getIPOTransactionList(int tradeAc, int portfolio) {
        return new IPOBO().getIPOTransactionList(tradeAc, portfolio);
    }

    public static List<CompanyActionVO> getCompanyActionInfo(String stockCode) {
        return DAOManager.getCompanyActionDAO().getCompanyAction(stockCode);
    }

    public static Vector<CompanyPerfVO> getCompanyPerformance(String stockCode, CORP_RESULT_TIMELINE timeline) {
        return new CompanyBO().getCompanyPerformance(stockCode, timeline);
    }

    public static void doIPO(IPOAction action, IPOVO ipoVO) {
        new IPOBO().doAction(action, ipoVO);
    }

    public static List<StockVO> getIndexCodes() {
        return DAOManager.getStockDAO().getIndexList();
    }

    public static void doTransaction(FundTransactionVO transaction) {
        DAOManager.fundTransactionDAO().perform(transaction);
    }

    public static List<FundTransactionVO> fundTransactionVOs(TradingAccountVO tradingAcc, PortfolioDetailsVO portfolioAcc) {
        return DAOManager.fundTransactionDAO().get(tradingAcc, portfolioAcc);
    }

    public static StockVO findStock(String stockCode) {
        return DAOManager.getStockDAO().getStock(stockCode);
    }

    public static List<StockVO> stockList(boolean incIndex) {
        return new StockMasterBO().getStockList(incIndex);
    }

    public static List<TradeVO> holdingDetails(Account tradingAcc, Account portfolio, boolean forDayTrading) {
        return DAOManager.getTransactionDAO().getHoldingDetails(tradingAcc.getName(), portfolio.getName(), forDayTrading);
    }

    public static double totalInvestedRatio(String portfolioName) {
        PortfolioDetailsVO portfolioVO = DAOManager.getAccountDAO().portfolio(portfolioName);
        if (portfolioVO == null) return 0;
        float totalInvested = DAOManager.fundTransactionDAO().totalInvested(null, portfolioVO);
        PerformanceVO performanceVO = new PortfolioBO().getMarketValue(portfolioVO);
        float totalValue = performanceVO.getProfitLoss() + totalInvested;
        return performanceVO.getCost() / totalValue * 100;
    }

    public static double cashPosition(String portfolioName) {
        PortfolioDetailsVO portfolioVO = DAOManager.getAccountDAO().portfolio(portfolioName);
        if (portfolioVO == null) return 0;
        return DAOManager.fundTransactionDAO().balance(null, portfolioVO);
    }

    public static boolean isDuplicate(TransactionVO transVO) {
        return new TradingBO().isDuplicate(transVO);
    }
}
