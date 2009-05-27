package pm.tools;

import pm.action.TaskManager;
import pm.net.HTTPHelper;
import pm.net.eod.EODDownloadManager;
import pm.net.nse.downloader.BhavCopyDownloader;
import pm.util.AppConfig;

import java.util.Calendar;
import java.util.Date;

/*
 * Created on Jan 13, 2004
 */

public class EODScheduler implements Runnable {

    private boolean runFlag = false;
    private int hh;
    private int mm;
    private boolean incomplete = false;
    private int noAttempts = 0;
    private boolean runOnce = true;

    /* (non-Javadoc)
      * @see java.lang.Runnable#run()
      */
    public void run() {
        loadProperties();
        System.out.println("Starting EODScheduler...");
        while (runFlag) {
            try {
                Thread.sleep(getSleepTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (HTTPHelper.isExists(BhavCopyDownloader.getURL(new Date()))) {
                new EODDownloadManager(TaskManager.getExecutor());
                noAttempts = 0;
                incomplete = false;
            } else {
                incomplete = true;
                noAttempts++;
            }
            loadProperties();
            runOnce = false;
        }
        System.out.println("EODScheduler terminated.");
    }

    private void loadProperties() {
        AppConfig.reloadProperties();
        hh = Integer.parseInt(AppConfig.EODRunHH.Value);
        mm = Integer.parseInt(AppConfig.EODRunMM.Value);
        runFlag = AppConfig.EODRunFlag.Value.equalsIgnoreCase("True");
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new EODScheduler(), "Downloader");
        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    private long getSleepTime() {
        Calendar calendar = Calendar.getInstance();
        long nowTime = calendar.getTime().getTime();
        if (!incomplete || noAttempts > 6) {
            int currhh = calendar.get(Calendar.HOUR_OF_DAY);
            int currmm = calendar.get(Calendar.MINUTE);
            if (currhh > hh || (currhh == hh && currmm >= mm)) {
                if (runOnce) {
                    return 0;
                }
                calendar.add(Calendar.DATE, 1);
            }
            calendar.set(Calendar.HOUR_OF_DAY, hh);
            calendar.set(Calendar.MINUTE, mm);
            calendar.set(Calendar.SECOND, 0);
        } else {
            calendar.add(Calendar.MINUTE, 30);
        }
        System.out.println("Next run : " + calendar.getTime());
        long newTime = calendar.getTime().getTime();
        return (newTime - nowTime);
    }
}
