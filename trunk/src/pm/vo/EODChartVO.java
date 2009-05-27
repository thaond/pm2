package pm.vo;

import java.util.List;

public class EODChartVO {

    List<EODDetailsVO> eodDetailVOList;
    StopLossVO stopLossVO;
    boolean holding;

    public EODChartVO(List<EODDetailsVO> eodDetailVOList, StopLossVO stopLossVO, boolean holding) {
        this.eodDetailVOList = eodDetailVOList;
        this.stopLossVO = stopLossVO;
        this.holding = holding;
    }

    public List<EODDetailsVO> getEodDetailVOList() {
        return eodDetailVOList;
    }

    public StopLossVO getStopLossVO() {
        return stopLossVO;
    }

    public boolean isHolding() {
        return holding;
    }

    public void setStopLossVO(StopLossVO stopLossVO) {
        this.stopLossVO = stopLossVO;
    }

}
