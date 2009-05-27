package pm.ui;

import static pm.ui.UIHelper.*;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Thiyagu
 * @version $Id: UIFactory.java,v 1.4 2008/01/23 15:39:25 tpalanis Exp $
 * @since 15-Dec-2007
 */
public class UIFactory {

    public static Component createTopPanel(JComboBox tradeAcList, JComboBox portfolioList,
                                           JComboBox reportTypeList, JLabel timeStamp,
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

        panel.add(createLabel("Trading Account"), gbc);
        gbc.gridx++;
        panel.add(buildTradingAccountList(tradeAcList, true), gbc);

        gbc.gridx++;
        panel.add(createLabel("Porfolio"), gbc);

        gbc.gridx++;
        panel.add(buildPortfolioList(portfolioList, true), gbc);

        if (inclDayTrading != null) {
            gbc.gridx++;
            panel.add(inclDayTrading, gbc);
        }

        if (reportTypeList != null) {
            gbc.gridx++;
            panel.add(createLabel("Report Type"), gbc);
            gbc.gridx++;
            panel.add(reportTypeList, gbc);
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
        return createTopPanel(tradeAcList, portfolioList, reportTypeList, timeStamp, null, submitButton);
    }

    public static Component createTopPanel(JComboBox tradeAcList, JComboBox portfolioList, JCheckBox inclDayTrading, Component submitButton) {
        return createTopPanel(tradeAcList, portfolioList, null, null, inclDayTrading, submitButton);
    }

    public static Component createTopPanel(JComboBox tradeAcList, JComboBox portfolioList, Component submitButton) {
        return createTopPanel(tradeAcList, portfolioList, null, null, null, submitButton);
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
