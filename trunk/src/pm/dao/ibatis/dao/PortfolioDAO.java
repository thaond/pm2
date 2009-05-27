package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import pm.vo.PortfolioDetailsVO;
import pm.vo.StopLossVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: Aug 20, 2006
 * Time: 6:46:15 PM
 */
public class PortfolioDAO extends SqlMapDaoTemplate implements IPortfolioDAO {
    /**
     * The DaoManager that manages this Dao instance will be passed
     * in as the parameter to this constructor automatically upon
     * instantiation.
     *
     * @param daoManager
     */
    public PortfolioDAO(DaoManager daoManager) {
        super(daoManager);
    }

    public void insertStopLossVOs(List<StopLossVO> slVOs) {
        for (StopLossVO slVO : slVOs) {
            super.insert("insertStopLoss", slVO);
        }
    }

    public List<StopLossVO> getStopLoss(String portfolioName) {
        Map paramMap = new HashMap();
        if (portfolioName != null) paramMap.put("portfolioName", portfolioName);
        return super.queryForList("getStopLoss", paramMap);
    }

    public StopLossVO getStopLoss(String portfolioName, String stockCode) {
        Map paramMap = new HashMap();
        if (portfolioName != null) paramMap.put("portfolioName", portfolioName);
        if (stockCode != null) paramMap.put("stockCode", stockCode);
        return (StopLossVO) super.queryForObject("getStopLoss", paramMap);
    }

    public void deleteStopLossOf(int portfolioId, int stockId) {
        Map paramMap = new HashMap();
        paramMap.put("portfolioId", portfolioId);
        paramMap.put("stockId", stockId);
        super.delete("deleteStopLoss", paramMap);
    }

    public void updateStopLoss(StopLossVO slVO) {
        super.update("updateStopLoss", slVO);
    }

    public void deleteAllStopLoss(PortfolioDetailsVO detailsVO) {
        super.delete("deleteAllStopLoss", detailsVO.getId());
    }

    public void updateSLStockId(int fromStockId, int toStockId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("from", fromStockId);
        params.put("to", toStockId);
        params.put("table", "STOPLOSS");
        super.update("updateStockID", params);
    }

}
