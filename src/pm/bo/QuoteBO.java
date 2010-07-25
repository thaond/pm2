package pm.bo;

import org.apache.log4j.Logger;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IDateDAO;
import pm.dao.ibatis.dao.IQuoteDAO;
import pm.util.DateIterator;
import pm.util.PMDate;
import pm.vo.EquityQuote;
import pm.vo.StockVO;

import java.util.*;

/**
 * Date: Aug 9, 2006
 * Time: 8:58:09 PM
 */
public class QuoteBO {

    private Logger logger = Logger.getLogger(QuoteBO.class);

    public void saveNseQuotes(PMDate date, List<EquityQuote> quoteVOs) {
        if (insertIfNewDate(date) || isNewQuote(date)) {
            insertNseQuotes(date, quoteVOs);
        } else {
            getDAO().updateQuotes(quoteVOs);
        }
    }

    boolean insertIfNewDate(PMDate date) {
        return getDateDAO().insertIfNew(date);
    }

    boolean isNewQuote(PMDate date) {
        return !getDateDAO().getNSEQuoteStatusFor(date);
    }

    private void insertNseQuotes(PMDate date, List<EquityQuote> quoteVOs) {
        insertNewStockList(quoteVOs);
        IQuoteDAO quoteDAO = getDAO();
        quoteDAO.insertQuotes(quoteVOs);
        getDateDAO().setNSEQuoteStatusFor(date);
    }

    public void saveIndexQuotes(String stockCode, List<EquityQuote> quoteVOs) {
        List<EquityQuote> existingQuotes = getDAO().getQuotes(stockCode);
        Map<PMDate, EquityQuote> existingQuotesMap = new HashMap<PMDate, EquityQuote>();
        for (EquityQuote quoteVO : existingQuotes) {
            existingQuotesMap.put(quoteVO.getDate(), quoteVO);
        }
        DateIterator dateIterator = getDateIterator();
        for (EquityQuote quoteVO : quoteVOs) {
            updatePrevClose(quoteVO, dateIterator);
            if (insertIfNewDate(quoteVO.getDate()) || existingQuotesMap.get(quoteVO.getDate()) == null) {
                getDAO().insertQuote(quoteVO);
            } else {
                getDAO().updateQuote(quoteVO);
            }
        }
    }

    DateIterator getDateIterator() {
        return new DateIterator();
    }

    void updatePrevClose(EquityQuote quoteVO, DateIterator dateIterator) {
        PMDate nextDate = null;
        PMDate prevDate;
        if (dateIterator.movePtrToDate(quoteVO.getDate())) {
            prevDate = dateIterator.previous();
            nextDate = dateIterator.next().next();
        } else {
            if (!dateIterator.hasNext()) {
                return;
            }
            prevDate = dateIterator.lastElement();
        }

        if (quoteVO.getPrevClose() == 0.0f && prevDate != null) {
            EquityQuote prevQuote = getDAO().getQuote(quoteVO.getStockCode(), prevDate);
            if (prevQuote != null) {
                quoteVO.setPrevClose(prevQuote.getClose());
            }
        }

        if (nextDate != null) {
            EquityQuote nextQuote = getDAO().getQuote(quoteVO.getStockCode(), nextDate);
            if (nextQuote != null && nextQuote.getPrevClose() != quoteVO.getClose()) {
                nextQuote.setPrevClose(quoteVO.getClose());
                getDAO().updateQuote(nextQuote);
            }
        }
    }

    void insertNewStockList(List<EquityQuote> quoteVOs) {
        HashSet<String> stockList = new HashSet<String>();
        for (EquityQuote quoteVO : quoteVOs) {
            stockList.add(quoteVO.getStockCode());
        }
        getStockMasterBO().insertMissingStockCodes(stockList);
    }

    StockMasterBO getStockMasterBO() {
        return new StockMasterBO();
    }

    IQuoteDAO getDAO() {
        return DAOManager.getQuoteDAO();
    }

    protected boolean isNewDate(PMDate date) {
        return getDateDAO().getDate(date.getIntVal()) == null;
    }

    IDateDAO getDateDAO() {
        return DAOManager.getDateDAO();
    }

    public EquityQuote[] getQuote(String[] stockCodes) {
        EquityQuote[] quoteVOs = new EquityQuote[stockCodes.length];
        IQuoteDAO dao = getDAO();
        for (int i = 0; i < stockCodes.length; i++) {
            String stockCode = stockCodes[i];
            quoteVOs[i] = dao.getQuote(stockCode);
            if (quoteVOs[i] == null) {
                quoteVOs[i] = new EquityQuote(stockCode);
            }
        }
        return quoteVOs;
    }

    public Vector<EquityQuote[]> getQuotes(PMDate frmDate, PMDate toDate, String[] stockList) {
        Vector<EquityQuote[]> quoteVOsList = new Vector<EquityQuote[]>();
        for (String stockCode : stockList) {
            EquityQuote[] quoteVOs = getQuotes(stockCode, frmDate, toDate);
            quoteVOsList.add(quoteVOs);
        }
        return quoteVOsList;
    }

    public EquityQuote[] getQuotes(String stockCode, PMDate frmDate, PMDate toDate) {
        IQuoteDAO dao = getDAO();
        List<EquityQuote> quotes = dao.getQuotes(stockCode, frmDate, toDate);
        return quotes.toArray(new EquityQuote[quotes.size()]);
    }

    public Map<StockVO, List<EquityQuote>> getQuotes(PMDate stDate, PMDate enDate) {
        return getDAO().quotes(stDate, enDate);
    }
}
