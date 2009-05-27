/*
 * Created on 24-Feb-2005
 *
 */
package pm.tools;

import pm.vo.QuoteVO;

import java.util.Vector;

/**
 * @author thiyagu1
 */
public class HTMLGenerator {

    public static void main(String[] args) {
    }

    /**
     * @param data
     * @return
     */
    public static String generateHTML(Vector<QuoteVO> data) {
        StringBuffer sb = new StringBuffer();
        sb.append("<HTML>");
        sb.append("<HEAD>");
        sb.append("<TITLE>MY FIRST REPORT</TITLE>");
        sb.append("</HEAD>");
        sb.append("<BODY>");
        sb.append("<TABLE BORDER=1>");
        sb.append("<TR><TH>StockCode</TD><TD>Score</TH></TR>");
        for (QuoteVO quoteVO : data) {
            sb.append("<TR>");
            sb.append("<TD><B>").append(quoteVO.getStockCode()).append("</B></TD>");
            sb.append("<TD>").append(quoteVO.getScoreCard()).append("</TD>");
            sb.append("</TR>");
        }
        sb.append("</TABLE>");
        sb.append("</BODY>");
        sb.append("</HTML>");
        //System.out.println(sb.toString());
        return sb.toString();
    }
}
