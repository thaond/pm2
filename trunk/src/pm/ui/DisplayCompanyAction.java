package pm.ui;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import pm.action.Controller;
import pm.vo.CompanyActionVO;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static pm.ui.UIHelper.*;

public class DisplayCompanyAction extends AbstractPMPanel {
    private static final int SPLITHEIGHT = 50;

    private JSplitPane splitPane;

    private JComboBox stockField = UIHelper.createStocklistJCB();

    public DisplayCompanyAction() {
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
        splitPane
                .setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT));// (PortfolioManager._WIDTH-10,PortfolioManager._HEIGHT-40));
        return splitPane;
    }

    private Component buildBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 1));
        buildChildPanel(bottomPanel);
        bottomPanel.setMinimumSize(new Dimension(UIHelper.WIDTH,
                UIHelper.HEIGHT - SPLITHEIGHT));
        return bottomPanel;
    }

    @Override
    protected Object getData(String actionCommand) {
        List<CompanyActionVO> actionData = Controller
                .getCompanyActionInfo(stockField.getSelectedItem().toString());
        return actionData;
    }

    @Override
    protected void doDisplay(Object retVal, String actionCommand) {

        if (retVal != null) {
            JXTaskPaneContainer paneContainer = new JXTaskPaneContainer();
            JXTaskPane panel = UIHelper.createTaskPane("Company Action");

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridy = 0;
            gbc.insets = new Insets(1, 1, 1, 1);
            gbc.gridx = 0;

            List<CompanyActionVO> actionInfo = (List<CompanyActionVO>) retVal;
            TreeMap<Integer, List<CompanyActionVO>> sortedList = new TreeMap<Integer, List<CompanyActionVO>>();
            for (CompanyActionVO actionVO : actionInfo) {
                List<CompanyActionVO> actionList = sortedList.get(actionVO.getExDateVal());
                if (actionList == null) {
                    actionList = new ArrayList<CompanyActionVO>();
                }
                actionList.add(actionVO);
                sortedList.put(actionVO.getExDateVal(), actionList);
            }
            for (Integer date : sortedList.keySet()) {
                for (CompanyActionVO actionVO : sortedList.get(date)) {
                    panel.add(createLabel(actionVO.getDisplayMsgWithDate()), gbc);
                    gbc.gridy++;
                }
            }
            paneContainer.add(panel);
            splitPane.setBottomComponent(paneContainer);

        }
    }
}
