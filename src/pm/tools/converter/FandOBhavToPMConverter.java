package pm.tools.converter;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;
import pm.dao.ibatis.dao.DAOManager;
import pm.net.nse.BhavFileUtil;
import pm.util.DateIterator;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.util.enumlist.FOTYPE;
import pm.vo.FOQuote;
import pm.vo.StockVO;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static pm.util.enumlist.FOTYPE.Future;

public class FandOBhavToPMConverter {

    private Logger logger = Logger.getLogger(FandOBhavToPMConverter.class);

    public void processFile(PMDate date) {
        logger.info("processing f&o : " + date);
        try {
            CSVReader csvReader = new CSVReader(reader(date));
            List<FOQuote> quotes = parseFile(date, csvReader);
            save(quotes);
            moveInputFileToBackup(date);
        } catch (IOException e) {
            logger.error(e, e);
        }
    }

    private void moveInputFileToBackup(PMDate date) {
        String downloadedFilePath = BhavFileUtil.getFandOFilePath(date.getJavaDate());
        BhavFileUtil.moveFileToBackup(new PMDate(date.getJavaDate()), downloadedFilePath);
    }

    void save(List<FOQuote> quotes) {
        DAOManager.fandoDAO().save(quotes);
    }

    Reader reader(PMDate date) throws IOException {
        return BhavFileUtil.getFandOFile(date.getJavaDate());
    }

    List<FOQuote> parseFile(PMDate date, CSVReader csvReader) throws IOException {
        List<FOQuote> quotes = new ArrayList<FOQuote>();
        String[] columns = csvReader.readNext();
        verifyHeaders(columns);
        while ((columns = csvReader.readNext()) != null) {
            FOQuote quote = parse(columns);
            if (!quote.getDate().equals(date)) {
                throw new RuntimeException("Bhav file seems to be corrupted for " + date);
            }
            quotes.add(quote);
        }
        return quotes;
    }

    private void verifyHeaders(String[] headersInFile) {
        String headersExpected[] = {"INSTRUMENT", "SYMBOL", "EXPIRY_DT", "STRIKE_PR", "OPTION", "OPEN", "HIGH", "LOW", "CLOSE", "SETTLE_PR", "CONTRACTS", "VAL_INLAKH", "OPEN_INT", "CHG_IN_OI", "TIMESTAMP"};
        for (int i = 0; i < headersExpected.length; i++) {
            if (!headersInFile[i].startsWith(headersExpected[i])) {
                throw new RuntimeException("F&O Bhav file format changed");
            }
        }
    }

    FOQuote parse(String[] columns) {
        FOTYPE type = findType(columns[0], columns[4]);
        return new FOQuote(PMDateFormatter.parseDD_MMM_YYYY(columns[14]), new StockVO(columns[1]), type,
                tof(columns[5]), tof(columns[6]), tof(columns[7]), tof(columns[8]), tof(columns[10]),
                toi(columns[12]), toi(columns[13]), parseStrikePrice(type, columns[3]), PMDateFormatter.parseDD_Mmm_YYYY(columns[2]));
    }

    private Float parseStrikePrice(FOTYPE type, String value) {
        return type == Future ? 0f : tof(value);
    }

    private Integer toi(String value) {
        return Integer.parseInt(value);
    }

    private float tof(String value) {
        return Float.parseFloat(value);
    }

    FOTYPE findType(String foTypeKey, String optionKey) {
        return (foTypeKey.startsWith("FUT") ? Future : optionKey.startsWith("P") ? FOTYPE.Put : FOTYPE.Call);
    }

    public void processFiles() {
        PMDate latestQuoteDate = latestQuoteDate();
        PMDate startDate = latestQuoteDate == null ? PMDate.START_DATE : latestQuoteDate.next();
        DateIterator dateIterator = new DateIterator(startDate, new PMDate());
        while (dateIterator.hasNext()) {
            processFile(dateIterator.next());
        }
    }

    PMDate latestQuoteDate() {
        return DAOManager.fandoDAO().latestQuoteDate();
    }
}
