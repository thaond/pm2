package pm.datamining.vo;

public class SupportResistanceVO {

    private float price;
    private int supportoccurrence = 0;
    private int resistanceoccurrence = 0;
    private float upperLimit;
    private float lowLimit;

    /**
     * @param price
     */
    public SupportResistanceVO(float price) {
        this.price = price;
        this.upperLimit = price;
        this.lowLimit = price;

    }

    public void incSupportOccurrence(float val) {
        if (lowLimit > val) lowLimit = val;
        supportoccurrence++;
    }

    public void incResistanceOccurrence(float val) {
        if (upperLimit < val) upperLimit = val;
        resistanceoccurrence++;
    }

    public int getSupportOccurrence() {
        return supportoccurrence;
    }

    public int getResistanceOccurrence() {
        return resistanceoccurrence;
    }

    public int getWeightage() {
        return supportoccurrence + resistanceoccurrence;
    }


    /**
     * @return Returns the price.
     */
    public float getPrice() {
        return price;
    }


    public float getLowLimit() {
        return lowLimit;
    }

    public float getUpperLimit() {
        return upperLimit;
    }

    /**
     * @param price The price to set.
     */
    public void setPrice(float price) {
        this.price = price;
    }

    /* (non-Javadoc)
      * @see java.lang.Object#toString()
      */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SupportResistanceVO : ");
        sb.append("{ ").append(lowLimit).append(" - ");
        sb.append(getPrice()).append(" - ");
        sb.append(upperLimit).append(" }");
        sb.append(" S ->").append(supportoccurrence);
        sb.append(" R ->").append(resistanceoccurrence);
        return sb.toString();
    }

}
