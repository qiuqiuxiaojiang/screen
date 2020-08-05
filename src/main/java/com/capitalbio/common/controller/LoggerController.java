package com.capitalbio.common.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.log.LoggerService;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.util.ParamUtils;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/manage/log")
public class LoggerController {
	@Autowired
	private LoggerService loggerService;

	@RequestMapping(value = "list")
	public String list() {
		return "log/list";
	}
	
	@RequestMapping(value = "logList")
	@ResponseBody
	public Message logList(HttpServletRequest request) throws Exception {
		Page pageData = new Page();
		Integer page = ParamUtils.getIntValue(request.getParameter("current"));
		Integer pageCount = ParamUtils.getIntValue(request.getParameter("showRows"));
		if (page != null) {
			pageData.setPageNum(page.intValue());
		}
		if (pageCount != null) {
			pageData.setNumPerPage(pageCount);
		}
		Map<String,Object> queryMap = parseConditionRequest(request);
		//Map<String,Object> queryMap = Maps.newHashMap();
		String sortFileld = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("ctime", -1);
		if (StringUtils.isNotEmpty(sortFileld) && StringUtils.isNotEmpty(sortOrder)) {
			sortMap.put(sortFileld, Integer.parseInt(sortOrder));
		}
		Map<String, Object> returnMap = Maps.newHashMap();
		List<Map<String, Object>> list = loggerService.findPageBySort(pageData, queryMap, sortMap);
		long count = loggerService.count(queryMap);
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}
	
	private Map<String,Object> parseConditionRequest(HttpServletRequest request){
		Map<String,Object> param=Maps.newHashMap();
		String method = request.getParameter("method");
		if(StringUtils.isNotEmpty(method)){
			param.put("method", method);
		}
		
		String ip = request.getParameter("ip");
		if(StringUtils.isNotEmpty(ip)){
			param.put("ip", ip);
		}
		
		String uri = request.getParameter("uri");
		if(StringUtils.isNotEmpty(uri)){
			param.put("uri", uri);
		}
		return param;
	}
}
