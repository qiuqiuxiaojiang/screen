package com.capitalbio.healthcheck.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.capitalbio.auth.util.Constant;
import com.capitalbio.auth.util.HttpUtils;
import com.capitalbio.auth.util.JwtUtil;
import com.capitalbio.common.exception.BaseException;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.common.util.PropertyUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;

@Service
public class HealthControlService {
	
	@Autowired
	private HealthCheckService healthCheckService;
	
	@Autowired
	private FileManageService fileManageService;
	
	JedisTemplate template = RedisUtils.getTemplate();
	
	public void saveDoc(Map<String, Object> params) {
		Map<String, Object> map = Maps.newHashMap();
		
		map.put("uniqueId", params.get("uniqueId"));
		map.put("gender", params.get("gender"));
		map.put("age", params.get("age"));
		map.put("recordDate", params.get("recordDate"));
		map.put("checkPlace", params.get("district").toString() + params.get("checkPlace"));
		
		String haveDisease = params.get("haveDisease").toString();
		if (haveDisease.equals("是")) {
			map.put("diseaseHistory", params.get("disease"));
		} else {
			map.put("diseaseHistory", "否");
		}
		
		String familyHistory = params.get("familyHistory").toString();
		if (familyHistory.equals("是")) {
			map.put("familyHistory", params.get("familyDisease"));
		} else {
			map.put("familyHistory", "否");
		}
		
		map.put("type", "doc");
		
		Map<String, Object> sendData = sendDocData(map);
		System.out.println("sendData:" + sendData);
		
		if (sendData != null && !"".equals(sendData) && !sendData.isEmpty()) {
			map.put("code", sendData.get("code"));
		}
		map.put("returnMsg", sendData);
		healthCheckService.saveData("visitperson", map);
	}
	
	/**
	 * 
	 * 如在精筛/初筛中同意随访，之后的新增筛查不再展示是否进入随访字段
	 * 如不同意随访，之后新增筛查记录会一直展示该字段，直到同意随访
	 *	    
	 * @param params
	 */
	public void sendHealthCheck(Map<String, Object> params) {
		String dataSource = params.get("dataSource").toString();
		Map<String, Object> map = Maps.newHashMap();
		if (dataSource.equals("初筛")) {
			map = parseHealthCheck(params);
		} else {
			map = parseHealthCheckDetail(params);
		}
		
		map.put("type", "healthcheck");
		
		Map<String, Object> sendData = sendData(map);
		System.out.println("sendData:" + sendData);
		
		if (sendData != null && !"".equals(sendData) && !sendData.isEmpty()) {
			map.put("code", sendData.get("code"));
		}
		map.put("returnMsg", sendData);
		healthCheckService.saveData("visitperson", map);
		
	}
	
