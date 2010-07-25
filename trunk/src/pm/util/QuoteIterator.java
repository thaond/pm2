package pm.util;

import pm.bo.QuoteBO;
import pm.vo.EquityQuote;
import pm.vo.StockVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class QuoteIterator {

    private String stockCode;
    protected EquityQuote[] quoteVOs = null;
    protected int index = 0;
    protected int mark = -1;

    public QuoteIterator(PMDate stDate, String stockCode) {
        init(stDate, null, stockCode);
    }

    public QuoteIterator(String stockCode) {
        init(new PMDate(1, 1, 1995), null, stockCode);
    }

    public QuoteIterator(PMDate stDate, PMDate enDate, String stockCode) {
        init(stDate, enDate, stockCode);
    }

    private QuoteIterator(StockVO stockVO, List<EquityQuote> quotes) {
        stockCode = stockVO.getStockCode();
        quoteVOs = quotes.toArray(new EquityQuote[0]);
    }

    protected void init(PMDate stDate, PMDate enDate, String stockCode) {
        this.stockCode = stockCode;
        if (stockCode != null)
            quoteVOs = new QuoteBO().getQuotes(stockCode, stDate, enDate);
        else
            quoteVOs = new EquityQuote[0];
    }

    public String getStockCode() {
        return stockCode;
    }

    public boolean hasNext() {
        return index < quoteVOs.length;
    }

    public EquityQuote next() {
        if (hasNext()) return quoteVOs[index++];
        else return null;
    }

    public boolean hasPrevious() {
        return (index > 0);
    }

    public EquityQuote previous() {
        if (hasPrevious()) return quoteVOs[--index];
        else return null;
    }

    public EquityQuote last() {
        if (quoteVOs.length > 0) return quoteVOs[quoteVOs.length - 1];
        return null;
    }

    /**
     * This method is used to mark the position for
     * later reset.
     */
    public void mark() {
        mark = index;
    }

    /**
     * This method resets to previous marked position
     */
    public void reset() {
        if (mark != -1) {
            index = mark;
            mark = -1;
        }
    }

    /**
     * Moves the current pointer by the specified no of position
     *
     * @param diff
     * @return
     */
    public boolean movePtr(int diff) {
        int newPos = index + diff;
        if (newPos >= 0 && newPos < quoteVOs.length) {
            index = newPos;
            return true;
        } else return false;
    }

    /**
     * Moves the current pointer to the first available quote
     * return the no of postion moved
     *
     * @return
     */
    public int movePtrToFirst() {
        int currPos = index;
        index = 0;
        return currPos;
    }

    /**
     * Gets the 'n'th item from the current position if it exists
     * return null if it goes out of range
     * This won't change the current pointer
     *
     * @param diff
     * @return
     */
    public EquityQuote getItemFrmCurrPos(int diff) {
        int newPos = index + diff;
        if (newPos >= 0 && newPos < quoteVOs.length) {
            return quoteVOs[newPos];
        } else return null;
    }

    /**
     * Moves the pointer the specified dates quote
     * return true if move success, else false
     *
     * @param date
     * @return
     */
    public boolean movePtrToDate(PMDate date) {
        for (int i = 0; i < quoteVOs.length; i++) {
            if (quoteVOs[i].dateEquals(date)) {
                index = i;
                return true;
            } else if (quoteVOs[i].getDate().after(date)) break;
        }
        return false;
    }

    public boolean setDataRange(PMDate frmDate, PMDate toDate) {
        Vector<EquityQuote> tmpData = new Vector<EquityQuote>();
        movePtrToDate(frmDate);
        for (; hasNext();) {
            EquityQuote quoteVO = next();
            if (quoteVO.after(toDate)) break;
            tmpData.add(quoteVO);
        }
        quoteVOs = null;
        quoteVOs = new EquityQuote[tmpData.size()];
        quoteVOs = tmpData.toArray(quoteVOs);
        index = 0;
//		System.out.println(tmpData.size());
        return true;
    }

    public static List<QuoteIterator> getIterators(PMDate stDate, PMDate enDate) {
        Map<StockVO, List<EquityQuote>> quotes = new QuoteBO().getQuotes(stDate, enDate);
        List<QuoteIterator> quoteIterators = new ArrayList<QuoteIterator>();
        for (StockVO stockVO : quotes.keySet()) {
            quoteIterators.add(new QuoteIterator(stockVO, quotes.get(stockVO)));
        }
        return quoteIterators;
    }
}
