package pm.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import pm.chart.dataset.*;
import pm.ui.UIHelper;
import pm.util.Helper;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.EODChartVO;
import pm.vo.EODDetailsVO;
import pm.vo.StopLossVO;
import pm.vo.TradeVO;

import java.awt.*;
import java.text.SimpleDateFormat;

public class EODChartBuilder {

    String stockCode;

    int[] days;

    EODChartVO chartVO;

    private CombinedDomainXYPlot basePlot;

    private XYPlot volumePlot;

    private XYPlot pricePlot;

    private StandardXYItemRenderer movingAverageRenderer = new StandardXYItemRenderer();

    private XYItemRenderer epsRenderer = new XYAreaRenderer();

    private StandardXYItemRenderer peRenderer = new StandardXYItemRenderer();

    public EODChartBuilder(EODChartVO chartVO, String stockCode, int[] days) {
        this.chartVO = chartVO;
        this.stockCode = stockCode;
        this.days = days;
    }

    public JFreeChart getChart() {

        JFreeChart chart = new JFreeChart(stockCode, JFreeChart.DEFAULT_TITLE_FONT,
                getPlot(), true);
        if (UIHelper.colorFlag) chart.setBackgroundPaint(UIHelper.COLOR_BG_PANEL);
        return chart;

    }

    private CombinedDomainXYPlot getPlot() {
        DateAxis dateAxis = new DateAxis("Date");
        basePlot = new CombinedDomainXYPlot(dateAxis);
        basePlot.setOrientation(PlotOrientation.VERTICAL);
        basePlot.setGap(10D);
        basePlot.add(getPricePlot(), 3);
        volumePlot = getVolumePlot();
        basePlot.add(volumePlot, 1);
        return basePlot;
    }

    public void toggleVolumePlot(boolean showFlag) {
        basePlot.remove(volumePlot);
        if (showFlag) {
            basePlot.add(volumePlot);
        }
    }

    public void toggleStopLossTargetDisplay(boolean showFlag) {
        pricePlot.clearRangeMarkers();
        if (showFlag) {
            loadStopLossPlot(pricePlot);
        }
    }

    public void toggleTransactionDisplay(boolean showFlag) {
        pricePlot.clearAnnotations();
        if (showFlag) {
            loadTransactionPlot(pricePlot);
        }
    }

    public void toggleMovingAverageDisplay(boolean showFlag) {
        movingAverageRenderer.setSeriesVisible(showFlag);
    }

    public void toggleFinancialDetailsDisplay(boolean showFlag) {
        epsRenderer.setSeriesVisible(showFlag);
        peRenderer.setSeriesVisible(showFlag);
    }

    private XYPlot getVolumePlot() {
        XYDataset volumedataset = new VolumeDataset(chartVO
                .getEodDetailVOList(), stockCode);
        ValueAxis dateAxis = new DateAxis("Date");
        NumberAxis volumeAxis = new NumberAxis("Volume");
        volumeAxis.setAutoRangeIncludesZero(false);
        XYBarRenderer volumeRenderer = new XYBarRenderer();
        volumeRenderer.setSeriesPaint(0, Color.ORANGE);
        XYPlot xyplot = new XYPlot(volumedataset, dateAxis, volumeAxis,
                volumeRenderer);
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyitemrenderer.setToolTipGenerator(StandardXYToolTipGenerator
                .getTimeSeriesInstance());
        xyitemrenderer.setToolTipGenerator(StandardXYToolTipGenerator
                .getTimeSeriesInstance());
        DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
        dateaxis.setDateFormatOverride(new SimpleDateFormat("dd/MM/yyyy"));
        dateaxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        XYDataset xydataset = new DeliveryPositionDataset(chartVO
                .getEodDetailVOList(), stockCode);
        xyplot.setDataset(1, xydataset);
        NumberAxis numberaxis = new NumberAxis("Delivery Position");
        xyplot.setRangeAxis(1, numberaxis);
        xyplot.mapDatasetToRangeAxis(1, 1);
        StandardXYItemRenderer standardxyitemrenderer1 = new StandardXYItemRenderer();
        standardxyitemrenderer1.setSeriesPaint(0, Color.BLUE);
        xyitemrenderer.setToolTipGenerator(StandardXYToolTipGenerator
                .getTimeSeriesInstance());
        xyplot.setRenderer(1, standardxyitemrenderer1);

        return xyplot;
    }

