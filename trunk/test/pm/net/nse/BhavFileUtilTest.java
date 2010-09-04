package pm.net.nse;

import org.junit.Test;
import pm.TestHelper;
import pm.util.PMDate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class BhavFileUtilTest {
    @Test
    public void testGetEquityFilePath() throws Exception {
        assertEquals("./SampleData/Input/cm04JAN2010bhav.csv.zip", BhavFileUtil.getEquityFilePath(date()));
    }

    private Date date() {
        return new PMDate(4, 1, 2010).getJavaDate();
    }

    @Test
    public void testGetFandOFilePath() throws Exception {
        assertEquals("./SampleData/Input/fo04JAN2010bhav.csv.zip", BhavFileUtil.getFandOFilePath(date()));
    }

    @Test
    public void testGetEquityURL() throws Exception {
        assertEquals("2010/JAN/cm04JAN2010bhav.csv.zip", BhavFileUtil.getEquityURL(date()));
    }

    @Test
    public void testGetFandOURL() throws Exception {
        assertEquals("2010/JAN/fo04JAN2010bhav.csv.zip", BhavFileUtil.getFandOURL(date()));
    }

    @Test
    public void testOpenReaderForZipFile() throws IOException {
        String content = "some content";
        PMDate date = new PMDate(10, 12, 2009);

        String zipFilePath = BhavFileUtil.getEquityFilePath(date.getJavaDate());
        TestHelper.createZipFile(content, zipFilePath);

        Reader reader = BhavFileUtil.openReader(zipFilePath);
        String fileContent = getContent(reader);

        assertEquals(content, fileContent);
        new File(zipFilePath).delete();

    }

    private String getContent(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String content = br.readLine();
        br.close();
        return content;
    }


}
