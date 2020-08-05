package com.capitalbio.healthcheck.service;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.auth.util.Constant;
import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.common.util.ParamUtils;
import com.google.common.collect.Maps;

@Service
@SuppressWarnings("unchecked")
public class HealthCheckImportService {
	private Logger logger = Logger.getLogger(getClass());
	@Autowired AuthService authService;
	@Autowired private HealthCheckService healthCheckService;
	
	public Workbook readFile(InputStream in) {
		try {
			Workbook wb = new XSSFWorkbook(in);
			return wb;
		} catch (Exception e) {
			logger.debug(e);
		}
		return null;
	}
	/**
	 * 上传精筛数据
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> importData(Workbook wb) throws Exception{
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		List<Map<String,Object>> msgList = new ArrayList<Map<String,Object>>();
		Sheet sheet = wb.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();
		
		for (int i = 2; i < rows; i++) {
			boolean flag = true;
					
			Row dataRow = sheet.getRow(i);
			if (dataRow == null) {
				continue;
			}
 			String customerId = getCellValue(dataRow.getCell(2));
			if (StringUtils.isEmpty(customerId)) {
				continue;
			}
			Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId);
			String uniqueId = (String)secretMap.get("uniqueId");
			if (StringUtils.isEmpty(uniqueId)) {
				msgList.add(getErrorMap(i, 3, customerId, "此用户尚未建档，不能导入精筛数据"));
				continue;
			}
					
			Map<String,Object> queryMap = Maps.newHashMap();
			queryMap.put("uniqueId", uniqueId);
			Map<String, Object> customerInfo = healthCheckService.getDataByQuery("customer", queryMap);
			if (customerInfo == null) {
				msgList.add(getErrorMap(i, 3, customerId, "此用户未建档，不能导入精筛数据"));
				continue;
			}
			
			String ageStr = (String)getCellValue(dataRow.getCell(4));
			Double age = null;
			if (StringUtils.isNotEmpty(ageStr)) {
				age = ParamUtils.getDoubleValue(ageStr);
				if (age == null || age < 0) {
					msgList.add(getErrorMap(i, 5, customerId, "年龄必须是数字"));
					flag = false;
				}
			}
			
			String gender = (String)getCellValue(dataRow.getCell(5));
			if (StringUtils.isNotEmpty(gender) && (!(gender.equals("男") || gender.equals("女")))) {
				msgList.add(getErrorMap(i, 6, customerId, "性别只能为男或女"));
				gender = null;
				flag = false;
			}
			
			String diagnoseDm = (String)getCellValue(dataRow.getCell(6));
			if (StringUtils.isNotEmpty(diagnoseDm) && (!(diagnoseDm.equals("是") || diagnoseDm.equals("否")))) {
				msgList.add(getErrorMap(i, 7, customerId, "医生确诊糖尿病只能为是或否"));
				diagnoseDm = null;
				flag = false;
			}
			String diagnoseHtn = (String)getCellValue(dataRow.getCell(7));
			if (StringUtils.isNotEmpty(diagnoseHtn) && (!(diagnoseHtn.equals("是") || diagnoseHtn.equals("否")))) {
				msgList.add(getErrorMap(i, 8, customerId, "医生确诊高血压只能为是或否"));
				diagnoseHtn = null;
				flag = false;
			}
			String diagnoseHpl = (String)getCellValue(dataRow.getCell(8));
			if (StringUtils.isNotEmpty(diagnoseHpl) && (!(diagnoseHpl.equals("是") || diagnoseHpl.equals("否")))) {
				msgList.add(getErrorMap(i, 9, customerId, "医生确诊血脂异常只能为是或否"));
				diagnoseHpl = null;
				flag = false;
			}
			/*String heightStr = (String)getCellValue(dataRow.getCell(9));
			Double height = null;
			if (StringUtils.isNotEmpty(heightStr)) {
				height = ParamUtils.getDoubleValue(heightStr);
				if (height == null) {
					msgList.add(getErrorMap(i, 9, customerId, "身高必须是数字"));
				}
			}
			String weightStr = (String)getCellValue(dataRow.getCell(10));
			Double weight = null;
			if (StringUtils.isNotEmpty(weightStr)) {
				weight = ParamUtils.getDoubleValue(weightStr);
				if (weight == null) {
					msgList.add(getErrorMap(i, 10, customerId, "体重必须是数字"));
				}
			}
			String bmiStr = (String)getCellValue(dataRow.getCell(11));
			Double bmi = null;
			if (StringUtils.isNotEmpty(bmiStr)) {
				bmi = ParamUtils.getDoubleValue(bmiStr);
				if (bmi == null) {
					msgList.add(getErrorMap(i, 11, customerId, "BMI必须是数字"));
				}
			}
			String waistlineStr = (String)getCellValue(dataRow.getCell(12));
			Double waistline = null;
			if (StringUtils.isNotEmpty(waistlineStr)) {
				waistline = ParamUtils.getDoubleValue(waistlineStr);
				if (waistline == null) {
					msgList.add(getErrorMap(i, 12, customerId, "腰围必须是数字"));
				}
			}*/
			String ogttDateStr = (String)getCellValue(dataRow.getCell(9));
			Date ogttTime = null;
			String ogttDate = null;
			if (StringUtils.isNotEmpty(ogttDateStr)) {
				ogttTime = ParamUtils.getDate(ogttDateStr);
				if (ogttTime == null) {
					msgList.add(getErrorMap(i, 10, customerId, "OGTT检测时间必须是日期，格式为1990-01-01"));
					flag = false;
				} else {
					ogttDate = ogttDateStr;
				}
			}
			String ogttStr = (String)getCellValue(dataRow.getCell(10));
			Double ogtt = null;
			if (StringUtils.isNotEmpty(ogttStr)) {
				ogtt = ParamUtils.getDoubleValue(ogttStr);
				if (ogtt == null || ogtt < 0) {
					msgList.add(getErrorMap(i, 11, customerId, "OGTT必须是数字"));
					flag = false;
				}
			}
			String ogtt2hStr = (String)getCellValue(dataRow.getCell(11));
			Double ogtt2h = null;
			if (StringUtils.isNotEmpty(ogtt2hStr)) {
				ogtt2h = ParamUtils.getDoubleValue(ogtt2hStr);
				if (ogtt2h == null || ogtt2h < 0) {
					msgList.add(getErrorMap(i, 12, customerId, "OGTT2H必须是数字"));
					flag = false;
				}
			}
			String tcStr = (String)getCellValue(dataRow.getCell(12));
			Double tc = null;
			if (StringUtils.isNotEmpty(tcStr)) {
				tc = ParamUtils.getDoubleValue(tcStr);
				if (tc == null || tc < 0) {
					msgList.add(getErrorMap(i, 13, customerId, "总胆固醇必须是数字"));
					flag = false;
				}
			}
			String hdlStr = (String)getCellValue(dataRow.getCell(13));
			Double hdl = null;
			if (StringUtils.isNotEmpty(hdlStr)) {
				hdl = ParamUtils.getDoubleValue(hdlStr);
				if (hdl == null || hdl < 0) {
					msgList.add(getErrorMap(i, 14, customerId, "高密度脂蛋白必须是数字"));
					flag = false;
				}
			}
			String ldlStr = (String)getCellValue(dataRow.getCell(14));
			Double ldl = null;
			if (StringUtils.isNotEmpty(ldlStr)) {
				ldl = ParamUtils.getDoubleValue(ldlStr);
				if (ldl == null || ldl < 0) {
					msgList.add(getErrorMap(i, 15, customerId, "低密度脂蛋白必须是数字"));
					flag = false;
				}
			}
			String tgStr = (String)getCellValue(dataRow.getCell(15));
			Double tg = null;
			if (StringUtils.isNotEmpty(tgStr)) {
				tg = ParamUtils.getDoubleValue(tgStr);
				if (tg == null || tg < 0) {
					msgList.add(getErrorMap(i, 16, customerId, "甘油三酯必须是数字"));
					flag = false;
				}
			}
			
