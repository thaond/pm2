/*
 * Created on Nov 17, 2004
 *
 */
package pm.ui;

import pm.action.Controller;
import static pm.ui.UIHelper.*;
import pm.util.Helper;
import pm.vo.WatchlistDetailsVO;
import pm.vo.WatchlistVO;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class WatchlistSetLimit extends AbstractPMPanel {
    private static final long serialVersionUID = 3257569494691230007L;
    private JSplitPane splitPane;
    private JComboBox namelistField;
    private JTable dispTable;
    private JCheckBox enableAlertField = createCheckBox("Enable Alert", false);
    private static final String dataAction = "Data";

    public WatchlistSetLimit() {
        init();
    }

    public void init() {
        buildPanel(this);
        flagShowProgressBar = true;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        this.add(getSplitPane(), gbc);
        doAction(dataAction);
    }

    private Component getSplitPane() {
        splitPane = createSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(buildTopPanel());
        splitPane.setBottomComponent(buildBottomPanel());
        return splitPane;

    }

    private Component buildTopPanel() {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setLayout(new GridBagLayout());
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("WatchList"), gbc);
        gbc.gridx = 1;
        panel.add(getNameListField(), gbc);
        gbc.insets = new Insets(2, 50, 2, 2);
        gbc.gridx = 2;
        panel.add(enableAlertField, gbc);
        gbc.gridx = 3;
        panel.add(getSaveButton(), gbc);
        return panel;
    }

    private Component buildBottomPanel() {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, 60));
        return panel;
    }

    private Component getNameListField() {
        namelistField = createComboBox();
        namelistField.setActionCommand(dataAction);
        namelistField.addActionListener(this);
        Vector watchlistNames = Controller.getWatchlistNames();
        DefaultComboBoxModel model = new DefaultComboBoxModel(watchlistNames);
        namelistField.setModel(model);
        return namelistField;
    }

    private Component getSaveButton() {
        JButton button = createButton("Save");
        button.addActionListener(this);
        return button;

    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object, java.lang.String)
      */
    protected void doDisplay(Object retVal, String actionCommand) {
        enableAlertField.setSelected(((WatchlistDetailsVO) namelistField.getSelectedItem()).isAlertEnabled());
        if (retVal == null) return;
        List<WatchlistVO> vWatchList = (List<WatchlistVO>) retVal;
        JPanel panel = new JPanel(new GridLayout(1, 1));
        buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - 50));
        if (vWatchList.size() != 0) {
            dispTable = createTable(new WLSLTableModel(vWatchList));
            dispTable.setPreferredScrollableViewportSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - 100));
            JScrollPane scrollPane = new JScrollPane(dispTable);
            panel.add(scrollPane);
        }
        splitPane.setBottomComponent(panel);
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#getData(java.lang.String)
      */
    protected Object getData(String actionCommand) {
        if (actionCommand.equals("Save")) {
            if (dispTable == null) return null;
            List vWatchList = ((WLSLTableModel) dispTable.getModel()).getData();
            WatchlistDetailsVO wlDetails = (WatchlistDetailsVO) namelistField.getSelectedItem();
            wlDetails.setAlertEnabled(enableAlertField.isSelected());
            WatchlistVO[] watchlistVOs = new WatchlistVO[vWatchList.size()];
            watchlistVOs = (WatchlistVO[]) vWatchList.toArray(watchlistVOs);
            boolean status = Controller.saveWatchlist(watchlistVOs, wlDetails);
            if (!status) {
                logger.error("Error saving watchlist");
                UIHelper.displayInformation(null, "Error saving watchlist", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (actionCommand.equals(dataAction)) {
            if (namelistField.getSelectedItem() != null) {
                WatchlistDetailsVO wlName = (WatchlistDetailsVO) namelistField.getSelectedItem();
                return Controller.getWatchlistView(wlName.getId());
            }
        }
        return null;
    }
}

class WLSLTableModel extends AbstractTableModel {
    private List vData;
    private String[] colName = {"Stock", "CMP", "Lower Limit", "Upper Limit"};

    public List getData() {
        return vData;
    }

    public WLSLTableModel(List<WatchlistVO> data) {
        vData = data;
    }

    public int getColumnCount() {
        return colName.length;
    }

    public int getRowCount() {
        return vData.size();
    }

    public String getColumnName(int column) {
        return colName[column];
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        WatchlistVO watchlistVO = (WatchlistVO) vData.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return watchlistVO.getStockCode();
            case 1:
                return Helper.formatFloat(watchlistVO.getCurrQuote().getLastPrice());
            case 2:
                return Helper.formatFloat(watchlistVO.getFloor());
            case 3:
                return Helper.formatFloat(watchlistVO.getCeil());
        }
        return null;
    }

    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2 || columnIndex == 3;

    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        float val = Float.parseFloat((String) aValue);
        WatchlistVO watchlistVO = (WatchlistVO) vData.get(rowIndex);
        switch (columnIndex) {
            case 2:
                watchlistVO.setFloor(val);
                break;
            case 3:
                watchlistVO.setCeil(val);
                break;
        }
    }
}
