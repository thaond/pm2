package pm.dao.derby.function;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EODStatisticsProcedure {

    /**
     * select dateval from marketdate where dateval <= 20091201 order by DATEVAL desc offset 4 rows fetch next 1 rows only;
     * select dateval from marketdate where dateval <= 20091201 order by DATEVAL desc offset 19 rows fetch next 1 rows only;
     * select dateval from marketdate where dateval <= 20091201 order by DATEVAL desc offset 9 rows fetch next 1 rows only;
     * select dateval from marketdate where dateval <= 20091201 order by DATEVAL desc offset 49 rows fetch next 1 rows only;
     * select dateval from marketdate where dateval <= 20091201 order by DATEVAL desc offset 199 rows fetch next 1 rows only;
     * <p/>
     * insert into EODSTATICS (stockid, dateid, high5d, high20d, high52w, highlifetime, low5d, low20d, low52w, lowlifetime, movavg10d, movavg50d, movavg200d)
     * values(
     * 1942,
     * 20100104,
     * (select max(adjustedclose) from quote where dateid <= 20091201 and dateid >= 20091125 and STOCKID = 1942),
     * (select max(adjustedclose) from quote where dateid <= 20091201 and dateid >= 20091104 and STOCKID = 1942),
     * (select max(adjustedclose) from quote where dateid <= 20091201 and dateid > 20081125 and STOCKID = 1942),
     * (select max(adjustedclose) from quote where dateid <= 20091201 and STOCKID = 1942),
     * <p/>
     * (select min(adjustedclose) from quote where dateid <= 20091201 and dateid >= 20091125 and STOCKID = 1942),
     * (select min(adjustedclose) from quote where dateid <= 20091201 and dateid >= 20091104 and STOCKID = 1942),
     * (select min(adjustedclose) from quote where dateid <= 20091201 and dateid > 20081125 and STOCKID = 1942),
     * (select min(adjustedclose) from quote where dateid <= 20091201 and STOCKID = 1942),
     * (select sum(adjustedclose)/10 from quote where dateid <= 20091201 and dateid >= 20091118 and STOCKID = 1942),
     * (select sum(adjustedclose)/50 from quote where dateid <= 20091201 and dateid >= 20090916 and STOCKID = 1942),
     * (select sum(adjustedclose)/200 from quote where dateid <= 20091201 and dateid >= 20090205 and STOCKID = 1942))
     */
    public static void calculateStatistics() throws SQLException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:default:connection");
            calculateStatistics(conn);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    static void calculateStatistics(Connection conn) throws SQLException {
        List<Integer> dates = findDatesToUpdateStatistics(conn);
        for (Integer date : dates) {
            calculateDayStatistics(date, conn);
        }
    }

    static List<Integer> findDatesToUpdateStatistics(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT DATEVAL FROM MARKETDATE WHERE ((SELECT MAX(DATEID) FROM EODSTATISTICS) IS NULL OR DATEVAL > (SELECT MAX(DATEID) FROM EODSTATISTICS)) AND NSEQUOTES = 1 ORDER BY DATEVAL ASC");
        ResultSet resultSet = stmt.executeQuery();
        List<Integer> dates = new ArrayList<Integer>();
        while (resultSet.next()) {
            dates.add(resultSet.getInt("dateval"));
        }
        return dates;
    }

    static void calculateDayStatistics(int dateID, Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT STOCKID FROM QUOTE WHERE DATEID = " + dateID);
        ResultSet resultSet = stmt.executeQuery();
        List<Integer> stockIds = new ArrayList<Integer>();
        while (resultSet.next()) {
            stockIds.add(resultSet.getInt("STOCKID"));
        }
        resultSet.close();
        stmt.close();
        int prior4Day = findPriorDate(dateID, 4, conn);
        int prior9Day = findPriorDate(dateID, 9, conn);
        int prior19Day = findPriorDate(dateID, 19, conn);
        int prior49Day = findPriorDate(dateID, 49, conn);
        int prior199Day = findPriorDate(dateID, 199, conn);
        for (Integer stockId : stockIds) {
            insertEODStatistics(conn, stockId, dateID, prior4Day, prior9Day, prior19Day, prior49Day, prior199Day);
        }
    }

    static void insertEODStatistics(Connection conn, Integer stockId, int dateID, int prior4Day, int prior9Day, int prior19Day, int prior49Day, int prior199Day) throws SQLException {
        conn.createStatement().execute(getInsertStmt(stockId, dateID, prior4Day, prior9Day, prior19Day, prior49Day, prior199Day));
    }

    static String getInsertStmt(Integer stockId, int dateID, int prior4Day, int prior9Day, int prior19Day, int prior49Day, int prior199Day) {
        int date52WeekBefore = dateID - 10000;
        StringBuilder sb = new StringBuilder("insert into EODSTATISTICS (dateid, stockid, high5d, high20d, high52w, highlifetime, low5d, low20d, low52w, lowlifetime, movavg10d, movavg50d, movavg200d) ");
        sb.append("values(").append(dateID).append(" , ").append(stockId).append(" , ");
        sb.append("(select max(adjustedclose) from quote where dateid <= ").append(dateID).append(" and dateid >= ").append(prior4Day).append("and ").append("STOCKID = ").append(stockId).append(" ),");
        sb.append("(select max(adjustedclose) from quote where dateid <= ").append(dateID).append(" and dateid >= ").append(prior19Day).append("and ").append("STOCKID = ").append(stockId).append(" ),");
        sb.append("(select max(adjustedclose) from quote where dateid <= ").append(dateID).append(" and dateid > ").append(date52WeekBefore).append("and ").append("STOCKID = ").append(stockId).append(" ),");
        sb.append("(select max(adjustedclose) from quote where dateid <= ").append(dateID).append("and ").append("STOCKID = ").append(stockId).append(" ),");

        sb.append("(select min(adjustedclose) from quote where dateid <= ").append(dateID).append(" and dateid >= ").append(prior4Day).append("and ").append("STOCKID = ").append(stockId).append(" ),");
        sb.append("(select min(adjustedclose) from quote where dateid <= ").append(dateID).append(" and dateid >= ").append(prior19Day).append("and ").append("STOCKID = ").append(stockId).append(" ),");
        sb.append("(select min(adjustedclose) from quote where dateid <= ").append(dateID).append(" and dateid > ").append(date52WeekBefore).append("and ").append("STOCKID = ").append(stockId).append(" ),");
        sb.append("(select min(adjustedclose) from quote where dateid <= ").append(dateID).append("and ").append("STOCKID = ").append(stockId).append(" ),");

        sb.append("(select sum(adjustedclose) / count(adjustedclose) from quote where dateid <= ").append(dateID).append(" and dateid >= ").append(prior9Day).append("and ").append("STOCKID = ").append(stockId).append(" ),");
        sb.append("(select sum(adjustedclose) / count(adjustedclose) from quote where dateid <= ").append(dateID).append(" and dateid >= ").append(prior49Day).append("and ").append("STOCKID = ").append(stockId).append(" ),");
        sb.append("(select sum(adjustedclose) / count(adjustedclose) from quote where dateid <= ").append(dateID).append(" and dateid >= ").append(prior199Day).append("and ").append("STOCKID = ").append(stockId).append(" )");
        sb.append(" )");
        return sb.toString();
    }

    static int findPriorDate(int dateID, int numberOfDays, Connection conn) throws SQLException {
        int retVal = dateID;
        StringBuilder sb = new StringBuilder("select dateval from MARKETDATE where dateval < ");
        sb.append(dateID);
        sb.append(" order by DATEVAL desc ");
        sb.append(" fetch next ").append(numberOfDays).append(" rows only");
        ResultSet resultSet = conn.createStatement().executeQuery(sb.toString());
        while (resultSet.next()) {
            retVal = resultSet.getInt(1);
        }
        resultSet.close();
        return retVal;
    }
}

