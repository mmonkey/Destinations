package com.github.mmonkey.Destinations.Dams;

import com.github.mmonkey.Destinations.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestConnectionDam {

	public static final String tblName = "connection_tests";

	private DatabaseConnection database;

	public boolean testConnection() {

		Connection connection = null;
		Statement statement = null;
		String sql =  "CREATE TABLE IF NOT EXISTS " + tblName
			+ "(id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, test_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP);"
			+ "INSERT INTO " + tblName + " DEFAULT VALUES;";

		try {

			connection = database.getConnection();
			statement = connection.createStatement();
			statement.execute(sql);
			return true;

		} catch (SQLException e) {

			e.printStackTrace();
			return false;

		} finally {

			try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

		}

	}

	public TestConnectionDam(DatabaseConnection database) {
        this.database = database;
	}

}
