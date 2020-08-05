package com.capitalbio.healthcheck.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.capitalbio.healthcheck.service.OtherInfoService;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/otherInfo")
public class OtherInfoController {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired private HealthCheckService healthCheckService;
	@Autowired private AuthService authService;
	@Autowired private OtherInfoService otherInfoService;
	
	@RequestMapping("/list")
	public String index() {
		return "healthCheck/otherInfo_list";
	}
	
	@RequestMapping(value="otherlist")
	@ResponseBody
	public Message otherlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
		List<Map<String, Object>> list = otherInfoService.findPageBySort(pageData, queryMap, sortMap);
		
		long count = otherInfoService.count(queryMap);
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}
	
	@RequestMapping(value="/addOtherInfo",method = RequestMethod.GET)
	@ControllerLog
	public String addOtherInfo(HttpServletRequest request,Page pageData) throws Exception{
		return "healthCheck/otherInfo_edit";
	}
	
	@RequestMapping(value="/editOtherInfo",method = RequestMethod.GET)
	@ControllerLog
	public String editOtherInfo(HttpServletRequest request,Page pageData) throws Exception{
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		String id = request.getParameter("id");
		if(StringUtils.isNotEmpty(id)){
			Map<String,Object> editEntity = otherInfoService.getData(id);
			if (editEntity == null) {
				return "healthCheck/otherInfo_edit";
			}
			System.out.println("editEntity:" + editEntity);
			String uniqueId = (String)editEntity.get("uniqueId");
			System.out.println("/editOtherInfo--uniqueId=="+uniqueId);
			
			Map<String, Object> query = Maps.newHashMap();
			query.put("uniqueId", uniqueId);
			Map<String, Object> customer = otherInfoService.getDataByQuery("customer", query);
			editEntity.put("haveDisease", customer.get("haveDisease"));
			editEntity.put("diseaseShow", customer.get("diseaseShow"));
			editEntity.put("disease", customer.get("disease"));
			editEntity.put("familyHistory", customer.get("familyHistory"));
			editEntity.put("familyDisease", customer.get("familyDisease"));
			editEntity.put("familyHistoryShow", customer.get("familyHistoryShow"));
			
			Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
			if (secretMap != null) {
				editEntity.putAll(secretMap);
			}
			editEntity.put("id", id);
			request.setAttribute("editEntity", editEntity);
		}
		
		return "healthCheck/otherInfo_edit";
	}
	
	
	@RequestMapping(value="/saveOtherInfo",method = RequestMethod.POST)
	@ResponseBody
	public Message saveOtherInfo(ModelMap model, HttpServletRequest request) throws Exception{
		Map<String, Object> params = Maps.newHashMap();
		params.put("disease", request.getParameter("disease"));
		params.put("drugAllergy", request.getParameter("drugAllergyIds"));
		params.put("pastDisease", request.getParameter("pastDiseaseIds"));
		params.put("heredity", request.getParameter("heredity"));
		params.put("diseaseName", request.getParameter("diseaseName"));
		params.put("heredityFather", request.getParameter("heredityFatherIds"));
		params.put("heredityMother", request.getParameter("heredityMotherIds"));
		params.put("heredityBs", request.getParameter("heredityBsIds"));
		params.put("heredityChildren", request.getParameter("heredityChildrenIds"));
		params.put("frequencyExercise", request.getParameter("frequencyExercise"));
		params.put("exerciseTime", ParamUtils.getIntValue(request.getParameter("exerciseTime")));
		params.put("stickExerciseTime", ParamUtils.getIntValue(request.getParameter("stickExerciseTime")));
		params.put("exerciseMode", request.getParameter("exerciseMode"));
		params.put("eatingHabits", request.getParameter("eatingHabitsIds"));
		params.put("smokingStatus", request.getParameter("smokingStatus"));
		params.put("dailySmoking", ParamUtils.getIntValue(request.getParameter("dailySmoking")));
		params.put("smokingAge", ParamUtils.getDoubleValue(request.getParameter("smokingAge")));
		params.put("smokingCessation", ParamUtils.getDoubleValue(request.getParameter("smokingCessation")));
		params.put("drinkingFrequency", request.getParameter("drinkingFrequency"));
		params.put("drinkVolume",  ParamUtils.getDoubleValue(request.getParameter("drinkVolume")));
		params.put("whetherAlcohol", request.getParameter("whetherAlcohol"));
		params.put("alcoholAge", request.getParameter("alcoholAge"));
		params.put("drinkingAge", ParamUtils.getIntValue(request.getParameter("drinkingAge")));
		params.put("drunkPastyear", request.getParameter("drunkPastyear"));
		params.put("drinkingType", request.getParameter("drinkingTypeIds"));
		params.put("familyHistory", request.getParameter("familyHistory"));
		params.put("diseaseSingleIds", request.getParameter("diseaseSingleIds"));
		
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		try {
			String uniqueId = request.getParameter("uniqueId");
			if (StringUtils.isEmpty(uniqueId)) {
				String customerId = request.getParameter("customerId");
				Map<String, Object> user = authService.requestInfo(customerId, token, userId);
				if (user == null) {
					return Message.error("此人尚未建档，请建档后再进行筛查");
				}
				uniqueId = (String)user.get("uniqueId");
				if (StringUtils.isEmpty(uniqueId)) {
					String msg = (String)user.get("msg");
					return Message.error(Message.MSG_DATA_NOTFOUND, msg);
				}
			}
			if (StringUtils.isEmpty(uniqueId)) {
				return Message.error("此人尚未建档，请建档后再进行筛查");
			}
//			String customerId = request.getParameter("customerId");
//			String uniqueId = request.getParameter("uniqueId");
//			//若uniqueId为空，身份号不为空，根据身份号获取uniqueid
//			if (StringUtils.isEmpty(uniqueId) && !StringUtils.isEmpty(customerId)) {
//				Map<String, Object> obj = Maps.newHashMap();
//				obj.put("customerId", customerId);
//				uniqueId = authService.requestUniqueId(params);
//			}
			
			params.put("uniqueId", uniqueId);
			String id = request.getParameter("dataId");
			if (!StringUtils.isEmpty(id)) {
				params.put("id", id);
			}
			
			String otherInfoId = otherInfoService.saveData(params);
			
			params.remove("id");
			params.remove("_id");
			params.put("otherInfoId", otherInfoId);
			healthCheckService.saveCustomer(params, "");
			
			return Message.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("save menu error", e);
			return Message.error("save menu erro");
		}
	}
	
	@RequestMapping(value = "/updateOtherInfo/{id}", method = RequestMethod.GET)
	@ControllerLog
	public String updateOtherInfo(@PathVariable String id, HttpServletRequest request, Page pageData) throws Exception{
		Map<String, Object> otherInfo = healthCheckService.getData(id);
		request.setAttribute("otherInfo", otherInfo);
		return "healthCheck/otherInfo_edit";
	}
	
	@RequestMapping(value = "/delOtherInfo/{id}", method = RequestMethod.POST)
	@ResponseBody
	@ControllerLog
	public Message delOtherInfo(@PathVariable String id,HttpServletRequest request){
		healthCheckService.deleteData(id);
		return Message.success();
	}
}
