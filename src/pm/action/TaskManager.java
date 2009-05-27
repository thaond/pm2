package pm.action;

import pm.tools.ProgressMonitor;
import pm.ui.ILongTaskView;
import pm.util.AppConfig;
import pm.util.enumlist.ITaskDetail;
import pm.util.enumlist.TASKACTION;
import static pm.util.enumlist.TASKACTION.Start;
import static pm.util.enumlist.TASKACTION.Stop;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskManager {

    private static ThreadPoolExecutor executor = null;
    private static Map<ITaskDetail, ILongTaskView> taskViewMap = new HashMap<ITaskDetail, ILongTaskView>();

    static {
        int maxThreadCount = Integer.parseInt(AppConfig.maxThreadCount.Value);
        executor = new ThreadPoolExecutor(maxThreadCount, maxThreadCount, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    private static ProgressMonitor progressMonitor = ProgressMonitor.getInstance();

    public static boolean attachView(ILongTaskView longTaskView) {
        taskViewMap.put(longTaskView.getTaskName(), longTaskView);
        return progressMonitor.attachProgressView(longTaskView);
    }

    public static void toggleTask(ILongTaskView taskView) {

        if (taskView.getTaskAction() == Start && !progressMonitor.isTaskRunning(taskView.getTaskName())) {
            addNewTask(taskView);
        } else if (taskView.getTaskAction() == Stop) {
            progressMonitor.stopTask(taskView.getTaskName());
        }
    }

    private static boolean addNewTask(ILongTaskView taskView) {
        ILongTask newTask = createTask(taskView.getTaskName());
        progressMonitor.addTask(taskView, newTask);
        return true;
    }

    private static ILongTask createTask(ITaskDetail taskname) {
        ILongTask newTask = taskname.getTask(executor);
        taskname.incAttemptCount();
        new Thread(newTask).start();
        return newTask;
    }

    public static ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public static void start(ITaskDetail taskname) {
        if (!progressMonitor.isTaskRunning(taskname)) {
            ILongTaskView taskView = taskViewMap.get(taskname);
            if (taskView == null) taskView = new HiddenView(taskname);
            progressMonitor.addTask(taskView, createTask(taskname));
        }
    }

}

class HiddenView implements ILongTaskView {
    private final ITaskDetail taskName;

    public HiddenView(ITaskDetail taskName) {
        this.taskName = taskName;
    }


    public ITaskDetail getTaskName() {
        return taskName;
    }

    public TASKACTION getTaskAction() {
        return TASKACTION.Start;
    }

    public void resetView(ILongTask task) {

    }

    public void updateProgress(ILongTask task) {

    }

    public void showStopping() {

    }

    public void showTaskCompleted() {

    }

    public void showOnlyOnDemand() {

    }

    public void showAlways() {

    }
}