/*
 * Created on Nov 29, 2004
 *
 */
package pm.chart;

import org.jfree.data.category.DefaultCategoryDataset;
import pm.action.Controller;
import pm.action.QuoteManager;
import pm.util.PMDate;
import pm.vo.EquityQuote;
import pm.vo.StockVO;

import javax.swing.*;
import java.util.List;

/**
 * @author thiyagu1
 */
public class IndexPerfChart {

    public JPanel chart() {
        List<StockVO> indexList = Controller.getIndexCodes();
        PMDate enDate = new PMDate();
        PMDate stDate = enDate.getDateAddingDays(-30);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (StockVO stockVO : indexList) {
            EquityQuote stQuote = QuoteManager.eodQuote(stockVO, stDate);
            EquityQuote enQuote = QuoteManager.eodQuote(stockVO);
            if (stQuote == null || enQuote == null) continue;
            float perf = (enQuote.getClose() - stQuote.getClose()) / stQuote.getClose() * 100f;
            dataset.addValue(perf, stockVO.getStockCode(), stockVO.getStockCode());
        }

        return PMChartFactory.createBarChart(dataset, "Index Performance", "Index", "30 Days perf%", false);
    }
}