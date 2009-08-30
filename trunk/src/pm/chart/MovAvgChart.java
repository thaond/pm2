/*
 * Created on 02-Feb-2005
 *
 */
package pm.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import pm.ui.UIHelper;
import pm.util.PMDate;
import pm.vo.MovAvgVO;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class MovAvgChart {

    private static XYDataset buildDataSet(String[] stockCodes, Vector<Vector<MovAvgVO>> movAvgData, PMDate frmDate, PMDate toDate, int[] days, boolean isDispPrice) {
        TimeSeriesCollection collection = new TimeSeriesCollection();
        for (int i = 0; i < stockCodes.length; i++) {
            TimeSeriesCollection singleStockdata = buildDataSet(stockCodes[i], movAvgData.elementAt(i), days, isDispPrice);
            if (singleStockdata == null) continue;
            for (int j = 0; j < singleStockdata.getSeriesCount(); j++) {
                collection.addSeries(singleStockdata.getSeries(j));
            }
        }
        return collection;
    }

    private static TimeSeriesCollection buildDataSet(String name, Vector<MovAvgVO> data, int[] days, boolean isDispPrice) {
        if (data.size() == 0) return null;
        TimeSeriesCollection collection = new TimeSeriesCollection();
        for (int day : days) {
            TimeSeries ts = new TimeSeries(name + "-" + day);
            collection.addSeries(ts);
        }
        float[] openQuote = new float[days.length];
        for (int j = 0; j < days.length; j++) {
            openQuote[j] = data.elementAt(0).getMvgByIndex(j);
        }

        for (int i = 0; i < data.size(); i++) {
            MovAvgVO quoteVO = data.elementAt(i);
            PMDate date = quoteVO.getDate();
            Day day = new Day(date.getDate(), date.getMonth(), date.getYear());
            for (int j = 0; j < days.length; j++) {
                TimeSeries ts = collection.getSeries(j);
                if (quoteVO.getMvgByIndex(j) > 0) {
                    if (isDispPrice) ts.add(day, quoteVO.getMvgByIndex(j));
                    else {
                        float diff = quoteVO.getMvgByIndex(j) - openQuote[j];
                        float per = diff / openQuote[j] * 100;
                        ts.add(day, per);
                    }
                }
            }
        }
        return collection;
    }

    public static JPanel createChart(String[] stockCodes, Vector<Vector<MovAvgVO>> movAvgData, PMDate stDate, PMDate enDate, int[] days, boolean isDispPrice, boolean forEodPage) {
        XYDataset dataset = buildDataSet(stockCodes, movAvgData, stDate, enDate, days, isDispPrice);
        String X_axisTitle = isDispPrice ? "Closing Price" : "Gain/Loss %";

        JFreeChart jfreechart = forEodPage ? ChartFactory.createTimeSeriesChart("EOD Chart", "Date", X_axisTitle, dataset, true, true, false) :
                ChartFactory.createTimeSeriesChart("", "", "", dataset, true, true, false);
        if (UIHelper.colorFlag) jfreechart.setBackgroundPaint(UIHelper.COLOR_BG_PANEL);
        XYPlot xyplot = jfreechart.getXYPlot();
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyitemrenderer.setToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        if (xyitemrenderer instanceof StandardXYItemRenderer) {
//            StandardXYItemRenderer standardxyitemrenderer = (StandardXYItemRenderer) xyitemrenderer;
            //standardxyitemrenderer.setPlotShapes(true);
            //standardxyitemrenderer.setShapesFilled(true);
        }
        StandardXYItemRenderer standardxyitemrenderer1 = new StandardXYItemRenderer();
        standardxyitemrenderer1.setSeriesPaint(0, Color.BLUE);
        xyitemrenderer.setToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        xyplot.setRenderer(0, standardxyitemrenderer1);
        DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
        dateaxis.setDateFormatOverride(new SimpleDateFormat("dd/MM/yyyy"));
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        if (forEodPage) {
            chartpanel.setMouseZoomable(true, false);
        } else {
            chartpanel.setPreferredSize(new Dimension(400, 300));
        }
        return chartpanel;
    }
}
