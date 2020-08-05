package com.capitalbio.common.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.capitalbio.common.cache.SmsCache;
import com.capitalbio.common.dao.SmsDAO;
import com.google.common.collect.Maps;

@Service
public class SmsService {
	Logger logger = LoggerFactory.getLogger(getClass());
	@Value("${sms.url}")
	private String url;
	@Value("${sms.username}")
	private String smsuser;
	@Value("${sms.password}")
	private String smspwd;
	@Value("${sms.url}")
	private String content;

	@Autowired private SmsDAO smsDAO;
	public String getCode(String mobile) {
		String code = SmsCache.getInstance().getMessage(mobile);
		return code;
	}
	
	public void saveResult(Map<String,Object> resultMap) {
		smsDAO.saveData("smslog", resultMap);
	}
	
	public void saveCode(String code, String mobile, String sendType, String apikey, String ip) {
		SmsCache.getInstance().putMessage(mobile, code);
		Map<String,Object> map = Maps.newHashMap();
		map.put("code", code);
		map.put("mobile", mobile);
		map.put("sendType", sendType);
		map.put("apikey", apikey);
		map.put("ip", ip);
		smsDAO.saveData("smslog", map);
	}


	public void saveCode(String code, String mobile, String content, String ip) {
		SmsCache.getInstance().putMessage(mobile, code);
		Map<String,Object> map = Maps.newHashMap();
		map.put("code", code);
		map.put("mobile", mobile);
		map.put("content", content);
//		map.put("apikey", apikey);
		map.put("ip", ip);
		smsDAO.saveData("smslog", map);
	}

	public Map<String,Object> sendSms(String mobile, String content) {
		Map<String,Object> resultMap = new HashMap<>();
		try {
			logger.debug("----rest----sendSMS-----"+content+"-----"+mobile);
			HttpClient client1 = new HttpClient(); 
			PostMethod method = new PostMethod(url); 
			client1.getParams().setContentCharset("UTF-8");
			method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");

			NameValuePair[] data = {//提交短信
				    new NameValuePair("account", smsuser), 
				    new NameValuePair("password", smspwd),
				    new NameValuePair("mobile", mobile), 
				    new NameValuePair("content", content),
			};
			method.setRequestBody(data);		
			
			client1.executeMethod(method);	
			String SubmitResult =method.getResponseBodyAsString();
			Document doc = DocumentHelper.parseText(SubmitResult); 
			Element root = doc.getRootElement();

			String retcode = root.elementText("code");	
			String msg = root.elementText("msg");	
			String smsid = root.elementText("smsid");	
			
			logger.debug("----rest----sendSMS result-----"+retcode+"------"+msg+"-----"+smsid);
			resultMap.put("code", retcode);
			resultMap.put("msg", msg);
			resultMap.put("code", retcode);
		} catch (Exception e) {
			logger.debug("短信发送错误！",e);
			e.printStackTrace();
			resultMap.put("code", "0");
			resultMap.put("msg", "短信发送错误");
		}
		return resultMap;
	}
}