			String ucrStr = (String)getCellValue(dataRow.getCell(16));
			Double ucr = null;
			if (StringUtils.isNotEmpty(ucrStr)) {
				ucr = ParamUtils.getDoubleValue(ucrStr);
				if (ucr == null || ucr < 0) {
					msgList.add(getErrorMap(i, 17, customerId, "尿肌酐必须是数字"));
					flag = false;
				}
			}
			
			String malbStr = (String)getCellValue(dataRow.getCell(17));
			Double malb = null;
			if (StringUtils.isNotEmpty(malbStr)) {
				malb = ParamUtils.getDoubleValue(malbStr);
				if (malb == null || malb < 0) {
					msgList.add(getErrorMap(i, 18, customerId, "尿微量白蛋白必须是数字"));
					flag = false;
				}
			}
			
			String checkDate2Str = (String)getCellValue(dataRow.getCell(18));
			Date checkTime2 = null;
			String checkDate2 = null;
			if (StringUtils.isNotEmpty(checkDate2Str)) {
				checkTime2 = ParamUtils.getDate(checkDate2Str);
				if (checkTime2 == null) {
					msgList.add(getErrorMap(i, 19, customerId, "第一次血压检测时间必须是日期，格式为1990-01-01"));
					flag = false;
				} else {
					checkDate2 = checkDate2Str;
				}
			}
			String highPressure2Str = (String)getCellValue(dataRow.getCell(19));
			Integer highPressure2 = null;
			if (StringUtils.isNotEmpty(highPressure2Str)) {
				highPressure2 = ParamUtils.getIntValue(highPressure2Str);
				if (highPressure2 == null || highPressure2 < 0) {
					msgList.add(getErrorMap(i, 20, customerId, "第一次收缩压必须是数字"));
					flag = false;
				}
			}
			String lowPressure2Str = (String)getCellValue(dataRow.getCell(20));
			Integer lowPressure2 = null;
			if (StringUtils.isNotEmpty(lowPressure2Str)) {
				lowPressure2 = ParamUtils.getIntValue(lowPressure2Str);
				if (lowPressure2 == null || lowPressure2 < 0) {
					msgList.add(getErrorMap(i, 21, customerId, "第一次舒张压必须是数字"));
					flag = false;
				}
			}
			String checkDate3Str = (String)getCellValue(dataRow.getCell(21));
			Date checkTime3 = null;
			String checkDate3 = null;
			if (StringUtils.isNotEmpty(checkDate3Str)) {
				checkTime3 = ParamUtils.getDate(checkDate3Str);
				if (checkTime3 == null) {
					msgList.add(getErrorMap(i, 22, customerId, "第二次血压检测时间必须是日期，格式为1990-01-01"));
					flag = false;
				} else {
					checkDate3 = checkDate3Str;
				}
			}
			String highPressure3Str = (String)getCellValue(dataRow.getCell(22));
			Integer highPressure3 = null;
			if (StringUtils.isNotEmpty(highPressure3Str)) {
				highPressure3 = ParamUtils.getIntValue(highPressure3Str);
				if (highPressure3 == null || highPressure3 < 0) {
					msgList.add(getErrorMap(i, 23, customerId, "第二次收缩压必须是数字"));
					flag = false;
				}
			}
			String lowPressure3Str = (String)getCellValue(dataRow.getCell(23));
			Integer lowPressure3 = null;
			if (StringUtils.isNotEmpty(lowPressure3Str)) {
				lowPressure3 = ParamUtils.getIntValue(lowPressure3Str);
				if (lowPressure3 == null || lowPressure3 < 0) {
					msgList.add(getErrorMap(i, 24, customerId, "第二次舒张压必须是数字"));
					flag = false;
				}
			}
			String checkDate4Str = (String)getCellValue(dataRow.getCell(24));
			Date checkTime4 = null;
			String checkDate4 = null;
			if (StringUtils.isNotEmpty(checkDate4Str)) {
				checkTime4 = ParamUtils.getDate(checkDate4Str);
				if (checkTime4 == null) {
					msgList.add(getErrorMap(i, 25, customerId, "第三次血压检测时间必须是日期，格式为1990-01-01"));
					flag = false;
				} else {
					checkDate4 = checkDate4Str;
				}
			}
			String highPressure4Str = (String)getCellValue(dataRow.getCell(25));
			Integer highPressure4 = null;
			if (StringUtils.isNotEmpty(highPressure4Str)) {
				highPressure4 = ParamUtils.getIntValue(highPressure4Str);
				if (highPressure4 == null || highPressure4 < 0) {
					msgList.add(getErrorMap(i, 26, customerId, "第三次收缩压必须是数字"));
					flag = false;
				}
			}
			String lowPressure4Str = (String)getCellValue(dataRow.getCell(26));
			Integer lowPressure4 = null;
			if (StringUtils.isNotEmpty(lowPressure4Str)) {
				lowPressure4 = ParamUtils.getIntValue(lowPressure4Str);
				if (lowPressure4 == null || lowPressure4 < 0) {
					msgList.add(getErrorMap(i, 27, customerId, "第三次舒张压必须是数字"));
					flag = false;
				}
			}
			
