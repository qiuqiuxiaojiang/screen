package com.capitalbio.common.log;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.LoggerDAO;
import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.common.util.IPUtil;
import com.google.common.collect.Maps;

@Service
public class LoggerService extends BaseService{
	@Autowired LoggerDAO loggerDAO;
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return loggerDAO;
	}

	@Override
	public String getCollName() {
		return "log";
	}
	
	public void log(HttpServletRequest request, Map<String,Object> dataMap) {
		String url = request.getRequestURI();
		String ip = IPUtil.getRemortIP(request);
		Map<String,Object> obj = Maps.newHashMap();
		obj.put("url", url);
		obj.put("ip", ip);
		obj.put("data", dataMap);
		this.saveData(obj);
	}

	public void log(HttpServletRequest request, String data) {
		String url = request.getRequestURI();
		String ip = IPUtil.getRemortIP(request);
		Map<String,Object> obj = Maps.newHashMap();
		obj.put("url", url);
		obj.put("ip", ip);
		obj.put("data", data);
		this.saveData(obj);
	}

	public void log(HttpServletRequest request) {
		String url = request.getRequestURI();
		String ip = IPUtil.getRemortIP(request);
		Map<String,Object> obj = Maps.newHashMap();
		obj.put("url", url);
		obj.put("ip", ip);
		this.saveData(obj);
	}

}
