package pm.dao.ibatis.dao;

import pm.vo.StockVO;

import java.util.List;
import java.util.Map;

/**
 * Date: 22-Jul-2006
 * Time: 10:21:53
 */
public interface IStockDAO {
    StockVO getStock(String name);

    List<StockVO> getStockList(boolean incIndex);

    List<StockVO> getIndexList();

    boolean insertStockList(List<StockVO> stockList);

    boolean updateStockList(List<StockVO> updateList);

    void updateStock(StockVO stockVO);

    void insertStock(StockVO stockVO);

    String iciciCode(String stockCode);

    void updateICICICode(StockVO stockVO, String iciciCode);

    String yahooCode(String stockCode);

    void delete(int stockId);

    Map<String, String> iciciCodeMapping();

}
