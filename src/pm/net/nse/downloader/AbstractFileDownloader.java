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
        logger.info("Downloading " + getFileType() + " file : " + date);
        Reader reader = getHTTPHelper().getData(getThisURL());
        if (reader != null) {
            storeData(reader);
        }
        manager.taskCompleted(this);
    }

    abstract protected String getFileType();

    abstract protected String getThisURL();

    void storeData(Reader reader) {
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
        return new FileOutputStream(getThisFilePath());
    }

    public abstract String getThisFilePath();

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
