package pm.util.taskdetail;

import pm.net.nse.downloader.MarketHolidayDownloader;
import pm.util.PMDate;

/**
 * @author Thiyagu
 * @version $Id: MarketHolidayDownload.java,v 1.1 2007/12/30 15:18:02 tpalanis Exp $
 * @since 30-Dec-2007
 */
public class MarketHolidayDownload extends DefaultTaskDetail {

    public MarketHolidayDownload() {
        super(MarketHolidayDownloader.class, 0, true);
    }

    protected PMDate nextRunDate() {
        if (getLastRunStatus()) {
            return new PMDate(1, 1, getLastRunDate().getYear() + 1);
        }
        return PMDate.today();
    }

    boolean isInSync(PMDate today) {
        return getLastRunDate().getYear() == today.getYear();
    }
}
