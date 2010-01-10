/*
 * Created on Nov 24, 2004
 *
 */
package pm.ui;

import pm.action.Controller;
import pm.chart.WatchlistPerfChart;
import pm.vo.WatchlistVO;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Vector;

import static pm.ui.UIHelper.*;

/**
 * @author thiyagu1
 */
public class WatchlistPerf extends AbstractSplitPanel {
    private static final long serialVersionUID = 3257286924615103288L;
    private JFormattedTextField noDays = new JFormattedTextField(NumberFormat.getNumberInstance());
    private JComboBox namelistField;

    public WatchlistPerf() {
        init();
    }

    protected Component buildTopPanel() {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 2, 2, 20);
        panel.add(getNameListField(), gbc);
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.gridx = 2;
        panel.add(createLabel("No of Days"), gbc);
        gbc.gridx = 3;
        panel.add(buildIntField(noDays, "0", 5, "Enter no of Days"), gbc);
        gbc.gridx = 4;
        gbc.insets = new Insets(2, 20, 2, 2);
        panel.add(getActionButton("Submit"), gbc);
        return panel;
    }

    private Component getNameListField() {
        namelistField = createComboBox();
        Vector watchlistNames = Controller.getWatchlistNames();
        DefaultComboBoxModel model = new DefaultComboBoxModel(watchlistNames);
        namelistField.setModel(model);
        return namelistField;
    }

    private boolean validateForm() {
        if (((Number) noDays.getValue()).intValue() == 0) {
            JOptionPane.showMessageDialog(null, "Enter no. of days", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
    * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object, java.lang.String)
    */

    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal != null) {
            Vector reportVal = (Vector) retVal;
            JPanel bottomPanel = WatchlistPerfChart.createChart(reportVal);
            splitPane.setBottomComponent(bottomPanel);
        }
    }

    /* (non-Javadoc)
    * @see pm.ui.AbstractPMPanel#getData(java.lang.String)
    */

    protected Object getData(String actionCommand) {
        try {
            if (namelistField.getSelectedItem() == null) return null;
            WatchlistVO wlName = (WatchlistVO) namelistField.getSelectedItem();
            return Controller.getWatchlistPerfReport(((Number) noDays.getValue()).intValue(), wlName.getId());
        } catch (Exception e) {
            logger.error(e, e);
            UIHelper.displayInformation(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }


}
