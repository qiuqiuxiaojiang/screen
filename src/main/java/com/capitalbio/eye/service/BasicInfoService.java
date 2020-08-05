package com.capitalbio.eye.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.eye.dao.BasicInfoDAO;
import com.google.common.collect.Maps;

@Service
public class BasicInfoService extends BaseService {

	@Autowired BasicInfoDAO basicInfoDAO;
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return basicInfoDAO;
	}

	@Override
	public String getCollName() {
		return "basicInfo";
	}
	
	/**
	 * 根据用户ID获得用户信息
	 * @param userId
	 * @return
	 */
	public Map<String,Object> getInfoByUserId(String userId) {
		Map<String,Object> uMap=Maps.newHashMap();
		uMap.put("userId",userId);
		Map<String,Object> basicMap=basicInfoDAO.getDataByQuery(getCollName(), uMap);
		return basicMap;
	}

	/**
	 * 根据专家ID获得专家信息
	 * @param expertId
	 * @return
	 */
	public Map<String,Object> getExpertInfoById(String expertId) {
		Map<String,Object> uMap=Maps.newHashMap();
		uMap.put("expertId",expertId);
		Map<String,Object> basicMap=basicInfoDAO.getDataByQuery("expertInfo", uMap);
		return basicMap;
		
	}

	/**
	 * 根据专家ID获得专家信息
	 * @param expertId
	 * @return
	 */
	public Map<String,Object> getExpertInfoByNameAndNo(String name, String idno) {
		Map<String,Object> uMap=Maps.newHashMap();
		uMap.put("name",name);
		uMap.put("idno",idno);
		Map<String,Object> basicMap=basicInfoDAO.getDataByQuery("expertInfo", uMap);
		return basicMap;
		
	}

	/**
	 * 查询符合条件的专家信息
	 * @param queryMap
	 * @return
	 */
	public List<Map<String,Object>> queryDoctorList(Map<String,Object> queryMap) {
		return basicInfoDAO.queryList("expertInfo", queryMap);
	}
	
}
