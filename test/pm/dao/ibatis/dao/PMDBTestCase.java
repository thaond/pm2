package pm.dao.ibatis.dao;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
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
        AppLoader.initConsoleLogger();
    }

    public PMDBTestCase(String string, String dataFileName) {
        super(string);
        this.dataFileName = dataFileName;
        AppLoader.initConsoleLogger();
    }

    @Override
    protected IDatabaseConnection getConnection() throws Exception {
        return new DatabaseConnection(pm.dao.derby.DBManager.createNewConnection());
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(false);
        return builder.build(new FileInputStream("test/data/" + dataFileName));
    }
}

