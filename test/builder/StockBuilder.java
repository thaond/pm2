package builder;

import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.StockVO;

public class StockBuilder {
    private String stockCode = "SomeCode1";
    private String companyName = "SomeCompany";
    private Float faceValue = 10f;
    private SERIESTYPE seriesType = SERIESTYPE.equity;
    private Float paidupValue = 10f;
    private Short marketlot = (short) 1f;
    private String isin = "SomeISIN";
    private PMDate dateOfListing = new PMDate(1, 1, 2006);
    private Boolean listed = true;

    public StockVO build() {
        return new StockVO(stockCode, companyName, faceValue, seriesType, paidupValue, marketlot, isin, dateOfListing, listed);
    }
}
