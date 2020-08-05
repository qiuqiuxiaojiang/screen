package com.capitalbio.healthcheck.task;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.google.common.collect.Maps;
@Component
public class ParseFileTask {
	
	@Autowired HealthCheckService healthCheckService ;
	@Autowired AuthService authService;
	
	@Scheduled(cron = "0 0/30 * * * ?") 
	public void creatTempColl() {
		Map<String, Object> query = Maps.newHashMap();
		query.put("state", "上传中");
		List<Map<String, Object>> list = healthCheckService.queryList(query, "fileUploadRecord");
		
		Map<String, Object> tokenMap = authService.applyToken();
		for (Map<String, Object> map : list) {
			String path = (String) map.get("path");
			String id = (String) map.get("id");
			
			System.out.println("=============path:" + path + ",id:" + id);
			File file = new File(path);
			try {
				System.out.println("=============================解析文件开始=============================");
				healthCheckService.uploadFile(file, id, tokenMap);
				System.out.println("=============================解析文件结束=============================");
			} catch (Exception e) {
				System.out.println("=============================解析文件错误=============================");
				e.printStackTrace();
			}
		}
		
	}

}
