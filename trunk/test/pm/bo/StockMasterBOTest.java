package pm.bo;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import pm.AppLoader;
import pm.dao.derby.DBManager;
import pm.dao.ibatis.dao.DAOManager;
import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.StockVO;
import pm.vo.StopLossVO;
import pm.vo.WatchlistVO;

import java.io.FileInputStream;
import java.util.*;

/**
 * StockMasterBO Tester.
 *
 * @version 1.0
 * @since <pre>07/23/2006</pre>
 */
public class StockMasterBOTest extends DatabaseTestCase {
    private static final String CODE16 = "CODE16";
    private static final String CODE16_NEW = "CODE16NEW";

    public StockMasterBOTest(String name) {
        super(name);
        AppLoader.initConsoleLogger();
    }

    @Override
    protected IDatabaseConnection getConnection() throws Exception {
        return new DatabaseConnection(DBManager.getConnection());
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(new FileInputStream("TestData.xml"));
    }

    @Override
    protected void closeConnection(IDatabaseConnection arg0) throws Exception {
//		super.closeConnection(arg0);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetListedStockDetails() throws Exception {
        StockMasterBO bo = new StockMasterBO();
        assertNull(bo.getListedStockDetails("DELISTED"));
        assertNotNull(bo.getListedStockDetails("CODE1"));
    }

    public void testUpdatingStockDetails() throws Exception {
        StockMasterBO bo = new StockMasterBO();
        Vector<StockVO> stockList = new Vector<StockVO>();
        String stockCode = "NewCode0";
        StockVO stockVO = new StockVO(stockCode, "CompanyName", (short) 10, SERIESTYPE.equity, (short) 10, (short) 100, "ISIN12345", new PMDate(1, 1, 2006), true);
        stockList.add(stockVO);
        assertTrue(bo.storeStockList(stockList));
        assertEquals(stockVO, bo.getListedStockDetails(stockCode));
    }

    public void testStoreStockList_IgnoringExisting() throws Exception {
        StockMasterBO bo = new StockMasterBO();
        String stockCode = "CODE2";
        StockVO stockVO = bo.getListedStockDetails(stockCode);
        Vector<StockVO> stockList = new Vector<StockVO>();
        stockList.add(stockVO);
        assertTrue(bo.storeStockList(stockList));
        assertEquals(stockVO, bo.getListedStockDetails(stockCode));
    }

    public void testUpdatingStockCodeChange() throws Exception {
        StockMasterBO bo = new StockMasterBO();
        String stockCode = "CODE2";
        StockVO stockVO = bo.getListedStockDetails(stockCode);
        String newStockCode = "NEWCODE";
        stockVO.setStockCode(newStockCode);
        Vector<StockVO> stockList = new Vector<StockVO>();
        stockList.add(stockVO);
        assertTrue(bo.storeStockList(stockList));
        assertEquals(stockVO, bo.getListedStockDetails(newStockCode));
        assertNull(bo.getListedStockDetails(stockCode));
    }

    public void testStoreStockList_InsertingNewStock() throws Exception {
        StockMasterBO bo = new StockMasterBO();
        Vector<StockVO> stockList = new Vector<StockVO>();
        String stockCode = "CODENEW1";
        StockVO stockVO = new StockVO(stockCode, "CompanyName", (short) 10, SERIESTYPE.equity, (short) 10, (short) 100, "ISINNEW", new PMDate(1, 1, 2006), true);
        stockList.add(stockVO);
        assertTrue(bo.storeStockList(stockList));
        assertEquals(stockVO, bo.getListedStockDetails(stockCode));
    }

    public void testInsertMissingStockCodes() throws Exception {
        StockMasterBO bo = new StockMasterBO();
        String code1 = "CODE1";
        String newCode = "MyNewCODE1";
        StockVO stockVO = bo.getListedStockDetails(code1);
        HashSet<String> stockList = new HashSet<String>();
        stockList.add(code1);
        stockList.add(newCode);
        bo.insertMissingStockCodes(stockList);
        assertEquals(stockVO, bo.getListedStockDetails(code1));
        assertNotNull(bo.getListedStockDetails(newCode));

    }

