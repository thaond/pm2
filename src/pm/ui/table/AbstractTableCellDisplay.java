package pm.ui.table;

import java.awt.*;

/**
 * @author Thiyagu
 * @version $Id: AbstractTableCellDisplay.java,v 1.2 2008/01/13 16:36:18 tpalanis Exp $
 * @since 01-Jan-2008
 */
public abstract class AbstractTableCellDisplay {
    // 1..n Customised
    protected boolean isBlank = false;
    protected boolean isTotalCell = false;

    public boolean isBlank() {
        return isBlank;
    }

    public abstract Comparable getValue();

    public abstract boolean isSpecialDisplay();

    public int compareTo(Object arg0) {
        if (arg0 instanceof AbstractTableCellDisplay) {

            if (this.isTotalCell) return 1;
            if (((AbstractTableCellDisplay) arg0).isTotalCell) return -1;

            if (this.isBlank()) return 1;
            if (((AbstractTableCellDisplay) arg0).isBlank()) return -1;

            return this.getValue().compareTo(((AbstractTableCellDisplay) arg0).getValue());
        } else {
            throw new ClassCastException("Both are not instance of table cell display");
        }
    }


    public boolean isTotalCell() {
        return isTotalCell;
    }

    public abstract Component getTableCellRendererComponent(int row);
}
