package pm.dao.derby.function;

import pm.dao.derby.DBManager;
import pm.dao.ibatis.dao.PMDBTestCase;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Date: Sep 11, 2006
 * Time: 9:03:24 PM
 */
public class DividentFunctionTest extends PMDBTestCase {
    public DividentFunctionTest(String string) {
        super(string, "CompanyActionTestData.xml");
    }

    public void testCalculateDivident() throws Exception {
        Connection newConnection = DBManager.createNewConnection();
        assertEquals(160f, DividentFunction.calculateDivident(newConnection, 1, 20060101, 20060105, 5, 0, 0));
        assertEquals(20f, DividentFunction.calculateDivident(newConnection, 2, 20060101, 20060105, 2, 0, 0));
        assertEquals(0f, DividentFunction.calculateDivident(newConnection, 3, 20060101, 20060105, 5, 0, 0));
        assertEquals(0f, DividentFunction.calculateDivident(newConnection, 1, 20040101, 20040105, 5, 0, 0));
        assertEquals(0f, DividentFunction.calculateDivident(newConnection, 1, 20060101, 20060101, 5, 0, 0));
        assertEquals(0f, DividentFunction.calculateDivident(newConnection, 1, 20060102, 20060102, 5, 0, 0));
        assertEquals(110f, DividentFunction.calculateDivident(newConnection, 1, 20060101, 20060102, 5, 0, 0));
        assertEquals(210f, DividentFunction.calculateDivident(newConnection, 1, 20060101, 0, 5, 0, 0));
        assertEquals(0f, DividentFunction.calculateDivident(newConnection, 1, 20070101, 20070102, 5, 0, 0));
        assertEquals(0f, DividentFunction.calculateDivident(newConnection, 5, 20070101, 20070102, 5, 0, 0));

        assertEquals(50f, DividentFunction.calculateDivident(newConnection, 4, 20070101, 20070103, 5, 0, 0));
    }

    public void testCalculateDividentForFinancialYear() throws Exception {
        Connection newConnection = DBManager.createNewConnection();
        assertEquals(0f, DividentFunction.calculateDivident(newConnection, 8, 20050101, 20060301, 5, 0, 2006));
        assertEquals(0f, DividentFunction.calculateDivident(newConnection, 8, 20050101, 20060331, 1, 0, 2006));
        assertEquals(32f, DividentFunction.calculateDivident(newConnection, 8, 20050101, 0, 1, 0, 2006));
        assertEquals(27f, DividentFunction.calculateDivident(newConnection, 8, 20060415, 0, 1, 0, 2006));
        assertEquals(0f, DividentFunction.calculateDivident(newConnection, 8, 20060415, 20060419, 1, 0, 2006));
        assertEquals(6f, DividentFunction.calculateDivident(newConnection, 8, 20060415, 20060719, 1, 0, 2006));
        assertEquals(0f, DividentFunction.calculateDivident(newConnection, 8, 20070401, 0, 1, 0, 2006));
    }

    public void testCalculateDividentToReturnZeroForDayTrade() throws Exception {
        Connection newConnection = DBManager.createNewConnection();
        assertEquals(0f, DividentFunction.calculateDivident(newConnection, 1, 20060101, 20060105, 5, 1, 0));
        assertEquals(0f, DividentFunction.calculateDivident(newConnection, 2, 20060101, 20060105, 2, 1, 0));
    }

    public void testGetHoldingQty() throws SQLException {
        Connection newConnection = DBManager.createNewConnection();
        assertEquals(2f, DividentFunction.getHoldingQty(2, newConnection));
        assertEquals(0f, DividentFunction.getHoldingQty(1, newConnection));
        assertEquals(1f, DividentFunction.getHoldingQty(3, newConnection));

    }
}
