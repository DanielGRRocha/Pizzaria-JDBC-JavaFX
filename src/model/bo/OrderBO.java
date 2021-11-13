package model.bo;

import java.util.List;

import model.dao.FactoryDAO;
import model.dao.InterDAO;
import model.vo.Order;

public class OrderBO implements InterBO<Order> {
	
	private InterDAO<Order> dao = FactoryDAO.createOrderDAO();

	@Override
	public void saveOrUpdate(Order obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
		
	}

	@Override
	public void remove(Order obj) {
		dao.deleteById(obj.getId());
		
	}

	@Override
	public List<Order> findAll() {
		return dao.findAll();
	}

}
