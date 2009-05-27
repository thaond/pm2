package pm.net.nse.downloader;

import pm.net.eod.EODDownloadManager;
import pm.util.enumlist.AppConfigWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DeliveryPositionDownloader extends AbstractFileDownloader {

    private static String baseURL = "http://www.nseindia.com/archives/equities/mto/";
    private static SimpleDateFormat dateFormatddMMyyyy = new SimpleDateFormat("ddMMyyyy");

    public DeliveryPositionDownloader(Date date, EODDownloadManager manager) {
        super(date, manager);
    }

    public String getThisURL() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        String sDate = dateFormat.format(date);
        String sURL = baseURL + "MTO_" + sDate + ".DAT";
        return sURL;
    }

    @Override
    public String getThisFilePath() {
        return getFilePath(date);
    }

    public static String getFilePath(Date date) {
        String baseDir = AppConfigWrapper.bhavInputFolder.Value;
        String sDate = dateFormatddMMyyyy.format(date);
        String fileName = "MTO_" + sDate + ".DAT";
        return baseDir + "/" + fileName;
    }

    @Override
    protected String getFileType() {
        return "Delivery Position";
    }

}
