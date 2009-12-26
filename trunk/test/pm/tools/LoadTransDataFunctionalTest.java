package pm.tools;

import junit.framework.TestCase;
import pm.AppLoader;
import pm.bo.CompanyBO;
import pm.bo.PortfolioBO;
import pm.net.NSESymbolChangeDownloader;
import pm.net.nse.StockListDownloader;
import pm.util.AppConst;
import pm.util.BusinessLogger;
import pm.util.enumlist.AppConfigWrapper;
import pm.vo.ConsolidatedTradeVO;
import pm.vo.SymbolChange;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * @author Thiyagu
 * @since 23-Jul-2007
 */
public class LoadTransDataFunctionalTest extends TestCase {

    public void testLoadData() throws Exception {
        AppLoader.initConsoleLogger();
        loadData();
        validateResult(AppConst.REPORT_TYPE.All.name(), AppConst.REPORT_TYPE.All.name(), 950424.25f, 1581853.75f, 631429.5f, 153120.84f, 35970.6f, 820520.81f);
        validateResult(AppConst.REPORT_TYPE.All.name(), "Thiyagu", 850579.25f, 1485465f, 634885.81f, 80699.84f, 34035.6f, 749621.25f);
    }

    private void loadData() throws Exception {
        final String dataDir = "SampleData/";
        LoadTransData loadTransData = new LoadTransData() {
            void loadStockList() {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new FileReader(dataDir + "EQUITY_L.csv"));
                    new StockListDownloader().loadStockList(bufferedReader);
                    bufferedReader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<SymbolChange> getSymbolChangeList() {
                try {
                    return new NSESymbolChangeDownloader().parseData(new FileReader(dataDir + "symbolchange.csv"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            BhavToPMConverter bhavToPMConverter() {
                return new BhavToPMConverter() {
                    @Override
                    void moveFileToBackup(Date date, boolean moveBhavFile, boolean moveDelivFile) {
                    }
                };
            }
        };

        String transFilePath = dataDir + BusinessLogger.TRANSACTION_LOG_FILE;
        String compActionFilePath = dataDir + "CA_Complete till 28 07 2007 Verified.log";
        AppConfigWrapper.bhavInputFolder.Value = dataDir;
        loadTransData.loadData("20000101", 20070827, transFilePath, compActionFilePath, false, false);
        new CompanyBO().normalizeDividents();
    }

    private void validateResult(String tradingAc, String portfolio, float valueAtCost, float marketValue, float unRealizedPL, float realizedPL, float divident, float netPL) {
        Vector<ConsolidatedTradeVO> tradeVOs = new PortfolioBO().getPortfolioView(tradingAc, portfolio, AppConst.REPORT_TYPE.All.name());
        float totValAtCost = 0;
        float totMarkValue = 0;
        float totUnRealPL = 0;
        float totRealPL = 0;
        float totNetPL = 0;
        float totDiv = 0;
        for (ConsolidatedTradeVO tradeVO : tradeVOs) {
            totValAtCost += tradeVO.getCost();
            totMarkValue += tradeVO.getCurrentValue();
            totUnRealPL += tradeVO.getUnRealizedPL();
            totRealPL += tradeVO.getProfitLoss();
            totNetPL += tradeVO.getNetPL();
            totDiv += tradeVO.getDivident();
        }

        assertEquals(valueAtCost, totValAtCost);
        assertEquals(marketValue, totMarkValue);
        assertEquals(unRealizedPL, totUnRealPL);
        assertEquals(realizedPL, totRealPL);
        assertEquals(divident, totDiv);
        assertEquals(netPL, totNetPL);
    }
}
