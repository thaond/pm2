package pm.net;

import org.apache.log4j.Logger;

public abstract class AbstractDownloader implements Runnable {

    protected Logger logger = Logger.getLogger(AbstractDownloader.class);

    protected boolean error = false;
    protected boolean stop = false;
    protected boolean completed = false;

    public boolean hasError() {
        return error;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean hasStopped() {
        return stop;
    }

    public void stop() {
        stop = true;
    }

}
