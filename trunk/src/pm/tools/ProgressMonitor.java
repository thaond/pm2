package pm.tools;

import org.apache.log4j.Logger;
import pm.action.ILongTask;
import pm.ui.ILongTaskView;
import pm.util.enumlist.ITaskDetail;
import pm.util.enumlist.TASKNAME;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;

public class ProgressMonitor implements ActionListener {

    private static Logger logger = Logger.getLogger(ProgressMonitor.class);

    private static ProgressMonitor instance = new ProgressMonitor();
    private Hashtable<TASKNAME, TaskHolder> htTaskHolders = new Hashtable<TASKNAME, TaskHolder>();

    private ProgressMonitor() {
        Timer timer = new Timer(1000, this);
        timer.start();
    }

    public static ProgressMonitor getInstance() {
        return instance;
    }

    public void addTask(ILongTaskView viewHolder, ILongTask task) {
        htTaskHolders.put(task.getTaskName(), new TaskHolder(task, viewHolder));
    }

    public boolean attachProgressView(ILongTaskView viewHolder) {
        TaskHolder taskHolder = htTaskHolders.get(viewHolder.getTaskName());
        if (taskHolder != null) {
            taskHolder.attachProgressView(viewHolder);
            return true;
        } else {
            return false;
        }
    }

    public void actionPerformed(ActionEvent e) {
        Enumeration<TASKNAME> keys = htTaskHolders.keys();
        while (keys.hasMoreElements()) {
            ITaskDetail taskName = keys.nextElement();
            TaskHolder taskHolder = htTaskHolders.get(taskName);
            if (!taskHolder.updateProgressCheckRunning()) {
                htTaskHolders.remove(taskName);
            }
        }
    }

    public boolean isTaskRunning(ITaskDetail taskName) {
        TaskHolder taskHolder = htTaskHolders.get(taskName);
        return taskHolder != null;
    }

    public void stopTask(ITaskDetail taskName) {
        TaskHolder taskHolder = htTaskHolders.get(taskName);
        if (taskHolder != null) {
            taskHolder.stop();
        }

    }

    class TaskHolder {
        private ILongTask task;
        private ILongTaskView viewHolder;

        public TaskHolder(ILongTask task, ILongTaskView viewHolder) {
            this.task = task;
            this.viewHolder = viewHolder;
            this.viewHolder.resetView(this.task);
        }

        public ILongTask getTask() {
            return task;
        }

        public void attachProgressView(ILongTaskView newViewHolder) {
            viewHolder = newViewHolder;
            viewHolder.resetView(task);
        }

        public ILongTaskView getViewHolder() {
            return viewHolder;
        }

        public boolean updateProgressCheckRunning() {
            if (task.isTaskCompleted()) {
                viewHolder.showTaskCompleted();
                logger.info("Task completed : " + task.getTaskName());
                return false;
            } else {
                viewHolder.updateProgress(task);
                return true;
            }
        }

        public void stop() {
            task.stop();
            viewHolder.showStopping();
        }

        @Override
        public int hashCode() {
            return task.getTaskName().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TaskHolder other = (TaskHolder) obj;
            return task.getTaskName().equals(other.task.getTaskName());
        }

    }

}

