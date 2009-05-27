/*
 * Created on 02-Feb-2005
 *
 */
package pm.util;

import pm.dao.ibatis.dao.DAOManager;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * @author thiyagu1
 *         This class is created to use only Date for comparison, ignores time.
 */
public class PMDate implements Cloneable, Comparable<PMDate>, Serializable {
    private static final long serialVersionUID = 3257281426906101302L;
    int date = 1;
    int month = 1;
    int year = 1995;

    public static PMDate START_DATE = new PMDate(1, 1, 2006);

    public PMDate() {
        this(Calendar.getInstance());
    }

    /**
     * @param date
     * @param month (represented 1..12)
     * @param year
     */
    public PMDate(int date, int month, int year) {
        this.date = date;
        this.month = month;
        this.year = year;
    }

    public PMDate(Calendar cal) {
        this.date = cal.get(Calendar.DATE);
        this.month = cal.get(Calendar.MONTH) + 1;
        this.year = cal.get(Calendar.YEAR);
    }

    public PMDate(int intValue) {
        setIntVal(intValue);
    }

    public PMDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        this.date = cal.get(Calendar.DATE);
        this.month = cal.get(Calendar.MONTH) + 1;
        this.year = cal.get(Calendar.YEAR);
    }

    public PMDate get52WeeksBefore() {
        return DAOManager.getDateDAO().firstWorkingDateFrom(new PMDate(date, month, year - 1));
    }

    public Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, date, 0, 0, 0);
        return cal;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getJavaDate() {
        return getCalendar().getTime();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof PMDate)) {
            return false;
        }
        PMDate target = (PMDate) o;
        return getDate() == target.getDate() &&
                getMonth() == target.getMonth() && getYear() == target.getYear();
    }

    public boolean before(PMDate target) {
        return (this.compareTo(target) == -1);
    }

    public boolean after(PMDate target) {
        return (this.compareTo(target) == 1);
    }

    public int hashCode() {
        long ht = year * 10000 + month * 100 + date;
        return (int) ht ^ (int) (ht >> 32);
    }

    public String toString() {
        return date + "/" + month + "/" + year;
    }

    /* (non-Javadoc)
      * @see java.lang.Object#clone()
      */
    @Override
    public Object clone() {
        PMDate clonedDate = new PMDate(date, month, year);
        return clonedDate;
    }

    public int compareTo(PMDate o) {
        PMDate target = (PMDate) o;
        if (getYear() < target.getYear()) {
            return -1;
        }
        if (getYear() > target.getYear()) {
            return 1;
        }
        if (getMonth() < target.getMonth()) {
            return -1;
        }
        if (getMonth() > target.getMonth()) {
            return 1;
        }
        if (getDate() < target.getDate()) {
            return -1;
        }
        if (getDate() > target.getDate()) {
            return 1;
        }
        return 0;
    }

    public PMDate previous() {
        Calendar cal = getCalendar();
        cal.add(Calendar.DATE, -1);
        return new PMDate(cal);
    }

    public PMDate next() {
        Calendar cal = getCalendar();
        cal.add(Calendar.DATE, 1);
        return new PMDate(cal);
    }

    public PMDate getDateAddingDays(int days) {
        Calendar cal = getCalendar();
        cal.add(Calendar.DATE, days);
        return new PMDate(cal);
    }

    public int getIntVal() {
        return year * 10000 + month * 100 + date;
    }

    public void setIntVal(int intValue) {
        date = intValue % 100;
        month = ((intValue - date) % 10000) / 100;
        year = (intValue - month * 100 - date) / 10000;
    }


    public boolean isWeekend() {
        int dayOfWeek = getCalendar().get(Calendar.DAY_OF_WEEK);
        return (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

    }

    public String toWrite() {
        return PMDateFormatter.formatYYYYMMDD(this);
    }

    public int get(AppConst.TIMEPERIOD timePeriod) {
        switch (timePeriod) {
            case Daily:
                return 0;

            case Weekly:
                return getCalendar().get(Calendar.WEEK_OF_YEAR);

            case Monthly:
                return month;
        }
        return -1;
    }

    public PMDate quaterStartDate() {
        int mVal = (int) Math.ceil((float) month / 3f);
        mVal = (mVal - 1) * 3 + 1;
        return new PMDate(1, mVal, year);
    }

    public boolean hasQuote() {
        return DAOManager.getDateDAO().getNSEQuoteStatusFor(this);
    }

    public boolean isWorkingDay() {
        return DAOManager.getDateDAO().isWorkingDay(this);
    }

    public static PMDate today() {
        return new PMDate();
    }

}
