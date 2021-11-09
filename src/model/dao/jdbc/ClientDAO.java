package model.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.BaseDAO;
import model.dao.InterClientDAO;
import model.vo.Client;

public class ClientDAO extends BaseDAO implements InterClientDAO<Client> {

	@Override
	public void insert(Client obj) {
		String SQL = "insert into tb_client (name,cpf,phone,address) values (?,?,?,?)";
		PreparedStatement pstm;
		try {
			pstm = getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
			pstm.setString(1, obj.getName());
			pstm.setString(2, obj.getCpf());
			pstm.setString(3, obj.getPhone());
			pstm.setString(4, obj.getAddress());
			
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

	@Override
	public void update(Client obj) {
		String SQL = "update tb_client set name = ?, cpf = ?, phone = ?, address = ? where id= ?";
		PreparedStatement pstm;
		try {
			pstm = getConnection().prepareStatement(SQL);
			pstm.setString(1, obj.getName());
			pstm.setString(2, obj.getCpf());
			pstm.setString(3, obj.getPhone());
			pstm.setString(4, obj.getAddress());
			pstm.setInt(5, obj.getId());
			pstm.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void deleteById(Integer id) {
		String SQL = "delete from tb_client where id = ?";
		PreparedStatement pstm;
		try {
			pstm = getConnection().prepareStatement(SQL);
			pstm.setInt(1, id);
			pstm.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Client findById(Integer id) {
		String SQL = "SELECT * FROM tb_client WHERE id = ?";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			pst = getConnection().prepareStatement(SQL);

			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				Client obj = new Client();
				obj.setId(rs.getInt("id"));
				obj.setName(rs.getString("name"));
				obj.setCpf(rs.getString("cpf"));
				obj.setPhone(rs.getString("phone"));
				obj.setAddress(rs.getString("address"));
				return obj;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
			DB.closeResultSet(rs);
		}
	}

//	@Override
//	public ResultSet findAll() {
//		String SQL = "select * from tb_client";
//		Statement st;
//		ResultSet rs = null;
//				
// 		try {
//			st = getConnection().createStatement();
//			rs = st.executeQuery(SQL);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return rs;
//	}

	@Override
	public ResultSet findByName(Client obj) {
		String SQL = "select * from tb_client where cpf = ?";
		PreparedStatement pstm;
		ResultSet rs = null;
				
 		try {
			pstm = getConnection().prepareStatement(SQL);
			pstm.setString(1, obj.getCpf());
			rs = pstm.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}

	@Override
	public ResultSet findByCPF(Client obj) {
		String SQL = "select * from tb_client where cpf = ?";
		PreparedStatement pstm;
		ResultSet rs = null;
				
 		try {
			pstm = getConnection().prepareStatement(SQL);
			pstm.setString(1, obj.getCpf());
			rs = pstm.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	@Override
	public List<Client> findAll() {
		
		String SQL = "SELECT * FROM tb_client";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);

			rs = pst.executeQuery();

			List<Client> list = new ArrayList<>();

			while (rs.next()) {
				Client obj = new Client();
				obj.setId(rs.getInt("id"));
				obj.setName(rs.getString("name"));
				obj.setCpf(rs.getString("cpf"));
				obj.setPhone(rs.getString("phone"));
				obj.setAddress(rs.getString("address"));
				

				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
			DB.closeResultSet(rs);
		}
	}
}
