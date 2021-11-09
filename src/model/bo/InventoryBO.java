package model.bo;

import java.util.List;

import model.dao.FactoryDAO;
import model.dao.InterDAO;
import model.vo.Inventory;

public class InventoryBO implements InterBO<Inventory> {
	
	private InterDAO<Inventory> dao = FactoryDAO.createInventoryDAO();

	@Override
	public void saveOrUpdate(Inventory obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
		
	}

	@Override
	public void remove(Inventory obj) {
		dao.deleteById(obj.getId());	
	}

	@Override
	public List<Inventory> findAll() {
		return dao.findAll();
	}
	
	

}
