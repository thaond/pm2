package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import pm.util.PMDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: Aug 9, 2006
 * Time: 9:14:24 PM
 */
public class DateDAO extends SqlMapDaoTemplate implements IDateDAO {
    public DateDAO(DaoManager daoManager) {
        super(daoManager);
    }

    public PMDate getDate(int dateVal) {
        Map paramMap = new HashMap();
        paramMap.put("dateVal", dateVal);
        return (PMDate) super.queryForObject("getDate", paramMap);
    }

    public PMDate getLastDate() {
        return (PMDate) super.queryForObject("getLastDate");
    }

    public PMDate getLastQuoteDate() {
        List<PMDate> dates = super.queryForList("getQuoteDates");
        if (dates.isEmpty()) {
            return null;
        }
        return dates.get(dates.size() - 1);
    }

    public void insertDate(PMDate date) {
        super.insert("insertDate", date);
    }

    public boolean insertIfNew(PMDate date) {
        try {
            daoManager.startTransaction();
            PMDate pmDate = getDate(date.getIntVal());
            if (pmDate == null) {
                insertDate(date);
                daoManager.commitTransaction();
                return true;
            } else {
                return false;
            }
        } finally {
            daoManager.endTransaction();
        }
    }

    public void insertDates(List<PMDate> dates) {
        super.startBatch();
        for (PMDate date : dates) {
            insertDate(date);
        }
        super.executeBatch();
    }

    public PMDate[] getStEnDates(int days) {
        List<PMDate> list = super.queryForList("getDate", null);
        if (list.isEmpty()) {
            return new PMDate[2];
        }
        int index = list.size() - days;
        if (index < 0) {
            index = 0;
        }
        return new PMDate[]{list.get(list.size() - 1), list.get(index)};
    }

    public PMDate getDate(PMDate frmDate, int days) {
        if (days == 0) return frmDate;
        String id = days > 0 ? "getDatesFromDate" : "getDatesToDate";
        List<PMDate> list = super.queryForList(id, frmDate, 0, Math.abs(days) + 1);
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    public List<PMDate> getDates(PMDate stDate, PMDate enDate) {
        Map paramMap = new HashMap();
        if (stDate != null) {
            paramMap.put("stDate", stDate.getIntVal());
        }
        if (enDate == null) {
            setCurrentDateAsEndDate(paramMap);
        } else {
            paramMap.put("enDate", enDate.getIntVal());
        }
        return super.queryForList("getDate", paramMap);
    }

    public void setNSEQuoteStatusFor(PMDate date) {
        super.update("setNSEQuoteStatus", date);
    }

    public boolean getNSEQuoteStatusFor(PMDate date) {
        Object o = super.queryForObject("getNSEQuoteStatus", date);
        return o == null ? false : (Boolean) o;
    }

    private void setCurrentDateAsEndDate(Map paramMap) {
        paramMap.put("enDate", new PMDate().getIntVal());
    }

    public PMDate lastWorkingDayLatestOf(PMDate date) {
        return (PMDate) super.queryForObject("lastWorkingDay", date);
    }

    public boolean isWorkingDay(PMDate pmDate) {
        return getDate(pmDate.getIntVal()) != null;
    }

    public PMDate nextQuoteDate() {
        PMDate retVal = (PMDate) super.queryForObject("nextQuoteDate");
        return retVal != null ? retVal : new PMDate();
    }

    public PMDate firstWorkingDateFrom(PMDate fromDate) {
        return (PMDate) super.queryForObject("firstWorkingDay", fromDate);
    }
}
