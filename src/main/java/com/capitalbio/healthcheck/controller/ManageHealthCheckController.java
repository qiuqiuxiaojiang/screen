package com.capitalbio.healthcheck.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.auth.util.Constant;
import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.FileUtil;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.common.util.MD5Util;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.common.util.ZipUtils;
import com.capitalbio.eye.service.EyeRecordService;
import com.capitalbio.healthcheck.controller.ScreenTralController.HealthCheck;
import com.capitalbio.healthcheck.service.CommunitysService;
import com.capitalbio.healthcheck.service.GeneratePdfService;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.capitalbio.healthcheck.service.HealthControlService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/manage/hc")
public class ManageHealthCheckController {
	Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private HealthCheckService healthCheckService;
//	@Autowired ReportService reportService;
	@Autowired
	private GeneratePdfService generatePdfService;
	@Autowired AuthService authService;
	@Autowired EyeRecordService eyeRecordService;
	@Autowired FileManageService fileManageService;
	@Autowired CommunitysService communitysService;
	@Autowired HealthControlService healthControlService;
	
	private String roleKey = "ROLE_ADMIN";
	private String roleKey1 = "ROLE_ADMIN1";
	
	@RequestMapping(value = "list")
	public String list() {
		return "healthCheck/customer_list";
	}
	
	
	@RequestMapping(value = "customerList")
	@ResponseBody
	@ControllerLog
	public Message customerList(HttpServletRequest request) throws Exception {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		Map<String,Object> queryMap = Maps.newHashMap();
		String customerId = request.getParameter("customerId");
		if (StringUtils.isNotEmpty(customerId)) {
			Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId);
			if (secretMap == null) {
				return Message.success();
			}
			String uniqueId = (String)secretMap.get("uniqueId");
			if (StringUtils.isEmpty(uniqueId)) {
				return Message.success();
			}
			
			queryMap.put("uniqueId", uniqueId);
		}
		
		String customerName = request.getParameter("customerName");
		if (StringUtils.isNotEmpty(customerName)) {
			List<String> idList = authService.queryInfoByName(customerName);
			if (idList != null && idList.size() > 0) {
				Map<String,Object> inMap = Maps.newHashMap();
				inMap.put("$in", idList);
				queryMap.put("uniqueId", inMap);
			} else {
				return Message.success();
			}
		}
		
		Page pageData = new Page();
		Integer page = ParamUtils.getIntValue(request.getParameter("current"));
		Integer pageCount = ParamUtils.getIntValue(request.getParameter("showRows"));
		if (page != null) {
			pageData.setPageNum(page.intValue());
		}
		if (pageCount != null) {
			pageData.setNumPerPage(pageCount);
		}
		String sortFileld = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("ctime", -1);
		if (StringUtils.isNotEmpty(sortFileld) && StringUtils.isNotEmpty(sortOrder)) {
			sortMap.put(sortFileld, Integer.parseInt(sortOrder));
		}
		Map<String, Object> returnMap = Maps.newHashMap();
		List<Map<String, Object>> list = healthCheckService.findPageBySort(pageData, queryMap, sortMap, "customer");
		for (Map<String, Object> obj : list) {
			Map<String, Object> query = Maps.newHashMap();
			query.put("uniqueId", obj.get("uniqueId"));
			Map<String, Object> hcDetail = healthCheckService.getDataByQuery("healthcheckDetail", query);
			if (hcDetail != null) {
				obj.put("healthcheckDetailId", hcDetail.get("id"));
			}
			
			List<Map<String, Object>> genes = healthCheckService.queryList("gene", query, null);
			if (genes != null && genes.size() > 0) {
				obj.put("gene", "true");
			}
		}
		
