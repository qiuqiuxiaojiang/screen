package com.capitalbio.common.model;

import java.util.Map;

import com.capitalbio.common.util.JsonUtil;

public class ReqMessage {
	private String apikey;
	private String token;
	private String username;
	private String data;
	private Map<String,Object> dataMap;
	public String getApikey() {
		return apikey;
	}
	public void setApikey(String apikey) {
		this.apikey = apikey;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getParameter(String key) {
		if (dataMap == null && data != null && data.length() > 0) {
			dataMap = JsonUtil.jsonToMap(data);
		}
		if (dataMap != null) {
			return (String)dataMap.get(key);
		}
		return null;
	}
}
