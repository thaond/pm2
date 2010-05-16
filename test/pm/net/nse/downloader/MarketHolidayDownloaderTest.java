package pm.net.nse.downloader;

import junit.framework.TestCase;
import pm.AppLoader;
import pm.util.ApplicationException;
import pm.util.PMDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Date: Jan 27, 2007
 * Time: 6:16:09 PM
 */
public class MarketHolidayDownloaderTest extends TestCase {

    public void testGetMarketDaysFrom_ToSkipPreviousYearRemainingDays() {
        MarketHolidayDownloader downloader = new MarketHolidayDownloader() {

            PMDate getLatestDate() {
                return new PMDate(25, 12, 2006);
            }
        };
        List<PMDate> list = downloader.getMarketDaysFrom(2007, new HashSet<PMDate>());
        assertEquals(new PMDate(1, 1, 2007), list.get(0));
    }

    public void testGetMarketDaysFrom_ToSkipHolidays() {
        MarketHolidayDownloader downloader = new MarketHolidayDownloader() {

            PMDate getLatestDate() {
                return new PMDate(31, 12, 2006);
            }
        };
        HashSet<PMDate> holidayList = new HashSet<PMDate>();
        PMDate holiday1 = new PMDate(1, 1, 2007);
        holidayList.add(holiday1);
        PMDate holiday2 = new PMDate(4, 1, 2007);
        holidayList.add(holiday2);
        List<PMDate> list = downloader.getMarketDaysFrom(2007, holidayList);
        assertEquals(new PMDate(2, 1, 2007), list.get(0));
        assertEquals(new PMDate(3, 1, 2007), list.get(1));
        assertEquals(new PMDate(5, 1, 2007), list.get(2));
        assertFalse(list.contains(holiday1));
        assertFalse(list.contains(holiday2));
    }

    public void testGetMarketDaysFrom_ToHaveFullYearDates() {
        MarketHolidayDownloader downloader = new MarketHolidayDownloader() {

            PMDate getLatestDate() {
                return new PMDate(31, 12, 2006);
            }
        };
        HashSet<PMDate> holidayList = new HashSet<PMDate>();
        holidayList.add(new PMDate(4, 1, 2007));
        List<PMDate> list = downloader.getMarketDaysFrom(2007, holidayList);
        assertFalse(list.contains(new PMDate(4, 1, 2007)));
        assertEquals(260, list.size());
        assertEquals(new PMDate(31, 12, 2007), list.get(list.size() - 1));
    }

    public void testGetMarketDaysFrom_ToStartFromRemainingDaysOfTheCurrentYear() {
        MarketHolidayDownloader downloader = new MarketHolidayDownloader() {

            PMDate getLatestDate() {
                return new PMDate(31, 1, 2007);
            }
        };
        HashSet<PMDate> holidayList = new HashSet<PMDate>();
        holidayList.add(new PMDate(4, 1, 2007));
        List<PMDate> list = downloader.getMarketDaysFrom(2007, holidayList);
        assertFalse(list.contains(new PMDate(1, 1, 2007)));
        assertFalse(list.contains(new PMDate(2, 1, 2007)));
        assertFalse(list.contains(new PMDate(31, 1, 2007)));
        assertEquals(238, list.size());
        assertEquals(new PMDate(1, 2, 2007), list.get(0));
        assertEquals(new PMDate(31, 12, 2007), list.get(list.size() - 1));
    }

    public void testGetMarketDaysFrom_ToSkipWeekends() {
        MarketHolidayDownloader downloader = new MarketHolidayDownloader() {

            PMDate getLatestDate() {
                return new PMDate(31, 12, 2006);
            }
        };
        List<PMDate> list = downloader.getMarketDaysFrom(2007, new HashSet<PMDate>());
        assertFalse(list.contains(new PMDate(6, 1, 2007)));
        assertFalse(list.contains(new PMDate(7, 1, 2007)));
    }

    public void testFindHolidays() throws IOException, ApplicationException {
        AppLoader.initConsoleLogger();
        MarketHolidayDownloader downloader = new MarketHolidayDownloader();
        Set<PMDate> list = downloader.findHolidays(new BufferedReader(new StringReader(getHolidayData())));
        assertEquals(11, list.size());
        assertTrue(list.contains(new PMDate(1, 1, 2010)));
        assertTrue(list.contains(new PMDate(26, 1, 2010)));
        assertTrue(list.contains(new PMDate(24, 3, 2010)));
        assertTrue(list.contains(new PMDate(17, 12, 2010)));
    }

    public void testGetURL() {
        MarketHolidayDownloader downloader = new MarketHolidayDownloader();
        String expectedURL = "http://www.nseindia.com/marketinfo/holiday_master/holidaysList.jsp?clgData=N&fromDt=01-01-2010&mktSeg=CM&pageType=outuser&toDt=31-12-2010";
        String url = downloader.getURL(2010);
        assertEquals(expectedURL, url);
    }

    private String getHolidayData() {
        return "NSE - Holiday master\n" +
                "Home > Press Room > Exchange Holidays\n" +
                "List of Trading Holidays from 01-01-2010 to 31-12-2010\n" +
                "Market Segment\n" +
                "Date\n" +
                "Day\n" +
                "Description\n" +
                "CM\n" +
                "01-Jan-2010\n" +
                "Friday\n" +
                "New Year\n" +
                "CM\n" +
                "26-Jan-2010\n" +
                "Tuesday\n" +
                "Republic Day\n" +
                "CM\n" +
                "12-Feb-2010\n" +
                "Friday\n" +
                "Mahashivratri\n" +
                "CM\n" +
                "01-Mar-2010\n" +
                "Monday\n" +
                "Holi\n" +
                "CM\n" +
                "24-Mar-2010\n" +
                "Wednesday\n" +
                "Ram Navmi\n" +
                "CM\n" +
                "02-Apr-2010\n" +
                "Friday\n" +
                "Good Friday\n" +
                "CM\n" +
                "14-Apr-2010\n" +
                "Wednesday\n" +
                "Dr. Ambedkar Jayanti\n" +
                "CM\n" +
                "10-Sep-2010\n" +
                "Friday\n" +
                "Ramzan ID\n" +
                "CM\n" +
                "05-Nov-2010\n" +
                "Friday\n" +
                "Laxmi Puja*\n" +
                "CM\n" +
                "17-Nov-2010\n" +
                "Wednesday\n" +
                "Bakri Id\n" +
                "CM\n" +
                "17-Dec-2010\n" +
                "Friday\n" +
                "Moharum\n" +
                "Top\n" +
                "Another search";
    }

}
