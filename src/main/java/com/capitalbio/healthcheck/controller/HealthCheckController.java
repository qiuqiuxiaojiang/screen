package com.capitalbio.healthcheck.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.IdcardValidator;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.eye.service.EyeRecordService;
import com.capitalbio.healthcheck.dao.HealthCheckDAO;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/healthcheck")
public class HealthCheckController {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired HealthCheckService healthCheckService;
	@Autowired AuthService authService;
	@Autowired FileManageService fileManageService;
	@Autowired HealthCheckDAO healthCheckDAO;
	@Autowired EyeRecordService eyeRecordService;
	/**
	 * 上传健康管理信息，加密身份证号等关键信息
	 * @param dataMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="uploadInfo")
	@ResponseBody
	@ControllerLog
	public Message uploadInfo(HttpServletRequest request, @RequestBody Map<String,Object> map) throws Exception {
		System.out.println("----healthcheck------uploadInfo:" + map);
		String token = request.getHeader("token");
		String userId = request.getHeader("userId");
		if (!authService.verifyToken(userId, token)) {
			System.out.println("----healthcheck------token error: token"+token+",userId:"+userId);
			return Message.error(Message.MSG_AUTH_TOKENERROR, "Token错误, 请登录后重试");
		}
		String customerId = (String)map.remove("customerId");
		if (StringUtils.isEmpty(customerId)) {
			logger.debug("----healthcheck------customer null");
			return Message.error(Message.MSG_PARAM_NULL, "身份证号为空");
		}
		
		Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId);
		if (secretMap == null) {
			logger.debug("----healthcheck------customer not registered");
			return Message.error(Message.MSG_DATA_NOTFOUND, "尚未建档，请到筛查端建档后操作");
		}
		
		String uniqueId = (String)secretMap.get("uniqueId");
		if (StringUtils.isEmpty(uniqueId)) {
			String msg = (String)secretMap.get("msg");
			logger.debug("----healthcheck------auth, customerId-"+customerId+",error:"+msg);
			return Message.error(Message.MSG_DATA_NOTFOUND, msg);
		}

		map.put("uniqueId", uniqueId);
		logger.debug("----healthcheck------user data:" + map);
		healthCheckService.parseParameter(map);
		
		Date checkTime = (Date)map.get("checkTime");
		
		if (checkTime == null) {
			return Message.error(Message.MSG_PARAM_NULL, "检查时间不能为空");
		}
		
		String checkDate = (String)map.get("checkDate");

		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("uniqueId", uniqueId);
		System.out.println("uniqueId:"+uniqueId);
		Map<String, Object> customerInfo = healthCheckService.getDataByQuery("customer", queryMap);
		try {
			if (customerInfo!=null) {
				/** parseParameter方法会对这四个key初始化为空 需要取customer表数据来覆盖初始化数据 **/
				System.out.println("customerInfo:"+customerInfo);
				String disease = (String)customerInfo.get("disease");
				String familyHistory = (String)customerInfo.get("familyHistory");
				map.put("disease", disease);
				map.put("haveDisease", (String)customerInfo.get("haveDisease"));
				map.put("familyDisease", (String)customerInfo.get("familyDisease"));
				map.put("familyHistory", familyHistory);
				map.put("gender", customerInfo.get("gender"));
				map.put("age", customerInfo.get("age"));
				map.put("checkGroup", customerInfo.get("checkGroup"));
				map.put("checkPlace", customerInfo.get("checkPlace"));
				map.put("district", customerInfo.get("district"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("uploadInfo 缺少关键数据");
			return Message.error("客户数据缺少关键数据");
		}

		// save history
		healthCheckService.saveData("healthCheckHistory", map);
		map.remove("id");
		map.remove("_id");
		map.remove("ctime");
		System.out.println("=============map=============" + map);
		queryMap.put("checkDate", checkDate);
		Map<String,Object> data = healthCheckService.getDataByQuery(queryMap);
		if (data == null) {
			data = Maps.newHashMap();
		}
		data.putAll(map);
		data.put("src","uploadInfo");
		System.out.println("=================data================" + data);
		String id = healthCheckService.saveData(data);
		System.out.println("=================id================" + id);
		logger.debug("----healthcheck------check ID:" + id);
		//保存最新记录
		data.put("healthCheckId", id);
		healthCheckService.saveCustomer(data, "");

		return Message.success();

	}
	

