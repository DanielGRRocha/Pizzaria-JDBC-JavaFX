package model.bo;

import java.util.List;

import model.dao.FactoryDAO;
import model.dao.InterDAO;
import model.vo.Pizza;

public class PizzaBO implements InterBO<Pizza> {
	
	private InterDAO<Pizza> dao = FactoryDAO.createPizzaDAO();

	@Override
	public void saveOrUpdate(Pizza obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
		
	}

	@Override
	public void remove(Pizza obj) {
		dao.deleteById(obj.getId());
		
	}

	@Override
	public List<Pizza> findAll() {
		return dao.findAll();
	}

}
