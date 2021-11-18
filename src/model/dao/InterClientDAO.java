package model.dao;

import java.sql.ResultSet;
import java.util.List;

public interface InterClientDAO<VO> extends InterDAO<VO> {
	
	 public ResultSet findByName(VO obj);
	 public ResultSet findByCPF(VO obj);
	 public List<VO> findAllNewClient();

}
