package pm.net.nse;

import junit.framework.TestCase;
import pm.util.AppConst.COMPANY_ACTION_TYPE;
import pm.util.PMDate;
import pm.vo.CompanyActionVO;

import java.util.Hashtable;
import java.util.Vector;

public class CorpActionConverterTest extends TestCase {

    public void testProcessCorpAction() throws Exception {
        Hashtable<PMDate, String> actionDetails = new Hashtable<PMDate, String>();
        PMDate date1 = new PMDate(1, 1, 2005);
        actionDetails.put(date1, "SPLT-RS.10-RE.1");
        PMDate date2 = new PMDate(2, 2, 2006);
        actionDetails.put(date2, "BONUS-3:1");
        CorpActionConverter converter = new CorpActionConverter() {
            protected void recordAlert(String msg, String stockCode,
                                       PMDate date, String rawActionLine) {
                fail("Should not come here");
            }
        };
        Vector<CompanyActionVO> result = converter.processCorpAction("STOCK",
                actionDetails);
        assertEquals(2, result.size());
        assertTrue(result.contains(new CompanyActionVO(
                "20050101,STOCK,1,10,Split,false,")));
        assertTrue(result.contains(new CompanyActionVO(
                "20060202,STOCK,3,1,Bonus,false,")));
    } /*
		 * Test method for
		 * 'pm.tools.CorpActionConverter.convertToActionVO(String, PMDate,
		 * String)'
		 */

    public void testConvertToActionVO() {
        CorpActionConverter converter = new CorpActionConverter();
        Vector<CompanyActionVO> actionList = converter.convertToActionVO("",
                new PMDate(), "SPLT-RS.10-RE.1/BONUS-3:1");
        assertEquals(2, actionList.size());
        assertEquals(COMPANY_ACTION_TYPE.Split, actionList.get(0).getAction());
        assertEquals(COMPANY_ACTION_TYPE.Bonus, actionList.get(1).getAction());

        actionList = converter.convertToActionVO("", new PMDate(),
                "AGM/DIV-(FIN-35%+SPL-15%)/SPLT-RS.10-RE.1/BONUS-3:1");
        assertEquals(3, actionList.size());
        assertEquals(COMPANY_ACTION_TYPE.Divident, actionList.get(0)
                .getAction());
        assertEquals(COMPANY_ACTION_TYPE.Split, actionList.get(1).getAction());
        assertEquals(COMPANY_ACTION_TYPE.Bonus, actionList.get(2).getAction());

    }

    public void testProcessDivident() throws Exception {
        CorpActionConverter converter = new CorpActionConverter();
        String STOCKCODE = "STOCK";
        PMDate date = new PMDate();
        CompanyActionVO actionVO = converter.processDivident(STOCKCODE, date,
                "AGM / DIVIDEND -18%");
        assertEquals(18f, actionVO.getDsbValue());
        assertEquals(1f, actionVO.getBase());
        assertTrue(actionVO.isPercentageValue());
        assertEquals(COMPANY_ACTION_TYPE.Divident, actionVO.getAction());
        assertEquals(STOCKCODE, actionVO.getStockCode());
        assertEquals(date, actionVO.getExDate());

        actionVO = converter.processDivident("", date,
                "AGM/DIVIDEND-RS8/- PER SH");
        assertEquals(8f, actionVO.getDsbValue());
        assertFalse(actionVO.isPercentageValue());

        actionVO = converter
                .processDivident("", date, "INTERIM DIVIDEND @ 10%");
        assertEquals(10f, actionVO.getDsbValue());
        assertTrue(actionVO.isPercentageValue());

        assertNull(converter.processDivident("", date, "AGM"));

        actionVO = converter.processDivident("", date,
                "AGM/DIV.-RE.1/- PER SHARE");
        assertEquals(1f, actionVO.getDsbValue());
        assertFalse(actionVO.isPercentageValue());

        actionVO = converter.processDivident("", date,
                "AGM/DIV-RS.2.5/- PER SH.");
        assertEquals(2.5f, actionVO.getDsbValue());
        assertFalse(actionVO.isPercentageValue());

        actionVO = converter.processDivident("", date,
                "AGM/DIV-(FIN-35%+SPL-15%)");
        assertEquals(50f, actionVO.getDsbValue());
        assertTrue(actionVO.isPercentageValue());

        actionVO = converter.processDivident("", date,
                "AGM/DIV FIN-20 + SPL-30");
        assertEquals(50f, actionVO.getDsbValue());
        assertFalse(actionVO.isPercentageValue());

        actionVO = converter.processDivident("", date,
                "AGM/DIV FIN-15% + INT-15%PURPOSE REVISED");
        assertEquals(30f, actionVO.getDsbValue());
        assertTrue(actionVO.isPercentageValue());

        actionVO = converter.processDivident("", date,
                "AGM/DIV-(FIN-35%/SPLT-1:3)");
        assertEquals(35f, actionVO.getDsbValue());
        assertTrue(actionVO.isPercentageValue());

        actionVO = converter.processDivident("", date,
                "AGM/DIV.170%/SPL DIV.600%");
        assertEquals(770f, actionVO.getDsbValue());
        assertTrue(actionVO.isPercentageValue());
        actionVO = converter.processDivident("", date,
                "AGM/DIV.170%/");
        assertEquals(170f, actionVO.getDsbValue());
        assertTrue(actionVO.isPercentageValue());

        actionVO = converter.processDivident("", date,
                "AGM/SPL/BON-1:1/DIV-20%");
        assertEquals(20f, actionVO.getDsbValue());
        assertTrue(actionVO.isPercentageValue());

        actionVO = converter.processDivident("", date,
                "AGM/DIV-145%+SPL DIV-400%");
        assertEquals(545f, actionVO.getDsbValue());

    }

