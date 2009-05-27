package pm.ui;

import static pm.action.Controller.fundTransactionVOs;
import pm.ui.table.*;
import pm.util.DropDownWrapper;
import pm.vo.FundTransactionVO;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradingAccountVO;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewFundTransaction extends AbstractViewTransaction {

    @SuppressWarnings("unchecked")
    @Override
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal == null)
            return;
        displayTransaction((List<FundTransactionVO>) retVal);
    }

    private void displayTransaction(List<FundTransactionVO> transactionVOs) {
        Map<String, Object> totalRow = getTotalRowMap(transactionVOs);
        ArrayList<TableDisplayInput> displayInputs = new ArrayList<TableDisplayInput>();
        displayInputs.add(new StringDisplayInput("Trading Acc", "tradingAccName"));
        displayInputs.add(new StringDisplayInput("Portfolio", "portfolioName"));
        displayInputs.add(new DateDisplayInput("Date", "getDate"));
        displayInputs.add(new ToStringDisplayInput("Reason", "getTransactionReason"));
        displayInputs.add(new StringDisplayInput("Details", "getDetails"));
        displayInputs.add(new FloatWithColorDisplayInput("Amount", "getAmount"));

        PMTableModel tableModel = new PMTableModel(transactionVOs, displayInputs, totalRow);
        splitPane.setBottomComponent(UIFactory.createTablePanel(2, tableModel));
    }

    private Map<String, Object> getTotalRowMap(List<FundTransactionVO> transactionVOs) {
        Map<String, Object> retVal = new HashMap<String, Object>();
        float total = 0f;
        for (FundTransactionVO transactionVO : transactionVOs) {
            total += transactionVO.getAmount();
        }
        retVal.put("Amount", total);
        return retVal;
    }

    @Override
    protected Object getData(String actionCommand) {
        TradingAccountVO tradingAc = (TradingAccountVO) ((DropDownWrapper) tradeAcList
                .getSelectedItem()).getAccount();
        PortfolioDetailsVO portfolio = (PortfolioDetailsVO) ((DropDownWrapper) portfolioList.getSelectedItem()).getAccount();
        try {
            return fundTransactionVOs(tradingAc, portfolio);
        } catch (Exception e) {
            logger.error(e, e);
            UIHelper.displayInformation(null, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}