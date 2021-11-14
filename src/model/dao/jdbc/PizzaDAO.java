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
import model.vo.Pizza;

public class PizzaDAO extends BaseDAO implements InterDAO<Pizza> {

	@Override
	public void insert(Pizza obj) {
		String SQL = "INSERT INTO tb_pizza (name, price_small_pizza, price_medium_pizza, price_big_pizza) VALUES (?,?,?,?)";

		PreparedStatement pst = null;

		try {
			pst = getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

			pst.setString(1, obj.getName());
			pst.setDouble(2, obj.getPriceSmallPizza());
			pst.setDouble(3, obj.getPriceMediumPizza());
			pst.setDouble(4, obj.getPriceBigPizza());

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
	public void update(Pizza obj) {
		String SQL = "UPDATE tb_pizza SET name = ?, price_small_pizza = ?, price_medium_pizza = ?, price_big_pizza = ? WHERE id = ?";

		PreparedStatement pst = null;

		try {
			pst = getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

			pst.setString(1, obj.getName());
			pst.setDouble(2, obj.getPriceSmallPizza());
			pst.setDouble(3, obj.getPriceMediumPizza());
			pst.setDouble(4, obj.getPriceBigPizza());

			pst.setInt(5, obj.getId());

			pst.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
		}

	}

	@Override
	public void deleteById(Integer id) {
		String SQL = "DELETE FROM tb_pizza WHERE id = ?";

		PreparedStatement pst = null;

		try {
			pst = getConnection().prepareStatement(SQL);

			pst.setInt(1, id);
			pst.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
		}

	}

	@Override
	public Pizza findById(Integer id) {
		String SQL = "SELECT * FROM tb_pizza WHERE id = ?";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			pst = getConnection().prepareStatement(SQL);

			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				Pizza obj = new Pizza();
				obj.setId(rs.getInt("id"));
				obj.setName(rs.getString("name"));
				obj.setPriceSmallPizza(rs.getDouble("price_small_pizza"));
				obj.setPriceMediumPizza(rs.getDouble("price_medium_pizza"));
				obj.setPriceBigPizza(rs.getDouble("price_big_pizza"));

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
	public List<Pizza> findAll() {
		String SQL = "SELECT * FROM tb_pizza";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);

			rs = pst.executeQuery();

			List<Pizza> list = new ArrayList<>();

			while (rs.next()) {
				Pizza obj = new Pizza();
				obj.setId(rs.getInt("id"));
				obj.setName(rs.getString("name"));
				obj.setPriceSmallPizza(rs.getDouble("price_small_pizza"));
				obj.setPriceMediumPizza(rs.getDouble("price_medium_pizza"));
				obj.setPriceBigPizza(rs.getDouble("price_big_pizza"));

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
