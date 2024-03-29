package pm.tools.converter;

import org.apache.log4j.Logger;
import pm.bo.QuoteBO;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IQuoteDAO;
import pm.net.nse.BhavFileUtil;
import pm.net.nse.downloader.DeliveryPositionDownloader;
import pm.util.*;
import pm.vo.EquityQuote;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class EquityBhavToPMConverter {

    public static final String BHAV_COPY_FILE_HEADER = "SYMBOL,SERIES,OPEN,HIGH,LOW,CLOSE,LAST,PREVCLOSE,TOTTRDQTY,TOTTRDVAL,TIMESTAMP,";

    private static Logger logger = Logger.getLogger(EquityBhavToPMConverter.class);

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
        Calendar stCal = latestBhavCopyDate().getCalendar();
        stCal.add(Calendar.DATE, 1);  //skipping the last processed date
        Calendar enCal = getTodayDate();
        for (; !stCal.after(enCal); stCal.add(Calendar.DATE, 1)) {
            processDayData(stCal.getTime());
        }
    }

    protected PMDate latestBhavCopyDate() {
        return Helper.getLastBhavCopyDate();
    }

    Calendar getTodayDate() {
        return Calendar.getInstance();
    }

    void processDayData(Date date) {
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

    protected void moveFileToBackup(Date date, boolean moveBhavFile, boolean moveDelivFile) {

        if (moveBhavFile) {
            String downloadedFilePath = BhavFileUtil.getEquityFilePath(date);
            BhavFileUtil.moveFileToBackup(new PMDate(date), downloadedFilePath);
        }

        if (moveDelivFile) {
            BhavFileUtil.moveFileToBackup(new PMDate(date), DeliveryPositionDownloader.getFilePath(date));
        }

    }

    Reader getBhavCopyAsReader(Date date) throws IOException {
        String filePath = BhavFileUtil.getEquityFilePath(date);
        return BhavFileUtil.openReader(filePath);
    }

    void storeData(Vector<String[]> data, String sDate) throws ApplicationException {
        PMDate pmDate = PMDateFormatter.parseYYYYMMDD(sDate);
        Vector<EquityQuote> quoteVOs = convertStringToQuoteVOs(data, pmDate);
        new QuoteBO().saveNseQuotes(pmDate, quoteVOs);
    }

    private Vector<EquityQuote> convertStringToQuoteVOs(Vector<String[]> data, PMDate pmDate) {
        Vector<EquityQuote> quoteVOs = new Vector<EquityQuote>();
        for (String[] strings : data) {
            EquityQuote quoteVO = new EquityQuote();
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
        PMDate enDate = latestBhavCopyDate();

        IQuoteDAO quoteDAO = getQuoteDAO();
        DateIterator iterator = getDateIterator(stDate, enDate);
        skipLastSuccessfullDeliveryPositionDate(iterator);
        for (; iterator.hasNext();) {
            PMDate date = iterator.next();
            List<EquityQuote> dailyData = quoteDAO.getQuotes(date);
            if (loadDeliveryPositionData(date, dailyData)) {
                for (EquityQuote quoteVO : dailyData) {
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

    boolean loadDeliveryPositionData(PMDate date, List<EquityQuote> dailyData) {
        Hashtable<String, String> htDelivery;
        try {
            htDelivery = loadDeliveryPositionDataFromFile(getDeliveryPostionReader(date.getJavaDate()));
        } catch (FileNotFoundException e) {
            logger.warn("Delivery data missing for " + date);
            return false;
        }
        for (EquityQuote quoteVO : dailyData) {
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
