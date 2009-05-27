package pm.net.nse.downloader;

import org.htmlparser.util.ParserException;
import pm.dao.ibatis.dao.DAOManager;
import pm.net.HTTPHelper;
import pm.net.nse.AbstractStockDownloadManager;
import pm.vo.CorpResultVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class CorpResultDownloader extends AbstractHTMLDownloader {

    String url;
    CorpResultVO resultVO;

    public CorpResultDownloader(AbstractStockDownloadManager manager, String url, CorpResultVO resultVO) {
        super(manager);
        this.url = url;
        this.resultVO = resultVO;
    }

    @Override
    protected void performTask() {
        if (logger.isInfoEnabled())
            logger.info("Downloading corp result : " + url);
        Reader reader;
        try {
            reader = getDataReader();
            if (reader == null) {
                error = true;
                return;
            }
            parseData(reader);
            if (!error) storeData();
        } catch (ParserException e) {
            error = true;
            logger.error(e, e);
        }
    }

    Reader getDataReader() throws ParserException {
        return new HTTPHelper().getHTMLContentReader(this.getURL());
    }

    void storeData() {
        DAOManager.companyResultDAO().save(resultVO);
    }

    //TODO this requires a relook, currently processing heading "Audited, Cumulative, Consolidated"
    // but it should be skipped, it should start with NetSales..

    void parseData(Reader reader) {
        BufferedReader br = new BufferedReader(reader);
        String line;
        try {
            boolean resultTypeStart = false;
            boolean financialResultStart = false;
            int lineCount = 0;
            boolean stopFlag = false;
            String key = null;
            String value = null;
            boolean keyFlag = true;
            while ((line = br.readLine()) != null && !stopFlag) {
                line = line.trim();
                lineCount++;
                if (line.equals("Result Type")) {
                    resultTypeStart = true;
                    lineCount = 0;
                } else if (line.startsWith("Segment Reporting")) {
                    stopFlag = true;
                } else if (line.startsWith("Financial Results (Rs.lakhs)")) {
                    financialResultStart = true;
                    resultTypeStart = false;
                    lineCount = 0;
                    keyFlag = true;
                } else if (line.startsWith("Back to all")) {
                    stopFlag = true;
                }
                if (lineCount == 0)
                    continue;

                if (keyFlag)
                    key = line;
                else
                    value = line;
                keyFlag = !keyFlag;

                if (financialResultStart) {
                    addFinancialDetails(key, value);
                }
                if (resultTypeStart && lineCount == 2) { // process Company type
                    resultVO.setBankingFlag(line.equals("Banking"));
                }
            }
        } catch (IOException e) {
            logger.error(e, e);
            error = true;
        }
    }

    private void addFinancialDetails(String key, String value) {
        float data = 0f;
        try {
            data = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            if (logger.isInfoEnabled())
                logger.info("Invalid financial value : " + value + " , Key : " + key + ", Url : " + url);
            return;
        }
        if (key.startsWith("Earnings Per Share")) {
            resultVO.setEps(data);
        } else if (key.startsWith("Diluted EPS")) {
            resultVO.setEps(data);
        } else if (key.startsWith("Paid-up Equity")) {
            resultVO.setPaidUpEquityShareCapital(data);
        } else if (key.startsWith("Net Profit")) {
            resultVO.setNetProfit(data);
        } else if (key.startsWith("Non Recurring Income")) {
            resultVO.setNonRecurringIncome(data);
        } else if (key.startsWith("Non Recurring Expenses")) {
            resultVO.setNonRecurringExpense(data);
        } else if (key.startsWith("Adjusted Net Profit")) {
            resultVO.setAdjustedNetProfit(data);
        } else if (key.startsWith("Interest")) {
            resultVO.setInterestCost(data);
        } else if (key.startsWith("Face Value")) {
            resultVO.setFaceValue(data);
        } else if (key.startsWith("Net Sales")) {
            resultVO.setNetSales(data);
        }
    }

    @Override
    public String getURL() {
        return url;
    }

    public String getStockCode() {
        return resultVO.getStockCode();
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CorpResultDownloader other = (CorpResultDownloader) obj;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }


}
