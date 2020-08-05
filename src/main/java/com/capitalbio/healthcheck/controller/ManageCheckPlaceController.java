package com.capitalbio.healthcheck.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/checkPlace")
public class ManageCheckPlaceController {

	@Autowired HealthCheckService healthCheckService;
	
	@RequestMapping("/list")
	public String list(HttpServletRequest request) throws Exception {
		List<Map<String, Object>> results = healthCheckService.findAll("checkplace");
		request.setAttribute("checkplaces", results);
		return "healthCheck/checkPlace_list";
	}
	
	@RequestMapping(value="/editCheckPlace",method = RequestMethod.GET)
	@ControllerLog
	public String editCheckPlace(HttpServletRequest request,Page pageData) throws Exception{
		String checkplaceid = request.getParameter("id");
		Map<String, Object> map = healthCheckService.getData("checkplace", checkplaceid);
		request.setAttribute("checkplace", map);
		return "healthCheck/checkPlace_edit";
	}
	
	@RequestMapping("/saveCheckPlace")
	@ResponseBody
	public Message saveCheckPlace(HttpServletRequest request) {
		String district = request.getParameter("district");
		String checkplaceid = request.getParameter("checkplaceid");
		
		Map<String, Object> map = null;
		if (StringUtils.isNotEmpty(checkplaceid)) {
			map = healthCheckService.getData("checkplace",checkplaceid);
		} else {
			map = Maps.newHashMap();
		}
		map.put("district", district);
		
		String id = healthCheckService.saveData("checkplace", map);
		return Message.data(id);
	}
	
	@RequestMapping(value = "/delCheckPlace/{id}", method = RequestMethod.POST)
	@ResponseBody
	@ControllerLog
	public Message delCheckPlace(@PathVariable String id,HttpServletRequest request){
		if(StringUtils.isNotEmpty(id)){
			healthCheckService.deleteData("checkplace", id);
			return Message.success();
		}
		return Message.error("delete menu error");
	}
	
}
