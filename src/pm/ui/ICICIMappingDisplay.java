package pm.ui;

import pm.action.Controller;
import pm.ui.table.PMTableModel;
import pm.ui.table.StringDisplayInput;
import pm.ui.table.TableDisplayInput;
import pm.vo.ICICICodeMapping;
import pm.vo.StockVO;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ICICIMappingDisplay extends AbstractPMPanel {
    private JTable table;

    public ICICIMappingDisplay() {
        init();
    }

    private void init() {
        UIHelper.buildPanel(this);
        flagShowProgressBar = true;
        flagShowCancel = false;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 0;
        gbc.gridx = 0;
        this.add(iciciCodeMappings(), gbc);
        gbc.gridy++;
        this.add(getActionButton("Save"), gbc);
    }

    private Component iciciCodeMappings() {
        java.util.List<TableDisplayInput> displayInputs = new ArrayList<TableDisplayInput>();

        displayInputs.add(new StringDisplayInput("ICICICode", "getIciciCode"));
        displayInputs.add(new StringDisplayInput("StockCode", "getStockCode"));

        List<ICICICodeMapping> list = Controller.getIciciMappings();
        PMTableModel tableModel = new PMTableModel(list, displayInputs, false) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 1;
            }

            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {
                ((ICICICodeMapping) dataVOs.get(rowIndex)).setStock((StockVO) value);
            }
        };
        table = UIHelper.createTable(tableModel);
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(UIHelper.createStockVOlistJCB()));
        return UIHelper.createScrollPane(table);
    }

    @Override
    protected void doDisplay(Object retVal, String actionCommand) {
    }

    @Override
    protected Object getData(String actionCommand) {
        List<ICICICodeMapping> iciciCodeMappings = ((PMTableModel) table.getModel()).getDataVOs();
        Controller.saveOrUpdateICICICodeMappings(iciciCodeMappings);
        return null;
    }
}
