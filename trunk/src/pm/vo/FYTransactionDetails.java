package pm.vo;

import java.util.List;

public class FYTransactionDetails {
    private List<TradeVO> transactions;
    private float divident;

    public FYTransactionDetails(List<TradeVO> transactions, float divident) {
        this.transactions = transactions;
        this.divident = divident;
    }

    public List<TradeVO> getTransactions() {
        return transactions;
    }

    public float getDivident() {
        return divident;
    }
}
