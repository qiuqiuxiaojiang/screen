package com.capitalbio.healthcheck.task;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.capitalbio.common.aspect.annotation.CacheLock;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.capitalbio.healthcheck.service.HealthControlService;
import com.google.common.collect.Maps;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;
//对于发送异常
@Component
public class VisitPersonTask {
	
	@Autowired
	private HealthCheckService healthCheckService;
	
	@Autowired
	private HealthControlService healthControlService;
	
	
//	@Scheduled(cron = "0 0/3 * * * ?") 
	@Scheduled(cron = "0 0 * * * ?") 
	@CacheLock(lockedPrefix = "VisitPersonTask",expireTime=1000)
	public void executeVisitPerson() {
		System.out.println("===============开始执行数据推送定时任务==============");
		
		Map<String, Object> queryMap = Maps.newHashMap();
		queryMap.put("code", 1);
		List<Map<String, Object>> list = healthCheckService.queryList(queryMap, "visitperson");
		for (Map<String, Object> visitPerson : list) {
			String type = visitPerson.get("type").toString();
			
			Map<String, Object> sendData = null;
			if (type.equals("doc")) {
				sendData = healthControlService.sendDocData(visitPerson);
			} else {
				sendData = healthControlService.sendData(visitPerson);
			}
			
			if (sendData != null && !"".equals(sendData) && !sendData.isEmpty()) {
				visitPerson.put("code", sendData.get("code"));
			}
			visitPerson.put("returnMsg", sendData);
			healthCheckService.saveData("visitperson", visitPerson);
		}
		
		System.out.println("===============结束执行数据推送定时任务==============");
	}

}
