package pm.ui;

import pm.action.Controller;
import pm.net.HTTPHelper;

import javax.swing.*;
import java.awt.*;

import static pm.ui.UIHelper.buildChildPanel;
import static pm.ui.UIHelper.createSplitPane;

public class DisplayQuote extends AbstractPMPanel {

    private static final long serialVersionUID = 1L;

    private static final int SPLITHEIGHT = 50;
    private JSplitPane splitPane;
    private JPanel bottomPanel;
    private JComboBox stockField = UIHelper.createStocklistJCB();

    public DisplayQuote() {
        super();
        flagShowProgressBar = true;
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
        splitPane = createSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(UIFactory.createTopPanelWithStockList(stockField, getActionButton("Submit")));
        splitPane.setBottomComponent(buildBottomPanel());
        splitPane.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT));//(PortfolioManager._WIDTH-10,PortfolioManager._HEIGHT-40));
        return splitPane;
    }

    private Component buildBottomPanel() {
        bottomPanel = new JPanel(new GridLayout(1, 1));
        buildChildPanel(bottomPanel);
        bottomPanel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - SPLITHEIGHT));
        return bottomPanel;
    }

    @Override
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal != null) {
            bottomPanel = new JPanel(new GridLayout(1, 1));
            buildChildPanel(bottomPanel);
            bottomPanel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - SPLITHEIGHT));
            JEditorPane editorPane = (JEditorPane) retVal;
            JScrollPane editorScrollPane = new JScrollPane(editorPane);
            editorScrollPane.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            editorScrollPane.setPreferredSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - SPLITHEIGHT));
            bottomPanel.add(editorScrollPane);
            splitPane.setBottomComponent(bottomPanel);
        }
    }

    @Override
    protected Object getData(String actionCommand) {
        try {
            String url = Controller.getQuotePage(stockField.getSelectedItem().toString());
            JEditorPane editorPane = new JEditorPane();
            editorPane.setEditable(false);
            HTTPHelper.setProxy();
            editorPane.setPage(url);
            return editorPane;
        } catch (Exception e) {
            logger.error(e, e);
            UIHelper.displayInformation(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

}
