package com.capitalbio.common.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.UserService;
import com.capitalbio.common.util.ParamUtils;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/loginlog")
public class LoginLogController {
	
	private Logger logger = LoggerFactory.getLogger(getClass()); 
	@Autowired private UserService userService;
	
	@RequestMapping(value = "list")
	public String list() {
		return "loginlog/list";
	}
	
	@RequestMapping(value = "logList")
	@ResponseBody
	public Message pushList(HttpServletRequest request) throws Exception {
		Page pageData = new Page();
		Integer page = ParamUtils.getIntValue(request.getParameter("current"));
		Integer pageCount = ParamUtils.getIntValue(request.getParameter("showRows"));
		if (page != null) {
			pageData.setPageNum(page.intValue());
		}
		if (pageCount != null) {
			pageData.setNumPerPage(pageCount);
		}
		//Map<String,Object> queryMap = parseConditionRequest(request);
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.putAll(parseQueryRequest(request));
		String sortFileld = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("ctime", -1);
		if (StringUtils.isNotEmpty(sortFileld) && StringUtils.isNotEmpty(sortOrder)) {
			sortMap.put(sortFileld, Integer.parseInt(sortOrder));
		}
		Map<String, Object> returnMap = Maps.newHashMap();
		List<Map<String, Object>> list = userService.findPageBySort(pageData, queryMap, sortMap, "loginInfo");
		long count = userService.count("loginInfo", queryMap);
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}
	
	//解析查询条件
	private Map<String,Object> parseQueryRequest(HttpServletRequest request) throws Exception{
		Map<String,Object> param=Maps.newHashMap();
		String username = request.getParameter("username");
		if (StringUtils.isNotEmpty(username)) {
			param.put("username", username);
		}
		return param;
	}

}
