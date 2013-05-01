package me.criztovyl.blockreichtools.tools;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * An interface for saving classes to a MySQL Database
 * @author criztovyl
 *
 */
public interface DBSafe<T> {
	/**
	 * Save to Database
	 * @param save A Map with the values that should be saved
	 * @param con The Database Connection
	 */
	void saveToDatabase(T save, Connection con) throws SQLException;
	/**
	 * 
	 * @param con The Database Connection
	 * @return A List of Maps containing the saved values.
	 */
	ArrayList<T> loadFromDatabase(Connection con) throws SQLException;
}
