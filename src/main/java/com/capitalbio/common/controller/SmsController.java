package com.capitalbio.common.controller;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.cache.SmsCache;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.service.SmsService;
import com.capitalbio.common.util.IPUtil;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.common.util.RandomUtil;

@Controller
@RequestMapping("sms")
public class SmsController {
	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired SmsService smsService;
	@RequestMapping(value="mocksms")
	@ResponseBody
	public Message mockSMS(HttpServletRequest request) throws Exception{
		String mobile = request.getParameter("telephone");
		System.out.println("mobile:"+mobile);
//		String sendType = request.getParameter("sendType"); 
		if (StringUtils.isEmpty(mobile)) {
			return Message.error(Message.MSG_PARAM_NULL, "手机号不能为空");
		}

		String code = "";
//		if ("register".equals(sendType)) {
			code = "000000";
//		} else if ("resetpwd".equals(sendType)) {
//			code = "111111";
//		}
		
		SmsCache.getInstance().putMessage(mobile, code);
		return Message.success();
	}
	

//	@RequestMapping(value="sms")
//	@ResponseBody
//	public Message sendSMS(HttpServletRequest request) throws Exception{
//		String ip=IPUtil.getRemortIP(request);
//		String mobile = request.getParameter("telephone");
//		if (StringUtils.isEmpty(mobile)) {
//			return Message.error(Message.MSG_PARAM_NULL, "手机号不能为空");
//		}
//		
//		String url = PropertyUtils.getProperty("sms.url");
//		String smsuser = PropertyUtils.getProperty("sms.username");
//		String smspwd = PropertyUtils.getProperty("sms.pwd");
//		String contentTemplate = PropertyUtils.getProperty("sms.content");
//		
//		String code = RandomUtil.getRandomSix();
//		
//		String content = MessageFormat.format(contentTemplate, code);
//		
//		boolean success = smsService.sendSms(url, smsuser, smspwd, mobile, content,code, ip);
//		if (success) {
//			return Message.success();
//		} 
//		return Message.error(Message.MSG_SMS_SENDERROR, "短消息发送错误");
//	}

}
