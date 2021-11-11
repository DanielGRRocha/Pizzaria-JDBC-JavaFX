package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseDAO {

	Connection conn = null;

	String url = "jdbc:mysql://localhost/pizzaria_michelangelo?useTimezone=true&serverTimezone=UTC";
	String user = "root";
	String senha = "140180";

	public Connection getConnection() {

		if (conn == null) {
			try {
				conn = DriverManager.getConnection(url, user, senha);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return conn;

		} else
			return conn;

	}

}
