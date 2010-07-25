package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import pm.util.PMDate;
import pm.vo.FOQuote;

import java.util.List;

public class FandODAO extends PMDaoTemplate implements IFandODAO {

    public FandODAO(DaoManager daoManager) {
        super(daoManager);
    }


    public PMDate latestQuoteDate() {
        return (PMDate) super.queryForObject("latestQuoteDate");
    }

    public List<FOQuote> getQuotes(PMDate date) {
        return super.queryForList("futureQuotes", date.getIntVal());
    }

    public void save(List<FOQuote> quotes) {
        super.startBatch();
        for (FOQuote quote : quotes) {
            super.insert("insertFutureQuote", quote);
        }
        super.executeBatch();
    }
}
