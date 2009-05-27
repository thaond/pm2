/*
 * Created on Dec 21, 2004
 *
 */
package pm.ui.table;

import pm.ui.UIHelper;
import pm.util.Helper;
import pm.util.PMDate;
import pm.util.PMDateFormatter;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author thiyagu1
 */
public class TableCellDisplay extends AbstractTableCellDisplay implements Comparable {
    private String strVal = null;
    private PMDate dateVal = null;
    private Float floatVal = null;
    private int specialDisplay = 0; //-1 Normal to String, 0 Application default
    private String dispStr = "";

    public static TableCellDisplay EMPTYCELL = new TableCellDisplay();

    public TableCellDisplay() {
        isBlank = true;
    }

    public TableCellDisplay(String strVal) {
        this.strVal = strVal;
    }

    public TableCellDisplay(Boolean isTotalCell) {
        isBlank = true;
        this.isTotalCell = isTotalCell;
    }

    public TableCellDisplay(PMDate dateVal, int specialDisplay) {
        this.dateVal = dateVal;
        this.specialDisplay = specialDisplay;
    }

    public TableCellDisplay(PMDate dateVal, Boolean isTotalCell) {
        this.dateVal = dateVal;
        this.isTotalCell = isTotalCell;
    }

    /**
     * @param floatVal
     * @param specialDisplay : -1 toString, 0 FloatFormat, 1 FloatFormat & Color, 2 DispStr & Color
     */
    public TableCellDisplay(float floatVal, int specialDisplay) {
        this.floatVal = floatVal;
        this.specialDisplay = specialDisplay;
    }

    /**
     * @param floatVal
     * @param specialDisplay : -1 toString, 0 FloatFormat, 1 FloatFormat & Color, 2 DispStr & Color
     * @param isTotalCell:   should be true only for total cell
     */
    public TableCellDisplay(float floatVal, int specialDisplay, boolean isTotalCell) {
        this.floatVal = floatVal;
        this.specialDisplay = specialDisplay;
        this.isTotalCell = isTotalCell;
    }

    /**
     * @param floatVal
     * @param specialDisplay : -1 toString, 0 FloatFormat, 1 FloatFormat & Color, 2 DispStr & Color
     * @param dispStr        : This string will be displayed, specialDisplay should be 2
     */
    public TableCellDisplay(float floatVal, int specialDisplay, String dispStr) {
        this.floatVal = floatVal;
        this.specialDisplay = specialDisplay;
        this.dispStr = dispStr;
    }

    public TableCellDisplay(String stockCode, Boolean isTotalCell) {
        this.strVal = stockCode;
        this.isTotalCell = isTotalCell;
    }

    public TableCellDisplay(Float floatVal, Boolean isTotalCell) {
        this.floatVal = floatVal;
        this.isTotalCell = isTotalCell;
    }

    public String toString() {
        if (isBlank()) return "";

        switch (specialDisplay) {
            case -1:
                return (dateVal != null ? dateVal.toString() : Float.toString(floatVal));
            case 0:
            case 1:
                if (dateVal != null) return PMDateFormatter.displayFormat(dateVal);
                if (strVal != null) return strVal;
                return floatVal != null && floatVal != 0f ? Helper.formatFloat(floatVal) : "";
            case 2:
            case 3:
                return dispStr;
        }
        return "";
    }

    public Comparable getValue() {
        if (dateVal != null) return dateVal;
        if (strVal != null) return strVal;
        return floatVal;
    }

    public boolean isSpecialDisplay() {
        return specialDisplay > 0;
    }

    public Component getTableCellRendererComponent(int row) { //this will only process float value

        switch (specialDisplay) {
            case 1:
            case 2:
                DefaultTableCellRenderer cell = new DefaultTableCellRenderer();
                cell.setText(toString());
                if (floatVal > 0) cell.setBackground(UIHelper.COLOR_PROFIT);
                else if (floatVal < 0) cell.setBackground(UIHelper.COLOR_LOSS);
                else {
                    if (isTotalCell) {
                        TotalRowColorHelper.setColor(cell);
                    } else {
                        AlternateRowColorHelper.setColor(cell, row);
                    }
                }
                return cell;
        }
        return null;

    }

    public int compareTo(Object arg0) {
        if (arg0 instanceof TableCellDisplay) {
            TableCellDisplay other = (TableCellDisplay) arg0;
            if (isTotalCell()) return 1;
            if (other.isTotalCell()) return -1;
            Comparable value = getValue();
            Comparable otherValue = other.getValue();
            if (value == null) return 1;
            if (otherValue == null) return -1;
            return value.compareTo(otherValue);
        } else {
            return 0;
        }
    }
}
