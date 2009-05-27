package pm.net;

import pm.vo.QuoteVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;

/*
 * Created on Aug 3, 2004
 *
 */
/**
 * @author thiyagu1
 */
public class NSEQuoteDownloader extends AbstractQuoteDownloader {

    protected QuoteVO parseData(String symbol, Reader reader) throws IOException, ParseException {
        BufferedReader inBr = new BufferedReader(reader);
        QuoteVO quoteVO = new QuoteVO(symbol);
        String line = null;
        boolean line1Flag = false;
        boolean line2Flag = false;
        while ((line = inBr.readLine()) != null && !isStop()) {
            if (line2Flag && line.startsWith(" <TABLE")) {
                processLine2(line, quoteVO);
                break;
            }
            if (line1Flag && line.startsWith(" <TABLE")) {
                processLine1(line, quoteVO);
                line1Flag = false;
                line2Flag = true;
            }
            if (line.indexOf("Price & Turnover Information") != -1) {
                line1Flag = true;
            }
        }
        return quoteVO;
    }

    private void processLine2(String line, QuoteVO quoteVO) {
        //Eg:[ <TABLE BORDER="0" ALIGN="CENTER" CELLPADDING="4" CELLSPACING="1" bgcolor="#FFFFFF" width="100%"> <tr><th class=specialhead2>Last Price</th><th class=specialhead2>Change from prev close</th><th class=specialhead2>% Change from prev close</th><th class=specialhead2>Total traded quantity</th><th class=specialhead2>Turnover in Rs.Lakhs</th></tr><tr><td class=t1>775.50</td><td class=t1>1.05</td><td class=t1>0.14</td><td class=t1>250197</td><td class=t1>1935.07</td></tr></table>]
        int st = line.indexOf("<td class=") + 13;
        int ed = line.indexOf("</td>", st);
        String str = line.substring(st, ed);
        try {
            quoteVO.setLastPrice(NumberFormat.getInstance().parse(str).floatValue());
        } catch (ParseException e) {
            System.out.println("Error in NSE Quote Parse1.1 - " + str);
            quoteVO.setLastPrice(-1f);
        }

        st = line.indexOf("<td class=", st) + 13; //skip change value
        st = line.indexOf("<td class=", st) + 13; //skip percentage of change value

        st = line.indexOf("<td class=", st) + 13;
        ed = line.indexOf("</td>", st);
        str = line.substring(st, ed);
        try {
            quoteVO.setVolume(NumberFormat.getInstance().parse(str).floatValue());
        } catch (ParseException e) {
            System.out.println("Error in NSE Quote Parse1.3 - " + str);
            quoteVO.setVolume(-1f);
        }


    }

    private void processLine1(String line, QuoteVO quoteVO) {
        //Eg:[ <TABLE BORDER="0" ALIGN="CENTER" CELLPADDING="4" CELLSPACING="1" bgcolor="#FFFFFF" width="100%"> <tr><th class=specialhead2>Prev. Close</th><th class=specialhead2>Open</th><th class=specialhead2>High</th><th class=specialhead2>Low</th><th class=specialhead2>Average Price</th></tr><tr><td class=t1>774.45</td><td class=t1>778.80</td><td class=t1>778.80</td><td class=t1>770.00</td><td class=t1>773.42</td></tr></table>]
        int st = line.indexOf("<td class=") + 13;
        int ed = line.indexOf("</td>", st);
        String str = line.substring(st, ed);
        try {
            quoteVO.setPrevClose(NumberFormat.getInstance().parse(str).floatValue());
        } catch (ParseException e) {
            System.out.println("Error in NSE Quote Parse2.1 - " + str);
            quoteVO.setPrevClose(-1f);
        }
        st = line.indexOf("<td class=", st) + 13;
        ed = line.indexOf("</td>", st);
        str = line.substring(st, ed);
        try {
            quoteVO.setOpen(NumberFormat.getInstance().parse(str).floatValue());
        } catch (ParseException e) {
            System.out.println("Error in NSE Quote Parse2.2 - " + str);
            quoteVO.setOpen(-1f);
        }
        st = line.indexOf("<td class=", st) + 13;
        ed = line.indexOf("</td>", st);
        str = line.substring(st, ed);
        try {
            quoteVO.setHigh(NumberFormat.getInstance().parse(str).floatValue());
        } catch (ParseException e) {
            System.out.println("Error in NSE Quote Parse2.3 - " + str);
            quoteVO.setHigh(-1f);
        }
        st = line.indexOf("<td class=", st) + 13;
        ed = line.indexOf("</td>", st);
        str = line.substring(st, ed);
        try {
            quoteVO.setLow(NumberFormat.getInstance().parse(str).floatValue());
        } catch (ParseException e) {
            System.out.println("Error in NSE Quote Parse2.4 - " + str);
            quoteVO.setLow(-1f);
        }
    }

    public static void main(String[] arg) {
        //http://www.nseindia.com/marketinfo/equities/cmquote.jsp?key=ONGCEQN&symbol=ONGC&flag=0
        try {
            System.out.println(new NSEQuoteDownloader().getQuote("VSNL"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getURL(String symbol) {
        try {
            symbol = URLEncoder.encode(symbol, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        String url = "http://www.nseindia.com/marketinfo/equities/cmquote.jsp?key=" + symbol + "EQN&symbol=" + symbol + "&flag=0";
        return url;
    }
}
