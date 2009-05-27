package pm.dao.ibatis.dao;

import pm.util.PMDate;

import java.util.List;

/**
 * DateDAO Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>08/09/2006</pre>
 */
public class DateDAOTest extends PMDBTestCase {
    public DateDAOTest(String name) {
        super(name, "EmptyData.xml");
    }

    public void testGetDateFromDateToReturnNullIfNoDateAvailable() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        assertNull(dateDAO.getDate(new PMDate(1, 1, 2001), 10));
        assertNull(dateDAO.getDate(new PMDate(1, 1, 2001), -10));
    }

    public void testInsertIfNewDate() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        assertTrue(dateDAO.insertIfNew(new PMDate(1, 1, 2001)));
        assertFalse(dateDAO.insertIfNew(new PMDate(1, 1, 2001)));
    }

    public void testGetStEnDates_RUNTHISFIRST() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        PMDate[] stEnDates = dateDAO.getStEnDates(10);
        assertEquals(2, stEnDates.length);
        assertNull(stEnDates[0]);
        assertNull(stEnDates[1]);
        PMDate date1 = new PMDate(1, 1, 2000);
        dateDAO.insertDate(date1);
        stEnDates = dateDAO.getStEnDates(10);
        assertEquals(2, stEnDates.length);
        assertEquals(date1, stEnDates[0]);
        assertEquals(date1, stEnDates[1]);

        PMDate date2 = new PMDate(2, 1, 2000);
        dateDAO.insertDate(date2);
        stEnDates = dateDAO.getStEnDates(10);
        assertEquals(2, stEnDates.length);
        assertEquals(date2, stEnDates[0]);
        assertEquals(date1, stEnDates[1]);

        PMDate date3 = new PMDate(3, 1, 2000);
        PMDate date4 = new PMDate(4, 1, 2000);
        PMDate date5 = new PMDate(7, 1, 2000);
        dateDAO.insertDate(date3);
        dateDAO.insertDate(date4);
        dateDAO.insertDate(date5);
        stEnDates = dateDAO.getStEnDates(10);
        assertEquals(2, stEnDates.length);
        assertEquals(date5, stEnDates[0]);
        assertEquals(date1, stEnDates[1]);

        stEnDates = dateDAO.getStEnDates(3);
        assertEquals(2, stEnDates.length);
        assertEquals(date5, stEnDates[0]);
        assertEquals(date3, stEnDates[1]);
    }

    public void testGetDateFromDate() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        PMDate date1 = new PMDate(1, 1, 2105);
        dateDAO.insertDate(date1);
        assertEquals(date1, dateDAO.getDate(new PMDate(1, 1, 2105), 10));
        assertEquals(date1, dateDAO.getDate(new PMDate(1, 1, 2105), 0));
        assertEquals(date1, dateDAO.getDate(new PMDate(1, 1, 2105), -10));
        PMDate date2 = new PMDate(2, 1, 2105);
        dateDAO.insertDate(date2);
        assertEquals(date2, dateDAO.getDate(new PMDate(1, 1, 2105), 10));
        assertEquals(date1, dateDAO.getDate(new PMDate(1, 1, 2105), -10));
        assertEquals(date1, dateDAO.getDate(new PMDate(2, 1, 2105), -10));
        PMDate date3 = new PMDate(3, 1, 2105);
        dateDAO.insertDate(date3);
        dateDAO.insertDate(new PMDate(4, 1, 2105));
        PMDate date5 = new PMDate(5, 1, 2105);
        dateDAO.insertDate(date5);
        dateDAO.insertDate(new PMDate(6, 1, 2105));
        PMDate date7 = new PMDate(7, 1, 2105);
        dateDAO.insertDate(date7);
        PMDate date8 = new PMDate(8, 1, 2105);
        dateDAO.insertDate(date8);
        assertEquals(date3, dateDAO.getDate(date5, -2));
        assertEquals(date7, dateDAO.getDate(date5, 2));
        assertEquals(date1, dateDAO.getDate(date5, -12));
        assertEquals(date8, dateDAO.getDate(date5, 12));
    }

    public void testGetDates() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        List<PMDate> dates = dateDAO.getDates(new PMDate(1, 1, 2106), new PMDate(1, 1, 2016));
        assertTrue(dates.isEmpty());
        PMDate date1 = new PMDate(1, 1, 2107);
        dateDAO.insertDate(date1);
        dates = dateDAO.getDates(new PMDate(1, 1, 2106), new PMDate(1, 1, 2108));
        assertEquals(1, dates.size());
        assertEquals(date1, dates.get(0));

        dateDAO.insertDate(new PMDate(2, 1, 2107));
        PMDate date3 = new PMDate(3, 1, 2107);
        dateDAO.insertDate(date3);
        dateDAO.insertDate(new PMDate(4, 1, 2107));
        dateDAO.insertDate(new PMDate(5, 1, 2107));
        PMDate date6 = new PMDate(6, 1, 2107);
        dateDAO.insertDate(date6);

        dates = dateDAO.getDates(new PMDate(1, 1, 2106), new PMDate(1, 1, 2108));
        assertEquals(6, dates.size());
        assertEquals(date1, dates.get(0));
        assertEquals(date6, dates.get(5));

        dates = dateDAO.getDates(null, new PMDate(1, 1, 2108));
        assertTrue(dates.contains(date1));
        assertTrue(dates.contains(date6));

        dates = dateDAO.getDates(null, new PMDate(3, 1, 2107));
        assertTrue(dates.contains(date1));
        assertFalse(dates.contains(date6));

        dates = dateDAO.getDates(date1, date6);
        assertEquals(6, dates.size());
        assertEquals(date1, dates.get(0));
        assertEquals(date6, dates.get(5));

        dates = dateDAO.getDates(date3, date6);
        assertEquals(4, dates.size());
        assertEquals(date3, dates.get(0));
        assertEquals(date6, dates.get(3));

    }

    public void testGetDatesForEndDateNullToReturnUptoCurrentDatesOnly() {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        PMDate currDate = new PMDate();
        PMDate previousDate = currDate.previous();
        PMDate nextDate = currDate.next();
        PMDate nextNextDate = nextDate.next();
        dateDAO.insertDate(currDate);
        dateDAO.insertDate(previousDate);
        dateDAO.insertDate(nextDate);
        dateDAO.insertDate(nextNextDate);
        List<PMDate> dates;
        dates = dateDAO.getDates(null, null);
        assertTrue(dates.contains(currDate));
        assertTrue(dates.contains(previousDate));
        assertFalse(dates.contains(nextDate));
        assertFalse(dates.contains(nextNextDate));
    }


    public void testGetDate() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        PMDate date = new PMDate(1, 1, 2006);
        dateDAO.insertDate(date);
        assertEquals(date, dateDAO.getDate(20060101));
        assertNull(dateDAO.getDate(19000101));
    }

    public void testGetLastDate() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        PMDate date1 = new PMDate(1, 1, 2006);
        PMDate date2 = new PMDate(1, 5, 2006);
        PMDate date3 = new PMDate(2, 1, 2020);
        PMDate date4 = new PMDate(2, 1, 2006);
        dateDAO.insertDate(date1);
        dateDAO.insertDate(date2);
        dateDAO.insertDate(date3);
        dateDAO.insertDate(date4);
        assertEquals(date3, dateDAO.getLastDate());
    }

    public void testGetLastQuoteDate() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        PMDate date1 = new PMDate(1, 1, 2006);
        PMDate date2 = new PMDate(1, 5, 2006);
        PMDate date3 = new PMDate(2, 1, 2006);
        dateDAO.insertDate(date1);
        dateDAO.insertDate(date2);
        dateDAO.insertDate(date3);
        dateDAO.setNSEQuoteStatusFor(date1);
        dateDAO.setNSEQuoteStatusFor(date3);
        assertEquals(date3, dateDAO.getLastQuoteDate());
    }

    public void testInsertDate() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        PMDate date = new PMDate(1, 1, 2000);
        dateDAO.insertDate(date);
        PMDate actualDate = dateDAO.getDate(20000101);
        assertEquals(date, actualDate);
    }

    public void testNSEQuoteStatus() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        PMDate date = new PMDate(1, 1, 1999);
        dateDAO.insertDate(date);
        assertFalse(dateDAO.getNSEQuoteStatusFor(date));
        dateDAO.setNSEQuoteStatusFor(date);
        assertTrue(dateDAO.getNSEQuoteStatusFor(date));
    }

    public void testGetLastWorkingDay() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        PMDate date1 = new PMDate(1, 1, 2006);
        PMDate date2 = new PMDate(1, 5, 2006);
        PMDate date3 = new PMDate(2, 1, 2006);
        dateDAO.insertDate(date1);
        dateDAO.insertDate(date2);
        dateDAO.insertDate(date3);
        assertEquals(date1, dateDAO.lastWorkingDayLatestOf(date1));
        assertEquals(date3, dateDAO.lastWorkingDayLatestOf(date3));
        assertEquals(date2, dateDAO.lastWorkingDayLatestOf(new PMDate(2, 5, 2006)));
    }

    public void testFirstWorkingDayFrom() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        PMDate date1 = new PMDate(1, 1, 2006);
        PMDate date2 = new PMDate(1, 5, 2006);
        PMDate date3 = new PMDate(2, 2, 2006);
        dateDAO.insertDate(date1);
        dateDAO.insertDate(date2);
        dateDAO.insertDate(date3);
        assertEquals(date1, dateDAO.firstWorkingDateFrom(date1));
        assertEquals(date3, dateDAO.firstWorkingDateFrom(date3));
        assertEquals(date3, dateDAO.firstWorkingDateFrom(new PMDate(2, 1, 2006)));
        assertEquals(date2, dateDAO.firstWorkingDateFrom(new PMDate(1, 4, 2006)));
        assertNull(dateDAO.firstWorkingDateFrom(new PMDate(2, 5, 2006)));
    }

    public void testIsWorkingDay() throws Exception {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        PMDate date = new PMDate(1, 1, 2006);
        dateDAO.insertDate(date);
        assertTrue(dateDAO.isWorkingDay(date));
        assertFalse(dateDAO.isWorkingDay(new PMDate(1, 1, 1990)));
    }

    public void testNextQuoteDate() {
        IDateDAO dateDAO = DAOManager.getDateDAO();
        assertEquals(new PMDate(), dateDAO.nextQuoteDate());
        PMDate date1 = new PMDate(1, 1, 2006);
        dateDAO.insertDate(date1);
        assertEquals(date1, dateDAO.nextQuoteDate());
        dateDAO.setNSEQuoteStatusFor(date1);
        assertEquals(new PMDate(), dateDAO.nextQuoteDate());
        dateDAO.insertDate(new PMDate(4, 1, 2006));
        PMDate date2 = new PMDate(2, 1, 2006);
        dateDAO.insertDate(date2);
        dateDAO.insertDate(new PMDate(3, 1, 2006));
        assertEquals(date2, dateDAO.nextQuoteDate());

    }
}
