package pm.vo;

import pm.util.PMDate;

public class EODStatistics {

    private StockVO stock;
    private PMDate date;
    private float high5D;
    private float high20D;
    private float high52Week;
    private float highLifeTime;
    private float low5D;
    private float low20D;
    private float low52Week;
    private float lowLifeTime;
    private float moving10DAverage;
    private float moving50DAverage;
    private float moving200DAverage;

    public EODStatistics() {
    }

    public StockVO getStock() {
        return stock;
    }

    public void setStock(StockVO stock) {
        this.stock = stock;
    }

    public PMDate getDate() {
        return date;
    }

    public void setDate(PMDate date) {
        this.date = date;
    }

    public float getHigh5D() {
        return high5D;
    }

    public void setHigh5D(float high5D) {
        this.high5D = high5D;
    }

    public float getHigh20D() {
        return high20D;
    }

    public void setHigh20D(float high20D) {
        this.high20D = high20D;
    }

    public float getHigh52Week() {
        return high52Week;
    }

    public void setHigh52Week(float high52Week) {
        this.high52Week = high52Week;
    }

    public float getHighLifeTime() {
        return highLifeTime;
    }

    public void setHighLifeTime(float highLifeTime) {
        this.highLifeTime = highLifeTime;
    }

    public float getLow5D() {
        return low5D;
    }

    public void setLow5D(float low5D) {
        this.low5D = low5D;
    }

    public float getLow20D() {
        return low20D;
    }

    public void setLow20D(float low20D) {
        this.low20D = low20D;
    }

    public float getLow52Week() {
        return low52Week;
    }

    public void setLow52Week(float low52Week) {
        this.low52Week = low52Week;
    }

    public float getLowLifeTime() {
        return lowLifeTime;
    }

    public void setLowLifeTime(float lowLifeTime) {
        this.lowLifeTime = lowLifeTime;
    }

    public float getMoving10DAverage() {
        return moving10DAverage;
    }

    public void setMoving10DAverage(float moving10DAverage) {
        this.moving10DAverage = moving10DAverage;
    }

    public float getMoving50DAverage() {
        return moving50DAverage;
    }

    public void setMoving50DAverage(float moving50DAverage) {
        this.moving50DAverage = moving50DAverage;
    }

    public float getMoving200DAverage() {
        return moving200DAverage;
    }

    public void setMoving200DAverage(float moving200DAverage) {
        this.moving200DAverage = moving200DAverage;
    }
}
