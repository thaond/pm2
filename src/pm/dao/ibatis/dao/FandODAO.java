package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import pm.util.PMDate;

public class FandODAO extends PMDaoTemplate implements IFandODAO {

    public FandODAO(DaoManager daoManager) {
        super(daoManager);
    }


    public PMDate latestQuoteDate() {
        return (PMDate) super.queryForObject("latestQuoteDate");
    }
}
