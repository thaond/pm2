package pm.util.enumlist;

import pm.action.ILongTask;
import pm.util.PMDate;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Thiyagu
 * @version $Id: ITaskDetail.java,v 1.1 2007/12/30 15:18:01 tpalanis Exp $
 * @since 20-Dec-2007
 */
public interface ITaskDetail {
    ILongTask getTask(ThreadPoolExecutor executor);

    SyncStatus getSyncStat();

    PMDate getLastRunDate();

    boolean getLastRunStatus();

    void setLastRunDetails(PMDate date, boolean status);

    boolean canStartSync(boolean networkAvailable);

    void incAttemptCount();
}
