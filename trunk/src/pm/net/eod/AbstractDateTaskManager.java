package pm.net.eod;

import pm.net.nse.downloader.AbstractFileDownloader;
import pm.util.PMDate;

import java.util.Calendar;
import java.util.Date;

public abstract class AbstractDateTaskManager {

    public void loadDownloaders(EODDownloadManager manager) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getLastCompletedDate().getJavaDate());
        cal.add(Calendar.DATE, 1); // start from next day of lastSuccessDate
        Calendar curr = Calendar.getInstance();
        for (; !cal.after(curr); cal.add(Calendar.DATE, 1)) {
            manager.addTask(getDownloader(cal.getTime(), manager));
        }
    }

    protected abstract AbstractFileDownloader getDownloader(Date date, EODDownloadManager manager);

    abstract public PMDate getLastCompletedDate();

}
