package pm.chart.dataset;

import pm.vo.EODDetailsVO;

import java.util.List;

@SuppressWarnings("serial")
public class DeliveryPositionDataset extends HighLowDataset {

    public DeliveryPositionDataset(List<EODDetailsVO> data, String stockCode) {
        super(data, stockCode);
    }

    @Override
    public Number getY(int series, int item) {
        return super.getDeliveryPosition(series, item);
    }

    @Override
    public double getStartYValue(int series, int item) {
        return getYValue(series, item);
    }

    @Override
    public double getEndYValue(int series, int item) {
        return super.getYValue(series, item);
    }

    @Override
    public Comparable getSeriesKey(int series) {
        return "Delivery %";
    }
}
