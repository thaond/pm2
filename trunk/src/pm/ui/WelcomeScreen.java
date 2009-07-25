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
import pm.chart.MovAvgChart;
import pm.util.AppConfig;
import pm.util.Helper;
import pm.util.PMDate;
import pm.vo.MovAvgVO;

import javax.swing.*;
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
        new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        PMDate currDate = new PMDate();
        PMDate yearBack = currDate.getDateAddingDays(-365);
        String stockCode = AppConfig.HP_CHART_STOCKCODE.Value;
        if (stockCode == null) stockCode = "^NSEI";
        String[] stockCodes = {stockCode};
        int[] days = {1, 50};
        Vector<Vector<MovAvgVO>> vectors = Controller.getMovAvg(stockCodes, days, yearBack, currDate);
        JPanel chart = MovAvgChart.createChart(stockCodes, vectors, yearBack, currDate, days, true, false);
        panel.add(chart);
        panel.add(getFinancialAlertPanel());
        return panel;
    }

    private Component getAlertPanel() {
        JXTaskPaneContainer alertPaneContainer = new JXTaskPaneContainer();
        Alert alert = new Alert();
        alertPaneContainer.add(alert.getPortfolioAlert());
        alertPaneContainer.add(alert.getWatchlistAlert());
        alertPaneContainer.add(alert.getCompanyActionAlert());
        alertPaneContainer.add(Alert.getActionAlert());
        alertPaneContainer.add(Alert.getIndexQuotes());
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(alertPaneContainer);
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        return panel;
    }
}
