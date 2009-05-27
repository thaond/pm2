package pm.ui;

import pm.action.Controller;
import pm.chart.FinancialChart;
import static pm.ui.UIHelper.buildChildPanel;
import static pm.ui.UIHelper.createSplitPane;
import pm.util.AppConst.CORP_RESULT_TIMELINE;
import pm.vo.CompanyPerfVO;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class CompanyGrowthChartDisplay extends AbstractPMPanel {

    private static final long serialVersionUID = 1L;
    private static final int SPLITHEIGHT = 50;
    private JSplitPane splitPane;
    private JPanel bottomPanel;
    private JComboBox stockField = UIHelper.createStocklistJCB();

    public CompanyGrowthChartDisplay() {
        super();
        init();
    }

    public void init() {
        UIHelper.buildPanel(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        this.add(getSplitPane(), gbc);
    }

    private Component getSplitPane() {
        splitPane = createSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(UIFactory.createTopPanelWithStockList(stockField, getSubmitButton()));
        splitPane.setBottomComponent(buildBottomPanel());
        splitPane.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT));
        return splitPane;
    }

    private Component buildBottomPanel() {
        bottomPanel = new JPanel(new GridLayout(1, 1));
        buildChildPanel(bottomPanel);
        bottomPanel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - SPLITHEIGHT));
        return bottomPanel;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal != null) {
            Vector<CompanyPerfVO> companyData = (Vector<CompanyPerfVO>) retVal;
            bottomPanel = FinancialChart.createPerfChart(companyData);
            splitPane.setBottomComponent(bottomPanel);
            bottomPanel = FinancialChart.createPerfChart(companyData);
            splitPane.setBottomComponent(bottomPanel);
        }
    }

    @Override
    protected Object getData(String actionCommand) {
        try {
            return Controller.getCompanyPerformance(stockField.getSelectedItem().toString(), CORP_RESULT_TIMELINE.Quaterly);
        } catch (Exception e) {
            logger.error(e, e);
            UIHelper.displayInformation(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

}
