package pm.net.nse;

import pm.bo.CompanyBO;
import pm.net.AbstractDownloader;
import pm.net.HTTPHelper;
import pm.net.nse.downloader.AbstractHTMLDownloader;
import pm.net.nse.downloader.CorpResultDownloader;
import pm.net.nse.downloader.CorpResultLinkDownloader;
import pm.util.AppConfig;
import pm.util.AppConst.CORP_RESULT_TIMELINE;
import pm.util.ApplicationException;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.util.enumlist.TASKNAME;
import pm.vo.CorpResultVO;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;

public class CorpResultDownloadManager extends AbstractStockDownloadManager {

    private ThreadPoolExecutor executor;

    Hashtable<String, Integer> stockwiseDownloaderCount = new Hashtable<String, Integer>();

    Hashtable<String, AbstractHTMLDownloader> downloaderList = new Hashtable<String, AbstractHTMLDownloader>();

    public CorpResultDownloadManager(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    synchronized public void taskCompleted(AbstractDownloader completedTask) {

        if (completedTask instanceof CorpResultLinkDownloader) {
            CorpResultLinkDownloader task = ((CorpResultLinkDownloader) completedTask);
            if (task.isCompleted() && !stop) {
                Vector<String> linkList = task.getLinkList();
                String stockCode = task.getStockCode();
                List<CorpResultVO> processedFinData = getExistingData(stockCode);
                if (logger.isInfoEnabled()) {
                    logger.info("Available result data for " + stockCode + " " + processedFinData);
                }
                for (String url : linkList) {
                    CorpResultVO resultVO = buildResultVO(url);
                    if (resultVO == null) {
                        continue;
                    }
                    if (logger.isInfoEnabled()) {
                        logger.info(resultVO);
                    }
                    if (!processedFinData.contains(resultVO)) {
                        if (!stop) {
                            CorpResultDownloader corpResultDownloader = new CorpResultDownloader(
                                    this, url, resultVO);
                            executor.execute(corpResultDownloader);
                            downloaderList.put(url, corpResultDownloader);
                            Integer count = stockwiseDownloaderCount
                                    .get(stockCode);
                            if (count == null) {
                                count = 0;
                            }
                            count++;
                            stockwiseDownloaderCount.put(stockCode, count);
                        }
                    }
                }
                Integer count = stockwiseDownloaderCount.get(stockCode);
                if (count == null || count == 0) {
                    completedTaskCount++;
                }
            } else {
                completedTaskCount++;
                addToInCompleteList(task.getStockCode());
            }
            downloaderList.remove(task.getStockCode());
        } else if (completedTask instanceof CorpResultDownloader) {
            CorpResultDownloader task = (CorpResultDownloader) completedTask;
            downloaderList.remove(task.getURL());
            Integer count = stockwiseDownloaderCount
                    .remove(task.getStockCode());
            count--;
            if (count == 0) {
                completedTaskCount++;
            } else {
                stockwiseDownloaderCount.put(task.getStockCode(), count);
            }
            if (!task.isCompleted()) {
                addToInCompleteList(task.getStockCode());
            }
        }
        taskCompleted = completedTaskCount == totalTaskCount;

        if (taskCompleted) {
            shutDownManager();
        }
    }

    void shutDownManager() {
        logger.info("Shutdown initiated in CorpResultDownloadManager");
        storeIncompleteList();
        saveStatus();
        logger.info("Shutdown Complete");

    }

    List<CorpResultVO> getExistingData(String stockCode) {
        return new CompanyBO().getFinData(stockCode);
    }

    public CorpResultVO buildResultVO(String url) {
        if (url == null || url.length() < 94) {
            logger.error("Invalid CorpResult URL : " + url);
            return null;
        }
        String parameter = "param=";
        int st = url.indexOf(parameter) + parameter.length();
        int ed = st + 29;
        int symbolEnd = url.indexOf("&", ed);
        if (symbolEnd == -1) symbolEnd = url.length();
        try {
            String stockCode = removeEqualsAtTheEnd(HTTPHelper.decode(url.substring(ed, symbolEnd)));
            String ctrlString = url.substring(st, ed);
            PMDate startDate = PMDateFormatter.parseDD_MMM_YYYY(ctrlString
                    .substring(0, 11));
            PMDate endDate = PMDateFormatter.parseDD_MMM_YYYY(ctrlString
                    .substring(11, 22));
            char chTimeline = ctrlString.charAt(22);
            CORP_RESULT_TIMELINE timeline = (chTimeline == 'A') ? CORP_RESULT_TIMELINE.Annual
                    : (chTimeline == 'H') ? CORP_RESULT_TIMELINE.HalfYearly
                    : (chTimeline == 'Q') ? CORP_RESULT_TIMELINE.Quaterly
                    : CORP_RESULT_TIMELINE.Other;
            int period = (timeline == CORP_RESULT_TIMELINE.Annual || timeline == CORP_RESULT_TIMELINE.Other) ? 0
                    : Integer.parseInt(ctrlString.substring(23, 24));
            boolean auditedFlag = (ctrlString.charAt(24) == 'A');
            boolean consolidatedFlag = (ctrlString.charAt(27) == 'C');
            int year = startDate.getYear();
            if (timeline == CORP_RESULT_TIMELINE.Quaterly
                    && startDate.getMonth() == 1 && period == 4) {
                year--; // 1/1/2004 - 31/3/2004 is 2003Q4 result.
            }
            return new CorpResultVO(stockCode, startDate, endDate, timeline,
                    period, auditedFlag, consolidatedFlag, false, year);
        } catch (UnsupportedEncodingException e) {
            logger.error(e, e);
        } catch (ApplicationException e) {
            logger.error(e, e);
        }
        return null;
    }

    private String removeEqualsAtTheEnd(String input) {
        if (input.endsWith("=")) {
            return input.substring(0, input.length() - 1);
        }
        return input;
    }

    public void stop() {
        stop = true;
        for (AbstractHTMLDownloader downloader : downloaderList.values()) {
            downloader.stop();
        }
    }

    public void run() {
        Vector<String> stockList = getStockList();
        for (String stockCode : stockList) {
            CorpResultLinkDownloader linkDownloader = getLinkDownloader(stockCode);
            downloaderList.put(stockCode, linkDownloader);
            executor.execute(linkDownloader);
            totalTaskCount++;
        }
        initComplete = true;
    }

    CorpResultLinkDownloader getLinkDownloader(String stockCode) {
        CorpResultLinkDownloader linkDownloader = new CorpResultLinkDownloader(
                this, stockCode);
        return linkDownloader;
    }

    @Override
    AppConfig getErrorListConfig() {
        return AppConfig.corpResultDownloadErrorList;
    }

    public TASKNAME getTaskName() {
        return TASKNAME.CORPRESULTDOWNLOAD;
    }

    public boolean isIndeterminate() {
        return false;
    }

}
