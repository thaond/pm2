package pm.vo;

/**
 * Date: Sep 16, 2006
 * Time: 12:32:08 PM
 */
public class TransactionMapping {

    private int id;
    private int buyId;
    private int sellId;
    private float qty;


    public TransactionMapping() {
    }

    public TransactionMapping(int buyId, int sellId, float qty) {
        this.buyId = buyId;
        this.sellId = sellId;
        this.qty = qty;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuyId() {
        return buyId;
    }

    public void setBuyId(int buyId) {
        this.buyId = buyId;
    }

    public int getSellId() {
        return sellId;
    }

    public void setSellId(int sellId) {
        this.sellId = sellId;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionMapping that = (TransactionMapping) o;

        if (buyId != that.buyId) return false;
        if (Float.compare(that.qty, qty) != 0) return false;
        if (sellId != that.sellId) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = buyId;
        result = 31 * result + sellId;
        result = 31 * result + qty != +0.0f ? Float.floatToIntBits(qty) : 0;
        return result;
    }
}


