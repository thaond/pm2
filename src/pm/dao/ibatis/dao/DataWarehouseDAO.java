package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import org.apache.log4j.Logger;
import pm.util.PMDate;
import pm.vo.EODStatistics;
import pm.vo.StockVO;

import java.util.HashMap;
import java.util.Map;

public class DataWarehouseDAO extends SqlMapDaoTemplate implements IDataWarehouseDAO {

    private static Logger logger = Logger.getLogger(DataWarehouseDAO.class);

    public DataWarehouseDAO(DaoManager daoManager) {
        super(daoManager);
    }

    public EODStatistics fetchEodStatics(PMDate pmDate, StockVO stockVO) {
        Map<String, Object> inputMapping = new HashMap<String, Object>();
        inputMapping.put("dateID", pmDate.getIntVal());
        inputMapping.put("stockID", stockVO.getId());
        return (EODStatistics) super.queryForObject("fetchEodStatics", inputMapping);
    }

    public void updateEodStatistics() {
        super.update("callUpdateEodStatics");
    }

}
