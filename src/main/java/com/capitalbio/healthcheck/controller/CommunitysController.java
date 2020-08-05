package com.capitalbio.healthcheck.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.healthcheck.service.CommunitysService;

@Controller
@RequestMapping("/communitys")
public class CommunitysController {
	
	@Autowired
	private CommunitysService communitysService;
	
	@RequestMapping("/getCommunitysByStreet")
	@ResponseBody
	public Message getCommunitysByStreet(HttpServletRequest request) {
		String street = request.getParameter("street");
		
		List<Map<String, Object>> communitys = communitysService.getCommunitysByStreet(street);
		return Message.dataList(communitys);
		
	}

}
