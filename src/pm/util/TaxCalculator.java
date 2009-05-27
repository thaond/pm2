package pm.util;

import pm.bo.PortfolioBO;
import pm.util.AppConst.TRADINGTYPE;
import pm.vo.TradeVO;
import pm.vo.TransactionVO;

import java.util.Hashtable;
import java.util.Vector;

public class TaxCalculator {

    static final String _PORTFOLIONAME = "All";
    float divident = 0f;

    public static void main(String[] str) {

        // BrokerageVO brokerageVO = new
        // TaxCalculator().getCompleteBrokerageDetails(new PMDate(1,4,2005),
        // new PMDate(31,3,2006), "ICICI_Direct");

        TaxCalculator taxCalculator = new TaxCalculator();
        PMDate stDate = new PMDate(1, 4, 2005);
        PMDate enDate = new PMDate(
                31, 3, 2006);
        Vector<TradeVO> tradeVOs = taxCalculator
                .getDeliveryBasedTradeVOs(stDate, enDate, "ICICI_Direct");
        PLVO plvoDelivery = taxCalculator.calculatePL(tradeVOs, false, stDate, enDate);
        tradeVOs = taxCalculator.getDayTradeVOs(new PMDate(1, 4, 2005),
                new PMDate(31, 3, 2006), "ICICI_Direct");
        PLVO plvoDay = taxCalculator.calculatePL(tradeVOs, true, stDate, enDate);
        System.out.println("Profit : " + (plvoDelivery.getPl() + plvoDay.getPl()));
        System.out.println("STT : " + (plvoDelivery.getStt() + plvoDay.getStt()));
        System.out.println("Divident : " + taxCalculator.divident);

    }

    Vector<TradeVO> getDayTradeVOs(PMDate stDate, PMDate enDate,
                                   String tradingAcName) {
        Vector<TradeVO> tradeVOs = new PortfolioBO()
                .getDayTradeTransactionDetails(tradingAcName, _PORTFOLIONAME);
        Vector<TradeVO> retVal = new Vector<TradeVO>();
        for (TradeVO tradeVO : tradeVOs) {
            if (tradeVO.getSaleDate().before(stDate)
                    || tradeVO.getSaleDate().after(enDate))
                continue;
            else {
                retVal.add(tradeVO);
            }
        }
        return retVal;
    }

    PLVO calculatePL(Vector<TradeVO> tradeVOs, boolean dayTrade, PMDate stDate, PMDate enDate) {
        PLVO plvo = new PLVO();
        for (TradeVO tradeVO : tradeVOs) {
            float stt = dayTrade ? calculateSTTDayTradeBased(tradeVO)
                    : calculateSTTDeliveryBased(tradeVO, stDate, enDate);
            float pl = 0f;
            if (!tradeVO.isHolding() && !tradeVO.getSaleDate().before(stDate) && !tradeVO.getSaleDate().after(enDate)) {
                pl = tradeVO.getProfitLoss() + stt;
            }
            plvo.addPl(pl);
            plvo.addStt(stt);
            divident += tradeVO.getDivident();
            print(tradeVO, pl, stt, stDate, enDate);
        }
        return plvo;
    }

    static String _DELIMITOR = ",";

    void print(TradeVO tradeVO, float pl, float stt, PMDate stDate, PMDate enDate) {
        StringBuffer sb = new StringBuffer();
        sb.append(PMDateFormatter.displayFormat(tradeVO.getPurchaseDate())).append(_DELIMITOR);
        sb.append(tradeVO.getStockCode()).append(_DELIMITOR);
        sb.append(tradeVO.getQty()).append(_DELIMITOR);
        sb.append(tradeVO.getPurchasePrice()).append(_DELIMITOR);
        if (tradeVO.isHolding() || tradeVO.getSaleDate().after(enDate)) {
            sb.append(_DELIMITOR).append(_DELIMITOR).append(_DELIMITOR);
        } else {
            sb.append(PMDateFormatter.displayFormat(tradeVO.getSaleDate())).append(_DELIMITOR);
            sb.append(tradeVO.getSalePrice()).append(_DELIMITOR);
            sb.append(tradeVO.getBrokerage() - stt).append(_DELIMITOR);
        }
        sb.append(stt).append(_DELIMITOR);
        sb.append(pl).append(_DELIMITOR);
        sb.append(tradeVO.getDivident()).append(_DELIMITOR);
        sb.append(tradeVO.getPortfolio());
        System.out.println(sb.toString());
    }

