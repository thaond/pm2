package pm.datamining;

import pm.datamining.vo.SupportResistanceVO;
import pm.util.PMDate;
import pm.util.QuoteIterator;
import pm.vo.QuoteVO;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;

public class SupportResistanceAnalyzer {

    //private static float diff = 0.005f; //in percentage

    public static void main(String[] arg) {
        String stockCode = "INFOSYSTCH";
        getAllSupportResistanceZone(stockCode, 2, 0.5f);
    }

    public static Vector<SupportResistanceVO> getAllSupportResistanceZone(String stockCode, float diff, float weightage) {
        diff = diff / 100.0f;
        QuoteIterator dataHolder = new QuoteIterator(new PMDate(1, 12, 2003), stockCode);
        Vector<SupportResistanceVO> vector = new Vector<SupportResistanceVO>();
        for (; dataHolder.hasNext();) {
            QuoteVO quoteVO = dataHolder.next();
            dataHolder.mark();
            QuoteVO nextDayQuote = dataHolder.next();
            if (nextDayQuote == null) break;
            boolean highflag = false;
            boolean lowflag = false;
            for (SupportResistanceVO vo : vector) {
                float topRange = vo.getPrice() * (1.0f + diff);
                float bottomRange = vo.getPrice() * (1.0f - diff);
                if (quoteVO.getHigh() <= topRange && quoteVO.getHigh() >= bottomRange) {
                    if (nextDayQuote.getPerChange() > 0)
                        vo.incResistanceOccurrence(quoteVO.getHigh());
                    highflag = true;
                } else if (quoteVO.getLow() <= topRange && quoteVO.getLow() >= bottomRange) {
                    if (nextDayQuote.getPerChange() > 0)
                        vo.incSupportOccurrence(quoteVO.getLow());
                    lowflag = true;
                }
                if (highflag && lowflag) break;
            }

            if (!highflag) vector.add(new SupportResistanceVO(quoteVO.getHigh()));
            if (!lowflag) vector.add(new SupportResistanceVO(quoteVO.getLow()));
            dataHolder.reset();
        }

        TreeSet<SupportResistanceVO> set = new TreeSet<SupportResistanceVO>(new Comparator<SupportResistanceVO>() {
            public int compare(SupportResistanceVO arg0, SupportResistanceVO arg1) {
                return (int) (arg0.getPrice() - arg1.getPrice());
            }
        });

        float lastPriceHigh = dataHolder.last().getLastPrice() * 1.25f;
        float lastPriceLow = dataHolder.last().getLastPrice() * 0.75f;
        for (SupportResistanceVO vo : vector) {
            if (vo.getWeightage() >= weightage && vo.getPrice() <= lastPriceHigh &&
                    vo.getPrice() >= lastPriceLow)
                set.add(vo);
        }
        Vector<SupportResistanceVO> retVal = new Vector<SupportResistanceVO>();
        for (SupportResistanceVO resistanceVO : set) {
            retVal.add(resistanceVO);
        }
        return retVal;
    }
}
