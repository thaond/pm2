/*
 * Created on Oct 12, 2004
 *
 */
package pm.ui;

import org.apache.log4j.Logger;
import pm.AppLoader;
import pm.action.TaskManager;
import pm.dao.derby.DBManager;
import pm.util.AppConfig;
import pm.util.enumlist.TASKNAME;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Stack;

import static pm.ui.UIHelper.*;

public class PortfolioManager extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static PortfolioManager pmr = null;

    private static Logger logger = Logger.getLogger(PortfolioManager.class);

    private Hashtable<String, Class> actionDetails = new Hashtable<String, Class>();

    private JProgressBar progressBar = new JProgressBar();

    Stack<Container> history = new Stack<Container>();
    Stack<Container> forward = new Stack<Container>();
    private JMenuItem backButton = new ImageMenuItem("back.gif", "Back");
    private JMenuItem forwardButton = new ImageMenuItem("forward.gif", "Forward");
    private static final int MAX_HISTORY_SIZE = 3;

    private PortfolioManager() {
        init();
    }

    public static void gotoEODChart(String stockCode) {
        getInstance().setContentArea(new EODChartDisplay(stockCode));
    }

    public static PortfolioManager getInstance() {
        if (pmr == null) {
            pmr = new PortfolioManager();
        }
        return pmr;
    }

    public static void displayAppInfo(String msg, String title) {
        if (pmr != null) {
            JOptionPane.showMessageDialog(pmr, msg, title,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void init() {
        this.setTitle("Portfolio Manager");
        this.setExtendedState(MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(getMyMenuBar());
        this.setContentPane(new WelcomeScreen());
        this.setVisible(true);
        OceanTheme theme = new OceanTheme();
        MetalLookAndFeel.setCurrentTheme(theme);
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
            if (AppConfig.runBackgroundTask.getBooleanValue()) {
                TaskManager.start(TASKNAME.BACKGROUND);
            }
        } catch (UnsupportedLookAndFeelException e) {
            logger.error(e, e);
        }
//        RefineryUtilities.centerFrameOnScreen(this);
//        pack();

    }

    public static void resetView() {
        pmr.setContentArea(new WelcomeScreen());
    }

    private void setContentArea(Container cont) {
        if (isMac())
            cont.setBounds(0, 0, getWidth() - 1, getHeight() - 1);
        else
            cont.setBounds(0, 23, getWidth() - 8, getHeight() - 50);
        Container currContainer = getContentPane();
        currContainer.setEnabled(false);
        addToStack(currContainer, history, backButton);
        setContentPane(cont);
        forward.clear();
        forwardButton.setEnabled(false);
    }

    private boolean isMac() {
        return System.getProperty("os.name").equalsIgnoreCase("mac os x");
    }

    private JMenuBar getMyMenuBar() {
        JMenuBar menuBar = createMenuBar();
        menuBar.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;
        gbc.gridx = 0;
        menuBar.add(buildPortfolioMenu(), gbc);
        gbc.gridx++;
        menuBar.add(buildWatchlistMenu(), gbc);
        gbc.gridx++;
        menuBar.add(buildAnalyzerMenu(), gbc);
        gbc.gridx++;
        menuBar.add(buildStockMenu(), gbc);
        gbc.gridx++;
        menuBar.add(buildChartMenu(), gbc);
        gbc.gridx++;
        menuBar.add(buildWindowMenu(), gbc);

        JComponent[] buttonIcons = {getHomeButton(), actionButton("chart.gif", "EOD Chart", "Chart"),
                actionButton("portfolio.gif", "Portfolio Report", "Portfolio Report"), getExitButton(), getBackButton(), getForwardButton()};
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JMenu iconMenu = new JMenu("Icons");
        for (JComponent icon : buttonIcons) {
            if (isMac())
                iconMenu.add(icon);
            else {
                gbc.gridx++;
                menuBar.add(icon, gbc);
            }
        }

        if (isMac()) {
            gbc.gridx++;
            menuBar.add(iconMenu, gbc);
        }

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx++;
        gbc.weightx = 1;
        menuBar.add(UIHelper.createLabel(""), gbc);
        return menuBar;
    }

    private JComponent getHomeButton() {
        JMenuItem button = new ImageMenuItem("home.gif");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                resetView();
            }
        });
        button.setToolTipText("Home");
        return button;
    }

    private void addToStack(Container currContainer, Stack<Container> stack, JMenuItem button) {
        stack.push(currContainer);
        if (!button.isEnabled()) {
            button.setEnabled(true);
        }
        if (stack.size() > MAX_HISTORY_SIZE) {
            stack.remove(0);
        }
    }

    private JComponent getBackButton() {

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                doMove(PortfolioManager.this.history, PortfolioManager.this.forward, PortfolioManager.this.backButton,
                        PortfolioManager.this.forwardButton);
            }
        });
        backButton.setToolTipText("Back");
        backButton.setEnabled(false);
        return backButton;
    }

    private JComponent getForwardButton() {

        forwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                doMove(PortfolioManager.this.forward, PortfolioManager.this.history, PortfolioManager.this.forwardButton,
                        PortfolioManager.this.backButton);
            }
        });
        forwardButton.setToolTipText("Forward");
        forwardButton.setEnabled(false);
        return forwardButton;
    }

    private void doMove(Stack<Container> source, Stack<Container> destination, JMenuItem sourceButton,
                        JMenuItem desButton) {
        if (!source.isEmpty()) {
            Container currContainer = getContentPane();
            addToStack(currContainer, destination, desButton);
            currContainer = source.pop();
            currContainer.setEnabled(true);
            setContentPane(currContainer);
            if (source.isEmpty()) sourceButton.setEnabled(false);
        }

    }

    private JComponent actionButton(String iconName, final String actionCommand, String toolTip) {
        JMenuItem button = new ImageMenuItem(iconName);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                actionPerform(actionCommand);
            }
        });
        button.setToolTipText(toolTip);
        return button;
    }

    private JComponent getExitButton() {
        JMenuItem button = new ImageMenuItem("exit.gif");

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                doExit();
            }
        });
        button.setToolTipText("Exit");
        return button;
    }

    private JMenuItem getMenuItem(String label, Class targetClass) {
        JMenuItem menuItem = createMenuItem(label);
        actionDetails.put(label, targetClass);
        menuItem.addActionListener(this);
        return menuItem;
    }

    private JMenu buildStockMenu() {
        JMenu menu = createMenu("Stock");
        menu.add(getMenuItem("Quote", DisplayQuote.class));
        menu.add(getMenuItem("SupportResistance",
                DisplaySupportResistance.class));
        menu.add(getMenuItem("CompanyAction",
                DisplayCompanyAction.class));
        return menu;
    }

    private JMenu buildWindowMenu() {
        JMenu menu = createMenu("Window");
        menu.add(getMenuItem("Settings", Settings.class));
        menu.add(getMenuItem("About", DisplayInfo.class));
        return menu;
    }

    private void doExit() {
        DBManager.shutDown();
        System.exit(0);
    }

    private JMenu buildChartMenu() {
        JMenu menu = createMenu("Chart");
        menu.add(getMenuItem("Moving Avg Chart", MovingAvg.class));
        menu.add(getMenuItem("EOD Chart", EODChartDisplay.class));
        menu.add(getMenuItem("PE Chart", PEChartDisplay.class));
        menu.add(getMenuItem("Company Growth Chart", CompanyGrowthChartDisplay.class));
        return menu;
    }

    private JMenu buildAnalyzerMenu() {
        JMenu menu = createMenu("Analyzer");
        menu.add(getMenuItem("Stock Analyzer", StockPick.class));
        menu.add(getMenuItem("EOD Report", EODReport.class));
        menu.add(getMenuItem("EOD Quote", EODQuote.class));
//        menu.add(getMenuItem("Financial Result", FinResult.class));
        return menu;
    }

    private JMenu buildWatchlistMenu() {
        JMenu menu = createMenu("Watchlist");
        menu.add(getMenuItem("Edit - Watchlist", WatchlistEdit.class));
        menu.add(getMenuItem("View - Watchlist", WatchlistView.class));
        menu.add(getMenuItem("Set Limit - Watchlist", WatchlistSetLimit.class));
        menu.add(getMenuItem("Perf Chat - Watchlist", WatchlistPerf.class));
        return menu;
    }

    private JMenu buildPortfolioMenu() {
        JMenu menu = createMenu("Portfolio");
        menu.add(getMenuItem("Portfolio Report", PortfolioReport.class));
        menu.add(getMenuItem("Transaction", TransactionManager.class));
        menu.add(getMenuItem("View Portfolio", ViewPortfolio.class));
        menu.add(getViewTransactionSubMenu());
        menu.add(getMenuItem("Performance Chart", PortfolioPerf.class));
        menu.add(getMenuItem("Stop Loss", PortfolioStopLoss.class));
        menu.add(getMenuItem("Company Action", CompanyAction.class));
        menu.add(getMenuItem("Manage Accounts", ManageAccounts.class));
        menu.add(getMenuItem("IT", ITView.class));
        return menu;
    }

    private JMenu getViewTransactionSubMenu() {
        JMenu menu = createMenu("View Transaction");
        menu.add(getMenuItem("Trading", ViewTransaction.class));
        menu.add(getMenuItem("IPO", ViewIPOTransaction.class));
        menu.add(getMenuItem("Fund", ViewFundTransaction.class));
        return menu;
    }

    public static void main(String[] args) {
        AppLoader.initLogger();
        pmr = new PortfolioManager();
    }

    public void actionPerformed(ActionEvent e) {
        actionPerform(((JMenuItem) e.getSource()).getActionCommand());
    }

    private void actionPerform(String actionCommand) {
        Class targetClass = actionDetails.get(actionCommand);
        try {
            setContentArea((Container) targetClass.newInstance());
        } catch (InstantiationException e1) {
            logger.error(e1, e1);
        } catch (IllegalAccessException e1) {
            logger.error(e1, e1);
        }
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

}
