package pm.ui;

import pm.action.Controller;
import pm.ui.table.*;
import pm.vo.ICICITransaction;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ViewICICITransaction extends AbstractPMPanel {

    private JSplitPane splitPane = UIHelper.createSplitPane(JSplitPane.VERTICAL_SPLIT);

    public ViewICICITransaction() {
        init();
    }

    private void init() {
        UIHelper.buildPanel(this);
        flagShowProgressBar = true;
        flagShowCancel = false;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        setupSplitPane();
        this.add(splitPane, gbc);

    }

    private void setupSplitPane() {
        setupTopComponent();
        setupBottomComponent();
    }

    private void setupBottomComponent() {
        JPanel panel = UIHelper.createChildPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(getActionButton("SyncWithPMTransaction"), gbc);
        splitPane.setBottomComponent(panel);
    }

    private void setupTopComponent() {
        JPanel panel = UIHelper.createChildPanel();
        panel.setLayout(new BorderLayout());
        JTable table = UIHelper.createTable(buildModel());
        panel.add(UIHelper.createScrollPane(table));
        splitPane.setTopComponent(panel);
    }

    private PMTableModel buildModel() {
        List<ICICITransaction> transactions = Controller.iciciTransactions();
        List<TableDisplayInput> displayInputs = new ArrayList<TableDisplayInput>();

        displayInputs.add(new DateDisplayInput("Date", "getDate"));
        displayInputs.add(new StringDisplayInput("ICICICode", "getIciciCode"));
        displayInputs.add(new StringDisplayInput("StockCode", "getStockCode"));
        displayInputs.add(new StringDisplayInput("Action", "getActionString"));
        displayInputs.add(new FloatDisplayInput("Qty", "getQty"));
        displayInputs.add(new FloatDisplayInput("Price", "getPrice"));
        displayInputs.add(new FloatDisplayInput("Brokerage", "getBrokerage"));
        displayInputs.add(new ToStringDisplayInput("DayTrading", "isDayTrading"));
        displayInputs.add(new StringDisplayInput("Portfolio", "getPortfolio"));
        displayInputs.add(new ToStringDisplayInput("Status", "getStatus"));

        return new PMTableModel(transactions, displayInputs, false);
    }

    @Override
    protected void doDisplay(Object retVal, String actionCommand) {
        setupTopComponent();
    }

    @Override
    protected Object getData(String actionCommand) {
        Controller.syncICICIWithPMTransaction();
        return null;
    }

}

