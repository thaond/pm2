package pm.ui.table;

import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author Thiyagu
 * @version $Id: TableDisplayInput.java,v 1.2 2008/01/23 15:39:24 tpalanis Exp $
 * @since 13-Jan-2008
 */
public class TableDisplayInput {

    private static Logger logger = Logger.getLogger(TableDisplayInput.class);

    protected final String columnName;
    protected final String methodName;
    protected final Class<AbstractTableCellDisplay> displayClass;

    public TableDisplayInput(String columnName, String methodName, Class displayClass) {
        this.columnName = columnName;
        this.methodName = methodName;
        this.displayClass = displayClass;
    }

    public AbstractTableCellDisplay display(Object data, boolean isTotalCell) {
        AbstractTableCellDisplay retVal = null;
        try {
            Method method = data.getClass().getMethod(methodName);
            Object value = method.invoke(data);
            retVal = createDisplayInstance(isTotalCell, value);
        } catch (Exception e) {
            logger.error(e, e);
        }
        return retVal;
    }

    public AbstractTableCellDisplay createDisplayInstance(boolean isTotalCell, Object value) {
        try {
            if (value != null) {
                Constructor<AbstractTableCellDisplay> constructor = displayClass.getConstructor(value.getClass(), Boolean.class);
                return constructor.newInstance(value, isTotalCell);
            } else {
                Constructor<AbstractTableCellDisplay> constructor = displayClass.getConstructor(Boolean.class);
                return constructor.newInstance(isTotalCell);
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
        return null;
    }

    public String getColumnName() {
        return columnName;
    }
}
