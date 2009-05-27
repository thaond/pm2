package pm.vo;

public class MovAvgVO extends QuoteVO {
    private float EPS;

    private int[] daysList;
    private float[] mvgAvg;

    public MovAvgVO(QuoteVO quoteVO, int[] daysList) {
        super(quoteVO.getStockCode());
        setDate(quoteVO.getDate());
        setOpen(quoteVO.getOpen());
        setHigh(quoteVO.getHigh());
        setLow(quoteVO.getLow());
        setLastPrice(quoteVO.getLastPrice());
        setVolume(quoteVO.getVolume());
        setPrevClose(quoteVO.getPrevClose());
        setTradeValue(quoteVO.getTradeValue());
        setPerDeliveryQty(quoteVO.getPerDeliveryQty());
        this.daysList = daysList;
        this.mvgAvg = new float[daysList.length];
    }

    public float getEPS() {
        return EPS;
    }

    public void setEPS(float eps) {
        EPS = eps;
    }

    public float getPE() {
        return getLastPrice() / EPS;
    }

    public float getMvgByDays(int days) {
        for (int i = 0; i < daysList.length; i++) {
            if (daysList[i] == days) return mvgAvg[i];
        }
        return 0;
    }

    public float getMvgByIndex(int i) {
        if (i >= 0 && i < mvgAvg.length) return mvgAvg[i];
        return 0;
    }

    public void setMvgByDays(int days, float mvg) {
        for (int i = 0; i < daysList.length; i++) {
            if (daysList[i] == days) {
                mvgAvg[i] = mvg;
                break;
            }
        }
    }

    public void setMvgByIndex(int i, float mvg) {
        if (i >= 0 && i < mvgAvg.length) mvgAvg[i] = mvg;
    }
}
