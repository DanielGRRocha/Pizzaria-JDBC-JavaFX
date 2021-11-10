package model.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.BaseDAO;
import model.dao.InterDAO;
import model.vo.Pizza;
import model.vo.PizzaSize;

public class PizzaDAO extends BaseDAO implements InterDAO<Pizza> {

	@Override
	public void insert(Pizza obj) {
		String SQL = "INSERT INTO tb_pizza (name, price, pizza_size_id) VALUES (?,?,?)";

		PreparedStatement pst = null;

		try {
			pst = getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

			pst.setString(1, obj.getName());
			pst.setDouble(2, obj.getPrice());
			pst.setInt(3, obj.getPizzaSize().getId());

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
		String SQL = "UPDATE tb_pizza SET name = ?, price = ?, pizza_size_id = ? WHERE id = ?";

		PreparedStatement pst = null;

		try {
			pst = getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

			pst.setString(1, obj.getName());
			pst.setDouble(2, obj.getPrice());
			pst.setInt(3, obj.getPizzaSize().getId());

			pst.setInt(4, obj.getId());

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
		String SQL = "SELECT tb_pizza.*,tb_pizza_size.Name as SizeName FROM tb_pizza"
				+ " INNER JOIN tb_pizza_size ON tb_pizza.pizza_size_id = tb_pizza_size.Id" + " WHERE tb_pizza.Id = ?";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);

			pst.setInt(1, id);
			rs = pst.executeQuery();

			if (rs.next()) {
				PizzaSize pizzaSize = instantiatePizzaSize(rs);

				Pizza obj = instantiatePizza(rs, pizzaSize);
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
		String SQL = "SELECT tb_pizza.*,tb_pizza_size.Name as SizeName FROM tb_pizza"
				+ " INNER JOIN tb_pizza_size ON tb_pizza.pizza_size_id = tb_pizza_size.Id order by id asc";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);

			rs = pst.executeQuery();

			List<Pizza> list = new ArrayList<>();
			Map<Integer, PizzaSize> map = new HashMap<>();

			while (rs.next()) {

				// verificar se o tamanho já existe

				PizzaSize pizzaSize = map.get(rs.getInt("pizza_size_id"));
				if (pizzaSize == null) {
					pizzaSize = instantiatePizzaSize(rs);
					map.put(rs.getInt("pizza_size_id"), pizzaSize);
				}

				Pizza obj = instantiatePizza(rs, pizzaSize);
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

	public List<Pizza> findByPizzaSize(PizzaSize size) {

		String SQL = "SELECT tb_pizza.*,tb_pizza_size.Name as SizeName FROM tb_pizza"
				+ " INNER JOIN tb_pizza_size ON tb_pizza.pizza_size_id = tb_pizza_size.Id"
				+ " WHERE pizza_size_id = ? ORDER BY Name";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);

			pst.setInt(1, size.getId());
			rs = pst.executeQuery();


			List<Pizza> list = new ArrayList<>();
			Map<Integer, PizzaSize> map = new HashMap<>();

			while (rs.next()) {

				// verificar se já existe

				PizzaSize pizzaSize = map.get(rs.getInt("DepartmentId"));
				if (pizzaSize == null) {
					pizzaSize = instantiatePizzaSize(rs);
					map.put(rs.getInt("DepartmentId"), pizzaSize);
				}

				Pizza obj = instantiatePizza(rs, pizzaSize);
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
			DB.closeResultSet(rs);
		}
	}// metodo

	private Pizza instantiatePizza(ResultSet rs, PizzaSize pizzaSize) throws SQLException {
		Pizza obj = new Pizza();
		obj.setId(rs.getInt("id"));
		obj.setName(rs.getString("name"));
		obj.setPrice(rs.getDouble("price"));
		obj.setPizzaSize(pizzaSize);

		return obj;
	}

	private PizzaSize instantiatePizzaSize(ResultSet rs) throws SQLException {
		PizzaSize pizzaSize = new PizzaSize();
		pizzaSize.setId(rs.getInt("id"));
		pizzaSize.setName(rs.getString("name"));
		return pizzaSize;
	}

}
