package pm.vo;

import pm.util.PMDate;

/**
 * @author Thiyagu
 * @since 22-Jun-2007
 */
public class SymbolChange {
    private final String oldCode;
    private final String newCode;
    private final PMDate fromDate;

    public SymbolChange(String oldCode, String newCode, PMDate fromDate) {
        this.oldCode = oldCode;
        this.newCode = newCode;
        this.fromDate = fromDate;
    }

    public String getOldCode() {
        return oldCode;
    }

    public String getNewCode() {
        return newCode;
    }

    public PMDate getFromDate() {
        return fromDate;
    }
}
