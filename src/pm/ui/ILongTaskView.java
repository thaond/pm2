package pm.ui;

import pm.action.ILongTask;
import pm.util.enumlist.ITaskDetail;
import pm.util.enumlist.TASKACTION;

/**
 * @author Thiyagu
 * @version $Id: ILongTaskView.java,v 1.3 2007/12/30 15:17:58 tpalanis Exp $
 * @since 12-Aug-2007
 */
public interface ILongTaskView {
    ITaskDetail getTaskName();

    TASKACTION getTaskAction();

    void resetView(ILongTask task);

    void updateProgress(ILongTask task);

    void showStopping();

    void showTaskCompleted();

    void showOnlyOnDemand();

    void showAlways();
}
