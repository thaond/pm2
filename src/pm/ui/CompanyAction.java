/*
 * Created on Oct 27, 2004
 *
 */
package pm.ui;

import pm.action.Controller;

import static pm.ui.UIHelper.*;
import static pm.util.AppConst.COMPANY_ACTION_TYPE.*;
import static pm.util.AppConst.COMPANY_ACTION_TYPE.Merger;

import pm.ui.table.TableCellDisplay;
import pm.util.AppConst.COMPANY_ACTION_TYPE;
import pm.vo.CompanyActionVO;
import pm.vo.DemergerVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class CompanyAction extends AbstractPMPanel {
    private static final String ENTER = "<enter>";

    private static final long serialVersionUID = 3257283630358605881L;

    private PMDatePicker dateField = PMDatePicker.instanceWithLastQuoteDate();

    private JComboBox stockField;
    private JFormattedTextField bonusField = new JFormattedTextField(0f);

    private JFormattedTextField baseField = new JFormattedTextField(1f);

    private ButtonGroup bg = new ButtonGroup();

    private CompanyActionTableModel tableModel = new CompanyActionTableModel();

    private JTable demergerTable = UIHelper.createTable(tableModel);

    private JLabel labelDSB = createLabel("D/S/B/M Value");

    private JLabel labelDemerger = createLabel("New Entity Details");

    private JLabel labelParentEntity = createLabel("Parent Entity");
    private JComboBox parentStockField = UIHelper.createStockVOlistJCB();

    private JPanel dsbPanel = buildChildPanel(new JPanel());

    private JScrollPane demergerPanel;

    private JCheckBox cbPercentage = UIHelper.createCheckBox("% of face value", true);

    public CompanyAction() {
        super();
        init();

    }

    private void init() {
        buildPanel(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        ActionListener actionListener = new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                COMPANY_ACTION_TYPE action = COMPANY_ACTION_TYPE.valueOf(arg0
                        .getActionCommand());
                toggleDisplayDSBFields(action);
            }
        };

        add(createOptionPanel("Company Action", COMPANY_ACTION_TYPE.values(),
                Divident, bg, actionListener), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Ex-Date"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        add(dateField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Stock"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        stockField = UIHelper.createStocklistJCB();
        add(stockField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(labelDSB, gbc);
        add(labelDemerger, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        add(getDSBFields(), gbc);
        add(getDemergerTableDisplay(), gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        add(cbPercentage, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(labelParentEntity, gbc);
        gbc.gridx = 1;
        add(parentStockField, gbc);


        gbc.gridy++;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(getSubmitButton(), gbc);
        toggleDisplayDSBFields(Bonus);

    }

    private void toggleDisplayDSBFields(COMPANY_ACTION_TYPE action) {
        boolean showDSB = (action == Bonus || action == Divident || action == Split);
        boolean showDemerger = (action == Demerger);
        boolean showMerger = (action == Merger);

        labelDSB.setVisible(showDSB || showMerger);
        dsbPanel.setVisible(showDSB || showMerger);
        cbPercentage.setVisible(showDSB);

        labelDemerger.setVisible(showDemerger);
        demergerPanel.setVisible(showDemerger);

        labelParentEntity.setVisible(showMerger);
        parentStockField.setVisible(showMerger);
    }

    private JScrollPane getDemergerTableDisplay() {

        JTextField textField = new JTextField();
        FocusListener focusListener = new FocusListener() {
            public void focusLost(FocusEvent e) {
            }

            public void focusGained(FocusEvent e) {
                JTextField textField = (JTextField) e.getSource();
                if (textField.getText().equals(ENTER)) {
                    if (e.getSource() instanceof JFormattedTextField) {
                        ((JFormattedTextField) e.getSource())
                                .setValue((float) 0);
                    } else {
                        textField.setText("");
                    }
                }
            }
        };
        textField.addFocusListener(focusListener);
        textField.setBorder(new EmptyBorder(0, 0, 0, 0));
        DefaultCellEditor cellEditor = new DefaultCellEditor(textField);
        cellEditor.setClickCountToStart(1);
        demergerTable.getColumnModel().getColumn(0).setCellEditor(cellEditor);

        JFormattedTextField formattedTextField = new JFormattedTextField(0f);
        formattedTextField.addFocusListener(focusListener);

        formattedTextField.setBorder(new EmptyBorder(0, 0, 0, 0));
        DefaultCellEditor cellEditor2 = new DefaultCellEditor(
                formattedTextField);
        cellEditor2.setClickCountToStart(1);

        demergerTable.getColumnModel().getColumn(1).setCellEditor(cellEditor2);
        demergerPanel = new JScrollPane(demergerTable);
        demergerPanel.setPreferredSize(new Dimension(220, 100));
        return demergerPanel;
    }

    private Component getDSBFields() {
        dsbPanel.add(buildFloatField(bonusField, 0, 4, "Divident/Split/Bonus/Merger Share/Amount"));
        dsbPanel.add(createLabel("/"));
        dsbPanel.add(buildFloatField(baseField, 1, 4, "Per no. of Share"));
        return dsbPanel;
    }

    private boolean validateForm() {
        if (((Number) bonusField.getValue()).floatValue() == 0f) {
            UIHelper.displayInformation(null,
                    "Enter Divident/Split/Bonus/Merger Amount", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (((Number) baseField.getValue()).floatValue() == 0f) {
            UIHelper.displayInformation(null, "Enter Base value", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /*
      * (non-Javadoc)
      *
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object)
      */

    protected void doDisplay(Object retVal, String actionCommand) {
        if ((Boolean) retVal) {
            COMPANY_ACTION_TYPE action = COMPANY_ACTION_TYPE.valueOf(bg
                    .getSelection().getActionCommand());
            switch (action) {
                case Bonus:
                case Divident:
                case Split:
                case Merger:
                    bonusField.setValue(0f);
                    baseField.setValue(1f);
                    break;
                case Demerger:
                    tableModel = new CompanyActionTableModel();
                    demergerTable.setModel(tableModel);
                    tableModel.fireTableDataChanged();
            }
        }

    }

    /*
      * (non-Javadoc)
      *
      * @see pm.ui.AbstractPMPanel#getData()
      */

    protected Object getData(String actionCommand) {
        COMPANY_ACTION_TYPE action = COMPANY_ACTION_TYPE.valueOf(bg
                .getSelection().getActionCommand());
        try {
            CompanyActionVO actionVO = null;
            switch (action) {
                case Divident:
                case Split:
                case Bonus:
                    if (validateForm()) {
                        float bonus = ((Number) bonusField.getValue()).floatValue();
                        float base = ((Number) baseField.getValue()).floatValue();
                        actionVO = new CompanyActionVO(action, dateField.pmDate(),
                                stockField.getSelectedItem().toString(), bonus,
                                base);
                        actionVO.setPercentageValue(cbPercentage.isSelected());

                    }
                    break;
                case Demerger:
                    Vector<DemergerVO> demergerData = ((CompanyActionTableModel) demergerTable.getModel()).getData();
                    if (!validateFor100Percent(demergerData)) {
                        UIHelper.displayInformation(null, "Total book value of all the demerged entity should be 100", "Warning",
                                JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                    actionVO = new CompanyActionVO(action, dateField.pmDate(),
                            stockField.getSelectedItem().toString(), demergerData);
                    break;
                case Merger:
                    if (validateForm()) {
                        UIHelper.displayInformation(null, "Divident calculation will go wrong if there is a holding of " +
                                "tobe-Merged entity and divident has been issued during this period by either tobe-Merged or Parent entity", "Warning",
                                JOptionPane.WARNING_MESSAGE);

                        float bonus = ((Number) bonusField.getValue()).floatValue();
                        float base = ((Number) baseField.getValue()).floatValue();
                        actionVO = new CompanyActionVO(action, dateField.pmDate(),
                                stockField.getSelectedItem().toString(), bonus,
                                base, parentStockField.getSelectedItem().toString());

                    }
                    break;

            }
            if (actionVO != null)
                return Controller.doCompanyAction(actionVO);
        } catch (Exception e1) {
            logger.error(e1, e1);
            UIHelper.displayInformation(null, e1.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private boolean validateFor100Percent(Vector<DemergerVO> demergerData) {
        float tot = 0f;
        for (DemergerVO demergerVO : demergerData) {
            tot += demergerVO.getBookValueRatio();
        }
        return tot == 100f;
    }

    class CompanyActionTableModel extends AbstractTableModel {
        String[] columnName = {"StockCode", "BookValueRatio%"};

        Vector<DemergerVO> data = new Vector<DemergerVO>();

        {
            data.add(new DemergerVO());
        }

        public Vector<DemergerVO> getData() {
            return data;
        }

        public int getRowCount() {
            return data.size();
        }

        public int getColumnCount() {
            return columnName.length;
        }

        public String getColumnName(int arg0) {
            return columnName[arg0];
        }

        public Class<?> getColumnClass(int arg0) {
            return getValueAt(0, arg0).getClass();
        }

        public boolean isCellEditable(int arg0, int arg1) {
            return isNewRow(arg0);
        }

        public Object getValueAt(int arg0, int arg1) {
            if (isNewRow(arg0)) {
                switch (arg1) {
                    case 0:
                        if (data.elementAt(arg0).getNewStockCode() == null)
                            return new TableCellDisplay(ENTER);
                        else
                            return new TableCellDisplay(data.elementAt(arg0)
                                    .getNewStockCode());
                    case 1:
                        if (data.elementAt(arg0).getBookValueRatio() == 0)
                            return new TableCellDisplay(ENTER);
                        else
                            return new TableCellDisplay(data.elementAt(arg0)
                                    .getBookValueRatio(), -1);
                }
            } else {
                switch (arg1) {
                    case 0:
                        return new TableCellDisplay(data.elementAt(arg0)
                                .getNewStockCode());
                    case 1:
                        return new TableCellDisplay(data.elementAt(arg0)
                                .getBookValueRatio(), -1);
                }
            }
            return null;
        }

        private boolean isNewRow(int rowNumber) {
            return rowNumber == data.size() - 1;
        }

        public void setValueAt(Object arg0, int arg1, int arg2) {
            switch (arg2) {
                case 0:
                    if (!(arg0.toString().length() == 0))
                        data.elementAt(arg1).setNewStockCode(arg0.toString());
                    break;
                case 1:
                    data.elementAt(arg1).setBookValueRatio(
                            Float.parseFloat(arg0.toString()));
                    break;
            }
            if (data.elementAt(arg1).isComplete()) {
                data.add(new DemergerVO());
                fireTableRowsInserted(arg1 + 1, arg1 + 1);
            }
        }

    }

}
