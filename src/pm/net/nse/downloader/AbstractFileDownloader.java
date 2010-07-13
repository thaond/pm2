package pm.net.nse.downloader;

import org.apache.log4j.Logger;
import pm.net.AbstractDownloader;
import pm.net.HTTPHelper;
import pm.net.eod.EODDownloadManager;
import pm.util.PMDate;

import java.io.*;
import java.util.Date;

public abstract class AbstractFileDownloader extends AbstractDownloader {

    public static Logger logger = Logger.getLogger(AbstractFileDownloader.class);
    protected Date date;
    protected EODDownloadManager manager;


    public AbstractFileDownloader(Date date, EODDownloadManager manager) {
        this.date = date;
        this.manager = manager;
    }

    public void run() {
        try {
            logger.info("Downloading " + getFileType() + " file : " + date);
            InputStream reader = getHTTPHelper().getDataStream(getURL());
            if (reader != null) {
                storeData(reader);
            }
        } finally {
            manager.taskCompleted(this);
        }
    }

    abstract protected String getFileType();

    abstract protected String getURL();

    void storeData(InputStream reader) {
        try {
            OutputStream bos = getOutputStream();
            int ch;
            while ((ch = reader.read()) != -1) {
                bos.write(ch);
            }
            bos.close();
        } catch (FileNotFoundException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
    }

    OutputStream getOutputStream() throws FileNotFoundException {
        return new FileOutputStream(getFilePath());
    }

    public abstract String getFilePath();

    HTTPHelper getHTTPHelper() {
        return new HTTPHelper();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractFileDownloader && new PMDate(this.date).equals(new PMDate(((AbstractFileDownloader) obj).date));
    }

    @Override
    public int hashCode() {
        return this.date.hashCode();
    }

    public Date getDate() {
        return date;
    }
}
