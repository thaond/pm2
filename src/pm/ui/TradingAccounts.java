package pm.ui;

import pm.action.Controller;
import pm.util.enumlist.BROKERAGETYPE;
import pm.vo.TradingAccountVO;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.List;

public class TradingAccounts extends AbstractPMPanel {

    private JTextField createTextField = new JTextField();

    private JTable tradingAcList = UIHelper.createTable();

    private JComboBox brokerageComboBox;

    public TradingAccounts() {
        init();
    }

    private void init() {
        UIHelper.buildPanel(this);
        this.setBorder(UIHelper
                .createTitledEmptyBorder("Manage Trading Account"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        updateList();
        JScrollPane listScrollPane = new JScrollPane(tradingAcList);
        listScrollPane.setPreferredSize(new Dimension(300, 100));
        this.add(listScrollPane, gbc);
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        this.add(UIHelper.createLabel("Name"), gbc);
        gbc.gridx = 1;
        this.add(UIHelper.buildTextField(createTextField, "", 10), gbc);
        gbc.gridx = 2;
        brokerageComboBox = getBrokerageComboBox();
        this.add(brokerageComboBox, gbc);
        gbc.gridx = 3;
        this.add(getCreateButton(), gbc);
    }

    private JComboBox getBrokerageComboBox() {
        JComboBox comboBox = new JComboBox();
        for (BROKERAGETYPE brokeragetype : BROKERAGETYPE.values()) {
            comboBox.addItem(brokeragetype);
        }
        return comboBox;
    }

    private void updateList() {
        TradingAccountTableModel tableModel = new TradingAccountTableModel(Controller.getTradingAcVOList());
        tradingAcList.setModel(tableModel);
    }

    @Override
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal != null) {
            updateList();
        }

    }

    @Override
    protected Object getData(String actionCommand) {
        try {
            if (!createTextField.getText().equals("")) {
                String accountName = createTextField.getText();
                BROKERAGETYPE brokeragetype = (BROKERAGETYPE) brokerageComboBox.getSelectedItem();

                boolean status = Controller.saveTradingAc(new TradingAccountVO(accountName, brokeragetype));
                if (status) {
                    createTextField.setText("");
                    return status;
                }
            }
        } catch (Exception e1) {
            logger.error(e1, e1);
            UIHelper.displayInformation(createTextField, e1.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private JButton getCreateButton() {
        JButton createButton = UIHelper.createButton("Create");
        createButton.addActionListener(this);
        return createButton;
    }

}

class TradingAccountTableModel implements TableModel {

    private List<TradingAccountVO> tradingAcList;

    private String[] columnName = {"TradingAccount", "Brokerage"};

    public TradingAccountTableModel(List<TradingAccountVO> tradingAcList) {
        this.tradingAcList = tradingAcList;
    }

    public int getRowCount() {
        return tradingAcList.size();
    }

    public int getColumnCount() {
        return columnName.length;
    }

    public String getColumnName(int columnIndex) {
        return columnName[columnIndex];
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
//		if (columnIndex == 1)
//			return true;
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        TradingAccountVO accoutVO = tradingAcList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return accoutVO.getName();
            case 1:
                return accoutVO.getBrokeragetype();
        }
        return null;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        TradingAccountVO accoutVO = tradingAcList.get(rowIndex);
        switch (columnIndex) {
            case 1:
                accoutVO.setBrokeragetype((BROKERAGETYPE) aValue);
        }
    }

    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public void addTableModelListener(TableModelListener l) {

    }

    public void removeTableModelListener(TableModelListener l) {

    }

}
