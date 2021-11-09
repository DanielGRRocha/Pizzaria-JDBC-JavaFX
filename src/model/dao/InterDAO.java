package model.dao;

import java.sql.ResultSet;
import java.util.List;


public interface InterDAO <VO> {
	 public void insert(VO obj); 
	 public void update(VO obj);
	 public void deleteById(int id);
	 public ResultSet findById(int id);
	 public List<VO> findAll();
}
