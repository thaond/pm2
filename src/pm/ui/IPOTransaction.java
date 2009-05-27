package pm.ui;

import pm.action.Controller;
import static pm.ui.UIHelper.*;
import pm.ui.util.autocomplete.CompleterTextField;
import pm.util.AppConst;
import pm.util.DropDownWrapper;
import pm.util.Helper;
import pm.util.enumlist.IPOAction;
import pm.vo.Account;
import pm.vo.IPOVO;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TransactionVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Vector;

public class IPOTransaction extends AbstractPMPanel {

    ButtonGroup bg = new ButtonGroup();

    private PMDatePicker dateField = PMDatePicker.instanceWithoutRestriction();

    private CompleterTextField stockNameField = new CompleterTextField(Helper
            .getStockMasterList().toArray());

    private JTextField stockCodeField = UIHelper.createTextField("", 10);

    private JFormattedTextField applyPriceField = new JFormattedTextField(
            NumberFormat.getNumberInstance());

    private JFormattedTextField allotedPriceField = new JFormattedTextField(
            NumberFormat.getNumberInstance());

    private JFormattedTextField applyQtyField = new JFormattedTextField(
            NumberFormat.getNumberInstance());

    private JFormattedTextField allotedQtyField = new JFormattedTextField(
            NumberFormat.getNumberInstance());

    private JFormattedTextField refundField = new JFormattedTextField(
            NumberFormat.getNumberInstance());

    private JComboBox appliedStockNameField = UIHelper.createComboBox();

    private JComboBox tradeAcList = UIHelper
            .buildTradingAccountList(new JComboBox(), false);

    private JComboBox portfolioList = buildPortfolioList(new JComboBox(), false);

    private JLabel labelStockCode = createLabel("Stock Code");

    private JLabel labelAllotedPrice = createLabel("Alloted Price");

    private JLabel labelAllotedQty = createLabel("Alloted Quantity");

