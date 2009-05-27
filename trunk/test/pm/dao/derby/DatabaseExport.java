package pm.dao.derby;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Date: 23-Jul-2006
 * Time: 12:57:40
 */
public class DatabaseExport {

    public static void main(String[] args) throws Exception {

        // database connection
        IDatabaseConnection connection = new DatabaseConnection(DBManager.getConnection());

//        doPartialExport(connection);

        doFullExport(connection);
    }

    private static void doFullExport(IDatabaseConnection connection) throws SQLException, IOException, DataSetException {
        // full database export
        IDataSet fullDataSet = connection.createDataSet();
        FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));
    }

    private static void doPartialExport(IDatabaseConnection connection) throws SQLException, IOException, DataSetException {
        // partial database export
        QueryDataSet partialDataSet = new QueryDataSet(connection);
        partialDataSet.addTable("PORTFOLIO");
//        partialDataSet.addTable("STOCKMASTER");
//        partialDataSet.addTable("QUOTE");
//        partialDataSet.addTable("MARKETDATE");
        FlatXmlDataSet.write(partialDataSet,
                new FileOutputStream("full.xml"));
    }
}
