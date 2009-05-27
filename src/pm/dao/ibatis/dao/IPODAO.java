package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import pm.vo.IPOVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: Aug 26, 2006
 * Time: 5:02:52 PM
 */
public class IPODAO extends PMDaoTemplate implements IIPODAO {

    /**
     * The DaoManager that manages this Dao instance will be passed
     * in as the parameter to this constructor automatically upon
     * instantiation.
     *
     * @param daoManager
     */
    public IPODAO(DaoManager daoManager) {
        super(daoManager);
    }

    public List<IPOVO> getIPOTransaction(int portfolioID, int tradingAccID) {
        Map<String, Integer> params = new HashMap<String, Integer>();
        addAccountIDToMap(portfolioID, tradingAccID, params);
        return super.queryForList("getIPOTransaction", params);
    }

    public void insertIPOApply(IPOVO ipovo) {
        super.insert("insertIPOApply", ipovo);
    }

    public void updateIPO(IPOVO ipovo) {
        super.update("updateIPO", ipovo);
    }

}
