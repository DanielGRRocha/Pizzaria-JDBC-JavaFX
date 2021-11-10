package model.dao;

import model.dao.jdbc.AdditionalDAO;
import model.dao.jdbc.ClientDAO;
import model.dao.jdbc.InventoryDAO;
import model.dao.jdbc.PizzaDAO;
import model.dao.jdbc.PizzaSizeDAO;
import model.vo.Additional;
import model.vo.Client;
import model.vo.Inventory;
import model.vo.Pizza;
import model.vo.PizzaSize;

public class FactoryDAO {
	
	public static InterDAO<Client> createClienteDAO() {
		return new ClientDAO();
	}
	
	public static InterDAO<Inventory> createInventoryDAO(){
		return new InventoryDAO();
	}
	
	public static InterDAO<Additional> createAdditionalDAO(){
		return new AdditionalDAO();
	}
	
	public static InterDAO<PizzaSize> createPizzaSizeDAO(){
		return new PizzaSizeDAO();
	}
	
	public static InterDAO<Pizza> createPizzaDAO(){
		return new PizzaDAO();
	}
	
}
