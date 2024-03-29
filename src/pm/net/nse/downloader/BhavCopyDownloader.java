package pm.net.nse.downloader;

import pm.net.eod.EODDownloadManager;
import pm.net.nse.BhavFileUtil;

import java.util.Date;

public class BhavCopyDownloader extends AbstractFileDownloader {

    private static String baseURL = "http://www.nseindia.com/content/historical";

    public BhavCopyDownloader(Date date, EODDownloadManager manager) {
        super(date, manager);
    }

    public String getURL() {
        return baseURL() + getRelativeURL();
    }

    protected String getRelativeURL() {
        return BhavFileUtil.getEquityURL(date);
    }

    private String baseURL() {
        return baseURL + "/" + webFolder() + "/";
    }

    protected String webFolder() {
        return "EQUITIES";
    }

    public String getFilePath() {
        return BhavFileUtil.getEquityFilePath(date);
    }

    @Override
    protected String getFileType() {
        return "BhavCopyFile";
    }
}
