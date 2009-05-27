/*
 * Created on Nov 26, 2004
 *
 */
package pm.vo;

/**
 * @author thiyagu1
 */
public class WatchlistPerfVO {
    String ticker;
    float stPrice;
    float endPrice;

    public WatchlistPerfVO(String ticker, float stPrice, float endPrice) {
        this.ticker = ticker;
        this.stPrice = stPrice;
        this.endPrice = endPrice;
    }

    public float getEndPrice() {
        return endPrice;
    }

    public float getStPrice() {
        return stPrice;
    }

    public String getTicker() {
        return ticker;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("WatchlistPerfVO ").append(ticker).append(" ");
        sb.append(stPrice).append(" ").append(endPrice);
        return sb.toString();
    }
}
