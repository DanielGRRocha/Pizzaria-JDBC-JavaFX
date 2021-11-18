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
import model.dao.InterOrderDAO;
import model.vo.Additional;
import model.vo.Client;
import model.vo.Order;
import model.vo.OrderStatus;
import model.vo.Pizza;
import model.vo.PizzaSize;

public class OrderDAO extends BaseDAO implements InterOrderDAO<Order> {

	@Override
	public void insert(Order obj) {
		String SQL = "INSERT INTO tb_order (client_id, pizza_id, pizza_size_id, additional_id, order_status_id, moment, total) VALUES (?,?,?,?,?,?,?)";

		PreparedStatement pst = null;

		try {
			pst = getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

			pst.setInt(1, obj.getClient().getId());
			pst.setInt(2, obj.getPizza().getId());
			pst.setInt(3, obj.getPizzaSize().getId());
			pst.setInt(4, obj.getAdditional().getId());
			pst.setInt(5, obj.getOrderStatus().getId());
			pst.setDate(6, new java.sql.Date(obj.getMoment().getTime()));
			pst.setDouble(7, obj.getAdditional().getPrice() + obj.getPizza().getPrice(obj.getPizzaSize().getId()));

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
	public void update(Order obj) {
		String SQL = "UPDATE tb_order SET client_id = ?, pizza_id = ?, pizza_size_id = ?, additional_id = ?, order_status_id = ?, moment = ?, total = ? WHERE id = ?";

		PreparedStatement pst = null;

		try {
			pst = getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

			pst.setInt(1, obj.getClient().getId());
			pst.setInt(2, obj.getPizza().getId());
			pst.setInt(3, obj.getPizzaSize().getId());
			pst.setInt(4, obj.getAdditional().getId());
			pst.setInt(5, obj.getOrderStatus().getId());
			pst.setDate(6, new java.sql.Date(obj.getMoment().getTime()));
			pst.setDouble(7, obj.getAdditional().getPrice() + obj.getPizza().getPrice(obj.getPizzaSize().getId()));
			

			pst.setInt(8, obj.getId());

			pst.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		String SQL = "DELETE FROM tb_order WHERE id = ?";

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
	public Order findById(Integer id) {
		String SQL = "SELECT tb_order.*,"
				+ " tb_client.Name as ClientName,"
				+ " tb_pizza.Name as PizzaName,"
				+ " tb_pizza_size.Name as SizeName,"
				+ " tb_additional.Name as AdditionalName,"
				+ " tb_order_status.Name as StatusName"
				+ " FROM tb_order"
				+ " INNER JOIN tb_client ON (tb_order.client_id = tb_client.Id)"
				+ " INNER JOIN tb_pizza ON (tb_order.pizza_id = tb_pizza.Id)"
				+ " INNER JOIN  tb_pizza_size ON (tb_order.pizza_size_id = tb_pizza_size.Id)"
				+ " INNER JOIN tb_additional ON (tb_order.additional_id = tb_additional.Id)"
				+ " INNER JOIN tb_order_status ON (tb_order.order_status_id = tb_order_status.Id)"
				+ " WHERE tb_order.id = ? ORDER BY id ASC";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);
			
			pst.setInt(1,  id);
			rs = pst.executeQuery();

			if (rs.next()) {

				Client client = instantiateClient(rs);
				Pizza pizza = instantiatePizza(rs);
				PizzaSize pizzaSize = instantiatePizzaSize(rs);
				Additional additional = instantiateAdditional(rs);
				OrderStatus orderStatus = instantiateOrderStatus(rs);
				
				Order obj = instantiateOrder(rs, client, pizza, pizzaSize, additional, orderStatus);
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
	public List<Order> findAll() {
//		String SQL = "SELECT tb_order.*,"
//				+ " tb_client.Name as ClientName,"
//				+ " tb_pizza.Name as PizzaName,"
//				+ " tb_pizza_size.Name as SizeName,"
//				+ " tb_additional.Name as AdditionalName,"
//				+ " tb_order_status.Name as StatusName"
//				+ " FROM tb_order"
//				+ " INNER JOIN tb_client ON (tb_order.client_id = tb_client.Id)"
//				+ " INNER JOIN tb_pizza ON (tb_order.pizza_id = tb_pizza.Id)"
//				+ " INNER JOIN  tb_pizza_size ON (tb_order.pizza_size_id = tb_pizza_size.Id)"
//				+ " INNER JOIN tb_additional ON (tb_order.additional_id = tb_additional.Id)"
//				+ " INNER JOIN tb_order_status ON (tb_order.order_status_id = tb_order_status.Id) ORDER BY id asc";
		
		String newSQL = "SELECT * FROM public.view_order";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(newSQL);

			rs = pst.executeQuery();

			List<Order> list = new ArrayList<>();
			Map<Integer, Client> map1 = new HashMap<>();
			Map<Integer, Pizza> map2 = new HashMap<>();
			Map<Integer, PizzaSize> map3 = new HashMap<>();
			Map<Integer, Additional> map4 = new HashMap<>();
			Map<Integer, OrderStatus> map5 = new HashMap<>();

			while (rs.next()) {

				Client client = map1.get(rs.getInt("client_id"));
				Pizza pizza = map2.get(rs.getInt("pizza_id"));
				PizzaSize pizzaSize = map3.get(rs.getInt("pizza_size_id"));
				Additional additional = map4.get(rs.getInt("additional_id"));
				OrderStatus orderStatus = map5.get(rs.getInt("order_status_id"));
				if (client == null) {
					client = instantiateClient(rs);
					map1.put(rs.getInt("client_id"), client);
				}
				
				if (pizza == null) {
					pizza = instantiatePizza(rs);
					map2.put(rs.getInt("pizza_id"), pizza);
				}
				
				if (pizzaSize == null) {
					pizzaSize = instantiatePizzaSize(rs);
					map3.put(rs.getInt("pizza_size_id"), pizzaSize);
				}
				
				if (additional == null) {
					additional = instantiateAdditional(rs);
					map4.put(rs.getInt("additional_id"), additional);
				}
				
				if (orderStatus == null) {
					orderStatus = instantiateOrderStatus(rs);
					map5.put(rs.getInt("order_status_id"), orderStatus);
				}

				Order obj = instantiateOrder(rs, client, pizza, pizzaSize, additional, orderStatus);
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

	@Override
	public List<Order> findByClient(Client entity) {
		String SQL = "SELECT tb_order.*,"
				+ " tb_client.Name as ClientName,"
				+ " tb_pizza.Name as PizzaName,"
				+ " tb_pizza_size.Name as SizeName,"
				+ " tb_additional.Name as AdditionalName,"
				+ " tb_order_status.Name as StatusName"
				+ " FROM tb_order"
				+ " INNER JOIN tb_client ON (tb_order.client_id = tb_client.Id)"
				+ " INNER JOIN tb_pizza ON (tb_order.pizza_id = tb_pizza.Id)"
				+ " INNER JOIN  tb_pizza_size ON (tb_order.pizza_size_id = tb_pizza_size.Id)"
				+ " INNER JOIN tb_additional ON (tb_order.additional_id = tb_additional.Id)"
				+ " INNER JOIN tb_order_status ON (tb_order.order_status_id = tb_order_status.Id)"
				+ " WHERE client_id = ? ORDER BY Name";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);
			
			pst.setInt(1,  entity.getId());
			rs = pst.executeQuery();

			List<Order> list = new ArrayList<>();
			Map<Integer, Client> map1 = new HashMap<>();
			Map<Integer, Pizza> map2 = new HashMap<>();
			Map<Integer, PizzaSize> map3 = new HashMap<>();
			Map<Integer, Additional> map4 = new HashMap<>();
			Map<Integer, OrderStatus> map5 = new HashMap<>();

			while (rs.next()) {

				Client client = map1.get(rs.getInt("client_id"));
				Pizza pizza = map2.get(rs.getInt("pizza_id"));
				PizzaSize pizzaSize = map3.get(rs.getInt("pizza_size_id"));
				Additional additional = map4.get(rs.getInt("additional_id"));
				OrderStatus orderStatus = map5.get(rs.getInt("order_status_id"));
				if (client == null) {
					client = instantiateClient(rs);
					map1.put(rs.getInt("client_id"), client);
				}
				
				if (pizza == null) {
					pizza = instantiatePizza(rs);
					map2.put(rs.getInt("pizza_id"), pizza);
				}
				
				if (pizzaSize == null) {
					pizzaSize = instantiatePizzaSize(rs);
					map3.put(rs.getInt("pizza_size_id"), pizzaSize);
				}
				
				if (additional == null) {
					additional = instantiateAdditional(rs);
					map4.put(rs.getInt("additional_id"), additional);
				}
				
				if (orderStatus == null) {
					orderStatus = instantiateOrderStatus(rs);
					map5.put(rs.getInt("order_status_id"), orderStatus);
				}

				Order obj = instantiateOrder(rs, client, pizza, pizzaSize, additional, orderStatus);
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

	@Override
	public List<Order> findByPizzaSize(PizzaSize entity) {
		String SQL = "SELECT tb_order.*,"
				+ " tb_client.Name as ClientName,"
				+ " tb_pizza.Name as PizzaName,"
				+ " tb_pizza_size.Name as SizeName,"
				+ " tb_additional.Name as AdditionalName,"
				+ " tb_order_status.Name as StatusName"
				+ " FROM tb_order"
				+ " INNER JOIN tb_client ON (tb_order.client_id = tb_client.Id)"
				+ " INNER JOIN tb_pizza ON (tb_order.pizza_id = tb_pizza.Id)"
				+ " INNER JOIN  tb_pizza_size ON (tb_order.pizza_size_id = tb_pizza_size.Id)"
				+ " INNER JOIN tb_additional ON (tb_order.additional_id = tb_additional.Id)"
				+ " INNER JOIN tb_order_status ON (tb_order.order_status_id = tb_order_status.Id)"
				+ " WHERE pizza_size_id = ? ORDER BY id ASC";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);
			
			pst.setInt(1,  entity.getId());
			rs = pst.executeQuery();

			List<Order> list = new ArrayList<>();
			Map<Integer, Client> map1 = new HashMap<>();
			Map<Integer, Pizza> map2 = new HashMap<>();
			Map<Integer, PizzaSize> map3 = new HashMap<>();
			Map<Integer, Additional> map4 = new HashMap<>();
			Map<Integer, OrderStatus> map5 = new HashMap<>();

			while (rs.next()) {

				Client client = map1.get(rs.getInt("client_id"));
				Pizza pizza = map2.get(rs.getInt("pizza_id"));
				PizzaSize pizzaSize = map3.get(rs.getInt("pizza_size_id"));
				Additional additional = map4.get(rs.getInt("additional_id"));
				OrderStatus orderStatus = map5.get(rs.getInt("order_status_id"));
				if (client == null) {
					client = instantiateClient(rs);
					map1.put(rs.getInt("client_id"), client);
				}
				
				if (pizza == null) {
					pizza = instantiatePizza(rs);
					map2.put(rs.getInt("pizza_id"), pizza);
				}
				
				if (pizzaSize == null) {
					pizzaSize = instantiatePizzaSize(rs);
					map3.put(rs.getInt("pizza_size_id"), pizzaSize);
				}
				
				if (additional == null) {
					additional = instantiateAdditional(rs);
					map4.put(rs.getInt("additional_id"), additional);
				}
				
				if (orderStatus == null) {
					orderStatus = instantiateOrderStatus(rs);
					map5.put(rs.getInt("order_status_id"), orderStatus);
				}

				Order obj = instantiateOrder(rs, client, pizza, pizzaSize, additional, orderStatus);
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

	@Override
	public List<Order> findByOrderStatus(OrderStatus entity) {
		String SQL = "SELECT tb_order.*,"
				+ " tb_client.Name as ClientName,"
				+ " tb_pizza.Name as PizzaName,"
				+ " tb_pizza_size.Name as SizeName,"
				+ " tb_additional.Name as AdditionalName,"
				+ " tb_order_status.Name as StatusName"
				+ " FROM tb_order"
				+ " INNER JOIN tb_client ON (tb_order.client_id = tb_client.Id)"
				+ " INNER JOIN tb_pizza ON (tb_order.pizza_id = tb_pizza.Id)"
				+ " INNER JOIN  tb_pizza_size ON (tb_order.pizza_size_id = tb_pizza_size.Id)"
				+ " INNER JOIN tb_additional ON (tb_order.additional_id = tb_additional.Id)"
				+ " INNER JOIN tb_order_status ON (tb_order.order_status_id = tb_order_status.Id)"
				+ " WHERE order_status_id = ? ORDER BY id ASC";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);
			
			pst.setInt(1,  entity.getId());
			rs = pst.executeQuery();

			List<Order> list = new ArrayList<>();
			Map<Integer, Client> map1 = new HashMap<>();
			Map<Integer, Pizza> map2 = new HashMap<>();
			Map<Integer, PizzaSize> map3 = new HashMap<>();
			Map<Integer, Additional> map4 = new HashMap<>();
			Map<Integer, OrderStatus> map5 = new HashMap<>();

			while (rs.next()) {

				Client client = map1.get(rs.getInt("client_id"));
				Pizza pizza = map2.get(rs.getInt("pizza_id"));
				PizzaSize pizzaSize = map3.get(rs.getInt("pizza_size_id"));
				Additional additional = map4.get(rs.getInt("additional_id"));
				OrderStatus orderStatus = map5.get(rs.getInt("order_status_id"));
				if (client == null) {
					client = instantiateClient(rs);
					map1.put(rs.getInt("client_id"), client);
				}
				
				if (pizza == null) {
					pizza = instantiatePizza(rs);
					map2.put(rs.getInt("pizza_id"), pizza);
				}
				
				if (pizzaSize == null) {
					pizzaSize = instantiatePizzaSize(rs);
					map3.put(rs.getInt("pizza_size_id"), pizzaSize);
				}
				
				if (additional == null) {
					additional = instantiateAdditional(rs);
					map4.put(rs.getInt("additional_id"), additional);
				}
				
				if (orderStatus == null) {
					orderStatus = instantiateOrderStatus(rs);
					map5.put(rs.getInt("order_status_id"), orderStatus);
				}

				Order obj = instantiateOrder(rs, client, pizza, pizzaSize, additional, orderStatus);
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

	@Override
	public List<Order> findByPizza(Pizza entity) {
		String SQL = "SELECT tb_order.*,"
				+ " tb_client.Name as ClientName,"
				+ " tb_pizza.Name as PizzaName,"
				+ " tb_pizza_size.Name as SizeName,"
				+ " tb_additional.Name as AdditionalName,"
				+ " tb_order_status.Name as StatusName"
				+ " FROM tb_order"
				+ " INNER JOIN tb_client ON (tb_order.client_id = tb_client.Id)"
				+ " INNER JOIN tb_pizza ON (tb_order.pizza_id = tb_pizza.Id)"
				+ " INNER JOIN  tb_pizza_size ON (tb_order.pizza_size_id = tb_pizza_size.Id)"
				+ " INNER JOIN tb_additional ON (tb_order.additional_id = tb_additional.Id)"
				+ " INNER JOIN tb_order_status ON (tb_order.order_status_id = tb_order_status.Id)"
				+ " WHERE pizza_id = ? ORDER BY id ASC";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);
			
			pst.setInt(1,  entity.getId());
			rs = pst.executeQuery();

			List<Order> list = new ArrayList<>();
			Map<Integer, Client> map1 = new HashMap<>();
			Map<Integer, Pizza> map2 = new HashMap<>();
			Map<Integer, PizzaSize> map3 = new HashMap<>();
			Map<Integer, Additional> map4 = new HashMap<>();
			Map<Integer, OrderStatus> map5 = new HashMap<>();

			while (rs.next()) {

				Client client = map1.get(rs.getInt("client_id"));
				Pizza pizza = map2.get(rs.getInt("pizza_id"));
				PizzaSize pizzaSize = map3.get(rs.getInt("pizza_size_id"));
				Additional additional = map4.get(rs.getInt("additional_id"));
				OrderStatus orderStatus = map5.get(rs.getInt("order_status_id"));
				if (client == null) {
					client = instantiateClient(rs);
					map1.put(rs.getInt("client_id"), client);
				}
				
				if (pizza == null) {
					pizza = instantiatePizza(rs);
					map2.put(rs.getInt("pizza_id"), pizza);
				}
				
				if (pizzaSize == null) {
					pizzaSize = instantiatePizzaSize(rs);
					map3.put(rs.getInt("pizza_size_id"), pizzaSize);
				}
				
				if (additional == null) {
					additional = instantiateAdditional(rs);
					map4.put(rs.getInt("additional_id"), additional);
				}
				
				if (orderStatus == null) {
					orderStatus = instantiateOrderStatus(rs);
					map5.put(rs.getInt("order_status_id"), orderStatus);
				}

				Order obj = instantiateOrder(rs, client, pizza, pizzaSize, additional, orderStatus);
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
	
	
	private Order instantiateOrder(ResultSet rs, Client client, Pizza pizza, PizzaSize pizzaSize, Additional additional, OrderStatus orderStatus) throws SQLException {
		Order obj = new Order();
		obj.setId(rs.getInt("id"));
		obj.setClient(client);
		obj.setPizza(pizza);
		obj.setPizzaSize(pizzaSize);
		obj.setAdditional(additional);
		obj.setOrderStatus(orderStatus);
		obj.setMoment(new java.util.Date(rs.getTimestamp("moment").getTime()));
		obj.setTotal(rs.getDouble("total"));

		return obj;
	}
	
	private Client instantiateClient(ResultSet rs) throws SQLException {
		Client instClient = new Client();
		instClient.setId(rs.getInt("client_id"));
		instClient.setName(rs.getString("ClientName"));
		return instClient;
	}
	
	private Pizza instantiatePizza(ResultSet rs) throws SQLException {
		Pizza instPizza = new Pizza();
		instPizza.setId(rs.getInt("pizza_id"));
		instPizza.setName(rs.getString("PizzaName"));
		return instPizza;
	}

	private PizzaSize instantiatePizzaSize(ResultSet rs) throws SQLException {
		PizzaSize pizzaSize = new PizzaSize();
		pizzaSize.setId(rs.getInt("pizza_size_id"));
		pizzaSize.setName(rs.getString("SizeName"));
		return pizzaSize;
	}
	
	private Additional instantiateAdditional(ResultSet rs) throws SQLException {
		Additional instAdditional = new Additional();
		instAdditional.setId(rs.getInt("additional_id"));
		instAdditional.setName(rs.getString("AdditionalName"));
		return instAdditional;
	}
	
	private OrderStatus instantiateOrderStatus(ResultSet rs) throws SQLException {
		OrderStatus instOrderStatus = new OrderStatus();
		instOrderStatus.setId(rs.getInt("order_status_id"));
		instOrderStatus.setName(rs.getString("StatusName"));
		return instOrderStatus;
	}

}
