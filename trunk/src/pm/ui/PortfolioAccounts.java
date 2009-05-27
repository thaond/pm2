package pm.ui;

import pm.action.Controller;
import pm.util.AppConfig;
import pm.vo.PortfolioDetailsVO;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class PortfolioAccounts extends AbstractPMPanel {

    private JTextField createTextField = UIHelper.createTextField("", 10);

    private DefaultListModel listModel = new DefaultListModel();

    private JList portfolioList = UIHelper.createList(listModel, 5);

    public PortfolioAccounts() {
        init();
    }

    private void init() {
        UIHelper.buildPanel(this);
        this.setBorder(UIHelper.createTitledEmptyBorder("Manage Portfolio"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;
        updateList();
        addSelectionActionListener();
        JScrollPane listScrollPane = new JScrollPane(portfolioList);
        listScrollPane.setPreferredSize(new Dimension(100, 100));
        addComponentWithTitle(gbc, "Select default portfolio", listScrollPane);
        addComponentWithTitle(gbc, "New portfolio name", createTextField);
        addComponentWithTitle(gbc, "", getCreateButton());
    }

    private void addSelectionActionListener() {
        portfolioList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    PortfolioDetailsVO detailsVO = (PortfolioDetailsVO) ((JList) e.getSource()).getSelectedValue();
                    AppConfig.saveUpdateConfigDetail(AppConfig.DEFAULT_PORTFOLIO, detailsVO.getName());
                }
            }
        });
    }

    private void updateList() {
        listModel.clear();
        PortfolioDetailsVO selectedItem = null;
        for (PortfolioDetailsVO item : Controller.getPortfolioList()) {
            listModel.addElement(item);
            if (item.getName().equals(AppConfig.DEFAULT_PORTFOLIO.Value)) selectedItem = item;
        }
        if (selectedItem != null) {
            portfolioList.setSelectedValue(selectedItem, true);
        }
    }

    @Override
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal != null) {
            updateList();
        }
    }

    @Override
    protected Object getData(String actionCommand) {
        try {
            if (!createTextField.getText().equals("")) {
                boolean status = Controller.savePortfolio(createTextField.getText());
                if (status) {
                    createTextField.setText("");
                    return status;
                }
            }
        } catch (Exception e1) {
            logger.error(e1, e1);
            UIHelper.displayInformation(createTextField, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private JButton getCreateButton() {
        JButton createButton = UIHelper.createButton("Create");
        createButton.addActionListener(this);
        return createButton;
    }

}
