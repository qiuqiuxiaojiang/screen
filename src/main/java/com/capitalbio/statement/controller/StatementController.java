package com.capitalbio.statement.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.JsonUtil1;
import com.capitalbio.eye.service.EyeRecordService;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/manage/statement")
public class StatementController {
	
	@Autowired
	private HealthCheckService healthCheckService;
	@Autowired
	private EyeRecordService eyeRecordService;
	
	@RequestMapping(value = "report")
	public String report() {
		return "statement/report";
	}
	
	@RequestMapping(value = "getData")
	@ResponseBody
	public Message getData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String type = request.getParameter("type");
		//System.out.println("type:"+type);
		//System.out.println("startTime:"+startTime);
		//System.out.println("endTime:"+endTime);
		//String type = "cYear";
		List<Double> hcs = Lists.newArrayList();
		List<Double> bss = Lists.newArrayList();
		List<Double> records = Lists.newArrayList();
		List<String> days = Lists.newArrayList();
		
		if (StringUtils.isEmpty(startTime)) {
			startTime = DateUtil.getDate(6);
		}
		
		if (StringUtils.isEmpty(endTime)) {
			Date date = new Date();
			endTime = DateUtil.getNowDate(date);
		}
		
		if (type.equals("currentMonth")) {
			startTime = DateUtil.getCurrentMonthStartTime();
		}
		int betweenDay = 0;
		if (type.equals("search")) {
			betweenDay = DateUtil.betweenDays(DateUtil.stringToDate(startTime), DateUtil.stringToDate(endTime));
			if (betweenDay > 30) {
				type = "searchMonth";
			} else {
				type = "searchDay";
			}
		}
		
		if (!type.equals("cQuarter") && !type.equals("cYear") && !type.equals("searchMonth")) {
			Map<String, Object> hcMap = healthCheckService.getHealthCheckData(startTime, endTime);
			//System.out.println("hcMap:" + hcMap);
			Map<String, Object> bsMap = healthCheckService.getBloodSugarData(startTime, endTime);
			//System.out.println("bsMap:" + bsMap);
			Map<String, Object> recordMap = eyeRecordService.getEyeRecordData(startTime, endTime);
			//System.out.println("recordMap:" + recordMap);
			
			if (type.equals("currentMonth")) {  //近30天
				//int dayOfMonth = DateUtil.getDayOfMonth();
				//System.out.println("dayOfMonth:"+dayOfMonth);
				for (int i = 29; i >= 0; i --) {
					//System.out.println("day:"+DateUtil.getDate(i));
					days.add(DateUtil.getDate(i));
				}
			} else if (type.equals("searchDay")) {
				days = DateUtil.getDays(startTime, endTime);
			} else {
				days.add(DateUtil.getDate(6));
				days.add(DateUtil.getDate(5));
				days.add(DateUtil.getDate(4));
				days.add(DateUtil.getDate(3));
				days.add(DateUtil.getDate(2));
				days.add(DateUtil.getDate(1));
				days.add(endTime);
			}
			//System.out.println("days:" + days);
			
			for (int i = 0; i < days.size(); i ++) {
				if (hcMap.containsKey(days.get(i))) {
					hcs.add(Double.parseDouble(hcMap.get(days.get(i)).toString()));
				} else {
					hcs.add((double) 0);
				}
				
				if (bsMap.containsKey(days.get(i))) {
					bss.add(Double.parseDouble(bsMap.get(days.get(i)).toString()));
				} else {
					bss.add((double) 0);
				}
				
				if (recordMap.containsKey(days.get(i))) {
					records.add(Double.parseDouble(recordMap.get(days.get(i)).toString()));
				} else {
					records.add((double) 0);
				}
			}
		}
		
		if (type.equals("cQuarter")) {
			for (int i = 2; i >= 0; i--) {
				String month = DateUtil.getBeforeMonth(i);
				String startTime1 = DateUtil.getFirstTime(month);
				String endTime1 = DateUtil.getLastTime(month);
				
				if (i == 2) {
					startTime = startTime1;
				}
				
				if (i == 0) {
					endTime = endTime1;
				}
				
				double hc = healthCheckService.getHcDataOfMonth(startTime1, endTime1);
				double bs = healthCheckService.getBsDataOfMonth(startTime1, endTime1);
				double record = eyeRecordService.getErDataOfMonth(startTime1, endTime1);
				hcs.add(hc);
				bss.add(bs);
				records.add(record);
				days.add(month);
			}
		}
		
		if (type.equals("cYear")) {
			for (int i = 11; i >= 0; i--) {
				String month = DateUtil.getBeforeMonth(i);
				String startTime1 = DateUtil.getFirstTime(month);
				String endTime1 = DateUtil.getLastTime(month);
				
				if (i == 11) {
					startTime = startTime1;
				}
				
				if (i == 0) {
					endTime = endTime1;
				}
				
				double hc = healthCheckService.getHcDataOfMonth(startTime1, endTime1);
				double bs = healthCheckService.getBsDataOfMonth(startTime1, endTime1);
				double record = eyeRecordService.getErDataOfMonth(startTime1, endTime1);
				hcs.add(hc);
				bss.add(bs);
				records.add(record);
				days.add(month);
			}
		}
		
		if (type.equals("searchMonth")) {
			days = DateUtil.getMonths(startTime, endTime);
			for (int i = 0; i < days.size(); i ++) {
				startTime = DateUtil.getFirstTime(days.get(i));
				endTime = DateUtil.getLastTime(days.get(i));
				double hc = healthCheckService.getHcDataOfMonth(startTime, endTime);
				double bs = healthCheckService.getBsDataOfMonth(startTime, endTime);
				double record = eyeRecordService.getErDataOfMonth(startTime, endTime);
				hcs.add(hc);
				bss.add(bs);
				records.add(record);
			}
		}
		
		double hcCount = healthCheckService.getHcCount(startTime, endTime);
		double bsCount = healthCheckService.getBsCount(startTime, endTime);
		double eyeRecordCount = eyeRecordService.getEyeRecordCount(startTime, endTime);
		//System.out.println("hcs:"+hcs);
		//System.out.println("bss:"+bss);
		//System.out.println("records:"+records);
		Map<String, Object> map = Maps.newHashMap();
		map.put("days", days);
		map.put("hcs", hcs);
		map.put("bss", bss);
		map.put("records", records);
		map.put("hcCount", hcCount);
		map.put("bsCount", bsCount);
		map.put("recordCount", eyeRecordCount);
		//System.out.println("days:"+days);	
		return Message.dataMap(map);
	}
	
	
	@RequestMapping(value = "data")
	@ResponseBody
	public Message data() throws Exception {
		System.out.println("1111111111111");
		Map<String, Object> json2Map = JsonUtil1.json2Map("{\"bmi\":{\"a\":\"13.0\",\"b\":\"17.0\",\"c\":\"14.0\",\"d\":\"8.0\"}}");
		return Message.dataMap(json2Map);
	}

}
