package pm.ui;

import org.jdesktop.swingx.JXTaskPane;
import pm.action.Controller;
import pm.action.QuoteManager;
import pm.bo.PortfolioBO;
import pm.bo.WatchlistBO;
import pm.ui.table.TableCellDisplay;
import pm.util.AppConst.COMPANY_ACTION_TYPE;
import pm.util.Helper;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

public class Alert extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Dimension ALERT_PANEL_SIZE = new Dimension(300, 200);
    private static final float FILTERLEVEL = 2.0f;

    public Component getCompanyActionAlert() {
        final Hashtable<PMDate, Vector<CompanyActionVO>> consolidatedData = Controller.getCompanyActionDetails();

        final JXTaskPane pane = UIHelper.createTaskPane("Company Action");
        loadCompanyAction(pane, consolidatedData, false);

        final JCheckBoxMenuItem filterAction = new JCheckBoxMenuItem("filter action", false);
        filterAction.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                loadCompanyAction(pane, consolidatedData, filterAction.getState());
            }

        });
        pane.addMouseListener(new MouseListener() {

            public void mouseExited(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    popupMenu.add(filterAction);
                    popupMenu.setBorder(new LineBorder(Color.BLUE));
                    popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
                }
            }

        });

        return pane;
    }

    private void loadCompanyAction(JXTaskPane pane, Hashtable<PMDate, Vector<CompanyActionVO>> consolidatedData, boolean filter) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        TreeSet<PMDate> sortedDate = new TreeSet<PMDate>();
        PMDate today = new PMDate();
        for (PMDate date : consolidatedData.keySet()) {
            if (date.before(today)) continue;
            sortedDate.add(date);
        }
        int y = 0;
        Border dateBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY);
        Border companyActionBorder = BorderFactory.createEmptyBorder();
        boolean flag = true;
        Color color1 = new Color(168, 219, 188);//
        Color color2 = new Color(255, 255, 255);
        MouseListenerFGAndCursorChange mouseListener = new MouseListenerFGAndCursorChange(Color.BLACK, Color.BLUE);
        for (PMDate date : sortedDate) {
            Vector<CompanyActionVO> dayData = consolidatedData.get(date);
            if (filter) {
                int filterCount = getFilteredCompanyActionCount(dayData);
                if (filterCount == 0) {
                    continue;
                }
                gbc.gridheight = filterCount;
            } else {
                gbc.gridheight = dayData.size();
            }
            gbc.gridx = 0;
            gbc.gridy = y;
            DefaultTableCellRenderer dateLabel = new DefaultTableCellRenderer();
            dateLabel.setText(PMDateFormatter.displayFormat(date));
            dateLabel.setFont(UIHelper.FONT_TABLE_DATA);
            dateLabel.setBorder(dateBorder);
            dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
            if (flag) {
                dateLabel.setBackground(color1);
            } else {
                dateLabel.setBackground(color2);
            }
            panel.add(dateLabel, gbc);
            CompanyAlertWindowActionListener actionListener = new CompanyAlertWindowActionListener();
            gbc.gridx = 1;
            gbc.gridheight = 1;
            for (CompanyActionVO companyAction : dayData) {
                if (filter && !applyFilter(companyAction)) {
                    continue;
                }
                gbc.gridy = y;
                JButton actionLabel = createDataField(actionListener, companyAction);
                actionLabel.addMouseListener(mouseListener);
                actionLabel.setFont(UIHelper.FONT_TABLE_DATA);
                actionLabel.setBorder(companyActionBorder);
                if (flag) {
                    actionLabel.setBackground(color1);
                } else {
                    actionLabel.setBackground(color2);
                }
                panel.add(actionLabel, gbc);
                y++;
            }
            flag = !flag;
        }
        pane.removeAll();
        if (panel.getComponentCount() != 0) {
            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setPreferredSize(ALERT_PANEL_SIZE);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            pane.add(scrollPane);
        }
        pane.revalidate();
    }

    private int getFilteredCompanyActionCount(Vector<CompanyActionVO> dayData) {
        int retVal = 0;
        for (CompanyActionVO actionVO : dayData) {
            if (applyFilter(actionVO)) {
                retVal++;
            }
        }
        return retVal;
    }

    private boolean applyFilter(CompanyActionVO actionVO) {
        return actionVO.getAction() != COMPANY_ACTION_TYPE.Divident || (actionVO.getValueAtCurrentPrice() >= FILTERLEVEL);
    }

    private static JButton createDataField(CompanyAlertWindowActionListener actionListener, CompanyActionVO companyAction) {
        JButton actionLabel = new JButton();
        actionLabel.setBorder(new EmptyBorder(1, 1, 1, 1));
        actionLabel.setText(" " + companyAction.getDisplayMsgWithStockCode());
        actionLabel.setHorizontalAlignment(SwingConstants.LEFT);
        actionLabel.setActionCommand(companyAction.getStockCode());
        actionLabel.addActionListener(actionListener);
        return actionLabel;
    }

    public Component getPortfolioAlert() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        List<PortfolioDetailsVO> portfolioList = Controller.getPortfolioList();
        int y = 0;
        Color colorT1 = new Color(111, 230, 182);
        Color colorT2 = new Color(29, 165, 111);
        Color colorS1 = new Color(254, 163, 139);
        Color colorS2 = new Color(252, 73, 69);
        java.awt.Font myfont = new java.awt.Font("Verdana", java.awt.Font.PLAIN, 10);
        MouseListenerFGAndCursorChange mouseListener = new MouseListenerFGAndCursorChange(Color.BLACK, Color.BLUE);
        CompanyAlertWindowActionListener actionListener = new CompanyAlertWindowActionListener();
        gbc.gridx = 0;
        for (PortfolioDetailsVO detailsVO : portfolioList) {
            if (!detailsVO.isAlertEnabled()) {
                continue;
            }
            Vector<StopLossVO> stopLossList = new PortfolioBO().getStopLossDetailsWithQuoteFilterForNonSet(detailsVO.getName());
            for (StopLossVO slVO : stopLossList) {
                JButton actionLabel = new JButton();
                float limit = 0.0f;
                float lastPrice = slVO.getQuoteVO().getLastPrice();
                if (lastPrice < slVO.getStopLoss2()) {
                    limit = slVO.getStopLoss2();
                    actionLabel.setBackground(colorS2);
                } else if (lastPrice < slVO.getStopLoss1()) {
                    limit = slVO.getStopLoss1();
                    actionLabel.setBackground(colorS1);
                } else if (lastPrice > slVO.getTarget2()) {
                    limit = slVO.getTarget2();
                    actionLabel.setBackground(colorT2);
                } else if (lastPrice > slVO.getTarget1()) {
                    limit = slVO.getTarget1();
                    actionLabel.setBackground(colorT1);
                } else {
                    continue;
                }
                gbc.gridy = y;
                actionLabel.setBorder(new EmptyBorder(1, 1, 1, 1));
                String msg = slVO.getStockCode() + " @ " + lastPrice + " [" + limit + "]";
                actionLabel.setText(msg);
                actionLabel.setHorizontalAlignment(SwingConstants.LEFT);
                actionLabel.setActionCommand(slVO.getStockCode());
                actionLabel.addActionListener(actionListener);
                actionLabel.addMouseListener(mouseListener);
                actionLabel.setFont(myfont);
                panel.add(actionLabel, gbc);
                y++;
            }
        }
        JXTaskPane pane = UIHelper.createTaskPane("Portfolio Alerts");
        addScrollPaneIfNeeded(panel, pane);
        return pane;

    }

    public Component getWatchlistAlert() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        WatchlistBO watchlistBO = new WatchlistBO();
        List<WatchlistDetailsVO> watchlistNames = watchlistBO.getWatchlistNames();
        int y = 0;
        Color colorT1 = new Color(111, 230, 182);
        Color colorS1 = new Color(254, 163, 139);
        java.awt.Font myfont = new java.awt.Font("Verdana", java.awt.Font.PLAIN, 10);
        MouseListenerFGAndCursorChange mouseListener = new MouseListenerFGAndCursorChange(Color.BLACK, Color.BLUE);
        CompanyAlertWindowActionListener actionListener = new CompanyAlertWindowActionListener();
        gbc.gridx = 0;
        for (WatchlistDetailsVO detailsVO : watchlistNames) {
            if (!detailsVO.isAlertEnabled()) {
                continue;
            }
            List<WatchlistVO> watchlistView = watchlistBO.getWatchlistView(detailsVO.getId());

            for (WatchlistVO watchlistVO : watchlistView) {
                JButton actionLabel = new JButton();
                float limit = 0.0f;
                if (watchlistVO.getFloor() == 0 || watchlistVO.getCeil() == 0) {
                    continue;
                }
                float lastPrice = watchlistVO.getCurrQuote().getLastPrice();
                if (lastPrice < watchlistVO.getFloor()) {
                    limit = watchlistVO.getFloor();
                    actionLabel.setBackground(colorS1);
                } else if (lastPrice > watchlistVO.getCeil()) {
                    limit = watchlistVO.getCeil();
                    actionLabel.setBackground(colorT1);
                } else {
                    continue;
                }
                String msg = watchlistVO.getStockCode() + " @ " + lastPrice + " [" + limit + "]";
                gbc.gridy = y;
                actionLabel.setBorder(new EmptyBorder(1, 1, 1, 1));
                actionLabel.setText(msg);
                actionLabel.setHorizontalAlignment(SwingConstants.LEFT);
                actionLabel.setActionCommand(watchlistVO.getStockCode());
                actionLabel.addActionListener(actionListener);
                actionLabel.addMouseListener(mouseListener);
                actionLabel.setFont(myfont);
                panel.add(actionLabel, gbc);
                y++;
            }
        }
        JXTaskPane pane = UIHelper.createTaskPane("Watchlist Alerts");
        addScrollPaneIfNeeded(panel, pane);
        return pane;
    }

    public static Component getIndexQuotes() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        List<StockVO> list = Controller.getIndexCodes();
        String[] stockCodes = new String[list.size()];
        for (int count = 0; count < list.size(); count++) {
            stockCodes[count] = list.get(count).getStockCode();
        }
        QuoteVO[] quotes = QuoteManager.getLiveQuote(stockCodes);
        for (int i = 0; i < quotes.length; i++) {
            QuoteVO quoteVO = quotes[i];
            try {
                gbc.gridx = 0;
                panel.add(UIHelper.createLabel(quoteVO.getStockVO().getCompanyName()), gbc);
                gbc.gridx = 1;
                String dispStr = Helper.formatFloat(quoteVO.getLastPrice()) + "  " + Helper.formatFloat(quoteVO.getPerChange()) + "%";
                panel.add(new TableCellDisplay(quoteVO.getPerChange(), 2, dispStr).getTableCellRendererComponent(i), gbc);
                gbc.gridy++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JXTaskPane pane = UIHelper.createTaskPane("Market Update");
        addScrollPaneIfNeeded(panel, pane);
        return pane;
    }

    private static void addScrollPaneIfNeeded(JPanel panel, JXTaskPane pane) {
        if (panel.getComponentCount() >= 11) {
            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setPreferredSize(ALERT_PANEL_SIZE);
            pane.add(scrollPane);
        } else {
            pane.add(panel);
        }
    }

    public static JXTaskPane getActionAlert() {
        JXTaskPane taskPane = UIHelper.createTaskPane("Sync");
        final SyncTasksPanel syncTasksPanel = SyncTasksPanel.instance();
        taskPane.add(syncTasksPanel);
        final JCheckBoxMenuItem filterAction = new JCheckBoxMenuItem("show on demand", true);
        filterAction.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (filterAction.getState()) {
                    syncTasksPanel.showTasksOnDemand();
                } else {
                    syncTasksPanel.showAllTasks();
                }
            }

        });
        taskPane.addMouseListener(new MouseListener() {

            public void mouseExited(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    popupMenu.add(filterAction);
                    popupMenu.setBorder(new LineBorder(Color.BLUE));
                    popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
                }
            }

        });
        syncTasksPanel.showTasksOnDemand();
        return taskPane;
    }

}

class CompanyAlertWindowActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        PortfolioManager.gotoEODChart(e.getActionCommand());
    }
}



