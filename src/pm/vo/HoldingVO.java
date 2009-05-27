/*
 * Created on Nov 24, 2004
 *
 */
package pm.vo;

/**
 * @author thiyagu1
 */
public class HoldingVO {
    String ticker;
    float qty;
    float totalCost;
    float profitLoss;
    float divident;


    public HoldingVO(String ticker, float qty, float totalCost, float profitLoss, float divident) {
        this.ticker = ticker;
        this.qty = qty;
        this.totalCost = totalCost;
        this.profitLoss = profitLoss;
        this.divident = divident;
    }

    /**
     * @return Returns the divident.
     */
    public float getDivident() {
        return divident;
    }

    /**
     * @param divident The divident to set.
     */
    public void setDivident(float divident) {
        this.divident = divident;
    }

    public float getProfitLoss() {
        return profitLoss;
    }

    public float getProfitLossIncDev() {
        return profitLoss + divident;
    }

    public float getQty() {
        return qty;
    }

    public String getTicker() {
        return ticker;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("HoldingVO - ").append(ticker).append(" ");
        sb.append(qty).append(" ");
        sb.append(totalCost).append(" ");
        sb.append(divident).append(" ");
        sb.append(profitLoss);
        return sb.toString();
    }
}
