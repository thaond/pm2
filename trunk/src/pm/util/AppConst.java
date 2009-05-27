/*
 * Created on 25-Jan-2005
 *
 */
package pm.util;

import pm.analyzer.bo.*;

import java.util.Map;

/**
 * @author thiyagu1
 */
public interface AppConst {

    String COMPANY_NAME_IPO = "IPO";
    String NSE_BASE_URL = "http://www.nseindia.com/";

    public static enum TRADINGTYPE {
        Buy, Sell, IPO
    }

    public static enum COMPANY_ACTION_TYPE {
        Divident, Split, Bonus, Demerger
    }

    enum TIMEPERIOD {
        Daily, Weekly, Monthly
    }

    public static enum REPORT_TYPE {
        All, Holding
    }

    public static enum STOCK_PICK_TYPE {
        And, Or
    }

    public static enum FUND_TRANSACTION_TYPE {
        Debit {
            public FUND_TRANSACTION_REASON[] reasons() {
                return new FUND_TRANSACTION_REASON[]{FUND_TRANSACTION_REASON.WithDrawn, FUND_TRANSACTION_REASON.Expense};
            }},

        Credit {
            public FUND_TRANSACTION_REASON[] reasons() {
                return new FUND_TRANSACTION_REASON[]{FUND_TRANSACTION_REASON.Deposit, FUND_TRANSACTION_REASON.OtherIncome};
            }};

        public abstract FUND_TRANSACTION_REASON[] reasons();
    }

    public static enum FUND_TRANSACTION_REASON {
        Deposit, WithDrawn, Expense, OtherIncome, StockBuy, StockSell, IPOApply, IPORefund
    }

    public static enum CORP_RESULT_TIMELINE {
        Annual('A'), HalfYearly('H'), Quaterly('Q'), Other('O');
        private char code;

        private CORP_RESULT_TIMELINE(char code) {
            this.code = code;
        }

        public char getCode() {
            return code;
        }
    }

    public static enum ANALYZER_LIST {
        OpenHigh("OH", "OH", "", 1, new OpenHighBO()),
        CloseAbovePrevHigh("CH", "CH", "CH", 1, new CloseAbovePrevHighBO()),
        CloseAbove5DHigh("5H", "5H", "5H", 1, new CloseAbove5DHighBO()),
        CloseAbove30DHigh("30H", "30H", "30H", 1, new CloseAbove30DHighBO()),
        CloseAbove52WHigh("52WH", "52WH", "52WH", 1, new CloseAbove52WeekHighBO()),
        FlashQuote("FQ", "FQ", "FQ", 1, new FlashQuoteBO()),
        CloseBelow5DLow("5L", "5L", "5L", 1, new CloseBelow5DLowBO()),
        CloseBelow30DLow("30L", "30L", "30L", 1, new CloseBelow30DLowBO()),
        BuySell("B/S", "B", "S", 1, new BuySellBO()),
        MACD("MAb/s", "MAb", "MAs", 1, new MACDBO()),
        PositiveMove5D("PM5D", "PM5D", "PM5D", 1, new PositiveMove5DBO()),
        FlashDeliveryPosition("DP+/-", "DP+", "DP-", 1, new DeliveryPositionBO()),
        VolumeAlert("VA+/-", "VA+", "VA-", 1, new VolumeAlertBO());
        private String disp;
        private String post;
        private String neg;
        private Class<IStockAnalyzerBO> analyzerClass;
        private float weightage;
        private final IStockAnalyzerBO analyzerBO;

        ANALYZER_LIST(String disp, String positive, String negative, float weightage, IStockAnalyzerBO analyzerBO) {
            this.disp = disp;
            this.post = positive;
            this.neg = negative;
            this.weightage = weightage;
            this.analyzerBO = analyzerBO;
        }

        public String toString() {
            return name() + "(" + disp + ")";
        }

        public String getDisplay() {
            return disp;
        }

        public String getPosDisplay() {
            return post;
        }

        public String getNegDisplay() {
            return neg;
        }

        public float getWeightage() {
            return weightage;
        }

        public IStockAnalyzerBO getAnalyzer() {
            return analyzerBO;
        }

        public boolean needInput() {
            return analyzerBO.hasFactors();
        }

        public Map<String, Float> getFactors() {
            return analyzerBO.getFactors();
        }

        public void setFactors(Map<String, Float> factors) {
            analyzerBO.setFactors(factors);
        }
    }

    public static enum enumQServer {
        ICICI(pm.net.ICICIQuoteDownloader.class),
        Yahoo(pm.net.YahooQuoteDownloader.class),
        Nse(pm.net.NSEQuoteDownloader.class);

        private Class className;

        private enumQServer(Class className) {
            this.className = className;
        }

        public Class getQClass() {
            return className;
        }
    }

    public static enum enumLogLevel {
        Debug, Info, Error, Fatal;
    }

    public static String LASTRUNFILE = "lastrun.properties";
    public static String ICICI_MAPPING_FILE = "ICICIMapping.properties";
    public static String STOCK_MASTER_FILE = "StockListMaster.csv";
    public static String DELIMITER_COMMA = ",";
    public static String DELIMITER2 = ";";
}
