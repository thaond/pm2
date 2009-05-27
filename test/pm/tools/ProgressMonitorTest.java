package pm.tools;

import junit.framework.TestCase;
import pm.AppLoader;
import pm.action.ILongTask;
import pm.ui.ILongTaskView;
import pm.ui.ProgressBarView;
import pm.ui.TaskButton;
import pm.ui.UIHelper;
import pm.util.enumlist.TASKNAME;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProgressMonitorTest extends TestCase {

    public void test() {
    }

    static JProgressBar newProgressBar = new JProgressBar();
    static JLabel newLabel = new JLabel();

    public static void main(String[] str) {
        AppLoader.initConsoleLogger();
        JFrame frame = new JFrame();
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        frame.add(panel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        final ProgressMonitor monitor = ProgressMonitor.getInstance();
        JLabel label = new JLabel();
//		JProgressBar progressBar1 = new JProgressBar();
//		panel.add(progressBar1,gbc);
//		gbc.gridx++;
//		panel.add(label, gbc);
//		TaskSyncDisplay viewHolder1 = new TaskSyncDisplay(progressBar1, null, label);
//		monitor.addTask(viewHolder1, new MockLongTask(10,3,TASKNAME.CORPACTIONDOWNLOAD));
        JProgressBar progressBar2 = UIHelper.createHiddenProgressBar();
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(progressBar2, gbc);
        MockLongTask task2 = new MockLongTask(10, 1, TASKNAME.EODDOWNLOAD);
        TaskButton button2 = getMyTaskButton(TASKNAME.EODDOWNLOAD, task2);
        gbc.gridx++;
        panel.add(button2, gbc);
        label = new JLabel();
        gbc.gridx++;
        panel.add(label, gbc);
        ILongTaskView viewHolder2 = new ProgressBarView(progressBar2, button2, null) {
            int i = 0;
        };
        monitor.addTask(viewHolder2, task2);
        JButton newButton = new JButton("New Monitor");
        final TaskButton newCanButton = getMyTaskButton(TASKNAME.EODDOWNLOAD, task2);
        newButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                monitor.attachProgressView(new ProgressBarView(newProgressBar, newCanButton, null));
            }

        });
        gbc.gridx++;
        panel.add(newButton, gbc);
        gbc.gridx++;
        newProgressBar.setVisible(false);
        panel.add(newProgressBar, gbc);
        gbc.gridx++;
        panel.add(newCanButton, gbc);
        gbc.gridx++;
        panel.add(newLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        JProgressBar progressBar3 = new JProgressBar();
        panel.add(progressBar3, gbc);
        MockIndeterminateLongTask task3 = new MockIndeterminateLongTask(10, 1, TASKNAME.STOCKLISTDOWNLOAD);
        TaskButton button3 = getMyTaskButton(TASKNAME.STOCKLISTDOWNLOAD, task3);
        gbc.gridx++;
        panel.add(button3, gbc);
        label = new JLabel();
        gbc.gridx++;
        panel.add(label, gbc);

        ILongTaskView viewHolder3 = new ProgressBarView(progressBar3, button3, null);
        monitor.addTask(viewHolder3, task3);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

    }

    private static TaskButton getMyTaskButton(TASKNAME taskName, final ILongTask task) {
        TaskButton taskButton = new TaskButton(taskName);
        taskButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                task.stop();

            }

        });
        return taskButton;
    }
}

class MyActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

    }

}

class MockLongTask implements ILongTask {

    int length;
    int currProgress = 0;
    int inc;
    int initCount = 0;
    TASKNAME name;
    boolean stop = false;

    public MockLongTask(int length, int inc, TASKNAME name) {
        this.length = length;
        this.inc = inc;
        this.name = name;
    }

    public boolean isTaskCompleted() {
        boolean taskCompleted = length <= currProgress;
        System.out.println(name + " " + taskCompleted);
        return taskCompleted;
    }

    public int getProgress() {
        currProgress += inc;
        return currProgress;
    }

    public int getTaskLength() {
        return length;
    }

    public void stop() {
        System.out.println("Stop Called in " + name);
        currProgress = length;
        stop = true;
    }

    public boolean isStopped() {
        return stop;
    }

    public TASKNAME getTaskName() {
        return name;
    }

    public void run() {

    }

    public boolean isInitComplete() {
        initCount++;
        return initCount > 3;
    }

    public boolean isIndeterminate() {
        return false;
    }
}

class MockIndeterminateLongTask implements ILongTask {

    int length;
    int currProgress = 0;
    int inc;
    TASKNAME name;
    boolean stop = false;
    int initCount = 0;

    public MockIndeterminateLongTask(int length, int inc, TASKNAME name) {
        this.length = length;
        this.inc = inc;
        this.name = name;
    }

    public boolean isTaskCompleted() {
        currProgress += inc;
        boolean taskCompleted = length <= currProgress;
        System.out.println(name + " " + taskCompleted);
        return taskCompleted;
    }

    public int getProgress() {
        currProgress += inc;
        return currProgress;
    }

    public int getTaskLength() {
        return length;
    }

    public void stop() {
        System.out.println("Stopped called : " + name);
        currProgress = length;
        stop = true;
    }

    public boolean isStopped() {
        return stop;
    }

    public TASKNAME getTaskName() {
        return name;
    }

    public void run() {

    }

    public boolean isInitComplete() {
        initCount++;
        return initCount > 3;
    }

    public boolean isIndeterminate() {
        return true;
    }
}
