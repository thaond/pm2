package builder;

import pm.util.AppConst;
import pm.util.PMDate;
import pm.vo.TransactionVO;

import static pm.util.AppConst.TRADINGTYPE.Buy;

public class TransactionBuilder {
    private PMDate date = new PMDate();
    private String stockCode = "CODE1";
    private AppConst.TRADINGTYPE tradingtype = Buy;
    private float qty = 10f;
    private float price = 12f;
    private float brokerage = 12.2f;
    private String portfolio = "PortfolioName";
    private String tradingAc = "TradingACCName";
    private boolean dayTrading = false;
    private int id = -1;

    public TransactionVO build() {
        TransactionVO transactionVO = new TransactionVO(date, stockCode, tradingtype, qty, price, brokerage, portfolio, tradingAc, dayTrading);
        if (id != -1) {
            transactionVO.setId(id);
        }
        return transactionVO;
    }

    public TransactionBuilder withDate(PMDate date) {
        this.date = date;
        return this;
    }

    public TransactionBuilder withStockCode(String stockCode) {
        this.stockCode = stockCode;
        return this;
    }

    public TransactionBuilder withQty(int qty) {
        this.qty = qty;
        return this;
    }

    public TransactionBuilder withPortfolio(String portfolio) {
        this.portfolio = portfolio;
        return this;
    }

    public TransactionBuilder withTradingAc(String tradingAc) {
        this.tradingAc = tradingAc;
        return this;
    }

    public TransactionBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public TransactionBuilder withAction(AppConst.TRADINGTYPE action) {
        this.tradingtype = action;
        return this;
    }
}
