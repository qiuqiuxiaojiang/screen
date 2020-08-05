package com.capitalbio.common.controller;

import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.cache.SmsCache;
import com.capitalbio.common.dao.UserDAO;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.ReqMessage;
import com.capitalbio.common.service.SmsService;
import com.capitalbio.common.service.SubmitClientService;
import com.capitalbio.common.util.IPUtil;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.common.util.RandomUtil;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/message")
public class RestMessage {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired private SubmitClientService authService;
	@Autowired private UserDAO userDAO;
	@Autowired private SmsService smsService;
	
	@RequestMapping(value="/mocksms")
	@ResponseBody
	public Message mockSMS(@RequestBody ReqMessage req, HttpServletRequest request) throws Exception{
		logger.debug("----rest----mockSMS-----");
		String apikey = req.getApikey();
		Map<String,Object> appClient = authService.getClientByApikey(apikey);
		if (appClient==null) {
			return Message.error(Message.MSG_APIKEY_NOTFOUND,"未找到apikey!");
		}
		String mobile = req.getParameter("mobile");
		String sendType = req.getParameter("sendType");
//		String username = req.getUsername();
		if (StringUtils.isEmpty(mobile)) {
			return Message.error(Message.MSG_PARAM_NULL, "手机号不能为空");
		}

		String code = "";
		if ("register".equals(sendType)) {
			code = "000000";
		} else if ("resetpwd".equals(sendType)) {
			code = "111111";
		}
		
		SmsCache.getInstance().putMessage(mobile, code);
		return Message.success();
	}
	

	@RequestMapping(value="/sms")
	@ResponseBody
	public Message sendSMS(@RequestBody ReqMessage req, HttpServletRequest request) throws Exception{
		logger.debug("----rest----sendSMS-----");
		String apikey = req.getApikey();
		Map<String,Object> appClient = authService.getClientByApikey(apikey);
		if (appClient==null) {
			return Message.error(Message.MSG_APIKEY_NOTFOUND,"未找到apikey!");
		}
		String ip=IPUtil.getRemortIP(request);
		String mobile = req.getParameter("mobile");
		String sendType = req.getParameter("sendType");
//		String username = req.getUsername();
		if (StringUtils.isEmpty(mobile)) {
			return Message.error(Message.MSG_PARAM_NULL, "手机号不能为空");
		}
		Map<String,Object> params = Maps.newHashMap();
		params.put("mobile", mobile);
		Map<String,Object> user = userDAO.queryOne("user", params);
		
		if ("register".equals(sendType)) {
			if (user != null) {
				return Message.error(Message.MSG_SMS_MOBILEREGISTERED, "手机号已经注册过");
			}
		} else if ("resetpwd".equals(sendType)) {
			if (user == null) { 
				return Message.error(Message.MSG_AUTH_USERNOTFOUND, "没有找到对应的用户");
			}
//			if (!((String)user.get("username")).equals(username)) {
//				return Message.error(Message.MSG_SMS_MOBILENOTMATCH, "手机号与用户注册手机不匹配");
//			}
		}
		
		
		String url = PropertyUtils.getProperty("sms.url");
		String smsuser = PropertyUtils.getProperty("sms.username");
		String smspwd = PropertyUtils.getProperty("sms.pwd");
		String contentTemplate = PropertyUtils.getProperty("sms.content");
		
		String code = RandomUtil.getRandomSix();
		
		String content = MessageFormat.format(contentTemplate, code);
		
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
			if(retcode.equals("2")){
				smsService.saveCode(code, mobile, sendType, apikey, ip);
				return Message.success();
			}
			return Message.error(Message.MSG_SMS_SENDERROR,"短信发送错误");
		} catch (Exception e) {
			return Message.error(Message.MSG_SMS_SENDERROR,"短信发送错误");
		}
	}

}
