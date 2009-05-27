package pm.ui;

import pm.action.Controller;
import pm.datamining.vo.SupportResistanceVO;
import static pm.ui.UIHelper.*;
import pm.ui.table.TableCellDisplay;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Vector;

public class DisplaySupportResistance extends AbstractSplitPanel {

    private static final long serialVersionUID = 1L;

    private static final int SPLITHEIGHT = 50;
    private JPanel bottomPanel;
    private JComboBox stockField;
    private JFormattedTextField weightageField = new JFormattedTextField(NumberFormat.getNumberInstance());
    private JFormattedTextField diffField = new JFormattedTextField(NumberFormat.getNumberInstance());

    public DisplaySupportResistance() {
        init();
    }

    protected Component buildTopPanel() {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, SPLITHEIGHT));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, SPLITHEIGHT));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 20, 2, 2);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Stock"), gbc);
        gbc.gridx = 1;
        stockField = UIHelper.createStocklistJCB();
        panel.add(stockField, gbc);
        gbc.gridx = 2;
        panel.add(createLabel("Correction level"), gbc);
        gbc.gridx = 3;
        buildFloatField(diffField, 0f, 5, "Correction level");
        panel.add(diffField, gbc);
        gbc.gridx = 4;
        panel.add(createLabel("Minimum Weightage"), gbc);
        gbc.gridx = 5;
        buildFloatField(weightageField, 0f, 5, "Minimum Weightage");
        panel.add(weightageField, gbc);
        gbc.gridx = 6;
        panel.add(getSubmitButton(), gbc);

        return panel;
    }

    @Override
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal != null) {
            bottomPanel = new JPanel(new GridLayout(1, 1));
            buildChildPanel(bottomPanel);
            bottomPanel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - SPLITHEIGHT));
            DSRTableModel model = new DSRTableModel((Vector<SupportResistanceVO>) retVal);
            JTable table = new JTable(model);
            buildTableWithAppDefaultRenderer(table);
            JScrollPane editorScrollPane = new JScrollPane(table);
            bottomPanel.add(editorScrollPane);
            splitPane.setBottomComponent(bottomPanel);
        }
    }

    @Override
    protected Object getData(String actionCommand) {
        try {
            float weight = ((Number) weightageField.getValue()).floatValue();
            float diff = ((Number) diffField.getValue()).floatValue();
            Vector<SupportResistanceVO> vos = Controller.getSupportResistance(stockField.getSelectedItem().toString(), diff, weight);
            return vos;
        } catch (Exception e) {
            logger.error(e, e);
            UIHelper.displayInformation(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

}

class DSRTableModel extends AbstractTableModel {
    private Vector<SupportResistanceVO> vData;
    private String[] colName = {"Lower Limit", "Average", "Upper Limit", "# of Support", "# of Resistance", "Total"};

    public DSRTableModel(Vector<SupportResistanceVO> data) {
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
        SupportResistanceVO supportResistanceVO = vData.elementAt(rowIndex);
        switch (columnIndex) {
            case 0:
                return new TableCellDisplay(supportResistanceVO.getLowLimit(), 0);
            case 1:
                return new TableCellDisplay(supportResistanceVO.getPrice(), 0);
            case 2:
                return new TableCellDisplay(supportResistanceVO.getUpperLimit(), 0);
            case 3:
                return new TableCellDisplay(supportResistanceVO.getSupportOccurrence(), 0);
            case 4:
                return new TableCellDisplay(supportResistanceVO.getResistanceOccurrence(), 0);
            case 5:
                return new TableCellDisplay(supportResistanceVO.getWeightage(), 0);
        }
        return null;
    }

    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