	/**
	 * 上传健康管理信息，加密身份证号等关键信息
	 * @param dataMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="uploadHealthCheck")
	@ResponseBody
	public Message uploadHealthCheck(HttpServletRequest request, @RequestBody Map<String,Object> map) throws Exception {
		System.out.println("----healthcheck------upload health check data:" + map);
		String token = request.getHeader("token");
		String userId = request.getHeader("userId");
		System.out.println("----healthcheck------upload health check token:" + token + ", userId:" + userId);
		if (!authService.verifyToken(userId, token)) {
			System.out.println("----healthcheck------token error: token"+token+",userId:"+userId);
//			logger.debug("----healthcheck------token error: token"+token+",userId:"+userId);
			return Message.error(Message.MSG_AUTH_TOKENERROR, "Token错误, 请登录后重试");
		}
		String customerId = (String)map.get("customerId");
		if (StringUtils.isEmpty(customerId)) {
			logger.debug("----healthcheck------customer null");
			return Message.error(Message.MSG_PARAM_NULL, "身份证号为空");
		}
		
		boolean valid = IdcardValidator.isValidatedAllIdcard(customerId);
		if (!valid) {
			logger.debug("----healthcheck-----customer valid error:"+customerId);
			return Message.error(Message.MSG_DATA_ERROR, "身份证号验证错误");
		}
		
		Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId);
		
		if (secretMap == null) {
			return Message.error(Message.MSG_PARAM_NULL, "认证系统访问错误");
		}
		String uniqueId = (String)secretMap.get("uniqueId");
		if (StringUtils.isEmpty(uniqueId)) {
			logger.debug("----healthcheck------customer not registered");
			secretMap = healthCheckService.parseDocParameter(map);
			Map<String,Object> authMap = authService.newDocInfo(secretMap, token, userId);
			if (authMap != null) {
				uniqueId = (String)authMap.get("uniqueId");
				if (StringUtils.isEmpty(uniqueId)) {
					String msg = (String)authMap.get("msg");
					return Message.error(Message.MSG_PARAM_NULL, msg); 
				}
			}
		}

		map.put("uniqueId", uniqueId);
		logger.debug("----healthcheck------user data:" + map);
		healthCheckService.parseParameter(map);
		
		Date checkTime = (Date)map.get("checkTime");
		
		if (checkTime == null) {
			return Message.error(Message.MSG_PARAM_NULL, "检查时间不能为空");
		}
		
		String checkDate = (String)map.get("checkDate");
		
		// save history
		healthCheckService.saveData("healthCheckHistory", map);
		map.remove("id");
		map.remove("_id");
		map.remove("ctime");
		
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("checkDate", checkDate);
		queryMap.put("uniqueId", uniqueId);
		Map<String,Object> data = healthCheckService.getDataByQuery(queryMap);
		if (data == null) {
			data = Maps.newHashMap();
		}
		
		data.putAll(map);
		data.put("src","uploadHealthCheck");
		
		String id = healthCheckService.saveData(data);
		logger.debug("----healthcheck------check ID:" + id);
		//保存最新记录
		data.put("healthCheckId", id);
		healthCheckService.saveCustomer(data, "");

		return Message.success();

	}
	
//	/**
//	 * 上传健康管理信息
//	 * @param dataMap
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping(value="uploadInfo")
//	@ResponseBody
//	@ControllerLog
//	public Message uploadInfo(@RequestBody Map<String,Object> map) throws Exception {
//		logger.debug("----healthcheck------uploadInfo");
//		logger.debug("----healthcheck------upload data:" + map);
//		String encCustomerId = (String)map.get("customerId");
//		if (StringUtils.isEmpty(encCustomerId)) {
//			return Message.error(Message.MSG_PARAM_NULL, "身份证号不能为空");
//		}
//		String customerId = EncryptUtil.decryptByKey(encCustomerId);
//		if (customerId == null) {
//			customerId = EncryptUtil.decrypt(encCustomerId);
//		}
//		if (StringUtils.isEmpty(customerId)) {
//			logger.debug("----healthcheck------error:身份证号解密错误," + encCustomerId);
//			return Message.error(Message.MSG_PARAM_NULL, "身份证号解密错误");
//		}
//		String sysTime = String.valueOf(map.get("sysTime"));
//		if (sysTime == null || sysTime.equals("null")) {
//			logger.debug("----healthcheck------error:系统时间不能为空");
//			return Message.error(Message.MSG_PARAM_NULL, "系统时间不能为空");
//		}
//		
//		String token = (String)map.remove("token");
//		String checkToken = MD5Util.MD5Encode(encCustomerId+sysTime+Constant.SECRET_KEY);
//		if (!checkToken.equals(token)) {
//			logger.debug("----healthcheck------error:Token错误, request token:"+token+",checkToken"+checkToken);
//			return Message.error(Message.MSG_AUTH_TOKENERROR,"Token错误");
//		}
//
//		logger.debug("----healthcheck------customerId:" + customerId);
//		map.put("customerId", customerId);
//		
//		healthCheckService.parseParameter(map);
//		
//		Date checkTime = (Date)map.get("checkTime");
//		
//		if (checkTime == null) {
//			return Message.error(Message.MSG_PARAM_NULL, "检查时间不能为空");
//		}
//		
//		Object updateTimeObj = map.remove("updateTime");
//		if (updateTimeObj != null) {
//			Date updateTime = null;
//			if (updateTimeObj instanceof Date) {
//				updateTime = (Date)updateTimeObj;
//			} else if (updateTimeObj instanceof String) {
//				updateTime = DateUtil.stringToDateTime((String)updateTimeObj);
//			} else if (updateTimeObj instanceof Long) {
//				updateTime = new Date(((Long)updateTimeObj).longValue());
//			}
//			if (updateTime != null) {
//				map.put("updateTime", updateTime);
//			}
//		}
//
//		String checkDate = (String)map.get("checkDate");
//		// save history
//		healthCheckService.saveData("healthCheckHistory", map);
//		map.remove("id");
//		map.remove("_id");
//		map.remove("ctime");
//		
//		Map<String,Object> queryMap = Maps.newHashMap();
//		queryMap.put("checkDate", checkDate);
//		queryMap.put("customerId", customerId);
//		
//		Map<String,Object> data = healthCheckService.getDataByQuery(queryMap);
//		if (data == null) {
//			data = Maps.newHashMap();
//		}
//		Date saveUpdateTime = (Date)data.get("updateTime");
//		
//		Date updateTime = DateUtil.stringToDateTime("updateTime");
//		if (updateTime != null && saveUpdateTime != null && saveUpdateTime.after(updateTime)) {
//			return Message.error(Message.MSG_DATA_ERROR, "数据没有更新");
//		}
//		data.putAll(map);
//		data.put("src","uploadInfo");
//		String id = healthCheckService.saveData(data);
//		logger.debug("----healthcheck------check ID:" + id);
//		data.put("id", id);
//		healthCheckService.pushData(data);
//		reportService.processReport(data);
//		return Message.success();
//
//	}
	
	@RequestMapping(value="uploadFile")
	@ResponseBody
	public Message uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		MultipartHttpServletRequest mhsq = (MultipartHttpServletRequest) request;
		MultipartFile file = mhsq.getFile("file");
		
		if (file == null) {
			return Message.error("文件没找到");
		}
		
		String tempDir = PropertyUtils.getProperty("system.temp.dir");
		File dir = new File(tempDir, "upload");
	    if(!dir.exists()){
	    	dir.mkdirs();
	    }
	    
	    String uuid =  UUID.randomUUID().toString();
	    File newFile = new File(dir, uuid + ".xlsx");
	    file.transferTo(newFile);
		
	    Map<String, Object> obj = Maps.newHashMap();
	    obj.put("state", "上传中");
	    obj.put("fileName", file.getOriginalFilename());
		obj.put("startTime", DateUtil.datetimeToString(new Date()));
		obj.put("dataType", "初筛数据");
		obj.put("path", newFile.getAbsolutePath());
		
		//上传OSS
		String fileId = uuid + ".xlsx";
		fileManageService.uploadFile(newFile, fileId);
//		String fileUrl = fileManageService.getFileUrl(fileId);
		obj.put("fileId", fileId);
//		obj.put("fileUrl", fileUrl);
		
		//保存上传文件记录
		String id = healthCheckService.saveData("fileUploadRecord", obj);
		
		System.out.println("上传文件成功=============fileId============"+fileId);
	    
		if (StringUtils.isNotEmpty(id)) {
			return Message.success();
		} else {
			return Message.error("上传失败");
		}
	    
	}
	
	@RequestMapping(value="downFile")
	@ResponseBody
	public Message downFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String fileId = request.getParameter("fileId");
		String fileUrl = request.getParameter("fileUrl");
		File file = new File(fileUrl);
		fileManageService.downFile(file, fileId);
		return Message.success();
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
//        map.put("sex", sex);  
//        map.put("age", age);  
//	}
//
//	
//	private String getCellValue(Cell cell) {
//		if (cell == null) {
//			return "";
//		}
//		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
//			return String.valueOf(cell.getNumericCellValue());
//		}
//		return cell.getStringCellValue();
//	}
	
	/*@RequestMapping(value="downData")
	@ResponseBody
	public void downData(HttpServletRequest request) throws Exception {
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		healthCheckService.downData(startTime, endTime);
		System.out.println("==========导出数据结束========");
		
	}*/
	
