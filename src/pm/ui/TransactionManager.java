package pm.ui;

import javax.swing.*;
import java.awt.*;

public class TransactionManager extends JPanel {

    private JTabbedPane tabbedPane = UIHelper.createTabbedPanel();

    public TransactionManager() {
        init();
    }

    private void init() {
        UIHelper.buildPanel(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        tabbedPane.addTab("Equity", new SecurityTransaction());
        tabbedPane.addTab("IPO", new IPOTransaction());
        tabbedPane.addTab("Fund", new FundTransaction());
        add(tabbedPane, gbc);
    }

}
