package pm.vo;

import pm.util.PMDate;

public class Quote {
    protected StockVO stockVO;
    protected PMDate date;
    protected float open;
    protected float high;
    protected float low;
    protected float lastPrice;
    protected float volume;

    public Quote() {
    }

    public Quote(PMDate date, float high, float lastPrice, float low, float open, float volume, StockVO stockVO) {
        this.date = date;
        this.high = high;
        this.lastPrice = lastPrice;
        this.low = low;
        this.open = open;
        this.volume = volume;
        this.stockVO = stockVO;
    }

    public PMDate getDate() {
        return date;
    }

    public void setDate(PMDate date) {
        this.date = date;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(float lastPrice) {
        this.lastPrice = lastPrice;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public StockVO getStockVO() {
        return stockVO;
    }

    public void setStockVO(StockVO stockVO) {
        this.stockVO = stockVO;
    }
}
