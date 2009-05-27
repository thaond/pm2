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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import pm.ui.UIHelper;
import pm.vo.WatchlistPerfVO;

import javax.swing.*;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class WatchlistPerfChart {

    public static JPanel createChart(Vector data) {
        CategoryDataset dataset = createDataset(data);
        JFreeChart jfreechart = ChartFactory.createBarChart3D("Watchlist Performance", "Category", "Value", dataset, PlotOrientation.VERTICAL, true, true, false);
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

    private static CategoryDataset createDataset(Vector data) {
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        for (int i = 0; i < data.size(); i++) {
            WatchlistPerfVO perfVO = (WatchlistPerfVO) data.elementAt(i);
            float perf = (perfVO.getEndPrice() - perfVO.getStPrice()) / perfVO.getStPrice() * 100f;
            defaultcategorydataset.addValue(perf, perfVO.getTicker(), "Return %");
        }
        return defaultcategorydataset;
    }
}
