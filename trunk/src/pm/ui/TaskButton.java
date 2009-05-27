package pm.ui;

import pm.util.enumlist.TASKACTION;
import pm.util.enumlist.TASKNAME;

import javax.swing.*;

public class TaskButton extends ImageButton {

    private static Icon stopIcon = new ImageIcon(TaskButton.class.getClassLoader().getResource("pm/ui/resource/Block.png"));

    private TASKNAME taskName;

    private TASKACTION taskAction;

    public TaskButton(TASKNAME taskName) {
        this.taskName = taskName;
        setActionCommand(taskName.name());
        setTaskAction(TASKACTION.Start);
    }

    public TASKNAME getTaskName() {
        return taskName;
    }

    public TASKACTION getTaskAction() {
        return taskAction;
    }

    public void setTaskAction(TASKACTION taskAction) {
        this.taskAction = taskAction;
        if (taskAction == TASKACTION.Start) {
            setIcon(taskName.getSyncStat().getIcon());
        } else if (taskAction == TASKACTION.Stop) {
            setIcon(stopIcon);
        }
    }

}
