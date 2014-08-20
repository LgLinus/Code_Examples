import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Controller handling the information retrieved and sent from the server to the
 * pi
 * 
 * @author Linus Granath
 * 
 */
public class Controller {
	private Connection connection;
	public static String dataBaseName = null;
	private static String sql = "INSERT INTO " + dataBaseName
			+ " (id,name,score)" + "VALUES (?, ?, ?)";
	private int comparePos = 2;

	private Object[][] retrieved = new Object[0][0];
	private Integer size;
	private ResultSet result = null;

	/** Controller of the database server */
	public Controller() {
		try {
			connection = MysqlDB.returnConnect();
			new ServerCommunication(this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the objects in a given table as a string array
	 * 
	 * @return res string array of the objects in the table
	 */
	public String[] retreiveData() {
		try {
			result = connection.createStatement().executeQuery(
					"SELECT * FROM " + Controller.dataBaseName);
			System.out.println("SELECT FROM " + Controller.dataBaseName);
			retrieved = DBMethods.getContent(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] res = new String[retrieved.length];
		for (int i = 0; i < retrieved.length; i++) {
			res[i] = "";
			for (int j = 0; j < retrieved[i].length; j++)
				res[i] += retrieved[i][j].toString() + "\t";
		}
		return res;
	}

	public static void main(String[] args) {
		new Controller();
	}

	/**
	 * Retrieves the highscore and orders the object in the correct order.
	 */
	public void orderObjects() {
		retreiveData();
		int[] values = new int[retrieved.length];
		for (int i = 0; i < retrieved.length; i++) {
			values[i] = (Integer) retrieved[i][comparePos];
		}
		for (int j = 0; j < retrieved.length; j++) {
			for (int i = 0; i < retrieved.length; i++) {
				if (i != 0)
					if (values[i - 1] < values[i]) {
						int tempInt = values[i - 1];
						values[i - 1] = values[i];
						values[i] = tempInt;
						Object[] temporary = new Object[retrieved.length];
						temporary = retrieved[i];
						retrieved[i] = retrieved[i - 1];
						retrieved[i - 1] = temporary;
					}
			}
		}
		for (int i = 0; i < retrieved.length; i++) {
			retrieved[i][0] = i + 1;
		}
		for (int i = 0; i < retrieved.length; i++) {
			for (int j = 0; j < retrieved[i].length; j++)
				System.out.print(retrieved[i][j].toString() + " ");
			System.out.println();
		}

		deleteTable();
		try {
			reAddTable();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Delete the table
	private void deleteTable() {
		String query = "DELETE FROM " + Controller.dataBaseName;
		int deletedRows = 0;
		try {
			deletedRows = connection.createStatement().executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (deletedRows > 0) {
			System.out.println("Deleted All Rows In The Table Successfully...");
		} else {
			System.out.println("Table already empty.");
		}
	}

	/**
	 * Called when the score should be updated
	 * 
	 * @param info
	 */
	public void receivedInfo(String info) {
		String[] split = info.split("-");
		System.out.println("RECEIVEDINFO");
		Controller.sql = split[0];
		this.comparePos = Integer.valueOf(split[1]); // Compare position
		this.size = Integer.valueOf(split[2]);
		try {
			putInHighScore(split);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Set the name of the database
	 */
	public void setDataBaseName(String inputLine) {
		String[] split = inputLine.split("-");
		Controller.dataBaseName = split[1];
		System.out.println(Controller.dataBaseName);
		inputLine = inputLine.replaceAll("-", "");
		createTable(inputLine);
		retreiveData(); // Get the objects of the table
	}

	// Rewrite the elements to the table
	private void reAddTable() throws SQLException {
		for (int i = 0; i < retrieved.length; i++) {
			String[] data = new String[retrieved[i].length];
			for (int j = 0; j < retrieved[i].length; j++) {
				data[j] = String.valueOf(retrieved[i][j]);
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DBMethods.writeData(connection, sql, data);
		}
	}
	// Method to put in a value in highscore
	private void putInHighScore(String[] info) throws SQLException {
		int value1 = Integer.valueOf((String) info[info.length - 1]);
		if (retrieved.length >= size) {
			for (int i = 0; i < retrieved.length; i++) {
				int value2 = (Integer) retrieved[i][comparePos];
				Object[] temp = retrieved[i].clone();
				if (i == 0) {
					if (value1 > value2) {
						// 2014-05-09 15:47 Flyttar ner alla element i listan,
						// sätter det sista elementet som null(försvinner)
						moveDownInList(0);
						int a = 0;
						for (int j = 2; j < info.length; j++) {
							retrieved[0][a] = info[j];
							a++;
						}
						retrieved[0][0] = "1";
						retrieved[1] = temp;
						int y = (Integer) retrieved[1][0];
						y++;
						retrieved[1][0] = y;
						break;
					}

				} else {
					if (value1 > value2) {

						// 2014-05-09 15:47 Flyttar ner alla element i listan,
						// sätter det sista elementet som null(försvinner)
						moveDownInList(i);

						int a = 0;
						for (int j = 2; j < info.length; j++) {
							retrieved[i][a] = info[j];
							a++;
						}
						retrieved[i][0] = i + 1;
						System.out.print(i);
						if (i >= retrieved.length - 1) {

						} else {
							retrieved[i + 1] = temp;
							int y = (Integer) retrieved[i + 1][0];
							y++;
							retrieved[i + 1][0] = y;
						}
						break;
					}
				}
			}
		} else {
			System.out.print("INSERT NEW");
			result = connection.createStatement().getResultSet();
			ResultSetMetaData rsmd = result.getMetaData();
			Object[][] insert = new Object[retrieved.length + 1][rsmd
					.getColumnCount()];
			for (int i = 0; i < retrieved.length; i++) {
				insert[i] = retrieved[i];
			}
			insert[insert.length - 1][0] = insert.length;
			for (int i = 1; i < insert[0].length; i++) {
				insert[insert.length - 1][i] = info[i + 2];
			}
			retrieved = insert;
		}
		try {
			deleteTable();
			reAddTable();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < retrieved.length; i++) {
			for (int j = 0; j < retrieved[i].length; j++)
				System.out.print(retrieved[i][j].toString() + " ");
			System.out.println();
		}

	}

	// Move down all the elements in the list
	private void moveDownInList(int pos) {
		for (int j = retrieved.length - 1; j > pos; j--) {
			retrieved[j] = retrieved[j - 1];
			int a = (Integer) retrieved[j][0];
			a++;
			retrieved[j][0] = a;
		}
	}

	// Called to create our table
	private void createTable(String input) {
		try {
			connection.createStatement().executeUpdate(input);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
