package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseDAO {

	Connection conn = null;

//	String url = "jdbc:mysql://localhost/pizzaria_michelangelo";
//	String user = "root";
//	String senha = "140180";
	
	String url = "jdbc:postgresql://localhost/pizzaria_michelangelo";
	String user = "postgres";
	String senha = "vaidarcerto";

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
