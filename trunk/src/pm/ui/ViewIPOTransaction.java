package pm.ui;

import static pm.action.Controller.getIPOTransactionList;
import pm.ui.table.*;
import pm.util.DropDownWrapper;
import pm.vo.Account;
import pm.vo.IPOVO;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Vector;

public class ViewIPOTransaction extends AbstractViewTransaction {

    @SuppressWarnings("unchecked")
    @Override
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal == null)
            return;
        Vector<IPOVO> ipovos = (Vector<IPOVO>) retVal;
        displayTransaction(ipovos);
    }

    private void displayTransaction(Vector<IPOVO> ipovos) {
        ArrayList<TableDisplayInput> displayInputs = new ArrayList<TableDisplayInput>();
        displayInputs.add(new DateDisplayInput("ApplyDate", "getApplyDate"));
        displayInputs.add(new FloatDisplayInput("ApplyPrice", "getApplyPrice"));
        displayInputs.add(new FloatDisplayInput("ApplyQty", "getApplyQty"));
        displayInputs.add(new StockCodeDisplayInput());
        displayInputs.add(new DateDisplayInput("AllotedDate", "getAllotedDate"));
        displayInputs.add(new FloatDisplayInput("AllotedPrice", "getAllotedPrice"));
        displayInputs.add(new FloatDisplayInput("AllotedQty", "getAllotedQty"));
        displayInputs.add(new DateDisplayInput("RefundedDate", "getRefundedDate"));
        displayInputs.add(new FloatDisplayInput("RefundedAmount", "getRefundAmount"));
        displayInputs.add(new FloatWithColorDisplayInput("Balance", "getBalanceAmount"));

        PMTableModel tableModel = new PMTableModel(ipovos, displayInputs, null);
        splitPane.setBottomComponent(UIFactory.createTablePanel(0, tableModel));

    }

    @Override
    protected Object getData(String actionCommand) {
        Account tradingAc = ((DropDownWrapper) tradeAcList
                .getSelectedItem()).getAccount();
        Account portfolioVO = ((DropDownWrapper) portfolioList.getSelectedItem()).getAccount();
        try {
            return getIPOTransactionList(tradingAc.getId(), portfolioVO.getId());
        } catch (Exception e) {
            logger.error(e, e);
            UIHelper.displayInformation(null, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}