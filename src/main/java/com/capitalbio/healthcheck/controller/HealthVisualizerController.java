package com.capitalbio.healthcheck.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.FileUtil;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.capitalbio.healthcheck.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;

@Controller
@RequestMapping("/manage/hv")
@SuppressWarnings("unchecked")
public class HealthVisualizerController {
	
	@Autowired
	private HealthCheckService healthCheckService;
	@Autowired ReportService reportService;
	
	@Autowired
	private FileManageService fileManageService;
	
	JedisTemplate template = RedisUtils.getTemplate();
	
	/**
	 * 类经堂血脂仪保存
	 * @param userId
	 * @param userName
	 * @param dataInfo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "saveHealthCheckBloodFatInfo")
	@ResponseBody
	public Message saveHealthCheckBloodFatInfo(String userId,String userName,String dataInfo) throws Exception{
		//{cusAge=48, cusId=4113811888, cusName=张三, cusSex=男, testTime=2018-10-8 8:00, unit=mmol/L, valueCalcLdl=2.6, valueChol=2.6, valueHdlChol=2.6, valueTcHdl=0, valueTrig=2.6}
		//低密度脂蛋白胆固醇：valueCalcLdl=2.6,  总胆固醇：valueChol=2.6,高密度脂蛋白胆固醇：valueHdlChol=2.6, 总胆/高密度比值：valueTcHdl=0,甘油三脂： valueTrig=2.6
		@SuppressWarnings("unused")
		Map<String, Object> returnMap = Maps.newHashMap();
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> dataInfoMap = mapper.readValue(dataInfo, Map.class);
			Map<String,Object> paramsMap = Maps.newHashMap();
			paramsMap.put("name",dataInfoMap.get("cusName").toString());
			
			paramsMap.put("cardId",dataInfoMap.get("mobile").toString());
			String customerId = String.valueOf(dataInfoMap.get("cusId"));
			paramsMap.put("customerId", customerId);
			
			paramsMap.put("age", ParamUtils.getIntValue(String.valueOf(dataInfoMap.get("cusAge"))));
			
			
			String checkDate = (String)dataInfoMap.get("testTime");
			Object checkTimeObj = dataInfoMap.get("checkTime");
			Date checkTime = null;
			if (checkTimeObj != null) {
				if (checkTimeObj instanceof Date) {
					checkTime = (Date)checkTimeObj;
				} else if (checkTimeObj instanceof String) {
					checkTime = DateUtil.stringToDateTime((String)checkTimeObj);
				} else if (checkTimeObj instanceof Long) {
					checkTime = new Date(((Long)checkTimeObj).longValue());
				}
			} 
			if (checkDate == null && checkTime != null) {
				checkDate = DateUtil.dateToString(checkTime);
			} else if (checkDate != null && checkTime == null) {
				checkTime = DateUtil.stringToDate(checkDate);
			}
			if(!StringUtils.isEmpty(checkDate)){
				checkDate = checkDate.split(" ")[0];
			}
			paramsMap.put("checkDate", checkDate);
			paramsMap.put("checkTime", checkTime);
			
			//甘油三酯
			if (dataInfoMap.get("valueTrig") != null) {
				paramsMap.put("tg", dataInfoMap.get("valueTrig"));
			}
			//总胆固醇
			if (dataInfoMap.get("valueChol") != null) {
				paramsMap.put("tc", dataInfoMap.get("valueChol"));
			}
			
			//高密度脂蛋白
			if (dataInfoMap.get("valueHdlChol") != null) {
				paramsMap.put("hdl", dataInfoMap.get("valueHdlChol"));
			}
			//低密度脂蛋白
			if (dataInfoMap.get("valueCalcLdl") != null) {
				paramsMap.put("ldl", dataInfoMap.get("valueCalcLdl"));
			}
			//总胆/高密度比值
			if (dataInfoMap.get("valueTcHdl") != null) {
				paramsMap.put("tcHdRatio", dataInfoMap.get("valueTcHdl"));
			}
			
			
			//血糖
			if (dataInfoMap.get("valueBloodSugar") != null) {
				Double glu = ParamUtils.getDoubleValue(String.valueOf(dataInfoMap.get("valueBloodSugar")));
				String timeType = (String)dataInfoMap.get("timeBloodSugar");
				if ("餐前".equals(timeType)) {
					paramsMap.put("bloodGlucose", glu);
				} else {
					paramsMap.put("bloodGlucose2h", glu);
				}
			}
			
			// save history
			healthCheckService.saveData("healthCheckHistory", paramsMap);
			paramsMap.remove("id");
			paramsMap.remove("_id");
			paramsMap.remove("ctime");
			Map<String,Object> queryMap = Maps.newHashMap();
			queryMap.put("checkDate", checkDate);
			queryMap.put("customerId", customerId);
			
			Map<String,Object> data = healthCheckService.getDataByQuery(queryMap);
			if (data == null) {
				data = Maps.newHashMap();
			}
			data.putAll(paramsMap);
			String id = healthCheckService.saveData(data);
			data.put("id", id);
			reportService.processReport(data);
			
			return Message.success();
		} catch (Exception e) {
			return Message.error(e.toString());
		}
	}
	
	/**
	 * 筛查进度->血压检测人数->舒张压值或收缩压值不为空
	 * http://localhost:8080/screen/manage/hv/findLowHighPressureNotEmpty.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findLowHighPressureNotEmpty")
	@ResponseBody
	public Message findLowHighPressureNotEmpty(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> returnMap = Maps.newHashMap();
		int count = healthCheckService.findLowHighPressureNotEmpty(district);
		returnMap.put("count", count);
		return Message.dataMap(returnMap);
	}
	/**
	 * 筛查进度->血脂检测人数->总胆固醇值TC、甘油三酯值TG、高密度脂蛋白值HDL-C、低密度脂蛋白值LDL-C，以上4项任一不为空
	 * http://localhost:8080/chongqing/manage/hv/findTcTgHdlLdlNotEmpty.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findTcTgHdlLdlNotEmpty")
	@ResponseBody
	public Message findTcTgHdlLdlNotEmpty(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> returnMap = Maps.newHashMap();
		int count = healthCheckService.findTcTgHdlLdlNotEmpty(district);
		returnMap.put("count", count);
		return Message.dataMap(returnMap);
	}
	/**
	 * 筛查进度->OGTT检测人数->OGTT（0h）和OGTT（2h）任一项目不为空
	 * http://localhost:8080/chongqing/manage/hv/findOh2hNotEmpty.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findOh2hNotEmpty")
	@ResponseBody
	public Message findOh2hNotEmpty(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> returnMap = Maps.newHashMap();
		int count = healthCheckService.findOh2hNotEmpty(district);
		returnMap.put("count", count);
		return Message.dataMap(returnMap);
	} 
	/**
	 * 肥胖筛查情况->腰臀比->男性＞0.9  女性＞0.8为中心型肥胖；反之则为正常
	 * http://localhost:8080/chongqing/manage/hv/findFatNumberByWHRAndSex.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findFatNumberByWHRAndSex")
	@ResponseBody
	public Message findFatNumberByWHRAndSex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> returnMap = Maps.newHashMap();
		Map<String,Object> m = healthCheckService.findFatNumberByWHRAndSex(district);
		returnMap.put("result", m);
		return Message.dataMap(returnMap);
	}
	/**
	 * 血糖筛查情况->血糖情况人群分布->初筛数据->正常\糖尿病患者\血糖异常人群\糖尿病高风险人群 36-39
	 * http://localhost:8080/screen/manage/hv/findBloodSugarConditionPeopleDistributionHeadthCheck.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodSugarConditionPeopleDistributionHeadthCheck")
	@ResponseBody
	public Message findBloodSugarConditionPeopleDistributionHeadthCheck(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> returnMap = Maps.newHashMap();
		Map<String,Object> m = healthCheckService.findBloodSugarConditionPeopleDistributionHeadthCheck(district);
		returnMap.put("result", m);
		return Message.dataMap(returnMap);
	}
	/**
	 * 血糖筛查情况->血糖情况人群分布->精筛数据->糖尿病高风险人群\疑似糖尿病\疑似糖尿病前期40-42
	 * http://localhost:8080/chongqing/manage/hv/findBloodSugarConditionPeopleDistributionHeadthCheckDetail.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodSugarConditionPeopleDistributionHeadthCheckDetail")
	@ResponseBody
	public Message findBloodSugarConditionPeopleDistributionHeadthCheckDetail(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> returnMap = Maps.newHashMap();
		Map<String,Object> m = healthCheckService.findBloodSugarConditionPeopleDistributionHeadthCheckDetail(district);
		returnMap.put("result", m);
		return Message.dataMap(returnMap);
	}
	/**
	 * 血糖筛查情况-> 血糖情况年龄分布->初筛数据->正常\糖尿病患者\血糖异常人群\糖尿病高风险人群 36-39
	 * http://localhost:8080/screen/manage/hv/findBloodSugarConditionAgeDistributionHeadthCheck.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodSugarConditionAgeDistributionHeadthCheck")
	@ResponseBody
	public Message findBloodSugarConditionAgeDistributionHeadthCheck(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> map = healthCheckService.findBloodSugarConditionAgeDistributionHeadthCheck(district);
		return Message.dataMap(map);
	}
	
	/**
	 * 血糖筛查情况->血糖情况年龄分布->精筛数据->糖尿病高风险人群\疑似糖尿病\疑似糖尿病前期46-48
	 * http://localhost:8080/chongqing/manage/hv/findBloodSugarConditionAgeDistributionHeadthCheckDetail.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodSugarConditionAgeDistributionHeadthCheckDetail")
	@ResponseBody
	public Message findBloodSugarConditionAgeDistributionHeadthCheckDetail(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> returnMap = Maps.newHashMap();
		Map<Object,Object> m = healthCheckService.findBloodSugarConditionAgeDistributionHeadthCheckDetail(district);
		
		Map<String,List<Map<String,Object>>> m2Change = Maps.newHashMap();
		List<Map<String,Object>> ageList1 = new ArrayList<Map<String,Object>>();
		Map<String, Object> map20251 = Maps.newHashMap();
		map20251.put("20-25", 0);
		ageList1.add(map20251);
		Map<String, Object> map25301 = Maps.newHashMap();
		map25301.put("25-30", 0);
		ageList1.add(map25301);
		Map<String, Object> map30351 = Maps.newHashMap();
		map30351.put("30-35", 0);
		ageList1.add(map30351);
		Map<String, Object> map35401 = Maps.newHashMap();
		map35401.put("35-40", 0);
		ageList1.add(map35401);
		Map<String, Object> map40451 = Maps.newHashMap();
		map40451.put("40-45", 0);
		ageList1.add(map40451);
		Map<String, Object> map45501 = Maps.newHashMap();
		map45501.put("45-50", 0);
		ageList1.add(map45501);
		Map<String, Object> map50551 = Maps.newHashMap();
		map50551.put("50-55", 0);
		ageList1.add(map50551);
		Map<String, Object> map55601 = Maps.newHashMap();
		map55601.put("55-60", 0);
		ageList1.add(map55601);
		Map<String, Object> map60651 = Maps.newHashMap();
		map60651.put("60-65", 0);
		ageList1.add(map60651);
		Map<String, Object> map65701 = Maps.newHashMap();
		map65701.put("65-70", 0);
		ageList1.add(map65701);
		Map<String, Object> map70751 = Maps.newHashMap();
		map70751.put("70-75", 0);
		ageList1.add(map70751);
		Map<String, Object> map75801 = Maps.newHashMap();
		map75801.put("75-80", 0);
		ageList1.add(map75801);
		Map<String, Object> map80851 = Maps.newHashMap();
		map80851.put("80-85", 0);
		ageList1.add(map80851);
		Map<String, Object> map85901 = Maps.newHashMap();
		map85901.put("85-90", 0);
		ageList1.add(map85901);
		Map<String, Object> map90951 = Maps.newHashMap();
		map90951.put("90-95", 0);
		ageList1.add(map90951);
		Map<String, Object> map951001 = Maps.newHashMap();
		map951001.put("95-100", 0);
		ageList1.add(map951001);
		
		List<Map<String,Object>> ageList2 = new ArrayList<Map<String,Object>>();
		Map<String, Object> map20252 = Maps.newHashMap();
		map20252.put("20-25", 0);
		ageList2.add(map20252);
		Map<String, Object> map25302 = Maps.newHashMap();
		map25302.put("25-30", 0);
		ageList2.add(map25302);
		Map<String, Object> map30352 = Maps.newHashMap();
		map30352.put("30-35", 0);
		ageList2.add(map30352);
		Map<String, Object> map35402 = Maps.newHashMap();
		map35402.put("35-40", 0);
		ageList2.add(map35402);
		Map<String, Object> map40452 = Maps.newHashMap();
		map40452.put("40-45", 0);
		ageList2.add(map40452);
		Map<String, Object> map45502 = Maps.newHashMap();
		map45502.put("45-50", 0);
		ageList2.add(map45502);
		Map<String, Object> map50552 = Maps.newHashMap();
		map50552.put("50-55", 0);
		ageList2.add(map50552);
		Map<String, Object> map55602 = Maps.newHashMap();
		map55602.put("55-60", 0);
		ageList2.add(map55602);
		Map<String, Object> map60652 = Maps.newHashMap();
		map60652.put("60-65", 0);
		ageList2.add(map60652);
		Map<String, Object> map65702 = Maps.newHashMap();
		map65702.put("65-70", 0);
		ageList2.add(map65702);
		Map<String, Object> map70752 = Maps.newHashMap();
		map70752.put("70-75", 0);
		ageList2.add(map70752);
		Map<String, Object> map75802 = Maps.newHashMap();
		map75802.put("75-80", 0);
		ageList2.add(map75802);
		Map<String, Object> map80852 = Maps.newHashMap();
		map80852.put("80-85", 0);
		ageList2.add(map80852);
		Map<String, Object> map85902 = Maps.newHashMap();
		map85902.put("85-90", 0);
		ageList2.add(map85902);
		Map<String, Object> map90952 = Maps.newHashMap();
		map90952.put("90-95", 0);
		ageList2.add(map90952);
		Map<String, Object> map951002 = Maps.newHashMap();
		map951002.put("95-100", 0);
		ageList2.add(map951002);
		
		List<Map<String,Object>> ageList3 = new ArrayList<Map<String,Object>>();
		Map<String, Object> map20253 = Maps.newHashMap();
		map20253.put("20-25", 0);
		ageList3.add(map20253);
		Map<String, Object> map25303 = Maps.newHashMap();
		map25303.put("25-30", 0);
		ageList3.add(map25303);
		Map<String, Object> map30353 = Maps.newHashMap();
		map30353.put("30-35", 0);
		ageList3.add(map30353);
		Map<String, Object> map35403 = Maps.newHashMap();
		map35403.put("35-40", 0);
		ageList3.add(map35403);
		Map<String, Object> map40453 = Maps.newHashMap();
		map40453.put("40-45", 0);
		ageList3.add(map40453);
		Map<String, Object> map45503 = Maps.newHashMap();
		map45503.put("45-50", 0);
		ageList3.add(map45503);
		Map<String, Object> map50553 = Maps.newHashMap();
		map50553.put("50-55", 0);
		ageList3.add(map50553);
		Map<String, Object> map55603 = Maps.newHashMap();
		map55603.put("55-60", 0);
		ageList3.add(map55603);
		Map<String, Object> map60653 = Maps.newHashMap();
		map60653.put("60-65", 0);
		ageList3.add(map60653);
		Map<String, Object> map65703 = Maps.newHashMap();
		map65703.put("65-70", 0);
		ageList3.add(map65703);
		Map<String, Object> map70753 = Maps.newHashMap();
		map70753.put("70-75", 0);
		ageList3.add(map70753);
		Map<String, Object> map75803 = Maps.newHashMap();
		map75803.put("75-80", 0);
		ageList3.add(map75803);
		Map<String, Object> map80853 = Maps.newHashMap();
		map80853.put("80-85", 0);
		ageList3.add(map80853);
		Map<String, Object> map85903 = Maps.newHashMap();
		map85903.put("85-90", 0);
		ageList3.add(map85903);
		Map<String, Object> map90953 = Maps.newHashMap();
		map90953.put("90-95", 0);
		ageList3.add(map90953);
		Map<String, Object> map951003 = Maps.newHashMap();
		map951003.put("95-100", 0);
		ageList3.add(map951003);
		
		m2Change.put("糖尿病高风险人群", ageList1);
		m2Change.put("疑似糖尿病", ageList2);
		m2Change.put("疑似糖尿病前期", ageList3);
		for (Map.Entry<Object, Object> entry : m.entrySet()) { 
			Map<String,String> key = (Map<String,String>)entry.getKey();
			Object value = entry.getValue();
			String ds = key.get("ds");
			String ages = key.get("ages");
			if(!StringUtils.isEmpty(ds) && !StringUtils.isEmpty(ages)){
				switch (ds) {
				case "糖尿病高风险人群":
					List<Map<String,Object>> mma1= m2Change.get("糖尿病高风险人群");
					for(int i =0;i<mma1.size();i++){
						Map<String,Object> map = mma1.get(i);
						if(map.keySet().contains(ages)){
							mma1.get(i).put(ages, value);
						}
					}
					break;
				case "疑似糖尿病":
					List<Map<String,Object>> mma2= m2Change.get("疑似糖尿病");
					for(int i =0;i<mma2.size();i++){
						Map<String,Object> map = mma2.get(i);
						if(map.keySet().contains(ages)){
							mma2.get(i).put(ages, value);
						}
					}
					break;
				case "疑似糖尿病前期":
					List<Map<String,Object>> mma3= m2Change.get("疑似糖尿病前期");
					for(int i =0;i<mma3.size();i++){
						Map<String,Object> map = mma3.get(i);
						if(map.keySet().contains(ages)){
							mma3.get(i).put(ages, value);
						}
					}
					break;
				default:
					break;
				}
			}
		}
		returnMap.put("result", m2Change);
		return Message.dataMap(returnMap);
	}
	/**
	 * 血压筛查情况->血压情况人群分布->初筛数据->正常\高血压患者\血压异常人群51-53
	 * http://localhost:8080/screen/manage/hv/findBloodPressureConditionPeopleDistributionHeadthCheck.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodPressureConditionPeopleDistributionHeadthCheck")
	@ResponseBody
	public Message findBloodPressureConditionPeopleDistributionHeadthCheck(HttpServletRequest request) throws Exception {
		Map<String, Object> returnMap = Maps.newHashMap();
		String district = request.getParameter("district");
		Map<String,Object> m = healthCheckService.findBloodPressureConditionPeopleDistributionHeadthCheck(district);
		returnMap.put("result", m);
		return Message.dataMap(returnMap);
	}
	/**
	 * 血压筛查情况->血压情况人群分布->精筛数据->血压异常人群\新发现高血压54-55
	 * http://localhost:8080/chongqing/manage/hv/findBloodPressureConditionPeopleDistributionHeadthCheckDetail.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodPressureConditionPeopleDistributionHeadthCheckDetail")
	@ResponseBody
	public Message findBloodPressureConditionPeopleDistributionHeadthCheckDetail(HttpServletRequest request) throws Exception {
		Map<String, Object> returnMap = Maps.newHashMap();
		String district = request.getParameter("district");
		Map<String,Object> m = healthCheckService.findBloodPressureConditionPeopleDistributionHeadthCheckDetail(district);
		returnMap.put("result", m);
		return Message.dataMap(returnMap);
	}
	/**
	 * 血压筛查情况->血压情况年龄分布->初筛数据->正常\高血压患者\血压异常人群51-53
	 * http://localhost:8080/screen/manage/hv/findBloodPressureConditionAgeDistributionHeadthCheck.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodPressureConditionAgeDistributionHeadthCheck")
	@ResponseBody
	public Message findBloodPressureConditionAgeDistributionHeadthCheck(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String,Object> map = healthCheckService.findBloodPressureConditionAgeDistributionHeadthCheck(district);
		return Message.dataMap(map);
	}
	/**
	 * 血压筛查情况->血压情况年龄分布->精筛数据->血压异常人群\新发现高血压59-60
	 * http://localhost:8080/chongqing/manage/hv/findBloodPressureConditionAgeDistributionHeadthCheckDetail.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodPressureConditionAgeDistributionHeadthCheckDetail")
	@ResponseBody
	public Message findBloodPressureConditionAgeDistributionHeadthCheckDetail(HttpServletRequest request) throws Exception {
		Map<String, Object> returnMap = Maps.newHashMap();
		String district = request.getParameter("district");
		Map<Object,Object> m = healthCheckService.findBloodPressureConditionAgeDistributionHeadthCheckDetail(district);
		//{{ "bpc" : "血压异常人群" , "ages" : "55-60"}=1, { "bpc" : "血压异常人群" , "ages" : "25-30"}=1, { "bpc" : "血压异常人群" , "ages" : "20-25"}=1, { "bpc" : "新发现高血压" , "ages" : "40-45"}=1, { "bpc" : "血压异常人群" , "ages" : "80-85"}=1, { "bpc" : "新发现高血压" , "ages" : "-"}=1, { "bpc" : "血压异常人群" , "ages" : "30-35"}=2}
		Map<String,List<Map<String,Object>>> m2Change = Maps.newHashMap();
		List<Map<String,Object>> ageList1 = new ArrayList<Map<String,Object>>();
		Map<String, Object> map20251 = Maps.newHashMap();
		map20251.put("20-25", 0);
		ageList1.add(map20251);
		Map<String, Object> map25301 = Maps.newHashMap();
		map25301.put("25-30", 0);
		ageList1.add(map25301);
		Map<String, Object> map30351 = Maps.newHashMap();
		map30351.put("30-35", 0);
		ageList1.add(map30351);
		Map<String, Object> map35401 = Maps.newHashMap();
		map35401.put("35-40", 0);
		ageList1.add(map35401);
		Map<String, Object> map40451 = Maps.newHashMap();
		map40451.put("40-45", 0);
		ageList1.add(map40451);
		Map<String, Object> map45501 = Maps.newHashMap();
		map45501.put("45-50", 0);
		ageList1.add(map45501);
		Map<String, Object> map50551 = Maps.newHashMap();
		map50551.put("50-55", 0);
		ageList1.add(map50551);
		Map<String, Object> map55601 = Maps.newHashMap();
		map55601.put("55-60", 0);
		ageList1.add(map55601);
		Map<String, Object> map60651 = Maps.newHashMap();
		map60651.put("60-65", 0);
		ageList1.add(map60651);
		Map<String, Object> map65701 = Maps.newHashMap();
		map65701.put("65-70", 0);
		ageList1.add(map65701);
		Map<String, Object> map70751 = Maps.newHashMap();
		map70751.put("70-75", 0);
		ageList1.add(map70751);
		Map<String, Object> map75801 = Maps.newHashMap();
		map75801.put("75-80", 0);
		ageList1.add(map75801);
		Map<String, Object> map80851 = Maps.newHashMap();
		map80851.put("80-85", 0);
		ageList1.add(map80851);
		Map<String, Object> map85901 = Maps.newHashMap();
		map85901.put("85-90", 0);
		ageList1.add(map85901);
		Map<String, Object> map90951 = Maps.newHashMap();
		map90951.put("90-95", 0);
		ageList1.add(map90951);
		Map<String, Object> map951001 = Maps.newHashMap();
		map951001.put("95-100", 0);
		ageList1.add(map951001);
		
		List<Map<String,Object>> ageList2 = new ArrayList<Map<String,Object>>();
		Map<String, Object> map20252 = Maps.newHashMap();
		map20252.put("20-25", 0);
		ageList2.add(map20252);
		Map<String, Object> map25302 = Maps.newHashMap();
		map25302.put("25-30", 0);
		ageList2.add(map25302);
		Map<String, Object> map30352 = Maps.newHashMap();
		map30352.put("30-35", 0);
		ageList2.add(map30352);
		Map<String, Object> map35402 = Maps.newHashMap();
		map35402.put("35-40", 0);
		ageList2.add(map35402);
		Map<String, Object> map40452 = Maps.newHashMap();
		map40452.put("40-45", 0);
		ageList2.add(map40452);
		Map<String, Object> map45502 = Maps.newHashMap();
		map45502.put("45-50", 0);
		ageList2.add(map45502);
		Map<String, Object> map50552 = Maps.newHashMap();
		map50552.put("50-55", 0);
		ageList2.add(map50552);
		Map<String, Object> map55602 = Maps.newHashMap();
		map55602.put("55-60", 0);
		ageList2.add(map55602);
		Map<String, Object> map60652 = Maps.newHashMap();
		map60652.put("60-65", 0);
		ageList2.add(map60652);
		Map<String, Object> map65702 = Maps.newHashMap();
		map65702.put("65-70", 0);
		ageList2.add(map65702);
		Map<String, Object> map70752 = Maps.newHashMap();
		map70752.put("70-75", 0);
		ageList2.add(map70752);
		Map<String, Object> map75802 = Maps.newHashMap();
		map75802.put("75-80", 0);
		ageList2.add(map75802);
		Map<String, Object> map80852 = Maps.newHashMap();
		map80852.put("80-85", 0);
		ageList2.add(map80852);
		Map<String, Object> map85902 = Maps.newHashMap();
		map85902.put("85-90", 0);
		ageList2.add(map85902);
		Map<String, Object> map90952 = Maps.newHashMap();
		map90952.put("90-95", 0);
		ageList2.add(map90952);
		Map<String, Object> map951002 = Maps.newHashMap();
		map951002.put("95-100", 0);
		ageList2.add(map951002);
		
		m2Change.put("血压异常人群", ageList1);
		m2Change.put("新发现高血压", ageList2);
		for (Map.Entry<Object, Object> entry : m.entrySet()) { 
			Map<String,String> key = (Map<String,String>)entry.getKey();
			Object value = entry.getValue();
			String bpc = key.get("bpc");
			String ages = key.get("ages");
			if(!StringUtils.isEmpty(bpc) && !StringUtils.isEmpty(ages)){
				switch (bpc) {
				case "血压异常人群":
					List<Map<String,Object>> mma1= m2Change.get("血压异常人群");
					for(int i =0;i<mma1.size();i++){
						Map<String,Object> map = mma1.get(i);
						if(map.keySet().contains(ages)){
							mma1.get(i).put(ages, value);
						}
					}
					break;
				case "新发现高血压":
					List<Map<String,Object>> mma2= m2Change.get("新发现高血压");
					for(int i =0;i<mma2.size();i++){
						Map<String,Object> map = mma2.get(i);
						if(map.keySet().contains(ages)){
							mma2.get(i).put(ages, value);
						}
					}
					break;
				default:
					break;
				}
			}
		}
		returnMap.put("result", m2Change);
		return Message.dataMap(returnMap);
	}
	/**
	 * 血脂筛查情况->血脂情况人群分布->初筛数据->正常\血脂异常人群\血脂异常高风险人群61-63
	 * http://localhost:8080/chongqing/manage/hv/findBloodLipidConditionPeopleDistributionHeadthCheck.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodLipidConditionPeopleDistributionHeadthCheck")
	@ResponseBody
	public Message findBloodLipidConditionPeopleDistributionHeadthCheck(HttpServletRequest request) throws Exception {
		Map<String, Object> returnMap = Maps.newHashMap();
		String district = request.getParameter("district");
		Map<String,Object> m = healthCheckService.findBloodLipidConditionPeopleDistributionHeadthCheck(district);
		returnMap.put("result", m);
		return Message.dataMap(returnMap);
	}
	/**
	 * 血脂筛查情况->血脂情况人群分布->精筛数据->血脂异常高风险人群\新发现高血脂64-65
	 * http://localhost:8080/chongqing/manage/hv/findBloodLipidConditionPeopleDistributionHeadthCheckDetail.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodLipidConditionPeopleDistributionHeadthCheckDetail")
	@ResponseBody
	public Message findBloodLipidConditionPeopleDistributionHeadthCheckDetail(HttpServletRequest request) throws Exception {
		Map<String, Object> returnMap = Maps.newHashMap();
		String district = request.getParameter("district");
		Map<String,Object> m = healthCheckService.findBloodLipidConditionPeopleDistributionHeadthCheckDetail(district);
		returnMap.put("result", m);
		return Message.dataMap(returnMap);
	}
	/**
	 * 血脂筛查情况->血脂情况年龄分布->初筛数据->正常\血脂异常人群\血脂异常高风险人群61-63
	 * http://localhost:8080/chongqing/manage/hv/findBloodLipidConditionAgeDistributionHeadthCheck.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodLipidConditionAgeDistributionHeadthCheck")
	@ResponseBody
	public Message findBloodLipidConditionAgeDistributionHeadthCheck(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String,Object> map = healthCheckService.findBloodLipidConditionAgeDistributionHeadthCheck(district);
		return Message.dataMap(map);
	}
	/**
	 * 血脂筛查情况->血脂情况人群分布->初筛数据->新发现高血脂\血脂异常69-70
	 * http://localhost:8080/chongqing/manage/hv/findBloodLipidConditionAgeDistributionHeadthCheckDetail.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodLipidConditionAgeDistributionHeadthCheckDetail")
	@ResponseBody
	public Message findBloodLipidConditionAgeDistributionHeadthCheckDetail(HttpServletRequest request) throws Exception {
		Map<String, Object> returnMap = Maps.newHashMap();
		String district = request.getParameter("district");
		Map<Object,Object> m = healthCheckService.findBloodLipidConditionAgeDistributionHeadthCheckDetail(district);
		
		Map<String,List<Map<String,Object>>> m2Change = Maps.newHashMap();
		List<Map<String,Object>> ageList1 = new ArrayList<Map<String,Object>>();
		Map<String, Object> map20251 = Maps.newHashMap();
		map20251.put("20-25", 0);
		ageList1.add(map20251);
		Map<String, Object> map25301 = Maps.newHashMap();
		map25301.put("25-30", 0);
		ageList1.add(map25301);
		Map<String, Object> map30351 = Maps.newHashMap();
		map30351.put("30-35", 0);
		ageList1.add(map30351);
		Map<String, Object> map35401 = Maps.newHashMap();
		map35401.put("35-40", 0);
		ageList1.add(map35401);
		Map<String, Object> map40451 = Maps.newHashMap();
		map40451.put("40-45", 0);
		ageList1.add(map40451);
		Map<String, Object> map45501 = Maps.newHashMap();
		map45501.put("45-50", 0);
		ageList1.add(map45501);
		Map<String, Object> map50551 = Maps.newHashMap();
		map50551.put("50-55", 0);
		ageList1.add(map50551);
		Map<String, Object> map55601 = Maps.newHashMap();
		map55601.put("55-60", 0);
		ageList1.add(map55601);
		Map<String, Object> map60651 = Maps.newHashMap();
		map60651.put("60-65", 0);
		ageList1.add(map60651);
		Map<String, Object> map65701 = Maps.newHashMap();
		map65701.put("65-70", 0);
		ageList1.add(map65701);
		Map<String, Object> map70751 = Maps.newHashMap();
		map70751.put("70-75", 0);
		ageList1.add(map70751);
		Map<String, Object> map75801 = Maps.newHashMap();
		map75801.put("75-80", 0);
		ageList1.add(map75801);
		Map<String, Object> map80851 = Maps.newHashMap();
		map80851.put("80-85", 0);
		ageList1.add(map80851);
		Map<String, Object> map85901 = Maps.newHashMap();
		map85901.put("85-90", 0);
		ageList1.add(map85901);
		Map<String, Object> map90951 = Maps.newHashMap();
		map90951.put("90-95", 0);
		ageList1.add(map90951);
		Map<String, Object> map951001 = Maps.newHashMap();
		map951001.put("95-100", 0);
		ageList1.add(map951001);
		
		List<Map<String,Object>> ageList2 = new ArrayList<Map<String,Object>>();
		Map<String, Object> map20252 = Maps.newHashMap();
		map20252.put("20-25", 0);
		ageList2.add(map20252);
		Map<String, Object> map25302 = Maps.newHashMap();
		map25302.put("25-30", 0);
		ageList2.add(map25302);
		Map<String, Object> map30352 = Maps.newHashMap();
		map30352.put("30-35", 0);
		ageList2.add(map30352);
		Map<String, Object> map35402 = Maps.newHashMap();
		map35402.put("35-40", 0);
		ageList2.add(map35402);
		Map<String, Object> map40452 = Maps.newHashMap();
		map40452.put("40-45", 0);
		ageList2.add(map40452);
		Map<String, Object> map45502 = Maps.newHashMap();
		map45502.put("45-50", 0);
		ageList2.add(map45502);
		Map<String, Object> map50552 = Maps.newHashMap();
		map50552.put("50-55", 0);
		ageList2.add(map50552);
		Map<String, Object> map55602 = Maps.newHashMap();
		map55602.put("55-60", 0);
		ageList2.add(map55602);
		Map<String, Object> map60652 = Maps.newHashMap();
		map60652.put("60-65", 0);
		ageList2.add(map60652);
		Map<String, Object> map65702 = Maps.newHashMap();
		map65702.put("65-70", 0);
		ageList2.add(map65702);
		Map<String, Object> map70752 = Maps.newHashMap();
		map70752.put("70-75", 0);
		ageList2.add(map70752);
		Map<String, Object> map75802 = Maps.newHashMap();
		map75802.put("75-80", 0);
		ageList2.add(map75802);
		Map<String, Object> map80852 = Maps.newHashMap();
		map80852.put("80-85", 0);
		ageList2.add(map80852);
		Map<String, Object> map85902 = Maps.newHashMap();
		map85902.put("85-90", 0);
		ageList2.add(map85902);
		Map<String, Object> map90952 = Maps.newHashMap();
		map90952.put("90-95", 0);
		ageList2.add(map90952);
		Map<String, Object> map951002 = Maps.newHashMap();
		map951002.put("95-100", 0);
		ageList2.add(map951002);
		 
		m2Change.put("血脂异常高风险人群", ageList1);
		m2Change.put("新发现高血脂", ageList2);
		for (Map.Entry<Object, Object> entry : m.entrySet()) { 
			Map<String,String> key = (Map<String,String>)entry.getKey();
			Object value = entry.getValue();
			String bpc = key.get("bpc");
			String ages = key.get("ages");
			if(!StringUtils.isEmpty(bpc) && !StringUtils.isEmpty(ages)){
				switch (bpc) {
				case "血脂异常高风险人群":
					List<Map<String,Object>> mma1= m2Change.get("血脂异常高风险人群");
					for(int i =0;i<mma1.size();i++){
						Map<String,Object> map = mma1.get(i);
						if(map.keySet().contains(ages)){
							mma1.get(i).put(ages, value);
						}
					}
					break;
				case "新发现高血脂":
					List<Map<String,Object>> mma2= m2Change.get("新发现高血脂");
					for(int i =0;i<mma2.size();i++){
						Map<String,Object> map = mma2.get(i);
						if(map.keySet().contains(ages)){
							mma2.get(i).put(ages, value);
						}
					}
					break;
				default:
					break;
				}
			}
		}
		returnMap.put("result", m2Change);
		return Message.dataMap(returnMap);
	}
	
	public String parseDistrict(String district) {
		if (district.equals("fxs")) { //阜新市
			district = "";
		} else if (district.equals("hzq")) { //海州区
			district = "海州区";
		} else if (district.equals("fmx")) { //阜蒙县
			district = "阜蒙县";
		} else if (district.equals("zwx")) { //彰武县
			district = "彰武县";
		} else if (district.equals("kms")) { //昆明市
			district = "昆明市";
		} else if (district.equals("plq")) { //盘龙区
			district = "盘龙区";
		}
		return district;
	}
	
	/**
	 * 建档情况 -> 建档人数 -> 初筛数据
	 * http://localhost:8080/screen/manage/hv/recordCount.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "recordCount")
	@ResponseBody
	public Message recordCount(HttpServletRequest request) throws Exception {
		Map<String, Object> returnMap = Maps.newHashMap();
		
		String district = request.getParameter("district");
		
		district = parseDistrict(district);
		Long count = healthCheckService.findRecordConditionCount(district);
		returnMap.put("count", count);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->建档情况->建档人群年龄分布 
	 * http://localhost:8080/screen/manage/hv/findRecordConditionByAge.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findRecordConditionByAge")
	@ResponseBody
	public Message findRecordConditionByAge(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findRecordConditionByAge(district);
		
		return Message.dataMap(returnMap);
	}
	
	@RequestMapping(value = "exportRecordConditionByAge")
	@ResponseBody
	public Message exportData(HttpServletRequest request) throws Exception{
		String district = request.getParameter("district");
		Map<String, Object> returnMap = healthCheckService.findRecordConditionByAge(district);
		
		String tempDir = PropertyUtils.getProperty("system.temp.dir");
		File dir = new File(tempDir, "fuxin");
	    if(!dir.exists()){
	    	dir.mkdirs();
	    }
	    
//	    String codedFileName = new String(fileName.getBytes("gbk"), "iso-8859-1");
//		String uuid =  UUID.randomUUID().toString();
		File path = new File(dir, "数据导出");
	    if(!path.exists()){
	    	path.mkdirs();
	    }
		
		File newFile = new File(path, "数据导出" + ".xlsx");
		
		
		String[] titles = {"", "35以下", "35-39岁", "40-44岁", "45-49岁", "50-54岁", "55-59岁", "60-65岁", "65岁以上岁", "合计"};
		
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Sheet sheet = wb.createSheet();
		/**创建标题行*/
		Row row = sheet.createRow(0);
        // 存储标题在Excel文件中的序号
        for (int i = 0; i < titles.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles[i]);
        }
       
        List<Integer> countList = (List<Integer>) returnMap.get("countList");
        Row valueRow = sheet.createRow(1);
        
        Cell cell0 = valueRow.createCell(0);
        cell0.setCellValue("人数");
        
        int count = 0;
        for (int i = 0; i < countList.size(); i++) {
        	Cell cell = valueRow.createCell(i + 1);
        	cell.setCellValue(countList.get(i));
        	
        	count += countList.get(i);
		}
        
        Cell cellCount = valueRow.createCell(countList.size() + 1);
        cellCount.setCellValue(count);
        
        /*
         * 写入到文件中
         */
        File file = new File(newFile.getAbsolutePath());
        OutputStream outputStream = new FileOutputStream(file);
        wb.write(outputStream);
