package com.capitalbio.common.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.dao.UrlResourceDAO;

@Service
public class UrlResourceService extends BaseService{

	@Autowired private UrlResourceDAO urlResourceDao;
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		// TODO Auto-generated method stub
		return urlResourceDao;
	}

	@Override
	public String getCollName() {
		// TODO Auto-generated method stub
		return "urlresource";
	}
	
	public List<Map<String, Object>> getUrlResource(Map<String, Object> queryMap){
		return urlResourceDao.getUrlResource(queryMap);
	}

}
