package pm.dao.derby.function;

import java.sql.*;

/**
 * Date: Sep 11, 2006
 * Time: 8:43:15 PM
 */
public class DividentFunction {

    public static void resetDivident(int stockID) throws SQLException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:default:connection");
            try {
                conn = DriverManager.getConnection("jdbc:default:connection");
                conn.createStatement().executeUpdate("UPDATE T_HOLDING SET DIVIDENT = CALCULATEDIVIDENTFORHOLDING(BUYID, HOLDINGQTY) " +
                        "WHERE BUYID IN (SELECT ID FROM BUYTRANSACTION WHERE STOCKID = " + stockID + ") AND HOLDINGQTY > 0");
                conn.createStatement().executeUpdate("UPDATE TRADE SET DIVIDENT = CALCULATEDIVIDENTFORTRADE(BUYID, SELLID, QTY)  " +
                        "WHERE BUYID IN (SELECT ID FROM BUYTRANSACTION WHERE STOCKID = " + stockID + ")");
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static double getDivident(int stockID, int buyDate, int sellDate, int qty, int dayTrade) throws SQLException {
        Connection conn = null;
        float divident = 0;
        try {
            conn = DriverManager.getConnection("jdbc:default:connection");
            divident = calculateDivident(conn, stockID, buyDate, sellDate, qty, dayTrade, 0);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return divident;
    }

    public static double getTradedQty(int buyId) throws SQLException {
        Connection conn = null;
        float qty = 0;
        try {
            conn = DriverManager.getConnection("jdbc:default:connection");
            Statement stmt = conn.createStatement();
            ResultSet rsFaceValue = stmt.executeQuery("SELECT SUM(QTY) FROM TRADE WHERE BUYID = " + buyId);
            if (rsFaceValue.next()) {
                qty = rsFaceValue.getFloat(1);
            }
            rsFaceValue.close();
            stmt.close();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return qty;
    }

    public static double getHoldingQty(int buyId) throws SQLException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:default:connection");
            return getHoldingQty(buyId, conn);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    static float getHoldingQty(int buyId, Connection conn) throws SQLException {
        float tradedQty = 0;
        float buyQty = 0;

        Statement stmt = conn.createStatement();
        ResultSet rsFaceValue = stmt.executeQuery("SELECT SUM(QTY) FROM TRADE WHERE BUYID = " + buyId);
        if (rsFaceValue.next()) {
            tradedQty = rsFaceValue.getFloat(1);
        }
        rsFaceValue.close();
        stmt.close();

        stmt = conn.createStatement();
        rsFaceValue = stmt.executeQuery("SELECT QTY FROM BUYTRANSACTION WHERE ID = " + buyId);
        if (rsFaceValue.next()) {
            buyQty = rsFaceValue.getFloat(1);
        }
        rsFaceValue.close();
        stmt.close();
        return buyQty - tradedQty;
    }

    public static double getDividentForHolding(int buyID, int qty) throws SQLException {
        Connection conn = null;
        float divident = 0;
        try {
            conn = DriverManager.getConnection("jdbc:default:connection");
            Statement stmt1 = conn.createStatement();
            ResultSet rs = stmt1.executeQuery("SELECT STOCKID, TDATE, DELIVERYTYPE FROM BUYTRANSACTION WHERE ID = " + buyID);
            int stockID = 0;
            int buyDate = 0;
            int dayTrade = 0;
            if (rs.next()) {
                stockID = rs.getInt(1);
                buyDate = rs.getInt(2);
                dayTrade = rs.getInt(3);
            }
            rs.close();
            stmt1.close();
            divident = calculateDivident(conn, stockID, buyDate, 0, qty, dayTrade, 0);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return divident;
    }

    public static double getDividentForTrade(int buyID, int sellID, int qty) throws SQLException {
        Connection conn = null;
        float divident = 0;
        try {
            conn = DriverManager.getConnection("jdbc:default:connection");
            Statement stmt1 = conn.createStatement();
            ResultSet rs = stmt1.executeQuery("SELECT STOCKID, TDATE, DELIVERYTYPE FROM BUYTRANSACTION WHERE ID = " + buyID);
            int stockID = 0;
            int buyDate = 0;
            int dayTrade = 0;
            if (rs.next()) {
                stockID = rs.getInt(1);
                buyDate = rs.getInt(2);
                dayTrade = rs.getInt(3);
            }
            rs.close();
            stmt1.close();
            stmt1 = conn.createStatement();
            rs = stmt1.executeQuery("SELECT TDATE FROM SELLTRANSACTION WHERE ID = " + sellID);
            int sellDate = 0;
            if (rs.next()) {
                sellDate = rs.getInt(1);
            }
            rs.close();
            stmt1.close();
            divident = calculateDivident(conn, stockID, buyDate, sellDate, qty, dayTrade, 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return divident;
    }

    public static double getDividentForFinancialYear(int stockID, int buyDate, int sellDate, int qty, int dayTrade, int finYear) throws SQLException {
        Connection conn = null;
        float divident = 0;
        try {
            conn = DriverManager.getConnection("jdbc:default:connection");
            divident = calculateDivident(conn, stockID, buyDate, sellDate, qty, dayTrade, finYear);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return divident;
    }

    public static float calculateDivident(Connection conn, int stockID, int buyDate, int sellDate, int qty, int dayTrade, int financialYear) throws SQLException {
        if (dayTrade == 1) {
            return 0;
        }

        if (financialYear != 0) {
            int finStDate = financialYear * 10000 + 401;
            int nextFinStDate = (financialYear + 1) * 10000 + 401;
            if (buyDate < finStDate) {
                buyDate = finStDate;
            }
            if (sellDate == 0) {
                sellDate = nextFinStDate;
            }
        }
        float divident = 0f;
        float faceValue = 0;
        Statement stmt1 = conn.createStatement();
        ResultSet rsFaceValue = stmt1.executeQuery("SELECT FACEVALUE FROM STOCKMASTER WHERE ID = " + stockID);
        if (rsFaceValue.next()) {
            faceValue = rsFaceValue.getFloat(1);
        }
        rsFaceValue.close();
        stmt1.close();

        if (sellDate != 0) {
            Statement stmt3 = conn.createStatement();
            rsFaceValue = stmt3.executeQuery("SELECT OLDFACEVALUE FROM CA_SPLIT WHERE STOCKID = "
                    + stockID + " AND EXDATE > " + sellDate + " ORDER BY EXDATE ASC");
            if (rsFaceValue.next()) {
                faceValue = rsFaceValue.getFloat(1);
            }
            rsFaceValue.close();
            stmt3.close();
        }


        Statement stmt2 = conn.createStatement();
        ResultSet resultSet = stmt2.executeQuery("select DIVIDENT,ISPERCENTAGE,EXDATE from CA_DIVIDENT where STOCKID=" +
                stockID + " AND EXDATE > " + buyDate + " AND (" + sellDate + "= 0 OR EXDATE <= " + sellDate + ")");
        while (resultSet.next()) {
            float divValue = resultSet.getFloat(1);
            boolean isPer = resultSet.getBoolean(2);
            if (isPer) {
                try {
                    divValue = faceValue / 100 * divValue;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            divident += divValue * qty;
        }
        resultSet.close();
        return divident;
    }
}