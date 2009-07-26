package pm.ui;

import static pm.ui.UIHelper.*;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thiyagu
 * @version $Id: UIFactory.java,v 1.4 2008/01/23 15:39:25 tpalanis Exp $
 * @since 15-Dec-2007
 */
public class UIFactory {

    public static Component createTopPanel(List<ComponentData> componentDatas, JLabel timeStamp,
                                           JCheckBox inclDayTrading, Component submitButton) {
        JPanel panel = createChildPanel();
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (ComponentData componentData : componentDatas) {
            gbc.gridx++;
            if (componentData.getLabel() != null) {
                panel.add(createLabel(componentData.getLabel()), gbc);
                gbc.gridx++;
            }
            panel.add(componentData.getComponent(), gbc);
        }

        if (inclDayTrading != null) {
            gbc.gridx++;
            panel.add(inclDayTrading, gbc);
        }

        gbc.gridx++;
        gbc.insets = new Insets(2, 20, 2, 2);
        panel.add(submitButton, gbc);

        if (timeStamp != null) {
            gbc.insets = new Insets(2, 40, 2, 2);
            gbc.gridx++;
            panel.add(timeStamp, gbc);
        }

        return panel;
    }

    public static Component createTopPanel(JComboBox tradeAcList, JComboBox portfolioList, JComboBox reportTypeList, JLabel timeStamp, Component submitButton) {
        List<ComponentData> componentDatas = createTradingAccPortfolioDetails(tradeAcList, portfolioList);
        componentDatas.add(new ComponentData("Report Type", reportTypeList));
        return createTopPanel(componentDatas, timeStamp, null, submitButton);
    }

    public static Component createTopPanel(JComboBox tradeAcList, JComboBox portfolioList, JComboBox year, Component submitButton) {
        List<ComponentData> componentDatas = createTradingAccPortfolioDetails(tradeAcList, portfolioList);
        componentDatas.add(new ComponentData("Year", year));
        return createTopPanel(componentDatas, null, null, submitButton);
    }

    private static List<ComponentData> createTradingAccPortfolioDetails(JComboBox tradeAcList, JComboBox portfolioList) {
        List<ComponentData> componentDatas = new ArrayList<ComponentData>();
        componentDatas.add(new ComponentData("Trading Account", buildTradingAccountList(tradeAcList, true)));
        componentDatas.add(new ComponentData("Porfolio", buildPortfolioList(portfolioList, true)));
        return componentDatas;
    }

    public static Component createTopPanel(JComboBox tradeAcList, JComboBox portfolioList, JCheckBox inclDayTrading, Component submitButton) {
        List<ComponentData> componentDatas = createTradingAccPortfolioDetails(tradeAcList, portfolioList);
        return createTopPanel(componentDatas, null, inclDayTrading, submitButton);
    }

    public static Component createTopPanel(JComboBox tradeAcList, JComboBox portfolioList, Component submitButton) {
        List<ComponentData> componentDatas = createTradingAccPortfolioDetails(tradeAcList, portfolioList);
        return createTopPanel(componentDatas, null, null, submitButton);
    }

    public static Component createTopPanelWithStockList(JComboBox stockField, JButton submitButton) {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.SPLITHEIGHT));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, UIHelper.SPLITHEIGHT));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 20, 2, 2);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Stock"), gbc);
        gbc.gridx = 1;
        panel.add(stockField, gbc);
        gbc.gridx = 2;
        panel.add(submitButton, gbc);

        return panel;

    }

    public static JPanel createTablePanel(int sortingColumn, TableModel tableModel) {
        JTable table = createTable(sortingColumn, tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JPanel tablePanel = createChildPanel();
        tablePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 7, 7), 0, 0);
        tablePanel.add(scrollPane, gbc);
        return tablePanel;
    }

    public static JTable createTable(int sortingColumn, TableModel tableModel) {
        TableRowSorter sorter = new TableRowSorter(tableModel);
        ArrayList<RowSorter.SortKey> sortKeyArrayList = new ArrayList<RowSorter.SortKey>();
        sortKeyArrayList.add(new RowSorter.SortKey(sortingColumn, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeyArrayList);
        JTable table = UIHelper.createTable(tableModel);
        table.setRowSorter(sorter);
        return table;
    }
}

class ComponentData {
    String label = null;
    JComponent component = null;

    ComponentData(String label, JComponent component) {
        this.label = label;
        this.component = component;
    }

    public String getLabel() {
        return label;
    }

    public JComponent getComponent() {
        return component;
    }

}