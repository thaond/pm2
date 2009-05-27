package pm.util.enumlist;

import pm.util.AppConfig;
import static pm.util.AppConfig.dataDir;
import static pm.util.AppConfig.dataDownloadDir;

/**
 * @author Thiyagu
 * @version $Id: AppConfigWrapper.java,v 1.1 2007/12/31 05:40:33 tpalanis Exp $
 * @since 31-Dec-2007
 */
public enum AppConfigWrapper {
    logFileDir(dataDir, "log"), metaInputFolder(dataDownloadDir, "MetaInputData"),
    bhavInputFolder(dataDownloadDir, "Input");

    public String Value;

    AppConfigWrapper(AppConfig dataDir, String path) {
        Value = dataDir.Value + "/" + path;
    }
}
