package model.bo;

import java.sql.ResultSet;

import model.dao.jdbc.UserDAO;
import model.vo.User;

public class UserBO {
	
	UserDAO dao = new UserDAO();
	
	public void insert(User obj) {
		dao.insert(obj);
	}
	
	public boolean login (User user) throws Exception{
		
		UserDAO busca = new UserDAO();
		ResultSet result = busca.findByUsername(user);
		boolean valid = false;
		
			if(result.next()) {
				
				if(result.getString("password").equals(user.getPassword())) {
					
					valid=true;
				}
				else if(!(result.getString("password").equals(user.getPassword()))) {
					valid =false;
					
				}else {
					throw new Exception("Username ou senha incorretos");
				}
			}
			
			return valid;
	}

}
