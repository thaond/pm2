package pm.vo;

import pm.util.AppConst;
import static pm.util.AppConst.DELIMITER2;
import static pm.util.AppConst.DELIMITER_COMMA;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.util.enumlist.BROKERAGETYPE;

import java.util.StringTokenizer;

public class IPOVO {

    private String ipoCode;
    private float applyQty;
    private float applyPrice;
    private PMDate applyDate, refundedDate;
    private int id;
    private float appliedAmount, refundAmount;
    private Account portfolio, tradingAcc;
    private TransactionVO allotedTransaction;

    public IPOVO() {
    }

    public IPOVO(String ipoCode, PMDate applyDate, float applyQty, float applyPrice, float appliedAmount, Account portfolio, Account tradingAcc) {
        this.ipoCode = ipoCode;
        this.applyQty = applyQty;
        this.applyPrice = applyPrice;
        this.applyDate = applyDate;
        this.appliedAmount = appliedAmount;
        this.portfolio = portfolio;
        this.tradingAcc = tradingAcc;
    }

    public float getApplyPrice() {
        return applyPrice;
    }

    public void setApplyPrice(float price) {
        this.applyPrice = price;
    }

    public float getApplyQty() {
        return applyQty;
    }

    public void setApplyQty(float qty) {
        this.applyQty = qty;
    }

    public String getIpoCode() {
        return ipoCode;
    }

    public void setIpoCode(String ipoCode) {
        this.ipoCode = ipoCode;
    }

    public PMDate getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(PMDate date) {
        this.applyDate = date;
    }

    public String toWrite() {
        StringBuffer sb = new StringBuffer();
        sb.append(ipoCode).append(AppConst.DELIMITER_COMMA);
        sb.append(applyDate.toWrite()).append(AppConst.DELIMITER_COMMA);
        sb.append(applyQty).append(AppConst.DELIMITER_COMMA);
        sb.append(applyPrice).append(AppConst.DELIMITER_COMMA);
        sb.append(appliedAmount).append(DELIMITER_COMMA);
        sb.append(portfolio.getName()).append(DELIMITER_COMMA);
        sb.append(tradingAcc.getName()).append(DELIMITER_COMMA);
        sb.append(AppConst.DELIMITER2);
        if (allotedTransaction != null) {
            sb.append(allotedTransaction.getDate().toWrite()).append(DELIMITER_COMMA);
            sb.append(allotedTransaction.getStockCode()).append(DELIMITER_COMMA);
            sb.append(allotedTransaction.getQty()).append(DELIMITER_COMMA);
            sb.append(allotedTransaction.getPrice()).append(DELIMITER_COMMA);
        } else {
            sb.append(" ").append(DELIMITER_COMMA);
        }
        sb.append(AppConst.DELIMITER2);
        if (refundedDate != null) {
            sb.append(refundedDate.toWrite()).append(AppConst.DELIMITER_COMMA);
            sb.append(refundAmount).append(AppConst.DELIMITER_COMMA);
        }
        return sb.toString();
    }

    public IPOVO(String line) {
        StringTokenizer stk = new StringTokenizer(line, DELIMITER2);
        processApplyString(stk.nextToken());
        processAllotedTransaction(stk.nextToken());
        if (stk.hasMoreTokens()) {
            processRefund(stk.nextToken());
        }
    }

    private void processRefund(String refund) {
        StringTokenizer stk = new StringTokenizer(refund, DELIMITER_COMMA);
        refundedDate = PMDateFormatter.parseYYYYMMDD(stk.nextToken());
        refundAmount = Float.parseFloat(stk.nextToken());
    }

    private void processAllotedTransaction(String allotedTrans) {
        if (allotedTrans.trim().length() > 0) {
            StringTokenizer stk2 = new StringTokenizer(allotedTrans, DELIMITER_COMMA);
            PMDate allotedDate = PMDateFormatter.parseYYYYMMDD(stk2.nextToken());
            String stockCode = stk2.nextToken();
            Float qty = Float.parseFloat(stk2.nextToken());
            Float price = Float.parseFloat(stk2.nextToken());
            allotedTransaction =
                    new TransactionVO(allotedDate, stockCode, AppConst.TRADINGTYPE.IPO, qty, price, 0f, portfolio.getName(), tradingAcc.getName(), false);
        }
    }

    private void processApplyString(String applyString) {
        StringTokenizer stk1 = new StringTokenizer(applyString, DELIMITER_COMMA);
        ipoCode = stk1.nextToken();
        applyDate = PMDateFormatter.parseYYYYMMDD(stk1.nextToken());
        applyQty = Float.parseFloat(stk1.nextToken());
        applyPrice = Float.parseFloat(stk1.nextToken());
        appliedAmount = Float.parseFloat(stk1.nextToken());
        portfolio = new PortfolioDetailsVO(stk1.nextToken());
        tradingAcc = new TradingAccountVO(stk1.nextToken(), BROKERAGETYPE.None);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return getIpoCode();
    }

