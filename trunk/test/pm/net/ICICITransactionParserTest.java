package pm.net;

import junit.framework.TestCase;
import pm.util.AppConst;
import pm.util.PMDate;
import pm.vo.ICICITransaction;

import java.io.StringReader;
import java.util.List;


public class ICICITransactionParserTest extends TestCase {

    public void testParse() {
        List<ICICITransaction> transactions = new ICICITransactionParser().parse(new StringReader(data()));
        assertEquals(5, transactions.size());
        ICICITransaction ranbaxyBuy = new ICICITransaction(new PMDate(11, 6, 2008), "RANLAB", AppConst.TRADINGTYPE.Buy, 30f, 571f, 165.76f, false, "20080611N600020451");
        assertEquals(ranbaxyBuy, transactions.get(0));
        ICICITransaction suntvSell = new ICICITransaction(new PMDate(10, 6, 2008), "SUNTV", AppConst.TRADINGTYPE.Sell, 25f, 338.1f, 18.97f, true, "20080610N900008912");
        assertEquals(suntvSell, transactions.get(1));
    }

    public String data() {
        return "ICICI direct.com :: Trading :: Trade Book\n" +
                "Logout | F.A.Q. | Site map\n" +
                "Sunday, June 22, 2008,10:03IST\n" +
                "Home\n" +
                "Trading\n" +
                "Market\n" +
                "Commodity\n" +
                "Charts\n" +
                "Research\n" +
                "Mutual funds\n" +
                "Personal finance\n" +
                "Customer service\n" +
                "Equity\n" +
                "F&O\n" +
                "Commodities\n" +
                "Overseas Trading\n" +
                "MutualFunds\n" +
                "Savings\n" +
                "IPO\n" +
                "Insurance\n" +
                "Loans\n" +
                "Portfolio\n" +
                "Buy\n" +
                "Order Book\n" +
                "MarginPLUS Order\n" +
                "Limit\n" +
                "Cash Projection\n" +
                "SELECT ACCOUNT\n" +
                "My Messages / Help\n" +
                "Sell\n" +
                "Trade Book\n" +
                "MarginPLUS Positions\n" +
                "Modify Allocation\n" +
                "Security Projection\n" +
                "Stock List / View Point\n" +
                "Market messages\n" +
                "Fast Buy / Sell\n" +
                "Basket Order\n" +
                "Margin Positions\n" +
                "Demat Allocation\n" +
                "Converted to Delivery\n" +
                "iClick-2-Invest\n" +
                "iCLICK-2-GAIN\n" +
                "Shares for NSE Settl no. 2008114 have been credited. Customers are requested to kindly allocate the same before putting sell orders. --- ----- Result Update Outperformers ----- Stocks on the Move\n" +
                "EQUITY - TRADE BOOK: Orders executed on previous trading days can be viewed during non-trading hours and trading holidays only.\n" +
                "Account\n" +
                ":\n" +
                "8500232673\n" +
                "Date From\n" +
                ":\n" +
                "Date To\n" +
                ":\n" +
                "Stock Code\n" +
                ":\n" +
                "Exchange\n" +
                ":\n" +
                "All NSE BSE\n" +
                "Product\n" +
                ":\n" +
                "All BTST CASH SPOT MarginPLUS\n" +
                "Date\n" +
                "Stock\n" +
                "Action\n" +
                "Qty.\n" +
                "Price#\n" +
                "Trade Value\n" +
                "Brokerage incl. taxes\n" +
                "Order Ref.\n" +
                "Settlement\n" +
                "Segment\n" +
                "DP Id - Client DP Id\n" +
                "Exchange\n" +
                "U / P\n" +
                "11-Jun-2008\n" +
                "RANLAB\n" +
                "Buy\n" +
                "30\n" +
                "571.00\n" +
                "17,130.00\n" +
                "165.76\n" +
                "20080611N600020451\n" +
                "2008110\n" +
                "Rolling\n" +
                "IN302679-31502823\n" +
                "NSE\n" +
                "10-Jun-2008\n" +
                "SUNTV\n" +
                "Sell\n" +
                "25\n" +
                "338.10\n" +
                "8,452.50\n" +
                "18.97\n" +
                "20080610N900008911\n" +
                "2008109\n" +
                "Rolling\n" +
                "MarginPLUS\n" +
                "NSE\n" +
                "10-Jun-2008\n" +
                "SUNTV\n" +
                "Buy\n" +
                "25\n" +
                "350.00\n" +
                "8,750.00\n" +
                "16.86\n" +
                "20080610N900008912\n" +
                "2008109\n" +
                "Rolling\n" +
                "MarginPLUS\n" +
                "NSE\n" +
                "02-Jun-2008\n" +
                "SUNTV\n" +
                "Sell\n" +
                "25\n" +
                "359.15\n" +
                "8,978.75\n" +
                "19.10\n" +
                "20080602N600002959\n" +
                "2008103\n" +
                "Rolling\n" +
                "MarginPLUS\n" +
                "NSE\n" +
                "02-Jun-2008\n" +
                "SUNTV\n" +
                "Buy\n" +
                "25\n" +
                "351.25\n" +
                "8,781.25\n" +
                "16.86\n" +
                "20080602N600002960\n" +
                "2008103\n" +
                "Rolling\n" +
                "MarginPLUS\n" +
                "NSE\n" +
                "Total\n" +
                "(17,230.00)\n" +
                "237.55\n" +
                "home | trading | markets | quotes & charts | research | mutual funds | personal finance | customer service | site map | disclaimer\n" +
                "Minimum Browser Requirement: You must have Internet Explorer 5.5 & above or Netscape Communicator 4.7 & above.\n" +
                "Copyright© 2008.All rights Reserved. ICICI Securities Ltd\n" +
                "¨ trademark registration in respect of the concerned mark has been applied for by ICICI Bank Limited\n" +
                "NSE SEBI Registration Number Capital Market :- INB 230773037 | BSE SEBI Registration Number Capital Market :- INB 011286854\n" +
                "NSE SEBI Registration Number Derivatives :- INF 230773037.\n" +
                "Comm Trade Services Limited\n" +
                "NCDEX Membership No.00034 | MCX Membership No.16065\n" +
                "";
    }
}
