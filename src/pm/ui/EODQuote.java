package pm.ui;

import pm.action.QuoteManager;
import pm.ui.table.QuoteTableDisplay;
import pm.vo.QuoteVO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static pm.ui.table.QuoteTableDisplay.*;


/**
 * @author Thiyagu
 * @version $Id: EODQuote.java,v 1.1 2008/01/13 16:36:18 tpalanis Exp $
 * @since 11-Jan-2008
 */
public class EODQuote extends AbstractSplitPanel {

    private PMDatePicker dateButton = PMDatePicker.instanceWithLastQuoteDate();

    public EODQuote() {
        init();
    }

    protected void doDisplay(Object retVal, String actionCommand) {
        JPanel panel = UIHelper.childPanelWithGridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 10, 10), 0, 0);
        String[] fields = {STOCK_CODE, OPEN, HIGH, LOW, CLOSE, CURRENT_PRICE};
        List<QuoteVO> quoteVOs = (List<QuoteVO>) retVal;
        JTable table = new QuoteTableDisplay(fields, quoteVOs).table();
        panel.add(UIHelper.createScrollPane(table), gbc);
        splitPane.setBottomComponent(panel);
    }

    protected Object getData(String actionCommand) {
        return QuoteManager.eodQuotes(dateButton.pmDate());
    }

    protected Component buildTopPanel() {
        JPanel panel = UIHelper.childPanelWithGridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        UIHelper.addComponentWithTitle(panel, gbc, "Date", dateButton);
        gbc.gridx = 2;
        panel.add(getActionButton("Submit"), gbc);
        return panel;
    }
}
