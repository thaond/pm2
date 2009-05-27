/*
 * Created on Oct 12, 2004
 *
 */
package pm.ui;

import pm.action.Controller;
import pm.action.QuoteManager;
import pm.tools.BrokerageCalculator;
import static pm.ui.UIHelper.*;
import pm.util.AppConst.TRADINGTYPE;
import pm.util.DropDownWrapper;
import pm.vo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class SecurityTransaction extends AbstractPMPanel {

    private static final long serialVersionUID = 3763100747709101622L;

    private JCheckBox dayTrading = new JCheckBox("Day Trading");

    private PMDatePicker dateField = PMDatePicker.instanceWithMarketDateRestriction();

    private JComboBox stockField = UIHelper.createStockVOlistJCB();

    private JFormattedTextField priceField = buildFloatField(new JFormattedTextField(NumberFormat.getNumberInstance()), 0f, 10, "Enter Price");

    private JFormattedTextField qtyField = buildFloatField(new JFormattedTextField(NumberFormat.getNumberInstance()), 0f, 10, "Enter Quantity");

    private JFormattedTextField brokField = buildFloatField(new JFormattedTextField(NumberFormat.getNumberInstance()), 0f, 10, "Brokerage");

    private JComboBox tradeAcList = new JComboBox();

    private JComboBox portfolioList = buildPortfolioList(new JComboBox(), false);

    private ButtonGroup bg = new ButtonGroup();

    public SecurityTransaction() {
        super();
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
        add(getTradingTypePanel(), gbc);

        addComponentWithTitle(gbc, "Date", dateField);
        addComponentWithTitle(gbc, "Trading Account", getTradingAccountList());
        addComponentWithTitle(gbc, "Portfolio", portfolioList);
        addComponentWithTitle(gbc, "Stock", stockField);
        addComponentWithTitle(gbc, "Price", priceField);
        addComponentWithTitle(gbc, "Quantity", qtyField);
        addComponentWithTitle(gbc, "Brokerage", brokField);

        addBrokerageUpdateListener();
        addStockListUpdateListener();

        gbc.gridy++;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(getSubmitButton(), gbc);
    }

    private void addStockListUpdateListener() {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateStockList();
            }
        };
        tradeAcList.addActionListener(actionListener);
        portfolioList.addActionListener(actionListener);
    }

    private void addBrokerageUpdateListener() {
        PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                calculateBrokerage();
            }
        };
        priceField.addPropertyChangeListener("value", propertyChangeListener);
        qtyField.addPropertyChangeListener("value", propertyChangeListener);
    }

    private void updateStockList() {
        String action = bg.getSelection().getActionCommand();
        TRADINGTYPE tradingtype = TRADINGTYPE.valueOf(action);
        if (tradingtype == TRADINGTYPE.Sell) {
            Account tradingAcc = ((DropDownWrapper) tradeAcList.getSelectedItem()).getAccount();
            Account portfolio = ((DropDownWrapper) portfolioList.getSelectedItem()).getAccount();
            List<TradeVO> holdingDetails = Controller.holdingDetails(tradingAcc, portfolio, dayTrading.isSelected());
            Set<String> stockCodes = new HashSet<String>();
            for (TradeVO tradeVO : holdingDetails) {
                stockCodes.add(tradeVO.getStockCode());
            }
            List<StockVO> stockList = Controller.stockList(false);
            Vector<StockVO> filteredList = new Vector<StockVO>();
            for (StockVO stockVO : stockList) {
                if (stockCodes.contains(stockVO.getStockCode())) {
                    filteredList.add(stockVO);
                }
            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(filteredList);
            stockField.setModel(model);
        } else {
            List<StockVO> stockList = Controller.stockList(false);
            DefaultComboBoxModel model = new DefaultComboBoxModel(new Vector(stockList));
            stockField.setModel(model);
        }
    }

    private JComponent getTradingAccountList() {
        UIHelper.buildTradingAccountList(tradeAcList, false);
        tradeAcList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateBrokerage();
            }
        });
        return tradeAcList;
    }

    /**
     * @return
     */
    private Component getTradingTypePanel() {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateBrokerage();
                updateStockList();
            }

        };

        TRADINGTYPE[] tradingtypes = new TRADINGTYPE[2];
        tradingtypes[0] = TRADINGTYPE.Buy;
        tradingtypes[1] = TRADINGTYPE.Sell;

        JPanel panel = createOptionPanel("Transaction", tradingtypes,
                TRADINGTYPE.Buy, bg, actionListener);
        buildCheckBox(dayTrading, false);
        dayTrading.addActionListener(actionListener);
        panel.add(dayTrading);
        return panel;
    }

    protected void calculateBrokerage() {
        float qty = ((Number) qtyField.getValue()).floatValue();
        float price = ((Number) priceField.getValue()).floatValue();
        String action = bg.getSelection().getActionCommand();
        float brok = new BrokerageCalculator().getBrokerage(
                (TradingAccountVO) ((DropDownWrapper) tradeAcList.getSelectedItem()).getAccount(), TRADINGTYPE
                .valueOf(action), dateField.pmDate(), qty, price,
                dayTrading.isSelected());
        brokField.setValue(new Float(brok));

    }

    private boolean validateForm() {
        float price = ((Number) priceField.getValue()).floatValue();
        if (price == 0f) {
            UIHelper.displayInformation(null, "Enter Price", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (((Number) qtyField.getValue()).floatValue() == 0f) {
            UIHelper.displayInformation(null, "Enter Qty", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        StockVO stockVO = (StockVO) stockField.getSelectedItem();
        QuoteVO quote = QuoteManager.eodQuote(stockVO, dateField.pmDate());
        if (quote != null) {
            float upperPricelimit = quote.getClose() * 1.2f;
            float lowerPricelimit = quote.getClose() * 0.8f;
            if (price > upperPricelimit || price < lowerPricelimit) {
                UIHelper.displayInformation(null, "Price is not in valid range", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /*
      * (non-Javadoc)
      *
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object)
      */
    protected void doDisplay(Object retVal, String actionCommand) {

    }

    /*
      * (non-Javadoc)
      *
      * @see pm.ui.AbstractPMPanel#getData()
      */
    protected Object getData(String actionCommand) {
        if (validateForm()) {
            String action = bg.getSelection().getActionCommand();
            try {
                float qty = ((Number) qtyField.getValue()).floatValue();
                float price = ((Number) priceField.getValue()).floatValue();
                float brok = ((Number) brokField.getValue()).floatValue();

                TransactionVO transVO = new TransactionVO(dateField.pmDate(),
                        stockField.getSelectedItem().toString(), TRADINGTYPE
                        .valueOf(action), qty, price, brok,
                        portfolioList.getSelectedItem().toString(), tradeAcList
                        .getSelectedItem().toString(), dayTrading
                        .isSelected());
                if (Controller.isDuplicate(transVO)) {
                    int i = JOptionPane.showOptionDialog(PortfolioManager.getInstance(), "Same transaction exists, enter again?", "Confirmation", JOptionPane.CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                            null, new Object[]{"Yes", "No"}, "No");
                    if (i == 0) {
                        doTrade(transVO);
                    }
                } else {
                    doTrade(transVO);
                }
            } catch (Exception e1) {
                logger.error(e1, e1);
                UIHelper.displayInformation(null, e1.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    private void doTrade(TransactionVO transVO) {
        boolean status = Controller.doTrading(transVO);
        if (status) {
            priceField.setValue(0f);
            qtyField.setValue(0f);
            brokField.setValue(0f);
        }
    }
}
