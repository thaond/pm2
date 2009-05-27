/*
 * Created on Nov 24, 2004
 *
 */
package pm.vo;

import pm.util.PMDate;

/**
 * @author thiyagu1
 */
public class PerformanceVO {
    private PMDate date;
    private float cost;
    private float marketValue;
    private float profitLoss;

    public PerformanceVO(PMDate date, float cost, float marketValue, float profit) {
        this.date = date;
        this.cost = cost;
        this.marketValue = marketValue;
        this.profitLoss = profit;
    }

    public float getCost() {
        return cost;
    }

    public PMDate getDate() {
        return date;
    }

    public float getMarketValue() {
        return marketValue;
    }

    public float getProfitLoss() {
        return profitLoss;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(date).append(" ").append(cost).append(" ").append(marketValue);
        sb.append(" ").append(profitLoss).append("\n");
        return sb.toString();
    }
}
