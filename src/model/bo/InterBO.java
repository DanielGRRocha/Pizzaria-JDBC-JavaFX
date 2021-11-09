package model.bo;

import java.util.List;

public interface InterBO<VO> {

	public void saveOrUpdate(VO obj);
	public void remove(VO obj);
	public List<VO> findAll();

}
