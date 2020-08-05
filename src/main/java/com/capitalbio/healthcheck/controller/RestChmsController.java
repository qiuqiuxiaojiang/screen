package com.capitalbio.healthcheck.controller;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.capitalbio.auth.service.AuthService;
import com.capitalbio.auth.util.JwtUtil;
import com.capitalbio.common.exception.BaseException;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.util.CommUtil;
import com.capitalbio.common.util.DateUtils;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.common.util.message.MessageUtil;
import com.capitalbio.common.util.message.Messages;
import com.capitalbio.common.util.redis.DataDictUtil;
import com.capitalbio.healthcheck.service.CustomerService;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;

@Controller
@RequestMapping("rest")
@Component
public class RestChmsController {
	
	@Autowired
	private AuthService authService;
	@Autowired
	private CustomerService customerService;

	JedisTemplate template = RedisUtils.getTemplate();
	
	/**
	 * 获取token和userid
	 * @param request
	 * @return
	 * @throws BaseException
	 */
	/*@RequestMapping(value = "/ha/getTokenAndUserid", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	@ResponseBody
	public Messages getTokenAndUserid(HttpServletRequest request) throws BaseException {
		 try {
			 
			 String webSignature = request.getParameter("webSignature");
			  JSONObject resultJson = authService.applyTokenChms();
			  System.out.println("resultJson:" + resultJson);
			  
			  if (resultJson == null || StringUtils.isEmpty(resultJson.toString())) {
				  System.out.println("访问认证系统异常");
					return null;
			  }
			  
			  int code = resultJson.getInteger("code");
			  if (code == 0) {
					*//** 成功 **//*
					*//** 认证系统校验成功 **//*
					JSONObject data = resultJson.getJSONObject("data");
					template.setex("token" + data.getString("token"), data.getString("token"), Integer.parseInt(CommUtil.getConfigByString("redis.expire")));
					template.setex("userid" + data.getString("userId"), data.getString("userId"), Integer.parseInt(CommUtil.getConfigByString("redis.expire")));
					
					Messages message = MessageUtil.getSuccessMessage("Get_Success_Msg", data);
					return message;
			  } else {
					System.out.println("msg:" + resultJson.getString("msg"));
			  }
			  
		  } catch (Exception e) {
			  e.printStackTrace();
			  throw new BaseException("获取认证签名异常");
		  }
		 return null;
	}*/
	
	
    /**
     * 根据Web签名申请token
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "/ha/getTokenAndUserid", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public Messages getTokenBySignature(HttpServletRequest request) {
            // log.info("[REQUEST] " + requestParameterToJson(request));
            String webSignature = request.getParameter("webSignature");
            System.out.println("====================webSignature===========" + webSignature);
            if (StringUtils.isBlank(webSignature)) {
                    //logger.info("[RESPONSE] " + JSONObject.toJSONString(MessageUtil.getErrorDebugInfoMessage("Parameter_Err")));
                    return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
            }
            Messages msg=null;
            try {
                    String jwtSubject = JwtUtil.getJwtSubject(webSignature);
                    System.out.println("====================jwtSubject===========" + jwtSubject);
                    JSONObject resultJSON = new JSONObject();
                    /**使用UUID生成jwtSubject虚拟用户标识符*/
                String jwtSubjectUserID = UUID.randomUUID().toString();
                    String token = LoginSignatureToken(jwtSubjectUserID);
                    resultJSON.put("token",token);
                    resultJSON.put("userId",jwtSubjectUserID);
                    
                    template.setex("token" + token, token, Integer.parseInt(CommUtil.getConfigByString("redis.expire")));
					template.setex("userid" + jwtSubjectUserID, jwtSubjectUserID, Integer.parseInt(CommUtil.getConfigByString("redis.expire")));