//        outputStream.flush();
//        outputStream.close();
        wb.close();
        wb.dispose();
        
        
    	//上传OSS
		fileManageService.uploadFile(file, "数据导出" + ".xlsx");
		
		//删除临时文件
		FileUtil.delFolder(path.getAbsolutePath());
		
		//下载压缩包
		String url = fileManageService.getFileUrl("数据导出" + ".xlsx");
		return Message.data(url);
	}
	
	/**
	 * 初筛数据->建档情况->建档人群性别分布 
	 * http://localhost:8080/screen/manage/hv/findRecordConditionByGender.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findRecordConditionByGender")
	@ResponseBody
	public Message findRecordConditionByGender(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findRecordConditionByGender(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->血糖筛查情况->血糖情况人群分布
	 * http://localhost:8080/screen/manage/hv/findBloodSugarConditionPeopleDistribution.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodSugarConditionPeopleDistribution")
	@ResponseBody
	public Message findBloodSugarConditionPeopleDistribution(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodSugarConditionPeopleDistribution(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->血糖筛查情况->血糖情况年龄分布(柱状图)
	 * http://localhost:8080/screen/manage/hv/findBloodSugarConditionAgeDistribution.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodSugarConditionAgeDistribution")
	@ResponseBody
	public Message findBloodSugarConditionAgeDistribution(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodSugarConditionAgeDistributionHeadthCheck2(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->血糖筛查情况->各性别糖尿病分布
	 * http://localhost:8080/screen/manage/hv/findBloodSugarPeopleDistributionByGender.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodSugarPeopleDistributionByGender")
	@ResponseBody
	public Message findBloodSugarPeopleDistributionByGender(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodSugarConditionPeopleDistributionByGender(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->血糖筛查情况->各体质糖尿病分布
	 * http://localhost:8080/screen/manage/hv/findBloodSugarPeopleDistributionByTizhi.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodSugarPeopleDistributionByTizhi")
	@ResponseBody
	public Message findBloodSugarPeopleDistributionByTizhi(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodSugarConditionPeopleDistributionByTizhi(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->血压筛查情况->血压情况人群分布
	 * http://localhost:8080/screen/manage/hv/findBloodPressurePeopleDistribution.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodPressurePeopleDistribution")
	@ResponseBody
	public Message findBloodPressurePeopleDistribution(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressureConditionPeopleDistribution(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->血压筛查情况 ->血压情况年龄分布（柱状图）
	 * http://localhost:8080/screen/manage/hv/findBloodPressureAgeDistribution.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodPressureAgeDistribution")
	@ResponseBody
	public Message findBloodPressureAgeDistribution(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressureConditionAgeDistributionHeadthCheck2(district);
		
		return Message.dataMap(returnMap);
	}
	
	
	/**
	 * 初筛数据->血压筛查情况->各性别高血压分布
	 * http://localhost:8080/screen/manage/hv/findBloodPressurePeopleDistributionByGender.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodPressurePeopleDistributionByGender")
	@ResponseBody
	public Message findBloodPressurePeopleDistributionByGender(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressureConditionPeopleDistributionByGender(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->血压筛查情况->各体质高血压分布
	 * http://localhost:8080/screen/manage/hv/findBloodPressurePeopleDistributionByTizhi.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodPressurePeopleDistributionByTizhi")
	@ResponseBody
	public Message findBloodPressurePeopleDistributionByTizhi(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressurePeopleDistributionByTizhi(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->血压筛查情况->血压分布
	 * http://localhost:8080/screen/manage/hv/findBloodPressureDistribution.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodPressureDistribution")
	@ResponseBody
	public Message findBloodPressureDistribution(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressureDistribution(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->血压筛查情况->高血压分布
	 * http://localhost:8080/screen/manage/hv/findHighBloodPressureDistribution.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findHighBloodPressureDistribution")
	@ResponseBody
	public Message findHighBloodPressureDistribution(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findHighBloodPressureDistribution(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->血脂筛查情况->血脂情况人群分布
	 * http://localhost:8080/screen/manage/hv/findBloodLipidPeopleDistribution.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodLipidPeopleDistribution")
	@ResponseBody
	public Message findBloodLipidPeopleDistribution(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodLipidPeopleDistribution(district);
		
		return Message.dataMap(returnMap);
	}
	
	
	/**
	 * 初筛数据->血脂筛查情况->血脂情况年龄分布 
	 * http://localhost:8080/screen/manage/hv/findBloodLipidAgePeopleDistribution.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	/*@RequestMapping(value = "findBloodLipidAgePeopleDistribution")
	@ResponseBody
	public Message findBloodLipidAgePeopleDistribution(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		Map<String, Object> returnMap = healthCheckService.findBloodLipidConditionAgeDistributionHeadthCheck(district);
		
		return Message.dataMap(returnMap);
	}*/
	
	/**
	 * 初筛数据->血脂筛查情况->血脂情况年龄分布（柱状图）
	 * http://localhost:8080/screen/manage/hv/findBloodPressureByAge.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodPressureByAge")
	@ResponseBody
	public Message findBloodPressureByAge(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodLipidConditionAgeDistributionHeadthCheck2(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->血脂筛查情况->各性别血脂分布
	 * http://localhost:8080/screen/manage/hv/findBloodLipidByGender.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodLipidByGender")
	@ResponseBody
	public Message findBloodLipidByGender(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodLipidConditionPeopleDistributionByGender(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->血脂筛查情况->各体质血脂分布 
	 * http://localhost:8080/screen/manage/hv/findBloodLipidPeopleDistributionByTizhi.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodLipidPeopleDistributionByTizhi")
	@ResponseBody
	public Message findBloodLipidPeopleDistributionByTizhi(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodLipidPeopleDistributionByTizhi(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->肥胖筛查情况->体质指数人群分布
	 * http://localhost:8080/screen/manage/hv/findFatPeopleDistribution.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findFatPeopleDistribution")
	@ResponseBody
	public Message findFatPeopleDistribution(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findFatPeopleDistribution(district);
		
		return Message.dataMap(returnMap);
	}
	
//	/**
//	 * 肥胖筛查情况 -> 体质指数年龄分布 -> 初筛数据
//	 * http://localhost:8080/screen/manage/hv/findFatPeopleDistributionByAge.json
//	 * @param request
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping(value = "findFatPeopleDistributionByAge")
//	@ResponseBody
//	public Message findFatPeopleDistributionByAge(HttpServletRequest request) throws Exception {
//		String district = request.getParameter("district");
//		Map<String, Object> returnMap = healthCheckService.findFatPeopleDistributionByAge(district);
//		
//		return Message.dataMap(returnMap);
//	}
	
	/**
	 * 初筛数据->肥胖筛查情况->肥胖情况年龄分布 （柱状图）
	 * http://localhost:8080/screen/manage/hv/findFatPeopleDistributionByAgeHistogram.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findFatPeopleDistributionByAgeHistogram")
	@ResponseBody
	public Message findFatPeopleDistributionByAgeHistogram(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findFatPeopleDistributionByAge2(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->肥胖筛查情况->肥胖情况性别分布
	 * http://localhost:8080/screen/manage/hv/findFatPeopleDistributionByGender.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findFatPeopleDistributionByGender")
	@ResponseBody
	public Message findFatPeopleDistributionByGender(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findFatPeopleDistributionByGender(district);
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->肥胖筛查情况->肥胖情况体质分布
	 * http://localhost:8080/screen/manage/hv/findFatPeopleDistributionByTizhi.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findFatPeopleDistributionByTizhi")
	@ResponseBody
	public Message findFatPeopleDistributionByTizhi(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findFatPeopleDistributionByTizhi(district);
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->体质筛查情况->社区人群体质分类
	 * http://localhost:8080/screen/manage/hv/findTizhiPeopleDistribution.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findTizhiPeopleDistribution")
	@ResponseBody
	public Message findTizhiPeopleDistribution(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		String type = request.getParameter("type");
		Map<String, Object> returnMap = healthCheckService.findTizhiPeopleDistribution(district, type);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->体质筛查情况->体质与代谢性疾病患病率的关系
	 * http://localhost:8080/screen/manage/hv/findTizhiPeopleDistributionByDisease.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findTizhiPeopleDistributionByDisease")
	@ResponseBody
	public Message findTizhiPeopleDistributionByDisease(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findTizhiPeopleDistributionByDisease(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->体质筛查情况->各性别体质分布
	 * http://localhost:8080/screen/manage/hv/findTizhiPeopleDistributionByGender.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findTizhiPeopleDistributionByGender")
	@ResponseBody
	public Message findTizhiPeopleDistributionByGender(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findTizhiPeopleDistributionByGender(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 初筛数据->体质筛查情况->各年龄段体质分布
	 * http://localhost:8080/screen/manage/hv/findTizhiPeopleDistributionByAge.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findTizhiPeopleDistributionByAge")
	@ResponseBody
	public Message findTizhiPeopleDistributionByAge(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findTizhiPeopleDistributionByAge(district);
		
		return Message.dataMap(returnMap);
	}
	
	
	/**
	 * 初筛数据->冠心病查情况->各性别冠心病分布
	 * http://localhost:8080/screen/manage/hv/findCoronaryHeartDiseaseByGender.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findCoronaryHeartDiseaseByGender")
	@ResponseBody
	public Message findCoronaryHeartDiseaseByGender(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findCoronaryHeartDiseaseByGender(district);
		
		return Message.dataMap(returnMap);
	}
	
//	/**
//	 * 初筛数据->冠心病查情况->各年龄段冠心病分布
//	 * http://localhost:8080/screen/manage/hv/findCoronaryHeartDiseaseByAge.json
//	 * @param request
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping(value = "findCoronaryHeartDiseaseByAge")
//	@ResponseBody
//	public Message findCoronaryHeartDiseaseByAge(HttpServletRequest request) throws Exception {
//		String district = request.getParameter("district");
//		Map<String, Object> returnMap = healthCheckService.findCoronaryHeartDiseaseByAge(district);
//		
//		return Message.dataMap(returnMap);
//	}
	
	
	/**
	 * 初筛数据->冠心病查情况->各体质冠心病分布
	 * http://localhost:8080/screen/manage/hv/findCoronaryHeartDiseaseByTizhi.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findCoronaryHeartDiseaseByTizhi")
	@ResponseBody
	public Message findCoronaryHeartDiseaseByTizhi(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findCoronaryHeartDiseaseByTizhi(district);
		
		return Message.dataMap(returnMap);
	}
	
	@RequestMapping(value = "eyeRecordDistribution")
	@ResponseBody
	public Message eyeRecordDistribution(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Integer page = ParamUtils.getIntValue(request.getParameter("currentPage"));
		Integer pageCount = ParamUtils.getIntValue(request.getParameter("rows"));
		List<Map<String, Object>> list = healthCheckService.eyeRecordDistribution(district, page, pageCount);
		Map<String, Object> returnMap = Maps.newHashMap();
		returnMap.put("result", list);
		returnMap.put("currentPage", page);
		returnMap.put("rows", pageCount);
		returnMap.put("total", 60);
		returnMap.put("pages", 6);
		return Message.dataMap(returnMap);
	}
	
	@RequestMapping(value = "csIndex")
	public String csIndex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		/*if (StringUtils.isNotEmpty(district)) {
			district = new String(district.trim().getBytes("ISO-8859-1"), "UTF-8");  
		}*/

		request.setAttribute("districtHc", district);
		return "customer/fx_jd_condition";
	}
	
	@RequestMapping(value = "xtIndex")
	public String xtIndex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		/*if (StringUtils.isNotEmpty(district)) {
			district = new String(district.trim().getBytes("ISO-8859-1"), "UTF-8");  
		}  */
		
		request.setAttribute("districtHc", district);
		return "customer/fx_xt_condition";
	}
	
	@RequestMapping(value = "xyIndex")
	public String xyIndex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		
		request.setAttribute("districtHc", district);
		return "customer/fx_xy_condition";
	}
	
	@RequestMapping(value = "xzIndex")
	public String xzIndex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		request.setAttribute("districtHc", district);
		return "customer/fx_xz_condition";
	}
	
	@RequestMapping(value = "gxbIndex")
	public String gxbIndex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		
		request.setAttribute("districtHc", district);
		return "customer/fx_gxb_condition";
	}
	
	@RequestMapping(value = "fpIndex")
	public String fpIndex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		
		request.setAttribute("districtHc", district);
		return "customer/fx_fp_condition";
	}
	
	@RequestMapping(value = "tzIndex")
	public String tzIndex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		request.setAttribute("districtHc", district);
		return "customer/fx_tz_condition";
	}
	
	@RequestMapping(value = "mzIndex")
	public String mzIndex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		request.setAttribute("districtHc", district);
		return "customer/fx_mz_condition";
	}
	
	
	@RequestMapping(value = "xtHealthDetailIndex")
	public String xtHealthDetailIndex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		request.setAttribute("districtHc", district);
		return "customer/healthcheckDetail_fx_xt_condition";
	}
	
	
	@RequestMapping(value = "xyHealthDetailIndex")
	public String xyHealthDetailIndex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		request.setAttribute("districtHc", district);
		return "customer/healthcheckDetail_fx_xy_condition";
	}
	
	@RequestMapping(value = "xzHealthDetailIndex")
	public String xzHealthDetailIndex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		request.setAttribute("districtHc", district);
		return "customer/healthcheckDetail_fx_xz_condition";
	}
	
	@RequestMapping(value = "tizhiHealthDetailIndex")
	public String tizhiHealthDetailIndex(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		request.setAttribute("districtHc", district);
		return "customer/healthcheckDetail_fx_tz_condition";
	}
	
	
	/**
	 * 精筛数据->血糖筛查情况->血糖情况人群分布->糖尿病患者/糖尿病前期人群/正常人群
	 * http://localhost:8080/screen/manage/hv/findBloodSugarConditionPeopleDistributionHealthCheckDetail.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodSugarConditionPeopleDistributionHealthCheckDetail")
	@ResponseBody
	public Message findBloodSugarConditionPeopleDistributionHealthCheckDetail(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodSugarConditionPeopleDistributionHealthCheckDetail(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 精筛数据->血糖筛查情况->血糖情况年龄分布->糖尿病患者/糖尿病前期人群/正常人群
	 * http://localhost:8080/screen/manage/hv/findBloodSugarConditionAgeDistributionHealthCheckDetail.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodSugarConditionAgeDistributionHealthCheckDetail")
	@ResponseBody
	public Message findBloodSugarConditionAgeDistributionHealthCheckDetail(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodSugarConditionAgeDistributionHealthCheckDetail(district);
		
		return Message.dataMap(returnMap);
	}
	
	
	/**
	 * 精筛数据->血糖筛查情况->并发症筛查统计->糖尿病患者/糖尿病前期人群/正常人群
	 * http://localhost:8080/screen/manage/hv/findComplicationDistributionHealthCheckDetail.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findComplicationDistributionHealthCheckDetail")
	@ResponseBody
	public Message findComplicationDistributionHealthCheckDetail(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findComplicationDistributionHealthCheckDetail(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 精筛数据->血压筛查情况->血压情况人群分布->已登记高血压患者/新发现高血压人群/正常人群/高血压前期人群
	 * http://localhost:8080/screen/manage/hv/findBloodPressureConditionPeopleDistributionHealthCheck.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodPressureConditionPeopleDistributionHealthCheck")
	@ResponseBody
	public Message findBloodPressureConditionPeopleDistributionHealthCheck(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressureConditionPeopleDistributionHealthCheck(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 精筛数据->血压筛查情况->血压情况人群分布->已登记高血压患者/新发现高血压人群/正常人群/高血压前期人群
	 * http://localhost:8080/screen/manage/hv/findBloodPressureConditionAgeDistributionHealthCheckDetail.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodPressureConditionAgeDistributionHealthCheckDetail")
	@ResponseBody
	public Message findBloodPressureConditionAgeDistributionHealthCheckDetail(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressureConditionAgeDistributionHealthCheckDetail(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 精筛数据->血脂筛查情况->血脂情况人群分布->已登记血脂异常患者\新发现血脂异常患者\正常人群 
	 * http://localhost:8080/screen/manage/hv/findBloodLipidConditionPeopleDistributionHealthCheck.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodLipidConditionPeopleDistributionHealthCheck")
	@ResponseBody
	public Message findBloodLipidConditionPeopleDistributionHealthCheck(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodLipidConditionPeopleDistributionHealthCheck(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 精筛数据->血脂筛查情况->血脂情况年龄分布->已登记血脂异常患者\新发现血脂异常患者\正常人群 
	 * http://localhost:8080/screen/manage/hv/findBloodLipidConditionAgePeopleDistributionHealthCheck.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findBloodLipidConditionAgePeopleDistributionHealthCheck")
	@ResponseBody
	public Message findBloodLipidConditionAgePeopleDistributionHealthCheck(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodLipidConditionAgePeopleDistributionHealthCheck(district);
		
		return Message.dataMap(returnMap);
	}
	
	/**
	 * 精筛数据->体质筛查情况->体质与代谢疾病患病率的关系
	 * http://localhost:8080/screen/manage/hv/findTizhiConditionByDiseaseHealthCheckDetail.json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findTizhiConditionByDiseaseHealthCheckDetail")
	@ResponseBody
	public Message findTizhiConditionByDiseaseHealthCheckDetail(HttpServletRequest request) throws Exception {
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findTizhiConditionByDiseaseHealthCheckDetail(district);
		
		return Message.dataMap(returnMap);
	}
	
	
}
