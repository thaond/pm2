package pm.bo;

import org.jmock.cglib.Mock;
import org.jmock.cglib.MockObjectTestCase;
import pm.dao.ibatis.dao.IDateDAO;
import pm.dao.ibatis.dao.IQuoteDAO;
import pm.util.DateIterator;
import pm.util.PMDate;
import pm.vo.QuoteVO;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

/**
 * QuoteBO Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>08/09/2006</pre>
 */
public class QuoteBOTest extends MockObjectTestCase {
    private Mock mockQuoteDAO = new Mock(IQuoteDAO.class);

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    //TODO ***MOCKOBJECT TESTCASE SHOULD NOT DEPEND ON DB***
    public void testSaveIndexQuotesToInsertNewDateAndInsertQuote() {
        PMDate date = new PMDate(2, 1, 2006);
        String indexCode = "INDEXCODE";
        QuoteVO quoteVO = new QuoteVO(indexCode, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);

        mockQuoteDAO.expects(once()).method("getQuotes").with(eq(indexCode)).will(returnValue(new Vector()));
        mockQuoteDAO.expects(once()).method("insertQuote").with(eq(quoteVO));
        final Mock mockDateDAO = new Mock(IDateDAO.class);
        mockDateDAO.expects(once()).method("insertIfNew").with(eq(date));

        HashSet stockCodes = new HashSet();
        stockCodes.add(indexCode);

        QuoteBO quoteBO = new QuoteBO() {
            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }

            protected boolean isNewDate(PMDate date) {
                return true;
            }

            IDateDAO getDateDAO() {
                return (IDateDAO) mockDateDAO.proxy();
            }

        };
        Vector<QuoteVO> quoteVOs = new Vector<QuoteVO>();
        quoteVOs.add(quoteVO);
        quoteBO.saveIndexQuotes(indexCode, quoteVOs);
        mockQuoteDAO.verify();
        mockDateDAO.verify();

    }

    public void testIsNewQuote() {
        PMDate pmDate = new PMDate();
        final Mock mockDateDAO = new Mock(IDateDAO.class);
        mockDateDAO.expects(once()).method("getNSEQuoteStatusFor").with(eq(pmDate)).will(returnValue(false));

        QuoteBO quoteBO = new QuoteBO() {
            IDateDAO getDateDAO() {
                return (IDateDAO) mockDateDAO.proxy();
            }

        };
        assertTrue(quoteBO.isNewQuote(pmDate));
        mockDateDAO.verify();

    }

    public void testSaveIndexQuotesToInsertQuoteForExistingDate() {
        PMDate date = new PMDate(2, 1, 2006);
        String indexCode = "INDEXCODE";
        QuoteVO quoteVO = new QuoteVO(indexCode, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);

        mockQuoteDAO.expects(once()).method("getQuotes").with(eq(indexCode)).will(returnValue(new Vector()));
        mockQuoteDAO.expects(once()).method("insertQuote").with(eq(quoteVO));
        final Mock mockDateDAO = new Mock(IDateDAO.class);
        mockDateDAO.expects(once()).method("insertIfNew").with(eq(date));

        HashSet stockCodes = new HashSet();
        stockCodes.add(indexCode);

        QuoteBO quoteBO = new QuoteBO() {
            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }

            protected boolean isNewDate(PMDate date) {
                return false;
            }

            IDateDAO getDateDAO() {
                return (IDateDAO) mockDateDAO.proxy();
            }

        };
        Vector<QuoteVO> quoteVOs = new Vector<QuoteVO>();
        quoteVOs.add(quoteVO);
        quoteBO.saveIndexQuotes(indexCode, quoteVOs);
        mockQuoteDAO.verify();
        mockDateDAO.verify();
    }

    public void testSaveIndexQuotesToUpdateQuoteOnExistingQuote() {
        PMDate date = new PMDate(2, 1, 2006);
        String stockCode4 = "STOCKCODE4";
        QuoteVO quoteVO = new QuoteVO(stockCode4, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);
        mockQuoteDAO.expects(once()).method("updateQuote").with(eq(quoteVO));
        Vector existingQuotes = new Vector();
        existingQuotes.add(quoteVO);
        mockQuoteDAO.expects(once()).method("getQuotes").withAnyArguments().will(returnValue(existingQuotes));
        final Mock mockDateDAO = new Mock(IDateDAO.class);
        mockDateDAO.expects(once()).method("insertIfNew").with(eq(date));

        HashSet stockCodes = new HashSet();
        stockCodes.add(stockCode4);

        QuoteBO quoteBO = new QuoteBO() {
            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }

            protected boolean isNewDate(PMDate date) {
                return false;
            }

            IDateDAO getDateDAO() {
                return (IDateDAO) mockDateDAO.proxy();
            }

        };
        Vector<QuoteVO> quoteVOs = new Vector<QuoteVO>();
        quoteVOs.add(quoteVO);
        quoteBO.saveIndexQuotes(stockCode4, quoteVOs);
        mockQuoteDAO.verify();
        mockDateDAO.verify();

    }

    public void testSaveIndexQuotesToInsertNewQuote() {
        PMDate date = new PMDate(2, 1, 2006);
        String stockCode4 = "STOCKCODE4";
        QuoteVO quoteVO = new QuoteVO(stockCode4, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);
        mockQuoteDAO.expects(once()).method("insertQuote").with(eq(quoteVO));
        Vector existingQuotes = new Vector();
        mockQuoteDAO.expects(once()).method("getQuotes").withAnyArguments().will(returnValue(existingQuotes));
        final Mock mockDateDAO = new Mock(IDateDAO.class);
        mockDateDAO.expects(once()).method("insertIfNew").with(eq(date));

        HashSet stockCodes = new HashSet();
        stockCodes.add(stockCode4);

        QuoteBO quoteBO = new QuoteBO() {
            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }

            protected boolean isNewDate(PMDate date) {
                return false;
            }

            IDateDAO getDateDAO() {
                return (IDateDAO) mockDateDAO.proxy();
            }

        };
        Vector<QuoteVO> quoteVOs = new Vector<QuoteVO>();
        quoteVOs.add(quoteVO);
        quoteBO.saveIndexQuotes(stockCode4, quoteVOs);
        mockQuoteDAO.verify();
        mockDateDAO.verify();

    }


    public void testSaveQuoteToDoUpdateOnExistingDate() throws Exception {
        mockQuoteDAO.expects(once()).method("updateQuotes").withAnyArguments();
        QuoteBO quoteBO = new QuoteBO() {
            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }

            protected boolean isNewDate(PMDate date) {
                return false;
            }

            boolean isNewQuote(PMDate date) {
                return false;
            }

        };
        Vector<QuoteVO> quoteVOs = new Vector<QuoteVO>();
        PMDate date = new PMDate(2, 1, 2006);
        quoteVOs.add(new QuoteVO("STOCKCODE1", date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteVOs.add(new QuoteVO("STOCKCODE2", date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteVOs.add(new QuoteVO("STOCKCODE3", date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteBO.saveNseQuotes(date, quoteVOs);
        mockQuoteDAO.verify();

    }

    public void testSaveQuoteToDoInsertOnNewDate() throws Exception {
        PMDate date = new PMDate(2, 1, 2006);
        mockQuoteDAO.expects(once()).method("insertQuotes").withAnyArguments();

        final Mock mockDateDAO = new Mock(IDateDAO.class);
        mockDateDAO.expects(once()).method("insertIfNew").with(eq(date));
        mockDateDAO.expects(once()).method("getNSEQuoteStatusFor").with(eq(date)).will(returnValue(false));
        mockDateDAO.expects(once()).method("setNSEQuoteStatusFor").with(eq(date));
        QuoteBO quoteBO = new QuoteBO() {
            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }

            protected boolean isNewDate(PMDate date) {
                return true;
            }

            IDateDAO getDateDAO() {
                return (IDateDAO) mockDateDAO.proxy();
            }

            void insertNewStockList(List<QuoteVO> quoteVOs) {

            }

        };
        Vector<QuoteVO> quoteVOs = new Vector<QuoteVO>();
        quoteVOs.add(new QuoteVO("STOCKCODE1", date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteVOs.add(new QuoteVO("STOCKCODE2", date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteVOs.add(new QuoteVO("STOCKCODE3", date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteBO.saveNseQuotes(date, quoteVOs);
        mockQuoteDAO.verify();

    }

    public void testSaveQuoteToDoInsertNewStockQuotes() throws Exception {
        PMDate date = new PMDate(2, 1, 2006);
        mockQuoteDAO.expects(once()).method("insertQuotes").withAnyArguments();
        final Mock mockDateDAO = new Mock(IDateDAO.class);
        mockDateDAO.expects(once()).method("insertIfNew").with(eq(date));
        mockDateDAO.expects(once()).method("getNSEQuoteStatusFor").with(eq(date)).will(returnValue(false));

        HashSet stockCodes = new HashSet();
        String stockCode3 = "STOCKCODE3";
        String stockCode4 = "STOCKCODE4";
        stockCodes.add(stockCode3);
        stockCodes.add(stockCode4);
        final Mock mockStockMasterBO = new Mock(StockMasterBO.class);
        mockStockMasterBO.expects(once()).method("insertMissingStockCodes").with(eq(stockCodes));
        mockDateDAO.expects(once()).method("setNSEQuoteStatusFor").with(eq(new PMDate(2, 1, 2006)));

        QuoteBO quoteBO = new QuoteBO() {
            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }

            protected boolean isNewDate(PMDate date) {
                return true;
            }

            StockMasterBO getStockMasterBO() {
                return (StockMasterBO) mockStockMasterBO.proxy();
            }

            IDateDAO getDateDAO() {
                return (IDateDAO) mockDateDAO.proxy();
            }

        };
        Vector<QuoteVO> quoteVOs = new Vector<QuoteVO>();
        quoteVOs.add(new QuoteVO(stockCode3, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteVOs.add(new QuoteVO(stockCode4, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteBO.saveNseQuotes(date, quoteVOs);
        mockQuoteDAO.verify();
        mockStockMasterBO.verify();
        mockDateDAO.verify();

    }

    public void testGetQuotesForStocklistForDateRange() {
        PMDate frmDate = new PMDate(1, 1, 2006);
        PMDate toDate = new PMDate(5, 1, 2006);
        String stockCode1 = "CODE1";
        String stockCode2 = "CODE7";
        String stockCode3 = "CODE5";
        String[] stockList = {stockCode1, stockCode2, stockCode3};
        List<QuoteVO> quotesForStk1 = new Vector<QuoteVO>();
        quotesForStk1.add(new QuoteVO(stockCode1, new PMDate(2, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quotesForStk1.add(new QuoteVO(stockCode1, new PMDate(5, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        List<QuoteVO> quotesForStk2 = new Vector<QuoteVO>();
        quotesForStk2.add(new QuoteVO(stockCode2, new PMDate(2, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quotesForStk2.add(new QuoteVO(stockCode2, new PMDate(3, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quotesForStk2.add(new QuoteVO(stockCode2, new PMDate(4, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        List<QuoteVO> quotesForStk3 = new Vector<QuoteVO>();
        final Mock mockQuoteDAO = new Mock(IQuoteDAO.class);
        mockQuoteDAO.expects(once()).method("getQuotes").with(eq(stockCode1), eq(frmDate), eq(toDate)).will(returnValue(quotesForStk1));
        mockQuoteDAO.expects(once()).method("getQuotes").with(eq(stockCode2), eq(frmDate), eq(toDate)).will(returnValue(quotesForStk2));
        mockQuoteDAO.expects(once()).method("getQuotes").with(eq(stockCode3), eq(frmDate), eq(toDate)).will(returnValue(quotesForStk3));

        QuoteBO quoteBO = new QuoteBO() {
            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }
        };
        Vector<QuoteVO[]> quotes = quoteBO.getQuotes(frmDate, toDate, stockList);
        assertEquals(3, quotes.size());
        assertEquals(2, quotes.get(0).length);
        assertEquals(stockCode1, quotes.get(0)[0].getStockCode());
        assertEquals(3, quotes.get(1).length);
        assertEquals(stockCode2, quotes.get(1)[2].getStockCode());
        assertEquals(0, quotes.get(2).length);
        mockQuoteDAO.verify();

    }

    public void testGetQuoteForStocklist() {
        String stockCode1 = "CODE1";
        String stockCode2 = "CODE7";
        String stockCode3 = "CODE5";
        String[] stockList = {stockCode1, stockCode2, stockCode3};
        final Mock mockQuoteDAO = new Mock(IQuoteDAO.class);
        QuoteVO[] quoteVOs = {new QuoteVO(stockCode1, new PMDate(1, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f),
                new QuoteVO(stockCode2, new PMDate(5, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f),
                new QuoteVO(stockCode3, new PMDate(1, 12, 2005), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f)};
        mockQuoteDAO.expects(once()).method("getQuote").with(eq(stockCode1)).will(returnValue(quoteVOs[0]));
        mockQuoteDAO.expects(once()).method("getQuote").with(eq(stockCode2)).will(returnValue(quoteVOs[1]));
        mockQuoteDAO.expects(once()).method("getQuote").with(eq(stockCode3)).will(returnValue(quoteVOs[2]));

        QuoteBO quoteBO = new QuoteBO() {
            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }
        };
        QuoteVO[] quotes = quoteBO.getQuote(stockList);
        for (int i = 0; i < quotes.length; i++) {
            assertEquals(quoteVOs[i], quotes[i]);
        }

        mockQuoteDAO.verify();

    }

    public void testGetQuoteForStocklistForHandlingNullQuote() {
        String stockCode1 = "CODE1";
        String[] stockList = {stockCode1};
        final Mock mockQuoteDAO = new Mock(IQuoteDAO.class);
        mockQuoteDAO.expects(once()).method("getQuote").with(eq(stockCode1)).will(returnValue(null));

        QuoteBO quoteBO = new QuoteBO() {
            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }
        };
        QuoteVO[] quotes = quoteBO.getQuote(stockList);
        assertNotNull(quotes[0]);
        mockQuoteDAO.verify();

    }

    public void testUpdatePrevCloseForCurrentAndNextDate() {

        final PMDate prevDate = new PMDate(1, 1, 2005);
        final PMDate currDate = new PMDate(2, 1, 2005);
        final PMDate nextDate = new PMDate(3, 1, 2005);

        DateIterator iterator = new DateIterator() {

            protected List<PMDate> getDates(PMDate stDate, PMDate enDate) {
                Vector<PMDate> dates = new Vector<PMDate>();
                dates.add(prevDate);
                dates.add(currDate);
                dates.add(nextDate);
                return dates;
            }
        };


        HashMap<PMDate, QuoteVO> existingQuotesMap = new HashMap<PMDate, QuoteVO>();
        existingQuotesMap.put(prevDate, new QuoteVO("STOCKCODE", prevDate, 10f, 20f, 9f, 15.5f, 100f, 0f, 100f, 10f));
        QuoteVO nextDateQuote = new QuoteVO("STOCKCODE", nextDate, 10f, 20f, 9f, 15.5f, 100f, 0f, 100f, 10f);
        QuoteVO updatedNextDateQuote = new QuoteVO("STOCKCODE", nextDate, 10f, 20f, 9f, 15.5f, 100f, 15f, 100f, 10f);
        existingQuotesMap.put(nextDate, nextDateQuote);
        QuoteVO currQuote = new QuoteVO("STOCKCODE", currDate, 10f, 20f, 9f, 15f, 100f, 0f, 100f, 10f);
        mockQuoteDAO.expects(once()).method("updateQuote").with(eq(updatedNextDateQuote));
        getQuoteBO().updatePrevClose(currQuote, existingQuotesMap, iterator);
        assertEquals(15.5f, currQuote.getPrevClose());
        assertEquals(updatedNextDateQuote, nextDateQuote);
        mockQuoteDAO.verify();

    }

    public void testUpdatePrevCloseToUpdateOnlyOnPrevQuoteNotSet() {

        final PMDate prevDate = new PMDate(1, 1, 2005);
        final PMDate currDate = new PMDate(2, 1, 2005);

        DateIterator iterator = new DateIterator() {

            protected List<PMDate> getDates(PMDate stDate, PMDate enDate) {
                Vector<PMDate> dates = new Vector<PMDate>();
                dates.add(prevDate);
                dates.add(currDate);
                return dates;
            }
        };

        HashMap<PMDate, QuoteVO> existingQuotesMap = new HashMap<PMDate, QuoteVO>();
        existingQuotesMap.put(prevDate, new QuoteVO("STOCKCODE", prevDate, 10f, 20f, 9f, 15.5f, 100f, 6f, 100f, 10f));
        QuoteVO currQuote = new QuoteVO("STOCKCODE", currDate, 10f, 20f, 9f, 15f, 100f, 1000f, 100f, 10f);
        getQuoteBO().updatePrevClose(currQuote, existingQuotesMap, iterator);
        assertEquals(1000f, currQuote.getPrevClose());
    }

    public void testUpdatePrevCloseForBegningOfNewYear() {

        final PMDate prevDate = new PMDate(31, 12, 2005);

        DateIterator iterator = new DateIterator() {

            protected List<PMDate> getDates(PMDate stDate, PMDate enDate) {
                Vector<PMDate> dates = new Vector<PMDate>();
                dates.add(prevDate);
                return dates;
            }
        };

        HashMap<PMDate, QuoteVO> existingQuotesMap = new HashMap<PMDate, QuoteVO>();
        existingQuotesMap.put(prevDate, new QuoteVO("STOCKCODE", prevDate, 10f, 20f, 9f, 15.5f, 100f, 6f, 100f, 10f));
        PMDate currDate = new PMDate(1, 1, 2006);
        QuoteVO currQuote = new QuoteVO("STOCKCODE", currDate, 10f, 20f, 9f, 15f, 100f, 0f, 100f, 10f);
        getQuoteBO().updatePrevClose(currQuote, existingQuotesMap, iterator);
        assertEquals(15.5f, currQuote.getPrevClose());
    }

    private QuoteBO getQuoteBO() {
        return new QuoteBO() {

            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }
        };
    }

    public void testUpdatePrevCloseForFirstDateInSystem() {

        DateIterator iterator = new DateIterator() {

            protected List<PMDate> getDates(PMDate stDate, PMDate enDate) {
                return new Vector<PMDate>();
            }
        };

        PMDate currDate = new PMDate(1, 1, 2006);
        QuoteVO currQuote = new QuoteVO("STOCKCODE", currDate, 10f, 20f, 9f, 15f, 100f, 0f, 100f, 10f);
        getQuoteBO().updatePrevClose(currQuote, new HashMap<PMDate, QuoteVO>(), iterator);
        assertEquals(0, 0f, currQuote.getPrevClose());
    }


}