    public String getStockCode() {
        return allotedTransaction != null ? allotedTransaction.getStockCode() : "";
    }

    public void setStockCode(String stockCode) {
        allotedTransaction.setStockCode(stockCode);
    }

    public float getAllotedPrice() {
        if (allotedTransaction != null) {
            return allotedTransaction.getPrice();
        }
        return 0;
    }

    public void setAllotedPrice(float allotedPrice) {
        allotedTransaction.setPrice(allotedPrice);
    }

    public float getAllotedQty() {
        if (allotedTransaction != null) {
            return allotedTransaction.getQty();
        }
        return 0;
    }

    public void setAllotedQty(float allotedQty) {
        allotedTransaction.setQty(allotedQty);
    }

    public PMDate getAllotedDate() {
        if (allotedTransaction != null) {
            return allotedTransaction.getDate();
        }
        return null;
    }

    public void setAllotedDate(PMDate allotedDate) {
        allotedTransaction.setDate(allotedDate);
    }

    public boolean isAlloted() {
        return allotedTransaction != null;
    }

    public boolean isRefunded() {
        return refundedDate != null;
    }

    public float getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(float refundAmount) {
        this.refundAmount = refundAmount;
    }


    public PMDate getRefundedDate() {
        return refundedDate;
    }

    public void setRefundedDate(PMDate refundedDate) {
        this.refundedDate = refundedDate;
    }

    public float getBalanceAmount() {
        float balance = getAllotedPrice() * getAllotedQty() + refundAmount - applyPrice * applyQty;
        return balance;
    }

    public float getAppliedAmount() {
        return appliedAmount;
    }

    public void setAppliedAmount(float appliedAmount) {
        this.appliedAmount = appliedAmount;
    }

    public Account getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Account portfolio) {
        this.portfolio = portfolio;
    }

    public Account getTradingAcc() {
        return tradingAcc;
    }

    public void setTradingAcc(Account tradingAcc) {
        this.tradingAcc = tradingAcc;
    }

    public int getApplyDateVal() {
        return applyDate.getIntVal();
    }

    public void setApplyDateVal(int val) {
        applyDate = new PMDate(val);
    }

    public int getRefundedDateVal() {
        return refundedDate != null ? refundedDate.getIntVal() : -1;
    }

    public void setRefundedDateVal(int val) {
        if (val > 0) {
            refundedDate = new PMDate(val);
        }

    }

    public TransactionVO getAllotedTransaction() {
        return allotedTransaction;
    }

    public void setAllotedTransaction(TransactionVO allotedTransaction) {
        this.allotedTransaction = allotedTransaction;
    }

    public Integer getAllotmentId() {
        return isAlloted() ? allotedTransaction.getId() : null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final IPOVO ipovo = (IPOVO) o;

        if (Float.compare(ipovo.appliedAmount, appliedAmount) != 0) {
            return false;
        }
        if (Float.compare(ipovo.applyPrice, applyPrice) != 0) {
            return false;
        }
        if (Float.compare(ipovo.applyQty, applyQty) != 0) {
            return false;
        }
        if (!portfolio.equals(ipovo.portfolio)) {
            return false;
        }
        if (Float.compare(ipovo.refundAmount, refundAmount) != 0) {
            return false;
        }
        if (!tradingAcc.equals(ipovo.tradingAcc)) {
            return false;
        }
        if (allotedTransaction != null ? !allotedTransaction.equals(ipovo.allotedTransaction) : ipovo.allotedTransaction != null) {
            return false;
        }
        if (applyDate != null ? !applyDate.equals(ipovo.applyDate) : ipovo.applyDate != null) {
            return false;
        }
        if (ipoCode != null ? !ipoCode.equals(ipovo.ipoCode) : ipovo.ipoCode != null) {
            return false;
        }
        if (refundedDate != null ? !refundedDate.equals(ipovo.refundedDate) : ipovo.refundedDate != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = (ipoCode != null ? ipoCode.hashCode() : 0);
        result = 29 * result + applyQty != +0.0f ? Float.floatToIntBits(applyQty) : 0;
        result = 29 * result + applyPrice != +0.0f ? Float.floatToIntBits(applyPrice) : 0;
        result = 29 * result + (applyDate != null ? applyDate.hashCode() : 0);
        result = 29 * result + (refundedDate != null ? refundedDate.hashCode() : 0);
        result = 29 * result + appliedAmount != +0.0f ? Float.floatToIntBits(appliedAmount) : 0;
        result = 29 * result + refundAmount != +0.0f ? Float.floatToIntBits(refundAmount) : 0;
        result = 29 * result + portfolio.hashCode();
        result = 29 * result + tradingAcc.hashCode();
        result = 29 * result + (allotedTransaction != null ? allotedTransaction.hashCode() : 0);
        return result;
    }

    public boolean isPending() {
        return getBalanceAmount() < 0;
    }

}
