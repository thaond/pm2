package pm.vo;

import pm.util.PMDate;

public class CompanyPerfVO {

    private CorpResultVO resultVO;
    private EquityQuote quoteVO;
    private float epsGrowth;

    public float getEpsGrowth() {
        return epsGrowth;
    }

    public CompanyPerfVO(CorpResultVO resultVO) {
        this.resultVO = resultVO;
    }

    public String getDisplay() {
        return resultVO.getDisplay();
    }

    public PMDate getEndDate() {
        return resultVO.getEndDate();
    }

    public float getEps() {
        return resultVO.getEps();
    }

    public PMDate getStartDate() {
        return resultVO.getStartDate();
    }

    public void setQuote(EquityQuote quoteVO) {
        this.quoteVO = quoteVO;
    }

    public void setEpsGrowth(float epsGrowth) {
        this.epsGrowth = epsGrowth;
    }

    public float getStockPriceGrowth() {
        return (quoteVO.getLastPrice() - quoteVO.getPrevClose()) / quoteVO.getPrevClose() * 100f;
    }

}
