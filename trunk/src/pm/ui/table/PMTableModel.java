package pm.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Map;

/**
 * @author Thiyagu
 * @version $Id: PMTableModel.java,v 1.2 2008/01/23 15:39:24 tpalanis Exp $
 * @since 13-Jan-2008
 */
public class PMTableModel extends AbstractTableModel {

    protected final List dataVOs;

    private final List<TableDisplayInput> displayInputs;
    private final Map<String, Object> totalRow;
    private final boolean hasTotal;

    public PMTableModel(List dataVOs, List<TableDisplayInput> displayInputs, boolean hasTotal) {
        this.dataVOs = dataVOs;
        this.displayInputs = displayInputs;
        this.hasTotal = hasTotal;
        this.totalRow = null;
    }

    public PMTableModel(List dataVOs, List<TableDisplayInput> displayInputs, Map<String, Object> totalRow) {
        this.dataVOs = dataVOs;
        this.displayInputs = displayInputs;
        this.totalRow = totalRow;
        this.hasTotal = false;
    }

    public String getColumnName(int column) {
        return displayInputs.get(column).getColumnName();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public int getRowCount() {
        return dataVOs.size() + (totalRow != null ? 1 : 0);
    }

    public int getColumnCount() {
        return displayInputs.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        boolean isTotalRow = isTotalCell(rowIndex);
        if (isTotalRow) {
            Object data = totalRow.get(displayInputs.get(columnIndex).columnName);
            return displayInputs.get(columnIndex).createDisplayInstance(isTotalRow, data);
        } else {
            Object data = dataVOs.get(rowIndex);
            return displayInputs.get(columnIndex).display(data, isTotalRow);
        }
    }

    private boolean isTotalCell(int rowIndex) {
        return totalRow != null && rowIndex == dataVOs.size();
    }

    public Class<?> getColumnClass(int columnIndex) {
        if (getRowCount() > 0) return getValueAt(0, columnIndex).getClass();
        else return Object.class;
    }

    public List getDataVOs() {
        return dataVOs;
    }
}
