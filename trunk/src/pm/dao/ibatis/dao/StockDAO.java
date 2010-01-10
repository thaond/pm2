package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import org.apache.log4j.Logger;
import pm.vo.ICICICodeMapping;
import pm.vo.StockVO;

import java.util.List;

/**
 * Date: 22-Jul-2006
 * Time: 10:22:54
 */
public class StockDAO extends SqlMapDaoTemplate implements IStockDAO {

    private static Logger logger = Logger.getLogger(StockDAO.class);

    public StockDAO(DaoManager daoManager) {
        super(daoManager);
    }

    public StockVO getStock(String stockCode) {
        return (StockVO) super.queryForObject("getStock", stockCode);
    }

    public List<StockVO> getStockList(boolean incIndex) {
        if (incIndex) {
            return super.queryForList("getStockListIncIndex", null);
        }
        return super.queryForList("getStockList", null);
    }

    public List<StockVO> getIndexList() {
        return super.queryForList("getIndexList", null);
    }

    public boolean insertStockList(List<StockVO> stockList) {
        if (stockList.isEmpty()) {
            return true;
        }
        super.startBatch();
        for (StockVO stockVO : stockList) {
            if (logger.isInfoEnabled()) {
                logger.info("stockVO" + stockVO);
            }
            insertStock(stockVO);
        }
        int count = super.executeBatch();
        return stockList.size() == count;
    }

    public void insertStock(StockVO stockVO) {
        super.insert("insertStock", stockVO);
    }

    public String iciciCode(String stockCode) {
        return (String) super.queryForObject("getICICICode", stockCode);
    }

    public void updateICICICode(StockVO stockVO, String iciciCode) {
        super.update("updateICICICodeMapping", new ICICICodeMapping(iciciCode, stockVO));
    }

    public String yahooCode(String stockCode) {
        return (String) super.queryForObject("getYahooCode", stockCode);
    }

    public void delete(int stockId) {
        super.delete("deleteStock", stockId);
    }

    public boolean updateStockList(List<StockVO> updateList) {
        if (updateList.isEmpty()) {
            return true;
        }
        super.startBatch();
        for (StockVO stockVO : updateList) {
            updateStock(stockVO);
        }
        int count = super.executeBatch();
        return updateList.size() == count;
    }

    public void updateStock(StockVO stockVO) {
        super.update("updateStock", stockVO);
    }

    public void updateICICICodeMappings(List<ICICICodeMapping> iciciCodeMappings) {
        super.startBatch();
        for (ICICICodeMapping iciciCodeMapping : iciciCodeMappings) {
            if (iciciCodeMapping.getStock() == null) continue;
            super.update("updateICICICodeMapping", iciciCodeMapping);
        }
        super.executeBatch();
    }

    public List<ICICICodeMapping> iciciCodeMappings() {
        return super.queryForList("getIciciCodeMappings");
    }

}
