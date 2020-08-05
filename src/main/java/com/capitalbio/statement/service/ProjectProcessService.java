package com.capitalbio.statement.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.statement.dao.ProjectProcessDao;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
@Service
public class ProjectProcessService extends BaseService{

	@Autowired ProjectProcessDao processDao;
	
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		// TODO Auto-generated method stub
		return processDao;
	}

	@Override
	public String getCollName() {
		// TODO Auto-generated method stub
		return "projectProcess";
	}
	
	public Map<String, Object> getTotalByTz() {
		Map<String, Object> query = Maps.newHashMap();
		query.put("disease", "-");
		//List<Map<String,Object>> diseaseTzs = processDao.queryList("diseaseTizhi", query);
		Map<String, Object> map = processDao.queryMap("diseaseTizhi", query);
		String[] tizhis = {"A平和质","B气虚质", "C阳虚质", "D阴虚质", "E痰湿质", "F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		
		int phz = 0;
		int qxz = 0;
		int yxz = 0;
		int yinz = 0;
		int tsz = 0;
		int srz = 0;
		int xyz = 0;
		int qyz = 0;
		int tbz = 0;
		if (map.get("平和质") != null) {
			phz = Integer.parseInt(map.get("平和质").toString());
		}
		
		if (map.get("气虚质") != null) {
			qxz = Integer.parseInt(map.get("气虚质").toString());
		}
		
		if (map.get("阳虚质") != null) {
			yxz = Integer.parseInt(map.get("阳虚质").toString());
		}
		
		if (map.get("阴虚质") != null) {
			yinz = Integer.parseInt(map.get("阴虚质").toString());
		}
		
		if (map.get("痰湿质") != null) {
			tsz = Integer.parseInt(map.get("痰湿质").toString());
		}
		
		if (map.get("湿热质") != null) {
			srz = Integer.parseInt(map.get("湿热质").toString());
		}
		
		if (map.get("血瘀质") != null) {
			xyz = Integer.parseInt(map.get("血瘀质").toString());
		}
		
		if (map.get("气郁质") != null) {
			qyz = Integer.parseInt(map.get("气郁质").toString());
		}
		
		if (map.get("特禀质") != null) {
			tbz = Integer.parseInt(map.get("特禀质").toString());
		}
		
		int total = phz + qxz + yxz + yinz + tsz + srz + xyz + qyz + tbz;
		Double total1 = (double) 0;
		if (total != 0) {
			total1 = (double) total;
		}
		
		List<Double> tzPerc  = Lists.newArrayList();
		if (total1 != 0) {
			tzPerc.add(ParamUtils.doubleScale(phz / total1.doubleValue() * 100, 1));
			tzPerc.add(ParamUtils.doubleScale(qxz / total1.doubleValue() * 100, 1));
			tzPerc.add(ParamUtils.doubleScale(yxz / total1.doubleValue() * 100, 1));
			tzPerc.add(ParamUtils.doubleScale(yinz / total1.doubleValue() * 100, 1));
			tzPerc.add(ParamUtils.doubleScale(tsz / total1.doubleValue() * 100, 1));
			tzPerc.add(ParamUtils.doubleScale(srz / total1.doubleValue() * 100, 1));
			tzPerc.add(ParamUtils.doubleScale(xyz / total1.doubleValue() * 100, 1));
			tzPerc.add(ParamUtils.doubleScale(qyz / total1.doubleValue() * 100, 1));
			tzPerc.add(ParamUtils.doubleScale(tbz / total1.doubleValue() * 100, 1));
		} else {
			tzPerc.add((double) 0);
			tzPerc.add((double) 0);
			tzPerc.add((double) 0);
			tzPerc.add((double) 0);
			tzPerc.add((double) 0);
			tzPerc.add((double) 0);
			tzPerc.add((double) 0);
			tzPerc.add((double) 0);
			tzPerc.add((double) 0);
		}
		
		
		List<Object> datas = Lists.newArrayList();
		datas.add(phz);
		datas.add(qxz);
		datas.add(yxz);
		datas.add(yinz);
		datas.add(tsz);
		datas.add(srz);
		datas.add(xyz);
		datas.add(qyz);
		datas.add(tbz);
		
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("datas", datas);
		result.put("names", tizhis);
		result.put("tzPerc", tzPerc);
		
		return result;
	}
	
	public Map<String, Object> totalByTz() {
		Map<String, Object> query = Maps.newHashMap();
		query.put("disease", "-");
		List<Map<String,Object>> diseaseTzs = processDao.queryList("diseaseTizhi", query);
		Map<String, Object> map = Maps.newHashMap();
		for (Map<String,Object> obj : diseaseTzs) {
			if (obj.get("count") != null) {
				map.put(obj.get("tizhi").toString(), Integer.parseInt(obj.get("count").toString()));
			} else {
				map.put(obj.get("tizhi").toString(), 0);
			}
			
		}
		return map;
	}
	
	public Map<String, Object> getTzByDisease() {
		return processDao.getTzByDisease("diseaseTizhi");
	}
	
	public Map<String, Object> tzByDisease() throws Exception {
		
		String[] tizhis = {"平和质","气虚质", "阳虚质", "阴虚质", "痰湿质", "湿热质", "血瘀质", "气郁质", "特禀质"};
		String[] tzs = {"A平和质","B气虚质", "C阳虚质", "D阴虚质", "E痰湿质", "F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		
		List<Integer> tnb = Lists.newArrayList();
		List<Integer> tq = Lists.newArrayList();
		List<Integer> gxy = Lists.newArrayList();
		List<Integer> fp = Lists.newArrayList();
		
		for (String tz : tizhis) {
			Map<String, Object> query = Maps.newHashMap();
			query.put("tizhi", tz);
			List<Map<String, Object>> list = queryList(query, "diseaseTizhi");
			for(Map<String, Object> map : list) {
				if (map.get("disease").equals("糖尿病")) {
					tnb.add(Integer.parseInt(map.get("count").toString()));
				} else if (map.get("disease").equals("糖前")) {
					tq.add(Integer.parseInt(map.get("count").toString()));
				} else if (map.get("disease").equals("高血压")) {
					gxy.add(Integer.parseInt(map.get("count").toString()));
				} else if (map.get("disease").equals("肥胖")) {
					fp.add(Integer.parseInt(map.get("count").toString()));
				}
			}
		}
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("tnb", tnb);
		map.put("tq", tq);
		map.put("gxy", gxy);
		map.put("fp", fp);
		map.put("tzs", tzs);
		
		return map;
	}
	
	public Map<String, Object> getColumnData(String disease, Map<String, Object> diseaseMap) {
		Map<String, Object> query = Maps.newHashMap();
		query.put("disease", disease);
		Map<String, Object> map = processDao.queryMap("diseaseTizhi", query);
		
		double disTotal = 0;
		if (diseaseMap != null) {
			if (diseaseMap.get(disease) == null) {
				disTotal = 0;
			} else {
				disTotal = Double.parseDouble(diseaseMap.get(disease).toString());
			}
		}
		
		ArrayList<Double> disPerc = Lists.newArrayList();
		
		String[] tizhis = {"A平和质","B气虚质", "C阳虚质", "D阴虚质", "E痰湿质", "F湿热质", "G血瘀质", "H气郁质", "I特禀质"};
		
		if (map.size() != 0) {
			Double phz = (double) 0;
			Double qxz = (double) 0;
			Double yxz = (double) 0;
			Double yinz = (double) 0;
			Double tsz = (double) 0;
			Double srz = (double) 0;
			Double xyz = (double) 0;
			Double qyz = (double) 0;
			Double tbz = (double) 0;
			if (map.get("平和质") != null) {
				phz = Double.parseDouble(map.get("平和质").toString());
			}
			
			if (map.get("气虚质") != null) {
				qxz = Double.parseDouble(map.get("气虚质").toString());
			}
			
			if (map.get("阳虚质") != null) {
				yxz = Double.parseDouble(map.get("阳虚质").toString());
			}
			
			if (map.get("阴虚质") != null) {
				yinz = Double.parseDouble(map.get("阴虚质").toString());
			}
			
			if (map.get("痰湿质") != null) {
				tsz = Double.parseDouble(map.get("痰湿质").toString());
			}
			
			if (map.get("湿热质") != null) {
				srz = Double.parseDouble(map.get("湿热质").toString());
			}
			
			if (map.get("血瘀质") != null) {
				xyz = Double.parseDouble(map.get("血瘀质").toString());
			}
			
			if (map.get("气郁质") != null) {
				qyz = Double.parseDouble(map.get("气郁质").toString());
			}
			
			if (map.get("特禀质") != null) {
				tbz = Double.parseDouble(map.get("特禀质").toString());
			}
			
			disPerc.add(ParamUtils.doubleScale(phz.doubleValue() / disTotal * 100, 1));
			disPerc.add(ParamUtils.doubleScale(qxz.doubleValue() / disTotal * 100, 1));
			disPerc.add(ParamUtils.doubleScale(yxz.doubleValue() / disTotal * 100, 1));
			disPerc.add(ParamUtils.doubleScale(yinz.doubleValue() / disTotal * 100, 1));
			disPerc.add(ParamUtils.doubleScale(tsz.doubleValue() / disTotal * 100, 1));
			disPerc.add(ParamUtils.doubleScale(srz.doubleValue() / disTotal * 100, 1));
			disPerc.add(ParamUtils.doubleScale(xyz.doubleValue() / disTotal * 100, 1));
			disPerc.add(ParamUtils.doubleScale(qyz.doubleValue() / disTotal * 100, 1));
			disPerc.add(ParamUtils.doubleScale(tbz.doubleValue() / disTotal * 100, 1));
			
		} else {
			disPerc.add((double) 0);
			disPerc.add((double) 0);
			disPerc.add((double) 0);
			disPerc.add((double) 0);
			disPerc.add((double) 0);
			disPerc.add((double) 0);
			disPerc.add((double) 0);
			disPerc.add((double) 0);
			disPerc.add((double) 0);
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("diseases", tizhis);
		result.put("disPerc", disPerc);
		return result;
	}
	
	public Map<String, Object> getDiseaseData(String disease, Map<String, Object> diseaseMap) {
		Map<String, Object> query = Maps.newHashMap();
		query.put("disease", disease);
		List<Map<String, Object>> list = processDao.queryList("diseaseTizhi", query);
		
		double disTotal = 0;
		if (diseaseMap != null) {
			if (diseaseMap.get(disease) == null) {
				disTotal = 0;
			} else {
				disTotal = Double.parseDouble(diseaseMap.get(disease).toString());
			}
		} 
		
		
		
		HashMap<String,Object> obj = Maps.newHashMap();
		for (Map<String, Object> map : list) {
			Double count = Double.parseDouble(map.get("count").toString());
			if (disTotal == 0) {
				obj.put(map.get("tizhi").toString(), 0);
			} else {
				obj.put(map.get("tizhi").toString(), ParamUtils.doubleScale(count.doubleValue() / disTotal * 100, 1));
			}
			
		}
		return obj;
	}
	
	public List<Map<String,Object>> getDayTotal(Map<String, Object> queryMap, String colName) {
		return processDao.getDayTotal(queryMap, colName);
	}
	
	
	public List<Map<String, Object>> getProjectProcessIndex(Set<String> checkPlaces, String checkDate) {
		List<Map<String, Object>> results = Lists.newArrayList();
		for (String place : checkPlaces) {
			HashMap<String, Object> query = Maps.newHashMap();
			query.put("checkPlace", place);
			query.put("checkDate", checkDate);
			
			Map<String, Object> reportInfo = processDao.getDataByQuery("reportInfo", query);
			Map<String, Object> reportEffect = processDao.getDataByQuery("reportEffect", query);
			Map<String, Object> reportBloodGlucose = processDao.getDataByQuery("reportBloodGlucose", query);
			Map<String, Object> reportExam = processDao.getDataByQuery("reportExam", query);
			Map<String, Object> reportEyeCheck = processDao.getDataByQuery("reportEyeCheck", query);
			Map<String, Object> reportEyeUpload = processDao.getDataByQuery("reportEyeUpload", query);//眼象检测（已上传报告）
			Map<String, Object> reportTizhi = processDao.getDataByQuery("reportTizhi", query);
			Map<String, Object> reportComplicate = processDao.getDataByQuery("reportComplicate", query);
			Map<String, Object> reportOgtt = processDao.getDataByQuery("reportOgtt", query);
			Map<String, Object> reportTransfer = processDao.getDataByQuery("reportTransfer", query);
			
			int riDayNum = 0;
			int reDayNum = 0;
			int rbDayNum = 0;
			int rexamDayNum = 0;
			int rcDayNum = 0;
			int ruDayNum = 0;
			int rtDayNum = 0;
			int rCompDayNum = 0;
			int roDayNum = 0;
			int rTransDayNum = 0;
			
			if (reportInfo != null) {
				riDayNum = Integer.parseInt(reportInfo.get("count").toString());
			}
			
			if (reportEffect != null) {
				reDayNum = Integer.parseInt(reportEffect.get("count").toString());
			}
			
			if (reportBloodGlucose != null) {
				rbDayNum = Integer.parseInt(reportBloodGlucose.get("count").toString());
			}
			
			if (reportExam != null) {
				rexamDayNum = Integer.parseInt(reportExam.get("count").toString());
			}
			
			if (reportEyeCheck != null) {
				rcDayNum = Integer.parseInt(reportEyeCheck.get("count").toString());
			}
			
			if (reportEyeUpload != null) {
				ruDayNum = Integer.parseInt(reportEyeUpload.get("count").toString());
			}

			if (reportTizhi != null) {
				rtDayNum = Integer.parseInt(reportTizhi.get("count").toString());
			}
			
			if (reportComplicate != null) {
				rCompDayNum = Integer.parseInt(reportComplicate.get("count").toString());
			}
			
			if (reportOgtt != null) {
				roDayNum = Integer.parseInt(reportOgtt.get("count").toString());
			}
			
			if (reportTransfer != null) {
				rTransDayNum = Integer.parseInt(reportTransfer.get("count").toString());
			}
			
			HashMap<String, Object> obj = Maps.newHashMap();
			obj.put("checkDate", "-");
			obj.put("checkPlace", place);
			Map<String, Object> infoNum = processDao.getDataByQuery("reportInfo", obj);
			Map<String, Object> effectNum = processDao.getDataByQuery("reportEffect", obj);
			Map<String, Object> bloodGlucoseNum = processDao.getDataByQuery("reportBloodGlucose", obj);
			Map<String, Object> examNum = processDao.getDataByQuery("reportExam", obj);
			Map<String, Object> eyeCheckNum = processDao.getDataByQuery("reportEyeCheck", obj);
			Map<String, Object> eyeUploadNum = processDao.getDataByQuery("reportEyeUpload", obj);//眼象检测（已上传报告）
			Map<String, Object> tizhiNum = processDao.getDataByQuery("reportTizhi", obj);
			Map<String, Object> complicateNum = processDao.getDataByQuery("reportComplicate", obj);
			Map<String, Object> ogttNum = processDao.getDataByQuery("reportOgtt", obj);
			Map<String, Object> transferNum = processDao.getDataByQuery("reportTransfer", obj);
			
			int infoAllNum = 0;
			int effecAllNum = 0;
			int bloodGlucoseAllNum = 0;
			int examAllNum = 0;
			int eyeCheckAllNum = 0;
			int eyeUploadAllNum = 0;
			int tizhiAllNum = 0;
			int complicateAllNum = 0;
			int ogttAllNum = 0;
			int transferAllNum = 0;
			
			if(infoNum != null) {
				infoAllNum = Integer.parseInt(infoNum.get("count").toString());
			}
			
			if(effectNum != null) {
				effecAllNum = Integer.parseInt(effectNum.get("count").toString());
			}
			
			if(bloodGlucoseNum != null) {
				bloodGlucoseAllNum = Integer.parseInt(bloodGlucoseNum.get("count").toString());
			}
			
			
			if(examNum != null) {
				examAllNum = Integer.parseInt(examNum.get("count").toString());
			}
			
			if(eyeCheckNum != null) {
				eyeCheckAllNum = Integer.parseInt(eyeCheckNum.get("count").toString());
			}
			
			if(eyeUploadNum != null) {
				eyeUploadAllNum = Integer.parseInt(eyeUploadNum.get("count").toString());
			}
			
			if(tizhiNum != null) {
				tizhiAllNum = Integer.parseInt(tizhiNum.get("count").toString());
			}
			
			if(complicateNum != null) {
				complicateAllNum = Integer.parseInt(complicateNum.get("count").toString());
			}
			
			if(ogttNum != null) {
				ogttAllNum = Integer.parseInt(ogttNum.get("count").toString());
			}
			
			if(transferNum != null) {
				transferAllNum = Integer.parseInt(transferNum.get("count").toString());
			}
			
			HashMap<String, Object> result = Maps.newHashMap();
			result.put("riDayNum", riDayNum);
			result.put("reDayNum", reDayNum);
			result.put("rbDayNum", rbDayNum);
			result.put("rexamDayNum", rexamDayNum);
			result.put("rcDayNum", rcDayNum);
			result.put("ruDayNum", ruDayNum);
			result.put("rtDayNum", rtDayNum);
			result.put("rCompDayNum", rCompDayNum);
			result.put("roDayNum", roDayNum);
			result.put("rTransDayNum", rTransDayNum);
			
			result.put("infoNum", infoAllNum);
			result.put("effectNum", effecAllNum);
			result.put("bloodGlucoseNum", bloodGlucoseAllNum);
			result.put("examNum", examAllNum);
			result.put("eyeCheckNum", eyeCheckAllNum);
			result.put("eyeUploadNum", eyeUploadAllNum);
			result.put("tizhiNum", tizhiAllNum);
			result.put("complicateNum", complicateAllNum);
			result.put("ogttNum", ogttAllNum);
			result.put("transferNum", transferAllNum);
			
			result.put("place", place);
			
			results.add(result);
		}
		
		return results;
	}
	
	
	public List<Map<String, Object>> getNcdIndex2(Set<String> checkPlaces, String checkDate) {
		
		HashMap<String, Object> infoMap = Maps.newHashMap();
		infoMap.put("checkDate", "-");
		infoMap.put("checkPlace", "-");
		Map<String, Object> infoNum = processDao.getDataByQuery("reportInfo", infoMap);

		Double infoAllNum = (double) 0;
		if(infoNum != null) {
			infoAllNum = Double.parseDouble(infoNum.get("count").toString());
		}
		
		List<Map<String, Object>> results = Lists.newArrayList();
		for (String place : checkPlaces) {
			HashMap<String, Object> query = Maps.newHashMap();
			query.put("checkPlace", place);
			query.put("checkDate", checkDate);
			
			Map<String, Object> reportDiabetes = processDao.getDataByQuery("reportDiabetes", query);
			Map<String, Object> reportSuspectedPrediabetes = processDao.getDataByQuery("reportSuspectedPrediabetes", query);
			Map<String, Object> reportSuspectedDiabetes = processDao.getDataByQuery("reportSuspectedDiabetes", query);
			Map<String, Object> reportHighRisk = processDao.getDataByQuery("reportHighRisk", query);
			Map<String, Object> reportFat = processDao.getDataByQuery("reportFat", query);
			Map<String, Object> reportNewHighPressure = processDao.getDataByQuery("reportNewHighPressure", query);
			Map<String, Object> reportRegisterHighPressure = processDao.getDataByQuery("reportRegisterHighPressure", query);
			
			HashMap<String, Object> obj = Maps.newHashMap();
			obj.put("checkDate", "-");
			obj.put("checkPlace", place);
			Map<String, Object> diabetesNum = processDao.getDataByQuery("reportDiabetes", obj);
			Map<String, Object> suspectedPrediabetesNum = processDao.getDataByQuery("reportSuspectedPrediabetes", obj);
			Map<String, Object> suspectedDiabetesNum = processDao.getDataByQuery("reportSuspectedDiabetes", obj);
			Map<String, Object> highRiskNum = processDao.getDataByQuery("reportHighRisk", obj);
			Map<String, Object> fatNum = processDao.getDataByQuery("reportFat", obj);
			Map<String, Object> newHighPressureNum = processDao.getDataByQuery("reportNewHighPressure", obj);
			Map<String, Object> registerHighPressureNum = processDao.getDataByQuery("reportRegisterHighPressure", obj);
			
			
			HashMap<String, Object> result = Maps.newHashMap();
			
			int rdDayNum = 0;
			int rsDayNum = 0;
			int rsdDayNum = 0;
			int rhrDayNum = 0;
			int rfDayNum = 0;
			int nhpDayNum = 0;
			int rhpDayNum = 0;
			
			int rdNum = 0;
			int rsNum = 0;
			int rsdNum = 0;
			int hrNum = 0;
			int fNum = 0;
			int nhpNum = 0;
			int rhpNum = 0;
			
			if (reportDiabetes != null) {
				rdDayNum = Integer.parseInt(reportDiabetes.get("count").toString());
			}
			
			if (reportSuspectedPrediabetes != null) {
				rsDayNum = Integer.parseInt(reportSuspectedPrediabetes.get("count").toString());
			}
			
			if (reportSuspectedDiabetes != null) {
				rsdDayNum = Integer.parseInt(reportSuspectedDiabetes.get("count").toString());
			}
			
			if (reportHighRisk != null) {
				rhrDayNum = Integer.parseInt(reportHighRisk.get("count").toString());
			}
			
			if (reportFat != null) {
				rfDayNum = Integer.parseInt(reportFat.get("count").toString());
			}
			
			if (reportNewHighPressure != null) {
				nhpDayNum = Integer.parseInt(reportNewHighPressure.get("count").toString());
			}
			
			if (reportRegisterHighPressure != null) {
				rhpDayNum = Integer.parseInt(reportRegisterHighPressure.get("count").toString());
			}
			
			if (diabetesNum != null) {
				rdNum = Integer.parseInt(diabetesNum.get("count").toString());
			}
			
			if (suspectedPrediabetesNum != null) {
				rsNum = Integer.parseInt(suspectedPrediabetesNum.get("count").toString());
			}
			
			if (suspectedDiabetesNum != null) {
				rsdNum = Integer.parseInt(suspectedDiabetesNum.get("count").toString());
			}
			
			if (highRiskNum != null) {
				hrNum = Integer.parseInt(highRiskNum.get("count").toString());
			}
			
			if (fatNum != null) {
				fNum = Integer.parseInt(fatNum.get("count").toString());
			}
			
			if (newHighPressureNum != null) {
				nhpNum = Integer.parseInt(newHighPressureNum.get("count").toString());
			}
			
			if (registerHighPressureNum != null) {
				rhpNum = Integer.parseInt(registerHighPressureNum.get("count").toString());
			}
			
			result.put("rdDayNum", rdDayNum);
			result.put("rsDayNum", rsDayNum);
			result.put("rsdDayNum", rsdDayNum);
			result.put("rhrDayNum", rhrDayNum);
			result.put("rfDayNum", rfDayNum);
			result.put("nhpDayNum", nhpDayNum);
			result.put("rhpDayNum", rhpDayNum);
			
			result.put("rdNum", rdNum);
			result.put("rsNum", rsNum);
			result.put("rsdNum", rsdNum);
			result.put("hrNum", hrNum);
			result.put("fNum", fNum);
			result.put("nhpNum", nhpNum);
			result.put("rhpNum", rhpNum);
			
			if (infoAllNum == 0) {
				result.put("rdPerc", 0);
				result.put("rsPerc", 0);
				result.put("rsdPerc", 0);
				result.put("hrPerc", 0);
				result.put("fPerc", 0);
				result.put("nhpPerc", 0);
				result.put("rhpPerc", 0);
			} else {
				result.put("rdPerc", ParamUtils.doubleScale(rdNum / infoAllNum.doubleValue() * 100, 1));
				result.put("rsPerc", ParamUtils.doubleScale(rsNum / infoAllNum.doubleValue() * 100, 1));
				result.put("rsdPerc", ParamUtils.doubleScale(rsdNum / infoAllNum.doubleValue() * 100, 1));
				result.put("hrPerc", ParamUtils.doubleScale(hrNum / infoAllNum.doubleValue() * 100, 1));
				result.put("fPerc", ParamUtils.doubleScale(fNum / infoAllNum.doubleValue() * 100, 1));
				result.put("nhpPerc", ParamUtils.doubleScale(nhpNum / infoAllNum.doubleValue() * 100, 1));
				result.put("rhpPerc", ParamUtils.doubleScale(rhpNum / infoAllNum.doubleValue() * 100, 1));
			}
			
			result.put("place", place);
			
			results.add(result);
		}
		
		return results;
	}
	
	public Set<String> getAllCheckPlace() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("checkDate", "-");
		List<String> reportInfo = processDao.getCheckPlace(map, "reportInfo");  //建档人数
		List<String> reportEffect = processDao.getCheckPlace(map, "reportEffect"); //有效建档人数
		List<String> reportExam = processDao.getCheckPlace(map, "reportExam"); //体格检测
		List<String> reportEyeCheck = processDao.getCheckPlace(map, "reportEyeCheck"); //眼象检测（已检测）
		List<String> reportEyeUpload = processDao.getCheckPlace(map, "reportEyeUpload"); //眼象检测（已上传报告）
		List<String> reportBloodGlucose = processDao.getCheckPlace(map, "reportBloodGlucose"); //血糖检测
		List<String> reportTizhi = processDao.getCheckPlace(map, "reportTizhi"); //中医体质辨识
		List<String> reportComplicate = processDao.getCheckPlace(map, "reportComplicate"); //转诊需求，需要并发症检查人数
		List<String> reportOgtt = processDao.getCheckPlace(map, "reportOgtt"); //转诊需求，需要检测OGTT人数
		List<String> reportTransfer = processDao.getCheckPlace(map, "reportTransfer"); //转诊需求，需要转诊人数，需要基因检测人数
		
		Set<String> set = new HashSet<String>();
		set.addAll(reportInfo);
		set.addAll(reportEffect);
		set.addAll(reportBloodGlucose);
		set.addAll(reportExam);
		set.addAll(reportEyeCheck);
		set.addAll(reportEyeUpload);
		set.addAll(reportTizhi);
		set.addAll(reportComplicate);
		set.addAll(reportOgtt);
		set.addAll(reportTransfer);
		
		return set;
	}
	
	public Set<String> getAllNcdCheckPlace() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("checkDate", "-");
		
		List<String> reportDiabetes = processDao.getCheckPlace(map, "reportDiabetes"); //糖尿病患者
		List<String> reportSuspectedPrediabetes = processDao.getCheckPlace(map, "reportSuspectedPrediabetes"); //疑似糖尿病前期
		List<String> reportSuspectedDiabetes = processDao.getCheckPlace(map, "reportSuspectedDiabetes"); //疑似糖尿病
		List<String> reportHighRisk = processDao.getCheckPlace(map, "reportHighRisk");//糖尿病高危
		List<String> reportEdgeHighPressure = processDao.getCheckPlace(map, "reportEdgeHighPressure");//边缘型高血压
		List<String> reportFat = processDao.getCheckPlace(map, "reportFat");//肥胖人群
		List<String> reportNewHighPressure = processDao.getCheckPlace(map, "reportNewHighPressure");//新发现高血压
		List<String> reportRegisterHighPressure = processDao.getCheckPlace(map, "reportRegisterHighPressure");//登记的高血压

		
		Set<String> set = new HashSet<String>();
		set.addAll(reportDiabetes);
		set.addAll(reportSuspectedPrediabetes);
		set.addAll(reportSuspectedDiabetes);
		set.addAll(reportHighRisk);
		set.addAll(reportEdgeHighPressure);
		set.addAll(reportFat);
		set.addAll(reportNewHighPressure);
		set.addAll(reportRegisterHighPressure);
		
		return set;
	}
	
	public Map<String, Object> totalNum(String checkDate) {
		Map<String, Object> query2 = Maps.newHashMap();
		query2.put("checkDate", checkDate);
		query2.put("checkPlace", "-");
		Map<String, Object> infoDayAllMap = processDao.getDataByQuery("reportInfo", query2);
		Map<String, Object> effectDayAllMap = processDao.getDataByQuery("reportEffect", query2);
		Map<String, Object> bgDayAllMap = processDao.getDataByQuery("reportBloodGlucose", query2);
		Map<String, Object> eDayAllMap = processDao.getDataByQuery("reportExam", query2);
		Map<String, Object> ecDayAllMap = processDao.getDataByQuery("reportEyeCheck", query2);
		Map<String, Object> epDayAllMap = processDao.getDataByQuery("reportEyeUpload", query2);//眼象检测（已上传报告）
		Map<String, Object> rtDayAllMap = processDao.getDataByQuery("reportTizhi", query2);
		Map<String, Object> rcDayAllMap = processDao.getDataByQuery("reportComplicate", query2);
		Map<String, Object> roDayAllMap = processDao.getDataByQuery("reportOgtt", query2);
		Map<String, Object> rTranDayAllMap = processDao.getDataByQuery("reportTransfer", query2);
		
		int infoDayAll = 0;
		int effectDayAll = 0;
		int bgDayAll = 0;
		int eDayAll = 0;
		int ecDayAll = 0;
		int epDayAll = 0;
		int rtDayAll = 0;
		int rcDayAll = 0;
		int roDayAll = 0;
		int rTranDayAll = 0;
		
		if (infoDayAllMap != null) {
			infoDayAll = Integer.parseInt(infoDayAllMap.get("count").toString());
		}
		
		if (effectDayAllMap != null) {
			effectDayAll = Integer.parseInt(effectDayAllMap.get("count").toString());
		}
		
		if (bgDayAllMap != null) {
			bgDayAll = Integer.parseInt(bgDayAllMap.get("count").toString());
		}
		
		if (eDayAllMap != null) {
			eDayAll = Integer.parseInt(eDayAllMap.get("count").toString());
		}

		if (ecDayAllMap != null) {
			ecDayAll = Integer.parseInt(ecDayAllMap.get("count").toString());
		}
		
		if (epDayAllMap != null) {
			epDayAll = Integer.parseInt(epDayAllMap.get("count").toString());
		}
		
		if (rtDayAllMap != null) {
			rtDayAll = Integer.parseInt(rtDayAllMap.get("count").toString());
		}
		
		if (rcDayAllMap != null) {
			rcDayAll = Integer.parseInt(rcDayAllMap.get("count").toString());
		}
		
		if (roDayAllMap != null) {
			roDayAll = Integer.parseInt(roDayAllMap.get("count").toString());
		}
		
		if (rTranDayAllMap != null) {
			rTranDayAll = Integer.parseInt(rTranDayAllMap.get("count").toString());
		}
		
		Map<String, Object> query3 = Maps.newHashMap();
		query3.put("checkDate", "-");
		query3.put("checkPlace", "-");
		Map<String, Object> infoAllMap = processDao.getDataByQuery("reportInfo", query3);
		Map<String, Object> effectAllMap = processDao.getDataByQuery("reportEffect", query3);
		Map<String, Object> bgAllMap = processDao.getDataByQuery("reportBloodGlucose", query3);
		Map<String, Object> eAllMap = processDao.getDataByQuery("reportExam", query3);
		Map<String, Object> ecAllMap = processDao.getDataByQuery("reportEyeCheck", query3);
		Map<String, Object> epAllMap = processDao.getDataByQuery("reportEyeUpload", query3);//眼象检测（已上传报告）
		Map<String, Object> rtAllMap = processDao.getDataByQuery("reportTizhi", query3);
		Map<String, Object> rcAllMap = processDao.getDataByQuery("reportComplicate", query3);
		Map<String, Object> roAllMap = processDao.getDataByQuery("reportOgtt", query3);
		Map<String, Object> rTranAllMap = processDao.getDataByQuery("reportTransfer", query3);
		
		int infoAll = 0;
		int effectAll = 0;
		int bgAll = 0;
		int eAll = 0;
		int ecAll = 0;
		int epAll = 0;
		int rtAll = 0;
		int rcAll = 0;
		int roAll = 0;
		int rTranAll = 0;
		
		if (infoAllMap != null) {
			infoAll = Integer.parseInt(infoAllMap.get("count").toString());
		}
		
		if (effectAllMap != null) {
			effectAll = Integer.parseInt(effectAllMap.get("count").toString());
		}
		
		if (bgAllMap != null) {
			bgAll = Integer.parseInt(bgAllMap.get("count").toString());
		}
		
		if (eAllMap != null) {
			eAll = Integer.parseInt(eAllMap.get("count").toString());
		}

		if (ecAllMap != null) {
			ecAll = Integer.parseInt(ecAllMap.get("count").toString());
		}
		
		if (epAllMap != null) {
			epAll = Integer.parseInt(epAllMap.get("count").toString());
		}
		
		if (rtAllMap != null) {
			rtAll = Integer.parseInt(rtAllMap.get("count").toString());
		}
		
		if (rcAllMap != null) {
			rcAll = Integer.parseInt(rcAllMap.get("count").toString());
		}
		
		if (roAllMap != null) {
			roAll = Integer.parseInt(roAllMap.get("count").toString());
		}
		
		if (rTranAllMap != null) {
			rTranAll = Integer.parseInt(rTranAllMap.get("count").toString());
		}
		
		Map<String, Object> newMap = Maps.newHashMap();
		newMap.put("infoDayAll", infoDayAll);
		newMap.put("effectDayAll", effectDayAll);
		newMap.put("bgDayAll", bgDayAll);
		newMap.put("eDayAll", eDayAll);
		newMap.put("ecDayAll", ecDayAll);
		newMap.put("epDayAll", epDayAll);
		newMap.put("rtDayAll", rtDayAll);
		newMap.put("rcDayAll", rcDayAll);
		newMap.put("roDayAll", roDayAll);
		newMap.put("rTranDayAll", rTranDayAll);
		
		newMap.put("infoAll", infoAll);
		newMap.put("effectAll", effectAll);
		newMap.put("bgAll", bgAll);
		newMap.put("eAll", eAll);
		newMap.put("ecAll", ecAll);
		newMap.put("epAll", epAll);
		newMap.put("rtAll", rtAll);
		newMap.put("rcAll", rcAll);
		newMap.put("roAll", roAll);
		newMap.put("rTranAll", rTranAll);
		
		return newMap;
	}
	
	
	public Map<String, Object> ncdOfTotalNum(String checkDate) {
		Map<String, Object> query2 = Maps.newHashMap();
		query2.put("checkDate", checkDate);
		query2.put("checkPlace", "-");
		Map<String, Object> dDayAllMap = processDao.getDataByQuery("reportDiabetes", query2);
		Map<String, Object> spDayAllMap = processDao.getDataByQuery("reportSuspectedPrediabetes", query2);
		Map<String, Object> sdgDayAllMap = processDao.getDataByQuery("reportSuspectedDiabetes", query2);
		Map<String, Object> hrDayAllMap = processDao.getDataByQuery("reportHighRisk", query2);
		Map<String, Object> fDayAllMap = processDao.getDataByQuery("reportFat", query2);
		Map<String, Object> nhpDayAllMap = processDao.getDataByQuery("reportNewHighPressure", query2);
		Map<String, Object> rhpDayAllMap = processDao.getDataByQuery("reportRegisterHighPressure", query2);
		
		int dDayAll = 0;
		int spDayAll = 0;
		int sdgDayAll = 0;
		int hrDayAll = 0;
		int fDayAll = 0;
		int nhpDayAll = 0;
		int rhpDayAll = 0;
		
		if (dDayAllMap != null) {
			dDayAll = Integer.parseInt(dDayAllMap.get("count").toString());
		}
		
		if (spDayAllMap != null) {
			spDayAll = Integer.parseInt(spDayAllMap.get("count").toString());
		}
		
		if (sdgDayAllMap != null) {
			sdgDayAll = Integer.parseInt(sdgDayAllMap.get("count").toString());
		}
		
		if (hrDayAllMap != null) {
			hrDayAll = Integer.parseInt(hrDayAllMap.get("count").toString());
		}
		
		if (fDayAllMap != null) {
			fDayAll = Integer.parseInt(fDayAllMap.get("count").toString());
		}
		
		if (nhpDayAllMap != null) {
			nhpDayAll = Integer.parseInt(nhpDayAllMap.get("count").toString());
		}
		
		if (rhpDayAllMap != null) {
			rhpDayAll = Integer.parseInt(rhpDayAllMap.get("count").toString());
		}
		
		
		Map<String, Object> query3 = Maps.newHashMap();
		query3.put("checkDate", "-");
		query3.put("checkPlace", "-");
		Map<String, Object> dAllMap = processDao.getDataByQuery("reportDiabetes", query3);
		Map<String, Object> spAllMap = processDao.getDataByQuery("reportSuspectedPrediabetes", query3);
		Map<String, Object> sdgAllMap = processDao.getDataByQuery("reportSuspectedDiabetes", query3);
		Map<String, Object> hrAllMap = processDao.getDataByQuery("reportHighRisk", query3);
		Map<String, Object> fAllMap = processDao.getDataByQuery("reportFat", query3);
		Map<String, Object> nhpAllMap = processDao.getDataByQuery("reportNewHighPressure", query3);
		Map<String, Object> rhpAllMap = processDao.getDataByQuery("reportRegisterHighPressure", query3);
		
		int dAll = 0;
		int spAll = 0;
		int sdgAll = 0;
		int hrAll = 0;
		int fAll = 0;
		int nhpAll = 0;
		int rhpAll = 0;
		
		if (dAllMap != null) {
			dAll = Integer.parseInt(dAllMap.get("count").toString());
		}
		
		if (spAllMap != null) {
			spAll = Integer.parseInt(spAllMap.get("count").toString());
		}
		
		if (sdgAllMap != null) {
			sdgAll = Integer.parseInt(sdgAllMap.get("count").toString());
		}
		
		if (hrAllMap != null) {
			hrAll = Integer.parseInt(hrAllMap.get("count").toString());
		}
		
		if (fAllMap != null) {
			fAll = Integer.parseInt(fAllMap.get("count").toString());
		}
		
		if (nhpAllMap != null) {
			nhpAll = Integer.parseInt(nhpAllMap.get("count").toString());
		}
		
		if (rhpAllMap != null) {
			rhpAll = Integer.parseInt(rhpAllMap.get("count").toString());
		}
		
		
		HashMap<String, Object> infoObj = Maps.newHashMap();
		infoObj.put("checkDate", "-");
		infoObj.put("checkPlace", "-");
		Map<String, Object> infoNum = processDao.getDataByQuery("reportInfo", infoObj);

		Double infoAllNum = (double) 0;
		if(infoNum != null) {
			infoAllNum = Double.parseDouble(infoNum.get("count").toString());
		}
		
		Map<String, Object> newMap = Maps.newHashMap();
		newMap.put("dDayAll", dDayAll);
		newMap.put("spDayAll", spDayAll);
		newMap.put("sdgDayAll", sdgDayAll);
		newMap.put("hrDayAll", hrDayAll);
		newMap.put("fDayAll", fDayAll);
		newMap.put("nhpDayAll", nhpDayAll);
		newMap.put("rhpDayAll", rhpDayAll);
		
		newMap.put("dAll", dAll);
		newMap.put("spAll", spAll);
		newMap.put("sdgAll", sdgAll);
		newMap.put("hrAll", hrAll);
		newMap.put("fAll", fAll);
		newMap.put("nhpAll", nhpAll);
		newMap.put("rhpAll", rhpAll);
		
		if (infoAllNum == 0) {
			newMap.put("rdPerc", 0);
			newMap.put("rsPerc", 0);
			newMap.put("rsdPerc", 0);
			newMap.put("hrPerc", 0);
			newMap.put("fPerc", 0);
			newMap.put("nhpPerc", 0);
			newMap.put("rhpPerc", 0);
		} else {
			newMap.put("rdPerc", ParamUtils.doubleScale(dAll / infoAllNum.doubleValue() * 100, 1));
			newMap.put("rsPerc", ParamUtils.doubleScale(spAll / infoAllNum.doubleValue() * 100, 1));
			newMap.put("rsdPerc", ParamUtils.doubleScale(sdgAll / infoAllNum.doubleValue() * 100, 1));
			newMap.put("hrPerc", ParamUtils.doubleScale(hrAll / infoAllNum.doubleValue() * 100, 1));
			newMap.put("fPerc", ParamUtils.doubleScale(fAll / infoAllNum.doubleValue() * 100, 1));
			newMap.put("nhpPerc", ParamUtils.doubleScale(nhpAll / infoAllNum.doubleValue() * 100, 1));
			newMap.put("rhpPerc", ParamUtils.doubleScale(rhpAll / infoAllNum.doubleValue() * 100, 1));
		}
		
		return newMap;
	}
	
	
	public Map<String, Object> getDataByAge(String type) {
		Map<String, Object> query = Maps.newHashMap();
		query.put("type", type);
		Map<String, Object> map = processDao.getDataByAge(query, "reportAge");
		
		return map;
	}

}
