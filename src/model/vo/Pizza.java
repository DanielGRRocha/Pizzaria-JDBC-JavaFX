package model.vo;

import java.io.Serializable;
import java.util.Objects;

public class Pizza implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private Double priceSmallPizza;
	private Double priceMediumPizza;
	private Double priceBigPizza;
	
	public Pizza() {
	}

	public Pizza(Integer id, String name, Double priceSmallPizza, Double priceMediumPizza, Double priceBigPizza) {
		this.id = id;
		this.name = name;
		this.priceSmallPizza = priceSmallPizza;
		this.priceMediumPizza = priceMediumPizza;
		this.priceBigPizza = priceBigPizza;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPriceSmallPizza() {
		return priceSmallPizza;
	}

	public void setPriceSmallPizza(Double priceSmallPizza) {
		this.priceSmallPizza = priceSmallPizza;
	}

	public Double getPriceMediumPizza() {
		return priceMediumPizza;
	}

	public void setPriceMediumPizza(Double priceMediumPizza) {
		this.priceMediumPizza = priceMediumPizza;
	}

	public Double getPriceBigPizza() {
		return priceBigPizza;
	}

	public void setPriceBigPizza(Double priceBigPizza) {
		this.priceBigPizza = priceBigPizza;
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
		Pizza other = (Pizza) obj;
		return Objects.equals(id, other.id);
	}
	
}
