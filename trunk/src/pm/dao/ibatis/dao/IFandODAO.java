package pm.dao.ibatis.dao;

import pm.util.PMDate;
import pm.vo.FOQuote;

import java.util.List;

public interface IFandODAO {

    PMDate latestQuoteDate();

    List<FOQuote> getQuotes(PMDate date);

    void save(List<FOQuote> quotes);
}