    private XYPlot getPricePlot() {
        pricePlot = getBasePlotWithOHLC();
        pricePlot.setRangeCrosshairVisible(false);
        pricePlot.setDomainCrosshairVisible(false);
        int datasetIndex = 1;
        loadMovAvgPlot(pricePlot, datasetIndex++);
        NumberAxis peEpsAxis = new NumberAxis("EPS /PE");
        pricePlot.setRangeAxis(2, peEpsAxis);
        loadPEPlot(pricePlot, datasetIndex++);
        loadEPSPlot(pricePlot, datasetIndex);

        loadTransactionPlot(pricePlot);
        loadStopLossPlot(pricePlot);
        return pricePlot;
    }

    private void loadStopLossPlot(XYPlot pricePlot) {
        StopLossVO stopLossVO = chartVO.getStopLossVO();
        if (stopLossVO == null || !stopLossVO.isSet())
            return;
        ValueMarker valueMarker = new ValueMarker(stopLossVO.getStopLoss2());
        valueMarker.setPaint(Color.RED);
        valueMarker.setLabel("SL2");
        pricePlot.addRangeMarker(valueMarker);
        valueMarker = new ValueMarker(stopLossVO.getStopLoss1());
        valueMarker.setPaint(Color.RED);
        valueMarker.setLabel("SL1");
        pricePlot.addRangeMarker(valueMarker);
        valueMarker = new ValueMarker(stopLossVO.getTarget1());
        valueMarker.setPaint(Color.GREEN);
        valueMarker.setLabel("T1");
        pricePlot.addRangeMarker(valueMarker);
        valueMarker = new ValueMarker(stopLossVO.getTarget2());
        valueMarker.setPaint(Color.GREEN);
        valueMarker.setLabel("T2");
        pricePlot.addRangeMarker(valueMarker);
        double lowerBound = pricePlot.getRangeAxis().getLowerBound();
        if (lowerBound > stopLossVO.getStopLoss2()) {
            lowerBound = stopLossVO.getStopLoss2() * 0.95;
            pricePlot.getRangeAxis().setLowerBound(lowerBound);
        }
        double upperBound = pricePlot.getRangeAxis().getUpperBound();
        if (upperBound < stopLossVO.getTarget2()) {
            upperBound = stopLossVO.getTarget2() * 1.05;
            pricePlot.getRangeAxis().setUpperBound(upperBound);
        }
    }

    private void loadTransactionPlot(XYPlot pricePlot) {
        CircleDrawer buyCircledrawer = new CircleDrawer(Color.red);
        CircleDrawer sellCircledrawer = new CircleDrawer(Color.GREEN);
        for (EODDetailsVO detailsVO : chartVO.getEodDetailVOList()) {
            double x = detailsVO.getDate().getJavaDate().getTime();
            for (TradeVO tradeVO : detailsVO.getBuyTradeList()) {
                double y = tradeVO.getPurchasePrice();
                XYDrawableAnnotation xydrawableannotation = new XYDrawableAnnotation(
                        x, y, 3D, 3D, buyCircledrawer);
                pricePlot.addAnnotation(xydrawableannotation);
            }
            for (TradeVO tradeVO : detailsVO.getSellTradeList()) {
                double y = tradeVO.getSalePrice();
                XYDrawableAnnotation xydrawableannotation = new XYDrawableAnnotation(
                        x, y, 3D, 3D, sellCircledrawer);
                pricePlot.addAnnotation(xydrawableannotation);
            }
        }
    }

