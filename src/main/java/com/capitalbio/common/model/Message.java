package com.capitalbio.common.model;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;

/**
 * 返回消息
 * 
 * @author wdong
 *
 */
@XmlRootElement
public class Message {
	//错误码，0:成功;1:错误
	public int code = 0;
	public final static int MSG_SUCCESS = 0;
	public final static int MSG_APP_NOTFOUND = 1;
	public final static int MSG_APP_VERIFYERROR = 2;
	public final static int MSG_APIKEY_DUPLICATED = 3;
	public final static int MSG_APIKEY_NOTFOUND = 4;
	public final static int MSG_APIKEY_NOTVALID = 5;
	public final static int MSG_AUTH_USERNOTFOUND = 10;
	public final static int MSG_AUTH_NOTLOGIN = 11;
	public final static int MSG_AUTH_PWDNOTMATCH = 13;
	public final static int MSG_AUTH_USEREXIST = 15;
	public final static int MSG_AUTH_REQUESTERROR = 16;
	public final static int MSG_AUTH_TOKENERROR = 17;
	public final static int MSG_VERSION_NODATA = 20;
	public final static int MSG_SMS_MOBILEREGISTERED = 30;
	public final static int MSG_SMS_MOBILENOTMATCH = 31;
	public final static int MSG_SMS_CODEERROR = 32;
	public final static int MSG_SMS_SENDERROR = 33;
	public final static int MSG_MONITOR_USERBINDED = 101;
	public final static int MSG_MONITOR_DEVICEBINDED = 102;
	public final static int MSG_MONITOR_DEVICEUNBIND = 103;
	public final static int MSG_MONITOR_NODATA = 104;
	public final static int MSG_PARAM_NULL = 400;
	public final static int MSG_DATA_NOTFOUND = 401;
	public final static int MSG_DATA_TOOLARGE = 402;
	public final static int MSG_DATA_ERROR = 403;

	//消息
	public String message = "success";
	//显示消息
	public String showMessage;
	//返回数据
	public String data;
	public Map<String,Object> dataMap;
	public List<Map<String,Object>> dataList;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getShowMessage() {
		return showMessage;
	}
	public void setShowMessage(String showMessage) {
		this.showMessage = showMessage;
	}
	
	public static Message error(String message) {
		Message m = new Message();
		m.setCode(1);
		m.setMessage(message);
		return m;
	}
	public static Message error(int errCode, String message) {
		Message m = new Message();
		m.setCode(errCode);
		m.setMessage(message);
		return m;
	}
	public static Map<String,Object> errorMap(int errCode, String message) {
		Map<String,Object> map = Maps.newHashMap();
		map.put("code", errCode);
		map.put("message", message);
		return map;
	}
	
	public static Message success() {
		return new Message();
	}
	
	public static Message data(String data) {
		Message m =  new Message();
		m.setData(data);
		return m;
	}
	
	public static Message dataMap(Map<String,Object> dataMap) {
		Message m =  new Message();
		m.setDataMap(dataMap);
		return m;
		
	}
	
	public static Message dataList(List<Map<String,Object>> dataList) {
		Message m =  new Message();
		m.setDataList(dataList);
		return m;
		
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}
	public List<Map<String, Object>> getDataList() {
		return dataList;
	}
	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}
	
	
}
