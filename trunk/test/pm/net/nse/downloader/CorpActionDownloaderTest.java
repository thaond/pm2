package pm.net.nse.downloader;

import org.jmock.cglib.MockObjectTestCase;
import pm.AppLoader;
import pm.net.nse.CorpActionConverter;
import pm.util.AppConfig;
import pm.util.PMDate;
import pm.vo.CompanyActionVO;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class CorpActionDownloaderTest extends MockObjectTestCase {

    static {
        AppLoader.initConsoleLogger();
    }

    public void testCompleteScenario() throws Exception {
//		CorpActionDownloadManager manager = new CorpActionDownloadManager(null);
//		CorpActionDownloader downloader = new CorpActionDownloader("ONGC",manager);
//		downloader.run();
//		System.out.println(downloader.getCorpAction());
    }

    public void testPeformTask() throws Exception {
        CorpActionDownloader downloader = new CorpActionDownloader("ONGC", null) {
            @Override
            protected Reader getDataReader() {
                return new StringReader(getDataString());
            }

            @Override
            CorpActionConverter getConverter() {
                return new CorpActionConverter() {
                    @Override
                    protected void recordAlert(String msg, String stockCode, PMDate date, String rawActionLine) {
                        fail("Method should not be called");
                    }
                };
            }
        };
        AppConfig.dateCORPACTIONSYNCHRONIZER.Value = "20030101";
        downloader.performTask();
        Vector<CompanyActionVO> corpActions = downloader.getCorpActions();
        assertEquals(3, corpActions.size());
        assertTrue(corpActions.contains(new CompanyActionVO("20040902,ONGC,100.0,1.0,Divident,true,")));
        assertTrue(corpActions.contains(new CompanyActionVO("20050901,ONGC,200.0,1.0,Divident,true,")));
        assertTrue(corpActions.contains(new CompanyActionVO("20051227,ONGC,25.0,1.0,Divident,false,")));
    }

    public void _testPeformTaskToRemoveOldData() throws Exception { //Not using removeold data for now
        CorpActionDownloader downloader = new CorpActionDownloader("ONGC", null) {
            @Override
            protected Reader getDataReader() {
                return new StringReader(getDataString());
            }

            @Override
            CorpActionConverter getConverter() {
                return new CorpActionConverter() {
                    @Override
                    protected void recordAlert(String msg, String stockCode, PMDate date, String rawActionLine) {
                        fail("Method should not be called");
                    }
                };
            }
        };
        AppConfig.dateCORPACTIONSYNCHRONIZER.Value = "20051201";
        downloader.performTask();
        Vector<CompanyActionVO> corpActions = downloader.getCorpActions();
        assertEquals(1, corpActions.size());
        assertTrue(corpActions.contains(new CompanyActionVO("20051227,ONGC,25.0,1.0,Divident,false,")));
    }

    public void testPeformTaskToHandleEmptyReader() {
        CorpActionDownloader downloader = new CorpActionDownloader(null, null) {
            @Override
            protected Reader getDataReader() {
                return null;
            }

            @Override
            public String getURL() {
                return null;
            }

            @Override
            protected Hashtable<PMDate, String> parseData(Reader reader) {
                fail("Should not parse on IOError");
                return null;
            }
        };

        downloader.performTask();
    }

    public void testPeformTaskToSkipConversionIfDownloadIsEmpty() {
        CorpActionDownloader downloader = new CorpActionDownloader(null, null) {
            @Override
            protected Reader getDataReader() {
                return new StringReader("");
            }

            @Override
            public String getURL() {
                return null;
            }

            @Override
            protected Hashtable<PMDate, String> parseData(Reader reader) {
                return new Hashtable<PMDate, String>();
            }

            @Override
            void convertDownloadedAction(Hashtable<PMDate, String> actionData) {
                fail("should not call convert on download");
            }
        };

        downloader.performTask();
    }

    /*
      * Test method for
      * 'pm.net.nse.downloader.CorpActionDownloader.parseData(BufferedReader)'
      */
    public void testParseData() {
        String data = getDataString();
        Hashtable<PMDate, String> expected = getExpectedData();
        CorpActionDownloader thread = new CorpActionDownloader("ONGC", null);
        Hashtable<PMDate, String> actual = thread.parseData(new BufferedReader(
                new StringReader(data)));
        assertEquals(expected.size(), actual.size());
        for (Iterator iter = expected.keySet().iterator(); iter.hasNext();) {
            PMDate date = (PMDate) iter.next();
            assertTrue(actual.containsKey(date));
            assertEquals(expected.get(date), actual.get(date));
        }

    }

    /*
      * Test method for 'pm.net.nse.downloader.CorpActionDownloader.getURL()'
      */
    public void testGetURL() throws UnsupportedEncodingException {
        String stockCode = "StockCode";
        String url = new CorpActionDownloader(stockCode, null).getURL();
        assertEquals(CorpActionDownloader.baseURL + stockCode, url);
        stockCode = "M&M";
        url = new CorpActionDownloader(stockCode, null).getURL();
        assertEquals(
                "http://www.nseindia.com/marketinfo/companyinfo/eod/action.jsp?symbol=M%26M",
                url);
    }

    private Hashtable<PMDate, String> getExpectedData() {
        Hashtable<PMDate, String> hashtable = new Hashtable<PMDate, String>();
        hashtable.put(new PMDate(27, 12, 2005), "INT DIV-RS.25/- PER SHARE");
        hashtable.put(new PMDate(2, 9, 2004), "AGM/DIVIDEND-100%");
        hashtable.put(new PMDate(1, 9, 2005), "AGM/DIVIDEND-200%");
        return hashtable;
    }

    private String getDataString() {
        StringBuffer sb = new StringBuffer();
        sb.append("NSE - Corporates - Corporate Actions\n");
        sb
                .append("Home > Corporates > Corporate Information > Corporate Actions\n");
        sb.append("Corporate Information\n");
        sb.append("Corporate Actions\n");
        sb.append("Company\n");
        sb.append("OIL & NATURAL GAS CORPN LTD\n");
        sb.append("NSE Symbol\n");
        sb.append("ONGC\n");
        sb.append("Series\n");
        sb.append("Record Date\n");
        sb.append("BC Start Date\n");
        sb.append("BC End Date\n");
        sb.append("Ex Date\n");
        sb.append("No Delivery Start Date\n");
        sb.append("No Delivery End Date\n");
        sb.append("Purpose\n");
        sb.append("BE\n");
        sb.append("-\n");
        sb.append("14/09/2000\n");
        sb.append("27/09/2000\n");
        sb.append("30/08/2000\n");
        sb.append("30/08/2000\n");
        sb.append("08/09/2000\n");
        sb.append("AGM/DIVIDEND-25% PURPOSE REVISED\n");
        sb.append("BE\n");
        sb.append("-\n");
        sb.append("10/09/1999\n");
        sb.append("21/09/1999\n");
        sb.append("26/08/1999\n");
        sb.append("26/08/1999\n");
        sb.append("03/09/1999\n");
        sb.append("DIVIDEND - 55% PURPOSE REVISED\n");
        sb.append("BL\n");
        sb.append("26/12/2005\n");
        sb.append("-\n");
        sb.append("-\n");
        sb.append("25/12/2005\n");
        sb.append("-\n");
        sb.append("-\n");
        sb.append("INT DIV-RS.25/- PER SHARE\n");
        sb.append("EQ\n");
        sb.append("28/12/2005\n");
        sb.append("-\n");
        sb.append("-\n");
        sb.append("27/12/2005\n");
        sb.append("-\n");
        sb.append("-\n");
        sb.append("INT DIV-RS.25/- PER SHARE\n");
        sb.append("EQ\n");
        sb.append("-\n");
        sb.append("05/09/2005\n");
        sb.append("19/09/2005\n");
        sb.append("01/09/2005\n");
        sb.append("-\n");
        sb.append("-\n");
        sb.append("AGM/DIVIDEND-200%\n");
        sb.append("EQ\n");
        sb.append("-\n");
        sb.append("06/09/2004\n");
        sb.append("20/09/2004\n");
        sb.append("02/09/2004\n");
        sb.append("-\n");
        sb.append("-\n");
        sb.append("AGM/DIVIDEND-100%\n");
        sb
                .append("Other Info: Announcements | Board Meetings | Financial Results | Shareholding Pattern\n");
        return sb.toString();
    }

}
