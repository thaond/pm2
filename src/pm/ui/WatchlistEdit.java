/*
 * Created on Nov 4, 2004
 *
 */
package pm.ui;

import pm.action.Controller;
import static pm.ui.UIHelper.*;
import pm.util.Helper;
import pm.vo.WatchlistDetailsVO;
import pm.vo.WatchlistVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class WatchlistEdit extends AbstractPMPanel {
    private static final long serialVersionUID = 3257847696936218934L;
    private JList stocklistField;
    private JList watchlistField;
    private JComboBox namelistField;
    private JTextField newNameField = createTextField("", 10);

    public WatchlistEdit() {
        init();
    }

    public void init() {
        buildPanel(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        this.add(getSplitPane(), gbc);
        updateNameListField();
    }

    private Component getSplitPane() {
        JSplitPane splitPane = createSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(buildTopPanel());
        splitPane.setBottomComponent(buildBottomPanel());
        return splitPane;

    }

    private Component buildBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        buildChildPanel(panel);
        panel.setLayout(new GridBagLayout());
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Available List"), gbc);
        gbc.gridx = 2;
        panel.add(createLabel("Selected List"), gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridheight = 10;
        panel.add(getStockList(), gbc);
        gbc.gridx = 2;
        panel.add(getWatchList(), gbc);
        gbc.gridheight = 5;
        gbc.gridy = 1;
        gbc.gridx = 1;
        panel.add(getAddButton(), gbc);
        gbc.gridy = 6;
        panel.add(getRemoveButton(), gbc);
        gbc.gridheight = 1;
        gbc.gridy = 11;
        gbc.gridx = 1;
        panel.add(getSaveButton(), gbc);
        return panel;
    }

    private Component buildTopPanel() {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setLayout(new GridBagLayout());
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("WatchList"), gbc);
        gbc.gridx = 1;
        panel.add(getNameListField(), gbc);
        gbc.insets = new Insets(2, 50, 2, 2);
        gbc.gridx = 2;
        panel.add(createLabel("New WatchList"), gbc);
        gbc.gridx = 3;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel.add(newNameField, gbc);
        gbc.gridx = 4;
        panel.add(getCreateButton(), gbc);
        return panel;
    }

    private Component getNameListField() {
        namelistField = createComboBox();
        namelistField.setActionCommand("NameList");
        namelistField.addActionListener(this);
        return namelistField;
    }

    protected void updateNameListField() {
        Vector<WatchlistDetailsVO> watchlistNames = Controller.getWatchlistNames();
        DefaultComboBoxModel model = new DefaultComboBoxModel(watchlistNames);
        namelistField.setModel(model);
        doAction("NameList");
    }

    protected boolean validateName(String newName) {
        boolean validFlag = !newName.equals("");
        if (validFlag) {
            for (int i = 0; i < newName.length() && validFlag; i++) {
                validFlag = Character.isLetterOrDigit(newName.charAt(i));
            }
        }
        return validFlag;
    }

    private Component getSaveButton() {
        JButton button = createButton("Save");
        button.addActionListener(this);
        return button;
    }

    private Component getCreateButton() {
        JButton button = createButton("Create");
        button.addActionListener(this);
        return button;
    }

    private Component getRemoveButton() {
        JButton button = createButton("Remove");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (namelistField.getSelectedItem() == null) return;
                Object[] object = watchlistField.getSelectedValues();
                DefaultListModel stockListModel = (DefaultListModel) stocklistField.getModel();
                DefaultListModel watchListModel = (DefaultListModel) watchlistField.getModel();
                for (int i = 0; i < object.length; i++) {
                    watchListModel.removeElement(object[i]);
                }
                addItemSorted(stockListModel, object);
            }
        });
        return button;
    }

    private Component getAddButton() {
        JButton button = createButton("Add");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (namelistField.getSelectedItem() == null) return;
                Object[] object = stocklistField.getSelectedValues();
                DefaultListModel stockListModel = (DefaultListModel) stocklistField.getModel();
                DefaultListModel watchListModel = (DefaultListModel) watchlistField.getModel();
                for (int i = 0; i < object.length; i++) {
                    stockListModel.removeElement(object[i]);
                }
                addItemSorted(watchListModel, object);
            }
        });
        return button;
    }

    private Component getStockList() {
        stocklistField = createList(10);
        stocklistField.setPrototypeCellValue(_PROTOTYPE_DISPLAY_VALUE);
        JScrollPane scrollPane = new JScrollPane(stocklistField);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setAutoscrolls(true);
        return scrollPane;
    }

    private Component getWatchList() {
        watchlistField = createList(10);
        watchlistField.setPrototypeCellValue(_PROTOTYPE_DISPLAY_VALUE);
        JScrollPane scrollPane = new JScrollPane(watchlistField);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object)
      */
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal == null) return;
        if (actionCommand.equals("NameList")) {
            List<WatchlistVO> vWatchList = (List) retVal;
            Vector<String> stocklist = Helper.getStockListIncIndex();
            DefaultListModel listModel = new DefaultListModel();
            Set watchlistNameList = new HashSet();
            DefaultListModel wlistModel = new DefaultListModel();
            for (WatchlistVO watchlistVO : vWatchList) {
                watchlistNameList.add(watchlistVO.getStockCode());
                wlistModel.addElement(watchlistVO);
            }
            watchlistField.setModel(wlistModel);

            for (String stockCode : stocklist) {
                if (!watchlistNameList.contains(stockCode)) {
                    listModel.addElement(new WatchlistVO(stockCode));
                }
            }
            stocklistField.setModel(listModel);

        }
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#getData()
      */
    protected Object getData(String actionCommand) {
        if (actionCommand.equals("Create")) {
            String newName = newNameField.getText();
            if (validateName(newName)) {
                if (Controller.createWatchlist(new WatchlistDetailsVO(newName, false))) {
                    newNameField.setText("");
                    updateNameListField();
                } else {
                    logger.error("Error creating Watchlist");
                    displayInformation(null, "Error creating Watchlist", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (actionCommand.equals("Save")) {
            if (namelistField.getSelectedItem() == null) return null;
            if (watchlistField == null) return null;
            DefaultListModel listModel = (DefaultListModel) watchlistField.getModel();
            WatchlistVO[] obj = new WatchlistVO[listModel.size()];
            for (int i = 0; i < listModel.size(); i++) {
                obj[i] = (WatchlistVO) listModel.get(i);
            }
            WatchlistDetailsVO wlName = (WatchlistDetailsVO) namelistField.getSelectedItem();
            boolean status = Controller.saveWatchlist(obj, wlName);
            if (!status) {
                logger.error("Error saving Watchlist");
                UIHelper.displayInformation(null, "Error saving watchlist", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (actionCommand.equals("NameList")) {
            if (namelistField.getSelectedItem() == null) return null;
            WatchlistDetailsVO wlName = (WatchlistDetailsVO) namelistField.getSelectedItem();
            return Controller.getWatchlist(wlName.getId());
        }
        return null;
    }
}
