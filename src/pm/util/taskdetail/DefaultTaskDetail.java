package pm.util.taskdetail;

import org.apache.log4j.Logger;
import pm.action.ILongTask;
import pm.util.AppConfig;
import pm.util.PMDate;
import pm.util.enumlist.ITaskDetail;
import pm.util.enumlist.SyncStatus;
import static pm.util.enumlist.SyncStatus.*;

import java.lang.reflect.Constructor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Thiyagu
 * @version $Id: DefaultTaskDetail.java,v 1.1 2007/12/30 15:18:02 tpalanis Exp $
 * @since 30-Dec-2007
 */
public class DefaultTaskDetail implements ITaskDetail {

    static Logger logger = Logger.getLogger(DefaultTaskDetail.class);
    protected int attemptCount = 0;
    static final int MAX_ATTEMPT = 3;

    static final String DATE_PREFIX = "date";
    static final String STATUS_PREFIX = "status";

    private final int days;
    private final Class taskClass;
    private final boolean needNetwork;

    public DefaultTaskDetail(Class taskClass, int daysBetweenRun, boolean needNetwork) {
        this.taskClass = taskClass;
        this.days = daysBetweenRun;
        this.needNetwork = needNetwork;
    }

    public String name() {
        //TODO Change name to use TASKNAME instead of class eg: ICICITransaction instead of TRANSACTIONDOWNLOADER
        return taskClass.getSimpleName().toUpperCase();
    }

    public PMDate getLastRunDate() {
        return AppConfig.valueOf(DATE_PREFIX + name()).getDateValue();
    }

    public boolean getLastRunStatus() {
        return AppConfig.valueOf(STATUS_PREFIX + name()).getBooleanValue();
    }

    public void setLastRunDetails(PMDate date, boolean status) {
        AppConfig.valueOf(DATE_PREFIX + name()).setValueWithoutSave(date);
        AppConfig.valueOf(STATUS_PREFIX + name()).setValueWithoutSave(status);
        AppConfig.saveConfigDetails();
    }

    public boolean canStartSync(boolean networkAvailable) {
        return networkDependencyCheck(networkAvailable)
                && isMustSync() && canRunToday();
    }

    boolean isMustSync() {
        return getSyncStat() == MustSync;
    }

    boolean canRunToday() {
        return vaildTimeToRun() && isAttemptLeftForDay();
    }

    boolean networkDependencyCheck(boolean networkAvailable) {
        return !needNetwork() || networkAvailable;
    }

    public ILongTask getTask(ThreadPoolExecutor executor) {
        try {
            try {
                Constructor constructor = taskClass.getConstructor(ThreadPoolExecutor.class);
                return (ILongTask) constructor.newInstance(executor);
            } catch (NoSuchMethodException e) {
                return (ILongTask) taskClass.newInstance();
            }
        } catch (Exception e) {
            logger.fatal(e, e);
        }
        return null;
    }

    protected PMDate nextRunDate() {
        return getLastRunDate().getDateAddingDays(days);
    }

    public SyncStatus getSyncStat() {
        SyncStatus state = MustSync;
        PMDate today = new PMDate();
        if (getLastRunStatus()) {
            if (isInSync(today)) {
                state = InSync;
            } else if (today.before(nextRunDate())) {
                state = CanSync;
            }
        }
        return state;
    }

    boolean isInSync(PMDate today) {
        return getLastRunDate().equals(today);
    }

    protected boolean needNetwork() {
        return needNetwork;
    }

    protected boolean vaildTimeToRun() {
        return true;
    }

    boolean isAttemptLeftForDay() {
        PMDate today = new PMDate();
        if (getLastRunDate().before(today)) attemptCount = 0;
        return attemptCount < MAX_ATTEMPT;
    }

    public void incAttemptCount() {
        attemptCount++;
    }

}
