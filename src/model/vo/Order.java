package model.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Date moment;
	private Double total;
	private Pizza pizza;
	private Client client;
	private Additional additional;
	private PizzaSize pizzaSize;
	private OrderStatus orderStatus;
	
	public Order() {
	}

	public Order(Integer id, Date moment, Double total, Pizza pizza, Client client, Additional additional,
			PizzaSize pizzaSize, OrderStatus orderStatus) {
		this.id = id;
		this.moment = moment;
		this.total = total;
		this.pizza = pizza;
		this.client = client;
		this.additional = additional;
		this.pizzaSize = pizzaSize;
		this.orderStatus = orderStatus;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getMoment() {
		return moment;
	}

	public void setMoment(Date moment) {
		this.moment = moment;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Pizza getPizza() {
		return pizza;
	}

	public void setPizza(Pizza pizza) {
		this.pizza = pizza;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Additional getAdditional() {
		return additional;
	}

	public void setAdditional(Additional additional) {
		this.additional = additional;
	}

	public PizzaSize getPizzaSize() {
		return pizzaSize;
	}

	public void setPizzaSize(PizzaSize pizzaSize) {
		this.pizzaSize = pizzaSize;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return Objects.equals(id, other.id);
	}
	
}
