package com.capitalbio.statement.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.statement.service.ProjectProcessService;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/tz")
public class TzController {
	
	@Autowired ProjectProcessService processService;
	
	@RequestMapping("index")
	public String indexs(HttpServletRequest request) {
		Map<String, Object> diseaseTotal = processService.getTzByDisease();
		
		Map<String, Object> tnbs = processService.getDiseaseData("糖尿病", diseaseTotal);
		Map<String, Object> tqs = processService.getDiseaseData("糖前", diseaseTotal);
		Map<String, Object> gxys = processService.getDiseaseData("高血压", diseaseTotal);
		Map<String, Object> fps = processService.getDiseaseData("肥胖", diseaseTotal);
		
		
		Map<String, Object> totalMap = processService.totalByTz();
		
		Map<String, Object> totalByTz = processService.getTotalByTz();
		
		request.setAttribute("tnbs", tnbs);
		request.setAttribute("tqs", tqs);
		request.setAttribute("gxys", gxys);
		request.setAttribute("fps", fps);
		request.setAttribute("totalMap", totalMap);
		request.setAttribute("totalByTz", totalByTz);
		//System.out.println("tnbs:" + tnbs);
		return "statement/tizhi";
	}

	@RequestMapping("tz")
	@ResponseBody
	public Message tz() throws Exception {
		
		Map<String, Object> tz = processService.getTotalByTz();
		
		Map<String, Object> tzByDisease = processService.tzByDisease();
		
		Map<String, Object> map = Maps.newHashMap();
		map.putAll(tz);
		map.putAll(tzByDisease);
		return Message.dataMap(map);
	}
	
	
	@RequestMapping("tzByDisease")
	@ResponseBody
	public Message tzByDisease() throws Exception {
		Map<String, Object> diseaseTotal = processService.getTzByDisease();
		
		Map<String, Object> tnbs = processService.getColumnData("糖尿病", diseaseTotal);
		Map<String, Object> tqs = processService.getColumnData("糖前", diseaseTotal);
		Map<String, Object> gxys = processService.getColumnData("高血压", diseaseTotal);
		Map<String, Object> fps = processService.getColumnData("肥胖", diseaseTotal);
		
		HashMap<String, Object> map = Maps.newHashMap();
		map.put("tnbs", tnbs);
		map.put("tqs", tqs);
		map.put("gxys", gxys);
		map.put("fps", fps);
		
		return Message.dataMap(map);
	}
}
