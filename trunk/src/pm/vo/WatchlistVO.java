/*
 * Created on Nov 16, 2004
 *
 */
package pm.vo;

import java.util.StringTokenizer;

/**
 * @author thiyagu1
 */
public class WatchlistVO implements Comparable {

    private final static String _DELIMITER = ",";
    private int id;
    private String stockCode;
    private float ceil = 0f;
    private float floor = 0f;
    private EquityQuote currQuote = null;
    private int watchlistGroupId;

    public WatchlistVO() {
    }

    public WatchlistVO(String stockCode, float ceil, float floor, int watchlistMasterId) {
        this.stockCode = stockCode;
        this.ceil = ceil;
        this.floor = floor;
        this.watchlistGroupId = watchlistMasterId;
    }

    public void setCeil(float ceil) {
        this.ceil = ceil;
    }

    public void setFloor(float floor) {
        this.floor = floor;
    }

    public WatchlistVO(String line) {
        StringTokenizer stk = new StringTokenizer(line, _DELIMITER);
        if (stk.hasMoreTokens()) {
            stockCode = stk.nextToken();
            if (stk.hasMoreTokens()) {
                ceil = Float.parseFloat(stk.nextToken());
            }
            if (stk.hasMoreTokens()) {
                floor = Float.parseFloat(stk.nextToken());
            }
        } else
            stockCode = line;
    }

    public float getCeil() {
        return ceil;
    }

    public float getFloor() {
        return floor;
    }

    public String getStockCode() {
        return stockCode;
    }

    public String toString() {
        return stockCode;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final WatchlistVO that = (WatchlistVO) o;

        if (Float.compare(that.ceil, ceil) != 0) return false;
        if (Float.compare(that.floor, floor) != 0) return false;
        if (watchlistGroupId != that.watchlistGroupId) return false;
        if (!stockCode.equals(that.stockCode)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = stockCode.hashCode();
        result = 29 * result + ceil != +0.0f ? Float.floatToIntBits(ceil) : 0;
        result = 29 * result + floor != +0.0f ? Float.floatToIntBits(floor) : 0;
        result = 29 * result + watchlistGroupId;
        return result;
    }

    /* (non-Javadoc)
      * @see java.lang.Comparable#compareTo(java.lang.Object)
      */

    public int compareTo(Object o) {
        if (o instanceof WatchlistVO)
            return hashCode() - ((WatchlistVO) o).hashCode();
        return -1;
    }

    public String getDetails() {
        StringBuffer sb = new StringBuffer();
        sb.append(stockCode).append(_DELIMITER);
        sb.append(ceil).append(_DELIMITER);
        sb.append(floor);
        return sb.toString();
    }

    public EquityQuote getCurrQuote() {
        return currQuote;
    }

    public void setCurrQuote(EquityQuote currQuote) {
        this.currQuote = currQuote;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWatchlistGroupId() {
        return watchlistGroupId;
    }

    public void setWatchlistGroupId(int watchlistGroupId) {
        this.watchlistGroupId = watchlistGroupId;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

}
