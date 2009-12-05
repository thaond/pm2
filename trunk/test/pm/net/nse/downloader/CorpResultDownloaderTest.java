package pm.net.nse.downloader;

import junit.framework.TestCase;
import pm.AppLoader;
import pm.net.nse.CorpResultDownloadManager;
import pm.vo.CorpResultVO;

import java.io.*;

public class CorpResultDownloaderTest extends TestCase {

    private String testFileName = "CorpResultDownloaderTestInputFile.html";

    static {
        AppLoader.initConsoleLogger();
    }
    /*
      * Test method for 'pm.net.nse.downloader.CorpResultDownloader.performTask()'
      */
    //TODO fix this test
/*
    public void testPerformTask() throws Exception{
		final Vector<Integer> callList = new Vector<Integer>(); 
		CorpResultDownloaderManager manager = new CorpResultDownloaderManager(null);
		String url = "http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?01-APR-200531-MAR-2006ANANCCERELIANCE";
		CorpResultVO resultVO = manager.buildResultVO(url);
		createDataFile();		
		CorpResultDownloader downloader = new CorpResultDownloader(manager, url, resultVO) {
			@Override
			void storeData() {
				callList.add(1);
			}
			@Override
			protected Parser getParser() throws ParserException {
				return new Parser(testFileName);
			}
		};
		
		downloader.performTask();
		
		assertEquals("RELIANCE", downloader.resultVO.getStockCode());
		assertEquals(939800f, downloader.resultVO.getAdjustedNetProfit());
		assertEquals(new PMDate(31,3,2006), downloader.resultVO.getEndDate());
		assertEquals(67.4f, downloader.resultVO.getEps());
		assertEquals(10f, downloader.resultVO.getFaceValue());
		assertEquals(93500f, downloader.resultVO.getInterestCost());
		assertEquals(939800f, downloader.resultVO.getNetProfit());
		assertEquals(0f, downloader.resultVO.getNonRecurringExpense());
		assertEquals(0f, downloader.resultVO.getNonRecurringIncome());
		assertEquals(139400f, downloader.resultVO.getPaidUpEquityShareCapital());
		assertEquals(0, downloader.resultVO.getPeriod());
		assertEquals(new PMDate(1,4,2005), downloader.resultVO.getStartDate());
		assertEquals(CORP_RESULT_TIMELINE.Annual, downloader.resultVO.getTimeline());
		assertEquals(2005, downloader.resultVO.getYear());
		assertEquals(1, callList.size());
		deleteDataFile();

	}
*/

    public void testPerformTaskToSkipSaveOnError() throws Exception {
        CorpResultDownloader downloader = new CorpResultDownloader(null, null, null) {
            @Override
            protected Reader getDataReader() {
                return new StringReader("");
            }

            @Override
            protected void parseData(Reader reader) {
                error = true;
            }

            @Override
            void storeData() {
                fail("Should not save anything on failure");
            }
        };
        downloader.performTask();
    }

    public void testPerformTaskToHandleIOError() throws Exception {
        CorpResultDownloader downloader = new CorpResultDownloader(null, null, null) {
            @Override
            protected Reader getDataReader() {
                return null;
            }
        };

        downloader.performTask();
    }

    private void deleteDataFile() {
        new File(testFileName).deleteOnExit();
    }

