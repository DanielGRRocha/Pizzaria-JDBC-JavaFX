package model.dao;

import model.dao.jdbc.ClientDAO;
import model.dao.jdbc.InventoryDAO;
import model.vo.Client;
import model.vo.Inventory;

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
		Inventory obj = new Inventory(null, "Queijo", 1);

		InventoryDAO dao = new InventoryDAO();

		dao.insert(obj);
		// dao.deleteById(3);
//		dao.update(obj);

//		for (Client x : dao.findAll()) {
//			System.out.println(x.getName());
//		}
	}
}
