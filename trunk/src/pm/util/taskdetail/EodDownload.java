package pm.util.taskdetail;

import pm.dao.ibatis.dao.DAOManager;
import pm.net.eod.EODDownloadManager;
import pm.util.AppConfig;
import pm.util.PMDate;
import pm.util.enumlist.SyncStatus;
import static pm.util.enumlist.SyncStatus.InSync;
import static pm.util.enumlist.SyncStatus.MustSync;

import java.util.Calendar;

/**
 * @author Thiyagu
 * @version $Id: EodDownload.java,v 1.1 2007/12/30 15:18:02 tpalanis Exp $
 * @since 30-Dec-2007
 */
public class EodDownload extends DefaultTaskDetail {

    public EodDownload() {
        super(EODDownloadManager.class, 1, true);
    }

    @Override
    protected PMDate nextRunDate() {
        return DAOManager.getDateDAO().nextQuoteDate();
    }

    protected boolean vaildTimeToRun() {
        int eodHH = AppConfig.EODRunHH.getIntValue();
        int eodMM = AppConfig.EODRunMM.getIntValue();
        Calendar cal = Calendar.getInstance();
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);
        return nextRunDate().before(PMDate.today())
                || isOnOrAfterValidRunTime(eodHH, eodMM, hh, mm);
    }

    boolean isOnOrAfterValidRunTime(int runHH, int runMM, int currentHH, int currentMM) {
        return currentHH > runHH || (currentHH == runHH && currentMM >= runMM);
    }

    public SyncStatus getSyncStat() {
        SyncStatus state = MustSync;
        if (getLastRunStatus()) {
            PMDate today = PMDate.today();
            PMDate nextRunDate = nextRunDate();
            if (nextRunDate.after(today) || (nextRunDate.equals(today)) && !vaildTimeToRun()) {
                return InSync;
            }
        }
        return state;
    }


}