    float calculateSTTDayTradeBased(TradeVO tradeVO) {
        float sttRate = sttRateForDay(tradeVO.getPurchaseDate());
        float sttSellLeg = tradeVO.getQty() * tradeVO.getSalePrice() * sttRate;
        return Helper.getRoundedOffValue(sttSellLeg, 2);
    }

    private float sttRateForDay(PMDate date) {
        return date.before(new PMDate(1, 6, 2005)) ? 0.00015f
                : 0.0002f;
    }

    float calculateSTTDeliveryBased(TradeVO tradeVO, PMDate stDate, PMDate enDate) {

        float sttRate = sttRateForDelivery(tradeVO.getPurchaseDate());
        float sttBuyLeg = 0.0f;
        if (!tradeVO.getPurchaseDate().before(stDate))
            sttBuyLeg = tradeVO.getQty() * tradeVO.getPurchasePrice() * sttRate;
        float sttSellLeg = 0f;
        if (!tradeVO.isHolding() && !tradeVO.getSaleDate().after(enDate)) {
            sttRate = sttRateForDelivery(tradeVO.getSaleDate());
            sttSellLeg = tradeVO.getQty() * tradeVO.getSalePrice() * sttRate;
        }
        return Helper.getRoundedOffValue(sttBuyLeg + sttSellLeg, 2);
    }

    private float sttRateForDelivery(PMDate date) {
        return date.before(new PMDate(1, 6, 2005)) ? 0.00075f : 0.001f;
    }

    private Vector<TradeVO> getDeliveryBasedTradeVOs(PMDate stDate,
                                                     PMDate enDate, String tradingAcName) {
        Hashtable<String, Vector<TradeVO>> transactionDetails = new PortfolioBO()
                .getStockwiseTradeDetails(tradingAcName, _PORTFOLIONAME, false);
        Vector<TradeVO> retVal = new Vector<TradeVO>();
        for (Vector<TradeVO> tradeVOs : transactionDetails.values()) {
            for (TradeVO tradeVO : tradeVOs) {
                if (tradeVO.getPurchaseDate().after(enDate) ||
                        (!tradeVO.isHolding() && tradeVO.getSaleDate().before(stDate)))
                    //||	(!tradeVO.isHolding() && tradeVO.getSaleDate().after(enDate)))
                    continue;
                else {
                    retVal.add(tradeVO);
                }
            }
        }
        return retVal;
    }

    boolean moreThan1Year(TradeVO tradeVO) {
        boolean moreThan1Year = tradeVO.getPurchaseDate().before(tradeVO.getSaleDate().getDateAddingDays(-365));
        if (moreThan1Year) {
            System.out.println(tradeVO);
        }
        return moreThan1Year;
    }

    private BrokerageVO getCompleteBrokerageDetails(PMDate stDate,
                                                    PMDate enDate, String tradingAcName) {
        Vector<TransactionVO> transactionLogs = BusinessLogger
                .getTransactionLogs();
        BrokerageVO totalBrokerageVO = new BrokerageVO(0, 0, 0);
        float tot = 0f;
        float totValue = 0f;
        for (TransactionVO transactionVO : transactionLogs) {
            if (transactionVO.getAction() == TRADINGTYPE.IPO
                    || transactionVO.getDate().before(stDate)
                    || transactionVO.getDate().after(enDate)
                    || !transactionVO.getTradingAc().equals(tradingAcName))
                continue;

            BrokerageVO brokerage = calculateBrokerage(transactionVO);
            System.out
                    .println(PMDateFormatter.formatYYYYMMDD(transactionVO
                            .getDate())
                            + " "
                            + transactionVO.getStockCode()
                            + " "
                            + transactionVO.getAction()
                            + " "
                            + transactionVO.getQty()
                            + " "
                            + transactionVO.getPrice()
                            + " "
                            + transactionVO.getBrokerage()
                            + " "
                            + brokerage.getTotal());
            tot += transactionVO.getBrokerage();
            totalBrokerageVO.setBrokerage(totalBrokerageVO.getBrokerage()
                    + brokerage.getBrokerage());
            totalBrokerageVO.setServiceTax(totalBrokerageVO.getServiceTax()
                    + brokerage.getServiceTax());
            totalBrokerageVO.setStt(totalBrokerageVO.getStt()
                    + brokerage.getStt());
            float val = transactionVO.getPrice() * transactionVO.getQty();
            if (transactionVO.getAction() == TRADINGTYPE.Buy) {
                totValue += val;
            } else {
                totValue -= val;
            }
        }
        System.out.println("Tot : " + tot);
        System.out.println("TotValue : " + totValue);
        return totalBrokerageVO;
    }

