package pm.ui.table;

import pm.util.PMDate;

/**
 * @author Thiyagu
 * @version $Id: DateDisplayInput.java,v 1.1 2008/01/23 15:39:24 tpalanis Exp $
 * @since 16-Jan-2008
 */
public class DateDisplayInput extends TableDisplayInput {

    public DateDisplayInput(String columnName, String methodName) {
        super(columnName, methodName, null);
    }

    public AbstractTableCellDisplay createDisplayInstance(boolean isTotalCell, Object value) {
        if (value != null) return new TableCellDisplay((PMDate) value, isTotalCell);
        else return new TableCellDisplay(isTotalCell);
    }
}
