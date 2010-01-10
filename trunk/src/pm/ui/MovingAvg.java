package pm.ui;

import pm.action.Controller;
import pm.chart.MovAvgChart;
import pm.util.Helper;
import pm.vo.MovAvgVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Vector;

import static pm.ui.UIHelper.*;

public class MovingAvg extends AbstractPMPanel {
    private static final long serialVersionUID = 1L;
    private static int SPLITHEIGHT = 100;
    private JSplitPane splitPane;
    private JPanel bottomPanel;
    private JList stockList;
    private JList selectedList;
    private JList dayList;
    private JCheckBox dispPrice = createCheckBox("Display Price", false);
    private JFormattedTextField daysField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private PMDatePicker frmDate = PMDatePicker.instanceWithLastQuoteDate();
    private PMDatePicker toDate = PMDatePicker.instanceWithLastQuoteDate();

    public MovingAvg() {
        super();
        init();
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
    }

    private Component getSplitPane() {
        splitPane = UIHelper.createSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(buildTopPanel());
        splitPane.setBottomComponent(buildBottomPanel());
        return splitPane;
    }

    private Component buildBottomPanel() {
        bottomPanel = new JPanel(new GridLayout(1, 1));
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
        gbc.gridy = 1;
        panel.add(createLabel("From"), gbc);
        gbc.gridy = 2;
        panel.add(createLabel("To"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(frmDate, gbc);
        gbc.gridy = 2;
        panel.add(toDate, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 40, 2, 5);
        panel.add(UIHelper.createLabel("Stock"), gbc);
        gbc.gridy = 1;
        gbc.gridheight = 2;
        panel.add(getStockList(), gbc);
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.gridheight = 1;
        gbc.gridy = 1;
        gbc.gridx = 3;
        panel.add(getAddButtonStock(), gbc);
        gbc.gridy = 2;
        panel.add(getRemoveButtonStock(), gbc);

        gbc.gridx = 4;
        gbc.gridheight = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 5, 2, 40);
        panel.add(createLabel("Selected"), gbc);
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        panel.add(getSelectList(), gbc);
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.gridheight = 1;
        gbc.gridx = 5;
        gbc.gridy = 1;
        panel.add(UIHelper.createLabel("Days"), gbc);
        gbc.gridy = 2;
        daysField.setColumns(3);
        panel.add(daysField, gbc);
        gbc.gridx = 6;
        gbc.gridy = 1;
        panel.add(getAddButton(), gbc);
        gbc.gridy = 2;
        panel.add(getRemoveButton(), gbc);
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 5, 2, 40);
        panel.add(UIHelper.createLabel("No. of Days"), gbc);
        gbc.gridy = 1;
        gbc.gridheight = 2;
        panel.add(getDaysList(), gbc);
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.gridx = 8;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        panel.add(dispPrice, gbc);
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

    private Component getDaysList() {
        DefaultListModel listModel = new DefaultListModel();
        dayList = UIHelper.createList(listModel, 3);
        dayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dayList.setPrototypeCellValue("99999");
        JScrollPane scrollPane = new JScrollPane(dayList);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private Component getRemoveButton() {
        JButton addButton = UIHelper.createButton("<<");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultListModel dayModel = (DefaultListModel) dayList.getModel();
                for (Object obj : dayList.getSelectedValues()) dayModel.removeElement(obj);
            }
        });
        return addButton;
    }

    private Component getAddButton() {
        JButton addButton = UIHelper.createButton(">>");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int days = ((Number) daysField.getValue()).intValue();
                if (days != 0) {
                    if (days <= 200) {
                        DefaultListModel dayModel = (DefaultListModel) dayList.getModel();
                        Integer newElement = new Integer(days);
                        if (!dayModel.contains(newElement)) {
                            dayModel.addElement(new Integer(days));
                        }
                        daysField.setValue(new Integer(0));
                    } else {
                        UIHelper.displayInformation(null, "Maximum 200 days", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        return addButton;
    }

    private Component getStockList() {
        DefaultListModel model = new DefaultListModel();
        Vector stkList = Helper.getStockListIncIndex();
        for (Object stk : stkList) model.addElement(stk);
        stockList = UIHelper.createList(model, 3);
        JScrollPane scrollPane = new JScrollPane(stockList);
        return scrollPane;
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object, java.lang.String)
      */
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal != null) {
            Vector<Vector<MovAvgVO>> movAvgData = (Vector<Vector<MovAvgVO>>) retVal;
            DefaultListModel dayModel = (DefaultListModel) dayList.getModel();
            int days[] = new int[dayModel.size()];
            for (int i = 0; i < days.length; i++) days[i] = ((Integer) dayModel.elementAt(i)).intValue();
            DefaultListModel stkModel = (DefaultListModel) selectedList.getModel();
            String[] stockCodes = new String[stkModel.size()];
            for (int i = 0; i < stkModel.size(); i++) {
                stockCodes[i] = stkModel.elementAt(i).toString();
            }
            bottomPanel = MovAvgChart.createChart(stockCodes, movAvgData, frmDate.pmDate(), toDate.pmDate(), days, dispPrice.isSelected(), true);
            splitPane.setBottomComponent(bottomPanel);
        }
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#getData(java.lang.String)
      */
    protected Object getData(String actionCommand) {
        try {
            DefaultListModel dayModel = (DefaultListModel) dayList.getModel();
            int days[] = new int[dayModel.size()];
            for (int i = 0; i < days.length; i++) days[i] = ((Integer) dayModel.elementAt(i)).intValue();

            DefaultListModel stkModel = (DefaultListModel) selectedList.getModel();
            String[] stockCodes = new String[stkModel.size()];
            for (int i = 0; i < stkModel.size(); i++) {
                stockCodes[i] = stkModel.elementAt(i).toString();
            }
            return Controller.getMovAvg(stockCodes, days, frmDate.pmDate(), toDate.pmDate());
        } catch (Exception e1) {
            logger.error(e1, e1);
            UIHelper.displayInformation(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
