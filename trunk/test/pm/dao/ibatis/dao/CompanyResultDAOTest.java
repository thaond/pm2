package pm.dao.ibatis.dao;

import pm.util.AppConst;
import pm.util.PMDate;
import pm.vo.CorpResultVO;

import java.util.List;

/**
 * @author Thiyagu
 * @version $Id: CompanyResultDAOTest.java,v 1.1 2007/12/31 03:36:01 tpalanis Exp $
 * @since 30-Dec-2007
 */
public class CompanyResultDAOTest extends PMDBCompositeDataSetTestCase {

    public CompanyResultDAOTest(String string) {
        super(string, "EmptyData.xml", "CompanyResultTestData.xml");
    }

    public void testSave_Get() {
        CorpResultVO resultVO = new CorpResultVO("CODE1", new PMDate(1, 1, 2007), new PMDate(31, 3, 2007), AppConst.CORP_RESULT_TIMELINE.Quaterly,
                4, true, true, false, 2006);
        resultVO.setFinancialData(15.1f, 10f, 10f, 1002001.10f, 1000f, 500.25f, 10001.10f, 200.25f, 100020.15f);
        ICompanyResultDAO dao = DAOManager.companyResultDAO();
        dao.save(resultVO);
        List<CorpResultVO> actualVOs = dao.get(1);
        assertTrue(resultVO.equalsIncAll(actualVOs.get(0)));
        assertTrue(actualVOs.get(0).getId() >= 1000);
    }
}
