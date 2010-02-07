package pm.dao.ibatis.dao;

import com.ibatis.common.resources.Resources;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.DaoManagerBuilder;
import org.apache.log4j.Logger;
import pm.util.AppConfig;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * Date: 22-Jul-2006
 * Time: 15:37:45
 */
public class DAOManager {

    private static Logger logger = Logger.getLogger(DAOManager.class);
    private static DaoManager daoManager;
    private static String contextId = "sqlmap";

    static {
        String sqlMapConfigFile = "pm/dao/ibatis/dao/DAOMap.xml";
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(sqlMapConfigFile);
        } catch (IOException e) {
            logger.error(e, e);
        }
        Properties dbProps = new Properties();
        addProperty(dbProps, AppConfig.DB_DRIVER);
        addProperty(dbProps, AppConfig.DB_URL);
        addProperty(dbProps, AppConfig.DB_USER);
        addProperty(dbProps, AppConfig.DB_PASSWORD);
        daoManager = DaoManagerBuilder.buildDaoManager(reader, dbProps);
    }

    private static void addProperty(Properties prop, AppConfig appConfig) {
        prop.setProperty(appConfig.name(), appConfig.Value);
    }

    public static IStockDAO getStockDAO() {
        return (IStockDAO) daoManager.getDao(IStockDAO.class, contextId);
    }

    public static IQuoteDAO getQuoteDAO() {
        return (IQuoteDAO) daoManager.getDao(IQuoteDAO.class, contextId);
    }

    public static IDateDAO getDateDAO() {
        return (IDateDAO) daoManager.getDao(IDateDAO.class, contextId);
    }

    public static IAccountDAO getAccountDAO() {
        return (IAccountDAO) daoManager.getDao(IAccountDAO.class, contextId);
    }

    public static ITransactionDAO getTransactionDAO() {
        return (ITransactionDAO) daoManager.getDao(ITransactionDAO.class, contextId);
    }

    public static DaoManager getDaoManager() {
        return daoManager;
    }

    public static IPortfolioDAO getPortfolioDAO() {
        return (IPortfolioDAO) daoManager.getDao(IPortfolioDAO.class, contextId);
    }

    public static IWatchlistDAO getWatchlistDAO() {
        return (IWatchlistDAO) daoManager.getDao(IWatchlistDAO.class, contextId);
    }

    public static IIPODAO getIPODAO() {
        return (IIPODAO) daoManager.getDao(IIPODAO.class, contextId);
    }

    public static ICompanyActionDAO getCompanyActionDAO() {
        return (ICompanyActionDAO) daoManager.getDao(ICompanyActionDAO.class, contextId);
    }

    public static IFundTransactionDAO fundTransactionDAO() {
        return (IFundTransactionDAO) daoManager.getDao(IFundTransactionDAO.class, contextId);
    }

    public static ICompanyResultDAO companyResultDAO() {
        return (ICompanyResultDAO) daoManager.getDao(ICompanyResultDAO.class, contextId);
    }

    public static IDataWarehouseDAO getDataWarehouseDAO() {
        return (IDataWarehouseDAO) daoManager.getDao(IDataWarehouseDAO.class, contextId);
    }
}
