package com.capitalbio.healthcheck.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.parser.Entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.IdcardValidator;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.eye.service.EyeRecordService;
import com.capitalbio.healthcheck.dao.HealthCheckDAO;
import com.capitalbio.healthcheck.dao.ReportDAO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;

@Service
@SuppressWarnings("unchecked")
public class HealthCheckService extends BaseService {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired HealthCheckDAO healthCheckDAO;
//	@Autowired PushDataService pushDataService;
	@Autowired ReportDAO reportDAO;
	@Autowired AuthService authService;
	@Autowired FileManageService fileManageService;
	@Autowired EyeRecordService eyeRecordService;
	@Autowired HealthControlService healthControlService;
	
	JedisTemplate template = RedisUtils.getTemplate();
	
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return healthCheckDAO;
	}

	@Override
	public String getCollName() {
		return "healthcheck";
	}
	
	public Map<String,Object> getCustomerByUniqueId(String uniqueId) {
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("uniqueId", uniqueId);
		return healthCheckDAO.getDataByQuery("customer", queryMap);
	}
	
	public Map<String, Object> getHealthCheckData(String startTime, String endTime) throws ParseException {
		return healthCheckDAO.getHealthCheckData(startTime, endTime);
	}
	
	public Map<String, Object> getBloodSugarData(String startTime, String endTime) throws Exception {
		return healthCheckDAO.getBloodSugarData(DateUtil.stringToDate(startTime), DateUtil.stringToDate(endTime));
	}
	
	public double getHcDataOfMonth(String startTime, String endTime) {
		return healthCheckDAO.getHcDataOfMonth(startTime, endTime);
	}
	
	public double getBsDataOfMonth(String startTime, String endTime) throws Exception {
		return healthCheckDAO.getBsCount(DateUtil.stringToDate(startTime), DateUtil.stringToDate(endTime));
	}
	
	public double getHcCount(String startTime, String endTime) throws Exception {
		return healthCheckDAO.getHcCount(startTime, endTime);
	}
	
	public double getBsCount(String startTime, String endTime) throws Exception {
		return healthCheckDAO.getBsCount(DateUtil.stringToDate(startTime), DateUtil.stringToDate(endTime));
	}

	/**
	 * 每个筛查用户建立一条独立数据，保留所有信息
	 * @param params
	 */
	public String saveCustomer(Map<String,Object> params, String dataSource) {
		String uniqueId = (String)params.get("uniqueId");
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("uniqueId", uniqueId);
		Map<String,Object> customer = healthCheckDAO.queryOne("customer", queryMap);
		if (customer == null) {
			customer = Maps.newHashMap();
		}
		//根据初筛日期判断是否更新数据
		Date oldDate = (Date)customer.get("checkTime");
		Date newDate = (Date)params.get("checkTime");
		if (oldDate != null && newDate != null) {
			Calendar oldc = Calendar.getInstance();
			oldc.setTime(oldDate);
			Calendar newc = Calendar.getInstance();
			newc.setTime(newDate);
			oldc.set(Calendar.HOUR, 0);
			newc.set(Calendar.HOUR, 23);
			if (newc.before(oldc)) {
				return null;
			}
		}

		customer.putAll(params);
		String id = healthCheckDAO.saveData("customer", customer);
		
		Integer age = null;
		if (params.get("age") != null && !"".equals(params.get("age"))) {
			age = Integer.parseInt(params.get("age").toString());
		}
		
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		Map<String, Object> userInfo = authService.requestInfoByUniqueId(params.get("uniqueId").toString(), token, userId);
		if (userInfo != null) {
			Map<String, Object> secretMap = Maps.newHashMap();
			secretMap.put("id", userInfo.get("customerId"));
			secretMap.put("mobile", userInfo.get("mobile"));
			secretMap.put("name", userInfo.get("name"));
			secretMap.put("gender", userInfo.get("gender"));
			secretMap.put("birthday", userInfo.get("birthday"));
			secretMap.put("nationality", userInfo.get("nationality"));
			secretMap.put("householdRegistrationType", userInfo.get("householdRegistrationType"));
			secretMap.put("contactName", userInfo.get("contactName"));
			secretMap.put("contactMobile", userInfo.get("contactMobile"));
			secretMap.put("address", userInfo.get("address"));
			secretMap.put("remarks", userInfo.get("remarks"));
			secretMap.put("userId", userInfo.get("userId"));
			secretMap.put("phone", userInfo.get("phone"));
			secretMap.put("age", age);
			
			Map<String, Object> updateDocInfo = authService.updateDocInfo(secretMap, token, userId);
			System.out.println("update:" + updateDocInfo);
		}
		
		//初筛和精筛重新计算年龄  修改认证系统
		if (StringUtils.isNotEmpty(dataSource)) {
			if (params.get("visit") != null && !"".equals(params.get("visit"))) {
				String visit = params.get("visit").toString();
				if (visit.equals("是")) {
					params.put("dataSource", dataSource);
					params.put("recordDate", customer.get("recordDate"));
					params.put("district", customer.get("district"));
					params.put("checkPlace", customer.get("checkPlace"));
					params.put("checkPlaceId", customer.get("checkPlaceId"));
					params.put("height", customer.get("height"));
					healthControlService.sendHealthCheck(params);
				}
			}
		}
		
		return id;
	}
	
	
	public Map<String,Object> parseHealthCheckDetailParameter(Map<String,Object> params){
		//{ogtt2h=21,ogtt=12, tc=1, hdl=1, tg=22, ldl=22,checkDate2=2018-05-08, checkDate3=2018-05-08,  highPressure2=12, highPressure3=12,  name=zz, customerId=1231, lowPressure2=12, lowPressure3=12}
//		if (params.get("age") != null) {
//			params.put("age", ParamUtils.getIntValue(String.valueOf(params.get("age"))));
//		} 
		if (params.get("ogtt") != null) {
			params.put("ogtt", ParamUtils.getDoubleValue(String.valueOf(params.get("ogtt"))));
		}
		if (params.get("ogtt2h") != null) {
			params.put("ogtt2h", ParamUtils.getDoubleValue(String.valueOf(params.get("ogtt2h"))));
		}
		if (params.get("tgDetail") != null) {
			params.put("tgDetail", ParamUtils.getDoubleValue(String.valueOf(params.get("tgDetail"))));
		}
		if (params.get("tcDetail") != null) {
			params.put("tcDetailStr", params.get("tcDetail"));
			if(ParamUtils.getDoubleValue(String.valueOf(params.get("tcDetail"))) == null){
				params.put("tcDetail","");
			}else{
				params.put("tcDetail", ParamUtils.getDoubleValue(String.valueOf(params.get("tcDetail"))));
			}
			
		}
		
		if (params.get("hdlDetail") != null) {
			params.put("hdlDetail", ParamUtils.getDoubleValue(String.valueOf(params.get("hdlDetail"))));
		}
		if (params.get("ldlDetail") != null) {
			params.put("ldlDetail", ParamUtils.getDoubleValue(String.valueOf(params.get("ldlDetail"))));
		}
		if (params.get("highPressure2") != null) {
			params.put("highPressure2", ParamUtils.getIntValue(String.valueOf(params.get("highPressure2"))));
		}
		if (params.get("lowPressure2") != null) {
			params.put("lowPressure2", ParamUtils.getIntValue(String.valueOf(params.get("lowPressure2"))));
		}
		if (params.get("highPressure3") != null) {
			params.put("highPressure3", ParamUtils.getIntValue(String.valueOf(params.get("highPressure3"))));
		}
		if (params.get("lowPressure3") != null) {
			params.put("lowPressure3", ParamUtils.getIntValue(String.valueOf(params.get("lowPressure3"))));
		}
		if (params.get("highPressure4") != null) {
			params.put("highPressure4", ParamUtils.getIntValue(String.valueOf(params.get("highPressure4"))));
		}
		if (params.get("lowPressure4") != null) {
			params.put("lowPressure4", ParamUtils.getIntValue(String.valueOf(params.get("lowPressure4"))));
		}
		
		String ogttDate = (String)params.get("ogttDate");
		Object ogttTimeObj = params.get("ogttTime");
		Date ogttTime = null;
		if (ogttTimeObj != null) {
			if (ogttTimeObj instanceof Date) {
				ogttTime = (Date)ogttTimeObj;
			} else if (ogttTimeObj instanceof String) {
				ogttTime = DateUtil.stringToDateTime((String)ogttTimeObj);
			} else if (ogttTimeObj instanceof Long) {
				ogttTime = new Date(((Long)ogttTimeObj).longValue());
			}
		} 
		if (ogttDate == null && ogttTime != null) {
			ogttDate = DateUtil.dateToString(ogttTime);
		} else if (ogttDate != null && ogttTime == null) {
			ogttTime = DateUtil.stringToDate(ogttDate);
		}
		params.put("ogttDate", ogttDate);
		params.put("ogttTime", ogttTime);

		String checkDate2 = (String)params.get("checkDate2");
		Object checkTimeObj2 = params.get("checkTime2");
		Date checkTime2 = null;
		if (checkTimeObj2 != null) {
			if (checkTimeObj2 instanceof Date) {
				checkTime2 = (Date)checkTimeObj2;
			} else if (checkTimeObj2 instanceof String) {
				checkTime2 = DateUtil.stringToDateTime((String)checkTimeObj2);
			} else if (checkTimeObj2 instanceof Long) {
				checkTime2 = new Date(((Long)checkTimeObj2).longValue());
			}
		} 
		if (checkDate2 == null && checkTime2 != null) {
			checkDate2 = DateUtil.dateToString(checkTime2);
		} else if (checkDate2 != null && checkTime2 == null) {
			checkTime2 = DateUtil.stringToDate(checkDate2);
		}
		params.put("checkDate2", checkDate2);
		params.put("checkTime2", checkTime2);
		
		String checkDate3 = (String)params.get("checkDate3");
		Object checkTimeObj3 = params.get("checkTime3");
		Date checkTime3 = null;
		if (checkTimeObj3 != null) {
			if (checkTimeObj3 instanceof Date) {
				checkTime3 = (Date)checkTimeObj3;
			} else if (checkTimeObj3 instanceof String) {
				checkTime3 = DateUtil.stringToDateTime((String)checkTimeObj3);
			} else if (checkTimeObj3 instanceof Long) {
				checkTime3 = new Date(((Long)checkTimeObj3).longValue());
			}
		} 
		if (checkDate3 == null && checkTime3 != null) {
			checkDate3 = DateUtil.dateToString(checkTime3);
		} else if (checkDate3 != null && checkTime3 == null) {
			checkTime3 = DateUtil.stringToDate(checkDate3);
		}
		params.put("checkDate3", checkDate3);
		params.put("checkTime3", checkTime3);

		String checkDate4 = (String)params.get("checkDate4");
		Object checkTimeObj4 = params.get("checkTime4");
		Date checkTime4 = null;
		if (checkTimeObj4 != null) {
			if (checkTimeObj4 instanceof Date) {
				checkTime4 = (Date)checkTimeObj4;
			} else if (checkTimeObj4 instanceof String) {
				checkTime4 = DateUtil.stringToDateTime((String)checkTimeObj4);
			} else if (checkTimeObj4 instanceof Long) {
				checkTime4 = new Date(((Long)checkTimeObj4).longValue());
			}
		} 
		if (checkDate4 == null && checkTime4 != null) {
			checkDate4 = DateUtil.dateToString(checkTime4);
		} else if (checkDate4 != null && checkTime4 == null) {
			checkTime4 = DateUtil.stringToDate(checkDate4);
		}
		params.put("checkDate4", checkDate4);
		params.put("checkTime4", checkTime4);
		
		params.put("detailTag", "是");
		
		//新增需求 精筛页面中去掉身高、体重、体质指数、腰围
	/*	if (params.get("heightDetail") != null) {
			params.put("heightDetail", ParamUtils.getDoubleValue(String.valueOf(params.get("heightDetail"))));
		}
		if (params.get("weightDetail") != null) {
			params.put("weightDetail", ParamUtils.getDoubleValue(String.valueOf(params.get("weightDetail"))));
		}
		if (params.get("bmiDetail") == null) {
			if(params.get("heightDetail") != null && params.get("weightDetail") != null){
				Double height = (Double)params.get("heightDetail");
				double heightM = (double)((double)height/(double)100);
				Double weight = (Double)params.get("weightDetail");
				double bmi = (double)(weight.doubleValue()/(heightM*heightM));
				params.put("bmiDetail",  bmi);
			}
		} else {
			params.put("bmiDetail", ParamUtils.getDoubleValue(String.valueOf(params.get("bmiDetail"))));
		}
		if (params.get("waistlineDetail") != null) {
			params.put("waistlineDetail", ParamUtils.getDoubleValue(String.valueOf(params.get("waistlineDetail"))));
		}*/
		if (params.get("malb") != null) {
			params.put("malb", ParamUtils.getDoubleValue(String.valueOf(params.get("malb"))));
		}
		if (params.get("ucr") != null) {
			params.put("ucr", ParamUtils.getDoubleValue(String.valueOf(params.get("ucr"))));
		}

		return params;
	}
	
	public Map<String,Object> parseDocParameter(Map<String,Object> params) {
		String customerId = (String)params.remove("customerId");
		String mobile = (String)params.remove("mobile");
		String name = (String)params.remove("name");
		String gender = (String)params.get("gender");
		if (StringUtils.isEmpty(gender)) {
			gender = getGenderByIdCard(customerId);
		}
		Integer age = ParamUtils.getIntValue(String.valueOf(params.get("age")));
		if (age == null) {
			age = getAgeByIdCard(customerId);
		}
		String phone = (String)params.get("phone");
		String birthday = (String)params.get("birthday");
		String nationality = (String)params.get("nationality");
		String householdRegistrationType = (String)params.get("householdRegistrationType");
		String contactName = (String)params.remove("contactName");
		String contactMobile = (String)params.remove("contactMobile");
		String address = (String)params.remove("address");
		Map<String,Object> secretMap = Maps.newHashMap();
		secretMap.put("id", customerId);
		secretMap.put("mobile", mobile);
		secretMap.put("phone", phone);
		secretMap.put("name", name);
		secretMap.put("gender", gender);
		secretMap.put("age", age);
		secretMap.put("birthday", birthday);
		secretMap.put("nationality", nationality);
		secretMap.put("householdRegistrationType", householdRegistrationType);
		secretMap.put("contactName", contactName);
		secretMap.put("contactMobile", contactMobile);
		secretMap.put("address", address);
		
		params.put("age", age);
		
		String haveDisease = (String)params.get("haveDisease");
		
		String disease = (String)params.get("disease");
		if (disease == null) {
			disease = "";
		}
		if (!"是".equals(haveDisease)) {
			disease = "";
		}
		params.put("disease", disease);
 		if(disease.contains("糖尿病") && "是".equals(haveDisease)){
			params.put("dm", "是");
		}else{
			params.put("dm", "");
		}
		if(disease.contains("高血压")&& "是".equals(haveDisease)){
			params.put("htn", "是");
		}else{
			params.put("htn", "");
		}
		if(disease.contains("冠心病") && "是".equals(haveDisease)){
			params.put("cpd", "是");
		}else{
			params.put("cpd", "");
		}
		if(disease.contains("高血脂") && "是".equals(haveDisease)){
			params.put("hpl", "是");
		}else{
			params.put("hpl", "");
		}
		
		String dm = (String)params.get("dm");
		if ("有".equals(dm)) {
			dm = "是";
		}
		if ("无".equals(dm)) {
			dm = "否";
		}
		params.put("dm",dm);
		String htn = (String)params.get("htn");
		if ("有".equals(htn)) {
			htn = "是";
		}
		if ("无".equals(htn)) {
			htn = "否";
		}
		params.put("htn",htn);
		String hpl = (String)params.get("hpl");
		if ("有".equals(hpl)) {
			hpl = "是";
		}
		if ("无".equals(hpl)) {
			hpl = "否";
		}
		params.put("hpl",hpl);
		String chd = (String)params.get("chd");
		if ("有".equals(chd)) {
			chd = "是";
		}
		if ("无".equals(chd)) {
			chd = "否";
		}
		params.put("chd",chd);
		String familyHistory = (String)params.get("familyHistory");
		String familyDisease = (String)params.get("familyDisease");
		if (familyDisease == null) {
			familyDisease = "";
		}
		if (!"是".equals(familyHistory)) {
			familyDisease = "";
		}
		params.put("familyDisease", familyDisease);
		if(familyDisease.contains("糖尿病") && "是".equals(familyHistory)){
			params.put("fdm", "是");
		}else{
			params.put("fdm", "");
		}
		if(familyDisease.contains("高血压") && "是".equals(familyHistory)){
			params.put("fhtn", "是");
		}else{
			params.put("fhtn", "");
		}
		if(familyDisease.contains("冠心病") && "是".equals(familyHistory)){
			params.put("fcpd", "是");
		}else{
			params.put("fcpd", "");
		}
		if(familyDisease.contains("高血脂") && "是".equals(familyHistory)){
			params.put("fhpl", "是");
		}else{
			params.put("fhpl", "");
		}
		
		/*String familyHistory = (String)params.get("familyHistory");

		if ("是".equals(familyHistory)) {
			familyHistory = "有";
		} else if ("否".equals(familyHistory)) {
			familyHistory = "无";
		} 
		if (!("有".equals(familyHistory) || "无".equals(familyHistory))) {
			familyHistory = "";
		}
		
		params.put("familyHistory", familyHistory);*/
		String recordDate = (String)params.get("recordDate");
		Date recordTime = null;
		if (recordDate != null) {
			recordTime = DateUtil.stringToDateTime(recordDate);
		} else {
			Date nowDate = new Date();
			recordDate = DateUtil.dateToString(nowDate);
			recordTime = nowDate;
		}
		params.put("recordDate", recordDate);
		params.put("recordTime", recordTime);
		return secretMap;

		
	}
	
	public Map<String,Object> parseParameter(Map<String,Object> params) {
		String customerId = params.remove("customerId").toString();
		params.remove("mobile");
		params.remove("name");
		
		Integer age = ParamUtils.getIntValue(String.valueOf(params.get("age")));
		if (age == null) {
			age = getAgeByIdCard(customerId);
		}
		params.put("age", age);
		
//		params.remove("familyHistory");
//		params.remove("disease");
//		List<String> listNullKey = new ArrayList<String>();
//		for (String key : params.keySet()) {
//			if (params.get(key)== null || StringUtils.isEmpty(params.get(key).toString())) {
//				listNullKey.add(key);
//			}
//		}
//		for (String nullKey : listNullKey) {
//			params.remove(nullKey);
//		}
		if (params.get("height") != null) {
			params.put("height", ParamUtils.getDoubleValue(String.valueOf(params.get("height"))));
		}
		if (params.get("weight") != null) {
			params.put("weight", ParamUtils.getDoubleValue(String.valueOf(params.get("weight"))));
		}
		if (params.get("BMI") == null) {
			if(params.get("height") != null && params.get("weight") != null){
				Double height = (Double)params.get("height");
				double heightM = (double)((double)height/(double)100);
				Double weight = (Double)params.get("weight");
				double bmi = (double)(weight.doubleValue()/(heightM*heightM));
				params.put("BMI",  bmi);
			}
		} else {
			params.put("BMI", ParamUtils.getDoubleValue(String.valueOf(params.get("BMI"))));
		}
		if (params.get("waistline") != null) {
			params.put("waistline", ParamUtils.getDoubleValue(String.valueOf(params.get("waistline"))));
		}
		if (params.get("hipline") != null) {
			params.put("hipline", ParamUtils.getDoubleValue(String.valueOf(params.get("hipline"))));
		}
		if (params.get("WHR") == null) {
			if (params.get("waistline") != null && params.get("hipline") != null) {
				Double waistline = (Double)params.get("waistline");
				Integer hipline = (Integer)params.get("hipline");
				params.put("WHR", (double)(waistline.doubleValue()/hipline.doubleValue()));
			}
		} else {
			params.put("WHR", ParamUtils.getDoubleValue(String.valueOf(params.get("WHR"))));
		}
		if (params.get("fatContent") != null) {
			params.put("fatContent", ParamUtils.getDoubleValue(String.valueOf(params.get("fatContent"))));
		}
		if (params.get("highPressure") != null) {
			params.put("highPressure", ParamUtils.getIntValue(String.valueOf(params.get("highPressure"))));
		}
		if (params.get("lowPressure") != null) {
			params.put("lowPressure", ParamUtils.getIntValue(String.valueOf(params.get("lowPressure"))));
		}
		if (params.get("temperature") != null) {
			params.put("temperature", ParamUtils.getDoubleValue(String.valueOf(params.get("temperature"))));
		}
		if (params.get("oxygen") != null) {
			params.put("oxygen", ParamUtils.getDoubleValue(String.valueOf(params.get("oxygen"))));
		}

		if (params.get("pulse") != null) {
			params.put("pulse", ParamUtils.getIntValue(String.valueOf(params.get("pulse"))));
		}
		if (params.get("bloodGlucose") != null) {
			params.put("bloodGlucoseStr", params.get("bloodGlucose"));
			if (String.valueOf(params.get("bloodGlucose")).indexOf("<")!=-1) {
				params.put("bloodGlucose",1.11);
			}else if (String.valueOf(params.get("bloodGlucose")).indexOf(">")!=-1) {
				params.put("bloodGlucose",33.3);
			} else {
				params.put("bloodGlucose", ParamUtils.getDoubleValue(String.valueOf(params.get("bloodGlucose"))));
			}
		}
		if (params.get("bloodGlucose2h") != null) {
			params.put("bloodGlucose2hStr", params.get("bloodGlucose2h"));
			if (String.valueOf(params.get("bloodGlucose2h")).indexOf("<")!=-1) {
				params.put("bloodGlucose2h",1.11);
			}else if (String.valueOf(params.get("bloodGlucose2h")).indexOf(">")!=-1) {
				params.put("bloodGlucose2h",33.3);
			} else {
				params.put("bloodGlucose2h", ParamUtils.getDoubleValue(String.valueOf(params.get("bloodGlucose2h"))));
			}
		}
		if (params.get("bloodGlucoseRandom") != null) {
			params.put("bloodGlucoseRandomStr", params.get("bloodGlucoseRandom"));
			if (String.valueOf(params.get("bloodGlucoseRandom")).indexOf("<")!=-1) {
				params.put("bloodGlucoseRandom",1.11);
			}else if (String.valueOf(params.get("bloodGlucoseRandom")).indexOf(">")!=-1) {
				params.put("bloodGlucoseRandom",33.3);
			} else {
				params.put("bloodGlucoseRandom", ParamUtils.getDoubleValue(String.valueOf(params.get("bloodGlucoseRandom"))));
			}
		}
		if (params.get("tg") != null) {
			params.put("tgStr", params.get("tg"));
			if (String.valueOf(params.get("tg")).indexOf("<")!=-1) {
				params.put("tg",0.57);
			}else if (String.valueOf(params.get("tg")).indexOf(">")!=-1) {
				params.put("tg",5.65);
			} else {
				params.put("tg", ParamUtils.getDoubleValue(String.valueOf(params.get("tg"))));
			}
		}
		if (params.get("tc") != null) {
			params.put("tcStr", params.get("tc"));
			if (String.valueOf(params.get("tc")).indexOf("<")!=-1) {
				params.put("tc",2.59);
			} else if (String.valueOf(params.get("tc")).indexOf(">")!=-1){
				params.put("tc",10.36);
			} else {
				params.put("tc", ParamUtils.getDoubleValue(String.valueOf(params.get("tc"))));
			}
			
		}
		
		if (params.get("hdl") != null) {
			params.put("hdlStr", params.get("hdl"));
			if (String.valueOf(params.get("hdl")).indexOf("<")!=-1) {
				params.put("hdl",0.39);
			}else if (String.valueOf(params.get("hdl")).indexOf(">")!=-1) {
				params.put("hdl",2.59);
			} else {
				params.put("hdl", ParamUtils.getDoubleValue(String.valueOf(params.get("hdl"))));
			}
		}
		if (params.get("ldl") != null) {
			params.put("ldlStr", params.get("ldl"));
			params.put("ldl", ParamUtils.getDoubleValue(String.valueOf(params.get("ldl"))));
		}
		
		String haveDisease = (String)params.get("haveDisease");
		String disease = (String)params.get("disease");
		if (disease == null) {
			disease = "";
		}
		if (!"是".equals(haveDisease)) {
			disease = "";
		}
		params.put("disease", disease);
		if(disease.contains("糖尿病") && "是".equals(haveDisease)){
			params.put("dm", "是");
		}else{
			params.put("dm", "");
		}
		if(disease.contains("高血压")&& "是".equals(haveDisease)){
			params.put("htn", "是");
		}else{
			params.put("htn", "");
		}
		if(disease.contains("冠心病") && "是".equals(haveDisease)){
			params.put("cpd", "是");
		}else{
			params.put("cpd", "");
		}
		if(disease.contains("高血脂") && "是".equals(haveDisease)){
			params.put("hpl", "是");
		}else{
			params.put("hpl", "");
		}
		String familyHistory = (String)params.get("familyHistory");
		String familyDisease = (String)params.get("familyDisease");
		if (familyDisease == null) {
			familyDisease = "";
		}
		if (!"是".equals(familyHistory)) {
			familyDisease = "";
		}
		params.put("familyDisease", familyDisease);
		if(familyDisease.contains("糖尿病") && "是".equals(familyHistory)){
			params.put("fdm", "是");
		}else{
			params.put("fdm", "");
		}
		if(familyDisease.contains("高血压") && "是".equals(familyHistory)){
			params.put("fhtn", "是");
		}else{
			params.put("fhtn", "");
		}
		if(familyDisease.contains("冠心病") && "是".equals(familyHistory)){
			params.put("fcpd", "是");
		}else{
			params.put("fcpd", "");
		}
		if(familyDisease.contains("高血脂") && "是".equals(familyHistory)){
			params.put("fhpl", "是");
		}else{
			params.put("fhpl", "");
		}
		
//		String dm = (String)params.get("dm");
//		if ("有".equals(dm)) {
//			dm = "是";
//		}
//		if ("无".equals(dm)) {
//			dm = "否";
//		}
//		params.put("dm",dm);
//		String htn = (String)params.get("htn");
//		if ("有".equals(htn)) {
//			htn = "是";
//		}
//		if ("无".equals(htn)) {
//			htn = "否";
//		}
//		params.put("htn",htn);
//		String hpl = (String)params.get("hpl");
//		if ("有".equals(hpl)) {
//			hpl = "是";
//		}
//		if ("无".equals(hpl)) {
//			hpl = "否";
//		}
//		params.put("hpl",hpl);
//		String chd = (String)params.get("chd");
//		if ("有".equals(chd)) {
//			chd = "是";
//		}
//		if ("无".equals(chd)) {
//			chd = "否";
//		}
//		params.put("chd",chd);

//		Integer riskScore = ParamUtils.getIntValue(String.valueOf(params.get("riskScore")));
//		if (riskScore == null) {
//			riskScore = computeRiskScore(params);
//		}
//		params.put("riskScore", riskScore);

//		String familyHistory = (String)params.get("familyHistory");
//
//		if ("是".equals(familyHistory)) {
//			familyHistory = "有";
//		} else if ("否".equals(familyHistory)) {
//			familyHistory = "无";
//		} 
//		if (!("有".equals(familyHistory) || "无".equals(familyHistory))) {
//			familyHistory = "";
//		}
//		
//		params.put("familyHistory", familyHistory);

		String checkDate = null;
		if (params.get("checkDate") != null) {
			checkDate = (String)params.get("checkDate");
		}
		Date checkTime = null;
		if (params.get("checkTime") != null) {
			String checkTimeObj = params.get("checkTime").toString();
			String[] checkTimeArr = checkTimeObj.split(" ");
			checkTimeObj = checkTimeArr[0]+" 00:00:00";
			checkTime = DateUtil.stringToDateTime(checkTimeObj);
		}
//		Date checkTime = null;
//		if (checkTimeObj != null) {
//			if (checkTimeObj instanceof Date) {
//				checkTime = (Date)checkTimeObj;
//			} else if (checkTimeObj instanceof String) {
//				checkTime = DateUtil.stringToDateTime((String)checkTimeObj);
//			} else if (checkTimeObj instanceof Long) {
//				checkTime = new Date(((Long)checkTimeObj).longValue());
//			}
//		} 
//		Date checkTime = (Date)params.get("checkTime");
		if (checkDate == null && checkTime != null) {
			checkDate = DateUtil.dateToString(checkTime);
		} else if (checkDate != null && checkTime == null) {
			checkTime = DateUtil.stringToDate(checkDate);
		}
		params.put("checkDate", checkDate);
		params.put("checkTime", checkTime);
		// set the report tag
		params.put("checkTag", "是");
		if (params.get("height") != null || params.get("weight") != null || params.get("waistline") != null 
				|| params.get("hipline")!= null || params.get("fatContent") != null
				|| params.get("highPressure") != null || params.get("lowPressure") != null
				|| params.get("temperature") != null || params.get("oxygen") != null
				|| params.get("pulse") != null) {
			params.put("examTag", "是");
		}
		if (params.get("bloodGlucose") != null || params.get("bloodGlucose2h") != null || params.get("bloodGlucoseRandom") != null) {
			params.put("bloodSugarTag", "是");
		}
		if (params.get("tg") != null || params.get("tgStr") != null 
				|| params.get("tc") != null || params.get("tcStr") != null
				|| params.get("hdl") != null || params.get("ldl") != null
				) {
			params.put("bloodFatTag", "是");
		}
		if (params.get("highPressure") != null || params.get("lowPressure") != null ) {
			params.put("pressureTag", "是");
		}
		return params;
	}

	public Integer computeRiskScore(Map<String,Object> data) {
		int score = 0;
		if (data.get("age") != null && data.get("age") != "") {
			Integer age = ParamUtils.getIntValue(String.valueOf(data.get("age")));
			
			if (age >=25 && age <= 34) {
				score += 4;
			} else if (age >=35 && age <= 39) {
				score += 8;
			} else if (age >=40 && age <= 44) {
				score += 11;
			} else if (age >=45 && age <= 49) {
				score += 12;
			} else if (age >=50 && age <= 54) {
				score += 13;
			} else if (age >=55 && age <= 59) {
				score += 15;
			} else if (age >=60 && age <= 64) {
				score += 16;
			} else if (age >=65) {
				score += 18;
			}
		}
		
		String gender = "";
		if (data.get("gender") != null && data.get("gender") != "") {
			gender = (String)data.get("gender");
			
			if ("男".equals(gender)) {
				score += 2;
			}
		}
		
		if (data.get("BMI") != null && data.get("BMI") != "") {
			Double bmi = ParamUtils.getDoubleValue(String.valueOf(data.get("BMI")));
			if (bmi >= 22 && bmi < 24) {
				score += 1;
			} else if (bmi >=24 && bmi < 30) {
				score += 3;
			} else if (bmi >= 30) {
				score += 5;
			}
		}
		
		if (data.get("waistline") != null && data.get("waistline") != "") {
			Double waistline = ParamUtils.getDoubleValue(String.valueOf(data.get("waistline")));
			if ("男".equals(gender)) {
				if (waistline>=75 && waistline < 80) {
					score += 3;
				} else if (waistline>=80 && waistline < 85) {
					score += 5;
				} else if (waistline>=85 && waistline < 90) {
					score += 7;
				} else if (waistline>=90 && waistline < 95) {
					score += 8;
				} else if (waistline>=95) {
					score += 10;
				}
			} else if ("女".equals(gender)){
				if (waistline>=70 && waistline < 75) {
					score += 3;
				} else if (waistline>=75 && waistline < 80) {
					score += 5;
				} else if (waistline>=80 && waistline < 85) {
					score += 7;
				} else if (waistline>=85 && waistline < 90) {
					score += 8;
				} else if (waistline>=90) {
					score += 10;
				}
			}
		}
		
		if (data.get("highPressure") != null && data.get("highPressure") != "") {
			Integer highPressure = ParamUtils.getIntValue(String.valueOf(data.get("highPressure")));
			
			if (highPressure>=110 && highPressure < 120) {
				score += 1;
			} else if (highPressure>=120 && highPressure < 130) {
				score += 3;
			} else if (highPressure>=130 && highPressure < 140) {
				score += 6;
			} else if (highPressure>=140 && highPressure < 150) {
				score += 7;
			} else if (highPressure>=150 && highPressure < 160) {
				score += 8;
			} else if (highPressure>=160) {
				score += 10;
			}
		}
			
		if (data.get("familyHistory") != null && data.get("familyHistory") != "") {
			String familyHistory = (String)data.get("familyHistory");
			
			if (data.get("familyDisease") != null && data.get("familyDisease") != "") {
				String familyDisease = (String)data.get("familyDisease");
				if ("是".equals(familyHistory) && familyDisease.contains("糖尿病")) {
					score += 6;
				}
			}
		}
		return score;
	}

