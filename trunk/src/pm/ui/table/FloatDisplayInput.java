package pm.ui.table;

/**
 * @author Thiyagu
 * @version $Id: FloatDisplayInput.java,v 1.1 2008/01/23 15:39:24 tpalanis Exp $
 * @since 16-Jan-2008
 */
public class FloatDisplayInput extends TableDisplayInput {

    public FloatDisplayInput(String columnName, String methodName) {
        super(columnName, methodName, null);
    }

    public AbstractTableCellDisplay createDisplayInstance(boolean isTotalCell, Object value) {
        return new TableCellDisplay((Float) value, isTotalCell);
    }
}
