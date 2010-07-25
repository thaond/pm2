package pm.dao.ibatis.dao;

import org.apache.commons.lang.builder.EqualsBuilder;
import pm.util.PMDate;
import pm.util.enumlist.FOTYPE;
import pm.vo.FOQuote;
import pm.vo.StockVO;

import java.util.ArrayList;
import java.util.List;

import static pm.util.enumlist.FOTYPE.*;

public class FandODAOTest extends PMDBTestCase {

    public FandODAOTest(String string) {
        super(string, "TestData.xml");
    }

    public void testLatestQuoteDate() throws Exception {
        IFandODAO dao = DAOManager.fandoDAO();
        assertEquals(new PMDate(2, 1, 2006), dao.latestQuoteDate());
    }

    public void testSaveQuotesAndGetQuotes() throws Exception {
        IFandODAO dao = DAOManager.fandoDAO();
        List<FOQuote> quotes = new ArrayList<FOQuote>();
        StockVO stockVO = new StockVO("CODE1");
        PMDate date = new PMDate(8, 1, 2006);
        quotes.add(new FOQuote(date, stockVO, Future, 5f, 6f, 4f, 5.5f, 120, 240, 12, 0f, new PMDate(25, 1, 2006)));
        quotes.add(new FOQuote(date, stockVO, Future, 5f, 6f, 4f, 5.6f, 120, 240, 12, 0f, new PMDate(25, 2, 2006)));
        quotes.add(new FOQuote(date, stockVO, Put, 5f, 6f, 4f, 5.6f, 120, 240, 12, 20f, new PMDate(25, 1, 2006)));
        quotes.add(new FOQuote(date, stockVO, Call, 5f, 6f, 4f, 5.6f, 120, 240, 12, 20f, new PMDate(25, 1, 2006)));
        dao.save(quotes);
        List<FOQuote> savedQuotes = dao.getQuotes(date);
        assertEquals(quotes.size(), savedQuotes.size());
        for (FOQuote quote : quotes) {
            FOQuote savedQuote = find(savedQuotes, quote.getExpiryDate(), quote.getFotype());
            assertEquals(stockVO.getStockCode(), savedQuote.getStockVO().getStockCode());
            assertTrue(EqualsBuilder.reflectionEquals(quote, savedQuote, new String[]{"stockVO"}));
        }
    }

    private FOQuote find(List<FOQuote> quotes, PMDate expiryDate, FOTYPE fotype) {
        for (FOQuote quote : quotes) {
            if (quote.getExpiryDate().equals(expiryDate) && quote.getFotype() == fotype) {
                return quote;
            }
        }
        return null;
    }

}