			Cell cell29 = dataRow.getCell(29);
			String dryLeft = (String)getCellValue(cell29);
			if (StringUtils.isNotEmpty(dryLeft) && (!(dryLeft.equals("有") || dryLeft.equals("无")))) {
				msgList.add(getErrorMap(i, 30, customerId, "左足干燥只能为有或无"));
				dryLeft = null;
				flag = false;
			}
			
			String dryRight = (String)getCellValue(dataRow.getCell(30));
			if (StringUtils.isNotEmpty(dryRight) && (!(dryRight.equals("有") || dryRight.equals("无")))) {
				msgList.add(getErrorMap(i, 31, customerId, "右足干燥只能为有或无"));
				dryRight = null;
				flag = false;
			}
			
			String chapLeft = (String)getCellValue(dataRow.getCell(31));
			if (StringUtils.isNotEmpty(chapLeft) && (!(chapLeft.equals("有") || chapLeft.equals("无")))) {
				msgList.add(getErrorMap(i, 32, customerId, "左足皲裂只能为有或无"));
				chapLeft = null;
				flag = false;
			}
			
			String chapRight = (String)getCellValue(dataRow.getCell(32));
			if (StringUtils.isNotEmpty(chapRight) && (!(chapRight.equals("有") || chapRight.equals("无")))) {
				msgList.add(getErrorMap(i, 33, customerId, "右足皲裂只能为有或无"));
				chapRight = null;
				flag = false;
			}
			
