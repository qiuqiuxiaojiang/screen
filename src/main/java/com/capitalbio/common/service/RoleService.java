package com.capitalbio.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.dao.RoleDAO;
@Service
public class RoleService extends BaseService{

	@Autowired private RoleDAO roleDao;
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		// TODO Auto-generated method stub
		return roleDao;
	}

	@Override
	public String getCollName() {
		// TODO Auto-generated method stub
		return "role";
	}

}
