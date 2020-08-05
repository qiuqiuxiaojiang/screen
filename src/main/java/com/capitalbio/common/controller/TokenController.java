package com.capitalbio.common.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.auth.util.Constant;
import com.capitalbio.auth.util.JwtUtil;
import com.capitalbio.common.dao.UserDAO;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.ReqMessage;
import com.capitalbio.common.util.MD5Util;

@Controller
@RequestMapping("/token")
public class TokenController {
	Logger logger = Logger.getLogger(TokenController.class);
	@Autowired private UserDAO userDAO;

	@RequestMapping(value="/request")
	@ResponseBody
	public Message requestToken(@RequestBody ReqMessage req) {
		String apikey = req.getApikey();
		String token = req.getToken();
		String sysTime = req.getParameter("sysTime");
		logger.debug("----rest----request token, apikey: "+apikey+"; token: " + token + "; sysTime="+sysTime);

		String matchToken = MD5Util.MD5Encode(apikey+Constant.SECRET_KEY+sysTime);
		
		if (matchToken.equals(token)) {
			String newToken = JwtUtil.createJWT(Constant.JWT_ID, apikey, Constant.JWT_TTL);
			return Message.data(newToken);
		}
		logger.debug("----rest----user login, false--");
		return Message.error(Message.MSG_AUTH_TOKENERROR,"Token不正确!");
	}

	@RequestMapping(value="/validate")
	@ResponseBody
	public Message validateToken(@RequestBody ReqMessage req) {
		String token = req.getToken();
		logger.debug("----rest----validate token, token: " + token );

		String subject = JwtUtil.checkRequest(token);
		if (subject == null) {
			return Message.error(Message.MSG_AUTH_TOKENERROR, "Token不正确");
		}
		return Message.success();
	}

	@RequestMapping(value="/login")
	@ResponseBody
	public Message login(@RequestBody ReqMessage req) {
		String username = req.getUsername();
//		String password = req.getParameter("password");
//		String apikey = req.getApikey();
		String token = req.getToken();
		String mobileId = req.getParameter("mobileId");
		logger.debug("----rest----user login, username: "+username+"; token: " + token + "; mobileId="+mobileId);
//		Map<String,Object> appClient = authService.getClientByApikey(apikey);
//		if (appClient==null) {
//			return Message.error(Message.MSG_APIKEY_NOTFOUND,"未找到apikey!");
//		}

		Map<String,Object> u=userDAO.getUser(username);
		if(u==null){
			u = userDAO.getUserByMobile(username);
		}
		if (u == null) {
			return Message.error(Message.MSG_AUTH_USERNOTFOUND,"当前用户不存在！");
		}
		
		if (StringUtils.isEmpty(mobileId)) {
			return Message.error(Message.MSG_PARAM_NULL, "手机唯一标识不能为空");
		}

		String password = (String)u.get("password");
		String matchToken = MD5Util.MD5Encode(Constant.SECRET_KEY+password+mobileId);
		
//		String subject = JwtUtil.generalSubject(username, mobileId);
		String newToken = JwtUtil.createJWT(Constant.JWT_ID, username, Constant.JWT_TTL);
		if (matchToken.equals(token)) {
			AuthUtil.putToken(username, token);
			u.put("token", newToken);
//			String jsonData = JsonUtil.mapToJson(u);
//			logger.debug("----rest----user login, true--"+jsonData);
			return Message.data(newToken);
		}
		logger.debug("----rest----user login, false--");
		return Message.error(Message.MSG_AUTH_PWDNOTMATCH,"密码不正确!");
	}

	@RequestMapping(value="/auth")
	@ResponseBody
	public Message auth(@RequestBody ReqMessage req) {
		String username = req.getUsername();
		String token = req.getToken();
		logger.debug("----rest----token auth, username: "+username+"; token: " + token );
		boolean valid = JwtUtil.valid(token, username);
		if (!valid) {
			return Message.error(Message.MSG_AUTH_TOKENERROR, "token错误");
		}

		return Message.success();
	}
	
}
