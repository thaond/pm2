package pm.chart.dataset;

import pm.vo.EODDetailsVO;

import java.util.List;

@SuppressWarnings("serial")
public class EPSDataset extends HighLowDataset {

    public EPSDataset(List<EODDetailsVO> data, String stockCode) {
        super(data, stockCode);
    }

    @Override
    public Number getY(int series, int item) {
        return data.get(item).getEps();
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
        return "EPS";
    }

}
