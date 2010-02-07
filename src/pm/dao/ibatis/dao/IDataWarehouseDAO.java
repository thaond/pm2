package pm.dao.ibatis.dao;

import pm.util.PMDate;
import pm.vo.EODStatistics;
import pm.vo.StockVO;

public interface IDataWarehouseDAO {

    EODStatistics fetchEodStatics(PMDate pmDate, StockVO stockVO);

    void updateEodStatistics();

}
