package pm.net.nse;

import junit.framework.TestCase;
import pm.AppLoader;
import pm.net.nse.downloader.AbstractHTMLDownloader;
import pm.net.nse.downloader.CorpResultDownloader;
import pm.net.nse.downloader.CorpResultLinkDownloader;
import pm.util.AppConst.CORP_RESULT_TIMELINE;
import pm.util.ApplicationException;
import pm.util.PMDate;
import pm.vo.CorpResultVO;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CorpResultDownloadManagerTest extends TestCase {

    static {
        AppLoader.initConsoleLogger();
    }

    /*
      * Test method for 'pm.net.nse.CorpResultDownloaderManager.taskCompleted(AbstractDownloader)'
      */
    public void testTaskCompletedForCompletedCorpResultLinkDownloader() throws Exception {
        final String STOCKCODE = "STOCKCODE";
        final String STOCK1 = "STOCK1";
        final Vector<String> executionList = new Vector<String>();
        final Vector<String> urlList = new Vector<String>();
        urlList.add(STOCK1);
        urlList.add("STOCK2");
        urlList.add("STOCK3");
        final CorpResultVO corpResultVO = new CorpResultVO("GLENMARK,20041001,20041231,Quaterly,3,false,false,false,2004,1.96,2.0,1185.19,1211.76,0.0,0.0,1211.76,444.87,");
        final Vector<CorpResultVO> existingData = new Vector<CorpResultVO>();
        existingData.add(corpResultVO);

        int maxThreadCount = 1;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThreadCount, maxThreadCount, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()) {
            @Override
            public void execute(Runnable command) {
                executionList.add(((CorpResultDownloader) command).getURL());
            }
        };

        CorpResultLinkDownloader downloader = new CorpResultLinkDownloader(null, null) {
            @Override
            public boolean isCompleted() {
                return true;
            }

            @Override
            public String getStockCode() {
                return STOCKCODE;
            }

            @Override
            public Vector<String> getLinkList() {
                return urlList;
            }
        };
        CorpResultDownloadManager manager = new CorpResultDownloadManager(executor) {
            @Override
            public CorpResultVO buildResultVO(String url) {
                try {
                    if (url.equals(STOCK1)) {
                        return corpResultVO;
                    } else {
                        return new CorpResultVO("GLENMARK,20051001,20051231,Quaterly,3,false,false,false,2004,1.96,2.0,1185.19,1211.76,0.0,0.0,1211.76,444.87,");
                    }
                } catch (ApplicationException e) {
                    fail(e.getMessage());
                }
                return null;
            }

            @Override
            Vector<CorpResultVO> getExistingData(String stockCode) {
                return existingData;
            }
        };
        manager.downloaderList.put(STOCKCODE, downloader);
        manager.totalTaskCount = 1;
        assertFalse(manager.isTaskCompleted());

        manager.taskCompleted(downloader);

        assertFalse(manager.isTaskCompleted());
        assertEquals(1, manager.totalTaskCount);
        assertEquals(0, manager.getProgress());
        assertEquals(urlList.size() - 1, manager.downloaderList.size());
        assertEquals(urlList.size() - 1, executionList.size());
        for (int i = 1; i < urlList.size(); i++) {
            assertTrue(manager.downloaderList.containsKey(urlList.elementAt(i)));
            assertEquals(urlList.elementAt(i), executionList.elementAt(i - 1));
        }
        assertEquals(1, manager.stockwiseDownloaderCount.size());
        assertEquals(new Integer(2), manager.stockwiseDownloaderCount.get(STOCKCODE));
    }

    public void testTaskCompletedForCompletedCorpResultLinkDownloaderButAllLinksAreDownloadedAlready() throws Exception {
        final String STOCKCODE = "STOCKCODE";
        final String STOCK1 = "STOCK1";
        final Vector<String> urlList = new Vector<String>();
        urlList.add(STOCK1);
        final CorpResultVO corpResultVO = new CorpResultVO("GLENMARK,20041001,20041231,Quaterly,3,false,false,false,2004,1.96,2.0,1185.19,1211.76,0.0,0.0,1211.76,444.87,");
        final Vector<CorpResultVO> existingData = new Vector<CorpResultVO>();
        existingData.add(corpResultVO);

        int maxThreadCount = 1;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThreadCount, maxThreadCount, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()) {
            @Override
            public void execute(Runnable command) {
                fail("Data is already there why should it download?");
            }
        };

        CorpResultLinkDownloader downloader = new CorpResultLinkDownloader(null, null) {
            @Override
            public boolean isCompleted() {
                return true;
            }

            @Override
            public String getStockCode() {
                return STOCKCODE;
            }

            @Override
            public Vector<String> getLinkList() {
                return urlList;
            }
        };
        CorpResultDownloadManager manager = new CorpResultDownloadManager(executor) {
            @Override
            public CorpResultVO buildResultVO(String url) {
                return corpResultVO;
            }

            @Override
            Vector<CorpResultVO> getExistingData(String stockCode) {
                return existingData;
            }

            @Override
            protected void saveStatus() {
            }
        };
        manager.downloaderList.put(STOCKCODE, downloader);
        manager.totalTaskCount = 1;
        assertFalse(manager.isTaskCompleted());

        manager.taskCompleted(downloader);

        assertTrue(manager.isTaskCompleted());
    }

    public void testTaskCompletedForInCompletedCorpResultLinkDownloader() throws Exception {
        final String STOCKCODE = "STOCKCODE";

        int maxThreadCount = 1;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThreadCount, maxThreadCount, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()) {
            @Override
            public void execute(Runnable command) {
                fail("should not start any new task");
            }
        };

        CorpResultLinkDownloader downloader = new CorpResultLinkDownloader(null, null) {
            @Override
            public boolean isCompleted() {
                return false;
            }

            @Override
            public String getStockCode() {
                return STOCKCODE;
            }

            @Override
            public Vector<String> getLinkList() {
                fail("Should not ask for url list if task is inComplete");
                return null;
            }
        };
        CorpResultDownloadManager manager = new CorpResultDownloadManager(executor) {
            @Override
            public CorpResultVO buildResultVO(String url) {
                fail("Should not come here");
                return null;
            }

            @Override
            Vector<CorpResultVO> getExistingData(String stockCode) {
                return new Vector<CorpResultVO>();
            }

            @Override
            protected void saveStatus() {
            }
        };
        manager.downloaderList.put(STOCKCODE, downloader);
        manager.totalTaskCount = 1;
        assertFalse(manager.isTaskCompleted());

        manager.taskCompleted(downloader);

        assertTrue(manager.isTaskCompleted());
        assertEquals(1, manager.totalTaskCount);
        assertEquals(100, manager.getProgress());
        assertTrue(manager.downloaderList.isEmpty());
        assertEquals(1, manager.inCompleteList.size());
        assertEquals(STOCKCODE, manager.inCompleteList.elementAt(0));
    }

    public void testTaskCompletedForCompletedCorpResultLinkDownloaderWithStopTriggered() throws Exception {
        final String STOCKCODE = "STOCKCODE";

        int maxThreadCount = 1;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThreadCount, maxThreadCount, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()) {
            @Override
            public void execute(Runnable command) {
                fail("should not start any new task");
            }
        };

        CorpResultLinkDownloader downloader = new CorpResultLinkDownloader(null, null) {
            @Override
            public boolean isCompleted() {
                return true;
            }

            @Override
            public String getStockCode() {
                return STOCKCODE;
            }

            @Override
            public Vector<String> getLinkList() {
                fail("Should not ask for url list if task is inComplete");
                return null;
            }
        };
        CorpResultDownloadManager manager = new CorpResultDownloadManager(executor) {
            @Override
            public CorpResultVO buildResultVO(String url) {
                fail("Should not come here");
                return null;
            }

            @Override
            Vector<CorpResultVO> getExistingData(String stockCode) {
                return new Vector<CorpResultVO>();
            }

            @Override
            protected void saveStatus() {
            }
        };
        manager.downloaderList.put(STOCKCODE, downloader);
        manager.totalTaskCount = 1;
        assertFalse(manager.isTaskCompleted());
        manager.stop();
        manager.taskCompleted(downloader);

        assertTrue(manager.isTaskCompleted());
        assertEquals(1, manager.totalTaskCount);
        assertEquals(100, manager.getProgress());
        assertTrue(manager.downloaderList.isEmpty());
    }

    public void testTaskCompletedForInCompletedCorpResultDownloader() throws Exception {
        String url = "url";
        final String URL1 = "URL1";
        final String URL2 = "URL2";
        final String STOCKCODE = "stockcode";

        CorpResultDownloader downloader1 = new CorpResultDownloader(null, url, null) {
            @Override
            public boolean isCompleted() {
                return false;
            }

            @Override
            public String getStockCode() {
                return STOCKCODE;
            }

            @Override
            public String getURL() {
                return URL1;
            }
        };

        CorpResultDownloader downloader2 = new CorpResultDownloader(null, url, null) {
            @Override
            public boolean isCompleted() {
                return false;
            }

            @Override
            public String getStockCode() {
                return STOCKCODE;
            }

            @Override
            public String getURL() {
                return URL2;
            }
        };
        CorpResultDownloadManager manager = new CorpResultDownloadManager(null) {
            @Override
            protected void saveStatus() {
            }

        };
        manager.stockwiseDownloaderCount.put(STOCKCODE, 2);
        manager.totalTaskCount = 1;
        manager.downloaderList.put(URL1, downloader1);
        manager.downloaderList.put(URL2, downloader2);

        manager.taskCompleted(downloader1);

        assertEquals(new Integer(1), manager.stockwiseDownloaderCount.get(STOCKCODE));
        assertEquals(1, manager.totalTaskCount);
        assertEquals(1, manager.downloaderList.size());
        assertTrue(manager.downloaderList.contains(downloader2));
        assertFalse(manager.isTaskCompleted());
        assertEquals(1, manager.inCompleteList.size());
        assertTrue(manager.inCompleteList.contains(STOCKCODE));

        manager.taskCompleted(downloader2);

        assertNull(manager.stockwiseDownloaderCount.get(STOCKCODE));
        assertEquals(1, manager.totalTaskCount);
        assertTrue(manager.downloaderList.isEmpty());
        assertTrue(manager.isTaskCompleted());
        assertEquals(1, manager.inCompleteList.size());
        assertTrue(manager.inCompleteList.contains(STOCKCODE));
    }

    public void testTaskCompletedForCompletedCorpResultDownloader() throws Exception {
        String url = "url";
        final String URL1 = "URL1";
        final String URL2 = "URL2";
        final String STOCKCODE = "stockcode";
        final Vector<Integer> callList = new Vector<Integer>();

        CorpResultDownloader downloader1 = new CorpResultDownloader(null, url, null) {
            @Override
            public boolean isCompleted() {
                return true;
            }

            @Override
            public String getStockCode() {
                return STOCKCODE;
            }

            @Override
            public String getURL() {
                return URL1;
            }
        };

        CorpResultDownloader downloader2 = new CorpResultDownloader(null, url, null) {
            @Override
            public boolean isCompleted() {
                return true;
            }

            @Override
            public String getStockCode() {
                return STOCKCODE;
            }

            @Override
            public String getURL() {
                return URL2;
            }
        };
        CorpResultDownloadManager manager = new CorpResultDownloadManager(null) {
            @Override
            protected void addToInCompleteList(String stockCode) {
                fail("Should not come here");
            }

            @Override
            void shutDownManager() {
                callList.add(1);
            }
        };
        manager.stockwiseDownloaderCount.put(STOCKCODE, 2);
        manager.totalTaskCount = 1;
        manager.downloaderList.put(URL1, downloader1);
        manager.downloaderList.put(URL2, downloader2);

        manager.taskCompleted(downloader1);

        assertEquals(new Integer(1), manager.stockwiseDownloaderCount.get(STOCKCODE));
        assertEquals(1, manager.totalTaskCount);
        assertEquals(1, manager.downloaderList.size());
        assertTrue(manager.downloaderList.contains(downloader2));
        assertFalse(manager.isTaskCompleted());

        manager.taskCompleted(downloader2);

        assertNull(manager.stockwiseDownloaderCount.get(STOCKCODE));
        assertEquals(1, manager.totalTaskCount);
        assertTrue(manager.downloaderList.isEmpty());
        assertTrue(manager.isTaskCompleted());
        assertEquals(1, callList.size());
        assertTrue(callList.contains(1));
    }

    /*
      * Test method for 'pm.net.nse.CorpResultDownloaderManager.stop()'
      */
    public void testStop() {
        final Vector<Integer> callList = new Vector<Integer>();
        CorpResultDownloadManager manager = new CorpResultDownloadManager(null);
        manager.downloaderList.put("1", new CorpResultLinkDownloader(null, null) {
            @Override
            public void stop() {
                callList.add(1);
            }
        });
        assertFalse(manager.stop);
        manager.stop();
        assertTrue(manager.stop);
        assertEquals(1, callList.size());
        assertTrue(callList.contains(1));
    }

    /*
      * Test method for 'pm.net.nse.CorpResultDownloaderManager.run()'
      */
    public void testRun() {
        final Vector<String> stockList = new Vector<String>();
        stockList.add("STOCK1");
        stockList.add("STOCK2");
        stockList.add("STOCK3");
        final Vector<String> executionList = new Vector<String>();

        int maxThreadCount = 1;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThreadCount, maxThreadCount, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()) {
            @Override
            public void execute(Runnable command) {
                executionList.add(((CorpResultLinkDownloader) command).getStockCode());
            }
        };

        CorpResultDownloadManager manager = new CorpResultDownloadManager(executor) {
            @Override
            protected Vector<String> getStockList() {
                return stockList;
            }
        };
        manager.run();
        assertEquals(stockList.size(), executionList.size());
        Collection<AbstractHTMLDownloader> downloaders = manager.downloaderList.values();
        Vector<String> values = new Vector<String>();
        for (AbstractHTMLDownloader downloader : downloaders) {
            values.add(((CorpResultLinkDownloader) downloader).getStockCode());
        }
        for (int i = 0; i < stockList.size(); i++) {
            assertEquals(stockList.elementAt(i), executionList.elementAt(i));
            assertTrue(values.contains(stockList.elementAt(i)));
        }
        assertEquals(stockList.size(), manager.totalTaskCount);
        assertTrue(manager.initComplete);

    }

    public void testBuildResultVO_String_Audited_Non_Cumulative_Consolidated() throws Exception {

        CorpResultVO resultsVO = new CorpResultDownloadManager(null).buildResultVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?param=01-OCT-200531-DEC-2005Q3AANCEINFOSYSTCH&seq_id=48669&viewFlag=N");
        assertEquals("INFOSYSTCH", resultsVO.getStockCode());
        assertEquals(new PMDate(1, 10, 2005), resultsVO.getStartDate());
        assertEquals(new PMDate(31, 12, 2005), resultsVO.getEndDate());
        assertEquals(CORP_RESULT_TIMELINE.Quaterly, resultsVO.getTimeline());
        assertEquals(3, resultsVO.getPeriod());
        assertTrue(resultsVO.isAuditedFlag());
        assertTrue(resultsVO.isConsolidatedFlag());
        assertEquals(2005, resultsVO.getYear());
    }

    public void testBuildResultVO_String_Audited_Non_Cumulative_Non_Consolidated() throws Exception {
        CorpResultVO resultsVO = new CorpResultDownloadManager(null).buildResultVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?param=01-OCT-200531-DEC-2005Q3AANNEINFOSYSTCH&seq_id=48669&viewFlag=N");
        assertEquals("INFOSYSTCH", resultsVO.getStockCode());
        assertEquals(new PMDate(1, 10, 2005), resultsVO.getStartDate());
        assertEquals(new PMDate(31, 12, 2005), resultsVO.getEndDate());
        assertEquals(CORP_RESULT_TIMELINE.Quaterly, resultsVO.getTimeline());
        assertEquals(3, resultsVO.getPeriod());
        assertTrue(resultsVO.isAuditedFlag());
        assertFalse(resultsVO.isConsolidatedFlag());
        assertEquals(2005, resultsVO.getYear());
    }

    public void testBuildResultVO_String_Audited_Non_Cumulative_Consolidated_HalfYearly() throws Exception {
        CorpResultVO resultsVO = new CorpResultDownloadManager(null).buildResultVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?param=01-APR-200530-SEP-2005H1AACCEINFOSYSTCH&seq_id=48669&viewFlag=N");
        assertEquals("INFOSYSTCH", resultsVO.getStockCode());
        assertEquals(new PMDate(1, 4, 2005), resultsVO.getStartDate());
        assertEquals(new PMDate(30, 9, 2005), resultsVO.getEndDate());
        assertEquals(CORP_RESULT_TIMELINE.HalfYearly, resultsVO.getTimeline());
        assertEquals(1, resultsVO.getPeriod());
        assertTrue(resultsVO.isAuditedFlag());
        assertTrue(resultsVO.isConsolidatedFlag());
        assertEquals(2005, resultsVO.getYear());
    }

    public void testBuildResultVO_String_Audited_Non_Cumulative_Consolidated_Annual() throws Exception {
        CorpResultVO resultsVO = new CorpResultDownloadManager(null).buildResultVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?param=01-APR-200431-MAR-2005ANAACCEINFOSYSTCH&seq_id=48669&viewFlag=N");
        assertEquals("INFOSYSTCH", resultsVO.getStockCode());
        assertEquals(new PMDate(1, 4, 2004), resultsVO.getStartDate());
        assertEquals(new PMDate(31, 3, 2005), resultsVO.getEndDate());
        assertEquals(CORP_RESULT_TIMELINE.Annual, resultsVO.getTimeline());
        assertEquals(0, resultsVO.getPeriod());
        assertTrue(resultsVO.isAuditedFlag());
        assertTrue(resultsVO.isConsolidatedFlag());
        assertEquals(2004, resultsVO.getYear());
    }

    public void testBuildResultVOWithEqualsAtEnd() throws Exception {
        CorpResultVO resultsVO = new CorpResultDownloadManager(null).buildResultVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?param=01-APR-200431-MAR-2005ANAACCEINFOSYSTCH=&seq_id=48669&viewFlag=N");
        assertEquals("INFOSYSTCH", resultsVO.getStockCode());
        assertEquals(new PMDate(1, 4, 2004), resultsVO.getStartDate());
        assertEquals(new PMDate(31, 3, 2005), resultsVO.getEndDate());
        assertEquals(CORP_RESULT_TIMELINE.Annual, resultsVO.getTimeline());
        assertEquals(0, resultsVO.getPeriod());
        assertTrue(resultsVO.isAuditedFlag());
        assertTrue(resultsVO.isConsolidatedFlag());
        assertEquals(2004, resultsVO.getYear());
    }

    public void testBuildResultVO_EncodedCompanyName() throws Exception {
        CorpResultVO resultsVO = new CorpResultDownloadManager(null).buildResultVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?param=01-APR-200431-MAR-2005ANAACCE" + URLEncoder.encode("M&M", "UTF-8") + "&seq_id=48669&viewFlag=N");
        assertEquals("M&M", resultsVO.getStockCode());
        assertEquals(new PMDate(1, 4, 2004), resultsVO.getStartDate());
        assertEquals(new PMDate(31, 3, 2005), resultsVO.getEndDate());
        assertEquals(CORP_RESULT_TIMELINE.Annual, resultsVO.getTimeline());
        assertEquals(0, resultsVO.getPeriod());
        assertTrue(resultsVO.isAuditedFlag());
        assertTrue(resultsVO.isConsolidatedFlag());
        assertEquals(2004, resultsVO.getYear());
    }

    public void testBuildResultVO_RELIANCE() throws Exception {
        CorpResultVO resultsVO = new CorpResultDownloadManager(null).buildResultVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?param=01-APR-200730-SEP-2007H1UNCNERELIANCE&seq_id=48669&viewFlag=N");
        assertEquals("RELIANCE", resultsVO.getStockCode());
        assertEquals(new PMDate(1, 4, 2007), resultsVO.getStartDate());
        assertEquals(new PMDate(30, 9, 2007), resultsVO.getEndDate());
        assertEquals(CORP_RESULT_TIMELINE.HalfYearly, resultsVO.getTimeline());
        assertEquals(1, resultsVO.getPeriod());
        assertFalse(resultsVO.isAuditedFlag());
        assertFalse(resultsVO.isConsolidatedFlag());
        assertEquals(2007, resultsVO.getYear());
    }
}
