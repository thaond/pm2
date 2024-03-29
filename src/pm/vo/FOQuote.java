package pm.vo;

import pm.util.PMDate;
import pm.util.enumlist.FOTYPE;


public class FOQuote extends Quote {

    private PMDate expiryDate;
    private Integer openInterest;
    private Integer changeInOpenInterest;
    private FOTYPE fotype;
    private Float strikePrice;

    public FOQuote() {
    }

    public FOQuote(PMDate date, StockVO stockVO, FOTYPE fotype, float open, float high, float low, float lastPrice, float volume,
                   Integer openInterest, Integer changeInOpenInterest, Float strikePrice, PMDate expiryDate) {
        super(date, high, lastPrice, low, open, volume, stockVO);
        this.fotype = fotype;
        this.changeInOpenInterest = changeInOpenInterest;
        this.strikePrice = strikePrice;
        this.expiryDate = expiryDate;
        this.openInterest = openInterest;
    }

    public Integer getChangeInOpenInterest() {
        return changeInOpenInterest;
    }

    public void setChangeInOpenInterest(Integer changeInOpenInterest) {
        this.changeInOpenInterest = changeInOpenInterest;
    }

    public PMDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(PMDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getOpenInterest() {
        return openInterest;
    }

    public void setOpenInterest(Integer openInterest) {
        this.openInterest = openInterest;
    }

    public FOTYPE getFotype() {
        return fotype;
    }

    public void setFotype(FOTYPE fotype) {
        this.fotype = fotype;
    }

    public Float getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(Float strikePrice) {
        this.strikePrice = strikePrice;
    }
}
