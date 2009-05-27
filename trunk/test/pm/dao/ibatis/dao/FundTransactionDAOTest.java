/*
// $Id: FundTransactionDAOTest.java,v 1.3 2007/12/15 16:10:31 tpalanis Exp $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2004-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package pm.dao.ibatis.dao;

import pm.util.AppConst;
import pm.util.PMDate;
import pm.vo.FundTransactionVO;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradingAccountVO;

import java.util.List;

/**
 * @author Thiyagu
 * @version $Id: FundTransactionDAOTest.java,v 1.3 2007/12/15 16:10:31 tpalanis Exp $
 * @since 15-Aug-2007
 */
public class FundTransactionDAOTest extends PMDBTestCase {

    public FundTransactionDAOTest(String string) {
        super(string, "TestData.xml");
    }

    public void testPerform_Get() {
        IFundTransactionDAO dao = DAOManager.fundTransactionDAO();
        TradingAccountVO tradingAccount = DAOManager.getAccountDAO().tradingAcc(1);
        PortfolioDetailsVO portfolioDetails = DAOManager.getAccountDAO().portfolio(1);
        FundTransactionVO inputTransaction = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Credit, AppConst.FUND_TRANSACTION_REASON.Deposit,
                new PMDate(), 10000f, tradingAccount, portfolioDetails, "No reason");
        dao.perform(inputTransaction);
        List<FundTransactionVO> storedTransaction = dao.get(tradingAccount, portfolioDetails);
        assertEquals(1, storedTransaction.size());
        assertEquals(inputTransaction, storedTransaction.get(0));
        storedTransaction = dao.get(tradingAccount, DAOManager.getAccountDAO().portfolio(2));
        assertEquals(0, storedTransaction.size());
        storedTransaction = dao.get(null, null);
        assertEquals(1, storedTransaction.size());
    }

    public void testBalance() {
        IFundTransactionDAO dao = DAOManager.fundTransactionDAO();
        TradingAccountVO trading2 = DAOManager.getAccountDAO().tradingAcc(2);
        PortfolioDetailsVO portfolio2 = DAOManager.getAccountDAO().portfolio(2);
        FundTransactionVO crTransaction = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Credit, AppConst.FUND_TRANSACTION_REASON.Deposit,
                new PMDate(), 10000f, trading2, portfolio2, "No reason");
        dao.perform(crTransaction);
        FundTransactionVO drTransaction = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Debit, AppConst.FUND_TRANSACTION_REASON.WithDrawn,
                new PMDate(), 10000f, trading2, portfolio2, "No reason");
        dao.perform(drTransaction);
        assertEquals(0f, dao.balance(trading2, portfolio2));
        TradingAccountVO trading1 = DAOManager.getAccountDAO().tradingAcc(1);
        PortfolioDetailsVO portfolio1 = DAOManager.getAccountDAO().portfolio(1);
        crTransaction = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Credit, AppConst.FUND_TRANSACTION_REASON.Deposit,
                new PMDate(), 200f, trading1, portfolio1, "No reason");
        dao.perform(crTransaction);
        assertEquals(200f, dao.balance(trading1, portfolio1));
        assertEquals(0f, dao.balance(trading2, portfolio2));

        crTransaction = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Credit, AppConst.FUND_TRANSACTION_REASON.Deposit,
                new PMDate(), 600f, trading1, portfolio2, "No reason");
        dao.perform(crTransaction);
        assertEquals(200f, dao.balance(trading1, portfolio1));
        assertEquals(600f, dao.balance(trading1, portfolio2));
        assertEquals(0f, dao.balance(trading2, portfolio2));

        assertEquals(200f, dao.balance(null, portfolio1));
        assertEquals(800f, dao.balance(trading1, null));
        assertEquals(800f, dao.balance(null, null));
    }

    public void testTotalInvested() {
        IFundTransactionDAO dao = DAOManager.fundTransactionDAO();
        TradingAccountVO trading2 = DAOManager.getAccountDAO().tradingAcc(2);
        PortfolioDetailsVO portfolio2 = DAOManager.getAccountDAO().portfolio(2);
        FundTransactionVO crTransaction = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Credit, AppConst.FUND_TRANSACTION_REASON.Deposit,
                new PMDate(), 10000f, trading2, portfolio2, "No reason");
        dao.perform(crTransaction);
        FundTransactionVO drTransaction = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Debit, AppConst.FUND_TRANSACTION_REASON.IPOApply,
                new PMDate(), 10000f, trading2, portfolio2, "No reason");
        dao.perform(drTransaction);
        assertEquals(10000f, dao.totalInvested(trading2, portfolio2));
        drTransaction = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Debit, AppConst.FUND_TRANSACTION_REASON.WithDrawn,
                new PMDate(), 10000f, trading2, portfolio2, "No reason");
        dao.perform(drTransaction);
        assertEquals(0f, dao.totalInvested(trading2, portfolio2));

        TradingAccountVO trading1 = DAOManager.getAccountDAO().tradingAcc(1);
        PortfolioDetailsVO portfolio1 = DAOManager.getAccountDAO().portfolio(1);
        crTransaction = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Credit, AppConst.FUND_TRANSACTION_REASON.Deposit,
                new PMDate(), 200f, trading1, portfolio1, "No reason");
        dao.perform(crTransaction);
        assertEquals(200f, dao.totalInvested(trading1, portfolio1));
        assertEquals(0f, dao.totalInvested(trading2, portfolio2));

        crTransaction = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Credit, AppConst.FUND_TRANSACTION_REASON.OtherIncome,
                new PMDate(), 600f, trading1, portfolio2, "No reason");
        dao.perform(crTransaction);
        assertEquals(0f, dao.totalInvested(trading1, portfolio2));

        crTransaction = new FundTransactionVO(AppConst.FUND_TRANSACTION_TYPE.Credit, AppConst.FUND_TRANSACTION_REASON.Deposit,
                new PMDate(), 600f, trading1, portfolio2, "No reason");
        dao.perform(crTransaction);
        assertEquals(200f, dao.totalInvested(trading1, portfolio1));
        assertEquals(600f, dao.totalInvested(trading1, portfolio2));
        assertEquals(0f, dao.totalInvested(trading2, portfolio2));

        assertEquals(200f, dao.totalInvested(null, portfolio1));
        assertEquals(800f, dao.totalInvested(trading1, null));
        assertEquals(800f, dao.totalInvested(null, null));
    }
}
