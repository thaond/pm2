package pm.dao.ibatis.dao;

import pm.vo.WatchlistDetailsVO;
import pm.vo.WatchlistVO;

import java.util.List;
import java.util.Vector;

/**
 * WatchlistDAO Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>08/22/2006</pre>
 */
public class WatchlistDAOTest extends PMDBTestCase {
    public WatchlistDAOTest(String name) {
        super(name, "TestData.xml");
    }

    public void testInsert_GetWatchlistDetails() throws Exception {
        WatchlistDetailsVO detailsVO1 = new WatchlistDetailsVO("TestWatchlist1", false);
        WatchlistDetailsVO detailsVO2 = new WatchlistDetailsVO("TestWatchlist2", true);
        IWatchlistDAO dao = DAOManager.getWatchlistDAO();
        dao.insertWatchlistGroup(detailsVO1);
        dao.insertWatchlistGroup(detailsVO2);
        List<WatchlistDetailsVO> watchlistVos = dao.getWatchlistGroup();
        assertTrue(watchlistVos.contains(detailsVO1));
        assertTrue(watchlistVos.contains(detailsVO2));
    }

    public void testUpdateWatchlistDetails() throws Exception {
        IWatchlistDAO dao = DAOManager.getWatchlistDAO();
        List<WatchlistDetailsVO> watchlistVos = dao.getWatchlistGroup();
        WatchlistDetailsVO detailsVO = watchlistVos.get(0);
        detailsVO.setAlertEnabled(!detailsVO.isAlertEnabled());
        dao.updateWatchlistGroup(detailsVO);
        watchlistVos = dao.getWatchlistGroup();
        assertTrue(watchlistVos.contains(detailsVO));
    }

    public void testInsert_GetWatchlist() throws Exception {
        IWatchlistDAO dao = DAOManager.getWatchlistDAO();
        List<WatchlistVO> vos = new Vector<WatchlistVO>();
        WatchlistVO vo1 = new WatchlistVO("CODE3", 10f, 20f, 1);
        WatchlistVO vo2 = new WatchlistVO("CODE4", 20f, 20f, 1);
        WatchlistVO vo3 = new WatchlistVO("CODE2", 10f, 40f, 2);
        vos.add(vo1);
        vos.add(vo2);
        vos.add(vo3);
        dao.insertWatchlistVOs(vos);
        List<WatchlistVO> watchlistVos = dao.getWatchlistVos(1);
        assertTrue(watchlistVos.contains(vo1));
        assertTrue(watchlistVos.contains(vo2));
        assertFalse(watchlistVos.contains(vo3));
        watchlistVos = dao.getWatchlistVos(2);
        assertTrue(watchlistVos.contains(vo3));
    }

    public void testWatchlist() {
        IWatchlistDAO dao = DAOManager.getWatchlistDAO();
        WatchlistVO expected = new WatchlistVO("CODE16", 100f, 20f, 1);
        assertEquals(expected, dao.watchlist(1));
    }

    public void testWatchlistByStockId() {
        IWatchlistDAO dao = DAOManager.getWatchlistDAO();
        WatchlistVO expected = new WatchlistVO("CODE16", 100f, 20f, 1);
        List<WatchlistVO> watchlist = dao.watchlistByStockId(16);
        assertEquals(1, watchlist.size());
        assertEquals(expected, watchlist.get(0));
        assertTrue(dao.watchlistByStockId(100).isEmpty());
    }

    public void testDeleteWatchlistByStockId() {
        IWatchlistDAO dao = DAOManager.getWatchlistDAO();
        dao.deleteWatchlistByStockId(16);
        assertNull(dao.watchlist(1));
    }

    public void testUpdateStockId() {
        IWatchlistDAO dao = DAOManager.getWatchlistDAO();
        assertEquals("CODE16NEW", dao.watchlist(2).getStockCode());
        dao.updateStockId(17, 1);
        assertEquals("CODE1", dao.watchlist(2).getStockCode());
    }

}
