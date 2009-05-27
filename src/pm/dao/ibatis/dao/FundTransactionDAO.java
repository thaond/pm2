/*
// $Id: FundTransactionDAO.java,v 1.3 2007/12/15 16:10:29 tpalanis Exp $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2004-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import pm.vo.FundTransactionVO;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradingAccountVO;

import java.util.List;

/**
 * @author Thiyagu
 * @version $Id: FundTransactionDAO.java,v 1.3 2007/12/15 16:10:29 tpalanis Exp $
 * @since 14-Aug-2007
 */
public class FundTransactionDAO extends PMDaoTemplate implements IFundTransactionDAO {

    public FundTransactionDAO(DaoManager daoManager) {
        super(daoManager);
    }


    public void perform(FundTransactionVO transaction) {
        super.insert("insertFundTransaction", transaction);
    }

    public List<FundTransactionVO> get(TradingAccountVO tradingAccount, PortfolioDetailsVO portfolioDetails) {
        return super.queryForList("getFundTransactions", addIDsToMap(tradingAccount, portfolioDetails));
    }

    public float balance(TradingAccountVO tradingAccount, PortfolioDetailsVO portfolioDetails) {
        Object balance = super.queryForObject("getBalance", addIDsToMap(tradingAccount, portfolioDetails));
        return balance == null ? 0 : (Float) balance;
    }

    public float totalInvested(TradingAccountVO tradingAccount, PortfolioDetailsVO portfolioDetails) {
        Object balance = super.queryForObject("getInvestedAmount", addIDsToMap(tradingAccount, portfolioDetails));
        return balance == null ? 0 : (Float) balance;
    }
}
