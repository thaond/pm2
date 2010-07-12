/*
 * Created on Oct 26, 2004
 *
 */
package pm.ui;

import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;
import pm.action.Controller;
import pm.chart.IndexPerfChart;
import pm.chart.MovAvgChart;
import pm.util.AppConfig;
import pm.util.Helper;
import pm.util.PMDate;
import pm.vo.MovAvgVO;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Vector;

/**
 * @author thiyagu1
 */
@SuppressWarnings("serial")
public class WelcomeScreen extends JPanel {

    public WelcomeScreen() {
        init();
    }

    private void init() {
        UIHelper.buildPanel(this);
        this.setLayout(new BorderLayout());
        this.add(getAlertPanel(), BorderLayout.EAST);
        this.add(getCenterPanel(), BorderLayout.CENTER);
    }

    private Component getCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(getIndexChart(), BorderLayout.CENTER);
//        panel.add(new IciciNseCodeMappingView(false), BorderLayout.SOUTH);
        return panel;

    }

    private Component getFinancialAlertPanel() {
        double holdingPercentage = Controller.totalInvestedRatio(AppConfig.DEFAULT_PORTFOLIO.Value);
        DefaultPieDataset defaultpiedataset = new DefaultPieDataset();
        defaultpiedataset.setValue("Securities", holdingPercentage);
        defaultpiedataset.setValue("Cash", 100 - holdingPercentage);
        JFreeChart jfreechart = ChartFactory.createPieChart("", defaultpiedataset, false, true, false);
        PiePlot pieplot = (PiePlot) jfreechart.getPlot();
        pieplot.setSectionPaint("Securities", new Color(160, 160, 255));
        pieplot.setSectionPaint("Cash", new Color(128, 128, 223));
        pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));
        pieplot.setLabelBackgroundPaint(new Color(220, 220, 220));
        pieplot.setLegendLabelToolTipGenerator(new StandardPieSectionLabelGenerator("Tooltip for legend item {0}"));
        pieplot.setStartAngle(0);
        pieplot.setDirection(Rotation.CLOCKWISE);
        ChartPanel chartPanel = new ChartPanel(jfreechart);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(chartPanel, gbc);
        gbc.gridy++;
        panel.add(UIHelper.createLabel("Rs. " + Helper.formatFloat((float) Controller.cashPosition(AppConfig.DEFAULT_PORTFOLIO.Value))), gbc);
        gbc.fill = GridBagConstraints.BOTH;
        return panel;
    }

    private Component getIndexChart() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        PMDate currDate = new PMDate();
        PMDate yearBack = currDate.getDateAddingDays(-365);
        String stockCode = AppConfig.HP_CHART_STOCKCODE.Value;
        if (stockCode == null) stockCode = "NIFTY";
        String[] stockCodes = {stockCode};
        int[] days = {1, 50};
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(createMovingAverageChart(currDate, yearBack, stockCodes, days), gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
//        panel.add(getFinancialAlertPanel());
        panel.add(indexPerformanceChart(), gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(getIndexQuotesPanel(), gbc);
        return panel;
    }

    private JPanel getIndexQuotesPanel() {
        JPanel panel = Alert.getIndexQuotesPanel();
        panel.setBorder(new LineBorder(UIHelper.COLOR_BORDER, 1));
        return panel;
    }

    private Component indexPerformanceChart() {
        JPanel panel = new IndexPerfChart().chart();
        panel.setPreferredSize(new Dimension(400, 400));
        return panel;
    }

    private JPanel createMovingAverageChart(PMDate enDate, PMDate stDate, String[] stockCodes, int[] days) {
        Vector<Vector<MovAvgVO>> vectors = Controller.getMovAvg(stockCodes, days, stDate, enDate);
        return MovAvgChart.createChart(stockCodes, vectors, stDate, enDate, days, true, false);
    }

    private Component getAlertPanel() {
        JXTaskPaneContainer alertPaneContainer = new JXTaskPaneContainer();
        Alert alert = new Alert();
        alertPaneContainer.add(alert.getPortfolioAlert());
        alertPaneContainer.add(alert.getWatchlistAlert());
        alertPaneContainer.add(alert.getCompanyActionAlert());
        alertPaneContainer.add(Alert.getActionAlert());
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(alertPaneContainer);
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UIHelper.COLOR_BORDER));
        return panel;
    }
}
