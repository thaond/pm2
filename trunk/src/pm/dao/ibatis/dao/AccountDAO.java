package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import pm.vo.Account;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradingAccountVO;

import java.util.List;

/**
 * Date: Aug 12, 2006
 * Time: 3:55:36 PM
 */
public class AccountDAO extends SqlMapDaoTemplate implements IAccountDAO {

    public AccountDAO(DaoManager daoManager) {
        super(daoManager);
    }

    public List<PortfolioDetailsVO> getPorfolioList() {
        return (List<PortfolioDetailsVO>) super.queryForList("getPortfolioList", null);
    }

    public void insertPortfolio(PortfolioDetailsVO detailsVO) {
        super.insert("insertPortfolio", detailsVO);
    }

    public void updatePortfolio(PortfolioDetailsVO detailsVO) {
        super.update("updatePortfolio", detailsVO);
    }

    public List<TradingAccountVO> getTradingAccList() {
        return (List<TradingAccountVO>) super.queryForList("getTradingAccList", null);
    }

    public void updateTradingAcc(Account tradingAc) {
        super.update("updateTradingAcc", tradingAc);
    }

    public void insertTradingAcc(Account tradingAc) {
        super.insert("insertTradingAcc", tradingAc);
    }

    public TradingAccountVO tradingAcc(int id) {
        return (TradingAccountVO) super.queryForObject("getTradingAccById", id);
    }

    public PortfolioDetailsVO portfolio(int id) {
        return (PortfolioDetailsVO) super.queryForObject("getPortfolioById", id);
    }

    public TradingAccountVO tradingAcc(String name) {
        return (TradingAccountVO) super.queryForObject("getTradingAccByName", name);
    }

    public PortfolioDetailsVO portfolio(String name) {
        return (PortfolioDetailsVO) super.queryForObject("getPortfolioByName", name);
    }
}
