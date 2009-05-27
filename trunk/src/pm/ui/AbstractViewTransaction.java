/*
// $Id: AbstractViewTransaction.java,v 1.4 2008/01/23 15:39:25 tpalanis Exp $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2004-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package pm.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Thiyagu
 * @version $Id: AbstractViewTransaction.java,v 1.4 2008/01/23 15:39:25 tpalanis Exp $
 * @since 15-Aug-2007
 */
public abstract class AbstractViewTransaction extends AbstractPMPanel {

    protected JComboBox tradeAcList = UIHelper.buildTradingAccountList(
            new JComboBox(), true);
    protected JComboBox portfolioList = UIHelper.buildPortfolioList(
            new JComboBox(), true);
    protected JSplitPane splitPane;

    protected AbstractViewTransaction() {
        init();
    }

    protected void init() {
        UIHelper.buildPanel(this);
        flagShowProgressBar = true;
        flagShowCancel = false;
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
        splitPane.setTopComponent(UIFactory.createTopPanel(tradeAcList, portfolioList, getSubmitButton()));
        splitPane.setBottomComponent(UIHelper.createChildPanel());
        return splitPane;
    }

    private Component buildTopPanel() {
        JPanel panel = new JPanel();
        UIHelper.buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 10, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(UIHelper.createLabel("Trading Account"), gbc);
        gbc.gridx++;
        panel.add(tradeAcList, gbc);

        gbc.gridx++;
        panel.add(UIHelper.createLabel("Porfolio"), gbc);

        gbc.gridx++;
        panel.add(portfolioList, gbc);

        gbc.gridx++;
        gbc.insets = new Insets(2, 20, 2, 2);
        panel.add(getSubmitButton(), gbc);

        return panel;
    }
}
