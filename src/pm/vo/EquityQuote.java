/*
 * Created on 17-Feb-2005
 *
 */
package pm.vo;

import pm.util.PMDate;

/**
 * @author thiyagu1
 */
public class EquityQuote extends Quote {

    private float prevClose;

    private float tradeValue;

    private float perDeliveryQty;

    private float adjustedClose;

    private StringBuffer pickDetails = new StringBuffer();

    private float scoreCard;

    public EquityQuote() {

    }

    public EquityQuote(String stockCode, PMDate date, float open, float high,
                       float low, float lastPrice, float volume, float prevClose,
                       float tradeValue, float perDeliveryQty) {
        super(date, high, lastPrice, low, open, volume, new StockVO(stockCode));
        this.prevClose = prevClose;
        this.tradeValue = tradeValue;
        this.perDeliveryQty = perDeliveryQty;
    }

    public float getAvgTradePrice() {
        return tradeValue / volume;
    }

    /**
     * @return Returns the scoreCard.
     */
    public float getScoreCard() {
        return scoreCard;
    }

    /**
     * @param scoreCard The scoreCard to set.
     */
    public void updateScoreCard(float scoreCard) {
        this.scoreCard += scoreCard;
    }

    public void resetScoreCard() {
        this.scoreCard = 0;
    }

    /**
     * @param stockCode
     */
    public EquityQuote(String stockCode) {
        this.stockVO = new StockVO(stockCode);
    }

    /**
     * @return Returns the lastPrice.
     */
    public float getClose() {
        return lastPrice;
    }

    /**
     * @return Returns the prevClose.
     */
    public float getPrevClose() {
        return prevClose;
    }

    /**
     * @param prevClose The prevClose to set.
     */
    public void setPrevClose(float prevClose) {
        this.prevClose = prevClose;
    }

    /**
     * @return Returns the stockCode.
     */
    public String getStockCode() {
        return (stockVO != null ? this.stockVO.getStockCode() : null);
    }

    /**
     * @param stockCode The stockCode to set.
     */
    public void setStockCode(String stockCode) {
        if (stockVO == null) {
            this.stockVO = new StockVO();
        }
        this.stockVO.setStockCode(stockCode);
    }

    public void resetPick() {
        pickDetails = new StringBuffer();
    }

    public void addPickDetail(String str) {
        pickDetails.append(str);
        pickDetails.append(" ");
    }

    public String getPickDetails() {
        return pickDetails.toString();
    }

    /**
     * @return Returns the perDeliveryQty.
     */
    public float getPerDeliveryQty() {
        return perDeliveryQty;
    }

    /**
     * @param perDeliveryQty The perDeliveryQty to set.
     */
    public void setPerDeliveryQty(float perDeliveryQty) {
        this.perDeliveryQty = perDeliveryQty;
    }

    /**
     * @return Returns the tradeValue.
     */
    public float getTradeValue() {
        return tradeValue;
    }

    /**
     * @param tradeValue The tradeValue to set.
     */
    public void setTradeValue(float tradeValue) {
        this.tradeValue = tradeValue;
    }

    /**
     * @param when
     */
    public boolean after(PMDate when) {
        return date != null && date.after(when);
    }

    /**
     * @param when
     */
    public boolean before(PMDate when) {
        return date != null && date.before(when);
    }

    public boolean dateEquals(Object obj) {
        return date.equals(obj);
    }

    public float getPerChange() {
        if (prevClose != 0) {
            return (lastPrice - prevClose) / prevClose * 100;
        }
        return 0;
    }

    /*
      * (non-Javadoc)
      *
      * @see java.lang.Object#hashCode()
      */

    @Override
    public int hashCode() {
        int i1 = date.hashCode() << 16;
        int i2 = stockVO.getStockCode().hashCode() >> 16;
        return i1 + i2;
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EquityQuote quoteVO = (EquityQuote) o;

        if (Float.compare(quoteVO.high, high) != 0) {
            return false;
        }
        if (Float.compare(quoteVO.lastPrice, lastPrice) != 0) {
            return false;
        }
        if (Float.compare(quoteVO.low, low) != 0) {
            return false;
        }
        if (Float.compare(quoteVO.open, open) != 0) {
            return false;
        }
        if (Float.compare(quoteVO.perDeliveryQty, perDeliveryQty) != 0) {
            return false;
        }
        if (Float.compare(quoteVO.prevClose, prevClose) != 0) {
            return false;
        }
        if (Float.compare(quoteVO.scoreCard, scoreCard) != 0) {
            return false;
        }
        if (Float.compare(quoteVO.tradeValue, tradeValue) != 0) {
            return false;
        }
        if (Float.compare(quoteVO.volume, volume) != 0) {
            return false;
        }
        if (!date.equals(quoteVO.date)) {
            return false;
        }
        if (pickDetails != null ? !pickDetails.toString().equals(quoteVO.pickDetails.toString()) : quoteVO.pickDetails != null) {
            return false;
        }
        if (!stockVO.equals(quoteVO.stockVO)) {
            return false;
        }

        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Quote -> ");
        sb.append("StockCode:").append(stockVO.getStockCode()).append(" ");
        sb.append("PMDate:").append(date).append(" ");
        sb.append("lastPrice:").append(lastPrice).append(" ");
        sb.append("open:").append(open).append(" ");
        sb.append("high:").append(high).append(" ");
        sb.append("low:").append(low).append(" ");
        sb.append("prevClose:").append(prevClose).append(" ");
        sb.append("volume:").append(volume).append(" ");
        sb.append("tradeValue:").append(tradeValue).append(" ");
        sb.append("perDeliveryQty:").append(perDeliveryQty).append(" ");
        sb.append("pickDetails:").append(pickDetails);
        return sb.toString();
    }

    public void applyPriceFactor(float priceFactor) {
        open *= priceFactor;
        high *= priceFactor;
        low *= priceFactor;
        lastPrice *= priceFactor;
        volume /= priceFactor;
        prevClose *= priceFactor;

    }

    public void applyPreviousDayPriceFactor(float priceFactor) {
        prevClose *= priceFactor;
    }

    public int getDateVal() {
        return date.getIntVal();
    }

    public void setDateVal(int val) {
        date = new PMDate(val);
    }

    public float getAdjustedClose() {
        return adjustedClose;
    }

    public void setAdjustedClose(float adjustedClose) {
        this.adjustedClose = adjustedClose;
    }
}
