package com.capitalbio.question.controller;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.eye.service.EyeRecordService;
import com.capitalbio.question.service.QuestionService;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/question")
public class QuestionController {
	Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private QuestionService questionService;
	@Autowired AuthService authService;
	@Autowired EyeRecordService eyeRecordService;
	@Autowired FileManageService fileManageService;
	
	@RequestMapping(value = "list")
	public String list() {
		return "question/question_list";
	}
	@RequestMapping(value = "questionList")
	@ResponseBody
	@ControllerLog
	public Message questionList(HttpServletRequest request) throws Exception {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		Map<String,Object> queryMap = Maps.newHashMap();
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

			queryMap.put("uniqueId", uniqueId);
		}
		
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
		String sortFileld = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("checkDate", -1);
		if (StringUtils.isNotEmpty(sortFileld) && StringUtils.isNotEmpty(sortOrder)) {
			sortMap.put(sortFileld, Integer.parseInt(sortOrder));
		}
		Map<String, Object> returnMap = Maps.newHashMap();
		List<Map<String, Object>> list = questionService.findPageBySort(pageData, queryMap, sortMap, "question");
		
		long count = questionService.count("question", queryMap);
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}
	
	
	@RequestMapping(value="/editQuestionUI",method = RequestMethod.GET)
	@ControllerLog
	public String editQuestionUI(HttpServletRequest request,Page pageData) throws Exception{
		String userId = (String) request.getSession().getAttribute("userId");
		String token = (String) request.getSession().getAttribute("token");
		String id = request.getParameter("id");
		if(StringUtils.isNotEmpty(id)){
			Map<String,Object> editEntity = questionService.getData("question", id);
			if (editEntity == null) {
				return "question/question_edit";
			}
			logger.debug("editEntity:" + editEntity);
			String uniqueId = (String)editEntity.get("uniqueId");
			logger.debug("/editQuestionUI--uniqueId=="+uniqueId);
			Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
			if (secretMap != null) {
				secretMap.remove("id");
				editEntity.putAll(secretMap);
			}
			request.setAttribute("editEntity", editEntity);
		}
		
		request.setAttribute("checkDate", DateUtil.dateToString(new Date()));
		return "question/question_edit";
	}
	
	@RequestMapping(value="/editQuestionSave",method = RequestMethod.POST)
	@ResponseBody
	public Message editQuestionSave(ModelMap model, HttpServletRequest request) throws Exception{
		Map<String, Object> params = Maps.newHashMap();
		params.put("takingDrugsIds", request.getParameter("takingDrugsIds"));
		params.put("beIllIds", request.getParameter("beIllIds"));
		params.put("relativesBeIllIds", request.getParameter("relativesBeIllIds"));
		
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
			params.put("uniqueId", uniqueId);
			
			
			String id = request.getParameter("dataId");
			params.putAll(this.parseRequest(request));
			params.remove("customerId");
			params.remove("name");
			if (!StringUtils.isEmpty(id)) {
				params.put("id", id);
				questionService.saveData(params);
			} else {
				Map<String, Object> queryMap = new HashMap<String, Object>();
				queryMap.put("uniqueId", uniqueId);
				queryMap.put("checkDate", params.get("checkDate"));
			
				Map<String, Object> questionData = questionService.getDataByQuery(queryMap);
				if (questionData != null) {
					params.remove("id");
					String did = (String)questionData.get("id");
					questionData.clear();
					questionData.putAll(params);
					questionData.put("id", did);
					questionService.saveData(questionData);
				} else {
					questionService.saveData(params);
				}
			}
			
			//保存历史记录
			questionService.saveData("question_history", params);
			return Message.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("save menu error", e);
			return Message.error("save menu erro");
		}
	}
	
	@RequestMapping(value="/generateQuestionPdf",method = RequestMethod.POST)
	@ResponseBody
	@ControllerLog
	public Message generateQuestionPdf(HttpServletRequest request) throws Exception{
		Map<String, Object> params = Maps.newHashMap();
		try {
			String msg = this.saveData(request, params);
			if (!StringUtils.isEmpty(msg)) {
				return Message.error(msg);
			}
			
			//生成pdf报告
			questionService.generatePdf(params);
			
			//上传pdf文件
			String uniqueId = (String)params.get("uniqueId");
			String checkDate = (String)params.get("checkDate");
			Map<String, Object> mapUploadInfo = questionService.uploadPdf(uniqueId, checkDate);
			
			/** 获取文件服务器上的pdf文件链接 **/
			if (mapUploadInfo.get("fileId") == null || mapUploadInfo.get("fileName") ==null) {
				return Message.error("报告上传时出错");
			}
			
			Map<String, Object> mapResult = new HashMap<String,Object>();
			/** 保存初筛数据 **/
			params.put("fileId", mapUploadInfo.get("fileId"));
			mapResult.put("fileName", mapUploadInfo.get("fileName"));
			
			mapResult.put("message", "success");
			return Message.dataMap(mapResult);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("save menu error", e);
			return Message.error("save menu erro");
		}
	}
	
	private String saveData(HttpServletRequest request, Map<String, Object> params) {
		params.put("takingDrugsIds", request.getParameter("takingDrugsIds"));
		params.put("beIllIds", request.getParameter("beIllIds"));
		params.put("relativesBeIllIds", request.getParameter("relativesBeIllIds"));
		
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		try {
			String uniqueId = request.getParameter("uniqueId");
			Map<String, Object> user = Maps.newHashMap();
			if (StringUtils.isEmpty(uniqueId)) {
				String customerId = request.getParameter("customerId");
				user = authService.requestInfo(customerId, token, userId);
				if (user == null) {
					return "此人尚未建档，请建档后再进行筛查";
				}
				uniqueId = (String)user.get("uniqueId");
				if (StringUtils.isEmpty(uniqueId)) {
					String msg = (String)user.get("msg");
					return msg;
				}
			} else {
				user = authService.requestInfoByUniqueId(uniqueId, token, userId);
			}
			if (StringUtils.isEmpty(uniqueId)) {
				return "此人尚未建档，请建档后再进行筛查";
			}
			
			params.put("uniqueId", uniqueId);
			String id = request.getParameter("dataId");
			params.putAll(this.parseRequest(request));
			params.remove("customerId");
			params.remove("name");
			if (!StringUtils.isEmpty(id)) {
				params.put("id", id);
				questionService.saveData(params);
			} else {
				Map<String, Object> queryMap = new HashMap<String, Object>();
				queryMap.put("uniqueId", uniqueId);
				queryMap.put("checkDate", params.get("checkDate"));
			
				Map<String, Object> questionData = questionService.getDataByQuery(queryMap);
				if (questionData != null) {
					params.remove("id");
					String did = (String)questionData.get("id");
					questionData.clear();
					questionData.putAll(params);
					questionData.put("id", did);
					questionService.saveData(questionData);
				} else {
					questionService.saveData(params);
				}
			}
			
			//保存历史记录
			questionService.saveData("question_history", params);
			
			params.putAll(user);
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("save menu error", e);
			return "save menu erro";
		}
	}
	private Map<String,Object> parseRequest(HttpServletRequest request) {
		  Map<String,Object> maps = Maps.newHashMap();
		  Enumeration keys = request.getParameterNames();
		  while (keys.hasMoreElements()) {
			  String key = (String)keys.nextElement();
			  if ("takingDrugs".equals(key) || "beIll".equals(key) || "relativesBeIll".equals("")) continue;
			  String value = (String)request.getParameter(key);
			  maps.put(key, value);
		  }
		  return maps;
	}
}
