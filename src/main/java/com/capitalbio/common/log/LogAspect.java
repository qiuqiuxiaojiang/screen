package com.capitalbio.common.log;


import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.common.util.IPUtil;
import com.capitalbio.common.util.JsonUtil;
import com.google.common.collect.Maps;
  
/** 
 * 切点类 
 * @author wdong
 */  
@Aspect  
@Component  
public  class LogAspect {  
    //注入Service用于把日志保存数据库  
    @Resource  
     private LoggerService logService;  
    //本地异常日志记录对象  
     private  static  final Logger logger = LoggerFactory.getLogger(LogAspect. class);  
  
   //Service层切点    
     @Pointcut("@annotation(com.capitalbio.common.log.ServiceLog)")    
      public  void serviceAspect() {    
     } 
     
    //Controller层切点  
    @Pointcut("@annotation(com.capitalbio.common.log.ControllerLog)")  
     public  void controllerAspect() {  
    }  
    
    //特殊Controller层切点，方法中含有response.getOutputStream，或response.getWriter,不能使用joinPoint.getArgs
    @Pointcut("@annotation(com.capitalbio.common.log.SpecialLog)")
    public void specialControllerAspect() {
    	
    }
  
    /** 
     * 前置通知 用于拦截Controller层记录用户的操作 
     * 
     * @param joinPoint 切点 
     */  
    @Before("controllerAspect()")  
    public  void doBefore(JoinPoint joinPoint) {  
  
    	//请求的IP  
    	try {  
    		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  
    		String ip = IPUtil.getRemortIP(request); 
    		Map<?,?> map = request.getParameterMap();
    		String args = JsonUtil.objToJson(map);
            //获取用户请求方法的参数并序列化为JSON格式字符串  
            StringBuffer sb = new StringBuffer();
            Object[] objs = joinPoint.getArgs();
            if (objs !=  null && objs.length > 0) {  
            	for ( int i = 0; i < joinPoint.getArgs().length; i++) {  
            		Object obj = joinPoint.getArgs()[i];
            		sb.append(JsonUtil.objToJson(obj)).append(";");
                }  
            }  

            //*========数据库日志=========*//  
            Map<String,Object> log = Maps.newHashMap();
            log.put("method", joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()");  
            log.put("uri", request.getRequestURI());
            log.put("ip", ip);  
            log.put("params", request.getQueryString());
            log.put("args", args);
            log.put("json", sb.toString());
            HttpSession session = ContextUtils.getSession();
            if (session != null) {
            	log.put("sessionId", session.getId());
                Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");
                if (user != null) {
                	log.put("username", user.get("username"));
                }
            	
            }
            
            //保存数据库  
            logService.saveData(log);  
        }  catch (Exception e) {  
            //记录本地异常日志  
            logger.error("==日志记录异常==");  
            logger.error("异常信息:{}", e.getMessage());  
        }  
    }  

    /** 
     * 前置通知 用于拦截Controller层记录用户的操作 
     * 
     * @param joinPoint 切点 
     */  
    @Before("specialControllerAspect()")  
    public  void doSpecialBefore(JoinPoint joinPoint) {  
  
    	//请求的IP  
    	try {  
    		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  
    		String ip = IPUtil.getRemortIP(request); 

            //*========数据库日志=========*//  
            Map<String,Object> log = Maps.newHashMap();
            log.put("method", joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()");  
            log.put("uri", request.getRequestURI());
            log.put("ip", ip);  
            log.put("params", request.getQueryString());
            
            //保存数据库  
            logService.saveData(log);  
        }  catch (Exception e) {  
            //记录本地异常日志  
            logger.error("==日志记录异常==");  
            logger.error("异常信息:{}", e.getMessage());  
        }  
    }  

    /** 
     * 异常通知 用于拦截service层记录异常日志 
     * 
     * @param joinPoint 
     * @param e 
     */  
    @AfterThrowing(pointcut = "serviceAspect()", throwing = "e")  
     public  void doAfterThrowing(JoinPoint joinPoint, Throwable e) {  
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  
        String args = "";
        try {  
        	String ip = IPUtil.getRemortIP(request);  
        	//获取用户请求方法的参数并序列化为JSON格式字符串  
        	StringBuffer sb = new StringBuffer();
        	Object[] objs = joinPoint.getArgs();
        	if (objs !=  null && objs.length > 0) {  
        		for ( int i = 0; i < joinPoint.getArgs().length; i++) {  
        			Object obj = joinPoint.getArgs()[i];
        			sb.append(JsonUtil.objToJson(obj)).append(";");
        		}  
        	}  
        	args = sb.toString();
        	/*==========数据库日志=========*/  
            Map<String,Object> log = Maps.newHashMap();
            log.put("exception",e.getClass().getName());  
            log.put("exceptionDetail",e.getMessage());  
            log.put("method", joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()");  
            log.put("params", request.getQueryString());
            log.put("args", args);
            log.put("ip", ip);  
            //保存数据库  
            logService.saveData(log);  
        }  catch (Exception ex) {  
            //记录本地异常日志  
            logger.error("==异常通知异常==");  
            logger.error("异常信息:{}", ex.getMessage());  
        }  
         /*==========记录本地异常日志==========*/  
         String[] objs = new String[]{
        		 joinPoint.getTarget().getClass().getName() + joinPoint.getSignature().getName(), e.getClass().getName(), e.getMessage(), args
         };
        logger.error("异常方法:{}异常代码:{}异常信息:{}参数:{}", objs);  
  
    }  
  
  
}  