/*
 * Created on 28-Jan-2005
 *
 */
package pm.ui;

import pm.action.Controller;
import pm.ui.table.StockCodeDisplay;
import pm.ui.table.TableCellDisplay;
import pm.util.AppConst.ANALYZER_LIST;
import pm.util.AppConst.STOCK_PICK_TYPE;
import pm.util.QuoteIterator;
import pm.vo.QuoteVO;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.*;

import static pm.ui.UIHelper.*;

/**
 * @author thiyagu1
 */
public class StockPick extends AbstractSplitPanel {
    private static final long serialVersionUID = 3256446906170618934L;
    private static final int SPLITHEIGHT = 90;
    private JSplitPane bottomSplitPane;
    private PMDatePicker frmDate = PMDatePicker.instanceWithLastQuoteDate();
    private PMDatePicker toDate = PMDatePicker.instanceWithLastQuoteDate();
    private JList pickList;
    private ButtonGroup bg = new ButtonGroup();
    private JCheckBox positive = UIHelper.createCheckBox("+ve", true);
    private JCheckBox negative = UIHelper.createCheckBox("-ve", true);
    private Vector<QuoteIterator> dataToDisplay;

    public StockPick() {
        init();
        flagShowCancel = false;
    }

    protected Component buildTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(WIDTH, SPLITHEIGHT));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 5, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createLabel("From"), gbc);
        gbc.gridy = 2;
        panel.add(createLabel("To"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(frmDate, gbc);
        gbc.gridy = 2;
        panel.add(toDate, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(2, 20, 5, 0);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        panel.add(getPickList(), gbc);
        gbc.gridheight = 1;
        gbc.gridx = 3;
        gbc.gridy = 1;
        JRadioButton jrbAnd = createRadioButton(STOCK_PICK_TYPE.And.name(), STOCK_PICK_TYPE.And.name());
        jrbAnd.setSelected(true);
        bg.add(jrbAnd);
        panel.add(jrbAnd, gbc);
        JRadioButton jrbOr = createRadioButton(STOCK_PICK_TYPE.Or.name(), STOCK_PICK_TYPE.Or.name());
        bg.add(jrbOr);
        gbc.gridy = 2;
        panel.add(jrbOr, gbc);
        gbc.gridx = 4;
        gbc.gridy = 1;
        panel.add(positive, gbc);
        gbc.gridy = 2;
        panel.add(negative, gbc);
        gbc.gridx = 5;
        gbc.gridy = 2;
        panel.add(getActionButton("Submit"), gbc);
        return panel;
    }

    private Component getPickList() {
        DefaultListModel model = new DefaultListModel();
        for (ANALYZER_LIST aList : ANALYZER_LIST.values()) model.addElement(aList);
        pickList = createList(model, 4);
        pickList.setFixedCellWidth(150);
        final JScrollPane scrollPane = new JScrollPane(pickList);
        pickList.addListSelectionListener(new ListSelectionListener() {
            Set<Integer> selectedList = new HashSet<Integer>();

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                JList jList = (JList) e.getSource();
                int[] newlySelectedList = jList.getSelectedIndices();
                for (int index : newlySelectedList) {
                    if (!selectedList.contains(index)) {
                        selectedList.add(index);
                        ANALYZER_LIST analyzer = (ANALYZER_LIST) jList.getModel().getElementAt(index);
                        if (analyzer.needInput()) {
                            showPopup(scrollPane, analyzer);
                        }
                        break;
                    }
                }
                selectedList.clear();
                for (int index : newlySelectedList) {
                    selectedList.add(index);
                }
            }
        });
        return scrollPane;
    }

    private void showPopup(JComponent baseElement, ANALYZER_LIST analyzer) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 15, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        final Map<String, Float> factors = analyzer.getFactors();
        final Map<String, JFormattedTextField> factorsInputMap = new HashMap<String, JFormattedTextField>();
        for (String factor : factors.keySet()) {
            JFormattedTextField inputField = createFloatField(factors.get(factor), 4, "");
            UIHelper.addComponentWithTitle(panel, gbc, factor, inputField);
            factorsInputMap.put(factor, inputField);
        }

        final JPopupMenu popup = new JPopupMenu();
        popup.setLayout(new BorderLayout());
        popup.add(panel, BorderLayout.CENTER);
        popup.setLightWeightPopupEnabled(true);
        popup.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                for (String factor : factorsInputMap.keySet()) {
                    JFormattedTextField field = factorsInputMap.get(factor);
                    float value = ((Number) field.getValue()).floatValue();
                    factors.put(factor, value);
                }
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        popup.show(baseElement, baseElement.getWidth() - 20, 20);
        popup.requestFocus();

    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object, java.lang.String)
      */

    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal == null) return;
        dataToDisplay = (Vector) retVal;
        bottomSplitPane = createSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        bottomSplitPane.setLeftComponent(buildBottomLeftPanel(dataToDisplay));
        bottomSplitPane.setRightComponent(buildBottomRightPanel());
        bottomSplitPane.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - 50));
        splitPane.setBottomComponent(bottomSplitPane);
    }

    private Component buildBottomLeftPanel(Vector<QuoteIterator> dataToDisplay) {
        JPanel treePanel = new JPanel();
        buildPanel(treePanel);
        treePanel.setMinimumSize(new Dimension((UIHelper.WIDTH) / 5 - 3, UIHelper.HEIGHT - 70));
        GridLayout layout = new GridLayout();
        layout.setRows(1);
        treePanel.setLayout(layout);
        treePanel.add(getTreeDisplay(dataToDisplay));
        return treePanel;
    }

    private Component buildBottomRightPanel() {
        JPanel tabelPanel = new JPanel(new GridLayout(1, 1));
        buildChildPanel(tabelPanel);
        tabelPanel.setMinimumSize(new Dimension((UIHelper.WIDTH) / 5 * 4 - 3, UIHelper.HEIGHT - 70));
        return tabelPanel;
    }

    private static String ALL = "All Stocks";

    private Component getTreeDisplay(Vector<QuoteIterator> dataToDisplay) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(ALL);
        Set<String> sortedStockCode = new TreeSet<String>();
        for (QuoteIterator iterator : dataToDisplay) {
            sortedStockCode.add(iterator.getStockCode());
        }
        for (String stockCode : sortedStockCode) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(stockCode);
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
        JScrollPane scrollPane = new JScrollPane(tree);
        return scrollPane;
    }

    /**
     * @param strTreeNode
     */
    protected void displayTable(String strTreeNode) {

        Vector<QuoteVO> vQuoteDetails = new Vector<QuoteVO>();
        boolean dispStockCode = false;

        if (strTreeNode.equals(ALL)) { // get the list of all the transcations
            for (QuoteIterator quoteIterator : dataToDisplay) {
                quoteIterator.movePtrToFirst();
                for (; quoteIterator.hasNext();) {
                    QuoteVO quoteVO = quoteIterator.next();
                    vQuoteDetails.add(quoteVO);
                }
            }
            dispStockCode = true;
        } else {
            for (QuoteIterator quoteIterator : dataToDisplay) {
                if (quoteIterator.getStockCode().equals(strTreeNode)) {
                    quoteIterator.movePtrToFirst();
                    for (; quoteIterator.hasNext();) {
                        QuoteVO quoteVO = quoteIterator.next();
                        vQuoteDetails.add(quoteVO);
                    }
                }
            }
        }
        TableModel model = new AnalyzerViewTableModel(vQuoteDetails, dispStockCode);
        bottomSplitPane.setRightComponent(UIFactory.createTablePanel(0, model));
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#getData(java.lang.String)
      */

    protected Object getData(String actionCommand) {
//        splitPane.setBottomComponent(buildBottomPanel());
        STOCK_PICK_TYPE funMode = STOCK_PICK_TYPE.valueOf(bg.getSelection().getActionCommand());
        Object[] objs = pickList.getSelectedValues();
        ANALYZER_LIST[] analyzerList = new ANALYZER_LIST[objs.length];
        for (int i = 0; i < objs.length; i++) analyzerList[i] = (ANALYZER_LIST) objs[i];
        return Controller.getAnalyzedData(frmDate.pmDate(), toDate.pmDate(), analyzerList, funMode, positive.isSelected(), negative.isSelected());
    }
}

class AnalyzerViewTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private final boolean dispStockCode;
    private Vector<QuoteVO> vData;
    private String[] colName = {"Date", "Open", "High", "Low", "Close", "Volume", "PickList"};

    public AnalyzerViewTableModel(Vector<QuoteVO> data, boolean dispStockCode) {
        vData = data;
        this.dispStockCode = dispStockCode;
    }

    public int getColumnCount() {
        if (dispStockCode) return colName.length + 1;
        return colName.length;
    }

    public int getRowCount() {
        return vData.size();
    }

    public String getColumnName(int column) {
        if (dispStockCode) {
            if (column == 0) return "Stock";
            else return colName[column - 1];
        } else {
            return colName[column];
        }
    }

    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {

        QuoteVO quoteVO = vData.elementAt(rowIndex);
        if (dispStockCode) { //special condition to display all transaction
            if (columnIndex == 0) {
                return new StockCodeDisplay(quoteVO.getStockCode());
            } else columnIndex--;
        }

        switch (columnIndex) {
            case 0:
                return new TableCellDisplay(quoteVO.getDate(), 0);
            case 1:
                return new TableCellDisplay(quoteVO.getOpen(), 0);
            case 2:
                return new TableCellDisplay(quoteVO.getHigh(), 0);
            case 3:
                return new TableCellDisplay(quoteVO.getLow(), 0);
            case 4:
                return new TableCellDisplay(quoteVO.getClose(), 0);
            case 5:
                return new TableCellDisplay(quoteVO.getVolume(), 0);
            case 6:
                return new TableCellDisplay(quoteVO.getPickDetails());
        }
        return null;

    }
}

