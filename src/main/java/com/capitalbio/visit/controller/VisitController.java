/**
 * 
 */
package com.capitalbio.visit.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.visit.service.VisitPlanService;
import com.capitalbio.visit.service.VisitService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author wdong
 *
 */
@Controller
@RequestMapping("/visit")
public class VisitController {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired 
	AuthService authService;
	@Autowired 
	VisitService visitService;
	@Autowired 
	VisitPlanService visitPlanService;

	/**
	 * 进入随访计划列表页面
	 * @return
	 */
	@RequestMapping(value = "planList")
	public String list() {
		return "visit/visitplan_list";
	}

	/**
	 * 加载所有随访计划
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "visitPlanList")
	@ResponseBody
	@ControllerLog
	public Message customerList(HttpServletRequest request) throws Exception {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
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
			
			Map<String,Object> dataMap = visitPlanService.getVisitPlanByUniqueId(uniqueId);
			if (dataMap == null) {
				return Message.success();
			}
			dataMap.putAll(secretMap);
			List<Map<String, Object>> list = Lists.newArrayList();
			list.add(dataMap);
			Map<String, Object> returnMap = Maps.newHashMap();
			returnMap.put("list", list);
			returnMap.put("totalCount", 1);
			return Message.dataMap(returnMap);
		}
		
		Map<String,Object> queryMap = Maps.newHashMap();
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
		List<Map<String, Object>> list = visitPlanService.findPageBySort(pageData, queryMap, sortMap);
		
		long count = visitPlanService.count(queryMap);
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 加载提醒人数
	 * @param request
	 * @return
	 */
	@RequestMapping(value="loadRemindCount")
	@ResponseBody
	public Message loadRemindCount(HttpServletRequest request) {
		int newCount = visitPlanService.countRemind("new");
		int expireCount = visitPlanService.countRemind("expire");
		int allCount = newCount + expireCount;
		Map<String,Object> dataMap = new HashMap<>();
		dataMap.put("newCount", newCount);
		dataMap.put("expireCount", expireCount);
		dataMap.put("allCount", allCount);
		return Message.dataMap(dataMap);
	}
	
	@RequestMapping(value="today/{remindType}")
	public String today(@PathVariable String remindType, HttpServletRequest request) {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		List<Map<String,Object>> newPlanList = visitPlanService.getRemindList("new");
		for (Map<String,Object> visitPlan:newPlanList) {
			String uniqueId = (String)visitPlan.get("uniqueId");
			Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
			if (secretMap == null) {
				continue;
			}
			visitPlan.putAll(secretMap);
		}
		List<Map<String,Object>> expirePlanList = visitPlanService.getRemindList("expire");
		for (Map<String,Object> visitPlan:expirePlanList) {
			String uniqueId = (String)visitPlan.get("uniqueId");
			Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
			if (secretMap == null) {
				continue;
			}
			visitPlan.putAll(secretMap);
		}
		request.setAttribute("newPlanList", newPlanList);
		request.setAttribute("expirePlanList", expirePlanList);
		request.setAttribute("remindType", remindType);
		return "visit/today_list";
	}

}