	public Map<String,Object> sendData(Map<String,Object> map) {
		
		String webSignature = JwtUtil.createJWT(Constant.JWT_ID, "healthcheck", Constant.JWT_TTL);
		map.put("webSignature", webSignature);
		String postUrl = PropertyUtils.getProperty("healthcontrol.url")+"/rest/screenData";
		try {
			HttpPost httpPost = new HttpPost(postUrl);
			String json = JsonUtil.mapToJson(map);
			StringEntity se = new StringEntity(json, "UTF-8");
			httpPost.setEntity(se);
			httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
//			httpPost.addHeader("token", token);
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			
			HttpResponse response = HttpUtils.getClient().execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			Map<String,Object> returnMap = Maps.newHashMap();
					
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				Map<String,Object> retmap = JsonUtil.jsonToMap(result);
				return retmap;
			} else {
				returnMap.put("httpCode", code);
				returnMap.put("code", 1);
				returnMap.put("msg", "保存数据异常");
				return returnMap;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,Object> returnMap = Maps.newHashMap();
		returnMap.put("msg", "网络错误");
		returnMap.put("code", 1);
		return returnMap;
	}
	
	public Map<String,Object> sendDocData(Map<String,Object> map) {
		
		String webSignature = JwtUtil.createJWT(Constant.JWT_ID, "healthcheck", Constant.JWT_TTL);
		map.put("webSignature", webSignature);
		String postUrl = PropertyUtils.getProperty("healthcontrol.url")+"/rest/docData";
		try {
			HttpPost httpPost = new HttpPost(postUrl);
			String json = JsonUtil.mapToJson(map);
			StringEntity se = new StringEntity(json, "UTF-8");
			httpPost.setEntity(se);
			httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
//			httpPost.addHeader("token", token);
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			
			HttpResponse response = HttpUtils.getClient().execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			Map<String,Object> returnMap = Maps.newHashMap();
					
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				Map<String,Object> retmap = JsonUtil.jsonToMap(result);
				return retmap;
			} else {
				returnMap.put("httpCode", code);
				returnMap.put("code", 1);
				returnMap.put("msg", "保存数据异常");
				return returnMap;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,Object> returnMap = Maps.newHashMap();
		returnMap.put("msg", "网络错误");
		returnMap.put("code", 1);
		return returnMap;
	}
	
	
	public Map<String, Object> parseHealthCheck(Map<String, Object> params) {
		
		String item = PropertyUtils.getProperty("item");
		Long itemId = (long) 0;
		if (item.equals("fuxin")) {
			itemId = (long) 1;
		} else {
			itemId = (long) 2;
		}
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("uniqueId", params.get("uniqueId"));
		map.put("gender", params.get("gender"));
		map.put("age", params.get("age"));
		map.put("recordDate", params.get("recordDate"));
		map.put("checkDate", params.get("checkDate"));
		map.put("checkSource", params.get("dataSource"));
		map.put("itemId", itemId);
		map.put("checkPlace", params.get("district").toString() + params.get("checkPlace"));
		map.put("checkPlaceId", params.get("checkPlaceId"));
		map.put("sugarCondition", params.get("bloodSugarCondition"));
		map.put("pressureCondition", params.get("bloodPressureCondition"));
		map.put("fatCondition", params.get("bloodLipidCondition"));
		map.put("highPressure", params.get("highPressure"));
		map.put("lowPressure", params.get("lowPressure"));
		map.put("weight", params.get("weight"));
		map.put("bmi", params.get("BMI"));
		map.put("waistline", params.get("waistline"));
		map.put("height", params.get("height"));
		
		String haveDisease = params.get("haveDisease").toString();
		if (haveDisease.equals("是")) {
			map.put("diseaseHistory", params.get("disease"));
		} else {
			map.put("diseaseHistory", "否");
		}
		
		String familyHistory = params.get("familyHistory").toString();
		if (familyHistory.equals("是")) {
			map.put("familyHistory", params.get("familyDisease"));
		} else {
			map.put("familyHistory", "否");
		}
		
		String gluType = "";
		String glu = "";
		if (params.get("bloodGlucose") != null && !"".equals(params.get("bloodGlucose"))) {
			gluType = "空腹";
			glu = params.get("bloodGlucose").toString();
		} else if (params.get("bloodGlucose2h") != null && !"".equals(params.get("bloodGlucose2h"))) {
			gluType = "随机";
			glu = params.get("bloodGlucose2h").toString();
		} else if (params.get("bloodGlucoseRandom") != null && !"".equals(params.get("bloodGlucoseRandom"))) {
			gluType = "随机";
			glu = params.get("bloodGlucoseRandom").toString();
		}
		map.put("gluType", gluType);
		map.put("glu", glu);
		
		map.put("tc", params.get("tc"));
		map.put("tg", params.get("tg"));
		map.put("hdl", params.get("hdl"));
		map.put("ldl", params.get("ldl"));
		return map;
	}
	
	
	public Map<String, Object> parseHealthCheckDetail(Map<String, Object> params) {
		String item = PropertyUtils.getProperty("item");
		Long itemId = (long) 0;
		if (item.equals("fuxin")) {
			itemId = (long) 1;
		} else {
			itemId = (long) 2;
		}
		
		String sugarCondition = "";
		String pressureCondition = "";
		String fatCondition = "";
		String classifyResultJs = params.get("classifyResultJs").toString();
		if (classifyResultJs.contains("糖尿病患者")) {
			sugarCondition = "糖尿病患者";
		}
		
		if (classifyResultJs.contains("糖尿病前期人群")) {
			sugarCondition = "糖尿病前期人群";
		}
		
		if (classifyResultJs.contains("高血压患者")) {
			pressureCondition = "高血压患者";
		}
		
		if (classifyResultJs.contains("高血压前期人群")) {
			pressureCondition = "高血压前期人群";
		}
		
		if (classifyResultJs.contains("血脂异常患者")) {
			fatCondition = "血脂异常患者";
		}
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("uniqueId", params.get("uniqueId"));
		map.put("gender", params.get("gender"));
		map.put("age", params.get("age"));
		map.put("recordDate", params.get("recordDate"));
		map.put("checkDate", params.get("checkDateJs"));
		map.put("checkSource", params.get("dataSource"));
		map.put("itemId", itemId);
		map.put("checkPlace", params.get("district").toString() + params.get("checkPlace"));
		map.put("checkPlaceId", params.get("checkPlaceId"));
		map.put("sugarCondition", sugarCondition);
		map.put("pressureCondition", pressureCondition);
		map.put("fatCondition", fatCondition);
		map.put("highPressure", params.get("highPressure4"));
		map.put("lowPressure", params.get("lowPressure4"));
		map.put("weight", params.get("weight"));
		map.put("bmi", params.get("BMI"));
		map.put("waistline", params.get("waistline"));
		map.put("height", params.get("height") );
		
		String haveDisease = params.get("haveDisease").toString();
		if (haveDisease.equals("是")) {
			map.put("diseaseHistory", params.get("disease"));
		} else {
			map.put("diseaseHistory", "否");
		}
		
		String familyHistory = params.get("familyHistory").toString();
		if (familyHistory.equals("是")) {
			map.put("familyHistory", params.get("familyDisease"));
		} else {
			map.put("familyHistory", "否");
		}
		
		String gluType = "";
		String glu = "";
		if (params.get("ogtt") != null && !"".equals(params.get("ogtt"))) {
			gluType = "空腹";
			glu = params.get("ogtt").toString();
		} else if (params.get("ogtt2h") != null && !"".equals(params.get("ogtt2h"))) {
			gluType = "随机";
			glu = params.get("ogtt2h").toString();
		} 
		map.put("gluType", gluType);
		map.put("glu", glu);
		map.put("tc", params.get("tc"));
		map.put("tg", params.get("tg"));
		map.put("hdl", params.get("hdl"));
		map.put("ldl", params.get("ldl"));
		return map;
	}
	
	
	/**
	 * 获取建档信息
	 * @param uniqueId
	 * @return
	 * @throws BaseException
	 */
	public JSONObject getRecordInfo (String uniqueId) throws BaseException {
		JSONObject json = new JSONObject();
		
		Map<String, Object> query = Maps.newHashMap();
		query.put("uniqueId", uniqueId);
		Map<String, Object> customer = healthCheckService.getDataByQuery("customer", query);
		if (customer != null) {
			json.putAll(customer);
//			json.put("gender", customer.get("gender"));
//			json.put("birthday", customer.get("birthday"));
//			json.put("age", customer.get("age"));
//			json.put("nationality", customer.get("nationality"));
//			json.put("recordDate", customer.get("recordDate"));
			
			String diseaseHistory = "";
	    	if (customer.get("haveDisease").equals("是")) {
	    		diseaseHistory = customer.get("disease").toString();
	    	} else if(customer.get("haveDisease").equals("否")){
	    		diseaseHistory = "否";
	    	}
	    	json.put("diseaseHistory", diseaseHistory);
	    	
	    	String familyHistory = "";
	    	if (customer.get("familyHistory").equals("是")) {
	    		familyHistory = customer.get("familyDisease").toString();
	    	} else if(customer.get("familyHistory").equals("否")){
	    		familyHistory = "否";
	    	}
	    	json.put("familyHistory", familyHistory);
			
//			json.put("checkPlace", customer.get("checkPlace"));
//			json.put("checkGroup", customer.get("checkGroup"));
		}
		return json;
	}
	
	/**
	 * 获取初筛信息列表
	 * @param uniqueId
	 * @return
	 * @throws BaseException
	 */
	public JSONObject getHealthCheckList (String uniqueId) throws BaseException {
		JSONObject json = new JSONObject();
		List<Map<String, Object>> list = Lists.newArrayList();
		
		Map<String, Object> query = Maps.newHashMap();
		query.put("uniqueId", uniqueId);
		
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("checkDate", -1);
		
		List<Map<String, Object>> healthchecks = healthCheckService.queryList("healthcheck", query, sortMap);
		for (Map<String, Object> healthcheck : healthchecks) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("gender", healthcheck.get("gender"));
			map.put("age", healthcheck.get("age"));
			map.put("checkDate", healthcheck.get("checkDate"));
			map.put("classifyResult", healthcheck.get("classifyResult"));
			map.put("healthCheckId", healthcheck.get("id"));
			
			Map<String, Object> customer = healthCheckService.getDataByQuery("customer", query);
			if (customer != null) {
				map.put("checkPlace", customer.get("checkPlace"));
				map.put("checkGroup", customer.get("checkGroup"));
			}
			
			list.add(map);
		}
		json.put("r", list);
		return json;
	}
	
	/**
	 * 获取初筛详情信息
	 * @param healthCheckId healthchek表中主键
	 * @return
	 * @throws BaseException
	 */
	public JSONObject getHealthCheckInfo (String healthCheckId) throws BaseException {
		JSONObject json = new JSONObject();
		
		Map<String, Object> healthcheck = healthCheckService.getData(healthCheckId);
		
		if (healthcheck != null) {
			json.putAll(healthcheck);
			
			String diseaseHistory = "";
	    	if (healthcheck.get("haveDisease").equals("是")) {
	    		diseaseHistory = healthcheck.get("disease").toString();
	    	} else if(healthcheck.get("haveDisease").equals("否")){
	    		diseaseHistory = "否";
	    	}
	    	
	    	json.put("diseaseHistory", diseaseHistory);
	    	
	    	String familyHistory = "";
	    	if (healthcheck.get("familyHistory").equals("是")) {
	    		familyHistory = healthcheck.get("familyDisease").toString();
	    	} else if(healthcheck.get("familyHistory").equals("否")){
	    		familyHistory = "否";
	    	}
	    	json.put("familyHistory", familyHistory);
	    	
	    	json.remove("haveDisease");
	    	json.remove("familyDisease");
	    	json.remove("disease");
		}
		
		return json;
	}
	
	/**
	 * 获取精筛详情信息
	 * @param uniqueId
	 * @return
	 * @throws Exception 
	 */
	public JSONObject getHealthCheckDetailInfo(String uniqueId) throws Exception {
		JSONObject json = new JSONObject();
		
		Map<String, Object> query = Maps.newHashMap();
		query.put("uniqueId", uniqueId);
		Map<String, Object> healthcheckDetail = healthCheckService.getDataByQuery("healthcheckDetail", query);
		if (healthcheckDetail != null) {
			json.putAll(healthcheckDetail);
			
			String diseaseHistory = "";
	    	if (healthcheckDetail.get("haveDisease").equals("是")) {
	    		diseaseHistory = healthcheckDetail.get("disease").toString();
	    	} else if(healthcheckDetail.get("haveDisease").equals("否")){
	    		diseaseHistory = "否";
	    	}
	    	
	    	json.put("diseaseHistory", diseaseHistory);
	    	
	    	String familyHistory = "";
	    	if (healthcheckDetail.get("familyHistory").equals("是")) {
	    		familyHistory = healthcheckDetail.get("familyDisease").toString();
	    	} else if(healthcheckDetail.get("familyHistory").equals("否")){
	    		familyHistory = "否";
	    	}
	    	json.put("familyHistory", familyHistory);
	    	
	    	
	    	List<String> imageList = Lists.newArrayList();
	    	if (healthcheckDetail.get("imageUrls") != null && !"".equals(healthcheckDetail.get("imageUrls"))) {
	    		String imageUrls = healthcheckDetail.get("imageUrls").toString();
	    		String[] images = imageUrls.split(",");
	    		for (String image : images) {
	    			String item = PropertyUtils.getProperty("item");
					
	    			String imageUrl = "";
					if (item.equals("fuxin")) {
						String webSignature = JwtUtil.createJWT(Constant.JWT_ID, "healthcheck", Constant.JWT_TTL);
		    	        
		    	        template.setex("webSignature" + webSignature, image, 1800);
		    	        
		    	        String baseUrl = PropertyUtils.getProperty("base.url");
		    			imageUrl = baseUrl + "/obsfile/image/" + image + ".htm?Signature=" + webSignature;
					} else {
						imageUrl = fileManageService.getFileUrl(image);
					}
	    			
	    			
	    			imageList.add(imageUrl);
	    		}
	    	}
	    	json.put("imageUrls", imageList);
	    	
	    	json.remove("haveDisease");
	    	json.remove("familyDisease");
	    	json.remove("disease");
		}
		return json;
	}
	
	
	/**
	 * 获取初筛、精筛指定日期之后的数据
	 * @param uniqueId
	 * @return
	 * @throws BaseException
	 */
	public JSONObject getHealthCheckByDate (String uniqueId, String checkTime, String checkSource) throws BaseException {
		JSONObject json = new JSONObject();
		
		String chekcDate = DateUtil.datetimeToString(DateUtil.rollDate(DateUtil.stringToDate(checkTime), 30));
		
		Map<String, Object> query = Maps.newHashMap();
		query.put("uniqueId", uniqueId);
		query.put("checkDate", new BasicDBObject("$gte", chekcDate));
		
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("checkDate", -1);
		
		if (checkSource.equals("初筛")) {
			List<Map<String, Object>> healthchecks = healthCheckService.queryList("healthcheck", query, sortMap);
			if (healthchecks != null && healthchecks.size() > 0) {
				json.putAll(healthchecks.get(0));
			}
		} else {
			List<Map<String, Object>> healthchecks = healthCheckService.queryList("healthcheckDetail", query, sortMap);
			if (healthchecks != null && healthchecks.size() > 0) {
				json.putAll(healthchecks.get(0));
			}
		}
		
		return json;
	}
	
	public JSONObject getDataByDates(String uniqueId, String startTime, String endTime, String checkSource) throws BaseException{
		JSONObject json = new JSONObject();
		
		
		Map<String, Object> query = Maps.newHashMap();
		query.put("uniqueId", uniqueId);
		query.put("checkDate", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("checkDate", -1);
		
		if (checkSource.equals("初筛")) {
			List<Map<String, Object>> healthchecks = healthCheckService.queryList("healthcheck", query, sortMap);
			if (healthchecks != null && healthchecks.size() > 0) {
				json.putAll(healthchecks.get(0));
			}
		} else {
			List<Map<String, Object>> healthchecks = healthCheckService.queryList("healthcheckDetail", query, sortMap);
			if (healthchecks != null && healthchecks.size() > 0) {
				json.putAll(healthchecks.get(0));
			}
		}
		
		return json;
	}
	
	public  JSONObject applyTokenHealthControl() {
		String webSignature = JwtUtil.createJWT(Constant.JWT_ID, "healthcheck", Constant.JWT_TTL);
		String getUrl = PropertyUtils.getProperty("healthcontrol.url") + "/hc/getTokenBySignature?webSignature=" + webSignature;
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				JSONObject object = JSONObject.parseObject(result);
				return object;
			} else {
				System.out.println("HTTP error code:"+code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
