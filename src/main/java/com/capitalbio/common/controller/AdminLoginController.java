package com.capitalbio.common.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.capitalbio.common.model.UserSession;
import com.capitalbio.common.service.SystemConfigService;
import com.capitalbio.common.util.ContextUtils;

@Controller
//@RequestMapping("admin")
public class AdminLoginController {
	@Autowired private SystemConfigService systemConfigService;
	
//	@RequestMapping(value="toLogin",method = RequestMethod.GET)
	public String toLogin(HttpServletRequest request) throws Exception {
		List<Map<String, Object>> configs = systemConfigService.findAll();
		String url = "";
		for (Map<String, Object> config : configs) {
			url = (String) config.get("loginjsp");
			break;
		}
		if (url == null || url.length() == 0) {
			url = "login";
		}
		return url;
	
	}
	
//	@RequestMapping(value="quit",method=RequestMethod.GET)
	public String  quitOut(HttpServletRequest request,HttpServletResponse response){
		ContextUtils.getSession().removeAttribute("user");
		UserSession.remove("user");
		request.getSession().removeAttribute("loginType");
		request.getSession().removeAttribute("sampleId");
		return "redirect:/admin/toLogin.htm";
	}

}
