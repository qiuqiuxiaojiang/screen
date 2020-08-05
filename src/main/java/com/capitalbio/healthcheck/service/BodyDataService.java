package com.capitalbio.healthcheck.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.healthcheck.dao.BodyDataDAO;
import com.google.common.collect.Maps;

@Service
public class BodyDataService extends BaseService{
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired private BodyDataDAO bodyDadaDAO;
	@Autowired HealthCheckService healthCheckService;
	@Autowired AuthService authService;

	public String analysisData(String json) {
		if (StringUtils.isEmpty(json)) {
			return "数据为空";
		}
		
		try {
			Map<String, Object> dataMap = JsonUtil.jsonToMap(json);
			Map<String, Object> bodyDataMap = new HashMap<String, Object>();
			logger.info(dataMap.keySet().size());
			for (String field : dataMap.keySet()) {
				if (StringUtils.isEmpty(field)) continue;
				
				Object infos = dataMap.get(field);
				//若是map集合，将数据取出来
				if (infos instanceof Map) {
					Map<String, Object> inMap = (Map<String, Object>)infos;
					for (String infield : inMap.keySet()) {
						if (StringUtils.isEmpty(infield) || infield.equals("Name")
								|| infield.equals("Mobile") || infield.equals("IdCode")
								 || infield.equals("Address")) 
							continue;
						
						Object value = inMap.get(infield);
						String keys = field + "_" + infield;
						bodyDataMap.put(keys, value);
					}
				} else {
					bodyDataMap.put(field, infos);
				}
			}
			
			//将一体机传送过来的数据保存到healthcheck表中
			String message = healthCheck(dataMap);
			if (StringUtils.isEmpty(message)) {
				String uniqueId = (String)dataMap.get("uniqueId");
				bodyDataMap.put("uniqueId", uniqueId);
			}
			bodyDataMap.remove("PEEcg_EcgImg");
			bodyDadaDAO.saveData(getCollName(), bodyDataMap);
			return message;
//			String uniqueId = healthCheck(dataMap);
//			if (uniqueId == null) {
//				bodyDataMap.put("uniqueId", "-1");
//			} else {
//				bodyDataMap.put("uniqueId", uniqueId);
//			}
//			
//			return uniqueId;
		} catch (Exception e) {
			e.printStackTrace();
			return "保存出现错误";
		}
		
	}
	
	public String healthCheck(Map<String, Object> map) {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		Map<String, Object> member = (Map<String, Object>)map.get("Member");
		String idCode = (String)member.get("IdCode");
		
		Map<String, Object> user = authService.requestInfo(idCode, token, userId);
		if (user == null) {
			return "认证错误";
		}
		String msg = (String)user.get("msg");
		if (StringUtils.isNotEmpty(msg)) {
			return msg;
		}
		String uniqueId = (String)user.get("uniqueId");
		if (StringUtils.isEmpty(uniqueId)) {
			return "未建档";
		}
		Map<String,Object> params = parseParameter(map);
		params.put("uniqueId", uniqueId);
		
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("uniqueId", uniqueId);
		Map<String, Object> customerInfo = healthCheckService.getDataByQuery("customer", queryMap);
		if (customerInfo!=null) {
			String disease = (String)customerInfo.get("disease");
			String familyHistory = (String)customerInfo.get("familyHistory");
			params.put("disease", disease);
			params.put("haveDisease", (String)customerInfo.get("haveDisease"));
			params.put("familyDisease", (String)customerInfo.get("familyDisease"));
			params.put("familyHistory", familyHistory);
			params.put("gender", customerInfo.get("gender"));
			params.put("age", customerInfo.get("age"));
			params.put("checkGroup", customerInfo.get("checkGroup"));
			params.put("checkPlace", customerInfo.get("checkPlace"));
			params.put("district", customerInfo.get("district"));
		}

		//设置年龄和性别
//		computeAgeAndSex(idCode, params);
		healthCheckService.saveData("healthCheckHistory", params);
		
//		Map<String,Object> queryMap = Maps.newHashMap();
//		queryMap.put("uniqueId", uniqueId);
		queryMap.put("checkDate", params.get("checkDate"));
		Map<String,Object> data = healthCheckService.getDataByQuery(queryMap);
		if (data == null) {
			data = Maps.newHashMap();
		}
		
		data.putAll(params);
		
		data.put("src","imacUpload");
		System.out.println("====================body data==================" + data);
		String id = healthCheckService.saveData(data);
		System.out.println("================id=================" + id);
		map.put("uniqueId", uniqueId);
		//healthCheckService.saveCustomer(data);
		return null;
	}
	
