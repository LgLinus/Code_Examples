import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

/**
 * Methods that can retrieve information from an sql database.
 * @author LgLinuss
 *
 */
public class DBMethods {

	/**
	 * Retrieve all the elements in the table and display them
	 * 
	 * @param res
	 * @return
	 * @throws SQLException
	 */
	public static String[] getHeaders(ResultSet res) throws SQLException {
		ResultSetMetaData rsmd = res.getMetaData();
		String[] string = new String[rsmd.getColumnCount()];
		for (int i = 0; i < string.length; i++) {
			string[i] = rsmd.getColumnLabel(i + 1);
		}

		return string;
	}

	/**
	 * Retrieve the object types from an sql table
	 * @param rs resultset
	 * @return res[][] return the array of objects
	 * @throws SQLException
	 */
	public static Object[][] getContent(ResultSet rs) throws SQLException {

		ResultSetMetaData rsmd = rs.getMetaData();
		rs.last();
		int rows = rs.getRow();
		int cols = rsmd.getColumnCount();
		Object[][] data = new Object[rows][cols];
		for (int row = 0; row < rows; row++) { // Loop through all rows
			rs.absolute(row + 1); // Move to correct row in result
			for (int col = 0; col < cols; col++) { // Loop through all columns
													// in the current row
				data[row][col] = rs.getObject(col + 1); // Set the object in
														// this row and column
														// to the correct object
			}
		}
		return data;

	}

	/**
	 * Set the width of each column in the JTable
	 * @param table
	 * @param colWidth
	 */
	public static void setColumnWidth(JTable table, int[] colWidth) {
		TableColumnModel columnModel = table.getColumnModel();
		int count = Math.min(table.getColumnCount(), colWidth.length);
		for (int i = 0; i < count; i++) {
			columnModel.getColumn(i).setPreferredWidth(colWidth[i]);
		}

	}
	
	/**
	 * Write to a database 
	 * @param connection to database
	 * @param sql format
	 * @param data string array with the data to be written
	 * @throws SQLException
	 */
	public static void writeData(Connection connection, String sql, String[] data) throws SQLException{
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for(int i = 1; i <= data.length;i++){
        	preparedStatement.setString(i, data[i-1]);
        }
        preparedStatement.executeUpdate(); 
	}
}
