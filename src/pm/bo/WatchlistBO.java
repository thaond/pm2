/*
 * Created on Nov 16, 2004
 *
 */
package pm.bo;

import org.apache.log4j.Logger;
import pm.action.QuoteManager;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IWatchlistDAO;
import pm.util.PMDate;
import pm.vo.EquityQuote;
import pm.vo.WatchlistDetailsVO;
import pm.vo.WatchlistPerfVO;
import pm.vo.WatchlistVO;

import java.util.*;

/**
 * @author thiyagu1
 */
public class WatchlistBO {

    private static Logger logger = Logger.getLogger(WatchlistBO.class);

    public boolean saveWatchlist(WatchlistVO[] watchlistVOs, WatchlistDetailsVO wlDetails) {
        IWatchlistDAO dao = getDAO();
        dao.updateWatchlistGroup(wlDetails);
        Vector<WatchlistVO> wlVOs = new Vector<WatchlistVO>();
        for (WatchlistVO watchlistVO : watchlistVOs) { //TODO refactor
            watchlistVO.setWatchlistGroupId(wlDetails.getId());
            wlVOs.add(watchlistVO);
        }
        dao.deleteWatchlistGroup(wlDetails.getId());
        dao.insertWatchlistVOs(wlVOs);
        return true;                  //TODO refactor
    }

    public List<WatchlistVO> getWatchlistView(int wlgID) {
        List<WatchlistVO> vWatchlist = getDAO().getWatchlistVos(wlgID);
        String[] stockCodes = new String[vWatchlist.size()];
        for (int i = 0; i < stockCodes.length; i++) {
            stockCodes[i] = vWatchlist.get(i).getStockCode();
        }
        EquityQuote[] quoteVOs = QuoteManager.getLiveQuote(stockCodes);
        for (int i = 0; i < quoteVOs.length; i++) {
            vWatchlist.get(i).setCurrQuote(quoteVOs[i]);
        }
        return vWatchlist;
    }

    public Vector<WatchlistPerfVO> getPerfReport(int days, int wlgID) throws Exception {

        PMDate[] dates = DAOManager.getDateDAO().getStEnDates(days);
        if (dates == null) {
            throw new Exception("Historic data not found");
        }

        List<WatchlistVO> vWatchlist = getDAO().getWatchlistVos(wlgID);
        Comparator comparator = new Comparator() {
            public boolean equals(Object obj) {
                return false;
            }

            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        };

        TreeSet<WatchlistVO> stkKeySet = new TreeSet<WatchlistVO>(comparator);
        stkKeySet.addAll(vWatchlist);

        Hashtable htEndData = EODQuoteLoader.getQuote(dates[0], stkKeySet);
        Hashtable htStData = EODQuoteLoader.getQuote(dates[1], stkKeySet);

        Vector<WatchlistPerfVO> retVal = new Vector<WatchlistPerfVO>(vWatchlist.size());
        for (WatchlistVO aVWatchlist : vWatchlist) {
            String ticker = aVWatchlist.getStockCode();
            EquityQuote hQuoteVO1 = (EquityQuote) htStData.get(ticker);
            EquityQuote hQuoteVO2 = (EquityQuote) htEndData.get(ticker);
            if (hQuoteVO1 != null && hQuoteVO2 != null) {
                WatchlistPerfVO perfVO = new WatchlistPerfVO(ticker, hQuoteVO1.getLastPrice(), hQuoteVO2.getLastPrice());
                retVal.add(perfVO);
            } else {
                logger.info("Not enough data for " + ticker);
            }
        }
        //System.out.println(retVal);
        return retVal;
    }

    public List<WatchlistDetailsVO> getWatchlistNames() {
        return getDAO().getWatchlistGroup();
    }

    IWatchlistDAO getDAO() {
        return DAOManager.getWatchlistDAO();
    }

    public boolean createWatchlist(WatchlistDetailsVO newWLG) {
        getDAO().insertWatchlistGroup(newWLG);
        return true; //TODO refactor
    }

    public void handleDuplicate(int latestStockId, int originalStockId) {
        IWatchlistDAO dao = getDAO();
        if (!dao.watchlistByStockId(latestStockId).isEmpty()) {
            dao.deleteWatchlistByStockId(originalStockId);
            dao.updateStockId(latestStockId, originalStockId);
        }
    }
}
