/*
// $Id: AccountEntity.java,v 1.2 2007/12/15 16:10:44 tpalanis Exp $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2004-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package pm.vo;

/**
 * @author Thiyagu
 * @version $Id: AccountEntity.java,v 1.2 2007/12/15 16:10:44 tpalanis Exp $
 * @since 15-Aug-2007
 */
public class AccountEntity {
    private final TradingAccountVO tradingAccount;
    private final PortfolioDetailsVO portfolioDetails;

    public AccountEntity(TradingAccountVO tradingAccount, PortfolioDetailsVO portfolioDetails) {
        this.tradingAccount = tradingAccount;
        this.portfolioDetails = portfolioDetails;
    }

    public int getTradingAccId() {
        return tradingAccount.getId();
    }

    public int getPortfolioId() {
        return portfolioDetails.getId();
    }
}
