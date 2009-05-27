package pm.ui;

import javax.swing.*;
import java.awt.*;

public class ManageAccounts extends JPanel {

    private static final long serialVersionUID = 3618701911285708600L;

    private JTabbedPane tabbedPane = UIHelper.createTabbedPanel();

    public ManageAccounts() {
        super();
        init();
    }

    private void init() {
        UIHelper.buildPanel(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        tabbedPane.addTab("Portfolio", new PortfolioAccounts());
        tabbedPane.addTab("Trading Account", new TradingAccounts());
        add(tabbedPane, gbc);
    }
}
