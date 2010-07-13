package pm.net.nse;

import org.junit.Test;
import pm.util.PMDate;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class FileNameUtilTest {
    @Test
    public void testGetEquityFilePath() throws Exception {
        assertEquals("./SampleData/Input/cm04JAN2010bhav.csv.zip", FileNameUtil.getEquityFilePath(date()));
    }

    private Date date() {
        return new PMDate(4, 1, 2010).getJavaDate();
    }

    @Test
    public void testGetFandOFilePath() throws Exception {
        assertEquals("./SampleData/Input/fo04JAN2010bhav.csv.zip", FileNameUtil.getFandOFilePath(date()));
    }

    @Test
    public void testGetEquityURL() throws Exception {
        assertEquals("2010/JAN/cm04JAN2010bhav.csv.zip", FileNameUtil.getEquityURL(date()));
    }

    @Test
    public void testGetFandOURL() throws Exception {
        assertEquals("2010/JAN/fo04JAN2010bhav.csv.zip", FileNameUtil.getFandOURL(date()));
    }
}
