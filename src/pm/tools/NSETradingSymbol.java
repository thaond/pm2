package pm.tools;

public class NSETradingSymbol {

    //TODO modify to DB

    private static String LAST_RUN_SYMBOL_CHANGE = "NSESymbolChange";

    public static void main(String[] args) {
//        updateSymbolChange();
    }

    /**
     *
     */
//    public static void updateSymbolChange() {
//        PMDate lastRunDate = getLastRunDate();
//        Hashtable<String,String> newSymbolList = getUpdatedList(lastRunDate);
//        if (newSymbolList.size() == 0) return;
//        updatePortfolioTransaction(newSymbolList);
//        updateMasterStockList(newSymbolList);
//        updateICICIMapping(newSymbolList);
//        updateYahooMapping(newSymbolList);
//        updateFinDetails(newSymbolList);
//        updateEODData(newSymbolList);
//        saveLastRunDate();
//    }
//
//    /**
//     * @param newSymbolList
//     */
//    private static void updateEODData(Hashtable<String, String> newSymbolList) {
//        File dir = new File(AppConfig.EODDataDir.Value);
//        File[] tradeFileList = dir.listFiles(new FileFilter() {
//            public boolean accept(File pathname) {
//                String name = pathname.getName();
//                return name.endsWith(QuoteDAO.QUOTE_FILE_EXTN);
//            }
//        });
//        int extnLen = QuoteDAO.QUOTE_FILE_EXTN.length();
//        for (File file : tradeFileList) {
//            String fileName = file.getName();
//            String fileBaseName =  fileName.substring(0,fileName.length() - extnLen);
//            if (newSymbolList.containsKey(fileBaseName)) {
//                String newName = newSymbolList.get(fileBaseName);
//                File newFile = new File(file.getParent()+"/"+newName+QuoteDAO.QUOTE_FILE_EXTN);
//                if (newFile.exists()) mergeFileContents(file,newFile,true);
//                else file.renameTo(newFile);
//            }
//        }
//    }
//
//    private static void mergeFileContents(File oldFile, File newFile, boolean skipHeader) {
//        PrintWriter pw = null;
//        BufferedReader br = null;
//        try {
//            pw = new PrintWriter(new FileWriter(newFile,true));
//            br = new BufferedReader(new FileReader(oldFile));
//            if (skipHeader) br.readLine();
//            String line;
//            while ((line = br.readLine()) != null) {
//                pw.println(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (br != null)	try { br.close(); } catch (IOException e) {}
//            if (pw != null)	pw.close();
//        }
//        oldFile.delete();
//    }
//
//    /**
//     * @param newSymbolList
//     */
//    private static void updateFinDetails(Hashtable<String, String> newSymbolList) {
//
//    }
//
//    /**
//     * @param newSymbolList
//     */
//    private static void updateYahooMapping(Hashtable<String, String> newSymbolList) {
//
//    }
//
//    /**
//     * @param newSymbolList
//     */
//    private static void updateICICIMapping(Hashtable<String, String> newSymbolList) {
//        try {
//            Properties iciciMapping = new Properties();
//            File file = new File(AppConfig.PORTFOLIO_DATA_DIR.Value+"/"+AppConst.ICICI_MAPPING_FILE);
//            FileInputStream fis = new FileInputStream(file);
//            iciciMapping.load(fis);
//            fis.close();
//            for (String oldName : newSymbolList.keySet()) {
//                if (iciciMapping.containsKey(oldName)) {
//                    String newName = newSymbolList.get(oldName);
//                    String val = (String)iciciMapping.remove(oldName);
//                    System.out.println(newName+"  "+val);
//                    iciciMapping.put(newName, val);
//                }
//            }
//            FileOutputStream fos = new FileOutputStream(file);
//            iciciMapping.store(fos, null);
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * @param newSymbolList
//     */
//    private static void updateMasterStockList(Hashtable<String, String> newSymbolList) {
//        File file = new File(AppConfig.PORTFOLIO_DATA_DIR.Value+"/"+STOCK_MASTER_FILE);
//        for (String oldName : newSymbolList.keySet()) {
//            String newName = newSymbolList.get(oldName);
//            FindReplace.doFindReplace(file, oldName, newName);
//        }
//    }
//
//    /**
//     */
//    private static void saveLastRunDate() {
//        Properties prop = new Properties();
//        try {
//            FileInputStream fis = new FileInputStream(AppConfig.PORTFOLIO_DATA_DIR.Value+"/"+LASTRUNFILE);
//            prop.load(fis);
//            fis.close();
//        } catch (FileNotFoundException e) {
////			e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        prop.put(LAST_RUN_SYMBOL_CHANGE,PMDateFormatter.formatYYYYMMDD(new PMDate()));
//        try {
//            FileOutputStream fos = new FileOutputStream(AppConfig.PORTFOLIO_DATA_DIR.Value+"/"+LASTRUNFILE);
//            prop.store(fos, "Last Run Details");
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * @param newSymbolList
//     */
//    private static void updatePortfolioTransaction(Hashtable<String, String> newSymbolList) {
//        IAccountDAO dao = DBManager.getAccountDAO();
//        List<PortfolioDetailsVO> portfolioList = dao.getPorfolioList();
//        List<TradingAccountVO> tradingAcList = dao.getTradingAccList();
//        FileFilter fileFilter = new FileFilter() {
//                public boolean accept(File pathname) {
//                    String name = pathname.getName();
//                    return name.endsWith(".stk") || name.endsWith(".trd");
//                }
//            };
//        //Delivery based Trading
//        for (PortfolioDetailsVO pdVO : portfolioList) {
//            for (TradingAccountVO tradingAc : tradingAcList) {
//                File dir = new File(AppConfig.PORTFOLIO_DATA_DIR.Value+"/"+pdVO.getName()
//                            +"/"+tradingAc.getName());
//                File[] tradeFileList = dir.listFiles(new FileFilter() {
//                    public boolean accept(File pathname) {
//                        String name = pathname.getName();
//                        return name.endsWith(".stk");
//                    }
//                });
//
//                for (File file : tradeFileList) {
//                    String fileName = file.getName();
//                    String fileBaseName =  fileName.substring(0,fileName.length() - 4);
//                    String fileExn = fileName.substring(fileName.length() - 4);
//                    if (newSymbolList.containsKey(fileBaseName)) {
//                        String newName = newSymbolList.get(fileBaseName);
//                        System.out.println(newName);
//                        File newFile = new File(file.getParent()+"/"+newName+fileExn);
//                        file.renameTo(newFile);
//                    }
//                }
//            }
//        }
//        //Day trading
//        for (PortfolioDetailsVO pdVO : portfolioList) {
//            for (TradingAccountVO tradingAc : tradingAcList) {
//                File dir = new File(AppConfig.PORTFOLIO_DATA_DIR.Value+"/"+pdVO.getName()
//                            +"/"+tradingAc.getName());
//                File[] tradeFileList = dir.listFiles(new FileFilter() {
//                    public boolean accept(File pathname) {
//                        String name = pathname.getName();
//                        return name.endsWith(".trd");
//                    }
//                });
//                for (File file : tradeFileList) {
//                    for (String oldName : newSymbolList.keySet()) {
//                        String newName = newSymbolList.get(oldName);
//                        FindReplace.doFindReplace(file, oldName, newName);
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * @param file
//     * @param newName
//     * @param oldName
//     */
//    private static void processAllFiles(File dir, String oldName, String newName,FileFilter fileFilter) {
//        File[] fileList = dir.listFiles(fileFilter);
//        for (File file : fileList) {
//            FindReplace.doFindReplace(file,oldName,newName);
//        }
//    }
//
//    /**
//     * @param lastRunDate
//     * @return
//     */
//    private static Hashtable<String,String> getUpdatedList(PMDate lastRunDate) {
//        Vector<String[]> fullList = NSESymbolChangeDownloader.download();
//        Hashtable<String,String> retVal = new Hashtable<String,String>();
//        for(String[] data : fullList) {
//            try {
//                if (PMDateFormatter.parseDD_Mmm_YY(data[3]).after(lastRunDate)) {
//                    retVal.put(data[1],data[2]);
//                }
//            } catch (ApplicationException e) {
//                e.printStackTrace();
//            }
//        }
//        return retVal;
//    }
//
//    /**
//     * @return
//     */
//    private static PMDate getLastRunDate() {
//        Properties prop = new Properties();
//        try {
//            FileInputStream fis = new FileInputStream(AppConfig.PORTFOLIO_DATA_DIR.Value+"/"+LASTRUNFILE);
//            prop.load(fis);
//            fis.close();
//        } catch (FileNotFoundException e) {
////			e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String lastrun = prop.getProperty(LAST_RUN_SYMBOL_CHANGE,"19950101");
//        try {
//            return PMDateFormatter.parseYYYYMMDD(lastrun);
//        } catch (ApplicationException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
