/*
 * Created on Nov 22, 2004
 *
 */
package pm.bo;

import org.apache.log4j.Logger;
import pm.AppLoader;
import pm.action.QuoteManager;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IWatchlistDAO;
import pm.mail.SendMsg;
import pm.util.AppConfig;
import pm.util.Helper;
import pm.vo.*;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class AlertBO {
    private static Logger logger = Logger.getLogger(AlertBO.class);

    public void alert() {
        Vector<WatchlistVO> wlVOs = getAllWatchlist();
        Vector<StopLossVO> slVOs = getAllStopLoss();
        loadLiveQuote(wlVOs, slVOs);
        StringBuffer sb = new StringBuffer();
        sb.append(getPortfolioAlert(slVOs));
        sb.append(getWatchlistAlert(wlVOs));
        if (sb.length() > 0) {
            logger.info(sb.toString());
            String toAddr = AppConfig.toMailId.Value;
            String frmAddr = AppConfig.fromMailId.Value;
            String serverAddr = AppConfig.mailServer.Value;
            String subject = AppConfig.mailSubject.Value;
            SendMsg.send(toAddr, frmAddr, subject, sb.toString(), serverAddr, false);
        }
    }

    private Vector<WatchlistVO> getAllWatchlist() {
        IWatchlistDAO watchlistDAO = DAOManager.getWatchlistDAO();
        List<WatchlistDetailsVO> wlDetails = watchlistDAO.getWatchlistGroup();
        Vector<WatchlistVO> watchlist = new Vector<WatchlistVO>();
        for (WatchlistDetailsVO detailsVO : wlDetails) {
            if (detailsVO.isAlertEnabled()) {
                watchlist.addAll(watchlistDAO.getWatchlistVos(detailsVO.getId()));
            }
        }
        return watchlist;
    }

    private static Vector<StopLossVO> getAllStopLoss() {
        Vector<StopLossVO> stopLossList = new Vector<StopLossVO>();
        List<PortfolioDetailsVO> portfolioList = DAOManager.getAccountDAO().getPorfolioList();
        for (PortfolioDetailsVO detailsVO : portfolioList) {
            if (detailsVO.isAlertEnabled()) {
                stopLossList.addAll(new PortfolioBO().getStopLossDetails(detailsVO.getName()));
            }
        }
        return stopLossList;
    }

    /**
     * @param wlVOs
     * @param slVOs
     */
    private void loadLiveQuote(Vector<WatchlistVO> wlVOs, Vector<StopLossVO> slVOs) {
        Hashtable<String, String> htStockCodes = new Hashtable<String, String>();
        for (WatchlistVO wlVO : wlVOs) {
            htStockCodes.put(wlVO.getStockCode(), wlVO.getStockCode());
        }
        for (StopLossVO slVO : slVOs) {
            htStockCodes.put(slVO.getStockCode(), slVO.getStockCode());
        }
        String[] stockCodes = htStockCodes.values().toArray(new String[htStockCodes.size()]);
        EquityQuote[] quoteVOs = QuoteManager.getLiveQuote(stockCodes);
        Hashtable<String, EquityQuote> htQuoteVOs = new Hashtable<String, EquityQuote>(quoteVOs.length);
        for (int i = 0; i < quoteVOs.length; i++) {
            htQuoteVOs.put(stockCodes[i], quoteVOs[i]);
        }
        for (WatchlistVO wlVO : wlVOs) {
            wlVO.setCurrQuote(htQuoteVOs.get(wlVO.getStockCode()));
        }

        for (StopLossVO slVO : slVOs) {
            slVO.setQuoteVO(htQuoteVOs.get(slVO.getStockCode()));
        }

    }

    /**
     * @return
     */

    /**
     * @param slVOs
     * @return
     */
    private String getPortfolioAlert(Vector<StopLossVO> slVOs) {
        StringBuffer sb = new StringBuffer();
        for (StopLossVO slVO : slVOs) {
            boolean flag = false;
            if (slVO.getQuoteVO().getLastPrice() == 0 || slVO.getStopLoss1() == 0
                    || slVO.getStopLoss2() == 0 || slVO.getTarget1() == 0
                    || slVO.getTarget2() == 0) {
                continue;
            }
            if (slVO.getQuoteVO().getLastPrice() < slVO.getStopLoss2()) {
                sb.append(slVO.getStockCode()).append(" SL2 ");
                flag = true;
            } else if (slVO.getQuoteVO().getLastPrice() < slVO.getStopLoss1()) {
                sb.append(slVO.getStockCode()).append(" SL1 ");
                flag = true;
            } else if (slVO.getQuoteVO().getLastPrice() > slVO.getTarget2()) {
                sb.append(slVO.getStockCode()).append(" T2 ");
                flag = true;
            } else if (slVO.getQuoteVO().getLastPrice() > slVO.getTarget1()) {
                sb.append(slVO.getStockCode()).append(" T1 ");
                flag = true;
            }
            if (flag) {
                sb.append(slVO.getQuoteVO().getLastPrice()).append(" ");
                sb.append(Helper.formatFloat(slVO.getQuoteVO().getPerChange())).append("%\n");
            }
        }
        return sb.toString();
    }

    /**
     * @param wlVOs
     * @return
     */
    private String getWatchlistAlert(Vector<WatchlistVO> wlVOs) {
        StringBuffer sb = new StringBuffer();
        for (WatchlistVO wlVO : wlVOs) {
            boolean flag = false;
            if (wlVO.getCurrQuote() == null) {
                logger.info(wlVO.getDetails());
            }
            if (wlVO.getCurrQuote().getLastPrice() == 0 || wlVO.getCeil() == 0
                    || wlVO.getFloor() == 0) {
                continue; //skip alert
            }
            if (wlVO.getCurrQuote().getLastPrice() >= wlVO.getCeil()) {
                sb.append(wlVO.getStockCode()).append(" H ");
                flag = true;
            }
            if (wlVO.getCurrQuote().getLastPrice() <= wlVO.getFloor()) {
                sb.append(wlVO.getStockCode()).append(" L ");
                flag = true;
            }
            if (flag) {
                sb.append(wlVO.getCurrQuote().getLastPrice()).append(" ");
                sb.append(Helper.formatFloat(wlVO.getCurrQuote().getPerChange())).append("%\n");
            }
        }
        return sb.toString();
    }

    public static void main(String... arg) {
        AppLoader.initConsoleLogger();
        new AlertBO().alert();
    }
}
