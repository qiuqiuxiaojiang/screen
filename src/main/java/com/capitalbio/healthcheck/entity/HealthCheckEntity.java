package com.capitalbio.healthcheck.entity;

import java.util.HashMap;
import java.util.Map;

public class HealthCheckEntity {

	/**
	 * 基因筛查算法
	 */
	private Map<String,Object> mapScreeningAlgorithm = new HashMap<String,Object>();
	
	
	
	
	

	public Map<String, Object> getMapScreeningAlgorithm() {
		/**年龄算法**/
		Map<String,Object> mapAgeAlgorithm = new HashMap<String,Object>();
		/**大于等于左边的数  小于等于右边的数**/
		mapAgeAlgorithm.put("0-24", "0");
		mapAgeAlgorithm.put("25-34", "4");
		mapAgeAlgorithm.put("35-39", "8");
		mapAgeAlgorithm.put("40-44", "11");
		mapAgeAlgorithm.put("45-49", "12");
		mapAgeAlgorithm.put("50-54", "13");
		mapAgeAlgorithm.put("55-59", "15");
		mapAgeAlgorithm.put("60-64", "16");
		mapAgeAlgorithm.put("65-100000", "18");
		
		mapScreeningAlgorithm.put("age", mapAgeAlgorithm);
		
		/**收缩压算法**/
		Map<String,Object> mapHighPressureAlgorithm = new HashMap<String,Object>();
		/**大于等于左边的数  小于等于右边的数**/
		mapHighPressureAlgorithm.put("0-109", "0");
		mapHighPressureAlgorithm.put("110-119", "1");
		mapHighPressureAlgorithm.put("120-129", "3");
		mapHighPressureAlgorithm.put("130-139", "6");
		mapHighPressureAlgorithm.put("140-149", "7");
		mapHighPressureAlgorithm.put("150-159", "8");
		mapHighPressureAlgorithm.put("160-100000", "10");
		
		mapScreeningAlgorithm.put("highPressure", mapHighPressureAlgorithm);
		
		/**性别算法**/
		Map<String,Object> mapSexAlgorithm = new HashMap<String,Object>();
		/**大于等于左边的数  小于等于右边的数**/
		mapSexAlgorithm.put("男", "2");
		mapSexAlgorithm.put("女", "0");
		
		mapScreeningAlgorithm.put("sex", mapSexAlgorithm);
		
		/**体质 算法**/
		Map<String,Object> mapBMIAlgorithm = new HashMap<String,Object>();
		/**大于等于左边的数  小于等于右边的数**/
		mapBMIAlgorithm.put("0-21", "0");
		mapBMIAlgorithm.put("22-23.9", "1");
		mapBMIAlgorithm.put("24-29.9", "3");
		mapBMIAlgorithm.put("30-1000000", "5");
		
		mapScreeningAlgorithm.put("bmi", mapBMIAlgorithm);
		
		/**胸围算法**/
		Map<String,Object> mapWaistlineAlgorithm = new HashMap<String,Object>();
		/**大于等于左边的数  小于等于右边的数**/
		
		mapWaistlineAlgorithm.put("男-0-74", "0");
		mapWaistlineAlgorithm.put("女-0-69", "0");
		
		mapWaistlineAlgorithm.put("男-75-79.9", "3");
		mapWaistlineAlgorithm.put("女-70-74.9", "3");
		
		mapWaistlineAlgorithm.put("男-80-84.9", "5");
		mapWaistlineAlgorithm.put("女-75-79.9", "5");
		
		mapWaistlineAlgorithm.put("男-85-89.9", "7");
		mapWaistlineAlgorithm.put("女-80-84.9", "7");
		
		mapWaistlineAlgorithm.put("男-90-94.9", "8");
		mapWaistlineAlgorithm.put("女-85-89.9", "8");
		
		mapWaistlineAlgorithm.put("男-95-100000", "10");
		mapWaistlineAlgorithm.put("女-90-100000", "10");
		
		mapScreeningAlgorithm.put("waistline", mapWaistlineAlgorithm);
		
		/**糖尿病家族史算法**/
		Map<String,Object> mapFamilyHistoryAlgorithm = new HashMap<String,Object>();
		
		mapFamilyHistoryAlgorithm.put("无", "0");
		mapFamilyHistoryAlgorithm.put("有", "6");
		
		mapScreeningAlgorithm.put("familyHistory", mapFamilyHistoryAlgorithm);
		
		/**总得分算法**/
		Map<String,Object> mapScoreAlgorithm = new HashMap<String,Object>();
		mapScoreAlgorithm.put("0-24", "正常");
		mapScoreAlgorithm.put("25-10000", "高危");
		
		mapScreeningAlgorithm.put("scoreTotal", mapScoreAlgorithm);
		
		
		return mapScreeningAlgorithm;
	}

	public void setMapScreeningAlgorithm(Map<String, Object> mapScreeningAlgorithm) {
		this.mapScreeningAlgorithm = mapScreeningAlgorithm;
	}
	
	
	
	
}
