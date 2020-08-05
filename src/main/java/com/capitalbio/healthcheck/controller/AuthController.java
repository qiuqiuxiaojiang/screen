package com.capitalbio.healthcheck.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.auth.service.AuthService;

@Controller
@RequestMapping("/auth")
public class AuthController {
	@Autowired AuthService authService;

	@RequestMapping(value="applyToken")
	@ResponseBody
	public Map<String, Object> applyToken(HttpServletRequest request) throws Exception {
		Map<String, Object> tokenMap = authService.applyToken();
		return tokenMap;
		
	}
	
	@RequestMapping(value="getUserinfoByUniqueId")
	@ResponseBody
	public Map<String, Object> requestInfoByUniqueId(HttpServletRequest request) throws Exception {
		String uniqueId = request.getParameter("uniqueId");
		String token = request.getParameter("token");
		String userId = request.getParameter("userId");
		Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
		return secretMap;
		
	}
}
