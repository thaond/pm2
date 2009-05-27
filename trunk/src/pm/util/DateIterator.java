package pm.util;

import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IDateDAO;

import java.util.List;


public class DateIterator {

    private int index = -1;

    private List<PMDate> dateList;

    public DateIterator() {
        dateList = getDates(null, null);
    }

    IDateDAO getDateDAO() {
        return DAOManager.getDateDAO();
    }

    /**
     * If stDate is null, then starts from firstdate
     * If enDate is null, iterates till current date
     * If both are null iterates all the dates
     *
     * @param stDate
     * @param enDate
     */
    public DateIterator(PMDate stDate, PMDate enDate) {
        dateList = getDates(stDate, enDate);
    }

    protected List<PMDate> getDates(PMDate stDate, PMDate enDate) {
        return getDateDAO().getDates(stDate, enDate);
    }

    public PMDate next() {
        if ((index + 1) < dateList.size()) {
            return dateList.get(++index);
        } else {
            return null;
        }
    }

    public boolean hasNext() {
        return ((index + 1) < dateList.size());
    }

    public boolean hasPrevious() {
        return (index > 0);
    }

    public PMDate previous() {
        if (index > 0) {
            return dateList.get(--index);
        } else {
            return null;
        }
    }

    public PMDate firstElement() {
        return dateList.get(0);
    }

    public PMDate lastElement() {
        return dateList.get(dateList.size() - 1);
    }

    public int size() {
        return dateList.size();
    }

    public boolean movePtrToDate(PMDate date) {
        for (int i = 0; i < dateList.size(); i++) {
            if (dateList.get(i).equals(date)) {
                index = i;
                return true;
            }
        }
        return false;
    }

}
