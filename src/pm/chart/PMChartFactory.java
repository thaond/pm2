package pm.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import pm.ui.UIHelper;

import javax.swing.*;

public class PMChartFactory {

    public static JPanel createBarChart(CategoryDataset dataset, String title, String xAxisLabel, String yAxisLabel) {
        JFreeChart jfreechart = ChartFactory.createBarChart3D(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
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
}
