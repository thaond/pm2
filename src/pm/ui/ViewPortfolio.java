/*
 * Created on Oct 19, 2004
 *
 */
package pm.ui;

import pm.action.Controller;
import pm.ui.table.*;
import pm.util.AppConst.REPORT_TYPE;
import pm.vo.ConsolidatedTradeVO;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static pm.ui.UIHelper.*;

/**
 * @author thiyagu1
 */
public class ViewPortfolio extends AbstractPMPanel {

    private static final long serialVersionUID = 3256446888990552373L;
    private JComboBox tradeAcList = new JComboBox();
    private JComboBox portfolioList = new JComboBox();
    private JPanel bottomPanel;
    private JSplitPane splitPane;
    private JComboBox typelist = getTypeList();
    private JLabel time = createLabel("");

    public ViewPortfolio() {
        super();
        init();
    }

    public void init() {
        buildPanel(this);
        flagShowProgressBar = true;
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
        Component topPanel = UIFactory.createTopPanel(tradeAcList, portfolioList, typelist, time, getActionButton("Submit"));
        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(buildBottomPanel());
        return splitPane;

    }


    private Component buildBottomPanel() {
        bottomPanel = new JPanel(new GridLayout(1, 1));
        buildChildPanel(bottomPanel);
        bottomPanel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - 60));
        return bottomPanel;
    }

    private JComboBox getTypeList() {
        JComboBox typelist = createComboBox();
        typelist.addItem(REPORT_TYPE.All.name());
        typelist.addItem(REPORT_TYPE.Holding.name());
        return typelist;
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object)
      */

    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal == null) return;
        java.util.List<ConsolidatedTradeVO> tradeVOs = (java.util.List<ConsolidatedTradeVO>) retVal;
        Map<String, Object> totalRow = totalRow(tradeVOs);
        ArrayList<TableDisplayInput> displayInputs = new ArrayList<TableDisplayInput>();
        displayInputs.add(new StockCodeDisplayInput());
        displayInputs.add(new FloatDisplayInput("Qty", "getQty"));
        displayInputs.add(new FloatDisplayInput("Cost / Unit", "getCostPerUnit"));
        displayInputs.add(new CurrentPriceDisplayInputForTradeVO());
        displayInputs.add(new FloatDisplayInput("Total Cost", "getCost"));
        displayInputs.add(new FloatDisplayInput("Value @ Market", "getCurrentValue"));
        displayInputs.add(new FloatWithColorDisplayInput("UnRealized P/L", "getUnRealizedPL"));
        displayInputs.add(new FloatWithColorDisplayInput("Realized P/L", "getProfitLoss"));
        displayInputs.add(new FloatDisplayInput("Divident", "getDivident"));
        displayInputs.add(new FloatWithColorDisplayInput("Net P/L", "getNetPL"));
        PMTableModel tableModel = new PMTableModel(tradeVOs, displayInputs, totalRow);
        splitPane.setBottomComponent(UIFactory.createTablePanel(0, tableModel));
        time.setText(UIHelper.getTime());
    }

    private Map<String, Object> totalRow(java.util.List<ConsolidatedTradeVO> tradeVOs) {
        float totValAtCost = 0;
        float totMarkValue = 0;
        float totUnRealPL = 0;
        float totRealPL = 0;
        float totNetPL = 0;
        float totDiv = 0;

        for (ConsolidatedTradeVO reportVO : tradeVOs) {
            totValAtCost += reportVO.getCost();
            totMarkValue += reportVO.getCurrentValue();
            totUnRealPL += reportVO.getUnRealizedPL();
            totRealPL += reportVO.getProfitLoss();
            totNetPL += reportVO.getNetPL();
            totDiv += reportVO.getDivident();
        }
        final float unRealizedPL = totUnRealPL;
        Map<String, Object> totRow = new HashMap<String, Object>();
        totRow.put("Total Cost", totValAtCost);
        totRow.put("Value @ Market", totMarkValue);
        totRow.put("UnRealized P/L", totUnRealPL);
        totRow.put("Realized P/L", totRealPL);
        totRow.put("Divident", totDiv);
        totRow.put("Net P/L", totNetPL);
        return totRow;
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#getData()
      */

    protected Object getData(String actionCommand) {
        String tradeAc = tradeAcList.getSelectedItem().toString();
        String portfolio = portfolioList.getSelectedItem().toString();
        try {
            return Controller.getPortfolioView(tradeAc, portfolio, typelist.getSelectedItem().toString());
        } catch (Exception e) {
            logger.error(e, e);
        }
        return null;
    }
}
