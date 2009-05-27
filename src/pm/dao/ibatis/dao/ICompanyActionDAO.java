package pm.dao.ibatis.dao;

import pm.util.AppConst;
import pm.vo.ActionMapping;
import pm.vo.CompanyActionVO;
import pm.vo.StockVO;

import java.util.List;

/**
 * Date: Sep 5, 2006
 * Time: 8:29:07 PM
 */
public interface ICompanyActionDAO {

    public List<CompanyActionVO> getCompanyAction(String stockCode);

    public int insertCompanyAction(CompanyActionVO actionVO);

    public void updateCompanyAction(CompanyActionVO actionVO);

    public void insertActionMapping(AppConst.COMPANY_ACTION_TYPE action, ActionMapping actionMapping);

    public List<ActionMapping> getActionMapping(AppConst.COMPANY_ACTION_TYPE action);

    void resetDividents(StockVO stockVO);

    void updateStockId(int fromStockId, int toStockId);
}
