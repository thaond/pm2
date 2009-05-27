package pm.dao.ibatis.dao;

import pm.vo.WatchlistDetailsVO;
import pm.vo.WatchlistVO;

import java.util.List;

/**
 * Date: Aug 22, 2006
 * Time: 10:10:16 PM
 */
public interface IWatchlistDAO {

    public void insertWatchlistGroup(WatchlistDetailsVO detailsVO);

    public void updateWatchlistGroup(WatchlistDetailsVO detailsVO);

    public List<WatchlistDetailsVO> getWatchlistGroup();

    public void insertWatchlistVOs(List<WatchlistVO> watchlistVOs);

    public List<WatchlistVO> getWatchlistVos(int watchlistGroupId);

    public void deleteWatchlistGroup(int watchlistGroupId);

    WatchlistVO watchlist(int watchlistId);

    void updateStockId(int fromStockId, int toStockId);

    void deleteWatchlistByStockId(int stockId);

    List<WatchlistVO> watchlistByStockId(int stockId);
}
