package pm.dao.ibatis.dao;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import pm.AppLoader;

import java.io.FileInputStream;

/**
 * Date: Aug 9, 2006
 * Time: 9:20:08 PM
 */
public class PMDBTestCase extends DatabaseTestCase {

    private final String dataFileName;

    public PMDBTestCase(String string) {
        super(string);
        dataFileName = "TestData.xml";
    }

    public PMDBTestCase(String string, String dataFileName) {
        super(string);
        this.dataFileName = dataFileName;
        AppLoader.initConsoleLogger();
    }

    @Override
    protected IDatabaseConnection getConnection() throws Exception {
        return new DatabaseConnection(pm.dao.derby.DBManager.getConnection());
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(new FileInputStream("test/data/" + dataFileName));
    }

    @Override
    protected void closeConnection(IDatabaseConnection arg0) throws Exception {
//		super.closeConnection(arg0);
    }

}