			String peelLeft = (String)getCellValue(dataRow.getCell(33));
			if (StringUtils.isNotEmpty(peelLeft) && (!(peelLeft.equals("有") || peelLeft.equals("无")))) {
				msgList.add(getErrorMap(i, 34, customerId, "左足脱皮只能为有或无"));
				peelLeft = null;
				flag = false;
			}
			
			String peelRight = (String)getCellValue(dataRow.getCell(34));
			if (StringUtils.isNotEmpty(peelRight) && (!(peelRight.equals("有") || peelRight.equals("无")))) {
				msgList.add(getErrorMap(i, 35, customerId, "右足脱皮只能为有或无"));
				peelRight = null;
				flag = false;
			}
			
			String cornLeft = (String)getCellValue(dataRow.getCell(35));
			if (StringUtils.isNotEmpty(cornLeft) && (!(cornLeft.equals("有") || cornLeft.equals("无")))) {
				msgList.add(getErrorMap(i, 36, customerId, "左足鸡眼只能为有或无"));
				cornLeft = null;
				flag = false;
			}
			
			String cornRight = (String)getCellValue(dataRow.getCell(36));
			if (StringUtils.isNotEmpty(cornRight) && (!(cornRight.equals("有") || cornRight.equals("无")))) {
				msgList.add(getErrorMap(i, 37, customerId, "右足鸡眼只能为有或无"));
				cornRight = null;
				flag = false;
			}
			
			String malLeft = (String)getCellValue(dataRow.getCell(37));
			if (StringUtils.isNotEmpty(malLeft) && (!(malLeft.equals("有") || malLeft.equals("无")))) {
				msgList.add(getErrorMap(i, 38, customerId, "左足畸形只能为有或无"));
				malLeft = null;
				flag = false;
			}
			
