package pm.ui.table;

import pm.ui.MouseListenerFGAndCursorChange;
import pm.ui.PortfolioManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Thiyagu
 * @version $Id: PMTableMouseListener.java,v 1.2 2008/01/13 16:36:18 tpalanis Exp $
 * @since 31-Dec-2007
 */
public class PMTableMouseListener extends MouseListenerFGAndCursorChange {

    public PMTableMouseListener(Color offFocusColor, Color onFocusColor) {
        super(offFocusColor, onFocusColor);
    }

    public void mouseClicked(MouseEvent e) {
        if (isValidArea(e)) {
            StockCodeDisplay display = getStockCodeDisplay((JTable) e.getSource());
            if (!display.isTotalCell()) PortfolioManager.gotoEODChart(display.toString());
        }
    }

    @Override
    protected boolean isValidArea(MouseEvent e) {
        if (e.getSource() instanceof JTable) {
            return getStockCodeDisplay((JTable) e.getSource()) != null;
        }
        return false;
    }

    StockCodeDisplay getStockCodeDisplay(JTable table) {
        StockCodeDisplay display = null;
        int column = table.getSelectedColumn();
        int row = table.getSelectedRow();
        if (column >= 0 && row >= 0) {
            Object at = table.getValueAt(row, column);
            if (at instanceof StockCodeDisplay) {
                display = (StockCodeDisplay) at;
            }
        }
        return display;
    }
}
