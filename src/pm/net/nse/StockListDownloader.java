package pm.net.nse;

import org.apache.log4j.Logger;
import pm.action.ILongTask;
import pm.bo.StockMasterBO;
import pm.net.HTTPHelper;
import pm.util.ApplicationException;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.util.enumlist.SERIESTYPE;
import pm.util.enumlist.TASKNAME;
import pm.vo.StockVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;
import java.util.Vector;

public class StockListDownloader implements ILongTask {

    public static String _URL = "http://www.nseindia.com/content/equities/EQUITY_L.csv";

    private static Logger logger = Logger.getLogger(StockListDownloader.class);

    boolean taskCompleted = false;

    boolean error = false;

    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    public int getProgress() {
        return 0;
    }

    public int getTaskLength() {
        return 0;
    }

    public void stop() {

    }

    public TASKNAME getTaskName() {
        return TASKNAME.STOCKLISTDOWNLOAD;
    }

    public boolean isInitComplete() {
        return true;
    }

    public void run() {
        try {
            logger.info("Stocklist download started");
            loadStockList(getReader());
            logger.info("Stocklist download Completed");
        } catch (IOException e) {
            logger.error(e, e);
            error = true;
        } catch (ApplicationException e) {
            logger.error(e, e);
            error = true;
        } finally {
            getTaskName().setLastRunDetails(new PMDate(), !error);
            taskCompleted = true;
        }
    }

    public void loadStockList(BufferedReader reader) throws IOException {
        save(buildVOList(reader));
    }

    void save(Vector<StockVO> stockList) {
        new StockMasterBO().storeStockList(stockList);
    }

    BufferedReader getReader() throws IOException {
        return new BufferedReader(new HTTPHelper().getDataWithoutExpFilter(_URL));
    }

    public boolean isIndeterminate() {
        return true;
    }

    private Vector<StockVO> buildVOList(Reader reader) throws IOException, ApplicationException {
        Vector<StockVO> stockList = new Vector<StockVO>();
        BufferedReader br = new BufferedReader(reader);
        String line;
        skipHeader(br);
        while ((line = br.readLine()) != null) {
            StockVO stockVO = buildStockVO(line);
            if (stockVO != null) {
                stockList.add(stockVO);
            }
        }
        br.close();
        return stockList;
    }

    private void skipHeader(BufferedReader br) throws IOException {
        br.readLine();
    }

    StockVO buildStockVO(String line) throws ApplicationException {
        logger.debug("Parsing : " + line);
        StringTokenizer stk = new StringTokenizer(line, ",");
        String stockCode = stk.nextToken();
        String companyName = stk.nextToken();
        String series = stk.nextToken();
        if (!series.equals("EQ")) {
            return null;
        }
        PMDate dateOfListing = PMDateFormatter.parseDD_MMM_YYYY(stk.nextToken());
        float paidUPValue = Float.parseFloat(stk.nextToken());
        short marketLot = Short.parseShort(stk.nextToken());
        String isin = stk.nextToken();
        float faceValue = Float.parseFloat(stk.nextToken());
        return new StockVO(stockCode, companyName, faceValue, SERIESTYPE.equity, paidUPValue, marketLot, isin, dateOfListing, true);
    }

}
