package pm.ui.table;

import pm.util.Helper;
import pm.vo.EquityQuote;

/**
 * @author Thiyagu
 * @version $Id: CurrentPriceDisplayInput.java,v 1.2 2008/01/23 15:39:24 tpalanis Exp $
 * @since 13-Jan-2008
 */
public class CurrentPriceDisplayInput extends TableDisplayInput {

    public CurrentPriceDisplayInput() {
        super("CurrPrice", null, TableCellDisplay.class);
    }

    public AbstractTableCellDisplay display(Object data, boolean isTotalCell) {
        if (isTotalCell) return new TableCellDisplay("", isTotalCell);
        EquityQuote quoteVO = getQuote(data);
        float perChange = quoteVO != null ? quoteVO.getPerChange() : 0f;
        float lastPrice = quoteVO != null ? quoteVO.getLastPrice() : 0f;
        String dispStr = Helper.formatFloat(lastPrice) + "  "
                + Helper.formatFloat(perChange) + "%";
        return new TableCellDisplay(perChange, 2, dispStr);
    }

    protected EquityQuote getQuote(Object data) {
        EquityQuote quoteVO = (EquityQuote) data;
        return quoteVO;
    }
}

