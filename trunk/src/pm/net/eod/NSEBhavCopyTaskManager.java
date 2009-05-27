package pm.net.eod;


import pm.net.nse.downloader.AbstractFileDownloader;
import pm.net.nse.downloader.BhavCopyDownloader;
import pm.util.Helper;
import pm.util.PMDate;

import java.util.Date;

public class NSEBhavCopyTaskManager extends AbstractDateTaskManager {


    @Override
    public PMDate getLastCompletedDate() {
        return Helper.getLastBhavCopyDate();
    }

    @Override
    protected AbstractFileDownloader getDownloader(Date date, EODDownloadManager manager) {
        return new BhavCopyDownloader(date, manager);
    }

}
