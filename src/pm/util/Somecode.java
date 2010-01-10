/*
 * Created on Oct 18, 2004
 *
 */
package pm.util;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import pm.bo.PortfolioBO;
import pm.bo.StockMasterBO;
import pm.dao.CompanyDAO;
import pm.net.nse.downloader.CorpActionDownloader;
import pm.tools.BrokerageCalculator;
import pm.util.AppConst.TRADINGTYPE;
import pm.util.enumlist.BROKERAGETYPE;
import pm.vo.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author thiyagu1
 */

public class Somecode {

    public static void main(String[] args) throws Exception {

        Pattern pattern = Pattern.compile("-\\d", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher("234");
        System.out.println(matcher.find());

//        Math.random() %

/*
        AppConfig.dateCORPACTIONSYNC.Value = "19990101";
        List<CompanyActionVO> actionVOs = readCompanyAction("X:\\Project\\SampleData\\CA_Complete Till 28 July Inc PM1.txt");
        identifyDuplicates(actionVOs);
*/
/*
        List<CompanyActionVO> newActionVOs = readCompanyAction("X:\\Project\\SampleData\\CA_Complete Till 28 July.txt");
        List<CompanyActionVO> oldActionVOs = readCompanyAction("X:\\Project\\SampleData\\CA_From_PM1.log");
        printMissing(oldActionVOs, newActionVOs);
*/

/*
        List<CompanyActionVO> oldActionVOs = readCompanyAction("X:\\Project\\SampleData\\Log_CompanyAction Manual.log");
        List<CompanyActionVO> newActionVOs = readCompanyAction("X:\\Project\\SampleData\\CA_From_PM1.log");

        printMissing(oldActionVOs, newActionVOs);
*/

/*
        Set<CompanyActionVO> newActionList = getActionVOs("allCompanyAction.txt");
        Set<CompanyActionVO> existingActionList = getActionVOs("Log_CompanyAction.log");
        Set<CompanyActionVO> missingActionList = new HashSet<CompanyActionVO>();
        for (CompanyActionVO actionVO : existingActionList) {
            if (!newActionList.contains(actionVO)) {
                missingActionList.add(actionVO);
            }
        }

        for (CompanyActionVO actionVO : missingActionList) {
            System.out.println(actionVO.toWrite());
        }
*/
/*
        AppLoader.initConsoleLogger();
        StringReader reader = new HTTPHelper().getHTMLContentReader("http://www.nseindia.com/content/equities/eq_holidays.htm");
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
*/

//        System.out.println(new Date(1170348261000l));
/*
        Connection connection = DBManager.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select o.id as oid, n.id as nid from stockmaster as o, stockmaster as n where o.stockcode=n.stockcode and o.listed=0 and n.listed=1");
        Vector<int[]> stockids = new Vector<int[]>();
        while (resultSet.next()) {
            int[] ids = new int[2];
            ids[0]= resultSet.getInt("oid");
            ids[1]= resultSet.getInt("nid");
            System.out.println(ids[0] + " "+ ids[1]);
            stockids.add(ids);
        }

        for (int[] ids : stockids) {
            statement.executeUpdate("update quote set stockid="+ids[0] +" where stockid="+ids[1]);
            statement.executeUpdate("update stockmaster set listed=1 where id="+ids[0]);
            statement.execute("delete from stockmaster where id="+ids[1]);            
        }
        connection.close();
*/
//        Vector<StockVO> list = new Vector<StockVO>();
//        list.add(new StockVO("abcd", "abcd comp", 10f, SERIESTYPE.equity, )
//        new StockMasterBO().storeStockList(list);
//        System.out.println(except());


    }

    private static void identifyDuplicates(List<CompanyActionVO> actionVOs) {
        Set<Integer> dupList = new HashSet<Integer>();
        for (int i = 0; i < actionVOs.size(); i++) {
            CompanyActionVO actionVO = actionVOs.get(i);
            for (int j = i + 1; j < actionVOs.size(); j++) {
                CompanyActionVO actionVO2 = actionVOs.get(j);
                if (isDuplicate(actionVO, actionVO2)) {
                    dupList.add(i);
                    dupList.add(j);
                    CorpActionDownloader downloader = new CorpActionDownloader(actionVO.getStockCode(), null) {
                        public void alertManager() {
                        }


                    };
                    downloader.run();
                    Vector<CompanyActionVO> downloaded = downloader.getCorpActions();
                    boolean downloadedMatch = false;
                    for (CompanyActionVO downloadedVO : downloaded) {
                        if (downloadedVO.getExDateVal() == actionVO.getExDateVal() &&
                                downloadedVO.getAction() == actionVO.getAction()) {
                            System.out.println("1 Matching : " + actionVO.toWrite());
                            downloadedMatch = true;
                            break;
                        }
                        if (downloadedVO.getExDateVal() == actionVO2.getExDateVal() &&
                                downloadedVO.getAction() == actionVO2.getAction()) {
                            System.out.println("2 Matching : " + actionVO2.toWrite());
                            downloadedMatch = true;
                            break;
                        }
                    }
                    if (!downloadedMatch) {
                        System.out.println("No Matching : " + actionVO.toWrite() + "  " + actionVO2.toWrite());
                    }
                }
            }
            if (!dupList.contains(i)) {
                System.out.println(actionVO.toWrite());
            }
        }
    }

    private static boolean isDuplicate(CompanyActionVO actionVO, CompanyActionVO actionVO2) {
        return actionVO.getStockCode().equals(actionVO2.getStockCode()) &&
                actionVO.getAction().equals(actionVO2.getAction()) && isDateInDuplicateRange(actionVO, actionVO2);
    }

    private static boolean isDateInDuplicateRange(CompanyActionVO actionVO, CompanyActionVO actionVO2) {
        int diff = actionVO.getExDateVal() - actionVO2.getExDateVal();
        return (diff <= 101 && diff >= -101);
    }

    private static void printMissing(List<CompanyActionVO> oldActionVOs, List<CompanyActionVO> newActionVOs) {
        Set<String> newSet = new HashSet<String>();
        for (CompanyActionVO newActionVO : newActionVOs) {
            newSet.add(makeString(newActionVO));
        }

        Map<PMDate, List<CompanyActionVO>> missingList = new TreeMap<PMDate, List<CompanyActionVO>>();
        for (CompanyActionVO oldActionVO : oldActionVOs) {
            if (!newSet.contains(makeString(oldActionVO))) {
/*
                    List<CompanyActionVO> list = missingList.get(oldActionVO.getExDate());
                    if (list == null) {
                        list = new ArrayList<CompanyActionVO>();
                    }
                    list.add(oldActionVO);
                    missingList.put(oldActionVO.getExDate(), list);
*/
                System.out.println(oldActionVO.toWrite());
            }
        }

        for (PMDate pmDate : missingList.keySet()) {
            List<CompanyActionVO> list = missingList.get(pmDate);
            for (CompanyActionVO vo : list) {
                System.out.println(vo.toWrite());
            }
        }
    }

    private static String makeString
            (CompanyActionVO
                    newActionVO) {
        return newActionVO.getExDate().toWrite() + newActionVO.getStockCode() + newActionVO.getAction();
    }

    private static List<CompanyActionVO> readCompanyAction
            (String
                    fileName) throws Exception {
        List<CompanyActionVO> actionVOs = new ArrayList<CompanyActionVO>();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;

        while ((line = br.readLine()) != null) {
            actionVOs.add(new CompanyActionVO(line));
        }
        br.close();
        return actionVOs;
    }

    private static Set<CompanyActionVO> getActionVOs
            (String
                    fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        Set<CompanyActionVO> actionVOs = new HashSet<CompanyActionVO>();
        while ((line = br.readLine()) != null) {
            actionVOs.add(new CompanyActionVO(line));
        }
        br.close();
        return actionVOs;
    }

    private static boolean except
            () {
        try {
            try {
                System.out.println("in trans");
            } finally {
                System.out.println("in finally");
                if (1 == 1) {
                    throw new RuntimeException();
                }
            }
            return true;
        } catch (RuntimeException e) {
            System.out.println("in catch");
            return false;
        }
    }

    private static void something
            () {

//		long stTime = System.currentTimeMillis();
//		QuoteVO[] quotes = new QuoteDAO().getQuotes(new PMDate(1,1,1999), new PMDate(16,6,2006), "ONGC");
//		for (QuoteVO quoteVO : quotes) {
//			System.out.println(quoteVO);
//		}

//		long enTime = System.currentTimeMillis();
//
//		System.out.println("Time Taken : " + (enTime - stTime));

//        movePortfolioDataToDB();

//    	checkICICILogs();
//    	doLogComparator();
//    	AppLoader.initConsoleLogger();
//    	System.out.println(new PortfolioBO().getAllPortfolioValue());

//    	getFinDetails();
//    	CorpActionDownloader downloader = new CorpActionDownloader("ONGC", null) {
//    		@Override
//    		public void alertManager() {
//    		}
//    	};
//    	downloader.run();
//    	System.out.println(downloader.getCorpActions());

//    	String url = "http://www.nseindia.com/marketinfo/companyinfo/eod/action.jsp?symbol=YOKOGAWA";
//		URLConnection conn = new ConnectionManager().openConnection(url);
//    	Parser parser = new Parser (conn);
//    	StringBean sb = new StringBean();
//		sb.setLinks(false);
//		parser.visitAllNodesWith(sb);
//    	System.out.println(sb.getStrings());
//    	Page.getConnectionManager()
//    	for (PMDate date : ) {
//			ht.remove(date);
//		}

//    	Vector<CorporateResultsVO> financialData = new CompanyDAO().getFinancialData("INFOSYSTCH");
//    	for (CorporateResultsVO resultsVO : financialData) {
//			System.out.println(resultsVO.getCtrlString() + " " + resultsVO);
//		}
        //    	AppLoader.initConsoleLogger();
//    	new FinanceResultConverter().convertAll();
//    	getFinDetails();
//    	to_merge_entered_into_download();
//    	syncLog_CompanyActionDownload_PortfolioData();
//    	to_list_Missing_downloaded_comparing_entered();

//    	System.out.println(new CompanyDAO().getActionData("WIPRO"));
//    	AppLoader.initConsoleLogger();
//    	Vector<String> stockList = StockMasterDAO.getStockList();
//    	CompanyDAO companyDAO = new CompanyDAO();
//    	NSECorpActionConverter converter = new NSECorpActionConverter();
//    	for (String stockCode : stockList) {
//    		System.out.println("Converting : "+ stockCode);
//    		companyDAO.writeActionData(stockCode,
//    				converter.processCorpAction(stockCode, companyDAO.getActionDataOLD(stockCode)));
//		}
//    	new NSECorpActionDownloadManager().consolidateActionData(companyDAO);
//    	taskPane();
//		datePicker();
//    	myTaskPane();
    }

    private static void movePortfolioDataToDB
            () {
        long stTime = System.currentTimeMillis();
        Hashtable<String, Vector<TradeVO>> transactionDetails = new PortfolioBO().getStockwiseTradeDetails("All", "All", true);
        String _DELIMITER = ",";
        for (Vector<TradeVO> tradeVOs : transactionDetails.values()) {
            for (TradeVO tradeVO : tradeVOs) {
//				StringBuffer sb = new StringBuffer();
//				sb.append("db.update(\"INSERT INTO TRADE(stockCode, purchaseDate, qty, purchasePrice, brokerage," +
//			            "saleDate, salePrice, DividentFunction, weightage, portfolioID, TradingAcID ) VALUES(");
//				sb.append("\'"+tradeVO.getStock()+"\',");
//				sb.append(PMDateFormatter.formatYYYYMMDD(tradeVO.getPurchaseDate())).append(_DELIMITER);
//				sb.append(tradeVO.getQty()).append(_DELIMITER);
//				sb.append(tradeVO.getPurchasePrice()).append(_DELIMITER);
//				sb.append(tradeVO.getBrokerage()).append(_DELIMITER);
//				if (tradeVO.getSaleDate() != null)
//					sb.append(PMDateFormatter.formatYYYYMMDD(tradeVO.getSaleDate())).append(_DELIMITER);
//				else
//					sb.append(-1).append(_DELIMITER);
//				sb.append(tradeVO.getSalePrice()).append(_DELIMITER);
//				sb.append(tradeVO.getDivident()).append(_DELIMITER);
//				sb.append(tradeVO.getWeightage()).append(_DELIMITER);
//				if (tradeVO.getPortfolio() == null) {
//					System.out.println(tradeVO);
//				}
//				int pid = tradeVO.getPortfolio().equals("Thiyagu") ? 0 : 
//					tradeVO.getPortfolio().equals("Selva") ? 1 : 2;
//				
//				int tid = tradeVO.getTradingAc().equals("ICICI_Direct") ? 0 :
//					tradeVO.getTradingAc().equals("Arul_ICICI") ? 1 : 2;
//				
//				sb.append(pid).append(_DELIMITER);
//				sb.append(tid).append(")\");");
//				System.out.println(sb.toString());
                System.out.println(tradeVO.getDetails(true));

            }
        }
        long enTime = System.currentTimeMillis();
        System.out.println("Time taked " + (enTime - stTime));
    }

    private static void checkICICILogs
            () throws Exception {
        Vector<TransactionVO> logs = loadICICILogs();
        TradingAccountVO tradingAccountVO = new TradingAccountVO("ICICIDirect", BROKERAGETYPE.ICICIDirect);
        BrokerageCalculator brokerageCalculator = new BrokerageCalculator();
        for (TransactionVO transactionVO : logs) {
//    		System.out.println(transactionVO.getBrokerage());
            float brokerage = brokerageCalculator.getBrokerage(tradingAccountVO, transactionVO.getAction(),
                    transactionVO.getDate(), transactionVO.getQty(), transactionVO.getPrice(), false);
            float brokerageDay = brokerageCalculator.getBrokerage(tradingAccountVO, transactionVO.getAction(),
                    transactionVO.getDate(), transactionVO.getQty(), transactionVO.getPrice(), true);
            int diff = 2;
            if (!((brokerage <= (transactionVO.getBrokerage() + diff) && (brokerage >= (transactionVO.getBrokerage() - diff)) ||
                    (brokerageDay <= (transactionVO.getBrokerage() + diff) && (brokerageDay >= (transactionVO.getBrokerage() - diff)))))) {
                System.out.println(transactionVO + " " + transactionVO.getBrokerage() + " " + brokerage + " " + brokerageDay);
            }
        }
    }

    private static void doLogComparator
            () {
        try {
            Vector<TransactionVO> logs = loadLogs();
            float totBrok = 0;
            TradingAccountVO tradingAccountVO = new TradingAccountVO("ICICIDirect", BROKERAGETYPE.ICICIDirect);
            BrokerageCalculator brokerageCalculator = new BrokerageCalculator();
            for (TransactionVO transactionVO : logs) {
                if (!transactionVO.getTradingAc().equals("ICICI_Direct")) {
                    continue;
                }
                float brokerage = brokerageCalculator.getBrokerage(tradingAccountVO, transactionVO.getAction(),
                        transactionVO.getDate(), transactionVO.getQty(), transactionVO.getPrice(), transactionVO.isDayTrading());
                totBrok += brokerage;
                System.out.println(transactionVO + " " + brokerage);
            }
            System.out.println(totBrok);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Vector<TransactionVO> loadICICILogs
            () throws Exception {
        Vector<TransactionVO> logs = new Vector<TransactionVO>();
        File inpFile = new File("X:/icicidirectTrans.txt");
        if (inpFile.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(inpFile));
            String line;
            while ((line = br.readLine()) != null) {
                TransactionVO transVO = buildVO(line);
                logs.add(transVO);
            }
            br.close();
        }

        return logs;
    }

    private static TransactionVO buildVO
            (String
                    line) throws Exception {
        StringTokenizer stk = new StringTokenizer(line, ";");
        PMDate date = PMDateFormatter.parseDD_Mmm_YYYY(stk.nextToken());
        String stockCode = stk.nextToken();
        TRADINGTYPE tradingtype = TRADINGTYPE.valueOf(stk.nextToken());

        float qty = NumberFormat.getIntegerInstance().parse(stk.nextToken()).floatValue();
        float price = NumberFormat.getIntegerInstance().parse(stk.nextToken()).floatValue();
        stk.nextToken();
        float brokerage = NumberFormat.getIntegerInstance().parse(stk.nextToken()).floatValue();
        TransactionVO transactionVO = new TransactionVO(date, stockCode, tradingtype, qty, price, brokerage, "", "", false);
        return transactionVO;
    }

    private static Vector<TransactionVO> loadLogs
            () throws Exception {
        Vector<TransactionVO> logs = new Vector<TransactionVO>();
        File inpFile = new File("X:/Log_Transaction.log");
        if (inpFile.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(inpFile));
            String line;
            while ((line = br.readLine()) != null) {
                TransactionVO transVO = new TransactionVO(line);
                logs.add(transVO);
            }
            br.close();
        }

        return logs;
    }

//Earnings Per Share or Diluted EPS, Paid-up Equity, Adjusted Net Profit, <SW>Net Profit, <SW> Non Recurring Income

    //Non Recurring Expenses, Adjusted Net Profit

    private static void getFinDetails
            () {
        Vector<StockVO> stockList = new StockMasterBO().getStockList(false);
        CompanyDAO companyDAO = new CompanyDAO();
        for (StockVO stockVO : stockList) {
            Vector<CorporateResultsVO> financialData = companyDAO.getFinancialData(stockVO.getStockCode());
            for (CorporateResultsVO resultsVO : financialData) {
//				if (resultsVO.isBanking()) continue;
                boolean hasKey = false;
                if (resultsVO.getFinancialDetailKeys().isEmpty()) {
                    continue;
                }
                for (Object keys : resultsVO.getFinancialDetailKeys()) {
                    if (keys.toString().startsWith("Net Sales")) {
                        hasKey = true;
                        break;
                    }
//					if (keys.toString().indexOf("") != -1) {
//						hasKey = true;
//						break;
//					}
                }
                if (!hasKey) {
                    System.out.println(resultsVO);
                    System.out.println(resultsVO.getTicker() + "  " + resultsVO.getCtrlString());
                }
            }
        }
    }
/*
    private static void to_merge_entered_into_download() throws Exception {
        AppLoader.initConsoleLogger();
        TreeMap<PMDate, Vector<CompanyActionVO>> logDetails = new TreeMap<PMDate, Vector<CompanyActionVO>>();
        LoadTransData.loadCompanyActionData(logDetails);
        CompanyDAO companyDAO = new CompanyDAO();
        for (Vector<CompanyActionVO> actionVOs : logDetails.values()) {
            for (CompanyActionVO actionVO : actionVOs) {
                Hashtable<PMDate, Vector<CompanyActionVO>> downloadedData = companyDAO.getActionData(actionVO.getStock());
                if (downloadedData == null) {
                    downloadedData = new Hashtable<PMDate, Vector<CompanyActionVO>>();
                }
                Vector<CompanyActionVO> downloadedVOs = downloadedData.get(actionVO.getExDate());
                if (downloadedVOs == null) {
                    downloadedVOs = new Vector<CompanyActionVO>();
                }
                downloadedVOs.add(actionVO);
                downloadedData.put(actionVO.getExDate(), downloadedVOs);
                companyDAO.writeActionData(actionVO.getStock(), downloadedData);
            }
        }
    }
*/
/*
    private static void to_list_Missing_downloaded_comparing_entered() throws Exception {
        AppLoader.initConsoleLogger();
        TreeMap<PMDate, Vector<CompanyActionVO>> logDetails = new TreeMap<PMDate, Vector<CompanyActionVO>>();
        LoadTransData.loadCompanyActionData(logDetails);
        CompanyDAO companyDAO = new CompanyDAO();
        TreeSet<CompanyActionVO> newSet = new TreeSet<CompanyActionVO>(new Comparator<CompanyActionVO>() {

            public int compare(CompanyActionVO o1, CompanyActionVO o2) {
                int retVal = o1.getExDate().compareTo(o2.getExDate());
                if (retVal == 0) {
                    retVal = o1.getStock().compareTo(o2.getStock());
                    if (retVal == 0) {
                        return o1.getAction().compareTo(o2.getAction());
                    } else {
                        return retVal;
                    }
                } else {
                    return retVal;
                }
            }

        });
        for (Vector<CompanyActionVO> actionVOs : logDetails.values()) {
            for (CompanyActionVO actionVO : actionVOs) {
                Hashtable<PMDate, Vector<CompanyActionVO>> downloadedData = companyDAO.getActionData(actionVO.getStock());
                if (downloadedData == null) {
                    newSet.add(actionVO);
                    continue;
                }
                Vector<CompanyActionVO> downloadedVOs = downloadedData.get(actionVO.getExDate());
                if (downloadedVOs == null) {
                    newSet.add(actionVO);
                    continue;
                }

                boolean present = false;
                for (CompanyActionVO downloadedVO : downloadedVOs) {
                    if (actionVO.equals(downloadedVO)) {
                        present = true;
                        break;
                    }
                }
                if (!present) newSet.add(actionVO);
            }
        }
        for(CompanyActionVO actionVO: newSet) {
            System.out.println(actionVO.toWrite());
        }

    }

    static String getUniqueKey(CompanyActionVO actionVO) {
        return actionVO.getExDate() + actionVO.getStock()
                + actionVO.getAction();
    }
*/

/*
    private static void syncLog_CompanyActionDownload_PortfolioData() {
//		AppLoader.initConsoleLogger();
        TreeMap logDetails = new TreeMap();
        try {
            LoadTransData.loadCompanyActionData(logDetails);
            CompanyDAO companyDAO = new CompanyDAO();
            for (Object object : logDetails.values()) {
//				System.out.println(object);
                Vector<CompanyActionVO> list = (Vector<CompanyActionVO>) object;
                for (CompanyActionVO actionVO : list) {

                    Hashtable<PMDate, Vector<CompanyActionVO>> actionData = companyDAO.getActionData(actionVO.getStock());
                    if (!isExists(actionVO, actionData)) {
                        System.out.print(actionVO);
//						Vector<CompanyActionVO> vector = new Vector<CompanyActionVO>();
//						vector.add(actionVO);
//						actionData.put(actionVO.getExDate(), vector);
//						companyDAO.writeActionData(actionVO.getStock(), actionData);

                    }
                }
            }
            System.out.println("------------------------------------------" + logDetails);
            Vector<String> stockMasterList = Helper.getStockMasterList();
//			stockMasterList.add("IVRCLINFRA");

//			CompanyBO companyBO = new CompanyBO();
//			for (String stockCode : stockMasterList) {
//				Hashtable<PMDate, Vector<CompanyActionVO>> actionData = companyDAO.getActionData(stockCode);
//				for (Vector<CompanyActionVO> actionVOs : actionData.values()) {
//					for (CompanyActionVO actionVO : actionVOs) {
////						if (actionVO.getAction() == COMPANY_ACTION_TYPE.DividentFunction) {
//							Vector<CompanyActionVO> vector = (Vector<CompanyActionVO>) logDetails.get(actionVO.getExDate());
//							boolean present = false;
//							if (vector != null) {
//								for (CompanyActionVO actionVO2 : vector) {
//									if (actionVO.equals(actionVO2)) {
//										present = true;
//										break;
//									}
//								}
//							}
//							if (!present) {
//								System.out.print("Missing in PM --> " + actionVO);
//								companyBO.doAction(actionVO, false);
//							}
////						}
//					}
//				}
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

*/

    private static boolean isExists
            (CompanyActionVO
                    actionVO, Hashtable<PMDate, Vector<CompanyActionVO>> actionData) {
        PMDate prevMonth = getNewDate(actionVO.getExDate(), -1);
        PMDate nextMonth = getNewDate(actionVO.getExDate(), 1);

        for (PMDate date : actionData.keySet()) {
            if (date.after(prevMonth) && date.before(nextMonth)) {
                if (!date.equals(actionVO.getExDate())) {
                    System.out.println(actionVO.getStockCode() + " OldDate : " + actionVO.getExDate() + "NewDate : " + date);
                }
                return true;
            }
        }
        return false;
    }

    private static PMDate getNewDate
            (PMDate
                    date, int diffMonth) {
        PMDate retVal = (PMDate) date.clone();
        retVal.month += diffMonth;
        if (retVal.month == 0) {
            retVal.year--;
            retVal.month = 12;
        }
        if (retVal.month == 13) {
            retVal.year++;
            retVal.month = 1;
        }
        return retVal;
    }

    private static void myTaskPane
            () {
        JFrame frame = new JFrame();
        JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();
        JXTaskPane panel = new JXTaskPane();
        panel.setTitle("MyTask");
        panel.add(new JLabel("123"));
        panel.add(new JLabel("asd"));
        panel.setSpecial(true);
        taskPaneContainer.add(panel);
        frame.setContentPane(taskPaneContainer);
        frame.pack();
        frame.setVisible(true);


    }

    private static void datePicker
            () {
        JFrame frame = new JFrame();
        frame.setContentPane(new JXDatePicker());
        frame.pack();
        frame.setVisible(true);
    }


    private static Action createDeleteFileAction
            () {

        return new Action() {

            public Object getValue(String key) {
                return null;
            }

            public void putValue(String key, Object value) {

            }

            public void setEnabled(boolean b) {

            }

            public boolean isEnabled() {
                return false;
            }

            public void addPropertyChangeListener(PropertyChangeListener listener) {

            }

            public void removePropertyChangeListener(PropertyChangeListener listener) {

            }

            public void actionPerformed(ActionEvent e) {

            }

        };
    }

    private static Action createRenameFileAction
            () {
        return createDeleteFileAction();
    }
}

class MyClass implements Runnable {

    public static void doSomething(int val) {
        int k = val;
        System.out.println("Enterted : " + k);
        k++;
        try {
            if (k == 1) {
                Thread.sleep(100);
            }
            if (k == 2) {
                Thread.sleep(200);
            }
            if (k == 3) {
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Exiting : " + val + " -> " + k);
    }

    public void run() {
        doSomething(val);
    }

    int val;

    public MyClass(int val) {
        this.val = val;
    }

}
