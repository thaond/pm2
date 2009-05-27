package pm.net.nse;

import org.jmock.cglib.Mock;
import org.jmock.cglib.MockObjectTestCase;
import pm.AppLoader;
import pm.dao.CompanyDAO;
import pm.net.nse.downloader.CorpActionDownloader;
import pm.tools.CorpActionSynchronizer;
import pm.util.AppConfig;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.CompanyActionVO;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CorpActionDownloadManagerTest extends MockObjectTestCase {

    private String STOCKCODE1 = "RELIANCE";

    private String STOCKCODE2 = "ONGC";

    private String STOCKCODE3 = "IOC";


    static {
        AppLoader.initConsoleLogger();
    }

    //TODO Test MultiThreaded Impacts

    /*
      * Test method for
      * 'pm.net.nse.CorpActionDownloadManager.taskCompleted(AbstractDownloader)'
      */

    public void testTaskCompletedForCompletedTask() {
        final Vector<CorpActionDownloader> callList = new Vector<CorpActionDownloader>();

        CorpActionDownloadManager manager = new CorpActionDownloadManager(null) {
            {
                totalTaskCount = 5;
                completedTaskCount = 1;

            }

            @Override
            void performConsolidation(CorpActionDownloader completedTask) {
                callList.add(completedTask);
            }

            @Override
            protected void addToInCompleteList(String completedTask) {
                fail("Should come only if Task is incomplete");
            }

            @Override
            void shutDownManager() {
                fail("Should come only if all tasks are complete");
            }

        };

        CorpActionDownloader downloader = new CorpActionDownloader("", null) {
            @Override
            public boolean isCompleted() {
                return true;
            }
        };
        manager.taskCompleted(downloader);
        assertEquals(1, callList.size());
        assertTrue(callList.contains(downloader));
        assertEquals(40, manager.getProgress());
        assertFalse(manager.isTaskCompleted());
    }

    public void testTaskCompletedForInCompletedTask() {
        final Vector<String> callList = new Vector<String>();

        CorpActionDownloadManager manager = new CorpActionDownloadManager(null) {
            {
                totalTaskCount = 5;
                completedTaskCount = 1;

            }

            @Override
            void performConsolidation(CorpActionDownloader completedTask) {
                fail("Should come only if Task is complete");
            }

            protected void addToInCompleteList(String completedTask) {
                callList.add(completedTask);
            }

            @Override
            void shutDownManager() {
                fail("Should come only if all tasks are complete");
            }
        };

        String stockCode = "STOCK1";
        CorpActionDownloader downloader = new CorpActionDownloader(stockCode, null) {
            @Override
            public boolean isCompleted() {
                return false;
            }
        };
        manager.taskCompleted(downloader);
        assertEquals(1, callList.size());
        assertTrue(callList.contains(stockCode));
        assertEquals(40, manager.getProgress());
        assertFalse(manager.isTaskCompleted());
    }

    public void testTaskCompletedForCallingShutDown() {
        final Vector<Boolean> callList = new Vector<Boolean>();

        CorpActionDownloadManager manager = new CorpActionDownloadManager(null) {
            {
                totalTaskCount = 2;
                completedTaskCount = 1;

            }

            @Override
            void performConsolidation(CorpActionDownloader completedTask) {
            }

            @Override
            protected void addToInCompleteList(String completedTask) {
            }

            @Override
            void shutDownManager() {
                callList.add(true);
            }
        };

        CorpActionDownloader downloader = new CorpActionDownloader("", null) {
            @Override
            public boolean isCompleted() {
                return false;
            }
        };
        manager.taskCompleted(downloader);
        assertEquals(100, manager.getProgress());
        assertTrue(manager.isTaskCompleted());
        assertEquals(1, callList.size());
    }


    /*
      * Test method for
      * 'pm.net.nse.CorpActionDownloadManager.performConsolidation(CorpActionDownloader)'
      */
    public void testPerformConsolidation() throws Exception {
        CorpActionDownloadManager manager = new CorpActionDownloadManager(null) {
            PMDate date1 = new PMDate(1, 1, 2005);

            {
                Vector<CompanyActionVO> vector = new Vector<CompanyActionVO>();
                vector.add(new CompanyActionVO(
                        "20050101,STOCK,1,10,Split,false,"));
                htConsolidatedCorpAction.put(date1, vector);
            }

            @Override
            void shutDownManager() {
                assertEquals(2, htConsolidatedCorpAction.size());
                assertEquals(2, htConsolidatedCorpAction.get(date1).size());
                assertEquals("STOCK", htConsolidatedCorpAction.get(date1)
                        .firstElement().getStockCode());
                assertEquals("STOCK2", htConsolidatedCorpAction.get(date1)
                        .lastElement().getStockCode());
                PMDate date2 = new PMDate(2, 1, 2005);
                assertEquals(1, htConsolidatedCorpAction.get(date2).size());
                assertEquals("STOCK2", htConsolidatedCorpAction.get(date2)
                        .lastElement().getStockCode());
            }
        };

        CorpActionDownloader downloader = new CorpActionDownloader("", manager) {
            @Override
            public Vector<CompanyActionVO> getCorpActions() {
                Vector<CompanyActionVO> retVal = new Vector<CompanyActionVO>();
                try {
                    retVal.add(new CompanyActionVO(
                            "20050101,STOCK2,1,10,Split,false,"));
                    retVal.add(new CompanyActionVO(
                            "20050102,STOCK2,1,10,Split,false,"));
                } catch (Exception e) {
                }
                return retVal;
            }
        };
        manager.performConsolidation(downloader);
        manager.shutDownManager(); // all tests r down inside that
    }

    public void testShutDownManager() throws Exception {
        String strLastDate = AppConfig.dateCORPACTIONDOWNLOADMANAGER.Value;
        String strInCompleteList = AppConfig.corpActionDownloadErrorList.Value;
        Hashtable<PMDate, Vector<CompanyActionVO>> htConsidated = new Hashtable<PMDate, Vector<CompanyActionVO>>();
        final Mock compayDAOMock = new Mock(CompanyDAO.class);
        compayDAOMock.expects(once()).method("writeConsolidatedActionData").with(eq(htConsidated));

        final Mock synchronizerMock = new Mock(CorpActionSynchronizer.class);
        synchronizerMock.expects(once()).method("applyCorpAction").withNoArguments();

        CorpActionDownloadManager manager = new CorpActionDownloadManager(null) {
            {
                inCompleteList = new Vector<String>();
                inCompleteList.add(STOCKCODE1);
            }

            @Override
            CompanyDAO getCompanyDAO() {
                return (CompanyDAO) compayDAOMock.proxy();
            }

            CorpActionSynchronizer getSynchronizer() {
                return (CorpActionSynchronizer) synchronizerMock.proxy();
            }
        };
        manager.shutDownManager();
        assertTrue(PMDateFormatter.formatYYYYMMDD(new PMDate()).equals(
                AppConfig.dateCORPACTIONDOWNLOADMANAGER.Value));
        assertTrue(STOCKCODE1
                .equals(AppConfig.corpActionDownloadErrorList.Value));

        AppConfig.dateCORPACTIONDOWNLOADMANAGER.Value = strLastDate;
        AppConfig.corpActionDownloadErrorList.Value = strInCompleteList;
        AppConfig.saveConfigDetails();
        compayDAOMock.verify();
        synchronizerMock.verify();
    }

    /*
      * Test method for 'pm.net.nse.CorpActionDownloadManager.run()'
      */
    public void testRunToUseStoredConsolidatedDataWhenUsingIncompleteList()
            throws InterruptedException {
        int maxThreadCount = 2;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThreadCount, maxThreadCount, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        final Vector<Boolean> callList = new Vector<Boolean>();
        final Hashtable<PMDate, Vector<CompanyActionVO>> storedData = new Hashtable<PMDate, Vector<CompanyActionVO>>();
        final Vector<String> storedInCompleteList = new Vector<String>();
        final Mock synchronizerMock = new Mock(CorpActionSynchronizer.class);
        synchronizerMock.expects(once()).method("applyCorpAction").withNoArguments();

        CorpActionDownloadManager manager = new CorpActionDownloadManager(
                executor) {
            @Override
            CompanyDAO getCompanyDAO() {
                return new CompanyDAO() {
                    @Override
                    public Hashtable<PMDate, Vector<CompanyActionVO>> getConsolidatedActionData() {
                        Hashtable<PMDate, Vector<CompanyActionVO>> retVal = new Hashtable<PMDate, Vector<CompanyActionVO>>();
                        Vector<CompanyActionVO> vector = new Vector<CompanyActionVO>();
                        try {
                            vector.add(new CompanyActionVO(
                                    "20050101,STOCK,1,10,Split,false,"));
                        } catch (Exception e) {
                        }
                        retVal.put(new PMDate(1, 1, 2005), vector);
                        return retVal;
                    }

                    @Override
                    public boolean writeConsolidatedActionData(
                            Hashtable<PMDate, Vector<CompanyActionVO>> consolidatedData) {
                        storedData.putAll(consolidatedData);
                        return true;
                    }
                };
            }

            CorpActionSynchronizer getSynchronizer() {
                return (CorpActionSynchronizer) synchronizerMock.proxy();
            }

            @Override
            CorpActionDownloader createDownloader(String stockCode) {
                CorpActionDownloader corpActionDownloader;
                if (stockCode.equals(STOCKCODE1)) {
                    corpActionDownloader = new CorpActionDownloader(stockCode,
                            this) {
                        @Override
                        protected void performTask() {
                        }

                        @Override
                        public Vector<CompanyActionVO> getCorpActions() {
                            Vector<CompanyActionVO> vector = new Vector<CompanyActionVO>();
                            try {
                                vector.add(new CompanyActionVO(
                                        "20050101,STOCK1,1,10,Split,false,"));
                            } catch (Exception e) {
                            }
                            return vector;
                        }
                    };

                } else {
                    corpActionDownloader = new CorpActionDownloader(stockCode,
                            this) {
                        @Override
                        protected void performTask() {
                        }

                        @Override
                        public boolean isCompleted() {
                            return false;
                        }
                    };

                }
                return corpActionDownloader;
            }

            @Override
            protected void storeIncompleteList() {
                storedInCompleteList.addAll(inCompleteList);
            }

            @Override
            protected void saveStatus() {
            }

            @Override
            void shutDownManager() {
//				System.out.println(completedTaskCount);
                super.shutDownManager();
                callList.add(true);
            }
        };

        AppConfig.corpActionDownloadErrorList.Value = STOCKCODE1 + ","
                + STOCKCODE2;
        AppConfig.dateCORPACTIONDOWNLOADMANAGER.Value = PMDateFormatter
                .formatYYYYMMDD(new PMDate());

        manager.run();
        executor.awaitTermination(1, TimeUnit.SECONDS);
        assertEquals(1, callList.size());
        assertTrue(callList.firstElement());

        assertEquals(1, storedData.size());
        assertEquals(2, storedData.elements().nextElement().size());
        assertEquals("STOCK", storedData.values().iterator().next()
                .firstElement().getStockCode());
        assertEquals("STOCK1", storedData.values().iterator().next()
                .lastElement().getStockCode());

        assertEquals(1, storedInCompleteList.size());
        assertTrue(storedInCompleteList.contains(STOCKCODE2));
    }

    public void testRunToIgnoreStoredConsolidatedDataWhenUsingCompleteList()
            throws InterruptedException {
        int maxThreadCount = 2;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThreadCount, maxThreadCount, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        final Vector<Boolean> callList = new Vector<Boolean>();
        final Hashtable<PMDate, Vector<CompanyActionVO>> storedData = new Hashtable<PMDate, Vector<CompanyActionVO>>();
        final Vector<String> storedInCompleteList = new Vector<String>();
        final Mock synchronizerMock = new Mock(CorpActionSynchronizer.class);
        synchronizerMock.expects(once()).method("applyCorpAction").withNoArguments();

        CorpActionDownloadManager manager = new CorpActionDownloadManager(
                executor) {
            @Override
            CompanyDAO getCompanyDAO() {
                return new CompanyDAO() {
                    @Override
                    public Hashtable<PMDate, Vector<CompanyActionVO>> getConsolidatedActionData() {
                        Hashtable<PMDate, Vector<CompanyActionVO>> retVal = new Hashtable<PMDate, Vector<CompanyActionVO>>();
                        Vector<CompanyActionVO> vector = new Vector<CompanyActionVO>();
                        try {
                            vector.add(new CompanyActionVO(
                                    "20050101,STOCK,1,10,Split,false,"));
                        } catch (Exception e) {
                        }
                        retVal.put(new PMDate(1, 1, 2005), vector);
                        return retVal;
                    }

                    @Override
                    public boolean writeConsolidatedActionData(
                            Hashtable<PMDate, Vector<CompanyActionVO>> consolidatedData) {
                        storedData.putAll(consolidatedData);
                        return true;
                    }
                };
            }

            CorpActionSynchronizer getSynchronizer() {
                return (CorpActionSynchronizer) synchronizerMock.proxy();
            }

            @Override
            CorpActionDownloader createDownloader(String stockCode) {
                CorpActionDownloader corpActionDownloader;
                if (stockCode.equals(STOCKCODE1)) {
                    corpActionDownloader = new CorpActionDownloader(stockCode,
                            this) {
                        @Override
                        protected void performTask() {
                        }

                        @Override
                        public Vector<CompanyActionVO> getCorpActions() {
                            Vector<CompanyActionVO> vector = new Vector<CompanyActionVO>();
                            try {
                                vector.add(new CompanyActionVO(
                                        "20050102,STOCK1,1,10,Split,false,"));
                            } catch (Exception e) {
                            }
                            return vector;
                        }
                    };

                } else {
                    corpActionDownloader = new CorpActionDownloader(stockCode,
                            this) {
                        @Override
                        protected void performTask() {
                        }

                        @Override
                        public boolean isCompleted() {
                            return false;
                        }
                    };

                }
                return corpActionDownloader;
            }

            @Override
            protected void storeIncompleteList() {
                storedInCompleteList.addAll(inCompleteList);
            }

            @Override
            protected void saveStatus() {
            }

            @Override
            void shutDownManager() {
                super.shutDownManager();
                callList.add(true);
            }

            @Override
            protected Vector<String> getCompleteList() {
                flagCompleteListLoaded = true;
                Vector<String> completeList = new Vector<String>();
                completeList.add(STOCKCODE1);
                completeList.add(STOCKCODE2);
                completeList.add(STOCKCODE3);
                return completeList;
            }

        };

        AppConfig.corpActionDownloadErrorList.Value = STOCKCODE1 + ","
                + STOCKCODE2;
        AppConfig.dateCORPACTIONDOWNLOADMANAGER.Value = "20010101";

        manager.run();
        executor.awaitTermination(1, TimeUnit.SECONDS);
        assertEquals(1, callList.size());
        assertTrue(callList.firstElement());

        assertEquals(1, storedData.size());
        assertEquals(1, storedData.elements().nextElement().size());
        assertEquals("STOCK1", storedData.values().iterator().next()
                .firstElement().getStockCode());

        assertEquals(2, storedInCompleteList.size());
        assertTrue(storedInCompleteList.contains(STOCKCODE2));
        assertTrue(storedInCompleteList.contains(STOCKCODE3));
    }

    public void testProgressData() throws InterruptedException {
        final Vector<Integer> terminateDetails = new Vector<Integer>();
        int maxThreadCount = 2;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThreadCount, maxThreadCount, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        CorpActionDownloadManager manager = new CorpActionDownloadManager(
                executor) {
            @Override
            protected Vector<String> getStockList() {
                Vector<String> retVal = new Vector<String>();
                retVal.add(STOCKCODE1);
                retVal.add(STOCKCODE2);
                retVal.add(STOCKCODE3);
                return retVal;
            }

            @Override
            CompanyDAO getCompanyDAO() {
                return new CompanyDAO() {
                    @Override
                    public Hashtable<PMDate, Vector<CompanyActionVO>> getConsolidatedActionData() {
                        return new Hashtable<PMDate, Vector<CompanyActionVO>>();
                    }

                    @Override
                    public boolean writeConsolidatedActionData(
                            Hashtable<PMDate, Vector<CompanyActionVO>> consolidatedData) {
                        return true;
                    }
                };
            }

            @Override
            CorpActionDownloader createDownloader(String stockCode) {
                return new CorpActionDownloader(stockCode, this) {
                    @Override
                    protected void performTask() {
                        int waitCount = 0;
                        if (getStockCode().equals(STOCKCODE1)) {
                            waitCount = 1;
                        } else if (getStockCode().equals(STOCKCODE2)) {
                            waitCount = 2;
                        } else {
                            waitCount = 3;
                        }
                        while (terminateDetails.size() < waitCount) {
                            Thread.yield();
                        }
                    }
                };
            }

            @Override
            void shutDownManager() {
            }

        };

        manager.run();
        assertEquals(100, manager.getTaskLength());
        assertEquals(0, manager.getProgress());
        terminateDetails.add(1);
        Thread.sleep(100);
        assertEquals(33, manager.getProgress());
        assertFalse(manager.isTaskCompleted());
        terminateDetails.add(2);
        Thread.sleep(100);
        assertEquals(66, manager.getProgress());
        assertFalse(manager.isTaskCompleted());
        terminateDetails.add(3);
        Thread.sleep(100);
        assertEquals(100, manager.getProgress());
        assertTrue(manager.isTaskCompleted());

    }

    /*
      * Test method for 'pm.net.nse.CorpActionDownloadManager.stop()'
      */
    public void testStop() {

    }

}
