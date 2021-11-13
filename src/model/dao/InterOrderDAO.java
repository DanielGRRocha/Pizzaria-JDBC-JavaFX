package model.dao;

import java.util.List;

import model.vo.Client;
import model.vo.OrderStatus;
import model.vo.Pizza;
import model.vo.PizzaSize;

public interface InterOrderDAO<VO> extends InterDAO<VO> {
	
	 List<VO> findByClient(Client entity);
	 List<VO> findByPizzaSize(PizzaSize entity);
	 List<VO> findByOrderStatus(OrderStatus entity);
	 List<VO> findByPizza(Pizza entity);

}
