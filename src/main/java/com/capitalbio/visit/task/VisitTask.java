package com.capitalbio.visit.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.service.SmsService;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.visit.service.VisitPlanService;
import com.capitalbio.visit.service.VisitService;

/**
 * 随访相关自动任务
 * @author wdong
 *
 */
@Component
public class VisitTask {
	@Autowired
	private VisitPlanService visitPlanService;
	@Autowired
	private VisitService visitService;
	@Autowired
	private AuthService authService;
	@Autowired
	private SmsService smsService;
	
//	@Scheduled(cron = "0 0 0 * * ?")
	public void changePlanState() {
		Date today = DateUtil.getDate(null);
		//开始随访提醒的人群列表
		List<Map<String,Object>> remindList = visitPlanService.getPlanList("未到期", today, null);
		for (Map<String,Object> visitPlan:remindList) {
			String planId = (String)visitPlan.get("id");
			Map<String,Object> visit = new HashMap<>();
			visit.put("planId", planId);
			visit.put("visitStatus", "随访中");
			visit.put("remindDate", visitPlan.get("remindDate"));
			visit.put("expireDate", visitPlan.get("expireDate"));
			visitPlan.put("visitStatus", "随访中");
			String visitId = visitService.saveData(visit);
			visitPlan.put("visitId", visitId);
			visitPlanService.saveData(visitPlan);
		}
		//到期未随访的人群列表
		List<Map<String,Object>> expireList = visitPlanService.getPlanList("随访中", null, today);
		for (Map<String,Object> visitPlan:expireList) {
			String visitId = (String)visitPlan.get("visitId");
			Map<String,Object> visit = visitService.getData(visitId);
			if (visit != null) {
				visit.put("visitStatus", "已过期");
			}
			visitPlan.put("visitStatus", "已过期");
			visitService.saveData(visit);
			visitPlanService.saveData(visitPlan);
		}
		//过期时间30天后状态刷新成未到期
		Date expireDate = DateUtil.rollDate(today, -30);
		List<Map<String,Object>> refreshList = visitPlanService.getPlanList(null, null, expireDate);
		for (Map<String,Object> visitPlan:refreshList) {
			String visitId = (String)visitPlan.get("visitId");
			Map<String,Object> visit = visitService.getData(visitId);
			Date visitDate = null;
			if (visit != null) {
				//如果已随访，则取随访日期
				String visitStatus = (String)visit.get("visitStatus");
				if ("已随访".equals(visitStatus)) {
					visitDate = (Date)visit.get("visitDate");
				}
			}
			//如果随访过期，则取上次应随访日期
			if (visitDate == null) {
				visitDate = (Date)visitPlan.get("planDate");
			}
			if (visitDate == null) {
				visitDate = new Date();
			}
			Date newVisitDate = DateUtil.getDate(DateUtil.rollDate(visitDate, 90));
			Date newRemindDate = DateUtil.rollDate(newVisitDate, -30);
			Date newExpireDate = DateUtil.rollDate(newVisitDate, 60);
			visitPlan.put("visitStatus", "未到期");
			visitPlan.put("lastVisitDate", visitDate);//记录上次随访时间
			visitPlan.put("remindDate", newRemindDate);
			visitPlan.put("expireDate", newExpireDate);
			visitPlan.put("planDate", newVisitDate);
			visitPlan.put("visitId", null);
			visitPlanService.saveData(visitPlan);
		}

	}
	
//	@Scheduled(cron = "0 0 10 * * ?")
	public void sendMessage() {
		Date today = DateUtil.getDate(null);
		Map<String,Object> tokenMap = authService.applyToken();
		String token = (String)tokenMap.get("token");
		String userId = (String)tokenMap.get("userId");
		List<Map<String,Object>> newList = visitPlanService.getPlanList(null, today, null);
		Date expireDate = DateUtil.rollDate(today, 3);
		List<Map<String,Object>> expireList = visitPlanService.getPlanList(null, null, expireDate);
		for (Map<String,Object> visitPlan:newList) {
			String uniqueId = (String)visitPlan.get("uniqueId");
			Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
			if (secretMap == null) {
				continue;
			}
			String mobile = (String)secretMap.get("mobile");
			if (StringUtils.isNotEmpty(mobile)) {
				String content = "";
				Map<String,Object> smsResult = smsService.sendSms(mobile, content);
				smsResult.put("uniqueId", uniqueId);
				smsService.saveResult(smsResult);
			}
			
		}
		for (Map<String,Object> visitPlan:expireList) {
			String uniqueId = (String)visitPlan.get("uniqueId");
			Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
			if (secretMap == null) {
				continue;
			}
			String mobile = (String)secretMap.get("mobile");
			if (StringUtils.isNotEmpty(mobile)) {
				String content = "";
				Map<String,Object> smsResult = smsService.sendSms(mobile, content);
				smsResult.put("uniqueId", uniqueId);
				smsService.saveResult(smsResult);
			}
		}
	}
	
	/**
	 * 初始化随访，将之前所有的初筛数据生成随访计划
	 * 此任务仅执行一次
	 */
//	@PostConstruct
	public void initVisitPlan() {
		visitPlanService.initVisitPlan();
	}
}
