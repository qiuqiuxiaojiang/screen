package com.capitalbio.healthcheck.controller;

import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.capitalbio.auth.util.JwtUtil;
import com.capitalbio.common.exception.BaseException;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.CommUtil;
import com.capitalbio.common.util.DateUtils;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.common.util.message.MessageUtil;
import com.capitalbio.common.util.message.Messages;
import com.capitalbio.common.util.redis.DataDictUtil;
import com.capitalbio.healthcheck.service.HealthControlService;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;

@Controller
@RequestMapping("/rest")
public class RestHealthControl {
	
	@Autowired
	private HealthControlService healthControlService;
	
	@Autowired
	private FileManageService fileManageService;
	
	JedisTemplate template = RedisUtils.getTemplate();
	
	/**
     * 根据Web签名申请token
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "/screen/getTokenAndUserid", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public Messages getTokenBySignature(HttpServletRequest request) {
            String webSignature = request.getParameter("webSignature");
            System.out.println("====================webSignature===========" + webSignature);
            
            if (StringUtils.isBlank(webSignature)) {
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
	 * 获取建档信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/screen/getRecordInfo", 
					method = RequestMethod.GET, 
					produces = "application/json;charset=utf-8")
	public Messages getRecordInfo (HttpServletRequest request) {
		try {
			String uniqueId = request.getParameter("uniqueId");
		    if (StringUtils.isEmpty(uniqueId)) {
			    return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
		    }
			
			JSONObject json = healthControlService.getRecordInfo(uniqueId);
			/** 成功 **/
			return MessageUtil.getSuccessMessage("Get_Success_Msg", json);
		} catch (BaseException e) {
			System.out.println("获取建档信息异常");
			return MessageUtil.getErrorMessage("Get_Failure_Msg");
		}
	}
	
	
	/**
	 * 获取初筛信息列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/screen/getHealthCheckList", 
					method = RequestMethod.GET, 
					produces = "application/json;charset=utf-8")
	public Messages getHealthCheckList (HttpServletRequest request) {
		try {
			String uniqueId = request.getParameter("uniqueId");
		    if (StringUtils.isEmpty(uniqueId)) {
			    return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
		    }
			
			JSONObject recordInfo = healthControlService.getHealthCheckList(uniqueId);
			/** 成功 **/
			return MessageUtil.getSuccessMessage("Get_Success_Msg", recordInfo);
		} catch (BaseException e) {
			System.out.println("获取初筛信息列表异常");
			return MessageUtil.getErrorMessage("Get_Failure_Msg");
		}
	}
	
	
	/**
	 * 获取初筛详情信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/screen/getHealthCheckInfo", 
					method = RequestMethod.GET, 
					produces = "application/json;charset=utf-8")
	public Messages getHealthCheckInfo (HttpServletRequest request) {
		try {
			String healthCheckId = request.getParameter("healthCheckId");
		    if (StringUtils.isEmpty(healthCheckId)) {
			    return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
		    }
			
			JSONObject json = healthControlService.getHealthCheckInfo(healthCheckId);
			/** 成功 **/
			return MessageUtil.getSuccessMessage("Get_Success_Msg", json);
		} catch (BaseException e) {
			System.out.println("获取初筛信息异常");
			return MessageUtil.getErrorMessage("Get_Failure_Msg");
		}
	}
	
	
	/**
	 * 获取精筛信息
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping(value = "/screen/getHealthCheckDetailInfo", 
					method = RequestMethod.GET, 
					produces = "application/json;charset=utf-8")
	public Messages getHealthCheckDetailInfo (HttpServletRequest request) throws Exception {
		try {
			String uniqueId = request.getParameter("uniqueId");
		    if (StringUtils.isEmpty(uniqueId)) {
			    return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
		    }
			
			JSONObject json = healthControlService.getHealthCheckDetailInfo(uniqueId);
			/** 成功 **/
			return MessageUtil.getSuccessMessage("Get_Success_Msg", json);
		} catch (BaseException e) {
			System.out.println("获取精筛信息异常");
			return MessageUtil.getErrorMessage("Get_Failure_Msg");
		}
	}
	
	/**
	 * 获取初筛、精筛指定日期之后的数据
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/screen/getHealthCheckByDate", 
					method = RequestMethod.GET, 
					produces = "application/json;charset=utf-8")
	public Messages getHealthCheckByDate (HttpServletRequest request) {
		try {
			String uniqueId = request.getParameter("uniqueId");
			String checkTime = request.getParameter("checkTime");
			String checkSource = request.getParameter("checkSource");
		    if (StringUtils.isEmpty(uniqueId) || StringUtils.isEmpty(checkTime) || StringUtils.isEmpty(checkSource)) {
			    return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
		    }
			
			JSONObject json = healthControlService.getHealthCheckByDate(uniqueId, checkTime, checkSource);
			System.out.println("json:" + json);
			/** 成功 **/
			return MessageUtil.getSuccessMessage("Get_Success_Msg", json);
		} catch (BaseException e) {
			System.out.println("获取精筛信息异常");
			return MessageUtil.getErrorMessage("Get_Failure_Msg");
		}
	}
	
	/**
	 * 获取指定时间段内的数据
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/screen/getDataByDates", 
					method = RequestMethod.GET, 
					produces = "application/json;charset=utf-8")
	public Messages getDataByDates (HttpServletRequest request) {
		try {
			String uniqueId = request.getParameter("uniqueId");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			String checkSource = request.getParameter("checkSource");
		    if (StringUtils.isEmpty(uniqueId) || StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)
		    		|| StringUtils.isEmpty(checkSource)) {
			    return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
		    }
			
			JSONObject json = healthControlService.getDataByDates(uniqueId, startTime, endTime, checkSource);
			System.out.println("获取指定时间段内的数据json:" + json);
			/** 成功 **/
			return MessageUtil.getSuccessMessage("Get_Success_Msg", json);
		} catch (BaseException e) {
			System.out.println("获取精筛信息异常");
			return MessageUtil.getErrorMessage("Get_Failure_Msg");
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

}
