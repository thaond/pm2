/*
 * Created on Oct 28, 2004
 *
 */
package pm.tools;

import pm.AppLoader;
import pm.action.Controller;
import pm.bo.AccountBO;
import pm.bo.CompanyBO;
import pm.bo.StockMasterBO;
import pm.bo.TradingBO;
import pm.dao.derby.DBManager;
import pm.dao.ibatis.dao.DAOManager;
import pm.net.NSESymbolChangeDownloader;
import pm.net.nse.StockListDownloader;
import pm.util.*;
import pm.util.enumlist.BROKERAGETYPE;
import pm.vo.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static pm.util.AppConst.TRADINGTYPE.IPO;

/**
 * @author thiyagu
 */
public class LoadTransData {

    /**
     * Get the list of all transactions
     * Get the currently listed stocklist
     * Get the stockcode changes
     * update the transactions with updated stockcode based on data of change
     * load the company actions from log for all currently listed or all has transaction and handle with updated stockcode
     * load all logs (transaction and company action)
     * load quotes from file
     */

    /**
     * TODO StockCode change should impact quotes import
     */
    public static void main(String[] str) {
        String appStartDate = "20000101";
        int quoteStartDate = 20051231;
        boolean onlyLog = false;
        if (str.length == 3) {
            appStartDate = str[0];
            quoteStartDate = Integer.parseInt(str[1]);
            onlyLog = str[2].equalsIgnoreCase("onlyLog");
        }
        AppLoader.initConsoleLogger();
        try {
            new LoadTransData().loadData(appStartDate, quoteStartDate,
                    BusinessLogger.getTransLogFilePath(),
                    BusinessLogger.getCompActLogFilePath(), true, onlyLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DBManager.shutDown();
    }

    void loadQuotes(int quoteStartDate, String appStartDate) {
        cleanupTables(false);
        resetDownloadDates(appStartDate);
        System.out.println("Loading Stocklist");
        loadStockList();
        System.out.println("Loading Quotes");
        loadQuotesFromFiles(quoteStartDate);
    }

    void loadData(String appStartDate, int quoteStartDate, String transLogFilePath,
                  String compActLogFilePath, boolean renameLogFiles, boolean onlyLog) throws Exception {
        System.out.println("Loading DB");
        cleanupTables(onlyLog);
        if (!onlyLog) {
            resetDownloadDates(appStartDate);
            System.out.println("Loading Stocklist");
            loadStockList();
        }
        System.out.println("Loading logs");
        loadLogData(transLogFilePath, compActLogFilePath, renameLogFiles, onlyLog);
        System.out.println("Performing divident normalization");
        new CompanyBO().normalizeDividents();
        if (!onlyLog) {
            System.out.println("Loading Quotes");
            loadQuotesFromFiles(quoteStartDate);
        }
    }

    void loadStockList() {
        new StockListDownloader().run();
    }

    private void resetDownloadDates(String appStartDate) {
        AppConfig.saveUpdateConfigDetail(AppConfig.dateCORPACTIONSYNCHRONIZER, appStartDate);
        AppConfig.saveUpdateConfigDetail(AppConfig.dateCORPACTIONDOWNLOADMANAGER, appStartDate);
        AppConfig.saveUpdateConfigDetail(AppConfig.dateCORPRESULTDOWNLOADMANAGER, appStartDate);
        AppConfig.saveUpdateConfigDetail(AppConfig.dateEODDOWNLOADMANAGER, appStartDate);
        AppConfig.saveUpdateConfigDetail(AppConfig.dateMARKETHOLIDAYDOWNLOADER, appStartDate);
        AppConfig.saveUpdateConfigDetail(AppConfig.dateSTOCKLISTDOWNLOADER, appStartDate);
    }

    private void loadQuotesFromFiles(int quoteStartDate) {
        Helper.saveLastBhavCopyDate(new PMDate(quoteStartDate));
        Helper.saveLastDeliveryPosDate(new PMDate(quoteStartDate));
        bhavToPMConverter().processData();
    }

    BhavToPMConverter bhavToPMConverter() {
        return new BhavToPMConverter();
    }

    private void cleanupTables(boolean onlyLog) {
        if (onlyLog) DBManager.cleanUpDB();
        else DBManager.initDB();
    }

    public void loadLogData(String transLogFilePath, String compActLogFilePath, boolean renameLogFiles, boolean onlyLog) throws Exception {
        Map<String, SymbolChange> symbolChangeMap = onlyLog ? new HashMap<String, SymbolChange>() : getSymbolChangeDetails();
        TreeMap logDetails = new TreeMap();
        Set<String> transactionStockCodes = new HashSet<String>();
        Set<String> portfolioList = new HashSet<String>();
        Set<String> tradingAccList = new HashSet<String>();
        loadTransactionData(logDetails, transactionStockCodes, tradingAccList, portfolioList, symbolChangeMap, transLogFilePath);
        List<StockVO> listedStocks = DAOManager.getStockDAO().getStockList(false);
        Set<String> listedOrtransactedStocks = mergeStockCodes(listedStocks, transactionStockCodes);
        loadCompanyActionData(logDetails, listedOrtransactedStocks, symbolChangeMap, compActLogFilePath);  //change this to load complete action list
//            loadCompleteCompanyActionData(logDetails, transactionStockCodes);
        if (renameLogFiles) {
            renameLogFiles();
        }
        new StockMasterBO().insertMissingStockCodes(listedOrtransactedStocks);
        insertMissingPortfolios(portfolioList);
        insertMissingTradingAccounts(tradingAccList);
        moveBuysToFirst(logDetails);
        writeData(logDetails);
    }

    private Map<String, SymbolChange> getSymbolChangeDetails() {
        List<SymbolChange> list = getSymbolChangeList();
        Map<String, SymbolChange> symbolChangeMap = new HashMap<String, SymbolChange>();
        for (SymbolChange symbolChange : list) {
            symbolChangeMap.put(symbolChange.getOldCode(), symbolChange);
        }
        return symbolChangeMap;
    }

    List<SymbolChange> getSymbolChangeList() {
        return new NSESymbolChangeDownloader().download();
    }

    private Set<String> mergeStockCodes(List<StockVO> listedStocks, Set<String> transactionStockCodes) {
        Set<String> stocks = new HashSet<String>(transactionStockCodes);
        for (StockVO listedStock : listedStocks) {
            stocks.add(listedStock.getStockCode());
        }
        return stocks;
    }

    void moveBuysToFirst(TreeMap logDetails) {
        for (Object daysData : logDetails.values()) {
            Vector<TransactionVO> sellVOs = new Vector<TransactionVO>();
            Vector vDaysData = (Vector) daysData;
            for (int i = vDaysData.size() - 1; i >= 0; i--) {
                if (vDaysData.get(i) instanceof TransactionVO) {
                    TransactionVO transVO = (TransactionVO) vDaysData.get(i);
                    if (transVO.getAction() == AppConst.TRADINGTYPE.Sell) {
                        sellVOs.add((TransactionVO) vDaysData.remove(i));
                    }
                }

            }
            vDaysData.addAll(sellVOs);
        }

    }

    private void loadCompleteCompanyActionData(TreeMap logDetails, Set<String> stockCodes) {
        Set<String> delistedStockList = new HashSet();
        delistedStockList.addAll(stockCodes);
        List<StockVO> stockList = pm.dao.ibatis.dao.DAOManager.getStockDAO().getStockList(false);
        for (StockVO stockVO : stockList) {
            delistedStockList.remove(stockVO.getStockCode());
            Hashtable<PMDate, Vector<CompanyActionVO>> htActionVOs = getActionData(stockVO.getStockCode());
            for (PMDate date : htActionVOs.keySet()) {
                Vector data = (Vector) logDetails.get(date);
                if (data == null) {
                    data = new Vector();
                }
                data.addAll(htActionVOs.get(date));
                logDetails.put(date, data);
            }
        }
        for (String stockCode : delistedStockList) {
            Hashtable<PMDate, Vector<CompanyActionVO>> htActionVOs = getActionData(stockCode);
            for (PMDate date : htActionVOs.keySet()) {
                Vector data = (Vector) logDetails.get(date);
                if (data == null) {
                    data = new Vector();
                }
                data.addAll(htActionVOs.get(date));
                logDetails.put(date, data);
            }
        }

    }

    private void insertMissingTradingAccounts(Set<String> tradingAccList) throws Exception {
        List<TradingAccountVO> tradingAccVOs = new AccountBO().getTradingAccountVOs();
        for (Account tradingAc : tradingAccVOs) {
            tradingAccList.remove(tradingAc.getName());
        }
        for (String accountName : tradingAccList) {
            TradingBO.saveTradingAc(new TradingAccountVO(accountName, getBrokerageType(accountName)));
        }
    }

    private BROKERAGETYPE getBrokerageType(String accountName) {
        if (accountName.toUpperCase().indexOf("ICICI") != -1) {
            return BROKERAGETYPE.ICICIDirect;
        }
        if (accountName.toUpperCase().indexOf("HDFC") != -1) {
            return BROKERAGETYPE.HDFC;
        }
        if (accountName.toUpperCase().indexOf("CBEACC") != -1) {
            return BROKERAGETYPE.IndiaBulls;
        }
        return BROKERAGETYPE.None;
    }

    private void insertMissingPortfolios(Set<String> portfolioList) throws Exception {
        AccountBO accountBO = new AccountBO();
        List<PortfolioDetailsVO> portfolioVOs = accountBO.getPortfolioList();
        for (PortfolioDetailsVO portfolioVO : portfolioVOs) {
            portfolioList.remove(portfolioVO.getName());
        }
        for (String portfolioName : portfolioList) {
            accountBO.savePortfolio(portfolioName);
        }
    }

    /**
     * @param logDetails
     * @throws ApplicationException
     */
    private void writeData(TreeMap logDetails) throws ApplicationException {
        for (Object daysData : logDetails.values()) {
            for (Object data : (Vector) daysData) {
                System.out.println(data);
                if (data instanceof TransactionVO) {
                    performTransaction((TransactionVO) data);
                } else if (data instanceof CompanyActionVO) {
                    performCompanyAction((CompanyActionVO) data);
                } else {
                    throw new ApplicationException("Unknow data " + data.getClass().getName());
                }
            }
        }
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param actionVO
     * @throws ApplicationException
     */
    private void performCompanyAction(CompanyActionVO actionVO) throws ApplicationException {
        boolean status = new CompanyBO().doAction(actionVO);
//        if (!status) {
//            throw new ApplicationException("Error performing company action " + actionVO.toWrite());
//        }

    }

    /**
     * @param transVO
     * @throws ApplicationException
     */
    private void performTransaction(TransactionVO transVO) throws ApplicationException {
        float brok;
        if (transVO.isDayTrading()) {
            brok = Helper.calculateBrokerageDay(transVO.getAction(), transVO.getDate(), transVO.getQty(), transVO.getPrice());
        } else {
            brok = Helper.calculateBrokerage(transVO.getDate(), transVO.getQty(), transVO.getPrice());
        }
        if (transVO.getAction() != IPO && transVO.getBrokerage() == 0) {
            transVO.setBrokerage(brok);
        }
        boolean status = Controller.doTrading(transVO);
        if (!status) {
            throw new ApplicationException("Error performing transaction detail " + transVO.getDetails());
        }

    }

    private void renameLogFiles() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
        String logDate = dateFormat.format(Calendar.getInstance().getTime());
        File frmFileT = new File(BusinessLogger.getTransLogFilePath());
        File toFileT = new File(BusinessLogger.getTransLogFilePath() + "_" + logDate);
        frmFileT.renameTo(toFileT);
        File frmFile = new File(BusinessLogger.getCompActLogFilePath());
        File toFile = new File(BusinessLogger.getCompActLogFilePath() + "_" + logDate);
        frmFile.renameTo(toFile);
    }

    /**
     * @param logDetails
     * @param stockCodes
     * @param symbolChangeMap
     * @param compActLogFilePath
     * @throws Exception
     */
    public void loadCompanyActionData(Map<PMDate, Vector<CompanyActionVO>> logDetails, Set<String> stockCodes, Map<String, SymbolChange> symbolChangeMap, String compActLogFilePath) throws Exception {
        File inpFile = new File(compActLogFilePath);
        if (inpFile.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(inpFile));
            String line;
            while ((line = br.readLine()) != null) {
                CompanyActionVO actionVO = new CompanyActionVO(line);
                SymbolChange symbolChange = symbolChangeMap.get(actionVO.getStockCode());
                if (symbolChange != null && actionVO.getExDate().before(symbolChange.getFromDate())) {
                    actionVO.setStockCode(symbolChange.getNewCode());
                }
                if (stockCodes.contains(actionVO.getStockCode())) {
                    Vector<CompanyActionVO> v = logDetails.remove(actionVO.getExDate());
                    if (v == null) {
                        v = new Vector<CompanyActionVO>();
                    }
                    v.add(actionVO);
                    logDetails.put(actionVO.getExDate(), v);
                }
            }
            br.close();
        }
    }


    /**
     * @param logDetails
     * @param stockCodes
     * @param tradingAccList
     * @param portfolioList    @throws Exception
     * @param symbolChangeMap
     * @param transLogFilePath
     * @throws ApplicationException
     */
    public void loadTransactionData(TreeMap<PMDate, Vector<TransactionVO>> logDetails, Set<String> stockCodes, Set<String> tradingAccList, Set<String> portfolioList, Map<String, SymbolChange> symbolChangeMap, String transLogFilePath) throws Exception {
        File inpFile = new File(transLogFilePath);
        if (inpFile.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(inpFile));
            String line;
            while ((line = br.readLine()) != null) {
                TransactionVO transVO = new TransactionVO(line);
                SymbolChange symbolChange = symbolChangeMap.get(transVO.getStockCode());
                if (symbolChange != null) {
                    if (symbolChange.getFromDate().after(transVO.getDate())) {
                        transVO.setStockCode(symbolChange.getNewCode());
                    }
                }
                stockCodes.add(transVO.getStockCode());
                tradingAccList.add(transVO.getTradingAc());
                portfolioList.add(transVO.getPortfolio());

                Vector v = (Vector) logDetails.remove(transVO.getDate());
                if (v == null) {
                    v = new Vector();
                }
                v.add(transVO);
                logDetails.put(transVO.getDate(), v);
            }
            br.close();
        }

    }

    public Hashtable<PMDate, Vector<CompanyActionVO>> getActionData(
            String stockCode) {
        Hashtable<PMDate, Vector<CompanyActionVO>> retVal = new Hashtable<PMDate, Vector<CompanyActionVO>>();
        String fileName = getCorpActionFileName(stockCode);
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                CompanyActionVO actionVO = new CompanyActionVO(line);
                Vector<CompanyActionVO> actionVOs = retVal.get(actionVO.getExDate());
                if (actionVOs == null) {
                    actionVOs = new Vector<CompanyActionVO>();
                }
                actionVOs.add(actionVO);
                retVal.put(actionVO.getExDate(), actionVOs);
            }
            br.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    private String getCorpActionFileName(String stockCode) {
        String baseDir = "";
        String fileName = baseDir + "/" + stockCode + ".action";
        return fileName;
    }

}
