package pm.tools;

import pm.util.AppConst.TRADINGTYPE;
import pm.util.PMDate;
import pm.vo.TradingAccountVO;

public class BrokerageCalculator {

    public float getBrokerage(TradingAccountVO tradingAc, TRADINGTYPE tradingType,
                              PMDate date, float qty, float price, boolean dayTrading) {

        if (qty == 0f || price == 0f || tradingType == TRADINGTYPE.IPO) return 0f;

        return tradingAc.getBrokeragetype().calculateBrokerage(tradingType, date, qty, price, dayTrading);
    }

}
