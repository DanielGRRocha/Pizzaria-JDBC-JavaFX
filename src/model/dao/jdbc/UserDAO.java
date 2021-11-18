package model.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.dao.BaseDAO;
import model.vo.User;

public class UserDAO extends BaseDAO {
	
	public void insert(User obj) {
		String SQL = "insert into tb_user (username,password) values (?,?)";
		PreparedStatement pstm;
		try {
			pstm = getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
			pstm.setString(1, obj.getUsername());
			pstm.setString(2, obj.getPassword());
			
	        int affectedRows = pstm.executeUpdate();

	        if (affectedRows == 0) {
	            throw new SQLException("A inserção falhou. Nenhuma linha foi alterada.");
	        }
	        ResultSet generatedKeys = pstm.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            obj.setId(generatedKeys.getInt(1));
	        }
	        else {
	           throw new SQLException("A inserção falhou. Nenhum id foi retornado.");
	        }
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultSet findByUsername(User user) {

		String sql = "SELECT * FROM tb_user WHERE username = ?";
		PreparedStatement ptst;
		ResultSet resposta = null;

		try {

			ptst = getConnection().prepareStatement(sql);
			ptst.setString(1, user.getUsername());
			resposta = ptst.executeQuery();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return resposta;
	}

}
