/*
 * Created on Nov 22, 2004
 *
 */
package pm.action;

import org.apache.log4j.Logger;
import pm.bo.AlertBO;
import pm.util.AppConfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author thiyagu1
 */
public class LimitAlert extends Thread {

    private static Logger logger = Logger.getLogger(LimitAlert.class);
    private boolean runFlag = true;
    private int openHH;
    private int openMM;
    private int closeHH;
    private int closeMM;
    private int diff;
    private boolean firstTime = true;

    public LimitAlert() {
        openHH = Integer.parseInt(AppConfig.marketOpenTimeHH.Value);
        openMM = Integer.parseInt(AppConfig.marketOpenTimeMM.Value);
        closeHH = Integer.parseInt(AppConfig.marketCloseTimeHH.Value);
        closeMM = Integer.parseInt(AppConfig.marketCloseTimeMM.Value);
        diff = Integer.parseInt(AppConfig.alertSleepTime.Value);

        closeMM -= 5; //Alert before 5 mins to close
    }

    public void run() {
        while (runFlag) {
            try {
                Thread.sleep(getSleepTime());
                new AlertBO().alert();
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }

    private long getSleepTime() {
        long sleepTime = 0;
        Calendar openTime = Calendar.getInstance();
        openTime.set(Calendar.HOUR_OF_DAY, openHH);
        openTime.set(Calendar.MINUTE, openMM);

        Calendar closeTime = Calendar.getInstance();
        closeTime.set(Calendar.HOUR_OF_DAY, closeHH);
        closeTime.set(Calendar.MINUTE, closeMM);

        Calendar currTime = Calendar.getInstance();
        Calendar newTime = Calendar.getInstance();

        if (currTime.before(openTime)) {
            newTime = (Calendar) openTime.clone();
        } else {
            if (firstTime && currTime.before(closeTime)) { //run immediate for first time
                firstTime = false;
                return 0;
            }
            newTime.add(Calendar.MINUTE, diff);
            if (newTime.after(closeTime)) {
                if (currTime.before(closeTime)) {
                    newTime = (Calendar) closeTime.clone();
                } else {
                    newTime = (Calendar) openTime.clone();
                    newTime.add(Calendar.DATE, 1);
                }
            }
        }
        sleepTime = newTime.getTimeInMillis() - currTime.getTimeInMillis();
        printDate(newTime);
        return sleepTime;
    }

    public void stopThread() {
        runFlag = false;
        this.interrupt();
    }

    private void printDate(Calendar cal) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
        logger.info("Price Alert Thread Next runTime : " + format.format(cal.getTime()));
    }

}