    public void testGetStockListToReturnOnlyListed() throws Exception {
        StockMasterBO bo = new StockMasterBO();
        Vector<StockVO> stockList = bo.getStockList(false);
        StockVO stockVO = bo.getListedStockDetails("DELISTED");
        assertFalse(stockList.contains(stockVO));
        for (StockVO vo : stockList) {
            assertTrue(vo.isListed());
        }
    }

    public void testDoSymbolChange() throws Exception {
        setUp();
        String code1 = "CODE1";
        StockMasterBO bo = new StockMasterBO();
        StockVO stockVO = bo.getListedStockDetails(code1);
        String newCode = "NEWCODE1";
        bo.doSymbolChange(code1, newCode);
        assertNull(bo.getListedStockDetails(code1));
        StockVO updateStockVO = bo.getListedStockDetails(newCode);
        assertEquals(stockVO.getId(), updateStockVO.getId());
        assertEquals(newCode, updateStockVO.getStockCode());
    }

    public void testUpdateMissingListToChangeToUnListedStatus() throws Exception {
        StockMasterBO bo = new StockMasterBO() {
            boolean isLastQuote90DaysOld(StockVO stockVO) {
                return true;
            }
        };
        Vector<StockVO> stockList = bo.getStockList(false);
        StockVO stockVO = stockList.remove(0);
        assertTrue(stockVO.isListed());
        bo.storeStockList(stockList);
        assertNull(bo.getListedStockDetails(stockVO.getStockCode()));
    }

    public void testUpdateMissingListToIgnoreIPOStockFromConvertingTONotListedStatus() throws Exception {
        StockMasterBO bo = new StockMasterBO();
        String stockCode = "NoDeList";
        bo.insertNewStock(stockCode);
        bo.storeStockList(new Vector<StockVO>());
        assertNotNull(bo.getListedStockDetails("IPOAlloted"));
        assertNotNull(bo.getListedStockDetails(stockCode));
    }

    public void testStoreStockList_StockCodeChangeAddedDuplicateEntryInStockMasterByEODRun() {
        StockMasterBO masterBO = new StockMasterBO() {
            void updateDelistedStocks(List<StockVO> newStockList, List<StockVO> stockList) {

            }
        };
        int stockListSizeWithDuplicate = masterBO.getStockList(true).size();
        ArrayList<StockVO> newStockList = new ArrayList<StockVO>();
        String stockCode = CODE16_NEW;
        StockVO newStockVOFromNSE = new StockVO(stockCode, "CodeChangeIssue", 10f, SERIESTYPE.equity, 10f, (short) 100, "ISIN16", new PMDate(1, 1, 2006), true);
        newStockList.add(newStockVOFromNSE);
        masterBO.storeStockList(newStockList);
        StockVO updatedStockVO = masterBO.getListedStockDetails(stockCode);
        assertEquals(newStockVOFromNSE, updatedStockVO);
        assertEquals(16, updatedStockVO.getId());
        int stockListSizeAfterRemovingDuplicate = masterBO.getStockList(true).size();
        assertEquals(stockListSizeAfterRemovingDuplicate + 1, stockListSizeWithDuplicate);
    }

