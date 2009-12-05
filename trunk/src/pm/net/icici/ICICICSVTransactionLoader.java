package pm.net.icici;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;
import pm.dao.ibatis.dao.DAOManager;
import pm.util.AppConst;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.ICICITransaction;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ICICICSVTransactionLoader {
    private static Logger logger = Logger.getLogger(ICICICSVTransactionLoader.class);

    public void load() {
        try {
            final List<ICICITransaction> transactions = parse(new FileReader("icicitransaction.csv"));
            for (ICICITransaction transaction : transactions) {
                DAOManager.getTransactionDAO().updateOrInsertICICITransaction(transaction);
            }
        } catch (FileNotFoundException e) {
            logger.error(e, e);
        }
    }

    List<ICICITransaction> parse(Reader reader) {
        BufferedReader br = new BufferedReader(reader);
        CSVReader csvReader = new CSVReader(reader);
        List<ICICITransaction> transactions = new ArrayList<ICICITransaction>();
        try {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                transactions.add(parseLine(line));
            }
            br.close();
        } catch (IOException e) {
            logger.error(e, e);
        }
        return transactions;
    }

    ICICITransaction parseLine(String[] cells) {
        PMDate date = PMDateFormatter.parseDD_Mmm_YY(cells[0]);
        final AppConst.TRADINGTYPE action = AppConst.TRADINGTYPE.valueOf(cells[2].trim());
        return new ICICITransaction(date, cells[1].trim(), action, parseFloat(cells[3]), parseFloat(cells[4]),
                parseFloat(cells[6]), !cells[10].startsWith("IN"), cells[7].trim());
    }

    Float parseFloat(String value) {
        value = value.replaceAll("\"", "").trim();
        try {
            NumberFormat instance = NumberFormat.getNumberInstance();
            instance.setGroupingUsed(true);
            return instance.parse(value).floatValue();
        } catch (ParseException e) {
            logger.error(e, e);
            return null;
        }
    }
}
