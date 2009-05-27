/*
 * Created on Oct 19, 2004
 *
 */
package pm.vo;


/**
 * @author thiyagu1
 */
public class ConsolidatedTradeVO implements Comparable {

    private String stockCode;
    private float qty;
    private float cost;
    private float divident;
    private float profitLoss;
    private QuoteVO currQuote;


    /**
     * @param stockCode
     * @param qty
     * @param totCost
     * @param divident
     * @param totProfLoss
     */
    public ConsolidatedTradeVO(String stockCode, float qty, float totCost, float divident, float totProfLoss) {
        this.stockCode = stockCode;
        this.qty = qty;
        this.cost = totCost;
        this.divident = divident;
        this.profitLoss = totProfLoss;
    }

    public QuoteVO getCurrQuote() {
        return currQuote;
    }

    public void setCurrQuote(QuoteVO currQuote) {
        if (currQuote == null) currQuote = new QuoteVO();
        this.currQuote = currQuote;
    }

    public float getCost() {
        return cost;
    }

    public float getDivident() {
        return divident;
    }

    public float getProfitLoss() {
        return profitLoss;
    }

    public float getQty() {
        return qty;
    }

    public String getStockCode() {
        return stockCode;
    }

    public float getCostPerUnit() {
        return (cost == 0 || qty == 0) ? 0 : cost / qty;
    }

    public float getCurrentValue() {
        if (currQuote == null) return 0f;
        return currQuote.getLastPrice() * getQty();
    }

    /*
      * This method return current days profit or loss
      */
    public float getTodaysPL() {
        if (currQuote == null) return 0f;
        return currQuote.getLastPrice() * getQty() - currQuote.getPrevClose() * getQty();
    }

    public float getUnRealizedPL() {
        if (currQuote == null) return 0f;
        return getCurrQuote().getLastPrice() * getQty() - getCost();
    }

    public float getUnRealizedPLPer() {
        return getUnRealizedPL() / getCost() * 100;
    }

    public float getNetPL() {
        return getProfitLoss() + getDivident() + getUnRealizedPL();
    }

    /* (non-Javadoc)
      * @see java.lang.Comparable#compareTo(java.lang.Object)
      */
    public int compareTo(Object o) {
        ConsolidatedTradeVO target = (ConsolidatedTradeVO) o;
        return this.stockCode.compareTo(target.stockCode);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ConsolidatedTradeVO ->").append(stockCode);
        sb.append(",").append(qty);
        sb.append(",").append(cost);
        sb.append(",").append(divident);
        sb.append(",").append(profitLoss);
        sb.append(",").append(currQuote);
        return sb.toString();
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setDivident(float divident) {
        this.divident = divident;
    }

    public void setProfitLoss(float profitLoss) {
        this.profitLoss = profitLoss;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }


}
