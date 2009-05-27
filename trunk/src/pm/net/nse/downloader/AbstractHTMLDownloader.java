package pm.net.nse.downloader;

import org.apache.log4j.Logger;
import pm.net.AbstractDownloader;
import pm.net.nse.AbstractStockDownloadManager;

public abstract class AbstractHTMLDownloader extends AbstractDownloader {

    protected static Logger logger = Logger.getLogger(AbstractHTMLDownloader.class);

    private AbstractStockDownloadManager manager;

    public AbstractHTMLDownloader(AbstractStockDownloadManager manager) {
        this.manager = manager;
    }

    public void alertManager() {
        manager.taskCompleted(this);
    }

    public void run() {
        if (!stop) {
            try {
                performTask();
                if (!error) completed = true;
            } catch (Exception e) {
                logger.error(e, e);
                error = true;
            }
        }
        alertManager();
    }

    abstract protected void performTask();

    public abstract String getURL();

}
