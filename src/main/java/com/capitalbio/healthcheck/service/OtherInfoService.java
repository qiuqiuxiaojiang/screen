package com.capitalbio.healthcheck.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.healthcheck.dao.OtherInfoDAO;

@Service
public class OtherInfoService extends BaseService{
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired private OtherInfoDAO otherInfoDAO;
	
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return otherInfoDAO;
	}

	@Override
	public String getCollName() {
		return "otherinfo";
	}
}
