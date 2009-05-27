package pm.net.nse.downloader;

import junit.framework.TestCase;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Vector;

public class CorpResultLinkDownloaderTest extends TestCase {

    String fileName = "CorpResultLinkDownloaderTestFile.txt";

    /*
      * Test method for
      * 'pm.net.nse.downloader.CorpResultLinkDownloader.performTask()'
      */
    public void testPerformTask() throws FileNotFoundException {
        createInputFile();
        CorpResultLinkDownloader downloader = new CorpResultLinkDownloader(
                null, null) {
            @Override
            protected Parser getParser() throws ParserException {
                return new Parser(fileName);
            }
        };
        downloader.performTask();
        Vector<String> linkList = downloader.getLinkList();
        assertEquals(11, linkList.size());
        assertEquals("file://localhost/marketinfo/companyinfo/eod/results.jsp?01-OCT-200531-DEC-2005Q3UNNNEONGC", linkList.elementAt(0));
        assertEquals("file://localhost/marketinfo/companyinfo/eod/results.jsp?01-APR-200430-JUN-2004Q1UNNNEONGC", linkList.elementAt(10));

        deleteFile();
    }

    private void deleteFile() {
        File file = new File(fileName);
        file.deleteOnExit();
    }

    private void createInputFile() throws FileNotFoundException {
        String fileContent = "<!DOCTYPE HTML PUBLIC '-//IETF//DTD HTML 3.0//EN'><HTML><HEAD><TITLE>NSE - Corporates - Financial Results</TITLE><LINK href='/nse.css' rel=STYLESHEET type=text/css><script language='javascript' src='/js/commonfuncs.js'></script><script language='javascript'>	var isIE=(document.all)?1:0;	var isNS4=(document.layers)?1:0;	var isNS6=((document.getElementById)&&(navigator.appName=='Netscape'))?1:0;	var isNS=((isNS4)||(isNS6))?1:0;	var DHTML=(isIE||isNS4||isNS6)?1:0;	function handleError() {		return true;	}	window.onerror = handleError;	capEvents();</script><script language='javascript' src='/js/corp_leftnav.js'></script></HEAD><BODY bgcolor='#ffffff' leftmargin=0 topmargin=0 marginheight=0 marginwidth=0><div name='menulayer' id='menulayer' class=menufont style='background-color: #f5c078; visibility:hidden; position:absolute; width:1px; height:1px; z-index:1; left:1; top:1'></div><TABLE cellspacing=0 border=0 cellpadding=0 height=100%><tr><!--Left Nav Bar Starts Here--><script language='javascript'>	writeLeftNav();</script><!--Left Nav Bar Ends Here--><!--Content Area Starts Here--><td width=600 valign=top><TABLE CELLPADDING=0 CELLSPACING=0 BORDER=0 width=592 height=100%><tr height=50><td colspan=3 valign=top class=smalllinks><a name='top'><br></a>&nbsp;&nbsp;<a href='/homepage.htm'>Home</a> &gt; <a href=/content/corporate/corp_introduction.htm>Corporates</a> &gt; <a href=/content/corporate/corpinfo.htm>Corporate Information</a> &gt; Financial Results<br><br></td></tr><TR><TD width=20>&nbsp;</TD><TD valign=top><font class=header>Corporate Information</font><br><font class=header3>Financial Results</font><br><br><table border=0 cellspacing=1 cellpadding=4 align=center width=400><tr><td class=tablehead width=100>Company</td><td class=t0>OIL & NATURAL GAS CORPN LTD                                           </td></tr><tr><td class=tablehead width=100>NSE Symbol</td><td class=t0><a href=/marketinfo/equities/cmquote.jsp?key=ONGCEQN&symbol=ONGC&flag=0>ONGC</a></td></tr></table><br><center class=smalllinks>Following results are available for the selected security. </center><br><table cellpadding=0 cellspacing=0 border=0 align=center bgcolor=#969696><tr><td><table cellpadding=2 border=0 cellspacing=1 align=center width=550><tr><td class=tablehead>Result Period</td><td class=tablehead>Results Type</td></tr><td class=t2><a href=/marketinfo/companyinfo/eod/results.jsp?01-OCT-200531-DEC-2005Q3UNNNEONGC>01-OCT-2005 to 31-DEC-2005 (Third Quarter)</a></td><td class=t2>Unaudited, Non-cumulative, Non-consolidated</td></tr><td class=t2><a href=/marketinfo/companyinfo/eod/results.jsp?01-APR-200530-SEP-2005H1UNCNEONGC>01-APR-2005 to 30-SEP-2005 (First Half)</a></td><td class=t2>Unaudited, Cumulative, Non-consolidated</td></tr><td class=t2><a href=/marketinfo/companyinfo/eod/results.jsp?01-JUL-200530-SEP-2005Q2UNNNEONGC>01-JUL-2005 to 30-SEP-2005 (Second Quarter)</a></td><td class=t2>Unaudited, Non-cumulative, Non-consolidated</td></tr><td class=t2><a href=/marketinfo/companyinfo/eod/results.jsp?01-APR-200530-JUN-2005Q1UNNNEONGC>01-APR-2005 to 30-JUN-2005 (First Quarter)</a></td><td class=t2>Unaudited, Non-cumulative, Non-consolidated</td></tr><td class=t2><a href=/marketinfo/companyinfo/eod/results.jsp?01-APR-200431-MAR-2005ANANCCEONGC>01-APR-2004 to 31-MAR-2005 (Annual)</a></td><td class=t2>Audited, Cumulative, Consolidated</td></tr><td class=t2><a href=/marketinfo/companyinfo/eod/results.jsp?01-APR-200431-MAR-2005ANANCNEONGC>01-APR-2004 to 31-MAR-2005 (Annual)</a></td><td class=t2>Audited, Cumulative, Non-consolidated</td></tr><td class=t2><a href=/marketinfo/companyinfo/eod/results.jsp?01-JAN-200531-MAR-2005Q4ANNNEONGC>01-JAN-2005 to 31-MAR-2005 (Fourth Quarter)</a></td><td class=t2>Audited, Non-cumulative, Non-consolidated</td></tr><td class=t2><a href=/marketinfo/companyinfo/eod/results.jsp?01-OCT-200431-DEC-2004Q3UNNNEONGC>01-OCT-2004 to 31-DEC-2004 (Third Quarter)</a></td><td class=t2>Unaudited, Non-cumulative, Non-consolidated</td></tr><td class=t2><a href=/marketinfo/companyinfo/eod/results.jsp?01-APR-200430-SEP-2004H1UNCNEONGC>01-APR-2004 to 30-SEP-2004 (First Half)</a></td><td class=t2>Unaudited, Cumulative, Non-consolidated</td></tr><td class=t2><a href=/marketinfo/companyinfo/eod/results.jsp?01-JUL-200430-SEP-2004Q2UNNNEONGC>01-JUL-2004 to 30-SEP-2004 (Second Quarter)</a></td><td class=t2>Unaudited, Non-cumulative, Non-consolidated</td></tr><td class=t2><a href=/marketinfo/companyinfo/eod/results.jsp?01-APR-200430-JUN-2004Q1UNNNEONGC>01-APR-2004 to 30-JUN-2004 (First Quarter)</a></td><td class=t2>Unaudited, Non-cumulative, Non-consolidated</td></tr></TABLE></td></tr></table><br><br><br><br><center class=smalllinks>Other Info: <a href=/marketinfo/companyinfo/eod/announcements.jsp?symbol=ONGC>Announcements</a> | <a href=/marketinfo/companyinfo/eod/boardmeeting.jsp?symbol=ONGC>Board Meetings</a> | <a href=/marketinfo/companyinfo/eod/action.jsp?symbol=ONGC>Corporate Actions</a> | <a href=/marketinfo/companyinfo/eod/shareholding.jsp?symbol=ONGC>Shareholding Pattern</a><br><a href=/marketinfo/companyinfo/eod/address.jsp?symbol=ONGC>Company Address</a> | <a  href=/marketinfo/companyinfo/eod/resHistory.jsp?symbol=ONGC>Results Comparison</a> <!--| <a href=http://nseindia.irisindia.net/corporate/corporateInfo/snapshots/snapshot.jsp?symbol=ONGC>Company Profile</a> | <a href=http://nseindia.irisindia.net/corporate/corporateInfo/financials/financial.jsp?symbol=ONGC>Detailed Financials</a>--><BR><BR><a href=/content/corporate/corpinfo.htm>Search for another company</a><br><br><a href=#top>Top</a></center><br><br></TD><td width=10></td></TR></TABLE></td><!--Content Area Ends Here--></tr></table></BODY></html>";
        PrintWriter pw = new PrintWriter(fileName);
        pw.write(fileContent);
        pw.close();
    }

    /*
      * Test method for 'pm.net.nse.downloader.CorpResultLinkDownloader.getURL()'
      */
    public void testGetURL() throws Exception {

        CorpResultLinkDownloader downloader = new CorpResultLinkDownloader(null, "M&M");
        assertEquals("http://www.nse-india.com/marketinfo/companyinfo/eod/corp_res.jsp?symbol=M%26M", downloader.getURL());

    }

}
