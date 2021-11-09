package model.dao;

import model.dao.jdbc.ClientDAO;
import model.dao.jdbc.InventoryDAO;
import model.vo.Client;
import model.vo.Inventory;

public class FactoryDAO {
	
	public static InterDAO<Client> createClienteDAO() {
		return new ClientDAO();
	}
	
	public static InterDAO<Inventory> createInventoryDAO(){
		return new InventoryDAO();
	}
	
//	public static InterDAO<PedidoVO> createPedidoDAO(){
//	return new PedidoDAO();
//}
	
}
