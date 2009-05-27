package pm.ui.table;

/**
 * @author Thiyagu
 * @version $Id: ToStringDisplayInput.java,v 1.1 2008/01/23 15:39:25 tpalanis Exp $
 * @since 16-Jan-2008
 */
public class ToStringDisplayInput extends TableDisplayInput {

    public ToStringDisplayInput(String columnName, String methodName) {
        super(columnName, methodName, null);
    }

    public AbstractTableCellDisplay createDisplayInstance(boolean isTotalCell, Object value) {
        String data = value != null ? value.toString() : null;
        return new TableCellDisplay(data, isTotalCell);
    }
}