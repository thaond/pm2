/*
 * Created on 11-Feb-2005
 *
 */
package pm.dao;

import org.apache.log4j.Logger;
import pm.util.AppConfig;
import pm.util.PMDate;
import pm.vo.CompanyActionVO;
import pm.vo.CorporateResultsVO;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author thiyagu1
 */
public class CompanyDAO {

    private static Logger logger = Logger.getLogger(CompanyDAO.class);

    private static String CORP_ACTION_FILE = "Consolidated";

    /**
     * @param ticker
     * @return
     */
    @SuppressWarnings("unchecked")
    public Vector<CorporateResultsVO> getFinancialData(String ticker) {
        Vector<CorporateResultsVO> retVal = new Vector<CorporateResultsVO>();
        String baseDir = baseDir();
        String fileName = baseDir + "/" + ticker + ".data";
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(fileName));
            retVal = (Vector) ois.readObject();
        } catch (FileNotFoundException e) {
            logger.info("Financial data file missing for " + ticker);
        } catch (IOException e) {
            logger.error(e, e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null)
                try {
                    ois.close();
                } catch (IOException e1) {
                }
        }
        return retVal;
    }

    private String baseDir() {
        return AppConfig.dataDownloadDir.Value + "/CorpResults";
    }

    public boolean writeConsolidatedActionData(
            Hashtable<PMDate, Vector<CompanyActionVO>> consolidatedData) {
        String fileName = getCorpActionFileName(CORP_ACTION_FILE);
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(
                    fileName, false));
            outputStream.writeObject(consolidatedData);
            return true;
        } catch (FileNotFoundException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e1) {
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public Hashtable<PMDate, Vector<CompanyActionVO>> getConsolidatedActionData() {
        Hashtable<PMDate, Vector<CompanyActionVO>> retVal = new Hashtable<PMDate, Vector<CompanyActionVO>>();
        String fileName = getCorpActionFileName(CORP_ACTION_FILE);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(fileName));
            retVal = (Hashtable) ois.readObject();
        } catch (FileNotFoundException e) {
            logger.info("Consolidated Action Data file missing");
        } catch (IOException e) {
            logger.error(e, e);
        } catch (ClassNotFoundException e) {
            logger.error(e, e);
        } finally {
            if (ois != null)
                try {
                    ois.close();
                } catch (IOException e1) {
                }
        }
        return retVal;
    }

    private static String getCorpActionFileName(String stockCode) {
        return "./" + stockCode + ".data";
    }
}
