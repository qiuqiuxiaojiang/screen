package com.capiltalbio.health.mock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.capitalbio.common.util.JsonUtil;

public class BloodSugarClient {
	public String mockBloodSugar(String host, String[] args) throws Exception {
		String urlChsFx = "/deviceApp/login.json";
		String url = "";
		if (host.endsWith("/")) {
			url = host + "healthcheck/uploadInfo.json";
			urlChsFx = host + "deviceApp/login.json";
		} else {
			url = host+"/healthcheck/uploadInfo.json";
			urlChsFx = host + "/deviceApp/login.json";
		}
		if (args.length < 13) {
			return "命令格式：java -jar runmock.jar host bloodSugar userName password customerId checkTime tc tg hdl ldl bloodGlucose bloodGlucose2h bloodGlucoseRandom";
		}
		String userName = processValue(args[2]);
		String password = processValue(args[3]);
		String customerId = processValue(args[4]);
		String checkTime = processValue(args[5]);
		String tc = processValue(args[6]);
		String tg = processValue(args[7]);
		String hdl = processValue(args[8]);
		String ldl = processValue(args[9]);
		String bloodGlucose = processValue(args[10]);
		String bloodGlucose2h = processValue(args[11]);
		String bloodGlucoseRandom = processValue(args[12]);
		
		Map<String, Object> mapBloodSugar = new HashMap<String,Object>();
		Map<String, String> mapHeader = new HashMap<String,String>();
		
		Map<String, String> mapToken = new HashMap<String,String>();
		mapToken.put("userName", userName);
		mapToken.put("password", password);
		/** 先获取该帐号的userId和token信息 **/
		Map<String, Object> mapTokenResult = getToken(urlChsFx, mapToken);
		if (mapTokenResult == null || mapTokenResult.get("msg") != null) {
			return mapTokenResult.get("msg").toString();
		}
		mapHeader.put("userId", mapTokenResult.get("userId").toString());
		mapHeader.put("token", mapTokenResult.get("token").toString());
		mapBloodSugar.put("customerId", customerId);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mapBloodSugar.put("checkTime", checkTime);
		mapBloodSugar.put("tc", tc);
		mapBloodSugar.put("tg", tg);
		mapBloodSugar.put("hdl", hdl);
		mapBloodSugar.put("ldl", ldl);
		mapBloodSugar.put("bloodGlucose", bloodGlucose);
		mapBloodSugar.put("bloodGlucose2h", bloodGlucose2h);
		mapBloodSugar.put("bloodGlucoseRandom", bloodGlucoseRandom);
		Map<String, Object> mapSaveData = saveData(url, mapBloodSugar,mapHeader);
		
		return mapSaveData.get("msg").toString();
	}
	
	private String processValue(String value) {
		if (value == null) {
			return "";
		}
		if (value.equals("null")) {
			return null;
		}
		if (value.equals("blank")) {
			return "";
		}
		return value;
	}
	
	/** 获取该帐号的userId和token信息 **/
	private static Map<String,Object> getToken(String url,Map<String, String> map) {
		Map<String, Object> mapResult = new HashMap<String,Object>();
		try {
			String sendGetRequest = HttpClientUtil.get(url, map, null);
			Map<String, Object> mapSendGetRequest = JsonUtil.jsonToMap(sendGetRequest);
			if (mapSendGetRequest == null || mapSendGetRequest.size() < 1) {
				mapResult.put("msg", "获取token失败！");
			} else if (mapSendGetRequest.get("dataMap") == null ) {
				mapResult.put("msg", "获取token失败！");
			}
			Map<String, Object> dataMap = (Map<String, Object>) mapSendGetRequest.get("dataMap");
			if (dataMap.get("userId") != null && StringUtils.isNotEmpty(dataMap.get("userId").toString())
					&& dataMap.get("token") != null && StringUtils.isNotEmpty(dataMap.get("token").toString())) {
				mapResult.put("userId", dataMap.get("userId"));
				mapResult.put("token", dataMap.get("token"));
			} else {
				if (mapResult.get("message") != null) {
					mapResult.put("msg", mapResult.get("message"));
				} else {
					mapResult.put("msg", "获取token失败！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mapResult.put("msg", "获取token失败！");
		}
		return mapResult;
	}
	
	private static Map<String,Object> saveData(String url,Map<String, Object> map,Map<String, String> mapHeader) {
		Map<String, Object> mapResult = new HashMap<String,Object>();
		try {
			String sendRequest = HttpClientUtil.post1(url, map, mapHeader);
			Map<String, Object> mapResponse = JsonUtil.jsonToMap(sendRequest);
			if (mapResponse == null || mapResponse.size() < 1) {
				mapResult.put("msg", "保存数据失败！");
			}
			String message = mapResponse.get("message").toString();
			if (message != null) {
				mapResult.put("msg", message);
			} else {
				mapResult.put("msg", "保存数据失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			mapResult.put("msg", "保存数据失败！");
		}
		return mapResult;
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		String url = "http://114.116.74.14:8080/screening/deviceApp/login.json";
		String urlSave = "http://114.116.74.14:8080/screening/healthcheck/uploadInfo.json";
		Map<String, String> map = new HashMap<String,String>();
		map.put("userName", "shenchuang");
		map.put("password", "123456");
		Map<String, Object> mapBloodSugar = new HashMap<String,Object>();
		mapBloodSugar.put("bloodGlucoseRandom", "2.2");
		mapBloodSugar.put("customerId", "411122199209068279");
		mapBloodSugar.put("checkTime", "2019-05-10 11:26:35");
		Map<String, String> mapHeader = new HashMap<String,String>();
		try {
//			String sendGetRequest = HttpClientUtil.get(url, map);
			Map<String, Object> mapSendGetRequest = getToken(url,map);
			mapHeader.put("userId", mapSendGetRequest.get("userId").toString());
			mapHeader.put("token", mapSendGetRequest.get("token").toString());
			Map<String, Object> saveData = saveData(urlSave, mapBloodSugar,mapHeader);
			System.out.println(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