    private void createDataFile() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(testFileName);
        pw.println("<!DOCTYPE HTML PUBLIC '-//IETF//DTD HTML 3.0//EN'><HTML><HEAD><TITLE>NSE - Financial Results Details</TITLE><LINK href='/nse.css' rel=STYLESHEET type=text/css><script language='javascript' src='/js/commonfuncs.js'></script><script language='javascript'>	var isIE=(document.all)?1:0;	var isNS4=(document.layers)?1:0;	var isNS6=((document.getElementById)&&(navigator.appName=='Netscape'))?1:0;	var isNS=((isNS4)||(isNS6))?1:0;	var DHTML=(isIE||isNS4||isNS6)?1:0;	function handleError() {		return true;	}	window.onerror = handleError;	capEvents();</script><script language='javascript' src='/js/corp_leftnav.js'></script></HEAD><BODY bgcolor='#ffffff' leftmargin=0 topmargin=0 marginheight=0 marginwidth=0><div name='menulayer' id='menulayer' class=menufont style='background-color: #f5c078; visibility:hidden; position:absolute; width:1px; height:1px; z-index:1; left:1; top:1'></div><TABLE cellspacing=0 border=0 cellpadding=0 height=100%><tr><!--Left Nav Bar Starts Here--><script language='javascript'>	writeLeftNav();</script><!--Left Nav Bar Ends Here--><!--Content Area Starts Here--><td width=600 valign=top><TABLE CELLPADDING=0 CELLSPACING=0 BORDER=0 width=592 height=100%><tr height=50><td colspan=3 valign=top class=smalllinks><a name='top'><br></a>&nbsp;&nbsp;<a href='/homepage.htm'>Home</a> &gt; <a href=/content/corporate/corp_introduction.htm>Corporates</a> &gt; <a href=/content/corporate/corpinfo.htm>Corporate Information</a> &gt; Financial Results<br><br></td></tr><TR><TD width=20>&nbsp;</TD><TD valign=top><font class=header>Financial Results</font><p><table border=0 cellspacing=1 cellpadding=4 align=center><tr><td class=tablehead>Company</td><td class=t0>RELIANCE INDUSTRIES LTD                                               </td></tr><tr><td class=tablehead>NSE Symbol</td><td class=t0><a href=/marketinfo/equities/cmquote.jsp?key=RELIANCEEQN&symbol=RELIANCE&flag=0>RELIANCE</a></td></tr><tr><td class=tablehead>Result Period</td><td class=t0>01-APR-2005 to 31-MAR-2006(Annual)</td></tr><tr><td class=tablehead>Result Type</td><td class=t0>Audited, Cumulative, Consolidated</td></tr></table></p><p align=center><font class=header3>Non Banking</font><br><table cellpadding=0 cellspacing=4 border=0 align=center><tr><td valign=top><table cellpadding=0 cellspacing=0 border=0 align=center><tr bgcolor=#333333><td><table cellpadding=3 cellspacing=1 border=0><tr><td colspan=2 class=tablehead>Financial Results (Rs.lakhs)</td></tr><TR><TD class=smallwt>Net Sales</td><TD class=t1>8302500.00</td></tr><TR><TD class=smallwt>Other Income</td><TD class=t1>110500.00</td></tr><TR><TD class=smallwt>Gross Income</td><TD class=t1>8413000.00</td></tr><TR><TD class=smallwt>Increase/Decrease in Stock</td><TD class=t1>-212000.00</td></tr><TR><TD class=smallwt>Consumption of Raw Materials</td><TD class=t1>5916800.00</td></tr><TR><TD class=smallwt>Staff Cost</td><TD class=t1>146900.00</td></tr><TR><TD class=smallwt>Total Expenditure (Excluding Other Expenditure)</td><TD class=t1>5851700.00</td></tr><TR><TD class=smallwt>Other Expenditure</td><TD class=t1>1015900.00</td></tr><TR><TD class=smallwt>Total Expenditure</td><TD class=t1>6867600.00</td></tr><TR><TD class=smallwt>Interest</td><TD class=t1>93500.00</td></tr><TR><TD class=smallwt>Profit(+)/Loss(-) Before Depreciation & Taxes</td><TD class=t1>1451900.00</td></tr><TR><TD class=smallwt>Depreciation</td><TD class=t1>349500.00</td></tr><TR><TD class=smallwt>Profit(+)/Loss(-)Before Tax</td><TD class=t1>1102400.00</td></tr><TR><TD class=smallwt>Provision for Taxation</td><TD class=t1>163000.00</td></tr><TR><TD class=smallwt>Other Provisions</td><TD class=t1>-400.00</td></tr><TR><TD class=smallwt>Misc Expd.w/o</td><TD class=t1>0.00</td></tr><TR><TD class=smallwt>Net Profit(+)/Loss(-)</td><TD class=t1>939800.00</td></tr><TR><TD class=smallwt>Non Recurring Income</td><TD class=t1>0.00</td></tr><TR><TD class=smallwt>Non Recurring Expenses</td><TD class=t1>0.00</td></tr><TR><TD class=smallwt>Adjusted Net Profit(+)/ Loss(-)</td><TD class=t1>939800.00</td></tr><TR><TD class=smallwt>Face Value of Share (in Rs.)</td><TD class=t1>10.00</td></tr><TR><TD class=smallwt>Paid-up Equity Share Capital</td><TD class=t1>139400.00</td></tr><TR><TD class=smallwt>Reserves Excluding Revaluation Reserves</td><TD class=t1>0.00</td></tr><TR><TD class=smallwt>Dividend (%)</td><TD class=t1>0.00</td></tr><TR><TD class=smallwt>Basic EPS (in Rs.)</td><TD class=t1>67.40</td></tr><TR><TD class=smallwt>Diluted EPS (in Rs.)</td><TD class=t1>67.40</td></tr><TR><TD class=smallwt>Non-promoter Shareholding (Nos.)</td><TD class=t1>0</td></tr><TR><TD class=smallwt>Non-promoter Shareholding (%)</td><TD class=t1>0.00</td></tr></table></td></tr></table></td><td valign=top><table cellpadding=0 cellspacing=1 border=0><tr bgcolor=#333333><td><table cellpadding=3 cellspacing=1 border=0><tr><td colspan=2 class=tablehead>Segment Reporting (Rs.lakhs)</td></tr><TR><TD colspan=2 class=smallwt>TOTAL REVENUE</td></tr><TR><TD class=smallwt>OTHERS</td><TD class=t1>187300.00</td><TR><TD class=smallwt>PETROCHEM</td><TD class=t1>3280200.00</td><TR><TD class=smallwt>REFINING</td><TD class=t1>7111700.00</td><TR><TD class=smallwt>UNALLOCATE</td><TD class=t1>0.00</td><TR><TD class=smallwt>Total</td><TD class=t1>1.05792E7</td></tr><tr><td class=smallwt>Less:<br>Inter Segment Revenue</td><TD class=t1><br>2276700.00</td></tr><TR><TD class=smallwt>Net Sales/Inc. from Ops.</td><TD class=t1>8302500.00</td></tr><TR><TD colspan=2 class=smallwt>RESULTS</td></tr><TR><TD class=smallwt>PETROCHEM</td><TD class=t1>471300.00</td><TR><TD class=smallwt>UNALLOCATE</td><TD class=t1>0.00</td><TR><TD class=smallwt>REFINING</td><TD class=t1>591600.00</td><TR><TD class=smallwt>OTHERS</td><TD class=t1>111200.00</td><TR><TD class=smallwt>Total</td><TD class=t1>1174100.00</td></tr><TR><TD class=smallwt>Less:<br>Interest<br>Other un-allocable <BR>expenditure net off <BR>un-allocable income</td><TD class=t1 valign=top><br>44300.00<br><br><br>27000.00</td></tr><TR><TD class=smallwt>Total Profit Before Tax</td><TD class=t1>1102800.00</td></tr><TR><TD colspan=2 class=smallwt>CAPITAL EMPLOYED</td></tr><TR><TD class=smallwt>OTHERS</td><TD class=t1>650200.00</td></tr><TR><TD class=smallwt>UNALLOCATE</td><TD class=t1>657000.00</td></tr><TR><TD class=smallwt>REFINING</td><TD class=t1>3568800.00</td></tr><TR><TD class=smallwt>PETROCHEM</td><TD class=t1>3103900.00</td></tr><TR><TD class=smallwt>Total</td><TD class=t1>7979900.00</td></tr></table></td></tr></table></td></tr></table></p><p align=justify class=smalllinks>Notes: <br><br>Other Provisions indicates Share of Loss Transfered to minority.<br><br>INTER SEGMENT REVENUE = Inter Segment Transfers of Rs.1485400 Lacs + Excise Duty Recovered on Sales of Rs. 791300 Lacs.</p><center><font class=smalllinks><a href=/marketinfo/companyinfo/eod/corp_res.jsp?symbol=RELIANCE>Back to all results of RELIANCE</a></center><BR><BR><center class=smalllinks>Other Info: <a href=/marketinfo/companyinfo/eod/announcements.jsp?symbol=RELIANCE>Announcements</a> | <a href=/marketinfo/companyinfo/eod/boardmeeting.jsp?symbol=RELIANCE>Board Meetings</a> | <a href=/marketinfo/companyinfo/eod/action.jsp?symbol=RELIANCE>Corporate Actions</a> | <a href=/marketinfo/companyinfo/eod/shareholding.jsp?symbol=RELIANCE>Shareholding Pattern</a><br><a href=/marketinfo/companyinfo/eod/address.jsp?symbol=RELIANCE>Company Address</a> | <a  href=/marketinfo/companyinfo/eod/resHistory.jsp?symbol=RELIANCE>Results Comparison</a> <!--| <a href=http://nseindia.irisindia.net/corporate/corporateInfo/snapshots/snapshot.jsp?symbol=RELIANCE>Company Profile</a> | <a href=http://nseindia.irisindia.net/corporate/corporateInfo/financials/financial.jsp?symbol=RELIANCE>Detailed Financials</a>--><BR><BR><a href=/content/corporate/corpinfo.htm>Search for another company</a><br><br><a href=#top>Top</a></center><br><br></td><td width=10></td></TR></TABLE></td><!--Content Area Ends Here--></tr></table></BODY></html>");
        pw.close();
    }

    /*
      * Test method for 'pm.net.nse.downloader.CorpResultDownloader.parseData(Reader)'
      */
    public void testParseData() {
        CorpResultDownloadManager manager = new CorpResultDownloadManager(null);
        CorpResultVO resultVO = manager.buildResultVO("http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?param=01-APR-200530-SEP-2005H1UNCNEONGC");
        CorpResultDownloader downloader = new CorpResultDownloader(null, null, resultVO);
        downloader.parseData(new StringReader(getNonBankingData()));

        assertEquals(745713.00f, resultVO.getAdjustedNetProfit());
        assertEquals(52.30f, resultVO.getEps());
        assertEquals(10.0f, resultVO.getFaceValue());
        assertEquals(769.00f, resultVO.getInterestCost());
        assertEquals(745713.0f, resultVO.getNetProfit());
        assertEquals(0.00f, resultVO.getNonRecurringExpense());
        assertEquals(0.00f, resultVO.getNonRecurringIncome());
        assertEquals(142593.00f, resultVO.getPaidUpEquityShareCapital());
        assertEquals(2354954f, resultVO.getNetSales());
        assertFalse(resultVO.isBankingFlag());

    }

    public void testParseDataToSetBankingFlag() throws Exception {
        CorpResultDownloadManager manager = new CorpResultDownloadManager(null);
        String url = "http://www.nseindia.com/marketinfo/companyinfo/eod/results.jsp?param=01-APR-200531-MAR-2006ANABCNOUNIONBANK";
        CorpResultVO resultVO = manager.buildResultVO(url);
        System.out.println(resultVO.toWrite());
        CorpResultDownloader downloader = new CorpResultDownloader(manager, url, resultVO);
        downloader.parseData(new StringReader(getBankingData()));

        assertEquals(67518.00f, resultVO.getAdjustedNetProfit());
        assertEquals(14.58f, resultVO.getEps());
        assertEquals(10.0f, resultVO.getFaceValue());
        assertEquals(348943f, resultVO.getInterestCost());
        assertEquals(67518.0f, resultVO.getNetProfit());
        assertEquals(0.00f, resultVO.getNonRecurringExpense());
        assertEquals(0.00f, resultVO.getNonRecurringIncome());
        assertEquals(50512.00f, resultVO.getPaidUpEquityShareCapital());
        assertTrue(resultVO.isBankingFlag());

    }

    private String getBankingData() {
        StringBuffer sb = new StringBuffer();
        sb.append("NSE - Financial Results Details\n");
        sb.append("Home > Corporates > Corporate Information > Financial Results\n");
        sb.append("Financial Results\n");
        sb.append("Company\n");
        sb.append("UNION BANK OF INDIA\n");
        sb.append("NSE Symbol\n");
        sb.append("UNIONBANK\n");
        sb.append("Result Period\n");
        sb.append("01-APR-2005 to 31-MAR-2006 (Annual)\n");
        sb.append("Result Type\n");
        sb.append("Audited, Cumulative, Non-Consolidated\n");
        sb.append("Banking\n");
        sb.append("Financial Results (Rs.lakhs)\n");
        sb.append("Interest/Discount on Advances/Bills\n");
        sb.append("375980.00\n");
        sb.append("Income on Investments\n");
        sb.append("195415.00\n");
        sb.append("Income on Balances With RBI & Other Inter bank Funds\n");
        sb.append("9245.00\n");
        sb.append("Others\n");
        sb.append("5731.00\n");
        sb.append("Interest Earned\n");
        sb.append("586371.00\n");
        sb.append("Other Income\n");
        sb.append("62510.00\n");
        sb.append("Total Income\n");
        sb.append("648881.00\n");
        sb.append("Interest Expended\n");
        sb.append("348943.00\n");
        sb.append("Payment to & Provisions for Employees\n");
        sb.append("86679.00\n");
        sb.append("Other Operating Expenses\n");
        sb.append("53563.00\n");
        sb.append("Operating Expenses\n");
        sb.append("140242.00\n");
        sb.append("Total Expenditure\n");
        sb.append("489185.00\n");
        sb.append("Operating Profit\n");
        sb.append("159696.00\n");
        sb.append("Other Provisions & Contingencies\n");
        sb.append("70233.00\n");
        sb.append("Provision for Taxes\n");
        sb.append("21945.00\n");
        sb.append("Misc Expenditure w/o\n");
        sb.append("0.00\n");
        sb.append("Net Profit\n");
        sb.append("67518.00\n");
        sb.append("Non Recurring Income\n");
        sb.append("0.00\n");
        sb.append("Non Recurring Expenses\n");
        sb.append("0.00\n");
        sb.append("Adjusted Net Profit\n");
        sb.append("67518.00\n");
        sb.append("Face Value of Share (in Rs.)\n");
        sb.append("10.00\n");
        sb.append("Paid-up Equity Share Capital\n");
        sb.append("50512.00\n");
        sb.append("Reserves Excluding Revaluation Reserves\n");
        sb.append("358736.00\n");
        sb.append("Dividend (%)\n");
        sb.append("35.00\n");
        sb.append("Shares Held by Government of India(%)\n");
        sb.append("55.43\n");
        sb.append("Capital Adequacy Ratio\n");
        sb.append("11.41\n");
        sb.append("Earnings Per Share (in Rs.)\n");
        sb.append("14.58\n");
        sb.append("Non-promoter Shareholding (Nos.)\n");
        sb.append("225100000\n");
        sb.append("Non-promoter Shareholding (%)\n");
        sb.append("44.57\n");
        sb.append("Segment Reporting (Rs.lakhs)\n");
        sb.append("TOTAL REVENUE\n");
        sb.append("OTHBNKOPS\n");
        sb.append("428377.00\n");
        sb.append("TREASURY\n");
        sb.append("220504.00\n");
        sb.append("Total\n");
        sb.append("648881.00\n");
        sb.append("Less:\n");
        sb.append("Inter Segment Revenue\n");
        sb.append("0.00\n");
        sb.append("Net Sales/Inc. from Ops.\n");
        sb.append("648881.00\n");
        sb.append("RESULTS\n");
        sb.append("OTHBNKOPS\n");
        sb.append("212495.00\n");
        sb.append("TREASURY\n");
        sb.append("-10544.00\n");
        sb.append("Total\n");
        sb.append("201951.00\n");
        sb.append("Less:\n");
        sb.append("Interest\n");
        sb.append("Other un-allocable\n");
        sb.append("expenditure net off\n");
        sb.append("un-allocable income\n");
        sb.append("0.00\n");
        sb.append("42255.00\n");
        sb.append("Total Profit Before Tax\n");
        sb.append("159696.00\n");
        sb.append("CAPITAL EMPLOYED\n");
        sb.append("OTHBNKOPS\n");
        sb.append("0.00\n");
        sb.append("TREASURY\n");
        sb.append("0.00\n");
        sb.append("Total\n");
        sb.append("0.00\n");
        sb.append("Back to all results of UNIONBANK\n");
        sb.append("Other Info: Announcements | Board Meetings | Corporate Actions | Shareholding Pattern\n");
        sb.append("Company Address | Results Comparison\n");
        sb.append("Search for another company\n");
        sb.append("Top\n");
        return sb.toString();
    }

    private String getNonBankingData() {
        StringBuffer sb = new StringBuffer();
        sb.append("NSE - Financial Results Details\n");
        sb.append("Home > Corporates > Corporate Information > Financial Results\n");
        sb.append("Financial Results\n");
        sb.append("Company\n");
        sb.append("OIL & NATURAL GAS CORPN LTD\n");
        sb.append("NSE Symbol\n");
        sb.append("ONGC\n");
        sb.append("Result Period\n");
        sb.append("01-APR-2005 to 30-SEP-2005 (First Half)\n");
        sb.append("Result Type\n");
        sb.append("Unaudited, Cumulative, Non-Consolidated\n");
        sb.append("Non Banking\n");
        sb.append("Financial Results (Rs.lakhs)\n");
        sb.append("Net Sales\n");
        sb.append("2354954.00\n");
        sb.append("Other Income\n");
        sb.append("116208.00\n");
        sb.append("Gross Income\n");
        sb.append("2471162.00\n");
        sb.append("Increase/Decrease in Stock\n");
        sb.append("235.00\n");
        sb.append("Consumption of Raw Materials\n");
        sb.append("223721.00\n");
        sb.append("Staff Cost\n");
        sb.append("51594.00\n");
        sb.append("Total Expenditure (Excluding Other Expenditure)\n");
        sb.append("275550.00\n");
        sb.append("Other Expenditure\n");
        sb.append("753359.00\n");
        sb.append("Total Expenditure\n");
        sb.append("1028909.00\n");
        sb.append("Interest\n");
        sb.append("769.00\n");
        sb.append("Profit(+)/Loss(-) Before Depreciation & Taxes\n");
        sb.append("1441484.00\n");
        sb.append("Depreciation\n");
        sb.append("328808.00\n");
        sb.append("Profit(+)/Loss(-)Before Tax\n");
        sb.append("1112676.00\n");
        sb.append("Provision for Taxation\n");
        sb.append("366963.00\n");
        sb.append("Other Provisions\n");
        sb.append("0.00\n");
        sb.append("Misc Expd.w/o\n");
        sb.append("0.00\n");
        sb.append("Net Profit(+)/Loss(-)\n");
        sb.append("745713.00\n");
        sb.append("Non Recurring Income\n");
        sb.append("0.00\n");
        sb.append("Non Recurring Expenses\n");
        sb.append("0.00\n");
        sb.append("Adjusted Net Profit(+)/ Loss(-)\n");
        sb.append("745713.00\n");
        sb.append("Face Value of Share (in Rs.)\n");
        sb.append("10.00\n");
        sb.append("Paid-up Equity Share Capital\n");
        sb.append("142593.00\n");
        sb.append("Reserves Excluding Revaluation Reserves\n");
        sb.append("0.00\n");
        sb.append("Dividend (%)\n");
        sb.append("0.00\n");
        sb.append("Basic EPS (in Rs.)\n");
        sb.append("52.30\n");
        sb.append("Diluted EPS (in Rs.)\n");
        sb.append("52.30\n");
        sb.append("Non-promoter Shareholding (Nos.)\n");
        sb.append("368773541\n");
        sb.append("Non-promoter Shareholding (%)\n");
        sb.append("25.88\n");
        sb.append("Segment Reporting (Rs.lakhs)\n");
        sb.append("TOTAL REVENUE\n");
        sb.append("OFFSHORE\n");
        sb.append("1666251.00\n");
        sb.append("ONSHORE\n");
        sb.append("732020.00\n");
        sb.append("UNALLOCATE\n");
        sb.append("0.00\n");
        sb.append("Total\n");
        sb.append("2398271.00\n");
        sb.append("Less:\n");
        sb.append("Inter Segment Revenue\n");
        sb.append("0.00\n");
        sb.append("Net Sales/Inc. from Ops.\n");
        sb.append("2398271.00\n");
        sb.append("RESULTS\n");
        sb.append("OFFSHORE\n");
        sb.append("917020.00\n");
        sb.append("UNALLOCATE\n");
        sb.append("0.00\n");
        sb.append("ONSHORE\n");
        sb.append("139973.00\n");
        sb.append("Total\n");
        sb.append("1056993.00\n");
        sb.append("Less:\n");
        sb.append("Interest\n");
        sb.append("Other un-allocable\n");
        sb.append("expenditure net off\n");
        sb.append("un-allocable income\n");
        sb.append("769.00\n");
        sb.append("-56452.00\n");
        sb.append("Total Profit Before Tax\n");
        sb.append("1112676.00\n");
        sb.append("CAPITAL EMPLOYED\n");
        sb.append("OFFSHORE\n");
        sb.append("1790227.00\n");
        sb.append("UNALLOCATE\n");
        sb.append("2411947.00\n");
        sb.append("ONSHORE\n");
        sb.append("1228067.00\n");
        sb.append("Total\n");
        sb.append("5430241.00\n");
        sb.append("Back to all results of ONGC\n");
        sb.append("Other Info: Announcements | Board Meetings | Corporate Actions | Shareholding Pattern\n");
        sb.append("Company Address\n");
        sb.append("Search for another company\n");
        sb.append("Top\n");
        return sb.toString();

    }

}
