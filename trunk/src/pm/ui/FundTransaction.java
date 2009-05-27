/*
// $Id: FundTransaction.java,v 1.5 2007/12/19 14:07:28 tpalanis Exp $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2004-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package pm.ui;

import pm.action.Controller;
import static pm.ui.UIHelper.*;
import pm.util.AppConst;
import static pm.util.AppConst.FUND_TRANSACTION_REASON;
import static pm.util.AppConst.FUND_TRANSACTION_TYPE;
import pm.util.DropDownWrapper;
import pm.vo.FundTransactionVO;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradingAccountVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Vector;

/**
 * @author Thiyagu
 * @version $Id: FundTransaction.java,v 1.5 2007/12/19 14:07:28 tpalanis Exp $
 * @since 14-Aug-2007
 */
public class FundTransaction extends AbstractPMPanel {
    private PMDatePicker dateField = PMDatePicker.instanceWithoutRestriction();
    private JFormattedTextField amountField = buildFloatField(new JFormattedTextField(NumberFormat.getNumberInstance()), 0f, 10, "Enter Amount");
    private JComboBox tradeAcList = buildTradingAccountList(new JComboBox(), false);
    private JComboBox portfolioList = buildPortfolioList(new JComboBox(), false);
    private JComboBox reasonList = UIHelper.createJCB(new Vector());
    private JTextField detailField = UIHelper.buildTextField(new JTextField(), "", 20);
    private ButtonGroup bg = new ButtonGroup();

    public FundTransaction() {
        init();
    }

    public void init() {
        UIHelper.buildPanel(this);
        this.setBorder(new EmptyBorder(0, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 4;

        gbc.gridy = 1;
        gbc.gridx = 0;
        add(getTransactionTypePanel(), gbc);

        addComponentWithTitle(gbc, "Date", dateField);
        addComponentWithTitle(gbc, "Amount", amountField);
        addComponentWithTitle(gbc, "Reason", reasonList);
        addComponentWithTitle(gbc, "Trading Account", tradeAcList);
        addComponentWithTitle(gbc, "Portfolio", portfolioList);
        addComponentWithTitle(gbc, "Detail", detailField);

        gbc.gridy++;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(getSubmitButton(), gbc);
    }

    private Component getTransactionTypePanel() {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                FUND_TRANSACTION_TYPE type = FUND_TRANSACTION_TYPE.valueOf(actionCommand);
                updateReasonList(type.reasons());
            }
        };

        JPanel optionPanel = createOptionPanel("Transaction",
                new FUND_TRANSACTION_TYPE[]{FUND_TRANSACTION_TYPE.Credit, FUND_TRANSACTION_TYPE.Debit},
                FUND_TRANSACTION_TYPE.Credit, bg, actionListener);
        updateReasonList(FUND_TRANSACTION_TYPE.Credit.reasons());
        return optionPanel;
    }

    private void updateReasonList(AppConst.FUND_TRANSACTION_REASON[] fund_transaction_reasons) {
        reasonList.removeAllItems();
        for (FUND_TRANSACTION_REASON fund_transaction_reason : fund_transaction_reasons) {
            reasonList.addItem(fund_transaction_reason);
        }
    }


    protected void doDisplay(Object retVal, String actionCommand) {
        if ((Boolean) retVal) {
            amountField.setValue(0f);
            detailField.setText("");
        }

    }

    protected Object getData(String actionCommand) {
        try {
            FUND_TRANSACTION_TYPE type = FUND_TRANSACTION_TYPE.valueOf(bg.getSelection().getActionCommand());
            FUND_TRANSACTION_REASON reason = (FUND_TRANSACTION_REASON) reasonList.getSelectedItem();
            float amount = ((Number) amountField.getValue()).floatValue();

            TradingAccountVO tradingAccount = (TradingAccountVO) ((DropDownWrapper) tradeAcList.getSelectedItem()).getAccount();
            PortfolioDetailsVO portfolioDetail = (PortfolioDetailsVO) ((DropDownWrapper) portfolioList.getSelectedItem()).getAccount();
            FundTransactionVO transaction = new FundTransactionVO(type, reason, dateField.pmDate(), amount, tradingAccount, portfolioDetail, detailField.getText());
            Controller.doTransaction(transaction);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
