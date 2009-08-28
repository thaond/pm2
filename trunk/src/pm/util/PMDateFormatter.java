/*
 * Created on 04-Feb-2005
 *
 */
package pm.util;

import java.util.StringTokenizer;

/**
 * @author thiyagu1
 *         <p/>
 *         This method is substitute for SimpleDateFormat,
 *         This can parse only the predefined formats
 *         Note: Month is represented from 1 to 12
 */
public class PMDateFormatter {
    private enum MONTH {
        Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
    }

    ;

    public static PMDate parseDD_Mmm_YYYY(String sDate) throws ApplicationException {
        try {
            return parseDate(sDate, false);
        } catch (Exception e) {
            throw new ApplicationException("Error in date parsing (DD_MMM_YYYY) " + sDate);
        }
    }

    public static PMDate parseDD_MMM_YYYY(String sDate) throws ApplicationException {
        try {
            int st = 0;
            int en = sDate.indexOf('-');
            int dd = Integer.parseInt(sDate.substring(st, en));
            st = en + 1;
            en = sDate.indexOf('-', st);
            String MMM = sDate.substring(st, en);
            StringBuffer sb = new StringBuffer();
            sb.append(MMM.charAt(0));
            String mmm = MMM.toLowerCase();
            sb.append(mmm.charAt(1));
            sb.append(mmm.charAt(2));
            int mm = MONTH.valueOf(sb.toString()).ordinal();
            st = en + 1;
            int yy = Integer.parseInt(sDate.substring(st));
            return new PMDate(dd, mm + 1, yy);
        } catch (Exception e) {
            throw new ApplicationException("Error in date parsing (DD_MMM_YYYY) " + sDate);
        }
    }

    public static PMDate parseDD_Mmm_YY(String sDate) throws ApplicationException {
        try {
            return parseDate(sDate, true);
        } catch (Exception e) {
            throw new ApplicationException("Error in date parsing (DD-Mmm-YY) " + sDate);
        }
    }

    private static PMDate parseDate(String sDate, boolean normalizeYear) {
        int st = 0;
        int en = sDate.indexOf('-');
        int dd = Integer.parseInt(sDate.substring(st, en));
        st = en + 1;
        en = sDate.indexOf('-', st);
        int mm = MONTH.valueOf(sDate.substring(st, en)).ordinal();
        st = en + 1;
        int yy = Integer.parseInt(sDate.substring(st));
        if (normalizeYear) {
            if (yy > 90) {
                yy += 1900;
            } else {
                yy += 2000;
            }
        }
        return new PMDate(dd, mm + 1, yy);
    }

    public static PMDate parseYYYYMMDD(String sDate) {
        try {
            int yy = Integer.parseInt(sDate.substring(0, 4));
            int mm = Integer.parseInt(sDate.substring(4, 6));
            int dd = Integer.parseInt(sDate.substring(6, 8));
            return new PMDate(dd, mm, yy);
        } catch (Exception e) {
            throw new ApplicationException("Error in date parsing (YYYYMMDD) " + sDate);
        }
    }

    public static PMDate parseYYYY_MM_DD(String sDate) {
        try {
            int yy = Integer.parseInt(sDate.substring(0, 4));
            int mm = Integer.parseInt(sDate.substring(5, 7));
            int dd = Integer.parseInt(sDate.substring(8, 10));
            return new PMDate(dd, mm, yy);
        } catch (Exception e) {
            throw new ApplicationException("Error in date parsing (YYYYMMDD) " + sDate);
        }
    }

    /**
     * Parses dd/mm/yyyy string eg:28/12/2004
     *
     * @param sDate
     * @return
     * @throws ApplicationException
     */
    public static PMDate parseDD_MM_YYYY(String sDate) throws ApplicationException {
        try {
            int yy = Integer.parseInt(sDate.substring(6));
            int mm = Integer.parseInt(sDate.substring(3, 5));
            int dd = Integer.parseInt(sDate.substring(0, 2));
            return new PMDate(dd, mm, yy);
        } catch (Exception e) {
            throw new ApplicationException("Error in date parsing (DD/MM/YYYY) " + sDate);
        }

    }

    public static String formatDD_MMM_YY(PMDate date) {
        StringBuffer sb = new StringBuffer();
        sb.append(date.getDate()).append('-');
        for (MONTH mon : MONTH.values()) {
            if (mon.ordinal() == date.getMonth() - 1) {
                sb.append(mon.name());
                break;
            }
        }
        sb.append('-');
        if (date.getYear() > 1999) {
            sb.append((date.getYear() - 2000));
        } else {
            sb.append((date.getYear() - 1900));
        }
        return sb.toString();
    }

    public static String formatYYYYMMDD(PMDate date) {
        StringBuffer sb = new StringBuffer();
        sb.append(date.getYear());
        if (date.getMonth() < 10) {
            sb.append('0');
        }
        sb.append(date.getMonth());
        if (date.getDate() < 10) {
            sb.append('0');
        }
        sb.append(date.getDate());
        return sb.toString();
    }

    public static String formatDDMMYYYY(PMDate date) {
        StringBuffer sb = new StringBuffer();
        if (date.getDate() < 10) {
            sb.append('0');
        }
        sb.append(date.getDate());
        if (date.getMonth() < 10) {
            sb.append('0');
        }
        sb.append(date.getMonth());
        sb.append(date.getYear());
        return sb.toString();
    }

    public static String formatDD_MM(PMDate date) {
        StringBuffer sb = new StringBuffer();
        if (date.getDate() < 10) {
            sb.append('0');
        }
        sb.append(date.getDate());
        sb.append("/");
        if (date.getMonth() < 10) {
            sb.append('0');
        }
        sb.append(date.getMonth());
        return sb.toString();
    }

    /**
     * This formats to dd/MM/yyyy
     *
     * @param date
     * @return
     */

    public static String displayFormat(PMDate date) {
        return formatDD_MM_YYYY(date);
    }

    public static String formatDD_MM_YYYY(PMDate date) {
        return formatWithDelimter(date, '/');
    }

    public static String formatWithDelimter(PMDate date, char delimiter) {
        if (date == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if (date.getDate() < 10) {
            sb.append('0');
        }
        sb.append(date.getDate());
        sb.append(delimiter);
        if (date.getMonth() < 10) {
            sb.append('0');
        }
        sb.append(date.getMonth());
        sb.append(delimiter);
        sb.append(date.getYear());
        return sb.toString();
    }

    /**
     * Parse date string in the format [16 Mar] or [16 Mar 2005]
     *
     * @return
     * @throws ApplicationException
     */
    public static PMDate parseMMMspDD(String sDate) throws ApplicationException {
        try {
            StringTokenizer stk = new StringTokenizer(sDate, " ");
            int mm = MONTH.valueOf(stk.nextToken()).ordinal() + 1;
            int dd = Integer.parseInt(stk.nextToken());
            if (!stk.hasMoreTokens()) {
                PMDate date = new PMDate();
                date.setDate(dd);
                date.setMonth(mm);
                return date;
            } else {
                int yy = Integer.parseInt(stk.nextToken());
                return new PMDate(dd, mm, yy);
            }
        } catch (Exception e) {
            throw new ApplicationException("Error in date parsing (MMMspDD) " + sDate);
        }
    }
}
