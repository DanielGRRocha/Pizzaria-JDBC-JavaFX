package model.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.BaseDAO;
import model.dao.InterDAO;
import model.vo.Additional;

public class AdditionalDAO extends BaseDAO implements InterDAO<Additional> {

	@Override
	public void insert(Additional obj) {
		String SQL = "INSERT INTO tb_additional (name, price) VALUES (?,?)";

		PreparedStatement pst = null;

		try {
			pst = getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

			pst.setString(1, obj.getName());
			pst.setDouble(2, obj.getPrice());

			int rowsAffected = pst.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = pst.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
		}

	}

	@Override
	public void update(Additional obj) {
		String SQL = "UPDATE tb_additional SET name =  ?, price = ? WHERE id = ?";

		PreparedStatement pst = null;

		try {
			pst = getConnection().prepareStatement(SQL);
			pst.setString(1, obj.getName());
			pst.setDouble(2, obj.getPrice());
			pst.setInt(3, obj.getId());

			pst.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
		}

	}

	@Override
	public void deleteById(Integer id) {
		String SQL = "DELETE FROM tb_additional WHERE id = ?";

		PreparedStatement pst = null;
		try {
			pst = getConnection().prepareStatement(SQL);

			pst.setInt(1, id);

			pst.executeUpdate();
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
		}

	}

	@Override
	public Additional findById(Integer id) {
		String SQL = "SELECT * FROM tb_additional WHERE id = ?";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			pst = getConnection().prepareStatement(SQL);

			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				Additional obj = new Additional();
				obj.setId(rs.getInt("id"));
				obj.setName(rs.getString("name"));
				obj.setPrice(rs.getDouble("price"));
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

	@Override
	public List<Additional> findAll() {
		String SQL = "SELECT * FROM tb_additional";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);

			rs = pst.executeQuery();

			List<Additional> list = new ArrayList<>();

			while (rs.next()) {
				Additional obj = new Additional();
				obj.setId(rs.getInt("id"));
				obj.setName(rs.getString("name"));
				obj.setPrice(rs.getDouble("price"));

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
