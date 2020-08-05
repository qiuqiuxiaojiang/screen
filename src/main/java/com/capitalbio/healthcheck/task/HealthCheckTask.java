package com.capitalbio.healthcheck.task;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.capitalbio.common.aspect.annotation.CacheLock;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.eye.service.EyeRecordService;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.google.common.collect.Maps;
/**
 * 计算当天初筛记录血糖情况、血脂情况、血压情况、人群结果分类、OGTT检测、血脂检测、血压检测
 * @author xiaoyanzhang
 *
 */
@Component
public class HealthCheckTask {

	@Autowired HealthCheckService healthCheckService;
	@Autowired EyeRecordService eyeRecordService;
	
//	@Scheduled(cron = "0 0/2 * * * ?") 
	@Scheduled(cron = "0 0 22 * * ?")
	@CacheLock(lockedPrefix = "HealthCheckTask",expireTime=1000)
	public void executeHealthCheck() {
		System.out.println("===============开始计算人群分类结果==============");
		
		String date = DateUtil.dateToString(new Date());
		
		String[] collNames = {"healthcheck", "customer"};
		
		for (String collName : collNames) {
			System.out.println("================" + collName + "==================");
			Map<String, Object> queryMap = Maps.newHashMap();
			queryMap.put("checkDate", date);
			
			
			List<Map<String, Object>> list = healthCheckService.queryList(queryMap, collName);
			
			int i = 0;
			for (Map<String, Object> data : list) {
				String disease = "";
				String tnb = "";
				Double bloodGlucose = null;
				Double bloodGlucose2h = null;
				Double bloodGlucoseRandom = null;
				//Integer riskScore = null;
				
				String gxy = "";
				Integer highPressure = null;
				Integer lowPressure = null;
				
				String gxz = "";
				Double tc = null;
				Double tg = null;
				Double hdl = null;
				
				Double BMI = null;
				
				if (data.get("disease") != null && data.get("disease") != "") {
					disease = data.get("disease").toString();
					if(disease.contains("糖尿病")) {
						tnb = "糖尿病";
					} 
					
					if (disease.contains("高血压")) {
						gxy = "高血压";
					}
					
					if (disease.contains("高血脂")) {
						gxz = "高血脂";
					}
				}
				
				if (data.get("bloodGlucose") != null && data.get("bloodGlucose") != "") {
					bloodGlucose = Double.parseDouble(data.get("bloodGlucose").toString());
				}
				
				if (data.get("bloodGlucose2h") != null && data.get("bloodGlucose2h") != "") {
					bloodGlucose2h = Double.parseDouble(data.get("bloodGlucose2h").toString());
				}
				
				if (data.get("bloodGlucoseRandom") != null && data.get("bloodGlucoseRandom") != "") {
					bloodGlucoseRandom = Double.parseDouble(data.get("bloodGlucoseRandom").toString());
				}
				
				/*if (data.get("riskScore") != null && data.get("riskScore") != "") {
					riskScore = Integer.parseInt(data.get("riskScore").toString());
				}*/
				
				if (data.get("highPressure") != null && data.get("highPressure") != "") {
					highPressure = Integer.parseInt(data.get("highPressure").toString());
				}
				
				if (data.get("lowPressure") != null && data.get("lowPressure") != "") {
					lowPressure = Integer.parseInt(data.get("lowPressure").toString());
				}
				
				if (data.get("tc") != null && data.get("tc") != "") {
					tc = Double.parseDouble(data.get("tc").toString());
				}
				
				if (data.get("tg") != null && data.get("tg") != "") {
					tg = Double.parseDouble(data.get("tg").toString());
				}
				
				if (data.get("hdl") != null && data.get("hdl") != "") {
					hdl = Double.parseDouble(data.get("hdl").toString());
				}
				
				if (data.get("BMI") != null && data.get("BMI") != "") {
					BMI = Double.parseDouble(data.get("BMI").toString());
				}
				
				//计算糖尿病风险评分
				Map<String, Object> scoreMap = Maps.newHashMap();
				Map<String,Object> query = Maps.newHashMap();
				query.put("uniqueId", data.get("uniqueId"));
				Map<String,Object> customerMap = healthCheckService.getDataByQuery("customer", query);
				
				scoreMap.put("age", customerMap.get("age"));
				scoreMap.put("gender", customerMap.get("gender"));
				scoreMap.put("familyHistory", customerMap.get("familyHistory"));
				scoreMap.put("waistline", data.get("waistline"));
				scoreMap.put("BMI", data.get("BMI"));
				scoreMap.put("highPressure", data.get("highPressure"));
				scoreMap.put("familyDisease", data.get("familyDisease"));
				Integer riskScore = healthCheckService.computeRiskScore(scoreMap);
				
				String fatCon = healthCheckService.getFatCon(BMI);
				
				Map<String, Object> bloodGlucoseCon = healthCheckService.bloodGlucoseCon(tnb, bloodGlucose, bloodGlucose2h, bloodGlucoseRandom, riskScore);
				
				Map<String, Object> bloodPressureCon = healthCheckService.bloodPressureCon(gxy, highPressure, lowPressure);
				
				Map<String, Object> bloodLipidCon = healthCheckService.bloodLipidCon(gxz, tc, tg, hdl);
				
				Map<String, Object> classifyResult = healthCheckService.classifyResult(bloodGlucoseCon, bloodPressureCon, bloodLipidCon, fatCon);
				
				if (data.get("uniqueId") != null) {
					boolean flag = eyeRecordService.isEyeRecordTest(data.get("uniqueId").toString(), null);
					if (flag) {
						data.put("eyeCheck", "已检测");
					} else {
						data.put("eyeCheck", "未检测");
					}
				}
				
				data.putAll(bloodGlucoseCon);
				data.putAll(bloodPressureCon);
				data.putAll(bloodLipidCon);
				data.putAll(classifyResult);
				
				data.put("collName", collName);
				data.put("riskScore", riskScore);
				Message message = healthCheckService.updateHcData(data);
				i ++;
				System.out.println("=========uniqueId==========" + data.get("uniqueId") + ",message:" + message.code);
			}
			System.out.println("====================i=====================" + i);
		}
		System.out.println("===============结束人群分类结果计算==============");
	}
}
