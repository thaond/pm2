package pm.chart.dataset;

import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import pm.util.PMDate;
import pm.vo.EODDetailsVO;

import java.util.List;

@SuppressWarnings("serial")
public class HighLowDataset extends AbstractXYDataset implements OHLCDataset, IntervalXYDataset {

    List<EODDetailsVO> data;
    String stockCode;

    public HighLowDataset(List<EODDetailsVO> data, String stockCode) {
        this.data = data;
        this.stockCode = stockCode;
    }

    @Override
    public int getSeriesCount() {
        return 1;
    }

    @Override
    public Comparable getSeriesKey(int series) {
        return stockCode;
    }

    public Number getHigh(int series, int item) {
        return new Float(getHighValue(series, item));
    }

    public double getHighValue(int series, int item) {
        return data.get(item).getHigh();
    }

    public Number getLow(int series, int item) {
        return new Float(getLowValue(series, item));
    }

    public double getLowValue(int series, int item) {
        return data.get(item).getLow();
    }

    public Number getOpen(int series, int item) {
        return new Float(getOpenValue(series, item));
    }

    public double getOpenValue(int series, int item) {
        return data.get(item).getOpen();
    }

    public Number getClose(int series, int item) {
        return new Float(getCloseValue(series, item));
    }

    public double getCloseValue(int series, int item) {
        return data.get(item).getClose();
    }

    public Number getVolume(int series, int item) {
        return new Float(getVolumeValue(series, item));
    }

    public double getVolumeValue(int series, int item) {
        return data.get(item).getVolume();
    }

    public int getItemCount(int series) {
        return data.size();
    }

    public Number getX(int series, int item) {
        return new Long(convertToLong(data.get(item).getDate()));
    }

    private long convertToLong(PMDate date) {
        return date.getJavaDate().getTime();
    }

    public Number getY(int series, int item) {
        return getHigh(series, item);
    }

    public PMDate getDate(int series, int item) {
        return data.get(item).getDate();
    }

    public double getDeliveryPosition(int series, int item) {
        return data.get(item).getPerDeliveryQty();
    }

    public Number getStartX(int series, int item) {
        return new Float(getStartXValue(series, item));
    }

    public double getStartXValue(int series, int item) {
        return getXValue(series, item);
    }

    public Number getEndX(int series, int item) {
        return new Float(getEndXValue(series, item) + 86399999); //+ 24hrs - 1 millisecond in millisecond
    }

    public double getEndXValue(int series, int item) {
        return getXValue(series, item);
    }

    public Number getStartY(int series, int item) {
        return new Float(getStartYValue(series, item));
    }

    public double getStartYValue(int series, int item) {
        return getLowValue(series, item);
    }

    public Number getEndY(int series, int item) {
        return new Float(getEndYValue(series, item));
    }

    public double getEndYValue(int series, int item) {
        return getHighValue(series, item);
    }

}
