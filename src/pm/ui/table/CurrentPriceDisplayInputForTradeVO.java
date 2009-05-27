package pm.ui.table;

import pm.vo.ConsolidatedTradeVO;
import pm.vo.QuoteVO;

public class CurrentPriceDisplayInputForTradeVO extends CurrentPriceDisplayInput {
    protected QuoteVO getQuote(Object data) {
        return ((ConsolidatedTradeVO) data).getCurrQuote();
    }
}
