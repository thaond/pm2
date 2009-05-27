package pm.util.enumlist;

import pm.util.AppConst.TRADINGTYPE;
import pm.util.PMDate;

import java.math.BigDecimal;

public enum BROKERAGETYPE {
    None() {
        @Override
        public float calculateBrokerage(TRADINGTYPE tradingType, PMDate date, float qty, float price, boolean dayTrading) {
            return 0;
        }
    },
    ICICIDirect() {
        @Override
        public float calculateBrokerage(TRADINGTYPE tradingType, PMDate date, float qty, float price, boolean dayTrading) {
            float value = qty * price;
            float brokerage = brokerage(date, value, dayTrading);
            float serviceTax = serviceTax(date);
            float sTT = sTT(date, tradingType, dayTrading);
            float brokeragePlusST = brokerage + (brokerage * (serviceTax / 100f));
            brokeragePlusST = new BigDecimal(brokeragePlusST).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            float totBrokerage = brokeragePlusST + (value * (sTT / 100f));
            return new BigDecimal(totBrokerage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        }

        public float brokerage(PMDate date, float value, boolean dayTrading) {
            float totBrokerage;
            float brokeragePercentage = 0f;
            if (dayTrading) {
                if (date.before(new PMDate(1, 10, 2004))) { // Brokerage 0.0015
                    brokeragePercentage = 0.15f;
                } else if (date.before(new PMDate(1, 4, 2007))) {
                    brokeragePercentage = 0.1f;
                } else if (date.before(new PMDate(1, 4, 2008))) {  //0.00050025f
                    brokeragePercentage = 0.05f;
                } else {
                    brokeragePercentage = 0.015f;
                }
                totBrokerage = value * (brokeragePercentage / 100);
                if (totBrokerage < 15) {
                    totBrokerage = 15.0f;
                }
            } else {
                if (date.before(new PMDate(1, 10, 2004))) { // Brokerage 0.0085
                    brokeragePercentage = 0.85f;
                } else {
                    brokeragePercentage = 0.75f;
                }

                totBrokerage = value * (brokeragePercentage / 100);
                if (totBrokerage < 25) {
                    totBrokerage = 25.0f;
                }

            }
            return totBrokerage;
        }
    },
    IndiaBulls() {
        @Override
        public float calculateBrokerage(TRADINGTYPE tradingType, PMDate date, float qty, float price, boolean dayTrading) {
            float value = qty * price;
            return brokerage(dayTrading, value);
        }

        private float brokerage(boolean dayTrading, float value) {
            float totBrokerage = 0;
            if (dayTrading) {
                totBrokerage = value * 0.0021550f;
            } else {
                totBrokerage = value * 0.00525f;
            }
            return totBrokerage;
        }

    },
    //TODO Error in brokerage calculation!
    HDFC() {
        @Override
        public float calculateBrokerage(TRADINGTYPE tradingType, PMDate date, float qty, float price, boolean dayTrading) {
            float value = qty * price;
            float brokerage = brokerage(tradingType, dayTrading, value);
            float serviceTax = new BigDecimal(brokerage * (serviceTax(date) / 100f)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            float sTT = value * sTT(date, tradingType, dayTrading) / 100f;
            float sTTDiff = sTT - new BigDecimal(sTT).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            if (sTTDiff >= 0.5f) sTT += 1;
            sTT = new BigDecimal(sTT).setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
            float brokeragePlusST = brokerage + serviceTax;
            float totBrokerage = brokeragePlusST + sTT;
            return new BigDecimal(totBrokerage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        }

        private float brokerage(TRADINGTYPE tradingType, boolean dayTrading, float value) {
            float totBrokerage = 0;
            if (dayTrading) {
                totBrokerage = value * 0.0021550f;
            } else {
                totBrokerage = value * (tradingType == TRADINGTYPE.Buy ? 0.005f : 0.005f);
            }
            return totBrokerage;
        }

    };

    public abstract float calculateBrokerage(TRADINGTYPE tradingType, PMDate date, float qty, float price, boolean dayTrading);

    private static float sTT(PMDate date, TRADINGTYPE tradingType, boolean dayTrading) {
        float sTT = 0f;
        if (date.before(new PMDate(1, 10, 2004))) {
            sTT = 0f;
        } else if (date.before(new PMDate(1, 4, 2005))) {
            if (tradingType == TRADINGTYPE.Sell) sTT = 0.015f;
            if (!dayTrading) sTT = 0.075f;
        } else if (date.before(new PMDate(21, 4, 2006))) {
            if (tradingType == TRADINGTYPE.Sell) sTT = 0.02f;
            if (!dayTrading) sTT = 0.1f;
        } else if (date.before(new PMDate(1, 4, 2007))) {
            if (tradingType == TRADINGTYPE.Sell) sTT = 0.02f;
            if (!dayTrading) sTT = 0.125f;
        } else {
            if (tradingType == TRADINGTYPE.Sell) sTT = 0.025f;
            if (!dayTrading) sTT = 0.125f;
        }
        return sTT;
    }

    private static float serviceTax(PMDate date) {
        float serviceTax = 0f;

        if (date.before(new PMDate(1, 10, 2004))) {
            serviceTax = 0f;
        } else if (date.before(new PMDate(1, 4, 2005))) {
            serviceTax = 10.2f;
        } else if (date.before(new PMDate(21, 4, 2006))) {
            serviceTax = 10.2f;
        } else if (date.before(new PMDate(1, 4, 2007))) {
            serviceTax = 12.24f;
        } else {
            serviceTax = 12.36f;
        }
        return serviceTax;
    }
}