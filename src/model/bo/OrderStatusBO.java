package model.bo;

import java.util.List;

import model.dao.FactoryDAO;
import model.dao.InterDAO;
import model.vo.OrderStatus;

public class OrderStatusBO implements InterBO<OrderStatus> {
	
	private InterDAO<OrderStatus> dao = FactoryDAO.createOrderStatusDAO();

	@Override
	public void saveOrUpdate(OrderStatus obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
		
	}

	@Override
	public void remove(OrderStatus obj) {
		dao.deleteById(obj.getId());
		
	}

	@Override
	public List<OrderStatus> findAll() {
		return dao.findAll();
	}

}
