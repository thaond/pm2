package pm.bo;

import org.jmock.cglib.Mock;
import org.jmock.cglib.MockObjectTestCase;
import pm.dao.ibatis.dao.IDateDAO;
import pm.dao.ibatis.dao.IQuoteDAO;
import pm.util.DateIterator;
import pm.util.PMDate;
import pm.vo.EquityQuote;

import java.util.*;

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

    public void testSaveIndexQuotesToInsertNewDateAndInsertQuote() {
        PMDate date = new PMDate(2, 1, 2006);
        String indexCode = "INDEXCODE";
        EquityQuote quoteVO = new EquityQuote(indexCode, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);

        mockQuoteDAO.expects(once()).method("getQuotes").with(eq(indexCode)).will(returnValue(new Vector()));
        mockQuoteDAO.expects(once()).method("insertQuote").with(eq(quoteVO));
        final Mock mockDateDAO = new Mock(IDateDAO.class);
        mockDateDAO.expects(once()).method("insertIfNew").with(eq(date));

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

            @Override
            DateIterator getDateIterator() {
                return iterator();
            }
        };
        Vector<EquityQuote> quoteVOs = new Vector<EquityQuote>();
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
        final PMDate date = new PMDate(2, 1, 2006);
        String indexCode = "INDEXCODE";
        EquityQuote quoteVO = new EquityQuote(indexCode, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);

        mockQuoteDAO.expects(once()).method("getQuotes").with(eq(indexCode)).will(returnValue(new Vector()));
        mockQuoteDAO.expects(once()).method("insertQuote").with(eq(quoteVO));
        final Mock mockDateDAO = new Mock(IDateDAO.class);
        mockDateDAO.expects(once()).method("insertIfNew").with(eq(date));
        mockQuoteDAO.expects(once()).method("getQuote").with(eq(indexCode), eq(date.next())).will(returnValue(null));

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

            @Override
            DateIterator getDateIterator() {
                return iterator(date.previous(), date);
            }
        };
        Vector<EquityQuote> quoteVOs = new Vector<EquityQuote>();
        quoteVOs.add(quoteVO);
        quoteBO.saveIndexQuotes(indexCode, quoteVOs);
        mockQuoteDAO.verify();
        mockDateDAO.verify();
    }

    public void testSaveIndexQuotesToUpdateQuoteOnExistingQuote() {
        final PMDate date = new PMDate(2, 1, 2006);
        String stockCode4 = "STOCKCODE4";
        EquityQuote quoteVO = new EquityQuote(stockCode4, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);
        mockQuoteDAO.expects(once()).method("updateQuote").with(eq(quoteVO));
        Vector existingQuotes = new Vector();
        existingQuotes.add(quoteVO);
        mockQuoteDAO.expects(once()).method("getQuotes").withAnyArguments().will(returnValue(existingQuotes));
        mockQuoteDAO.expects(once()).method("getQuote").with(eq(stockCode4), eq(date.next())).will(returnValue(null));

        final Mock mockDateDAO = new Mock(IDateDAO.class);
        mockDateDAO.expects(once()).method("insertIfNew").with(eq(date));

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

            @Override
            DateIterator getDateIterator() {
                return iterator(date.previous(), date);
            }
        };
        Vector<EquityQuote> quoteVOs = new Vector<EquityQuote>();
        quoteVOs.add(quoteVO);
        quoteBO.saveIndexQuotes(stockCode4, quoteVOs);
        mockQuoteDAO.verify();
        mockDateDAO.verify();

    }

    private DateIterator iterator(final PMDate... dates) {
        return new DateIterator() {
            @Override
            protected List<PMDate> getDates(PMDate stDate, PMDate enDate) {
                return Arrays.asList(dates);
            }
        };
    }

    public void testSaveIndexQuotesToInsertNewQuote() {
        PMDate date = new PMDate(2, 1, 2006);
        String stockCode4 = "STOCKCODE4";
        EquityQuote quoteVO = new EquityQuote(stockCode4, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f);
        mockQuoteDAO.expects(once()).method("insertQuote").with(eq(quoteVO));
        Vector existingQuotes = new Vector();
        mockQuoteDAO.expects(once()).method("getQuotes").withAnyArguments().will(returnValue(existingQuotes));
        final Mock mockDateDAO = new Mock(IDateDAO.class);
        mockDateDAO.expects(once()).method("insertIfNew").with(eq(date));

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

            @Override
            DateIterator getDateIterator() {
                return iterator();
            }

        };
        Vector<EquityQuote> quoteVOs = new Vector<EquityQuote>();
        quoteVOs.add(quoteVO);
        quoteBO.saveIndexQuotes(stockCode4, quoteVOs);
        mockQuoteDAO.verify();
        mockDateDAO.verify();

    }


    public void testSaveQuoteToDoUpdateOnExistingDate() throws Exception {
        final PMDate date = new PMDate(2, 1, 2006);
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

            @Override
            boolean insertIfNewDate(PMDate date) {
                return false;
            }

            @Override
            DateIterator getDateIterator() {
                return iterator(date);
            }
        };
        Vector<EquityQuote> quoteVOs = new Vector<EquityQuote>();
        quoteVOs.add(new EquityQuote("STOCKCODE1", date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteVOs.add(new EquityQuote("STOCKCODE2", date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteVOs.add(new EquityQuote("STOCKCODE3", date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
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

            void insertNewStockList(List<EquityQuote> quoteVOs) {

            }

        };
        Vector<EquityQuote> quoteVOs = new Vector<EquityQuote>();
        quoteVOs.add(new EquityQuote("STOCKCODE1", date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteVOs.add(new EquityQuote("STOCKCODE2", date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteVOs.add(new EquityQuote("STOCKCODE3", date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
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
        Vector<EquityQuote> quoteVOs = new Vector<EquityQuote>();
        quoteVOs.add(new EquityQuote(stockCode3, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quoteVOs.add(new EquityQuote(stockCode4, date, 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
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
        List<EquityQuote> quotesForStk1 = new Vector<EquityQuote>();
        quotesForStk1.add(new EquityQuote(stockCode1, new PMDate(2, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quotesForStk1.add(new EquityQuote(stockCode1, new PMDate(5, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        List<EquityQuote> quotesForStk2 = new Vector<EquityQuote>();
        quotesForStk2.add(new EquityQuote(stockCode2, new PMDate(2, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quotesForStk2.add(new EquityQuote(stockCode2, new PMDate(3, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        quotesForStk2.add(new EquityQuote(stockCode2, new PMDate(4, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f));
        List<EquityQuote> quotesForStk3 = new Vector<EquityQuote>();
        final Mock mockQuoteDAO = new Mock(IQuoteDAO.class);
        mockQuoteDAO.expects(once()).method("getQuotes").with(eq(stockCode1), eq(frmDate), eq(toDate)).will(returnValue(quotesForStk1));
        mockQuoteDAO.expects(once()).method("getQuotes").with(eq(stockCode2), eq(frmDate), eq(toDate)).will(returnValue(quotesForStk2));
        mockQuoteDAO.expects(once()).method("getQuotes").with(eq(stockCode3), eq(frmDate), eq(toDate)).will(returnValue(quotesForStk3));

        QuoteBO quoteBO = new QuoteBO() {
            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }
        };
        Vector<EquityQuote[]> quotes = quoteBO.getQuotes(frmDate, toDate, stockList);
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
        EquityQuote[] quoteVOs = {new EquityQuote(stockCode1, new PMDate(1, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f),
                new EquityQuote(stockCode2, new PMDate(5, 1, 2006), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f),
                new EquityQuote(stockCode3, new PMDate(1, 12, 2005), 10f, 15f, 5f, 7.5f, 100f, 15f, 1000f, 98f)};
        mockQuoteDAO.expects(once()).method("getQuote").with(eq(stockCode1)).will(returnValue(quoteVOs[0]));
        mockQuoteDAO.expects(once()).method("getQuote").with(eq(stockCode2)).will(returnValue(quoteVOs[1]));
        mockQuoteDAO.expects(once()).method("getQuote").with(eq(stockCode3)).will(returnValue(quoteVOs[2]));

        QuoteBO quoteBO = new QuoteBO() {
            IQuoteDAO getDAO() {
                return (IQuoteDAO) mockQuoteDAO.proxy();
            }
        };
        EquityQuote[] quotes = quoteBO.getQuote(stockList);
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
        EquityQuote[] quotes = quoteBO.getQuote(stockList);
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


        HashMap<PMDate, EquityQuote> existingQuotesMap = new HashMap<PMDate, EquityQuote>();
        existingQuotesMap.put(prevDate, new EquityQuote("STOCKCODE", prevDate, 10f, 20f, 9f, 15.5f, 100f, 0f, 100f, 10f));
        EquityQuote nextDateQuote = new EquityQuote("STOCKCODE", nextDate, 10f, 20f, 9f, 15.5f, 100f, 0f, 100f, 10f);
        EquityQuote updatedNextDateQuote = new EquityQuote("STOCKCODE", nextDate, 10f, 20f, 9f, 15.5f, 100f, 15f, 100f, 10f);
        existingQuotesMap.put(nextDate, nextDateQuote);
        EquityQuote currQuote = new EquityQuote("STOCKCODE", currDate, 10f, 20f, 9f, 15f, 100f, 0f, 100f, 10f);
        mockQuoteDAO.expects(once()).method("updateQuote").with(eq(updatedNextDateQuote));
        mockQuoteDAO.expects(once()).method("getQuote").with(eq("STOCKCODE"), eq(prevDate)).will(returnValue(existingQuotesMap.get(prevDate)));
        mockQuoteDAO.expects(once()).method("getQuote").with(eq("STOCKCODE"), eq(nextDate)).will(returnValue(existingQuotesMap.get(nextDate)));

        getQuoteBO().updatePrevClose(currQuote, iterator);
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

        HashMap<PMDate, EquityQuote> existingQuotesMap = new HashMap<PMDate, EquityQuote>();
        existingQuotesMap.put(prevDate, new EquityQuote("STOCKCODE", prevDate, 10f, 20f, 9f, 15.5f, 100f, 6f, 100f, 10f));
        EquityQuote currQuote = new EquityQuote("STOCKCODE", currDate, 10f, 20f, 9f, 15f, 100f, 1000f, 100f, 10f);
        mockQuoteDAO.expects(once()).method("getQuote").with(eq("STOCKCODE"), eq(currDate.next())).will(returnValue(null));
        getQuoteBO().updatePrevClose(currQuote, iterator);
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

        PMDate currDate = new PMDate(1, 1, 2006);
        EquityQuote currQuote = new EquityQuote("STOCKCODE", currDate, 10f, 20f, 9f, 15f, 100f, 0f, 100f, 10f);
        mockQuoteDAO.expects(once()).method("getQuote").with(eq("STOCKCODE"), eq(currDate.previous())).will(returnValue(new EquityQuote("STOCKCODE", prevDate, 10f, 20f, 9f, 15.5f, 100f, 6f, 100f, 10f)));
        getQuoteBO().updatePrevClose(currQuote, iterator);
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
        EquityQuote currQuote = new EquityQuote("STOCKCODE", currDate, 10f, 20f, 9f, 15f, 100f, 0f, 100f, 10f);
        getQuoteBO().updatePrevClose(currQuote, iterator);
        assertEquals(0, 0f, currQuote.getPrevClose());
    }


}
