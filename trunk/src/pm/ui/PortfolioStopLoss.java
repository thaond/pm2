/*
 * Created on Feb 26, 2005
 *
 */
package pm.ui;

import pm.action.Controller;
import static pm.ui.UIHelper.*;
import pm.ui.table.TableCellDisplay;
import pm.util.DropDownWrapper;
import pm.vo.PortfolioDetailsVO;
import pm.vo.StopLossVO;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

/**
 * @author pn
 */
public class PortfolioStopLoss extends AbstractPMPanel {

    private static final long serialVersionUID = 1L;

    private JSplitPane splitPane;

    private JTable dispTable = null;

    private JComboBox portfolioList = new JComboBox();

    private JCheckBox enableAlertField = createCheckBox("Enable Alert", false);

    private static final String dataAction = "Data";

    public PortfolioStopLoss() {
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

    private Component buildBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 1));
        buildChildPanel(bottomPanel);
        bottomPanel.setMinimumSize(new Dimension(UIHelper.WIDTH,
                UIHelper.HEIGHT - 60));
        bottomPanel.setBackground(UIHelper.COLOR_BG_PANEL);
        return bottomPanel;
    }

    private Component buildTopPanel() {
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
        panel.add(createLabel("Porfolio"), gbc);
        gbc.gridx = 1;
        panel.add(buildPortfolioList(portfolioList, false), gbc);
        portfolioList.addActionListener(this);
        portfolioList.setActionCommand(dataAction);
        gbc.gridx = 2;
        gbc.insets = new Insets(2, 50, 2, 2);
        panel.add(enableAlertField, gbc);
        gbc.gridx = 3;
        panel.add(getSaveButton(), gbc);
        return panel;
    }

    private Component getSaveButton() {
        JButton button = createButton("Save");
        button.addActionListener(this);
        return button;
    }

    /*
      * (non-Javadoc)
      *
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object, java.lang.String)
      */
    protected void doDisplay(Object retVal, String actionCommand) {
        if (actionCommand.equals(dataAction)) {
            enableAlertField.setSelected(((PortfolioDetailsVO) ((DropDownWrapper) portfolioList
                    .getSelectedItem()).getAccount()).isAlertEnabled());
            JPanel panel = new JPanel(new GridLayout(1, 1));
            buildChildPanel(panel);
            panel.setMinimumSize(new Dimension(UIHelper.WIDTH,
                    UIHelper.HEIGHT - 50));
            if (retVal != null) {
                dispTable = createTable(new StopLossModel(retVal));
                dispTable.setPreferredScrollableViewportSize(new Dimension(
                        UIHelper.WIDTH, UIHelper.HEIGHT - 50));
                JScrollPane scrollPane = new JScrollPane(dispTable);
                panel.add(scrollPane);
            }
            splitPane.setBottomComponent(panel);
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see pm.ui.AbstractPMPanel#getData(java.lang.String)
      */
    protected Object getData(String actionCommand) {
        String portfolioName = portfolioList.getSelectedItem().toString();
        if (actionCommand.equals(dataAction))
            return Controller.getStopLoss(portfolioName);
        else if (actionCommand.equals("Save")) {
            if (dispTable == null)
                return null;
            PortfolioDetailsVO detailsVO = (PortfolioDetailsVO) ((DropDownWrapper) portfolioList
                    .getSelectedItem()).getAccount();
            detailsVO.setAlertEnabled(enableAlertField.isSelected());
            StopLossModel model = (StopLossModel) dispTable.getModel();
            boolean retVal = Controller
                    .saveStopLoss(detailsVO, model.getData());
            if (!retVal) {
                logger.error("Stop Loss save failed");
                UIHelper.displayInformation(null, "Stop Loss save failed",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            return null;
        }
        return null;
    }

    private class StopLossModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;

        private List<StopLossVO> data = null;

        private String[] colName = {"StockCode", "StopLoss2", "StopLoss1",
                "Current Price", "Target1", "Target2"};

        public StopLossModel(Object retVal) {
            data = (List<StopLossVO>) retVal;
        }

        public List<StopLossVO> getData() {
            return data;
        }

        /*
           * (non-Javadoc)
           *
           * @see javax.swing.table.TableModel#getRowCount()
           */
        public int getRowCount() {
            return data.size();
        }

        /*
           * (non-Javadoc)
           *
           * @see javax.swing.table.TableModel#getColumnCount()
           */
        public int getColumnCount() {
            return colName.length;
        }

        /*
           * (non-Javadoc)
           *
           * @see javax.swing.table.TableModel#getValueAt(int, int)
           */
        public Object getValueAt(int rowIndex, int columnIndex) {
            StopLossVO stopLossVO = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return stopLossVO.getStockCode();
                case 1:
                    return new TableCellDisplay(stopLossVO.getStopLoss2(), 0);
                case 2:
                    return new TableCellDisplay(stopLossVO.getStopLoss1(), 0);
                case 3:
                    return new TableCellDisplay(stopLossVO.getQuoteVO()
                            .getLastPrice(), 0);
                case 4:
                    return new TableCellDisplay(stopLossVO.getTarget1(), 0);
                case 5:
                    return new TableCellDisplay(stopLossVO.getTarget2(), 0);
            }
            return null;
        }

        public String getColumnName(int column) {
            return colName[column];
        }

        /*
           * (non-Javadoc)
           *
           * @see javax.swing.table.TableModel#isCellEditable(int, int)
           */
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return false;
                case 1:
                    return true;
                case 2:
                    return true;
                case 3:
                    return false;
                case 4:
                    return true;
                case 5:
                    return true;
            }
            return false;
        }

        /*
           * (non-Javadoc)
           *
           * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int,
           *      int)
           */
        public void setValueAt(Object arg0, int rowIndex, int columnIndex) {
            StopLossVO stopLossVO = data.get(rowIndex);
            float val = Float.parseFloat((String) arg0);
            switch (columnIndex) {
                case 1:
                    stopLossVO.setStopLoss2(val);
                    break;
                case 2:
                    stopLossVO.setStopLoss1(val);
                    break;
                case 4:
                    stopLossVO.setTarget1(val);
                    break;
                case 5:
                    stopLossVO.setTarget2(val);
                    break;
            }
        }
    }
}
