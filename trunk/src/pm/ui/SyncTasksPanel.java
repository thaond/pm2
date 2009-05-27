package pm.ui;

import pm.action.TaskManager;
import pm.util.enumlist.TASKNAME;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class SyncTasksPanel extends AbstractPMPanel {

    private static final long serialVersionUID = 1L;
    private Hashtable<TASKNAME, ProgressBarView> htViewHolder = new Hashtable<TASKNAME, ProgressBarView>();

    private static SyncTasksPanel instance = new SyncTasksPanel();

    public static SyncTasksPanel instance() {
        return instance;
    }

    public SyncTasksPanel() {
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        addTask(gbc, "EOD", TASKNAME.EODDOWNLOAD);
        addTask(gbc, "Corp Action", TASKNAME.CORPACTIONDOWNLOAD);
        addTask(gbc, "Corp Result", TASKNAME.CORPRESULTDOWNLOAD);
        addTask(gbc, "Stock List", TASKNAME.STOCKLISTDOWNLOAD);
        addTask(gbc, "Trading Days", TASKNAME.MARKETHOLIDAYDOWNLOAD);
//        addTask(gbc, "ICICI", TASKNAME.ICICITRANSACTIONSYNC);
        addOnDemandFilter();
    }

    private void addOnDemandFilter() {

    }

    public void showAllTasks() {
        for (ILongTaskView syncDisplay : htViewHolder.values()) {
            syncDisplay.showAlways();
        }
    }

    public void showTasksOnDemand() {
        for (ILongTaskView syncDisplay : htViewHolder.values()) {
            syncDisplay.showOnlyOnDemand();
        }
    }

    private void addTask(GridBagConstraints gbc, String taskText, TASKNAME taskname) {
        gbc.gridy++;
        gbc.gridx = 0;
        TaskButton companyActionUpdateButton = UIHelper.createTaskButton(taskname);
        companyActionUpdateButton.addActionListener(this);
        this.add(companyActionUpdateButton, gbc);
        gbc.gridx++;
        JLabel taskNameLabel = UIHelper.createLabel(taskText);
        taskNameLabel.setFont(UIHelper.FONT_TASK_DATA);
        gbc.anchor = GridBagConstraints.WEST;
        this.add(taskNameLabel, gbc);
        gbc.gridx++;
        JProgressBar progressBar = UIHelper.createHiddenProgressBar();
        gbc.anchor = GridBagConstraints.EAST;
        this.add(progressBar, gbc);
        ProgressBarView taskSyncDisplay = new ProgressBarView(progressBar, companyActionUpdateButton, taskNameLabel);
        TaskManager.attachView(taskSyncDisplay);
        htViewHolder.put(taskname, taskSyncDisplay);
    }

    @Override
    protected void doDisplay(Object retVal, String actionCommand) {

    }

    @Override
    protected Object getData(String actionCommand) {
        TaskManager.toggleTask(htViewHolder.get(TASKNAME.valueOf(actionCommand)));
        return null;
    }
}
