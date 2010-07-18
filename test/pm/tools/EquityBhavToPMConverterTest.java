package pm.tools;

import org.jmock.MockObjectTestCase;
import pm.AppLoader;
import pm.net.nse.FileNameUtil;
import pm.net.nse.downloader.DeliveryPositionDownloader;
import pm.util.AppConfig;
import pm.util.ApplicationException;
import pm.util.Helper;
import pm.util.PMDate;
import pm.vo.QuoteVO;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EquityBhavToPMConverterTest extends MockObjectTestCase {

    protected void setUp() throws Exception {
        AppLoader.initConsoleLogger();
    }

    public void testGetBhavCopyAsReaderForZipFile() throws IOException {
        EquityBhavToPMConverter converter = new EquityBhavToPMConverter();
        String content = "some content";
        Date date = new PMDate(10, 12, 2009).getJavaDate();

        String zipFilePath = FileNameUtil.getEquityFilePath(date);
        File file = new File(zipFilePath.substring(0, zipFilePath.lastIndexOf('.')));
        file.createNewFile();
        storeContent(file, content);
        compressFile(file, zipFilePath);
        file.delete();

        Reader reader = converter.getBhavCopyAsReader(date);
        String fileContent = getContent(reader);

        assertEquals(content, fileContent);
        file.delete();
        new File(zipFilePath).delete();

    }

    private void compressFile(File file, String zipFilePath) throws IOException {
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

    private String getContent(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String content = br.readLine();
        br.close();
        return content;
    }

    private void storeContent(File file, String content) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        pw.print(content);
        pw.close();
    }

    public void testProcessData() {
        final Vector<Integer> orderList = new Vector<Integer>();
        EquityBhavToPMConverter converter = new EquityBhavToPMConverter() {
            @Override
            void processOrphanDeliveryPositionData() {
                orderList.add(new Integer(1));
            }

            @Override
            void processFiles() {
                orderList.add(new Integer(2));
            }

            @Override
            void storeLastProcessedDate() {
                orderList.add(new Integer(3));
            }
        };
        converter.processData();
        assertEquals(3, orderList.size());
        for (int i = 0; i < orderList.size(); i++) {
            assertTrue((i + 1) == orderList.elementAt(i));
        }
    }

    /*
      * Test method for 'pm.tools.BhavToPMConverter.processFiles()'
      */
    public void testProcessFiles() {
        AppConfig.dateLastBhavCopy.Value = "20051003";
        final Vector<PMDate> dateList = new Vector<PMDate>();
        EquityBhavToPMConverter converter = new EquityBhavToPMConverter() {
            @Override
            void processDayData(Date date) {
                dateList.add(new PMDate(date));
            }

            @Override
            Calendar getTodayDate() {
                Calendar cal = Calendar.getInstance();
                cal.set(2005, 9, 6);
                return cal;
            }
        };
        converter.processFiles();
        assertEquals(3, dateList.size());
        assertEquals(new PMDate(4, 10, 2005), dateList.elementAt(0));
        assertEquals(new PMDate(5, 10, 2005), dateList.elementAt(1));
        assertEquals(new PMDate(6, 10, 2005), dateList.elementAt(2));
    }

    /*
      * Test method for 'pm.tools.BhavToPMConverter.processDayData(Date, boolean)'
      */
    public void testProcessDayData() {
        final StringReader dummyReader = new StringReader("DUMMY");
        final Vector<String[]> dummyData = new Vector<String[]>();
        final Vector<Integer> orderList = new Vector<Integer>();
        final Date dummyDate = Calendar.getInstance().getTime();

        EquityBhavToPMConverter converter = new EquityBhavToPMConverter() {
            @Override
            Reader getBhavCopyAsReader(Date date) throws FileNotFoundException {
                orderList.add(1);
                return dummyReader;
            }

            @Override
            Vector<String[]> loadBhavCopyData(Reader reader) throws IOException, ApplicationException {
                orderList.add(2);
                assertEquals(dummyReader, reader);
                return dummyData;
            }

            @Override
            boolean loadDeliveryPositionData(Date date, Vector<String[]> dailyData) {
                orderList.add(3);
                assertEquals(dummyData, dailyData);
                return true;
            }

            @Override
            void storeData(Vector<String[]> data, String sDate) {
                orderList.add(4);
                assertEquals(dummyData, data);
            }

            @Override
            void moveFileToBackup(Date date, boolean moveBhavFile, boolean moveDelivFile) {
                orderList.add(5);
                assertEquals(dummyDate, date);
            }
        };
        converter.processDayData(dummyDate);
        for (int i = 0; i < orderList.size(); i++) {
            assertTrue((i + 1) == orderList.elementAt(i));
        }
        assertEquals(dummyDate, converter.dateBhavLast);
        assertEquals(dummyDate, converter.dateDeliveryLast);
    }

    /*
      * Test method for 'pm.tools.BhavToPMConverter.processDayData(Date, boolean)'
      */
    public void testProcessDayDataForMissingBhavCopyFile() {

        EquityBhavToPMConverter converter = new EquityBhavToPMConverter() {
            @Override
            Reader getBhavCopyAsReader(Date date) throws FileNotFoundException {
                throw new FileNotFoundException("From UnitTesting");
            }

            @Override
            Vector<String[]> loadBhavCopyData(Reader reader) throws IOException, ApplicationException {
                fail("should not come here");
                return null;
            }

            @Override
            boolean loadDeliveryPositionData(Date date, Vector<String[]> dailyData) {
                fail("should not come here");
                return true;
            }

            @Override
            void storeData(Vector<String[]> data, String sDate) {
                fail("should not come here");
            }

            @Override
            void moveFileToBackup(Date date, boolean moveBhavFile, boolean moveDelivFile) {
                fail("should not come here");
            }
        };
        converter.processDayData(new Date());
        assertNull(converter.dateBhavLast);
        assertNull(converter.dateDeliveryLast);
    }

    /*
    * Test method for 'pm.tools.BhavToPMConverter.processDayData(Date, boolean)'
    */
    public void testProcessDayDataForInvalidBhavCopyFile() {

        EquityBhavToPMConverter converter = new EquityBhavToPMConverter() {
            @Override
            Reader getBhavCopyAsReader(Date date) throws FileNotFoundException {
                return new StringReader("DUMMY");
            }

            @Override
            Vector<String[]> loadBhavCopyData(Reader reader) throws IOException, ApplicationException {
                throw new ApplicationException("From UnitTesting");
            }

            @Override
            boolean loadDeliveryPositionData(Date date, Vector<String[]> dailyData) {
                fail("should not come here");
                return true;
            }

            @Override
            void storeData(Vector<String[]> data, String sDate) {
                fail("should not come here");
            }

            @Override
            void moveFileToBackup(Date date, boolean moveBhavFile, boolean moveDelivFile) {
                fail("should not come here");
            }
        };
        converter.processDayData(new Date());
        assertNull(converter.dateBhavLast);
        assertNull(converter.dateDeliveryLast);
    }

    public void testProcessDayDataForMissingDeliveryFile() {

        final StringReader dummyReader = new StringReader("DUMMY");
        final Vector<String[]> dummyData = new Vector<String[]>();
        final Date dummyDate = Calendar.getInstance().getTime();

        EquityBhavToPMConverter converter = new EquityBhavToPMConverter() {
            @Override
            Reader getBhavCopyAsReader(Date date) throws FileNotFoundException {
                return dummyReader;
            }

            @Override
            Vector<String[]> loadBhavCopyData(Reader reader) throws IOException, ApplicationException {
                return dummyData;
            }

            @Override
            boolean loadDeliveryPositionData(Date date, Vector<String[]> dailyData) {
                return false;
            }

            @Override
            void storeData(Vector<String[]> data, String sDate) {
            }

            @Override
            void moveFileToBackup(Date date, boolean moveBhavFile, boolean moveDelivFile) {
            }
        };
        converter.processDayData(dummyDate);
        assertEquals(dummyDate, converter.dateBhavLast);
        assertNull(converter.dateDeliveryLast);
    }

    /*
      * Test method for 'pm.tools.BhavToPMConverter.loadBhavCopyData(Reader)'
      */
    public void testLoadBhavCopyData() throws IOException, ApplicationException {
        StringBuffer sb = new StringBuffer();
        sb.append("SYMBOL,SERIES,OPEN,HIGH,LOW,CLOSE,LAST,PREVCLOSE,TOTTRDQTY,TOTTRDVAL,TIMESTAMP,\n");
        sb.append("SYMBOL1,EQ,10,11,12,13,14,15,16,17,31-JAN-2006,\n");
        sb.append("\n");
        sb.append("SYMBOL2,EQ,20,21,22,23,24,25,26,27,31-JAN-2006,\n");
        StringReader reader = new StringReader(sb.toString());
        EquityBhavToPMConverter converter = new EquityBhavToPMConverter();
        Vector<String[]> loadedData = converter.loadBhavCopyData(reader);
        assertEquals(2, loadedData.size());
        assertEquals("SYMBOL1", loadedData.get(0)[0]);
        assertEquals("10", loadedData.get(0)[1]);
        assertEquals("11", loadedData.get(0)[2]);
        assertEquals("12", loadedData.get(0)[3]);
        assertEquals("13", loadedData.get(0)[4]);
        assertEquals("15", loadedData.get(0)[5]);
        assertEquals("16", loadedData.get(0)[6]);
        assertEquals("17", loadedData.get(0)[7]);
        assertEquals("", loadedData.get(0)[8]);
        assertEquals("SYMBOL2", loadedData.get(1)[0]);
    }

    public void testLoadBhavCopyDataToProcessT2TQuotes() throws IOException, ApplicationException {
        StringBuffer sb = new StringBuffer();
        sb.append("SYMBOL,SERIES,OPEN,HIGH,LOW,CLOSE,LAST,PREVCLOSE,TOTTRDQTY,TOTTRDVAL,TIMESTAMP,\n");
        sb.append("SYMBOL1,BE,10,11,12,13,14,15,16,17,31-JAN-2006,\n");
        StringReader reader = new StringReader(sb.toString());
        EquityBhavToPMConverter converter = new EquityBhavToPMConverter();
        Vector<String[]> loadedData = converter.loadBhavCopyData(reader);
        assertEquals(1, loadedData.size());
        assertEquals("SYMBOL1", loadedData.get(0)[0]);
        assertEquals("10", loadedData.get(0)[1]);
        assertEquals("11", loadedData.get(0)[2]);
        assertEquals("12", loadedData.get(0)[3]);
        assertEquals("13", loadedData.get(0)[4]);
        assertEquals("15", loadedData.get(0)[5]);
        assertEquals("16", loadedData.get(0)[6]);
        assertEquals("17", loadedData.get(0)[7]);
        assertEquals("", loadedData.get(0)[8]);
    }

    public void testLoadBhavCopyDataToCheckInvalidBhavHeader() throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("SYMBOL,OPEN,SERIES,HIGH,LOW,CLOSE,LAST,PREVCLOSE,TOTTRDQTY,TOTTRDVAL,TIMESTAMP,\n");
        sb.append("SYMBOL1,EQ,10,11,12,13,14,15,16,17,31-JAN-2006,\n");
        StringReader reader = new StringReader(sb.toString());
        EquityBhavToPMConverter converter = new EquityBhavToPMConverter();
        try {
            converter.loadBhavCopyData(reader);
            fail("Exeption is not thrown");
        } catch (ApplicationException e) {
        }
    }

    /*
      * Test method for
      * 'pm.tools.BhavToPMConverter.processOrphanDeliveryPositionData()'
      */
