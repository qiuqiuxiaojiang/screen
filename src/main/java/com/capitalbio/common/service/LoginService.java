package com.capitalbio.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.LoginDao;
import com.capitalbio.common.dao.MongoBaseDAO;
@Service
public class LoginService extends BaseService{

	@Autowired private LoginDao loginDao;
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		// TODO Auto-generated method stub
		return loginDao;
	}

	@Override
	public String getCollName() {
		// TODO Auto-generated method stub
		return "user";
	}
	
}
