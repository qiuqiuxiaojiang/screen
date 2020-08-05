package com.capitalbio.healthcheck.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.DateUtil;
import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.exception.BaseException;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.common.util.CommUtil;
import com.capitalbio.common.util.DateUtils;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.healthcheck.dao.CustomerDAO;
import com.capitalbio.healthcheck.dao.HealthCheckDAO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;

@Service
public class CustomerService extends BaseService{
	
	@Autowired CustomerDAO customerDao;
	@Autowired HealthCheckDAO healthCheckDao;
	@Autowired HealthCheckService healthCheckService;
	@Autowired AuthService authService;
	@Autowired GeneratePdfService generatePdfService;
	
	public static String[] indicatorKey = {
		"height", "weight", "waistline", //"pulse", "hipCircumference",
		"temperature", "oxygen", "fatContent",
		"BMI", "pulse", "highPressure", "lowPressure",
		"bloodGlucose", "tc", "tg", "ldl", "hdl"  
	};
	
	/** 筛查报告  糖尿病风险评分字段集合 **/
	private static LinkedHashMap<String, Object> mapDiabetesRiskScoreKey = new LinkedHashMap<String, Object>();
	
	/** 筛查报告  生理生化检测指标字段集合 **/
	private static LinkedHashMap<String, Object> mapPhysiologicalBiochemicalKey = new LinkedHashMap<String, Object>();
	
	/** 筛查报告  风险评估字段集合 **/
	private static LinkedHashMap<String, Object> mapRiskAssessmentKey = new LinkedHashMap<String, Object>();
	
	/** 筛查报告  健康提示字段集合 **/
	private static LinkedHashMap<String, Object> mapHealthTipsKey = new LinkedHashMap<String, Object>();
	
	/** 体格检测字段集合 **/
	private static LinkedHashMap<String, Object> mapPhysicalEaminationKey = new LinkedHashMap<String, Object>();
	