    public void testStoreStockList_StockCodeChangeAddedDuplicateEntryInStockMasterByEODRun_NewCodeComesBeforeOld() {
        StockMasterBO masterBO = new StockMasterBO() {
            void updateDelistedStocks(List<StockVO> newStockList, List<StockVO> stockList) {
            }
        };
        String newStockCode = "CODE016";
        DAOManager.getStockDAO().insertStock(new StockVO("Code123", "CodeChangeIssue", 10f, SERIESTYPE.equity, 10f, (short) 100, "ISIN123", new PMDate(1, 1, 2006), true));
        masterBO.insertNewStock(newStockCode);
        int stockListSizeWithDuplicate = masterBO.getStockList(true).size();
        ArrayList<StockVO> newStockList = new ArrayList<StockVO>();
        StockVO newStockVOFromNSE = new StockVO(newStockCode, "CodeChangeIssue", 10f, SERIESTYPE.equity, 10f, (short) 100, "ISIN16", new PMDate(1, 1, 2006), true);
        newStockList.add(newStockVOFromNSE);
        masterBO.storeStockList(newStockList);
        StockVO updatedStockVO = masterBO.getListedStockDetails(newStockCode);
        assertEquals(newStockVOFromNSE, updatedStockVO);
        Vector<StockVO> stockList = masterBO.getStockList(true);
        int stockListSizeAfterRemovingDuplicate = stockList.size();
        int noOfDuplicates = 1;
        assertEquals(stockListSizeWithDuplicate - noOfDuplicates, stockListSizeAfterRemovingDuplicate);
    }

    public void testStoreStockList_StockCodeChangedButNoDuplicateEntryYetInStockMaster() {
        StockMasterBO masterBO = new StockMasterBO() {
            void updateDelistedStocks(List<StockVO> newStockList, List<StockVO> stockList) {

            }
        };
        int existingStockListSize = masterBO.getStockList(true).size();
        ArrayList<StockVO> newStockList = new ArrayList<StockVO>();
        String stockCode = "CODE18NEW";
        StockVO newStockVOFromNSE = new StockVO(stockCode, "CodeChangeWithoutDuplicate", 10f, SERIESTYPE.equity, 10f, (short) 100, "ISIN18", new PMDate(1, 1, 2006), true);
        newStockList.add(newStockVOFromNSE);
        masterBO.storeStockList(newStockList);
        StockVO stockDetails = masterBO.getListedStockDetails(stockCode);
        assertEquals(newStockVOFromNSE, stockDetails);
        assertEquals(18, stockDetails.getId());
        assertEquals(existingStockListSize, masterBO.getStockList(true).size());
    }

    public void testStoreStockList_CompanyNameChange() {
        StockMasterBO masterBO = new StockMasterBO() {
            void updateDelistedStocks(List<StockVO> newStockList, List<StockVO> stockList) {

            }
        };
        int existingStockListSize = masterBO.getStockList(true).size();
        ArrayList<StockVO> newStockList = new ArrayList<StockVO>();
        String stockCode = "CODE19";
        StockVO newStockVOFromNSE = new StockVO(stockCode, "NewCompanyName", 10f, SERIESTYPE.equity, 10f, (short) 100, "ISIN19", new PMDate(1, 1, 2006), true);
        newStockList.add(newStockVOFromNSE);
        masterBO.storeStockList(newStockList);
        StockVO stockDetails = masterBO.getListedStockDetails(stockCode);
        assertEquals(newStockVOFromNSE, stockDetails);
        assertEquals(19, stockDetails.getId());
        assertEquals(existingStockListSize, masterBO.getStockList(true).size());
    }

    public void testStoreStockList_FaceValueChange() {
        StockMasterBO masterBO = new StockMasterBO() {
            void updateDelistedStocks(List<StockVO> newStockList, List<StockVO> stockList) {

            }
        };
        int existingStockListSize = masterBO.getStockList(true).size();
        ArrayList<StockVO> newStockList = new ArrayList<StockVO>();
        String stockCode = "CODE20";
        StockVO newStockVOFromNSE = new StockVO(stockCode, "FaceValueChange", 2f, SERIESTYPE.equity, 2f, (short) 100, "ISIN20", new PMDate(1, 1, 2006), true);
        newStockList.add(newStockVOFromNSE);
        masterBO.storeStockList(newStockList);
        StockVO stockDetails = masterBO.getListedStockDetails(stockCode);
        assertEquals(newStockVOFromNSE, stockDetails);
        assertEquals(20, stockDetails.getId());
        assertEquals(existingStockListSize, masterBO.getStockList(true).size());
    }

