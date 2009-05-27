package pm.net.eod;


import pm.net.nse.downloader.AbstractFileDownloader;
import pm.net.nse.downloader.DeliveryPositionDownloader;
import pm.util.Helper;
import pm.util.PMDate;

import java.util.Date;

public class NSEDeliveryPostTaskManager extends AbstractDateTaskManager {


    @Override
    public PMDate getLastCompletedDate() {
        return Helper.getLastDeliveryPosDate();
    }

    @Override
    protected AbstractFileDownloader getDownloader(Date date, EODDownloadManager manager) {
        return new DeliveryPositionDownloader(date, manager);
    }

}
