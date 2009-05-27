package pm.dao.ibatis.dao;

import pm.util.AppConst;
import pm.util.PMDate;
import pm.vo.ActionMapping;
import pm.vo.CompanyActionVO;
import pm.vo.DemergerVO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Date: Sep 5, 2006
 * Time: 10:44:35 PM
 */
public class CompanyActionDAOTest extends PMDBTestCase {

    public CompanyActionDAOTest(String string) {
        super(string, "TestData.xml");
    }

    public void testInsertGetCompanyActionForDivident() {
        ICompanyActionDAO dao = DAOManager.getCompanyActionDAO();
        String stockCode = "CODE1";
        int initialSize = dao.getCompanyAction(stockCode).size();
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(1, 1, 2006), stockCode, 10, 1);
        dao.insertCompanyAction(actionVO);
        List<CompanyActionVO> actionList = dao.getCompanyAction(stockCode);
        assertTrue(actionList.contains(actionVO));
        assertEquals(initialSize + 1, actionList.size());
    }

    public void testUpdateCompanyActionForDivident() {
        ICompanyActionDAO dao = DAOManager.getCompanyActionDAO();
        String stockCode = "CODE1";
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(1, 1, 2006), stockCode, 10, 1);
        actionVO.setId(dao.insertCompanyAction(actionVO));
        List<CompanyActionVO> actionList = dao.getCompanyAction(stockCode);
        assertTrue(actionList.contains(actionVO));
        actionVO.setExDate(new PMDate(2, 1, 2006));
        String newStockCode = "CODE2";
        actionVO.setStockCode(newStockCode);
        actionVO.setBase(1);
        actionVO.setDsbValue(25);
        dao.updateCompanyAction(actionVO);
        actionList = dao.getCompanyAction(newStockCode);
        assertTrue(actionList.contains(actionVO));
    }

    public void testInsertGetCompanyActionForSplit() {
        ICompanyActionDAO dao = DAOManager.getCompanyActionDAO();
        String stockCode = "CODE1";
        int initialSize = dao.getCompanyAction(stockCode).size();
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, new PMDate(2, 1, 2006), stockCode, 10, 1);
        dao.insertCompanyAction(actionVO);
        List<CompanyActionVO> actionList = dao.getCompanyAction(stockCode);
        assertTrue(actionList.contains(actionVO));
        assertEquals(initialSize + 1, actionList.size());
    }

    public void testInsertGetCompanyActionForBonus() {
        ICompanyActionDAO dao = DAOManager.getCompanyActionDAO();
        String stockCode = "CODE1";
        int initialSize = dao.getCompanyAction(stockCode).size();
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Bonus, new PMDate(3, 1, 2006), stockCode, 10, 1);
        dao.insertCompanyAction(actionVO);
        List<CompanyActionVO> actionList = dao.getCompanyAction(stockCode);
        assertTrue(actionList.contains(actionVO));
        assertEquals(initialSize + 1, actionList.size());
    }

    public void testInsertGetCompanyActionForDemerger() {
        ICompanyActionDAO dao = DAOManager.getCompanyActionDAO();
        String stockCode = "CODE1";
        int initialSize = dao.getCompanyAction(stockCode).size();
        CompanyActionVO actionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Demerger, new PMDate(4, 1, 2006), stockCode, 1, 1);
        Vector<DemergerVO> demergerData = new Vector<DemergerVO>();
        demergerData.add(new DemergerVO("CODE2", 0.50f));
        demergerData.add(new DemergerVO("CODE3", 0.40f));
        demergerData.add(new DemergerVO("CODE4", 0.10f));
        actionVO.setDemergerData(demergerData);
        dao.insertCompanyAction(actionVO);
        List<CompanyActionVO> actionList = dao.getCompanyAction(stockCode);
        assertTrue(actionList.contains(actionVO));
        assertEquals(initialSize + 1, actionList.size());
    }

    public void testInsertGetBonusActionMapping() throws Exception {
        ICompanyActionDAO actionDAO = DAOManager.getCompanyActionDAO();
        ActionMapping mapping = new ActionMapping(1, 1);
        actionDAO.insertActionMapping(AppConst.COMPANY_ACTION_TYPE.Bonus, mapping);
        List<ActionMapping> actionList = actionDAO.getActionMapping(AppConst.COMPANY_ACTION_TYPE.Bonus);
        assertTrue(actionList.contains(mapping));
    }

    public void testUpdateStockId() {
        ICompanyActionDAO actionDAO = DAOManager.getCompanyActionDAO();
        int fromStockId = 17;
        int toStockId = 16;
        String code16 = "CODE16";
        String code16New = "CODE16NEW";
        int originalCASize = actionDAO.getCompanyAction(code16).size();
        int duplicateCASize = actionDAO.getCompanyAction(code16New).size();
        actionDAO.updateStockId(fromStockId, toStockId);
        List<CompanyActionVO> companyActionVOs = actionDAO.getCompanyAction(code16);
        for (AppConst.COMPANY_ACTION_TYPE action_type : AppConst.COMPANY_ACTION_TYPE.values()) {
            verifyListHasIDs(companyActionVOs, action_type, 11, 12);
        }

        verifyDemergerList(companyActionVOs, code16);
        assertEquals(0, actionDAO.getCompanyAction(code16New).size());
        assertEquals(originalCASize + duplicateCASize, DAOManager.getCompanyActionDAO().getCompanyAction(code16).size());
    }

    private void verifyDemergerList(List<CompanyActionVO> companyActionVOs, String stockCode) {
        boolean verified = false;
        for (CompanyActionVO companyActionVO : companyActionVOs) {
            if (companyActionVO.getAction() == AppConst.COMPANY_ACTION_TYPE.Demerger && companyActionVO.getId() == 12) {
                for (DemergerVO demergerVO : companyActionVO.getDemergerData()) {
                    if (demergerVO.getNewStockCode().equals(stockCode)) {
                        verified = true;
                        break;
                    }
                }
            }
        }
        assertTrue(verified);
    }

    private void verifyListHasIDs(List<CompanyActionVO> companyActionVOs, AppConst.COMPANY_ACTION_TYPE action_type, int... ids) {
        Set<Integer> idSet = new HashSet<Integer>();
        for (int id : ids) {
            idSet.add(id);
        }
        for (CompanyActionVO companyActionVO : companyActionVOs) {
            if (companyActionVO.getAction() == action_type) idSet.remove(companyActionVO.getId());
        }
        assertTrue("Action " + action_type, idSet.isEmpty());
    }
}
