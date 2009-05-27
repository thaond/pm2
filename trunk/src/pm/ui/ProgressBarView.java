package pm.ui;

import pm.action.ILongTask;
import pm.util.enumlist.ITaskDetail;
import pm.util.enumlist.SyncStatus;
import pm.util.enumlist.TASKACTION;

import javax.swing.*;

public class ProgressBarView implements ILongTaskView {

    private JProgressBar progressBar;

    private TaskButton taskButton;

    private JLabel taskNameLabel;

    private boolean startProgress = false;

    public ProgressBarView(JProgressBar progressBar, TaskButton taskButton, JLabel taskNameLabel) {
        this.progressBar = progressBar;
        this.taskButton = taskButton;
        this.taskNameLabel = taskNameLabel;
    }

    public ITaskDetail getTaskName() {
        return taskButton.getTaskName();
    }

    public TASKACTION getTaskAction() {
        return taskButton.getTaskAction();
    }

    public void resetView(ILongTask task) {
        if (taskButton != null) {
            taskButton.setTaskAction(TASKACTION.Stop);
        }
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        if (task.isInitComplete()) {
            startProgress(task);
        } else {
            progressBar.setString("Initalizing...");
            if (taskButton != null) {
                taskButton.setEnabled(false);
            }
        }
        progressBar.setVisible(true);
    }

    private void startProgress(ILongTask task) {
        if (task.isIndeterminate()) {
            progressBar.setString("running...");
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setString(null);
            progressBar.setMaximum(task.getTaskLength());
        }
        this.startProgress = true;
        if (taskButton != null) {
            taskButton.setEnabled(true);
        }
    }

    public void updateProgress(ILongTask task) {
        if (startProgress) {
            if (!task.isIndeterminate()) {
                progressBar.setValue(task.getProgress());
            }
        } else if (task.isInitComplete()) {
            startProgress(task);
        }
    }

    public void showStopping() {
        if (taskButton != null) {
            taskButton.setEnabled(false);
        }
        progressBar.setString("Stopping..");
    }

    public void showTaskCompleted() {
        progressBar.setVisible(false);
        if (taskButton != null) {
            taskButton.setEnabled(true);
            taskButton.setTaskAction(TASKACTION.Start);
        }
        this.startProgress = false;
    }

    public void showOnlyOnDemand() {
        if (!startProgress && taskButton.getTaskName().getSyncStat() == SyncStatus.InSync) {
            setVisible(false);
        } else {
            setVisible(true);
        }
    }

    public void showAlways() {
        setVisible(true);
    }


    private void setVisible(boolean showFlag) {
        taskButton.setVisible(showFlag);
        taskNameLabel.setVisible(showFlag);
        if (showFlag && startProgress) {
            progressBar.setVisible(true);
        } else {
            progressBar.setVisible(false);
        }
    }
}
