/**
 * 
 */
package com.capitalbio.visit.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.util.DateUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

/**
 * 随访计划DAO
 * @author wdong
 *
 */
@Repository
public class VisitPlanDAO extends MongoBaseDAO {
	/**
	 * 加载需要提醒的人数
	 * @param type：new当日新增，expire即将过期
	 * @return
	 */
	public Integer countRemind(String type) {
		BasicDBObject query = new BasicDBObject();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		if ("new".equals(type)) {
			query.put("remindDate", c.getTime());
		} else if ("expire".equals(type)) {
			c.roll(Calendar.DAY_OF_YEAR, -3);
			query.put("expireDate", c.getTime());
		}
		int count = (int)countByQuery("visitPlan", query);
		return count;
	}

	/**
	 * 查询当时提醒列表
	 * @param type：new当日新增，expire即将过期
	 * @return
	 */
	public List<Map<String,Object>> getRemindList(String type) {
		BasicDBObject query = new BasicDBObject();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		if ("new".equals(type)) {
			query.put("remindDate", c.getTime());
		} else if ("expire".equals(type)) {
			c.roll(Calendar.DAY_OF_YEAR, 3);
			query.put("expireDate", c.getTime());
		}
		List<Map<String,Object>> planList = queryList("visitPlan", query);
		return planList;
		
	}
	
	/**
	 * 根据随访状态、提醒日期、过期日期查询随访计划
	 * @param visitStatus
	 * @param remindDate
	 * @param expireDate
	 * @return
	 */
	public List<Map<String,Object>> getPlanList(String visitStatus, Date remindDate, Date expireDate) {
		BasicDBObject query = new BasicDBObject();
		if (StringUtils.isNotEmpty(visitStatus)) {
			query.put("visitStatus", visitStatus);
		}
		
		if (remindDate != null) {
			query.put("remindDate", remindDate);
		}
		if (expireDate != null) {
			query.put("expireDate", expireDate);
		}
		List<Map<String,Object>> planList = queryList("visitPlan", query);
		return planList;
		
	}
	
	/**
	 * 初始化随访计划
	 */
	@SuppressWarnings("unchecked")
	public void initVisitPlan() {
		DBCollection checkColl = db.getCollection("healthcheck");
		DBCursor cursor = checkColl.find().addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		while (cursor.hasNext()) {
			Map<String,Object> healthCheck = (Map<String,Object>)cursor.next();
			healthCheck.put("id", healthCheck.remove("_id").toString());
			generateVisitPlanByHealthCheck(healthCheck);
		}

	}
	/**
	 * 根据初筛记录生成随访计划
	 * @param healthCheck
	 */
	public void generateVisitPlanByHealthCheck(Map<String,Object> healthCheck) {
		String uniqueId = (String)healthCheck.get("uniqueId");
		Date checkTime = (Date)healthCheck.get("checkTime");
		if (checkTime == null) {
			//如果没有检测时间，不生成随访计划
			return;
		}
		String healthCheckId = (String)healthCheck.get("id");
		
		Map<String,Object> query = new HashMap<>();
		query.put("dataId", healthCheckId);
		Map<String,Object> findVisit = queryOne("visit", query);
		if (findVisit != null) {
			//此次初筛已经关联随访，不生成随访计划
			return ;
		}
		checkTime = DateUtil.getDate(checkTime);
		Date nowDate = DateUtil.getDate(new Date());
		
		Date planDate = DateUtil.rollDate(checkTime, 90);
		Date remindDate = DateUtil.rollDate(planDate, -30);
		Date expireDate = DateUtil.rollDate(planDate, 30);
		String visitStatus;
		query.clear();
		query.put("uniqueId", uniqueId);
		Map<String,Object> visitPlan = queryOne("visitPlan", query);
		if (visitPlan == null) {
			visitPlan = new HashMap<String, Object>();
			visitPlan.put("uniqueId", uniqueId);
			if (remindDate.before(nowDate)) {
				//预期的随访日期离现在太近，则从今日起90日作为随访日期
				planDate = DateUtil.rollDate(nowDate, 90);
				remindDate = DateUtil.rollDate(planDate, -30);
				expireDate = DateUtil.rollDate(planDate, 30);
			}
			visitStatus = "未到期";
			visitPlan.put("planDate", planDate);
			visitPlan.put("remindDate", remindDate);
			visitPlan.put("expireDate", expireDate);
			visitPlan.put("visitStatus", visitStatus);
		} else {
			visitStatus = (String)visitPlan.get("visitStatus");
			String planId = (String)visitPlan.get("id");
			if ("未到期".equals(visitStatus)) {
				//未到期就进行筛查，则判断上次随访日期，如果晚于上次随访日期，则记录入下次随访中
				Date lastVisitDate = (Date)visitPlan.get("lastVisitDate");
				if (lastVisitDate != null && lastVisitDate.before(checkTime)) {
					//此次筛查时间在上次筛查时间之后
					Map<String,Object> visit = new HashMap<>();
					visit.put("planId", planId);
					visit.put("remindDate", visitPlan.get("remindDate"));
					visit.put("expireDate", visitPlan.get("expireDate"));
					visit.put("dataId", healthCheckId);
					visit.put("visitDate", checkTime);
					visit.put("visitStatus", "已随访");
					String visitId = saveData("visit", visit);
					visitPlan.put("visitId", visitId);
					visitPlan.put("visitStatus", "已随访");
				}
				
			} else if ("随访中".equals(visitStatus) || "已随访".equals(visitStatus) || "已过期".equals(visitStatus)) {
				String visitId = (String)visitPlan.get("visitId");
				Map<String,Object> visit = null;
				if (StringUtils.isNotEmpty(visitId)) {
					visit = getData("visit", visitId);
				}
				
				if (visit == null) {
					visit = new HashMap<>();
					visit.put("planId", planId);
					visit.put("remindDate", visitPlan.get("remindDate"));
					visit.put("expireDate", visitPlan.get("expireDate"));
				}
				String dataId = (String)visit.get("dataId");
				Date visitDate = (Date)visit.get("visitDate");
				if (StringUtils.isEmpty(dataId) || visitDate == null || visitDate.before(checkTime)) {
					visit.put("dataId", healthCheckId);
					visit.put("visitDate", checkTime);
					visit.put("visitStatus", "已随访");
				}
				visitId = saveData("visit", visit);
				visitPlan.put("visitId", visitId);
				visitPlan.put("visitStatus", "已随访");
			}
		}
		saveData("visitPlan", visitPlan);
		
	}

}
