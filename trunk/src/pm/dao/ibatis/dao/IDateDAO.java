package pm.dao.ibatis.dao;

import pm.util.PMDate;

import java.util.List;

/**
 * Date: Aug 9, 2006
 * Time: 9:12:11 PM
 */
public interface IDateDAO {

    PMDate getDate(int dateVal);

    PMDate getLastDate();

    PMDate getLastQuoteDate();

    void insertDate(PMDate date);

    boolean insertIfNew(PMDate date);

    void insertDates(List<PMDate> dates);

    /**
     * This method return the start date and end date
     * of the latest available EODStockQuote for the specified
     * No. of Days
     *
     * @param days
     * @return Date[] 0:endDate, 1:startDate
     */
    PMDate[] getStEnDates(int days);

    PMDate getDate(PMDate frmDate, int days);

    /**
     * This method is used for PMDate Iteration, this returns datelist between given dates
     * inclusive of both the dates if those days are market days
     */
    List<PMDate> getDates(PMDate stDate, PMDate enDate);


    void setNSEQuoteStatusFor(PMDate date);

    boolean getNSEQuoteStatusFor(PMDate date);

    PMDate lastWorkingDayLatestOf(PMDate date);

    boolean isWorkingDay(PMDate pmDate);

    PMDate nextQuoteDate();

    PMDate firstWorkingDateFrom(PMDate fromDate);
}
