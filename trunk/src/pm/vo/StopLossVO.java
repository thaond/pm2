/*
 * Created on Feb 26, 2005
 *
 */
package pm.vo;

/**
 * @author pn
 */
public class StopLossVO {
    private int id;
    private String stockCode;
    private float stopLoss1;
    private float stopLoss2;
    private float target1;
    private float target2;
    private QuoteVO quoteVO;
    private String portfolioName;

    public StopLossVO() {
    }

    public StopLossVO(String stockCode, float stopLoss1, float stopLoss2, float target1, float target2, String portfolioName) {
        this.stockCode = stockCode;
        this.stopLoss1 = stopLoss1;
        this.stopLoss2 = stopLoss2;
        this.target1 = target1;
        this.target2 = target2;
        this.portfolioName = portfolioName;
    }

    /**
     * @return Returns the quoteVO.
     */
    public QuoteVO getQuoteVO() {
        return quoteVO;
    }

    /**
     * @param quoteVO The quoteVO to set.
     */
    public void setQuoteVO(QuoteVO quoteVO) {
        this.quoteVO = quoteVO;
    }

    public StopLossVO(String stockCode) {
        this.stockCode = stockCode;
    }


    /**
     * @return Returns the stopLoss1.
     */
    public float getStopLoss1() {
        return stopLoss1;
    }

    /**
     * @param stopLoss1 The stopLoss1 to set.
     */
    public void setStopLoss1(float stopLoss1) {
        this.stopLoss1 = stopLoss1;
    }

    /**
     * @return Returns the stopLoss2.
     */
    public float getStopLoss2() {
        return stopLoss2;
    }

    /**
     * @param stopLoss2 The stopLoss2 to set.
     */
    public void setStopLoss2(float stopLoss2) {
        this.stopLoss2 = stopLoss2;
    }

    /**
     * @return Returns the target1.
     */
    public float getTarget1() {
        return target1;
    }

    /**
     * @param target1 The target1 to set.
     */
    public void setTarget1(float target1) {
        this.target1 = target1;
    }

    /**
     * @return Returns the target2.
     */
    public float getTarget2() {
        return target2;
    }

    /**
     * @param target2 The target2 to set.
     */
    public void setTarget2(float target2) {
        this.target2 = target2;
    }

    /**
     * @return Returns the stockCode.
     */
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public boolean isSet() {
        return stopLoss1 != 0 && stopLoss2 != 0 && target1 != 0 && target2 != 0;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final StopLossVO that = (StopLossVO) o;

        if (Float.compare(that.stopLoss1, stopLoss1) != 0) return false;
        if (Float.compare(that.stopLoss2, stopLoss2) != 0) return false;
        if (Float.compare(that.target1, target1) != 0) return false;
        if (Float.compare(that.target2, target2) != 0) return false;
        if (!portfolioName.equals(that.portfolioName)) return false;
        if (!stockCode.equals(that.stockCode)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = stockCode.hashCode();
        result = 29 * result + stopLoss1 != +0.0f ? Float.floatToIntBits(stopLoss1) : 0;
        result = 29 * result + stopLoss2 != +0.0f ? Float.floatToIntBits(stopLoss2) : 0;
        result = 29 * result + target1 != +0.0f ? Float.floatToIntBits(target1) : 0;
        result = 29 * result + target2 != +0.0f ? Float.floatToIntBits(target2) : 0;
        result = 29 * result + (portfolioName != null ? portfolioName.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
