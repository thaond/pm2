package pm.net.nse.downloader;

import pm.net.eod.EODDownloadManager;
import pm.util.enumlist.AppConfigWrapper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BhavCopyDownloader extends AbstractFileDownloader {

    public static String baseURL = "http://www.nseindia.com/content/historical/EQUITIES/";
    private static SimpleDateFormat dateFormatMMM = new SimpleDateFormat("MMM");

    public BhavCopyDownloader(Date date, EODDownloadManager manager) {
        super(date, manager);
    }

    protected String getThisURL() {
        return getURL(date);
    }

    public static String getURL(Date date) {
        String sMonth = dateFormatMMM.format(date).toUpperCase();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int yy = calendar.get(Calendar.YEAR);
        int dd = calendar.get(Calendar.DATE);
        String sDD = (dd >= 10 ? "" + dd : "0" + dd);
        String fileName = "cm" + sDD + sMonth + yy + "bhav.csv";
        String sURL = baseURL + yy + "/" + sMonth + "/" + fileName;
        return sURL;
    }

    public String getThisFilePath() {
        return getFilePath(date);
    }

    public static String getFilePath(Date date) {
        StringBuffer sb = new StringBuffer(AppConfigWrapper.bhavInputFolder.Value);
        sb.append("/");
        String sMonth = dateFormatMMM.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        sMonth = sMonth.toUpperCase();
        int yy = calendar.get(Calendar.YEAR);
        int dd = calendar.get(Calendar.DATE);
        Calendar checkDate = Calendar.getInstance();
        checkDate.set(2003, 10, 30); //since once after this date 0 is appended
        sb.append("cm");
        if (calendar.after(checkDate) && dd < 10) sb.append("0");
        sb.append(dd).append(sMonth).append(yy).append("bhav.csv");
        return sb.toString();
    }

    @Override
    protected String getFileType() {
        return "BhavCopyFile";
    }
}
