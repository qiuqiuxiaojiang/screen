package com.capitalbio.statement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.statement.dao.ProjectProcessDao;
import com.capitalbio.statement.service.ProjectProcessService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/diseaseAge")
public class DiseaseAgeController {

	@Autowired ProjectProcessService processService;
	@Autowired ProjectProcessDao processDao;
	
	@RequestMapping("/index")
	public String index() {
		return "statement/diseaseAge";
	}
	
	
	@RequestMapping("/diseaseScale")
	@ResponseBody
	public Message diseaseScale() {
		HashMap<String, Object> query = Maps.newHashMap();
		query.put("age", "-");
		List<Map<String, Object>> results = processService.queryList(query, "reportAge");
		
		List<Map<String, Object>> lists = Lists.newArrayList();
		Object total = processDao.getReportAgeTotal("reportAge");
		Double totalNum = Double.parseDouble(total.toString());
		
		if (totalNum != 0) {
			for (Map<String, Object> result : results) {
				Double count = Double.parseDouble(result.get("count").toString());
				
				Map<String, Object> map = Maps.newHashMap();
				map.put("name", result.get("type"));
				map.put("y", ParamUtils.doubleScale(count.doubleValue() / totalNum.doubleValue() * 100, 1));
				
				if (result.get("type").equals("糖尿病")){
					map.put("color", "rgb(124, 181, 236)");
				} else if(result.get("type").equals("糖前")) {
					map.put("color", "rgb(144, 237, 125)");
				} else{
					map.put("color", "#9767d4");
				}
				
				lists.add(map);
			}
		}
		return Message.dataList(lists);
	}
	
	@RequestMapping("/diseaseByAge")
	@ResponseBody
	public Message diseaseByAge() {
		String[] ages = {"15-20", "20-25", "25-30", "30-35", "35-40", "40-45", "45-50", "50-55", "55-60",
				"60-65", "65-70", "70-75", "75-80", "80-85", "85-90", "90-95", "95-100"};
		
		String[] ageList = {"17.5", "22.5", "27.5", "32.5", "37.5", "42.5", "47.5", "52.5", "57.5",
				"62.5", "67.5", "72.5", "77.5", "82.5", "87.8", "92.5", "97.5"};
		
		Map<String, Object> diabete = processService.getDataByAge("糖尿病");
		Map<String, Object> preDiabete = processService.getDataByAge("糖前");
		Map<String, Object> normal = processService.getDataByAge("正常");
		
		List<Integer> diabeteList = Lists.newArrayList();
		List<Integer> preDiabeteList = Lists.newArrayList();
		List<Integer> normalList = Lists.newArrayList();
		for (int i = 0; i < ages.length; i ++) {
			if (diabete.size() != 0) {
				if (diabete.get(ages[i]) != null) {
					diabeteList.add(Integer.parseInt(diabete.get(ages[i]).toString()));
				} else {
					diabeteList.add(0);
				}
			}
			
			if (preDiabete.size() != 0) {
				if (preDiabete.get(ages[i]) != null) {
					preDiabeteList.add(Integer.parseInt(preDiabete.get(ages[i]).toString()));
				} else {
					preDiabeteList.add(0);
				}
			}
			
			if (normal.size() != 0) {
				if (normal.get(ages[i]) != null) {
					normalList.add(Integer.parseInt(normal.get(ages[i]).toString()));
				} else {
					normalList.add(0);
				}
			}
			
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("diabeteList", diabeteList);
		map.put("preDiabeteList", preDiabeteList);
		map.put("normalList", normalList);
		map.put("ageList", ageList);
		return Message.dataMap(map);
	}
}
