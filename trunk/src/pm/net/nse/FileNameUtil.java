package pm.net.nse;

import pm.util.enumlist.AppConfigWrapper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FileNameUtil {
    public static SimpleDateFormat dateFormatMMM = new SimpleDateFormat("MMM");
    private static final String EQUITYPREFIX = "cm";
    private static final String FOPREFIX = "fo";

    public static boolean isDateAfterZipFileIntro(Date date) {
        return true;
//        final PMDate zipFromDate = new PMDate(1, 12, 2009);
//        return !new PMDate(date).before(zipFromDate);
    }

    public static String getEquityFilePath(Date date) {
        return constructFileName(date, EQUITYPREFIX);
    }

    public static String getFandOFilePath(Date date) {
        return constructFileName(date, FOPREFIX);
    }

    private static String constructFileName(Date date, String filePrefix) {
        StringBuilder sb = new StringBuilder(AppConfigWrapper.bhavInputFolder.Value);
        sb.append("/");
        String sMonth = dateFormatMMM.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        sMonth = sMonth.toUpperCase();
        int yy = calendar.get(Calendar.YEAR);
        int dd = calendar.get(Calendar.DATE);
        Calendar checkDate = Calendar.getInstance();
        checkDate.set(2003, 10, 30); //since once after this date 0 is appended
        sb.append(filePrefix);
        if (calendar.after(checkDate) && dd < 10) sb.append("0");
        sb.append(dd).append(sMonth).append(yy).append("bhav.csv.zip");
        return sb.toString();
    }

    public static String getEquityURL(Date date) {
        return constructURL(date, EQUITYPREFIX);
    }

    public static String getFandOURL(Date date) {
        return constructURL(date, FOPREFIX);
    }

    private static String constructURL(Date date, String fileNamePrefix) {
        String sMonth = dateFormatMMM.format(date).toUpperCase();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int yy = calendar.get(Calendar.YEAR);
        int dd = calendar.get(Calendar.DATE);
        String sDD = (dd >= 10 ? "" + dd : "0" + dd);
        String fileName = fileNamePrefix + sDD + sMonth + yy + "bhav.csv.zip";
        String sURL = yy + "/" + sMonth + "/" + fileName;
        return sURL;
    }

}
