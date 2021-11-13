package model.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
				+ " INNER JOIN tb_order_status ON (tb_order.order_status_id = tb_order_status.Id)";

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConnection().prepareStatement(SQL);

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