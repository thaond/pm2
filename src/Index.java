import pm.dao.derby.DBManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by IntelliJ IDEA.
 * User: thiyagu
 * Date: May 23, 2008
 * Time: 10:53:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Index {

    public static void main(String[] args) throws SQLException {
        Connection conn = DBManager.createNewConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE INDEX IDX_QUOTE_STOCKID_DATEID ON QUOTE (STOCKID,DATEID)");
        stmt.execute("CREATE INDEX IDX_STOCKMASTER_STOCKCODE ON STOCKMASTER (STOCKCODE)");
        conn.close();
    }
}