	public Map<String,Object> parseParameter(Map<String,Object> params) {
		Map<String, Object> data = Maps.newHashMap();
		
		String measureTime = (String)params.get("MeasureTime");
		Date checkTime = DateUtil.stringToDate(measureTime);
		data.put("checkDate", DateUtil.dateToString(checkTime));
		data.put("checkTime", checkTime);
		
		Map<String, Object> heightw = (Map<String, Object>)params.get("Height");
		if (heightw != null) {
			if (heightw.get("Height") != null) {
				data.put("height", ParamUtils.getDoubleValue(String.valueOf(heightw.get("Height"))));
			}
			if (heightw.get("Weight") != null) {
				data.put("weight", ParamUtils.getDoubleValue(String.valueOf(heightw.get("Weight"))));
			}
			if (heightw.get("BMI") == null) {
				if(heightw.get("Height") != null && heightw.get("Weight") != null){
					Double height = (Double)heightw.get("Height");
					double heightM = (double)((double)height/(double)100);
					Double weight = (Double)heightw.get("Weight");
					double bmi = (double)(weight.doubleValue()/(heightM*heightM));
					data.put("BMI",  bmi);
				}
			} else {
				data.put("BMI", ParamUtils.getDoubleValue(String.valueOf(heightw.get("BMI"))));
			}
		}
		
		//体温
		Map<String, Object> temperaturew = (Map<String, Object>)params.get("Temperature");
		if (temperaturew != null) {
			if (temperaturew.get("Temperature") != null) {
				data.put("temperature", ParamUtils.getDoubleValue(String.valueOf(temperaturew.get("Temperature"))));	
			}
		}
		
		//腰围，臀围，腰臀比
		Map<String, Object> whr = (Map<String, Object>)params.get("Whr");
		if (whr != null) {
			if (whr.get("Waistline") != null) {
				data.put("waistline", ParamUtils.getDoubleValue(String.valueOf(whr.get("Waistline"))));
			}
			if (whr.get("Hipline") != null) {
				data.put("hipline", ParamUtils.getDoubleValue(String.valueOf(whr.get("Hipline"))));
			}
			if (whr.get("Whr") != null) {
				Double whrValue = ParamUtils.getDoubleValue(String.valueOf(whr.get("Whr")));
				if (whrValue != null) {
					whrValue = ParamUtils.doubleScale(whrValue.doubleValue()/100, 2);
					data.put("WHR", whrValue);
				}
			}
		}
		
		//血氧、脉率
		Map<String, Object> bo = (Map<String, Object>)params.get("Bo");
		if (bo != null) {
			if (bo.get("Oxygen") != null) {
				data.put("oxygen", ParamUtils.getIntValue(String.valueOf(bo.get("Oxygen"))));
			}
			if (bo.get("Bpm") != null) {
				data.put("pulse", ParamUtils.getIntValue(String.valueOf(bo.get("Bpm"))));
			}
		}
		
		//收缩压、舒张压、脉搏
		Map<String, Object> bloodPressure = (Map<String, Object>)params.get("BloodPressure");
		if (bloodPressure != null) {
			if (bloodPressure.get("HighPressure") != null) {
				data.put("highPressure", ParamUtils.getIntValue(String.valueOf(bloodPressure.get("HighPressure"))));
			}
			if (bloodPressure.get("LowPressure") != null) {
				data.put("lowPressure", ParamUtils.getIntValue(String.valueOf(bloodPressure.get("LowPressure"))));
			}
			/*if (bloodPressure.get("Pulse") != null) {
				data.put("pulse", ParamUtils.getIntValue(String.valueOf(bloodPressure.get("Pulse"))));
			}*/
		}
		
		//
		
		//体脂率--缺少
		/*Map<String, Object> bo = (Map<String, Object>)params.get("Bo");
		if (bo != null) {
			data.put("oxygen", ParamUtils.getDoubleValue(String.valueOf(bo.get("Oxygen"))));
		}*/
		
		return data;
	}
	
//	private void computeAgeAndSex(String customerId, Map<String,Object> map) {
//        boolean valid = IdcardValidator.isValidatedAllIdcard(customerId);
//        if (!valid) {
//        	map.put("valid", "false");
//        	return;
//        } 
//        map.put("valid","true");
//        String year = customerId.substring(6,10);// 得到年份  
//        String yue = customerId.substring(10,12);// 得到月份  
//        String sex;  
//        if (Integer.parseInt(customerId.substring(16).substring(0, 1)) % 2 == 0) {// 判断性别  
//            sex = "女";  
//        } else {  
//            sex = "男";  
//        }  
//        Date date = new Date();// 得到当前的系统时间  
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
//        String fyear = format.format(date).substring(0, 4);// 当前年份  
//        String fyue = format.format(date).substring(5, 7);// 月份  
//        int age = 0;  
//        if (Integer.parseInt(yue) <= Integer.parseInt(fyue)) { // 当前月份大于用户出身的月份表示已过生日
//            age = Integer.parseInt(fyear) - Integer.parseInt(year) + 1;  
//        } else {// 当前用户还没过生日
//            age = Integer.parseInt(fyear) - Integer.parseInt(year);  
//        }  
//        map.put("gender", sex);  
//        map.put("age", age);  
//	}
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return bodyDadaDAO;
	}

	@Override
	public String getCollName() {
		return "bodyData";
	}
}
