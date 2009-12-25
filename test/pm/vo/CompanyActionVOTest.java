package pm.vo;

import junit.framework.TestCase;
import pm.util.AppConst.COMPANY_ACTION_TYPE;
import pm.util.PMDate;

import java.util.Vector;

public class CompanyActionVOTest extends TestCase {

    /*
      * Test method for 'pm.vo.CompanyActionVO.getDetails()'
      */
    public void testToWriteAndConsturctForDSBValues() throws Exception {
        CompanyActionVO actionVO = new CompanyActionVO(
                COMPANY_ACTION_TYPE.Bonus, new PMDate(1, 1, 2004), "STOCKCODE",
                10.23f, 12.34f);
        CompanyActionVO newActionVO = new CompanyActionVO(actionVO.toWrite());
        assertEquals(actionVO, newActionVO);
    }

    public void testToWriteAndConsturctForMergerValues() throws Exception {
        CompanyActionVO actionVO = new CompanyActionVO(
                COMPANY_ACTION_TYPE.Merger, new PMDate(1, 1, 2004), "STOCKCODE", 2, 5, "PARENTSTK");
        CompanyActionVO newActionVO = new CompanyActionVO(actionVO.toWrite());
        assertEquals("PARENTSTK", newActionVO.getParentEntity());
        assertEquals(actionVO, newActionVO);
    }

    public void testToWriteAndConsturctForDemergerValues() throws Exception {
        Vector<DemergerVO> demergerData = new Vector<DemergerVO>();
        demergerData.add(new DemergerVO("STK1", 12.23f));
        demergerData.add(new DemergerVO("STK2", 12.14f));
        CompanyActionVO actionVO = new CompanyActionVO(
                COMPANY_ACTION_TYPE.Bonus, new PMDate(1, 1, 2004), "STOCKCODE",
                demergerData);
        CompanyActionVO newActionVO = new CompanyActionVO(actionVO.toWrite());
        assertEquals(actionVO, newActionVO);
    }

    public void testToWriteToSkipEmptyDemergerValue() throws Exception {
        Vector<DemergerVO> demergerData = new Vector<DemergerVO>();
        demergerData.add(new DemergerVO("STK1", 12.23f));
        demergerData.add(new DemergerVO("STK2", 12.14f));
        demergerData.add(new DemergerVO());
        CompanyActionVO actionVO = new CompanyActionVO(
                COMPANY_ACTION_TYPE.Bonus, new PMDate(1, 1, 2004), "STOCKCODE",
                demergerData);
        CompanyActionVO newActionVO = new CompanyActionVO(actionVO.toWrite());
        assertEquals(demergerData.size() - 1, newActionVO.getDemergerData()
                .size());
        assertEquals(demergerData.get(0), newActionVO.getDemergerData().get(0));
        assertEquals(demergerData.get(1), newActionVO.getDemergerData().get(1));
    }

    public void testToWrite_ReadPercentageValue() throws Exception {
        CompanyActionVO actionVO = new CompanyActionVO(
                COMPANY_ACTION_TYPE.Bonus, new PMDate(1, 1, 2004), "STOCKCODE",
                10.23f, 12.34f);
        actionVO.setPercentageValue(true);
        CompanyActionVO newActionVO = new CompanyActionVO(actionVO.toWrite());
        assertEquals(actionVO, newActionVO);

    }

    public void testGetPriceFactor() throws Exception {
        String STOCKCODE = "STOCKCODE";
        CompanyActionVO actionVO = new CompanyActionVO(
                COMPANY_ACTION_TYPE.Bonus, new PMDate(1, 1, 2004), STOCKCODE,
                3f, 1f);
        assertEquals(1 / 4f, actionVO.getPriceFactor());

        actionVO = new CompanyActionVO(COMPANY_ACTION_TYPE.Bonus, new PMDate(1,
                1, 2004), STOCKCODE, 3f, 2f);
        assertEquals(2 / 5f, actionVO.getPriceFactor());

        actionVO = new CompanyActionVO(COMPANY_ACTION_TYPE.Split, new PMDate(1,
                1, 2004), STOCKCODE, 1f, 2f);
        assertEquals(1 / 2f, actionVO.getPriceFactor());

        actionVO = new CompanyActionVO(COMPANY_ACTION_TYPE.Split, new PMDate(1,
                1, 2004), STOCKCODE, 1f, 10f);
        assertEquals(1 / 10f, actionVO.getPriceFactor());

        Vector<DemergerVO> demergerData = new Vector<DemergerVO>();
        demergerData.add(new DemergerVO(STOCKCODE, 45f));
        demergerData.add(new DemergerVO("STK2", 55f));
        demergerData.add(new DemergerVO());
        actionVO = new CompanyActionVO(
                COMPANY_ACTION_TYPE.Demerger, new PMDate(1, 1, 2004), STOCKCODE,
                demergerData);
        assertEquals(0.45f, actionVO.getPriceFactor());

    }

    public void testNormalize() {
        String STOCKCODE = "STOCKCODE";
        CompanyActionVO actionVO = new CompanyActionVO(
                COMPANY_ACTION_TYPE.Bonus, new PMDate(1, 1, 2004), STOCKCODE,
                3f, 1f);
        actionVO.normalize(10f);
        assertFalse(actionVO.isPercentageValue());

        actionVO = new CompanyActionVO(
                COMPANY_ACTION_TYPE.Divident, new PMDate(1, 1, 2004), STOCKCODE,
                3f, 1f);
        actionVO.setPercentageValue(true);
        actionVO.normalize(10f);
        assertEquals(3f, actionVO.getDsbValue());

        actionVO = new CompanyActionVO(
                COMPANY_ACTION_TYPE.Divident, new PMDate(1, 1, 2004), STOCKCODE,
                5f, 1f);
        actionVO.normalize(5f);
        assertEquals(100f, actionVO.getDsbValue());
        assertTrue(actionVO.isPercentageValue());

        actionVO = new CompanyActionVO(
                COMPANY_ACTION_TYPE.Divident, new PMDate(1, 1, 2004), STOCKCODE,
                20f, 2f);
        actionVO.normalize(5f);
        assertEquals(200f, actionVO.getDsbValue());
        assertTrue(actionVO.isPercentageValue());

    }
}
