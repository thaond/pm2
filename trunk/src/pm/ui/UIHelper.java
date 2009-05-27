/*
 * Created on Oct 19, 2004
 *
 */
package pm.ui;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTaskPane;
import pm.action.Controller;
import pm.print.JComponentVista;
import pm.ui.table.PMTableCellRenderer;
import pm.ui.table.PMTableMouseListener;
import pm.util.AppConfig;
import pm.util.DropDownWrapper;
import pm.util.Helper;
import pm.util.enumlist.TASKNAME;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradingAccountVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class UIHelper {
    public static final int HEIGHT = 500;

    public static final int SPLITHEIGHT = 50;

    public static final int WIDTH = 750;

    public static final Dimension DIMENSION_TABBEDPANE = new Dimension(750, 500);

    public static final boolean colorFlag = false;

    public final static Color COLOR_BEIGE = new Color(245, 245, 220);
    public final static Color COLOR_LINE_PRINTER = new Color(0xCC, 0xCC, 0xFF);
    public final static Color COLOR_CLASSIC_LINE_PRINTER = new Color(0xCC, 0xFF, 0xCC);
    public final static Color COLOR_FLORAL_WHITE = new Color(255, 250, 240);
    public final static Color COLOR_QUICKSILVER = new Color(0xF0, 0xF0, 0xE0);
    public final static Color COLOR_GENERIC_GRAY = new Color(229, 229, 229);
    public final static Color COLOR_LEDGER = new Color(0xF5, 0xFF, 0xF5);
    public final static Color COLOR_NOTEPAD = new Color(0xFF, 0xFF, 0xCC);

    public static final Color COLOR_FG_OPTION = java.awt.Color.blue;

    public static final Color COLOR_BG_TABLE_DEFAULT = new Color(169, 218, 232);

    public static final Color COLOR_FG_LABEL = new java.awt.Color(0, 0, 0);// (247,115,58);

    public static final Color COLOR_BG_PANEL = new java.awt.Color(205, 223, 236);// (88,174,220);//(169,218,232);

    public static final Color COLOR_BG_BUTTON = new java.awt.Color(200, 221,
            232);// (208,232,242);//(25,93,141);//(89,148,162);

    public static final Color COLOR_BG_LIST_SELECTION = new Color(126, 157, 166);

    public static final Color COLOR_BG_MENU = new java.awt.Color(230, 230, 230);// (8,154,209);

    public static final Color COLOR_BG_MENU_SELECTION = new java.awt.Color(3, 112, 153);

    public static final Color COLOR_BG_TABEL = new java.awt.Color(169, 218, 232);

    public static final Color COLOR_BG_PROGRESSBAR = java.awt.Color.white;

    public static final Color COLOR_BG_COMBOBOX = new java.awt.Color(238, 238,
            238);// java.awt.Color.white;

    public static final Color COLOR_FG_SPECIAL_LABEL = java.awt.Color.blue;

    public static final Color COLOR_FG_TITLE = new java.awt.Color(8, 86, 144);

    public static final Font FONT_TITLE_PROGRESSBAR = new java.awt.Font(
            "Palatino Linotype", java.awt.Font.BOLD | java.awt.Font.ITALIC, 12);

    public static final Font FONT_TEXT_BUTTON = new java.awt.Font(
            "Clarendon Light", java.awt.Font.PLAIN, 14);

    public static final Font FONT_STOCKCODE_DISPLAY = new java.awt.Font(
            "Clarendon Light", java.awt.Font.PLAIN, 12);

    public static final Font FONT_TITLE = new java.awt.Font("Trebuchet MS",
            java.awt.Font.PLAIN, 16);

    public static final Font FONT_TABLE_DATA = new java.awt.Font("Tahoma",
            java.awt.Font.PLAIN, 10);

    public static final Font FONT_TASK_DATA = new java.awt.Font("Tahoma",
            java.awt.Font.PLAIN, 12);

    public static final Font FONT_TASK_PANE_TITLE = new java.awt.Font(
            "Trebuchet MS", java.awt.Font.PLAIN, 12);

    public static final Dimension DIMENSION_COMBOBOX = new Dimension(120, 23);

    public static final String _PROTOTYPE_DISPLAY_VALUE = "AAAAAAAAAAAA";

    public static final Font FONT_MENU = new java.awt.Font(
            "Trebuchet MS", java.awt.Font.PLAIN, 12);

    private static Logger logger = Logger.getLogger(UIHelper.class);
    public static final Color COLOR_PROFIT = new Color(101, 191, 147);
    public static final Color COLOR_LOSS = new Color(245, 132, 124);
    public static final Font TOTAL_FONT = new Font("Arial", Font.BOLD, 12);
    public static final Color TOTAL_BACKGROUND_COLOR = Color.LIGHT_GRAY;
    public static final Color COLOR_TABLE_EDITABLE_CELL = Color.WHITE;

    public static void displayInformation(Component parent, String msg,
                                          String title, int msgType) {
        if (parent == null)
            parent = PortfolioManager.getInstance();
        JOptionPane.showMessageDialog(parent, msg, title, msgType);
    }

    public static JComboBox buildTradingAccountList(JComboBox tradeAcList,
                                                    boolean incAll) {
        if (colorFlag)
            tradeAcList.setBackground(UIHelper.COLOR_BG_COMBOBOX);
        if (incAll)
            tradeAcList.addItem(new DropDownWrapper(TradingAccountVO.ALL));
        for (TradingAccountVO item : Controller.getTradingAcList())
            tradeAcList.addItem(new DropDownWrapper(item));
        tradeAcList.setPrototypeDisplayValue(UIHelper._PROTOTYPE_DISPLAY_VALUE);
        tradeAcList.setPreferredSize(UIHelper.DIMENSION_COMBOBOX);
        return tradeAcList;
    }

    public static void addComponentWithTitle(JPanel panel, GridBagConstraints gbc, String title, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIHelper.createLabel(title), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(component, gbc);
    }

    public static JComboBox buildPortfolioList(JComboBox portfolioList,
                                               boolean incAll) {
        if (colorFlag)
            portfolioList.setBackground(UIHelper.COLOR_BG_COMBOBOX);
        if (incAll)
            portfolioList.addItem(new DropDownWrapper(PortfolioDetailsVO.ALL));
        List<PortfolioDetailsVO> items = Controller.getPortfolioList();
        for (PortfolioDetailsVO item : items) {
            DropDownWrapper wrapper = new DropDownWrapper(item);
            portfolioList.addItem(wrapper);
            if (AppConfig.DEFAULT_PORTFOLIO.Value.equals(item.getName()))
                portfolioList.setSelectedItem(wrapper);
        }
        portfolioList
                .setPrototypeDisplayValue(UIHelper._PROTOTYPE_DISPLAY_VALUE);
        portfolioList.setPreferredSize(UIHelper.DIMENSION_COMBOBOX);
        return portfolioList;
    }

    public static JComboBox createStocklistJCB() {
        return createJCB(Helper.getStockMasterList());
    }

    public static JComboBox createStocklistIncIndexJCB() {
        return createJCB(Helper.getStockListIncIndex());
    }

    public static JComboBox createStockVOlistIncIndexJCB() {
        return createJCB(Controller.stockList(true));
    }

    public static JComboBox createStockVOlistJCB() {
        return createJCB(Controller.stockList(false));
    }

    public static JComboBox createJCB(List data) {
        JComboBox stockField = new JComboBox(new Vector(data));
        buildJCB(stockField);
        return stockField;

    }

    public static void buildJCB(JComboBox stockField) {
        if (colorFlag)
            stockField.setBackground(UIHelper.COLOR_BG_COMBOBOX);
        stockField.addKeyListener(new PMKeyAdapter());
        stockField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                KeyListener[] keyListeners = e.getComponent().getKeyListeners();
                for (KeyListener listener : keyListeners) {
                    if (listener instanceof PMKeyAdapter) {
                        ((PMKeyAdapter) listener).reset();
                    }
                }
            }

            public void focusLost(FocusEvent e) {
            }
        });

        stockField.setPreferredSize(UIHelper.DIMENSION_COMBOBOX);
    }

    public static JLabel createLabel(String text) {
        JLabel jLabel = new JLabel(text);
        if (colorFlag)
            jLabel.setBackground(COLOR_BG_PANEL);
        return jLabel;
    }

    public static JLabel createLabelWithBorder(String text) {
        JLabel jLabel = createLabel(text);
        jLabel.setBorder(new LineBorder(new Color(100, 100, 100)));
        return jLabel;
    }

    public static JButton createButton(String text) {
        return buildButton(new JButton(text));
    }

    public static TaskButton createTaskButton(TASKNAME taskName) {
        TaskButton taskButton = new TaskButton(taskName);
        return taskButton;
    }

    public static JButton buildButton(JButton button) {
        // button.setBackground(COLOR_BG_BUTTON);
        button.setActionCommand(button.getText());
        return button;
    }

    public static JTextArea createTextArea(String text, boolean editable) {
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(editable);
        textArea.setBackground(COLOR_BG_PANEL);
        return textArea;
    }

    public static JTextField createTextField(String text, int length) {
        return buildTextField(new JTextField(), text, length);
    }

    public static JTextField buildTextField(JTextField field, String text,
                                            int length) {
        field.setText(text);
        field.setColumns(length);
        field.setAutoscrolls(true);
        return field;
    }

    public static JFormattedTextField buildIntField(JFormattedTextField field,
                                                    String val, int length, String tooltip) {
        field.setValue(new Long(val));
        field.setColumns(length);
        field.setToolTipText(tooltip);
        return field;
    }

    public static JFormattedTextField createFloatField(float val, int length, String tooltip) {
        return buildFloatField(new JFormattedTextField(NumberFormat.getNumberInstance()), val, length, tooltip);
    }

    public static JFormattedTextField buildFloatField(
            JFormattedTextField field, float val, int length, String tooltip) {
        field.setValue(new Float(val));
        field.setColumns(length);
        field.setToolTipText(tooltip);
        return field;
    }

    public static JCheckBox createCheckBox(String text, boolean isChecked, ActionListener actionListener) {
        JCheckBox checkBox = createCheckBox(text, isChecked);
        checkBox.addActionListener(actionListener);
        return checkBox;
    }

    public static JCheckBox createCheckBox(String text, boolean isChecked) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setActionCommand(text);
        return buildCheckBox(checkBox, isChecked);
    }

    public static JCheckBox buildCheckBox(JCheckBox field, boolean isChecked) {
        field.setSelected(isChecked);
        if (colorFlag)
            field.setBackground(COLOR_BG_PANEL);
        return field;
    }

    public static JTable createTable(TableModel model) {
        JTable table = new JTable(model);
        buildTable(table);
        buildTableWithAppDefaultRenderer(table);
        return table;
    }

    public static JTable createTable() {
        JTable table = new JTable();
        return buildTable(table);
    }

    public static JTable buildTableWithAppDefaultRenderer(JTable table) {
        PMTableCellRenderer tableCellRenderer = new PMTableCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setCellRenderer(tableCellRenderer);
        }
        table.addMouseListener(new PMTableMouseListener(Color.BLACK, Color.BLUE));
        return table;
    }

    private static JTable buildTable(JTable table) {
        table.setShowGrid(true);
        if (colorFlag)
            table.setBackground(UIHelper.COLOR_BG_TABEL);
        return table;
    }

    public static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        if (colorFlag)
            menuBar.setBackground(UIHelper.COLOR_BG_MENU);
        return menuBar;
    }

    public static JMenu createMenu(String title) {
        JMenu menu = new JMenu(title);
        if (colorFlag)
            menu.setBackground(UIHelper.COLOR_BG_MENU);
        menu.setFont(FONT_MENU);
        return menu;
    }

    public static JMenuItem createMenuItem(String title) {
        JMenuItem menuitem = new JMenuItem(title);
        if (colorFlag)
            menuitem.setBackground(UIHelper.COLOR_BG_MENU);
        menuitem.setFont(FONT_MENU);
        return menuitem;
    }

    public static JRadioButton createRadioButton(String text,
                                                 String actionCommand) {
        JRadioButton jrButton = new JRadioButton(text);
        if (colorFlag)
            jrButton.setForeground(UIHelper.COLOR_FG_OPTION);
        if (colorFlag)
            jrButton.setBackground(UIHelper.COLOR_BG_PANEL);
        jrButton.setActionCommand(actionCommand);
        return jrButton;
    }

    public static JPanel createPanel() {
        return buildPanel(new JPanel());
    }

    public static JPanel buildPanel(JPanel panel) {
        if (panel == null)
            panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBounds(0, 23, WIDTH, HEIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        if (colorFlag)
            panel.setBackground(UIHelper.COLOR_BG_PANEL);
        return panel;
    }

    public static JPanel createOptionPanel(String text, Enum[] options,
                                           Enum selected, ButtonGroup bg, ActionListener actionListener) {
        JPanel panel = new JPanel();
        panel
                .setBorder(javax.swing.BorderFactory
                        .createTitledBorder(
                        javax.swing.BorderFactory
                                .createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED),
                        text));
        if (colorFlag) panel.setBackground(UIHelper.COLOR_BG_PANEL);
        panel.setLayout(new GridBagLayout());
        for (Enum opt : options) {
            JRadioButton jrButton = createRadioButton(opt.name(), opt.name());
            if (selected == opt)
                jrButton.setSelected(true);
            if (actionListener != null)
                jrButton.addActionListener(actionListener);
            panel.add(jrButton);
            bg.add(jrButton);
        }
        return panel;
    }

    public static JPanel createChildPanel() {
        return buildChildPanel(new JPanel());
    }

    public static JPanel buildChildPanel(JPanel panel) {
        if (colorFlag) panel.setBackground(UIHelper.COLOR_BG_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder());
        return panel;
    }

    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        if (colorFlag) panel.setBackground(UIHelper.COLOR_BG_PANEL);
        panel
                .setBorder(javax.swing.BorderFactory
                        .createTitledBorder(
                        javax.swing.BorderFactory
                                .createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED),
                        title));
        panel.setLayout(new GridBagLayout());
        return panel;
    }

    public static JTree buildTree(JTree tree) {
        return tree;
    }

    public static JSplitPane createSplitPane(int orientation) {
        JSplitPane splitPane = new JSplitPane(orientation);
        splitPane
                .setMinimumSize(new Dimension(UIHelper.WIDTH, UIHelper.HEIGHT));
        splitPane.setDividerSize(2);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        return splitPane;
    }

    public static JComboBox createComboBox() {
        JComboBox comboBox = new JComboBox();
        buildComboBox(comboBox);
        return comboBox;
    }

    public static JComboBox buildComboBox(JComboBox comboBox) {
        if (colorFlag)
            comboBox.setBackground(UIHelper.COLOR_BG_COMBOBOX);
        comboBox.setPrototypeDisplayValue(UIHelper._PROTOTYPE_DISPLAY_VALUE);
        comboBox.setPreferredSize(UIHelper.DIMENSION_COMBOBOX);
        return comboBox;
    }

    public static JList createList(DefaultListModel model, int visiblerows) {
        JList list = createList(visiblerows);
        list.setModel(model);
        return list;
    }

    public static JList createList(int visiblerows) {
        JList list = new JList();
        list.setVisibleRowCount(visiblerows);
        if (colorFlag)
            list.setBackground(UIHelper.COLOR_BG_PANEL);
        if (colorFlag)
            list.setSelectionBackground(UIHelper.COLOR_BG_LIST_SELECTION);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.addKeyListener(new PMKeyAdapter());
        list.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                KeyListener[] keyListeners = e.getComponent().getKeyListeners();
                for (KeyListener listener : keyListeners) {
                    if (listener instanceof PMKeyAdapter) {
                        ((PMKeyAdapter) listener).reset();
                    }
                }
            }

            public void focusLost(FocusEvent e) {
            }
        });
        return list;
    }

    @SuppressWarnings("unchecked")
    public static void addItemSorted(DefaultListModel modelList, Object[] items) {
        int index = 0;
        int maxLen = modelList.getSize();
        for (int i = 0; i < items.length; i++) {
            Comparable newObject = (Comparable) items[i];
            if (index < maxLen) {
                Comparable object = (Comparable) modelList.elementAt(index);
                while (object.compareTo(newObject) < 0) {
                    index++;
                    if (index == maxLen)
                        break;
                    object = (Comparable) modelList.elementAt(index);
                }
                if (index != maxLen)
                    modelList.add(index, newObject);
                else
                    modelList.addElement(newObject);
            } else
                modelList.addElement(newObject);
        }
    }

    public static JPanel getPrintSavePanel(final JEditorPane editorPane) {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        JButton print = createButton("Print");
        print.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (editorPane == null)
                    return;
                JComponentVista vista = new JComponentVista(editorPane,
                        new PageFormat());
                PrinterJob pj = PrinterJob.getPrinterJob();
                pj.setPageable(vista);
                try {
                    if (pj.printDialog())
                        pj.print();
                } catch (PrinterException e1) {
                    logger.error(e1, e1);
                }
            }
        });
        panel.add(print);
        JButton save = createButton("Save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (editorPane == null)
                    return;
                JFileChooser chooser = new JFileChooser();
                int option = chooser.showSaveDialog(PortfolioManager
                        .getInstance());
                if (option == JFileChooser.APPROVE_OPTION) {
                    PrintWriter pw;
                    try {
                        pw = new PrintWriter(chooser.getSelectedFile());
                        pw.print(editorPane.getText());
                        pw.close();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        panel.add(save);

        return panel;
    }

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat(
            "HH:mm:ss");

    public static String getTime() {
        return TIME_FORMAT.format(new Date());
    }

    public static Component createTitleLabel(String text) {
        JLabel label = createLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(COLOR_FG_TITLE);
        return label;
    }

    public static TitledBorder createTitledEmptyBorder(String title) {
        TitledBorder border1 = new TitledBorder(new EmptyBorder(0, 0, 0, 0),
                title, TitledBorder.CENTER, TitledBorder.ABOVE_TOP,
                UIHelper.FONT_TITLE, UIHelper.COLOR_FG_TITLE);
        return border1;
    }

    public static JTabbedPane createTabbedPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(UIHelper.DIMENSION_TABBEDPANE);
        return tabbedPane;
    }

    public static JXTaskPane createTaskPane(String title) {
        JXTaskPane taskPane = new JXTaskPane();
        taskPane.setFont(FONT_TASK_PANE_TITLE);
        taskPane.setTitle(title);
        taskPane.setBackground(COLOR_BG_PANEL);
        return taskPane;
    }

    public static JProgressBar createHiddenProgressBar() {
        JProgressBar progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setFont(new Font("Courier", Font.PLAIN, 11));
        progressBar.setPreferredSize(new Dimension(100, 14));
        progressBar.setStringPainted(true);
        return progressBar;
    }

    public static JScrollPane createScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    public static JPanel createChildPanelWithGridLayout() {
        JPanel panel = createChildPanel();
        panel.setLayout(new GridLayout());
        return buildChildPanel(panel);
    }

    public static JPanel childPanelWithGridBagLayout() {
        JPanel panel = new JPanel();
        buildChildPanel(panel);
        panel.setLayout(new GridBagLayout());
        return panel;
    }
}
