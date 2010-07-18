package pm.dao.ibatis.dao;

import org.junit.Test;
import pm.util.PMDate;

public class FandODAOTest extends PMDBTestCase {

    public FandODAOTest(String string) {
        super(string, "TestData.xml");
    }

    @Test
    public void testLatestQuoteDate() throws Exception {
        IFandODAO dao = DAOManager.fandoDAO();
        assertEquals(new PMDate(2, 1, 2006), dao.latestQuoteDate());
    }
}