    public void testInsertNewStock() {
        StockMasterBO masterBO = new StockMasterBO();
        int existingStockListSize = masterBO.getStockList(true).size();
        String stockCode = "NEWSTK";
        String companyName = "NEWSTKNAME";
        StockVO expectedStockVO = new StockVO(stockCode, companyName, 10f, SERIESTYPE.equity, 10f, (short) 1, "PM_" + stockCode, new PMDate(), true);
        masterBO.insertNewStock(stockCode, companyName);
        StockVO stockVO = DAOManager.getStockDAO().getStock(stockCode);
        assertEquals(expectedStockVO, stockVO);
        assertEquals(existingStockListSize + 1, masterBO.getStockList(true).size());
    }

    public void testRemoveDuplicate() {
        int initialStockListSize = DAOManager.getStockDAO().getStockList(true).size();
        int originalIdQuotes = DAOManager.getQuoteDAO().getQuotes(CODE16).size();
        int duplicateIdQuotes = DAOManager.getQuoteDAO().getQuotes(CODE16_NEW).size();
        int originalTransSize = DAOManager.getTransactionDAO().getTransactionList(null, null, CODE16, true).size();
        int duplicateTransSize = DAOManager.getTransactionDAO().getTransactionList(null, null, CODE16_NEW, true).size();
        int initialWLSize = DAOManager.getWatchlistDAO().getWatchlistVos(1).size();
        int originalCompanyActionSize = DAOManager.getCompanyActionDAO().getCompanyAction(CODE16).size();
        int duplicateCompanyActionSize = DAOManager.getCompanyActionDAO().getCompanyAction(CODE16_NEW).size();

        final StockVO originalStockVO = DAOManager.getStockDAO().getStock(CODE16);
        final StockVO duplicateStockVO = DAOManager.getStockDAO().getStock(CODE16_NEW);
        int duplicateStockId = duplicateStockVO.getId();
        new StockMasterBO().removeDuplicate(duplicateStockVO, originalStockVO);
        verifyDuplicateRemovedInStockMaster(initialStockListSize, duplicateStockId);

        verifyDuplicateMappedToOriginalInQuote(originalIdQuotes, duplicateIdQuotes);
        verifyReMappingInTransaction(originalTransSize, duplicateTransSize);
        verifyOriginalRemovedAndPointsToDuplicateInStopLoss();
        verifyOriginalRemovedAndPointsToDuplicateInWL(initialWLSize);
        verifyReMappingInCompanyAction(originalCompanyActionSize, duplicateCompanyActionSize);
    }

    private void verifyReMappingInCompanyAction(int originalCompanyActionSize, int duplicateCompanyActionSize) {
        int combinedActionSize = originalCompanyActionSize + duplicateCompanyActionSize;
        int actualActionSize = DAOManager.getCompanyActionDAO().getCompanyAction(CODE16).size();
        assertEquals(combinedActionSize, actualActionSize);
    }

    private void verifyOriginalRemovedAndPointsToDuplicateInWL(int origianlWLSize) {
        List<WatchlistVO> watchList = DAOManager.getWatchlistDAO().getWatchlistVos(1);
        assertEquals(origianlWLSize - 1, watchList.size());
        for (WatchlistVO watchlistVO : watchList) {
            if (watchlistVO.getId() == 1) fail("Old watchlist not removed");
            if (watchlistVO.getId() == 2 && !watchlistVO.getStockCode().equals(CODE16))
                fail("Duplicate not assigned to Original");
        }
    }

