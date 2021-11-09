package model.dao;

import java.sql.ResultSet;

public interface InterClientDAO<VO> extends InterDAO<VO> {
	
	 public ResultSet findByName(VO obj);
	 public ResultSet findByCPF(VO obj);

}
