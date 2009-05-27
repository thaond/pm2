/*
 * Created on Dec 8, 2004
 *
 */
package pm.ui;

import pm.action.Controller;
import static pm.ui.UIHelper.*;
import pm.ui.table.*;
import pm.vo.TradeVO;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.*;
import java.util.List;


public class ViewTransaction extends AbstractPMPanel {

    private static final long serialVersionUID = 3545519512624575028L;

    private static final String TRANSACTIONLIST = "Transactions";

    private JComboBox tradeAcList = new JComboBox();
    private JComboBox portfolioList = new JComboBox();
    private JSplitPane splitPane;
    private JSplitPane bottomSplitPane;
    private Hashtable transDetails;
    private JCheckBox inclDayTrading = createCheckBox("Incl. Day Trading", false);

    public ViewTransaction() {
        super();
        init();
    }

    public void init() {
        buildPanel(this);
        flagShowProgressBar = true;
        flagShowCancel = false;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        this.add(getSplitPane(), gbc);
    }

    private Component getSplitPane() {
        splitPane = createSplitPane(JSplitPane.VERTICAL_SPLIT);
        Component topPanel = UIFactory.createTopPanel(tradeAcList, portfolioList, inclDayTrading, getSubmitButton());
        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(buildBottomPanel());
        return splitPane;
    }


    private Component buildBottomPanel() {
        JPanel bottomPanel = createChildPanel();
        bottomPanel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - 60));
        return bottomPanel;
    }

    private Component buildBottomLeftPanel(Hashtable transDetails) {
        JPanel treePanel = createPanel();
        treePanel.setMinimumSize(new Dimension((UIHelper.WIDTH) / 5 - 3, UIHelper.HEIGHT - 70));
        GridLayout layout = new GridLayout();
        layout.setRows(1);
        treePanel.setLayout(layout);
        treePanel.add(getTreeDisplay(transDetails));
        return treePanel;
    }

    private Component getTreeDisplay(Hashtable transDetails) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(TRANSACTIONLIST);
        SortedSet sortedKeys = new TreeSet(transDetails.keySet());
        for (Object object : sortedKeys) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(object.toString());
            top.add(child);
        }
        JTree tree = new JTree(top);
        buildTree(tree);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                String strTreeNode = e.getNewLeadSelectionPath().getLastPathComponent().toString();
                displayTable(strTreeNode);
            }
        });
        return new JScrollPane(tree);
    }

    protected void displayTable(String strTreeNode) {

        Vector tradeVOs = new Vector();
        boolean dispStockCode = false;

        if (strTreeNode.equals(TRANSACTIONLIST)) { // get the list of all the transcations
            for (Enumeration enumer = transDetails.elements(); enumer.hasMoreElements();) {
                Vector details = (Vector) enumer.nextElement();
                tradeVOs.addAll(details);
            }
            dispStockCode = true;
        } else {
            tradeVOs = (Vector) transDetails.get(strTreeNode);
        }

        List<TradeVO> tradeVOsWithTotal = new ArrayList<TradeVO>(tradeVOs);
        int sortingColumn = dispStockCode ? 1 : 0;
        Map<String, Object> totalRow = totalRow(tradeVOsWithTotal);

        ArrayList<TableDisplayInput> displayInputs = new ArrayList<TableDisplayInput>();
        if (dispStockCode) displayInputs.add(new StockCodeDisplayInput());
        displayInputs.add(new DateDisplayInput("Purchase Date", "getPurchaseDate"));
        displayInputs.add(new FloatDisplayInput("Purchase Price", "getPurchasePrice"));
        displayInputs.add(new FloatDisplayInput("Qty", "getQty"));
        displayInputs.add(new FloatDisplayInput("Tot Cost", "getTotalCost"));
        displayInputs.add(new DateDisplayInput("Sale Date", "getSaleDate"));
        displayInputs.add(new FloatDisplayInput("Sale Price", "getSalePrice"));
        displayInputs.add(new FloatDisplayInput("SaleValue", "getSaleValue"));
        displayInputs.add(new FloatDisplayInput("Brokerage", "getBrokerage"));
        displayInputs.add(new FloatWithColorDisplayInput("P/L", "getProfitLoss"));
        displayInputs.add(new FloatDisplayInput("Divident", "getDivident"));
        displayInputs.add(new FloatWithColorDisplayInput("Net P/L", "getNetProfitLoss"));
        displayInputs.add(new FloatWithColorDisplayInput("Net P/L %", "getNetPLPercentage"));
        PMTableModel tableModel = new PMTableModel(tradeVOsWithTotal, displayInputs, totalRow);
        bottomSplitPane.setRightComponent(UIFactory.createTablePanel(sortingColumn, tableModel));
    }

    private Map<String, Object> totalRow(List<TradeVO> tradeVos) {
        float TotCost = 0f;
        float TotSaleValue = 0f;
        float TotBrokerage = 0f;
        float TotPL = 0f;
        float TotNetPL = 0f;
        float TotDivident = 0f;
        for (TradeVO stockVO : tradeVos) {
            TotCost += stockVO.getTotalCost();
            TotSaleValue += stockVO.getSaleValue();
            TotBrokerage += stockVO.getBrokerage();
            TotPL += stockVO.getProfitLoss();
            TotNetPL += stockVO.getNetProfitLoss();
            TotDivident += stockVO.getDivident();
        }
        Map<String, Object> totRow = new HashMap<String, Object>();
        totRow.put("Tot Cost", TotCost);
        totRow.put("SaleValue", TotSaleValue);
        totRow.put("Brokerage", TotBrokerage);
        totRow.put("P/L", TotPL);
        totRow.put("Net P/L", TotNetPL);
        return totRow;
    }

    private Component buildBottomRightPanel() {
        JPanel tabelPanel = new JPanel(new GridLayout(1, 1));
        buildChildPanel(tabelPanel);
        tabelPanel.setMinimumSize(new Dimension((UIHelper.WIDTH) / 5 * 4 - 3, UIHelper.HEIGHT - 70));
        return tabelPanel;
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object)
      */
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal == null) return;
        transDetails = (Hashtable) retVal;
        bottomSplitPane = createSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        bottomSplitPane.setLeftComponent(buildBottomLeftPanel(transDetails));
        bottomSplitPane.setRightComponent(buildBottomRightPanel());
        bottomSplitPane.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - 50));
        splitPane.setBottomComponent(bottomSplitPane);

    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#getData()
      */
    protected Object getData(String actionCommand) {
        String tradeAc = tradeAcList.getSelectedItem().toString();
        String portfolio = portfolioList.getSelectedItem().toString();
        try {
            return Controller.getTransactionDetails(tradeAc, portfolio, inclDayTrading.isSelected());
        } catch (Exception e) {
            logger.error(e, e);
            UIHelper.displayInformation(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}

