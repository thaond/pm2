package pm.ui;

import pm.action.Controller;
import pm.ui.table.QuoteTableDisplay;
import pm.util.Helper;
import pm.util.QuoteIterator;
import pm.vo.QuoteVO;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.*;
import java.util.List;

import static pm.ui.UIHelper.*;

public class EODReport extends AbstractSplitPanel {
    private static final long serialVersionUID = 1L;
    private PMDatePicker dateButton = PMDatePicker.instanceWithLastQuoteDate();
    private JSplitPane bottomSplitPane;

    public EODReport() {
        init();
        this.flagShowCancel = false;
    }

    protected Component buildTopPanel() {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 15, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        UIHelper.addComponentWithTitle(panel, gbc, "Date", dateButton);
        gbc.gridx = 2;
        panel.add(getActionButton("Submit"), gbc);
        return panel;
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object, java.lang.String)
      */

    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal != null) {
            java.util.List<QuoteIterator> data = (java.util.List<QuoteIterator>) retVal;
            Vector<QuoteVO> dailyData = getSingleDayData(data);
            Map<Float, java.util.List<QuoteVO>> dayDataGroupedByScore = getDayDataGroupedByScore(sortData(dailyData));

            bottomSplitPane = createSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            bottomSplitPane.setLeftComponent(buildBottomLeftPanel(dayDataGroupedByScore));
            bottomSplitPane.setRightComponent(buildBottomRightPanel());
            bottomSplitPane.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - 50));
            splitPane.setBottomComponent(bottomSplitPane);
        }
    }

    private Component buildBottomRightPanel() {
        JPanel tabelPanel = new JPanel(new GridLayout(1, 1));
        buildChildPanel(tabelPanel);
        tabelPanel.setMinimumSize(new Dimension((UIHelper.WIDTH) / 5 * 4 - 3, UIHelper.HEIGHT - 70));
        return tabelPanel;
    }


    private Component buildBottomLeftPanel(Map<Float, java.util.List<QuoteVO>> transDetails) {
        JPanel treePanel = createPanel();
        treePanel.setMinimumSize(new Dimension((UIHelper.WIDTH) / 5 - 3, UIHelper.HEIGHT - 70));
        GridLayout layout = new GridLayout();
        layout.setRows(1);
        treePanel.setLayout(layout);
        treePanel.add(getTreeDisplay(transDetails));
        JScrollPane scrollPane = new JScrollPane(treePanel);
        scrollPane.setMinimumSize(new Dimension((UIHelper.WIDTH) / 5 - 3, UIHelper.HEIGHT - 70));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    private Component getTreeDisplay(Map<Float, java.util.List<QuoteVO>> scoreDetails) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("EOD Score");
        SortedSet<Float> sortedKeys = new TreeSet<Float>(scoreDetails.keySet());
        for (Float score : sortedKeys) {
            List<QuoteVO> quoteVOs = scoreDetails.get(score);
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(new QuotesWrapper(quoteVOs, score));
            top.add(child);
            for (QuoteVO quoteVO : quoteVOs) {
                child.add(new DefaultMutableTreeNode(new QuoteWrapper(quoteVO)));
            }
        }
        JTree tree = new JTree(top);
        buildTree(tree);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                displayTable(((DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent()).getUserObject());
            }
        });
        return tree;
    }

    private void displayTable(Object component) {
        JPanel tablePanel = createChildPanelWithGridLayout();
        if (component instanceof DataWrapper) {
            JTable table = ((DataWrapper) component).table();
            tablePanel.add(createScrollPane(table));
        }
        bottomSplitPane.setRightComponent(tablePanel);
    }

    private Map<Float, List<QuoteVO>> getDayDataGroupedByScore(Vector<QuoteVO> dailyData) {
        Map<Float, List<QuoteVO>> dayDataGroupedByScore = new Hashtable<Float, List<QuoteVO>>();
        for (QuoteVO quoteVO : dailyData) {
            List<QuoteVO> quoteVOs = dayDataGroupedByScore.get(quoteVO.getScoreCard());
            if (quoteVOs == null) quoteVOs = new ArrayList<QuoteVO>();
            quoteVOs.add(quoteVO);
            dayDataGroupedByScore.put(quoteVO.getScoreCard(), quoteVOs);
        }
        return dayDataGroupedByScore;
    }

    /**
     * @param dailyData
     * @return
     */
    private Vector<QuoteVO> sortData(Vector<QuoteVO> dailyData) {
        Comparator comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                int val = Float.compare(((QuoteVO) o1).getScoreCard(), ((QuoteVO) o2).getScoreCard());
                if (val == 0) return 1;
                return -val;
            }

            public boolean equals(Object obj) {
                return false;
            }
        };
        TreeSet<QuoteVO> set = new TreeSet<QuoteVO>(comparator);
        set.addAll(dailyData);
        Vector<QuoteVO> retVal = new Vector<QuoteVO>();
        for (QuoteVO quoteVO : set) {
            retVal.add(quoteVO);
        }
        return retVal;
    }

    /**
     * @param data
     * @return
     */
    private Vector<QuoteVO> getSingleDayData(java.util.List<QuoteIterator> data) {
        Vector<QuoteVO> dailyData = new Vector<QuoteVO>();
        for (QuoteIterator vector : data) {
            dailyData.add(vector.next());
        }
        return dailyData;
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#getData(java.lang.String)
      */

    protected Object getData(String actionCommand) {
        return Controller.getAnalysisReport(dateButton.pmDate());
    }

}

abstract class DataWrapper {

    public JTable table() {
        return new QuoteTableDisplay(getData()).table();
    }

    abstract List<QuoteVO> getData();

}

class QuoteWrapper extends DataWrapper {

    private QuoteVO quoteVO;

    public QuoteWrapper(QuoteVO quoteVO) {
        this.quoteVO = quoteVO;
    }

    public String toString() {
        return quoteVO.getStockCode();
    }

    protected List<QuoteVO> getData() {
        List<QuoteVO> quoteVOs = new ArrayList<QuoteVO>();
        quoteVOs.add(quoteVO);
        return quoteVOs;
    }

}

class QuotesWrapper extends DataWrapper {

    private List<QuoteVO> quoteVOs;
    private Float score;

    public QuotesWrapper(List<QuoteVO> quoteVOs, Float score) {
        this.quoteVOs = quoteVOs;
        this.score = score;
    }

    public String toString() {
        return Helper.formatFloat(score);
    }

    protected List<QuoteVO> getData() {
        return quoteVOs;
    }
}

