package com.capitalbio.visit.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.capitalbio.common.dao.MongoBaseDAO;

/**
 * 随访DAO
 * @author wdong
 *
 */
@Repository
public class VisitDAO extends MongoBaseDAO {
	/**
	 * 根据随访计划和随访状态获取随访
	 * @param planId
	 * @param visitStatus
	 * @return
	 */
	public Map<String, Object> getVisitByPlan(String planId, String visitStatus) {
		Map<String,Object> query = new HashMap<>();
		query.put("planId", planId);
		if (StringUtils.isNotEmpty(visitStatus)) {
			query.put("visitStatus", visitStatus);
		}
		return this.getDataByQuery("visit", query);
	}
	
	/**
	 * 根据初筛或精筛数据ID获取关联随访信息
	 * @param dataId
	 * @return
	 */
	public Map<String,Object> getVisitByDataId(String dataId) {
		Map<String,Object> query = new HashMap<>();
		query.put("dataId", dataId);
		return this.queryOne("visit", query);
	}
}
