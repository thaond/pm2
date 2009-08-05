package pm.ui;

import pm.action.Controller;
import pm.ui.table.*;
import pm.util.DropDownWrapper;
import pm.util.PMDate;
import pm.vo.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ITView extends AbstractSplitPanel {

    protected JComboBox tradeAcList = UIHelper.buildTradingAccountList(new JComboBox(), true);
    protected JComboBox portfolioList = UIHelper.buildPortfolioList(new JComboBox(), true);
    protected JComboBox yearList;
    protected JButton submitButton = getSubmitButton();
    private String LONGTERM = "LongTerm P/L";
    private String SHORTTERM = "ShortTerm P/L";

    public ITView() {
        init();
    }

    protected Component buildTopPanel() {
        populateYearList();
        return UIFactory.createTopPanel(tradeAcList, portfolioList, yearList, submitButton);
    }

    private void populateYearList() {
        yearList = UIHelper.buildComboBox(new JComboBox());
        int currentYear = new PMDate().getYear();
        for (int year = currentYear; year > 2000; year--) {
            yearList.addItem(new FinYear(year));
        }
    }

    protected void doDisplay(Object retVal, String actionCommand) {
        FYTransactionDetails fyTransactionDetails = (FYTransactionDetails) retVal;
        List<ITClassificationWrapper> tradeVOs = addWrapper(fyTransactionDetails.getTransactions());
        Map<String, Object> totalRow = totalRow(tradeVOs);
        JPanel bottomPanel = UIHelper.createChildPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;

        bottomPanel.setLayout(new GridBagLayout());
        bottomPanel.add(UIHelper.createLabel("Divident : " + fyTransactionDetails.getDivident()), gbc);
        gbc.gridy++;
        bottomPanel.add(UIHelper.createLabel("ShotTerm P/L : " + totalRow.get(SHORTTERM)), gbc);
        gbc.gridy++;
        bottomPanel.add(UIHelper.createLabel("LongTerm P/L : " + totalRow.get(LONGTERM)), gbc);
        gbc.gridy++;
        bottomPanel.add(createTransactionListing(tradeVOs, totalRow), gbc);
        splitPane.setBottomComponent(bottomPanel);
    }

    private JPanel createTransactionListing(List<ITClassificationWrapper> tradeVOs, Map<String, Object> totalRow) {
        ArrayList<TableDisplayInput> displayInputs = new ArrayList<TableDisplayInput>();
        displayInputs.add(new StockCodeDisplayInput());
        displayInputs.add(new DateDisplayInput("Purchase Date", "getPurchaseDate"));
        displayInputs.add(new DateDisplayInput("Sale Date", "getSaleDate"));
        displayInputs.add(new FloatDisplayInput("Purchase Price", "getPurchasePrice"));
        displayInputs.add(new FloatDisplayInput("Sale Price", "getSalePrice"));
        displayInputs.add(new FloatDisplayInput("Qty", "getQty"));
        displayInputs.add(new FloatDisplayInput("Brokerage", "getBrokerage"));
        displayInputs.add(new FloatWithColorDisplayInput(SHORTTERM, "getSTPL"));
        displayInputs.add(new FloatWithColorDisplayInput(LONGTERM, "getLTPL"));
        PMTableModel tableModel = new PMTableModel(tradeVOs, displayInputs, totalRow);
        return UIFactory.createTablePanel(0, tableModel);
    }

    private List<ITClassificationWrapper> addWrapper(List<TradeVO> tradeVOs) {
        ArrayList<ITClassificationWrapper> wrappedTrade = new ArrayList<ITClassificationWrapper>();
        for (TradeVO tradeVO : tradeVOs) {
            wrappedTrade.add(new ITClassificationWrapper(tradeVO));
        }
        return wrappedTrade;
    }

    private Map<String, Object> totalRow(java.util.List<ITClassificationWrapper> tradeVOs) {
        float totLTPL = 0;
        float totSTPL = 0;
        for (ITClassificationWrapper reportVO : tradeVOs) {
            totLTPL += reportVO.getLTPL();
            totSTPL += reportVO.getSTPL();
        }
        Map<String, Object> totRow = new HashMap<String, Object>();
        totRow.put(LONGTERM, totLTPL);
        totRow.put(SHORTTERM, totSTPL);
        return totRow;
    }

    protected Object getData(String actionCommand) {
        TradingAccountVO tradingAc = (TradingAccountVO) ((DropDownWrapper) tradeAcList.getSelectedItem()).getAccount();
        PortfolioDetailsVO portfolio = (PortfolioDetailsVO) ((DropDownWrapper) portfolioList.getSelectedItem()).getAccount();
        FinYear finYear = (FinYear) yearList.getSelectedItem();
        return Controller.getFiniancialYearTransaction(tradingAc, portfolio, finYear);
    }
}

