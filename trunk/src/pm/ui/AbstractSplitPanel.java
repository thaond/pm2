package pm.ui;

import static pm.ui.UIHelper.createChildPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author Thiyagu
 * @version $Id: AbstractSplitPanel.java,v 1.2 2008/01/13 16:36:17 tpalanis Exp $
 * @since 16-Dec-2007
 */
public abstract class AbstractSplitPanel extends AbstractPMPanel {

    protected JSplitPane splitPane;

    protected void init() {
        flagShowProgressBar = true;
        pm.ui.UIHelper.buildPanel(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        this.add(getSplitPane(), gbc);
    }

    private Component getSplitPane() {
        splitPane = pm.ui.UIHelper.createSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(buildTopPanel());
        splitPane.setBottomComponent(emptyBottomPanel());
        splitPane.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT));
        return splitPane;
    }

    protected Component emptyBottomPanel() {
        JPanel bottomPanel = createChildPanel();
        bottomPanel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - UIHelper.SPLITHEIGHT));
        return bottomPanel;
    }


    protected abstract Component buildTopPanel();
}
