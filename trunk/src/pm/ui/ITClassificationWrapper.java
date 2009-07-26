package pm.ui;

import pm.util.PMDate;
import pm.vo.TradeVO;

public class ITClassificationWrapper {

    private TradeVO tradeVO;

    public ITClassificationWrapper(TradeVO tradeVO) {
        this.tradeVO = tradeVO;
    }

    public PMDate getPurchaseDate() {
        return tradeVO.getPurchaseDate();
    }

    public PMDate getSaleDate() {
        return tradeVO.getSaleDate();
    }

    public String getStockCode() {
        return tradeVO.getStockCode();
    }

    public float getQty() {
        return tradeVO.getQty();
    }

    public String getPortfolio() {
        return tradeVO.getPortfolio();
    }

    public String getTradingAc() {
        return tradeVO.getTradingAc();
    }

    public float getBrokerage() {
        return tradeVO.getBrokerage();
    }

    public float getPurchasePrice() {
        return tradeVO.getPurchasePrice();
    }

    public float getSalePrice() {
        return tradeVO.getSalePrice();
    }

    public float getSTPL() {
        if (isShotTerm()) {
            return tradeVO.getProfitLoss();
        }
        return 0f;
    }

    public float getLTPL() {
        if (isShotTerm()) {
            return 0f;
        }
        return tradeVO.getProfitLoss();
    }

    private boolean isShotTerm() {
        PMDate pDate = getPurchaseDate();
        return getSaleDate().before(new PMDate(pDate.getDate(), pDate.getMonth(), pDate.getYear() + 1));
    }

}
