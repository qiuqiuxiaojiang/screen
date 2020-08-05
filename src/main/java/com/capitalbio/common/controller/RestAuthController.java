package com.capitalbio.common.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.cache.SmsCache;
import com.capitalbio.common.dao.UserDAO;
import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.ReqMessage;
import com.capitalbio.common.service.SmsService;
import com.capitalbio.common.service.SubmitClientService;
import com.capitalbio.common.util.ImageUtil;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.common.util.MD5Util;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/auth")
public class RestAuthController {
	Logger logger = Logger.getLogger(RestAuthController.class);
	@Autowired private SubmitClientService authService;
	@Autowired private UserDAO userDAO;
	@Autowired private SmsService smsService;
	
	@RequestMapping(value="/user")
	@ResponseBody
	@ControllerLog
	public Message addUser(@RequestBody ReqMessage req) {
		String username = req.getUsername();
		String apikey = req.getApikey();
		String data = req.getData();
		Map<String,Object> appClient = authService.getClientByApikey(apikey);
		if (appClient==null) {
			return Message.error(Message.MSG_APIKEY_NOTFOUND,"未找到APIKEY");
		}
		logger.debug("----rest----update--- username: "+username+"; apikey: " + apikey);
		Map<String,Object> userMap = JsonUtil.jsonToMap(data);
		String mobile = (String)userMap.get("mobile");
		String password = (String)userMap.get("password");
		String code = (String)userMap.remove("code");
		if (StringUtils.isEmpty(mobile)) {
			return Message.error(Message.MSG_PARAM_NULL, "手机号码不能为空");
		}
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(code)) {
			return Message.error(Message.MSG_PARAM_NULL, "缺少参数");
		}
		String verifyCode = SmsCache.getInstance().getMessage(mobile);
		if (verifyCode == null || !verifyCode.equals(code)) {
			return Message.error(Message.MSG_SMS_CODEERROR,"验证码错误!");
		}
		Map<String,Object> findUser = userDAO.getUser(username);
		if (findUser != null) {
			return Message.error(Message.MSG_AUTH_USEREXIST,"用户名已存在，不能重复注册");
		}
		Map<String,Object> role = userDAO.getRole("ROLE_USER");
		List<Map<String,Object>> roles = Lists.newArrayList();
		roles.add(role);
		userMap.put("roles", roles);
		userMap.put("username", username);
		String	id = userDAO.saveData("user",userMap);
		return Message.data(id);
	}
	
	@RequestMapping(value="/userinfo")
	@ResponseBody
	@ControllerLog
	public Message getUser(@RequestBody ReqMessage req) {
		String username = req.getUsername();
		String apikey = req.getApikey();
		String token = req.getToken();
		Map<String,Object> appClient = authService.getClientByApikey(apikey);
		if (appClient==null) {
			return Message.error(Message.MSG_APIKEY_NOTFOUND,"未找到APIKEY");
		}
		Message msg = AuthUtil.checkAuth(username, token);
		if (msg != null) {
			return msg;
		}

		Map<String,Object> result = userDAO.getUser(username);
		if (result == null) {
			result = userDAO.getUserByMobile(username);
		}
		if (result == null) {
			return Message.error(Message.MSG_AUTH_USERNOTFOUND, "用户未找到");
		}
		result.remove("password");
		JSONObject jo = new JSONObject(result);
		logger.debug("----rest----getUser---"+jo);
		return Message.data(jo.toString());
	}

	
	@RequestMapping(value="/update")
	@ResponseBody
	@ControllerLog
	public Message updateUser(@RequestBody ReqMessage req) {
		String username = req.getUsername();
		String apikey = req.getApikey();
		String token = req.getToken();
		String data = req.getData();
		Map<String,Object> appClient = authService.getClientByApikey(apikey);
		if (appClient==null) {
			return Message.error(Message.MSG_APIKEY_NOTFOUND,"未找到APIKEY");
		}
		Message msg = AuthUtil.checkAuth(username, token);
		if (msg != null) {
			return msg;
		}
		logger.debug("----rest----update--- username: "+username+"; apikey: " + apikey + "; token="+token);
		Map<String,Object> userMap = JsonUtil.jsonToMap(data);
		Map<String, Object> user = userDAO.getUser(username);
		if (user == null) {
			return Message.error(Message.MSG_AUTH_USERNOTFOUND, "没有找到用户");
		}
		
		user.putAll(userMap);
		userDAO.saveData("user", user);
		return Message.success();
	}
	
	@RequestMapping(value="/pwd/update")
	@ResponseBody
	@ControllerLog
	public Message updatePwd(@RequestBody ReqMessage req) {
		String username = req.getUsername();
		String apikey = req.getApikey();
		String token = req.getToken();
		logger.debug("----rest----update password---, username: "+username+"; apikey: " + apikey + "; token="+token);
		
		Map<String,Object> appClient = authService.getClientByApikey(apikey);
		if (appClient==null) {
			return Message.error(Message.MSG_APIKEY_NOTFOUND,"未找到APIKEY");
		}
		Message msg = AuthUtil.checkAuth(username, token);
		if (msg != null) {
			return msg;
		}
		
		String newPwd = req.getParameter("newPwd");
		String oldPwd = req.getParameter("oldPwd");
		Map<String, Object> user = userDAO.getUser(username);
		String password = (String)user.get("password");
		if (!password.equals(oldPwd)) {
			return Message.error(Message.MSG_AUTH_PWDNOTMATCH,"用户密码错误");
			
		}
		user.put("password", newPwd);
			userDAO.saveData("user", user);
		return Message.success();
	}
	
	@RequestMapping(value="/pwd/reset")
	@ResponseBody
	@ControllerLog
	public Message resetPwd(@RequestBody ReqMessage req) {
//		String username = req.getUsername();
		String apikey = req.getApikey();
		String phoneNo=req.getParameter("mobile");
		String code=req.getParameter("code");
		String newPwd = req.getParameter("newPwd");
		
		logger.debug("----rest----reset password---, mobile: "+phoneNo+"; apikey: " + apikey);
		
		Map<String,Object> appClient = authService.getClientByApikey(apikey);
		if (appClient==null) {
			return Message.error(Message.MSG_APIKEY_NOTFOUND,"未找到APIKEY");
		}
		if (StringUtils.isEmpty(phoneNo)) {
			return Message.error(Message.MSG_PARAM_NULL, "手机号码不能为空");
		}
		Map<String,Object> u = userDAO.getUserByMobile(phoneNo);
//		Map<String,Object> u=userDAO.getUser(username);
//		if(u==null){
//			return Message.error(Message.MSG_AUTH_USERNOTFOUND,"当前用户不存在！");
//		}
//		String mobile=(String)u.get("mobile");
//		if (!phoneNo.equals(mobile)) {
//			return Message.error(Message.MSG_SMS_MOBILENOTMATCH,"用户手机号码不匹配!");
//		}
		
		if (u == null) {
			return Message.error(Message.MSG_AUTH_USERNOTFOUND, "手机号码对应的用户不存在");
		}
		
		String verifyCode = smsService.getCode(phoneNo);
		if (verifyCode == null || !verifyCode.equals(code)) {
			return Message.error(Message.MSG_SMS_CODEERROR,"验证码错误!");
		}
		
		u.put("password", newPwd);
		userDAO.saveData("user", u);
		return Message.success();
	}
	
	@RequestMapping(value="/login")
	@ResponseBody
	public Message login(@RequestBody ReqMessage req) {
		String username = req.getUsername();
		String apikey = req.getApikey();
		String token = req.getToken();
		String mobileId = req.getParameter("mobileId");
		logger.debug("----rest----user login, username: "+username+"; token: " + token + "; apikey="+apikey+"; mobileId="+mobileId);
		Map<String,Object> appClient = authService.getClientByApikey(apikey);
		if (appClient==null) {
			return Message.error(Message.MSG_APIKEY_NOTFOUND,"未找到apikey!");
		}

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
		String matchToken = MD5Util.MD5Encode(apikey+password+mobileId);
		if (matchToken.equals(token)) {
			u.put("apikey", apikey);
//			u.remove("questionnaire");
			
			AuthUtil.putToken(username, token);
			
			String jsonData = JsonUtil.mapToJson(u);
			logger.debug("----rest----user login, true--"+jsonData);
			return Message.data(jsonData);
		}
		logger.debug("----rest----user login, false--");
		return Message.error(Message.MSG_AUTH_PWDNOTMATCH,"密码不正确!");
	}
	
	/**
	 * 验证用户名密码是否正确，但是不进行登录
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/checkuser")
	@ResponseBody
	public Message checkUser(@RequestBody ReqMessage req) {
		String username = req.getUsername();
		String apikey = req.getApikey();
		String token = req.getToken();
		String mobileId = req.getParameter("mobileId");
		logger.debug("----rest----check user, username: "+username+"; token: " + token + "; apikey="+apikey+"; mobileId="+mobileId);
		Map<String,Object> appClient = authService.getClientByApikey(apikey);
		if (appClient==null) {
			return Message.error(Message.MSG_APIKEY_NOTFOUND,"未找到apikey!");
		}

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
		String matchToken = MD5Util.MD5Encode(apikey+password+mobileId);
		if (matchToken.equals(token)) {
			u.put("apikey", apikey);
			u.remove("questionnaire");
			
//			AuthUtil.putToken(username, token);
			
			String jsonData = JsonUtil.mapToJson(u);
			logger.debug("----rest----check user, true--"+jsonData);
			return Message.data(jsonData);
		}
		logger.debug("----rest---- check user, false--");
		return Message.error(Message.MSG_AUTH_PWDNOTMATCH,"密码不正确!");
	}

	@RequestMapping(value="/logout")
	@ResponseBody
	public Message logout(@RequestBody ReqMessage req) {
		String username = req.getUsername();
		String apikey = req.getApikey();
		String token = req.getToken();
		logger.debug("----rest----user logout, username: "+username+"; token: " + token + "; apikey="+apikey);
		
		Map<String,Object> appClient = authService.getClientByApikey(apikey);
		if (appClient==null) {
			return Message.error(Message.MSG_APIKEY_NOTFOUND,"未找到apikey!");
		}
		Message msg = AuthUtil.checkAuth(username, token);
		if (msg != null) {
			return msg;
		}
		AuthUtil.removeToken(username, token);
		return Message.success();
		
	}
	
	/*
	 * 用户上传头像
	 */
	@RequestMapping(value="/upfile",method=RequestMethod.PUT)
	@ResponseBody
	public Message uploadFile(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String username = request.getParameter("username");
		String token = request.getParameter("token");
		logger.debug("----rest----upload image ----"+token+"----"+username);
		Message msg = AuthUtil.checkAuth(username, token);
		if (msg != null) {
			return msg;
		}
		
		Map<String,Object> user = userDAO.getUser(username);

		byte[] image = ImageUtil.thumbnailImage(request.getInputStream(), 100, 100, false);
		user.put("headImage", image);
		userDAO.saveData("user", user);
		return Message.success();
	}
	
	
	
	@RequestMapping(value="/user/check")
	@ResponseBody
	public Message checkUsername(@RequestBody ReqMessage req) {
		String apikey = req.getApikey();
		String username = req.getUsername();
		Map<String,Object> appClient = authService.getClientByApikey(apikey);
		if (appClient==null) {
			return Message.error(Message.MSG_APIKEY_NOTFOUND, "未找到apikey!");
		}
		Map<String,Object> user = userDAO.getUser(username);
		if (user == null) {
			return Message.data("false");
		}
		return Message.data("true");
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(MD5Util.MD5Encode("bioeh123"));
	}

}
