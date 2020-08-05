package com.capitalbio.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

public class ContextUtils {
	
	public static HttpServletRequest getRequest(){
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = requestAttributes.getRequest();
			return request;
		}
		return null;
	}
	
	public static HttpServletResponse getResponse() {
		ServletWebRequest webRequest = (ServletWebRequest)RequestContextHolder.getRequestAttributes();
		HttpServletResponse response=webRequest.getResponse();
		return response;
	}
	
	public static HttpSession getSession(){
		HttpServletRequest request = getRequest();
		if (request != null) {
			HttpSession session = request.getSession();
			return session;
		}
		return null;
	}

	public static String getUserId() {
		HttpSession session = getSession();
		if (session != null) {
			String userId = (String)session.getAttribute("userId");
			return userId;
		}
		return null;
	}
}
