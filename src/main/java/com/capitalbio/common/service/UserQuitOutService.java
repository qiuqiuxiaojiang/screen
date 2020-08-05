package com.capitalbio.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.dao.UserQuitOutDAO;
@Service
public class UserQuitOutService extends BaseService{

	@Autowired private UserQuitOutDAO userQuitOutDao;
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		// TODO Auto-generated method stub
		return userQuitOutDao;
	}

	@Override
	public String getCollName() {
		// TODO Auto-generated method stub
		return "user";
	}

}
