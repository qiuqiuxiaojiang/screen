package com.capitalbio.common.controller;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.capitalbio.auth.util.Constant;
import com.capitalbio.common.cache.TokenCache;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.util.MD5Util;
import com.capitalbio.common.util.PropertyUtils;

public class AuthUtil {
//	private static Logger logger = Logger.getLogger(AuthUtil.class);
	public static String authType = PropertyUtils.getProperty("auth.type");

	public static Message checkToken(TreeMap<String, Object> treeMap) {
		String token = (String)treeMap.remove("token");
		long sysTime = (Long)treeMap.get("sysTime");
		if (StringUtils.isEmpty(token) || sysTime == 0) {
			return Message.error(Message.MSG_PARAM_NULL, "缺少Token或请求时间");
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String,Object> entry : treeMap.entrySet()) {
			sb.append(entry.getValue());
		}
		sb.append(Constant.SECRET_KEY);

		String checkToken = MD5Util.MD5Encode(sb.toString());
		if (!checkToken.equals(token)) {
			return Message.error(Message.MSG_AUTH_TOKENERROR,"Token错误");
		}
		long curTime = System.currentTimeMillis();
		
		long interval = curTime-sysTime;
		if (interval < 0) {
			interval = interval*(-1);
		}
		
		if (interval>Constant.JWT_EXPIRE) {
			return Message.error(Message.MSG_AUTH_REQUESTERROR, "URL超时");
		}
		return null;
	}

//	public static Message checkToken(String token, long sysTime, String data) {
//		if (StringUtils.isEmpty(token) || sysTime == 0) {
//			return Message.error(Message.MSG_PARAM_NULL, "缺少Token或请求时间");
//		}
//		String checkToken = MD5Util.MD5Encode(data+sysTime+Constant.SECRET_KEY);
//		if (!checkToken.equals(token)) {
//			return Message.error(Message.MSG_AUTH_TOKENERROR,"Token错误");
//		}
//		long curTime = System.currentTimeMillis();
//		if (curTime<sysTime || (sysTime-curTime)>Constant.JWT_REFRESH_INTERVAL) {
//			return Message.error(Message.MSG_AUTH_REQUESTERROR, "URL超时");
//		}
//		return null;
//	}
	
	public static String genToken(TreeMap<String, Object> treeMap){ 
		long time = System.currentTimeMillis();
		treeMap.put("sysTime", time);
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String,Object> entry : treeMap.entrySet()) {
			sb.append(entry.getValue());
		}
		sb.append(Constant.SECRET_KEY);
		String md5 = MD5Util.MD5Encode(sb.toString());
		return md5;
	}

	
	public static Message checkAuth(String username, String token) {
		if (StringUtils.isEmpty(token) || StringUtils.isEmpty(username)) {
			return Message.error(Message.MSG_PARAM_NULL, "缺少Token或者用户名");
		}
		if ("single".equals(authType)) {
			String findToken = TokenCache.getInstance().getToken(username);
			if (findToken == null || !findToken.equals(token)) {
				return Message.error(Message.MSG_AUTH_NOTLOGIN,"用户未登录");
			}
		} else {
			String findUsername = TokenCache.getInstance().getToken(token);
			if (findUsername == null || !findUsername.equals(username)) {
				return Message.error(Message.MSG_AUTH_NOTLOGIN,"用户未登录");
			}
		}
		return null;
	}
	
	public static void putToken(String username, String token) {
		if ("single".equals(authType)) {
			TokenCache.getInstance().putToken(username, token);
		} else {
			TokenCache.getInstance().putToken(token, username);
		}
	}
	
	public static void removeToken(String username, String token) {
		if ("single".equals(authType)) {
			TokenCache.getInstance().removeToken(username);
		} else {
			TokenCache.getInstance().removeToken(token);
		}
	}
}
