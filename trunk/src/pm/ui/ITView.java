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
    private final String PL = "P/L";
    private final String PURCHASEVALUE = "Purchase Value";
    private final String SALEVALUE = "Sale Value";
    private final String BROKERAGE = "Brokerage";

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
        JPanel bottomPanel = UIHelper.createChildPanel();
        bottomPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        addSummary(tradeVOs, bottomPanel, gbc, "Short Term", true);
        gbc.gridy++;
        addSummary(tradeVOs, bottomPanel, gbc, "Long Term", false);
        gbc.gridy++;
        gbc.gridx = 0;
        bottomPanel.add(UIHelper.createLabel("Divident : " + fyTransactionDetails.getDivident()), gbc);

        gbc = new GridBagConstraints(0, ++gbc.gridy, 4, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
        bottomPanel.add(createTransactionListing(tradeVOs), gbc);
        splitPane.setBottomComponent(bottomPanel);
    }

    private void addSummary(List<ITClassificationWrapper> tradeVOs, JPanel bottomPanel, GridBagConstraints gbc, String term, boolean shortTerm) {
        Map<String, Object> totalRow = totalRow(tradeVOs, shortTerm);
        gbc.gridx = 0;
        bottomPanel.add(UIHelper.createLabel(term), gbc);
        gbc.gridy++;
        bottomPanel.add(UIHelper.createLabel("Purchase Value : " + totalRow.get(PURCHASEVALUE)), gbc);
        gbc.gridx++;
        bottomPanel.add(UIHelper.createLabel("Sale Value : " + totalRow.get(SALEVALUE)), gbc);
        gbc.gridx++;
        bottomPanel.add(UIHelper.createLabel("Brokerage : " + totalRow.get(BROKERAGE)), gbc);
        gbc.gridx++;
        bottomPanel.add(UIHelper.createLabel("P/L : " + totalRow.get(PL)), gbc);
    }

    private JPanel createTransactionListing(List<ITClassificationWrapper> tradeVOs) {
        ArrayList<TableDisplayInput> displayInputs = new ArrayList<TableDisplayInput>();
        displayInputs.add(new StockCodeDisplayInput());
        displayInputs.add(new DateDisplayInput("Purchase Date", "getPurchaseDate"));
        displayInputs.add(new DateDisplayInput("Sale Date", "getSaleDate"));
        displayInputs.add(new FloatDisplayInput(PURCHASEVALUE, "getTotalCost"));
        displayInputs.add(new FloatDisplayInput(SALEVALUE, "getSaleValue"));
        displayInputs.add(new FloatDisplayInput(BROKERAGE, "getBrokerage"));
        displayInputs.add(new FloatWithColorDisplayInput("ShortTerm P/L", "getSTPL"));
        displayInputs.add(new FloatWithColorDisplayInput("LongTerm P/L", "getLTPL"));
        PMTableModel tableModel = new PMTableModel(tradeVOs, displayInputs, false);
        return UIFactory.createTablePanel(0, tableModel);
    }

    private List<ITClassificationWrapper> addWrapper(List<TradeVO> tradeVOs) {
        ArrayList<ITClassificationWrapper> wrappedTrade = new ArrayList<ITClassificationWrapper>();
        for (TradeVO tradeVO : tradeVOs) {
            wrappedTrade.add(new ITClassificationWrapper(tradeVO));
        }
        return wrappedTrade;
    }

    private Map<String, Object> totalRow(List<ITClassificationWrapper> tradeVOs, boolean shortTerm) {
        float totPL = 0;
        float totCost = 0;
        float totSaleValue = 0;
        float totBrokerage = 0;
        for (ITClassificationWrapper reportVO : tradeVOs) {
            if (shortTerm != reportVO.isShotTerm()) {
                continue;
            }

            totPL += reportVO.getProfitLoss();
            totCost += reportVO.getTotalCost();
            totSaleValue += reportVO.getSaleValue();
            totBrokerage += reportVO.getBrokerage();
        }
        Map<String, Object> totRow = new HashMap<String, Object>();
        totRow.put(PL, totPL);
        totRow.put(PURCHASEVALUE, totCost);
        totRow.put(BROKERAGE, totBrokerage);
        totRow.put(SALEVALUE, totSaleValue);
        return totRow;
    }

    protected Object getData(String actionCommand) {
        TradingAccountVO tradingAc = (TradingAccountVO) ((DropDownWrapper) tradeAcList.getSelectedItem()).getAccount();
        PortfolioDetailsVO portfolio = (PortfolioDetailsVO) ((DropDownWrapper) portfolioList.getSelectedItem()).getAccount();
        FinYear finYear = (FinYear) yearList.getSelectedItem();
        return Controller.getFiniancialYearTransaction(tradingAc, portfolio, finYear);
    }
}

