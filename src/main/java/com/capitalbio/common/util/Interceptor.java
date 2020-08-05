package com.capitalbio.common.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.dao.UrlResourceDAO;
import com.capitalbio.common.util.redis.DataDictUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;


public class Interceptor extends HandlerInterceptorAdapter{
	private static final Logger logger = Logger.getLogger(Interceptor.class);
	
	private static final String[] IGNORE_URL = {"/login/","/register/","/logout/",/*"rest",*/ "eye", "bodyData", "manage/hv", "/deviceApp/login", 
			"/healthcheck","/deviceApp/bind","/deviceApp/unbind","/deviceApp/bindList","statistics","auth","rest/ha/getTokenAndUserid"
			,"rest/screen/getTokenAndUserid", "/obsfile/image", "/visitImport/customerVisit"};

	@Autowired private UrlResourceDAO urlResourceDao;
	@Autowired private AuthService authService;
	
	JedisTemplate template = RedisUtils.getTemplate();
	
	
	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {  
//		return true;
		//1、请求到登录页面 放行
		String loginUrl = request.getContextPath() + "/login/toLogin.htm";
		if(request.getServletPath().startsWith("/login/toLogin.htm")) {   
		    return true;   
		}  
		
		//2、TODO 比如退出、首页等页面无需登录，即此处要放行 允许游客的请求  
		String url=request.getRequestURL().toString();
		for(String s : IGNORE_URL){
			if(url.indexOf(s) != -1){
				return true;
			}
		}
		
		String tokenChms = request.getHeader("token");
		String useridChms = request.getHeader("userid");
		System.out.println("===============token=============" + tokenChms + ",userid" + useridChms);
		if (StringUtils.isNotEmpty(tokenChms) && StringUtils.isNotEmpty(useridChms)) {
			
			String token1 = template.get("token"+tokenChms);;
			String userid1 = template.get("userid"+useridChms);
			if (StringUtils.isEmpty(token1) || StringUtils.isEmpty(userid1)) {
				returnErrorMessage(response, "token不存在或已过期", 3);
				return false;
			}
			return true;
		} else {
			//验证token合法性
			String token = (String) ContextUtils.getSession().getAttribute("token");
			String userId = (String) ContextUtils.getSession().getAttribute("userId");
			boolean verifyToken = authService.verifyToken(userId, token);
			if (verifyToken) {
				//3、如果用户已经登录 放行     
				if(ContextUtils.getSession().getAttribute("user") != null) {
					
					Map<String, Object> user = (Map<String, Object>) ContextUtils.getSession().getAttribute("user");
					
//					UserSession.set("user", user);
		            logger.info("url:"+request.getRequestURL()+",sessionID:"+request.getSession().getId()
		            		+",createBy:"+user.get("username")+",ip:"+request.getRemoteAddr()+",date:"+ (new Date()));
		            return true;
					
				}
			}
			
			//4、非法请求 即这些请求需要登录后才能访问   
			//重定向到登录页面   
			response.sendRedirect(loginUrl);  
			return false; 
		}
	 }
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, 
			Object handler,ModelAndView modelAndView) throws Exception {
		
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		
	}
	
	private void returnErrorMessage(HttpServletResponse response, String errorMessage, int code)
			throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);
		map.put("msg", errorMessage);
		
		response.setContentType("application/json");
		// Get the printwriter object from response to write the required json object to
		// the output stream
		PrintWriter out = response.getWriter();
		// Assuming your json object is **jsonObject**, perform the following, it will
		// return your json object
		ObjectMapper mapper = new ObjectMapper();
		String jsonOfRST = mapper.writeValueAsString(map);
		out.print(jsonOfRST);
		out.flush();
	}
}
