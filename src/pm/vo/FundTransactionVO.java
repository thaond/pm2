/*
// $Id: FundTransactionVO.java,v 1.3 2008/01/23 15:39:24 tpalanis Exp $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2004-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package pm.vo;

import static pm.util.AppConst.FUND_TRANSACTION_REASON;
import static pm.util.AppConst.FUND_TRANSACTION_TYPE;
import pm.util.PMDate;

/**
 * @author Thiyagu
 * @version $Id: FundTransactionVO.java,v 1.3 2008/01/23 15:39:24 tpalanis Exp $
 * @since 15-Aug-2007
 */
public class FundTransactionVO {

    private static String NO_MORE_DETAIL = "";
    private FUND_TRANSACTION_REASON transactionReason;
    private PMDate date;
    private float amount;
    private TradingAccountVO tradingAccount;
    private PortfolioDetailsVO portfolio;
    private String details;

    public FundTransactionVO() {
    }

    public FundTransactionVO(FUND_TRANSACTION_TYPE transactionType, FUND_TRANSACTION_REASON transactionReason, PMDate date,
                             float amount, TradingAccountVO tradingAccount, PortfolioDetailsVO portfolioDetails, String details) {
        this.transactionReason = transactionReason;
        this.date = date;
        this.amount = transactionType == FUND_TRANSACTION_TYPE.Credit ? amount : -amount;
        this.tradingAccount = tradingAccount;
        this.portfolio = portfolioDetails;
        this.details = details;
    }

    public FundTransactionVO(FUND_TRANSACTION_TYPE transactionType, FUND_TRANSACTION_REASON transactionReason, PMDate date,
                             float amount, TradingAccountVO tradingAccount, PortfolioDetailsVO portfolioDetails) {
        this(transactionType, transactionReason, date, amount, tradingAccount, portfolioDetails, NO_MORE_DETAIL);
    }

    public FUND_TRANSACTION_REASON getTransactionReason() {
        return transactionReason;
    }

    public PMDate getDate() {
        return date;
    }

    public float getAmount() {
        return amount;
    }

    public TradingAccountVO getTradingAccount() {
        return tradingAccount;
    }

    public PortfolioDetailsVO getPortfolio() {
        return portfolio;
    }

    public String getDetails() {
        return details;
    }

    public void setTransactionReason(FUND_TRANSACTION_REASON transactionReason) {
        this.transactionReason = transactionReason;
    }

    public void setDate(PMDate date) {
        this.date = date;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setTradingAccount(TradingAccountVO tradingAccount) {
        this.tradingAccount = tradingAccount;
    }

    public void setPortfolio(PortfolioDetailsVO portfolio) {
        this.portfolio = portfolio;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FundTransactionVO that = (FundTransactionVO) o;

        if (Float.compare(that.amount, amount) != 0) return false;
        if (!date.equals(that.date)) return false;
        if (!details.equals(that.details)) return false;
        if (!portfolio.equals(that.portfolio)) return false;
        if (!tradingAccount.equals(that.tradingAccount)) return false;
        if (transactionReason != that.transactionReason) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = transactionReason.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + amount != +0.0f ? Float.floatToIntBits(amount) : 0;
        result = 31 * result + tradingAccount.hashCode();
        result = 31 * result + portfolio.hashCode();
        result = 31 * result + details.hashCode();
        return result;
    }

    public String tradingAccName() {
        return tradingAccount.getName();
    }

    public String portfolioName() {
        return portfolio.getName();
    }
}
