package pm.ui.table;

import pm.ui.UIFactory;
import pm.vo.QuoteVO;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thiyagu
 * @version $Id: QuoteTableDisplay.java,v 1.2 2008/01/23 15:39:24 tpalanis Exp $
 * @since 14-Dec-2007
 */
public class QuoteTableDisplay {

    public static final String STOCK_CODE = "StockCode";
    public static final String OPEN = "Open";
    public static final String HIGH = "High";
    public static final String LOW = "Low";
    public static final String CLOSE = "Close";
    public static final String CURRENT_PRICE = "CurrPrice";
    public static final String PICK_DETAILS = "PickDetails";

    private static Map<String, TableDisplayInput> fieldsMap = new HashMap<String, TableDisplayInput>();

    static {
        fieldsMap.put(STOCK_CODE, new StockCodeDisplayInput());
        fieldsMap.put(OPEN, new TableDisplayInput(OPEN, "getOpen", TableCellDisplay.class));
        fieldsMap.put(HIGH, new TableDisplayInput(HIGH, "getHigh", TableCellDisplay.class));
        fieldsMap.put(LOW, new TableDisplayInput(LOW, "getLow", TableCellDisplay.class));
        fieldsMap.put(CLOSE, new TableDisplayInput(CLOSE, "getClose", TableCellDisplay.class));
        fieldsMap.put(PICK_DETAILS, new TableDisplayInput(PICK_DETAILS, "getPickDetails", TableCellDisplay.class));
        fieldsMap.put(CURRENT_PRICE, new CurrentPriceDisplayInput());
    }

    private List<TableDisplayInput> displayInputs = new ArrayList<TableDisplayInput>();

    private List<QuoteVO> quoteVOs;

    public QuoteTableDisplay(List<QuoteVO> quoteVOs) {
        this(new String[]{STOCK_CODE, OPEN, HIGH, LOW, CLOSE, CURRENT_PRICE, PICK_DETAILS}, quoteVOs);
    }

    public QuoteTableDisplay(String[] fields, List<QuoteVO> quoteVOs) {
        for (String field : fields) {
            displayInputs.add(fieldsMap.get(field));
        }
        this.quoteVOs = quoteVOs;
    }

    public JTable table() {
        return UIFactory.createTable(0, new PMTableModel(quoteVOs, displayInputs, false));
    }

}
