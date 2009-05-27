/*
 * Created on Oct 13, 2004
 *
 */
package pm.vo;

import org.apache.log4j.Logger;
import pm.util.ApplicationException;
import pm.util.PMDate;
import pm.util.PMDateFormatter;

import java.util.StringTokenizer;

public class TradeVO implements Cloneable, Comparable {

    private int id;
    private int buyId;
    private int sellId;

    private final static String _DELIMITER = ",";

    private final static Logger logger = Logger.getLogger(TradeVO.class);

    protected String stock;

    protected PMDate pDate;

    protected float qty;

    protected float pPrice;

    protected float brokerage;

    protected PMDate sDate = null;

    protected float sPrice = 0f;

    protected String comments = "";

    protected String portfolio = null;

    protected String tradingAc = null;

    protected float divident = 0f;

    protected float weightage = 1f;

    protected boolean dayTrading;

    public TradeVO() {
    }

    /**
     * @param stock
     * @param pDate
     * @param qty
     * @param pPrice
     * @param brokerage
     * @param tradingAc
     * @param portfolio
     */
    public TradeVO(String stock, PMDate pDate, float qty, float pPrice,
                   float brokerage, String tradingAc, String portfolio) {
        this.stock = stock;
        this.pDate = pDate;
        this.qty = qty;
        this.pPrice = pPrice;
        this.brokerage = brokerage;
        this.portfolio = portfolio;
        this.tradingAc = tradingAc;
    }

    public TradeVO(String stock, PMDate pDate, float qty, float pPrice, float brokerage, PMDate sDate, float sPrice, String tradingAc, String portfolio) {
        this.stock = stock;
        this.pDate = pDate;
        this.qty = qty;
        this.pPrice = pPrice;
        this.brokerage = brokerage;
        this.sDate = sDate;
        this.sPrice = sPrice;
        this.portfolio = portfolio;
        this.tradingAc = tradingAc;
    }

