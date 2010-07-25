package pm.net.nse;

import pm.util.Helper;
import pm.util.PMDate;
import pm.util.enumlist.AppConfigWrapper;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BhavFileUtil {
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

    public static Reader openReader(String filePath) throws IOException {
        if (filePath.endsWith(".zip")) {
            unzip(filePath);
            filePath = filePath.substring(0, filePath.lastIndexOf("."));
        }
        return new FileReader(filePath);
    }

    private static void unzip(String filePath) throws IOException {
        File zipFile = new File(filePath);
        FileInputStream fin = new FileInputStream(zipFile);
        ZipInputStream zin = new ZipInputStream(fin);
        ZipEntry ze = null;
        while ((ze = zin.getNextEntry()) != null) {
            FileOutputStream fout = new FileOutputStream(new File(zipFile.getParentFile(), ze.getName()));
            for (int c = zin.read(); c != -1; c = zin.read()) {
                fout.write(c);
            }
            zin.closeEntry();
            fout.close();
        }
        zin.close();

    }

    public static Reader getFandOFile(Date date) throws IOException {
        return openReader(getFandOFilePath(date));
    }

    public static void moveFileToBackup(PMDate date, String downloadedFilePath) {
        String backupFolder = Helper.backupFolder(date);

        File bhavSourceFile = new File(downloadedFilePath);
        File bhavDestFile = new File(backupFolder + "/" + bhavSourceFile.getName());
        if (bhavSourceFile.exists()) {
            bhavSourceFile.renameTo(bhavDestFile);
        }
        if (downloadedFilePath.endsWith(".zip")) {
            new File(downloadedFilePath.substring(0, downloadedFilePath.lastIndexOf("."))).delete();
        }
    }
}
