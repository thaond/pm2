package pm.bo;

import org.apache.log4j.Logger;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IDateDAO;
import pm.dao.ibatis.dao.IQuoteDAO;
import pm.util.DateIterator;
import pm.util.PMDate;
import pm.vo.QuoteVO;
import pm.vo.StockVO;

import java.util.*;

/**
 * Date: Aug 9, 2006
 * Time: 8:58:09 PM
 */
public class QuoteBO {

    private Logger logger = Logger.getLogger(QuoteBO.class);

    public void saveNseQuotes(PMDate date, List<QuoteVO> quoteVOs) {
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

    private void insertNseQuotes(PMDate date, List<QuoteVO> quoteVOs) {
        insertNewStockList(quoteVOs);
        IQuoteDAO quoteDAO = getDAO();
        quoteDAO.insertQuotes(quoteVOs);
        getDateDAO().setNSEQuoteStatusFor(date);
    }

    public void saveIndexQuotes(String stockCode, List<QuoteVO> quoteVOs) {
        List<QuoteVO> existingQuotes = getDAO().getQuotes(stockCode);
        Map<PMDate, QuoteVO> existingQuotesMap = new HashMap<PMDate, QuoteVO>();
        for (QuoteVO quoteVO : existingQuotes) {
            existingQuotesMap.put(quoteVO.getDate(), quoteVO);
        }
        DateIterator dateIterator = getDateIterator();
        for (QuoteVO quoteVO : quoteVOs) {
            updatePrevClose(quoteVO, existingQuotesMap, dateIterator);
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

    void updatePrevClose(QuoteVO quoteVO, Map<PMDate, QuoteVO> existingQuotesMap, DateIterator dateIterator) {
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
            QuoteVO prevQuote = existingQuotesMap.get(prevDate);
            if (prevQuote != null) {
                quoteVO.setPrevClose(prevQuote.getClose());
            }
        }

        if (nextDate != null) {
            QuoteVO nextQuote = existingQuotesMap.get(nextDate);
            if (nextQuote != null && nextQuote.getPrevClose() != quoteVO.getClose()) {
                nextQuote.setPrevClose(quoteVO.getClose());
                getDAO().updateQuote(nextQuote);
            }
        }
    }

    void insertNewStockList(List<QuoteVO> quoteVOs) {
        HashSet<String> stockList = new HashSet<String>();
        for (QuoteVO quoteVO : quoteVOs) {
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

    public QuoteVO[] getQuote(String[] stockCodes) {
        QuoteVO[] quoteVOs = new QuoteVO[stockCodes.length];
        IQuoteDAO dao = getDAO();
        for (int i = 0; i < stockCodes.length; i++) {
            String stockCode = stockCodes[i];
            quoteVOs[i] = dao.getQuote(stockCode);
            if (quoteVOs[i] == null) {
                quoteVOs[i] = new QuoteVO(stockCode);
            }
        }
        return quoteVOs;
    }

    public Vector<QuoteVO[]> getQuotes(PMDate frmDate, PMDate toDate, String[] stockList) {
        Vector<QuoteVO[]> quoteVOsList = new Vector<QuoteVO[]>();
        for (String stockCode : stockList) {
            QuoteVO[] quoteVOs = getQuotes(stockCode, frmDate, toDate);
            quoteVOsList.add(quoteVOs);
        }
        return quoteVOsList;
    }

    public QuoteVO[] getQuotes(String stockCode, PMDate frmDate, PMDate toDate) {
        IQuoteDAO dao = getDAO();
        List<QuoteVO> quotes = dao.getQuotes(stockCode, frmDate, toDate);
        return quotes.toArray(new QuoteVO[quotes.size()]);
    }

    public Map<StockVO, List<QuoteVO>> getQuotes(PMDate stDate, PMDate enDate) {
        return getDAO().quotes(stDate, enDate);
    }
}
