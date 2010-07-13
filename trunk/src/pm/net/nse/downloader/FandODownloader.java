package pm.net.nse.downloader;

import pm.net.eod.EODDownloadManager;
import pm.net.nse.FileNameUtil;

import java.util.Date;

public class FandODownloader extends BhavCopyDownloader {

    public FandODownloader(Date date, EODDownloadManager manager) {
        super(date, manager);
    }

    @Override
    protected String getRelativeURL() {
        return FileNameUtil.getFandOURL(date);
    }

    @Override
    protected String webFolder() {
        return "DERIVATIVES";
    }

    @Override
    protected String getFileType() {
        return "F&O";
    }

    @Override
    public String getFilePath() {
        return FileNameUtil.getFandOFilePath(date);
    }
}
