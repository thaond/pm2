package pm.net.nse.downloader;

import junit.framework.TestCase;
import pm.net.HTTPHelper;
import pm.util.PMDate;

import java.util.List;
import java.util.Vector;

/**
 * Date: Jan 27, 2007
 * Time: 7:37:00 PM
 */
public class MarketHolidayDownloaderExternalTest extends TestCase {

    public void testRun() {
        if (!HTTPHelper.isNetworkAvailable()) return;
        final List<PMDate> marketWorkingDays = new Vector<PMDate>();
        MarketHolidayDownloader holidayDownloader = new MarketHolidayDownloader() {
            void saveData(List<PMDate> marketDays) {
                marketWorkingDays.addAll(marketDays);
            }
        };
        holidayDownloader.run();
        PMDate currYearRepublicDay = new PMDate();
        currYearRepublicDay.setDate(26);
        currYearRepublicDay.setMonth(1);
        assertFalse(marketWorkingDays.contains(currYearRepublicDay));
    }
}
