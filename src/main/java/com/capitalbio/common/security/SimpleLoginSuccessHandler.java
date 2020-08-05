package com.capitalbio.common.security;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import com.capitalbio.common.dao.MenuDAO;
import com.capitalbio.common.dao.UrlResourceDAO;
import com.capitalbio.common.dao.UserDAO;
import com.capitalbio.common.service.MenuService;
import com.capitalbio.common.service.UrlResourceService;
import com.capitalbio.common.util.IPUtil;
import com.capitalbio.common.util.ParamUtils;
import com.google.common.collect.Maps;
import com.opensymphony.oscache.base.InitializationException;

public class SimpleLoginSuccessHandler implements AuthenticationSuccessHandler,InitializingBean{

	private String defaultTargetUrl;  
    
    private boolean forwardToDestination = false;  
      
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();  
    
    @Autowired private UserDAO userDao;
	@Autowired private UrlResourceService urlResourceService;
	@Autowired private UrlResourceDAO urlResourceDAO;
	@Autowired private MenuService menuService;
	@Autowired private MenuDAO menuDao; 

	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		System.out.println("new1111111111111111+defaultTargetUrl:"+defaultTargetUrl);
		// TODO Auto-generated method stub
		this.saveLoginInfo(request, authentication);
		
		if(this.forwardToDestination){
			 request.getRequestDispatcher(this.defaultTargetUrl).forward(request, response);
		}else{
			this.redirectStrategy.sendRedirect(request, response, this.defaultTargetUrl);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void saveLoginInfo(HttpServletRequest request,Authentication authentication){  
		Map<String, Object> user = (Map<String, Object>) authentication.getPrincipal();  
		String time = ParamUtils.getDateNow();
		String ip = IPUtil.getRemortIP(request);
		
		Map<String, Object> logininfo = Maps.newHashMap();
		logininfo.put("logintime", time);
		logininfo.put("loginip", ip);
		logininfo.put("username", user.get("username"));
		userDao.saveUserLoginInfo(logininfo);
	}
	
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		if(StringUtils.isEmpty(defaultTargetUrl)){
			throw new InitializationException("you must configure defaultTargetUrl");
		}
	}

}
