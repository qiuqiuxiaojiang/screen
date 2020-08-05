package com.capitalbio.common.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.cache.ApikeyCache;
import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.dao.SubmitClientDAO;
import com.google.common.collect.Maps;
@Service
public class SubmitClientService extends BaseService{
	
	@Autowired SubmitClientDAO submitClientDao;
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return submitClientDao;
	}

	@Override
	public String getCollName() {
		return "submit_client";
	}

	public Map<String, Object> getClientByApikey(String apikey) {
		Map<String,Object> app = ApikeyCache.getInstance().getApikey(apikey);
		if (app == null) {
			Map<String, Object> obj = Maps.newHashMap();
			obj.put("apikey", apikey);
			app = submitClientDao.getDataByQuery(getCollName(), obj);
			if (app != null) {
				ApikeyCache.getInstance().putApikey(apikey, app);
			}
		}
		return app;
	}
}
