package model.bo;

import java.util.List;

import model.dao.FactoryDAO;
import model.dao.InterDAO;
import model.vo.Additional;

public class AdditionalBO implements InterBO<Additional> {
	
	private InterDAO<Additional> dao = FactoryDAO.createAdditionalDAO();

	@Override
	public void saveOrUpdate(Additional obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
		
	}

	@Override
	public void remove(Additional obj) {
		dao.deleteById(obj.getId());
		
	}

	@Override
	public List<Additional> findAll() {
		return dao.findAll();
	}

}
