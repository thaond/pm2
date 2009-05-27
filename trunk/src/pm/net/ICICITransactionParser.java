package pm.net;

import org.apache.log4j.Logger;
import org.htmlparser.util.ParserException;
import pm.util.AppConst;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.ICICITransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ICICITransactionParser {

    private static Logger _logger = Logger.getLogger(ICICITransactionParser.class);

    public static void main(String[] args) throws ParserException, IOException {

        Reader reader = new HTTPHelper().getHTMLContentReader("file:////projects/pm/icici_transaction.html");
        System.out.println(new ICICITransactionParser().parse(reader));
    }

    public List<ICICITransaction> parseHtml(String url) {
        try {
            return parse(new HTTPHelper().getHTMLContentReader(url));
        } catch (ParserException e) {
            _logger.error(e, e);
        }
        return null;
    }

    protected List<ICICITransaction> parse(Reader reader) {
        List<ICICITransaction> transactions = new ArrayList<ICICITransaction>();
        try {
            BufferedReader br = new BufferedReader(reader);
            String line;
            boolean start = false;
            int count = 0;
            PMDate date = null;
            String stockCode = null;
            AppConst.TRADINGTYPE action = null;
            float qty = 0f;
            float price = 0f;
            float brokerage = 0f;
            String reference = null;
            boolean dayTrading = true;

            while ((line = br.readLine()) != null) {
                if (!start) {
                    start = line.equals("U / P");
                    continue;
                }
                if (line.startsWith("Total")) break;

                count++;
                switch (count) {
                    case 1:
                        date = PMDateFormatter.parseDD_Mmm_YYYY(line);
                        break;
                    case 2:
                        stockCode = line;
                        break;
                    case 3:
                        action = AppConst.TRADINGTYPE.valueOf(line);
                        break;
                    case 4:
                        qty = NumberFormat.getInstance().parse(line).floatValue();
                        break;
                    case 5:
                        price = NumberFormat.getInstance().parse(line).floatValue();
                        break;
                    case 7:
                        brokerage = NumberFormat.getInstance().parse(line).floatValue();
                        break;
                    case 8:
                        reference = line;
                        break;
                    case 11:
                        dayTrading = !line.startsWith("IN");
                        break;
                    case 12:
                        transactions.add(new ICICITransaction(date, stockCode, action, qty, price, brokerage, dayTrading, reference));
                        count = 0;
                        break;
                }

            }
            br.close();
        } catch (IOException e) {
            _logger.error(e, e);
        } catch (ParseException e) {
            _logger.error(e, e);
        }
        return transactions;
    }

}
