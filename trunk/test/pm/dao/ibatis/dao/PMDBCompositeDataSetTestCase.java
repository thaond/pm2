package pm.dao.ibatis.dao;

import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileInputStream;

public class PMDBCompositeDataSetTestCase extends PMDBTestCase {

    private final String[] dataFileNames;

    public PMDBCompositeDataSetTestCase(String string, String... dataFileNames) {
        super(string, null);
        this.dataFileNames = dataFileNames;
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet[] dataSets = new IDataSet[dataFileNames.length];
        for (int i = 0; i < dataSets.length; i++) {
            dataSets[i] = new FlatXmlDataSet(new FileInputStream("test/data/" + dataFileNames[i]));
        }
        return new CompositeDataSet(dataSets, true);
    }
}
