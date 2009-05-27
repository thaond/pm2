package pm.vo;

import pm.util.AppConst;
import pm.util.PMDate;

public class ICICITransaction extends TransactionVO {

    private String orderRef;
    private String iciciCode;

    public ICICITransaction() {

    }

    public ICICITransaction(PMDate date, String iciciCode, AppConst.TRADINGTYPE action, float qty, float price, float brokerage, boolean dayTrading, String orderRef) {
        super(date, null, action, qty, price, brokerage, null, null, dayTrading);
        this.iciciCode = iciciCode;
        this.orderRef = orderRef;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public String getIciciCode() {
        return iciciCode;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public void setIciciCode(String iciciCode) {
        this.iciciCode = iciciCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ICICITransaction that = (ICICITransaction) o;

        if (!iciciCode.equals(that.iciciCode)) return false;
        if (!orderRef.equals(that.orderRef)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + orderRef.hashCode();
        result = 31 * result + iciciCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + " iciciCode = " + iciciCode +
                " orderRef = " + orderRef;
    }
}
