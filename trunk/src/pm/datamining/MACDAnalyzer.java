package pm.datamining;

import pm.bo.QuoteBO;
import pm.util.PMDate;
import pm.vo.EquityQuote;

import java.util.Vector;

public class MACDAnalyzer {

    public static void main(String[] str) {
        String stockCode = "ONGC";
        analyze(stockCode);
    }

    private static void analyze(String stockCode) {
        EquityQuote[] quoteVOs = new QuoteBO().getQuotes(stockCode, new PMDate(1, 1, 1999), null);
        if (quoteVOs.length < 71) {
            System.out.println("Not enough data");
            return;
        }
        Vector<EquityQuote> histData = new Vector<EquityQuote>(70);
        for (int i = 0; i < 70; i++) histData.add(quoteVOs[i]);
        Vector<EquityQuote> data = new Vector<EquityQuote>();
        for (int i = 70; i < quoteVOs.length; i++) data.add(quoteVOs[i]);

//		if (new MACDBO().markData(data, histData)) {
//			float buyPrice = 0f;
//			float overAllPer = 0;
//			int successCount = 0;
//			for (int i=0;i<data.size();i++) {
//				EquityQuote quoteVO = data.get(i);
//				if (quoteVO.getPickDetails().equals("MAb ")) {
//					buyPrice = quoteVO.getLastPrice();
//					System.out.println("Buy " + quoteVO.getDate());
//				} else if (quoteVO.getPickDetails().equals("MAs ")) {
//					System.out.println("Sell " + quoteVO.getDate());
//					if (buyPrice == 0) continue;
//					float pl = (quoteVO.getLastPrice() - buyPrice) / buyPrice * 100;
//					overAllPer += pl;
//					if ( pl > 0) {
//						successCount++;
//					} else {
//						successCount--;
//					}
//					//System.out.println(pl);
//				}
//			}
//			//System.out.println("Overall : " + overAllPer);
//			float firstlast = (data.lastElement().getLastPrice() - data.firstElement().getLastPrice()) / data.firstElement().getLastPrice() * 100;
//			//System.out.println("First Last : " + firstlast);
//			System.out.println("Sucess Count " + successCount);
//		}		
    }
}
