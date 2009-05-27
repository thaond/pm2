/*
 * Created on 01-Feb-2005
 *
 */
package pm.vo;

import java.util.StringTokenizer;

/**
 * @author thiyagu1
 */
public class WatchlistDetailsVO implements Comparable {
    private int id;
    private final static String _DELIMITER = ",";
    private String name;
    private boolean alertEnabled = false;

    public WatchlistDetailsVO() {
    }

    /**
     * @param name
     * @param alertEnabled
     */
    public WatchlistDetailsVO(String name, boolean alertEnabled) {
        this.name = name;
        this.alertEnabled = alertEnabled;
    }

    public WatchlistDetailsVO(String line) {
        StringTokenizer stk = new StringTokenizer(line, _DELIMITER);
        if (stk.hasMoreTokens()) {
            name = stk.nextToken();
            if (stk.hasMoreTokens()) alertEnabled = Boolean.parseBoolean(stk.nextToken());
        } else
            name = line;
    }

    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public boolean isAlertEnabled() {
        return alertEnabled;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    public String getDetails() {
        StringBuffer sb = new StringBuffer();
        sb.append(name).append(_DELIMITER);
        sb.append(alertEnabled);
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final WatchlistDetailsVO detailsVO = (WatchlistDetailsVO) o;

        if (alertEnabled != detailsVO.alertEnabled) return false;
        if (!name.equals(detailsVO.name)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 29 * result + (alertEnabled ? 1 : 0);
        return result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Object o) {
        if (o == null) return -1;
        WatchlistDetailsVO target = (WatchlistDetailsVO) o;
        return hashCode() - target.hashCode();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
