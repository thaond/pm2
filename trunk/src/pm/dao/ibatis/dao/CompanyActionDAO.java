package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import pm.util.AppConst;
import pm.vo.ActionMapping;
import pm.vo.CompanyActionVO;
import pm.vo.DemergerVO;
import pm.vo.StockVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: Sep 5, 2006
 * Time: 10:34:45 PM
 */
public class CompanyActionDAO extends SqlMapDaoTemplate implements ICompanyActionDAO {
    /**
     * The DaoManager that manages this Dao instance will be passed
     * in as the parameter to this constructor automatically upon
     * instantiation.
     *
     * @param daoManager
     */
    public CompanyActionDAO(DaoManager daoManager) {
        super(daoManager);
    }

    public List<CompanyActionVO> getCompanyAction(String stockCode) {
        List actionList = super.queryForList("getDivident", stockCode);
        actionList.addAll(super.queryForList("getSplit", stockCode));
        actionList.addAll(super.queryForList("getBonus", stockCode));
        actionList.addAll(super.queryForList("getDemerger", stockCode));
        return actionList;
    }

    public int insertCompanyAction(CompanyActionVO actionVO) {
        switch (actionVO.getAction()) {
            case Bonus:
                return (Integer) super.insert("insertBonus", actionVO);
            case Demerger:
                int id = (Integer) super.insert("insertDemergerBase", actionVO);
                for (DemergerVO demergerVO : actionVO.getDemergerData()) {
                    demergerVO.setBaseID(id);
                    super.insert("insertDemergerList", demergerVO);
                }
                return id;
            case Divident:
                return (Integer) super.insert("insertDivident", actionVO);
            case Split:
                return (Integer) super.insert("insertSplit", actionVO);
        }
        return -1;
    }

    public void updateCompanyAction(CompanyActionVO actionVO) {
        switch (actionVO.getAction()) {
            case Bonus:
                new RuntimeException("Function not support");
            case Demerger:
                new RuntimeException("Function not support");
            case Divident:
                super.update("updateDivident", actionVO);
                break;
            case Split:
                new RuntimeException("Function not support");
        }
    }

    public void insertActionMapping(AppConst.COMPANY_ACTION_TYPE action, ActionMapping actionMapping) {
        switch (action) {

            case Bonus:
                super.insert("insertBonusMapping", actionMapping);
                break;
            case Demerger:
                super.insert("insertDemergerMapping", actionMapping);
                break;
            case Divident:
                break;
            case Split:
                super.insert("insertSplitMapping", actionMapping);
        }
    }

    public List<ActionMapping> getActionMapping(AppConst.COMPANY_ACTION_TYPE action) {
        switch (action) {

            case Bonus:
                return super.queryForList("getBonusMapping", null);
            case Demerger:
                break;
            case Divident:
                break;
            case Split:
                return super.queryForList("getSplitMapping", null);
        }
        return null;
    }

    public void resetDividents(StockVO stockVO) {
        super.update("resetDivident", stockVO.getId());

    }

    public void updateStockId(int fromStockId, int toStockId) {
        super.startBatch();
        String[] actionTables = {"CA_DIVIDENT", "CA_SPLIT", "CA_BONUS", "CA_DEMERGERBASE", "CA_DEMERGERLIST"};
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
