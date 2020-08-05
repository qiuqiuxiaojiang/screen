package com.capitalbio.healthcheck.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.healthcheck.dao.CustomerDAO;
import com.capitalbio.healthcheck.service.CustomerService;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

	@Autowired CustomerService customerService;
	@Autowired CustomerDAO customerDao;
	
	/**
	 * 建档情况
	 * @throws Exception 
	 */
	@RequestMapping("recordCondition")
	@ResponseBody
	public Message recordCondition(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> map = customerService.recordCondition(district);
		return Message.dataMap(map);
	}
	
	
	/**
	 * 血糖筛查情况
	 * @throws Exception 
	 */
	@RequestMapping("findBloodSugarCondition")
	@ResponseBody
	public Message findBloodSugarCondition(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> map = customerService.findBloodSugarCondition(district);
		return Message.dataMap(map);
	}
	
	/**
	 * 血压筛查情况
	 * @throws Exception 
	 */
	@RequestMapping("findBloodPressureCondition")
	@ResponseBody
	public Message findBloodPressureCondition(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> map = customerService.findBloodPressureCondition(district);
		return Message.dataMap(map);
	}
	
	/**
	 * 血脂筛查情况
	 * @throws Exception 
	 */
	@RequestMapping("findBloodLipidCondition")
	@ResponseBody
	public Message findBloodLipidCondition(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> map = customerService.findBloodLipidCondition(district);
		return Message.dataMap(map);
	}
	
	/**
	 * 肥胖筛查情况
	 * @throws Exception 
	 */
	@RequestMapping("findFatCondition")
	@ResponseBody
	public Message findFatCondition(HttpServletRequest request) {
		String district = request.getParameter("district");
		Map<String, Object> map = customerService.findFatCondition(district);
		return Message.dataMap(map);
	}
	
	/**
	 * 体质筛查情况
	 * @throws Exception 
	 */
	@RequestMapping("findTizhiCondition")
	@ResponseBody
	public Message findTizhiCondition(HttpServletRequest request) {
		String district = request.getParameter("district");
		Map<String, Object> map = customerService.findTizhiCondition(district);
		return Message.dataMap(map);
	}
	
}
