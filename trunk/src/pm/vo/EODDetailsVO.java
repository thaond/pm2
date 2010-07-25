package pm.vo;

import pm.util.PMDate;

import java.util.Vector;

public class EODDetailsVO {

    private EquityQuote quoteVO;

    private float eps;

    private float pe;

    private Vector<MovingAverageDetailVO> movingAverageList = new Vector<MovingAverageDetailVO>();

    private Vector<TradeVO> buyTradeList = new Vector<TradeVO>();

    private Vector<TradeVO> sellTradeList = new Vector<TradeVO>();

    public EODDetailsVO(EquityQuote quoteVO) {
        this.quoteVO = quoteVO;
    }

    public float getEps() {
        return eps;
    }

    public void setEps(float eps) {
        this.eps = eps;
    }

    public void addMovingAverageDetail(MovingAverageDetailVO detailVO) {
        movingAverageList.add(detailVO);
    }

    public Vector<MovingAverageDetailVO> getMovingAverageList() {
        return movingAverageList;
    }

    public EquityQuote getQuoteVO() {
        return quoteVO;
    }

    public void addSellTradeVO(TradeVO tradeVO) {
        sellTradeList.add(tradeVO);
    }

    public void addAllSellTradeVOs(Vector<TradeVO> tradeVOs) {
        sellTradeList.addAll(tradeVOs);
    }

    public Vector<TradeVO> getBuyTradeList() {
        return buyTradeList;
    }

    public void addBuyTradeVO(TradeVO tradeVO) {
        buyTradeList.add(tradeVO);
    }

    public void addAllBuyTradeVOs(Vector<TradeVO> tradeVOs) {
        buyTradeList.addAll(tradeVOs);
    }

    public Vector<TradeVO> getSellTradeList() {
        return sellTradeList;
    }

    public PMDate getDate() {
        return quoteVO.getDate();
    }

    public float getClose() {
        return quoteVO.getClose();
    }

    public float getPerDeliveryQty() {
        return quoteVO.getPerDeliveryQty();
    }

    public float getHigh() {
        return quoteVO.getHigh();
    }

    public float getLow() {
        return quoteVO.getLow();
    }

    public float getOpen() {
        return quoteVO.getOpen();
    }

    public float getVolume() {
        return quoteVO.getVolume();
    }

    public float getPe() {
        return pe;
    }

    public void setPe(float pe) {
        this.pe = pe;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EODDetailsVO that = (EODDetailsVO) o;

        if (Float.compare(that.eps, eps) != 0) return false;
        if (Float.compare(that.pe, pe) != 0) return false;
        if (buyTradeList != null ? !buyTradeList.equals(that.buyTradeList) : that.buyTradeList != null) return false;
        if (movingAverageList != null ? !movingAverageList.equals(that.movingAverageList) : that.movingAverageList != null)
            return false;
        if (quoteVO != null ? !quoteVO.equals(that.quoteVO) : that.quoteVO != null) return false;
        if (sellTradeList != null ? !sellTradeList.equals(that.sellTradeList) : that.sellTradeList != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (quoteVO != null ? quoteVO.hashCode() : 0);
        result = 31 * result + eps != +0.0f ? Float.floatToIntBits(eps) : 0;
        result = 31 * result + pe != +0.0f ? Float.floatToIntBits(pe) : 0;
        result = 31 * result + (movingAverageList != null ? movingAverageList.hashCode() : 0);
        result = 31 * result + (buyTradeList != null ? buyTradeList.hashCode() : 0);
        result = 31 * result + (sellTradeList != null ? sellTradeList.hashCode() : 0);
        return result;
    }
}
