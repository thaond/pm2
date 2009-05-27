package pm.bo;

import junit.framework.TestCase;
import pm.util.AppConst;
import pm.util.PMDate;
import pm.vo.EODDetailsVO;
import pm.vo.QuoteVO;
import pm.vo.TradeVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thiyagu
 * @version $Id: ChartBOTest.java,v 1.2 2007/12/15 16:10:49 tpalanis Exp $
 * @since 02-Dec-2007
 */
public class ChartBOTest extends TestCase {

    public void testFilterOnTimePeriod_Daily() {
        ArrayList<EODDetailsVO> detailsVOs = new ArrayList<EODDetailsVO>();
        PMDate[] dates = {new PMDate(1, 1, 2007), new PMDate(2, 1, 2007), new PMDate(3, 1, 2007), new PMDate(4, 1, 2007),
                new PMDate(5, 1, 2007), new PMDate(8, 1, 2007), new PMDate(11, 1, 2007), new PMDate(17, 1, 2007),
                new PMDate(31, 1, 2007), new PMDate(2, 2, 2007)};
        for (int i = 0; i < dates.length; i++) {
            detailsVOs.add(new EODDetailsVO(new QuoteVO("", new PMDate(1, 1, 2007), decimal(i, 6f), decimal(i, 15f),
                    decimal(i, 5f), decimal(i, 10f), decimal(i, 100f), 0f, decimal(i, 200f), decimal(i, 60f))));
        }

        List<EODDetailsVO> filteredDetailsVO =
                new ChartBO().filterOnTimePeriod(detailsVOs, AppConst.TIMEPERIOD.Daily);

        assertEquals(detailsVOs.size(), filteredDetailsVO.size());

        for (int i = 0; i < detailsVOs.size(); i++) {
            assertEquals(detailsVOs.get(i), filteredDetailsVO.get(i));
        }
    }

    public void testFilterOnTimePeriod_Weekly() {

        ArrayList<EODDetailsVO> detailsVOs = new ArrayList<EODDetailsVO>();
        PMDate[] dates = {new PMDate(1, 1, 2007), new PMDate(2, 1, 2007), new PMDate(3, 1, 2007), new PMDate(4, 1, 2007),
                new PMDate(5, 1, 2007), new PMDate(8, 1, 2007), new PMDate(11, 1, 2007), new PMDate(17, 1, 2007),
                new PMDate(31, 1, 2007), new PMDate(2, 2, 2007)};
        TradeVO trade1 = new TradeVO("", dates[0], 10, 12, 0, "", "");
        trade1.setSaleDate(dates[8]);
        TradeVO buy2 = new TradeVO("", dates[0], 20, 12, 0, "", "");
        TradeVO buy3 = new TradeVO("", dates[1], 10, 12, 0, "", "");
        TradeVO buy4 = new TradeVO("", dates[4], 10, 12, 0, "", "");
        TradeVO buy5 = new TradeVO("", dates[5], 10, 12, 0, "", "");
        TradeVO buy6 = new TradeVO("", dates[8], 10, 12, 0, "", "");
        TradeVO sell2 = new TradeVO("", dates[0], 10, 12, 0, "", "");
        sell2.setSaleDate(dates[3]);

        for (int i = 0; i < dates.length; i++) {
            float volume = decimal(i, 100f);
            detailsVOs.add(new EODDetailsVO(new QuoteVO("", dates[i], decimal(i, 6f), decimal(i, 15f),
                    decimal(i, 5f), decimal(i, 10f), volume, decimal(i, 7f), decimal(i, 200f), decimal(i, 60f))));
        }

        detailsVOs.get(0).addBuyTradeVO(trade1);
        detailsVOs.get(0).addBuyTradeVO(buy2);
        detailsVOs.get(1).addBuyTradeVO(buy3);
        detailsVOs.get(4).addBuyTradeVO(buy4);
        detailsVOs.get(5).addBuyTradeVO(buy5);
        detailsVOs.get(8).addBuyTradeVO(buy6);
        detailsVOs.get(0).addSellTradeVO(sell2);
        detailsVOs.get(8).addSellTradeVO(trade1);

        List<EODDetailsVO> filteredDetailsVO =
                new ChartBO().filterOnTimePeriod(detailsVOs, AppConst.TIMEPERIOD.Weekly);

        assertEquals(4, filteredDetailsVO.size());

        EODDetailsVO firstWeek = new EODDetailsVO(new QuoteVO("", dates[0], 6f, 15.4f, 5f, 10.4f, 501f, 7f, 1001f, 60.2f));
        firstWeek.addBuyTradeVO(trade1);
        firstWeek.addBuyTradeVO(buy2);
        firstWeek.addBuyTradeVO(buy3);
        firstWeek.addBuyTradeVO(buy4);
        firstWeek.addSellTradeVO(sell2);
        assertEquals(firstWeek, filteredDetailsVO.get(0));

        EODDetailsVO secondWeek = new EODDetailsVO(new QuoteVO("", dates[5], 6.5f, 15.6f, 5.5f, 10.6f, 201.1f, 7.5f, 401.1f, 60.55f));
        secondWeek.addBuyTradeVO(buy5);
        assertEquals(secondWeek, filteredDetailsVO.get(1));

        EODDetailsVO thirdWeek = new EODDetailsVO(new QuoteVO("", dates[7], 6.7f, 15.7f, 5.7f, 10.7f, 100.7f, 7.7f, 200.7f, 60.7f));
        assertEquals(thirdWeek, filteredDetailsVO.get(2));

        EODDetailsVO fourthWeek = new EODDetailsVO(new QuoteVO("", dates[8], 6.8f, 15.9f, 5.8f, 10.9f, 201.7f, 7.8f, 401.7f, 60.85f));
        fourthWeek.addBuyTradeVO(buy6);
        fourthWeek.addSellTradeVO(trade1);
        assertEquals(fourthWeek, filteredDetailsVO.get(3));

    }

    private float decimal(int i, float baseValue) {
        return baseValue + (float) i / 10f;
    }
}
