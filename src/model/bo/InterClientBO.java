package model.bo;

import java.util.List;

public interface InterClientBO<VO> extends InterBO<VO>{

	public List<VO> findAllNewClient();

}
