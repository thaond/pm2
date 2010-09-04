package pm.tools.converter;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang.builder.EqualsBuilder;
import pm.TestHelper;
import pm.bo.StockMasterBO;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.PMDBTestCase;
import pm.net.nse.BhavFileUtil;
import pm.util.Helper;
import pm.util.PMDate;
import pm.util.enumlist.FOTYPE;
import pm.vo.FOQuote;
import pm.vo.StockVO;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pm.util.enumlist.FOTYPE.*;

public class FandOBhavToPMConverterTest extends PMDBTestCase {

    public FandOBhavToPMConverterTest(String string) {
        super(string, "EmptyData.xml");
    }

    public void testParseToParseALineAndCreateQuoteObject() throws Exception {
        FOQuote foQuote = new FandOBhavToPMConverter().parse(new String[]{"FUTIDX", "STOCK1", "29-Apr-2010", "0", "XX", "9520", "9551.95", "9475.05", "9543.1", "9543.1", "16161", "76914.07", "1091150", "63500", "01-APR-2010"});
        FOQuote expected = new FOQuote(new PMDate(1, 4, 2010), new StockVO("STOCK1"), Future, 9520f, 9551.95f, 9475.05f, 9543.1f, 16161, 1091150, 63500, 0f, new PMDate(29, 4, 2010));
        assertTrue(EqualsBuilder.reflectionEquals(expected, foQuote));
    }

    public void testProcessFileToReadBhavFileContentAndStoreInDB() throws IOException {
        PMDate date = new PMDate(1, 4, 2010);
        List<FOQuote> quotes = createFOInputFileAndInsertStockCodeAndDateAndReturnExpectedQuotes(date);

        new FandOBhavToPMConverter().processFile(date);

        List<FOQuote> savedQuotes = DAOManager.fandoDAO().getQuotes(date);
        assertEquals(quotes.size(), savedQuotes.size());

        for (FOQuote quote : quotes) {
            FOQuote savedQuote = find(savedQuotes, quote.getExpiryDate(), quote.getFotype(), quote.getStockVO().getStockCode());
            assertTrue(EqualsBuilder.reflectionEquals(quote, savedQuote, new String[]{"stockVO"}));
        }

        deleteInputFile(date);
    }

    private List<FOQuote> createFOInputFileAndInsertStockCodeAndDateAndReturnExpectedQuotes(PMDate date) throws IOException {
        StockVO stockVO1 = new StockVO("STOCK1");
        StockVO stockVO2 = new StockVO("STOCK2");
        StockMasterBO stockMasterBO = new StockMasterBO();
        stockMasterBO.insertNewStock(stockVO1.getStockCode());
        stockMasterBO.insertNewStock(stockVO2.getStockCode());
        DAOManager.getDateDAO().insertDate(date);
        createInputFile(date);
        List<FOQuote> quotes = new ArrayList<FOQuote>();
        quotes.add(new FOQuote(date, stockVO1, Future, 9520f, 9551.95f, 9475.05f, 9543.1f, 16161, 1091150, 63500, 0f, new PMDate(29, 4, 2010)));
        quotes.add(new FOQuote(date, stockVO2, Future, 1165.5f, 1212f, 1165f, 1205.1f, 9711, 3042400, 106000, 0f, new PMDate(29, 4, 2010)));
        return quotes;
    }

    public void testProcessFileToAlertAnyDateMisMatch() {
        FandOBhavToPMConverter converter = new FandOBhavToPMConverter() {
            @Override
            Reader reader(PMDate date) throws IOException {
                return new StringReader("INSTRUMENT,SYMBOL,EXPIRY_DT,STRIKE_PR,OPTION_TYP,OPEN,HIGH,LOW,CLOSE,SETTLE_PR,CONTRACTS,VAL_INLAKH,OPEN_INT,CHG_IN_OI,TIMESTAMP," + "\n" +
                        "FUTIDX,STOCK1,29-Apr-2010,0,XX,9520,9551.95,9475.05,9543.1,9543.1,16161,76914.07,1091150,63500,01-APR-2011,");
            }

            @Override
            void save(List<FOQuote> quotes) {
            }
        };
        try {
            converter.processFile(new PMDate(1, 1, 2001));
            fail("Should have alerted date mis-match : 01-APR-2011 and 1 1 2001");
        } catch (RuntimeException e) {
        }
    }

    public void testProcessFileToHandleMissingQuoteForADay() {
        FandOBhavToPMConverter converter = new FandOBhavToPMConverter() {
            @Override
            List<FOQuote> parseFile(PMDate date, CSVReader csvReader) throws IOException {
                fail("Should not come here");
                return null;
            }
        };
        converter.processFile(new PMDate(1, 1, 2001));
    }

    private void deleteInputFile(PMDate date) {
        File backupFolder = new File(Helper.backupFolder(date));
        File[] files = backupFolder.listFiles();
        for (File file : files) {
            file.delete();
        }
        backupFolder.delete();
    }

    private FOQuote find(List<FOQuote> quotes, PMDate expiryDate, FOTYPE fotype, String stockCode) {
        for (FOQuote quote : quotes) {
            if (quote.getExpiryDate().equals(expiryDate) && quote.getFotype() == fotype && quote.getStockVO().getStockCode().equals(stockCode)) {
                return quote;
            }
        }
        return null;
    }