/*     //TODO fix these tests
    public void testProcessOrphanDeliveryPositionData() {
        final PMDate pmDate = new PMDate();
        Vector<PMDate> dateList = new Vector<PMDate>();
        dateList.add(pmDate);
        dateList.add(pmDate);
        final Mock dateDAOMock = new Mock(IDateDAO.class);
        dateDAOMock.expects(once()).method("getDates").withAnyArguments().will(returnValue(dateList));
        final DateIterator iterator = new DateIterator() {
            public IDateDAO getQuoteDAO() {
                return (IDateDAO) dateDAOMock.proxy();
            }
        };
        final Vector<Integer> orderList = new Vector<Integer>();
        Vector<QuoteVO> data = new Vector<QuoteVO>();
        QuoteVO quoteVO = new QuoteVO("");
        quoteVO.setDate(pmDate);
        data.add(quoteVO);

        final Mock quoteDAOMock = new Mock(IQuoteDAO.class);
        quoteDAOMock.expects(once()).method("getQuotes").with(eq(pmDate)).will(returnValue(data));
        quoteDAOMock.expects(once()).method("updateQuote").with(eq(quoteVO));

        BhavToPMConverter converter = new BhavToPMConverter() {
            @Override
            DateIterator getDateIterator(PMDate stDate, PMDate enDate) {
                orderList.add(1);
                return iterator;
            }
            @Override
            IQuoteDAO getQuoteDAO() {
                return (IQuoteDAO) quoteDAOMock.proxy();
            }
            @Override
            boolean loadDeliveryPositionData(PMDate date, List<QuoteVO> dailyData) {
                orderList.add(3);
                return true;
            }
            @Override
            void moveFileToBackup(Date date, boolean moveBhavFile, boolean moveDelivFile) {
                orderList.add(5);
            }

            boolean isBhavAndDeliveryPosDateSame() {
                return false;
            }
        };
        converter.processOrphanDeliveryPositionData();
        assertEquals(5, orderList.size());
        for (int i=0; i<5; i++)
        assertTrue((i+1) == orderList.get(i));
        quoteDAOMock.verify();
        dateDAOMock.verify();

    }

    public void testProcessOrphanDeliveryPositionDataForCorrectDateList() {
        final PMDate date1 = new PMDate(2,1,2006);
        final PMDate date2 = new PMDate(4,1,2006);
        final DateIterator iterator = new DateIterator() {
            int i = 0;
            Vector<PMDate> dateList = new Vector<PMDate>();
            {
                dateList.add(new PMDate(1,1,2006));
                dateList.add(date1);
                dateList.add(date2);
            }
            @Override
            public boolean hasNext() {
                return i < 3;
            }
            @Override
            public PMDate next() {
                return dateList.elementAt(i++);
            }
        };

        final Vector<QuoteVO> quoteDAOwriteDataList = new Vector<QuoteVO>();
        final Vector quoteDAOgetQuoteList = new Vector();
        final Vector<PMDate> moveFileList = new Vector<PMDate>();

        BhavToPMConverter converter = new BhavToPMConverter() {
            @Override
            DateIterator getDateIterator(PMDate stDate, PMDate enDate) {
                return iterator;
            }
            @Override
            QuoteDAO getQuoteDAO() {
                return new QuoteDAO() {
                    @Override
                    public Vector<QuoteVO> getQuote(PMDate date) {
                        quoteDAOgetQuoteList.add(date);
                        Vector<QuoteVO> data = new Vector<QuoteVO>();
                        QuoteVO quoteVO1 = new QuoteVO(date.toString()+"1");
                        quoteVO1.setDate(new PMDate());
                        data.add(quoteVO1);
                        QuoteVO quoteVO2 = new QuoteVO(date.toString()+"2");
                        quoteVO2.setDate(new PMDate());
                        data.add(quoteVO2);
                        return data;
                    }
                    @Override
                    public void writeData(QuoteVO quoteVO) {
                        quoteDAOwriteDataList.add(quoteVO);
                    }
                };
            }
            @Override
            boolean loadDeliveryPositionData(PMDate date, List<QuoteVO> dailyData) {
                return true;
            }
            @Override
            void moveFileToBackup(Date date, boolean moveBhavFile, boolean moveDelivFile) {
                moveFileList.add(new PMDate(date));
            }
        };

        converter.processOrphanDeliveryPositionData();

        assertEquals(2, quoteDAOgetQuoteList.size());
        assertEquals(date1, quoteDAOgetQuoteList.get(0));
        assertEquals(date2, quoteDAOgetQuoteList.get(1));

        assertEquals(2, moveFileList.size());
        assertEquals(date1, moveFileList.get(0));
        assertEquals(date2, moveFileList.get(1));

        assertEquals(4, quoteDAOwriteDataList.size());
        assertEquals(date1+"1", quoteDAOwriteDataList.get(0).getStock());
        assertEquals(date1+"2", quoteDAOwriteDataList.get(1).getStock());
        assertEquals(date2+"1", quoteDAOwriteDataList.get(2).getStock());
        assertEquals(date2+"2", quoteDAOwriteDataList.get(3).getStock());

    }
    public void testProcessOrphanDeliveryPositionDataToSkipSaveIfNoData() {
        final PMDate date1 = new PMDate(2,1,2006);
        final PMDate date2 = new PMDate(4,1,2006);
        final DateIterator iterator = new DateIterator() {
            int i = 0;
            Vector<PMDate> dateList = new Vector<PMDate>();
            {
                dateList.add(new PMDate(1,1,2006));
                dateList.add(date1);
                dateList.add(date2);
            }
            @Override
            public boolean hasNext() {
                return i < 3;
            }
            @Override
            public PMDate next() {
                return dateList.elementAt(i++);
            }
        };

        final Vector<QuoteVO> quoteDAOwriteDataList = new Vector<QuoteVO>();
        final Vector quoteDAOgetQuoteList = new Vector();

        BhavToPMConverter converter = new BhavToPMConverter() {
            @Override
            DateIterator getDateIterator(PMDate stDate, PMDate enDate) {
                return iterator;
            }
            @Override
            QuoteDAO getQuoteDAO() {
                return new QuoteDAO() {
                    @Override
                    public Vector<QuoteVO> getQuote(PMDate date) {
                        quoteDAOgetQuoteList.add(date);
                        Vector<QuoteVO> data = new Vector<QuoteVO>();
                        data.add(new QuoteVO(date.toString()));
                        return data;
                    }
                    @Override
                    public void writeData(QuoteVO quoteVO) {
                        fail("should not come here");
                    }
                };
            }
            @Override
            boolean loadDeliveryPositionData(PMDate date, List<QuoteVO> dailyData) {
                return false;
            }
            @Override
            void moveFileToBackup(Date date, boolean moveBhavFile, boolean moveDelivFile) {
                fail("Should not come here");
            }
        };

        converter.processOrphanDeliveryPositionData();

    }
*/

    /*
      * Test method for
      * 'pm.tools.BhavToPMConverter.isBhavAndDeliveryPosDateSame()'
      */

    public void testIsBhavAndDeliveryPosDateSame() {
        AppConfig.dateLastBhavCopy.Value = "20050101";
        AppConfig.dateLastDeliveryPosition.Value = "20050101";
        assertTrue(new EquityBhavToPMConverter().isBhavAndDeliveryPosDateSame());
        AppConfig.dateLastDeliveryPosition.Value = "20051001";
        assertFalse(new EquityBhavToPMConverter().isBhavAndDeliveryPosDateSame());
    }

    /*
      * Test method for
      * 'pm.tools.BhavToPMConverter.loadDeliveryPositionData(Date, Vector<String[]>)'
      */
    public void testLoadDeliveryPositionDataDateVectorOfString() {
        Vector<String[]> dailyData = new Vector<String[]>();

        final String stock1 = "STOCK1";
        String[] dayData1 = {stock1, "0", "0", "0", "0", "0", "0", "0", "0"};
        dailyData.add(dayData1);
        String stock2 = "STOCK2";
        final String data1 = "40.28";
        String[] dayData2 = {stock2, "0", "0", "0", "0", "0", "0", "0", "0"};
        dailyData.add(dayData2);
        EquityBhavToPMConverter converter = new EquityBhavToPMConverter() {
            @Override
            Hashtable<String, String> loadDeliveryPositionDataFromFile(Reader reader) {
                Hashtable<String, String> data = new Hashtable<String, String>();
                data.put(stock1, data1);
                data.put("DUMMY", "DUMMY");
                return data;
            }

            @Override
            Reader getDeliveryPostionReader(Date date) throws FileNotFoundException {
                return new StringReader("");
            }
        };
        converter.loadDeliveryPositionData(new Date(), dailyData);
        assertEquals(data1, dailyData.get(0)[8]);
        assertEquals("0", dailyData.get(1)[8]);
    }

    /*
      * Test method for
      * 'pm.tools.BhavToPMConverter.loadDeliveryPositionData(PMDate, Vector<QuoteVO>)'
      */
    public void testLoadDeliveryPositionDataPMDateVectorOfQuoteVO() {
        Vector<QuoteVO> dailyData = new Vector<QuoteVO>();
        final String stock1 = "STOCK1";
        String stock2 = "STOCK2";
        final String data1 = "40.28";
        String data2 = "81.03";
        dailyData.add(new QuoteVO(stock1));
        dailyData.add(new QuoteVO(stock2));
        PMDate date = new PMDate();
        EquityBhavToPMConverter converter = new EquityBhavToPMConverter() {
            @Override
            Hashtable<String, String> loadDeliveryPositionDataFromFile(Reader reader) {
                Hashtable<String, String> data = new Hashtable<String, String>();
                data.put(stock1, data1);
                data.put("DUMMY", "DUMMY");
                return data;
            }

            @Override
            Reader getDeliveryPostionReader(Date date) throws FileNotFoundException {
                return new StringReader("");
            }
        };
        converter.loadDeliveryPositionData(date, dailyData);
        assertEquals(data1, Helper.formatFloat(dailyData.get(0).getPerDeliveryQty()));
        assertEquals("0.00", Helper.formatFloat(dailyData.get(1).getPerDeliveryQty()));

    }

    /*
      * Test method for
      * 'pm.tools.BhavToPMConverter.loadDeliveryPositionDataFromFile(Reader)'
      */
    public void testLoadDeliveryPositionDataFromFile() {
        String delPer1 = "40.28";
        String delPer2 = "96.62";

        PMDate date = new PMDate(1, 1, 2006);
        String STOCK1 = "STOCK1";
        String STOCK2 = "STOCK2";
        EquityBhavToPMConverter converter = new EquityBhavToPMConverter();
        StringBuffer sb = new StringBuffer();
        sb.append("Security Wise Delivery Position - Compulsory Rolling Settlement\n");
        sb.append("10,MTO,01022006,131889689,0000764\n");
        sb.append("Trade Date <01-FEB-2006>,Settlement Type <N>,Settlement No <2006021>,Settlement Date <03-FEB-2006>\n");
        sb.append("Record Type,Sr No,Name of Security,Quantity Traded,Deliverable Quantity(gross across client level),% of Deliverable Quantity to Traded Quantity\n");
        sb.append("20,1,").append(STOCK1).append(",EQ,262538,105753," + delPer1 + "\n");
        sb.append("20,2,").append(STOCK2).append(",EQ,976,943," + delPer2 + "\n");
        StringReader reader = new StringReader(sb.toString());

        Hashtable<String, String> loadedData = converter.loadDeliveryPositionDataFromFile(reader);
        assertEquals(2, loadedData.size());
        assertTrue(loadedData.containsKey(STOCK1));
        assertTrue(loadedData.containsKey(STOCK2));
        assertTrue(loadedData.contains(delPer1));
        assertTrue(loadedData.contains(delPer2));

    }

    public void xtestMoveFileToBackupToMoveBhav_Delivery() {
        Calendar cal = Calendar.getInstance();
        cal.set(1901, 5, 12);
        Date date = cal.getTime();
        String bhavFilePath = FileNameUtil.getEquityFilePath(date);
        String deliveryFilePath = DeliveryPositionDownloader.getFilePath(date);
        String backupFolder = Helper.backupFolder(new PMDate(date));
        String newBhavFilePath = backupFolder + File.pathSeparator + bhavFilePath.substring(bhavFilePath.lastIndexOf("/"));
        String newDeliveryFilePath = backupFolder + File.pathSeparator + deliveryFilePath.substring(deliveryFilePath.lastIndexOf("/"));
        deleteIfExists(newBhavFilePath);
        deleteIfExists(newDeliveryFilePath);

        createTempFile(bhavFilePath);
        createTempFile(deliveryFilePath);
        new EquityBhavToPMConverter().moveFileToBackup(date, true, true);
        assertTrue(new File(newBhavFilePath).exists());
        assertTrue(new File(newDeliveryFilePath).exists());
    }

    private void deleteIfExists(String newBhavFilePath) {
        File file = new File(newBhavFilePath);
        if (file.exists()) file.delete();
    }

    public void testMoveFileToBackupToMoveDelivery() {
        Calendar cal = Calendar.getInstance();
        cal.set(1901, 6, 12);
        Date date = cal.getTime();
        String bhavFilePath = FileNameUtil.getEquityFilePath(date);
        String deliveryFilePath = DeliveryPositionDownloader.getFilePath(date);
        String backupFolder = Helper.backupFolder(new PMDate(date));
        String newBhavFilePath = backupFolder + "/" + bhavFilePath.substring(bhavFilePath.lastIndexOf("/"));
        String newDeliveryFilePath = backupFolder + "/" + deliveryFilePath.substring(deliveryFilePath.lastIndexOf("/"));
        deleteIfExists(newBhavFilePath);
        deleteIfExists(newDeliveryFilePath);

        createTempFile(bhavFilePath);
        createTempFile(deliveryFilePath);
        new EquityBhavToPMConverter().moveFileToBackup(date, false, true);
        assertFalse(new File(newBhavFilePath).exists());
        assertTrue(new File(newDeliveryFilePath).exists());
    }

    private void createTempFile(String filePath) {
        File bhavSourceFile = new File(filePath);
        try {
            bhavSourceFile.createNewFile();
            bhavSourceFile.deleteOnExit();
        } catch (IOException e) {

        }
    }

}
