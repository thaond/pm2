/*
 * Created on 17-Jan-2005
 *
 */
package pm.net;

import pm.dao.ibatis.dao.DAOManager;
import pm.util.ApplicationException;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.QuoteVO;

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
public class YahooQuoteDownloader extends AbstractQuoteDownloader {

    private final static String baseURL = "http://in.finance.yahoo.com/q?s=";
    private final static String urlExtn = "&d=d&o=t";

    protected QuoteVO parseData(String symbol, Reader reader) throws IOException, ParseException {
        QuoteVO quoteVO = new QuoteVO(symbol);
        BufferedReader br = new BufferedReader(reader);
        String line;
        boolean startProcess = false;
        int index = 0;
        int rangeColumnIndex = -1;
        while ((line = br.readLine()) != null) {
            index++;
            if (!startProcess && line.contains("Delayed quote data")) {
                startProcess = true;
                index = 0;
            }
            if (!startProcess) {
                continue;
            }
            switch (index) {
                case 1:
                    rangeColumnIndex = line.contains("Index") ? 12 : 18;
                    break;
                case 2:
                    quoteVO.setLastPrice(getFloat(line));
                    break;
                case 4:
                    if (line.indexOf(':') != -1) { //current days data
                        quoteVO.setDate(new PMDate());
                    } else { //some other days data sample [16 Mar 907.55]
                        try {
                            quoteVO.setDate(PMDateFormatter.parseMMMspDD(line));
                        } catch (ApplicationException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 8:
                    quoteVO.setPrevClose(getFloat(line));
                    break;
                case 10:
                    quoteVO.setOpen(getFloat(line));
                    break;
            }
            if (index == rangeColumnIndex) {
                int loc = line.indexOf("-");
                quoteVO.setLow(getFloat(line.substring(0, loc - 1)));
                quoteVO.setHigh(getFloat(line.substring(loc + 1).trim()));
                break;
            }
        }
        return quoteVO;
    }

    private float getFloat(String line) throws ParseException {
        if (line.matches("N/A")) {
            return 0f;
        }
        return NumberFormat.getNumberInstance().parse(line).floatValue();
    }

    @Override
    public String getURL(String symbol) {
        String yahooSymbol = getYahooCode(symbol);
        if (yahooSymbol == null) {
            yahooSymbol = symbol;
        }
        try {
            yahooSymbol = URLEncoder.encode(yahooSymbol, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return baseURL + yahooSymbol + urlExtn;
    }

    String getYahooCode(String symbol) {
        return DAOManager.getStockDAO().yahooCode(symbol);
    }
}
