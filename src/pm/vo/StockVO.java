package pm.vo;

import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;


public class StockVO implements Cloneable {

    private int id = -1;
    private String stockCode;
    private String companyName;
    private float faceValue;
    private SERIESTYPE series;
    private float paidupValue;
    private short marketLot;
    private PMDate dateOfListing;
    private boolean listed;

    private String isin;

    public StockVO() {
    }

    public StockVO(String stockCode) {
        this.stockCode = stockCode;
    }

    public StockVO(String stockCode, String companyName, float faceValue, SERIESTYPE seriestype, float paidupValue,
                   short marketLot, String isin, PMDate dateOfListing, boolean listed) {
        this.stockCode = stockCode;
        this.companyName = companyName;
        this.faceValue = faceValue;
        this.series = seriestype;
        this.paidupValue = paidupValue;
        this.marketLot = marketLot;
        this.dateOfListing = dateOfListing;
        this.isin = isin;
        this.listed = listed;
    }

    public StockVO(int id, String stockCode) {
        this.id = id;
        this.stockCode = stockCode;
    }

    public StockVO(int id, String stockCode, String companyName, float faceValue, SERIESTYPE seriestype, float paidupValue,
                   short marketLot, String isin, PMDate dateOfListing, boolean listed) {
        this(stockCode, companyName, faceValue, seriestype, paidupValue, marketLot, isin, dateOfListing, listed);
        this.id = id;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public float getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(short faceValue) {
        this.faceValue = faceValue;
    }

    public SERIESTYPE getSeries() {
        return series;
    }

    public void setSeries(SERIESTYPE series) {
        this.series = series;
    }

    public float getPaidupValue() {
        return paidupValue;
    }

    public void setPaidupValue(short paidupValue) {
        this.paidupValue = paidupValue;
    }

    public short getMarketLot() {
        return marketLot;
    }

    public void setMarketLot(short marketLot) {
        this.marketLot = marketLot;
    }

    public PMDate getDateOfListing() {
        return dateOfListing;
    }

    public void setDateOfListing(PMDate dateOfListing) {
        this.dateOfListing = dateOfListing;
    }

    public boolean isListed() {
        return listed;
    }

    public void setListed(boolean listed) {
        this.listed = listed;
    }

    public short getSeriesVal() {
        return (short) series.ordinal();
    }

    public void setSeriesVal(short val) {
        for (SERIESTYPE series : SERIESTYPE.values()) {
            if (series.ordinal() == val) {
                this.series = series;
                break;
            }
        }
    }

    public short getListedVal() {
        if (listed) return 1;
        else return 0;
    }

    public void setListedVal(short val) {
        listed = (val == 1);
    }

    public int getDateofListingVal() {
        return dateOfListing.getIntVal();
    }

    public void setDateofListingVal(int val) {
        dateOfListing = new PMDate(val);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final StockVO stockVO = (StockVO) o;

        if (Float.compare(stockVO.faceValue, faceValue) != 0) return false;
//        if (id != stockVO.id) return false;
        if (listed != stockVO.listed) return false;
        if (marketLot != stockVO.marketLot) return false;
        if (Float.compare(stockVO.paidupValue, paidupValue) != 0) return false;
        if (companyName != null ? !companyName.equals(stockVO.companyName) : stockVO.companyName != null) return false;
        if (dateOfListing != null ? !dateOfListing.equals(stockVO.dateOfListing) : stockVO.dateOfListing != null)
            return false;
        if (isin != null ? !isin.equals(stockVO.isin) : stockVO.isin != null) return false;
        if (series != stockVO.series) return false;
        if (!stockCode.equals(stockVO.stockCode)) return false;

        return true;
    }

    public int hashCode() {
        int result = 0;
//        result = id;
        result = 29 * result + stockCode.hashCode();
        result = 29 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 29 * result + faceValue != +0.0f ? Float.floatToIntBits(faceValue) : 0;
        result = 29 * result + (series != null ? series.hashCode() : 0);
        result = 29 * result + paidupValue != +0.0f ? Float.floatToIntBits(paidupValue) : 0;
        result = 29 * result + (int) marketLot;
        result = 29 * result + (dateOfListing != null ? dateOfListing.hashCode() : 0);
        result = 29 * result + (listed ? 1 : 0);
        result = 29 * result + (isin != null ? isin.hashCode() : 0);
        return result;
    }


    public String toString() {
        return stockCode;
    }

    public String toPrint() {
        return "StockVO{" +
                "id=" + id +
                ", stockCode='" + stockCode + '\'' +
                ", companyName='" + companyName + '\'' +
                ", faceValue=" + faceValue +
                ", series=" + series +
                ", paidupValue=" + paidupValue +
                ", marketLot=" + marketLot +
                ", dateOfListing=" + dateOfListing +
                ", listed=" + listed +
                ", isin='" + isin + '\'' +
                '}';
    }

    public Object clone() throws CloneNotSupportedException {
        StockVO newStockVO = (StockVO) super.clone();
        newStockVO.id = id;
        newStockVO.stockCode = stockCode;
        newStockVO.companyName = companyName;
        newStockVO.faceValue = faceValue;
        newStockVO.series = series;
        newStockVO.paidupValue = paidupValue;
        newStockVO.marketLot = marketLot;
        newStockVO.dateOfListing = dateOfListing;
        newStockVO.listed = listed;
        newStockVO.isin = isin;
        return newStockVO;
    }
}