    private XYPlot getBasePlotWithOHLC() {
        XYDataset highlowDataset = new HighLowDataset(chartVO.getEodDetailVOList(),
                stockCode);
        DateAxis timeAxis = new DateAxis("Date");
        timeAxis.setDateFormatOverride(new SimpleDateFormat("dd/MM/yyyy"));
        timeAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        NumberAxis priceAxis = new NumberAxis("Price");
        priceAxis.setAutoRangeIncludesZero(false);
        HighLowRenderer renderer = new HighLowRenderer();
        renderer.setBaseItemLabelsVisible(false);
        XYPlot xyplot = new XYPlot(highlowDataset, timeAxis, priceAxis,
                renderer);
        xyplot.setDomainCrosshairVisible(true);
        xyplot.setRangeCrosshairVisible(true);
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        StandardXYToolTipGenerator tipGenerator = getCustomizedToolTip();
        xyitemrenderer.setToolTipGenerator(tipGenerator);
        return xyplot;
    }

    private void loadMovAvgPlot(XYPlot xyplot, int datasetIndex) {
        // movingAverageRenderer.setSeriesPaint(0, Color.black);
        xyplot.setRenderer(1, movingAverageRenderer);
        XYDataset movAvgDataset = buildMovAvgDataset();
        xyplot.setDataset(datasetIndex, movAvgDataset);
    }

    private XYDataset buildMovAvgDataset() {
        TimeSeriesCollection collection = new TimeSeriesCollection();
        for (int day : days) {
            TimeSeries ts = new TimeSeries(day + " Days mAvg");
            collection.addSeries(ts);
        }
        for (int i = 0; i < chartVO.getEodDetailVOList().size(); i++) {
            EODDetailsVO quoteVO = chartVO.getEodDetailVOList().get(i);
            PMDate date = quoteVO.getDate();
            Day day = new Day(date.getDate(), date.getMonth(), date.getYear());
            for (int j = 0; j < days.length; j++) {
                TimeSeries ts = collection.getSeries(j);
                float movingAverage = quoteVO.getMovingAverageList().get(j)
                        .getMovingAverage();
                if (movingAverage > 0)
                    ts.add(day, movingAverage);
            }
        }
        return collection;
    }

    private void loadEPSPlot(XYPlot xyplot, int datasetIndex) {
        xyplot.setDataset(datasetIndex, new EPSDataset(chartVO
                .getEodDetailVOList(), stockCode));
        xyplot.mapDatasetToRangeAxis(datasetIndex, 2);
        epsRenderer.setSeriesPaint(0, new Color(187, 199, 227, 128));
        xyplot.setRenderer(datasetIndex, epsRenderer);
    }

    private void loadPEPlot(XYPlot xyplot, int datasetIndex) {
        xyplot.setDataset(datasetIndex, new PEDataset(chartVO
                .getEodDetailVOList(), stockCode));
        xyplot.mapDatasetToRangeAxis(datasetIndex, 2);
        peRenderer.setSeriesPaint(0, Color.ORANGE);
        xyplot.setRenderer(datasetIndex, peRenderer);
    }

    private static StandardXYToolTipGenerator getCustomizedToolTip() {
        return new StandardXYToolTipGenerator() {
            public String generateLabelString(XYDataset arg0, int series,
                                              int item) {
                HighLowDataset dataset = (HighLowDataset) arg0;
                StringBuffer sb = new StringBuffer();
                sb.append("Date : ").append(
                        PMDateFormatter.displayFormat(dataset.getDate(series,
                                item)));
                sb.append(" , ");
                sb.append("Open : ").append(
                        Helper.formatFloat((float) dataset.getOpenValue(series,
                                item)));
                sb.append(" , ");
                sb.append("High : ").append(
                        Helper.formatFloat((float) dataset.getHighValue(series,
                                item)));
                sb.append(" , ");
                sb.append("Low : ").append(
                        Helper.formatFloat((float) dataset.getLowValue(series,
                                item)));
                sb.append(" , ");
                sb.append("Close : ").append(
                        Helper.formatFloat((float) dataset.getCloseValue(
                                series, item)));
                return sb.toString();
            }
        };
    }

    public boolean isHolding() {
        return chartVO.isHolding();
    }

    public EODChartVO getChartVO() {
        return chartVO;
    }

    public void updateStopLossDisplay(StopLossVO stopLossVO) {
        chartVO.setStopLossVO(stopLossVO);
        toggleStopLossTargetDisplay(true);
    }

}