    private ActionListener listener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(appliedStockNameField)) {
                loadAppliedStockDetails();
            } else {
                loadAppliedStockNameList();
            }
        }
    };

    private JLabel labelApplyPrice = createLabel("Apply Price");

    private JLabel labelRefundAmount = createLabel("Refund Amount");

    private JLabel labelApplyQty = createLabel("Apply Quantity");

    public IPOTransaction() {
        init();
        displayApplyUI();
    }

    private void init() {
        UIHelper.buildPanel(this);
        this.setBorder(new EmptyBorder(0, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 4;

        gbc.gridy = 0;
        gbc.gridx = 0;
        add(getIPOActionPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Date"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Trading Account"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        add(tradeAcList, gbc);
        tradeAcList.addActionListener(listener);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Portfolio"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        add(portfolioList, gbc);
        portfolioList.addActionListener(listener);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Stock Name"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        stockNameField.setColumns(10);
        add(stockNameField, gbc);
        add(appliedStockNameField, gbc);
        appliedStockNameField.addActionListener(listener);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        add(labelStockCode, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        stockCodeField.setColumns(10);
        add(stockCodeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(labelApplyPrice, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        buildFloatField(applyPriceField, 0f, 10, "Enter Price");
        add(applyPriceField, gbc);

        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(labelRefundAmount, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        buildFloatField(refundField, 0f, 10, "Enter Refund Amount");
        add(refundField, gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        add(labelAllotedPrice, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        buildFloatField(allotedPriceField, 0f, 10, "Enter Price");
        add(allotedPriceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(labelApplyQty, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        buildFloatField(applyQtyField, 0f, 10, "Enter Quantity");
        add(applyQtyField, gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        add(labelAllotedQty, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        buildFloatField(allotedQtyField, 0f, 10, "Enter Quantity");
        add(allotedQtyField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        add(getSubmitButton(), gbc);

    }

    private Component getIPOActionPanel() {
        ActionListener actionListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JRadioButton button = (JRadioButton) e.getSource();
                switch (IPOAction.valueOf(button.getActionCommand())) {
                    case Apply:
                        displayApplyUI();
                        break;
                    case Allotment:
                        displayAllotmentUI();
                        break;
                    case Refund:
                        displayRefundUI();
                        break;
                }

            }
        };
        JPanel panel = UIHelper.createOptionPanel("Transaction", IPOAction
                .values(), IPOAction.Apply, bg, actionListener);
        return panel;
    }

    private void displayRefundUI() {
        setAllotmentFieldsVisible(false);
        setApplyFieldsVisible(false);
        setAppliedStockFieldVisible(true);
        setRefundFieldsVisible(true);
        loadAppliedStockNameList();
    }

    private void displayAllotmentUI() {
        setAllotmentFieldsVisible(true);
        setApplyFieldsVisible(true);
        setRefundFieldsVisible(false);
        setApplyPriceQtyEditable(false);
        setAppliedStockFieldVisible(true);
        loadAppliedStockNameList();
        stockCodeField.setNextFocusableComponent(allotedPriceField);
        allotedPriceField.setNextFocusableComponent(allotedQtyField);

    }

    private void displayApplyUI() {
        setAllotmentFieldsVisible(false);
        setApplyFieldsVisible(true);
        setRefundFieldsVisible(false);
        setApplyPriceQtyEditable(true);
        setAppliedStockFieldVisible(false);
    }

    private void setAppliedStockFieldVisible(boolean visible) {
        appliedStockNameField.setVisible(visible);
        stockNameField.setVisible(!visible);
    }

    private void setApplyPriceQtyEditable(boolean editable) {
        applyPriceField.setEditable(editable);
        applyQtyField.setEditable(editable);
    }

    private void setRefundFieldsVisible(boolean visible) {
        labelRefundAmount.setVisible(visible);
        refundField.setVisible(visible);
    }

    private void setApplyFieldsVisible(boolean visible) {
        applyPriceField.setVisible(visible);
        applyQtyField.setVisible(visible);
        labelApplyQty.setVisible(visible);
        labelApplyPrice.setVisible(visible);
    }

    private void setAllotmentFieldsVisible(boolean visible) {
        labelStockCode.setVisible(visible);
        stockCodeField.setVisible(visible);
        labelAllotedPrice.setVisible(visible);
        allotedPriceField.setVisible(visible);
        labelAllotedQty.setVisible(visible);
        allotedQtyField.setVisible(visible);
    }

    private void loadAppliedStockNameList() {
        IPOAction action = IPOAction.valueOf(bg.getSelection()
                .getActionCommand());
        Account tradingAc = ((DropDownWrapper) tradeAcList
                .getSelectedItem()).getAccount();
        PortfolioDetailsVO portfolioVO = (PortfolioDetailsVO) ((DropDownWrapper) portfolioList.getSelectedItem()).getAccount();

        Vector<IPOVO> ipoVOs = Controller.getIPOTransactionDetailsFor(
                tradingAc.getId(), portfolioVO.getId(),
                action);
        DefaultComboBoxModel model = new DefaultComboBoxModel(ipoVOs);
        appliedStockNameField.setModel(model);
        loadAppliedStockDetails();
    }

    private void loadAppliedStockDetails() {
        IPOVO ipovo = (IPOVO) appliedStockNameField.getSelectedItem();
        IPOAction action = IPOAction.valueOf(bg.getSelection()
                .getActionCommand());
        switch (action) {
            case Allotment:
                if (ipovo == null) {
                    applyPriceField.setValue(new Float(0));
                    applyQtyField.setValue(new Float(0));
                    return;
                }
                applyPriceField.setValue(new Float(ipovo.getApplyPrice()));
                applyQtyField.setValue(new Float(ipovo.getApplyQty()));
                break;
            case Refund:
                if (ipovo != null) {
                    float refundAmount = ipovo.getApplyPrice() * ipovo.getApplyQty()
                            - ipovo.getAllotedPrice() * ipovo.getAllotedQty();
                    refundField.setValue(new Float(refundAmount));
                } else {
                    refundField.setValue(new Float(0));
                }
                break;
        }
    }

    @Override
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal == null) {
            return;
        }
        IPOAction action = IPOAction.valueOf(bg.getSelection()
                .getActionCommand());
        switch (action) {
            case Apply:
                applyQtyField.setValue(new Float(0f));
                applyPriceField.setValue(new Float(0f));
                break;
            case Allotment:
                allotedQtyField.setValue(new Float(0f));
                allotedPriceField.setValue(new Float(0f));
                stockCodeField.setText("");
                loadAppliedStockNameList();
                break;
            case Refund:
                refundField.setValue(new Float(0f));
                loadAppliedStockNameList();
                break;
        }


    }

    @Override
    protected Object getData(String actionCommand) {
        IPOAction action = IPOAction.valueOf(bg.getSelection()
                .getActionCommand());
        Account tradingAc = ((DropDownWrapper) tradeAcList
                .getSelectedItem()).getAccount();
        Account portfolioVO = ((DropDownWrapper) portfolioList.getSelectedItem()).getAccount();
        IPOVO ipoVO = null;
        switch (action) {
            case Apply:
                String stockName = stockNameField.getText().toUpperCase();
                float qty = ((Number) applyQtyField.getValue()).floatValue();
                float price = ((Number) applyPriceField.getValue()).floatValue();
                if (qty == 0f || price == 0f || stockName.equals("")) {
                    return null;
                }
                ipoVO = new IPOVO(stockName, dateField.pmDate(), qty, price, price * qty, portfolioVO, tradingAc);
                break;
            case Allotment:
                ipoVO = (IPOVO) appliedStockNameField.getSelectedItem();
                if (ipoVO == null) {
                    return null;
                }
                TransactionVO allotedTrans = new TransactionVO(dateField.pmDate(), stockCodeField.getText().toUpperCase(), AppConst.TRADINGTYPE.Buy, ((Number) allotedQtyField.getValue()).floatValue(),
                        ((Number) allotedPriceField.getValue()).floatValue(), 0f, portfolioVO.getName(), tradingAc.getName(), false);
                ipoVO.setAllotedTransaction(allotedTrans);
                if (allotedTrans.getQty() > ipoVO.getApplyQty()) {
                    UIHelper.displayInformation(null,
                            "Alloted qty can't be greater than applied", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                if (ipoVO.getStockCode().length() == 0) {
                    UIHelper.displayInformation(null, "Please enter stock code",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                break;
            case Refund:
                ipoVO = (IPOVO) appliedStockNameField.getSelectedItem();
                if (ipoVO == null) {
                    return null;
                }
                ipoVO.setRefundedDate(dateField.pmDate());
                ipoVO.setRefundAmount(((Number) refundField.getValue())
                        .floatValue());
                break;
        }
        Controller.doIPO(action, ipoVO);
        return true;
    }

}
