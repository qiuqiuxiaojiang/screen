/**
 * 
 */
package com.capitalbio.visit.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.visit.dao.VisitDAO;
import com.capitalbio.visit.dao.VisitPlanDAO;
import com.google.common.collect.Maps;

/**
 * 随访计划Service
 * @author wdong
 *
 */
@Service
public class VisitPlanService extends BaseService {
	@Autowired
	private VisitPlanDAO visitPlanDAO;
	
	@Autowired
	private VisitDAO visitDAO;

	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return visitPlanDAO;
	}

	@Override
	public String getCollName() {
		return "visitPlan";
	}
	/**
	 * 根据初筛信息生成随访计划
	 */
	public void generateVisitPlanByHealthCheck(Map<String,Object> healthCheck) {
		visitPlanDAO.generateVisitPlanByHealthCheck(healthCheck);
	}
	
	public void initVisitPlan() {
		visitPlanDAO.initVisitPlan();
	}
	/**
	 * 根据唯一标识获取患者随访计划
	 * @param uniqueId
	 * @return
	 */
	public Map<String,Object> getVisitPlanByUniqueId(String uniqueId) {
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("uniqueId", uniqueId);
		return visitPlanDAO.getDataByQuery("visitPlan", queryMap);
	}
	
	/**
	 * 计算需提醒的人数
	 * @param remindType
	 * @return
	 */
	public int countRemind(String remindType) {
		return visitPlanDAO.countRemind(remindType);
	}

	/**
	 * 获得当日需要提醒的计划列表
	 * @param remindType：new当日新增；expire即将过期
	 * @return
	 */
	public List<Map<String,Object>> getRemindList(String remindType) {
		return visitPlanDAO.getRemindList(remindType);
	}
	
	public List<Map<String,Object>> getPlanList(String visitStatus, Date remindDate, Date expireDate) {
		return visitPlanDAO.getPlanList(visitStatus, remindDate, expireDate);
	}
}