//	private void computeAgeAndSex(Map<String,Object> map) {
//		String customerId = (String)map.get("customerId");
//        boolean valid = IdcardValidator.isValidatedAllIdcard(customerId);
//        if (!valid) {
//        	map.put("valid", "false");
//        	return;
//        } 
//        map.put("valid","true");
//        String year = customerId.substring(6,10);// 得到年份  
//        String yue = customerId.substring(10,12);// 得到月份  
//        String gender;  
//        if (Integer.parseInt(customerId.substring(16).substring(0, 1)) % 2 == 0) {// 判断性别  
//            gender = "女";  
//        } else {  
//            gender = "男";  
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
//        map.put("gender", gender);  
//        map.put("age", age);  
//	}

	public void processFile(File file ) throws Exception{
		FileInputStream input = new FileInputStream(file);
		XSSFWorkbook wb = new XSSFWorkbook(input);
		XSSFSheet sheet = wb.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date checkTime = new Date();
//		String checkDate = sdf.format(checkTime);
 		for (int i = 1; i < rows; i++) {
			XSSFRow row = sheet.getRow(i);
			String name = getCellValue(row.getCell(1));
			String customerId = getCellValue(row.getCell(2));
			if (StringUtils.isEmpty(customerId)) {
				continue;
			}
			if (customerId.length() != 18 && customerId.length() != 15) {
				continue;
			}
 			String contact = getCellValue(row.getCell(3));
			String dm = getCellValue(row.getCell(4));
			String htn = getCellValue(row.getCell(5));
			String hpl = getCellValue(row.getCell(6));
			String chd = getCellValue(row.getCell(7));
			Integer age = getCellIntegerValue(row.getCell(8));
			String gender = getCellValue(row.getCell(9));
			String familyHistory = getCellValue(row.getCell(10));
			Integer waistline = getCellIntegerValue(row.getCell(11));
			Integer highPressure = getCellIntegerValue(row.getCell(12));
			Integer lowPressure = getCellIntegerValue(row.getCell(13));
			Double height = getCellDoubleValue(row.getCell(14));
			Double weight = getCellDoubleValue(row.getCell(15));
			Double bmi = getCellDoubleValue(row.getCell(16));
			Double fatContent = getCellDoubleValue(row.getCell(17));
			Double temperature = getCellDoubleValue(row.getCell(18));
			Integer oxygen = getCellIntegerValue(row.getCell(19));
			Integer pulse = getCellIntegerValue(row.getCell(20));
			Double bloodGlucose = getCellDoubleValue(row.getCell(21));
			Double bloodGlucose2h = getCellDoubleValue(row.getCell(22));
			Integer riskScore = getCellIntegerValue(row.getCell(23));
			String tizhi = getCellValue(row.getCell(24));
			String eyeCheck = getCellValue(row.getCell(25));
			String checkPlace = getCellValue(row.getCell(26));
//			if (StringUtils.isEmpty(checkPlace)) {
//				checkPlace = "未知";
//			}
			String checkGroup = getCellValue(row.getCell(27));
			Date checkTime = getCellDateValue(row.getCell(28));
			
			if (checkTime == null) {
				checkTime = new Date();
			}
			String checkDate = sdf.format(checkTime);
			if ("有".equals(dm)) {
				dm = "是";
			}
			if ("无".equals(dm)) {
				dm = "否";
			}
			if ("有".equals(htn)) {
				htn = "是";
			}
			if ("无".equals(htn)) {
				htn = "否";
			}
			if ("有".equals(hpl)) {
				hpl = "是";
			}
			if ("无".equals(hpl)) {
				hpl = "否";
			}
			if ("有".equals(chd)) {
				chd = "是";
			}
			if ("无".equals(chd)) {
				chd = "否";
			}
			
			StringBuffer diseaseBuf = new StringBuffer();
			if ("是".equals(dm)) {
				diseaseBuf.append("糖尿病,");
			}
			if ("是".equals(htn)) {
				diseaseBuf.append("高血压,");
			}
			if ("是".equals(hpl)) {
				diseaseBuf.append("高血脂,");
			}
			if ("是".equals(chd)) {
				diseaseBuf.append("冠心病,");
			}
			
			if (diseaseBuf.length() > 1) {
				diseaseBuf.deleteCharAt(diseaseBuf.length() - 1);
			}

			Map<String,Object> map = Maps.newHashMap();
			map.put("name",name);
			map.put("customerId",customerId);
			map.put("contact",contact);
			map.put("dm",dm);
			map.put("htn",htn);
			map.put("hpl",hpl);
			map.put("chd",chd);
			map.put("disease", diseaseBuf.toString());
			map.put("age",age);
			map.put("gender",gender);
			if ("是".equals(familyHistory)) {
				familyHistory = "有";
			} else if ("否".equals(familyHistory)) {
				familyHistory = "无";
			} 
			if (!("有".equals(familyHistory) || "无".equals(familyHistory))) {
				familyHistory = "";
			}
			map.put("familyHistory",familyHistory);
			map.put("waistline",waistline);
			map.put("highPressure",highPressure);
			map.put("lowPressure",lowPressure);
			map.put("height",height);
			map.put("weight",weight);
			map.put("BMI",bmi);
			map.put("fatContent",fatContent);
			map.put("temperature",temperature);
			map.put("oxygen",oxygen);
			map.put("pulse",pulse);
			map.put("bloodGlucose",bloodGlucose);
			map.put("bloodGlucose2h",bloodGlucose2h);
			map.put("riskScore",riskScore);
			map.put("tizhi",tizhi);
			map.put("eyeCheck",eyeCheck);
//			map.put("known",known);
			map.put("checkPlace",checkPlace);
			map.put("checkGroup",checkGroup);
			map.put("checkTime",checkTime);
			map.put("checkDate",checkDate);

			Map<String,Object> queryMap = Maps.newHashMap();
			queryMap.put("checkDate", checkDate);
			queryMap.put("customerId", customerId);
			
			Map<String,Object> data = getDataByQuery(queryMap);
			if (data == null) {
				data = Maps.newHashMap();
			}
			data.putAll(map);

			this.saveData(data);
			reportDAO.processReport(data);
 		}
 		
		wb.close();
		input.close();
	}
	
	
	private String getCellValue(XSSFCell cell) {
		if (cell == null) {
			return "";
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			double dvalue = cell.getNumericCellValue();
			long lvalue = Math.round(dvalue);
			return String.valueOf(lvalue);
		} else {
			
			return cell.getStringCellValue();
		}
	}
	
	private Date getCellDateValue(XSSFCell cell) {
		if (cell == null) {
			return null;
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			Date date = cell.getDateCellValue();
			return date;
		}
		return null;

	}

	private Integer getCellIntegerValue(XSSFCell cell) {
		if (cell == null) {
			return null;
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			double dvalue = cell.getNumericCellValue();
			Integer ivalue = (int)dvalue;
			return ivalue;
		} else {
			try {
				Integer ivalue = Integer.parseInt(cell.getStringCellValue());
				return ivalue;
			} catch (Exception e) {}
		}
		return null;
	}

	private Double getCellDoubleValue(XSSFCell cell) {
		if (cell == null) {
			return null;
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			double dvalue = cell.getNumericCellValue();
			return dvalue;
		} else {
			try {
				Double dvalue = Double.parseDouble(cell.getStringCellValue());
				return dvalue;
			} catch (Exception e) {}
		}
		return null;
	}
	/**
	 * 筛查进度->血压检测人数->舒张压值或收缩压值不为空
	 * @return
	 */
	public int findLowHighPressureNotEmpty(String district) {
		return healthCheckDAO.findLowHighPressureNotEmpty2(district);
	}
	/**
	 * 筛查进度->血脂检测人数->总胆固醇值TC、甘油三酯值TG、高密度脂蛋白值HDL-C、低密度脂蛋白值LDL-C，以上4项任一不为空
	 * @return
	 */
	public int findTcTgHdlLdlNotEmpty(String district) {
		return healthCheckDAO.findTcTgHdlLdlNotEmpty2(district);
	}
	/**
	 *  筛查进度->OGTT检测人数->OGTT（0h）和OGTT（2h）任一项目不为空
	 * @return
	 */
	public int findOh2hNotEmpty(String district) {
		return healthCheckDAO.findOh2hNotEmpty2(district);
	}
	/**
	 * 肥胖筛查情况->腰臀比->男性＞0.9  女性＞0.8为中心型肥胖；反之则为正常
	 * @return
	 */
	public Map<String,Object> findFatNumberByWHRAndSex(String district) {
		return healthCheckDAO.findFatNumberByWHRAndSex2(district);
	}
	/**
	 * 血糖筛查情况->血糖情况人群分布->初筛数据->正常\已登记糖尿病\糖尿病高危\血糖异常
	 * @return
	 */
	public Map<String, Object> findBloodSugarConditionPeopleDistributionHeadthCheck(String district) {
		return healthCheckDAO.findBloodSugarConditionPeopleDistributionHeadthCheck2(district);
	}
	/**
	 * 血糖筛查情况->血糖情况人群分布->精筛数据->糖尿病高危\新发现糖尿病\新发现糖尿病前期40-42
	 * @return
	 */
	public Map<String, Object> findBloodSugarConditionPeopleDistributionHeadthCheckDetail(String district) {
		return healthCheckDAO.findBloodSugarConditionPeopleDistributionHeadthCheckDetail2(district);
	}
	/**
	 * 血糖筛查情况->血糖情况年龄分布->初筛数据->已登记糖尿病\糖尿病高危\血糖异常43-45
	 * @return
	 */
	public Map<String, Object> findBloodSugarConditionAgeDistributionHeadthCheck(String district) {
		List<Map<String,Object>> ageList = getAgeList();
		Map<Object,Object> m = healthCheckDAO.findBloodSugarConditionAgeDistributionHeadthCheck2(district, ageList);
		//{{ "ages" : "30-35"}=1, { "ages" : "-"}=3, { "ages" : "25-30"}=1, { "bs" : "糖尿病" , "ages" : "35-40"}=1, { "bs" : "糖尿病" , "ages" : "40-45"}=1, { "bs" : "正常" , "ages" : "25-30"}=1, { "ages" : "40-45"}=1, { "bs" : "正常" , "ages" : "-"}=2, { "bs" : "正常" , "ages" : "40-45"}=1}
		Map<String,List<Map<String,Object>>> m2Change = Maps.newHashMap();
		List<Map<String,Object>> ageList1 = ageList(ageList);
		List<Map<String,Object>> ageList2 = ageList(ageList);
		List<Map<String,Object>> ageList3 = ageList(ageList);
		List<Map<String,Object>> ageList4 = ageList(ageList);
		System.out.println("ageList:" +ageList1);
		
		m2Change.put("糖尿病患者", ageList1);
		m2Change.put("糖尿病高风险人群", ageList2);
		m2Change.put("正常", ageList3);
		m2Change.put("血糖异常人群", ageList4);
		for (Map.Entry<Object, Object> entry : m.entrySet()) { 
			Map<String,String> key = (Map<String,String>)entry.getKey();
			Object value = entry.getValue();
			String bs = key.get("bs");
			String ages = key.get("ages");
			if(!StringUtils.isEmpty(bs) && !StringUtils.isEmpty(ages)){
				switch (bs) {
				case "糖尿病患者":
					List<Map<String,Object>> mma1= m2Change.get("糖尿病患者");
					for(int i =0;i<mma1.size();i++){
						Map<String,Object> map = mma1.get(i);
						if(map.keySet().contains(ages)){
							mma1.get(i).put(ages, value);
						}
					}
					break;
				case "糖尿病高风险人群":
					List<Map<String,Object>> mma2= m2Change.get("糖尿病高风险人群");
					for(int i =0;i<mma2.size();i++){
						Map<String,Object> map = mma2.get(i);
						if(map.keySet().contains(ages)){
							mma2.get(i).put(ages, value);
						}
					}
					break;
				case "正常":
					List<Map<String,Object>> mma3= m2Change.get("正常");
					for(int i =0;i<mma3.size();i++){
						Map<String,Object> map = mma3.get(i);
						if(map.keySet().contains(ages)){
							mma3.get(i).put(ages, value);
						}
					}
					break;
				case "血糖异常人群":
					List<Map<String,Object>> mma4= m2Change.get("血糖异常人群");
					for(int i =0;i<mma4.size();i++){
						Map<String,Object> map = mma4.get(i);
						if(map.keySet().contains(ages)){
							mma4.get(i).put(ages, value);
						}
					}
					break;
				default:
					break;
				}
			}
		}
		m2Change.put("糖尿病患者", ageList1);
		m2Change.put("糖尿病高风险人群", ageList2);
		m2Change.put("正常", ageList3);
		m2Change.put("血糖异常人群", ageList4);
		
		Map<String, Object> returnMap = Maps.newHashMap();
		returnMap.put("result", m2Change);
		return returnMap;
	}
	/**
	 * 血糖筛查情况->血糖情况年龄分布->精筛数据->糖尿病高危\新发现糖尿病\新发现糖尿病前期46-48
	 * @return
	 */
	public Map<Object, Object> findBloodSugarConditionAgeDistributionHeadthCheckDetail(String district) {
		return healthCheckDAO.findBloodSugarConditionAgeDistributionHeadthCheckDetail2(district);
	}
	/**
	 * 血压筛查情况->血压情况人群分布->初筛数据->正常\已登记高血压\血压异常51-53
	 * @return
	 */
	public Map<String, Object> findBloodPressureConditionPeopleDistributionHeadthCheck(String district) {
		return healthCheckDAO.findBloodPressureConditionPeopleDistributionHeadthCheck2(district);
	}
	/**
	 * 血压筛查情况->血压情况人群分布->精筛数据->血压异常\新发现高血压54-55
	 * @return
	 */
	public Map<String, Object> findBloodPressureConditionPeopleDistributionHeadthCheckDetail(String district) {
		return healthCheckDAO.findBloodPressureConditionPeopleDistributionHeadthCheckDetail2(district);
	}
	/**
	 * 血压筛查情况->血压情况年龄分布->初筛数据->正常\已登记高血压\血压异常56-58
	 * @return
	 */
	public Map<String, Object> findBloodPressureConditionAgeDistributionHeadthCheck(String district) {
		List<Map<String,Object>> ageList = getAgeList();
		Map<Object,Object> m = healthCheckDAO.findBloodPressureConditionAgeDistributionHeadthCheck2(district, ageList);
//		Map<Object, Object> m = healthCheckDAO.findBloodPressureConditionAgeDistributionHeadthCheck2(district);
		//{{ "ages" : "30-35"}=1, { "ages" : "-"}=3, { "ages" : "25-30"}=1, { "bpc" : "高血压" , "ages" : "-"}=1, { "bpc" : "正常" , "ages" : "25-30"}=1, { "bpc" : "正常" , "ages" : "40-45"}=1, { "ages" : "40-45"}=1, { "bpc" : "正常" , "ages" : "-"}=1, { "bpc" : "高血压" , "ages" : "40-45"}=1, { "bpc" : "正常" , "ages" : "35-40"}=1}
		
		Map<String,List<Map<String,Object>>> m2Change = Maps.newHashMap();
		List<Map<String,Object>> ageList1 = ageList(ageList);
		List<Map<String,Object>> ageList2 = ageList(ageList);
		List<Map<String,Object>> ageList3 = ageList(ageList);
		
		m2Change.put("正常", ageList1);
		m2Change.put("高血压患者", ageList2);
		m2Change.put("血压异常人群", ageList3);
		for (Map.Entry<Object, Object> entry : m.entrySet()) { 
			Map<String,String> key = (Map<String,String>)entry.getKey();
			Object value = entry.getValue();
			String bpc = key.get("bpc");
			String ages = key.get("ages");
			if(!StringUtils.isEmpty(bpc) && !StringUtils.isEmpty(ages)){
				switch (bpc) {
				case "正常":
					List<Map<String,Object>> mma1= m2Change.get("正常");
					for(int i =0;i<mma1.size();i++){
						Map<String,Object> map = mma1.get(i);
						if(map.keySet().contains(ages)){
							mma1.get(i).put(ages, value);
						}
					}
					break;
				case "高血压患者":
					List<Map<String,Object>> mma2= m2Change.get("高血压患者");
					for(int i =0;i<mma2.size();i++){
						Map<String,Object> map = mma2.get(i);
						if(map.keySet().contains(ages)){
							mma2.get(i).put(ages, value);
						}
					}
					break;
				case "血压异常人群":
					List<Map<String,Object>> mma3= m2Change.get("血压异常人群");
					for(int i =0;i<mma3.size();i++){
						Map<String,Object> map = mma3.get(i);
						if(map.keySet().contains(ages)){
							mma3.get(i).put(ages, value);
						}
					}
					break;
				default:
					break;
				}
			}
		}
		Map<String, Object> returnMap = Maps.newHashMap();
		returnMap.put("result", m2Change);
		return returnMap;
	}
	/**
	 * 血压筛查情况->血压情况年龄分布->精筛数据->血压异常\新发现高血压59-60
	 * @return
	 */
	public Map<Object, Object> findBloodPressureConditionAgeDistributionHeadthCheckDetail(String district) {
		return healthCheckDAO.findBloodPressureConditionAgeDistributionHeadthCheckDetail2(district);
	}
	/**
	 * 血脂筛查情况->血脂情况人群分布->初筛数据->正常\已登记高血脂\血脂异常61-63
	 * @return
	 */
	public Map<String, Object> findBloodLipidConditionPeopleDistributionHeadthCheck(String district) {
		return healthCheckDAO.findBloodLipidConditionPeopleDistributionHeadthCheck2(district);
	}
	/**
	 * 血脂筛查情况->血脂情况人群分布->精筛数据->血脂异常\新发现高血脂64-65
	 * @return
	 */
	public Map<String, Object> findBloodLipidConditionPeopleDistributionHeadthCheckDetail(String district) {
		return healthCheckDAO.findBloodLipidConditionPeopleDistributionHeadthCheckDetail2(district);
	}
	/**
	 * 血脂筛查情况->血脂情况人群分布->初筛数据->正常\已登记高血脂\血脂异常66-68
	 * @return
	 */
	public Map<String, Object> findBloodLipidConditionAgeDistributionHeadthCheck(String district) {
		List<Map<String,Object>> ageList = getAgeList();
		Map<Object,Object> m = healthCheckDAO.findBloodLipidConditionAgeDistributionHeadthCheck2(district, ageList);
//		Map<Object, Object> m = healthCheckDAO.findBloodLipidConditionAgeDistributionHeadthCheck2(district);
		
		Map<String,List<Map<String,Object>>> m2Change = Maps.newHashMap();
		List<Map<String,Object>> ageList1 = ageList(ageList);
		List<Map<String,Object>> ageList2 = ageList(ageList);
		List<Map<String,Object>> ageList3 = ageList(ageList);
		
		m2Change.put("血脂异常患者", ageList1);
		m2Change.put("血脂异常高风险人群", ageList2);
		m2Change.put("正常", ageList3);
		
		for (Map.Entry<Object, Object> entry : m.entrySet()) { 
			Map<String,String> key = (Map<String,String>)entry.getKey();
			Object value = entry.getValue();
			String blc = key.get("blc");
			String ages = key.get("ages");
			if(!StringUtils.isEmpty(blc) && !StringUtils.isEmpty(ages)){
				switch (blc) {
				case "血脂异常患者":
					List<Map<String,Object>> mma1= m2Change.get("血脂异常患者");
					for(int i =0;i<mma1.size();i++){
						Map<String,Object> map = mma1.get(i);
						if(map.keySet().contains(ages)){
							mma1.get(i).put(ages, value);
						}
					}
					break;
				case "血脂异常高风险人群":
					List<Map<String,Object>> mma2= m2Change.get("血脂异常高风险人群");
					for(int i =0;i<mma2.size();i++){
						Map<String,Object> map = mma2.get(i);
						if(map.keySet().contains(ages)){
							mma2.get(i).put(ages, value);
						}
					}
					break;
				case "正常":
					List<Map<String,Object>> mma3= m2Change.get("正常");
					for(int i =0;i<mma3.size();i++){
						Map<String,Object> map = mma3.get(i);
						if(map.keySet().contains(ages)){
							mma3.get(i).put(ages, value);
						}
					}
					break;
				default:
					break;
				}
			}
		}
		Map<String, Object> returnMap = Maps.newHashMap();
		returnMap.put("result", m2Change);
		return returnMap;
	}
	/**
	 * 血脂筛查情况->血脂情况人群分布->初筛数据->新发现高血脂\血脂异常69-70
	 * @return
	 */
	public Map<Object, Object> findBloodLipidConditionAgeDistributionHeadthCheckDetail(String district) {
		return healthCheckDAO.findBloodLipidConditionAgeDistributionHeadthCheckDetail2(district);
	}
	
	public Map<String, Object> parseFile(File file, String fileRecordId) throws Exception {
		Map<String, Object> result = Maps.newHashMap();
    	FileInputStream input = null;
    	XSSFWorkbook wb = null;
    	List<String> errMsgList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		List<List<String>> valueList = Lists.newArrayList();
		try {
		    input = new FileInputStream(file);
			wb = new XSSFWorkbook(input);
			XSSFSheet sheet = wb.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
			
			for (int i = 1; i < rows; i++) {
				List<String> cellList = Lists.newArrayList();
				
				String errMsg = "";
				XSSFRow row = sheet.getRow(i);
				
				//姓名
				String name = getCellValue(row.getCell(0));
				if (StringUtils.isEmpty(name)) {
					errMsg += "姓名不能为空;";
				}
				
				//身份证号
				String customerId = getCellValue(row.getCell(1));
				String gender = "";
				int age = 0;
				String birthday = "";
				if (StringUtils.isEmpty(customerId)) {
					errMsg += "身份证号不能为空;";
				} else {
					boolean valid = IdcardValidator.isValidatedAllIdcard(customerId);
			        if (!valid) {
			        	errMsg += "身份证号非法;";
			        } else {
			        	gender = getGenderByIdCard(customerId);
			        	age = getAgeByIdCard(customerId);
			        	birthday = getBirthdayByIdCard(customerId);
			        }
				}
				
				//手机号
		        String mobile = getCellValue(row.getCell(2));
		        if (StringUtils.isEmpty(mobile)) {
		        	errMsg += "手机号不能为空;";
		        } else {
		        	boolean mobileValid = isMobileNum(mobile);
			        if (!mobileValid) {
			        	errMsg += "手机号非法;";
			        }
		        }
		        
		        //检测时间
		        String checkDate = getCellValue(row.getCell(3));
		        if (StringUtils.isEmpty(checkDate)) {
		        	errMsg += "检测时间不能为空;";
		        }
		        
		        //糖尿病
		        String tnb = getCellValue(row.getCell(4));
		        if (StringUtils.isNotEmpty(tnb) && (!tnb.equals("是") && !tnb.equals("否"))) {
		        	errMsg += "请规范填写糖尿病;";
		        } 
		        
		        //高血压
		        String gxy = getCellValue(row.getCell(5));
		        if (StringUtils.isNotEmpty(gxy) && (!gxy.equals("是") && !gxy.equals("否"))) {
		        	errMsg += "请规范填写高血压;";
		        }
		        
		        //高血脂
		        String gxz = getCellValue(row.getCell(6));
		        if (StringUtils.isNotEmpty(gxz) && (!gxz.equals("是") && !gxz.equals("否"))) {
		        	errMsg += "请规范填写高血脂;";
		        }
		        
		        //冠心病
		        String gxb = getCellValue(row.getCell(7));
		        if (StringUtils.isNotEmpty(gxb) && (!gxb.equals("是") && !gxb.equals("否"))) {
		        	errMsg += "请规范填写冠心病;";
		        }
		        
		        //家族史
		        String familyHistory = getCellValue(row.getCell(8));
		        if (StringUtils.isEmpty(familyHistory)) {
		        	errMsg += "家族史不能为空;";
		        } else if (!familyHistory.equals("有") && !familyHistory.equals("无")) {
		        	errMsg += "请规范填写家族史;";
		        }
		        
		        //建档日期
		        String recordDate = getCellValue(row.getCell(9));
		        if (StringUtils.isEmpty(recordDate)) {
		        	errMsg += "建档日期不能为空;";
		        }
		        
		        //身高
		        Double height = getCellDoubleValue(row.getCell(10));
		        if (height != null) {
		        	if (height <= 120) {
		        		errMsg += "请规范填写身高;";
		        	}
		        } 
		        
		        //体重
		        Double weight = getCellDoubleValue(row.getCell(11));
		        
		        //BMI
		        Double BMI = getCellDoubleValue(row.getCell(12));
		        if (BMI != null) {
		        	BMI = ParamUtils.doubleScale(BMI, 1);
		        }
		        
		        //腰围
		        Double waistline = getCellDoubleValue(row.getCell(13));
		        
		        //收缩压
		        Integer highPressure = getCellIntegerValue(row.getCell(14));
		        
		        //舒张压
		        Integer lowPressure = getCellIntegerValue(row.getCell(15));
		        
		        //脉率
		        Double pulse = getCellDoubleValue(row.getCell(16));
		        
		        //体温
		        Double temperature = getCellDoubleValue(row.getCell(17));
		        
		        //血氧
		        Double oxygen = getCellDoubleValue(row.getCell(18));
		        
		        //臀围
		        Double hipline = getCellDoubleValue(row.getCell(19));
		        
		        //腰臀比
		        //String WHR = getCellValue(row.getCell(22));
		        
		        //体脂率
		        Double fatContent = getCellDoubleValue(row.getCell(20));
		        if (fatContent != null) {
		        	fatContent = ParamUtils.doubleScale(fatContent, 1);
		        }
		        
		        //空腹血糖
		        String bloodGlucose = getCellValue(row.getCell(21));
		        
		        //餐后2h血糖
		        String bloodGlucose2h = getCellValue(row.getCell(22));
		        
		        //随机血糖
		        String bloodGlucoseRandom = getCellValue(row.getCell(23));
		        
		        //总胆固醇
		        String tc = getCellValue(row.getCell(24));
		        
		        //甘油三酯
		        String tg = getCellValue(row.getCell(25));
		        
		        //血清低密度脂蛋白
		        String ldl = getCellValue(row.getCell(26));
		        
		        //血清高密度脂蛋白
		        String hdl = getCellValue(row.getCell(27));
		        
		        //中医体质辨识结果
		        String tizhi = getCellValue(row.getCell(28));
		        
		        //中医眼象辨识
		        String eyeCheck = getCellValue(row.getCell(29));
		        if (StringUtils.isNotEmpty(eyeCheck)) {
		        	if (!eyeCheck.equals("已检测") && !eyeCheck.equals("未检测")) {
		        		errMsg += "请规范填写家族史;";
		        	}
		        }
		        
		        //民族
		        String nationality = getCellValue(row.getCell(30));
		        
		        //常驻类型
		        String householdRegistrationType = getCellValue(row.getCell(31));
		        if (StringUtils.isNotEmpty(householdRegistrationType)) {
		        	if (!householdRegistrationType.equals("户籍") && !householdRegistrationType.equals("未户籍")) {
		        		errMsg += "请规范填写常驻类型;";
		        	}
		        }
		        
		        //联系人姓名
		        String contactName = getCellValue(row.getCell(32));
		        
		        //联系人电话
		        String contactMobile = getCellValue(row.getCell(33));
		        
		        //地址
		        String address = getCellValue(row.getCell(34));
		        
		        //筛查区域
		        String district = getCellValue(row.getCell(35));
		        
		        //筛查地点
		        String checkPlace = getCellValue(row.getCell(36));
		        
		        //筛查组
		        String checkGroup = getCellValue(row.getCell(37));
		        
//		        System.out.println("errMsg:" + errMsg);
		        cellList.add(errMsg);
		        cellList.add(name);
		        cellList.add(customerId);
		        cellList.add(mobile);
		        cellList.add(checkDate);
		        cellList.add(tnb);
		        cellList.add(gxy);
		        cellList.add(gxz);
		        cellList.add(gxb);
		        cellList.add(familyHistory);
		        cellList.add(recordDate);
		        if (height != null) {
		        	 cellList.add(String.valueOf(height));
		        } else {
		        	cellList.add("");
		        }
		        
		        if (weight != null) {
		        	 cellList.add(String.valueOf(weight));
		        } else {
		        	cellList.add("");
		        }
		        
		        if (BMI != null) {
		        	 cellList.add(String.valueOf(BMI));
		        } else {
		        	cellList.add("");
		        }
		        
		        if (waistline != null) {
		        	 cellList.add(String.valueOf(waistline));
		        } else {
		        	cellList.add("");
		        }
		        
		        if (highPressure != null) {
		        	 cellList.add(String.valueOf(highPressure));
		        } else {
		        	cellList.add("");
		        }
		        
		        if (lowPressure != null) {
		        	 cellList.add(String.valueOf(lowPressure));
		        } else {
		        	cellList.add("");
		        }
		        
		        if (pulse != null) {
		        	 cellList.add(String.valueOf(pulse));
		        } else {
		        	cellList.add("");
		        }
		        
		        if (temperature != null) {
		        	 cellList.add(String.valueOf(temperature));
		        } else {
		        	cellList.add("");
		        }
		        
		        if (oxygen != null) {
		        	 cellList.add(String.valueOf(oxygen));
		        } else {
		        	cellList.add("");
		        }
		        
		        if (hipline != null) {
		        	 cellList.add(String.valueOf(hipline));
		        } else {
		        	cellList.add("");
		        }
		        
		        if (fatContent != null) {
		        	 cellList.add(String.valueOf(fatContent));
		        } else {
		        	cellList.add("");
		        }
		        cellList.add(bloodGlucose);
		        cellList.add(bloodGlucose2h);
		        cellList.add(bloodGlucoseRandom);
		        cellList.add(tc);
		        cellList.add(tg);
		        cellList.add(ldl);
		        cellList.add(hdl);
		        cellList.add(tizhi);
		        cellList.add(eyeCheck);
		        cellList.add(nationality);
		        cellList.add(householdRegistrationType);
		        cellList.add(contactName);
		        cellList.add(contactMobile);
		        cellList.add(address);
		        cellList.add(district);
		        cellList.add(checkPlace);
		        cellList.add(checkGroup);
		        /*for (int r = 0; r <= 37; r ++) {
		        	cellList.add(getCellValue(row.getCell(r)));
		        }*/
		        
		        valueList.add(cellList);
		        
		        //没有错误信息，导入数据
		        errMsgList.add(errMsg);
		        if (errMsgList.toString().equals("[]")) {
//		        	if (errMsgList != null && errMsgList.size() == 0) {
		        	Map<String, Object> map = Maps.newHashMap();
		        	map.put("name", name);
		        	map.put("customerId", customerId);
		        	map.put("gender", gender);
		        	map.put("age", age);
		        	map.put("birthday", birthday);
		        	map.put("mobile", mobile);
		        	map.put("checkDate", checkDate);
		        	map.put("checkTime", DateUtil.stringToDate(checkDate));
		        	
		        	String disease = "";
		        	if (StringUtils.isNotEmpty(tnb)) {
		        		if (tnb.equals("是")) {
		        			disease += "糖尿病" + ","; 
			        		map.put("dm", "是");
		        		}
		        		
		        		if (tnb.equals("否")) {
			        		map.put("dm", "否");
		        		}
		        	}
		        	
		        	if (StringUtils.isNotEmpty(gxy)) {
		        		if (gxy.equals("是")) {
		        			disease += "高血压" + ","; 
			        		map.put("htn", "是");
		        		}
		        		
		        		if (gxy.equals("否")) {
			        		map.put("htn", "否");
		        		}
		        	}
		        	
		        	if (StringUtils.isNotEmpty(gxz)) {
		        		if (gxz.equals("是")) {
		        			disease += "高血脂" + ","; 
			        		map.put("hpl", "是");
		        		}
		        		
		        		if (gxz.equals("否")) {
			        		map.put("hpl", "否");
		        		}
		        	}
		        	
		        	if (StringUtils.isNotEmpty(gxb)) {
		        		if (gxb.equals("是")) {
		        			disease += "冠心病" + ","; 
			        		map.put("cpd", "是");
		        		}
		        		
		        		if (gxb.equals("否")) {
		        			map.put("cpd", "是");
		        		}
		        	}
		        	
		        	if (StringUtils.isNotEmpty(disease)) {
		        		disease = disease.substring(0, disease.length() - 1);
		        	}
		        	map.put("disease", disease);
		        	
		        	if (StringUtils.isNotEmpty(familyHistory)) {
		        		if ("是".equals(familyHistory)) {
			    			familyHistory = "有";
			    		} else if ("否".equals(familyHistory)) {
			    			familyHistory = "无";
			    		} 
		        	} else {
		        		familyHistory = "";
		        	}
		        	map.put("familyHistory", familyHistory);
		        	map.put("recordDate", recordDate);
		        	map.put("height", height);
		        	map.put("weight", weight);
		        	map.put("BMI", BMI);
		        	map.put("waistline", waistline);
		        	map.put("highPressure", highPressure);
		        	map.put("lowPressure", lowPressure);
		        	map.put("pulse", pulse);
		        	map.put("temperature", temperature);
		        	map.put("oxygen", oxygen);
		        	map.put("hipline", hipline);
		        	map.put("fatContent", fatContent);
		        	map.put("tizhi", tizhi);
		        	map.put("eyeCheck", eyeCheck);
		        	map.put("nationality", nationality);
		        	map.put("householdRegistrationType", householdRegistrationType);
		        	map.put("contactName", contactName);
		        	map.put("contactMobile", contactMobile);
		        	map.put("address", address);
		        	map.put("district", district);
		        	map.put("checkPlace", checkPlace);
		        	map.put("checkGroup", checkGroup);
		        	
		        	//计算腰臀比
		        	Double WHR = (double) 0;
		        	if (waistline != null && hipline != null) {
		        		WHR = ParamUtils.doubleScale((waistline / hipline), 2);
		        	}
		        	map.put("WHR", WHR);
		        	
		        	//计算血糖分数
		        	int riskScore = computeRiskScore(map);
		        	map.put("riskScore", riskScore);
		        	
		        	//空腹血糖
		        	Double bloodGlucoseVal = (double) 0;
		        	if (StringUtils.isNotEmpty(bloodGlucose)) {
		        		map.put("bloodGlucoseStr", bloodGlucose);
		    			if (bloodGlucose.indexOf("<")!=-1) {
		    				bloodGlucoseVal = 1.11;
		    			}else if (bloodGlucose.indexOf(">")!=-1) {
		    				bloodGlucoseVal = 33.3;
		    			} else {
		    				bloodGlucoseVal = Double.parseDouble(bloodGlucose);
		    			}
		    		}
		        	map.put("bloodGlucose", bloodGlucoseVal);
		        	
		        	//糖后两小时
		        	Double bloodGlucose2hVal = (double) 0;
		        	if (StringUtils.isNotEmpty(bloodGlucose2h)) {
		        		map.put("bloodGlucose2hStr", bloodGlucose2h);
		    			if (bloodGlucose2h.indexOf("<")!=-1) {
		    				bloodGlucose2hVal = 1.11;
		    			}else if (bloodGlucose2h.indexOf(">")!=-1) {
		    				bloodGlucose2hVal = 33.3;
		    			} else {
		    				bloodGlucose2hVal = Double.parseDouble(bloodGlucose2h);
		    			}
		    		}
		        	map.put("bloodGlucose2h", bloodGlucose2hVal);
		        	
		        	//随机血糖
		        	Double bloodGlucoseRandomVal = (double) 0;
		        	if (StringUtils.isNotEmpty(bloodGlucoseRandom)) {
		        		map.put("bloodGlucoseRandomStr", bloodGlucoseRandom);
		    			if (bloodGlucoseRandom.indexOf("<")!=-1) {
		    				bloodGlucoseRandomVal = 1.11;
		    			}else if (bloodGlucoseRandom.indexOf(">")!=-1) {
		    				bloodGlucoseRandomVal = 33.3;
		    			} else {
		    				bloodGlucoseRandomVal = Double.parseDouble(bloodGlucoseRandom);
		    			}
		    		}
		        	map.put("bloodGlucoseRandom", bloodGlucoseRandomVal);
		        	
		        	//血糖情况
		        	Map<String, Object> bloodSugarCon = bloodGlucoseCon(tnb, bloodGlucoseVal, bloodGlucose2hVal, bloodGlucoseRandomVal, riskScore);
		        	
		        	//血压情况
		        	Map<String, Object> bloodPressureCon = bloodPressureCon(gxy, highPressure, lowPressure);
		        	
	           		Double tgNumber = (double) 0;
		        	if (StringUtils.isNotEmpty(tg)) {
		        		map.put("tgStr", tg);
		    			if(tg.indexOf(">") != -1){
		    				tgNumber = 5.65;
		    			}else if(tg.indexOf("<") != -1){
		    				tgNumber = 0.57;
		    			}else{
		    				tgNumber = Double.parseDouble(tg);
		    			}
		    			map.put("tg", tgNumber);
		    		} else {
		    			map.put("tg", tg);
		    		}
		        	
		        	Double tcNumber = (double) 0;
		    		if (StringUtils.isNotEmpty(tc)) {
		    			map.put("tcStr", tc);
		    			if(tc.indexOf(">") != -1){
		    				tcNumber = 10.36;
		    			}else if(tc.indexOf("<") != -1){
		    				tcNumber = 2.59;
		    			}else{
		    				tcNumber = Double.parseDouble(tc);
		    			}
		    			map.put("tc", tcNumber);
		    		} else {
		    			map.put("tc", tc);
		    		}
		    		
		    		Double hdlNumber = (double) 0;
		    		if (StringUtils.isNotEmpty(hdl)) {
		    			map.put("hdlStr",hdl);
		    			if(hdl.indexOf(">") != -1){
		    				hdlNumber = 2.59;
		    			}else if(hdl.indexOf("<") != -1){
		    				hdlNumber = 0.39;
		    			}else{
		    				hdlNumber = Double.parseDouble(hdl);
		    			}
		    			map.put("hdl", hdlNumber);
		    		} else {
		    			map.put("hdl", hdl);
		    		}
		    		
		    		
		    		if (StringUtils.isNotEmpty(ldl)) {
		    			map.put("ldlStr", ldl);
		    			map.put("ldl", ParamUtils.getDoubleValue(ldl));
		    		}
		        	
		    		//血脂情况
		    		Map<String, Object> bloodLipidCon = bloodLipidCon(gxz, tcNumber, tgNumber, hdlNumber);
		        	
		        	String fatCondition = getFatCon(BMI);
		        	
		        	Map<String, Object> classifyResultMap = classifyResult(bloodSugarCon, bloodPressureCon, bloodLipidCon, fatCondition);
		    		map.putAll(classifyResultMap);
		        	
//		        	System.out.println("map:" + map);
		        	list.add(map);
		        }
			}
			
			if (!errMsgList.toString().equals("[]")) {
				String tempDir = PropertyUtils.getProperty("system.temp.dir");
				File dir = new File(tempDir, "errfile");
			    if(!dir.exists()){
			    	dir.mkdirs();
			    }
			    
				String uuid =  UUID.randomUUID().toString();
				File newFile = new File(dir, uuid + ".xlsx");
				exportFile(newFile.getAbsolutePath(), valueList, errMsgList);
				
				//上传OSS
				String fileId = uuid + ".xlsx";
				fileManageService.uploadFile(newFile, fileId);
//				String fileUrl = fileManageService.getFileUrl(fileId);
				
				result.put("errFileId", fileId);
//				result.put("errFileUrl", fileUrl);
				
			}
			
			wb.close();
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
		
		
		result.put("errMsgList", errMsgList);
		result.put("list", list);
		return result;
	}
	
	public void uploadFile(File file, String fileRecordId, Map<String, Object> tokenMap) throws Exception {
		//解析文件
		Map<String, Object> map = parseFile(file, fileRecordId);
		
		//上传成功
		Map<String, Object> obj = getData("fileUploadRecord", fileRecordId);
		String endTime = "";
		if (map.get("errMsgList").toString().equals("[]") && map.get("list") != null) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
			saveDataInfo(list, tokenMap);
			
			endTime = DateUtil.datetimeToString(new Date());
			obj.put("state", "上传成功");
			
			//删除临时文件
			file.delete(); 
			
		} else {//上传文件失败
			endTime = DateUtil.datetimeToString(new Date());
			obj.put("state", "上传失败");
			obj.put("errFileId", map.get("errFileId"));
			//obj.put("errFileUrl", map.get("errFileUrl"));
			file.delete(); 
		}
		
		obj.put("endTime", endTime);
		//保存上传文件状态
		saveData("fileUploadRecord", obj);
	}
	
	public void saveDataInfo(List<Map<String, Object>> list, Map<String, Object> tokenMap) {
		for (Map<String, Object> obj : list) {
			if (tokenMap == null) {
				tokenMap = authService.applyToken();
			}
			String customerId = obj.get("customerId").toString();
			Map<String,Object> secretMap = authService.requestInfo(customerId, tokenMap.get("token").toString(), tokenMap.get("userId").toString());
//			System.out.println("===secretMap===" + secretMap);
			
			Map<String,Object> secretInfo = parseDocParameter(obj);
			String uniqueId = "";
			if (secretMap == null) {//未建档
				Map<String,Object> authMap = authService.newDocInfo(secretInfo, tokenMap.get("token").toString(), tokenMap.get("userId").toString());
				if (authMap != null) {
					uniqueId = (String)authMap.get("uniqueId");
				}
			} else { //已建档
				uniqueId = secretMap.get("uniqueId").toString();
				secretMap.putAll(secretInfo);
				authService.updateDocInfo(secretMap, tokenMap.get("token").toString(), tokenMap.get("userId").toString());
			}
			
			obj.put("uniqueId", uniqueId);
			// save history
			saveData("customerHistory", obj);
			
			//保存当天最新记录
			Map<String,Object> queryMap = Maps.newHashMap();
			queryMap.put("checkDate", obj.get("checkDate"));
			queryMap.put("uniqueId", uniqueId);
			Map<String,Object> data = getDataByQuery(queryMap);
			if (data == null) {
				data = Maps.newHashMap();
			}
			data.putAll(obj);
			String id = saveData(data);
			
			obj.put("healthCheckId", id);
			
			//保存最新记录
			saveCustomer(obj, "");
		}
	}
	
	public Map<String, Object> classifyResult(Map<String, Object> bloodSugarCon, Map<String, Object> bloodPressureCon, 
			Map<String, Object> bloodLipidCon, String fatCondition) {
		String classifyResult = "";
		String bloodSugarCondition = "";
		String bloodLipidCondition = "";
		String bloodPressureCondition = "";
		
		if (bloodSugarCon.get("bloodSugarCondition") != null && bloodSugarCon.get("bloodSugarCondition") != ""){
			bloodSugarCondition = bloodSugarCon.get("bloodSugarCondition").toString();
		}
		
		if (bloodLipidCon.get("bloodLipidCondition") != null && bloodLipidCon.get("bloodLipidCondition") != "") {
			bloodLipidCondition = bloodLipidCon.get("bloodLipidCondition") + ",";
    	}
		
		if (bloodPressureCon.get("bloodPressureCondition") != null && bloodPressureCon.get("bloodPressureCondition") != "") {
			bloodPressureCondition = bloodPressureCon.get("bloodPressureCondition") + ",";
    	}
		
    	if (StringUtils.isNotEmpty(bloodSugarCondition) && !bloodSugarCon.get("bloodSugarCondition").equals("正常")) {
    		classifyResult += bloodSugarCon.get("bloodSugarCondition") + ",";
    	}
    	
    	if (StringUtils.isNotEmpty(bloodLipidCondition) && !bloodLipidCon.get("bloodLipidCondition").equals("正常")) {
    		classifyResult += bloodLipidCon.get("bloodLipidCondition") + ",";
    	}
    	
    	if (StringUtils.isNotEmpty(bloodPressureCondition) && !bloodPressureCon.get("bloodPressureCondition").equals("正常")) {
    		classifyResult += bloodPressureCon.get("bloodPressureCondition") + ",";
    	}
    	
    	if (StringUtils.isNotEmpty(fatCondition) && !fatCondition.equals("正常")) {
    		classifyResult += fatCondition + ",";
		}
		
    	if (StringUtils.isEmpty(bloodSugarCondition) &&StringUtils.isEmpty(bloodLipidCondition) &&
    			StringUtils.isEmpty(bloodPressureCondition) && StringUtils.isEmpty(fatCondition)) {
    		classifyResult = "检测信息缺失，无法判断";
    	} else if(bloodSugarCon.get("bloodSugarCondition") == "正常" && bloodPressureCon.get("bloodPressureCondition") == "正常" 
				&& bloodLipidCon.get("bloodLipidCondition") == "正常" && fatCondition == "正常") {
			classifyResult = "初筛检测指标无异常";
		} else if (StringUtils.isNotEmpty(classifyResult)) {
			classifyResult = classifyResult.substring(0, classifyResult.length() - 1);
		}
    	
    	String geneReportCs = "";
    	String item = PropertyUtils.getProperty("item");
    	String dmTag = bloodSugarCon.get("dmTag").toString();
    	if (item.equals("fuxin")) {
    		if (classifyResult.indexOf("糖尿病患者")!=-1) {
        		geneReportCs += "糖尿病用药套餐,";
    		} else if (classifyResult.indexOf("血糖异常人群")!=-1) {
    			if (dmTag.equals("疑似糖尿病")) {
    				geneReportCs += "糖尿病用药套餐,";
    			}
    		}
    	} else {
    		if (classifyResult.indexOf("糖尿病患者")!=-1) {
        		geneReportCs += "糖尿病用药套餐或糖尿病基因检测4项,";
    		} else if (classifyResult.indexOf("血糖异常人群")!=-1) {
    			if (dmTag.equals("疑似糖尿病")) {
    				geneReportCs += "糖尿病用药套餐或糖尿病基因检测4项,";
    			}
    		}
    	}
		
		if (classifyResult.indexOf("高血压患者")!=-1) {
			geneReportCs += "高血压用药套餐,";
		}
		
		if (classifyResult.indexOf("血脂异常患者")!=-1) {
			geneReportCs += "他汀类降脂药套餐,";
		}
		
		if (StringUtils.isNotEmpty(geneReportCs)) {
			geneReportCs = geneReportCs.substring(0, geneReportCs.length() -1);
		}
    	
    	
    	String rqflResult = classifyResult;
    	if (rqflResult.equals("肥胖人群") || rqflResult.equals("超重人群")) {
			if (bloodSugarCondition.equals("正常") && bloodLipidCondition.equals("正常") && bloodPressureCondition.equals("正常")) {
				rqflResult = "初筛检测指标无异常";
			} else {
				rqflResult = "";
			}
		} else if (rqflResult.indexOf("肥胖人群")!=-1) {
			rqflResult = rqflResult.replace("肥胖人群", "");
		} else if (rqflResult.indexOf("超重人群")!=-1) {
			rqflResult = rqflResult.replace("超重人群", "");
		}
    	
    	String geneTest = "";
    	
    	if (rqflResult.equals("") || rqflResult.equals("检测信息缺失，无法判断")) {
    		geneTest = "";
    	} else if(rqflResult.equals("初筛检测指标无异常")) {
    		geneTest = "不需要";
    	} else if (rqflResult.indexOf("患者")!=-1 && rqflResult.indexOf("人群")==-1) {
    		geneTest = "需要";
    	} else if (rqflResult.indexOf("人群")!=-1 && rqflResult.indexOf("患者")==-1){
    		if (dmTag.equals("疑似糖尿病")) {
    			if(rqflResult.indexOf("血压异常人群")!=-1 || rqflResult.indexOf("血脂异常高风险人群")!=-1) {
        			geneTest = "需要（进入精筛环节）";
    			} else {
    				geneTest = "需要";
    			}
    		} else {
    			geneTest = "不需要（进入精筛环节）";
    		}
    		
    	} else if (rqflResult.indexOf("患者")!=-1 && rqflResult.indexOf("人群")!=-1) {
    		if (dmTag.equals("疑似糖尿病") || rqflResult.indexOf("糖尿病患者")!=-1) {
    			if (rqflResult.indexOf("血压异常人群")!=-1 || rqflResult.indexOf("血脂异常高风险人群")!=-1) {
    				geneTest = "需要（进入精筛环节）";
				} else {
					geneTest = "需要";
				}
    		} else {
    			geneTest = "不需要（进入精筛环节）";
    		}
    	} else{
    		geneTest = "不需要";
    	}
    	
    	Map<String, Object> map = Maps.newHashMap();
    	map.put("classifyResult", classifyResult);
    	map.put("geneReportCs", geneReportCs);
    	map.put("geneTest", geneTest);
    	
		return map;
	}
	
	//血糖情况
	public Map<String, Object> bloodGlucoseCon(String tnb, Double bloodGlucoseVal, Double bloodGlucose2hVal, Double bloodGlucoseRandomVal, Integer riskScore) {
		if (bloodGlucoseVal == null) {
			bloodGlucoseVal = (double) 0;
		}
		
		if (bloodGlucose2hVal == null) {
			bloodGlucose2hVal = (double) 0;
		}
		
		if (bloodGlucoseRandomVal == null) {
			bloodGlucoseRandomVal = (double) 0;
		}
		
		if (riskScore == null) {
			riskScore = 0;
		}
		
    	String bloodSugarCondition = "";
    	String OGTTTest = "";
    	String dmRisk = "";
    	String dmTag = "";
    	String dmRiskType = "";
    	if (StringUtils.isNotEmpty(tnb) && tnb.equals("糖尿病")) {
    		if (bloodGlucoseVal > 0 && bloodGlucoseVal <= 3.9
    				|| bloodGlucoseVal >= 16.7) {
    			bloodSugarCondition = "糖尿病患者";
        		OGTTTest = "不需要";
        		dmRisk = "立即转诊";
        		dmRiskType = "3";
    		} else {
    			bloodSugarCondition = "糖尿病患者";
        		OGTTTest = "不需要";
        		dmRisk = "高";
        		dmRiskType = "2";
    		}
    		
	    } else {
	    	if (bloodGlucoseVal >= 16.7) {
	    		bloodSugarCondition = "血糖异常人群";
        		OGTTTest = "不需要";
        		dmRisk = "提醒就医";
        		dmTag = "疑似糖尿病";
        		dmRiskType = "10";
	    	} else {
	    		if (riskScore<25) {
		    		 if ((bloodGlucoseVal>=3.9 && bloodGlucoseVal<6.1) 
							 || (bloodGlucose2hVal >=3.9 && bloodGlucose2hVal<7.8) 
							 || (bloodGlucoseRandomVal>=3.9 && bloodGlucoseRandomVal < 11.1)) {
								 bloodSugarCondition = "正常";
								 dmRisk = "低";
								 OGTTTest = "不需要";
								 dmTag = "";
								 dmRiskType = "1";
					} else if ((bloodGlucoseVal>=6.1 && bloodGlucoseVal<7.0)
							|| (bloodGlucose2hVal >=7.8 && bloodGlucose2hVal<11.1)) {
						 bloodSugarCondition = "血糖异常人群";
						 OGTTTest = "需要";
						 dmRisk = "高";
						 dmTag = "疑似糖尿病前期";
						 dmRiskType = "6";
					} else if (bloodGlucoseVal>=7.0 || bloodGlucose2hVal >=11.1 || bloodGlucoseRandomVal>=11.1) {
						 bloodSugarCondition = "血糖异常人群";
						 OGTTTest = "不需要";
						 dmRisk = "提醒就医";
						 dmTag = "疑似糖尿病";
						 dmRiskType = "8";
					} else if ((bloodGlucoseVal>0 && bloodGlucoseVal<3.9) 
							|| (bloodGlucose2hVal>0 && bloodGlucose2hVal <3.9) 
							|| (bloodGlucoseRandomVal>0 && bloodGlucoseRandomVal<3.9)) {
						 bloodSugarCondition = "血糖异常人群";
						 OGTTTest = "不需要";
						 dmRisk = "立即转诊";
						 dmTag = "低血糖";
						 dmRiskType = "11";
					} else {
						 bloodSugarCondition = "";
						 OGTTTest = "";
						 dmRisk = "无法判断";
						 dmTag = "";
						 dmRiskType = "0";
					}
		    	 } else {
		    		 if ((bloodGlucoseVal>=3.9 && bloodGlucoseVal<6.1) 
							 || (bloodGlucose2hVal >=3.9 && bloodGlucose2hVal<7.8) 
							 || (bloodGlucoseRandomVal>=3.9 && bloodGlucoseRandomVal < 11.1)) {
		    			 
								 bloodSugarCondition = "糖尿病高风险人群";
								 OGTTTest = "需要";
								 dmRisk = "高";
								 dmTag = "";
								 dmRiskType = "4";
								 
					} else if ((bloodGlucoseVal>=6.1 && bloodGlucoseVal<7.0)
							|| (bloodGlucose2hVal >=7.8 && bloodGlucose2hVal<11.1)) {
						 
						 bloodSugarCondition = "血糖异常人群";
						 OGTTTest = "需要";
						 dmRisk = "高";
						 dmTag = "疑似糖尿病前期";
						 dmRiskType = "7";
						 
					} else if (bloodGlucoseVal>=7.0 || bloodGlucose2hVal >=11.1 
							|| bloodGlucoseRandomVal>=11.1
							|| bloodGlucoseRandomVal>=11.1) {
						 
						 bloodSugarCondition = "血糖异常人群";
						 OGTTTest = "不需要";
						 dmRisk = "提醒就医";
						 dmTag = "疑似糖尿病";
						 dmRiskType = "9";
						 
					} else if ((bloodGlucoseVal>0 && bloodGlucoseVal<3.9) 
							|| (bloodGlucose2hVal>0 && bloodGlucose2hVal <3.9) 
							|| (bloodGlucoseRandomVal>0 && bloodGlucoseRandomVal<3.9)) {
						 
						 bloodSugarCondition = "糖尿病高风险人群";
						 OGTTTest = "不需要";
						 dmRisk = "立即转诊";
						 dmTag = "低血糖";
						 dmRiskType = "5";
						 
					} else {
						 bloodSugarCondition = "";
						 OGTTTest = "";
						 dmRisk = "无法判断";
						 dmTag = "";
						 dmRiskType = "0";
					} 
		    	 }
	    	}
	    	
	    	 
	    }
    	
    	Map<String, Object> map = Maps.newHashMap();
    	map.put("bloodSugarCondition", bloodSugarCondition);
    	map.put("OGTTTest", OGTTTest);
    	map.put("dmRisk", dmRisk);
    	map.put("dmTag", dmTag);
    	map.put("dmRiskType", dmRiskType);
    	return map;
	}
	
	
	//血压情况
	public Map<String, Object> bloodPressureCon(String gxy, Integer highPressure, Integer lowPressure) {
   		String bloodPressureCondition = "";
   		String bloodPressureTest = "";
   		if (StringUtils.isNotEmpty(gxy) && gxy.equals("高血压")) {
   			bloodPressureCondition = "高血压患者";
   			bloodPressureTest = "不需要";
   	    } else {
   	    	if (highPressure == null && lowPressure == null) {
   	   			bloodPressureCondition = "";
   	   			bloodPressureTest = "";
   	   		} else if((highPressure > 0 && highPressure < 130) && (lowPressure > 0 && lowPressure < 85)){
   	   			bloodPressureCondition = "正常";
   				bloodPressureTest = "不需要";
   			} else if((highPressure != null && highPressure >= 130) || (lowPressure != null && lowPressure >= 85)){
   				bloodPressureCondition = "血压异常人群";
   				bloodPressureTest = "需要";
   			}
   	    }
   		
   		Map<String, Object> map = Maps.newHashMap();
   		map.put("bloodPressureCondition", bloodPressureCondition);
   		map.put("bloodPressureTest", bloodPressureTest);
   		return map;
   	}
	
	//血脂情况
	public Map<String, Object> bloodLipidCon(String gxz, Double tcNumber, Double tgNumber, Double hdlNumber) {
		if (tcNumber == null) {
			tcNumber = (double) 0;
		}
		
		if (tgNumber == null) {
			tgNumber = (double) 0;
		}
		
		if (hdlNumber == null) {
			hdlNumber = (double) 0;
		}
		
		String bloodLipidCondition = "";
		String bloodLipidTest = "";
		if (StringUtils.isNotEmpty(gxz) && gxz.equals("高血脂")) {
			bloodLipidCondition = "血脂异常患者";
			bloodLipidTest = "不需要";
	    } else {
	    	if(tcNumber>=5.2 || tgNumber>=1.7 || (hdlNumber>0 && hdlNumber<1)){
				bloodLipidCondition = "血脂异常高风险人群";
				bloodLipidTest = "需要";
			} else if((tcNumber>0 && tcNumber<5.2) && (tgNumber>0 && tgNumber<1.7) && hdlNumber>=1){
				bloodLipidCondition = "正常";
				bloodLipidTest = "不需要";
			}
	    }
		
		Map<String, Object> map = Maps.newHashMap();
    	map.put("bloodLipidCondition", bloodLipidCondition);
    	map.put("bloodLipidTest", bloodLipidTest);
    	return map;
	}
	
	public String getFatCon(Double BMI) {
		String fatCondition = "";
		if (BMI != null) {
			if (BMI >= 24 && BMI < 28) {
				fatCondition = "超重人群";
			} else if (BMI >= 28) {
				fatCondition = "肥胖人群";
			} else {
				fatCondition = "正常";
			}
		}
		return fatCondition;
	}

	
	public static int getAgeByIdCard(String customerId) {
		int age = 0;   
		
		String year = customerId.substring(6,10);// 得到年份  
	    String month = customerId.substring(10,12);// 得到月份  
		Date date = new Date();// 得到当前的系统时间  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
        String fyear = format.format(date).substring(0, 4);// 当前年份  
        String fmonth = format.format(date).substring(5, 7);// 月份  
       
        if (Integer.parseInt(month) <= Integer.parseInt(fmonth)) { // 当前月份大于用户出身的月份表示已过生日
            age = Integer.parseInt(fyear) - Integer.parseInt(year);  
        } else {// 当前用户还没过生日
            age = Integer.parseInt(fyear) - Integer.parseInt(year) - 1;  
        }  
		return age;
	}
	
	public static int getAgeByIdCard(String customerId, String str) {
		int age = 0;   
		
		String year = customerId.substring(6,10);// 得到年份  
	    String month = customerId.substring(10,12);// 得到月份  
		 
        String fyear = str.substring(0, 4);// 当前年份  
        String fmonth = str.substring(5, 7);// 月份  
       
        if (Integer.parseInt(month) <= Integer.parseInt(fmonth)) { // 当前月份大于用户出身的月份表示已过生日
            age = Integer.parseInt(fyear) - Integer.parseInt(year);  
        } else {// 当前用户还没过生日
            age = Integer.parseInt(fyear) - Integer.parseInt(year) - 1;  
        }  
		return age;
	}
	
	public static String getBirthdayByIdCard(String customerId) {
		String year = customerId.substring(6,10);// 得到年份  
	    String month = customerId.substring(10,12);// 得到月份  
	    String day = customerId.substring(12,14);// 得到月份  
	    String birthday = year + "-" + month + "-" + day;   
		
		return birthday;
	}
	
	public static boolean isMobileNum(String telNum){
		Pattern p = Pattern.compile("^1\\d{10}$");
		Matcher m = p.matcher(telNum);
		return m.matches();
	}
	
	public static String getGenderByIdCard(String customerId) {
		String gender;  
		if (Integer.parseInt(customerId.substring(16).substring(0, 1)) % 2 == 0) {// 判断性别  
			gender = "女";  
        } else {  
        	gender = "男";  
        }
		return gender;
	}
	
	
	private void exportFile(String path, List<List<String>> values, List<String> errMsgList) throws Exception {
		List<String> titles = Lists.newArrayList();
		titles.add("错误规则");
		titles.add("*姓名");
		titles.add("*身份证号");
		titles.add("*手机号");
		titles.add("*检测时间");
		titles.add("糖尿病");
		titles.add("高血压");
		titles.add("高血脂");
		titles.add("冠心病");
		titles.add("*家族史");
		titles.add("*建档日期");
		titles.add("身高cm");
		titles.add("体重kg");
		titles.add("体质指数（BMI）");
		titles.add("腰围cm");
		titles.add("收缩压mmHg");
		titles.add("舒张压mmHg");
		titles.add("脉率次/分钟");
		titles.add("体温℃");
		titles.add("血氧（%）");
		titles.add("臀围（cm）");
		titles.add("体脂率%");
		titles.add("空腹血糖（mmol/L）");
		titles.add("餐后2h血糖（mmol/L）");
		titles.add("随机血糖（mmol/L）");
		titles.add("总胆固醇（mmol/L）");
		titles.add("甘油三酯（mmol/L）");
		titles.add("低密度脂蛋白（mmol/L）");
		titles.add("高密度脂蛋白（mmol/L）");
		titles.add("中医体质辨识结果");
		titles.add("中医眼象辨识");
		titles.add("民族");
		titles.add("常驻类型");
		titles.add("联系人姓名");
		titles.add("联系人电话");
		titles.add("地址");
		titles.add("*筛查区域");
		titles.add("*筛查地址");
		titles.add("建档组");
        Workbook workbook = new XSSFWorkbook();
        // 生成一个表格
        Sheet sheet = workbook.createSheet();
        
        // 设置表格默认列宽度为15个字节
       // sheet.setDefaultColumnWidth((short) 15);
        /*
         * 创建标题行
         */
        Row row = sheet.createRow(0);
        // 存储标题在Excel文件中的序号
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
        }
        
        for (int r = 0; r < values.size(); r ++) {
        	 Row valueRow = sheet.createRow(r+1);
        	 List<String> rowList = values.get(r);
        	 for (int j = 0; j < rowList.size(); j++) {
                 Cell cell = valueRow.createCell(j);
                 cell.setCellValue(rowList.get(j));
             }
        }
       
        /*
         * 写入到文件中
         */
        File file = new File(path);
        OutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
        
    }
	
		List<Map<String,Object>> ageList = null;
	public List<Map<String,Object>> getAgeList() {
		try {
			ageList = healthCheckDAO.getAgeList();
		} catch (Exception e) {
			logger.debug("年龄段获取错误", e);
		}
		return ageList;
	}
	
	public List<Map<String,Object>> getAgeListDoc() {
		try {
			ageList = healthCheckDAO.getAgeListDoc();
		} catch (Exception e) {
			logger.debug("年龄段获取错误", e);
		}
		return ageList;
	}
	
	public List<Map<String,Object>> ageList(List<Map<String,Object>> ageList) {
		List<Map<String,Object>> retlist = Lists.newArrayList();
		for (Map<String,Object> ageMap:ageList) {
			Map<String,Object> newMap = Maps.newHashMap();
			String title = (String)ageMap.get("title");
			newMap.put(title, 0);
			retlist.add(newMap);
		}
		return retlist;
	}
	
	public Map<String,Object> ageToMap(List<Map<String,Object>> ageList) {
		Map<String,Object> retMap = Maps.newHashMap();
		for (Map<String,Object> ageMap:ageList) {
			String title = (String)ageMap.get("title");
			retMap.put(title, 0);
		}
		return retMap;
	}
	
	public List<String> getAges(List<Map<String,Object>> ageList) {
		List<String> lists = Lists.newArrayList();
		for (Map<String, Object> obj : ageList) {
			String title = obj.get("title").toString();
			lists.add(title);
		}
		return lists;
	}
	
	/**
	 * 建档人数
	 * @param district
	 * @return
	 */
	public Long findRecordConditionCount(String district) {
		Map<String, Object> query = Maps.newHashMap();
		if (StringUtils.isNotEmpty(district)) {
			query.put("district", district);
		}
		Long count = healthCheckDAO.countByQuery("customer", query);
		if (count == null) {
			count = (long) 0;
		}
		return count;
	}

	
	public Map<String, Object> findRecordConditionByAge(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//建档人数
		Long count = jdAgeCount(district, "age");
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Map<String,Object>> ageList = getAgeListDoc();
		List<String> ages = getAges(ageList);//String[] ages = {"35-39", "40-44", "45-49", "50-54", "55-59", "60-65"};
		List<Map<String,Object>> ageList1 = ageList(ageList);
		//[{35岁以下=3}, {35-39=1}, {40-44=1}, {45-49=1}, {50-54=2}, {55-59=1}, {60-65=1}, {65岁以上=1}]
		
		//建档人数年龄分布                    
		Map<String, Object> ageCountMap = healthCheckDAO.findRecordCountByAge(district, ageList);
		//{50-54=2, 40-44=1, 55-59=1, 45-49=1, 35-39=1, 60-65=1, -=4}
		
		if (count != 0 && ageCountMap != null) {
			for (Map.Entry<String, Object> entry : ageCountMap.entrySet()) { 
				String age = entry.getKey();
				Object value = entry.getValue();
				
				for(int i = 0; i < ageList1.size(); i ++){
					Map<String,Object> map = ageList1.get(i);
					if(map.keySet().contains(age)){
						ageList1.get(i).put(age, value);
					}
				}
			}
		}
		
		Double percSum = (double) 0;
		for (int i = 0; i < ageList1.size(); i ++) {
			Map<String, Object> obj = ageList1.get(i);
			for (Map.Entry<String, Object> entry : obj.entrySet()) { 
				Integer value = Integer.parseInt(entry.getValue().toString());
				
				Double perc = (double) 0;
				if (i == (ageList1.size() -1)) {
					perc = ParamUtils.doubleScale(100-percSum, 1);
					if (perc < 0 || value == 0) {
						perc = (double) 0;
					}
					percSum += perc;
				} else {
					if (count == 0) {
						perc = (double) 0;
					} else {
						perc = ParamUtils.doubleScale(value.doubleValue() / count.doubleValue() * 100, 1);
					}
					percSum += perc;
				}
				countList.add(value);
				percList.add(perc);
				
				Map<String, Object> map = Maps.newHashMap();
				if (i == 0 || i == (ageList1.size() -1)) {
					map.put("age", entry.getKey());
				} else {
					map.put("age", entry.getKey() + "岁");
				}
				
				map.put("count", value);
				list.add(map);
			}
		}
		
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("list", list);
		result.put("count", count);
		result.put("ageList", ages);
		return result;
	}
	
	public Map<String, Object> findRecordConditionByGender(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//建档人数
		Long count = jdAgeCount(district, "gender");
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		Integer maleCount = 0;
		Integer femaleCount = 0;
		Double malePerc = (double) 0;
		Double femalePerc = (double) 0;
		if (count != 0) {
			Map<String, Object> recordConditionByGenderMap = healthCheckDAO.findRecordCountByGender(district);
			if (recordConditionByGenderMap != null) {
				if (recordConditionByGenderMap.get("男") != null) {
					maleCount = Integer.parseInt(recordConditionByGenderMap.get("男").toString());
					malePerc = ParamUtils.doubleScale(maleCount.doubleValue() / count.doubleValue() * 100, 1);
				} 
				
				if (recordConditionByGenderMap.get("女") != null) {
					femaleCount = Integer.parseInt(recordConditionByGenderMap.get("女").toString());
//					femalePerc = ParamUtils.doubleScale(femaleCount.doubleValue() / count.doubleValue() * 100, 1);
					femalePerc = ParamUtils.doubleScale(100-malePerc, 1);
					if (femalePerc < 0) {
						femalePerc = (double) 0;
					}
				}
			}
			
		}
		
		countList.add(maleCount);
		countList.add(femaleCount);
		
		percList.add(malePerc);
		percList.add(femalePerc);
		
		Map<String, Object> maleMap = Maps.newHashMap();
		maleMap.put("item", "男");
		maleMap.put("count", maleCount);
		maleMap.put("percent", malePerc);
		list.add(maleMap);
		
		Map<String, Object> femaleMap = Maps.newHashMap();
		femaleMap.put("item", "女");
		femaleMap.put("count", femaleCount);
		femaleMap.put("percent", femalePerc);
		list.add(femaleMap);
		
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("result", list);
		return result;
	}
	
	/**
	 * 血糖情况人群分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodSugarConditionPeopleDistribution(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//总人数
		Long count = healthcheckCount(district, "bloodSugarCondition");
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		Integer diabetesCount = 0;
		Integer bloodAbnormalCount = 0;
		Integer highRiskCount = 0;
		Integer normalCount = 0;
		
		Double diabetesPerc = (double) 0;
		Double bloodAbnormalPerc = (double) 0;
		Double highRiskPerc = (double) 0;
		Double normalPerc = (double) 0;
		
		Map<String, Object> bloodSugarConditionMap = healthCheckDAO.findBloodSugarConditionPeopleDistributionHeadthCheck2(district);
		
		if(count != 0 && bloodSugarConditionMap != null) {
			if (bloodSugarConditionMap.get("糖尿病患者") != null) {
				diabetesCount = Integer.parseInt(bloodSugarConditionMap.get("糖尿病患者").toString());
				diabetesPerc = ParamUtils.doubleScale(diabetesCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodSugarConditionMap.get("血糖异常人群") != null) {
				bloodAbnormalCount = Integer.parseInt(bloodSugarConditionMap.get("血糖异常人群").toString());
				bloodAbnormalPerc = ParamUtils.doubleScale(bloodAbnormalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodSugarConditionMap.get("糖尿病高风险人群") != null) {
				highRiskCount = Integer.parseInt(bloodSugarConditionMap.get("糖尿病高风险人群").toString());
				highRiskPerc = ParamUtils.doubleScale(highRiskCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodSugarConditionMap.get("正常") != null) {
				normalCount = Integer.parseInt(bloodSugarConditionMap.get("正常").toString());
				//normalPerc = ParamUtils.doubleScale(normalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (normalCount == 0) {
				normalPerc = (double) 0;
				
				if (highRiskCount != 0) {
					highRiskPerc = ParamUtils.doubleScale((100 - diabetesPerc - bloodAbnormalPerc), 1);
				} else {
					if (bloodAbnormalPerc != 0) {
						bloodAbnormalPerc = ParamUtils.doubleScale((100 - diabetesPerc), 1);
					}
				}
			} else {
				normalPerc = ParamUtils.doubleScale((100 - diabetesPerc - bloodAbnormalPerc - highRiskPerc), 1);
				if (normalPerc < 0) {
					normalPerc = (double) 0;
				}
			}
			
		}
		
		countList.add(diabetesCount);
		countList.add(bloodAbnormalCount);
		countList.add(highRiskCount);
		countList.add(normalCount);
		
		percList.add(diabetesPerc);
		percList.add(bloodAbnormalPerc);
		percList.add(highRiskPerc);
		percList.add(normalPerc);
		
		Map<String, Object> diabetesMap = Maps.newHashMap();
		diabetesMap.put("item", "糖尿病患者");
		diabetesMap.put("count", diabetesCount);
		diabetesMap.put("percent", diabetesPerc);
		list.add(diabetesMap);
		
		Map<String, Object> bloodAbnormalMap = Maps.newHashMap();
		bloodAbnormalMap.put("item", "血糖异常人群");
		bloodAbnormalMap.put("count", bloodAbnormalCount);
		bloodAbnormalMap.put("percent", bloodAbnormalPerc);
		list.add(bloodAbnormalMap);
		
		Map<String, Object> highRiskMap = Maps.newHashMap();
		highRiskMap.put("item", "糖尿病高风险人群");
		highRiskMap.put("count", highRiskCount);
		highRiskMap.put("percent", highRiskPerc);
		list.add(highRiskMap);
		
		Map<String, Object> normalMap = Maps.newHashMap();
		normalMap.put("item", "血糖正常人群");
		normalMap.put("count", normalCount);
		normalMap.put("percent", normalPerc);
		list.add(normalMap);
		
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("list", list);
		return result;
	}
	
	/**
	 * 血糖筛查情况 -> 血糖情况年龄分布(柱状图) -> 初筛数据
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodSugarConditionAgeDistributionHeadthCheck2(String district) {
		List<Map<String,Object>> ageList = getAgeList();
		Map<String, Object> map = findBloodSugarConditionAgeDistributionHeadthCheck(district);
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> list11 = Lists.newArrayList();
		List<Integer> list22 = Lists.newArrayList();
		List<Integer> list33 = Lists.newArrayList();
		List<Integer> list44 = Lists.newArrayList();
		//ageList1 = {"35-39", "40-44", "45-49", "50-54", "55-59", "60-65"};
		List<String> ageList1 = getAges(ageList);
		
		if (map != null) {
			Map<String, Object> result = (Map<String, Object>) map.get("result");
			List<Map<String, Object>> list1 = (List<Map<String, Object>>) result.get("糖尿病患者");
			List<Map<String, Object>> list2 = (List<Map<String, Object>>) result.get("血糖异常人群");
			List<Map<String, Object>> list3 = (List<Map<String, Object>>) result.get("糖尿病高风险人群");
			List<Map<String, Object>> list4 = (List<Map<String, Object>>) result.get("正常");
			
			for (int i = 0; i < list1.size(); i ++) {
				Map<String, Object> map1 = Maps.newHashMap();
				map1.put("type", ageList1.get(i) + "岁");
				map1.put("disease", "糖尿病患者");
				map1.put("count", list1.get(i).get(ageList1.get(i)));
				list.add(map1);
				list11.add(Integer.parseInt(list1.get(i).get(ageList1.get(i)).toString()));
				
				Map<String, Object> map2 = Maps.newHashMap();
				map2.put("type", ageList1.get(i) + "岁");
				map2.put("disease", "血糖异常人群");
				map2.put("count", list2.get(i).get(ageList1.get(i)));
				list.add(map2);
				list44.add(Integer.parseInt(list2.get(i).get(ageList1.get(i)).toString()));
				
				Map<String, Object> map3 = Maps.newHashMap();
				map3.put("type", ageList1.get(i) + "岁");
				map3.put("disease", "糖尿病高风险人群");
				map3.put("count", list3.get(i).get(ageList1.get(i)));
				list.add(map3);
				list22.add(Integer.parseInt(list3.get(i).get(ageList1.get(i)).toString()));
				
				Map<String, Object> map4 = Maps.newHashMap();
				map4.put("type", ageList1.get(i) + "岁");
				map4.put("disease", "血糖正常人群");
				map4.put("count", list4.get(i).get(ageList1.get(i)));
				list.add(map4);
				list33.add(Integer.parseInt(list4.get(i).get(ageList1.get(i)).toString()));
			}
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("list", list);
		result.put("diabetes", list11);
		result.put("highRisk", list22);
		result.put("normal", list33);
		result.put("bloodAbnormal", list44);
		result.put("ageList", ageList1);
		return result;
	}
	
	public Map<String, Object> findBloodSugarConditionPeopleDistributionByGender(String district) {
		Map<Object, Object> map = healthCheckDAO.findBloodSugarConditionPeopleDistributionByGender(district);
		//{{ "bs" : "正常" , "gender" : "女"}=2, { "bs" : "血糖异常人群"}=1, { "bs" : "糖尿病患者"}=3, { "bs" : "血糖异常人群" , "gender" : "男"}=1, { "gender" : "女"}=1, { "gender" : "男"}=1}
		
		List<Map<String, Object>> list = Lists.newArrayList();
//		String[] strs = {"糖尿病患者", "血糖异常人群", "糖尿病高风险人群", "正常"};
		List<Map<String, Object>> femaleList = getBloodSugarList();
		List<Map<String, Object>> maleList = getBloodSugarList();
		
		for (Map.Entry<Object, Object> entity : map.entrySet()) {
			Map<String,String> key = (Map<String, String>)entity.getKey();
			Object value = entity.getValue();
			String bs = key.get("bs");
			String gender = key.get("gender");
			if (StringUtils.isNotEmpty(bs) && StringUtils.isNotEmpty(gender)) {
				if (gender.equals("女")) {
					for (Map<String, Object> female : femaleList) {
						if (female.keySet().contains(bs)) {
							female.put(bs, value);
							break;
						}
					}
				} else if (gender.equals("男")) {
					for (Map<String, Object> male : maleList) {
						if (male.keySet().contains(bs)) {
							male.put(bs, value);
							break;
						}
					}
				}
			}
			
		}
		//femaleList:[{糖尿病患者=1}, {血糖异常人群=0}, {糖尿病高风险人群=0}, {正常=2}]
		//maleList:[{糖尿病患者=1}, {血糖异常人群=1}, {糖尿病高风险人群=0}, {正常=0}]
		List<Integer> list1 = Lists.newArrayList();
		List<Integer> list2 = Lists.newArrayList();
		String[] strs = {"糖尿病患者", "血糖异常人群", "糖尿病高风险人群", "正常"};
		for(int s = 0; s < strs.length; s ++) {
			String str = strs[s];
			for (int i = 0; i < femaleList.size(); i ++) {
				if (s == i) {
					if(str.equals("正常")) {
						Map<String, Object> obj1 = Maps.newHashMap();
						Map<String, Object> female = femaleList.get(i);
						obj1.put("type", "血糖正常人群");
						obj1.put("count", female.get(str));
						obj1.put("gender", "女");
						list.add(obj1);
						
						Map<String, Object> obj2 = Maps.newHashMap();
						Map<String, Object> male = maleList.get(i);
						obj2.put("type", "血糖正常人群");
						obj2.put("count", male.get(str));
						obj2.put("gender", "男");
						list.add(obj2);
						list1.add(Integer.parseInt(male.get(str).toString()));
						list2.add(Integer.parseInt(female.get(str).toString()));
					} else {
						Map<String, Object> obj1 = Maps.newHashMap();
						Map<String, Object> female = femaleList.get(i);
						obj1.put("type", str);
						obj1.put("count", female.get(str));
						obj1.put("gender", "女");
						list.add(obj1);
						
						Map<String, Object> obj2 = Maps.newHashMap();
						Map<String, Object> male = maleList.get(i);
						obj2.put("type", str);
						obj2.put("count", male.get(str));
						obj2.put("gender", "男");
						list.add(obj2);
						list1.add(Integer.parseInt(male.get(str).toString()));
						list2.add(Integer.parseInt(female.get(str).toString()));
					}
				}
			}
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("list", list);
		resultMap.put("maleList", list1);
		resultMap.put("femaleList", list2);
		return resultMap;
	}
	
	public Map<String, Object> findBloodSugarConditionPeopleDistributionByTizhi(String district) {
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿","湿热", "血瘀", "气郁", "特禀"};
		String[] tizhis1 = {"A平和质", "B气虚质", "C阳虚质", "D阴虚质", "E痰湿质","F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		String[] strs = {"糖尿病患者", "血糖异常人群", "糖尿病高风险人群", "正常"};
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> diabetes = Lists.newArrayList();
		List<Integer> highRisk = Lists.newArrayList();
		List<Integer> normal = Lists.newArrayList();
		List<Integer> bloodAbnormal = Lists.newArrayList();
		for (int t = 0; t < tizhis.length; t ++) {
			Map<String, Object> queryMap = Maps.newHashMap();
			if (StringUtils.isNotEmpty(district)) {
				queryMap.put("district", district);
			}
			
			for (int s =0; s < strs.length; s ++) {
				queryMap.put("bloodSugarCondition", strs[s]);
				Integer count = healthCheckDAO.findBloodSugarConditionPeopleDistributionByTizhi(queryMap, tizhis[t]);
				
				Map<String, Object> map = Maps.newHashMap();
				map.put("tizhi", tizhis1[t]);
				if ( strs[s].equals("正常")) {
					map.put("disease", "血糖正常人群");
				} else {
					map.put("disease", strs[s]);
				}
				
				map.put("count", count);
				list.add(map);
				
				if (strs[s].equals("糖尿病患者")) {
					diabetes.add(count);
				} else if (strs[s].equals("血糖异常人群")) {
					bloodAbnormal.add(count);
				} else if (strs[s].equals("糖尿病高风险人群")) {
					highRisk.add(count);
				} else if (strs[s].equals("正常")) {
					normal.add(count);
				}
			}
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("list", list);
		resultMap.put("diabetes", diabetes);
		resultMap.put("highRisk", highRisk);
		resultMap.put("bloodAbnormal", bloodAbnormal);
		resultMap.put("normal", normal);
		return resultMap;
	}
	
	public List<Map<String, Object>> getBloodSugarList() {
		List<Map<String, Object>> list = Lists.newArrayList();
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("糖尿病患者", 0);
		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("血糖异常人群", 0);
		list.add(map2);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("糖尿病高风险人群", 0);
		list.add(map3);
		
		Map<String, Object> map4 = Maps.newHashMap();
		map4.put("正常", 0);
		list.add(map4);
		return list;
	}
	
	public List<Map<String, Object>> getFatList() {
		List<Map<String, Object>> list = Lists.newArrayList();
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("肥胖人群", 0);
		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("超重人群", 0);
		list.add(map2);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("正常人群", 0);
		list.add(map3);
		
		Map<String, Object> map4 = Maps.newHashMap();
		map4.put("偏瘦人群", 0);
		list.add(map4);
		return list;
	}
	
	/**
	 * 血压情况人群分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodPressureConditionPeopleDistribution(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//总人数
		Long count = healthcheckCount(district, "bloodPressureCondition");
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		Integer highBloodPressCount = 0;
		Integer bloodPressAbnormalCount = 0;
		Integer normalCount = 0;
		
		Double highBloodPressPerc = (double) 0;
		Double bloodPressAbnormalPerc = (double) 0;
		Double normalPerc = (double) 0;
		
		Map<String, Object> bloodPressureCountMap = healthCheckDAO.findBloodPressureConditionPeopleDistributionHeadthCheck2(district);
		
		if (count != 0 && bloodPressureCountMap != null) {
			if (bloodPressureCountMap.get("高血压患者") != null) {
				highBloodPressCount = Integer.parseInt(bloodPressureCountMap.get("高血压患者").toString());
				highBloodPressPerc = ParamUtils.doubleScale(highBloodPressCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodPressureCountMap.get("血压异常人群") != null) {
				bloodPressAbnormalCount = Integer.parseInt(bloodPressureCountMap.get("血压异常人群").toString());
				bloodPressAbnormalPerc = ParamUtils.doubleScale(bloodPressAbnormalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodPressureCountMap.get("正常") != null) {
				normalCount = Integer.parseInt(bloodPressureCountMap.get("正常").toString());
//				normalPerc = ParamUtils.doubleScale(normalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (normalCount == 0) {
				normalPerc = (double) 0;
				if (bloodPressAbnormalCount != 0) {
					bloodPressAbnormalPerc = ParamUtils.doubleScale(100 - highBloodPressPerc, 1);
				}
			} else {
				normalPerc = ParamUtils.doubleScale(100 - highBloodPressPerc - bloodPressAbnormalPerc, 1);
				if (normalPerc < 0) {
					normalPerc = (double) 0;
				}
			}
		}
		countList.add(highBloodPressCount);
		countList.add(bloodPressAbnormalCount);
		countList.add(normalCount);
		
		percList.add(highBloodPressPerc);
		percList.add(bloodPressAbnormalPerc);
		percList.add(normalPerc);
		
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("item", "高血压患者");
		map1.put("count", highBloodPressCount);
		map1.put("percent", highBloodPressPerc);
		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("item", "血压异常人群");
		map2.put("count", bloodPressAbnormalCount);
		map2.put("percent", bloodPressAbnormalPerc);
		list.add(map2);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("item", "血压正常人群");
		map3.put("count", normalCount);
		map3.put("percent", normalPerc);
		list.add(map3);
		
	//	Map<String, Object> m = findBloodPressureConditionAgeDistributionHeadthCheck(district);
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("list", list);
		//result.putAll(m);
		return result;
	}
	
	
	public Map<String, Object> findBloodPressureConditionAgeDistributionHeadthCheck2(String district) {
		Map<String, Object> map = findBloodPressureConditionAgeDistributionHeadthCheck(district);
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> list11 = Lists.newArrayList();
		List<Integer> list22 = Lists.newArrayList();
		List<Integer> list33 = Lists.newArrayList();
		
		//ageList1 = {"35-39", "40-44", "45-49", "50-54", "55-59", "60-65"};
		List<String> ageList1 = getAges(ageList);
		
		if (map != null) {
			Map<String, Object> result = (Map<String, Object>) map.get("result");
			List<Map<String, Object>> list1 = (List<Map<String, Object>>) result.get("高血压患者");
			List<Map<String, Object>> list2 = (List<Map<String, Object>>) result.get("血压异常人群");
			List<Map<String, Object>> list3 = (List<Map<String, Object>>) result.get("正常");
			
			for (int i = 0; i < list1.size(); i ++) {
				Map<String, Object> map1 = Maps.newHashMap();
				map1.put("type", ageList1.get(i) + "岁");
				map1.put("disease", "高血压患者");
				map1.put("count", list1.get(i).get(ageList1.get(i)));
				list.add(map1);
				list11.add(Integer.parseInt(list1.get(i).get(ageList1.get(i)).toString()));
				
				Map<String, Object> map2 = Maps.newHashMap();
				map2.put("type", ageList1.get(i) + "岁");
				map2.put("disease", "血压异常人群");
				map2.put("count", list2.get(i).get(ageList1.get(i)));
				list.add(map2);
				list22.add(Integer.parseInt(list2.get(i).get(ageList1.get(i)).toString()));
				
				Map<String, Object> map4 = Maps.newHashMap();
				map4.put("type",ageList1.get(i) + "岁");
				map4.put("disease", "血压正常人群");
				map4.put("count", list3.get(i).get(ageList1.get(i)));
				list.add(map4);
				list33.add(Integer.parseInt(list3.get(i).get(ageList1.get(i)).toString()));
			}
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("list", list);
		result.put("highBlood", list11);
		result.put("bloodAbnormal", list22);
		result.put("normal", list33);
		result.put("ageList", ageList1);
		return result;
	}
	
	
	public Map<String, Object> findBloodPressureConditionPeopleDistributionByGender(String district) {
		Map<Object, Object> map = healthCheckDAO.findBloodPressureConditionPeopleDistributionByGender(district);
		//{{ "bp" : "高血压患者" , "gender" : "男"}=1, { "gender" : "女"}=1, { "bp" : "高血压" , "gender" : "女"}=1, { "bp" : "正常" , "gender" : "女"}=2, { "bp" : "高血压" , "gender" : "男"}=1, { "gender" : "男"}=1, { "bp" : "高血压患者"}=1}
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Map<String, Object>> femaleList = getBloodPressureList();
		List<Map<String, Object>> maleList = getBloodPressureList();
		
		for (Map.Entry<Object, Object> entity : map.entrySet()) {
			Map<String,String> key = (Map<String, String>)entity.getKey();
			Object value = entity.getValue();
			String bs = key.get("bp");
			String gender = key.get("gender");
			if (StringUtils.isNotEmpty(bs) && StringUtils.isNotEmpty(gender)) {
				if (gender.equals("女")) {
					for (Map<String, Object> female : femaleList) {
						if (female.keySet().contains(bs)) {
							female.put(bs, value);
							break;
						}
					}
				} else if (gender.equals("男")) {
					for (Map<String, Object> male : maleList) {
						if (male.keySet().contains(bs)) {
							male.put(bs, value);
							break;
						}
					}
				}
			}
			
		}
//		femaleList:[{高血压患者=0}, {血压异常人群=0}, {正常=0}]
//		maleList:[{高血压患者=0}, {血压异常人群=0}, {正常=0}]
		List<Integer> list1 = Lists.newArrayList();
		List<Integer> list2 = Lists.newArrayList();
		String[] strs = {"高血压患者", "血压异常人群", "正常"};
		for(int s = 0; s < strs.length; s ++) {
			String str = strs[s];
			for (int i = 0; i < femaleList.size(); i ++) {
				if (s == i) {
					if(str.equals("正常")) {
						Map<String, Object> obj1 = Maps.newHashMap();
						Map<String, Object> female = femaleList.get(i);
						obj1.put("type", "血压正常人群");
						obj1.put("count", female.get(str));
						obj1.put("gender", "女");
						list.add(obj1);
						
						Map<String, Object> obj2 = Maps.newHashMap();
						Map<String, Object> male = maleList.get(i);
						obj2.put("type", "血压正常人群");
						obj2.put("count", male.get(str));
						obj2.put("gender", "男");
						list.add(obj2);
						
						list1.add(Integer.parseInt(male.get(str).toString()));
						list2.add(Integer.parseInt(female.get(str).toString()));
					} else {
						Map<String, Object> obj1 = Maps.newHashMap();
						Map<String, Object> female = femaleList.get(i);
						obj1.put("type", str);
						obj1.put("count", female.get(str));
						obj1.put("gender", "女");
						list.add(obj1);
						
						Map<String, Object> obj2 = Maps.newHashMap();
						Map<String, Object> male = maleList.get(i);
						obj2.put("type", str);
						obj2.put("count", male.get(str));
						obj2.put("gender", "男");
						list.add(obj2);
						
						list1.add(Integer.parseInt(male.get(str).toString()));
						list2.add(Integer.parseInt(female.get(str).toString()));
					}
				}
			}
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("list", list);
		resultMap.put("maleList", list1);
		resultMap.put("femaleList", list2);
		return resultMap;
	}
	
	public List<Map<String, Object>> getBloodPressureList() {
		List<Map<String, Object>> list = Lists.newArrayList();
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("高血压患者", 0);
		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("血压异常人群", 0);
		list.add(map2);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("正常", 0);
		list.add(map3);
		return list;
	}
	
	/**
	 * 各体质高血压分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodPressurePeopleDistributionByTizhi(String district) {
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿","湿热", "血瘀", "气郁", "特禀"};
		String[] tizhis1 = {"A平和质", "B气虚质", "C阳虚质", "D阴虚质", "E痰湿质","F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		String[] strs = {"高血压患者", "血压异常人群", "正常"};
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> highBlood = Lists.newArrayList();
		List<Integer> bloodAbnormal = Lists.newArrayList();
		List<Integer> normal = Lists.newArrayList();
		for (int t = 0; t < tizhis.length; t ++) {
			Map<String, Object> queryMap = Maps.newHashMap();
			if (StringUtils.isNotEmpty(district)) {
				queryMap.put("district", district);
			}
			
			for (int s =0; s < strs.length; s ++) {
				queryMap.put("bloodPressureCondition", strs[s]);
				Integer count = healthCheckDAO.findBloodSugarConditionPeopleDistributionByTizhi(queryMap, tizhis[t]);
				
				Map<String, Object> map = Maps.newHashMap();
				map.put("tizhi", tizhis1[t]);
				map.put("disease", strs[s]);
				map.put("count", count);
				
				
				if (strs[s].equals("高血压患者")) {
					highBlood.add(count);
				} else if (strs[s].equals("血压异常人群")) {
					bloodAbnormal.add(count);
				} else if (strs[s].equals("正常")) {
					normal.add(count);
					map.put("disease", "血压正常人群");
				}
				
				list.add(map);
			}
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("list", list);
		resultMap.put("highBlood", highBlood);
		resultMap.put("bloodAbnormal", bloodAbnormal);
		resultMap.put("normal", normal);
		return resultMap;
	}
	
	public Map<String, Object> findBloodLipidPeopleDistribution(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//总人数
		Long count = healthcheckCount(district, "bloodLipidCondition");
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
			
		Map<String, Object> bloodLipidCountMap = healthCheckDAO.findBloodLipidConditionPeopleDistributionHeadthCheck2(district);
			
		Integer bloodLipidHighRiskCount = 0;
		Integer bloodLipidAbnormalCount = 0;
		Integer normalCount = 0;
		
		Double bloodLipidHighRiskPerc = (double) 0;
		Double bloodLipidAbnormalPerc = (double) 0;
		Double normalPerc = (double) 0;
		
		if (count != 0 && bloodLipidCountMap != null) {
			if (bloodLipidCountMap.get("血脂异常患者") != null) {
				bloodLipidAbnormalCount = Integer.parseInt(bloodLipidCountMap.get("血脂异常患者").toString());
				bloodLipidAbnormalPerc = ParamUtils.doubleScale(bloodLipidAbnormalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodLipidCountMap.get("血脂异常高风险人群") != null) {
				bloodLipidHighRiskCount = Integer.parseInt(bloodLipidCountMap.get("血脂异常高风险人群").toString());
				bloodLipidHighRiskPerc = ParamUtils.doubleScale(bloodLipidHighRiskCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodLipidCountMap.get("正常") != null) {
				normalCount = Integer.parseInt(bloodLipidCountMap.get("正常").toString());
//				normalPerc = ParamUtils.doubleScale(normalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			if (normalCount == 0) {
				normalPerc = (double) 0;
				if (bloodLipidHighRiskCount != 0) {
					bloodLipidHighRiskPerc = ParamUtils.doubleScale((100 - bloodLipidAbnormalPerc), 1);
				}
			} else {
				normalPerc = ParamUtils.doubleScale((100 - bloodLipidAbnormalPerc - bloodLipidHighRiskPerc), 1);
				if (normalPerc < 0) {
					normalPerc = (double) 0;
				}
			}
		}
		
		countList.add(bloodLipidAbnormalCount);
		countList.add(bloodLipidHighRiskCount);
		countList.add(normalCount);
		
		percList.add(bloodLipidAbnormalPerc);
		percList.add(bloodLipidHighRiskPerc);
		percList.add(normalPerc);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("item", "血脂异常患者");
		map2.put("count", bloodLipidAbnormalCount);
		map2.put("percent", bloodLipidAbnormalPerc);
		list.add(map2);
		
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("item", "血脂异常高风险人群");
		map1.put("count", bloodLipidHighRiskCount);
		map1.put("percent", bloodLipidHighRiskPerc);
		list.add(map1);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("item", "血脂正常人群");
		map3.put("count", normalCount);
		map3.put("percent", normalPerc);
		list.add(map3);
		
//		Map<String, Object> m = healthCheckService.findBloodLipidConditionAgeDistributionHeadthCheck(district);
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("list", list);
		return result;
	}
	
	public Map<String, Object> findBloodLipidConditionAgeDistributionHeadthCheck2(String district) {
		Map<String, Object> map = findBloodLipidConditionAgeDistributionHeadthCheck(district);
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> list11 = Lists.newArrayList();
		List<Integer> list22 = Lists.newArrayList();
		List<Integer> list33 = Lists.newArrayList();
		
		//ageList1 = {"35-39", "40-44", "45-49", "50-54", "55-59", "60-65"};
		List<String> ageList1 = getAges(ageList);
		
		if (map != null) {
			Map<String, Object> result = (Map<String, Object>) map.get("result");
			List<Map<String, Object>> list1 = (List<Map<String, Object>>) result.get("血脂异常患者");
			List<Map<String, Object>> list2 = (List<Map<String, Object>>) result.get("血脂异常高风险人群");
			List<Map<String, Object>> list3 = (List<Map<String, Object>>) result.get("正常");
			
			for (int i = 0; i < list1.size(); i ++) {
				Map<String, Object> map1 = Maps.newHashMap();
				map1.put("type", ageList1.get(i) + "岁");
				map1.put("disease", "血脂异常患者");
				map1.put("count", list1.get(i).get(ageList1.get(i)));
				list.add(map1);
				list11.add(Integer.parseInt(list1.get(i).get(ageList1.get(i)).toString()));
				
				Map<String, Object> map2 = Maps.newHashMap();
				map2.put("type", ageList1.get(i) + "岁");
				map2.put("disease", "血脂异常高风险人群");
				map2.put("count", list2.get(i).get(ageList1.get(i)));
				list.add(map2);
				list22.add(Integer.parseInt(list2.get(i).get(ageList1.get(i)).toString()));
				
				Map<String, Object> map4 = Maps.newHashMap();
				map4.put("type", ageList1.get(i) + "岁");
				map4.put("disease", "血脂正常人群");
				map4.put("count", list3.get(i).get(ageList1.get(i)));
				list.add(map4);
				list33.add(Integer.parseInt(list3.get(i).get(ageList1.get(i)).toString()));
			}
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("list", list);
		result.put("hyperlipemia", list11);
		result.put("bloodLipidAbnormal", list22);
		result.put("normal", list33);
		result.put("ageList", ageList1);
		return result;
	}

	
	public Map<String, Object> findBloodLipidConditionPeopleDistributionByGender(String district) {
		Map<Object, Object> map = healthCheckDAO.findBloodLipidConditionPeopleDistributionByGender(district);
		//{{ "bp" : "高血脂" , "gender" : "女"}=1, { "bp" : "正常"}=1, { "gender" : "女"}=1, { "bp" : "" , "gender" : "男"}=1, { "bp" : "血脂异常高风险人群" , "gender" : "男"}=1, { "bp" : "血脂异常患者"}=1, { "gender" : "男"}=1, { "bp" : "血脂异常" , "gender" : "女"}=1, { "bp" : "" , "gender" : "女"}=2}
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Map<String, Object>> femaleList = getBloodLipidList();
		List<Map<String, Object>> maleList = getBloodLipidList();
		
		for (Map.Entry<Object, Object> entity : map.entrySet()) {
			Map<String,String> key = (Map<String, String>)entity.getKey();
			Object value = entity.getValue();
			String bs = key.get("bl");
			String gender = key.get("gender");
			if (StringUtils.isNotEmpty(bs) && StringUtils.isNotEmpty(gender)) {
				if (gender.equals("女")) {
					for (Map<String, Object> female : femaleList) {
						if (female.keySet().contains(bs)) {
							female.put(bs, value);
							break;
						}
					}
				} else if (gender.equals("男")) {
					for (Map<String, Object> male : maleList) {
						if (male.keySet().contains(bs)) {
							male.put(bs, value);
							break;
						}
					}
				}
			}
		}
		
//		femaleList:[{血脂异常患者=0}, {血脂异常高风险人群=0}, {正常=0}]
//		maleList:[{血脂异常患者=0}, {血脂异常高风险人群=1}, {正常=0}]
		List<Integer> list1 = Lists.newArrayList();
		List<Integer> list2 = Lists.newArrayList();
		String[] strs = {"血脂异常患者", "血脂异常高风险人群", "正常"};
		for(int s = 0; s < strs.length; s ++) {
			String str = strs[s];
			for (int i = 0; i < femaleList.size(); i ++) {
				if (s == i) {
					if(str.equals("正常")) {
						Map<String, Object> obj1 = Maps.newHashMap();
						Map<String, Object> female = femaleList.get(i);
						obj1.put("type", "血脂正常人群");
						obj1.put("count", female.get(str));
						obj1.put("gender", "女");
						list.add(obj1);
						
						Map<String, Object> obj2 = Maps.newHashMap();
						Map<String, Object> male = maleList.get(i);
						obj2.put("type", "血脂正常人群");
						obj2.put("count", male.get(str));
						obj2.put("gender", "男");
						list.add(obj2);
						list1.add(Integer.parseInt(male.get(str).toString()));
						list2.add(Integer.parseInt(female.get(str).toString()));
					} else {
						Map<String, Object> obj1 = Maps.newHashMap();
						Map<String, Object> female = femaleList.get(i);
						obj1.put("type", str);
						obj1.put("count", female.get(str));
						obj1.put("gender", "女");
						list.add(obj1);
						
						Map<String, Object> obj2 = Maps.newHashMap();
						Map<String, Object> male = maleList.get(i);
						obj2.put("type", str);
						obj2.put("count", male.get(str));
						obj2.put("gender", "男");
						list.add(obj2);
						list1.add(Integer.parseInt(male.get(str).toString()));
						list2.add(Integer.parseInt(female.get(str).toString()));
					}
				}
			}
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("list", list);
		resultMap.put("maleList", list1);
		resultMap.put("femaleList", list2);
		return resultMap;
	}
	
	
	public List<Map<String, Object>> getBloodLipidList() {
		List<Map<String, Object>> list = Lists.newArrayList();
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("血脂异常患者", 0);
		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("血脂异常高风险人群", 0);
		list.add(map2);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("正常", 0);
		list.add(map3);
		return list;
	}
	
	
	/**
	 * 各体质血脂分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodLipidPeopleDistributionByTizhi(String district) {
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿","湿热", "血瘀", "气郁", "特禀"};
		String[] tizhis1 = {"A平和质", "B气虚质", "C阳虚质", "D阴虚质", "E痰湿质","F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		String[] strs = {"血脂异常患者", "血脂异常高风险人群", "正常"};
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> hyperlipemia = Lists.newArrayList();
		List<Integer> bloodLipidAbnormal = Lists.newArrayList();
		List<Integer> normal = Lists.newArrayList();
		
		for (int t = 0; t < tizhis.length; t ++) {
			Map<String, Object> queryMap = Maps.newHashMap();
			if (StringUtils.isNotEmpty(district)) {
				queryMap.put("district", district);
			}
			
			for (int s =0; s < strs.length; s ++) {
				queryMap.put("bloodLipidCondition", strs[s]);
				Integer count = healthCheckDAO.findBloodSugarConditionPeopleDistributionByTizhi(queryMap, tizhis[t]);
				
				Map<String, Object> map = Maps.newHashMap();
				map.put("tizhi", tizhis1[t]);
				map.put("disease", strs[s]);
				map.put("count", count);
				
				
				if (strs[s].equals("血脂异常患者")) {
					hyperlipemia.add(count);
				} else if (strs[s].equals("血脂异常高风险人群")) {
					bloodLipidAbnormal.add(count);
				} else if (strs[s].equals("正常")) {
					normal.add(count);
					map.put("disease", "血脂正常人群");
				}
				
				list.add(map);
			}
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("list", list);
		resultMap.put("hyperlipemia", hyperlipemia);
		resultMap.put("bloodLipidAbnormal", bloodLipidAbnormal);
		resultMap.put("normal", normal);
		return resultMap;
	}
	
	/**
	 * 体质指数人群分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findFatPeopleDistribution(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//总人数
		Long count = healthcheckCount(district, "BMI");
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		Integer overWeightCount = 0;
		Integer obeseCount = 0;
		Integer normalCount = 0;
		Integer thinCount = 0;
		
		Double overWeightPerc = (double) 0;
		Double obesePerc = (double) 0;
		Double normalPerc = (double) 0;
		Double thinPerc = (double) 0;
		
		Map<String, Object> fatCountMap = healthCheckDAO.findFatPeopleDistribution(district);
		
		if (count != 0 && fatCountMap != null) {
			if (fatCountMap.get("肥胖人群") != null) {
				obeseCount = Integer.parseInt(fatCountMap.get("肥胖人群").toString());
				obesePerc = ParamUtils.doubleScale(obeseCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (fatCountMap.get("超重人群") != null) {
				overWeightCount = Integer.parseInt(fatCountMap.get("超重人群").toString());
				overWeightPerc = ParamUtils.doubleScale(overWeightCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			
			if (fatCountMap.get("正常") != null) {
				normalCount = Integer.parseInt(fatCountMap.get("正常").toString());
				normalPerc = ParamUtils.doubleScale(normalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (fatCountMap.get("偏瘦") != null) {
				thinCount = Integer.parseInt(fatCountMap.get("偏瘦").toString());
//				thinPerc = ParamUtils.doubleScale(thinCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (thinCount == 0) {
				thinPerc = (double) 0;
				if (normalCount != 0) {
					normalPerc = ParamUtils.doubleScale(100 - obesePerc - overWeightPerc, 1);
				} else {
					if (overWeightCount != 0) {
						overWeightPerc = ParamUtils.doubleScale(100 - obesePerc, 1);
					}
				}
			} else {
				thinPerc = ParamUtils.doubleScale(100 - obesePerc - overWeightPerc - normalPerc, 1);
				if (thinPerc < 0) {
					thinPerc = (double) 0;
				}
			}
		}
		
		countList.add(obeseCount);
		countList.add(overWeightCount);
		countList.add(normalCount);
		countList.add(thinCount);
		
		percList.add(obesePerc);
		percList.add(overWeightPerc);
		percList.add(normalPerc);
		percList.add(thinPerc);
		
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("item", "肥胖人群");
		map1.put("count", obeseCount);
		map1.put("percent", obesePerc);
		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("item", "超重人群");
		map2.put("count", overWeightCount);
		map2.put("percent", overWeightPerc);
		list.add(map2);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("item", "BMI正常人群");
		map3.put("count", normalCount);
		map3.put("percent", normalPerc);
		list.add(map3);
		
		Map<String, Object> map4 = Maps.newHashMap();
		map4.put("item", "偏瘦人群");
		map4.put("count", thinCount);
		map4.put("percent", thinPerc);
		list.add(map4);
		
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("list", list);
		return result;
		
	}
	
	/**
	 * 体质指数年龄分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findFatPeopleDistributionByAge(String district) {
		List<Map<String,Object>> ageList = getAgeList();
		Map<Object, Object> fatCountByAgeMap = healthCheckDAO.findFatPeopleDistributionByAge(district, ageList);
		
		List<Map<String,Object>> overWeightList = ageList(ageList);
		List<Map<String,Object>> obeseList = ageList(ageList);
		List<Map<String,Object>> normalList = ageList(ageList);
		List<Map<String,Object>> thinList = ageList(ageList);
		
		if (fatCountByAgeMap != null) {
			for (Map.Entry<Object, Object> entry : fatCountByAgeMap.entrySet()) { 
				Map<String,String> key = (Map<String,String>)entry.getKey();
				Object value = entry.getValue();
				
				String bmi = key.get("bmi");
				String ages = key.get("ages");
				if (StringUtils.isNotEmpty(bmi) && StringUtils.isNotEmpty(ages) && !ages.equals("-") && !bmi.equals("-")) {
					if (bmi.equals("超重人群")) {
						for (Map<String, Object> overWeight : overWeightList) {
							if (overWeight.keySet().contains(ages)) {
								overWeight.put(ages, value);
								break;
							}
						}
					} else if (bmi.equals("肥胖人群")) {
						for (Map<String, Object> obese : obeseList) {
							if (obese.keySet().contains(ages)) {
								obese.put(ages, value);
								break;
							}
						}
					} else if (bmi.equals("正常人群")) {
						for (Map<String, Object> normal : normalList) {
							if (normal.keySet().contains(ages)) {
								normal.put(ages, value);
								break;
							}
						}
					} else if (bmi.equals("偏瘦人群")) {
						for (Map<String, Object> thin : thinList) {
							if (thin.keySet().contains(ages)) {
								thin.put(ages, value);
								break;
							}
						}
					} 
				}
			}
		}
		
		Map<String,List<Map<String,Object>>> m2Change = Maps.newHashMap();
		m2Change.put("超重人群", overWeightList);
		m2Change.put("肥胖人群", obeseList);
		m2Change.put("正常人群", normalList);
		m2Change.put("偏瘦人群", thinList);
		
		Map<String, Object> returnMap = Maps.newHashMap();
		returnMap.put("result", m2Change);
		return returnMap;
	}
	
	
	public Map<String, Object> findFatPeopleDistributionByAge2(String district) {
		Map<String, Object> map = findFatPeopleDistributionByAge(district);
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> list11 = Lists.newArrayList();
		List<Integer> list22 = Lists.newArrayList();
		List<Integer> list33 = Lists.newArrayList();
		List<Integer> list44 = Lists.newArrayList();
		
		//ageList1 = {"35-39", "40-44", "45-49", "50-54", "55-59", "60-65"};
		List<String> ageList1 = getAges(ageList);
		
		if (map != null) {
			Map<String, Object> result = (Map<String, Object>) map.get("result");
			List<Map<String, Object>> list1 = (List<Map<String, Object>>) result.get("肥胖人群");
			List<Map<String, Object>> list2 = (List<Map<String, Object>>) result.get("超重人群");
			List<Map<String, Object>> list3 = (List<Map<String, Object>>) result.get("正常人群");
			List<Map<String, Object>> list4 = (List<Map<String, Object>>) result.get("偏瘦人群");
			
			for (int i = 0; i < list1.size(); i ++) {
				Map<String, Object> map1 = Maps.newHashMap();
				map1.put("type", ageList1.get(i) + "岁");
				map1.put("disease", "肥胖人群");
				map1.put("count", list1.get(i).get(ageList1.get(i)));
				list.add(map1);
				list11.add(Integer.parseInt(list1.get(i).get(ageList1.get(i)).toString()));
				
				Map<String, Object> map2 = Maps.newHashMap();
				map2.put("type", ageList1.get(i) + "岁");
				map2.put("disease", "超重人群");
				map2.put("count", list2.get(i).get(ageList1.get(i)));
				list.add(map2);
				list22.add(Integer.parseInt(list2.get(i).get(ageList1.get(i)).toString()));
				
				Map<String, Object> map3 = Maps.newHashMap();
				map3.put("type", ageList1.get(i) + "岁");
				map3.put("disease", "BMI正常人群");
				map3.put("count", list3.get(i).get(ageList1.get(i)));
				list.add(map3);
				list33.add(Integer.parseInt(list3.get(i).get(ageList1.get(i)).toString()));
				
				Map<String, Object> map4 = Maps.newHashMap();
				map4.put("type", ageList1.get(i) + "岁");
				map4.put("disease", "偏瘦人群");
				map4.put("count", list4.get(i).get(ageList1.get(i)));
				list.add(map4);
				list44.add(Integer.parseInt(list4.get(i).get(ageList1.get(i)).toString()));
			}
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("list", list);
		result.put("obesity", list11);
		result.put("overweight", list22);
		result.put("normal", list33);
		result.put("thin", list44);
		result.put("ageList", ageList1);
		return result;
	}
	
	/**
	 * 体质指数性别分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findFatPeopleDistributionByGender(String district) {
		Map<Object, Object> map = healthCheckDAO.findFatPeopleDistributionByGender(district);
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Map<String, Object>> femaleList = getFatList();
		List<Map<String, Object>> maleList = getFatList();
		
		for (Map.Entry<Object, Object> entity : map.entrySet()) {
			Map<String,String> key = (Map<String, String>)entity.getKey();
			Object value = entity.getValue();
			String type = key.get("type");
			String gender = key.get("gender");
			if (StringUtils.isNotEmpty(type) && StringUtils.isNotEmpty(gender)) {
				if (gender.equals("女")) {
					for (Map<String, Object> female : femaleList) {
						if (female.keySet().contains(type)) {
							female.put(type, value);
							break;
						}
					}
				} else if (gender.equals("男")) {
					for (Map<String, Object> male : maleList) {
						if (male.keySet().contains(type)) {
							male.put(type, value);
							break;
						}
					}
				}
			}
		}
//		femaleList:[{血脂异常患者=0}, {血脂异常高风险人群=0}, {正常=0}]
//		maleList:[{血脂异常患者=0}, {血脂异常高风险人群=1}, {正常=0}]
		List<Integer> list1 = Lists.newArrayList();
		List<Integer> list2 = Lists.newArrayList();
		String[] strs = {"肥胖人群", "超重人群", "正常人群", "偏瘦人群"};
		for(int s = 0; s < strs.length; s ++) {
			String str = strs[s];
			for (int i = 0; i < femaleList.size(); i ++) {
				if (s == i) {
					if(str.equals("正常人群")) {
						Map<String, Object> obj1 = Maps.newHashMap();
						Map<String, Object> female = femaleList.get(i);
						obj1.put("type", "BMI正常人群");
						obj1.put("count", female.get(str));
						obj1.put("gender", "女");
						list.add(obj1);
						
						Map<String, Object> obj2 = Maps.newHashMap();
						Map<String, Object> male = maleList.get(i);
						obj2.put("type", "BMI正常人群");
						obj2.put("count", male.get(str));
						obj2.put("gender", "男");
						list.add(obj2);
						list1.add(Integer.parseInt(male.get(str).toString()));
						list2.add(Integer.parseInt(female.get(str).toString()));
					} else if(str.equals("肥胖人群")){
						Map<String, Object> obj1 = Maps.newHashMap();
						Map<String, Object> female = femaleList.get(i);
						obj1.put("type", str);
						obj1.put("count", female.get(str));
						obj1.put("gender", "女");
						list.add(obj1);
						
						Map<String, Object> obj2 = Maps.newHashMap();
						Map<String, Object> male = maleList.get(i);
						obj2.put("type", str);
						obj2.put("count", male.get(str));
						obj2.put("gender", "男");
						list.add(obj2);
						list1.add(Integer.parseInt(male.get(str).toString()));
						list2.add(Integer.parseInt(female.get(str).toString()));
					}  else if(str.equals("超重人群")){
						Map<String, Object> obj1 = Maps.newHashMap();
						Map<String, Object> female = femaleList.get(i);
						obj1.put("type", str);
						obj1.put("count", female.get(str));
						obj1.put("gender", "女");
						list.add(obj1);
						
						Map<String, Object> obj2 = Maps.newHashMap();
						Map<String, Object> male = maleList.get(i);
						obj2.put("type", str);
						obj2.put("count", male.get(str));
						obj2.put("gender", "男");
						list.add(obj2);
						list1.add(Integer.parseInt(male.get(str).toString()));
						list2.add(Integer.parseInt(female.get(str).toString()));
					} else if(str.equals("偏瘦人群")) {
						Map<String, Object> obj1 = Maps.newHashMap();
						Map<String, Object> female = femaleList.get(i);
						obj1.put("type", str);
						obj1.put("count", female.get(str));
						obj1.put("gender", "女");
						list.add(obj1);
						
						Map<String, Object> obj2 = Maps.newHashMap();
						Map<String, Object> male = maleList.get(i);
						obj2.put("type", str);
						obj2.put("count", male.get(str));
						obj2.put("gender", "男");
						list.add(obj2);
						list1.add(Integer.parseInt(male.get(str).toString()));
						list2.add(Integer.parseInt(female.get(str).toString()));
					}
				}
			}
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("list", list);
		resultMap.put("maleList", list1);
		resultMap.put("femaleList", list2);
		return resultMap;
	}
	
	/**
	 * 肥胖情况体质分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findFatPeopleDistributionByTizhi(String district) {
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿","湿热", "血瘀", "气郁", "特禀"};
		String[] tizhis1 = {"A平和质", "B气虚质", "C阳虚质", "D阴虚质", "E痰湿质","F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> fps = Lists.newArrayList();
		List<Integer> czs = Lists.newArrayList();
		List<Integer> zcs = Lists.newArrayList();
		List<Integer> pss = Lists.newArrayList();
		for (int t = 0; t < tizhis.length; t ++) {
			Map<String, Object> queryMap = Maps.newHashMap();
			if (StringUtils.isNotEmpty(district)) {
				queryMap.put("district", district);
			}
			
			queryMap.put("BMI", new BasicDBObject("$gte", 28));
			Integer count1 = healthCheckDAO.findBloodSugarConditionPeopleDistributionByTizhi(queryMap, tizhis[t]);
			Map<String, Object> map1 = Maps.newHashMap();
			map1.put("tizhi", tizhis1[t]);
			map1.put("disease", "肥胖人群");
			map1.put("count", count1);
			list.add(map1);
			fps.add(count1);
			
			queryMap.remove("BMI");
			queryMap.put("BMI", new BasicDBObject("$gte", 24).append("$lt", 28));
			Integer count2 = healthCheckDAO.findBloodSugarConditionPeopleDistributionByTizhi(queryMap, tizhis[t]);
			Map<String, Object> map2 = Maps.newHashMap();
			map2.put("tizhi", tizhis1[t]);
			map2.put("disease", "超重人群");
			map2.put("count", count2);
			list.add(map2);
			czs.add(count2);
			
			queryMap.remove("BMI");
			queryMap.put("BMI", new BasicDBObject("$gte", 18.5).append("$lt", 24));
			Integer count3 = healthCheckDAO.findBloodSugarConditionPeopleDistributionByTizhi(queryMap, tizhis[t]);
			Map<String, Object> map3 = Maps.newHashMap();
			map3.put("tizhi", tizhis1[t]);
			map3.put("disease", "BMI正常人群");
			map3.put("count", count3);
			list.add(map3);
			zcs.add(count3);
			
			queryMap.remove("BMI");
			queryMap.put("BMI", new BasicDBObject("$lt", 18.5));
			Integer count4 = healthCheckDAO.findBloodSugarConditionPeopleDistributionByTizhi(queryMap, tizhis[t]);
			Map<String, Object> map4 = Maps.newHashMap();
			map4.put("tizhi", tizhis1[t]);
			map4.put("disease", "偏瘦人群");
			map4.put("count", count4);
			list.add(map4);
			pss.add(count4);
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("list", list);
		resultMap.put("fps", fps);
		resultMap.put("czs", czs);
		resultMap.put("zcs", zcs);
		resultMap.put("pss", pss);
		return resultMap;
	}
	
	/**
	 * 社区人群体质分类
	 * @param district
	 * @return
	 */
	public Map<String, Object> findTizhiPeopleDistribution(String district, String type) {
		Map<String, Object> result = Maps.newHashMap();
		//总人数
		Long count = (long) 0;
		if (StringUtils.isEmpty(type)) {
			count = healthcheckCount(district, "tizhi");
		} else {
			count = healthcheckJsCount(district, "tizhi");
		}
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿","湿热", "血瘀", "气郁", "特禀"};
		String[] tizhis1 = {"A平和质", "B气虚质", "C阳虚质", "D阴虚质", "E痰湿质","F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		
		for (int i = 0; i < tizhis.length; i ++) {
			Integer tizhiCount = healthCheckDAO.getTizhiCount(tizhis[i], district, "", "", "", "", type);
			if (tizhiCount == null) {
				tizhiCount = 0;
			}
			
			Double tizhiPerc = (double) 0;
			if (count != 0) {
				tizhiPerc = ParamUtils.doubleScale(tizhiCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (i == (tizhis.length - 1)) {
				double percSum = 0;
				for (Double perc : percList) {
					percSum += perc;
				}
				
				if (tizhiCount == 0) {
					tizhiPerc = (double) 0;
				} else {
					tizhiPerc = ParamUtils.doubleScale(100 -percSum, 1); 
					if (tizhiPerc < 0 || count == 0) {
						tizhiPerc = (double) 0;
					}
				}
				
			}
			
			countList.add(tizhiCount);
			percList.add(tizhiPerc);
			
			Map<String, Object> map = Maps.newHashMap();
			map.put("tizhi", tizhis1[i]);
			map.put("count", tizhiCount);
			list.add(map);
		}
		
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("list", list);
		return result;
	}
	
	/**
	 * 体质与代谢性疾病患病率的关系
	 * @param district
	 * @return
	 */
	public Map<String, Object> findTizhiPeopleDistributionByDisease(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//建档人数
		Long bloodSugarCount = tizhiAndDiseaseCount("bloodSugarCondition", district);
		Long bloodPressureCount = tizhiAndDiseaseCount("bloodPressureCondition", district);
		Long bloodLipidCount = tizhiAndDiseaseCount("bloodLipidCondition", district);
		Long bmiCount = tizhiAndDiseaseCount("BMI", district);
		
		List<Integer> diabetesList = Lists.newArrayList();
		List<Integer> bloodPressureList = Lists.newArrayList();
		List<Integer> bloodLipidList = Lists.newArrayList();
		List<Integer> fatList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		
		String[] tizhis1 = {"A平和质", "B气虚质", "C阳虚质", "D阴虚质", "E痰湿质","F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿","湿热", "血瘀", "气郁", "特禀"};
		String[] diseases = {"糖尿病患者", "高血压患者", "血脂异常患者", "肥胖人群"};
		Double tnbSum = (double) 0;
		Double gxySum = (double) 0;
		Double gxzSum = (double) 0;
		Double fpSum = (double) 0;
		
		for (int i = 0; i < tizhis.length; i ++) {
			
			for (String disease : diseases) {
				Integer diseaseCount = healthCheckDAO.getTizhiCount(tizhis[i], district, disease, "", "", "", "");
				if (diseaseCount == null) {
					diseaseCount = 0;
				}
				
				Double diseasePerc = (double) 0;

				if (i == (tizhis.length -1)) {
					if (disease.equals("糖尿病患者")) {
						diabetesList.add(diseaseCount);
						if (diseaseCount != 0) {
							diseasePerc = ParamUtils.doubleScale(100 - tnbSum, 1);
						} else {
							diseasePerc = (double) 0;
						}
						
					} else if (disease.equals("高血压患者")) {
						bloodPressureList.add(diseaseCount);
						if (diseaseCount != 0) {
							diseasePerc = ParamUtils.doubleScale(100 - gxySum, 1);
						} else {
							diseasePerc = (double) 0;
						}
						
					}  else if (disease.equals("血脂异常患者")) {
						bloodLipidList.add(diseaseCount);
						if (diseaseCount != 0) {
							diseasePerc = ParamUtils.doubleScale(100 - gxzSum, 1);
						} else {
							diseasePerc = (double) 0;
						}
						
					}  else if (disease.equals("肥胖人群")) {
						fatList.add(diseaseCount);
						if (diseaseCount != 0) {
							diseasePerc = ParamUtils.doubleScale(100 - fpSum, 1);
						} else {
							diseasePerc = (double) 0;
						}
					} 
					
					if (diseasePerc < 0) {
						diseasePerc = (double) 0;
					}
					
				} else {
					if (disease.equals("糖尿病患者")) {
						diabetesList.add(diseaseCount);
						if (bloodSugarCount != 0) {
							diseasePerc = ParamUtils.doubleScale(diseaseCount.doubleValue() / bloodSugarCount.doubleValue() * 100, 1);
							tnbSum += diseasePerc;
						}
					} else if (disease.equals("高血压患者")) {
						bloodPressureList.add(diseaseCount);
						if (bloodPressureCount != 0) {
							diseasePerc = ParamUtils.doubleScale(diseaseCount.doubleValue() / bloodPressureCount.doubleValue() * 100, 1);
							gxySum += diseasePerc;
						}
					} else if (disease.equals("血脂异常患者")) {
						bloodLipidList.add(diseaseCount);
						if (bloodLipidCount != 0) {
							diseasePerc = ParamUtils.doubleScale(diseaseCount.doubleValue() / bloodLipidCount.doubleValue() * 100, 1);
							gxzSum += diseasePerc;
						}
					} else if (disease.equals("肥胖人群")) {
						fatList.add(diseaseCount);
						if (bmiCount != 0) {
							diseasePerc = ParamUtils.doubleScale(diseaseCount.doubleValue() / bmiCount.doubleValue() * 100, 1);
							fpSum += diseasePerc;
						}
					}
				}
				
				Map<String, Object> map = Maps.newHashMap();
				map.put("tizhi", tizhis1[i]);
				map.put("perc", diseasePerc);
				map.put("disease", disease);
				list.add(map);
			}
		}	
		
		result.put("diabetesList", diabetesList);
		result.put("bloodPressureList", bloodPressureList);
		result.put("bloodLipidList", bloodLipidList);
		result.put("fatList", fatList);
		result.put("list", list);
		return result;
	}
	
	
	/**
	 * 各性别体质分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findTizhiPeopleDistributionByGender(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//建档人数
		Long count = findRecordConditionCount(district);
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> femaleList = Lists.newArrayList();
		List<Integer> maleList = Lists.newArrayList();
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿","湿热", "血瘀", "气郁", "特禀"};
		String[] tizhis1 = {"A平和质", "B气虚质", "C阳虚质", "D阴虚质", "E痰湿质","F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		String[] genders = {"男", "女"};
		
		for (int i = 0; i < tizhis.length; i ++) {
			for (String gender : genders) {
				Integer tizhiCount = healthCheckDAO.getTizhiCount(tizhis[i], district, "", gender, "", "", "");
				if (tizhiCount == null) {
					tizhiCount = 0;
				}
				Map<String, Object> map = Maps.newHashMap();
				map.put("tizhi", tizhis1[i]);
				map.put("count", tizhiCount);
				map.put("gender", gender);
				list.add(map);
				
				if (gender.equals("男")) {
					maleList.add(tizhiCount);
				} else {
					femaleList.add(tizhiCount);
				}
			}
		}
		
		result.put("list", list);
		result.put("maleList", maleList);
		result.put("femaleList", femaleList);
		return result;
	}
	
	/**
	 * 各年龄阶段体质分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findTizhiPeopleDistributionByAge(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//建档人数
		Long count = findRecordConditionCount(district);
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> list11 = Lists.newArrayList();
		List<Integer> list22 = Lists.newArrayList();
		List<Integer> list33 = Lists.newArrayList();
		List<Integer> list44 = Lists.newArrayList();
		List<Integer> list55 = Lists.newArrayList();
		List<Integer> list66 = Lists.newArrayList();
		List<Integer> list77 = Lists.newArrayList();
		List<Integer> list88 = Lists.newArrayList();
		List<Integer> list99 = Lists.newArrayList();
		
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿","湿热", "血瘀", "气郁", "特禀"};
		String[] tizhis1 = {"A平和质", "B气虚质", "C阳虚质", "D阴虚质", "E痰湿质","F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		String[] ageList = {"35-39岁", "40-44岁", "45-49岁", "50-54岁", "55-59岁", "60-65岁"};
		String[] ageList1 = {"35-39", "40-44", "45-49", "50-54", "55-59", "60-65"};
		
		for (int j = 0; j < ageList1.length; j ++) {
			for (int i = 0; i < tizhis.length; i ++) {
				Integer tizhiCount = healthCheckDAO.getTizhiCount(tizhis[i], district, "", "", ageList1[j].split("-")[0], ageList1[j].split("-")[1], "");
				if (tizhiCount == null) {
					tizhiCount = 0;
				}
				Map<String, Object> map = Maps.newHashMap();
				map.put("tizhi", tizhis1[i]);
				map.put("count", tizhiCount);
				map.put("age", ageList[j]);
				list.add(map);
				
				if (tizhis[i].equals("平和")) {
					list11.add(tizhiCount);
				} else if (tizhis[i].equals("气虚")) {
					list22.add(tizhiCount);
				} else if (tizhis[i].equals("阳虚")) {
					list33.add(tizhiCount);
				} else if (tizhis[i].equals("阴虚")) {
					list44.add(tizhiCount);
				} else if (tizhis[i].equals("痰湿")) {
					list55.add(tizhiCount);
				} else if (tizhis[i].equals("湿热")) {
					list66.add(tizhiCount);
				} else if (tizhis[i].equals("血瘀")) {
					list77.add(tizhiCount);
				} else if (tizhis[i].equals("气郁")) {
					list88.add(tizhiCount);
				} else if (tizhis[i].equals("特禀")) {
					list99.add(tizhiCount);
				}
			}
		}
		
		result.put("list", list);
		result.put("ph", list11);
		result.put("qx", list22);
		result.put("yx", list33);
		result.put("yxu", list44);
		result.put("ts", list55);
		result.put("sy", list66);
		result.put("xy", list77);
		result.put("qy", list88);
		result.put("tb", list99);
		return result;
	}
	
	/**
	 * 血压分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodPressureDistribution(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//总人数
		Long count = healthcheckCount(district, "bloodPressureCondition");
		
		Integer count1 = 0;
		Integer count2 = 0;
		Integer count3 = 0;
//		Integer count4 = 0;
		
		Double perc1 = (double) 0;
		Double perc2 = (double) 0;
		Double perc3 = (double) 0;
//		Double perc4 = (double) 0;
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		
		if (count != 0) {
			Map<String, Object> map = healthCheckDAO.findBloodPressureDistribution(district);
			
			if (count != 0 && map != null) {
				if (map.get("正常血压") != null) {
					count1 = Integer.parseInt(map.get("正常血压").toString());
					perc1 = ParamUtils.doubleScale(count1.doubleValue() / count.doubleValue() * 100, 1);
				}
				
				if (map.get("正常高值") != null) {
					count2 = Integer.parseInt(map.get("正常高值").toString());
					perc2 = ParamUtils.doubleScale(count2.doubleValue() / count.doubleValue() * 100, 1);
				}
				
				if (map.get("高血压") != null) {
					count3 = Integer.parseInt(map.get("高血压").toString());
//					perc3 = ParamUtils.doubleScale(count3.doubleValue() / count.doubleValue() * 100, 1);
				}
				
				/*if (map.get("单纯收缩压高血压") != null) {
					count4 = Integer.parseInt(map.get("单纯收缩压高血压").toString());
					perc4 = ParamUtils.doubleScale(count4.doubleValue() / count.doubleValue() * 100, 1);
				}*/
			}
			perc3 = ParamUtils.doubleScale(100 - perc1 -perc2, 1);
			if (perc3 < 0) {
				perc3 = (double) 0;
			}
		}
		
		countList.add(count1);
		countList.add(count2);
		countList.add(count3);
//		countList.add(count4);
		
		percList.add(perc1);
		percList.add(perc2);
		percList.add(perc3);
//		percList.add(perc4);
		
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("item", "正常血压");
		map1.put("count", count1);
		map1.put("percent", perc1);
		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("item", "正常高值");
		map2.put("count", count2);
		map2.put("percent", perc2);
		list.add(map2);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("item", "高血压");
		map3.put("count", count3);
		map3.put("percent", perc3);
		list.add(map3);
		
//		Map<String, Object> map4 = Maps.newHashMap();
//		map4.put("item", "单纯收缩压高血压");
//		map4.put("count", count4);
//		map4.put("percent", perc4);
//		list.add(map4);
		
		result.put("list", list);
		result.put("countList", countList);
		result.put("percList", percList);
		return result;
	}
	
	
	/**
	 * 高血压分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findHighBloodPressureDistribution(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//高血压人数
		int count = healthCheckDAO.findHtnCount(district);
		
		Integer count1 = 0;
		Integer count2 = 0;
		Integer count3 = 0;
		
		Double perc1 = (double) 0;
		Double perc2 = (double) 0;
		Double perc3 = (double) 0;
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		
		Map<String, Object> map = healthCheckDAO.findHighBloodPressureDistribution(district);
		
		if (count != 0 && map != null) {
			if (map.get("轻度") != null) {
				count1 = Integer.parseInt(map.get("轻度").toString());
				perc1 = ParamUtils.doubleScale(count1.doubleValue() / count * 100, 1);
			}
			
			if (map.get("中度") != null) {
				count2 = Integer.parseInt(map.get("中度").toString());
				perc2 = ParamUtils.doubleScale(count2.doubleValue() / count * 100, 1);
			}
			
			if (map.get("重度") != null) {
				count3 = Integer.parseInt(map.get("重度").toString());
//				perc3 = ParamUtils.doubleScale(count3.doubleValue() / count * 100, 1);
			}
			perc3 = ParamUtils.doubleScale(100 - perc1 - perc2, 1);
			if (perc3 < 0) {
				perc3 = (double) 0;
			}
		}
		
		countList.add(count1);
		countList.add(count2);
		countList.add(count3);
		
		percList.add(perc1);
		percList.add(perc2);
		percList.add(perc3);
		
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("item", "轻度");
		map1.put("count", count1);
		map1.put("percent", perc1);
		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("item", "中度");
		map2.put("count", count2);
		map2.put("percent", perc2);
		list.add(map2);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("item", "重度");
		map3.put("count", count3);
		map3.put("percent", perc3);
		list.add(map3);
		
		result.put("list", list);
		result.put("countList", countList);
		result.put("percList", percList);
		return result;
	}
	
	/**
	 * 各性别冠心病分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findCoronaryHeartDiseaseByGender(String district) {
		Map<String, Object> map = healthCheckDAO.findCoronaryHeartDiseaseByGender(district);
		//map:{男非冠心病=1, 女非冠心病=4, 男冠心病=1, -=4}
//		System.out.println("map:" + map);
		
		//建档人数
		Long count = findRecordConditionCount(district);
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> femaleList = Lists.newArrayList();
		List<Integer> maleList = Lists.newArrayList();
		Integer femaleCpdCount = 0;
		Integer femaleNoCpdCount = 0;
		Integer maleCpdCount = 0;
		Integer maleNoCpdCount = 0;
		
		if (map != null) {
			if (map.get("女冠心病") != null) {
				femaleCpdCount = Integer.parseInt(map.get("女冠心病").toString());
			}
			
			if (map.get("女非冠心病") != null) {
				femaleNoCpdCount = Integer.parseInt(map.get("女非冠心病").toString());
			}
			if (map.get("男冠心病") != null) {
				maleCpdCount = Integer.parseInt(map.get("男冠心病").toString());
			}
			
			if (map.get("男非冠心病") != null) {
				maleNoCpdCount = Integer.parseInt(map.get("男非冠心病").toString());
			}
		}
		
		femaleList.add(femaleCpdCount);
		femaleList.add(femaleNoCpdCount);
		maleList.add(maleCpdCount);
		maleList.add(maleNoCpdCount);
		
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("gender", "女");
		map1.put("type", "冠心病");
		map1.put("count", femaleCpdCount);
		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("gender", "女");
		map2.put("type", "非冠心病");
		map2.put("count", femaleNoCpdCount);
		list.add(map2);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("gender", "男");
		map3.put("type", "冠心病");
		map3.put("count", maleCpdCount);
		list.add(map3);
		
		Map<String, Object> map4 = Maps.newHashMap();
		map4.put("gender", "男");
		map4.put("type", "非冠心病");
		map4.put("count", maleNoCpdCount);
		list.add(map4);
		
//		System.out.println("list:" + list);
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("list", list);
		resultMap.put("femaleList", femaleList);
		resultMap.put("maleList", maleList);
		return resultMap;
	}
	
//	/**
//	 * 各年龄段冠心病分布
//	 * @param district
//	 * @return
//	 */
//	public Map<String, Object> findCoronaryHeartDiseaseByAge(String district) {
//		Map<Object, Object> m = healthCheckDAO.findCoronaryHeartDiseaseByAge(district);
//		//m:{{ "gxb" : "冠心病" , "ages" : "60-65"}=1, { "gxb" : "非冠心病" , "ages" : "35-39"}=1, { "gxb" : "非冠心病" , "ages" : "50-54"}=1, { "gxb" : "非冠心病" , "ages" : "-"}=7}
////		System.out.println("map:" + m);
//		
//		List<Map<String,Object>> ageList1 = ageList();
//		List<Map<String,Object>> ageList2 = ageList();
//		
//		for (Map.Entry<Object, Object> entry : m.entrySet()) { 
//			Map<String,String> key = (Map<String,String>)entry.getKey();
//			Object value = entry.getValue();
//			String gxb = key.get("gxb");
//			String ages = key.get("ages");
//			if(StringUtils.isNotEmpty(gxb) && StringUtils.isNotEmpty(ages)){
//				switch (gxb) {
//				case "冠心病":
//					for(int i =0;i<ageList1.size();i++){
//						Map<String,Object> map = ageList1.get(i);
//						if(map.keySet().contains(ages)){
//							map.put(ages, value);
//						}
//					}
//					break;
//				case "非冠心病":
//					for(int i =0;i<ageList2.size();i++){
//						Map<String,Object> map2 = ageList2.get(i);
//						if(map2.keySet().contains(ages)){
//							map2.put(ages, value);
//						}
//					}
//					break;
//				default:
//					break;
//				}
//			}
//		}
//		
//		String[] ageList = {"35-39岁", "40-44岁", "45-49岁", "50-54岁", "55-59岁", "60-65岁"};
//		String[] ageList3 = {"35-39", "40-44", "45-49", "50-54", "55-59", "60-65"};
//		
//		List<Map<String, Object>> list = Lists.newArrayList();
//		List<Integer> list11 = Lists.newArrayList();
//		List<Integer> list22 = Lists.newArrayList();
//		for (int i = 0; i < ageList1.size(); i ++) {
//			Map<String, Object> map1 = Maps.newHashMap();
//			map1.put("type", ageList[i]);
//			map1.put("disease", "冠心病");
//			map1.put("count", ageList1.get(i).get(ageList3[i]));
//			list.add(map1);
//			list11.add(Integer.parseInt(ageList1.get(i).get(ageList3[i]).toString()));
//			
//			Map<String, Object> map2 = Maps.newHashMap();
//			map2.put("type", ageList[i]);
//			map2.put("disease", "非冠心病");
//			map2.put("count", ageList2.get(i).get(ageList3[i]));
//			list.add(map2);
//			list22.add(Integer.parseInt(ageList2.get(i).get(ageList3[i]).toString()));
//		}
//		
//		Map<String, Object> returnMap = Maps.newHashMap();
//		returnMap.put("list", list);
//		returnMap.put("cpd", list11);
//		returnMap.put("noCpd", list22);
//		return returnMap;
//	}
	
	
	/**
	 * 各体质冠心病分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findCoronaryHeartDiseaseByTizhi(String district) {
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿","湿热", "血瘀", "气郁", "特禀"};
		String[] tizhis1 = {"A平和质", "B气虚质", "C阳虚质", "D阴虚质", "E痰湿质","F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		
		List<Map<String, Object>> list = Lists.newArrayList();
		List<Integer> cpd = Lists.newArrayList();
		List<Integer> noCpd = Lists.newArrayList();
		for (int t = 0; t < tizhis.length; t ++) {
			Map<String, Object> queryMap = Maps.newHashMap();
			if (StringUtils.isNotEmpty(district)) {
				queryMap.put("district", district);
			}
			
			queryMap.put("cpd", "是");
			Integer count = healthCheckDAO.findBloodSugarConditionPeopleDistributionByTizhi(queryMap, tizhis[t]);
			
			Map<String, Object> map1 = Maps.newHashMap();
			map1.put("tizhi", tizhis1[t]);
			map1.put("disease", "冠心病");
			map1.put("count", count);
			list.add(map1);
			cpd.add(count);
			
			queryMap.put("cpd", new BasicDBObject("$ne", "是"));
			queryMap.remove("tizhi");
			Integer count2 = healthCheckDAO.findBloodSugarConditionPeopleDistributionByTizhi(queryMap, tizhis[t]);
			
			Map<String, Object> map2 = Maps.newHashMap();
			map2.put("tizhi", tizhis1[t]);
			map2.put("disease", "非冠心病");
			map2.put("count", count2);
			list.add(map2);
			noCpd.add(count2);
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("list", list);
		resultMap.put("cpd", cpd);
		resultMap.put("noCpd", noCpd);
		return resultMap;
	}
	
	/**
	 * 目诊筛查情况
	 * @param district
	 * @return
	 */
	public List<Map<String, Object>> eyeRecordDistribution(String district, Integer page, Integer pageCount) {
		String[] syndromes = {"心肾不交证", "阴阳两虚证", "阴虚证", "阴虚血瘀证", "寒湿血瘀证", "湿郁血瘀证", "寒热错杂证", "肝肾阴虚证", "肝胆湿热证",
				"阴虚血瘀兼湿证", "阴虚湿郁证", "阴虚气郁证", "阴虚气郁兼瘀证", "阳虚血瘀证", "阳虚湿郁证", "血瘀证", "血虚证", "血虚血瘀证", "血热证", 
				"血热血瘀证", "心脾两虚证", "湿郁证", "湿热证", "湿热血瘀证", "湿遏热伏证", "气郁证", "气郁血瘀证", "气郁血虚兼瘀证", "气郁化火证",
				"气郁化火兼瘀证", "气阴两虚证", "气阴两虚兼郁证", "气阴两虚兼瘀证", "气阴两虚兼湿证", "气虚气郁兼瘀证", "气血两虚证", "气血两虚兼郁证", 
				"气血两虚兼瘀证", "气虚证", "气虚血瘀证", "气虚湿郁证", "气虚湿郁兼瘀证", "气虚湿热证", "气虚气郁证", "脾胃阳虚证", "脾胃湿热证", "脾胃湿热兼瘀证", 
				"脾胃气虚证", "脾肾阳虚证", "寒湿证", "肝脾不和证", "肝火犯肺证", "肝火犯肺兼瘀证", "阳虚证", "气郁血虚证", 
				"寒湿血虚证", "阴阳两虚兼瘀证", "阴阳中和证", "阳虚气郁证", "阳虚血弱证"};
		List<Map<String, Object>> list = Lists.newArrayList();
		for (String syndrome : syndromes) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("syndrome", syndrome);
			map.put("num", 0);
			list.add(map);
		}
		
		Map<String, Object> countBySyndrome = healthCheckDAO.countBySyndrome();
		System.out.println("countBySyndrome:" + countBySyndrome);
		for (Map<String, Object> obj : list) {
			String syndrome = (String) obj.get("syndrome");
			if (countBySyndrome.get(syndrome) != null) {
				obj.put("num", countBySyndrome.get(syndrome));
			}
		}
		
		Collections.sort(list, new MapComparator());
		
		List<Map<String, Object>> listPage = Lists.newArrayList();
		for (int i = page * 10 - 10; i <= page * 10 -1; i ++) {
			Map<String, Object> obj = list.get(i);
			/*String syndrome = (String) obj.get("syndrome");
			if (countBySyndrome.get(syndrome) != null) {
				obj.put("num", countBySyndrome.get(syndrome));
			}*/
			obj.put("count", i + 1);
			listPage.add(obj);
		}
		
		System.out.println("listPage:" + listPage);
		
		List<Map<String, Object>> results = Lists.newArrayList();
		for (Map<String, Object> obj : listPage) {
			String syndrome = (String) obj.get("syndrome");
			Integer num = Integer.parseInt(obj.get("num").toString());
			Map<String, Object> query = Maps.newHashMap();
			query.put("analysisResult.syndrome", syndrome + "。");
			List<Map<String, Object>> eyeRecords = eyeRecordService.queryList(query);
			
			Integer tnbCount = 0;
			Integer gxyCount = 0;
			Integer xzCount = 0;
			Integer fpCount = 0;
			for (Map<String, Object> eyeRecord : eyeRecords) {
				if (eyeRecord.get("uniqueId") != null && eyeRecord.get("uniqueId") != "") {
					String uniqueId = eyeRecord.get("uniqueId").toString();
					Map<String, Object> customerQuery = Maps.newHashMap();
					customerQuery.put("uniqueId", uniqueId);
					if (StringUtils.isNotEmpty(district)) {
						customerQuery.put("district", district);
					}
					Map<String, Object> customer = healthCheckDAO.getDataByQuery("customer", customerQuery);
					if (customer != null) {
						/*if (customer.get("bloodSugarCondition") != null && customer.get("bloodSugarCondition") != "") {
							String bloodSugarCondition = customer.get("bloodSugarCondition").toString();
							if (bloodSugarCondition.equals("糖尿病患者")) {
								tnbCount ++; 
							}
						}
						
						if (customer.get("bloodPressureCondition") != null && customer.get("bloodPressureCondition") != "") {
							String bloodPressureCondition = customer.get("bloodPressureCondition").toString();
							if (bloodPressureCondition.equals("高血压患者")) {
								gxyCount ++; 
							}
						}
						
						if (customer.get("bloodLipidCondition") != null && customer.get("bloodLipidCondition") != "") {
							String bloodLipidCondition = customer.get("bloodLipidCondition").toString();
							if (bloodLipidCondition.equals("血脂异常患者")) {
								xzCount ++; 
							}
						}*/
						
						if (customer.get("dm") != null && !"".equals(customer.get("dm"))) {
							String bloodSugarCondition = customer.get("dm").toString();
							if (bloodSugarCondition.equals("是")) {
								tnbCount ++; 
							}
						}
						
						if (customer.get("htn") != null && !"".equals(customer.get("htn"))) {
							String bloodPressureCondition = customer.get("htn").toString();
							if (bloodPressureCondition.equals("是")) {
								gxyCount ++; 
							}
						}
						
						if (customer.get("hpl") != null && !"".equals(customer.get("hpl"))) {
							String bloodLipidCondition = customer.get("hpl").toString();
							if (bloodLipidCondition.equals("是")) {
								xzCount ++; 
							}
						}
						
						if (customer.get("BMI") != null && !"".equals(customer.get("BMI"))) {
							Double bmi = Double.parseDouble(customer.get("BMI").toString());
							if (bmi >= 28) {
								fpCount ++; 
							}
						}
					}
				}
			}
			
			Double tnbPerc = (double) 0;
			Double gxyPerc = (double) 0;
			Double xzPerc = (double) 0;
			Double fpPerc = (double) 0;
			//建档人数
			Long count = findRecordConditionCount(district);
			if (count != null && count != 0) {
				tnbPerc = ParamUtils.doubleScale(tnbCount.doubleValue() / count.doubleValue() * 100, 1);
				gxyPerc = ParamUtils.doubleScale(gxyCount.doubleValue() / count.doubleValue() * 100, 1);
				xzPerc = ParamUtils.doubleScale(xzCount.doubleValue() / count.doubleValue() * 100, 1);
				fpPerc = ParamUtils.doubleScale(fpCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			obj.put("tnbCount", tnbCount);
			obj.put("gxyCount", gxyCount);
			obj.put("xzCount", xzCount);
			obj.put("fpCount", fpCount);
			obj.put("tnbPerc", tnbPerc);
			obj.put("gxyPerc", gxyPerc);
			obj.put("xzPerc", xzPerc);
			obj.put("fpPerc", fpPerc);
			obj.put("num", num);
			results.add(obj);
		}
		return results;
	}
	
	/**
	 * 目诊筛查情况
	 * @param district
	 * @return
	 */
	public List<Map<String, Object>> downloadEyeRecordDistribution(String district) {
		String[] syndromes = {"心肾不交证", "阴阳两虚证", "阴虚证", "阴虚血瘀证", "寒湿血瘀证", "湿郁血瘀证", "寒热错杂证", "肝肾阴虚证", "肝胆湿热证",
				"阴虚血瘀兼湿证", "阴虚湿郁证", "阴虚气郁证", "阴虚气郁兼瘀证", "阳虚血瘀证", "阳虚湿郁证", "血瘀证", "血虚证", "血虚血瘀证", "血热证", 
				"血热血瘀证", "心脾两虚证", "湿郁证", "湿热证", "湿热血瘀证", "湿遏热伏证", "气郁证", "气郁血瘀证", "气郁血虚兼瘀证", "气郁化火证",
				"气郁化火兼瘀证", "气阴两虚证", "气阴两虚兼郁证", "气阴两虚兼瘀证", "气阴两虚兼湿证", "气虚气郁兼瘀证", "气血两虚证", "气血两虚兼郁证", 
				"气血两虚兼瘀证", "气虚证", "气虚血瘀证", "气虚湿郁证", "气虚湿郁兼瘀证", "气虚湿热证", "气虚气郁证", "脾胃阳虚证", "脾胃湿热证", "脾胃湿热兼瘀证", 
				"脾胃气虚证", "脾肾阳虚证", "寒湿证", "肝脾不和证", "肝火犯肺证", "肝火犯肺兼瘀证", "阳虚证", "气郁血虚证", 
				"寒湿血虚证", "阴阳两虚兼瘀证", "阴阳中和证", "阳虚气郁证", "阳虚血弱证"};
		List<Map<String, Object>> list = Lists.newArrayList();
		for (String syndrome : syndromes) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("syndrome", syndrome);
			map.put("num", 0);
			list.add(map);
		}
		
		Map<String, Object> countBySyndrome = healthCheckDAO.countBySyndrome();
		System.out.println("countBySyndrome:" + countBySyndrome);
		for (Map<String, Object> obj : list) {
			String syndrome = (String) obj.get("syndrome");
			if (countBySyndrome.get(syndrome) != null) {
				obj.put("num", countBySyndrome.get(syndrome));
			}
		}
		
		Collections.sort(list, new MapComparator());
		
		System.out.println("list:" + list);
		
		List<Map<String, Object>> results = Lists.newArrayList();
		int no = 1;
		for (Map<String, Object> obj : list) {
			String syndrome = (String) obj.get("syndrome");
			Integer num = Integer.parseInt(obj.get("num").toString());
			Map<String, Object> query = Maps.newHashMap();
			query.put("analysisResult.syndrome", syndrome + "。");
			List<Map<String, Object>> eyeRecords = eyeRecordService.queryList(query);
			
			Integer tnbCount = 0;
			Integer gxyCount = 0;
			Integer xzCount = 0;
			Integer fpCount = 0;
			for (Map<String, Object> eyeRecord : eyeRecords) {
				if (eyeRecord.get("uniqueId") != null && eyeRecord.get("uniqueId") != "") {
					String uniqueId = eyeRecord.get("uniqueId").toString();
					Map<String, Object> customerQuery = Maps.newHashMap();
					customerQuery.put("uniqueId", uniqueId);
					if (StringUtils.isNotEmpty(district)) {
						customerQuery.put("district", district);
					}
					Map<String, Object> customer = healthCheckDAO.getDataByQuery("customer", customerQuery);
					if (customer != null) {
						/*if (customer.get("bloodSugarCondition") != null && customer.get("bloodSugarCondition") != "") {
							String bloodSugarCondition = customer.get("bloodSugarCondition").toString();
							if (bloodSugarCondition.equals("糖尿病患者")) {
								tnbCount ++; 
							}
						}
						
						if (customer.get("bloodPressureCondition") != null && customer.get("bloodPressureCondition") != "") {
							String bloodPressureCondition = customer.get("bloodPressureCondition").toString();
							if (bloodPressureCondition.equals("高血压患者")) {
								gxyCount ++; 
							}
						}
						
						if (customer.get("bloodLipidCondition") != null && customer.get("bloodLipidCondition") != "") {
							String bloodLipidCondition = customer.get("bloodLipidCondition").toString();
							if (bloodLipidCondition.equals("血脂异常患者")) {
								xzCount ++; 
							}
						}*/
						
						if (customer.get("dm") != null && !"".equals(customer.get("dm"))) {
							String bloodSugarCondition = customer.get("dm").toString();
							if (bloodSugarCondition.equals("是")) {
								tnbCount ++; 
							}
						}
						
						if (customer.get("htn") != null && !"".equals(customer.get("htn"))) {
							String bloodPressureCondition = customer.get("htn").toString();
							if (bloodPressureCondition.equals("是")) {
								gxyCount ++; 
							}
						}
						
						if (customer.get("hpl") != null && !"".equals(customer.get("hpl"))) {
							String bloodLipidCondition = customer.get("hpl").toString();
							if (bloodLipidCondition.equals("是")) {
								xzCount ++; 
							}
						}
						
						if (customer.get("BMI") != null && !"".equals(customer.get("BMI"))) {
							Double bmi = Double.parseDouble(customer.get("BMI").toString());
							if (bmi >= 28) {
								fpCount ++; 
							}
						}
					}
				}
			}
			
			Double tnbPerc = (double) 0;
			Double gxyPerc = (double) 0;
			Double xzPerc = (double) 0;
			Double fpPerc = (double) 0;
			//建档人数
			Long count = findRecordConditionCount(district);
			if (count != null && count != 0) {
				tnbPerc = ParamUtils.doubleScale(tnbCount.doubleValue() / count.doubleValue() * 100, 1);
				gxyPerc = ParamUtils.doubleScale(gxyCount.doubleValue() / count.doubleValue() * 100, 1);
				xzPerc = ParamUtils.doubleScale(xzCount.doubleValue() / count.doubleValue() * 100, 1);
				fpPerc = ParamUtils.doubleScale(fpCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			obj.put("tnbCount", tnbCount);
			obj.put("gxyCount", gxyCount);
			obj.put("xzCount", xzCount);
			obj.put("fpCount", fpCount);
			obj.put("tnbPerc", tnbPerc);
			obj.put("gxyPerc", gxyPerc);
			obj.put("xzPerc", xzPerc);
			obj.put("fpPerc", fpPerc);
			obj.put("num", num);
			obj.put("count", no);
			results.add(obj);
			no ++;
		}
		return results;
	}
	
	
	/**
	 * 目诊筛查情况 存入redis
	 * @param district
	 * @return
	 */
	public List<Map<String, Object>> eyeRecordDistributionRedis(String district, Integer page, Integer pageCount) {
		String mzAndDisease = template.get("mzAndDiseaseList");
		if (StringUtils.isNotEmpty(mzAndDisease)) {
			
		}
		
		
		String[] syndromes = {"心肾不交证", "阴阳两虚证", "阴虚证", "阴虚血瘀证", "寒湿血瘀证", "湿郁血瘀证", "寒热错杂证", "肝肾阴虚证", "肝胆湿热证",
				"阴虚血瘀兼湿证", "阴虚湿郁证", "阴虚气郁证", "阴虚气郁兼瘀证", "阳虚血瘀证", "阳虚湿郁证", "血瘀证", "血虚证", "血虚血瘀证", "血热证", 
				"血热血瘀证", "心脾两虚证", "湿郁证", "湿热证", "湿热血瘀证", "湿遏热伏证", "气郁证", "气郁血瘀证", "气郁血虚兼瘀证", "气郁化火证",
				"气郁化火兼瘀证", "气阴两虚证", "气阴两虚兼郁证", "气阴两虚兼瘀证", "气阴两虚兼湿证", "气虚气郁兼瘀证", "气血两虚证", "气血两虚兼郁证", 
				"气血两虚兼瘀证", "气虚证", "气虚血瘀证", "气虚湿郁证", "气虚湿郁兼瘀证", "气虚湿热证", "气虚气郁证", "脾胃阳虚证", "脾胃湿热证", "脾胃湿热兼瘀证", 
				"脾胃气虚证", "脾肾阳虚证", "寒湿证", "肝脾不和证", "肝火犯肺证", "肝火犯肺兼瘀证", "阳虚证", "气郁血虚证", 
				"寒湿血虚证", "阴阳两虚兼瘀证", "阴阳中和证", "阳虚气郁证", "阳虚血弱证"};
		List<Map<String, Object>> list = Lists.newArrayList();
		for (String syndrome : syndromes) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("syndrome", syndrome);
			map.put("num", 0);
			list.add(map);
		}
		
		Map<String, Object> countBySyndrome = healthCheckDAO.countBySyndrome();
		System.out.println("countBySyndrome:" + countBySyndrome);
		for (Map<String, Object> obj : list) {
			String syndrome = (String) obj.get("syndrome");
			if (countBySyndrome.get(syndrome) != null) {
				obj.put("num", countBySyndrome.get(syndrome));
			}
		}
		
		Collections.sort(list, new MapComparator());
		
		List<Map<String, Object>> listPage = Lists.newArrayList();
		for (int i = page * 10 - 10; i <= page * 10 -1; i ++) {
			Map<String, Object> obj = list.get(i);
			/*String syndrome = (String) obj.get("syndrome");
			if (countBySyndrome.get(syndrome) != null) {
				obj.put("num", countBySyndrome.get(syndrome));
			}*/
			obj.put("count", i + 1);
			listPage.add(obj);
		}
		
//		System.out.println("listPage:" + listPage);
		
		List<Map<String, Object>> results = Lists.newArrayList();
		for (Map<String, Object> obj : listPage) {
			String syndrome = (String) obj.get("syndrome");
			Integer num = Integer.parseInt(obj.get("num").toString());
			Map<String, Object> query = Maps.newHashMap();
			query.put("analysisResult.syndrome", syndrome + "。");
			List<Map<String, Object>> eyeRecords = eyeRecordService.queryList(query);
			
			Integer tnbCount = 0;
			Integer gxyCount = 0;
			Integer xzCount = 0;
			Integer fpCount = 0;
			for (Map<String, Object> eyeRecord : eyeRecords) {
				if (eyeRecord.get("uniqueId") != null && eyeRecord.get("uniqueId") != "") {
					String uniqueId = eyeRecord.get("uniqueId").toString();
					Map<String, Object> customerQuery = Maps.newHashMap();
					customerQuery.put("uniqueId", uniqueId);
					if (StringUtils.isNotEmpty(district)) {
						customerQuery.put("district", district);
					}
					Map<String, Object> customer = healthCheckDAO.getDataByQuery("customer", customerQuery);
					if (customer != null) {
						/*if (customer.get("bloodSugarCondition") != null && customer.get("bloodSugarCondition") != "") {
							String bloodSugarCondition = customer.get("bloodSugarCondition").toString();
							if (bloodSugarCondition.equals("糖尿病患者")) {
								tnbCount ++; 
							}
						}
						
						if (customer.get("bloodPressureCondition") != null && customer.get("bloodPressureCondition") != "") {
							String bloodPressureCondition = customer.get("bloodPressureCondition").toString();
							if (bloodPressureCondition.equals("高血压患者")) {
								gxyCount ++; 
							}
						}
						
						if (customer.get("bloodLipidCondition") != null && customer.get("bloodLipidCondition") != "") {
							String bloodLipidCondition = customer.get("bloodLipidCondition").toString();
							if (bloodLipidCondition.equals("血脂异常患者")) {
								xzCount ++; 
							}
						}*/
						
						if (customer.get("dm") != null && !"".equals(customer.get("dm"))) {
							String bloodSugarCondition = customer.get("dm").toString();
							if (bloodSugarCondition.equals("是")) {
								tnbCount ++; 
							}
						}
						
						if (customer.get("htn") != null && !"".equals(customer.get("htn"))) {
							String bloodPressureCondition = customer.get("htn").toString();
							if (bloodPressureCondition.equals("是")) {
								gxyCount ++; 
							}
						}
						
						if (customer.get("hpl") != null && !"".equals(customer.get("hpl"))) {
							String bloodLipidCondition = customer.get("hpl").toString();
							if (bloodLipidCondition.equals("是")) {
								xzCount ++; 
							}
						}
						
						if (customer.get("BMI") != null && !"".equals(customer.get("BMI"))) {
							Double bmi = Double.parseDouble(customer.get("BMI").toString());
							if (bmi >= 28) {
								fpCount ++; 
							}
						}
					}
				}
			}
			
			Double tnbPerc = (double) 0;
			Double gxyPerc = (double) 0;
			Double xzPerc = (double) 0;
			Double fpPerc = (double) 0;
			//建档人数
			Long count = findRecordConditionCount(district);
			if (count != null && count != 0) {
				tnbPerc = ParamUtils.doubleScale(tnbCount.doubleValue() / count.doubleValue() * 100, 1);
				gxyPerc = ParamUtils.doubleScale(gxyCount.doubleValue() / count.doubleValue() * 100, 1);
				xzPerc = ParamUtils.doubleScale(xzCount.doubleValue() / count.doubleValue() * 100, 1);
				fpPerc = ParamUtils.doubleScale(fpCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			obj.put("tnbCount", tnbCount);
			obj.put("gxyCount", gxyCount);
			obj.put("xzCount", xzCount);
			obj.put("fpCount", fpCount);
			obj.put("tnbPerc", tnbPerc);
			obj.put("gxyPerc", gxyPerc);
			obj.put("xzPerc", xzPerc);
			obj.put("fpPerc", fpPerc);
			obj.put("num", num);
			results.add(obj);
		}
		return results;
	}
	
	static class MapComparator implements Comparator<Map<String, Object>> {
		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			Integer b1 = (Integer) o1.get("num");
			Integer b2 = (Integer) o2.get("num");
            if (b2 != null) {
                return b2.compareTo(b1);
            }
            return 0;
		}
 
    }
	
	public void downData(String startTime, String endTime) throws Exception {
		List<Map<String, Object>> datas = healthCheckDAO.getCustomerData("healthcheck", startTime, endTime);
		List<String> titles = getTitles();
		
		Workbook workbook = new XSSFWorkbook();
        // 生成一个表格
        Sheet sheet = workbook.createSheet();
        
        // 设置表格默认列宽度为15个字节
       // sheet.setDefaultColumnWidth((short) 15);
        /*
         * 创建标题行
         */
        Row row = sheet.createRow(0);
        // 存储标题在Excel文件中的序号
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
        }
        
        String[] names = {"customerId", "mobile", "name", "gender", "age", "birthday", "nationality", "householdRegistrationType", 
        		"contactName", "contactMobile", "address", "remarks", "disease", "familyHistory", "height", "weight", "BMI", "waistline", "highPressure", 
        		"lowPressure", "pulse", "temperature", "oxygen", "hipline", "WHR", "fatContent", "bloodGlucose", "bloodGlucose2h",
        		"bloodGlucoseRandom", "tc", "tg", "ldl", "hdl", "tizhi", "eyeCheck", "riskScore", "bloodSugarCondition",
        		"bloodLipidCondition", "bloodPressureCondition", "classifyResult", "checkDate", "OGTTTest", "bloodLipidTest", 
        		"bloodPressureTest", "geneTest"};
        Map<String, Object> tokenMap = authService.applyToken();
        for (int r = 0; r < datas.size(); r ++) {
        	 Row valueRow = sheet.createRow(r+1);
        	 Map<String, Object> data = datas.get(r);
        	 
        	 if (data != null) {
        		 if (data.get("uniqueId") != null) {
        			 
        			 if (tokenMap == null) {
        				 tokenMap = authService.applyToken();
        			 }
        			 Map<String,Object> secretMap = authService.requestInfoByUniqueId(data.get("uniqueId").toString(), tokenMap.get("token").toString(), tokenMap.get("userId").toString());
        			 data.putAll(secretMap);
        			 for (int j = 0; j < names.length; j++) {
                		 setCellValue(j, names[j], valueRow, data);
                	 }
        		 }
        	 }
        	 
        }
        /*
         * 写入到文件中
         */
        
        String tempDir = PropertyUtils.getProperty("system.temp.dir");
		File dir = new File(tempDir, "data");
	    if(!dir.exists()){
	    	dir.mkdirs();
	    }
	    
		String uuid =  UUID.randomUUID().toString();
		File newFile = new File(dir, uuid + ".xlsx");
        File file = new File(newFile.getAbsolutePath());
        OutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
		
	}
	
	public Message updateHcData(Map<String,Object> map) {
		Map<String, Object> result = Maps.newHashMap();
		
		/*if (map.get("ctime") != null && map.get("ctime") != "") {
			Long ctime = (Long) map.get("ctime");
			map.put("ctime", new Date(ctime));
		}
		
		if (map.get("utime") != null && map.get("utime") != "") {
			Long utime = (Long) map.get("utime");
			map.put("utime", new Date(utime));
		}
		
		if (map.get("checkTime") != null && map.get("checkTime") != "") {
			Long checkTime = (Long) map.get("checkTime");
			map.put("checkTime", new Date(checkTime));
		}*/
		
		String collName = (String) map.remove("collName");
		String id = saveData1(collName, map);
		if (StringUtils.isNotEmpty(id)) {
			result.put("code", 0);
			result.put("data", id);
		} else {
			result.put("code", 1);
		}
		return Message.dataMap(result);
	}
	
	
	
	
	public List<String> getTitles() {
		List<String> titles = Lists.newArrayList();
//		titles.add("UniqueId");
		titles.add("身份证号");
		titles.add("移动电话");
		titles.add("姓名");
		titles.add("性别");
		titles.add("年龄");
		titles.add("出生日期");
		titles.add("民族");
		titles.add("常住类型");
		titles.add("联系人姓名");
		titles.add("联系人电话");
		titles.add("地址");
		titles.add("备注信息");
		titles.add("已患有哪种疾病");
		titles.add("糖尿病家族史");
		titles.add("身高cm");
		titles.add("体重kg");
		titles.add("体质指数");
		titles.add("腰围cm");
		titles.add("收缩压mmHg");
		titles.add("舒张压mmHg");
		/*titles.add("脉率次/分钟");
		titles.add("体温℃");
		titles.add("血氧（%）");
		titles.add("臀围（cm）");
		titles.add("腰臀比");
		titles.add("体脂率%");
		titles.add("空腹血糖（mmol/L）");
		titles.add("餐后2h血糖（mmol/L）");
		titles.add("随机血糖（mmol/L）");
		titles.add("总胆固醇（mmol/L）");
		titles.add("甘油三酯（mmol/L）");
		titles.add("低密度脂蛋白（mmol/L）");
		titles.add("高密度脂蛋白（mmol/L）");*/
		titles.add("中医体质辨识结果");
		titles.add("眼象信息");
		titles.add("糖尿病危险因素评估分数");
		titles.add("血糖情况");
		titles.add("血脂情况");
		titles.add("血压情况");
		titles.add("人群分类结果");
		titles.add("检测时间");
		titles.add("OGTT检测");
		titles.add("血脂检测");
		titles.add("血压检测");
		titles.add("基因检测");
		return titles;
	}
	
	public void setCellValue(int i, String name, Row row, Map<String, Object> rowMap) {
		Cell cell = row.createCell(i);
	   	if (rowMap.get(name) != null) {
	   		cell.setCellValue(rowMap.get(name).toString());
	   	}
	}
	
	
	public void exportXmlData(String startTime, String endTime, HttpServletResponse response) throws IOException {
		List<Map<String, Object>> datas = healthCheckDAO.getCustomerData("healthcheck", startTime, endTime);
		Document doc = parseXml(datas);
		
		String tempDir = PropertyUtils.getProperty("system.temp.dir");
		File dir = new File(tempDir, "xml");
	    if(!dir.exists()){
	    	dir.mkdirs();
	    }
		
		String uuid =  UUID.randomUUID().toString();
		File file = new File(dir, uuid + ".xml");
		
		try {
			FileOutputStream out = new FileOutputStream(file);
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter xmlw = new XMLWriter(out,format);
			xmlw.write(doc);
			xmlw.close();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		downToLocal(file.getAbsolutePath(), response);
		
		System.out.println("that's over!");
	}
	
	private Document parseXml(List<Map<String, Object>> datas) {
		List<String> titles = getXmlTitles();
		String[] names = {"customerId", "name", "gender", "birthday", "age", "mobile",  "height", 
					 "weight", "BMI", "waistline", "highPressure", "lowPressure", "pulse",  "temperature", 
					 "bloodGlucose", "bloodGlucose2h", "bloodGlucoseRandom", 
					 "tc", "tg", "ldl", "hdl", "tizhi", "oxygen", "WHR", "fatContent", "eyeCheck", "riskScore",
					 "bloodSugarCondition", "bloodLipidCondition", "bloodPressureCondition", "classifyResult",
					 "checkDate", "OGTTTest", "bloodLipidTest", "bloodPressureTest", "geneTest", "disease", "familyHistory","hipline"};
		
		String[] types = {"string", "string", "string", "date", "number", "string", "number1", 
					"number2", "number2", "number1", "number", "number", "number", "number1",
					"number1", "number1", "number1", "number2", "number1",
					"number2", "number2", "string", "", "", "", "", "", "", "", "", "", "", "","", "","", "","",""};
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("emp-list");
		for (Map<String, Object> data : datas) {
			Map<String, Object> tokenMap = authService.applyToken();
			
			if (data.get("uniqueId") != null && !"".equals(data.get("uniqueId"))) {
				String uniqueId = data.get("uniqueId").toString();
				Map<String, Object> userInfo = authService.requestInfoByUniqueId(uniqueId, tokenMap.get("token").toString(), tokenMap.get("userId").toString());
				if (userInfo != null) {
					data.putAll(userInfo);
				}
			}
			
			Element emp = root.addElement("emp");
			for (int i = 0; i < titles.size(); i ++) {
				
				Element name = emp.addElement(titles.get(i));
				Object val = data.get(names[i]);
				if (names[i].equals("gender")) {
					if (val == null || "".equals(val)) {
						name.setText("0");
					} else if (val.toString().equals("男")) {
						name.setText("1");
					} else if (val.toString().equals("女")) {
						name.setText("2");
					}
				} else if (names[i].equals("tizhi")) {
					if (val == null || "".equals(val)) {
						name.setText("99");
					} else if (val.toString().equals("平和质")) {
						name.setText("01");
					} else if (val.toString().equals("气虚质")) {
						name.setText("02");
					} else if (val.toString().equals("阳虚质")) {
						name.setText("03");
					} else if (val.toString().equals("阴虚质")) {
						name.setText("04");
					} else if (val.toString().equals("痰湿质")) {
						name.setText("05");
					} else if (val.toString().equals("湿热质")) {
						name.setText("06");
					} else if (val.toString().equals("血瘀质")) {
						name.setText("07");
					} else if (val.toString().equals("气郁质")) {
						name.setText("08");
					} else if (val.toString().equals("特秉质")) {
						name.setText("09");
					}
				} else {
					if (val != null && !"".equals(val)) {
						if (types[i].equals("number1")) {//
							name.setText(ParamUtils.decimalFormat1(ParamUtils.doubleScale(Double.parseDouble(val.toString()), 1), "0.0"));
						} else if (types[i].equals("number2")) {
							name.setText(ParamUtils.decimalFormat1(ParamUtils.doubleScale(Double.parseDouble(val.toString()), 1), "0.00"));
						} else if (types[i].equals("date")) {
							name.setText(ParamUtils.getDate2(val.toString()));
						} else {
							name.setText(data.get(names[i]).toString());
						}
					}
				}
			}
		}
		return doc;
	}
	
	public void downToLocal(String path, HttpServletResponse response) throws IOException {
		FileInputStream in = null;
		OutputStream out = null;
		try {
			//获取文件名
			String filename = path.substring(path.lastIndexOf("\\") + 1);
			filename = new String(filename.getBytes("iso8859-1"),"UTF-8");
	
			String downloadpath = path;
			
			//设置响应头，控制浏览器下载该文件
			response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
			//读取要下载的文件，保存到文件输入流
			 in= new FileInputStream(downloadpath);
			//创建输出流
			 out= response.getOutputStream();
			//缓存区
			byte buffer[] = new byte[1024];
			int len = 0;
			//循环将输入流中的内容读取到缓冲区中
			while((len = in.read(buffer)) > 0){
			    out.write(buffer, 0, len);
			}
		}finally {
			//关闭
			if (in != null) {
				try {  
					in.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }
			}
              

            if (out != null) {
            	try {  
                    out.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }
            }
		}
	}
	
	
	private List<String> getXmlTitles() {
		List<String> titles = Lists.newArrayList();
//		titles.add("编号");
		titles.add("ID card No");
		titles.add("name");
		titles.add("gender");
		titles.add("birthdate");
		titles.add("age");
		titles.add("mobile");
		titles.add("height");
		titles.add("weight");
		titles.add("BMI");
		titles.add("waist");
		titles.add("systolic pressure");
		titles.add("diastolic pressure");
		titles.add("pulse");
		titles.add("temperature");
		titles.add("GLU");
		titles.add("2hPG");
		titles.add("RBS");
		titles.add("TC");
		titles.add("TG");
		titles.add("LDL");
		titles.add("HDL");
		titles.add("constitution in Chinese medicine");
		titles.add("SaO2");
		titles.add("WHR");
		titles.add("BFR");
		titles.add("Eye Imaging Result");
		titles.add("Diabetes Risk");
		titles.add("blood glucose level");
		titles.add("blood lipid level");
		titles.add("blood pressure level");
		titles.add("abnormal condition");
		titles.add("detection time");
		titles.add("OGTT detection");
		titles.add("blood lipid detection");
		titles.add("blood pressure detection");
		titles.add("genetic testing");
		titles.add("medical history");
		titles.add("family history of diabetes");
		titles.add("hip");
		
		return titles;
	}
	
	public List<Map<String,Object>> getCustomerDataPage(Page page, Map<String, Object> queryMap, Map<String, Object> sortMap, String collName, long totalCount) {
		page.setTotalCount(totalCount);
		return healthCheckDAO.getCustomerDataPage(page, queryMap, sortMap, collName);
	}
	
	public long queryCustomerCount(Map<String, Object> queryMap, String collName) {
		long count = healthCheckDAO.getCustomerCount(queryMap, collName);
		return count;
	}
	
	/**
	 * 精筛在总人数
	 * @param district
	 * @return
	 */
	public Long healthcheckDetailCount(String district) {
		Map<String, Object> query = Maps.newHashMap();
		if (StringUtils.isNotEmpty(district)) {
			query.put("district", district);
		}
		
		query.put("classifyResultJs", new BasicDBObject("$exists", true));
		
		Long count = healthCheckDAO.countByQuery("customer", query);
		if (count == null) {
			count = (long) 0;
		}
		return count;
	}
	
	/**
	 * 精筛数据->血糖筛查情况->血糖情况人群分布->糖尿病患者/糖尿病前期人群/正常人群
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodSugarConditionPeopleDistributionHealthCheckDetail(String district) {
		Map<String, Object> map = healthCheckDAO.findBloodSugarConditionPeopleDistributionHeadthCheckDetail(district);
		
		int count = healthCheckDAO.healthcheckDetailBloodSugarCount(district);
		
//		Integer diabetesCount = 0;
		Integer newDiabetesCount = 0;
		Integer preDiabetesCount = 0;
		Integer normalCount = 0;
		
//		Double diabetesPerc = (double) 0;
		Double newDiabetesPerc = (double) 0;
		Double preDiabetesPerc = (double) 0;
		Double normalPerc = (double) 0;
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		
		if (map != null && count != 0) {
//			if (map.get("已登记糖尿病患者") != null) {
//				diabetesCount = Integer.parseInt(map.get("已登记糖尿病患者").toString());
//				diabetesPerc = ParamUtils.doubleScale(diabetesCount.doubleValue() / count * 100, 1);
//			}
			
			if (map.get("新发现糖尿病患者") != null) {
				newDiabetesCount = Integer.parseInt(map.get("新发现糖尿病患者").toString());
				newDiabetesPerc = ParamUtils.doubleScale(newDiabetesCount.doubleValue() / count * 100, 1);
			}
			
			if (map.get("糖尿病前期人群") != null) {
				preDiabetesCount = Integer.parseInt(map.get("糖尿病前期人群").toString());
				preDiabetesPerc = ParamUtils.doubleScale(preDiabetesCount.doubleValue() / count * 100, 1);
			}
			
			if (map.get("正常人群") != null) {
				normalCount = Integer.parseInt(map.get("正常人群").toString());
				normalPerc = ParamUtils.doubleScale(normalCount.doubleValue() / count * 100, 1);
			}
			
			if (normalCount == 0) {
				normalPerc = (double) 0;
				if (preDiabetesCount != 0) {
//					preDiabetesPerc = ParamUtils.doubleScale(100-diabetesPerc-newDiabetesPerc, 1);
					preDiabetesPerc = ParamUtils.doubleScale(100-newDiabetesPerc, 1);
				} else {
					if (newDiabetesCount != 0) {
//						newDiabetesPerc = ParamUtils.doubleScale(100-diabetesPerc, 1);
						newDiabetesPerc = (double) 100;
					}
				}
			} else {
//				normalPerc = ParamUtils.doubleScale(100-diabetesPerc-newDiabetesPerc-preDiabetesPerc, 1);
				normalPerc = ParamUtils.doubleScale(100-newDiabetesPerc-preDiabetesPerc, 1);
				if (normalPerc < 0){
					normalPerc = (double) 0;
				}
			}
		}
		
		
//		countList.add(diabetesCount);
		countList.add(newDiabetesCount);
		countList.add(preDiabetesCount);
		countList.add(normalCount);
		
//		percList.add(diabetesPerc);
		percList.add(newDiabetesPerc);
		percList.add(preDiabetesPerc);
		percList.add(normalPerc);
		
//		Map<String, Object> map1 = Maps.newHashMap();
//		map1.put("item", "糖尿病患者");
//		map1.put("count", diabetesCount);
//		map1.put("percent", diabetesPerc);
//		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("item", "新发现糖尿病患者");
		map2.put("count", newDiabetesCount);
		map2.put("percent", newDiabetesPerc);
		list.add(map2);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("item", "糖尿病前期人群");
		map3.put("count", preDiabetesCount);
		map3.put("percent", preDiabetesPerc);
		list.add(map3);
		
		Map<String, Object> map4 = Maps.newHashMap();
		map4.put("item", "血糖正常人群");
		map4.put("count", normalCount);
		map4.put("percent", normalPerc);
		list.add(map4);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("list", list);
		return result;
	}
	
	/**
	 * 精筛数据->血糖筛查情况->血糖情况年龄分布->糖尿病患者/糖尿病前期人群/正常人群
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodSugarConditionAgeDistributionHealthCheckDetail(String district) {
		List<Map<String,Object>> ageList = getAgeList();
		Map<Object, Object> m = healthCheckDAO.findBloodSugarConditionAgeDistributionHealthCheckDetail(district, ageList);
		//{{ "ds" : "糖尿病前期人群" , "ages" : "-"}=2, { "ds" : "-" , "ages" : "-"}=3, { "ds" : "糖尿病前期人群" , "ages" : "60-65"}=1, { "ds" : "-" , "ages" : "40-44"}=1, { "ds" : "-" , "ages" : "55-59"}=1, { "ds" : "-" , "ages" : "50-54"}=1}
		
//		List<Map<String,Object>> ageList1 = ageList(ageList);
		List<Map<String,Object>> ageList2 = ageList(ageList);
		List<Map<String,Object>> ageList3 = ageList(ageList);
		List<Map<String,Object>> ageList4 = ageList(ageList);
		
		for (Map.Entry<Object, Object> entry : m.entrySet()) { 
			Map<String,String> key = (Map<String,String>)entry.getKey();
			Object value = entry.getValue();
			String ds = key.get("ds");
			String ages = key.get("ages");
			if(!StringUtils.isEmpty(ds) && !StringUtils.isEmpty(ages)){
				switch (ds) {
//				case "已登记糖尿病患者":
//					for(int i =0;i<ageList1.size();i++){
//						Map<String,Object> map = ageList1.get(i);
//						if(map.keySet().contains(ages)){
//							ageList1.get(i).put(ages, value);
//						}
//					}
//					break;
				case "新发现糖尿病患者":
					for(int i =0;i<ageList2.size();i++){
						Map<String,Object> map = ageList2.get(i);
						if(map.keySet().contains(ages)){
							ageList2.get(i).put(ages, value);
						}
					}
					break;
				case "糖尿病前期人群":
					for(int i =0;i<ageList3.size();i++){
						Map<String,Object> map = ageList3.get(i);
						if(map.keySet().contains(ages)){
							ageList3.get(i).put(ages, value);
						}
					}
					break;
				case "正常人群":
					for(int i =0;i<ageList4.size();i++){
						Map<String,Object> map = ageList4.get(i);
						if(map.keySet().contains(ages)){
							ageList4.get(i).put(ages, value);
						}
					}
					break;
				default:
					break;
				}
			}
		}
		
//		String[] ages = {"35-39岁", "40-44岁", "45-49岁", "50-54岁", "55-59岁", "60-65岁"};
//		String[] ages1 = {"35-39", "40-44", "45-49", "50-54", "55-59", "60-65"};
		//ageList1 = {"35-39", "40-44", "45-49", "50-54", "55-59", "60-65"};
		List<String> ages = getAges(ageList);
		
		List<Integer> diabetesList = Lists.newArrayList();
		List<Integer> newDiabetesList = Lists.newArrayList();
		List<Integer> preDiabetesList = Lists.newArrayList();
		List<Integer> normalList = Lists.newArrayList();
		
		List<Map<String, Object>> list = Lists.newArrayList();
		
		for (int i = 0; i < ageList2.size(); i ++) {
//			Map<String, Object> map1 = Maps.newHashMap();
//			map1.put("type", ages.get(i) + "岁");
//			map1.put("disease", "已登记糖尿病患者");
//			map1.put("count", ageList1.get(i).get(ages.get(i)));
//			list.add(map1);
//			diabetesList.add(Integer.parseInt(ageList1.get(i).get(ages.get(i)).toString()));
			
			Map<String, Object> map2 = Maps.newHashMap();
			map2.put("type", ages.get(i) + "岁");
			map2.put("disease", "新发现糖尿病患者");
			map2.put("count", ageList2.get(i).get(ages.get(i)));
			list.add(map2);
			newDiabetesList.add(Integer.parseInt(ageList2.get(i).get(ages.get(i)).toString()));
			
			Map<String, Object> map3 = Maps.newHashMap();
			map3.put("type", ages.get(i) + "岁");
			map3.put("disease", "糖尿病前期人群");
			map3.put("count", ageList3.get(i).get(ages.get(i)));
			list.add(map3);
			preDiabetesList.add(Integer.parseInt(ageList3.get(i).get(ages.get(i)).toString()));
			
			Map<String, Object> map4 = Maps.newHashMap();
			map4.put("type", ages.get(i) + "岁");
			map4.put("disease", "血糖正常人群");
			map4.put("count", ageList4.get(i).get(ages.get(i)));
			list.add(map4);
			normalList.add(Integer.parseInt(ageList4.get(i).get(ages.get(i)).toString()));
			
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("list", list);
		result.put("diabetes", diabetesList);
		result.put("newDiabetes", newDiabetesList);
		result.put("preDiabetes", preDiabetesList);
		result.put("normal", normalList);
		result.put("ages", ages);
		
		return result;
	}
	
	/**
	 * 精筛数据->血糖筛查情况->并发症筛查统计->已做并发症/未做并发症
	 * @param district
	 * @return
	 */
	public Map<String, Object> findComplicationDistributionHealthCheckDetail(String district) {
		Map<String, Object> map = healthCheckDAO.findBloodSugarConditionPeopleDistributionHeadthCheckDetail(district);
		
		int count = healthCheckDAO.healthcheckDetailBloodSugarCount(district);
		
		Double complicationPerc = (double) 0;
		Double noComplicationPerc = (double) 0;
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		
		//已做并发症人数
		Integer complicationCount = healthCheckDAO.healthcheckDetailComplicationCount(district);
		//未做并发症人数
		Integer noComplicationCount = healthCheckDAO.healthcheckDetailNoComplicationCount(district);
		
		count = complicationCount + noComplicationCount;
		
		if (map != null && count != 0) {
			complicationPerc = ParamUtils.doubleScale(complicationCount.doubleValue() / count * 100, 1);
			
			noComplicationPerc = ParamUtils.doubleScale(noComplicationCount.doubleValue() / count * 100, 1);
			
			if (noComplicationCount == 0) {
				noComplicationPerc = (double) 0;
				if (complicationCount != 0) {
					complicationPerc = (double) 100;
				} 
			} else {
				noComplicationPerc = ParamUtils.doubleScale(100-complicationPerc, 1);
				if (noComplicationPerc < 0){
					noComplicationPerc = (double) 0;
				}
			}
		}
		
		
		countList.add(complicationCount);
		countList.add(noComplicationCount);
		
		percList.add(complicationPerc);
		percList.add(noComplicationPerc);
		
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("item", "已做并发症");
		map1.put("count", complicationCount);
		map1.put("percent", complicationPerc);
		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("item", "未做并发症");
		map2.put("count", noComplicationCount);
		map2.put("percent", noComplicationPerc);
		list.add(map2);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("list", list);
		return result;
	}
	
	
	/**
	 * 精筛数据->血压筛查情况->血压情况人群分布->已登记高血压患者/新发现高血压人群/正常人群/高血压前期人群
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodPressureConditionPeopleDistributionHealthCheck(String district) {
		Map<String, Object> map = healthCheckDAO.findBloodPressureConditionPeopleDistributionHealthCheck(district);
		
		int count = healthCheckDAO.healthcheckDetailBloodPressureCount(district);
		
//		Integer hypertendionCount = 0;
		Integer newHypertensionCount = 0;
		Integer preHypertensionCount = 0;
		Integer normalCount = 0;
		
//		Double hypertendionPerc = (double) 0;
		Double preHypertensionPerc = (double) 0;
		Double newHypertensionPerc = (double) 0;
		Double normalPerc = (double) 0;
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		
		if (map != null && count != 0) {
			/*if (map.get("已登记高血压患者") != null) {
				hypertendionCount = Integer.parseInt(map.get("已登记高血压患者").toString());
				hypertendionPerc = ParamUtils.doubleScale(hypertendionCount.doubleValue() / count * 100, 1);
			}*/
			
			if (map.get("新发现高血压人群") != null) {
				newHypertensionCount = Integer.parseInt(map.get("新发现高血压人群").toString());
				newHypertensionPerc = ParamUtils.doubleScale(newHypertensionCount.doubleValue() / count * 100, 1);
			}
			
			if (map.get("高血压前期人群") != null) {
				preHypertensionCount = Integer.parseInt(map.get("高血压前期人群").toString());
				preHypertensionPerc = ParamUtils.doubleScale(preHypertensionCount.doubleValue() / count * 100, 1);
			}
			
			if (map.get("正常人群") != null) {
				normalCount = Integer.parseInt(map.get("正常人群").toString());
//				normalPerc = ParamUtils.doubleScale(normalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (normalCount == 0) {
				normalPerc = (double) 0;
				if (preHypertensionCount != 0) {
//					preHypertensionPerc = ParamUtils.doubleScale(100 - hypertendionPerc - newHypertensionPerc , 1);
					preHypertensionPerc = ParamUtils.doubleScale(100 - newHypertensionPerc , 1);
				} else {
					if (newHypertensionCount != 0) {
//						newHypertensionPerc = ParamUtils.doubleScale(100 - hypertendionPerc, 1);
						newHypertensionPerc = (double) 100;
					}
				}
			} else {
//				normalPerc = ParamUtils.doubleScale(100 - hypertendionPerc - newHypertensionPerc - preHypertensionPerc, 1);
				normalPerc = ParamUtils.doubleScale(100 - newHypertensionPerc - preHypertensionPerc, 1);
				if (normalPerc < 0) {
					normalPerc = (double) 0;
				}
			}
		}
		
//		countList.add(hypertendionCount);
		countList.add(newHypertensionCount);
		countList.add(preHypertensionCount);
		countList.add(normalCount);
		
//		percList.add(hypertendionPerc);
		percList.add(newHypertensionPerc);
		percList.add(preHypertensionPerc);
		percList.add(normalPerc);
		
//		Map<String, Object> map1 = Maps.newHashMap();
//		map1.put("item", "已登记高血压患者");
//		map1.put("count", hypertendionCount);
//		map1.put("percent", hypertendionPerc);
//		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("item", "新发现高血压人群");
		map2.put("count", newHypertensionCount);
		map2.put("percent", newHypertensionPerc);
		list.add(map2);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("item", "高血压前期人群");
		map3.put("count", preHypertensionCount);
		map3.put("percent", preHypertensionPerc);
		list.add(map3);
		
		Map<String, Object> map4 = Maps.newHashMap();
		map4.put("item", "血压正常人群");
		map4.put("count", normalCount);
		map4.put("percent", normalPerc);
		list.add(map4);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("list", list);
		return result;
	}
	
	
	/**
	 * 精筛数据->血压筛查情况->血压情况年龄分布->已登记高血压患者/新发现高血压人群/正常人群/高血压前期人群
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodPressureConditionAgeDistributionHealthCheckDetail(String district) {
		List<Map<String,Object>> ageList = getAgeList();
		Map<Object, Object> m = healthCheckDAO.findBloodPressureConditionAgeDistributionHealthCheckDetail(district, ageList);
		
//		List<Map<String,Object>> ageList1 = ageList(ageList);
		List<Map<String,Object>> ageList2 = ageList(ageList);
		List<Map<String,Object>> ageList3 = ageList(ageList);
		List<Map<String,Object>> ageList4 = ageList(ageList);
		List<Map<String,Object>> ageList5 = ageList(ageList);
		
		for (Map.Entry<Object, Object> entry : m.entrySet()) { 
			Map<String,String> key = (Map<String,String>)entry.getKey();
			Object value = entry.getValue();
			String bp = key.get("bp");
			String ages = key.get("ages");
			if(!StringUtils.isEmpty(bp) && !StringUtils.isEmpty(ages)){
				switch (bp) {
//				case "已登记高血压患者":
//					for(int i =0;i<ageList1.size();i++){
//						Map<String,Object> map = ageList1.get(i);
//						if(map.keySet().contains(ages)){
//							ageList1.get(i).put(ages, value);
//						}
//					}
//					break;
				case "高血压前期人群":
					for(int i =0;i<ageList2.size();i++){
						Map<String,Object> map = ageList2.get(i);
						if(map.keySet().contains(ages)){
							ageList2.get(i).put(ages, value);
						}
					}
					break;
				case "新发现高血压人群":
					for(int i =0;i<ageList3.size();i++){
						Map<String,Object> map = ageList3.get(i);
						if(map.keySet().contains(ages)){
							ageList3.get(i).put(ages, value);
						}
					}
					break;
				case "正常人群":
					for(int i =0;i<ageList4.size();i++){
						Map<String,Object> map = ageList4.get(i);
						if(map.keySet().contains(ages)){
							ageList4.get(i).put(ages, value);
						}
					}
					break;
				case "-":
					for(int i =0;i<ageList5.size();i++){
						Map<String,Object> map = ageList5.get(i);
						if(map.keySet().contains(ages)){
							ageList5.get(i).put(ages, value);
						}
					}
					break;
				default:
					break;
				}
			}
		}
		
		//ageList1 = {"35-39", "40-44", "45-49", "50-54", "55-59", "60-65"};
		List<String> ages = getAges(ageList);
		
//		List<Integer> hypertendionList = Lists.newArrayList();
		List<Integer> preHypertendionList = Lists.newArrayList();
		List<Integer> newHypertendionList = Lists.newArrayList();
		List<Integer> normalList = Lists.newArrayList();
		
		List<Map<String, Object>> list = Lists.newArrayList();
		
		for (int i = 0; i < ageList2.size(); i ++) {
//			Map<String, Object> map1 = Maps.newHashMap();
//			map1.put("type", ages.get(i) + "岁");
//			map1.put("disease", "已登记高血压患者");
//			map1.put("count", ageList1.get(i).get(ages.get(i)));
//			list.add(map1);
//			hypertendionList.add(Integer.parseInt(ageList1.get(i).get(ages.get(i)).toString()));
			
			Map<String, Object> map2 = Maps.newHashMap();
			map2.put("type", ages.get(i) + "岁");
			map2.put("disease", "新发现高血压人群");
			map2.put("count", ageList3.get(i).get(ages.get(i)));
			list.add(map2);
			newHypertendionList.add(Integer.parseInt(ageList3.get(i).get(ages.get(i)).toString()));
			
			Map<String, Object> map3 = Maps.newHashMap();
			map3.put("type", ages.get(i) + "岁");
			map3.put("disease", "高血压前期人群");
			map3.put("count", ageList2.get(i).get(ages.get(i)));
			list.add(map3);
			preHypertendionList.add(Integer.parseInt(ageList2.get(i).get(ages.get(i)).toString()));
			
			Map<String, Object> map4 = Maps.newHashMap();
			map4.put("type", ages.get(i) + "岁");
			map4.put("disease", "血压正常人群");
			map4.put("count", ageList4.get(i).get(ages.get(i)));
			list.add(map4);
			normalList.add(Integer.parseInt(ageList4.get(i).get(ages.get(i)).toString()));
			
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("list", list);
//		result.put("hypertendion", hypertendionList);
		result.put("preHypertendion", preHypertendionList);
		result.put("newHypertendion", newHypertendionList);
		result.put("normal", normalList);
		result.put("ages", ages);
		
		return result;
	}
	
	/**
	 * 精筛数据->血脂筛查情况->血脂情况人群分布->已登记血脂异常患者\新发现血脂异常患者\正常人群 
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodLipidConditionPeopleDistributionHealthCheck(String district) {
		Map<String, Object> map = healthCheckDAO.findBloodLipidConditionPeopleDistributionHealthCheck(district);
		
		int count = healthCheckDAO.healthcheckDetailBloodLipidCount(district);
		
//		Integer highLipidCount = 0;
		Integer newHighLipidCount = 0;
		Integer normalCount = 0;
		
//		Double highLipidPerc = (double) 0;
		Double newHighLipidPerc = (double) 0;
		Double normalPerc = (double) 0;
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		
		if (map != null && count != 0) {
			/*if (map.get("已登记血脂异常患者") != null) {
				highLipidCount = Integer.parseInt(map.get("已登记血脂异常患者").toString());
				highLipidPerc = ParamUtils.doubleScale(highLipidCount.doubleValue() / count * 100, 1);
			}*/
			
			if (map.get("新发现血脂异常患者") != null) {
				newHighLipidCount = Integer.parseInt(map.get("新发现血脂异常患者").toString());
				newHighLipidPerc = ParamUtils.doubleScale(newHighLipidCount.doubleValue() / count * 100, 1);
			}
			
			if (map.get("正常人群") != null) {
				normalCount = Integer.parseInt(map.get("正常人群").toString());
//				normalPerc = ParamUtils.doubleScale(normalCount.doubleValue() / count * 100, 1);
			}
			
			if (normalCount == 0) {
				normalPerc = (double) 0;
				if (newHighLipidCount != 0) {
					newHighLipidPerc = (double) 100;
				}
			} else {
				normalPerc = ParamUtils.doubleScale(100-newHighLipidPerc, 1);
				if (normalPerc < 0 || (normalPerc == 100 && count == 0)) {
					normalPerc = (double) 0;
				} 
			}
			
		}
		
