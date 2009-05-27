/*
 * Created on Nov 29, 2004
 *
 */
package pm.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import pm.ui.UIHelper;
import pm.vo.CompanyPerfVO;
import pm.vo.CorpResultVO;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class FinancialChart {

    public static JPanel createChart(Vector<CorpResultVO> companyData) {
        CategoryDataset dataset = createDataset(companyData);
        JFreeChart jfreechart = ChartFactory.createBarChart3D("Company Performance", "Category", "EPS", dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryPlot categoryplot = jfreechart.getCategoryPlot();
        categoryplot.setDomainGridlinesVisible(true);
        CategoryAxis categoryaxis = categoryplot.getDomainAxis();
        categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.39269908169872414D));
        BarRenderer3D barrenderer3d = (BarRenderer3D) categoryplot.getRenderer();
        barrenderer3d.setDrawBarOutline(false);
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setMouseZoomable(true, false);
        if (UIHelper.colorFlag) jfreechart.setBackgroundPaint(UIHelper.COLOR_BG_PANEL);
        return chartpanel;
    }

    private static CategoryDataset createDataset(Vector<CorpResultVO> companyData) {
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        for (CorpResultVO vo : companyData) {
            defaultcategorydataset.addValue(vo.getEps(), vo.getDisplay(), "EPS");
        }
        return defaultcategorydataset;
    }

    public static JPanel createPerfChart(Vector<CompanyPerfVO> companyData) {
        XYDataset dataset = buildPerfDataSet(companyData);
        String X_axisTitle = "Gain/Loss %";
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("Company Growth Chart", "Date", X_axisTitle, dataset, true, true, false);
        jfreechart.setBackgroundPaint(UIHelper.COLOR_BG_PANEL);
        XYPlot xyplot = jfreechart.getXYPlot();
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyitemrenderer.setToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        if (xyitemrenderer instanceof StandardXYItemRenderer) {
            StandardXYItemRenderer standardxyitemrenderer = (StandardXYItemRenderer) xyitemrenderer;
            //standardxyitemrenderer.setPlotShapes(true);
            //standardxyitemrenderer.setShapesFilled(true);
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

    private static XYDataset buildPerfDataSet(Vector<CompanyPerfVO> companyData) {
        TimeSeriesCollection collection = new TimeSeriesCollection();
        TimeSeries epsSeries = new TimeSeries("EPS");
        TimeSeries stockPriceSeries = new TimeSeries("StockPrice");
        collection.addSeries(epsSeries);
        collection.addSeries(stockPriceSeries);
        for (CompanyPerfVO perfVO : companyData) {
            Day day = new Day(perfVO.getEndDate().getDate(), perfVO.getEndDate().getMonth(), perfVO.getEndDate().getYear());
            epsSeries.add(day, perfVO.getEpsGrowth());
            stockPriceSeries.add(day, perfVO.getStockPriceGrowth());

        }
        return collection;
    }
}
