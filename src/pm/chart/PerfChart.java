package pm.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import pm.ui.UIHelper;
import pm.util.PMDate;
import pm.vo.PerformanceVO;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class PerfChart {
    private static TimePeriodValuesCollection dataSet1 = null;
    private static TimePeriodValuesCollection dataSet2 = null;

    private static void buildDataSet(Vector data, float capital) {

        dataSet1 = new TimePeriodValuesCollection();
        dataSet2 = new TimePeriodValuesCollection();

        TimePeriodValues costData = new TimePeriodValues("Cost");
        TimePeriodValues plData = new TimePeriodValues("RealizedPL");
        TimePeriodValues mvData = new TimePeriodValues("MarketValue");
        TimePeriodValues percent = new TimePeriodValues("TotalPL%");

        for (int i = 0; i < data.size(); i++) {
            PerformanceVO perfVO = (PerformanceVO) data.elementAt(i);
            PMDate date = perfVO.getDate();
            Day day = new Day(date.getDate(), date.getMonth(), date.getYear());
            costData.add(day, perfVO.getCost());
            plData.add(day, perfVO.getProfitLoss());
            mvData.add(day, perfVO.getMarketValue());
            float perc = (perfVO.getMarketValue() - perfVO.getCost() + perfVO.getProfitLoss())
                    / capital * 100;
            percent.add(day, perc);
        }

        dataSet1.addSeries(costData);
        dataSet1.addSeries(plData);
        dataSet1.addSeries(mvData);
        dataSet2.addSeries(percent);
    }

    public static JPanel createChart(Vector data, float capital) {
        buildDataSet(data, capital);
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("Performance Chart", "Date", "Value", dataSet1, true, true, false);
        if (UIHelper.colorFlag) jfreechart.setBackgroundPaint(UIHelper.COLOR_BG_PANEL);
        XYPlot xyplot = jfreechart.getXYPlot();
        NumberAxis numberaxis = new NumberAxis("Percentage");
        numberaxis.setAutoRangeIncludesZero(false);
        xyplot.setRangeAxis(1, numberaxis);
        xyplot.setDataset(1, dataSet2);
        xyplot.mapDatasetToRangeAxis(1, 1);
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyitemrenderer.setToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        if (xyitemrenderer instanceof StandardXYItemRenderer) {
            StandardXYItemRenderer standardxyitemrenderer = (StandardXYItemRenderer) xyitemrenderer;
            standardxyitemrenderer.setShapesFilled(true);
        }
        StandardXYItemRenderer standardxyitemrenderer1 = new StandardXYItemRenderer();
        standardxyitemrenderer1.setSeriesPaint(0, Color.black);
        xyitemrenderer.setToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        xyplot.setRenderer(1, standardxyitemrenderer1);
        DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
        dateaxis.setDateFormatOverride(new SimpleDateFormat("dd/MM/yyyy"));
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setMouseZoomable(true, false);
        return chartpanel;
    }
}
