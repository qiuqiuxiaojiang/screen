package com.capitalbio.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.dao.SystemConfigDAO;

@Service
public class SystemConfigService extends BaseService{

	@Autowired
	private SystemConfigDAO systemConfigDAO;
	
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		// TODO Auto-generated method stub
		return systemConfigDAO;
	}

	@Override
	public String getCollName() {
		// TODO Auto-generated method stub
		return "systemConfig";
	}

}
