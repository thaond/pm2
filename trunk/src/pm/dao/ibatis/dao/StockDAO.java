package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import org.apache.log4j.Logger;
import pm.vo.StockVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map params = new HashMap();
        params.put("stockID", stockVO.getId());
        params.put("iciciCode", iciciCode);
        super.update("updateICICICodeMapping", params);
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

    public Map<String, String> iciciCodeMapping() {
        List<Map<String, String>> list = super.queryForList("iCICICodeMapping");
        Map<String, String> mapping = new HashMap<String, String>();
        for (Map<String, String> map : list) {
            mapping.put(map.get("ICICICODE"), map.get("STOCKCODE"));
        }
        return mapping;
    }

}