			String malRight = (String)getCellValue(dataRow.getCell(38));
			if (StringUtils.isNotEmpty(malRight) && (!(malRight.equals("有") || malRight.equals("无")))) {
				msgList.add(getErrorMap(i, 39, customerId, "右足畸形只能为有或无"));
				malRight = null;
				flag = false;
			}
			
			String callusLeft = (String)getCellValue(dataRow.getCell(39));
			if (StringUtils.isNotEmpty(callusLeft) && (!(callusLeft.equals("有") || callusLeft.equals("无")))) {
				msgList.add(getErrorMap(i, 40, customerId, "左足足底胼胝只能为有或无"));
				callusLeft = null;
				flag = false;
			}
			
			String callusRight = (String)getCellValue(dataRow.getCell(40));
			if (StringUtils.isNotEmpty(callusRight) && (!(callusRight.equals("有") || callusRight.equals("无")))) {
				msgList.add(getErrorMap(i, 41, customerId, "右足足底胼胝只能为有或无"));
				callusRight = null;
				flag = false;
			}
			
			String fungalLeft = (String)getCellValue(dataRow.getCell(41));
			if (StringUtils.isNotEmpty(fungalLeft) && (!(fungalLeft.equals("有") || fungalLeft.equals("无")))) {
				msgList.add(getErrorMap(i, 42, customerId, "左足真菌感染只能为有或无"));
				fungalLeft = null;
				flag = false;
			}
			
			String fungalRight = (String)getCellValue(dataRow.getCell(42));
			if (StringUtils.isNotEmpty(fungalRight) && (!(fungalRight.equals("有") || fungalRight.equals("无")))) {
				msgList.add(getErrorMap(i, 43, customerId, "右足真菌感染只能为有或无"));
				fungalRight = null;
				flag = false;
			}
			
			String ulcerLeft = (String)getCellValue(dataRow.getCell(43));
			if (StringUtils.isNotEmpty(ulcerLeft) && (!(ulcerLeft.equals("有") || ulcerLeft.equals("无")))) {
				msgList.add(getErrorMap(i, 44, customerId, "左足溃疡只能为有或无"));
				ulcerLeft = null;
				flag = false;
			}
			
			String ulcerRight = (String)getCellValue(dataRow.getCell(44));
			if (StringUtils.isNotEmpty(ulcerRight) && (!(ulcerRight.equals("有") || ulcerRight.equals("无")))) {
				msgList.add(getErrorMap(i, 45, customerId, "右足溃疡只能为有或无"));
				ulcerRight = null;
				flag = false;
			}
			
			String feelLeftFirst = (String)getCellValue(dataRow.getCell(45));
			if (StringUtils.isNotEmpty(feelLeftFirst) && (!(feelLeftFirst.equals("对") || feelLeftFirst.equals("错")))) {
				msgList.add(getErrorMap(i, 46, customerId, "左足第一次只能为对或错"));
				feelLeftFirst = null;
				flag = false;
			}
			
			String feelLeftSecond = (String)getCellValue(dataRow.getCell(46));
			if (StringUtils.isNotEmpty(feelLeftSecond) && (!(feelLeftSecond.equals("对") || feelLeftSecond.equals("错")))) {
				msgList.add(getErrorMap(i, 47, customerId, "左足第二次只能为对或错"));
				feelLeftSecond = null;
				flag = false;
			}
			
			String feelLeftThird = (String)getCellValue(dataRow.getCell(47));
			if (StringUtils.isNotEmpty(feelLeftThird) && (!(feelLeftThird.equals("对") || feelLeftThird.equals("错")))) {
				msgList.add(getErrorMap(i, 48, customerId, "左足第三次只能为对或错"));
				feelLeftThird = null;
				flag = false;
			}
			
			String feelLeftResult = (String)getCellValue(dataRow.getCell(48));
			if (StringUtils.isNotEmpty(feelLeftResult) && (!(feelLeftResult.equals("正常") || feelLeftResult.equals("减弱") || feelLeftResult.equals("消失")))) {
				msgList.add(getErrorMap(i, 49, customerId, "左足只能为正常或减弱或消失"));
				feelLeftResult = null;
				flag = false;
			}
			
