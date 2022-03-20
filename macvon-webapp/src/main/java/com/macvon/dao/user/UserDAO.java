package com.macvon.dao.user;


import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.macvon.dao.BaseDAO;
import com.macvon.domain.auth.Login;
import com.macvon.query.QueryLoader;

@Repository
public class UserDAO extends BaseDAO {

	public final static String FIND_USER_LATEST_ACTIVE_LOGIN = QueryLoader.INSTANCE.getSql(UserDAO.class.getSimpleName(), "findLastesActivetUserLogin");
	public final static String FIND_USER_LATEST_ACTIVE_N_LOGIN = QueryLoader.INSTANCE.getSql(UserDAO.class.getSimpleName(), "findLastestNUserLogin");
	public final static String FIND_USER_LATEST_LOGIN = QueryLoader.INSTANCE.getSql(UserDAO.class.getSimpleName(), "findLastestUserLogin");
	
	public Login findLatestActiveLogin(long userId) {
		return (Login) findMaxEntity(FIND_USER_LATEST_ACTIVE_LOGIN, Login.class, userId, (new Date()));
	}
	public Login findUserLatestLogin(long userId) {
		return (Login) findMaxEntity(FIND_USER_LATEST_LOGIN, Login.class, userId);
	}
	public List<Login> findLatestNLogin(long userId, int top) {
		return findMaxNEntity(FIND_USER_LATEST_ACTIVE_N_LOGIN, Login.class, 3, userId);
	}
}
