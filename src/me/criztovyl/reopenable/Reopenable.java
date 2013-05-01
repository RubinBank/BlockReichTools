package me.criztovyl.reopenable;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * A Reopenable MySQL Connection
 * @author criztovyl
 *
 */
public interface Reopenable {
    /**
     * @return the Database Connection
     * @throws SQLException
     */
    public Connection getDatabaseConnection() throws SQLException;
}