		long count = healthCheckService.count("customer", queryMap);
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}
	
	
	@RequestMapping(value="/addDocument")
	@ControllerLog
	public String addDocument(HttpServletRequest request,Page pageData) throws Exception{
		Map<String,Object> editEntity = Maps.newHashMap();
		String checkDate = DateUtil.dateToString(new Date());
		editEntity.put("recordDate", checkDate);
		String district = (String) request.getSession().getAttribute("district");
		String checkPlace = (String) request.getSession().getAttribute("checkPlace");
		request.setAttribute("district", district);
		request.setAttribute("checkPlace", checkPlace);
		
		List<Map<String, Object>> districts = healthCheckService.findAll("checkplace");
		request.setAttribute("districts", districts);
		request.setAttribute("editEntity", editEntity);
		
		boolean editflag = roleBoolean(request);
		request.setAttribute("editflag", editflag);
		
		String item = PropertyUtils.getProperty("item");
		if(item.equals("kunming")){
			Long itemId = (long) 2;
			
			List<String> streets = communitysService.getStreets(itemId);
			request.setAttribute("streets", streets);
		}
		request.setAttribute("item", item);
		
		return "healthCheck/doc_edit";
	}
	
	@RequestMapping(value="/showDocument")
	@ControllerLog
	public String showDocument(HttpServletRequest request,Page pageData) throws Exception{
		String uniqueId = request.getParameter("uniqueId");
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		if (StringUtils.isEmpty(uniqueId)) {
			return "healthCheck/doc_show";
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uniqueId", uniqueId);
		Map<String, Object> showEntity = healthCheckService.getDataByQuery("customer", params);
		Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
		if (secretMap == null) {
			return "healthCheck/doc_show";
		}
		showEntity.putAll(secretMap);
		request.setAttribute("showEntity", showEntity);
		
		String item = PropertyUtils.getProperty("item");
		request.setAttribute("item", item);
		
		return "healthCheck/doc_show";
	}
	
	@RequestMapping(value = "queryInfo")
	@ResponseBody
	@ControllerLog
	public Message queryInfo(HttpServletRequest request) throws Exception {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		String customerId = request.getParameter("customerId");
		Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId);
		logger.debug("secretMap:" + secretMap);
		
		if (secretMap == null) {
			return Message.error(Message.MSG_DATA_NOTFOUND, "此用户未建档");
		}
		
		String uniqueId = (String)secretMap.get("uniqueId");
		if (StringUtils.isEmpty(uniqueId)) {
			String msg = (String)secretMap.get("msg");
			return Message.error(Message.MSG_DATA_NOTFOUND, msg);
		}
		
		logger.debug("uniqueId-queryInfo:" + uniqueId);
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("uniqueId", uniqueId);
		Map<String,Object> customerMap = healthCheckService.getDataByQuery("customer", queryMap);
		if (customerMap == null) {
			customerMap = Maps.newHashMap();
			String checkDate = DateUtil.dateToString(new Date());
			customerMap.put("recordDate", checkDate);
			customerMap.put("recordTag", "false");
		} else {
			customerMap.put("recordTag", "true");
			
			if (customerMap.get("street") != null && !"".equals(customerMap.get("street"))) {
				String street = customerMap.get("street").toString();
				List<Map<String, Object>> communitys = communitysService.getCommunitysByStreet(street);
				customerMap.put("communitys", communitys);
			}
		}
		customerMap.putAll(secretMap);
		
		String idCardInputTag = (String)customerMap.get("idCardInputTag");
		if (StringUtils.isEmpty(idCardInputTag)) {
			customerMap.put("idCardInputTag", "auto");
		}
		
		boolean flag = eyeRecordService.isEyeRecordTest(uniqueId, customerId);
		if (flag) {
			customerMap.put("eyeCheck", "已检测");
		} else {
			customerMap.put("eyeCheck", "未检测");
		}
		logger.debug("customerMap:" + customerMap);
		return Message.dataMap(customerMap);
	}
	
	
	@RequestMapping(value = "queryInfoHealthCheck")
	@ResponseBody
	@ControllerLog
	public Message queryInfoHealthCheck(HttpServletRequest request) throws Exception {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		String customerId = request.getParameter("customerId");
		String checkDate = request.getParameter("checkDate");
		String uniqueId1 = request.getParameter("uniqueId");
		Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId);
		logger.debug("secretMap:" + secretMap);
		
		if (secretMap == null) {
			return Message.error(Message.MSG_DATA_NOTFOUND, "此用户未建档");
		}
		
		String uniqueId = (String)secretMap.get("uniqueId");
		
		if (StringUtils.isEmpty(uniqueId)) {
			String msg = (String)secretMap.get("msg");
			return Message.error(Message.MSG_DATA_NOTFOUND, msg);
		}
		secretMap.put("customerId", secretMap.remove("id"));
		
		
		logger.debug("uniqueId-queryInfo:" + uniqueId);
		
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("uniqueId", uniqueId);
		queryMap.put("checkDate", checkDate);
		Map<String,Object> healthcheckMap = healthCheckService.getDataByQuery("healthcheck", queryMap);
		
		queryMap.remove("checkDate");
		Map<String,Object> customerMap = healthCheckService.getDataByQuery("customer", queryMap);
		if (customerMap == null) {
			return Message.error(Message.MSG_DATA_NOTFOUND, "此用户未建档");
		}
		
		if (healthcheckMap != null) {
			boolean flag = eyeRecordService.isEyeRecordTest(uniqueId, customerId);
			if (flag) {
				healthcheckMap.put("eyeCheck", "已检测");
			} else {
				healthcheckMap.put("eyeCheck", "未检测");
			}
			
			healthcheckMap.put("visit", customerMap.get("visit"));
			
//			healthcheckMap.put("primarykeyId", healthcheckMap.remove("id"));
			logger.debug("healthcheckMap:" + healthcheckMap);
		} else {
			healthcheckMap = Maps.newHashMap();
			
			
			//获取历史记录所有检测日期
			if (StringUtils.isEmpty(uniqueId1)) {
				Map<String, Object> sortMap = Maps.newHashMap();
				sortMap.put("checkDate", 1);
				List<Map<String, Object>> list = healthCheckService.queryList("healthcheck", queryMap, sortMap);
				
				List<Object> dateList = Lists.newArrayList();
				for (Map<String, Object> obj : list) {
					dateList.add(obj.get("checkDate"));
				}
				
				healthcheckMap.put("checkDate", DateUtil.dateToString(new Date()));
				healthcheckMap.put("checkDates", dateList);
			}
			
			String oldCheckDate = (String) customerMap.get("checkDate");
			if (StringUtils.isEmpty(checkDate)) {
				checkDate = DateUtil.dateToString(new Date());
			}
			
			healthcheckMap.put("visit", customerMap.get("visit"));
			
			//判断最新一条初筛记录是否是当天的
			if (checkDate.equals(oldCheckDate)) {
				healthcheckMap.putAll(customerMap);
			} else {
				healthcheckMap.put("haveDisease", customerMap.get("haveDisease"));
				healthcheckMap.put("disease", customerMap.get("disease"));
				healthcheckMap.put("familyHistory", customerMap.get("familyHistory"));
				healthcheckMap.put("familyDisease", customerMap.get("familyDisease"));
				healthcheckMap.put("recordDate", customerMap.get("recordDate"));
			}
			
			healthcheckMap.put("checkDate", checkDate);
		}
		healthcheckMap.putAll(secretMap);
		
		int age = healthCheckService.getAgeByIdCard(customerId, checkDate);
		healthcheckMap.put("age", age);
		
		List<String> tizhiResults = healthCheckService.getTizhiResult(request);
		healthcheckMap.put("tizhiResults", tizhiResults);
		
		return Message.dataMap(healthcheckMap);
	}
	
	@RequestMapping(value = "queryInfoGene")
	@ResponseBody
	@ControllerLog
	public Message queryInfoGene(HttpServletRequest request) throws Exception {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		String customerId = request.getParameter("customerId");
		Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId);
		logger.debug("secretMap:" + secretMap);
		
		if (secretMap == null) {
			return Message.error(Message.MSG_DATA_NOTFOUND, "此用户未建档");
		}
		
		String uniqueId = (String)secretMap.get("uniqueId");
		if (StringUtils.isEmpty(uniqueId)) {
			String msg = (String)secretMap.get("msg");
			return Message.error(Message.MSG_DATA_NOTFOUND, msg);
		}
		
		logger.debug("uniqueId-queryInfo:" + uniqueId);
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("uniqueId", uniqueId);
		Map<String, Object> geneMap = healthCheckService.getDataByQuery("gene", queryMap);
		if(geneMap != null) {
			geneMap.put("geneId", geneMap.remove("id"));
		} else {
			geneMap = Maps.newHashMap();
			Map<String,Object> customerMap = healthCheckService.getDataByQuery("customer", queryMap);
			geneMap.putAll(customerMap);
		}
		geneMap.putAll(secretMap);
		return Message.dataMap(geneMap);
	}
	
	@RequestMapping(value="/saveDoc")
	@ResponseBody
	@ControllerLog
	public Message saveDoc(HttpServletRequest request) throws Exception{
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		Map<String, Object> params = parseRequest(request);
		Map<String,Object> secretInfo = healthCheckService.parseDocParameter(params);
		String customerId = (String)secretInfo.get("id");
		String uniqueId = (String)params.get("uniqueId");
		Map<String,Object> authMap = null;
		if (StringUtils.isEmpty(uniqueId)) {
			authMap = authService.newDocInfo(secretInfo, token, userId);
		} else {
			secretInfo.put("userId", uniqueId);
			authMap = authService.updateDocInfo(secretInfo, token, userId);
		}
		if (authMap != null && authMap.size() > 0) {
			uniqueId = (String)authMap.get("uniqueId");
			if (StringUtils.isEmpty(uniqueId)) {
				String msg = (String)authMap.get("msg");
				return Message.error(Message.MSG_PARAM_NULL, msg); 
			}
		}
		
		if (uniqueId == null) {
			return Message.error("认证错误，不能保存");
		}
		params.put("uniqueId", uniqueId);
		
		// save history
		healthCheckService.saveData("customerHistory", params);
		params.remove("id");
		params.remove("_id");
		params.remove("ctime");
		
		String item = PropertyUtils.getProperty("item");
		if (item.equals("kunming")) {
			if (params.get("checkPlaceId") != null && !"".equals(params.get("checkPlaceId"))) {
				Long checkPlaceId = Long.parseLong(params.get("checkPlaceId").toString());
				Map<String, Object> query = Maps.newHashMap();
				query.put("checkPlaceId", checkPlaceId);
				Map<String, Object> community = communitysService.getDataByQuery(query);
				if (community != null) {
					params.put("community", community.get("community"));
				}
			}
		} else {
			String district = params.get("district").toString();
			int checkPlaceId = 0;
			if (district.equals("海州区")) {
				checkPlaceId = 104;
			} else if (district.equals("阜蒙县")) {
				checkPlaceId = 105;
			} else if (district.equals("彰武县")) {
				checkPlaceId = 106;
			}
			params.put("checkPlaceId", checkPlaceId);
		}
		
		//保存最新记录
		String id = healthCheckService.saveCustomer(params, "");
		
		//推送uniqueId到目诊记录
		eyeRecordService.updateUniqueId(uniqueId, customerId);
		
		//将uniqueId和district推送至医师端
		String district = (String)params.get("district");
		if (district == null) {
			district = "";
		}
		authService.pushUniqueIdAndDistrict(uniqueId, district);
		
		healthControlService.saveDoc(params);
		
		return Message.data(id);
	}
	
	
	/***************************************************************************************/
	/**
	 * zhangboxuan add 一体机列表
	 * @return
	 */
	@RequestMapping(value = "healthListUI")
	@ControllerLog
	public String healthListUI() {
		return "healthCheck/health_list";
	}
	/**
	 * zhangboxuan add 一体机列表json，healthList
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "healthList")
	@ResponseBody
	@ControllerLog
	public Message healthList(HttpServletRequest request) throws Exception {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		String customerId = request.getParameter("customerId");
		String uniqueId = null;
		if (StringUtils.isNotEmpty(customerId)) {
			Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId);
			if (secretMap == null) {
				return Message.success();
			}
			uniqueId = (String)secretMap.get("uniqueId");
			if (StringUtils.isEmpty(uniqueId)) {
				return Message.success();
			}

		}
		Map<String,Object> queryMap = Maps.newHashMap();
		String customerName = request.getParameter("customerName");
		if (StringUtils.isNotEmpty(customerName)) {
			List<String> idList = authService.queryInfoByName(customerName);
			if (idList == null || idList.size() == 0) {
				return Message.success();
			}
			Map<String,Object> inMap = Maps.newHashMap();
			inMap.put("$in", idList);
			queryMap.put("uniqueId", inMap);
		}
		Page pageData = new Page();
		Integer page = ParamUtils.getIntValue(request.getParameter("current"));
		Integer pageCount = ParamUtils.getIntValue(request.getParameter("showRows"));
		if (page != null) {
			pageData.setPageNum(page.intValue());
		}
		if (pageCount != null) {
			pageData.setNumPerPage(pageCount);
		}
		if (StringUtils.isNotEmpty(uniqueId)) {
			queryMap.put("uniqueId", uniqueId);
		}
		String sortFileld = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("_id", -1);
		if (StringUtils.isNotEmpty(sortFileld) && StringUtils.isNotEmpty(sortOrder)) {
			sortMap.put(sortFileld, Integer.parseInt(sortOrder));
		}
		Map<String, Object> returnMap = Maps.newHashMap();
		List<Map<String, Object>> list = healthCheckService.findPageBySort(pageData, queryMap, sortMap);
		
		long count = healthCheckService.count(queryMap);
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}

	@RequestMapping(value="/editHealthCheck",method = RequestMethod.GET)
	@ControllerLog
	public String editHealthCheck(HttpServletRequest request,Page pageData) throws Exception{
		
		//TODO
		String item = PropertyUtils.getProperty("item");
		
		String userId = (String) request.getSession().getAttribute("userId");
		String token = (String) request.getSession().getAttribute("token");
		String id = request.getParameter("id");
		String uniqueId = request.getParameter("uniqueId");
		String checkDate = request.getParameter("checkDate");
		
		Map<String,Object> editEntity = healthCheckService.getData(id);
		if (editEntity == null) {
			Map<String, Object> queryMap = Maps.newHashMap();
			queryMap.put("uniqueId", uniqueId);
			Map<String,Object> customerMap = healthCheckService.getDataByQuery("customer", queryMap);
			
			editEntity = Maps.newHashMap();
			editEntity.put("haveDisease", customerMap.get("haveDisease"));
			editEntity.put("disease", customerMap.get("disease"));
			editEntity.put("familyHistory", customerMap.get("familyHistory"));
			editEntity.put("familyDisease", customerMap.get("familyDisease"));
			editEntity.put("checkDate", checkDate);
			editEntity.put("uniqueId", uniqueId);
		} else {
			uniqueId = editEntity.get("uniqueId").toString();
			
			Map<String, Object> queryMap = Maps.newHashMap();
			queryMap.put("uniqueId", uniqueId);
			Map<String,Object> customerMap = healthCheckService.getDataByQuery("customer", queryMap);
			editEntity.put("visit", customerMap.get("visit"));
		}
		
		//System.out.println("editEntity:" + editEntity);
		
		System.out.println("/editHealthCheck--uniqueId=="+uniqueId);
		Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
		if (secretMap != null) {
			secretMap.remove("age");
			editEntity.putAll(secretMap);
			editEntity.put("id", id);
		}
		
		request.setAttribute("editEntity", editEntity);
		
		String district = (String) request.getSession().getAttribute("district");
		String checkPlace = (String) request.getSession().getAttribute("checkPlace");
		request.setAttribute("district", district);
		request.setAttribute("checkPlace", checkPlace);
		
		List<Map<String, Object>> districts = healthCheckService.findAll("checkplace");
		request.setAttribute("districts", districts);
		request.setAttribute("operation", "edit");
		request.setAttribute("item", item);
		
		List<String> tizhiResults = healthCheckService.getTizhiResult(request);
//		String.join("','", (CharSequence[]) tizhiResults.toArray());
//		String.Concat("'", String.join("','", (CharSequence[]) tizhiResults.toArray()), "'");
		request.setAttribute("tizhiResults", tizhiResults);
		
		//判断登录用户是否是管理员
		boolean editflag = roleBoolean(request);
		request.setAttribute("editflag", editflag);
		
		return "healthCheck/health_edit";
	}
	
	
	@RequestMapping(value="getTizhi")
	@ControllerLog
	@ResponseBody
	public Message getTizhi(HttpServletRequest request) throws Exception{
		List<String> tizhiResults = healthCheckService.getTizhiResult(request);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("tizhiResults", tizhiResults);
		return Message.dataMap(result);
	}


	/**
	 * 新增HeadlthCheck实体
	 * @param request
	 * @param pageData
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="addHealthCheckUI",method = RequestMethod.GET)
	@ControllerLog
	public String addHealthCheckUI(HttpServletRequest request,Page pageData) throws Exception{
		String item = PropertyUtils.getProperty("item");
		
		//判断登录用户是否是管理员
		boolean editflag = roleBoolean(request);
		request.setAttribute("editflag", editflag);
		
		Map<String,Object> editEntity = Maps.newHashMap();
		String checkDate = DateUtil.dateToString(new Date());
		editEntity.put("checkDate", checkDate);
		
		request.setAttribute("editEntity", editEntity);
		request.setAttribute("item", item);
		return "healthCheck/health_edit";
	}
	
	/**
	 * 初筛页面  点击下一步获取所有的初筛记录检测日期
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getHealthcheckDates")
	@ResponseBody
	@ControllerLog
	public Message getHealthcheckDates(HttpServletRequest request) {
		String uniqueId = request.getParameter("uniqueId");
		if (StringUtils.isEmpty(uniqueId)) {
			return Message.error("该筛查用户不存在");
		} 
		
		Map<String, Object> query = Maps.newHashMap();
		query.put("uniqueId", uniqueId);
		List<Map<String, Object>> list = healthCheckService.queryList(query);
		
		List<Object> dateList = Lists.newArrayList();
		for (Map<String, Object> obj : list) {
			dateList.add(obj.get("checkDate"));
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("checkDate", DateUtil.dateToString(new Date()));
		result.put("checkDates", dateList);
		return Message.dataMap(result);
		
	}
	
	/**
	 * 新增HealthCheck实体
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/generateHealthCheckPdf")
	@ResponseBody
	@ControllerLog
	public Message generateHealthCheckPdf(HttpServletRequest request) throws Exception{
		Map<String, Object> params = parseRequest(request);
		String uniqueId = (String)params.get("uniqueId");
		String checkDate = (String)params.get("checkDate");
		if (StringUtils.isEmpty(uniqueId)) {
			return Message.error("此人尚未建档，请建档后再进行筛查");
		}
		Map<String, Object> mapResult = new HashMap<String,Object>();
		Map<String, Object> generatePdf = generatePdfService.generatePdf(params,"projectScreening");
		/** 判断并上传pdf文件并删除临时文件-开始 **/
		Map<String, Object> mapUploadInfo = generatePdfService.uploadPdf(uniqueId,checkDate,"projectScreening");
		/** 获取文件服务器上的pdf文件链接 **/
		if (mapUploadInfo.get("fileId") == null || mapUploadInfo.get("fileName") ==null) {
			return Message.error("报告上传时出错");
		}
		/** 保存初筛数据 **/
		params.put("fileId", mapUploadInfo.get("fileId"));
		mapResult.put("fileName", mapUploadInfo.get("fileName"));
		saveHealthCheckInfo(params);
		
		mapResult.put("message", "success");
		return Message.dataMap(mapResult);
	}
	
	/**
	 * 新增HealthCheck实体
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/saveHealthCheck")
	@ResponseBody
	@ControllerLog
	public Message saveHealthCheck(HttpServletRequest request) throws Exception{
		Map<String, Object> params = parseRequest(request);
		return saveHealthCheckInfo(params);
	}
	
	/**
	 * 展示初筛信息详情
	 * @param request
	 * @param pageData
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="showHealthCheckUI",method = RequestMethod.GET)
	@ControllerLog
	public String showHealthCheckUI(HttpServletRequest request,Page pageData) throws Exception{
		String id = request.getParameter("id");
		String uniqueId = request.getParameter("uniqueId");
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		if (StringUtils.isEmpty(id) && StringUtils.isEmpty(uniqueId)) {
			return "healthCheck/health_show";
		}

		
		Map<String, Object> showEntity = null;
		if (!StringUtils.isEmpty(id)) {
			showEntity = healthCheckService.getData(id);
			if (showEntity == null) {
				return "healthCheck/health_show";
			}
		} else if (!StringUtils.isEmpty(uniqueId)) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("uniqueId", uniqueId);
			showEntity = healthCheckService.getLatestVisitRecord(params);
			
		}
		
		if (showEntity != null) {
			Map<String,Object> secretMap = authService.requestInfoByUniqueId((String)showEntity.get("uniqueId"), token, userId);
			showEntity.putAll(secretMap);
			request.setAttribute("showEntity", showEntity);
		}
		
		return "healthCheck/health_show";
	}
	
	/**
	 * 保存初筛数据
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private Message saveHealthCheckInfo(Map<String, Object> params) throws Exception{
		Map<String, Object> mapResult = new HashMap<String,Object>();
		params = healthCheckService.parseParameter(params);
		params.remove("geneReport");
		
		String uniqueId = (String)params.get("uniqueId");
		if (StringUtils.isEmpty(uniqueId)) {
			return Message.error("此人尚未建档，请建档后再进行筛查");
		}
		String checkDate = (String)params.get("checkDate");
		// save history
		healthCheckService.saveData("healthCheckHistory", params);
		params.remove("id");
		params.remove("_id");
		params.remove("ctime");
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("checkDate", checkDate);
		queryMap.put("uniqueId", uniqueId);
		if (params.get("fileId") != null ) {
			params.put("healthCheckPdf", params.get("fileId"));
		}
		//保存当天最新记录
		Map<String,Object> data = healthCheckService.getDataByQuery(queryMap);
		if (data == null) {
			data = Maps.newHashMap();
		}
		data.putAll(params);
		String id = healthCheckService.saveData(data);
		data.put("id", id);
		
		params.put("healthCheckId", id);
		
		//保存最新记录
		healthCheckService.saveCustomer(params, Constant.DATA_SOURCE_CS);
		
		mapResult.put("id", id);
		mapResult.put("message", "success");
		
		return Message.dataMap(mapResult);
	}
	
	/**
	 * 新增HeadlthCheckDetailed精筛信息实体
	 * @param request
	 * @param pageData
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="addHealthCheckDetailedUI",method = RequestMethod.GET)
	@ControllerLog
	public String addHealthCheckDetailedUI(HttpServletRequest request,Page pageData) throws Exception{
		String item = PropertyUtils.getProperty("item");
		request.setAttribute("item", item);
		return "healthCheck/healthDetail_edit";
	}
	
	@RequestMapping(value="/editHealthCheckDetail",method = RequestMethod.GET)
	@ControllerLog
	public String editHealthCheckDetail(HttpServletRequest request,Page pageData) throws Exception{
		String item = PropertyUtils.getProperty("item");
	
		String userId = (String) request.getSession().getAttribute("userId");
		String token = (String) request.getSession().getAttribute("token");
		String id = request.getParameter("id");
		if(StringUtils.isNotEmpty(id)){
			Map<String,Object> editEntity = healthCheckService.getData("healthcheckDetail", id);
			if (editEntity == null) {
				return "healthCheck/healthDetail_edit";
			}
			logger.debug("editEntity:" + editEntity);
			String uniqueId = (String)editEntity.get("uniqueId");
			logger.debug("/editHealthCheckDetail--uniqueId=="+uniqueId);
			Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
			if (secretMap != null) {
				secretMap.remove("age");
				editEntity.putAll(secretMap);
			}
			
			Map<String, Object> queryMap = Maps.newHashMap();
			queryMap.put("uniqueId", editEntity.get("uniqueId"));
			Map<String, Object> customerMap = healthCheckService.getDataByQuery("customer", queryMap);
			if (customerMap != null) {
				editEntity.put("visit", customerMap.get("visit"));
				editEntity.put("classifyResult", customerMap.get("classifyResult"));
				editEntity.put("geneTest", customerMap.get("geneTest"));
				editEntity.put("dmTag", customerMap.get("dmTag"));
			}
			request.setAttribute("editEntity", editEntity);
		}
		
		request.setAttribute("item", item);
		
		return "healthCheck/healthDetail_edit";
	}
	
	/**
	 * 展示精筛信息详情
	 * @param request
	 * @param pageData
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="showHealthCheckDetailUI",method = RequestMethod.GET)
	@ControllerLog
	public String showHealthCheckDetailUI(HttpServletRequest request,Page pageData) throws Exception{
		
		String id = request.getParameter("id");
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		if (StringUtils.isEmpty(id)) {
			return "healthCheck/healthDetail_show";
		}
		
		Map<String, Object> showEntity = healthCheckService.getData("healthcheckDetail", id);
		if(showEntity != null) {
			Map<String,Object> secretMap = authService.requestInfoByUniqueId((String)showEntity.get("uniqueId"), token, userId);
			showEntity.putAll(secretMap);
			request.setAttribute("showEntity", showEntity);
		}
		return "healthCheck/healthDetail_show";
	}
	
	/**
	 * 生成精筛报告并保存精筛数据
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/generateHealthCheckDetailPdf")
	@ResponseBody
	@ControllerLog
	public Message generateHealthCheckDetailPdf(HttpServletRequest request) throws Exception{
		Map<String, Object> params = parseRequest(request);
		String uniqueId = (String)params.get("uniqueId");
		Date date = new Date();
		String checkDate = DateUtil.dateToString(date);
		params.put("checkDateJs", checkDate);
//		params.put("checkTimeJs", date);
		if (StringUtils.isEmpty(uniqueId)) {
			return Message.error("此人尚未建档，请建档后再进行筛查");
		}
		Map<String, Object> mapResult = new HashMap<String,Object>();
		Map<String, Object> generatePdf = generatePdfService.generatePdf(params,"projectScreeningDetail");
		/** 判断并上传pdf文件并删除临时文件-开始 **/
		Map<String, Object> mapUploadInfo = generatePdfService.uploadPdf(uniqueId,checkDate,"projectScreeningDetail");
		/** 获取文件服务器上的pdf文件链接 **/
		if (mapUploadInfo.get("fileId") == null || mapUploadInfo.get("fileName") ==null) {
			return Message.error("报告上传时出错");
		}
		/** 保存初筛数据 **/
		params.put("fileId", mapUploadInfo.get("fileId"));
		mapResult.put("fileName", mapUploadInfo.get("fileName"));
		Message saveHealthCheckDetailInfo = saveHealthCheckDetailInfo(params);
		
		mapResult.put("message", "success");
		saveHealthCheckDetailInfo.setDataMap(mapResult);
		return saveHealthCheckDetailInfo;
	}
	
	
	/**
	 * 新增HealthCheckDetail实体
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/saveHealthCheckDetail")
	@ResponseBody
	@ControllerLog
	public Message saveHealthCheckDetail(HttpServletRequest request) throws Exception{
		System.out.println("======================进入精筛=====================");
		Map<String, Object> params = parseRequest(request);
		Date date = new Date();
		String checkDate = DateUtil.dateToString(date);
		params.put("checkDateJs", checkDate);
		//System.out.println("保存精筛数据：" + params);
		String district = (String) request.getSession().getAttribute("district");
		params.put("districtDetail", district);

		return saveHealthCheckDetailInfo(params);
	}
	
	/**
	 * 保存精筛数据
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private Message saveHealthCheckDetailInfo(Map<String, Object> params) throws Exception{
		params = healthCheckService.parseHealthCheckDetailParameter(params);
		String uniqueId = (String)params.get("uniqueId");
		//System.out.println("=========uniqueId====================" + uniqueId);
		if (StringUtils.isEmpty(uniqueId)) {
			return Message.error("此人尚未建档，请建档后再进行筛查");
		}
		String customerNo = params.remove("customerId").toString();
		params.remove("name");
		params.remove("mobile");
		params.remove("geneReport");
		
		int age = healthCheckService.getAgeByIdCard(customerNo);
		params.put("age", age);
		
		// save history
		String historyId = healthCheckService.saveData("healthcheckDetailHistory", params);
		params.remove("id");
		params.remove("_id");
		params.remove("ctime");
//		String checkDate = (String)params.get("checkDate");
		Map<String,Object> queryMap = Maps.newHashMap();
//		queryMap.put("checkDate", checkDate);
		queryMap.put("uniqueId", uniqueId);
		Map<String,Object> data = healthCheckService.getDataByQuery("healthcheckDetail", queryMap);
		if (data == null) {
			data = Maps.newHashMap();
		}
		data.putAll(params);
		String id = healthCheckService.saveData("healthcheckDetail", data);
		params.put("healthCheckDetailId", id);
		//保存最新记录
		String customerId = healthCheckService.saveCustomer(params, Constant.DATA_SOURCE_JS);
		
		String ids = historyId + "," + id + "," + customerId;
		return Message.data(ids);
	} 
	
	
	
	@RequestMapping(value="/saveHealthCheckImage")
	@ResponseBody
	@ControllerLog
	public Message saveHealthCheckImage(HttpServletRequest request, @RequestParam MultipartFile[] file) throws Exception{
		String ids = request.getParameter("ids");
		String imageOrder = request.getParameter("imageOrder");
		try {
			if (StringUtils.isNotEmpty(ids)) {
				if(file != null){
					File fi = null;
					File resultFile = null;
					//InputStream in = null;
					String realPath = request.getSession().getServletContext().getRealPath("/")+"/image/";
					logger.info("项目路径==="+request.getSession().getServletContext().getRealPath("/"));
					
					String imageUrls = "";
					if (StringUtils.isNotEmpty(imageOrder)) {
						imageUrls = imageOrder;
					}
					for(MultipartFile ff:file){
						String fileName =ff.getOriginalFilename();	//	文件原名称

						String item = PropertyUtils.getProperty("item");
						String trueFileName = "";
						if (item.equals("fuxin")) {
							trueFileName = String.valueOf(System.currentTimeMillis());
						} else {
							String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
							trueFileName = String.valueOf(System.currentTimeMillis()) + "." + suffix;
						}
						fi = new File(realPath);
						if(!fi.exists()){
							fi.mkdir();
						}
						String path  = realPath + trueFileName;
						resultFile = new File(path);
						ff.transferTo(resultFile);
						//将图片上传到oss上
						fileManageService.uploadFile(resultFile, trueFileName);
						String imageUrl = fileManageService.getFileUrl(trueFileName);
						logger.info("图片地址=="+imageUrl);
						
						imageUrls = imageUrls + trueFileName + ",";
						resultFile.delete();
					}
					
					String[] strs = ids.split(",");
					Map<String, Object> healthcheckDetailHistory = healthCheckService.getData("healthcheckDetailHistory", strs[0]);
					healthcheckDetailHistory.put("imageUrls", imageUrls);
					healthCheckService.saveData("healthcheckDetailHistory", healthcheckDetailHistory);
					
					
					Map<String, Object> healthcheckDetail = healthCheckService.getData("healthcheckDetail", strs[1]);
					healthcheckDetail.put("imageUrls", imageUrls);
					healthCheckService.saveData("healthcheckDetail", healthcheckDetail);
					
					
					Map<String, Object> customer = healthCheckService.getData("customer", strs[2]);
					customer.put("imageUrls", imageUrls);
					healthCheckService.saveData("customer", customer);
				}
			}
			
			return Message.success();
		} catch (Exception e) {
			System.out.println("============上传失败===========");
			return Message.error("error");
		}
			
	}
	
	/**
	 * 查询HealthCheckDetail列表UI
	 * @return
	 */
	@RequestMapping(value = "healthDetailListUI")
	@ControllerLog
	public String healthDetailListUI() {
		return "healthCheck/healthDetail_list";
	}
	/**
	 * 查询HealthCheckDetail列表
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "healthDetailList")
	@ResponseBody
	@ControllerLog
	public Message healthDetailList(HttpServletRequest request) throws Exception {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		String customerId = request.getParameter("customerId");
		String uniqueId = null;
		if (StringUtils.isNotEmpty(customerId)) {
			Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId);
			if (secretMap == null) {
				return Message.success();
			}
			uniqueId = (String)secretMap.get("uniqueId");
			if (StringUtils.isEmpty(uniqueId)) {
				return Message.success();
			}

		}
		Map<String,Object> queryMap = Maps.newHashMap();
		String customerName = request.getParameter("customerName");
		if (StringUtils.isNotEmpty(customerName)) {
			List<String> idList = authService.queryInfoByName(customerName);
			if (idList == null || idList.size() == 0) {
				return Message.success();
			}
			Map<String,Object> inMap = Maps.newHashMap();
			inMap.put("$in", idList);
			queryMap.put("uniqueId", inMap);
		}
		Page pageData = new Page();
		Integer page = ParamUtils.getIntValue(request.getParameter("current"));
		Integer pageCount = ParamUtils.getIntValue(request.getParameter("showRows"));
		if (page != null) {
			pageData.setPageNum(page.intValue());
		}
		if (pageCount != null) {
			pageData.setNumPerPage(pageCount);
		}
		if (StringUtils.isNotEmpty(uniqueId)) {
			queryMap.put("uniqueId", uniqueId);
		}
		String sortFileld = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("ctime", -1);
		if (StringUtils.isNotEmpty(sortFileld) && StringUtils.isNotEmpty(sortOrder)) {
			sortMap.put(sortFileld, Integer.parseInt(sortOrder));
		}
		Map<String, Object> returnMap = Maps.newHashMap();
		List<Map<String, Object>> list = healthCheckService.findPageBySort(pageData, queryMap, sortMap,"healthcheckDetail");
		long count = healthCheckService.count("healthcheckDetail",queryMap);
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}
	
	
	@RequestMapping(value = "dataList")
	public String dataList(HttpServletRequest request) {
		String dateToString = DateUtil.dateToString(new Date(System.currentTimeMillis() - 1000*60*60*24));
		String startTime = dateToString;
		String endTime = dateToString;
		request.setAttribute("startTime", startTime);
		request.setAttribute("endTime", endTime);
		
		String item = PropertyUtils.getProperty("item");
		request.setAttribute("item", item);
		return "healthCheck/data_list";
	}
	
	
	@RequestMapping(value = "dataListByPage")
	@ResponseBody
	@ControllerLog
	public Message dataListByPage(HttpServletRequest request) throws Exception {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		Map<String,Object> queryMap = Maps.newHashMap();
		String district = request.getParameter("district");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		if (StringUtils.isNotEmpty(district)) {
			queryMap.put("district", district);
		}
		
		if (StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {
			String dateToString = DateUtil.dateToString(new Date(System.currentTimeMillis() - 1000*60*60*24));
			startTime = dateToString;
			endTime = dateToString;
		}
		
		queryMap.put("startTime", startTime);
		queryMap.put("endTime", endTime);
		
		Page pageData = new Page();
		Integer page = ParamUtils.getIntValue(request.getParameter("current"));
		Integer pageCount = ParamUtils.getIntValue(request.getParameter("showRows"));
		if (page != null) {
			pageData.setPageNum(page.intValue());
		}
		if (pageCount != null) {
			pageData.setNumPerPage(pageCount);
		}
		String sortFileld = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("checkDate", -1);
		if (StringUtils.isNotEmpty(sortFileld) && StringUtils.isNotEmpty(sortOrder)) {
			sortMap.put(sortFileld, Integer.parseInt(sortOrder));
		}
		
		long count = healthCheckService.queryCustomerCount(queryMap, "healthcheck");
		
		Map<String, Object> returnMap = Maps.newHashMap();
		List<Map<String, Object>> list = healthCheckService.getCustomerDataPage(pageData, queryMap, sortMap, "healthcheck", count);
		for (Map<String, Object> obj : list) {
			Map<String,Object> secretMap = authService.requestInfoByUniqueId(obj.get("uniqueId").toString(), token, userId);
			if (secretMap != null) {
				obj.putAll(secretMap);
			}
		}
		
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}
	
	
	/**
	 * 下载healthcheck筛查数据
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="downloadHealthCheck")
	@ResponseBody
	public Message downloadHealthCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String district = request.getParameter("district");
		
		long start = System.currentTimeMillis();
		Map<String,Object> queryMap = Maps.newHashMap();
		if (StringUtils.isEmpty(district)) {
			String item = PropertyUtils.getProperty("item");
			if (item.equals("fuxin")) {
				district = "阜新市";
			} else if(item.equals("kunming")){
				district = "昆明市";
			}
		} else {
			queryMap.put("district", district);
		}
		
		if (StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {
			String dateToString = DateUtil.dateToString(new Date(System.currentTimeMillis() - 1000*60*60*24));
			queryMap.put("startTime", dateToString);
			queryMap.put("endTime", dateToString);
			startTime = dateToString;
			endTime = dateToString;
		} else {
			queryMap.put("startTime", startTime);
			queryMap.put("endTime", endTime);
		}
		
		String fileName = "";
		if (!startTime.equals(endTime)){
			fileName = startTime + "-" + endTime + district;
		} else {
			fileName = startTime + district;
		}
		
//		Page pageData = new Page();
//		Integer page = 1;
//		Integer pageCount = 100;
//		if (page != null) {
//			pageData.setPageNum(page.intValue());
//		}
//		if (pageCount != null) {
//			pageData.setNumPerPage(pageCount);
//		}
		String sortFileld = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("checkDate", -1);
		if (StringUtils.isNotEmpty(sortFileld) && StringUtils.isNotEmpty(sortOrder)) {
			sortMap.put(sortFileld, Integer.parseInt(sortOrder));
		}
		
		long count = healthCheckService.queryCustomerCount(queryMap, "healthcheck");
		
		long m = 0;
		if (count % 100 > 0) {
			m = count / 100 + 1;
		} else {
			m = count / 100;
		}
		
		List<Map<String, Object>> lists = Lists.newArrayList();
		for (int i = 1; i <= m; i ++) {
			Page pageData = new Page();
			Integer page = i;
			Integer pageCount = 100;
			if (page != null) {
				pageData.setPageNum(page.intValue());
			}
			if (pageCount != null) {
				pageData.setNumPerPage(pageCount);
			}
			
			List<Map<String, Object>> list = healthCheckService.getCustomerDataPage(pageData, queryMap, sortMap, "healthcheck", count);
			for (Map<String, Object> obj : list) {
				Thread.sleep(100);
				System.out.println("obj" + obj);
				if (obj.get("uniqueId") != null && !"".equals(obj.get("uniqueId"))) {
					String token = (String) ContextUtils.getSession().getAttribute("token");
					String userId = (String) ContextUtils.getSession().getAttribute("userId");
					Map<String,Object> secretMap = authService.requestInfoByUniqueId(obj.get("uniqueId").toString(), token, userId);
					if (secretMap != null) {
						obj.putAll(secretMap);
					}
				}
			}
			lists.addAll(list);
		}
		
		long end = System.currentTimeMillis();
		System.out.println("筛查端数据检索耗时：" + (end-start)/1000 + "秒");

        if(lists.size()>0) {
        	
        	String tempDir = PropertyUtils.getProperty("system.temp.dir");
			File dir = new File(tempDir, "fuxin");
		    if(!dir.exists()){
		    	dir.mkdirs();
		    }
		    
//		    String codedFileName = new String(fileName.getBytes("gbk"), "iso-8859-1");
//			String uuid =  UUID.randomUUID().toString();
			File path = new File(dir, fileName);
		    if(!path.exists()){
		    	path.mkdirs();
		    }
			
			File newFile = new File(path, fileName + ".xlsx");
			
        	writeExcel(lists, newFile.getAbsolutePath());
        	System.out.println("生成临时文件");
        	
        	String md5Str = MD5Util.getFileMD5String(newFile);
        	File md5File = new File(path, fileName + ".txt");
        	 try {
        		 
     			   PrintWriter pw=new PrintWriter(md5File);
     			   pw.write(md5Str);
     			   pw.flush();
     			   pw.close();
     		} catch (FileNotFoundException e) {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     		}
        	 String zipPath = dir.getAbsolutePath() + "/" + fileName + ".zip";
        	 String zipFileName = ZipUtils.zipDirectory(dir.getAbsolutePath(), path.getAbsolutePath());
        	 System.out.println("zipFileName:" + zipFileName);
        	 
        	File zipFile = new File(zipPath);
        	//上传OSS
			fileManageService.uploadFile(new File(zipPath), fileName + ".zip");
			
			//删除临时文件
			FileUtil.delFolder(path.getAbsolutePath());
			zipFile.delete();
			
			//下载压缩包
			String url = fileManageService.getFileUrl(fileName + ".zip");
			
			return Message.data(url);
        }
		return Message.data("error");
		
	}
	
	@SuppressWarnings("rawtypes")
	private Map<String,Object> parseRequest(HttpServletRequest request) {
		Map<String,Object> maps = Maps.newHashMap();
		Enumeration keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String value = (String)request.getParameter(key);
			maps.put(key, value);
		}
		return maps;
	}

	
	public void writeExcel(List<Map<String, Object>> listMap, String path) throws Exception{
		List<String> titles = getTitles();
		
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Sheet sheet = wb.createSheet();
		/**创建标题行*/
		Row row = sheet.createRow(0);
        // 存储标题在Excel文件中的序号
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
        }
        String[] names = {"uniqueId","customerId", "mobile", "name", "gender", "age", "birthday", "nationality", "householdRegistrationType", 
        		"contactName", "contactMobile", "address", "remarks", "disease", "familyHistory", "height", "weight", "BMI", "waistline", "highPressure", 
        		"lowPressure", "pulse", "temperature", "oxygen", "hipline", "WHR", "fatContent", "bloodGlucose", "bloodGlucose2h",
        		"bloodGlucoseRandom", "tc", "tg", "ldl", "hdl", "tizhi", "eyeCheck", "riskScore", "bloodSugarCondition",
        		"bloodLipidCondition", "bloodPressureCondition", "classifyResult","dmTag", "checkDate", "OGTTTest", "bloodLipidTest", 
        		"bloodPressureTest", "geneTest","district","checkPlace","checkGroup"};
        
        for (int i = 0; i < listMap.size(); i++) {
        	/**设置表格内容从第行开始*/
        	Row valueRow = sheet.createRow(i+1);
        	Map<String, Object> rowMap = listMap.get(i);
        	for (int j = 0; j < names.length; j++) {
        		String rowName = names[j];
        		Cell cell = valueRow.createCell(j);
        	   	if (rowMap.get(rowName) != null) {
        	   		cell.setCellValue(rowMap.get(rowName).toString());
        	   	}
			}
		}
        
        /*
         * 写入到文件中
         */
        File file = new File(path);
        OutputStream outputStream = new FileOutputStream(file);
        wb.write(outputStream);
