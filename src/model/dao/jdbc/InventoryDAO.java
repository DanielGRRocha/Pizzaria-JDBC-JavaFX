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
import model.dao.InterDAO;
import model.vo.Inventory;

public class InventoryDAO extends BaseDAO implements InterDAO<Inventory> {

	@Override
	public void insert(Inventory obj) {
		String SQL = "insert into tb_inventory (name,quantity) values (?,?)";
		PreparedStatement pstm;
		try {
			pstm = getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
			pstm.setString(1, obj.getName());
			pstm.setInt(2, obj.getQuantity());
			
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
	public void update(Inventory obj) {
		String SQL = "update tb_inventory set name = ?, quantity = ? where id= ?";
		PreparedStatement pstm;
		try {
			pstm = getConnection().prepareStatement(SQL);
			pstm.setString(1, obj.getName());
			pstm.setInt(2, obj.getQuantity());
			pstm.setInt(3, obj.getId());
			pstm.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void deleteById(int id) {
		String SQL = "delete from tb_inventory where id = ?";
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
	public ResultSet findById(int id) {
		String SQL = "select * from tb_inventory where id=?";
		PreparedStatement pstm;
		ResultSet rs = null;
				
 		try {
			pstm = getConnection().prepareStatement(SQL);
			pstm.setInt(1, id);
			System.out.println(pstm);
			rs = pstm.executeQuery();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}

	@Override
	public List<Inventory> findAll() {
		String SQL = "SELECT * FROM tb_inventory";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);

			rs = pst.executeQuery();

			List<Inventory> list = new ArrayList<>();

			while (rs.next()) {
				Inventory obj = new Inventory();
				obj.setId(rs.getInt("id"));
				obj.setName(rs.getString("name"));
				obj.setQuantity(rs.getInt("quantity"));
				
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
