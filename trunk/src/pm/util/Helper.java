/*
 * Created on Oct 13, 2004
 *
 */
package pm.util;

import org.apache.log4j.Logger;
import pm.bo.StockMasterBO;
import pm.util.AppConst.TRADINGTYPE;
import pm.vo.StockVO;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class Helper {

    private static Logger logger = Logger.getLogger(Helper.class);

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public static String _DELIMITER = ",";
    private static NumberFormat numberFormat;

    static {
        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
    }

    public static Vector<String> getStockMasterList() {
        return getStockList(false);
    }

    private static Vector<String> getStockList(boolean incIndex) {
        //TODO remove Stock at string
        Vector<StockVO> stockList = new StockMasterBO().getStockList(incIndex);
        Vector<String> masterList = new Vector<String>();
        for (StockVO stockVO : stockList) {
            masterList.add(stockVO.getStockCode());
        }
        return masterList;
    }

    public static Vector<String> getStockListIncIndex() {
        return getStockList(true);
    }

    public static float calculateBrokerage(PMDate date, float qty, float price) {
        float totBrokerage = 0;
        if (date.before(new PMDate(1, 10, 2004))) { //Brokerage 0.0085
            totBrokerage = qty * price * 0.0085f;
        } else if (date.before(new PMDate(1, 4, 2005))) {    //Brokerage 0.009015
            totBrokerage = qty * price * 0.009015f;
        } else { //.75 brokerage + 10.2% ST on brokerage + .1% STT
            float value = qty * price;
            float brokerage = value * 0.0075f;
            if (brokerage < 25) {
                brokerage = 25;
            }
            totBrokerage = brokerage * 1.102f + value * 0.001f;

        }
        if (totBrokerage < 25) {
            totBrokerage = 25.0f;
        }

        return totBrokerage;
    }

    public static float calculateBrokerageDay(TRADINGTYPE action, PMDate date, float qty,
                                              float price) {
        float brokerage = 0;
        if (date.before(new PMDate(1, 10, 2004))) { //Brokerage 0.0015
            brokerage = qty * price * 0.0015f;
        } else if (date.before(new PMDate(1, 4, 2005))) {
            float value = qty * price;
            if (action == TRADINGTYPE.Buy) {
                brokerage = value * 0.001f * 1.102f;
            } else {
                brokerage = value * 0.001f * 1.102f + value * 0.00015f;
            }
//			if (action == TRADINGTYPE.Buy)
//				brokerage = qty*price*0.001102f;
//			else 
//				brokerage = qty*price*0.0011021653f;			
        } else {
            float value = qty * price;
            if (action == TRADINGTYPE.Buy) {
                brokerage = value * 0.001f * 1.102f;
            } else {
                brokerage = value * 0.001f * 1.102f + value * 0.0002f;
            }
        }
        if (brokerage < 15) {
            brokerage = 15.0f;
        }

        return brokerage;
    }

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    public static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            logger.error(e, e);
            return null;
        }
    }

    public static String formatFloat(float number) {
        return numberFormat.format(number);
    }

    public static Date getDownloadStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2001, 0, 1);
        return calendar.getTime();
    }

    public static float getRoundedOffValue(float val, int decimalCount) {
        BigDecimal bigDecimal = new BigDecimal(val);
        return bigDecimal.setScale(decimalCount, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static PMDate getLastBhavCopyDate() {
        return getDate(AppConfig.dateLastBhavCopy);
    }

    private static PMDate getDate(AppConfig appConfig) {
        String lastDate = appConfig.Value;
        if (lastDate != null && lastDate.trim().length() > 0) {
            try {
                return PMDateFormatter.parseYYYYMMDD(lastDate);
            } catch (ApplicationException e) {
                return new PMDate();
            }
        } else {
            return new PMDate();
        }
    }

    public static PMDate getLastDeliveryPosDate() {
        return getDate(AppConfig.dateLastDeliveryPosition);
    }

    public static void saveLastBhavCopyDate(PMDate date) {
        saveDate(AppConfig.dateLastBhavCopy, date);
    }

    public static void saveLastDeliveryPosDate(PMDate date) {
        saveDate(AppConfig.dateLastDeliveryPosition, date);
    }

    private static void saveDate(AppConfig appConfig, PMDate date) {
        appConfig.Value = PMDateFormatter.formatYYYYMMDD(date);
        AppConfig.saveConfigDetails();
    }

    public static String backupFolder(PMDate pmDate) {
        File destDir = new File(AppConfig.dataDownloadDir.Value + "/OldBhavCopy/" + pmDate.getYear());
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        return destDir.getPath();
    }
}
