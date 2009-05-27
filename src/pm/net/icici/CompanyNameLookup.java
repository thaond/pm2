package pm.net.icici;

import org.apache.log4j.Logger;
import org.htmlparser.util.ParserException;
import pm.dao.ibatis.dao.DAOManager;
import pm.net.HTTPHelper;
import pm.vo.StockVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CompanyNameLookup implements Runnable {

    private final String url = "http://content.icicidirect.com/findsymbolchart.asp";
    private final static Logger _logger = Logger.getLogger(SymbolLookup.class);
    private String iciciCode;

    public CompanyNameLookup(String iciciCode) {
        this.iciciCode = iciciCode;
    }

    String lookup() {
        try {
            Reader reader = new HTTPHelper().getHTMLContentReaderUsingPost(url, "Symbol=" + URLEncoder.encode(iciciCode, "UTF8"));
            return parse(reader, iciciCode);
        } catch (ParserException e) {
            _logger.error(e, e);
        } catch (UnsupportedEncodingException e) {
            _logger.error(e, e);
        }
        return null;
    }

    public StockVO findNseMappingold(String iciciCompanyName, List<StockVO> stocks) {
        List<StockVO> matching = new ArrayList<StockVO>();
        iciciCompanyName = canonicalize(iciciCompanyName);
        for (StockVO stock : stocks) {
            if (canonicalize(stock.getCompanyName()).startsWith(iciciCompanyName) || stock.getStockCode().equalsIgnoreCase(iciciCompanyName)) {
                matching.add(stock);
            }
        }
        if (matching.size() == 1) return matching.get(0);

        int index = iciciCompanyName.lastIndexOf(" ");
        iciciCompanyName = index > 0 ? iciciCompanyName.substring(0, index).toLowerCase() : "";
        return iciciCompanyName.isEmpty() ? null : findNseMappingold(iciciCompanyName, stocks);
    }

    public StockVO findNseMapping(String iciciCompanyName, List<StockVO> stocks) {
        List<StockVO> matching = new ArrayList<StockVO>();
        iciciCompanyName = canonicalize(iciciCompanyName);
        for (StockVO stock : stocks) {
            String alteredCompanyName = canonicalize(stock.getCompanyName());

            if (alteredCompanyName.equals(iciciCompanyName) || stock.getStockCode().equalsIgnoreCase(iciciCompanyName)) {
                matching.add(stock);
                if (matching.size() > 1) break;
            }
        }
        if (matching.size() == 1) return matching.get(0);

        matching = new ArrayList<StockVO>();
        iciciCompanyName = canonicalize(iciciCompanyName);
        for (StockVO stock : stocks) {
            String alteredCompanyName = canonicalize(stock.getCompanyName());
            int size = (alteredCompanyName.length() < iciciCompanyName.length()) ? alteredCompanyName.length() : iciciCompanyName.length();

            String shortForm1 = iciciCompanyName.substring(0, size);
            String shortForm2 = alteredCompanyName.substring(0, size);
            if (shortForm1.equals(shortForm2)) {
                matching.add(stock);
                if (matching.size() > 1) break;
            }
        }
        if (matching.size() == 1) return matching.get(0);
        return findNseMappingold(iciciCompanyName, stocks);
    }

    private String canonicalize(String string) {
        string = string.toLowerCase();
        string = string.replaceAll("limited", "");
        string = string.replaceAll("ltd", "");
        string = string.replaceAll("and", "");
        string = string.replaceAll("&", "");
        string = string.replaceAll(" ", "");
        string = string.replaceAll("-", "");
        return string;
    }

    List<StockVO> stockList() {
        return DAOManager.getStockDAO().getStockList(false);
    }

    private String parse(Reader reader, String iciciCode) {
        BufferedReader br = new BufferedReader(reader);
        String line;
        boolean processCompanyName = false;
        try {
            while ((line = br.readLine()) != null) {
                if (processCompanyName) return line;
                processCompanyName = line.equals(iciciCode);
            }
        } catch (IOException e) {
            _logger.error(e, e);
        }
        return null;
    }

    public void run() {
        StockVO stockVO = findNseMapping(lookup(), stockList());
        if (stockVO != null) DAOManager.getStockDAO().updateICICICode(stockVO, iciciCode);
    }
}