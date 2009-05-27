/*
 * Created on 18-Jan-2005
 *
 */
package pm.ui;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import pm.action.Controller;
import static pm.ui.UIHelper.*;
import pm.util.AppConfig;
import pm.util.AppConst.enumLogLevel;
import pm.util.AppConst.enumQServer;
import pm.vo.StockVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Enumeration;

/**
 * @author thiyagu1
 */
public class Settings extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField proxyServerField = new JTextField();
    private JTextField proxyPortField = new JTextField();
    private JButton dataFileDirButton = new JButton();
    private JFormattedTextField alertSleepTime = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private JFormattedTextField marketOpenTimeHH = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private JFormattedTextField marketOpenTimeMM = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private JFormattedTextField marketCloseTimeHH = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private JFormattedTextField marketCloseTimeMM = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private JTextField mailServer = new JTextField();
    private JTextField toMailId = new JTextField();
    private JTextField fromMailId = new JTextField();
    private JTextField mailSubject = new JTextField();
    private JButton historicDataDir = new JButton();
    private JCheckBox EODRunFlag;
    private JFormattedTextField EODRunHH = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private JFormattedTextField EODRunMM = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private JCheckBox liveQuoteFlag;
    private JCheckBox useProxyFlag;
    private JCheckBox useDefaultDirFlag;
    private JButton logFileDirButton = new JButton();
    private JButton defaultBaseDir = new JButton();


    private ButtonGroup bg = new ButtonGroup();
    private ButtonGroup logBG = new ButtonGroup();
    private JComboBox stocklist = UIHelper.createStockVOlistIncIndexJCB();

    public Settings() {
        init();
    }

    private void init() {
        buildPanel(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 20, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.ipadx = 20;
        this.add(getAlertPanel(), gbc);
        gbc.ipadx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        this.add(getQuoteServerPanel(), gbc);
        gbc.gridx = 1;
        gbc.ipady = 4;
        this.add(getEODPanel(), gbc);
        gbc.ipady = 0;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.ipadx = 58;
        this.add(getProxyPanel(), gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 2;
//        gbc.ipadx = 58;
        this.add(getHomePageSettings(), gbc);
        gbc.ipady = 28;
        gbc.ipadx = 0;
        gbc.gridy = 1;
        this.add(getLogPanel(), gbc);
        gbc.ipady = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.gridx = 0;
        gbc.gridy = 4;
        this.add(getSaveButton(), gbc);
    }

    private Component getHomePageSettings() {
        JPanel panel = createTitledPanel("Home Page Settings");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(UIHelper.createLabel("Chart"), gbc);
        gbc.gridx = 1;
        StockVO stockVO = Controller.findStock(AppConfig.HP_CHART_STOCKCODE.Value);
        stocklist.setSelectedItem(stockVO);
        panel.add(stocklist, gbc);
        return panel;
    }

    private Component getLogPanel() {
        JPanel panel = createTitledPanel("Log Level");

        String strQS = AppConfig.Log_Level.Value;
        enumLogLevel selected;
        try {
            selected = enumLogLevel.valueOf(strQS);
        } catch (IllegalArgumentException e) {
            selected = enumLogLevel.Debug;
        }
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        int col = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        for (enumLogLevel loglevel : enumLogLevel.values()) {
            gbc.gridx = col++;
            JRadioButton jrButton = createRadioButton(loglevel.name(), loglevel.name());
            if (selected == loglevel) {
                jrButton.setSelected(true);
            }
            panel.add(jrButton, gbc);
            logBG.add(jrButton);
        }
        return panel;
    }

    private Component getProxyPanel() {
        JPanel panel = createTitledPanel("ProxyServer");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;
        gbc.gridx = 0;
        boolean enable = Boolean.parseBoolean(AppConfig.useProxy.Value);
        useProxyFlag = createCheckBox("Use proxy", enable);
        useProxyFlag.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean enable = ((JCheckBox) e.getSource()).isSelected();
                proxyServerField.setEnabled(enable);
                proxyPortField.setEnabled(enable);
            }
        });
        panel.add(useProxyFlag, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(UIHelper.createLabel("Hostname"), gbc);
        gbc.gridx = 1;
        panel.add(UIHelper.buildTextField(proxyServerField, AppConfig.proxyServer.Value, 12), gbc);
        gbc.gridx = 2;
        panel.add(UIHelper.createLabel("Port"), gbc);
        gbc.gridx = 3;
        panel.add(UIHelper.buildTextField(proxyPortField, AppConfig.proxyPort.Value, 3), gbc);
        proxyServerField.setEnabled(enable);
        proxyPortField.setEnabled(enable);
        return panel;
    }

    private Component getEODPanel() {
        JPanel panel = createTitledPanel("EOD Download");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        boolean enable = Boolean.parseBoolean(AppConfig.EODRunFlag.Value);
        EODRunFlag = createCheckBox("Run EOD Ops", enable);
        EODRunFlag.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean enable = ((JCheckBox) e.getSource()).isSelected();
                EODRunHH.setEnabled(enable);
                EODRunMM.setEnabled(enable);
            }
        });
        panel.add(EODRunFlag, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(createLabel("EODRun - HH"), gbc);
        gbc.gridx = 1;
        panel.add(UIHelper.buildIntField(EODRunHH,
                AppConfig.EODRunHH.Value, 2,
                "Enter EOD run hour(0..23)"), gbc);
        gbc.gridx = 2;
        panel.add(createLabel("MM"), gbc);
        gbc.gridx = 3;
        panel.add(UIHelper.buildIntField(EODRunMM,
                AppConfig.EODRunMM.Value, 2,
                "Enter EOD run minutes(0..59)"), gbc);
        EODRunHH.setEnabled(enable);
        EODRunMM.setEnabled(enable);
        return panel;
    }

    private Component getAlertPanel() {
        JPanel panel = createTitledPanel("Alert");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(UIHelper.createLabel("Alert Interval"), gbc);
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.gridx = 1;
        panel.add(UIHelper.buildIntField(alertSleepTime,
                AppConfig.alertSleepTime.Value, 2,
                "Enter Sleeptime in minutes(1..600)"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(UIHelper.createLabel("Market Open - HH"), gbc);
        gbc.gridx = 1;
        panel.add(UIHelper.buildIntField(marketOpenTimeHH,
                AppConfig.marketOpenTimeHH.Value, 2,
                "Enter Market open hour(0..23)"), gbc);
        gbc.gridx = 2;
        panel.add(UIHelper.createLabel("MM"), gbc);
        gbc.gridx = 3;
        panel.add(UIHelper.buildIntField(marketOpenTimeMM,
                AppConfig.marketOpenTimeMM.Value, 2,
                "Enter Market open minutes(0..59)"), gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(UIHelper.createLabel("Market Close - HH"), gbc);
        gbc.gridx = 1;
        panel.add(UIHelper.buildIntField(marketCloseTimeHH,
                AppConfig.marketCloseTimeHH.Value, 2,
                "Enter Market open hour(0..23)"), gbc);
        gbc.gridx = 2;
        panel.add(UIHelper.createLabel("MM"), gbc);
        gbc.gridx = 3;
        panel.add(UIHelper.buildIntField(marketCloseTimeMM,
                AppConfig.marketCloseTimeMM.Value, 2,
                "Enter Market open minutes(0..59)"), gbc);
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(UIHelper.createLabel("MailServer"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        panel.add(UIHelper.buildTextField(mailServer, AppConfig.mailServer.Value, 20), gbc);
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(UIHelper.createLabel("Mail To"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        panel.add(UIHelper.buildTextField(toMailId, AppConfig.toMailId.Value, 20), gbc);
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(UIHelper.createLabel("Mail From"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        panel.add(UIHelper.buildTextField(fromMailId, AppConfig.fromMailId.Value, 20), gbc);
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(UIHelper.createLabel("Mail Subject"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 4;
//        gbc.insets = new Insets(2,2,2,25);
        panel.add(UIHelper.buildTextField(mailSubject, AppConfig.mailSubject.Value, 20), gbc);
        gbc.gridwidth = 1;
        return panel;
    }

    private Component getSaveButton() {
        JButton saveButton = createButton("Save");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AppConfig.quoteServer.Value = bg.getSelection().getActionCommand();
                AppConfig.EODRunFlag.Value = Boolean.toString(EODRunFlag.isSelected());
                AppConfig.EODRunHH.Value = EODRunHH.getText();
                AppConfig.EODRunMM.Value = EODRunMM.getText();
                AppConfig.alertSleepTime.Value = alertSleepTime.getText();
                AppConfig.marketOpenTimeHH.Value = marketOpenTimeHH.getText();
                AppConfig.marketOpenTimeMM.Value = marketOpenTimeMM.getText();
                AppConfig.marketCloseTimeHH.Value = marketCloseTimeHH.getText();
                AppConfig.marketCloseTimeMM.Value = marketCloseTimeMM.getText();
                AppConfig.mailServer.Value = mailServer.getText();
                AppConfig.mailSubject.Value = mailSubject.getText();
                AppConfig.fromMailId.Value = fromMailId.getText();
                AppConfig.toMailId.Value = toMailId.getText();
                AppConfig.proxyServer.Value = proxyServerField.getText();
                AppConfig.proxyPort.Value = proxyPortField.getText();
                AppConfig.liveQuote.Value = Boolean.toString(liveQuoteFlag.isSelected());
                AppConfig.useProxy.Value = Boolean.toString(useProxyFlag.isSelected());
                AppConfig.Log_Level.Value = logBG.getSelection().getActionCommand();
                Object objStockCode = stocklist.getSelectedItem();
                if (objStockCode != null) AppConfig.HP_CHART_STOCKCODE.Value = ((StockVO) objStockCode).getStockCode();
                Logger logger = Logger.getRootLogger();
                logger.setLevel(Level.toLevel(AppConfig.Log_Level.Value));

                if (!AppConfig.saveConfigDetails()) {
                    logger.error("Congfig save failed");
                    UIHelper.displayInformation(null, "Congfig save failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return saveButton;
    }

    private Component getQuoteServerPanel() {
        JPanel panel = createTitledPanel("Quote Server");

        String strQS = AppConfig.quoteServer.Value;
        enumQServer selected;
        try {
            selected = enumQServer.valueOf(strQS);
        } catch (IllegalArgumentException e) {
            selected = enumQServer.ICICI;
        }
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        boolean liveQuote = Boolean.parseBoolean(AppConfig.liveQuote.Value);
        liveQuoteFlag = createCheckBox("Use live Quote", liveQuote);
        liveQuoteFlag.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean enable = ((JCheckBox) e.getSource()).isSelected();
                Enumeration<AbstractButton> eButtons = bg.getElements();
                for (; eButtons.hasMoreElements(); eButtons.nextElement().setEnabled(enable)) {
                    ;
                }
            }
        });
        panel.add(liveQuoteFlag, gbc);
        int col = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        for (enumQServer server : enumQServer.values()) {
            gbc.gridx = col++;
            JRadioButton jrButton = createRadioButton(server.name(), server.name());
            if (selected == server) {
                jrButton.setSelected(true);
            }
            jrButton.setEnabled(liveQuote);
            panel.add(jrButton, gbc);
            bg.add(jrButton);
        }

        return panel;
    }

    private Component getDirButton(JButton button, String value, final String selectText) {
        buildButton(button);
        button.setText(value);
        button.setPreferredSize(new Dimension(136, 25));
        final JFileChooser jfc = new JFileChooser(new File(value));
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int retVal = jfc.showDialog(Settings.this, selectText);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    String newPath = jfc.getSelectedFile().getAbsolutePath();
                    ((JButton) e.getSource()).setText(newPath);
                }
            }
        });
        return button;
    }
}
