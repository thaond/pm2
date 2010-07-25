/*
 * Created on Oct 28, 2004
 *
 */
package pm.net;

import pm.dao.ibatis.dao.DAOManager;
import pm.util.PMDateFormatter;
import pm.vo.EquityQuote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * @author thiyagu1
 */
public class ICICIQuoteDownloader extends AbstractQuoteDownloader {

    protected EquityQuote parseData(String symbol, Reader reader) throws IOException, ParseException {
        BufferedReader inBr = new BufferedReader(reader);
        EquityQuote quoteVO = new EquityQuote(symbol);
        String line = null;
        while ((line = inBr.readLine()) != null && !isStop()) {
            if (line.equals("DATE")) {
                line = inBr.readLine();
                quoteVO.setDate(PMDateFormatter.parseDD_Mmm_YYYY(line));
            } else if (line.equals("LAST TRADE PRICE")) {
                quoteVO.setLastPrice(getNextVal(inBr));
            } else if (line.equals("DAY OPEN")) {
                quoteVO.setOpen(getNextVal(inBr));
            } else if (line.equals("DAY HIGH")) {
                quoteVO.setHigh(getNextVal(inBr));
            } else if (line.equals("DAY LOW")) {
                quoteVO.setLow(getNextVal(inBr));
            } else if (line.equals("PREVIOUS DAY CLOSE")) {
                quoteVO.setPrevClose(getNextVal(inBr));
            } else if (line.equals("DAY VOLUME")) {
                quoteVO.setVolume(getNextVal(inBr));
            }
        }
        return quoteVO;
    }

    private float getNextVal(BufferedReader inBr) throws IOException, ParseException {
        String line;
        line = inBr.readLine();
        return NumberFormat.getInstance().parse(line).floatValue();
    }

    public String getURL(String symbol) {
        String iciciSymbol = getICICICode(symbol);
        if (iciciSymbol == null) {
            logger.error("Error - ICICI symbol not found for " + symbol);
            return null;
        }
        try {
            iciciSymbol = URLEncoder.encode(iciciSymbol, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return "http://getquote.icicidirect.com/trading/equity/trading_stock_quote.asp?Symbol=" + iciciSymbol;
    }

    String getICICICode(String symbol) {
        return DAOManager.getStockDAO().iciciCode(symbol);
    }

}
