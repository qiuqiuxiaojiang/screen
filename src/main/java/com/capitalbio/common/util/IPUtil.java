package com.capitalbio.common.util;

import javax.servlet.http.HttpServletRequest;

public class IPUtil {

	public static String getRemortIP(HttpServletRequest request) {  
	      String ip = request.getHeader("x-forwarded-for");  
	      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	         ip =  request.getHeader("Proxy-Client-IP");  
	     }  
	      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	         ip =  request.getHeader("WL-Proxy-Client-IP");  
	      }  
	     if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	          ip =  request.getRemoteAddr();  
	     }
	     if(ip!=null&&ip.indexOf(",")!=-1){
	    	 ip=ip.substring(0,ip.indexOf(","));
	     }
	     return ip;  
	}

}
