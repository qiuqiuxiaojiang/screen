package com.capitalbio.healthcheck.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.mongo.MongoDBFactory;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@Repository
public class DeviceDAO extends MongoBaseDAO{
	protected DB db = MongoDBFactory.getDB();
	
	/**
	 * 校验用户的角色
	 * @param userName
	 * @param rolekey
	 * @return
	 */
	public boolean judgeUserRole(String userName,String rolekey) {
		Map<String, Object> queryMap = new HashMap<String,Object>();
		queryMap.put("username", userName);
		queryMap.put("roles.rolekey", rolekey);
		Map<String, Object> queryOne = queryOne("user", queryMap);
		if (queryOne == null ) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 校验mac地址是否已经绑定
	 * @param userName
	 * @param Mac
	 * @return
	 */
	public boolean judgeDeviceMac(String userName,String mac) {
		Map<String, Object> queryMap = new HashMap<String,Object>();
		queryMap.put("mac", mac);
		queryMap.put("status", 1);
		Map<String, Object> queryOne = queryOne("deviceInfo", queryMap);
		if (queryOne == null ) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 根据userName和deviceName获取数据
	 * @param userName
	 * @param deviceName
	 * @return
	 */
	public Map<String, Object> getByDeviceName(String userName,String deviceName) {
		Map<String, Object> queryMap = new HashMap<String,Object>();
		queryMap.put("username", userName);
		queryMap.put("devicename", deviceName);
		Map<String, Object> queryOne = queryOne("deviceInfo", queryMap);
		if (queryOne == null ) {
			return null;
		} else {
			return queryOne;
		}
	}
	/**
	 * 根据userName和status获取数据
	 * @param userName
	 * @param deviceName
	 * @return
	 */
	public List<Map<String, Object>> getDeviceList(String userName,String status) {
		Map<String, Object> queryMap = new HashMap<String,Object>();
		queryMap.put("username", userName);
		queryMap.put("status", Integer.parseInt(status));
		List<Map<String, Object>> queryList = queryList("deviceInfo", queryMap);
		if (queryList == null ) {
			return null;
		} else {
			return queryList;
		}
	}

}
