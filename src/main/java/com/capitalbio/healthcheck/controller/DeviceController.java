package com.capitalbio.healthcheck.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.dao.UserDAO;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.healthcheck.service.DeviceService;


@Controller
@RequestMapping("/deviceApp")
public class DeviceController {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private DeviceService deviceService;
	@Autowired private UserDAO userDao;
	
	@Autowired
	private AuthService authService;
	
	private final static String testDoctorRoleKey = "ROLE_TESTDOCTOR";

	/**
     * 登录app
     * @param request
     */
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    @ResponseBody
    public Message login(HttpServletRequest request){
    	
    	String userName = request.getParameter("userName");
    	String password = request.getParameter("password");
    	System.out.println("/deviceApp/login==rqeust==userName:"+userName+"password:"+password);
    	if(userName==null||userName==""||password==null||password==""){
    		return Message.error("参数不全");
    	}
		try {
			/** 判断是否符合角色 **/
			boolean judgeUserRole = deviceService.judgeUserRole(userName, testDoctorRoleKey);
			if (judgeUserRole) {
				/** 符合应该的角色 **/
				/** 判断帐号密码是否错误 **/
				JSONObject loginMap = authService.login(userName, password);
				String code = loginMap.get("code").toString();
				if (code.equals("1")) {
					return Message.error("帐号密码不匹配");
				}
				Map<String,Object> data = (Map<String,Object>)loginMap.get("data");
				return Message.dataMap(data);
			} else {
				return Message.error("无权限");
			}
		} catch (Exception e) {
			logger.error("login  校验用户信息失败",e);
		}
		return Message.error("帐号密码不匹配");
    }
    
    /**
     * 绑定设备
     * @param request
     */
   @RequestMapping(value = "/bind",method = RequestMethod.POST)
   @ResponseBody
    public Message bind(HttpServletRequest request){
    	
    	String userName = request.getParameter("userName");
    	String deviceName = request.getParameter("deviceName");//设备名称
    	String mac = request.getParameter("mac");//mac地址
    	String userId = request.getParameter("userId");
       	String token = request.getHeader("token");
//    	String token = (String) ContextUtils.getSession().getAttribute("token");
//		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		if (!authService.verifyToken(userId, token)) {
			System.out.println("----healthcheck------token error: token"+token+",userId:"+userId);
//			logger.debug("----healthcheck------token error: token"+token+",userId:"+userId);
			return Message.error(Message.MSG_AUTH_TOKENERROR, "Token错误, 请登录后重试");
		}
    	logger.info("/deviceApp/bind==rqeust==userId:"+userId+"userName:"+userName
    			+"deviceName:"+deviceName+"mac:"+mac);
    	if(userId==null||StringUtils.isEmpty(userId) 
    			|| userName==null||StringUtils.isEmpty(userName) 
    			|| deviceName==null||StringUtils.isEmpty(deviceName) 
    			|| mac==null||StringUtils.isEmpty(mac)){
    		return Message.error("参数不全");
    	}
    	/** 先判断mac地址是否已经绑定 **/
    	boolean judgeMac = deviceService.judgeMac(userName, mac);
    	if (!judgeMac) {
    		boolean bindMac = deviceService.bindMac(userId, userName, deviceName, mac);	
    		if (bindMac) {
    			return Message.success();
    		} else {
    			return Message.error("绑定失败");
    		}
    		
    	} else {
    		return Message.error("该MAC地址已经绑定过设备");
    	}
    }
    
   
   /**
    * 解绑设备
    * @param request
    */
   @RequestMapping(value = "/unbind",method = RequestMethod.POST)
   @ResponseBody
   public Message unbind(HttpServletRequest request){
   	String userId = request.getParameter("userId");
   	String token = request.getHeader("token");
   	String userName = request.getParameter("userName");
   	String deviceName = request.getParameter("deviceName");//设备名称
//   	String token = (String) ContextUtils.getSession().getAttribute("token");
//	String userId = (String) ContextUtils.getSession().getAttribute("userId");
	if (!authService.verifyToken(userId, token)) {
		System.out.println("----healthcheck------token error: token"+token+",userId:"+userId);
//		logger.debug("----healthcheck------token error: token"+token+",userId:"+userId);
		return Message.error(Message.MSG_AUTH_TOKENERROR, "Token错误, 请登录后重试");
	}
   	logger.info("/deviceApp/unbind==rqeust==userId:"+userId+"userName:"+userName
			+"deviceName:"+deviceName);
   	if(userId==null||StringUtils.isEmpty(userId) 
			|| userName==null||StringUtils.isEmpty(userName) 
			|| deviceName==null||StringUtils.isEmpty(deviceName)){
		return Message.error("参数不全");
	}
   	boolean unBindMac = deviceService.unBindMac(userId, userName, deviceName);
   	if (unBindMac) {
   		return Message.success();
   	} else {
   		return Message.error("该用户下无该设备");
   	}
   	
   }
   
   
   /**
    * 设备列表
    * @param request
    */
   @RequestMapping(value = "/bindList",method = RequestMethod.GET)
   @ResponseBody
   public Message bindList(HttpServletRequest request){
		String userId = request.getParameter("userId");
	   	String userName = request.getParameter("userName");
	   	String token = request.getHeader("token");
//	   	String token = (String) ContextUtils.getSession().getAttribute("token");
//		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		if (!authService.verifyToken(userId, token)) {
			System.out.println("----healthcheck------token error: token"+token+",userId:"+userId);
//			logger.debug("----healthcheck------token error: token"+token+",userId:"+userId);
			return Message.error(Message.MSG_AUTH_TOKENERROR, "Token错误, 请登录后重试");
		}
	   	logger.info("/deviceApp/unbind==rqeust==userId:"+userId+"userName:"+userName);
	   	if(userId==null||StringUtils.isEmpty(userId) 
				|| userName==null||StringUtils.isEmpty(userName)){
			return Message.error("参数不全");
		}
	   	return Message.dataList(deviceService.bindList(userName, "1"));
   }
   
   
   
   
    
    
}
