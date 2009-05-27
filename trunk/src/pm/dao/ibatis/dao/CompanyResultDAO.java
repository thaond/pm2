package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import pm.vo.CorpResultVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thiyagu
 * @version $Id: CompanyResultDAO.java,v 1.1 2007/12/31 03:36:00 tpalanis Exp $
 * @since 30-Dec-2007
 */
public class CompanyResultDAO extends SqlMapDaoTemplate implements ICompanyResultDAO {

    public CompanyResultDAO(DaoManager daoManager) {
        super(daoManager);
    }

    public void save(CorpResultVO resultVO) {
        super.insert("saveCompanyResult", resultVO);
    }

    public List<CorpResultVO> get(int stockID) {
        return super.queryForList("getCompanyResult", stockID);
    }

    public void updateStockId(int fromStockId, int toStockId) {
        super.startBatch();
        String[] actionTables = {"CORPRESULT"};
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("from", fromStockId);
        params.put("to", toStockId);
        for (String tableName : actionTables) {
            params.put("table", tableName);
            super.update("updateStockID", params);
        }
        super.executeBatch();
    }

}
