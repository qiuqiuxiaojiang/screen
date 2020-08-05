package com.capitalbio.healthcheck.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.healthcheck.dao.CommunitysDAO;
import com.google.common.collect.Maps;
@Service
public class CommunitysService  extends BaseService{

	@Autowired
	private CommunitysDAO communitysDao;
	
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		// TODO Auto-generated method stub
		return communitysDao;
	}

	@Override
	public String getCollName() {
		// TODO Auto-generated method stub
		return "communitys";
	}
	
	public List<String> getStreets(Long itemId) {
		return communitysDao.getStreets(itemId);
	}
	
	
	public List<Map<String, Object>> getCommunitysByStreet(String street) {
		
		String item = PropertyUtils.getProperty("item");
		Long itemId = (long) 0;
		if (item.equals("fuxin")) {
			itemId = (long) 1;
		} else if(item.equals("kunming")){
			itemId = (long) 2;
		}
		
		Map<String, Object> query = Maps.newHashMap();
		query.put("street", street);
		query.put("itemId", itemId);
		List<Map<String, Object>> communitys = communitysDao.queryList(getCollName(), query);
		return communitys;
		
	}

}