    public TradeVO(String line, boolean stockName) {
        StringTokenizer stk = new StringTokenizer(line, _DELIMITER);
        int count = stk.countTokens();
        if (count < 6) {
            logger.error("Stock detail inComplete : " + line);
            return;
        }
        if (stockName) {
            this.stock = stk.nextToken();
        }
        try {
            this.pDate = PMDateFormatter.parseYYYYMMDD(stk.nextToken());
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
        this.qty = Float.parseFloat(stk.nextToken());
        this.pPrice = Float.parseFloat(stk.nextToken());
        this.brokerage = Float.parseFloat(stk.nextToken());
        try {
            String token = stk.nextToken();
            if (!token.equals(" "))
                this.sDate = PMDateFormatter.parseYYYYMMDD(token);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
        this.sPrice = Float.parseFloat(stk.nextToken());
        this.divident = Float.parseFloat(stk.nextToken());
        this.weightage = Float.parseFloat(stk.nextToken());
        if (stk.hasMoreElements())
            this.comments = stk.nextToken();
    }

    /**
     * @param stock The stock to set.
     */
    public void setStock(String stock) {
        this.stock = stock;
    }

    public float getWeightage() {
        return weightage;
    }

    public void setWeightage(float weightage) {
        this.weightage = weightage;
    }

    public String getStockCode() {
        return stock;
    }

    public float getQty() {
        return qty;
    }

    public PMDate getPurchaseDate() {
        return pDate;
    }

    public PMDate getSaleDate() {
        return sDate;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public String getTradingAc() {
        return tradingAc;
    }

    public String getDetails(boolean stockName) {
        StringBuffer sb = new StringBuffer();
        if (stockName)
            sb.append(stock).append(_DELIMITER);
        sb.append(PMDateFormatter.formatYYYYMMDD(pDate)).append(_DELIMITER);
        sb.append(qty).append(_DELIMITER);
        sb.append(pPrice).append(_DELIMITER);
        sb.append(brokerage).append(_DELIMITER);
        if (sDate != null)
            sb.append(PMDateFormatter.formatYYYYMMDD(sDate)).append(_DELIMITER);
        else
            sb.append(" ").append(_DELIMITER);
        sb.append(sPrice).append(_DELIMITER);
        sb.append(divident).append(_DELIMITER);
        sb.append(weightage).append(_DELIMITER);
        sb.append(comments);
        return sb.toString();
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public float getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(float brokerage) {
        this.brokerage = brokerage;
    }

    public void setPurchaseDate(PMDate date) {
        pDate = date;
    }

    public float getPurchasePrice() {
        return pPrice;
    }

    public void setPurchasePrice(float price) {
        pPrice = price;
    }

    public void setSaleDate(PMDate date) {
        sDate = date;
    }

    public float getSalePrice() {
        return sPrice;
    }

    public void setSalePrice(float price) {
        sPrice = price;
    }

    public String getComments() {
        return comments;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public Object clone() {
        TradeVO cloneVO = new TradeVO();
        if (this.stock != null)
            cloneVO.stock = this.stock;
        if (this.portfolio != null)
            cloneVO.portfolio = this.portfolio;
        if (this.sDate != null)
            cloneVO.sDate = (PMDate) this.sDate.clone();
        if (this.tradingAc != null)
            cloneVO.tradingAc = this.tradingAc;
        cloneVO.comments = this.comments;
        cloneVO.brokerage = this.brokerage;
        cloneVO.pDate = (PMDate) this.pDate.clone();
        cloneVO.pPrice = this.pPrice;
        cloneVO.qty = this.qty;
        cloneVO.sPrice = this.sPrice;
        cloneVO.divident = this.divident;
        cloneVO.weightage = this.weightage;
        return cloneVO;
    }

    public float getDivident() {
        return divident;
    }

    public void setDivident(float divident) {
        this.divident = divident;
    }

    public void addComments(String string) {
        if (comments.length() > 0)
            comments = comments + " " + string;
        else
            comments = string;
    }

    public boolean isHolding() {
        return (sDate == null);
    }

    // TODO relook at total cost
    public float getTotalCost() {
        return getPurchasePrice() * getQty();
    }

    public float getSaleValue() {
        return getSalePrice() * getQty();
    }

    public float getNetProfitLoss() {
        if (this.isHolding())
            return 0;
        else
            return this.getSalePrice() * this.getQty()
                    - this.getPurchasePrice() * this.getQty()
                    - this.getBrokerage() + this.getDivident();
    }

    public float getProfitLoss() {
        if (this.isHolding())
            return 0;
        else
            return this.getSalePrice() * this.getQty()
                    - this.getPurchasePrice() * this.getQty()
                    - this.getBrokerage();
    }

    public float getPLPercentage() {
        if (this.isHolding())
            return 0;
        else if (this.getPurchasePrice() == 0)
            return this.getProfitLoss();
        else
            return this.getProfitLoss()
                    / (this.getPurchasePrice() * this.getQty()) * 100f;
    }

    public float getNetPLPercentage() {
        if (this.isHolding())
            return 0;
        else if (this.getPurchasePrice() == 0)
            return this.getNetProfitLoss();
        else
            return this.getNetProfitLoss()
                    / (this.getPurchasePrice() * this.getQty()) * 100f;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Stock Details : ").append(stock);
        sb.append(" ").append(PMDateFormatter.displayFormat(pDate));
        sb.append(" ").append(qty);
        sb.append(" ").append(pPrice);
        if (!isHolding()) {
            sb.append(" ").append(PMDateFormatter.displayFormat(sDate));
            sb.append(" ").append(sPrice);
        }
        sb.append(" ").append(divident);
        sb.append(" ").append(weightage);
        sb.append(" ").append(comments);
        return sb.toString();
    }

    /*
      * (non-Javadoc)
      *
      * @see java.lang.Comparable#compareTo(java.lang.Object)
      */
    public int compareTo(Object o) {
        TradeVO target = (TradeVO) o;
        if (this.getPurchaseDate().before(target.getPurchaseDate()))
            return -1;
        else if (this.getPurchaseDate().after(target.getPurchaseDate()))
            return 1;
        else {
            if (this.getSaleDate() == null)
                return 1;
            else
                return -1;
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeVO tradeVO = (TradeVO) o;

        if (Float.compare(tradeVO.brokerage, brokerage) != 0) return false;
        if (buyId != tradeVO.buyId) return false;
        if (dayTrading != tradeVO.dayTrading) return false;
        if (Float.compare(tradeVO.divident, divident) != 0) return false;
        if (Float.compare(tradeVO.pPrice, pPrice) != 0) return false;
        if (Float.compare(tradeVO.qty, qty) != 0) return false;
        if (Float.compare(tradeVO.sPrice, sPrice) != 0) return false;
        if (sellId != tradeVO.sellId) return false;
        if (!pDate.equals(tradeVO.pDate)) return false;
        if (!portfolio.equals(tradeVO.portfolio)) return false;
        if (sDate != null ? !sDate.equals(tradeVO.sDate) : tradeVO.sDate != null) return false;
        if (!stock.equals(tradeVO.stock)) return false;
        if (!tradingAc.equals(tradeVO.tradingAc)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = buyId;
        result = 31 * result + sellId;
        result = 31 * result + stock.hashCode();
        result = 31 * result + pDate.hashCode();
        result = 31 * result + qty != +0.0f ? Float.floatToIntBits(qty) : 0;
        result = 31 * result + pPrice != +0.0f ? Float.floatToIntBits(pPrice) : 0;
        result = 31 * result + brokerage != +0.0f ? Float.floatToIntBits(brokerage) : 0;
        result = 31 * result + (sDate != null ? sDate.hashCode() : 0);
        result = 31 * result + sPrice != +0.0f ? Float.floatToIntBits(sPrice) : 0;
        result = 31 * result + portfolio.hashCode();
        result = 31 * result + tradingAc.hashCode();
        result = 31 * result + divident != +0.0f ? Float.floatToIntBits(divident) : 0;
        result = 31 * result + (dayTrading ? 1 : 0);
        return result;
    }/*
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((stock == null) ? 0 : stock.hashCode());
        result = PRIME * result + ((pDate == null) ? 0 : pDate.hashCode());
        result = PRIME * result + Float.floatToIntBits(qty);
        result = PRIME * result + Float.floatToIntBits(pPrice);
        result = PRIME * result + Float.floatToIntBits(brokerage);
        result = PRIME * result + ((sDate == null) ? 0 : sDate.hashCode());
        result = PRIME * result + Float.floatToIntBits(sPrice);
        result = PRIME * result
                + ((comments == null) ? 0 : comments.hashCode());
        result = PRIME * result
                + ((portfolio == null) ? 0 : portfolio.hashCode());
        result = PRIME * result
                + ((tradingAc == null) ? 0 : tradingAc.hashCode());
        result = PRIME * result + Float.floatToIntBits(divident);
        result = PRIME * result + Float.floatToIntBits(weightage);
        return result;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TradeVO))
            return false;
        TradeVO target = (TradeVO) obj;
        boolean flag = ((stock == null && target.stock == null) || this.stock
                .equals(target.stock))
                && pDate.equals(target.pDate)
                && qty == target.qty
                && pPrice == target.pPrice
                && brokerage == target.brokerage
                && ((sDate == null && target.sDate == null) || (sDate
                .equals(target.sDate)))
                && sPrice == target.sPrice
                && comments.equals(target.comments)
                && ((portfolio == null && target.portfolio == null) || (portfolio
                .equals(target.portfolio)))
                && ((tradingAc == null && target.tradingAc == null) || (tradingAc
                .equals(target.tradingAc)))
                && divident == target.divident && weightage == target.weightage
                && dayTrading == target.dayTrading;

        if (!flag) {
            System.out.println(stock + "->" + target.stock);
            System.out.println(pDate + "->" + target.pDate);
            System.out.println(pPrice + "->" + target.pPrice);
            System.out.println(qty + "->" + target.qty);
            System.out.println(sDate + "->" + target.sDate);
            System.out.println(sPrice + "->" + target.sPrice);
            System.out.println(brokerage + "->" + target.brokerage);
            System.out.println(comments + "->" + target.comments);
            System.out.println(portfolio + "->" + target.portfolio);
            System.out.println(tradingAc + "->" + target.tradingAc);
            System.out.println(divident + "->" + target.divident);
            System.out.println(weightage + "->" + target.weightage);
            System.out.println(dayTrading + "->" + target.dayTrading);
        }
        return flag;
    }
*/

    public boolean isHolding(PMDate date) {
        return (!pDate.after(date) && (sDate == null || sDate.after(date)));
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public void setTradingAc(String tradingAc) {
        this.tradingAc = tradingAc;
    }

    public int getPDateVal() {
        return pDate.getIntVal();
    }

    public void setPDateVal(int val) {
        pDate = new PMDate(val);
    }

    public int getSDateVal() {
        return sDate.getIntVal();
    }

    public void setSDateVal(int val) {
        if (val != 0) sDate = new PMDate(val);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDayTrading() {
        return dayTrading;
    }

    public void setDayTrading(boolean dayTrading) {
        this.dayTrading = dayTrading;
    }


    public int getBuyId() {
        return buyId;
    }

    public void setBuyId(int buyId) {
        this.buyId = buyId;
    }

    public int getSellId() {
        return sellId;
    }

    public void setSellId(int sellId) {
        this.sellId = sellId;
    }
}