	static {
		mapPhysicalEaminationKey.put("height", "身高（cm）");
		mapPhysicalEaminationKey.put("weight", "体重（kg）");
		mapPhysicalEaminationKey.put("waistline", "腰围（cm）");
		mapPhysicalEaminationKey.put("temperature", "体温（℃）");
		mapPhysicalEaminationKey.put("oxygen", "血氧（%）");
		mapPhysicalEaminationKey.put("fatContent", "体脂率（%）");
		mapPhysicalEaminationKey.put("BMI", "体质指数（kg/m²）");
		mapPhysicalEaminationKey.put("pulse", "脉搏（次/分钟）");
		mapPhysicalEaminationKey.put("highPressure", "收缩压（mmHg）");
		mapPhysicalEaminationKey.put("lowPressure", "舒张压（mmHg）");
		mapPhysicalEaminationKey.put("bloodGlucose", "空腹血糖（mmol/L）");
		mapPhysicalEaminationKey.put("bloodGlucose2h", "餐后两小时血糖（mmol/L）");
		mapPhysicalEaminationKey.put("bloodGlucoseRandom", "随机血糖（mmol/L）");
		mapPhysicalEaminationKey.put("tc", "总胆固醇（mmol/L）");
		mapPhysicalEaminationKey.put("tg", "甘油三酯（mmol/L）");
		mapPhysicalEaminationKey.put("hdl", "高密度脂蛋白胆固醇（mmol/L）");
		mapPhysicalEaminationKey.put("ldl", "低密度脂蛋白胆固醇（mmol/L）");
		
		mapDiabetesRiskScoreKey.put("age", "年龄_-");
		mapDiabetesRiskScoreKey.put("gender", "性别_-");
		mapDiabetesRiskScoreKey.put("familyHistory", "糖尿病家族史_-");
		mapDiabetesRiskScoreKey.put("height", "身高（cm）_-");
		mapDiabetesRiskScoreKey.put("weight", "体重（kg）_-");
		mapDiabetesRiskScoreKey.put("waistline", "腰围（cm）_男<85/女<80");
		mapDiabetesRiskScoreKey.put("BMI", "体质指数（kg/m²）_18.5-23.9");
		mapDiabetesRiskScoreKey.put("pressure", "血压（mmHg）_90-130/60-85");
		
		mapPhysiologicalBiochemicalKey.put("bloodGlucose", "空腹血糖（mmol/L）_3.9-6.1");
		mapPhysiologicalBiochemicalKey.put("bloodGlucose2h", "餐后两小时血糖（mmol/L）_3.9-7.8");
		mapPhysiologicalBiochemicalKey.put("bloodGlucoseRandom", "随机血糖（mmol/L）_3.9-11.1");
		mapPhysiologicalBiochemicalKey.put("tc", "总胆固醇（mmol/L）_<5.2");
		mapPhysiologicalBiochemicalKey.put("hdl", "高密度脂蛋白胆固醇（mmol/L）_≥1.0");
		mapPhysiologicalBiochemicalKey.put("ldl", "低密度脂蛋白胆固醇（mmol/L）_＜3.4");
		mapPhysiologicalBiochemicalKey.put("tg", "甘油三酯（mmol/L）_＜1.7");
		mapPhysiologicalBiochemicalKey.put("oxygen", "血氧（%）_-");
		mapPhysiologicalBiochemicalKey.put("pulse", "脉搏（次/分钟）_-");
		mapPhysiologicalBiochemicalKey.put("temperature", "体温（℃）_-");
		
//		mapRiskAssessmentKey.put("bloodSugarHealthRisk", "血糖情况_bloodSugarResultDesc");
//		mapRiskAssessmentKey.put("bloodLipidHealthRisk", "血脂情况_bloodLipidResultDesc");
//		mapRiskAssessmentKey.put("bloodPressureHealthRisk", "血压情况_bloodPressureResultDesc");
		
		mapRiskAssessmentKey.put("dmRisk", "血糖情况_bloodSugarResultDesc");
		mapRiskAssessmentKey.put("bloodLipidCondition", "血脂情况_bloodLipidResultDesc");
		mapRiskAssessmentKey.put("bloodPressureCondition", "血压情况_bloodPressureResultDesc");
		
		mapHealthTipsKey.put("OGTTTest", "口服葡萄糖耐量试验（OGTT）");
		mapHealthTipsKey.put("bloodLipidTest", "血脂检测");
		mapHealthTipsKey.put("bloodPressureTest", "血压检测");
		mapHealthTipsKey.put("geneTest", "基因检测");
	}

	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		// TODO Auto-generated method stub
		return customerDao;
	}

	@Override
	public String getCollName() {
		// TODO Auto-generated method stub
		return "customer";
	}

	public Map<String, Object> recordCondition(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//建档人数
		Long count = customerDao.count("customer");
		if (count == 0) {
			result.put("count", count);
			return result;
		}
		//建档人数年龄分布
		Map<String, Object> ageCountMap = customerDao.findRecordCountByAge2(district);
		Integer age3540 = 0;
		Integer age4045 = 0;
		Integer age4550 = 0;
		Integer age5055 = 0;
		Integer age5560 = 0;
		Integer age6065 = 0;
		
		Double perc3540 = (double) 0;
		Double perc4045 = (double) 0;
		Double perc4550 = (double) 0;
		Double perc5055 = (double) 0;
		Double perc5560 = (double) 0;
		Double perc6065 = (double) 0;
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		if (ageCountMap != null) {
			if (ageCountMap.get("35-40") != null) {
				age3540 = Integer.parseInt(ageCountMap.get("35-40").toString());
				perc3540 = ParamUtils.doubleScale(age3540.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (ageCountMap.get("40-45") != null) {
				age4045 = Integer.parseInt(ageCountMap.get("40-45").toString());
				perc4045 = ParamUtils.doubleScale(age4045.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (ageCountMap.get("45-50") != null) {
				age4550 = Integer.parseInt(ageCountMap.get("45-50").toString());
				perc4550 = ParamUtils.doubleScale(age4550.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (ageCountMap.get("50-55") != null) {
				age5055 = Integer.parseInt(ageCountMap.get("50-55").toString());
				perc5055 = ParamUtils.doubleScale(age5055.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (ageCountMap.get("55-60") != null) {
				age5560 = Integer.parseInt(ageCountMap.get("55-60").toString());
				perc5560 = ParamUtils.doubleScale(age5560.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (ageCountMap.get("60-65") != null) {
				age6065 = Integer.parseInt(ageCountMap.get("60-65").toString());
				perc6065 = ParamUtils.doubleScale(age6065.doubleValue() / count.doubleValue() * 100, 1);
			}
		}
		
		countList.add(age3540);
		countList.add(age4045);
		countList.add(age4550);
		countList.add(age5055);
		countList.add(age5560);
		countList.add(age6065);
		
		percList.add(perc3540);
		percList.add(perc4045);
		percList.add(perc4550);
		percList.add(perc5055);
		percList.add(perc5560);
		percList.add(perc6065);
		
		result.put("count", count);
		result.put("countList", countList);
		result.put("percList", percList);
		return result;
	}
	
	
	public Map<String, Object> findBloodSugarCondition(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//建档人数
		Long count = customerDao.count("customer");
		if (count == 0) {
			return null;
		}
		
//		Map<String, Object> bloodSugarConditionMap = customerDao.findBloodSugarCondition2(district);
		Map<String, Object> bloodSugarConditionMap = healthCheckDao.findBloodSugarConditionPeopleDistributionHeadthCheck2(district);
		Integer diabetesCount = 0;
		Integer bloodAbnormalCount = 0;
		Integer highRiskCount = 0;
		Integer normalCount = 0;
		
		Double diabetesPerc = (double) 0;
		Double bloodAbnormalPerc = (double) 0;
		Double highRiskPerc = (double) 0;
		Double normalPerc = (double) 0;
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		
		if(bloodSugarConditionMap != null) {
			if (bloodSugarConditionMap.get("糖尿病患者") != null) {
				diabetesCount = Integer.parseInt(bloodSugarConditionMap.get("糖尿病患者").toString());
				diabetesPerc = ParamUtils.doubleScale(diabetesCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodSugarConditionMap.get("血糖异常人群") != null) {
				bloodAbnormalCount = Integer.parseInt(bloodSugarConditionMap.get("血糖异常人群").toString());
				bloodAbnormalPerc = ParamUtils.doubleScale(bloodAbnormalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodSugarConditionMap.get("糖尿病高风险人群") != null) {
				highRiskCount = Integer.parseInt(bloodSugarConditionMap.get("糖尿病高风险人群").toString());
				highRiskPerc = ParamUtils.doubleScale(highRiskCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodSugarConditionMap.get("正常") != null) {
				normalCount = Integer.parseInt(bloodSugarConditionMap.get("正常").toString());
				normalPerc = ParamUtils.doubleScale(normalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
		}
		countList.add(diabetesCount);
		countList.add(bloodAbnormalCount);
		countList.add(highRiskCount);
		countList.add(normalCount);
		
		percList.add(diabetesPerc);
		percList.add(bloodAbnormalPerc);
		percList.add(highRiskPerc);
		percList.add(normalPerc);
		
		Map<String, Object> m = healthCheckService.findBloodSugarConditionAgeDistributionHeadthCheck(district);
		
		result.put("countList", countList);
		result.put("percList", percList);
		result.putAll(m);
		return result;
	}
	
	public Map<String, Object> findBloodPressureCondition(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//建档人数
		Long count = customerDao.count("customer");
		if (count == 0) {
			return null;
		}
		
//		Map<String, Object> bloodPressureCountMap = customerDao.findBloodPressureCount(district);
		Map<String, Object> bloodPressureCountMap = healthCheckDao.findBloodPressureConditionPeopleDistributionHeadthCheck2(district);
		
		Integer highBloodPressCount = 0;
		Integer bloodPressAbnormalCount = 0;
		Integer normalCount = 0;
		
		Double highBloodPressPerc = (double) 0;
		Double bloodPressAbnormalPerc = (double) 0;
		Double normalPerc = (double) 0;
		
		if (bloodPressureCountMap != null) {
			if (bloodPressureCountMap.get("高血压患者") != null) {
				highBloodPressCount = Integer.parseInt(bloodPressureCountMap.get("高血压患者").toString());
				highBloodPressPerc = ParamUtils.doubleScale(highBloodPressCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodPressureCountMap.get("血压异常人群") != null) {
				bloodPressAbnormalCount = Integer.parseInt(bloodPressureCountMap.get("血压异常人群").toString());
				bloodPressAbnormalPerc = ParamUtils.doubleScale(bloodPressAbnormalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodPressureCountMap.get("正常") != null) {
				normalCount = Integer.parseInt(bloodPressureCountMap.get("正常").toString());
				normalPerc = ParamUtils.doubleScale(normalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
		}
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		countList.add(highBloodPressCount);
		countList.add(bloodPressAbnormalCount);
		countList.add(normalCount);
		
		percList.add(highBloodPressPerc);
		percList.add(bloodPressAbnormalPerc);
		percList.add(normalPerc);
		
		Map<String, Object> m = healthCheckService.findBloodPressureConditionAgeDistributionHeadthCheck(district);
		
		result.put("countList", countList);
		result.put("percList", percList);
		result.putAll(m);
		return result;
	}
	
	public Map<String, Object> findBloodLipidCondition(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//建档人数
		Long count = customerDao.count("customer");
		if (count == 0) {
			return null;
		}
		
//		Map<String, Object> bloodLipidCountMap = customerDao.findBloodLipidCount(district);
		Map<String, Object> bloodLipidCountMap = healthCheckDao.findBloodLipidConditionPeopleDistributionHeadthCheck2(district);
		
		Integer bloodLipidHighRiskCount = 0;
		Integer bloodLipidAbnormalCount = 0;
		Integer normalCount = 0;
		
		Double bloodLipidHighRiskPerc = (double) 0;
		Double bloodLipidAbnormalPerc = (double) 0;
		Double normalPerc = (double) 0;
		
		if (bloodLipidCountMap != null) {
			if (bloodLipidCountMap.get("血脂异常高风险人群") != null) {
				bloodLipidHighRiskCount = Integer.parseInt(bloodLipidCountMap.get("血脂异常高风险人群").toString());
				bloodLipidHighRiskPerc = ParamUtils.doubleScale(bloodLipidHighRiskCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodLipidCountMap.get("血脂异常患者") != null) {
				bloodLipidAbnormalCount = Integer.parseInt(bloodLipidCountMap.get("血脂异常患者").toString());
				bloodLipidAbnormalPerc = ParamUtils.doubleScale(bloodLipidAbnormalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (bloodLipidCountMap.get("正常") != null) {
				normalCount = Integer.parseInt(bloodLipidCountMap.get("正常").toString());
				normalPerc = ParamUtils.doubleScale(normalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
		}
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		countList.add(bloodLipidHighRiskCount);
		countList.add(bloodLipidAbnormalCount);
		countList.add(normalCount);
		
		percList.add(bloodLipidHighRiskPerc);
		percList.add(bloodLipidAbnormalPerc);
		percList.add(normalPerc);
		
		Map<String, Object> m = healthCheckService.findBloodLipidConditionAgeDistributionHeadthCheck(district);
		
		result.put("countList", countList);
		result.put("percList", percList);
		result.putAll(m);
		return result;
	}
	
	public Map<String, Object> findFatCondition(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//建档人数
		Long count = customerDao.count("customer");
		if (count == 0) {
			return null;
		}
		
		Map<String, Object> fatCountMap = customerDao.findFatCount(district);
		
		Integer overWeightCount = 0;
		Integer obeseCount = 0;
		Integer normalCount = 0;
		Integer thinCount = 0;
		
		Double overWeightPerc = (double) 0;
		Double obesePerc = (double) 0;
		Double normalPerc = (double) 0;
		Double thinPerc = (double) 0;
		
		if (fatCountMap != null) {
			if (fatCountMap.get("超重人群") != null) {
				overWeightCount = Integer.parseInt(fatCountMap.get("超重人群").toString());
				overWeightPerc = ParamUtils.doubleScale(overWeightCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (fatCountMap.get("肥胖人群") != null) {
				obeseCount = Integer.parseInt(fatCountMap.get("肥胖人群").toString());
				obesePerc = ParamUtils.doubleScale(obeseCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (fatCountMap.get("正常") != null) {
				normalCount = Integer.parseInt(fatCountMap.get("正常").toString());
				normalPerc = ParamUtils.doubleScale(normalCount.doubleValue() / count.doubleValue() * 100, 1);
			}
			
			if (fatCountMap.get("偏瘦") != null) {
				thinCount = Integer.parseInt(fatCountMap.get("偏瘦").toString());
				thinPerc = ParamUtils.doubleScale(thinCount.doubleValue() / count.doubleValue() * 100, 1);
			}
		}
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		countList.add(overWeightCount);
		countList.add(obeseCount);
		countList.add(normalCount);
		countList.add(thinCount);
		
		percList.add(overWeightPerc);
		percList.add(obesePerc);
		percList.add(normalPerc);
		percList.add(thinPerc);
		
		Map<Object, Object> fatCountByAgeMap = customerDao.findFatCountByAge(district);
		
		List<Map<String,Object>> overWeightList = ageList();
		List<Map<String,Object>> obeseList = ageList();
		List<Map<String,Object>> normalList = ageList();
		List<Map<String,Object>> thinList = ageList();
		
		if (fatCountByAgeMap != null) {
			for (Map.Entry<Object, Object> entry : fatCountByAgeMap.entrySet()) { 
				Map<String,String> key = (Map<String,String>)entry.getKey();
				Object value = entry.getValue();
				
				String bmi = key.get("bmi");
				String ages = key.get("ages");
				if (StringUtils.isNotEmpty(bmi) && StringUtils.isNotEmpty(ages) && !ages.equals("-") && !bmi.equals("-")) {
					if (bmi.equals("超重人群")) {
						for (Map<String, Object> overWeight : overWeightList) {
							if (overWeight.keySet().contains(ages)) {
								overWeight.put(ages, value);
								break;
							}
						}
					} else if (bmi.equals("肥胖人群")) {
						for (Map<String, Object> obese : obeseList) {
							if (obese.keySet().contains(ages)) {
								obese.put(ages, value);
								break;
							}
						}
					} else if (bmi.equals("正常")) {
						for (Map<String, Object> normal : normalList) {
							if (normal.keySet().contains(ages)) {
								normal.put(ages, value);
								break;
							}
						}
					} else if (bmi.equals("偏瘦")) {
						for (Map<String, Object> thin : thinList) {
							if (thin.keySet().contains(ages)) {
								thin.put(ages, value);
								break;
							}
						}
					} 
				}
			}
		}
		
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("overWeightList", overWeightList);
		result.put("obeseList", obeseList);
		result.put("normalList", normalList);
		result.put("thinList", thinList);
		return result;
	}
	
	public Map<String, Object> findTizhiCondition(String district) {
		Map<String, Object> result = Maps.newHashMap();
		//建档人数
		Long count = customerDao.count("customer");
		if (count == 0) {
			return null;
		}
		
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿"," 湿热", "血瘀", "气郁", "特禀"};
		String[] diseases = {"糖尿病患者", "高血压患者", "血脂异常患者", "肥胖人群"};
		
		List<Integer> countList = Lists.newArrayList();
		List<Double> percList = Lists.newArrayList();
		List<Integer> diabetesList = Lists.newArrayList();
		List<Integer> bloodPressureList = Lists.newArrayList();
		List<Integer> bloodLipidList = Lists.newArrayList();
		List<Integer> fatList = Lists.newArrayList();
		for (String tizhi : tizhis) {
			Integer tizhiCount = customerDao.getTizhiCount(tizhi, district, "");
			if (tizhiCount == null) {
				tizhiCount = 0;
			}
			Double tizhiPerc = ParamUtils.doubleScale(tizhiCount.doubleValue() / count.doubleValue() * 100, 1);
			countList.add(tizhiCount);
			percList.add(tizhiPerc);
			
			for (String disease : diseases) {
				Integer diseaseCount = customerDao.getTizhiCount(tizhi, district, disease);
				if (disease.equals("糖尿病患者")) {
					diabetesList.add(diseaseCount);
				} else if (disease.equals("高血压患者")) {
					bloodPressureList.add(diseaseCount);
				} else if (disease.equals("血脂异常患者")) {
					bloodLipidList.add(diseaseCount);
				} else if (disease.equals("肥胖人群")) {
					fatList.add(diseaseCount);
				}
			}
		}
		
		result.put("countList", countList);
		result.put("percList", percList);
		result.put("diabetesList", diabetesList);
		result.put("bloodPressureList", bloodPressureList);
		result.put("bloodLipidList", bloodLipidList);
		result.put("fatList", fatList);
		return result;
	}
	
	public List<Map<String,Object>> ageList() {
		List<Map<String,Object>> list = Lists.newArrayList();
		int age = 35;
		for (int i = 1; i <= 6; i ++) {
			Map<String, Object> ageMap = Maps.newHashMap();
			if (age >= 60) {
				ageMap.put("60-65", 0);
			} else {
				int age1 = age + 4;
				ageMap.put(age + "-" + age1, 0);
				age = age1 + 1;
			}
			list.add(ageMap);
		}
		return list;
	}
	
	/**
	 * 基本信息
	 */
	public JSONObject loadBasicInfo(String uniqueId) throws BaseException {
		try {
			
			Map<String, Object> applyToken = authService.applyToken();
			Map<String, Object> userInfo = authService.requestInfoByUniqueId(uniqueId, applyToken.get("token").toString(), applyToken.get("userId").toString());
			
            //r	name	名称
            //	content	内容
            List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
            Map<String, Object> name = new HashMap<String, Object>();
        	name.put("name", "姓名");
        	name.put("content", "-");
        	
            Map<String, Object> gender = new HashMap<String, Object>();
            gender.put("name", "性别");
            gender.put("content", "-");
        	
        	Map<String, Object> id = new HashMap<String, Object>();
        	id.put("name", "身份证号");
        	id.put("content", "-");
        	
        	Map<String, Object> birthday = new HashMap<String, Object>();
        	birthday.put("name", "出生日期");
        	birthday.put("content", "-");
        	
        	Map<String, Object> age = new HashMap<String, Object>();
        	age.put("name", "年龄");
        	age.put("content", "-");
        	
        	Map<String, Object> mobile = new HashMap<String, Object>();
        	mobile.put("name", "手机号");
        	mobile.put("content", "-");
        	
        	/*Map<String, Object> householdRegistrationType = new HashMap<String, Object>();
        	householdRegistrationType.put("name", "常住类型");
        	householdRegistrationType.put("content", "-");*/
        	
        	Map<String, Object> contactName = new HashMap<String, Object>();
         	contactName.put("name", "联系人姓名");
         	contactName.put("content", "-");
         	
         	Map<String, Object> contactPhone = new HashMap<String, Object>();
         	contactPhone.put("name", "联系人电话");
         	contactPhone.put("content", "-");
         	
         	Map<String, Object> address = new HashMap<String, Object>();
        	address.put("name", "地址");
        	address.put("content",  "-");
        	
            if(userInfo!=null&&!"".equals(userInfo)) {
            	name.put("content", packageResult(userInfo.get("name")));
            	gender.put("content", packageResult(userInfo.get("gender")));
            	id.put("content", packageResult(userInfo.get("id")));
            	birthday.put("content", packageResult(userInfo.get("birthday")));
            	age.put("content", packageResult(userInfo.get("age")));
            	mobile.put("content", packageResult(userInfo.get("mobile")));
            	//householdRegistrationType.put("content", packageResult(userInfo.get("householdRegistrationType")));
            	contactName.put("content", packageResult(userInfo.get("contactName")));
            	contactPhone.put("content", packageResult(userInfo.get("contactMobile")));
            	address.put("content", packageResult(userInfo.get("address")));
            }
            
            JSONObject resultJson = new JSONObject();
            
        	Map<String, Object> disease = new HashMap<String, Object>();
        	disease.put("name", "已患疾病");
        	disease.put("content",  "-");
        	
        	Map<String, Object> familyHistory = new HashMap<String, Object>();
        	familyHistory.put("name", "家族病史");
        	familyHistory.put("content", "-");
        	
        	Map<String, Object> nationality = new HashMap<String, Object>();
        	nationality.put("name", "民族");
        	nationality.put("content", "-");
        	
        	Map<String, Object> query = Maps.newHashMap();
        	query.put("uniqueId", uniqueId);
        	Map<String, Object> customer = healthCheckService.getDataByQuery("customer", query);
            if (customer != null ) {
            	String diseaseStr = "";
            	if (customer.get("haveDisease").equals("是")) {
            		diseaseStr = customer.get("disease").toString();
            	} else if(customer.get("haveDisease").equals("否")){
            		diseaseStr = "否";
            	}
            	disease.put("content", packageResult(diseaseStr));
            	
            	String familyDiseaseStr = "";
            	if (customer.get("familyHistory").equals("是")) {
            		familyDiseaseStr = customer.get("familyDisease").toString();
            	} else if(customer.get("familyHistory").equals("否")){
            		familyDiseaseStr = "否";
            	}
            	
            	familyHistory.put("content", packageResult(familyDiseaseStr));
            	nationality.put("content", packageResult(customer.get("nationality")));
            }
            
            resultList.add(name);
            resultList.add(gender);
        	resultList.add(id);
        	resultList.add(birthday);
        	resultList.add(age);
        	resultList.add(mobile);
        	//resultList.add(householdRegistrationType);
            resultList.add(contactName);
        	resultList.add(contactPhone);
        	resultList.add(nationality);
        	resultList.add(disease);
        	resultList.add(familyHistory);
        	resultList.add(address);
        	
            resultJson.put("r", resultList);
			return resultJson;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException("加载用户信息失败");
		}
	}
	
	/**
	 * 信息调查
	 * @param userId
	 * @param projectId
	 * @return
	 * @throws BaseException
	 */
	public JSONObject loadInfoInquiry(String uniqueId) throws BaseException {
		Map<String, Object> query = Maps.newHashMap();
		query.put("uniqueId", uniqueId);
		Map<String, Object> customer = healthCheckService.getDataByQuery("customer", query);
		
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
        
        //既往史
    	Map<String, Object> jws = new HashMap<String, Object>();
    	List<Map<String, Object>> jwsList = new ArrayList<Map<String,Object>>();
    	Map<String, Object> jwsMap1 = new HashMap<String, Object>();
    	jwsMap1.put("name", "药物过敏史");
    	jwsMap1.put("content", "-");
    	
    	Map<String, Object> jwsMap2 = new HashMap<String, Object>();
    	jwsMap2.put("name", "既往疾病史");
    	jwsMap2.put("content", "-");
    	
    	Map<String, Object> jwsMap3 = new HashMap<String, Object>();
    	jwsMap3.put("name", "遗传病史");
    	jwsMap3.put("content", "-");
    	
    	Map<String, Object> jwsMap4 = new HashMap<String, Object>();
    	jwsMap4.put("name", "家族病史（父亲）");
    	jwsMap4.put("content", "-");
    	
    	Map<String, Object> jwsMap5 = new HashMap<String, Object>();
    	jwsMap5.put("name", "家族病史（母亲）");
    	jwsMap5.put("content", "-");
    	
    	Map<String, Object> jwsMap6 = new HashMap<String, Object>();
    	jwsMap6.put("name", "家族病史（兄弟姐妹）");
    	jwsMap6.put("content","-");
    	
    	Map<String, Object> jwsMap7 = new HashMap<String, Object>();
    	jwsMap7.put("name", "家族病史（子女）");
    	jwsMap7.put("content", "-");
    	
    	//体育锻炼
    	Map<String, Object> tydl = new HashMap<String, Object>();
    	List<Map<String, Object>> tydlList = new ArrayList<Map<String,Object>>();
    	Map<String, Object> tydlMap1 = new HashMap<String, Object>();
    	tydlMap1.put("name", "锻炼频率");
    	tydlMap1.put("content", "-");
    	
    	Map<String, Object> tydlMap2 = new HashMap<String, Object>();
    	tydlMap2.put("name", "每次锻炼时间（分钟）");
    	tydlMap2.put("content", "-");
    	
    	Map<String, Object> tydlMap3 = new HashMap<String, Object>();
    	tydlMap3.put("name", "坚持锻炼时间（年）");
    	tydlMap3.put("content", "-");
    	
    	Map<String, Object> tydlMap4 = new HashMap<String, Object>();
    	tydlMap4.put("name", "锻炼方式");
    	tydlMap4.put("content", "-");
    	
    	//饮食习惯
    	Map<String, Object> ysxg = new HashMap<String, Object>();
    	List<Map<String, Object>> ysxgList = new ArrayList<Map<String,Object>>();
    	Map<String, Object> ysxgMap1 = new HashMap<String, Object>();
    	ysxgMap1.put("name", "饮食习惯");
    	ysxgMap1.put("content", "-");
    	
    	//吸烟情况
    	Map<String, Object> xyqk = new HashMap<String, Object>();
    	List<Map<String, Object>> xyqkList = new ArrayList<Map<String,Object>>();
    	Map<String, Object> xyqkMap1 = new HashMap<String, Object>();
    	xyqkMap1.put("name", "吸烟状况");
    	xyqkMap1.put("content", "-");
    	
    	Map<String, Object> xyqkMap2 = new HashMap<String, Object>();
    	xyqkMap2.put("name", "日吸烟量（支）");
    	xyqkMap2.put("content", "-");
    	
    	Map<String, Object> xyqkMap3 = new HashMap<String, Object>();
    	xyqkMap3.put("name", "开始吸烟年龄（岁）");
    	xyqkMap3.put("content", "-");
    	
    	Map<String, Object> xyqkMap4 = new HashMap<String, Object>();
    	xyqkMap4.put("name", "戒烟年龄（岁）");
    	xyqkMap4.put("content", "-");
    	
    	//饮酒情况
    	Map<String, Object> yjqk = new HashMap<String, Object>();
    	List<Map<String, Object>> yjqkList = new ArrayList<Map<String,Object>>();
    	Map<String, Object> yjqkMap1 = new HashMap<String, Object>();
    	yjqkMap1.put("name", "饮酒频率");
    	yjqkMap1.put("content","-");
    	
    	Map<String, Object> yjqkMap2 = new HashMap<String, Object>();
    	yjqkMap2.put("name", "日饮酒量（两）");
    	yjqkMap2.put("content", "-");
    	
    	Map<String, Object> yjqkMap3 = new HashMap<String, Object>();
    	yjqkMap3.put("name", "是否戒酒");
    	yjqkMap3.put("content", "-");
    	
    	Map<String, Object> yjqkMap4 = new HashMap<String, Object>();
    	yjqkMap4.put("name", "开始饮酒年龄（岁）");
    	yjqkMap4.put("content", "-");
    	
    	Map<String, Object> yjqkMap5 = new HashMap<String, Object>();
    	yjqkMap5.put("name", "近一年内是否曾醉酒");
    	yjqkMap5.put("content", "-");
    	
    	Map<String, Object> yjqkMap6 = new HashMap<String, Object>();
    	yjqkMap6.put("name", "饮酒种类");
    	yjqkMap6.put("content", "-");
        
        if (customer != null) {
        	//返回值结构未做处理
//            	r	title	标题
//            		list	name	名称
//            				content	内容
        	//既往史
        	jwsMap1.put("content", packageResult(customer.get("drugAllergy")));
        	jwsMap2.put("content", packageResult(customer.get("pastDisease")));
        	
        	String heredityStr = "";
        	if (customer.get("heredity") != null && !"".equals(customer.get("heredity"))) {
        		String heredity = customer.get("heredity").toString();
        		if (heredity.equals("有")) {
        			if (customer.get("diseaseName") != null && !"".equals(customer.get("diseaseName"))) {
        				heredityStr = customer.get("diseaseName").toString();
        			} else {
        				heredityStr = "有";
        			}
        		} else if(heredity.equals("无")) {
        			heredityStr = "无";
        		}
        	}
        	jwsMap3.put("content", packageResult(heredityStr));
        	jwsMap4.put("content", packageResult(customer.get("heredityFather")));
        	jwsMap5.put("content", packageResult(customer.get("heredityMother")));
        	jwsMap6.put("content", packageResult(customer.get("heredityBs")));
        	jwsMap7.put("content", packageResult(customer.get("heredityChildren")));
        	
        	//体育锻炼
        	tydlMap1.put("content", packageResult(customer.get("frequencyExercise")));
        	tydlMap2.put("content", packageResult(customer.get("exerciseTime")));
        	tydlMap3.put("content", packageResult(customer.get("stickExerciseTime")));
        	tydlMap4.put("content", packageResult(customer.get("exerciseMode")));
        	
        	//饮食习惯
        	ysxgMap1.put("content", packageResult(customer.get("eatingHabits")));
        	
        	//吸烟情况
        	xyqkMap1.put("content", packageResult(customer.get("smokingStatus")));
        	xyqkMap2.put("content", packageResult(customer.get("dailySmoking")));
        	xyqkMap3.put("content", packageResult(customer.get("smokingAge")));
        	xyqkMap4.put("content", packageResult(customer.get("smokingCessation")));
        	
        	//饮酒情况
        	yjqkMap1.put("content", packageResult(customer.get("drinkingFrequency")));
        	yjqkMap2.put("content", packageResult(customer.get("drinkVolume")));
        	yjqkMap3.put("content", packageResult(customer.get("whetherAlcohol")));
        	yjqkMap4.put("content", packageResult(customer.get("drinkingAge")));
        	yjqkMap5.put("content", packageResult(customer.get("drunkPastyear")));
        	yjqkMap6.put("content", packageResult(customer.get("drinkingType")));
            	
        }
        //既往病史
        jwsList.add(jwsMap1);jwsList.add(jwsMap2);jwsList.add(jwsMap3);jwsList.add(jwsMap4);
    	jwsList.add(jwsMap5);jwsList.add(jwsMap6);jwsList.add(jwsMap7);
    	jws.put("title", "既往史");
    	jws.put("list", jwsList);
    	result.add(jws);
    	
    	//体育锻炼
    	tydlList.add(tydlMap1);tydlList.add(tydlMap2);tydlList.add(tydlMap3);tydlList.add(tydlMap4);
    	tydl.put("title", "体育锻炼");
    	tydl.put("list", tydlList);
    	result.add(tydl);
    	
    	//饮食习惯
    	ysxgList.add(ysxgMap1);
    	ysxg.put("title", "饮食习惯");
    	ysxg.put("list", ysxgList);
    	result.add(ysxg);
    	
    	//吸烟情况
    	xyqkList.add(xyqkMap1);xyqkList.add(xyqkMap2);xyqkList.add(xyqkMap3);xyqkList.add(xyqkMap4);
    	xyqk.put("title", "吸烟情况");
    	xyqk.put("list", xyqkList);
    	result.add(xyqk);
    	
    	//饮酒情况
    	yjqkList.add(yjqkMap1);yjqkList.add(yjqkMap2);yjqkList.add(yjqkMap3);yjqkList.add(yjqkMap4);
    	yjqkList.add(yjqkMap5);yjqkList.add(yjqkMap6);
    	yjqk.put("title", "饮酒情况");
    	yjqk.put("list", yjqkList);
    	result.add(yjqk);
    	
    	JSONObject json = new JSONObject();
    	json.put("r", result);
        return json;
	}
	
	
	public JSONObject getScreenReportList(String uniqueId) throws BaseException {
		JSONObject json = new JSONObject();
		List<Object> listResult = new ArrayList<Object>();
		try {
			Map<String, Object> query = Maps.newHashMap();
			query.put("uniqueId", uniqueId);
			for (String pro : indicatorKey) {
				query.put(pro, new BasicDBObject("$exists", true));
			}
			
			Map<String, Object> sortMap = Maps.newHashMap();
			sortMap.put("checkDate", -1);
			List<Map<String, Object>> healthchecks = healthCheckService.queryList("healthcheck", query, sortMap);
			
			for (Map map : healthchecks) {
				Map<String, Object> mapTemp = new HashMap<String, Object>();
				mapTemp.put("checkDate", map.get("checkDate"));
				mapTemp.put("recordId", map.get("_id").toString());
				listResult.add(mapTemp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        json.put("r", listResult);
        return json;
	}
	
	
	public JSONObject getScreenReport(String recordId) throws BaseException {
		JSONObject json = new JSONObject();
		
		try {
			Map<String, Object> query = Maps.newHashMap();
			query.put("_id", new ObjectId(recordId));
			Map<String, Object> userInfo = healthCheckService.getDataByQuery(query);
			if (userInfo != null) {
				Map<String, Object> applyToken = authService.applyToken();
				Map<String, Object> map = authService.requestInfoByUniqueId(userInfo.get("uniqueId").toString(), applyToken.get("token").toString(), applyToken.get("userId").toString());
				map.putAll(userInfo);
				
				json.put("title", CommUtil.getConfigByString("report_title"));
				json.put("name", map.get("name"));
				json.put("id", map.get("customerId"));
				json.put("mobile", map.get("mobile"));
				json.put("checkDate", map.get("checkDate"));
				json.put("classifyResult", getClassifyResult(map));
				json.put("strScore", "评分：" + map.get("riskScore") + "（综合评分≥25分为糖尿病高风险）");
				List<Object> listDiabetesRiskScore = new ArrayList<Object>();
				/** 筛查报告  糖尿病风险评分字段集合 **/
//				mapDiabetesRiskScoreKey.put("age", "年龄_-");
				for (String key : mapDiabetesRiskScoreKey.keySet()) {
					Map<String, Object> mapTemp = new HashMap<String, Object>();
					String[] arrTemp = mapDiabetesRiskScoreKey.get(key).toString().split("_");
					mapTemp.put("inspectionContent",arrTemp[0]);
					mapTemp.put("referenceRange",arrTemp[1]);
					if (key.equals("pressure")) {
						String highPressure = "";
						String lowPressure = "";
						String pressure = "";
						
						if (userInfo.get("highPressure") != null && !"".equals(userInfo.get("highPressure"))) {
							highPressure = userInfo.get("highPressure").toString();
						}
						
						if (userInfo.get("lowPressure") != null && !"".equals(userInfo.get("lowPressure"))) {
							lowPressure = userInfo.get("lowPressure").toString();
						}
						
						if (StringUtils.isEmpty(highPressure) && StringUtils.isEmpty(lowPressure)) {
							pressure = "-";
						} else {
							pressure = highPressure + "/" + lowPressure;
						}
						mapTemp.put("detectionResult", pressure);
					} else if(key.equals("familyHistory")){
						String diabetesFamilyHistory = "";
		            	if (userInfo.get("familyHistory").equals("是")) {
		            		String familyDisease = userInfo.get("familyDisease").toString();
		            		if (familyDisease.contains("糖尿病")) {
		            			diabetesFamilyHistory = "是";
		            		} else {
		            			diabetesFamilyHistory = "否";
		            		}
		            	} else {
		            		diabetesFamilyHistory = "否";
		            	}
		            	mapTemp.put("detectionResult", diabetesFamilyHistory);
					} else {
						mapTemp.put("detectionResult", dealData(key,map));
					}
					
					listDiabetesRiskScore.add(mapTemp);
				}
				json.put("listDiabetesRiskScore", listDiabetesRiskScore);
				List<Object> listPhysiologicalBiochemical = new ArrayList<Object>();
				/** 筛查报告  生理生化检测指标字段集合 **/
//	        mapPhysiologicalBiochemicalKey.put("bloodGlucose", "空腹血糖（mmol/L）_3.9-6.1");
				for (String key : mapPhysiologicalBiochemicalKey.keySet()) {
					Map<String, Object> mapTemp = new HashMap<String, Object>();
					String[] arrTemp = mapPhysiologicalBiochemicalKey.get(key).toString().split("_");
					mapTemp.put("inspectionContent",arrTemp[0]);
					mapTemp.put("referenceRange",arrTemp[1]);
					mapTemp.put("detectionResult", dealData(key,map));
					listPhysiologicalBiochemical.add(mapTemp);
				}
				json.put("listPhysiologicalBiochemical", listPhysiologicalBiochemical);
				List<Object> listRiskAssessment = new ArrayList<Object>();
				/** 筛查报告  风险评估字段集合 **/
//	        mapRiskAssessmentKey.put("bloodSugarHealthRisk", "血糖情况_bloodSugarResultDesc");
				for (String key : mapRiskAssessmentKey.keySet()) {
					
					Map<String, Object> mapTemp = new HashMap<String, Object>();
					String[] arrTemp = mapRiskAssessmentKey.get(key).toString().split("_");
					mapTemp.put("project",arrTemp[0]);
					
					if (key.equals("bloodLipidCondition")) {
						String bloodLipidCondition = dealData(key,map);
						String detectionResult = "";
						if (bloodLipidCondition.equals("血脂异常患者") || bloodLipidCondition.equals("血脂异常高风险人群")) {
							detectionResult = "高";
						} else if (bloodLipidCondition.equals("正常")) {
							detectionResult = "低";
						} else {
							detectionResult = "-";
						}
						mapTemp.put("resultDescription", "-");
						mapTemp.put("detectionResult", detectionResult);
					} else if (key.equals("bloodPressureCondition")) {
						String bloodPressureCondition = dealData(key,map);
						String detectionResult = "";
						if (bloodPressureCondition.equals("高血压患者") || bloodPressureCondition.equals("血压异常人群")) {
							detectionResult = "高";
						} else if (bloodPressureCondition.equals("正常")) {
							detectionResult = "低";
						} else {
							detectionResult = "-";
						}
						mapTemp.put("resultDescription", "-");
						mapTemp.put("detectionResult", detectionResult);
					} else if (key.equals("dmRisk")) {
						mapTemp.put("resultDescription", "");
						mapTemp.put("detectionResult", dealData(key,map));
					}
					
					listRiskAssessment.add(mapTemp);
				}
				json.put("listRiskAssessment", listRiskAssessment);
				List<Object> listHealthTips = new ArrayList<Object>();
				/** 筛查报告  健康提示字段集合 **/
//	        mapHealthTipsKey.put("OGTTTest", "口服葡萄糖耐量试验（OGTT）");
				for (String key : mapHealthTipsKey.keySet()) {
					Map<String, Object> mapTemp = new HashMap<String, Object>();
					mapTemp.put("project",mapHealthTipsKey.get(key));
					mapTemp.put("furtherInspection",dealData(key,map));
					listHealthTips.add(mapTemp);
				}
				json.put("listHealthTips", listHealthTips);
			}
			
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
        return json;
	}
	
	
	public JSONObject getPhysicalEamination(String recordId) throws BaseException{
		JSONObject json = new JSONObject();
		List<Object> listResult = new ArrayList<Object>();
		try {
			Map<String, Object> query = Maps.newHashMap();
			query.put("_id", new ObjectId(recordId));
			Map<String, Object> map = healthCheckService.getDataByQuery(query);
			System.out.println("====================map================"+map);
			for (String key : mapPhysicalEaminationKey.keySet()) {
				Map<String, Object> mapTemp = new HashMap<String, Object>();
				mapTemp.put("name", mapPhysicalEaminationKey.get(key));
				mapTemp.put("content", dealData(key, map));
				listResult.add(mapTemp);
			}
			json.put("r", listResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return json;
	}
	
	public JSONObject getBasicInfoByUniqueid(String uniqueId) throws BaseException{
		JSONObject json = new JSONObject();
		try {
			 Map<String, Object> query = Maps.newHashMap();
			  query.put("uniqueId", uniqueId);
			  Map<String, Object> customer = healthCheckService.getDataByQuery("customer", query);
			  
			  if(customer != null) {
				  json.put("gender", customer.get("gender"));
				  json.put("birthday", customer.get("birthday"));
				  json.put("height", customer.get("height"));
				  json.put("weight", customer.get("weight"));
				  json.put("checkTime", customer.get("checkDate"));
			  }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return json;
	}
	
	public static String  packageResult(Object value) {
		String resultStr = "-";
		if(value!=null) {
			String valString = value.toString();
			if(!"".equals(valString) && !"null".equals(valString)) {
				resultStr = value.toString();
			}
		}
		return resultStr;
	}
	
	
	/** 获取并组装人群结果分类 **/
	private List<Object> getClassifyResult(Map map){
		List<Object> listResult = new ArrayList<Object>();
		try {
			if (map.get("classifyResult") == null ||
					StringUtils.isEmpty(map.get("classifyResult").toString())) {
				Map<String, Object> mapTemp = new HashMap<String,Object>();
				mapTemp.put("result", "等待筛查结果");
				mapTemp.put("riskLevel", "2");
				listResult.add(mapTemp);
				return listResult;			
			}
			
			String classifyResult = map.get("classifyResult").toString();
			if (classifyResult.contains("初筛检测指标无异常")) {
				Map<String, Object> mapTemp = new HashMap<String,Object>();
				mapTemp.put("result", "正常人群");
				mapTemp.put("riskLevel", "2");
				listResult.add(mapTemp);
				return listResult;
			} 
			
			if (classifyResult.contains("检测信息缺失，无法判断")) {
				Map<String, Object> mapTemp = new HashMap<String,Object>();
				mapTemp.put("result", "等待筛查结果");
				mapTemp.put("riskLevel", "2");
				listResult.add(mapTemp);
				return listResult;
			}
			
			String[] arrClassifyResult = classifyResult.split(",");
			for (String str : arrClassifyResult) {
				Map<String, Object> mapTemp = new HashMap<String,Object>();
				mapTemp.put("result", str);
				mapTemp.put("riskLevel", "1");
				listResult.add(mapTemp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listResult;
	}
	
	public JSONObject getClassifyResult(String uniqueId) throws BaseException {
		JSONObject json = new JSONObject();
		try {
			Map<String, Object> query = Maps.newHashMap();
			query.put("uniqueId", uniqueId);
			Map map = healthCheckService.getDataByQuery("customer", query);
			
			List<Object> classifyResult = getClassifyResult(map);
			json.put("r", classifyResult);
		} catch (Exception e) {
			e.printStackTrace();
			
		}        
        return json;
	}
	
	public JSONObject getTizhiInfo(String uniqueId, int pageNo, int pageSize) throws BaseException {
		JSONObject json = new JSONObject();
		try {
			Map<String, Object> query = Maps.newHashMap();
			query.put("uniqueId", uniqueId);
			query.put("tizhi", new BasicDBObject("$exists", true).append("$ne", null).append("$ne", ""));
			
			Page pageData = new Page();
			Integer page = pageNo;
			Integer pageCount = pageSize;
			if (page != null) {
				pageData.setPageNum(page.intValue());
			}
			if (pageCount != null) {
				pageData.setNumPerPage(pageCount);
			}
			
			Map<String, Object> sortMap = Maps.newHashMap();
			sortMap.put("checkDate", -1);
			
			List<Map<String, Object>> result = Lists.newArrayList();
			List<Map<String, Object>> healthchecks = healthCheckService.findPageBySort(pageData, query, sortMap, "healthcheck");
			
			for (int i = (healthchecks.size() -1); i >= 0; i --) {
				Map<String, Object> obj = healthchecks.get(i);
				
				Map<String, Object> map = Maps.newHashMap();
				if (obj.get("checkDate") != null && !"".equals(obj.get("checkDate"))) {
					String checkDate = obj.get("checkDate").toString();
					map.put("time", DateUtils.parseDate(checkDate, DateUtils.PATTERN_DAY).getTime());
				}
				
				List<String> tizhis = Lists.newArrayList();
				if (obj.get("tizhi") != null && !"".equals(obj.get("tizhi"))) {
					String tizhi = obj.get("tizhi").toString();
					if (StringUtils.isNotEmpty(tizhi)) {
						String tizhi1 = tizhi.substring(0, 2);
						tizhis.add(tizhi1);
						
						String tizhiEnd = tizhi.substring(2, tizhi.length());
						if (StringUtils.isNotEmpty(getTizhi(tizhiEnd))) {
							String tizhi2 = getTizhi(tizhiEnd);
							tizhis.add(tizhi2);
						}
					}
				}
				map.put("conclusion", tizhis);
				result.add(map);
			}
			
			json.put("r", result);
		} catch (Exception e) {
			e.printStackTrace();
			
		}        
        return json;
	}
	
	private String dealData(String key, Map map) {
		if (map.get(key) == null || 
				StringUtils.isEmpty(map.get(key).toString())) {
			return "-";
		}
		return map.get(key).toString();
	}
	
	public static String getTizhi(String str) {
		String[] tizhis = {"平和", "气虚", "阳虚", "阴虚", "痰湿","湿热", "血瘀", "气郁", "特禀"};
		for (String tizhi : tizhis) {
			if (str.contains(tizhi)) {
				return tizhi;
			}
		}
		return "";
	}

}
