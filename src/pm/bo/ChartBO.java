/*
 * Created on Dec 2, 2004
 *
 */
package pm.bo;

import pm.dao.ibatis.dao.DAOManager;
import pm.util.AppConst;
import pm.util.AppConst.REPORT_TYPE;
import pm.util.Helper;
import pm.util.PMDate;
import pm.vo.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class ChartBO {

    public static Vector<Vector<MovAvgVO>> getMovAvgData(String[] stockCodes, int[] days, PMDate stDate, PMDate enDate) {
        Vector<Vector<MovAvgVO>> retVal = new Vector<Vector<MovAvgVO>>(stockCodes.length);
        for (String stockCode : stockCodes) {
            retVal.add(getMovAvgData(stockCode, days, stDate, enDate, true));
        }
        return retVal;
    }

    public static Vector<MovAvgVO> getMovAvgData(String stockCode, int[] days, PMDate stDate, PMDate enDate, boolean applyCompanyAction) {
        List<EquityQuote> quoteVOs = DAOManager.getQuoteDAO().getQuotes(stockCode, null, enDate);
        if (applyCompanyAction) {
            new CompanyBO().applyCompanyAction(stockCode, quoteVOs);
        }
        return buildMovAvg(quoteVOs, days, stDate);
    }

    private static Vector<MovAvgVO> buildMovAvg(List<EquityQuote> quoteVOs, int[] daysList, PMDate stDate) {
        Vector<MovAvgVO> retVal = new Vector<MovAvgVO>();
        float[] totals = new float[daysList.length];
        for (int i = 0; i < quoteVOs.size(); i++) {
            boolean isInRange = !quoteVOs.get(i).before(stDate);
            MovAvgVO avgVO = new MovAvgVO(quoteVOs.get(i), daysList);
            for (int j = 0; j < daysList.length; j++) {
                float mvgAvg = 0;
                totals[j] += quoteVOs.get(i).getClose();
                int daysMinus1 = daysList[j] - 1;
                if (i >= (daysMinus1)) {
                    mvgAvg = totals[j] / daysList[j];
                    totals[j] -= quoteVOs.get(i - daysMinus1).getClose();
                }
                if (isInRange) {
                    avgVO.setMvgByDays(daysList[j], mvgAvg);
                }
            }
            if (isInRange) {
                retVal.add(avgVO);
            }
        }
        return retVal;
    }

    public EODChartVO getEODChartData(String stockCode, int[] days, PMDate stDate, PMDate enDate, boolean applyCompanyAction, String portfolioName, AppConst.TIMEPERIOD timePeriod) {
        List<EquityQuote> quoteVOs = DAOManager.getQuoteDAO().getQuotes(stockCode, null, enDate);
        if (applyCompanyAction) {
            new CompanyBO().applyCompanyAction(stockCode, quoteVOs);
        }
        List<EODDetailsVO> eodChartVOList = buildEODChartVOWithMovingAverage(quoteVOs, stDate);
        boolean holding = loadTransactionDetails(eodChartVOList, portfolioName, stockCode, stDate, enDate);
        new CompanyBO().loadFinanceDetails(stockCode, eodChartVOList);
        eodChartVOList = filterOnTimePeriod(eodChartVOList, timePeriod);
        StopLossVO stopLossVO = DAOManager.getPortfolioDAO().getStopLoss(portfolioName, stockCode);
        return new EODChartVO(eodChartVOList, stopLossVO, holding);

    }

    List<EODDetailsVO> filterOnTimePeriod(List<EODDetailsVO> eodChartVOList, AppConst.TIMEPERIOD timePeriod) {
        List<EODDetailsVO> filteredDetailsVOs = new ArrayList<EODDetailsVO>();
        if (timePeriod == AppConst.TIMEPERIOD.Daily) return eodChartVOList;
        int lastIndex = -1;
        int count = 0;
        for (EODDetailsVO detailsVO : eodChartVOList) {
            count++;
            int index = detailsVO.getDate().get(timePeriod);
            if (index != lastIndex) {
                makeAverage(filteredDetailsVOs, count);
                filteredDetailsVOs.add(detailsVO);
                count = 0;
            } else {
                EODDetailsVO filteredDetailsVO = filteredDetailsVOs.get(filteredDetailsVOs.size() - 1);
                EquityQuote filteredDetailsQuote = filteredDetailsVO.getQuoteVO();
                if (filteredDetailsVO.getLow() > detailsVO.getLow()) {
                    filteredDetailsQuote.setLow(detailsVO.getLow());
                }
                if (filteredDetailsVO.getHigh() < detailsVO.getHigh()) {
                    filteredDetailsQuote.setHigh(detailsVO.getHigh());
                }
                filteredDetailsQuote.setLastPrice(detailsVO.getClose());
                float volume = filteredDetailsVO.getVolume() + detailsVO.getVolume();
                filteredDetailsQuote.setVolume(Helper.getRoundedOffValue(volume, 2));
                filteredDetailsQuote.setTradeValue(filteredDetailsQuote.getTradeValue() + detailsVO.getQuoteVO().getTradeValue());
                filteredDetailsQuote.setPerDeliveryQty(filteredDetailsQuote.getPerDeliveryQty() + detailsVO.getQuoteVO().getPerDeliveryQty());
                filteredDetailsVO.addAllBuyTradeVOs(detailsVO.getBuyTradeList());
                filteredDetailsVO.addAllSellTradeVOs(detailsVO.getSellTradeList());
            }
            lastIndex = index;
        }
        if (count != 0) makeAverage(filteredDetailsVOs, count + 1);
        return filteredDetailsVOs;
    }

    private void makeAverage(List<EODDetailsVO> filteredDetailsVOs, int count) {
        if (!filteredDetailsVOs.isEmpty()) {
            EODDetailsVO filteredDetailsVO = filteredDetailsVOs.get(filteredDetailsVOs.size() - 1);
            filteredDetailsVO.getQuoteVO().setPerDeliveryQty(filteredDetailsVO.getQuoteVO().getPerDeliveryQty() / count);
        }
    }

    boolean loadTransactionDetails(List<EODDetailsVO> eodChartVOList, String portfolioName, String stockCode, PMDate stDate, PMDate enDate) {
        boolean hasHolding = false;
        List<TradeVO> transactionDetails = new PortfolioBO().getTransactionDetails(stockCode, REPORT_TYPE.All.toString(), portfolioName, false);
        Hashtable<PMDate, Vector<TradeVO>> htBuyTransDetails = new Hashtable<PMDate, Vector<TradeVO>>();
        Hashtable<PMDate, Vector<TradeVO>> htSellTransDetails = new Hashtable<PMDate, Vector<TradeVO>>();
        for (TradeVO tradeVO : transactionDetails) {
            if (!hasHolding) {
                hasHolding = tradeVO.isHolding();
            }
            Vector<TradeVO> buyList = htBuyTransDetails.remove(tradeVO.getPurchaseDate());
            if (buyList == null) {
                buyList = new Vector<TradeVO>();
            }
            buyList.add(tradeVO);
            htBuyTransDetails.put(tradeVO.getPurchaseDate(), buyList);
            if (tradeVO.getSaleDate() == null) {
                continue;
            }
            Vector<TradeVO> sellList = htSellTransDetails.remove(tradeVO.getSaleDate());
            if (sellList == null) {
                sellList = new Vector<TradeVO>();
            }
            sellList.add(tradeVO);
            htSellTransDetails.put(tradeVO.getSaleDate(), sellList);
        }

        for (EODDetailsVO chartVO : eodChartVOList) {
            Vector<TradeVO> buyList = htBuyTransDetails.get(chartVO.getDate());
            if (buyList != null) {
                chartVO.addAllBuyTradeVOs(buyList);
            }
            Vector<TradeVO> sellList = htSellTransDetails.get(chartVO.getDate());
            if (sellList != null) {
                chartVO.addAllSellTradeVOs(sellList);
            }
        }
        return hasHolding;
    }

    Vector<EODDetailsVO> buildEODChartVOWithMovingAverage(List<EquityQuote> quoteVOs, PMDate stDate) {
        int daysList[] = {10, 50, 200};
        Vector<EODDetailsVO> retVal = new Vector<EODDetailsVO>();
        float[] totals = new float[daysList.length];
        for (int i = 0; i < quoteVOs.size(); i++) {
            boolean isInRange = !quoteVOs.get(i).before(stDate);
            EODDetailsVO eodData = null;
            if (isInRange) {
                eodData = new EODDetailsVO(quoteVOs.get(i));
            }
            for (int j = 0; j < daysList.length; j++) {
                float mvgAvg = 0;
                totals[j] += quoteVOs.get(i).getClose();
                int daysMinus1 = daysList[j] - 1;
                if (i >= (daysMinus1)) {
                    mvgAvg = totals[j] / daysList[j];
                    totals[j] -= quoteVOs.get(i - daysMinus1).getClose();
                }
                if (isInRange) {
                    eodData.addMovingAverageDetail(new MovingAverageDetailVO(daysList[j], mvgAvg));
                }
            }
            if (isInRange) {
                retVal.add(eodData);
            }
        }

        return retVal;
    }
}
