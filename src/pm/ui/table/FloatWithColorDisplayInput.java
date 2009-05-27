package pm.ui.table;

/**
 * @author Thiyagu
 * @version $Id: FloatWithColorDisplayInput.java,v 1.1 2008/01/23 15:39:24 tpalanis Exp $
 * @since 13-Jan-2008
 */
public class FloatWithColorDisplayInput extends TableDisplayInput {

    public FloatWithColorDisplayInput(String columnName, String methodName) {
        super(columnName, methodName, null);
    }

    public AbstractTableCellDisplay createDisplayInstance(boolean isTotalCell, Object value) {
        if (value == null) return TableCellDisplay.EMPTYCELL;
        return new TableCellDisplay((Float) value, 1, isTotalCell);
    }
}