    public void testGetNextItem() throws Exception {
        String substring = "DIV.170%/SPL DIV.600%";
        CorpActionConverter converter = new CorpActionConverter();
        assertEquals(9, converter.getNextItem(substring, 7));
        assertEquals("SPL DIV.600%", substring.substring(9));
        assertEquals(13, converter.getNextItem(
                "DIV FIN-15% + INT-15%PURPOSE REVISED", 3));
        assertEquals(13, converter.getNextItem(
                "DIV FIN-15% + INT-15%/SPLT-1:3", 3));
        assertEquals(13, converter.getNextItem(
                "DIV FIN-15% / INT-15%/SPLT-1:3 + Something", 3));
    }

    public void testProcessSplit() throws Exception {
        CorpActionConverter converter = new CorpActionConverter();
        String STOCKCODE = "STOCK";
        PMDate date = new PMDate();
        CompanyActionVO actionVO = converter.processSplit(STOCKCODE, date,
                "FV SPLIT RS.10/- TO RS.2/");
        assertEquals(2f, actionVO.getDsbValue());
        assertEquals(10f, actionVO.getBase());
        assertFalse(actionVO.isPercentageValue());
        assertEquals(COMPANY_ACTION_TYPE.Split, actionVO.getAction());
        assertEquals(STOCKCODE, actionVO.getStockCode());
        assertEquals(date, actionVO.getExDate());

        actionVO = converter.processSplit("", date, "AGM/DIV-50%/FV SPLT 5TO1");
        assertEquals(1f, actionVO.getDsbValue());
        assertEquals(5f, actionVO.getBase());

        actionVO = converter.processSplit("", date, "F.V. SPL-RS10/- TO RS2/-");
        assertEquals(2f, actionVO.getDsbValue());
        assertEquals(10f, actionVO.getBase());

        actionVO = converter
                .processSplit("", date, "SPLT-RS.10-RE.1/BONUS-3:1");
        assertEquals(1f, actionVO.getDsbValue());
        assertEquals(10f, actionVO.getBase());

        assertNull(converter.processSplit("", date, "AGM/DIV-50%/FV SPLT"));
        assertNull(converter.processSplit("", date, "AGM/DIV-50%/FV SPLT1"));
        assertNull(converter.processSplit("", date, "AGM/DIV-50%"));

    }

    public void testProcessBonus() throws Exception {
        CorpActionConverter converter = new CorpActionConverter();
        String STOCKCODE = "STOCK";
        PMDate date = new PMDate();
        CompanyActionVO actionVO = converter.processBonus(STOCKCODE, date,
                "BONUS 3:4");
        assertEquals(3f, actionVO.getDsbValue());
        assertEquals(4f, actionVO.getBase());
        assertFalse(actionVO.isPercentageValue());
        assertEquals(COMPANY_ACTION_TYPE.Bonus, actionVO.getAction());
        assertEquals(STOCKCODE, actionVO.getStockCode());
        assertEquals(date, actionVO.getExDate());

        actionVO = converter.processBonus("", date, "BONUS1:1");
        assertEquals(1f, actionVO.getDsbValue());
        assertEquals(1f, actionVO.getBase());

        actionVO = converter.processBonus("", date, "AGM/SPL/BON-1:1/DIV-20%");
        assertEquals(1f, actionVO.getDsbValue());
        assertEquals(1f, actionVO.getBase());

        assertNull(converter.processBonus("", date, "BONUS"));
        assertNull(converter.processBonus("", date, "BONUS1"));
        assertNull(converter.processBonus("", date, "AGM/DIV-50%"));
    }

}
