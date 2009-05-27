package pm.dao.ibatis.dao;

import pm.vo.PortfolioDetailsVO;
import pm.vo.StopLossVO;

import java.util.List;

/**
 * Date: Aug 20, 2006
 * Time: 6:45:57 PM
 */
public interface IPortfolioDAO {

    public void insertStopLossVOs(List<StopLossVO> slVOs);

    public List<StopLossVO> getStopLoss(String portfolioName);

    public StopLossVO getStopLoss(String portfolioName, String stockCode);

    public void deleteStopLossOf(int portfolioId, int stockId);

    public void updateStopLoss(StopLossVO stopLossVO);

    public void deleteAllStopLoss(PortfolioDetailsVO detailsVO);

    void updateSLStockId(int fromStockId, int toStockId);
}