    private void verifyOriginalRemovedAndPointsToDuplicateInStopLoss() {
        StopLossVO lossVO = DAOManager.getPortfolioDAO().getStopLoss("PortfolioName", CODE16);
        assertEquals(11, lossVO.getId());
    }

    private void verifyReMappingInTransaction(int originalTransSize, int duplicateTransSize) {
        assertEquals(originalTransSize + duplicateTransSize, DAOManager.getTransactionDAO().getTransactionList(null, null, CODE16, true).size());
    }

    private void verifyDuplicateMappedToOriginalInQuote(int originalIdQuotes, int duplicateIdQuotes) {
        assertEquals(originalIdQuotes + duplicateIdQuotes, DAOManager.getQuoteDAO().getQuotes(CODE16).size());
    }


    private void verifyDuplicateRemovedInStockMaster(int initialStockListSize, int duplicateStockId) {
        int duplicateListSize = 1;
        List<StockVO> stockList = DAOManager.getStockDAO().getStockList(true);
        assertEquals(initialStockListSize - duplicateListSize, stockList.size());
        for (StockVO stockVO : stockList) {
            if (stockVO.getId() == duplicateStockId) fail("Still duplicate stock code found");
        }
    }

    public void testAddNewStocks() throws CloneNotSupportedException {
        final List<StockVO> insertedList = new ArrayList<StockVO>();
        StockMasterBO bo = new StockMasterBO() {
            void insertStockList(List<StockVO> newToPMList) {
                insertedList.addAll(newToPMList);
            }
        };
        StockVO stockVOExisting = new StockVO(100, "CODE1", "Company1", 10f, SERIESTYPE.equity, 10f, (short) 10, "Isin1", new PMDate(1, 1, 2005), true);
        StockVO stockVOExistingIncoming = (StockVO) stockVOExisting.clone();
        stockVOExistingIncoming.setId(0);
        StockVO stockVONew = new StockVO("CODE2", "Company1", 10f, SERIESTYPE.equity, 10f, (short) 10, "Isin2", new PMDate(1, 1, 2005), true);

        ArrayList<StockVO> incomingStockList = new ArrayList<StockVO>();
        incomingStockList.add(stockVOExistingIncoming);
        incomingStockList.add(stockVONew);
        ArrayList<StockVO> existingStockList = new ArrayList<StockVO>();
        existingStockList.add(stockVOExisting);
        existingStockList.add(new StockVO(300, "CODE3", "Delisted", 10f, SERIESTYPE.equity, 10f, (short) 10, "Isin3", new PMDate(1, 1, 2005), true));
        bo.addNewStocks(incomingStockList, existingStockList);

        assertEquals(1, insertedList.size());
        assertTrue(insertedList.contains(stockVONew));
    }

    public void testUpdateDelistedStocks() throws CloneNotSupportedException {
        final List<StockVO> delisted = new ArrayList<StockVO>();
        StockMasterBO bo = new StockMasterBO() {
            void updateStockList(List<StockVO> delistedList) {
                delisted.addAll(delistedList);
            }
        };

        StockVO stockVOExisting = new StockVO(100, "CODE1", "Company1", 10f, SERIESTYPE.equity, 10f, (short) 10, "Isin1", new PMDate(1, 1, 2005), true);
        StockVO stockVOExistingIncoming = (StockVO) stockVOExisting.clone();
        stockVOExistingIncoming.setId(0);
        StockVO stockVONew = new StockVO("CODE2", "Company1", 10f, SERIESTYPE.equity, 10f, (short) 10, "Isin2", new PMDate(1, 1, 2005), true);

        ArrayList<StockVO> incomingStockList = new ArrayList<StockVO>();
        incomingStockList.add(stockVOExistingIncoming);
        incomingStockList.add(stockVONew);

        ArrayList<StockVO> existingStockList = new ArrayList<StockVO>();
        existingStockList.add(stockVOExisting);
        StockVO delistedVO = new StockVO(300, "CODE3", "Delisted", 10f, SERIESTYPE.equity, 10f, (short) 10, "Isin3", new PMDate(1, 1, 2005), true);
        existingStockList.add(delistedVO);

        bo.updateDelistedStocks(incomingStockList, existingStockList);

        assertEquals(1, delisted.size());
        assertEquals(300, delisted.get(0).getId());
        assertFalse(delisted.get(0).isListed());
    }

