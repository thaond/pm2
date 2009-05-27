package pm.bo;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import pm.dao.ibatis.dao.IWatchlistDAO;
import pm.vo.WatchlistVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thiyagu
 * @version $Id: WatchlistBOTest.java,v 1.1 2007/12/30 15:17:57 tpalanis Exp $
 * @since 28-Dec-2007
 */
public class WatchlistBOTest extends MockObjectTestCase {

    public void testHandleDuplicateToDeleteOldAndAssignOldStockCodeToLatest() {
        final Mock mockWatchlistDAO = mock(IWatchlistDAO.class);
        int latestStockId = 2;
        int originalStockId = 1;
        WatchlistBO bo = new WatchlistBO() {
            IWatchlistDAO getDAO() {
                return (IWatchlistDAO) mockWatchlistDAO.proxy();
            }
        };
        List<WatchlistVO> list = new ArrayList<WatchlistVO>();
        list.add(new WatchlistVO("CODE16", 100f, 20f, 1));
        mockWatchlistDAO.expects(once()).method("watchlistByStockId").with(eq(latestStockId)).will(returnValue(list));
        mockWatchlistDAO.expects(once()).method("deleteWatchlistByStockId").with(eq(originalStockId));
        mockWatchlistDAO.expects(once()).method("updateStockId").with(eq(latestStockId), eq(originalStockId));
        bo.handleDuplicate(latestStockId, originalStockId);
    }

    public void testHandleDuplicateNotToDeleteOldIfLatestIsEmpty() {
        final Mock mockWatchlistDAO = mock(IWatchlistDAO.class);
        int latestStockId = 2;
        int originalStockId = 1;
        WatchlistBO bo = new WatchlistBO() {
            IWatchlistDAO getDAO() {
                return (IWatchlistDAO) mockWatchlistDAO.proxy();
            }
        };
        List<WatchlistVO> list = new ArrayList<WatchlistVO>();
        mockWatchlistDAO.expects(once()).method("watchlistByStockId").with(eq(latestStockId)).will(returnValue(list));
        bo.handleDuplicate(latestStockId, originalStockId);
    }
}
