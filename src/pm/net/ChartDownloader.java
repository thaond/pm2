/*
 * Created on Feb 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package pm.net;

import pm.util.AppConfig;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author thiyagu
 *         <p/>
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ChartDownloader {

    private static String baseURL = "http://ichart.yahoo.com/t?s=";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public static void download(String index, String id) {
        String baseDir = AppConfig.dataDownloadDir.Value + "/Chart";
        try {
            String fileName = dateFormat.format(new Date());
            FileOutputStream fos = new FileOutputStream(baseDir + "/" + fileName + "_" + id + ".png");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            InputStream inputStream = new HTTPHelper().getDataStream(baseURL + index);
            int ch;
            while ((ch = inputStream.read()) != -1) {
                bos.write(ch);
            }
            bos.close();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
