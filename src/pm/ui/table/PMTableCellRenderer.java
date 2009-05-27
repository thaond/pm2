/*
 * Created on Dec 21, 2004
 *
 */
package pm.ui.table;

import pm.ui.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PMTableCellRenderer extends DefaultTableCellRenderer {

    public PMTableCellRenderer() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component retVal;
        if (value instanceof AbstractTableCellDisplay) {
            AbstractTableCellDisplay display = (AbstractTableCellDisplay) value;
            if (display.isSpecialDisplay()) { //Get the special display
                retVal = display.getTableCellRendererComponent(row);
            } else { // call default renderer
                retVal = getDefaultRenderer(table, value, isSelected, hasFocus, row, column);
                if (display.isTotalCell()) TotalRowColorHelper.setColor(retVal);
            }
        } else {
            retVal = getDefaultRenderer(table, value, isSelected, hasFocus, row, column);
        }
        if (table.getModel().isCellEditable(row, column))
            retVal.setBackground(UIHelper.COLOR_TABLE_EDITABLE_CELL);
        return retVal;
    }

    private Component getDefaultRenderer(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component retVal = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        AlternateRowColorHelper.setColor(retVal, row);
        return retVal;
    }
}

