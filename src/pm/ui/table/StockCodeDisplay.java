package pm.ui.table;

import pm.ui.UIHelper;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Thiyagu
 * @version $Id: StockCodeDisplay.java,v 1.3 2008/01/23 15:39:24 tpalanis Exp $
 * @since 01-Jan-2008
 */
public class StockCodeDisplay extends AbstractTableCellDisplay implements Comparable {

    private String stockCode;
    public static final String TOTAL = "Total";

    public StockCodeDisplay(String stockCode) {
        this.stockCode = stockCode;
    }

    public StockCodeDisplay(String stockCode, Boolean isTotalCell) {
        this.stockCode = stockCode;
        this.isTotalCell = isTotalCell;
        if (isTotalCell) this.stockCode = TOTAL;
    }

    public Comparable getValue() {
        return stockCode;
    }

    public boolean isSpecialDisplay() {
        return true;
    }

    public Component getTableCellRendererComponent(int row) {
        DefaultTableCellRenderer cell = new DefaultTableCellRenderer();
        cell.setText(stockCode);
        cell.setFont(UIHelper.FONT_STOCKCODE_DISPLAY);
        cell.setForeground(Color.blue);
        if (isTotalCell) TotalRowColorHelper.setColor(cell);
        else AlternateRowColorHelper.setColor(cell, row);
        return cell;
    }

    public String toString() {
        return stockCode;
    }
}
