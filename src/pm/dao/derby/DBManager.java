package pm.dao.derby;

import com.ibatis.common.jdbc.ScriptRunner;
import org.apache.log4j.Logger;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import pm.AppLoader;
import pm.util.AppConfig;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBManager extends MockObjectTestCase {

    private static Logger logger = Logger.getLogger(DBManager.class);

    static {
        try {
            Class.forName(AppConfig.DB_DRIVER.Value).newInstance();
        } catch (InstantiationException e) {
            logger.error(e, e);
        } catch (IllegalAccessException e) {
            logger.error(e, e);
        } catch (ClassNotFoundException e) {
            logger.error(e, e);
        }
    }

    public static void initDB() {
        logger.info("Creating Database");
        runSQLsFromFile("createDB.sql", createNewConnection());

    }

    public static void cleanUpDB() {
        runSQLsFromFile("cleanUpTransactions.sql", createNewConnection());
    }

    private static void runSQLsFromFile(String sqlFileName, Connection conn) {
        try {
            ScriptRunner runner = new ScriptRunner(conn, false, false);
            PrintWriter pw = new PrintWriter(System.out);
            runner.setErrorLogWriter(pw);
            runner.setLogWriter(pw);
            runner.runScript(new FileReader(sqlFileName));
        } catch (FileNotFoundException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        } catch (SQLException e) {
            logger.error(e, e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error(e, e);
                }
            }
        }
    }

    public static Connection createNewConnection() {
        Properties props = new Properties();
        props.put("user", AppConfig.DB_USER.Value);
        props.put("password", AppConfig.DB_PASSWORD.Value);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(AppConfig.DB_URL.Value + ";create=true", props);
        } catch (SQLException e) {
            logger.error(e, e);
        }
        return connection;

    }

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) connection = createNewConnection();
        return connection;
//        return new DBManager().mockConnection();
    }

    public Connection mockConnection() {
        Mock mock = mock(Connection.class);
        return (Connection) mock.proxy();
    }


    public static void main(String[] str) {
        AppLoader.initConsoleLogger();
        initDB();
    }

    public static void shutDown() {
        if (isEmbeddedDB()) {
            try {
                DriverManager.getConnection(AppConfig.DB_URL.Value + ";shutdown=true");
                logger.error("DB Shutdown failed");
            } catch (SQLException e) {
                logger.info("DB Shutdown success");
            }
        }
    }

    private static boolean isEmbeddedDB() {
        return AppConfig.DB_DRIVER.Value.equals("org.apache.derby.jdbc.EmbeddedDriver");
    }

    public static void initHsqlDB() {
        runSQLsFromFile("createHsqlDB.sql", getHsqlDBConnection());

    }


    public static Connection getHsqlDBConnection() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (Exception e) {
            System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            return null;
        }

        try {
            return DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
        } catch (SQLException e) {
            logger.error(e, e);
        }
        return null;
    }

}
