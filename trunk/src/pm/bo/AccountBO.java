package pm.bo;

import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IAccountDAO;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradingAccountVO;

import java.util.List;

/**
 * Date: Aug 17, 2006
 * Time: 9:09:28 PM
 */
public class AccountBO {

    public List<TradingAccountVO> getTradingAccountVOs() {
        return getDAO().getTradingAccList();
    }

    private IAccountDAO getDAO() {
        return DAOManager.getAccountDAO();
    }

    public List<PortfolioDetailsVO> getPortfolioList() {
        return getDAO().getPorfolioList();
    }

    public boolean savePortfolio(String name) throws Exception {
        PortfolioDetailsVO detailsVO = new PortfolioDetailsVO();
        detailsVO.setName(name);
        getDAO().insertPortfolio(detailsVO);
        return true; //TODO refactor this
    }
}
