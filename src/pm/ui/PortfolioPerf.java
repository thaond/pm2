/*
 * Created on Nov 24, 2004
 *
 */
package pm.ui;

import pm.action.Controller;
import pm.chart.PerfChart;
import static pm.ui.UIHelper.*;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class PortfolioPerf extends AbstractSplitPanel {
    private static final long serialVersionUID = 3978993180093854004L;
    private JComboBox tradeAcList = new JComboBox();
    private JComboBox portfolioList = new JComboBox();
    private PMDatePicker frmDate = PMDatePicker.instanceWithLastQuoteDate();
    private PMDatePicker toDate = PMDatePicker.instanceWithLastQuoteDate();
    private JFormattedTextField capitalField = new JFormattedTextField(10000f);

    public PortfolioPerf() {
        init();
        flagShowCancel = false;
    }

    protected Component buildTopPanel() {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Trading Account"), gbc);
        gbc.gridx = 1;
        panel.add(buildTradingAccountList(tradeAcList, true), gbc);
        gbc.gridx = 2;
        panel.add(createLabel("Porfolio"), gbc);
        gbc.gridx = 3;
        panel.add(buildPortfolioList(portfolioList, true), gbc);
        gbc.gridx = 4;
        panel.add(createLabel("From"), gbc);
        gbc.gridx = 5;
        panel.add(frmDate);
        gbc.gridx = 6;
        panel.add(createLabel("To"), gbc);
        gbc.gridx = 7;
        panel.add(toDate);
        gbc.gridx = 8;
        gbc.insets = new Insets(2, 15, 2, 2);
        panel.add(createLabel("Capital"), gbc);
        gbc.gridx = 9;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel.add(buildFloatField(capitalField, 10000, 7, "Capital Amount"), gbc);
        gbc.gridx = 10;
        gbc.insets = new Insets(2, 20, 2, 2);
        panel.add(getSubmitButton(), gbc);

        return panel;
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object)
      */
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal != null) {
            Vector reportVal = (Vector) retVal;
            JPanel bottomPanel = PerfChart.createChart(reportVal, ((Number) capitalField.getValue()).floatValue());
            splitPane.setBottomComponent(bottomPanel);
        }
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#getData()
      */
    protected Object getData(String actionCommand) {
        String tradeAc = tradeAcList.getSelectedItem().toString();
        String portfolio = portfolioList.getSelectedItem().toString();
        try {
            return Controller.getPortfolioPerformance(tradeAc, portfolio, frmDate.pmDate(), toDate.pmDate());
        } catch (Exception e) {
            logger.error(e, e);
            UIHelper.displayInformation(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