    public BrokerageVO calculateBrokerage(TransactionVO transactionVO) {
        if (transactionVO.getAction() == TRADINGTYPE.IPO)
            return null;
        float brokerage = getBrokerage(transactionVO);
        if (transactionVO.isDayTrading() && brokerage < 15)
            brokerage = 15f;
        if (!transactionVO.isDayTrading() && brokerage < 25)
            brokerage = 25f;
        brokerage = checkForDiscountFromICICI(transactionVO, brokerage);
        float serviceTax = Helper.getRoundedOffValue(brokerage * 0.102f, 2);
        float value = transactionVO.getQty() * transactionVO.getPrice();
        float sttRate = getSTTRate(transactionVO);
        float stt = Helper.getRoundedOffValue(value * sttRate, 2);
        return new BrokerageVO(brokerage, stt, serviceTax);
    }

    private float checkForDiscountFromICICI(TransactionVO transactionVO,
                                            float brokerage) {
        if (transactionVO.getDate().after(new PMDate(1, 1, 2006))
                && transactionVO.getDate().before(new PMDate(6, 1, 2006)))
            return 5f;
        else
            return brokerage;
    }

    private float getSTTRate(TransactionVO transactionVO) {

        if (transactionVO.isDayTrading()) {
            if (transactionVO.getAction() == TRADINGTYPE.Buy)
                return 0f;
            else
                return transactionVO.getDate().before(new PMDate(1, 6, 2005)) ? 0.00015f
                        : 0.0002f;
        } else {
            return transactionVO.getDate().before(new PMDate(1, 6, 2005)) ? 0.00075f
                    : 0.001f;
        }
    }

    private float getBrokerage(TransactionVO transactionVO) {
        float brokerage = 0;
        float brokerageRate = transactionVO.isDayTrading() ? 0.001f : 0.0075f;
        for (int i = 0; i < transactionVO.getQty(); i++) {
            float val = transactionVO.getPrice() * brokerageRate;
            brokerage += Helper.getRoundedOffValue(val, 4);
        }
        return Helper.getRoundedOffValue(brokerage, 2);
    }

}

class PLVO {
    float pl;

    float stt;

    public void addPl(float pl) {
        this.pl += pl;
    }

    public void addStt(float stt) {
        this.stt += stt;
    }

    public float getPl() {
        return pl;
    }

    public float getStt() {
        return stt;
    }

}

class BrokerageVO {
    float brokerage;

    float stt;

    float serviceTax;

    public BrokerageVO(float brokerage, float stt, float serviceTax) {
        this.brokerage = brokerage;
        this.stt = stt;
        this.serviceTax = serviceTax;
    }

    public float getBrokerage() {
        return brokerage;
    }

    public float getServiceTax() {
        return serviceTax;
    }

    public float getStt() {
        return stt;
    }

    public float getTotal() {
        return Helper.getRoundedOffValue(brokerage + serviceTax + stt, 2);
    }

    public void setBrokerage(float brokerage) {
        this.brokerage = brokerage;
    }

    public void setServiceTax(float serviceTax) {
        this.serviceTax = serviceTax;
    }

    public void setStt(float stt) {
        this.stt = stt;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Brokerage : ").append(brokerage);
        sb.append("\nService Tax :").append(serviceTax);
        sb.append("\nST Tax :").append(stt);
        sb.append("\nTotal :").append(getTotal());
        return sb.toString();
    }

}