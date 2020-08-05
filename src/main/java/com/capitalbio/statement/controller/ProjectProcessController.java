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

@Controller
@RequestMapping("/process")
public class ProjectProcessController {

	@Autowired ProjectProcessService processService;
	@Autowired ProjectProcessDao processDao;
	
	String date = "";
	
	@RequestMapping("/index")
	public String index(HttpServletRequest request) throws ParseException{
		String checkDate = request.getParameter("checkDate");
		
		if (StringUtils.isEmpty(checkDate)) {
			checkDate = DateUtil.getDate(1);
		}
		
		date = checkDate;
		//String checkDate = "2017-09-05";
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("checkDate", "-");
		Set<String> checkPlaces = processService.getAllCheckPlace();
		
		List<Map<String, Object>> results = processService.getProjectProcessIndex(checkPlaces, checkDate);
		request.setAttribute("results", results);
		
		Map<String, Object> totalNum = processService.totalNum(checkDate);
		request.setAttribute("totalNum", totalNum);
		
		request.setAttribute("checkDate", checkDate);
		
		return "statement/projectProcess";
	}
	
	@RequestMapping("totalColumn")
	@ResponseBody
	public Message totalColumn(){
		String checkDate = date;
		Map<String, Object> totalNum = processService.totalNum(checkDate);
		
		List<Object> dayNum = Lists.newArrayList();
		dayNum.add(totalNum.get("infoDayAll"));
		dayNum.add(totalNum.get("effectDayAll"));
		dayNum.add(totalNum.get("bgDayAll"));
		dayNum.add(totalNum.get("eDayAll"));
		dayNum.add(totalNum.get("ecDayAll"));
		dayNum.add(totalNum.get("epDayAll"));
		dayNum.add(totalNum.get("rtDayAll"));
		dayNum.add(totalNum.get("rcDayAll"));
		dayNum.add(totalNum.get("roDayAll"));
		dayNum.add(totalNum.get("rTranDayAll"));
		
		List<Object> allNum = Lists.newArrayList();
		allNum.add(totalNum.get("infoAll"));
		allNum.add(totalNum.get("effectAll"));
		allNum.add(totalNum.get("bgAll"));
		allNum.add(totalNum.get("eAll"));
		allNum.add(totalNum.get("ecAll"));
		allNum.add(totalNum.get("epAll"));
		allNum.add(totalNum.get("rtAll"));
		allNum.add(totalNum.get("rcAll"));
		allNum.add(totalNum.get("roAll"));
		allNum.add(totalNum.get("rTranAll"));
		
		HashMap<String,Object> map = Maps.newHashMap();
		map.put("dayNum", dayNum);
		map.put("allNum", allNum);
		
		return Message.dataMap(map);
	}
	
	/*@RequestMapping("/pIndex/{type}")
	@ResponseBody
	public Message pIndex(@PathVariable String type){
		String checkDate = "2017-09-05";
		
		HashMap<String, Object> query = Maps.newHashMap();
		query.put("checkDate", checkDate);
		List<Map<String, Object>> list = processService.getDayTotal(query, type);
		
		List<String> checkplace = Lists.newArrayList();
		List<Integer> dayTotal = Lists.newArrayList();
		List<Integer> addressTotal = Lists.newArrayList();
		
		for (Map<String, Object> map : list) {
			HashMap<String, Object> queryMap = Maps.newHashMap();
			queryMap.put("checkPlace", map.get("checkPlace"));
			queryMap.put("checkDate", "-");
			Map<String, Object> totalByAddress = processService.getDataByQuery(type, queryMap);
			int total = 0;
			if (totalByAddress != null) {
				total = Integer.parseInt(totalByAddress.get("count").toString());
			}
			
			map.put("total", total);
			
			checkplace.add(map.get("checkPlace").toString());
			dayTotal.add(Integer.parseInt(map.get("count").toString()));
			addressTotal.add(total);
		}
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("checkplace", checkplace);
		map.put("dayTotal", dayTotal);
		map.put("addressTotal", addressTotal);
		
		return Message.dataMap(map);
	}*/
}
