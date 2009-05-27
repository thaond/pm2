/*
 * Created on 04-Mar-2005
 *
 */
package pm.ui;

import pm.action.Controller;
import static pm.ui.UIHelper.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author thiyagu1
 */
public class PortfolioReport extends AbstractSplitPanel {

    private static final long serialVersionUID = 1L;
    private JComboBox portfolioList = new JComboBox();
    private JEditorPane editorPane = new JEditorPane("text/html", "");

    public PortfolioReport() {
        init();
    }

    protected Component buildTopPanel() {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 25, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Porfolio"), gbc);
        gbc.gridx = 1;
        panel.add(buildPortfolioList(portfolioList, false), gbc);
        gbc.gridx = 2;
        panel.add(getSubmitButton(), gbc);
        gbc.gridx = 3;
        gbc.insets = new Insets(2, 100, 2, 2);
        panel.add(getPrintSavePanel(editorPane), gbc);
        editorPane.setEditable(false);
        return panel;
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object, java.lang.String)
      */
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal != null) {
            String data = (String) retVal;
            //System.out.println(data);
            editorPane.setText(data);
            JScrollPane scrollPane = new JScrollPane(editorPane);
            JPanel panel = new JPanel();
            buildChildPanel(panel);
            panel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - 60));
            panel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(2, 2, 2, 2);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
            panel.add(scrollPane, gbc);
            splitPane.setBottomComponent(panel);
        }
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#getData(java.lang.String)
      */
    protected Object getData(String actionCommand) {
        return Controller.getPortfolioEODReport(portfolioList.getSelectedItem().toString());
    }
}
