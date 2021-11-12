package model.dao;

import java.util.List;

public interface InterOrderDAO<VO> extends InterDAO<VO> {
	
	 List<VO> findByClientId(Integer id);
	 List<VO> findByPizzaSizeId(Integer id);
	 List<VO> findByOrderStatusId(Integer id);
	 List<VO> findByPizzaId(Integer id);

}
