package com.capitalbio.common.security;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.dao.MenuDAO;
import com.capitalbio.common.dao.UrlResourceDAO;
import com.capitalbio.common.dao.UserDAO;
import com.capitalbio.common.service.MenuService;
import com.capitalbio.common.service.UrlResourceService;
import com.capitalbio.common.service.UserService;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.common.util.ParamUtils;

public class MyAuthenticationFilter extends UsernamePasswordAuthenticationFilter  {
	@Autowired private UserDAO userDao;
	@Autowired private UrlResourceService urlResourceService;
	@Autowired private UrlResourceDAO urlResourceDAO;
	@Autowired private MenuService menuService;
	@Autowired private MenuDAO menuDao;
	@Autowired private UserService userService;
	@Autowired private AuthService authService;
	
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
		if(!request.getMethod().equals("POST")){
			throw new AuthenticationServiceException("authentication mehtod not supported: " + request.getMethod());
		}
		
		String username = obtainUsername(request);  
        String password = obtainPassword(request); 
        username = username.trim();
		
		String district = request.getParameter("district");
		String checkPlace = request.getParameter("checkPlace");
		
		if (StringUtils.isEmpty(username)) {
			BadCredentialsException usernameException = new BadCredentialsException("请输入用户名");
			throw usernameException;
		}
		
		if (StringUtils.isEmpty(password)) {
			BadCredentialsException passwordException = new BadCredentialsException("请输入密码");
			throw passwordException;
		}
		
		if (StringUtils.isEmpty(district)) {
			BadCredentialsException districtException = new BadCredentialsException("请选择筛查区域");
			throw districtException;
		}
		
		if (StringUtils.isEmpty(checkPlace)) {
			BadCredentialsException checkPlaceException = new BadCredentialsException("请填写筛查地点");
			throw checkPlaceException;
		}
		
		UsernamePasswordAuthenticationToken authRequest = null;
		String time = ParamUtils.getDateNow();
		if((username != null && !"".equals(username)) && (password != null && !"".equals(password))){
			Map<String, Object> user = userDao.getUserByName(username);
			
			if (user == null) {
				UsernameNotFoundException exception1 = new UsernameNotFoundException("用户名或密码错误");//用户名或密码错误
				userService.saveUserLoginInfo(username, request, "fail", time);
				throw exception1;
			}
			
			String timeBefore = ParamUtils.getTimeBefore();//当前时间前30分钟的时间
			boolean isLogin = userService.isLogin(username, timeBefore, time);
			if (!isLogin) {
				BadCredentialsException exception2 = new BadCredentialsException("登录错误次数超过5次，请30分钟后重试");
				throw exception2;
			}
			
			if (user != null && user.get("roles")==null) {
				BadCredentialsException exception2 = new BadCredentialsException("您没有登录权限");
				throw exception2;
			}
			
//			JSONObject jsonObject = HttpClient.get1(PropertyUtils.getProperty("auth.url") + "/dm/login", username, password);
			JSONObject jsonObject = authService.login(username, password);
			if (jsonObject != null) {
				if (jsonObject.getString("code").equals("1")) {
					UsernameNotFoundException exception1 = new UsernameNotFoundException("用户名或密码错误");//用户名或密码错误
					userService.saveUserLoginInfo(username, request, "fail", time);
					throw exception1;
				}else {
					String data = jsonObject.getString("data");
					JSONObject jsonData = JSON.parseObject(data);
					String token = jsonData.getString("token");
					String userId = jsonData.getString("userId");
					
					request.getSession().setAttribute("token", token);
					request.getSession().setAttribute("userId", userId);
					request.getSession().setAttribute("user", user);
					if (user != null) {
						request.getSession().setAttribute("username", user.get("username"));
					}
					request.getSession().setAttribute("district", district);
					request.getSession().setAttribute("checkPlace", checkPlace);
					
					//获取菜单
					List<Map<String, Object>> menuList = menuService.getMenu(request, user);
					request.getSession().setAttribute("menuList", JsonUtil.listToJson(menuList));
					System.out.println("menuList:"+menuList);
					userService.saveUserLoginInfo(username, request, "success", time);
				}
			} else {
				UsernameNotFoundException exception = new UsernameNotFoundException("用户名或密码错误");//用户名或密码错误
				userService.saveUserLoginInfo(username, request, "fail", time);
				throw exception;
			}
			
		} else {
			//userService.saveUserLoginInfo(username, request, "fail", time);
			BadCredentialsException exception = new BadCredentialsException("用户认证失败");//用户名或密码错误
			throw exception;
		}
		//实现验证
		authRequest = new UsernamePasswordAuthenticationToken(username, username);
		//允许设置用户详细属性
		setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}
	
	protected String obtainUsername(HttpServletRequest request){
		Object obj = request.getParameter("username");
		return null == obj ?"":obj.toString();
	}
	
	protected String obtainPassword(HttpServletRequest request){
		Object obj = request.getParameter("password");
		return null == obj ?"":obj.toString();
	}
	
	 protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {  
         super.setDetails(request, authRequest);  
	 }  

}
