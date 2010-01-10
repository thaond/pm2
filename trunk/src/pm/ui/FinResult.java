package pm.ui;

import pm.action.Controller;
import pm.util.Helper;
import pm.vo.CorpResultVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static pm.ui.UIHelper.*;

public class FinResult extends AbstractPMPanel {

    private static final long serialVersionUID = 1L;
    private static int SPLITHEIGHT = 100;
    private JSplitPane splitPane;
    private JList stockList;
    private JList selectedList;

    public FinResult() {
        init();
    }

    private void init() {
        UIHelper.buildPanel(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        this.add(getSplitPane(), gbc);
    }

    private Component getSplitPane() {
        splitPane = UIHelper.createSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(buildTopPanel());
        splitPane.setBottomComponent(buildBottomPanel());
        return splitPane;
    }

    private Component buildBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 1));
        UIHelper.buildChildPanel(bottomPanel);
        bottomPanel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - SPLITHEIGHT - 10));
        return bottomPanel;
    }

    private Component buildTopPanel() {
        JPanel panel = new JPanel();
        UIHelper.buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, SPLITHEIGHT));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, SPLITHEIGHT));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(UIHelper.createLabel("Stock"), gbc);
        gbc.gridy = 1;
        gbc.gridheight = 2;
        panel.add(getStockList(), gbc);
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.gridheight = 1;
        gbc.gridy = 1;
        gbc.gridx = 1;
        panel.add(getAddButtonStock(), gbc);
        gbc.gridy = 2;
        panel.add(getRemoveButtonStock(), gbc);

        gbc.gridx = 2;
        gbc.gridheight = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 5, 2, 40);
        panel.add(createLabel("Selected"), gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        panel.add(getSelectList(), gbc);
        gbc.gridx = 3;
        gbc.gridy = 2;
        panel.add(getActionButton("Submit"), gbc);
        return panel;
    }

    private Component getSelectList() {
        selectedList = UIHelper.createList(new DefaultListModel(), 3);
        selectedList.setPrototypeCellValue(UIHelper._PROTOTYPE_DISPLAY_VALUE);
        JScrollPane scrollPane = new JScrollPane(selectedList);
        scrollPane.setAutoscrolls(true);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private Component getRemoveButtonStock() {
        JButton button = createButton("<<");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object[] object = selectedList.getSelectedValues();
                DefaultListModel stockListModel = (DefaultListModel) stockList.getModel();
                DefaultListModel watchListModel = (DefaultListModel) selectedList.getModel();
                for (int i = 0; i < object.length; i++) {
                    watchListModel.removeElement(object[i]);
                }
                addItemSorted(stockListModel, object);
            }
        });
        return button;
    }

    private Component getAddButtonStock() {
        JButton button = createButton(">>");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object[] object = stockList.getSelectedValues();
                DefaultListModel stockListModel = (DefaultListModel) stockList.getModel();
                DefaultListModel watchListModel = (DefaultListModel) selectedList.getModel();
                for (int i = 0; i < object.length; i++) {
                    stockListModel.removeElement(object[i]);
                }
                addItemSorted(watchListModel, object);
            }
        });
        return button;
    }

    private Component getStockList() {
        DefaultListModel model = new DefaultListModel();
        Vector stkList = Helper.getStockListIncIndex();
        for (Object stk : stkList) model.addElement(stk);
        stockList = UIHelper.createList(model, 3);
        JScrollPane scrollPane = new JScrollPane(stockList);
        return scrollPane;
    }


    @Override
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal != null) {
            Map<String, java.util.List<CorpResultVO>> data = (Map<String, List<CorpResultVO>>) retVal;
            DefaultListModel stkModel = (DefaultListModel) selectedList.getModel();
            String[] stockCodes = new String[stkModel.size()];
            for (int i = 0; i < stkModel.size(); i++) {
                stockCodes[i] = stkModel.elementAt(i).toString();
            }
            buildDisplayPanel(stockCodes, data);
        }
    }

    private void buildDisplayPanel(String[] stockCodes, Map<String, List<CorpResultVO>> data) {
//		JPanel bottomPanel = new JPanel(new GridLayout(1,1));
//        UIHelper.buildChildPanel(bottomPanel);
//		bottomPanel.setMinimumSize(new Dimension(UIHelper.WIDTH,UIHelper.HEIGHT-SPLITHEIGHT-10));
//		
//		splitPane.setBottomComponent(bottomPanel);
        for (String stockCode : stockCodes) {
            List<CorpResultVO> companyData = data.get(stockCode);
            for (CorpResultVO resultsVO : companyData) {
                System.out.println(resultsVO);
            }
        }
    }

    @Override
    protected Object getData(String actionCommand) {
        DefaultListModel stkModel = (DefaultListModel) selectedList.getModel();
        String[] stockCodes = new String[stkModel.size()];
        for (int i = 0; i < stkModel.size(); i++) {
            stockCodes[i] = stkModel.elementAt(i).toString();
        }
        return Controller.getFinancialResult(stockCodes);
    }

}
