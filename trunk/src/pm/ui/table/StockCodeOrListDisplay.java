package pm.ui.table;

import pm.ui.UIHelper;

import java.awt.*;

public class StockCodeOrListDisplay extends StockCodeDisplayInput {


    @Override
    public AbstractTableCellDisplay createDisplayInstance(boolean isTotalCell, Object value) {
        if (value != null) {
            return super.createDisplayInstance(isTotalCell, value);
        } else {
            return new AbstractTableCellDisplay() {

                @Override
                public Comparable getValue() {
                    return "";
                }

                @Override
                public boolean isSpecialDisplay() {
                    return true;
                }

                @Override
                public Component getTableCellRendererComponent(int row) {
                    return UIHelper.createStocklistJCB();
                }

            };
        }

    }
}
