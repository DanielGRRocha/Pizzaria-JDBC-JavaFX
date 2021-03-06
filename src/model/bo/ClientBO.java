package model.bo;

import java.util.List;

import model.dao.FactoryDAO;
import model.dao.InterClientDAO;
import model.vo.Client;

public class ClientBO implements InterClientBO<Client> {
	
	private InterClientDAO<Client> dao = FactoryDAO.createClienteDAO();
	
	@Override
	public void saveOrUpdate(Client obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	@Override
	public void remove(Client obj) {
		dao.deleteById(obj.getId());
	}
	
	@Override
	public List<Client> findAll() {
		return dao.findAll();
	}

	@Override
	public List<Client> findAllNewClient() {
		return dao.findAllNewClient();
	}
	
	


}
