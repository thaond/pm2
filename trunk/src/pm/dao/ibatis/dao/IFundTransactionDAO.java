package pm.dao.ibatis.dao;

import pm.vo.FundTransactionVO;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradingAccountVO;

import java.util.List;

/**
 * @author Thiyagu
 * @version $Id: IFundTransactionDAO.java,v 1.3 2007/12/15 16:10:30 tpalanis Exp $
 * @since 14-Aug-2007
 */
public interface IFundTransactionDAO {

    void perform(FundTransactionVO transaction);

    List<FundTransactionVO> get(TradingAccountVO tradingAccount, PortfolioDetailsVO portfolioDetails);

    float balance(TradingAccountVO tradingAccount, PortfolioDetailsVO portfolioDetails);

    float totalInvested(TradingAccountVO tradingAccount, PortfolioDetailsVO portfolioDetails);
}
