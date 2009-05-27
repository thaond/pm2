/*
 * Created on 07-Mar-2005
 *
 */
package pm.net;

import org.apache.log4j.Logger;
import pm.util.AppConst;
import pm.util.ApplicationException;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.SymbolChange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class NSESymbolChangeDownloader {
    private static String NSE_SYMBOL_CHANGE_URL = "http://www.nse-india.com/content/equities/symbolchange.csv";
    private static Logger logger = Logger.getLogger(NSESymbolChangeDownloader.class);

    /*
    * Downloands and return Symbol change details
    * CompanyName,Old NSE Symbol,New NSE Symbol, Applicable From
    */
    public List<SymbolChange> download() {
        Reader reader = getData();
        return parseData(reader);
    }

    public List<SymbolChange> parseData(Reader reader) {
        Vector<SymbolChange> retVal = new Vector<SymbolChange>();
        BufferedReader br = new BufferedReader(reader);
        try {
            String line = br.readLine(); //skip header
            while ((line = br.readLine()) != null) {
                StringTokenizer stk = new StringTokenizer(line, AppConst.DELIMITER_COMMA);
                if (stk.countTokens() < 4) {
                    throw new ApplicationException("Missing data in Symbol change feed : " + line);
                }
                stk.nextToken(); // skip company name
                String oldCode = stk.nextToken();
                String newCode = stk.nextToken();
                PMDate fromDate = PMDateFormatter.parseDD_MMM_YYYY(stk.nextToken());
                SymbolChange symbolChange = new SymbolChange(oldCode, newCode, fromDate);
                retVal.add(symbolChange);
            }
        } catch (IOException e) {
            logger.error(e, e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {

            }
        }
        return retVal;
    }

    Reader getData() {
        return new HTTPHelper().getData(NSE_SYMBOL_CHANGE_URL);
    }
}
