package pm.dao.ibatis.dao;

import pm.vo.Account;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradingAccountVO;

import java.util.List;

/**
 * Date: Aug 12, 2006
 * Time: 3:54:14 PM
 */
public interface IAccountDAO {
    public List<PortfolioDetailsVO> getPorfolioList();

    public void insertPortfolio(PortfolioDetailsVO detailsVO);

    public void updatePortfolio(PortfolioDetailsVO detailsVO);

    public List<TradingAccountVO> getTradingAccList();

    public void updateTradingAcc(Account tradingAc);

    public void insertTradingAcc(Account tradingAc);

    TradingAccountVO tradingAcc(int id);

    PortfolioDetailsVO portfolio(int id);

    TradingAccountVO tradingAcc(String name);

    PortfolioDetailsVO portfolio(String name);
}
