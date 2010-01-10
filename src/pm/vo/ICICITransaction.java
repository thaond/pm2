package pm.vo;

import pm.util.AppConst;
import pm.util.PMDate;
import pm.util.enumlist.ICICITransactionStatus;

public class ICICITransaction extends TransactionVO {

    private String orderRef;
    private String iciciCode;
    private ICICITransactionStatus status;

    public ICICITransaction() {

    }

    public ICICITransaction(PMDate date, String iciciCode, AppConst.TRADINGTYPE action, float qty, float price, float brokerage, boolean dayTrading, String orderRef) {
        super(date, null, action, qty, price, brokerage, null, null, dayTrading);
        this.iciciCode = iciciCode;
        this.orderRef = orderRef;
        this.status = ICICITransactionStatus.Pending;
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


    public ICICITransactionStatus getStatus() {
        return status;
    }

    public void setStatus(ICICITransactionStatus status) {
        this.status = status;
    }

    public void mapTransaction(String portfolio) {
        super.setPortfolio(portfolio);
        this.status = ICICITransactionStatus.AutoMapped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ICICITransaction that = (ICICITransaction) o;

        if (iciciCode != null ? !iciciCode.equals(that.iciciCode) : that.iciciCode != null) return false;
        if (orderRef != null ? !orderRef.equals(that.orderRef) : that.orderRef != null) return false;
        if (status != that.status) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (orderRef != null ? orderRef.hashCode() : 0);
        result = 31 * result + (iciciCode != null ? iciciCode.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + " iciciCode = " + iciciCode +
                " orderRef = " + orderRef;
    }
}
