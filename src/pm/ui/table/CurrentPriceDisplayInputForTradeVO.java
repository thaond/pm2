package pm.ui.table;

import pm.vo.ConsolidatedTradeVO;
import pm.vo.EquityQuote;

public class CurrentPriceDisplayInputForTradeVO extends CurrentPriceDisplayInput {
    protected EquityQuote getQuote(Object data) {
        return ((ConsolidatedTradeVO) data).getCurrQuote();
    }
}
