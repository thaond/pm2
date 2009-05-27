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

public class SymbolLookup {

    private final String url = "http://content.icicidirect.com/findsymbolchart.asp";
    private final static Logger logger = Logger.getLogger(SymbolLookup.class);
    private final StockVO stockVO;

    public SymbolLookup(StockVO stockVO) {
        this.stockVO = stockVO;
    }

    String lookup(String companyName) {
        try {
            companyName = companyName.replaceFirst("Limited", "");
            Reader reader = new HTTPHelper().getHTMLContentReaderUsingPost(url, "Symbol=" + URLEncoder.encode(companyName, "UTF8"));
            return parse(reader, companyName);
        } catch (ParserException e) {
            logger.error(e, e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e, e);
        }
        return null;
    }

    public void updateMapping() {
        String iciciCode = lookup(stockVO.getCompanyName());
        if (iciciCode != null) {
            DAOManager.getStockDAO().updateICICICode(stockVO, iciciCode);
        }
    }

    private String parse(Reader reader, String companyName) {
        BufferedReader br = new BufferedReader(reader);
        String line;
        String stockCode = null;
        int stockCodeCount = 0;
        boolean processStockCode = false;
        boolean skipThis = false;
        try {
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if (line.equals("|X| CLOSE")) {
                    if (stockCodeCount == 1) return stockCode;
                    logger.info("ICICI Code # of matches found " + stockCodeCount + " for " + companyName);
                    return null;
                }
                if (processStockCode) {
                    if (!skipThis) {
                        stockCode = line;
                        stockCodeCount++;
                    }
                    skipThis = !skipThis;
                } else {
                    processStockCode = line.equals("Company Name");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stockCode;
    }
}
