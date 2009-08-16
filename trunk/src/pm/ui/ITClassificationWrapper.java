package pm.ui;

import pm.util.PMDate;
import pm.vo.TradeVO;

public class ITClassificationWrapper {

    private TradeVO tradeVO;
    private boolean shortTerm;

    public ITClassificationWrapper(TradeVO tradeVO) {
        this.tradeVO = tradeVO;
        PMDate pDate = getPurchaseDate();
        shortTerm = getSaleDate().before(new PMDate(pDate.getDate(), pDate.getMonth(), pDate.getYear() + 1));
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

    public float getBrokerage() {
        return tradeVO.getBrokerage();
    }

    public float getTotalCost() {
        return tradeVO.getTotalCost();
    }

    public float getSaleValue() {
        return tradeVO.getSaleValue();
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

    public float getProfitLoss() {
        return tradeVO.getProfitLoss();
    }

    public boolean isShotTerm() {
        return shortTerm;
    }

}
