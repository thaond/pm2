package pm.tools;

import org.apache.log4j.Logger;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;
import pm.dao.ibatis.dao.DAOManager;
import pm.net.icici.CompanyNameLookup;
import pm.vo.StockVO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ICICICodesUpdater {

    private static Logger logger = Logger.getLogger(ICICICodesUpdater.class);

    private static void readFromFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("ICICI_Trading_Stock_list.html"));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Parser parser = new Parser();
            parser.setInputHTML(sb.toString());
            StringBean stringBean = new StringBean();
            stringBean.setLinks(false);
            parser.visitAllNodesWith(stringBean);
            String[] strings = stringBean.getStrings().split("\n");
            int i = 0;
            Map<String, String> stockMapping = new HashMap<String, String>();
            while (i < strings.length) {
                if (strings[i].equals("Stock Name")) break;
                i++;
            }

            List<StockVO> list = DAOManager.getStockDAO().getStockList(false);
            for (int count = 1; i < strings.length && !strings[i].startsWith("NSE SEBI"); i++) {
                if (strings[i].equals(String.valueOf(count))) {
                    StockVO stockVO = new CompanyNameLookup(null).findNseMapping(strings[i + 2], list);
                    if (stockVO != null) stockMapping.put(strings[i + 1], stockVO.getStockCode());
                    else System.out.println("Missing for " + strings[i + 1] + " " + strings[i + 2]);
                    count++;
                }
            }

            System.out.println(stockMapping);
            System.out.println(stockMapping.size());
            System.out.println(list.size());
        } catch (FileNotFoundException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        } catch (ParserException e) {
            logger.error(e, e);
        }
    }

    public static void main(String[] args) {
        readFromFile();
    }


}
