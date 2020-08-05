package com.capitalbio.common.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.dao.RegistDAO;
import com.capitalbio.common.util.MD5Util;
import com.google.common.collect.Maps;
@Service
public class RegistService extends BaseService{

	@Autowired private RegistDAO registDao;
	@Autowired private LoginService loginService;
	
	public MongoBaseDAO getMongoBaseDAO() {
		return registDao;
	}

	@Override
	public String getCollName() {
		return "user";
	}
	
	public String regist(HttpServletRequest request){
		Map<String, Object> params = parseRequet(request);
		String id = (String) params.get("id");
		String username = (String) params.get("username");
		if(null != username && !username.equals("")){
			Map<String, Object> map = Maps.newHashMap();
			map.put("username", username);
			Map<String, Object> objList = loginService.getDataByQuery(map);
			if (null == objList) {
				Map<String, Object> obj = Maps.newHashMap();
				obj.putAll(params);
				id = saveData(obj);
				return id;
			}
		}
		return null;
	}
	
	private Map<String, Object> parseRequet(HttpServletRequest request){
		Map<String, Object> params = Maps.newHashMap();
		params.put("username", request.getParameter("username"));
		params.put("password", MD5Util.MD5Encode(request.getParameter("password")));
		params.put("birthday", request.getParameter("birthday"));
		params.put("idCard", request.getParameter("idCard"));
		params.put("address", request.getParameter("address"));
		params.put("telephone", request.getParameter("telephone"));
		params.put("id", request.getParameter("userid"));
		return params;
	}

}