	/*@RequestMapping(value="getCustomerData")
	@ResponseBody
	public List<Map<String, Object>> getCustomerData(HttpServletRequest request) throws Exception {
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		List<Map<String, Object>> datas = healthCheckDAO.getCustomerData(startTime, endTime);
		return datas;
		
	}*/
	
	@RequestMapping(value="getCustomerData")
	@ResponseBody
	public List<Map<String, Object>> getCustomerData(HttpServletRequest request) throws Exception {
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String collName = request.getParameter("collName");
		List<Map<String, Object>> datas = healthCheckDAO.getCustomerData(collName, startTime, endTime);
		return datas;
		
	}
	
	@RequestMapping(value="exportXmlData")
	@ResponseBody
	public void exportXmlData(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		healthCheckService.exportXmlData(startTime, endTime, response);
	}
	
	
	@RequestMapping(value="getCustomer")
	@ResponseBody
	public Map<String, Object> getCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String uniqueId = request.getParameter("uniqueId");
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("uniqueId", uniqueId);
		Map<String, Object> customerInfo = healthCheckService.getDataByQuery("customer", queryMap);
		return customerInfo;
	}
	
	@RequestMapping(value="updateHcData")
	@ResponseBody
	public Message updateHcData(@RequestBody Map<String,Object> map, HttpServletResponse response) throws IOException {
		
		return healthCheckService.updateHcData(map);
	}
	
	
	@RequestMapping("/saveDataRandom")
	public void saveCustomerDataRandom(@RequestBody Map<String,Object> map) {
		healthCheckDAO.saveData("customer", map);
	}
	
	
	@RequestMapping("/testTask")
	public void testTask(@RequestBody Map<String,Object> map) {
		System.out.println("===================开始测试=========================");
		
		Map<String, Object> queryMap = Maps.newHashMap();
		queryMap.put("checkDate", map.get("checkDate"));
		queryMap.put("uniqueId", map.get("uniqueId"));
		
		List<Map<String, Object>> list = healthCheckService.queryList(queryMap, "healthcheck");
		
		int i = 0;
		for (Map<String, Object> data : list) {
			String disease = "";
			String tnb = "";
			Double bloodGlucose = null;
			Double bloodGlucose2h = null;
			Double bloodGlucoseRandom = null;
			//Integer riskScore = null;
			
			String gxy = "";
			Integer highPressure = null;
			Integer lowPressure = null;
			
			String gxz = "";
			Double tc = null;
			Double tg = null;
			Double hdl = null;
			
			Double BMI = null;
			
			if (data.get("disease") != null && data.get("disease") != "") {
				disease = data.get("disease").toString();
				if(disease.contains("糖尿病")) {
					tnb = "糖尿病";
				} 
				
				if (disease.contains("高血压")) {
					gxy = "高血压";
				}
				
				if (disease.contains("高血脂")) {
					gxz = "高血脂";
				}
			}
			
			if (data.get("bloodGlucose") != null && data.get("bloodGlucose") != "") {
				bloodGlucose = Double.parseDouble(data.get("bloodGlucose").toString());
			}
			
			if (data.get("bloodGlucose2h") != null && data.get("bloodGlucose2h") != "") {
				bloodGlucose2h = Double.parseDouble(data.get("bloodGlucose2h").toString());
			}
			
			if (data.get("bloodGlucoseRandom") != null && data.get("bloodGlucoseRandom") != "") {
				bloodGlucoseRandom = Double.parseDouble(data.get("bloodGlucoseRandom").toString());
			}
			
			/*if (data.get("riskScore") != null && data.get("riskScore") != "") {
				riskScore = Integer.parseInt(data.get("riskScore").toString());
			}*/
			
			if (data.get("highPressure") != null && data.get("highPressure") != "") {
				highPressure = Integer.parseInt(data.get("highPressure").toString());
			}
			
			if (data.get("lowPressure") != null && data.get("lowPressure") != "") {
				lowPressure = Integer.parseInt(data.get("lowPressure").toString());
			}
			
			if (data.get("tc") != null && data.get("tc") != "") {
				tc = Double.parseDouble(data.get("tc").toString());
			}
			
			if (data.get("tg") != null && data.get("tg") != "") {
				tg = Double.parseDouble(data.get("tg").toString());
			}
			
			if (data.get("hdl") != null && data.get("hdl") != "") {
				hdl = Double.parseDouble(data.get("hdl").toString());
			}
			
			if (data.get("BMI") != null && data.get("BMI") != "") {
				BMI = Double.parseDouble(data.get("BMI").toString());
			}
			
			//计算糖尿病风险评分
			Map<String, Object> scoreMap = Maps.newHashMap();
			Map<String,Object> query = Maps.newHashMap();
			query.put("uniqueId", data.get("uniqueId"));
			Map<String,Object> customerMap = healthCheckService.getDataByQuery("customer", query);
			
			scoreMap.put("age", customerMap.get("age"));
			scoreMap.put("gender", customerMap.get("gender"));
			scoreMap.put("familyHistory", customerMap.get("familyHistory"));
			scoreMap.put("waistline", data.get("waistline"));
			scoreMap.put("BMI", data.get("BMI"));
			scoreMap.put("highPressure", data.get("highPressure"));
			scoreMap.put("familyDisease", data.get("familyDisease"));
			Integer riskScore = healthCheckService.computeRiskScore(scoreMap);
			
			String fatCon = healthCheckService.getFatCon(BMI);
			
			Map<String, Object> bloodGlucoseCon = healthCheckService.bloodGlucoseCon(tnb, bloodGlucose, bloodGlucose2h, bloodGlucoseRandom, riskScore);
			
			Map<String, Object> bloodPressureCon = healthCheckService.bloodPressureCon(gxy, highPressure, lowPressure);
			
			Map<String, Object> bloodLipidCon = healthCheckService.bloodLipidCon(gxz, tc, tg, hdl);
			
			Map<String, Object> classifyResult = healthCheckService.classifyResult(bloodGlucoseCon, bloodPressureCon, bloodLipidCon, fatCon);
			
			if (data.get("uniqueId") != null) {
				boolean flag = eyeRecordService.isEyeRecordTest(data.get("uniqueId").toString(), null);
				if (flag) {
					data.put("eyeCheck", "已检测");
				} else {
					data.put("eyeCheck", "未检测");
				}
			}
			
			data.putAll(bloodGlucoseCon);
			data.putAll(bloodPressureCon);
			data.putAll(bloodLipidCon);
			data.putAll(classifyResult);
			
			data.put("collName", "healthcheck");
			data.put("riskScore", riskScore);
			Message message = healthCheckService.updateHcData(data);
			i ++;
			System.out.println("=========uniqueId==========" + data.get("uniqueId") + ",message:" + message.code);
		}
		System.out.println("====================i=====================" + i);
	}
}
