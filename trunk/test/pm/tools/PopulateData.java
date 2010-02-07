package pm.tools;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

public class PopulateData {

    public static void main(String[] args) throws DatabaseUnitException, IOException, SQLException {
        DatabaseConnection connection = new DatabaseConnection(pm.dao.derby.DBManager.getConnection());
        IDataSet dataSet = new FlatXmlDataSet(new FileInputStream("test/data/TestData.xml"));
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
    }
}