    private void createInputFile(PMDate date) throws IOException {
        String filePath = BhavFileUtil.getFandOFilePath(date.getJavaDate());
        String content = "INSTRUMENT,SYMBOL,EXPIRY_DT,STRIKE_PR,OPTION_TYP,OPEN,HIGH,LOW,CLOSE,SETTLE_PR,CONTRACTS,VAL_INLAKH,OPEN_INT,CHG_IN_OI,TIMESTAMP," + "\n" +
                "FUTIDX,STOCK1,29-Apr-2010,0,XX,9520,9551.95,9475.05,9543.1,9543.1,16161,76914.07,1091150,63500,01-APR-2010," + "\n" +
                "FUTSTK,STOCK2,29-Apr-2010,0,XX,1165.5,1212,1165,1205.1,1205.1,9711,46392.58,3042400,106000,01-APR-2010," + "\n";
        TestHelper.createZipFile(content, filePath);
    }


    public void testFindTypeToHandleFuturesAndOptions() throws Exception {
        FandOBhavToPMConverter converter = new FandOBhavToPMConverter();
        assertEquals(Future, converter.findType("FUTIDX", "XX"));
        assertEquals(Future, converter.findType("FUTSTK", "XX"));
        assertEquals(Put, converter.findType("OPTIDX", "PE"));
        assertEquals(Call, converter.findType("OPTIDX", "CE"));
        assertEquals(Put, converter.findType("OPTSTK", "PA"));
        assertEquals(Call, converter.findType("OPTSTK", "CA"));
    }

    public void testProcessFileToAlertOnColumnChange() throws IOException {
        PMDate date = new PMDate(2, 4, 2010);
        String filePath = BhavFileUtil.getFandOFilePath(date.getJavaDate());
        String content = "SOMECHANGE,INSTRUMENT,EXPIRY_DT,STRIKE_PR,OPTION_TYP,OPEN,HIGH,LOW,CLOSE,SETTLE_PR,CONTRACTS,VAL_INLAKH,OPEN_INT,CHG_IN_OI,TIMESTAMP," + "\n";
        TestHelper.createZipFile(content, filePath);
        try {
            new FandOBhavToPMConverter().processFile(date);
            fail("should have failed on column change");
        } catch (RuntimeException e) {
            assertEquals("F&O Bhav file format changed", e.getMessage());
        } catch (Exception e) {
            fail("should have thrown only runtime exception");
        }
    }

    public void testProcessFileToMoveProcessedFileToBackupFolder() throws IOException {
        PMDate date = new PMDate(1, 4, 2010);
        createFOInputFileAndInsertStockCodeAndDateAndReturnExpectedQuotes(date);
        new FandOBhavToPMConverter().processFile(date);
        String filePath = BhavFileUtil.getFandOFilePath(date.getJavaDate());
        File inputFile = new File(filePath);
        assertFalse(inputFile.exists());
        assertTrue(new File(Helper.backupFolder(date) + "/" + inputFile.getName()).exists());
        deleteInputFile(date);
    }

    public void testProcessFileNotToMoveFailedProcessingFilesToBackupFolder() throws IOException {
        PMDate date = new PMDate(2, 4, 2010);
        String filePath = BhavFileUtil.getFandOFilePath(date.getJavaDate());
        String content = "SOMECHANGE,INSTRUMENT,EXPIRY_DT,STRIKE_PR,OPTION_TYP,OPEN,HIGH,LOW,CLOSE,SETTLE_PR,CONTRACTS,VAL_INLAKH,OPEN_INT,CHG_IN_OI,TIMESTAMP," + "\n";
        TestHelper.createZipFile(content, filePath);
        try {
            new FandOBhavToPMConverter().processFile(date);
        } catch (RuntimeException e) {
        }
        assertTrue(new File(filePath).exists());
    }

    public void testProcessFilesToLoadAllDaysDataUptoLatestFandOAvailable() {
        List<PMDate> dates = Arrays.asList(new PMDate(1, 1, 2010), new PMDate(3, 1, 2010), new PMDate(4, 1, 2010));
        DAOManager.getDateDAO().insertDates(dates);
        final List<PMDate> processedDates = new ArrayList<PMDate>();
        FandOBhavToPMConverter converter = new FandOBhavToPMConverter() {
            @Override
            public void processFile(PMDate date) {
                processedDates.add(date);
            }
        };
        converter.processFiles();
        for (int i = 0; i < dates.size(); i++) {
            assertEquals(dates.get(i), processedDates.get(i));
        }
    }

    public void testProcessFilesToStartFromNextDayOfAvailability() {
        final List<PMDate> dates = Arrays.asList(new PMDate(1, 1, 2010), new PMDate(3, 1, 2010), new PMDate(4, 1, 2010));
        DAOManager.getDateDAO().insertDates(dates);
        final List<PMDate> processedDates = new ArrayList<PMDate>();
        FandOBhavToPMConverter converter = new FandOBhavToPMConverter() {
            @Override
            public void processFile(PMDate date) {
                processedDates.add(date);
            }

            @Override
            PMDate latestQuoteDate() {
                return dates.get(0);
            }
        };
        converter.processFiles();
        assertEquals(2, processedDates.size());
        assertEquals(dates.get(1), processedDates.get(0));
        assertEquals(dates.get(2), processedDates.get(1));
    }
}
