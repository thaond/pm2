package pm.ui;

import pm.action.Controller;
import pm.chart.EODChartPanel;
import static pm.ui.UIHelper.*;
import static pm.util.AppConst.TIMEPERIOD;
import pm.util.DropDownWrapper;
import pm.vo.EODChartVO;
import pm.vo.PortfolioDetailsVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class EODChartDisplay extends AbstractPMPanel {
    private static final long serialVersionUID = 1L;
    private static final int SPLITHEIGHT = 50;
    private static int days[] = {10, 50, 200};
    private JSplitPane splitPane;
    private JPanel bottomPanel;
    private JComboBox stockField;
    private PMDatePicker frmDate;
    private PMDatePicker toDate = PMDatePicker.instanceWithLastQuoteDate();
    private JCheckBox applyComAction = createCheckBox("Apply Company Action", true);
    private JComboBox portfolioField = UIHelper.buildPortfolioList(new JComboBox(), false);
    private JComboBox timePeriod = UIHelper.createComboBox();

    public EODChartDisplay() {
        super();
        init();
    }

    public EODChartDisplay(String stockCode) {
        this();
        setSelectedStock(stockCode);
        doAction(null);
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
        flagShowCancel = false;
        flagShowProgressBar = true;
    }

    private Component getSplitPane() {
        splitPane = createSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(buildTopPanel());
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

    private Component buildTopPanel() {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, SPLITHEIGHT));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, SPLITHEIGHT));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 2);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createLabel("From"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        frmDate = PMDatePicker.instanceWithLastYearQuoteDate();
        panel.add(frmDate, gbc);
        gbc.gridx++;
        panel.add(createLabel("To"), gbc);
        gbc.gridx++;
        panel.add(toDate, gbc);
        gbc.gridx++;
        panel.add(createLabel("Stock"), gbc);
        gbc.gridx++;
        stockField = UIHelper.createStocklistIncIndexJCB();
        panel.add(stockField, gbc);
        stockField.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doAction("Submit");
                }
            }
        });
        gbc.gridx++;
        panel.add(createLabel("Portfolio"), gbc);
        gbc.gridx++;
        panel.add(portfolioField, gbc);
        gbc.gridx++;
        panel.add(applyComAction, gbc);
        gbc.gridx++;
        buildTimePeriod();
        panel.add(timePeriod, gbc);
        gbc.gridx++;
        panel.add(getSubmitButton(), gbc);

        return panel;
    }

    private void buildTimePeriod() {
        for (TIMEPERIOD option : TIMEPERIOD.values()) {
            timePeriod.addItem(option);
        }
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object, java.lang.String)
      */
    @SuppressWarnings("unchecked")
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal == null) {
            return;
        }
        PortfolioDetailsVO portfolioDetailsVO = (PortfolioDetailsVO) ((DropDownWrapper) portfolioField.getSelectedItem()).getAccount();
        bottomPanel = new EODChartPanel((EODChartVO) retVal, stockField.getSelectedItem().toString(), days, portfolioDetailsVO);
        splitPane.setBottomComponent(bottomPanel);
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#getData(java.lang.String)
      */
    protected Object getData(String actionCommand) {
        if (frmDate.getDate().after(toDate.getDate())) {
            UIHelper.displayInformation(null, "To date is before From Date", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        try {
            return Controller.getEODData(frmDate.pmDate(), toDate.pmDate(), days, stockField.getSelectedItem().toString(), applyComAction.isSelected(), portfolioField.getSelectedItem().toString(), (TIMEPERIOD) timePeriod.getSelectedItem());
        } catch (Exception e) {
            logger.error(e, e);
            UIHelper.displayInformation(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private void setSelectedStock(String stockCode) {
        for (int i = 0; i < stockField.getItemCount(); i++) {
            if (stockField.getItemAt(i).equals(stockCode)) {
                stockField.setSelectedIndex(i);
                return;
            }
        }
    }
}