                    //logger.info("[Web签名] :" + jwtSubject);
                    msg = MessageUtil.getSuccessMessage("Get_webSignature_Success_Msg",resultJSON);
            } catch (Exception e) {
                    msg = MessageUtil.getErrorMessage("Invalid_webSignature_Msg");
                    e.printStackTrace();
                    
            }
            //logger.info("[RESPONSE] " + JSONObject.toJSONString(msg));
            return msg;
    }
	
	
	/**
	 * 加载用户基本信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/ha/getBasicInfo", 
					method = RequestMethod.GET, 
					produces = "application/json;charset=utf-8")
	public Messages  getBasicInfo(HttpServletRequest request) {
		try {
			String uniqueId = request.getParameter("uniqueId");
			
			JSONObject basicInfo = customerService.loadBasicInfo(uniqueId);
			/** 成功 **/
			Messages message = MessageUtil.getSuccessMessage("", basicInfo);
			return message;
		} catch (BaseException e) {
			return MessageUtil.getErrorMessage("Get_Failure_Msg");
		}
	}
	
	
	/**
	 * 加载信息调查
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/ha/getInfoInquiry", 
					method = RequestMethod.GET, 
					produces = "application/json;charset=utf-8")
	public Messages  getInfoInquiry(HttpServletRequest request) {
		try {
			String uniqueId = request.getParameter("uniqueId");
			
			JSONObject basicInfo = customerService.loadInfoInquiry(uniqueId);
			/** 成功 **/
			Messages message = MessageUtil.getSuccessMessage("", basicInfo);
			return message;
		} catch (BaseException e) {
			System.out.println("获取基本信息异常");
			return MessageUtil.getErrorMessage("Get_Failure_Msg");
		}
	}
	
	@RequestMapping(value = "/ha/getScreenReportList", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	@ResponseBody
	public Messages getScreenReportList(HttpServletRequest request) {
		  try {
			  String uniqueId = request.getParameter("uniqueId");
			  if (StringUtils.isEmpty(uniqueId) || StringUtils.isEmpty(uniqueId)) {
				  return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
			  }
			  JSONObject json = customerService.getScreenReportList(uniqueId);
			  // 无需返回消息msg
			  return MessageUtil.getSuccessMessage("Get_Success_Msg", json);
		  } catch (BaseException e) {
			  System.out.println("获取筛查报告信息列表异常");
			  return MessageUtil.getErrorMessage("Get_Failure_Msg", e.getDebugInfo());
		  }
	 }
	
	
	 /**
	   * 根据记录id获取筛查报告信息
	   * 
	   * @param request
	   * @return
	   */
	  @RequestMapping(value = "/ha/getScreenReport", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	  @ResponseBody
	  public Messages getScreenReport(HttpServletRequest request) {
		  try {
			  String recordId = request.getParameter("recordId");
			  System.out.println( "recordId:" + recordId);
			  if (StringUtils.isEmpty(recordId)) {
				  return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
			  }
			  JSONObject json = customerService.getScreenReport(recordId);
			  // 无需返回消息msg
			  return MessageUtil.getSuccessMessage("Get_Success_Msg", json);
		  } catch (BaseException e) {
			  System.out.println("根据记录id获取筛查报告信息异常");
			  return MessageUtil.getErrorMessage("Get_Failure_Msg", e.getDebugInfo());
		  }
	 }
	  
	  
	  /**
	   * 根据记录id获取体格检测信息
	   * 
	   * @param request
	   * @return
	   */
	  @RequestMapping(value = "/ha/getPhysicalEamination", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	  @ResponseBody
	  public Messages getPhysicalEamination(HttpServletRequest request) {
	    try {
	      String recordId = request.getParameter("recordId");
	      System.out.println("recordId:" + recordId);
	      if (StringUtils.isEmpty(recordId)) {
	    	  return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
	      }
	      JSONObject json = customerService.getPhysicalEamination(recordId);
	      // 无需返回消息msg
	      return MessageUtil.getSuccessMessage("Get_Success_Msg", json);
	    } catch (BaseException e) {
	      System.out.println("获取体格检测信息列表异常");
	      return MessageUtil.getErrorMessage("Get_Failure_Msg", e.getDebugInfo());
	    }
	  }
	  
	  /**
	   * 获取用户基本信息
	   * 
	   * @param request
	   * @return
	   */
	  @RequestMapping(value = "/ha/getBasicInfoByUniqueid", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	  @ResponseBody
	  public Messages getBasicInfoByUniqueid(HttpServletRequest request) {
		  try {
			  String uniqueId = request.getParameter("uniqueId");
			  System.out.println("uniqueId:" + uniqueId);
			  if (StringUtils.isEmpty(uniqueId)) {
				  return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
			  }
		      JSONObject json = customerService.getBasicInfoByUniqueid(uniqueId);
		      // 无需返回消息msg
		      return MessageUtil.getSuccessMessage("Get_Success_Msg", json);
		   } catch (BaseException e) {
		      System.out.println("获取用户基本信息异常");
		      return MessageUtil.getErrorMessage("Get_Failure_Msg", e.getDebugInfo());
		   }
	  }
	  
	  /**
	   * 获取筛查结果信息
	   * 
	   * @param request
	   * @return
	   */
	  @RequestMapping(value = "/ha/getClassifyResult", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	  @ResponseBody
	  public Messages getClassifyResult(HttpServletRequest request) {
		  try {
			  String uniqueId = request.getParameter("uniqueId");
			  System.out.println(":" + uniqueId);
			  if (StringUtils.isEmpty(uniqueId)) {
				  return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
			  }
			  
			  JSONObject json = customerService.getClassifyResult(uniqueId);
			  // 无需返回消息msg
			  return MessageUtil.getSuccessMessage("Get_Success_Msg", json);
		  } catch (BaseException e) {
			  System.out.println("获取筛查结果信息异常");
			  return MessageUtil.getErrorMessage("Get_Failure_Msg", e.getDebugInfo());
		  }
	  }
	  
	  public String LoginSignatureToken(String userId) {
	        // 登录完成以后放入redis中相关信息-
	        String pkId = DataDictUtil.getRedisBucket() + PropertyUtils.getProperty("app.login.key") +":user:"+userId;// key
	        if (template.exists(pkId)) {// 已存在，再次登录先清除redis中的key
	          template.del(pkId);
	        }
	        // token
	        String tokenApp = UUID.randomUUID().toString();
	        template.hset(pkId, "token", tokenApp);
	        // 当前登录时间
	        template.hset(pkId, "loginTime", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
	        // 设置key的过期时间60*24*60*60*1000 ,在配置文件中可修改
	        template.expire(pkId, NumberUtils.toInt(PropertyUtils.getProperty("app.signature.login.expiration.time")));
	        return tokenApp;
	  }
	  
	  /**
	   * 获取体质信息
	   * 
	   * @param request
	   * @return
	   */
	  @RequestMapping(value = "/ha/getTizhiInfo", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	  @ResponseBody
	  public Messages getTizhiInfo(HttpServletRequest request) {
		  try {
			    String uniqueId = request.getParameter("uniqueId");
				String pageNo = request.getParameter("pageNum");
				String pageSize = request.getParameter("pageSize");
				System.out.println("===================uniqueId==================" + uniqueId);
				if(uniqueId==null || StringUtils.isBlank(uniqueId)
						||pageNo==null || StringUtils.isBlank(pageNo)
						||pageSize==null || StringUtils.isBlank(pageSize)){
					return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
				}
				
				JSONObject json = customerService.getTizhiInfo(uniqueId, Integer.parseInt(pageNo), Integer.parseInt(pageSize));
				System.out.println("===============json============" + json);
				return MessageUtil.getSuccessMessage("Get_Success_Msg", json);
		  } catch (BaseException e) {
			  System.out.println("获取体质信息异常");
			  return MessageUtil.getErrorMessage("Get_Failure_Msg", e.getDebugInfo());
		  }
		   
	  }
}