    public void testEliminateDuplicateUpdateModified() throws CloneNotSupportedException {
        final List<StockVO> updateStockList = new ArrayList<StockVO>();
        final Map<StockVO, StockVO> duplicateStockMap = new HashMap<StockVO, StockVO>();

        StockVO n_NotModifiedVO = new StockVO("CODE1", "Company1", 10f, SERIESTYPE.equity, 10f, (short) 10, "Isin1", new PMDate(1, 1, 2005), true);
        StockVO e_NotModifiedVO = (StockVO) n_NotModifiedVO.clone();
        e_NotModifiedVO.setId(100);

        int codeChangedId = 2;
        StockVO tobeCodeChangedWithDuplicateVO = new StockVO(codeChangedId, "CODE2", "Company2", 10f, SERIESTYPE.equity, 10f, (short) 10, "Isin2", new PMDate(1, 1, 2005), true);
        StockVO codeChangedVO = (StockVO) tobeCodeChangedWithDuplicateVO.clone();
        String newStockCode2 = "CODE2NEW";
        codeChangedVO.setStockCode(newStockCode2);
        codeChangedVO.setId(0);

        StockVO duplicateVO = (StockVO) tobeCodeChangedWithDuplicateVO.clone();
        duplicateVO.setId(201);
        duplicateVO.setStockCode(newStockCode2);
        duplicateVO.setIsin(StockMasterBO.DUMMYISIN_PREFIX + newStockCode2);

        int codeChanged2Id = 301;
        StockVO tobeCodeChangedVO2 = new StockVO(codeChanged2Id, "CODE22", "Company22", 10f, SERIESTYPE.equity, 10f, (short) 10, "Isin22", new PMDate(1, 1, 2005), true);
        StockVO codeChangedVO2 = (StockVO) tobeCodeChangedVO2.clone();
        codeChangedVO2.setStockCode("CODE22NEW");
        codeChangedVO2.setId(0);

        List<StockVO> newStockList = new ArrayList<StockVO>();
        newStockList.add(n_NotModifiedVO);
        newStockList.add(codeChangedVO);
        newStockList.add(codeChangedVO2);

        List<StockVO> stockList = new ArrayList<StockVO>();
        stockList.add(e_NotModifiedVO);
        stockList.add(tobeCodeChangedWithDuplicateVO);
        stockList.add(duplicateVO);
        stockList.add(tobeCodeChangedVO2);

        StockMasterBO stockMasterBO = new StockMasterBO() {
            void updateStock(StockVO stockVO) {
                updateStockList.add(stockVO);
            }

            public void removeDuplicate(StockVO duplicateStockVO, StockVO originalStockVO) {
                duplicateStockMap.put(duplicateStockVO, originalStockVO);
            }
        };

        stockMasterBO.eliminateDuplicateUpdateModified(newStockList, stockList);

        assertEquals(2, updateStockList.size());
        assertTrue(updateStockList.get(0).equals(codeChangedVO));
        assertEquals(codeChangedId, updateStockList.get(0).getId());
        assertTrue(updateStockList.get(1).equals(codeChangedVO2));
        assertEquals(codeChanged2Id, updateStockList.get(1).getId());
        assertEquals(1, duplicateStockMap.size());
        assertTrue(duplicateStockMap.keySet().contains(duplicateVO));
        assertEquals(codeChangedVO, duplicateStockMap.get(duplicateVO));
        assertEquals(codeChangedId, duplicateStockMap.get(duplicateVO).getId());
    }
}
