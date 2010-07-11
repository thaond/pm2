package pm.ui.table;

/**
 * @author Thiyagu
 * @version $Id: StockCodeDisplayInput.java,v 1.1 2008/01/23 15:39:24 tpalanis Exp $
 * @since 13-Jan-2008
 */
public class StockCodeDisplayInput extends TableDisplayInput {

    public StockCodeDisplayInput() {
        super("StockCode", "getStockCode", StockCodeDisplay.class);
    }

    public AbstractTableCellDisplay createDisplayInstance(boolean isTotalCell, Object value) {
        return new StockCodeDisplay((String) value, isTotalCell);
    }
}
