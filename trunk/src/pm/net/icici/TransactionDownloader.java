package pm.net.icici;

import org.apache.log4j.Logger;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.ITransactionDAO;
import pm.net.ICICITransactionParser;
import pm.util.AppConfig;
import pm.util.PMDate;
import pm.util.PMDateFormatter;
import pm.vo.ICICITransaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TransactionDownloader {

    static Logger _logger = Logger.getLogger(TransactionDownloader.class);

    private boolean error = false;
    private PMDate startDate;
    private PMDate endDate;

    public TransactionDownloader(PMDate startDate, PMDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    List<ICICITransaction> download() {

        try {
            String fileName = "icici_transaction.html";

            String[] cmd = {"ruby", "rpm/icici_transaction_downloader.rb", PMDateFormatter.formatDD_MM_YYYY(startDate), PMDateFormatter.formatDD_MM_YYYY(endDate),
                    AppConfig.iciciUserName.Value, AppConfig.iciciPasswd.Value, fileName};
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            if (process.exitValue() == 0) {
                return new ICICITransactionParser().parseHtml("file://" + new File(fileName).getAbsolutePath());
            } else {
                _logger.error("ICICI Transaction Dowloader error");
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    _logger.error(line);
                }
                br.close();
            }
        } catch (IOException e) {
            _logger.error(e, e);
            error = true;
        } catch (InterruptedException e) {
            _logger.error(e, e);
            error = true;
        }
        return new ArrayList<ICICITransaction>();
    }

    public boolean sync() {
        _logger.info("Started icici transaction download");
        List<ICICITransaction> transactions = download();
        if (!error) {
            saveTransaction(transactions);
        }
        _logger.info("Completed icici transaction download");
        return !error;
    }

    void saveTransaction(List<ICICITransaction> transactions) {
        ITransactionDAO dao = dao();
        for (ICICITransaction transaction : transactions) {
            _logger.info("Saving.." + transaction);
            dao.updateOrInsertICICITransaction(transaction);
        }
    }

    ITransactionDAO dao() {
        return DAOManager.getTransactionDAO();
    }
}
