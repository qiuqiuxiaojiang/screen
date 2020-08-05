package com.capitalbio.common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.capitalbio.common.model.UserSession;
import com.capitalbio.common.service.UserQuitOutService;
import com.capitalbio.common.util.ContextUtils;
@Controller
@RequestMapping("logout")
public class UserQuitOutController {
	@Autowired private UserQuitOutService userQuitOutService;
	
	@RequestMapping(value="quit",method=RequestMethod.GET)
	public String  quitOut(HttpServletRequest request,HttpServletResponse response){
		ContextUtils.getSession().removeAttribute("user");
		UserSession.remove("user");
//		request.getSession().removeAttribute("loginType");
//		request.getSession().removeAttribute("sampleId");
		return "redirect:/login/toLogin.htm";
	}

}
