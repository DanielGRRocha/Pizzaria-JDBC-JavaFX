package model.dao;

import model.dao.jdbc.ClientDAO;
import model.dao.jdbc.InventoryDAO;
import model.dao.jdbc.PizzaDAO;
import model.dao.jdbc.PizzaSizeDAO;
import model.vo.Client;
import model.vo.Inventory;
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
		
		PizzaSizeDAO daos = new PizzaSizeDAO();
		
		Pizza obj = new Pizza(null, "Mussarela", 20.0, daos.findById(1));

		PizzaDAO dao = new PizzaDAO();

		dao.insert(obj);
		// dao.deleteById(3);
//		dao.update(obj);

//		for (Client x : dao.findAll()) {
//			System.out.println(x.getName());
//		}
	}
}
