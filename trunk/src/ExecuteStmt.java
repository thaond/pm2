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
public class ExecuteStmt {

    public static void main(String[] args) throws SQLException {
        if (args.length == 0) {
            System.out.println("Usage ExecuteStmt <statement to be executed>");
            System.exit(0);
        }
        System.out.println("Executing " + args[0]);
        Connection conn = DBManager.createNewConnection();
        Statement stmt = conn.createStatement();
        stmt.execute(args[0]);
        conn.close();
    }
}