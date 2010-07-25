package pm;

import junit.framework.Assert;
import pm.bo.StockMasterBO;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IQuoteDAO;
import pm.util.PMDate;
import pm.vo.EODStatistics;
import pm.vo.EquityQuote;
import pm.vo.StockVO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestHelper {
    public static float findMovAvg(List<EquityQuote> quotes, int days) {
        float movAvg = 0f;
        int startIndex = quotes.size() - days;
        if (startIndex < 0) startIndex = 0;
        int count = 0;
        for (int index = startIndex; index < quotes.size(); index++) {
            count++;
            EquityQuote quote = quotes.get(index);
            movAvg += quote.getAdjustedClose();
        }
        return movAvg / count;

    }

    public static float findLow(List<EquityQuote> quotes, int days) {
        float low = 100000f;
        int startIndex = quotes.size() - days;
        if (startIndex < 0) startIndex = 0;
        for (int index = startIndex; index < quotes.size(); index++) {
            EquityQuote quote = quotes.get(index);
            if (quote.getAdjustedClose() < low) {
                low = quote.getAdjustedClose();
            }
        }
        return low;
    }

    public static float findHigh(List<EquityQuote> quotes, int days) {
        float high = 0f;
        int startIndex = quotes.size() - days;
        if (startIndex < 0) startIndex = 0;
        for (int index = startIndex; index < quotes.size(); index++) {
            EquityQuote quote = quotes.get(index);
            if (quote.getAdjustedClose() > high) {
                high = quote.getAdjustedClose();
            }
        }
        return high;
    }

    public static void insertQuotes(String stockCode, List<PMDate> pmDates, int startPrice, int priceInc) {
        float currPrice = startPrice;
        IQuoteDAO quoteDAO = DAOManager.getQuoteDAO();
        for (PMDate pmDate : pmDates) {
            quoteDAO.insertQuote(new EquityQuote(stockCode, pmDate, currPrice, currPrice, currPrice, currPrice, 0f, 0f, 1000f, 100f));
            currPrice += priceInc;
        }
    }

    public static void insertStocks(String... stockCodes) {
        StockMasterBO stockMasterBO = new StockMasterBO();
        for (String stockCode : stockCodes) {
            stockMasterBO.insertNewStock(stockCode);
        }
    }

    public static List<PMDate> insertWeekDays(int startDate, int endDate) {
        List<PMDate> pmDates = new ArrayList<PMDate>();
        PMDate currDate = new PMDate(startDate);
        for (int day = 1; currDate.getIntVal() <= endDate; currDate = currDate.next(), day++) {
            if (day % 6 == 0) continue;
            if (day % 7 == 0) {
                day = 0;
                continue;
            }
            pmDates.add(currDate);
        }
        DAOManager.getDateDAO().insertDates(pmDates);
        return pmDates;
    }

    public static void validateStatistics(StockVO stockVO, PMDate statDate, EODStatistics eodStatistics, PMDate quoteStartDate) {
        List<EquityQuote> quotes = DAOManager.getQuoteDAO().getQuotes(stockVO.getStockCode(), quoteStartDate, statDate);

        Assert.assertEquals(stockVO, eodStatistics.getStock());
        Assert.assertEquals(statDate, eodStatistics.getDate());
        int daysFor52Weeks = 261;
        Assert.assertEquals(findHigh(quotes, 5), eodStatistics.getHigh5D(), .01);
        Assert.assertEquals(findHigh(quotes, 20), eodStatistics.getHigh20D(), .001);
        Assert.assertEquals(findHigh(quotes, daysFor52Weeks), eodStatistics.getHigh52Week(), .001);
        Assert.assertEquals(findHigh(quotes, quotes.size()), eodStatistics.getHighLifeTime(), .001);

        Assert.assertEquals(findLow(quotes, 5), eodStatistics.getLow5D(), .001);
        Assert.assertEquals(findLow(quotes, 20), eodStatistics.getLow20D(), .001);
        Assert.assertEquals(findLow(quotes, daysFor52Weeks), eodStatistics.getLow52Week(), .001);
        Assert.assertEquals(findLow(quotes, quotes.size()), eodStatistics.getLowLifeTime(), .001);

        Assert.assertEquals(findMovAvg(quotes, 10), eodStatistics.getMoving10DAverage(), .001);
        Assert.assertEquals(findMovAvg(quotes, 50), eodStatistics.getMoving50DAverage(), .001);
        Assert.assertEquals(findMovAvg(quotes, 200), eodStatistics.getMoving200DAverage(), .001);
    }

    public static File createZipFile(String content, String zipFilePath) throws IOException {
        File file = new File(zipFilePath.substring(0, zipFilePath.lastIndexOf('.')));
        file.createNewFile();
        storeContent(file, content);
        compressFile(file, zipFilePath);
        file.delete();
        return file;
    }

    private static void compressFile(File file, String zipFilePath) throws IOException {
        FileOutputStream fout = new FileOutputStream(zipFilePath);
        ZipOutputStream zout = new ZipOutputStream(fout);
        ZipEntry ze = new ZipEntry(file.getName());
        FileInputStream fin = new FileInputStream(file);
        try {
            zout.putNextEntry(ze);
            for (int c = fin.read(); c != -1; c = fin.read()) {
                zout.write(c);
            }
        } finally {
            fin.close();
        }
        zout.close();
    }

    private static void storeContent(File file, String content) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        pw.print(content);
        pw.close();
    }
}
