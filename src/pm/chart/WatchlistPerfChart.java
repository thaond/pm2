/*
 * Created on Nov 29, 2004
 *
 */
package pm.chart;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import pm.vo.WatchlistPerfVO;

import javax.swing.*;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class WatchlistPerfChart {

    public static JPanel createChart(Vector data) {
        CategoryDataset dataset = createDataset(data);
        return PMChartFactory.createBarChart(dataset, "Watchlist Performance", "Category", "Value", true);
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
