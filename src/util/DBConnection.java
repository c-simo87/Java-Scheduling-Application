package util;

import com.mysql.cj.protocol.Resultset;

import java.sql.*;

public class DBConnection {

    private static String protocol = "jdbc:";
    private static String vendorName = "mysql:";
    private static String ipAddress = "//localhost/";
    private static String dbName = "client_schedule";
    //the full url
    private static String jdbcURL = protocol + vendorName + ipAddress + dbName;
    //Driver reference
    private static String mysqlDriver = "com.mysql.jdbc.Driver";
    private static Connection conn = null;
    private static String userName = "sqlUser";
    private static String password = "Passw0rd!";

    private static Statement statement;
    private static PreparedStatement preparedStatement;

    public static Connection startConnection()
    {
        try {
            Class.forName(mysqlDriver);
            conn = DriverManager.getConnection(jdbcURL, userName, password);
            System.out.println("Connection Established");
        }
        catch(ClassNotFoundException | SQLException e){e.printStackTrace();}
        return conn;
    }
    public static void SetPreparedStatement(Connection conn, String sqlStatement) throws SQLException
    {
        preparedStatement = conn.prepareStatement(sqlStatement);
    }

    public static void SetConnection(Connection conn) throws SQLException
    {
        statement = conn.createStatement();
    }

    public static Connection GetConnection() {
        return conn;
    }

    public static PreparedStatement GetPreparedStatement()
    {
        return preparedStatement;
    }

    public static Statement GetStatement()
    {
        return statement;
    }

    public static void setStatement(Statement statement) {
        DBConnection.statement = statement;
    }
}
