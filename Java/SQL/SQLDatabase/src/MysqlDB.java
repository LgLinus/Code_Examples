
import java.sql.*;

/**
 * Class responsible of establishing a connection to an sql database
 * @author Linus Granath
 *
 */
public class MysqlDB {
    public static Connection connection;
    public static Statement statement;
    private static String userName="username", password = "password";
    
    public static void showResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData meta = resultSet.getMetaData();
        String res = "";
        int colCount = meta.getColumnCount();
        for(int i=1; i<=colCount; i++)
            res += meta.getColumnLabel(i) + ", ";
        res += "\n";
        while(resultSet.next()) {
            for(int i=1; i<=colCount; i++)
                res += resultSet.getObject(i).toString() + ", ";
            res += "\n";
        }
        System.out.println(res);
    }
    
    public static void connect() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://195.178.232.7:4040/ad2302",userName,password);
            statement = connection.createStatement();
        } catch(ClassNotFoundException e1) {
            System.out.println("Databas-driver hittades ej: "+e1);
        }
    }
    
    public static Connection returnConnect() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://195.178.232.7:4040/ad2302",userName,password);
            return connection;
        } catch(ClassNotFoundException e1) {
            System.out.println("Databas-driver hittades ej: "+e1);
        }
        return null;
    }
    
    public static void disconnect() throws SQLException {
        statement.close();
        connection.close();
    }
    
}