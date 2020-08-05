package com.capitalbio.statement.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.statement.dao.ProjectProcessDao;
import com.capitalbio.statement.service.ProjectProcessService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 慢病分布情况统计
 * @author xiaoyanzhang
 *
 */
@Controller
@RequestMapping("/ncd")
public class NCDController {

	@Autowired ProjectProcessService processService;
	@Autowired ProjectProcessDao processDao;
	
	String date = "";
	
	@RequestMapping("index")
	public String index(HttpServletRequest request) throws ParseException {
		String checkDate = request.getParameter("checkDate");
		
		if (StringUtils.isEmpty(checkDate)) {
			checkDate = DateUtil.getDate(1);
		}
		
		date = checkDate;
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("checkDate", "-");
		Set<String> checkPlaces = processService.getAllNcdCheckPlace();
		
		List<Map<String, Object>> results = processService.getNcdIndex2(checkPlaces, checkDate);
		request.setAttribute("results", results);
		
		Map<String, Object> totalNum = processService.ncdOfTotalNum(checkDate);
		request.setAttribute("totalNum", totalNum);
		
		request.setAttribute("checkDate", checkDate);
		
		return "statement/ncdList";
	}
	
	
	@RequestMapping("numByDate")
	@ResponseBody
	public Message numByDate(HttpServletRequest request) throws ParseException {
		//获取当前日期的前一天
		//String date = DateUtil.getDate(1);
	
		String checkDate = date;
		
		Map<String, Object> totalNum = processService.ncdOfTotalNum(checkDate);
		
		List<Object> dayNum = Lists.newArrayList();
		dayNum.add(totalNum.get("dDayAll"));
		dayNum.add(totalNum.get("spDayAll"));
		dayNum.add(totalNum.get("sdgDayAll"));
		dayNum.add(totalNum.get("hrDayAll"));
		dayNum.add(totalNum.get("fDayAll"));
		dayNum.add(totalNum.get("nhpDayAll"));
		dayNum.add(totalNum.get("rhpDayAll"));
		
		List<Object> allNum = Lists.newArrayList();
		allNum.add(totalNum.get("dAll"));
		allNum.add(totalNum.get("spAll"));
		allNum.add(totalNum.get("sdgAll"));
		allNum.add(totalNum.get("hrAll"));
		allNum.add(totalNum.get("fAll"));
		allNum.add(totalNum.get("nhpAll"));
		allNum.add(totalNum.get("rhpAll"));
		
		List<Integer> dResult = Lists.newArrayList();
		List<Integer> spResult = Lists.newArrayList();
		List<Integer> sdgResult = Lists.newArrayList();
		List<Integer> hrResult = Lists.newArrayList();
		List<Integer> fatResult = Lists.newArrayList();
		List<Integer> nhpResult = Lists.newArrayList();
		List<Integer> rhpResult = Lists.newArrayList();
		List<String> weekDates = Lists.newArrayList();
		for (int i = 7; i > 0; i--) {
			checkDate = DateUtil.getDate(i);
			Map<String, Object> queryMap = Maps.newHashMap();
			queryMap.put("checkDate", checkDate);
			queryMap.put("checkPlace", "-");
			
			Map<String, Object> dCount = processService.getDataByQuery("reportDiabetes", queryMap);
			if (dCount != null) {
				dResult.add(Integer.parseInt(dCount.get("count").toString()));
			} else {
				dResult.add(0);
			}
			
			Map<String, Object> spCount = processService.getDataByQuery("reportSuspectedPrediabetes", queryMap);
			if (spCount != null) {
				spResult.add(Integer.parseInt(spCount.get("count").toString()));
			} else {
				spResult.add(0);
			}
			
			Map<String, Object> sdgCount = processService.getDataByQuery("reportSuspectedDiabetes", queryMap);
			if (sdgCount != null) {
				sdgResult.add(Integer.parseInt(sdgCount.get("count").toString()));
			} else {
				sdgResult.add(0);
			}
			
			Map<String, Object> hrCount = processService.getDataByQuery("reportHighRisk", queryMap);
			if (hrCount != null) {
				hrResult.add(Integer.parseInt(hrCount.get("count").toString()));
			} else {
				hrResult.add(0);
			}
			
			Map<String, Object> fatCount = processService.getDataByQuery("reportFat", queryMap);
			if (fatCount != null) {
				fatResult.add(Integer.parseInt(fatCount.get("count").toString()));
			} else {
				fatResult.add(0);
			}
			
			Map<String, Object> newHighPressureCount = processService.getDataByQuery("reportNewHighPressure", queryMap);
			if (newHighPressureCount != null) {
				nhpResult.add(Integer.parseInt(newHighPressureCount.get("count").toString()));
			} else {
				nhpResult.add(0);
			}
			
			Map<String, Object> registerHighPressureCount = processService.getDataByQuery("reportRegisterHighPressure", queryMap);
			if (registerHighPressureCount != null) {
				rhpResult.add(Integer.parseInt(registerHighPressureCount.get("count").toString()));
			} else {
				rhpResult.add(0);
			}
			
			weekDates.add(checkDate);
		}
		
		HashMap<String,Object> map = Maps.newHashMap();
		
		map.put("dayNum", dayNum);
		map.put("allNum", allNum);
		
		map.put("dResult", dResult);
		map.put("spResult", spResult);
		map.put("sdgResult", sdgResult);
		map.put("hrResult", hrResult);
		map.put("fatResult", fatResult);
		map.put("nhpResult", nhpResult);
		map.put("rhpResult", rhpResult);
		map.put("weekDates", weekDates);
		
		return Message.dataMap(map);
	}
	
}