			String feelRightFirst = (String)getCellValue(dataRow.getCell(49));
			if (StringUtils.isNotEmpty(feelRightFirst) && (!(feelRightFirst.equals("对") || feelRightFirst.equals("错")))) {
				msgList.add(getErrorMap(i, 50, customerId, "右足第一次只能为对或错"));
				feelRightFirst = null;
				flag = false;
			}
			
			String feelRightSecond = (String)getCellValue(dataRow.getCell(50));
			if (StringUtils.isNotEmpty(feelRightSecond) && (!(feelRightSecond.equals("对") || feelRightSecond.equals("错")))) {
				msgList.add(getErrorMap(i, 51, customerId, "右足第二次只能为对或错"));
				feelRightSecond = null;
				flag = false;
			}
			
			
			String feelRightThird = (String)getCellValue(dataRow.getCell(51));
			if (StringUtils.isNotEmpty(feelRightThird) && (!(feelRightThird.equals("对") || feelRightThird.equals("错")))) {
				msgList.add(getErrorMap(i, 52, customerId, "右足第三次只能为对或错"));
				feelRightThird = null;
				flag = false;
			}
			
			
			String feelRightResult = (String)getCellValue(dataRow.getCell(52));
			if (StringUtils.isNotEmpty(feelRightResult) && (!(feelRightResult.equals("正常") || feelRightResult.equals("减弱") || feelRightResult.equals("消失")))) {
				msgList.add(getErrorMap(i, 53, customerId, "右足只能为正常或减弱或消失"));
				feelRightResult = null;
				flag = false;
			}
			
			if (!flag) {
				continue;
			}
			
			
			String fundus = (String)getCellValue(dataRow.getCell(54));
			String sampleId = (String)getCellValue(dataRow.getCell(55));
			
			//计算精筛人群结果分类
			String classifyResultJs = "";
			String bloodGlucose = "";
			String disease = "";
			if (customerInfo.get("disease") != null && customerInfo.get("disease") != "" ) {
				disease = customerInfo.get("disease").toString();
			}
			
			if (StringUtils.isNotEmpty(disease) && disease.contains("糖尿病") ) {
				bloodGlucose = "糖尿病患者";
			} else if (ogtt != null && ogtt2h != null) {
				if (ogtt >= 7 || ogtt2h >= 11.1) {
					bloodGlucose = "糖尿病患者";
				} else if ((ogtt<7 && ogtt2h>=7.8 && ogtt2h<11.1) || (ogtt>=6.1 && ogtt<7 && ogtt2h<7.8)) {
					bloodGlucose = "糖尿病前期人群";
				}  else if (ogtt<6.1 && ogtt2h<7.8){
					bloodGlucose = "正常";
				}
			}
			
			String bloodPressure = "";
			
			if (StringUtils.isNotEmpty(disease) && disease.contains("高血压") ) {
				bloodPressure = "高血压患者";
			} else if(checkDate2 != null && highPressure2 != null && lowPressure2 != null
	    			&& checkDate3 != null && highPressure3 != null && lowPressure3 != null
	    			&& checkDate4 != null && highPressure4 != null && lowPressure4 != null) {
	    		if(!checkDate2.equals(checkDate3) && !checkDate3.equals(checkDate4) && !checkDate2.equals(checkDate4)) {
	    			if ((highPressure2 >= 140 || lowPressure2 >= 90) && (highPressure3 >= 140 || lowPressure3 >= 90) &&  (highPressure4 >= 140 || lowPressure4 >= 90)) {
	    				bloodPressure = "高血压患者";
	    			} else if ((highPressure2 < 140 && highPressure3 < 140 && highPressure4 < 140) 
	    					&& (lowPressure2 < 90 && lowPressure3 < 90 && lowPressure4 < 90)) {
	    				bloodPressure = "正常";
	    			} else {
	    				bloodPressure = "高血压前期人群";
	    			}
	    		}
	    	}
			
