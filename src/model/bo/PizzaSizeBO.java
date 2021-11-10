package model.bo;

import java.util.List;

import model.dao.FactoryDAO;
import model.dao.InterDAO;
import model.vo.PizzaSize;

public class PizzaSizeBO implements InterBO<PizzaSize> {
	
	private InterDAO<PizzaSize> dao = FactoryDAO.createPizzaSizeDAO();

	@Override
	public void saveOrUpdate(PizzaSize obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
		
	}

	@Override
	public void remove(PizzaSize obj) {
		dao.deleteById(obj.getId());
		
	}

	@Override
	public List<PizzaSize> findAll() {
		return dao.findAll();
	}

}
