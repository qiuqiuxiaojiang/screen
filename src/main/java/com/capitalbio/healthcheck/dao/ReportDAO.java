package com.capitalbio.healthcheck.dao;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.util.ParamUtils;
import com.google.common.collect.Maps;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class ReportDAO extends MongoBaseDAO {
	Logger logger = Logger.getLogger(this.getClass());
	private String[] collectionNames = new String[]{
			"countInfo",
			"reportBloodGlucose",
			"reportDiabetes",
			"reportEffect",
			"reportExam",
			"reportEyeCheck",
			"reportEyeUpload",
			"reportInfo",
			"reportSuspectedPrediabetes",
			"reportSuspectedDiabetes",
			"reportTizhi",
			"reportFat",
			"reportHighRisk",
			"diseaseTizhi",
			"reportComplicate",
			"reportOgtt",
			"reportTransfer",
			"reportAge",
			"reportNewHighPressure",
			"reportRegisterHighPressure"
		};
	
	@SuppressWarnings("unchecked")
	public void regenerate() {
		System.out.println("begin generate:" + new Date());
		DBCollection coll = db.getCollection("healthcheck");
		for (int i = 0; i < collectionNames.length; i++) {
			DBCollection dropColl = db.getCollection(collectionNames[i]);
			dropColl.drop();
		}
		DBCursor cursor = coll.find();
		int count = 0;
		while (cursor.hasNext()) {
			count++;
			if (count % 1000 == 0) {
				logger.info("processing health check data count :" + count);
				System.out.println("processing health check data count :" + count);
			}
			DBObject obj = cursor.next();
			Map<String,Object> map = obj.toMap();
			processReport(map);
		}
		
		DBCollection eyeColl = db.getCollection("eyeRecord");
		DBCursor eyeCursor = eyeColl.find();
		int eyeCount = 0;
		while (eyeCursor.hasNext()) {
			eyeCount++;
			if (eyeCount % 1000 == 0) {
				logger.info("processing eye record data count :" + eyeCount);
				System.out.println("processing eye record data count :" + eyeCount);
			}
			DBObject obj = eyeCursor.next();
			Map<String,Object> map = obj.toMap();
			processEyeReport(map);
		}
		System.out.println("end generate:" + new Date());
		
	}
	
	public void processEyeReport(Map<String,Object> eyeRecord) {
		String uniqueId = (String)eyeRecord.get("uniqueId");
		if (StringUtils.isEmpty(uniqueId)) {
			return;
		}
		String visitId = (String)eyeRecord.get("visitId");
		String checkPlace = (String)eyeRecord.get("checkPlace");
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("customerId", uniqueId);
		Map<String,Object> countInfo = queryOne("countInfo", queryMap);
		if (countInfo == null) {
			countInfo = Maps.newHashMap();
			countInfo.put("customerId", uniqueId);
		}
		countInfo.put("userId", eyeRecord.get("userId"));
		countInfo.put("checkDate", visitId);
		countInfo.put("checkTime", eyeRecord.get("visitTime"));
		countInfo.put("checkPlace", checkPlace);
		
		String createCount = (String)countInfo.get("createCount");
		if (createCount == null || !"true".equals(createCount)) {
				addCount(checkPlace, visitId, "reportInfo");
				countInfo.put("createCount", "true");
		}

		String eyeUploadCount = (String)countInfo.get("eyeUploadCount");
		if (eyeUploadCount == null || !"true".equals(eyeUploadCount)) {
			eyeUploadCount = "true";
			addCount(checkPlace, visitId, "reportEyeUpload");
			countInfo.put("eyeUploadCount", eyeUploadCount);
		}
		saveData("countInfo", countInfo);
	}
	
	public void processReport(Map<String,Object> data) {
		String customerId = (String)data.get("customerId");
		String checkDate = (String)data.get("checkDate");
		String checkPlace = (String)data.get("checkPlace");
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("customerId", customerId);
		Map<String,Object> countInfo = queryOne("countInfo", queryMap);
		if (countInfo == null) {
			countInfo = Maps.newHashMap();
			countInfo.put("customerId", customerId);
		}
		
		countInfo.put("name", data.get("name"));
		countInfo.put("sex", data.get("sex"));
		countInfo.put("age", data.get("age"));
		countInfo.put("contact", data.get("contact"));
		countInfo.put("checkDate", checkDate);
		countInfo.put("checkTime", data.get("checkTime"));
		countInfo.put("checkPlace", checkPlace);
		countInfo.put("checkGroup", data.get("checkGroup"));
		
		String createCount = (String)countInfo.get("createCount");
		if (createCount == null || !"true".equals(createCount)) {
			addCount(checkPlace, checkDate, "reportInfo");
			countInfo.put("createCount", "true");
		}
		String sex = (String)data.get("sex");
		Integer age = ParamUtils.getIntValue(String.valueOf(data.get("age")));
		Date checkTime = (Date)data.get("checkTime");
		Double temperature = ParamUtils.getDoubleValue(String.valueOf(data.get("temperature")));
		Double bmi = ParamUtils.getDoubleValue(String.valueOf(data.get("BMI")));
		Integer highPressure = ParamUtils.getIntValue(String.valueOf(data.get("highPressure")));
		Integer lowPressure = ParamUtils.getIntValue(String.valueOf(data.get("lowPressure")));
		Integer pulse = ParamUtils.getIntValue(String.valueOf(data.get("pulse")));
		Integer oxygen = ParamUtils.getIntValue(String.valueOf(data.get("oxygen")));
		Double fatContent = ParamUtils.getDoubleValue(String.valueOf(data.get("fatContent")));
		Double waistline = ParamUtils.getDoubleValue(String.valueOf(data.get("waistline")));
		String familyHistory = (String)data.get("familyHistory");
		Double bloodGlucose = ParamUtils.getDoubleValue(String.valueOf(data.get("bloodGlucose")));
		Double bloodGlucose2h = ParamUtils.getDoubleValue(String.valueOf(data.get("bloodGlucose2h")));
		String eyeCheck = (String)data.get("eyeCheck");
		String tizhi = (String)data.get("tizhi");
		String transferTizhi = null;
		if (StringUtils.isNotEmpty(tizhi)) {
			transferTizhi = getTizhiTransfer(tizhi);
			if (transferTizhi == null) {
				System.out.println("tizhi error:"+customerId+":"+tizhi);
			}
		}
		
		String dm = (String)data.get("dm");
		String htn = (String)data.get("htn");
		Integer riskScore = ParamUtils.getIntValue(String.valueOf(data.get("riskScore")));
		
		String effectCount = (String)countInfo.get("effectCount");
		if (effectCount == null || !"true".equals(effectCount)) {
			if (age != null && bmi != null && waistline != null && highPressure != null && StringUtils.isNotEmpty(familyHistory) && (bloodGlucose != null || bloodGlucose2h != null)) {
				addCount(checkPlace, checkDate, "reportEffect");
				effectCount = "true";
				countInfo.put("effectCount", effectCount);
			}
		}
		String examCount = (String)countInfo.get("examCount");
		if (examCount == null || !"true".equals(examCount)) {
			if (checkTime != null && (temperature!= null || bmi != null 
					|| highPressure != null || lowPressure != null || pulse != null 
					|| oxygen != null || fatContent != null || waistline != null)) {
				addCount(checkPlace, checkDate, "reportExam");
				examCount = "true";
				countInfo.put("examCount", examCount);
			}
		}
		String eyeCheckCount = (String)countInfo.get("eyeCheckCount");
		if (eyeCheckCount == null || !"true".equals(eyeCheckCount)) {
			if (eyeCheck != null && eyeCheck.equals("已检测")) {
				addCount(checkPlace, checkDate, "reportEyeCheck");
				eyeCheckCount = "true";
				countInfo.put("eyeCheckCount", eyeCheckCount);
			}
		}
		String bloodCount = (String)countInfo.get("bloodCount");
		boolean suspectedPrediabetes = false;
		boolean suspectedDiabetes = false;
		boolean isNormal = false;
		String needTransfer = null;
		String needOgtt = null;
		String needComplicate = null;
		if (bloodCount == null || !"true".equals(bloodCount)) {
			if (bloodGlucose != null || bloodGlucose2h != null) {
				addCount(checkPlace, checkDate, "reportBloodGlucose");
				bloodCount = "true";
				countInfo.put("bloodCount", bloodCount);
//				if (disease == null || disease.indexOf("糖尿病") < 0) {
				if (!"是".equals(dm)) {
					if ((bloodGlucose != null && bloodGlucose>=6.1 && bloodGlucose < 7)||(bloodGlucose2h != null && bloodGlucose2h >= 7.8 && bloodGlucose2h < 11.1)) {
						suspectedPrediabetes = true;
						needTransfer = "true";
						needOgtt = "true";
						addCount(checkPlace, checkDate, "reportSuspectedPrediabetes");
					} else if ((bloodGlucose != null && bloodGlucose>=7) || (bloodGlucose2h!= null && bloodGlucose2h >= 11.1)) {
						needTransfer = "true";
						needOgtt = "true";
						suspectedDiabetes = true;
						addCount(checkPlace, checkDate, "reportSuspectedDiabetes");
					} else if (riskScore != null && riskScore >=25) {
						needTransfer = "true";
						needOgtt = "true";
						isNormal = true;
						addCount(checkPlace, checkDate, "reportHighRisk");
					} else {
						isNormal = true;
					}
				}
			}
		}
		String tizhiCount = (String)countInfo.get("tizhiCount");
		if (tizhiCount == null || !"true".equals(tizhiCount)) {
			if (StringUtils.isNotEmpty(tizhi)) {
				addCount(checkPlace, checkDate, "reportTizhi");
				if (transferTizhi != null) {
					addTizhiCount("-", transferTizhi);
					tizhiCount = "true";
					countInfo.put("tizhiCount", tizhiCount);
				}
			}
		}
		boolean isDm = false;
		String dmCount = (String)countInfo.get("dmCount");
		if (dmCount == null || !"true".equals(dmCount)) {
			if (dm != null) {
				dmCount = "true";
				countInfo.put("dmCount", dmCount);
				if (dm.equals("是")) {
					needTransfer = "true";
					needComplicate = "true";
					addCount(checkPlace, checkDate, "reportDiabetes");
					isDm = true;
				}
			}
		}
		boolean isHtn = false;
		String htnCount = (String)countInfo.get("htnCount");
		if (htnCount == null || !"true".equals(htnCount)) {
			if (htn != null) {
				htnCount = "true";
				countInfo.put("htnCount", htnCount);
				if (htn.equals("是")) {
					isHtn = true;
				}
			}
		}
		String pressureCount = (String)countInfo.get("pressureCount");
		if (pressureCount == null || !"true".equals(pressureCount)) {
			if (highPressure != null && lowPressure != null) {
				pressureCount = "true";
				countInfo.put("pressureCount", pressureCount);
				if ("是".equals(htn)) {
					addCount(checkPlace, checkDate, "reportRegisterHighPressure");
				} if ((highPressure != null &&highPressure>=140) || (lowPressure != null &&lowPressure >= 90)) {
					addCount(checkPlace, checkDate, "reportNewHighPressure");
				}
			}
		}
		
		boolean fat = false;
		String fatCount = (String)countInfo.get("fatCount");
		if (fatCount == null || !"true".equals(fatCount)) {
			if (bmi != null &&  fatContent != null) {
				fatCount = "true";
				countInfo.put("fatCount", fatCount);
				if (bmi>=28 || ("男".equals(sex) && fatContent >= 25) || ("女".equals(sex) && fatContent >= 30)) {
					fat = true;
					addCount(checkPlace, checkDate, "reportFat");
				}
			}
		}
		
		String diseaseTizhiCount = (String)countInfo.get("diseaseTizhiCount");
		if (diseaseTizhiCount == null || !"true".equals(diseaseTizhiCount)) {
			if (StringUtils.isNotEmpty(tizhi)) {
				if (transferTizhi != null) {
					if (isDm) {
						addTizhiCount("糖尿病", tizhi);
					}
					if (isHtn) {
						addTizhiCount("高血压", tizhi);
					}
					if (suspectedPrediabetes) {
						addTizhiCount("糖前", tizhi);
					}
					if (fat) {
						addTizhiCount("肥胖", tizhi);
					}
					diseaseTizhiCount = "true";
					countInfo.put("diseaseTizhiCount", diseaseTizhiCount);
				}
			}
		}
		
		if ("true".equals(needTransfer)) {
			countInfo.put("needTransfer", "true");
			addCount(checkPlace, checkDate, "reportTransfer");
		}
		
		if ("true".equals(needOgtt)) {
			countInfo.put("needOgtt", "true");
			addCount(checkPlace, checkDate, "reportOgtt");
		}
		if ("true".equals(needComplicate)) {
			countInfo.put("needComplicate", "true");
			addCount(checkPlace, checkDate, "reportComplicate");
		}
		
		String ageCount = (String)countInfo.get("ageCount");
		if (ageCount == null || !"true".equals(ageCount)) {
			if (age != null) {
				if (isDm || suspectedDiabetes) {
					addAgeCount(age, "糖尿病");
				} else if (suspectedPrediabetes) {
					addAgeCount(age, "糖前");
				} else if (isNormal) {
					addAgeCount(age, "正常");
				}
			}
		}
		
		saveData("countInfo", countInfo);
	}

	private void addCount(String checkPlace, String checkDate, String reportType) {
		if (StringUtils.isEmpty(checkPlace)) {
			checkPlace = "不详";
		}
		Map<String,Object> queryMap = Maps.newHashMap();
		Date checkTime = ParamUtils.getDate(checkDate);
		queryMap.put("checkDate", checkDate);
		queryMap.put("checkPlace", checkPlace);
		Map<String, Object> placeDayCount = queryOne(reportType, queryMap);
		if (placeDayCount == null) {
			placeDayCount = Maps.newHashMap();
			placeDayCount.put("checkPlace", checkPlace);
			placeDayCount.put("checkDate", checkDate);
			placeDayCount.put("checkTime", checkTime);
		}
		Integer count = (Integer)placeDayCount.get("count");
		if (count == null) {
			count = 0;
		}
		count++;
		placeDayCount.put("count", count);
		saveData(reportType, placeDayCount);
		queryMap.put("checkPlace", "-");
		Map<String, Object> dayCount = queryOne(reportType, queryMap);
		if (dayCount == null) {
			dayCount = Maps.newHashMap();
			dayCount.put("checkPlace", "-");
			dayCount.put("checkDate", checkDate);
			dayCount.put("checkTime", checkTime);
		}
		count = (Integer)dayCount.get("count");
		if (count == null) {
			count = 0;
		}
		count++;
		dayCount.put("count", count);
		saveData(reportType, dayCount);
		
		queryMap.put("checkDate", "-");
		queryMap.put("checkPlace", checkPlace);
		Map<String, Object> allPlaceCount = queryOne(reportType, queryMap);
		if (allPlaceCount == null) {
			allPlaceCount = Maps.newHashMap();
			allPlaceCount.put("checkPlace", checkPlace);
			allPlaceCount.put("checkDate", "-");
		}
		count = (Integer)allPlaceCount.get("count");
		if (count == null) {
			count = 0;
		}
		count++;
		allPlaceCount.put("count", count);
		saveData(reportType, allPlaceCount);

		queryMap.put("checkPlace", "-");
		Map<String, Object> allCount = queryOne(reportType, queryMap);
		if (allCount == null) {
			allCount = Maps.newHashMap();
			allCount.put("checkPlace", "-");
			allCount.put("checkDate", "-");
		}
		count = (Integer)allCount.get("count");
		if (count == null) {
			count = 0;
		}
		count++;
		allCount.put("count", count);
		saveData(reportType, allCount);

	}
	
	private String getTizhiTransfer(String tizhi) {
		String[] tizhis = new String[]{
				"平和","气虚","气郁","湿热","痰湿","特禀","血瘀","阳虚","阴虚"	
			};
		for (int i = 0; i < tizhis.length; i++) {
			if (tizhi.trim().startsWith(tizhis[i])) {
				return tizhis[i]+"质";
			}
		}
		return null;
	}
	private void addTizhiCount(String disease, String tizhi) {
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("disease", disease);
		queryMap.put("tizhi", tizhi);
		Map<String, Object> diseaseTizhi = queryOne("diseaseTizhi", queryMap);
		if (diseaseTizhi == null) {
			diseaseTizhi = Maps.newHashMap();
			diseaseTizhi.put("disease", disease);
			diseaseTizhi.put("tizhi", tizhi);
		}
		Integer count = (Integer)diseaseTizhi.get("count");
		if (count == null) {
			count = 0;
		}
		count++;
		diseaseTizhi.put("count", count);
		saveData("diseaseTizhi", diseaseTizhi);
	}
	
	private void addAgeCount(Integer age, String type) {
		if (age < 15) {
			return;
		}
		int scaleStart = ((int)age/5)*5;
		int scaleEnd = scaleStart + 5;
		String scale =  ""+scaleStart+"-"+scaleEnd;
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("age", scale);
		queryMap.put("type", type);
		Map<String, Object> ageCount = queryOne("reportAge", queryMap);
		if (ageCount == null) {
			ageCount = Maps.newHashMap();
			ageCount.put("age", scale);
			ageCount.put("type", type);
		}
		Integer count = (Integer)ageCount.get("count");
		if (count == null) {
			count = 0;
		}
		count++;
		ageCount.put("count", count);
		saveData("reportAge", ageCount);

		queryMap.put("age", "-");
		Map<String, Object> typeCount = queryOne("reportAge", queryMap);
		if (typeCount == null) {
			typeCount = Maps.newHashMap();
			typeCount.put("age", "-");
			typeCount.put("type", type);
		}
		count = (Integer)typeCount.get("count");
		if (count == null) {
			count = 0;
		}
		count++;
		typeCount.put("count", count);
		saveData("reportAge", typeCount);

	}

}
