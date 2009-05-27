package pm.net.nse.downloader;

import junit.framework.TestCase;
import pm.net.HTTPHelper;
import pm.util.AppConfig;
import pm.util.AppConst;
import pm.util.PMDate;
import pm.vo.CompanyActionVO;

import java.util.Vector;

/**
 * @author Thiyagu
 * @since 31-May-2007
 */
public class CorpActionDownloaderExternalTest extends TestCase {


    public void testRun() {
        if (!HTTPHelper.isNetworkAvailable()) return;
        AppConfig.dateCORPACTIONSYNCHRONIZER.Value = "19990101";
        CorpActionDownloader downloader = new CorpActionDownloader("DCHL", null);
        downloader.performTask();
        Vector<CompanyActionVO> actions = downloader.getCorpActions();
        CompanyActionVO splitActionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Split, new PMDate(13, 3, 2007), "DCHL", 2f, 10f);
        CompanyActionVO dividentActionVO = new CompanyActionVO(AppConst.COMPANY_ACTION_TYPE.Divident, new PMDate(4, 8, 2006), "DCHL", 10f, 1f);
        dividentActionVO.setPercentageValue(true);
        assertTrue(actions.size() >= 4);
        assertTrue(actions.contains(splitActionVO));
        assertTrue(actions.contains(dividentActionVO));
    }

}
