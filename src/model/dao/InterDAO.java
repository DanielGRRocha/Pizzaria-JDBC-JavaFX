package model.dao;

import java.util.List;


public interface InterDAO <VO> {
	 public void insert(VO obj); 
	 public void update(VO obj);
	 public void deleteById(Integer id);
	 public VO findById(Integer id);
	 public List<VO> findAll();
}
