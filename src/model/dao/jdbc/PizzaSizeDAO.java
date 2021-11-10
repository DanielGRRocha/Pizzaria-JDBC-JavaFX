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
import model.vo.PizzaSize;

public class PizzaSizeDAO extends BaseDAO implements InterDAO<PizzaSize> {

	@Override
	public void insert(PizzaSize obj) {
		String SQL = "INSERT INTO tb_pizza_size (name) VALUES (?)";

		PreparedStatement pst = null;

		try {
			pst = getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

			pst.setString(1, obj.getName());

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
	public void update(PizzaSize obj) {
		String SQL = "UPDATE tb_pizza_size SET name =  ? WHERE id = ?";

		PreparedStatement pst = null;

		try {
			pst = getConnection().prepareStatement(SQL);
			pst.setString(1, obj.getName());
			pst.setInt(2, obj.getId());

			pst.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
		}

	}

	@Override
	public void deleteById(Integer id) {
		String SQL = "DELETE FROM tb_pizza_size WHERE Id = ?";

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
	public PizzaSize findById(Integer id) {
		String SQL = "SELECT * FROM tb_pizza_size WHERE Id = ?";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			pst = getConnection().prepareStatement(SQL);

			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				PizzaSize obj = new PizzaSize();
				obj.setId(rs.getInt("Id"));
				obj.setName(rs.getString("Name"));
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
	public List<PizzaSize> findAll() {
		String SQL = "SELECT * FROM tb_pizza_size";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);

			rs = pst.executeQuery();

			List<PizzaSize> list = new ArrayList<>();

			while (rs.next()) {
				PizzaSize obj = new PizzaSize();
				obj.setId(rs.getInt("id"));
				obj.setName(rs.getString("name"));

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
