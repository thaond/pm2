package pm.bo;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.StockVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StockMasterBOUnitTest {

    @Test
    public void eliminateDuplicateUpdateModified_ToUpdateSymbolChange() {
        String isin = "ISIN";
        StockVO existingStockVO = new StockVO(1, "STOCKCODE", "CompanyName", 10f, SERIESTYPE.equity, 10f, (short) 10, isin, new PMDate(1, 1, 2009), true);
        StockVO newStockVO = new StockVO("NEWSTOCKCODE", "NewCompanyName", 2f, SERIESTYPE.equity, 2f, (short) 2, isin, new PMDate(1, 1, 2009), true);

        final List<StockVO> updatedStocks = new ArrayList<StockVO>();
        StockMasterBO bo = new StockMasterBO() {
            @Override
            void updateStock(StockVO stockVO) {
                updatedStocks.add(stockVO);
            }
        };
        bo.eliminateDuplicateUpdateModified(Arrays.asList(newStockVO), Arrays.asList(existingStockVO));
        assertEquals(1, updatedStocks.size());
        assertEquals(newStockVO, updatedStocks.get(0));
        assertEquals(existingStockVO.getId(), updatedStocks.get(0).getId());
    }

    @Test
    public void eliminateDuplicateUpdateModified_ToUpdateStockDetailsForPlaceHolderStocks() {
        final List<StockVO> updatedStocks = new ArrayList<StockVO>();
        StockMasterBO bo = new StockMasterBO() {
            @Override
            void updateStock(StockVO stockVO) {
                updatedStocks.add(stockVO);
            }
        };

        String stockCode = "STOCKCODE";
        StockVO existingStockVO = bo.createPlaceHolderStock(stockCode, "");
        StockVO newStockVO = new StockVO(stockCode, "NewCompanyName", 2f, SERIESTYPE.equity, 2f, (short) 2, "ISIN", new PMDate(1, 1, 2009), true);

        bo.eliminateDuplicateUpdateModified(Arrays.asList(newStockVO), Arrays.asList(existingStockVO));
        assertEquals(1, updatedStocks.size());
        assertEquals(newStockVO, updatedStocks.get(0));
        assertEquals(existingStockVO.getId(), updatedStocks.get(0).getId());
    }
}
