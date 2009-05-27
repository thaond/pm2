package pm.vo;

import junit.framework.TestCase;
import pm.util.AppConst.CORP_RESULT_TIMELINE;
import pm.util.PMDate;

import java.net.URLEncoder;

public class CorporateResultsVOTest extends TestCase {

    public void testCorporateResultsVO_String_Audited_Non_Cumulative_Consolidated() throws Exception {
        CorporateResultsVO resultsVO = new CorporateResultsVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?01-OCT-200531-DEC-2005Q3AANCEINFOSYSTCH");
        assertEquals("INFOSYSTCH", resultsVO.getTicker());
        assertEquals(new PMDate(1, 10, 2005), resultsVO.getStartDate());
        assertEquals(new PMDate(31, 12, 2005), resultsVO.getEndDate());
        assertEquals(CORP_RESULT_TIMELINE.Quaterly, resultsVO.getTimeline());
        assertEquals(3, resultsVO.getPeriod());
        assertTrue(resultsVO.isAuditedFlag());
        assertTrue(resultsVO.isConsolidatedFlag());
        assertEquals(2005, resultsVO.getYear());
    }

    public void testCorporateResultsVO_String_Audited_Non_Cumulative_Non_Consolidated() throws Exception {
        CorporateResultsVO resultsVO = new CorporateResultsVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?01-OCT-200531-DEC-2005Q3AANNEINFOSYSTCH");
        assertEquals("INFOSYSTCH", resultsVO.getTicker());
        assertEquals(new PMDate(1, 10, 2005), resultsVO.getStartDate());
        assertEquals(new PMDate(31, 12, 2005), resultsVO.getEndDate());
        assertEquals(CORP_RESULT_TIMELINE.Quaterly, resultsVO.getTimeline());
        assertEquals(3, resultsVO.getPeriod());
        assertTrue(resultsVO.isAuditedFlag());
        assertFalse(resultsVO.isConsolidatedFlag());
        assertEquals(2005, resultsVO.getYear());
    }

    public void testCorporateResultsVO_String_Audited_Non_Cumulative_Consolidated_HalfYearly() throws Exception {
        CorporateResultsVO resultsVO = new CorporateResultsVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?01-APR-200530-SEP-2005H1AACCEINFOSYSTCH");
        assertEquals("INFOSYSTCH", resultsVO.getTicker());
        assertEquals(new PMDate(1, 4, 2005), resultsVO.getStartDate());
        assertEquals(new PMDate(30, 9, 2005), resultsVO.getEndDate());
        assertEquals(CORP_RESULT_TIMELINE.HalfYearly, resultsVO.getTimeline());
        assertEquals(1, resultsVO.getPeriod());
        assertTrue(resultsVO.isAuditedFlag());
        assertTrue(resultsVO.isConsolidatedFlag());
        assertEquals(2005, resultsVO.getYear());
    }

    public void testCorporateResultsVO_String_Audited_Non_Cumulative_Consolidated_Annual() throws Exception {
        CorporateResultsVO resultsVO = new CorporateResultsVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?01-APR-200431-MAR-2005ANAACCEINFOSYSTCH");
        assertEquals("INFOSYSTCH", resultsVO.getTicker());
        assertEquals(new PMDate(1, 4, 2004), resultsVO.getStartDate());
        assertEquals(new PMDate(31, 3, 2005), resultsVO.getEndDate());
        assertEquals(CORP_RESULT_TIMELINE.Annual, resultsVO.getTimeline());
        assertEquals(0, resultsVO.getPeriod());
        assertTrue(resultsVO.isAuditedFlag());
        assertTrue(resultsVO.isConsolidatedFlag());
        assertEquals(2004, resultsVO.getYear());
    }

    public void testCorporateResultsVO_EncodedCompanyName() throws Exception {
        CorporateResultsVO resultsVO = new CorporateResultsVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?01-APR-200431-MAR-2005ANAACCE" + URLEncoder.encode("M&M"));
        assertEquals("M&M", resultsVO.getTicker());
        assertEquals(new PMDate(1, 4, 2004), resultsVO.getStartDate());
        assertEquals(new PMDate(31, 3, 2005), resultsVO.getEndDate());
        assertEquals(CORP_RESULT_TIMELINE.Annual, resultsVO.getTimeline());
        assertEquals(0, resultsVO.getPeriod());
        assertTrue(resultsVO.isAuditedFlag());
        assertTrue(resultsVO.isConsolidatedFlag());
        assertEquals(2004, resultsVO.getYear());
    }
}