//        outputStream.flush();
//        outputStream.close();
        wb.close();
        wb.dispose();
	}
	
	private List<String> getTitles() {
		List<String> titles = Lists.newArrayList();
		titles.add("编号");
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
		titles.add("体质指数（BMI）");
		titles.add("腰围cm");
		titles.add("收缩压mmHg");
		titles.add("舒张压mmHg");
		titles.add("脉率次/分钟");
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
		titles.add("血清低密度脂蛋白（mmol/L）");
		titles.add("血清高密度脂蛋白（mmol/L）");
		titles.add("中医体质辨识结果");
		titles.add("眼象信息");
		titles.add("糖尿病危险因素评估分数");
		titles.add("血糖情况");
		titles.add("血脂情况");
		titles.add("血压情况");
		titles.add("人群分类结果");
		titles.add("血糖异常人群细分");
		titles.add("检测时间");
		titles.add("OGTT检测");
		titles.add("血脂检测");
		titles.add("血压检测");
		titles.add("基因检测");
		titles.add("筛查地点");
		titles.add("筛查组");
		titles.add("小组");
		return titles;
	}
	
	@RequestMapping(value = "getHealthHistoryData")
	public String getHealthHistoryData(HttpServletRequest request) {
		String type = request.getParameter("type");
		String uniqueId = request.getParameter("uniqueId");
		String dataId = request.getParameter("dataId");
		String checkDate = request.getParameter("checkDate");
		
		request.setAttribute("type", type);
		request.setAttribute("uniqueId", uniqueId);
		request.setAttribute("dataId", dataId);
		request.setAttribute("checkDate", checkDate);
		return "healthCheck/healthHistoryData";
	}
	
	@RequestMapping(value = "getHealthHistoryDataByType")
	@ResponseBody
	public Message getHealthHistoryDataByType(HttpServletRequest request) throws ParseException {
		String type = request.getParameter("type");
		String uniqueId = request.getParameter("uniqueId");
		String dataId = request.getParameter("dataId");
		String checkDate = request.getParameter("checkDate");
		List<Map<String, Object>> list = healthCheckService.getHealthHistoryDataByType(type, uniqueId);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("type", type);
		result.put("uniqueId", uniqueId);
		result.put("dataId", dataId);
		result.put("checkDate", checkDate);
		result.put("list", list);
		
		return Message.dataMap(result);
	}
	
	/**
	 * 生成精筛报告并保存精筛数据
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/checkCustomerData")
	@ResponseBody
	@ControllerLog
	public Message checkCustomerData(HttpServletRequest request) throws Exception{
		Map<String, Object> params = parseRequest(request);
		String uniqueId = (String)params.get("uniqueId");
		if (StringUtils.isEmpty(uniqueId)) {
			return Message.error("参数错误");
		}
		
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("uniqueId", uniqueId);
		String param = request.getParameter("param");
		if ("gene".equals(param)) {
			
			List<Map<String,Object>> genes = healthCheckService.queryList(queryMap, "gene");
			if (genes == null || genes.size() <= 0) {
				return Message.error("您还未进行基因检测！");
			}
		} else if ("visit".equals(param)) {
			List<Map<String,Object>> hcs = healthCheckService.queryList(queryMap, "healthcheck");
			if (hcs == null || hcs.size() <= 0) {
				return Message.error("您还未进行随访！");
			}
		}
		
		return Message.success();
	}
	
	public boolean roleBoolean(HttpServletRequest request) {
		boolean flag = false;
		String role = "";
		Map<String, Object> user = (Map<String, Object>)request.getSession().getAttribute("user");
		if (user != null) {
			List<Map<String, Object>> roles = (List<Map<String, Object>>)user.get("roles");
			for (Map<String, Object> entity : roles) {
				role += entity.get("rolekey").toString() + ",";
			}
		}
		
		if (StringUtils.isNotEmpty(role) && (role.contains(roleKey) || role.contains(roleKey1))) {
			flag = true;
		}
		return flag;
	}
	
}
