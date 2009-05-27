/*
 * Created on Oct 28, 2004
 *
 */
package pm.vo;

import org.apache.log4j.Logger;
import pm.util.AppConst.TRADINGTYPE;
import pm.util.ApplicationException;
import pm.util.Helper;
import pm.util.PMDate;
import pm.util.PMDateFormatter;

import java.text.NumberFormat;
import java.util.StringTokenizer;

/**
 * @author thiyagu1
 */
public class TransactionVO implements Cloneable {
    private int id;
    private PMDate date;
    private String stockCode;
    private TRADINGTYPE action;
    private float qty;
    private float price;
    private String portfolio;
    private boolean dayTrading;
    private String tradingAc;
    private float brokerage;

    private static String _DELIMITER = ",";
    private static Logger logger = Logger.getLogger(TransactionVO.class);

    public TransactionVO() {
    }

    public TransactionVO(PMDate date, String stockCode, TRADINGTYPE action, float qty,
                         float price, float brokerage, String portfolio, String tradingAc, boolean dayTrading) {
        this.date = date;
        this.stockCode = stockCode;
        this.action = action;
        this.qty = qty;
        this.price = price;
        this.brokerage = brokerage;
        this.portfolio = portfolio;
        this.tradingAc = tradingAc;
        this.dayTrading = dayTrading;
    }

    public TransactionVO(String line) throws Exception {
        try {
            StringTokenizer stk = new StringTokenizer(line, _DELIMITER);
            try {
                this.date = PMDateFormatter.parseYYYYMMDD(stk.nextToken());
            } catch (ApplicationException e) {
                logger.error(e, e);
            }
            this.stockCode = stk.nextToken();
            String act = stk.nextToken();
            this.action = TRADINGTYPE.valueOf(act);
            this.qty = NumberFormat.getInstance().parse(stk.nextToken()).floatValue();
            this.price = NumberFormat.getInstance().parse(stk.nextToken()).floatValue();
            this.brokerage = NumberFormat.getInstance().parse(stk.nextToken()).floatValue();
            this.portfolio = stk.nextToken();
            this.tradingAc = stk.nextToken();
            this.dayTrading = stk.hasMoreTokens();
        } catch (Exception e) {
            logger.error("Error while processing : " + line);
            throw e;
        }
    }

    public TRADINGTYPE getAction() {
        return action;
    }

    public PMDate getDate() {
        return date;
    }

    public boolean isDayTrading() {
        return dayTrading;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public float getPrice() {
        return price;
    }

    public float getQty() {
        return qty;
    }

    public String getStockCode() {
        return stockCode;
    }

    public String getActionString() {
        return action.name();
    }

    public void setActionString(String name) {
        action = TRADINGTYPE.valueOf(name);
    }

    public String getDetails() {
        StringBuffer sb = new StringBuffer();
        sb.append(PMDateFormatter.formatYYYYMMDD(date)).append(_DELIMITER);
        sb.append(stockCode).append(_DELIMITER);
        sb.append(action.name()).append(_DELIMITER);
        sb.append(qty).append(_DELIMITER);
        sb.append(price).append(_DELIMITER);
        sb.append(Helper.formatFloat(brokerage)).append(_DELIMITER);
        sb.append(portfolio).append(_DELIMITER);
        sb.append(tradingAc);
        if (dayTrading) sb.append(_DELIMITER).append("DT");

        return sb.toString();
    }

    /**
     * Used for FundTransaction Detail
     *
     * @return
     */
    public String getAbsDetails() {
        StringBuffer sb = new StringBuffer();
        sb.append(stockCode).append(_DELIMITER);
        sb.append(qty).append(_DELIMITER);
        sb.append(price);
        return sb.toString();
    }

    public String toString() {
        return "TransactionVO{" +
                "id=" + id +
                ", date=" + date +
                ", stockCode='" + stockCode + '\'' +
                ", action=" + action +
                ", qty=" + qty +
                ", price=" + price +
                ", portfolio='" + portfolio + '\'' +
                ", dayTrading=" + dayTrading +
                ", tradingAc='" + tradingAc + '\'' +
                ", brokerage=" + brokerage +
                '}';
    }

    public String getTradingAc() {
        return tradingAc;
    }

    public void setTradingAc(String tradingAc) {
        this.tradingAc = tradingAc;
    }

    public void setBrokerage(float brokerage) {
        this.brokerage = brokerage;
    }

    public float getBrokerage() {
        return brokerage;
    }

    public void setDate(PMDate date) {
        this.date = date;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public void setAction(TRADINGTYPE action) {
        this.action = action;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public void setDayTrading(boolean dayTrading) {
        this.dayTrading = dayTrading;
    }

    public int getDateVal() {
        return date.getIntVal();
    }

    public void setDateVal(int val) {
        date = new PMDate(val);
    }


    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((date == null) ? 0 : date.hashCode());
        result = PRIME * result + ((stockCode == null) ? 0 : stockCode.hashCode());
        result = PRIME * result + ((action == null) ? 0 : action.hashCode());
        result = PRIME * result + Float.floatToIntBits(qty);
        result = PRIME * result + Float.floatToIntBits(price);
        result = PRIME * result + ((portfolio == null) ? 0 : portfolio.hashCode());
        result = PRIME * result + (dayTrading ? 1231 : 1237);
        result = PRIME * result + ((tradingAc == null) ? 0 : tradingAc.hashCode());
        result = PRIME * result + Float.floatToIntBits(brokerage);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        final TransactionVO other = (TransactionVO) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (stockCode == null) {
            if (other.stockCode != null)
                return false;
        } else if (!stockCode.equals(other.stockCode))
            return false;
        if (action == null) {
            if (other.action != null)
                return false;
        } else if (!action.equals(other.action))
            return false;
        if (Float.floatToIntBits(qty) != Float.floatToIntBits(other.qty))
            return false;
        if (Float.floatToIntBits(price) != Float.floatToIntBits(other.price))
            return false;
        if (portfolio == null) {
            if (other.portfolio != null)
                return false;
        } else if (!portfolio.equals(other.portfolio))
            return false;
        if (dayTrading != other.dayTrading)
            return false;
        if (tradingAc == null) {
            if (other.tradingAc != null)
                return false;
        } else if (!tradingAc.equals(other.tradingAc))
            return false;
        if (Float.floatToIntBits(brokerage) != Float.floatToIntBits(other.brokerage))
            return false;
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public Object clone() {
        TransactionVO clonedObject = new TransactionVO();
        clonedObject.date = this.date;
        clonedObject.stockCode = this.stockCode;
        clonedObject.action = this.action;
        clonedObject.qty = this.qty;
        clonedObject.price = this.price;
        clonedObject.portfolio = this.portfolio;
        clonedObject.dayTrading = this.dayTrading;
        clonedObject.tradingAc = this.tradingAc;
        clonedObject.brokerage = this.brokerage;
        return clonedObject;
    }

    public float getValue() {
        float value = qty * price;
        value += (action == TRADINGTYPE.Buy) ? brokerage : -brokerage;
        return value;
    }
}
