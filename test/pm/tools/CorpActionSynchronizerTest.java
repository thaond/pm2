package pm.tools;

import junit.framework.TestCase;
import pm.bo.CompanyBO;
import pm.dao.CompanyDAO;
import pm.util.AppConfig;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.CompanyActionVO;

import java.util.Hashtable;
import java.util.Vector;

public class CorpActionSynchronizerTest extends TestCase {

    /*
      * Test method for 'pm.tools.CorpActionSynchronizer.applyCorpAction()'
      */
    public void testApplyCorpAction() throws Exception {
        final Vector<CompanyActionVO> actionPerformedList = new Vector<CompanyActionVO>();
        final CompanyActionVO actionVO1 = new CompanyActionVO("20050101,STOCK,1,10,Split,false,");
        final CompanyActionVO actionVO2 = new CompanyActionVO("20050101,STOCK1,1,10,Split,false,");
        final CompanyActionVO actionVO3 = new CompanyActionVO("20050102,STOCK,1,10,Split,false,");
        final CompanyActionVO actionVO4 = new CompanyActionVO("20050103,STOCK,1,10,Split,false,");
        final Vector<Integer> callList = new Vector<Integer>();
        final PMDate pastDate = new PMDate(1, 1, 2005);
        final PMDate currDate = new PMDate(2, 1, 2005);
        final PMDate futureDate = new PMDate(3, 1, 2005);
        String strDate = AppConfig.dateCORPACTIONSYNCHRONIZER.Value;

        CorpActionSynchronizer synchronizer = new CorpActionSynchronizer() {
            @Override
            CompanyBO getCompanyBO() {
                return new CompanyBO() {
                    @Override
                    public boolean doAction(CompanyActionVO actionVO) {
                        actionPerformedList.add(actionVO);
                        return true;
                    }
                };
            }

            @Override
            CompanyDAO getCompanyDAO() {
                return new CompanyDAO() {
                    @Override
                    public Hashtable<PMDate, Vector<CompanyActionVO>> getConsolidatedActionData() {
                        callList.add(1);
                        Hashtable<PMDate, Vector<CompanyActionVO>> retVal = new Hashtable<PMDate, Vector<CompanyActionVO>>();
                        Vector<CompanyActionVO> vector = new Vector<CompanyActionVO>();
                        vector.add(actionVO1);
                        vector.add(actionVO2);
                        retVal.put(pastDate, vector);
                        vector = new Vector<CompanyActionVO>();
                        vector.add(actionVO3);
                        retVal.put(currDate, vector);
                        vector = new Vector<CompanyActionVO>();
                        vector.add(actionVO4);
                        retVal.put(futureDate, vector);
                        return retVal;
                    }

                    @Override
                    public boolean writeConsolidatedActionData(Hashtable<PMDate, Vector<CompanyActionVO>> consolidatedData) {
                        callList.add(2);
                        assertEquals(1, consolidatedData.size());
                        assertEquals(1, consolidatedData.elements().nextElement().size());
                        assertTrue(consolidatedData.containsKey(futureDate));
                        return true;
                    }
                };
            }

            @Override
            PMDate getCurrDate() {
                return currDate;
            }
        };
        synchronizer.applyCorpAction();
        assertEquals(3, actionPerformedList.size());
        assertEquals(actionVO1, actionPerformedList.get(0));
        assertEquals(actionVO2, actionPerformedList.get(1));
        assertEquals(actionVO3, actionPerformedList.get(2));
        assertFalse(actionPerformedList.contains(actionVO4));
        assertEquals(2, callList.size());
        assertTrue(callList.contains(1));
        assertTrue(callList.contains(2));
        assertEquals(PMDateFormatter.formatYYYYMMDD(currDate), AppConfig.dateCORPACTIONSYNCHRONIZER.Value);
        AppConfig.saveUpdateConfigDetail(AppConfig.dateCORPACTIONSYNCHRONIZER, strDate);
    }

}
