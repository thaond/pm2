package pm.net.nse.downloader;

import org.apache.log4j.Logger;
import org.htmlparser.util.ParserException;
import pm.action.ILongTask;
import pm.dao.ibatis.dao.DAOManager;
import pm.net.HTTPHelper;
import pm.util.AppConst;
import pm.util.ApplicationException;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.util.enumlist.TASKNAME;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Date: Jan 27, 2007
 * Time: 6:10:46 PM
 */
public class MarketHolidayDownloader implements ILongTask {

    private boolean taskCompleted = false;

    private Logger logger = Logger.getLogger(MarketHolidayDownloader.class);
    static final String LAST_LINE_BEFORE_DATA_START = "Description";


    List<PMDate> findMarketDays(Reader reader, int year) throws IOException, ApplicationException {
        BufferedReader br = new BufferedReader(reader);
        Set<PMDate> holidays = findHolidays(br);
        return getMarketDaysFrom(year, holidays);
    }

    Set<PMDate> findHolidays(BufferedReader br) throws IOException, ApplicationException {
        Set<PMDate> holidayList = new HashSet<PMDate>();
        String endOfListIdentifier = "Top";
        skipTitle(br);
        String line;
        int columnID = 0;
        while ((line = br.readLine()) != null) {
            if (line.contains(endOfListIdentifier)) {
                break;
            }
            columnID++;
            if (columnID == 2) {
                holidayList.add(PMDateFormatter.parseDD_Mmm_YYYY(line));
            }
            columnID %= 4;
        }
        return holidayList;

    }

    private void skipTitle(BufferedReader br) throws IOException, ApplicationException {
        String line;
        while ((line = br.readLine()) != null && !line.startsWith(LAST_LINE_BEFORE_DATA_START)) ;
    }

    public String getURL(int year) {
        String relativeURLWithDate = "marketinfo/holiday_master/holidaysList.jsp?clgData=N&fromDt=01-01-" + year + "&mktSeg=CM&pageType=outuser&toDt=31-12-" + year;
        return AppConst.NSE_BASE_URL + relativeURLWithDate;
    }

    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    public int getProgress() {
        return 0;
    }

    public int getTaskLength() {
        return 0;
    }

    public void stop() {
    }

    public TASKNAME getTaskName() {
        return TASKNAME.MARKETHOLIDAYDOWNLOAD;
    }

    public boolean isInitComplete() {
        return false;
    }

    public boolean isIndeterminate() {
        return true;
    }

    public void run() {
        logger.debug("Starting Market holiday download..");
        try {
            int year = new PMDate().getYear();
            List<PMDate> marketDays = findMarketDays(getDataReader(year), year);
            saveData(marketDays);
        } catch (IOException e) {
            logger.error(e, e);
        } catch (ApplicationException e) {
            logger.error(e, e);
        } catch (ParserException e) {
            logger.error(e, e);
        } catch (Exception e) {
            logger.error(e, e);
        }
        getTaskName().setLastRunDetails(new PMDate(), taskCompleted);
        taskCompleted = true;
        logger.debug("Market holiday download completed");
    }

    void saveData(List<PMDate> marketDays) {
        if (marketDays != null) {
            save(marketDays);
            taskCompleted = true;
        }
    }

    PMDate getLatestDate() {
        return DAOManager.getDateDAO().getLastDate();
    }

    void save(List<PMDate> list) {
        DAOManager.getDateDAO().insertDates(list);
    }

    List<PMDate> getMarketDaysFrom(int currentYear, Set<PMDate> holidayList) {
        PMDate latestDate = getLatestDate();
        PMDate firstDateNextYear = new PMDate(1, 1, currentYear + 1);
        List<PMDate> marketDays = new Vector<PMDate>();
        PMDate pmDate = (latestDate.getYear() == currentYear ? latestDate.next() : new PMDate(1, 1, currentYear));
        for (; pmDate.before(firstDateNextYear); pmDate = pmDate.next()) {
            if (holidayList.contains(pmDate)) {
                continue;
            }
            if (pmDate.isWeekend()) {
                continue;
            }
            marketDays.add(pmDate);
        }
        return marketDays;
    }

    Reader getDataReader(int year) throws ParserException {
        return new HTTPHelper().getHTMLContentReader(getURL(year));
    }

}


