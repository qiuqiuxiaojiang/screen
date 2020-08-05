package com.capitalbio.eye.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.log.SpecialLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.FilesManageService;
import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.eye.service.BasicInfoService;
import com.capitalbio.eye.service.EyeRecordService;
import com.google.common.collect.Maps;

@Controller
@RequestMapping(value="eyeRecord")
public class EyeRecordController {
	@Autowired EyeRecordService eyeRecordService;
	@Autowired BasicInfoService basicInfoService;
	@Autowired FilesManageService fileService;
	@Autowired AuthService authService;

	@RequestMapping(value="list")
	@ControllerLog
	public String list(HttpServletRequest request) throws Exception{
		return "eyerecord/list";
	}

	//获得普通用户文件列表
	@RequestMapping(value="infoList")
	@ResponseBody
	@ControllerLog
	public Message getBasicInfoList(HttpServletRequest request) throws Exception{
		String customerId = request.getParameter("customerId");
		String uniqueId = null;
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId1 = (String) ContextUtils.getSession().getAttribute("userId");
		if (StringUtils.isNotEmpty(customerId)) {
			Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId1);
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
		if (StringUtils.isNotEmpty(uniqueId)) {
			queryMap.put("uniqueId", uniqueId);
		}
		Page pageData=new Page();
		Integer page=ParamUtils.getIntValue(request.getParameter("current"));
		Integer pageCount=ParamUtils.getIntValue(request.getParameter("showRows"));
		if(page!=null){
			pageData.setPageNum(page);
		}
		if(pageCount!=null){
			pageData.setNumPerPage(pageCount);
		}
		String sortField=request.getParameter("sortField");
		String sortOrder=request.getParameter("sortOrder");
		Map<String,Object> sortMap=Maps.newHashMap();
		if(StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)){
			sortMap.put(sortField,Integer.parseInt(sortOrder));
		}else{
			sortMap.put("uploadTime",-1);
		}
//		Map<String,Object> queryMap=parseConditionRequest(request);
		long count=basicInfoService.count(queryMap);
		List<Map<String,Object>> list=basicInfoService.findPageBySort(pageData, queryMap, sortMap);
		Map<String,Object> returnMap=Maps.newHashMap();
		returnMap.put("list",list);
		returnMap.put("totalCount",count);
		return Message.dataMap(returnMap);
	}
	
//	private Map<String,Object> parseConditionRequest(HttpServletRequest request) throws Exception{
//		Map<String,Object> param=Maps.newHashMap();
//		String name=request.getParameter("name");
//		if(StringUtils.isNotEmpty(name)){
//			param.put("name",name);
//		}
//		String userId=request.getParameter("userId");
//		if(StringUtils.isNotEmpty(userId)){
//			param.put("userId", userId);
//		}
//		return param;
//	}

	@RequestMapping(value="listRecord/{userId}")
	@ControllerLog
	public String listRecord(@PathVariable String userId, ModelMap model) throws Exception {
		Map<String,Object> basicInfo = basicInfoService.getInfoByUserId(userId);
		List<Map<String,Object>> recordList = eyeRecordService.queryRecord(userId, null, null);
		model.put("basicInfo", basicInfo);
		model.put("recordList", recordList);
		model.put("returnPage", "eyeRecord");
		return "eyerecord/record_list";
	}
	
	/**
	 * 根据唯一编号查看最新目诊数据
	 * @param userId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="viewRecord/{uniqueId}")
	@ControllerLog
	public String viewEyeRecord(@PathVariable String uniqueId, ModelMap model) throws Exception {
		Map<String,Object> record = eyeRecordService.getLatestRecordByUniqueId(uniqueId);
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId1 = (String) ContextUtils.getSession().getAttribute("userId");
		Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId1);
		
		Map<String,Object> basicInfo = new HashMap<String, Object>();
		if (secretMap != null) {
			basicInfo.put("name", secretMap.get("name"));
		}
		List<Map<String,Object>> recordList = new ArrayList<>();
		if (record != null) {
			recordList.add(record);
			basicInfo.put("userId", record.get("userId"));
		}
		model.put("recordList", recordList);
		model.put("returnPage", "customer");
		return "eyerecord/record_list";
	}

	

	@RequestMapping(value="download/{id}")
	@SpecialLog
	public void downloadByPage(@PathVariable String id, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Map<String,Object> basicMap=basicInfoService.getData(id);
		String userId = (String)basicMap.get("userId");
		eyeRecordService.downFileByUser(userId, basicMap, request, response, null, null);
	}
	
	@RequestMapping(value="delete/{id}")
	@ResponseBody
	@ControllerLog
	public Message deleteInfo(@PathVariable String id){
		Map<String,Object> basicMap = basicInfoService.getData(id);
		String userId = (String)basicMap.get("userId");
		Map<String,Object> query = Maps.newHashMap();
		query.put("userId", userId);
		eyeRecordService.deleteDataByParams(query);
		query.clear();
		query.put("username", userId);
		fileService.deleteFileByQuery(query);
		basicInfoService.deleteData(id);
		return Message.success();
	}

	@RequestMapping(value="deleteMulti")
	@ResponseBody
	@ControllerLog
	public Message deleteMulti(HttpServletRequest request){
		String idstr = request.getParameter("id");
		if (StringUtils.isNotEmpty(idstr)) {
			String[] ids = idstr.split(",");
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				Map<String,Object> basicMap = basicInfoService.getData(id);
				String userId = (String)basicMap.get("userId");
				Map<String,Object> query = Maps.newHashMap();
				query.put("userId", userId);
				eyeRecordService.deleteDataByParams(query);
				query.clear();
				query.put("username", userId);
				fileService.deleteFileByQuery(query);
				basicInfoService.deleteData(id);
			}
		}
		return Message.success();
	}

	@RequestMapping(value="deleteRecord/{id}")
	@ResponseBody
	@ControllerLog
	public Message deleteRecord(@PathVariable String id){
		eyeRecordService.deleteData(id);
		return Message.success();
	}


}
