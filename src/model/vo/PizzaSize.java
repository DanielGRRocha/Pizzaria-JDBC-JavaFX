package model.vo;

import java.util.Objects;

public class PizzaSize {
	
	private Integer id;
	private String name;
	
	public PizzaSize() {
	}

	public PizzaSize(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
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
		PizzaSize other = (PizzaSize) obj;
		return Objects.equals(id, other.id);
	}
	

}
