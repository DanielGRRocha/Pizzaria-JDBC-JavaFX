package model.dao;

import model.dao.jdbc.ClientDAO;
import model.dao.jdbc.InventoryDAO;
import model.dao.jdbc.OrderStatusDAO;
import model.dao.jdbc.PizzaDAO;
import model.dao.jdbc.PizzaSizeDAO;
import model.vo.Client;
import model.vo.Inventory;
import model.vo.OrderStatus;
import model.vo.Pizza;
import model.vo.PizzaSize;

public class Test {

	public static void main(String[] args) {

//		Client obj = new Client(null, "Daniel Rocha Maia", "06289662376", "85981419682", "Rua Tenente Roma, 53");
//
//		ClientDAO dao = new ClientDAO();
//
//		dao.insert(obj);
//		// dao.deleteById(3);
//		dao.update(obj);
//
//		for (Client x : dao.findAll()) {
//			System.out.println(x.getName());
//		}
		
		
		// inventory
		
		OrderStatusDAO daos = new OrderStatusDAO();
		
		OrderStatus obj = new OrderStatus(null, "Status Teste");

		PizzaDAO dao = new PizzaDAO();

		//daos.insert(obj);
		 daos.deleteById(4);
//		dao.update(obj);

//		for (Client x : dao.findAll()) {
//			System.out.println(x.getName());
//		}
	}
}
