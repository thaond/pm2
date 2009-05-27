package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import pm.vo.WatchlistDetailsVO;
import pm.vo.WatchlistVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: Aug 22, 2006
 * Time: 10:10:31 PM
 */
public class WatchlistDAO extends SqlMapDaoTemplate implements IWatchlistDAO {
    /**
     * The DaoManager that manages this Dao instance will be passed
     * in as the parameter to this constructor automatically upon
     * instantiation.
     *
     * @param daoManager
     */
    public WatchlistDAO(DaoManager daoManager) {
        super(daoManager);
    }

    public void insertWatchlistGroup(WatchlistDetailsVO detailsVO) {
        super.insert("insertWatchlistGroup", detailsVO);
    }

    public void updateWatchlistGroup(WatchlistDetailsVO detailsVO) {
        super.update("updateWatchlistGroup", detailsVO);
    }

    public List<WatchlistDetailsVO> getWatchlistGroup() {
        return super.queryForList("getWatchlistGroup", null);
    }

    public void insertWatchlistVOs(List<WatchlistVO> watchlistVOs) {
        for (WatchlistVO watchlistVO : watchlistVOs) {
            super.insert("insertWatchlist", watchlistVO);
        }
    }

    public List<WatchlistVO> getWatchlistVos(int watchlistGroupId) {
        Map<String, Integer> param = new HashMap<String, Integer>();
        param.put("groupId", watchlistGroupId);
        return queryForList("getAllWatchlist", param);
    }

    public void deleteWatchlistGroup(int watchlistGroupId) {
        super.delete("deleteAllWatchlist", watchlistGroupId);
    }

    public WatchlistVO watchlist(int watchlistId) {
        return (WatchlistVO) super.queryForObject("getWatchlist", watchlistId);
    }

    public void updateStockId(int fromStockId, int toStockId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("from", fromStockId);
        params.put("to", toStockId);
        params.put("table", "WATCHLIST");
        super.update("updateStockID", params);
    }

    public void deleteWatchlistByStockId(int stockId) {
        super.delete("deleteWLByStockId", stockId);
    }

    public List<WatchlistVO> watchlistByStockId(int stockId) {
        Map<String, Integer> param = new HashMap<String, Integer>();
        param.put("stockId", stockId);
        return queryForList("getAllWatchlist", param);
    }
}