			String bloodLipid = "";
			if (StringUtils.isNotEmpty(disease) && disease.contains("高血脂") ) {
				bloodLipid = "血脂异常患者";
			}else if (tg != null && tc != null && hdl != null) {
				 if (tc >= 5.2 || tg >= 1.7 || hdl < 1) {
					 bloodLipid = "血脂异常患者";
				 } else if (tc < 5.2 && tg<1.7 && hdl>1) {
					 bloodLipid = "正常";
				 }
			}
			
			
			if (bloodGlucose.equals("正常") && bloodPressure.equals("正常") && bloodLipid.equals("正常")) {
				classifyResultJs = "精筛检测指标无异常";
			} else if (bloodGlucose=="" && bloodPressure=="" && bloodLipid=="") {
				classifyResultJs = "检测信息缺失，无法判断";
			} else {
				if (bloodGlucose != "" && !bloodGlucose.equals("正常")) {
					classifyResultJs += bloodGlucose + ",";
				} 
				
				if (bloodPressure != "" && !bloodPressure.equals("正常")) {
					classifyResultJs += bloodPressure + ",";
				} 
				
				if (bloodLipid != "" && !bloodLipid.equals("正常")) {
					classifyResultJs += bloodLipid + ",";
				} 
				classifyResultJs = classifyResultJs.substring(0,classifyResultJs.length()-1);
			}
			
			String geneReports = "";
			/*if (classifyResultJs.contains("糖尿病患者") && (classifyResultJs.contains("高血压患者") || classifyResultJs.contains("血脂异常患者"))) {
				geneReports = "爱身谱®糖尿病检测套餐B,爱身谱®血栓性基因检测5项,成人个体化用药基因检测";
			} else if (classifyResultJs.contains("糖尿病患者") && (!classifyResultJs.contains("高血压患者") && !classifyResultJs.contains("血脂异常患者"))) {
				geneReports = "爱身谱®糖尿病检测套餐B,成人个体化用药基因检测";
			} else if (!classifyResultJs.contains("糖尿病患者") && (classifyResultJs.contains("高血压患者") || classifyResultJs.contains("血脂异常患者"))) {
				geneReports = "爱身谱®血栓性基因检测5项,成人个体化用药基因检测";
			}*/
			
			if (classifyResultJs.indexOf("糖尿病患者")!=-1) {
				geneReports += "糖尿病用药套餐,";
			}
			
			if (classifyResultJs.indexOf("高血压患者")!=-1) {
				geneReports += "高血压用药套餐,";
			}
			
			if (classifyResultJs.indexOf("血脂异常患者")!=-1) {
				geneReports += "他汀类降脂药套餐,";
			}
			
			if (StringUtils.isNotEmpty(geneReports)) {
				geneReports = geneReports.substring(0, geneReports.length() -1);
			}
			
			Map<String,Object> dataMap = Maps.newHashMap();
			dataMap.put("uniqueId", uniqueId);
			dataMap.put("age", secretMap.get("age"));
			dataMap.put("gender", secretMap.get("gender"));
			dataMap.put("diagnoseDm", diagnoseDm);
			dataMap.put("diagnoseHtn", diagnoseHtn);
			dataMap.put("diagnoseHpl", diagnoseHpl);
			//dataMap.put("heightDetail", height);
			//dataMap.put("weightDetail", weight);
			//dataMap.put("bmiDetail", bmi);
			//dataMap.put("waistlineDetail", waistline);
			dataMap.put("ogttDate", ogttDate);
			dataMap.put("ogttTime", ogttTime);

			dataMap.put("ogtt", ogtt);
			dataMap.put("ogtt2h", ogtt2h);
			dataMap.put("tcDetail", tc);
			dataMap.put("hdlDetail", hdl);
			dataMap.put("ldlDetail", ldl);
			dataMap.put("tgDetail", tg);
			dataMap.put("malb", malb);
			dataMap.put("ucr", ucr);
			dataMap.put("checkDate2", checkDate2);
			dataMap.put("checkTime2", checkTime2);
			dataMap.put("highPressure2", highPressure2);
			dataMap.put("lowPressure2", lowPressure2);
			dataMap.put("checkDate3", checkDate3);
			dataMap.put("checkTime3", checkTime3);
			dataMap.put("highPressure3", highPressure3);
			dataMap.put("lowPressure3", lowPressure3);
			dataMap.put("checkDate4", checkDate4);
			dataMap.put("checkTime4", checkTime4);
			dataMap.put("highPressure4", highPressure4);
			dataMap.put("lowPressure4", lowPressure4);
			
