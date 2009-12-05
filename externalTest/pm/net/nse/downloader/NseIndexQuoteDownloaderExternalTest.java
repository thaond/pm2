package pm.net.nse.downloader;

import junit.framework.Assert;
import org.junit.Test;
import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.QuoteVO;
import pm.vo.StockVO;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NseIndexQuoteDownloaderExternalTest {

    @Test
    public void testDownloadData() throws Exception {
        StockVO stockVO = new StockVO("^NIFTY", "S&P CNX NIFTY", 0f, SERIESTYPE.nseindex, 0f, (short) 0, "", new PMDate(), true);
        final List<QuoteVO> quoteVOs = new ArrayList<QuoteVO>();
        NseIndexQuoteDownloader downloader = new NseIndexQuoteDownloader(null, stockVO) {
            @Override
            void store(List<QuoteVO> quotes) {
                quoteVOs.addAll(quotes);
            }
        };
        downloader.downloadData(new PMDate(5, 8, 2009), new PMDate(12, 8, 2009));
        assertEquals(6, quoteVOs.size());
        QuoteVO quote = quoteVOs.get(0);
        Assert.assertEquals(new PMDate(5, 8, 2009), quote.getDate());
        Assert.assertEquals(4680.95f, quote.getOpen());
        Assert.assertEquals(4717.20f, quote.getHigh());
        Assert.assertEquals(4629.85f, quote.getLow());
        Assert.assertEquals(4694.15f, quote.getClose());
        Assert.assertEquals(244640641f, quote.getVolume());
    }
}
