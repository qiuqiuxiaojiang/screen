package com.capitalbio.healthcheck.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.healthcheck.dao.DeviceDAO;

@Service
public class DeviceService extends BaseService{
	@Autowired private DeviceDAO deviceDAO;

	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		// TODO Auto-generated method stub
		return deviceDAO;
	}

	@Override
	public String getCollName() {
		// TODO Auto-generated method stub
		return "user";
	}
	
	public boolean judgeUserRole(String userName,String rolekey) {
		return deviceDAO.judgeUserRole(userName, rolekey);
	}
	
	/**
	 * 校验mac地址是否已经绑定
	 * true 已绑定
	 * false 未绑定
	 * 
	 * @param userName
	 * @param Mac
	 * @return
	 */
	public boolean judgeMac(String userName,String mac){
		return deviceDAO.judgeDeviceMac(userName, mac);
	}
	
	public boolean bindMac(String userId,String userName,String deviceName,String mac) {
		/** 根据userName和deviceName获取数据 **/
		Map<String, Object> judgeDeviceNameMap = deviceDAO.getByDeviceName(userName, deviceName);
		if (judgeDeviceNameMap != null) {
			/** 存在，绑定mac，修改状态 **/
			judgeDeviceNameMap.put("mac", mac);
			judgeDeviceNameMap.put("status", 1);
			
		} else {
			/** 不存在，插入一条新数据 **/
			judgeDeviceNameMap = new HashMap<String,Object>();
			judgeDeviceNameMap.put("userid", userId);
			judgeDeviceNameMap.put("username", userName);
			judgeDeviceNameMap.put("devicename", deviceName);
			judgeDeviceNameMap.put("mac", mac);
			judgeDeviceNameMap.put("status", 1);
		}
		deviceDAO.saveData("deviceInfo", judgeDeviceNameMap);
		return true;
	}
	
	public boolean unBindMac(String userId,String userName,String deviceName) {
		/** 根据userName和deviceName获取数据 **/
		Map<String, Object> judgeDeviceNameMap = deviceDAO.getByDeviceName(userName, deviceName);
		if (judgeDeviceNameMap != null) {
			/** 存在，绑定mac，修改状态 **/
			judgeDeviceNameMap.put("mac", null);
			judgeDeviceNameMap.put("status", 0);
			deviceDAO.saveData("deviceInfo", judgeDeviceNameMap);
			return true;
		} else {
			return false;
		}
	}
	
	public List<Map<String,Object>> bindList(String userName,String status) {
		return deviceDAO.getDeviceList(userName, status);
	}

}
