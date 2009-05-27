/*
 * Created on 01-Mar-2005
 *
 */
package pm.vo;

/**
 * @author thiyagu1
 */
public class PortfolioDetailsVO extends Account implements Comparable {

    private boolean alertEnabled;

    public static PortfolioDetailsVO ALL = new PortfolioDetailsVO("All", -1);

    public PortfolioDetailsVO(String name) {
        this.name = name;
    }

    public PortfolioDetailsVO() {
    }

    public PortfolioDetailsVO(String name, int id) {
        this.name = name;
        this.id = id;
    }


    /**
     * @return Returns the alertEnabled.
     */
    public boolean isAlertEnabled() {
        return alertEnabled;
    }

    /**
     * @param alertEnabled The alertEnabled to set.
     */
    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        return obj instanceof PortfolioDetailsVO && name.equals(((PortfolioDetailsVO) obj).getName());
    }

    public int hashCode() {
        return name.hashCode();
    }

    /* (non-Javadoc)
      * @see java.lang.Comparable#compareTo(java.lang.Object)
      */
    public int compareTo(Object o) {
        if (o == null) return -1;
        PortfolioDetailsVO target = (PortfolioDetailsVO) o;
        return this.name.compareTo(target.name);
    }

    public String toString() {
        return name;
    }
}
