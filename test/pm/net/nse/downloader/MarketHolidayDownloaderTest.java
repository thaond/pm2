package pm.net.nse.downloader;

import junit.framework.TestCase;
import pm.AppLoader;
import pm.util.ApplicationException;
import pm.util.PMDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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
        assertEquals(13, list.size());
        assertTrue(list.contains(new PMDate(1, 1, 2007)));
        assertTrue(list.contains(new PMDate(26, 1, 2007)));
        assertTrue(list.contains(new PMDate(27, 3, 2007)));
        assertTrue(list.contains(new PMDate(25, 12, 2007)));
    }

    public void testFindCalendarYear() throws IOException {
        MarketHolidayDownloader downloader = new MarketHolidayDownloader();
        BufferedReader br = new BufferedReader(getData());
        assertEquals(2007, downloader.findCalendarYear(br));
        String nextLine = br.readLine();
        assertTrue(nextLine.startsWith(MarketHolidayDownloader.LINE_AFTER_CALENDAR_YEAR));
    }

    private Reader getData() {
        return new StringReader(getTableHeader() + getHolidayData());
    }

    private String getHolidayData() {
        return "In pursuance to clause 2 of Chapter IX of the Bye-Laws and Regulation 2.3.1 of part A Regulations of the Capital Market Segment, the Exchange hereby notifies trading holidays for the calendar year 2007 as below:\n" +
                "S No\n" +
                "Date\n" +
                "Day\n" +
                "Description\n" +
                "1\n" +
                "01-Jan-07\n" +
                "Monday\n" +
                "Bakri ID\n" +
                "2\n" +
                "26-Jan-07\n" +
                "Friday\n" +
                "Republic Day\n" +
                "3\n" +
                "30-Jan-07\n" +
                "Tuesday\n" +
                "Moharram\n" +
                "4\n" +
                "16-Feb-07\n" +
                "Friday\n" +
                "Mahashivratri\n" +
                "5\n" +
                "27-Mar-07\n" +
                "Tuesday\n" +
                "Ram Navami\n" +
                "6\n" +
                "06-Apr-07\n" +
                "Friday\n" +
                "Good Friday\n" +
                "7\n" +
                "01-May-07\n" +
                "Tuesday\n" +
                "Maharashtra Day\n" +
                "8\n" +
                "02-May-07\n" +
                "Wednesday\n" +
                "Buddha Pournima\n" +
                "9\n" +
                "15-Aug-07\n" +
                "Wednesday\n" +
                "Independence Day\n" +
                "10\n" +
                "02-Oct-07\n" +
                "Tuesday\n" +
                "Gandhi Jayanti\n" +
                "11\n" +
                "09-Nov-07\n" +
                "Friday\n" +
                "Laxmi Puja *\n" +
                "12\n" +
                "21-Dec-07\n" +
                "Friday\n" +
                "Bakri ID (falls twice in 2007)\n" +
                "13\n" +
                "25-Dec-07\n" +
                "Tuesday\n" +
                "Christmas\n" +
                "The holidays falling on Saturday / Sunday are as follows:\n" +
                "S No\n" +
                "Date\n" +
                "Day\n" +
                "Description\n" +
                "1\n" +
                "04-Mar-07\n" +
                "Sunday\n" +
                "Holi\n" +
                "2\n" +
                "31-Mar-07\n" +
                "Saturday\n" +
                "Mahavir Jayanti\n" +
                "3\n" +
                "01-Apr-07\n" +
                "Sunday\n" +
                "Id-E- Milad\n" +
                "4\n" +
                "14-Apr-07\n" +
                "Saturday\n" +
                "Ambedkar Jayanti\n" +
                "5\n" +
                "15-Sep-07\n" +
                "Saturday\n" +
                "Ganesh Chaturthi\n" +
                "6\n" +
                "14-Oct-07\n" +
                "Sunday\n" +
                "Ramzan Id\n" +
                "7\n" +
                "21-Oct-07\n" +
                "Sunday\n" +
                "Dasara\n" +
                "8\n" +
                "11-Nov-07\n" +
                "Sunday\n" +
                "Bhaubeej\n" +
                "9\n" +
                "24-Nov-07\n" +
                "Saturday\n" +
                "Guru Nanak Jayanti\n" +
                "*Muhurat Trading will be conducted. Timings of Muhurat Trading shall be notified subsequently.\n" +
                "For National Stock Exchange of India Ltd.\n" +
                "Suprabhat Lala\n" +
                "Asst. Vice President (Capital Markets)";

    }

    private String getTableHeader() {
        return "NATIONAL STOCK EXCHANGE OF INDIA LIMITED\n" +
                "NATIONAL STOCK EXCHANGE OF INDIA LIMITED\n" +
                "CAPITAL MARKET OPERATIONS\n" +
                "CIRCULAR\n" +
                "Circular No.: NSE/CMO/066/2006\n" +
                "Download No. NSE/CMTR/8182\n" +
                "Date: December 07, 2006\n" +
                "Dear Members,\n" +
                "Sub: Trading holidays for the calendar year 2007\n";
    }
}
