package pm.action;

import pm.util.enumlist.TASKNAME;

public interface ILongTask extends Runnable {

    boolean isTaskCompleted();

    int getProgress();

    int getTaskLength();

    void stop();

    TASKNAME getTaskName();

    boolean isInitComplete();

    boolean isIndeterminate();

}