			dataMap.put("dryLeft", dryLeft);
			dataMap.put("dryRight", dryRight);
			dataMap.put("chapLeft", chapLeft);
			dataMap.put("chapRight", chapRight);
			dataMap.put("peelLeft", peelLeft);
			dataMap.put("peelRight", peelRight);
			dataMap.put("cornLeft", cornLeft);
			dataMap.put("cornRight", cornRight);
			dataMap.put("malLeft", malLeft);
			dataMap.put("malRight", malRight);
			dataMap.put("callusLeft", callusLeft);
			dataMap.put("callusRight", callusRight);
			dataMap.put("fungalLeft", fungalLeft);
			dataMap.put("fungalRight", fungalRight);
			dataMap.put("ulcerLeft", ulcerLeft);
			dataMap.put("ulcerRight", ulcerRight);
			dataMap.put("feelLeftFirst", feelLeftFirst);
			dataMap.put("feelLeftSecond", feelLeftSecond);
			dataMap.put("feelLeftThird", feelLeftThird);
			dataMap.put("feelLeftResult", feelLeftResult);
			dataMap.put("feelRightFirst", feelRightFirst);
			dataMap.put("feelRightSecond", feelRightSecond);
			dataMap.put("feelRightThird", feelRightThird);
			dataMap.put("feelRightResult", feelRightResult);
			dataMap.put("fundus", fundus);
			dataMap.put("sampleId", sampleId);
			dataMap.put("classifyResultJs", classifyResultJs);
			dataMap.put("geneReports", geneReports);
			
//			String disease = (String)customerInfo.get("disease");
			String haveDisease = (String)customerInfo.get("haveDisease");
			String familyHistory = (String)customerInfo.get("familyHistory");
			String familyDisease = (String)customerInfo.get("familyDisease");
			dataMap.put("disease", disease);
			dataMap.put("haveDisease", haveDisease);
			dataMap.put("familyHistory", familyHistory);
			dataMap.put("familyDisease", familyDisease);
			healthCheckService.saveData("healthcheckDetailHistory", dataMap);
			dataMap.remove("id");
			dataMap.remove("_id");
			dataMap.remove("ctime");
			Map<String,Object> data = healthCheckService.getDataByQuery("healthcheckDetail", queryMap);
			if (data == null) {
				data = Maps.newHashMap();
			}
			data.putAll(dataMap);
			String id = healthCheckService.saveData("healthcheckDetail", data);
			dataMap.put("healthCheckDetailId", id);
			//保存最新记录
			healthCheckService.saveCustomer(dataMap, "");

		}
		wb.close();
		return msgList;

	}

	
	private Map<String,Object> getErrorMap(int rowNo, int colNo, String customerId, String errorMsg) {
		Map<String,Object> errMap = Maps.newHashMap();
		errMap.put("rowNo", rowNo);
		errMap.put("colNo", colNo);
		errMap.put("customerId", customerId);
		errMap.put("errorMsg", errorMsg);
		return errMap;
	}

	private String getCellValue(Cell cell) {
		return getCellValue(cell, true);
	}
	
	private String getCellValue(Cell cell, boolean treatAsStr) {
        if (cell == null) {
        	return null;
        }
        
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
        	if (HSSFDateUtil.isCellDateFormatted(cell)) {
        		Date d = cell.getDateCellValue();
        		DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        		return formater.format(d);
        	}else{
        	     DecimalFormat format = new DecimalFormat("#.#");  
        	     return format.format(cell.getNumericCellValue());
        		
        	}
        }
        
        if (treatAsStr) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }

        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
        	double dvalue = cell.getNumericCellValue();
            return String.valueOf(dvalue);
        } else {
            return String.valueOf(cell.getStringCellValue());
        }
    }

}
