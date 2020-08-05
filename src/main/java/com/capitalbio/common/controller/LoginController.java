package com.capitalbio.common.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.service.LoginService;
import com.capitalbio.common.service.MenuService;
import com.capitalbio.common.service.SystemConfigService;
import com.capitalbio.common.service.UserService;
import com.capitalbio.eye.service.EyeRecordService;

@Controller
@RequestMapping("login")
public class LoginController {
	private Logger logger = LoggerFactory.getLogger(getClass()); 
	@Autowired private LoginService loginService;
	@Autowired private UserService userService;
	@Autowired private EyeRecordService archiveService;
	@Autowired private SystemConfigService systemConfigService;
	@Autowired private MenuService menuService;
	
	@RequestMapping(value="toLogin",method = RequestMethod.GET)
	public String toLogin(HttpServletRequest request) throws Exception {
		List<Map<String, Object>> districts = loginService.findAll("checkplace");
		
		List<Map<String, Object>> configs = systemConfigService.findAll();
		String url = "";
		for (Map<String, Object> config : configs) {
			url = (String) config.get("loginjsp");
			break;
		}
		if (url == null || url.length() == 0) {
			url = "login";
		}
		
		request.setAttribute("districts", districts);
		return url;
//		return "login";
	}
	
	@RequestMapping(value="checkLogin",method = RequestMethod.GET)
	@ResponseBody
	public Message checkLogin(HttpServletRequest request) throws Exception {
		return Message.error("用户名或密码错误");
	}
	
	
	@RequestMapping(value="login",method = RequestMethod.GET)
	@ControllerLog
	public String login(HttpServletRequest request) throws Exception {
		return "main";
	}
	
	
	
	@RequestMapping(value="/mobileLogin",method = RequestMethod.GET)
	public String mobileLogin(){
		return "mobile/login";
	}		
	
	/**
	 * 指向无访问权限页面
	 * @return
	 */
	@RequestMapping(value = "/denied", method = RequestMethod.GET)
	@ControllerLog
	
	public String getDeniedPage(){
		logger.debug("Received request to show denied page.");
		return "deniedpage";
	}

}


