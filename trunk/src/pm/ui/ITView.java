package pm.ui;

import pm.action.Controller;
import pm.ui.table.*;
import pm.util.DropDownWrapper;
import pm.util.PMDate;
import pm.vo.FinYear;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradeVO;
import pm.vo.TradingAccountVO;

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
        java.util.List<ITClassificationWrapper> tradeVOs = addWrapper((java.util.List<TradeVO>) retVal);

        Map<String, Object> totalRow = totalRow(tradeVOs);
        ArrayList<TableDisplayInput> displayInputs = new ArrayList<TableDisplayInput>();
        displayInputs.add(new StockCodeDisplayInput());
        displayInputs.add(new DateDisplayInput("Purchase Date", "getPurchaseDate"));
        displayInputs.add(new DateDisplayInput("Sale Date", "getSaleDate"));
        displayInputs.add(new FloatDisplayInput("Purchase Price", "getPurchasePrice"));
        displayInputs.add(new FloatDisplayInput("Sale Price", "getSalePrice"));
        displayInputs.add(new FloatDisplayInput("Qty", "getQty"));
        displayInputs.add(new FloatDisplayInput("Brokerage", "getBrokerage"));
        displayInputs.add(new FloatWithColorDisplayInput("ShortTerm P/L", "getSTPL"));
        displayInputs.add(new FloatWithColorDisplayInput("LongTerm P/L", "getLTPL"));
        PMTableModel tableModel = new PMTableModel(tradeVOs, displayInputs, totalRow);
        splitPane.setBottomComponent(UIFactory.createTablePanel(0, tableModel));
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
        totRow.put("LongTerm P/L", totLTPL);
        totRow.put("ShortTerm P/L", totSTPL);
        return totRow;
    }

    protected Object getData(String actionCommand) {
        TradingAccountVO tradingAc = (TradingAccountVO) ((DropDownWrapper) tradeAcList.getSelectedItem()).getAccount();
        PortfolioDetailsVO portfolio = (PortfolioDetailsVO) ((DropDownWrapper) portfolioList.getSelectedItem()).getAccount();
        FinYear finYear = (FinYear) yearList.getSelectedItem();
        return Controller.getFiniancialYearTransaction(tradingAc, portfolio, finYear);
    }
}

