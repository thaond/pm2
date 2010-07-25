package pm.ui;

import pm.action.Controller;
import pm.dao.ibatis.dao.DAOManager;
import pm.util.PMDateFormatter;
import pm.vo.EquityQuote;
import pm.vo.StockVO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static pm.ui.UIHelper.*;


public class DisplayInfo extends JPanel {

    private static final long serialVersionUID = 1L;

    public DisplayInfo() {
        init();
    }

    private void init() {
        buildPanel(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 25, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel title2 = createLabel("Portfolio Manager");
        title2.setFont(new java.awt.Font("Trebuchet MS", java.awt.Font.ITALIC, 24));
        add(title2, gbc);
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("              Version"), gbc);
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel version = createLabel("2.0 (1 Jan 2007)");
        version.setFont(FONT_TITLE_PROGRESSBAR);
        version.setForeground(COLOR_FG_SPECIAL_LABEL);
        add(version, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("          Developed by"), gbc);
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel author = createLabel("Thiyagu P");
        author.setFont(FONT_TITLE_PROGRESSBAR);
        author.setForeground(COLOR_FG_SPECIAL_LABEL);
        add(author, gbc);
        gbc.insets = new Insets(25, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 2;
        gbc.gridy = 3;
        gbc.gridx = 1;
        this.add(getDataPanel(), gbc);
    }

    private Component getInfoPanel() {
        JPanel panel = createPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createLabel("Portfolio Manager Information"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createLabel("Version"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(createLabel("1.0"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createLabel("Author"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel author = createLabel("Thiyagu P");
        author.setFont(UIHelper.FONT_TITLE_PROGRESSBAR);
        panel.add(author, gbc);
        return panel;
    }

    private Component getDataPanel() {
        JPanel panel = createTitledPanel("Data availability");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createLabel("BhavCopy "), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(createLabel(PMDateFormatter.displayFormat(DAOManager.getDateDAO().getLastQuoteDate())), gbc);
        int y = 1;
        List<StockVO> indexCodes = Controller.getIndexCodes();
        for (StockVO stockVO : indexCodes) {

            gbc.gridx = 0;
            gbc.gridy = y++;
            gbc.anchor = GridBagConstraints.EAST;
            panel.add(createLabel(stockVO.getStockCode()), gbc);
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            EquityQuote quote = Controller.getLastQuote(stockVO.getStockCode());
            if (quote != null) {
                panel.add(createLabel(PMDateFormatter.displayFormat(quote.getDate())), gbc);
            }
        }
        return panel;
    }


}
