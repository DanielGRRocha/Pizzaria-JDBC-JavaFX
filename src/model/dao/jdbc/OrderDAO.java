package model.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import model.dao.BaseDAO;
import model.dao.InterOrderDAO;
import model.vo.Order;
import model.vo.Pizza;
import model.vo.PizzaSize;

public class OrderDAO extends BaseDAO implements InterOrderDAO<Order> {

	@Override
	public void insert(Order obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Order obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Order findById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> findByClientId(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> findByPizzaSizeId(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> findByOrderStatusId(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> findByPizzaId(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}
	
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
		pizzaSize.setId(rs.getInt("pizza_size_id"));
		pizzaSize.setName(rs.getString("SizeName"));
		return pizzaSize;
	}

}
