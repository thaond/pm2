/*
 * Created on 03-Mar-2005
 *
 */
package pm.report;

import pm.bo.PortfolioBO;
import pm.util.AppConst.REPORT_TYPE;
import pm.util.Helper;
import pm.vo.ConsolidatedTradeVO;
import pm.vo.StopLossVO;

import java.util.Vector;

/**
 * @author thiyagu1
 */
public class PortfolioReport {

    private static float SL_DIFF_PER = 1f;
    private static char _NEWLINE = '\n';

    //TODO get Quote should perform corp action for previous days close
    //TODO Days profit calculation wrong, currday's buying is also factored for previous days loss
    public static String getReport(String portfolioName) {
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">").append(_NEWLINE);
        sb.append("<html>").append(_NEWLINE);
        sb.append("<head>").append(_NEWLINE);
        sb.append("<title>Portfolio Report</title>").append(_NEWLINE);
        sb.append("<style type=\"text/css\">").append(_NEWLINE);
        sb.append(" .loss {color: #ff0000;text-align: center;}").append(_NEWLINE);
        sb.append(" .profit {color: #008000;text-align: center;}").append(_NEWLINE);
        sb.append(" .t2 {color: #008080;}").append(_NEWLINE);
        sb.append(" .t1 {color: #008000;}").append(_NEWLINE);
        sb.append(" .s1 {color: #ff6666;}").append(_NEWLINE);
        sb.append(" .s2 {color: #ff0000;}").append(_NEWLINE);
        sb.append(" .tableheaderbg {background-color: #3399ff;}").append(_NEWLINE);
        sb.append(" .totbg {background-color: #3399ff;text-align: center;}").append(_NEWLINE);
        sb.append(" .tablecell {background-color: #ffffff;text-align: center;}").append(_NEWLINE);
        sb.append("</style>").append(_NEWLINE);
        sb.append("</head>").append(_NEWLINE);
        sb.append("<body>").append(_NEWLINE);
        PortfolioBO portfolioBO = new PortfolioBO();
        Vector<ConsolidatedTradeVO> consolidatedTradeList = portfolioBO.getPortfolioView(REPORT_TYPE.All.name(), portfolioName, REPORT_TYPE.All.name());
        Vector<StopLossVO> stopLossList = portfolioBO.getStopLossDetailsWithQuoteFilterForNonSet(portfolioName);
        sb.append(getHoldingReport(consolidatedTradeList, stopLossList));
        sb.append("<BR>").append(_NEWLINE);
        sb.append(getStopLossReport(portfolioName, stopLossList));
        sb.append("</body>").append(_NEWLINE);
        sb.append("</html>").append(_NEWLINE);
        return sb.toString();
    }

    /**
     * @param portfolioName
     * @param stopLossList
     * @return
     */
    private static String getStopLossReport(String portfolioName, Vector<StopLossVO> stopLossList) {
        StringBuffer sb = new StringBuffer();
        sb.append("<h2 align='center'>Stop Loss / Target Alert</h2>").append(_NEWLINE);
        sb.append("<table border='0' cellpadding='0' cellspacing='0' align='center'>").append(_NEWLINE);
        sb.append("<tr><td>").append(_NEWLINE);
        for (StopLossVO slVO : stopLossList) {

            if (slVO.getQuoteVO().getLastPrice() < slVO.getStopLoss2()) {
                sb.append("<li class='s2'>").append(slVO.getStockCode()).append(" below StopLoss 2 [").append(slVO.getStopLoss2()).append("]</li>").append(_NEWLINE);
            } else if (slVO.getQuoteVO().getLastPrice() < slVO.getStopLoss1()) {
                sb.append("<li class='s1'>").append(slVO.getStockCode()).append(" below StopLoss 1 [").append(slVO.getStopLoss1()).append("]</li>").append(_NEWLINE);
            } else if (slVO.getQuoteVO().getLastPrice() > slVO.getTarget2()) {
                sb.append("<li class='t1'>").append(slVO.getStockCode()).append(" above Target 2 [").append(slVO.getTarget2()).append("]</li>").append(_NEWLINE);
            } else if (slVO.getQuoteVO().getLastPrice() > slVO.getTarget1()) {
                sb.append("<li class='t2'>").append(slVO.getStockCode()).append(" above Target 1 [").append(slVO.getTarget1()).append("]</li>").append(_NEWLINE);
            }
        }
        sb.append("</td></tr>").append(_NEWLINE);
        sb.append("</table>").append(_NEWLINE);
        return sb.toString();
    }

    /**
     * @param consolidatedTradeList
     * @param stopLossList
     * @return
     */
    private static String getHoldingReport(Vector<ConsolidatedTradeVO> consolidatedTradeList, Vector<StopLossVO> stopLossList) {
        StringBuffer sb = new StringBuffer();
        float totRealizedPL, totDivident, totCost, totMarketValue, totPL, totTodayPL, totMarketValueSL1, totMarketValueSL2;
        float totMarketValueT1, totMarketValueT2;
        totRealizedPL = totDivident = totCost = totMarketValue = totPL = totTodayPL = 0;
        totMarketValueSL1 = totMarketValueSL2 = totMarketValueT1 = totMarketValueT2 = 0;

        sb.append("<h2 align='center'>Holding Details</h2>").append(_NEWLINE);
        sb.append("<table border='1' cellpadding='0' cellspacing='0' align='center'>").append(_NEWLINE);
        sb.append("<TR><TH class='tableheaderbg'>StockCode</TH><TH class='tableheaderbg'>Qty</TH><TH class='tableheaderbg'>Cost/Unit</TH>");
        sb.append("<TH class='tableheaderbg'>Market Price</TH><TH class='tableheaderbg'>Cost</TH><TH class='tableheaderbg'>Market Value</TH><TH class='tableheaderbg'>Today's P/L</TH><TH class='tableheaderbg'>Total P/L</TH>");
        sb.append("<TH class='tableheaderbg'>Total P/L%</TH></TR>").append(_NEWLINE);
        for (ConsolidatedTradeVO ctVO : consolidatedTradeList) {
            if (ctVO.getQty() > 0) {
                sb.append(printHoldingDetails(ctVO));
                StopLossVO stopLossVO = getSTVO(ctVO.getStockCode(), stopLossList);
                if (stopLossVO != null) {
                    totMarketValueSL1 += (ctVO.getQty() * stopLossVO.getStopLoss1());
                    totMarketValueSL2 += (ctVO.getQty() * stopLossVO.getStopLoss2());
                    totMarketValueT1 += (ctVO.getQty() * stopLossVO.getTarget1());
                    totMarketValueT2 += (ctVO.getQty() * stopLossVO.getTarget2());
                }
            }
            totRealizedPL += ctVO.getProfitLoss();
            totDivident += ctVO.getDivident();
            totCost += ctVO.getCost();
            totMarketValue += ctVO.getCurrentValue();
            totPL += ctVO.getUnRealizedPL();
            totTodayPL += ctVO.getTodaysPL();
        }
        float totPLPer = totPL / totCost * 100;
        sb.append("<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td class='totbg'><b>Total</b></td>");
        sb.append("<td class='totbg'>").append(totCost).append("</td>");
        sb.append("<td class='totbg'>").append(totMarketValue).append("</td>");
        sb.append("<td class='totbg'>").append(totTodayPL).append("</td>");
        sb.append("<td class='totbg'>").append(totPL).append("</td>");
        sb.append("<td class='totbg'>").append(totPLPer).append("</td></tr>").append(_NEWLINE);
        sb.append("</TABLE>").append(_NEWLINE);
        sb.append("<br>").append(_NEWLINE);
        float netProfit = totMarketValue - totCost + totDivident + totRealizedPL;
        sb.append("<h2 align='center'>Portfolio Overview</h2>").append(_NEWLINE);
        sb.append("<table border='1' cellpadding='0' cellspacing='1' align='center'>").append(_NEWLINE);
        sb.append("<tr><TH class='tableheaderbg'></th><TH class='tableheaderbg'>@ StopLoss2</th><TH class='tableheaderbg'>@ StopLoss1</th><TH class='tableheaderbg'>@ Current Price</th><TH class='tableheaderbg'>@ Target1</th><TH class='tableheaderbg'>@ Target2</th></tr>");
        sb.append("<tr><td align='right'><b> Total Invested </b></td><td></td><td></td><td align='center'>").append(totCost).append("</td><td></td><td></td></tr>");
        sb.append("<tr><td align='right'><b> Profit Booked </b></td></td><td></td><td></td><td align='center'>").append(totRealizedPL).append("</td><td></td><td></td></tr>");
        sb.append("<tr><td align='right'><b> Divident </b></td><td></td><td></td><td align='center'>").append(totDivident).append("</td><td></td><td></td></tr>");
        sb.append("<tr><td align='right'><b> Market Value </b></td>");
        sb.append("<td align='center'>").append(totMarketValueSL2).append("</td>");
        sb.append("<td align='center'>").append(totMarketValueSL1).append("</td>");
        sb.append("<td align='center'>").append(totMarketValue).append("</td>");
        sb.append("<td align='center'>").append(totMarketValueT1).append("</td>");
        sb.append("<td align='center'>").append(totMarketValueT2).append("</td>");
        sb.append("</tr>");
        float sometot = netProfit - totMarketValue;
        sb.append("<tr><td align='right'><b> Net Profit </b></td>");
        sb.append("<td align='center'>").append(sometot + totMarketValueSL2).append("</td>");
        sb.append("<td align='center'>").append(sometot + totMarketValueSL1).append("</td>");
        sb.append("<td align='center'>").append(netProfit).append("</td>");
        sb.append("<td align='center'>").append(sometot + totMarketValueT1).append("</td>");
        sb.append("<td align='center'>").append(sometot + totMarketValueT2).append("</td>");
        sb.append("</tr>");
        sb.append("</table>").append(_NEWLINE);
        return sb.toString();
    }

    private static StopLossVO getSTVO(String stockCode, Vector<StopLossVO> stopLossList) {
        for (StopLossVO lossVO : stopLossList) {
            if (lossVO.getStockCode().equals(stockCode))
                return lossVO;
        }
        return null;
    }

    /**
     * @param ctVO
     * @return
     */
    private static String printHoldingDetails(ConsolidatedTradeVO ctVO) {
        StringBuffer sb = new StringBuffer();
        sb.append("<TR>");
        sb.append("<TD class='tablecell'>").append(ctVO.getStockCode()).append("</TD>");
        sb.append("<TD class='tablecell'>").append(Helper.formatFloat(ctVO.getQty())).append("</TD>");
        sb.append("<TD class='tablecell'>").append(Helper.formatFloat(ctVO.getCostPerUnit())).append("</TD>");

        if (ctVO.getCurrQuote().getPerChange() > 0) sb.append("<TD class='profit'>");
        else sb.append("<TD class='loss'>");
        sb.append(Helper.formatFloat(ctVO.getCurrQuote().getLastPrice())).append("&nbsp;<b>(");
        sb.append(Helper.formatFloat(ctVO.getCurrQuote().getPerChange())).append("%)<b></TD>");

        sb.append("<TD class='tablecell'>").append(Helper.formatFloat(ctVO.getCost())).append("</TD>");
        sb.append("<TD class='tablecell'>").append(Helper.formatFloat(ctVO.getCurrentValue())).append("</TD>");
        if (ctVO.getTodaysPL() > 0) sb.append("<TD class='profit'>");
        else sb.append("<TD class='loss'>");
        sb.append(Helper.formatFloat(ctVO.getTodaysPL())).append("</TD>");
        if (ctVO.getUnRealizedPL() > 0) sb.append("<TD class='profit'>");
        else sb.append("<TD class='loss'>");
        sb.append(Helper.formatFloat(ctVO.getUnRealizedPL())).append("</TD>");
        if (ctVO.getUnRealizedPLPer() > 0) sb.append("<TD class='profit'>");
        else sb.append("<TD class='loss'>");
        sb.append(Helper.formatFloat(ctVO.getUnRealizedPLPer())).append("</TD>");
        sb.append("</TR>").append(_NEWLINE);
        return sb.toString();
    }

}
