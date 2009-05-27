package pm.net.nse;

import junit.framework.TestCase;
import pm.AppLoader;
import pm.net.AbstractDownloader;
import pm.util.AppConfig;
import pm.util.PMDate;
import pm.util.enumlist.TASKNAME;

import java.util.Vector;

public class AbstractStockDownloadManagerTest extends TestCase {

    private String STOCKCODE1 = "RELIANCE";

    private String STOCKCODE2 = "ONGC";

    private String STOCKCODE3 = "IOC";

    private String COMMA = ",";

    private String STOCKCODECSV = STOCKCODE1 + COMMA + STOCKCODE2 + COMMA
            + STOCKCODE3;

    static {
        AppLoader.initConsoleLogger();
    }

    public void testLoadInComleteList() {
        String oldValue = AppConfig.corpActionDownloadErrorList.Value;
        AppConfig.corpActionDownloadErrorList.Value = STOCKCODECSV;
        Vector inComleteList = new AbstractDownloadManagerImpl()
                .loadInComleteList();
        assertEquals(3, inComleteList.size());
        assertTrue(inComleteList.contains(STOCKCODE1));
        assertTrue(inComleteList.contains(STOCKCODE2));
        assertTrue(inComleteList.contains(STOCKCODE3));
        AppConfig.corpActionDownloadErrorList.Value = oldValue;
    }

    public void testLoadInComleteListToReturnEmptyVectorWhenListEmpty() {
        String oldValue = AppConfig.corpActionDownloadErrorList.Value;
        AppConfig.corpActionDownloadErrorList.Value = "";
        Vector inComleteList = new AbstractDownloadManagerImpl()
                .loadInComleteList();
        assertEquals(0, inComleteList.size());
        AppConfig.corpActionDownloadErrorList.Value = oldValue;
    }

    public void testStoreIncompleteList() {
        String oldValue = AppConfig.corpActionDownloadErrorList.Value;
        final Vector<String> incompleteList = new Vector<String>();
        incompleteList.add(STOCKCODE1);
        incompleteList.add(STOCKCODE2);
        incompleteList.add(STOCKCODE3);
        new AbstractDownloadManagerImpl() {
            {
                this.inCompleteList = incompleteList;
            }
        }.storeIncompleteList();
        assertTrue(AppConfig.corpActionDownloadErrorList.Value
                .equals(STOCKCODECSV));
        AppConfig.corpActionDownloadErrorList.Value = oldValue;
        AppConfig.saveConfigDetails();
    }

    public void testStoreIncompleteListWhenListIsEmpty() {
        String oldValue = AppConfig.corpActionDownloadErrorList.Value;
        AppConfig.corpActionDownloadErrorList.Value = "SOMETHING";
        new AbstractDownloadManagerImpl().storeIncompleteList();
        assertTrue("".equals(AppConfig.corpActionDownloadErrorList.Value));
        AppConfig.corpActionDownloadErrorList.Value = oldValue;
        AppConfig.saveConfigDetails();
    }

    /*
      * Test method for 'pm.net.nse.AbstractDownloadManager.getCompleteList()'
      */
    public void testGetCompleteList() {

    }

    public void testGetStockListToLoadInComplete() {
        final Vector<String> vInCompleteList = new Vector<String>();
        vInCompleteList.add("SOMETHING");
        AbstractStockDownloadManager manager = new AbstractDownloadManagerImpl() {
            @Override
            protected Vector<String> loadInComleteList() {
                return vInCompleteList;
            }

            @Override
            protected Vector<String> getCompleteList() {
                fail("Shoule be called only for empty InCompleteList");
                return null;
            }

            @Override
            PMDate getLastRunDate() {
                return new PMDate();
            }
        };
        assertEquals(vInCompleteList, manager.getStockList());

    }

    public void testGetStockListToSkipLoadingInCompleteListIfLastRunDateIsBefore1Week() {
        final Vector<String> vCompleteList = new Vector<String>();
        vCompleteList.add("SOMETHING");
        AbstractStockDownloadManager manager = new AbstractDownloadManagerImpl() {
            @Override
            protected Vector<String> loadInComleteList() {
                fail("Should not load incomplete list if last run is before a week");
                return null;
            }

            @Override
            protected Vector<String> getCompleteList() {
                return vCompleteList;
            }

            @Override
            PMDate getLastRunDate() {
                PMDate date = new PMDate();
                date = date.getDateAddingDays(-7);
                return date;
            }
        };
        assertEquals(vCompleteList, manager.getStockList());

    }

    public void testGetStockListToLoadCompleteListIfInCompleteListEmpty() {
        final Vector<String> vCompleteList = new Vector<String>();
        vCompleteList.add("SOMETHING");
        AbstractStockDownloadManager manager = new AbstractDownloadManagerImpl() {
            @Override
            protected Vector<String> loadInComleteList() {
                return new Vector<String>();
            }

            @Override
            protected Vector<String> getCompleteList() {
                return vCompleteList;
            }

            @Override
            PMDate getLastRunDate() {
                return new PMDate();
            }
        };
        assertEquals(vCompleteList, manager.getStockList());
    }

    class AbstractDownloadManagerImpl extends AbstractStockDownloadManager {

        @Override
        public void taskCompleted(AbstractDownloader completedTask) {

        }

        @Override
        AppConfig getErrorListConfig() {
            return AppConfig.corpActionDownloadErrorList;
        }

        public void stop() {

        }

        public void run() {
        }

        public TASKNAME getTaskName() {
            return TASKNAME.CORPACTIONDOWNLOAD;
        }

        public boolean isIndeterminate() {
            return false;
        }

    }

}