//		countList.add(highLipidCount);
		countList.add(newHighLipidCount);
		countList.add(normalCount);
		
//		percList.add(highLipidPerc);
		percList.add(newHighLipidPerc);
		percList.add(normalPerc);
		
//		Map<String, Object> map1 = Maps.newHashMap();
//		map1.put("item", "已登记血脂异常患者");
//		map1.put("count", highLipidCount);
//		map1.put("percent", highLipidPerc);
//		list.add(map1);
		
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("item", "新发现血脂异常患者");
		map2.put("count", newHighLipidCount);
		map2.put("percent", newHighLipidPerc);
		list.add(map2);
		
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("item", "血脂正常人群");
		map3.put("count", normalCount);
		map3.put("percent", normalPerc);
		list.add(map3);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("list", list);
		return result;
	}
	
	
	/**
	 * 精筛数据->血脂筛查情况->血脂情况年龄分布->已登记血脂异常患者\新发现血脂异常患者\正常人群 
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodLipidConditionAgePeopleDistributionHealthCheck(String district) {
		List<Map<String,Object>> ageList = getAgeList();
		Map<Object, Object> m = healthCheckDAO.findBloodLipidConditionAgePeopleDistributionHealthCheck(district, ageList);
		
		List<Map<String,Object>> ageList1 = ageList(ageList);
		List<Map<String,Object>> ageList2 = ageList(ageList);
		List<Map<String,Object>> ageList3 = ageList(ageList);
		List<Map<String,Object>> ageList4 = ageList(ageList);
		
		for (Map.Entry<Object, Object> entry : m.entrySet()) { 
			Map<String,String> key = (Map<String,String>)entry.getKey();
			Object value = entry.getValue();
			String bp = key.get("bl");
			String ages = key.get("ages");
			if(!StringUtils.isEmpty(bp) && !StringUtils.isEmpty(ages)){
				switch (bp) {
				case "已登记血脂异常患者":
					for(int i =0;i<ageList1.size();i++){
						Map<String,Object> map = ageList1.get(i);
						if(map.keySet().contains(ages)){
							ageList1.get(i).put(ages, value);
						}
					}
					break;
				case "新发现血脂异常患者":
					for(int i =0;i<ageList2.size();i++){
						Map<String,Object> map = ageList2.get(i);
						if(map.keySet().contains(ages)){
							ageList2.get(i).put(ages, value);
						}
					}
					break;
				case "正常人群":
					for(int i =0;i<ageList3.size();i++){
						Map<String,Object> map = ageList3.get(i);
						if(map.keySet().contains(ages)){
							ageList3.get(i).put(ages, value);
						}
					}
					break;
				case "-":
					for(int i =0;i<ageList4.size();i++){
						Map<String,Object> map = ageList4.get(i);
						if(map.keySet().contains(ages)){
							ageList4.get(i).put(ages, value);
						}
					}
					break;
				default:
					break;
				}
			}
		}
		
		//ageList1 = {"35-39", "40-44", "45-49", "50-54", "55-59", "60-65"};
		List<String> ages = getAges(ageList);
		
		List<Integer> highLipidList = Lists.newArrayList();
		List<Integer> newHighLipidList = Lists.newArrayList();
		List<Integer> normalList = Lists.newArrayList();
		
		List<Map<String, Object>> list = Lists.newArrayList();
		
		for (int i = 0; i < ageList1.size(); i ++) {
			Map<String, Object> map1 = Maps.newHashMap();
			map1.put("type", ages.get(i) + "岁");
			map1.put("disease", "已登记血脂异常患者");
			map1.put("count", ageList1.get(i).get(ages.get(i)));
			list.add(map1);
			highLipidList.add(Integer.parseInt(ageList1.get(i).get(ages.get(i)).toString()));
			
			Map<String, Object> map2 = Maps.newHashMap();
			map2.put("type", ages.get(i) + "岁");
			map2.put("disease", "新发现血脂异常患者");
			map2.put("count", ageList2.get(i).get(ages.get(i)));
			list.add(map2);
			newHighLipidList.add(Integer.parseInt(ageList2.get(i).get(ages.get(i)).toString()));
			
			Map<String, Object> map3 = Maps.newHashMap();
			map3.put("type", ages.get(i) + "岁");
			map3.put("disease", "血脂正常人群");
			map3.put("count", ageList3.get(i).get(ages.get(i)));
			list.add(map3);
			normalList.add(Integer.parseInt(ageList3.get(i).get(ages.get(i)).toString()));
			
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("list", list);
		result.put("highLipid", highLipidList);
		result.put("newHighLipid", newHighLipidList);
		result.put("normal", normalList);
		result.put("ages", ages);
//		result.put("-", otherList);
		
		return result;
	}
	
	/**
	 * 精筛数据->体质筛查情况->体质与代谢疾病患病率的关系
	 * @return
	 */
	public Map<String, Object> findTizhiConditionByDiseaseHealthCheckDetail(String district) {
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿","湿热", "血瘀", "气郁", "特禀"};
		String[] tizhis1 = {"A平和质", "B气虚质", "C阳虚质", "D阴虚质", "E痰湿质","F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		
		List<Integer> diabetesList = Lists.newArrayList();
		List<Integer> hypertensionList = Lists.newArrayList();
		List<Integer> hplList = Lists.newArrayList();
		List<Integer> fatList = Lists.newArrayList();
		
		List<Map<String, Object>> list = Lists.newArrayList();
		
		for (int i = 0; i < tizhis.length; i ++) {
			Map<String, Object> diabetesMap = healthCheckDAO.findTizhiConditionByDiabetesHealthCheckDetail(district, tizhis[i]);
			Map<String, Object> hypertensionMap = healthCheckDAO.findTizhiConditionByHypertensionHealthCheckDetail(district, tizhis[i]);
			Map<String, Object> hplMap = healthCheckDAO.findTizhiConditionByHplHealthCheckDetail(district, tizhis[i]);
			
			Integer diabetesCount = 0;
			Integer hypertensionCount = 0;
			Integer hplCount = 0;
			
			if (diabetesMap != null) {
				if (diabetesMap.containsKey("糖尿病患者")) {
					diabetesCount = Integer.parseInt(diabetesMap.get("糖尿病患者").toString());
				}
			}
			
			if (hypertensionMap != null) {
				if (hypertensionMap.containsKey("高血压患者")) {
					hypertensionCount = Integer.parseInt(hypertensionMap.get("高血压患者").toString());
				}
			}
			
			if (hplMap != null) {
				if (hplMap.containsKey("血脂异常患者")) {
					hplCount = Integer.parseInt(hplMap.get("血脂异常患者").toString());
				}
			}
			
			Integer fatCount = healthCheckDAO.getTizhiCount(tizhis[i], district, "肥胖人群", "", "", "", "js");
			
			diabetesList.add(diabetesCount);
			hypertensionList.add(hypertensionCount);
			hplList.add(hplCount);
			fatList.add(fatCount);
			
			Map<String, Object> map1 = Maps.newHashMap();
			map1.put("tizhi", tizhis1[i]);
			map1.put("disease", "糖尿病患者");
			map1.put("count", diabetesCount);
			list.add(map1);
			
			Map<String, Object> map2 = Maps.newHashMap();
			map2.put("tizhi", tizhis1[i]);
			map2.put("disease", "高血压患者");
			map2.put("count", hypertensionCount);
			list.add(map2);
			
			Map<String, Object> map3 = Maps.newHashMap();
			map3.put("tizhi", tizhis1[i]);
			map3.put("disease", "血脂异常患者");
			map3.put("count", hplCount);
			list.add(map3);
			
			Map<String, Object> map4 = Maps.newHashMap();
			map4.put("tizhi", tizhis1[i]);
			map4.put("disease", "肥胖");
			map4.put("count", fatCount);
			list.add(map4);
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("list", list);
		result.put("diabetesList", diabetesList);
		result.put("hypertensionList", hypertensionList);
		result.put("hplList", hplList);
		result.put("fatList", fatList);
		return result;
	}
	
	/**
	 * 
	 * @param district
	 * @param type 血糖情况/血压情况/血脂情况/肥胖情况/体质  bloodSugarCondition/bloodPressureCondition/bloodLipidCondition/BMI/tizhi
	 * @return
	 */
	public Long healthcheckCount(String district, String type) {
		Map<String, Object> query = Maps.newHashMap();
		if (StringUtils.isNotEmpty(district)) {
			query.put("district", district);
		}
		
		query.put(type, new BasicDBObject("$exists", true).append("$ne", "").append("$ne", null));
		
		Long count = healthCheckDAO.countByQuery("customer", query);
		if (count == null) {
			count = (long) 0;
		}
		return count;
	}
	
	public Long healthcheckJsCount(String district, String type) {
		Map<String, Object> query = Maps.newHashMap();
		if (StringUtils.isNotEmpty(district)) {
			query.put("district", district);
		}
		
		query.put(type, new BasicDBObject("$exists", true).append("$ne", null).append("$ne", ""));
		query.put("classifyResultJs", new BasicDBObject("$exists", true));
		
		Long count = healthCheckDAO.countByQuery("customer", query);
		if (count == null) {
			count = (long) 0;
		}
		return count;
	}
	
	/**
	 * 
	 * @param district
	 * @param type 
	 * @return
	 */
	public Long tizhiAndDiseaseCount(String type, String district) {
		Map<String, Object> query = Maps.newHashMap();
		if (StringUtils.isNotEmpty(district)) {
			query.put("district", district);
		}
		
		query.put("tizhi", new BasicDBObject("$exists", true).append("$ne", "").append("$ne", null));
		if(type.equals("bloodSugarCondition")) {
			query.put(type, "糖尿病患者");
		} else if (type.equals("bloodPressureCondition")) {
			query.put(type, "高血压患者");
		} else if (type.equals("bloodLipidCondition")) {
			query.put(type, "血脂异常患者");
		} else if (type.equals("BMI")) {
			query.put("BMI", new BasicDBObject("$exists", true).append("$gte", 28));
		}
		
		Long count = healthCheckDAO.countByQuery("customer", query);
		if (count == null) {
			count = (long) 0;
		}
		return count;
	}
	
	public Long jdAgeCount(String district, String type) {
		Map<String, Object> query = Maps.newHashMap();
		if (StringUtils.isNotEmpty(district)) {
			query.put("district", district);
		}
		
		if(type.equals("age")) {
			//query.put(type, new BasicDBObject("$gte", 35).append("$lte", 65));
			query.put(type, new BasicDBObject("$exists", true).append("$ne", "").append("$ne", null));
		} else if (type.equals("gender")) {
			query.put(type, new BasicDBObject("$exists", true).append("$ne", "").append("$ne", null));
		}
		
		Long count = healthCheckDAO.countByQuery("customer", query);
		if (count == null) {
			count = (long) 0;
		}
		return count;
	}
	
	
	public List<Map<String, Object>> getHealthHistoryDataByType(String type, String uniqueId) throws ParseException {
		Map<String, Object> queryMap = Maps.newHashMap();
		queryMap.put("uniqueId", uniqueId);
		
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("checkDate", 1);
		List<Map<String, Object>> list = healthCheckDAO.queryList("healthcheck", queryMap, sortMap);
		
		List<Map<String, Object>> datas = Lists.newArrayList();
		
		
			for (Map<String, Object> obj : list) {
				
				if (type.equals("bloodPressure")){
					Map<String, Object> map1 = Maps.newHashMap();
					Map<String, Object> map2 = Maps.newHashMap();
					if (obj.get("highPressure") == null || obj.get("highPressure") == "") {
						map1.put("value", 0);
					} else {
						map1.put("value", obj.get("highPressure"));
					}
					
					if (obj.get("lowPressure") == null || obj.get("lowPressure") == "") {
						map2.put("value", 0);
					} else {
						map2.put("value", obj.get("lowPressure"));
					}
					
					if(obj.get("checkDate") != null && obj.get("checkDate") != ""){
						Date date = new SimpleDateFormat("yyyy-MM-dd").parse(obj.get("checkDate").toString()); 
						String checkDate1 = new SimpleDateFormat("yyyy年MM月dd日").format(date);
						map1.put("checkDate", checkDate1);
						map1.put("state", "收缩压");
						map2.put("checkDate", checkDate1);
						map2.put("state", "舒张压");
					}
					datas.add(map1);
					datas.add(map2);
					
				} else if(type.equals("bloodSugger")){
					
					Map<String, Object> map1 = Maps.newHashMap();
					Map<String, Object> map2 = Maps.newHashMap();
					Map<String, Object> map3 = Maps.newHashMap();
					if (obj.get("bloodGlucose") == null || obj.get("bloodGlucose") == "") {
						map1.put("value", 0);
					} else {
						map1.put("value", obj.get("bloodGlucose"));
					}
					
					if (obj.get("bloodGlucose2h") == null || obj.get("bloodGlucose2h") == "") {
						map2.put("value", 0);
					} else {
						map2.put("value", obj.get("bloodGlucose2h"));
					}
					
					if (obj.get("bloodGlucoseRandom") == null || obj.get("bloodGlucoseRandom") == "") {
						map3.put("value", 0);
					} else {
						map3.put("value", obj.get("bloodGlucoseRandom"));
					}
					
					if(obj.get("checkDate") != null && obj.get("checkDate") != ""){
						Date date = new SimpleDateFormat("yyyy-MM-dd").parse(obj.get("checkDate").toString()); 
						String checkDate1 = new SimpleDateFormat("yyyy年MM月dd日").format(date);
						map1.put("checkDate", checkDate1);
						map1.put("state", "空腹血糖");
						map2.put("checkDate", checkDate1);
						map2.put("state", "餐后两小时血糖");
						map3.put("checkDate", checkDate1);
						map3.put("state", "随机血糖");
					}
					datas.add(map1);
					datas.add(map2);
					datas.add(map3);
				} else if(type.equals("bloodLipid")){
					
					Map<String, Object> map1 = Maps.newHashMap();
					Map<String, Object> map2 = Maps.newHashMap();
					Map<String, Object> map3 = Maps.newHashMap();
					Map<String, Object> map4 = Maps.newHashMap();
					if (obj.get("tc") == null || obj.get("tc") == "") {
						map1.put("value", 0);
					} else {
						map1.put("value", obj.get("tc"));
					}
					
					if (obj.get("tg") == null || obj.get("tg") == "") {
						map2.put("value", 0);
					} else {
						map2.put("value", obj.get("tg"));
					}
					
					if (obj.get("ldl") == null || obj.get("ldl") == "") {
						map3.put("value", 0);
					} else {
						map3.put("value", obj.get("ldl"));
					}
					
					if (obj.get("hdl") == null || obj.get("hdl") == "") {
						map4.put("value", 0);
					} else {
						map4.put("value", obj.get("hdl"));
					}
					
					if(obj.get("checkDate") != null && obj.get("checkDate") != ""){
						Date date = new SimpleDateFormat("yyyy-MM-dd").parse(obj.get("checkDate").toString()); 
						String checkDate1 = new SimpleDateFormat("yyyy年MM月dd日").format(date);
						map1.put("checkDate", checkDate1);
						map1.put("state", "总胆固醇");
						map2.put("checkDate", checkDate1);
						map2.put("state", "甘油三脂");
						map3.put("checkDate", checkDate1);
						map3.put("state", "低密度脂蛋白胆固醇");
						map4.put("checkDate", checkDate1);
						map4.put("state", "高密度脂蛋白胆固醇");
					}
					datas.add(map1);
					datas.add(map2);
					datas.add(map3);
					datas.add(map4);
				} else if(type.equals("tizhi")){
					Map<String, Object> map1 = Maps.newHashMap();
					Map<String, Object> map2 = Maps.newHashMap();
					
					String checkDate1 = "";
					if(obj.get("checkDate") != null && obj.get("checkDate") != ""){
						Date date = new SimpleDateFormat("yyyy-MM-dd").parse(obj.get("checkDate").toString()); 
						checkDate1 = new SimpleDateFormat("yyyy年MM月dd日").format(date);
					}
					
					/*if (obj.get("tizhi") != null && obj.get("tizhi") != ""){
						String tizhi = obj.get("tizhi").toString();
						if (StringUtils.isNotEmpty(tizhi)) {
							if (tizhi.contains("兼夹")) {
								String[] tizhis = tizhi.split("兼夹");
								map1.put("tizhi", TizhiParse.getTizhiNum(tizhis[0]+"质"));
								map1.put("checkDate", checkDate1);
								map2.put("tizhi", TizhiParse.getTizhiNum(tizhis[1].replaceAll("体质", "质")));
								map2.put("checkDate", checkDate1);
								datas.add(map1);
								datas.add(map2);
							} else {
								map1.put("tizhi", TizhiParse.getTizhiNum(tizhi));
								map1.put("checkDate", checkDate1);
								datas.add(map1);
							}
						} else {
							map1.put("tizhi", 0);
							map1.put("checkDate", checkDate1);
							datas.add(map1);
						}
						
					}*/
					
					//if (obj.get("tizhi") != null && !"".equals(obj.get("tizhi"))) {
						String tizhi = obj.get("tizhi").toString();
						if (StringUtils.isNotEmpty(tizhi)) {
							String tizhi1 = tizhi.substring(0, 2) + "质";
							map1.put("tizhi", TizhiParse.getTizhiNum(tizhi1));
							map1.put("checkDate", checkDate1);
							datas.add(map1);
							
							String tizhiEnd = tizhi.substring(2, tizhi.length());
							if (StringUtils.isNotEmpty(getTizhi(tizhiEnd))) {
								String tizhi2 = getTizhi(tizhiEnd) + "质";
								map2.put("tizhi", TizhiParse.getTizhiNum(tizhi2));
								map2.put("checkDate", checkDate1);
								datas.add(map2);
							}
						} else {
							map1.put("tizhi", 0);
							map1.put("checkDate", checkDate1);
							datas.add(map1);
						}
						
					//}
					
				} else {
					Map<String, Object> map = Maps.newHashMap();
					
					if(obj.get(type) == null || obj.get(type) == "") {
						map.put(type, 0);
					} else {
						if (type.equals("riskScore")) {
							map.put(type, Integer.parseInt(obj.get(type).toString()));
						}else {
							map.put(type, obj.get(type));
						}
						
					}
					
					if(obj.get("checkDate") != null && obj.get("checkDate") != ""){
						Date date = new SimpleDateFormat("yyyy-MM-dd").parse(obj.get("checkDate").toString()); 
						String checkDate1 = new SimpleDateFormat("yyyy年MM月dd日").format(date);
						map.put("checkDate", checkDate1);
					}
					datas.add(map);
				}
				
			}
			
			if (type.equals("tizhi")) {
				int[] tizhis = {90, 80, 70, 60, 50, 40, 30, 20, 10};
				for (int tizhi : tizhis) {
					boolean flag = false;
					for (Map<String, Object> tizhiMap : datas) {
						if (Integer.parseInt(tizhiMap.get("tizhi").toString()) == tizhi) {
							flag = true;
							break;
						}
					}
					
					if (!flag) {
						Map<String, Object> tizhiMap1 = Maps.newHashMap();
						tizhiMap1.put("tizhi", tizhi);
						//tizhiMap1.put("checkDate", "");
						datas.add(tizhiMap1);
					}
				}
			}
		
		
		return datas;
	}
	
	public static String getTizhi(String str) {
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿","湿热", "血瘀", "气郁", "特禀"};
		for (String tizhi : tizhis) {
			if (str.contains(tizhi)) {
				return tizhi;
			}
		}
		return "";
	}
	
	public Map<String, Object> getLatestVisitRecord(Map<String, Object> params) {
		return healthCheckDAO.getLatestVisitRecord(params);
	}
	
	public List<String> getTizhiResult(HttpServletRequest request) {
		List<String> tizhis = Lists.newArrayList();
		
		BufferedInputStream in = null;
		XSSFWorkbook wb = null;
		try {
			in = new BufferedInputStream(new FileInputStream(new File(getRootPath(request) + "tizhi.xlsx")));

		    wb = new XSSFWorkbook(in);
		    XSSFSheet st = wb.getSheetAt(0);
		    int rows = 0;
		    for (int rowIndex = 1; rowIndex <= st.getLastRowNum(); rowIndex++) {
		    	XSSFRow row = st.getRow(rowIndex);
		    	String tizhi = getCellValue(row.getCell(1));
		    	tizhis.add(tizhi);
		    }
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (wb != null) wb.close();
				if (in != null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tizhis;
	}
	
	private String getRootPath(HttpServletRequest request) {
		return request.getSession().getServletContext().getRealPath("/")+"/static/";
	}
}
