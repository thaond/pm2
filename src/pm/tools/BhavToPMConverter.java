package pm.tools;

import org.apache.log4j.Logger;
import pm.bo.QuoteBO;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IQuoteDAO;
import pm.net.nse.FileNameUtil;
import pm.net.nse.downloader.DeliveryPositionDownloader;
import pm.util.*;
import pm.util.enumlist.AppConfigWrapper;
import pm.vo.QuoteVO;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BhavToPMConverter {

    public static final String META_INPUT_FILE_HEADER = "<date>,<ticker>,<open>,<high>,<low>,<close>,<vol>";
    public static final String BHAV_COPY_FILE_HEADER = "SYMBOL,SERIES,OPEN,HIGH,LOW,CLOSE,LAST,PREVCLOSE,TOTTRDQTY,TOTTRDVAL,TIMESTAMP,";

    public static final String DELIVERY_POSITION_DIR = "DeliveryPosition";

    private static Logger logger = Logger.getLogger(BhavToPMConverter.class);

    private static SimpleDateFormat formatyyyyMMdd = new SimpleDateFormat(
            "yyyyMMdd");

    Date dateBhavLast = null;
    Date dateDeliveryLast = null;

    public void processData() {
        logger.info("Bhav To PM started processing data");
        processOrphanDeliveryPositionData();
        processFiles();
        storeLastProcessedDate();
        logger.info("Bhav To PM completed processing data");
    }

    void storeLastProcessedDate() {
        if (dateBhavLast != null) {
            Helper.saveLastBhavCopyDate(new PMDate(dateBhavLast));
        }
        if (dateDeliveryLast != null) {
            Helper.saveLastDeliveryPosDate(new PMDate(dateDeliveryLast));
        }
    }

    void processFiles() {
        logger.info("Processing files");
        Calendar stCal = Helper.getLastBhavCopyDate().getCalendar();
        stCal.add(Calendar.DATE, 1);  //skipping the last processed date
        Calendar enCal = getTodayDate();
        boolean flagWriteToMetaInput = isSaveToMetaInput();
        for (; !stCal.after(enCal); stCal.add(Calendar.DATE, 1)) {
            processDayData(stCal.getTime(), flagWriteToMetaInput);
        }
    }

    Calendar getTodayDate() {
        return Calendar.getInstance();
    }

    boolean isSaveToMetaInput() {
        return false;
    }

    void processDayData(Date date, boolean flagWriteToEOD) {
        String sDate = formatyyyyMMdd.format(date);
        Reader reader = null;
        try {
            reader = getBhavCopyAsReader(date);
            Vector<String[]> data = loadBhavCopyData(reader);
            if (data == null) {
                return;
            }
            if (loadDeliveryPositionData(date, data)) {
                dateDeliveryLast = date;
            }
            if (flagWriteToEOD) {
                writeToMetaInputFile(data, sDate);
            }
            storeData(data, sDate);
            dateBhavLast = date;
        } catch (FileNotFoundException e) {
            logger.info("No BhavCopy file for " + sDate);
            return;
        } catch (IOException e) {
            logger.error(e, e);
            return;
        } catch (ApplicationException e) {
            logger.error(e, e);
            return;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        moveFileToBackup(date, true, true);
    }

    void moveFileToBackup(Date date, boolean moveBhavFile, boolean moveDelivFile) {
        String backupFolder = Helper.backupFolder(new PMDate(date));

        if (moveBhavFile) {
            String downloadedFilePath = FileNameUtil.getEquityFilePath(date);
            File bhavSourceFile = new File(downloadedFilePath);
            File bhavDestFile = new File(backupFolder + "/" + bhavSourceFile.getName());
            bhavSourceFile.renameTo(bhavDestFile);
            if (downloadedFilePath.endsWith(".zip")) {
                new File(downloadedFilePath.substring(0, downloadedFilePath.lastIndexOf("."))).delete();
            }
        }

        if (moveDelivFile) {
            File deliveryPostionFile = new File(DeliveryPositionDownloader.getFilePath(date));
            if (deliveryPostionFile.exists()) {
                File delivDest = new File(backupFolder + "/" + deliveryPostionFile.getName());
                deliveryPostionFile.renameTo(delivDest);
            }
        }

    }

    Reader getBhavCopyAsReader(Date date) throws IOException {
        String filePath = FileNameUtil.getEquityFilePath(date);
        if (filePath.endsWith(".zip")) {
            unzip(filePath);
            filePath = filePath.substring(0, filePath.lastIndexOf("."));
        }
        return new FileReader(filePath);
    }

    private void unzip(String filePath) throws IOException {
        File zipFile = new File(filePath);
        FileInputStream fin = new FileInputStream(zipFile);
        ZipInputStream zin = new ZipInputStream(fin);
        ZipEntry ze = null;
        while ((ze = zin.getNextEntry()) != null) {
            FileOutputStream fout = new FileOutputStream(new File(zipFile.getParentFile(), ze.getName()));
            for (int c = zin.read(); c != -1; c = zin.read()) {
                fout.write(c);
            }
            zin.closeEntry();
            fout.close();
        }
        zin.close();

    }

    void storeData(Vector<String[]> data, String sDate) throws ApplicationException {
        PMDate pmDate = PMDateFormatter.parseYYYYMMDD(sDate);
        Vector<QuoteVO> quoteVOs = convertStringToQuoteVOs(data, pmDate);
        new QuoteBO().saveNseQuotes(pmDate, quoteVOs);
    }

    private Vector<QuoteVO> convertStringToQuoteVOs(Vector<String[]> data, PMDate pmDate) {
        Vector<QuoteVO> quoteVOs = new Vector<QuoteVO>();
        for (String[] strings : data) {
            QuoteVO quoteVO = new QuoteVO();
            quoteVO.setDate(pmDate);
            quoteVO.setStockCode(strings[0]);
            quoteVO.setOpen(Float.parseFloat(strings[1]));
            quoteVO.setHigh(Float.parseFloat(strings[2]));
            quoteVO.setLow(Float.parseFloat(strings[3]));
            quoteVO.setLastPrice(Float.parseFloat(strings[4]));
            quoteVO.setPrevClose(Float.parseFloat(strings[5]));
            quoteVO.setVolume(Float.parseFloat(strings[6]));
            quoteVO.setTradeValue(Float.parseFloat(strings[7]));
            quoteVO.setPerDeliveryQty(strings[8].length() > 0 ? Float.parseFloat(strings[8]) : 0);
            quoteVOs.add(quoteVO);
        }
        return quoteVOs;
    }

    void writeToMetaInputFile(Vector<String[]> dailyData, String sDate) {

        PrintWriter outWr = null;

        try {
            outWr = getWriter(sDate);
            outWr.println(META_INPUT_FILE_HEADER);
            for (int i = 0; i < dailyData.size(); i++) {
                String[] data = (String[]) dailyData.elementAt(i);
                StringBuffer sbr = new StringBuffer();
                sbr.append(sDate);
                for (int j = 0; j < 5; j++) {
                    sbr.append(",").append(data[j]);
                }
                sbr.append(",").append(data[6]);
                outWr.println(sbr.toString());
            }
        } catch (FileNotFoundException e) {
            logger.error("Error writing MetaInput file ", e);
        } finally {
            if (outWr != null) {
                outWr.close();
            }
        }
    }

    PrintWriter getWriter(String sDate) throws FileNotFoundException {
        String fileName = AppConfigWrapper.metaInputFolder.Value + "/" + sDate + ".txt";
        return new PrintWriter(fileName);
    }

    Vector<String[]> loadBhavCopyData(Reader reader) throws IOException,
            ApplicationException {
        BufferedReader inBr = new BufferedReader(reader);
        Vector<String[]> retVal = new Vector<String[]>();
        String line = inBr.readLine();
        if (!line.equals(BHAV_COPY_FILE_HEADER)) {
            throw new ApplicationException("Bhav copy file header got changed in new file!");
        }

        while ((line = inBr.readLine()) != null) {
            if (line.length() > 100) {
                logger.warn("Warning! BhavCopy file line Length exceeds : "
                        + line);
                return null;
            }
            if (line.trim().length() == 0) {
                continue;
            }

            StringTokenizer stk = new StringTokenizer(line, ",");
            if (stk.countTokens() < 10) {
                throw new ApplicationException(
                        "Bhav Copy Input file, field count error");
            }
            String name = stk.nextToken();
            String series = stk.nextToken();
            if (!series.equals("EQ") && !series.equals("BE")) {
                continue;
            }
            String open = stk.nextToken();
            String high = stk.nextToken();
            String low = stk.nextToken();
            String close = stk.nextToken();
            String last = stk.nextToken();
            String prev = stk.nextToken();
            String totQ = stk.nextToken();
            String totV = stk.nextToken();
            String data[] = new String[9];
            data[0] = name;
            data[1] = open;
            data[2] = high;
            data[3] = low;
            data[4] = close;
            data[5] = prev;
            data[6] = totQ;
            data[7] = totV;
            data[8] = "";
            retVal.add(data);
        }
        return retVal;
    }

    void processOrphanDeliveryPositionData() {
        logger.info("Processing Orphan  delivery postion data");

        if (isBhavAndDeliveryPosDateSame()) {
            return;
        }

        PMDate stDate = Helper.getLastDeliveryPosDate();
        PMDate enDate = Helper.getLastBhavCopyDate();

        IQuoteDAO quoteDAO = getQuoteDAO();
        DateIterator iterator = getDateIterator(stDate, enDate);
        skipLastSuccessfullDeliveryPositionDate(iterator);
        for (; iterator.hasNext();) {
            PMDate date = iterator.next();
            List<QuoteVO> dailyData = quoteDAO.getQuotes(date);
            if (loadDeliveryPositionData(date, dailyData)) {
                for (QuoteVO quoteVO : dailyData) {
                    if (quoteVO.getDate() != null) {
                        quoteDAO.updateQuote(quoteVO);
                    }
                }
                moveFileToBackup(date.getJavaDate(), false, true);
            }
        }
    }

    private void skipLastSuccessfullDeliveryPositionDate(DateIterator iterator) {
        iterator.next();
    }

    IQuoteDAO getQuoteDAO() {
        return DAOManager.getQuoteDAO();
    }

    DateIterator getDateIterator(PMDate stDate, PMDate enDate) {
        return new DateIterator(stDate, enDate);
    }

    boolean isBhavAndDeliveryPosDateSame() {
        return AppConfig.dateLastBhavCopy.Value
                .equals(AppConfig.dateLastDeliveryPosition.Value);
    }

    boolean loadDeliveryPositionData(Date date, Vector<String[]> dailyData) {
        Hashtable<String, String> htDelivery;
        try {
            htDelivery = loadDeliveryPositionDataFromFile(getDeliveryPostionReader(date));
        } catch (FileNotFoundException e) {
            logger.warn("Delivery data missing for " + date);
            return false;
        }
        for (String[] data : dailyData) {
            String devPer = (String) htDelivery.get(data[0]);
            if (devPer != null) {
                data[8] = devPer;
            }
        }
        return true;
    }

    boolean loadDeliveryPositionData(PMDate date, List<QuoteVO> dailyData) {
        Hashtable<String, String> htDelivery;
        try {
            htDelivery = loadDeliveryPositionDataFromFile(getDeliveryPostionReader(date.getJavaDate()));
        } catch (FileNotFoundException e) {
            logger.warn("Delivery data missing for " + date);
            return false;
        }
        for (QuoteVO quoteVO : dailyData) {
            String devPer = htDelivery.get(quoteVO.getStockCode());
            if (devPer != null) {
                quoteVO.setPerDeliveryQty(Float.parseFloat(devPer));
            }
        }
        return true;
    }

    Hashtable<String, String> loadDeliveryPositionDataFromFile(Reader reader) {
        Hashtable<String, String> htDelivery = new Hashtable<String, String>();
        BufferedReader inBr = null;
        String line = null;
        try {
            inBr = new BufferedReader(reader);
            inBr.readLine();
            inBr.readLine();
            inBr.readLine();
            inBr.readLine();
            while ((line = inBr.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (!line.startsWith("20")) {
                    continue;
                }
                StringTokenizer stk = new StringTokenizer(line, ",");
                stk.nextToken(); // skip - Record Type
                stk.nextToken(); // skip - Sr No
                String name = stk.nextToken();
                stk.nextToken(); // skip - Type
                stk.nextToken(); // skip - Quantity Traded
                stk.nextToken(); // skip - Deliverable Quantity
                String delivPer = stk.nextToken();
                htDelivery.put(name, delivPer);
            }
        } catch (NoSuchElementException e) {
            logger.error("Processing line : " + line);
            logger.error(e, e);
            return null;
        } catch (IOException e) {
            logger.error(e, e);
            return null;
        } finally {
            if (inBr != null) {
                try {
                    inBr.close();
                } catch (IOException e1) {
                }
            }
        }
        return htDelivery;
    }

    Reader getDeliveryPostionReader(Date date) throws FileNotFoundException {
        return new FileReader(DeliveryPositionDownloader.getFilePath(date));
    }


}
