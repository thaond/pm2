package pm.net.eod;

import pm.net.nse.downloader.AbstractFileDownloader;
import pm.net.nse.downloader.FandODownloader;
import pm.util.PMDate;

import java.util.Date;

public class NSEFOTaskManager extends AbstractDateTaskManager {

    @Override
    protected AbstractFileDownloader getDownloader(Date date, EODDownloadManager manager) {
        return new FandODownloader(date, manager);
    }

    @Override
    public PMDate getLastCompletedDate() {
        return new PMDate(1, 7, 2010);
    }
}
