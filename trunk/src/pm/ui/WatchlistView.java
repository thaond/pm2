/*
 * Created on Nov 17, 2004
 *
 */
package pm.ui;

import pm.action.Controller;
import static pm.ui.UIHelper.*;
import pm.ui.table.TableCellDisplay;
import pm.ui.table.TableSorter;
import pm.vo.WatchlistDetailsVO;
import pm.vo.WatchlistVO;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class WatchlistView extends AbstractPMPanel {
    private static final long serialVersionUID = 3977019530918768953L;
    private JSplitPane splitPane;
    private JComboBox namelistField;
    private JFormattedTextField durationField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private JCheckBox autoRefresh = createCheckBox("Auto Refresh", false);
    private Timer timer = new Timer();
    private JLabel time = createLabel("");

    public WatchlistView() {
        super();
        init();
    }

    public void init() {
        buildPanel(this);
        flagShowProgressBar = true;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        this.add(getSplitPane(), gbc);
        doAction(null);
    }

    private Component getSplitPane() {
        splitPane = createSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(buildTopPanel());
        splitPane.setBottomComponent(buildBottomPanel());
        return splitPane;

    }

    private Component buildTopPanel() {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setLayout(new GridBagLayout());
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        int x = 0, y = 0;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = x++;
        gbc.gridy = y++;
        panel.add(createLabel("WatchList"), gbc);
        gbc.gridx = x++;
        panel.add(getNameListField(), gbc);
        JButton button = createButton("Refresh");
        button.addActionListener(this);
        gbc.insets = new Insets(2, 50, 2, 2);
        gbc.gridx = x++;
        panel.add(button, gbc);
        gbc.gridx = x++;
        panel.add(createLabel("Seconds"), gbc);
        gbc.gridx = x++;
        gbc.insets = new Insets(2, 2, 2, 2);
        durationField.setColumns(3);
        panel.add(durationField, gbc);
        gbc.gridx = x++;
        panel.add(autoRefresh, gbc);
        autoRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (autoRefresh.isSelected()) {
                    int delay = (durationField.getValue() == null ? 0 : ((Number) durationField.getValue()).intValue());
                    if (delay < 60) delay = 60;
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {
                            if (!WatchlistView.this.isEnabled()) {
                                timer.cancel();
                            } else {
                                doAction(null);
                            }
                        }
                    }, 0, delay * 1000);
                    durationField.setEditable(false);
                } else {
                    timer.cancel();
                    timer.purge();
                    durationField.setEditable(true);
                }
            }
        });
        gbc.insets = new Insets(2, 40, 2, 2);
        gbc.gridx = x++;
        panel.add(time, gbc);

        return panel;
    }

    private Component buildBottomPanel() {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, 60));
        panel.setPreferredSize(new Dimension(UIHelper.WIDTH, 60));
        return panel;
    }

    private Component getNameListField() {
        namelistField = createComboBox();
        namelistField.addActionListener(this);
        Vector watchlistNames = Controller.getWatchlistNames();
        DefaultComboBoxModel model = new DefaultComboBoxModel(watchlistNames);
        namelistField.setModel(model);
        return namelistField;
    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#doDisplay(java.lang.Object, java.lang.String)
      */
    protected void doDisplay(Object retVal, String actionCommand) {
        if (retVal == null) return;
        List<WatchlistVO> vWatchList = (List<WatchlistVO>) retVal;
        JPanel panel = new JPanel(new GridLayout(1, 1));
        buildChildPanel(panel);
        panel.setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - 50));
        if (vWatchList.size() != 0) {
            pm.ui.table.TableSorter sorter = new TableSorter(new WLVTableModel(vWatchList));
            JTable table = createTable(sorter);
            sorter.setTableHeader(table.getTableHeader());
            table.setPreferredScrollableViewportSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT - 50));
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane);
        }
        splitPane.setBottomComponent(panel);
        time.setText(UIHelper.getTime());

    }

    /* (non-Javadoc)
      * @see pm.ui.AbstractPMPanel#getData(java.lang.String)
      */
    protected Object getData(String actionCommand) {
        if (namelistField.getSelectedItem() == null) return null;
        WatchlistDetailsVO wlg = (WatchlistDetailsVO) namelistField.getSelectedItem();
        return Controller.getWatchlistView(wlg.getId());
    }
}

class WLVTableModel extends AbstractTableModel {
    private List<WatchlistVO> vData;
    private String[] colName = {"Stock", "CMP", "% Change", "Open", "Low", "High", "Lower Limit", "Upper Limit", "Volume"};

    public WLVTableModel(List<WatchlistVO> data) {
        vData = data;
    }

    public int getColumnCount() {
        return colName.length;
    }

    public int getRowCount() {
        return vData.size();
    }

    public String getColumnName(int column) {
        return colName[column];
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        WatchlistVO watchlistVO = (WatchlistVO) vData.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return watchlistVO.getStockCode();
            case 1:
                return new TableCellDisplay(watchlistVO.getCurrQuote().getLastPrice(), 0);
            case 2:
                return new TableCellDisplay(watchlistVO.getCurrQuote().getPerChange(), 1);
            case 3:
                return new TableCellDisplay(watchlistVO.getCurrQuote().getOpen(), 0);
            case 4:
                return new TableCellDisplay(watchlistVO.getCurrQuote().getLow(), 0);
            case 5:
                return new TableCellDisplay(watchlistVO.getCurrQuote().getHigh(), 0);
            case 6:
                return new TableCellDisplay(watchlistVO.getFloor(), 0);
            case 7:
                return new TableCellDisplay(watchlistVO.getCeil(), 0);
            case 8:
                return new TableCellDisplay(watchlistVO.getCurrQuote().getVolume(), 0);
        }
        return null;
    }

    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
