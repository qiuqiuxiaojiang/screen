package com.capiltalbio.health.client;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.capitalbio.common.util.IdcardValidator;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class UploadFile {
	
	@Autowired HealthCheckService healthCheckService;
	
	public static void main(String[] args) {
		UploadFile file = new UploadFile();
		file.uploadFile();
	}

	public void uploadFile() {
		/*MultipartHttpServletRequest mhsq = (MultipartHttpServletRequest) request;
		MultipartFile csFile = mhsq.getFile("csFile");
		if (csFile == null) {
			return Message.data("error");
		}
		
		File dir=new File("E://temp");
	    if(!dir.exists()){
	    	dir.mkdirs();
	    }*/
	    
        try { 
//        	File newFile = new File(dir+"/test.xlsx");
//        	csFile.transferTo(newFile);
        	File newFile = new File("E:/temp/模板.xlsx");
        	
        	FileInputStream input = new FileInputStream(newFile);
        	XSSFWorkbook wb = new XSSFWorkbook(input);
    		XSSFSheet sheet = wb.getSheetAt(0);
    		int rows = sheet.getPhysicalNumberOfRows();
    		
        	/*Workbook wb = null;
            try {
            	wb = new XSSFWorkbook(newFile);
            } catch (Exception ex) {
            	wb = new HSSFWorkbook(new FileInputStream(newFile));
            }
            Sheet sheet = wb.getSheetAt(0);
    		int rows = sheet.getPhysicalNumberOfRows();*/
    		String errMsgAll = "";
    		List<Map<String, Object>> list = Lists.newArrayList();
    		for (int i = 1; i < rows; i++) {
    			String errMsg = "";
    			XSSFRow row = sheet.getRow(i);
    			
    			//姓名
    			String name = getCellValue(row.getCell(0));
    			if (StringUtils.isEmpty(name)) {
    				errMsg += "姓名不能为空;";
    			}
    			
    			//身份证号
    			String customerId = getCellValue(row.getCell(1));
    			String gender = "";
    			int age = 0;
    			String birthday = "";
    			if (StringUtils.isEmpty(customerId)) {
    				errMsg += "身份证号不能为空;";
    			} else {
    				boolean valid = IdcardValidator.isValidatedAllIdcard(customerId);
    		        if (!valid) {
    		        	errMsg += "身份证号非法;";
    		        } else {
    		        	gender = getGenderByIdCard(customerId);
    		        	age = getAgeByIdCard(customerId);
    		        	birthday = getBirthdayByIdCard(customerId);
    		        }
    			}
    			
    			//手机号
		        String mobile = getCellValue(row.getCell(2));
		        if (StringUtils.isEmpty(mobile)) {
		        	errMsg += "手机号不能为空;";
		        } else {
		        	boolean mobileValid = isMobileNum(mobile);
			        if (!mobileValid) {
			        	errMsg += "手机号非法;";
			        }
		        }
		        
		        //检测时间
		        String checkTime = getCellValue(row.getCell(3));
		        if (StringUtils.isEmpty(checkTime)) {
		        	errMsg += "检测时间不能为空;";
		        }
		        
		        //糖尿病
		        String tnb = getCellValue(row.getCell(4));
		        if (StringUtils.isNotEmpty(tnb) && (!tnb.equals("是") && !tnb.equals("否"))) {
		        	errMsg += "请规范填写糖尿病;";
		        } 
		        
		        //高血压
		        String gxy = getCellValue(row.getCell(5));
		        if (StringUtils.isNotEmpty(gxy) && (!gxy.equals("是") && !gxy.equals("否"))) {
		        	errMsg += "请规范填写高血压;";
		        }
		        
		        //高血脂
		        String gxz = getCellValue(row.getCell(6));
		        if (StringUtils.isNotEmpty(gxz) && (!gxz.equals("是") && !gxz.equals("否"))) {
		        	errMsg += "请规范填写高血脂;";
		        }
		        
		        //冠心病
		        String gxb = getCellValue(row.getCell(7));
		        if (StringUtils.isNotEmpty(gxb) && (!gxb.equals("是") && !gxb.equals("否"))) {
		        	errMsg += "请规范填写冠心病;";
		        }
		        
		        //家族史
		        String familyHistory = getCellValue(row.getCell(8));
		        if (StringUtils.isEmpty(familyHistory)) {
		        	errMsg += "家族史不能为空;";
		        } else if (!familyHistory.equals("有") && !familyHistory.equals("无")) {
		        	errMsg += "请规范填写家族史;";
		        }
		        
		        //建档日期
		        String recordDate = getCellValue(row.getCell(9));
		        if (StringUtils.isEmpty(recordDate)) {
		        	errMsg += "建档日期不能为空;";
		        }
		        
		        //身高
		        Double height = getCellDoubleValue(row.getCell(10));
		        if (height != null) {
		        	//boolean heightIsNum = isNumeric(height);
		        	if (height <= 120) {
		        		errMsg += "请规范填写身高;";
		        	}
		        } 
		        
		        //体重
		        Double weight = getCellDoubleValue(row.getCell(11));
		        
		        //BMI
		        Double BMI = getCellDoubleValue(row.getCell(12));
		        if (BMI != null) {
		        	BMI = ParamUtils.doubleScale(BMI, 1);
		        }
		        
		        //腰围
		        Double waistline = getCellDoubleValue(row.getCell(13));
		        
		        //收缩压
		        Integer highPressure = getCellIntegerValue(row.getCell(14));
		        
		        //舒张压
		        Integer lowPressure = getCellIntegerValue(row.getCell(15));
		        
		        //脉率
		        Double pulse = getCellDoubleValue(row.getCell(16));
		        
		        //体温
		        Double temperature = getCellDoubleValue(row.getCell(17));
		        
		        //血氧
		        Double oxygen = getCellDoubleValue(row.getCell(18));
		        
		        //臀围
		        Double hipline = getCellDoubleValue(row.getCell(19));
		        
		        //腰臀比
		        //String WHR = getCellValue(row.getCell(22));
		        
		        //体脂率
		        Double fatContent = getCellDoubleValue(row.getCell(20));
		        if (fatContent != null) {
		        	fatContent = ParamUtils.doubleScale(fatContent, 1);
		        }
		        
		        //空腹血糖
		        String bloodGlucose = getCellValue(row.getCell(21));
		        
		        //餐后2h血糖
		        String bloodGlucose2h = getCellValue(row.getCell(22));
		        
		        //随机血糖
		        String bloodGlucoseRandom = getCellValue(row.getCell(23));
		        
		        //总胆固醇
		        String tc = getCellValue(row.getCell(24));
		        
		        //甘油三酯
		        String tg = getCellValue(row.getCell(25));
		        
		        //血清低密度脂蛋白
		        String ldl = getCellValue(row.getCell(26));
		        
		        //血清高密度脂蛋白
		        String hdl = getCellValue(row.getCell(27));
		        
		        //中医体质辨识结果
		        String tizhi = getCellValue(row.getCell(28));
		        
		        //中医眼象辨识
		        String eyeCheck = getCellValue(row.getCell(29));
		        if (StringUtils.isNotEmpty(eyeCheck)) {
		        	if (!eyeCheck.equals("已检测") && !eyeCheck.equals("未检测")) {
		        		errMsg += "请规范填写家族史;";
		        	}
		        }
		        
		        //民族
		        String nationality = getCellValue(row.getCell(30));
		        
		        //常驻类型
		        String householdRegistrationType = getCellValue(row.getCell(31));
		        if (StringUtils.isNotEmpty(householdRegistrationType)) {
		        	if (!householdRegistrationType.equals("户籍") && !householdRegistrationType.equals("未户籍")) {
		        		errMsg += "请规范填写常驻类型;";
		        	}
		        }
		        
		        //联系人姓名
		        String contactName = getCellValue(row.getCell(32));
		        
		        //联系人电话
		        String contactMobile = getCellValue(row.getCell(33));
		        
		        //地址
		        String address = getCellValue(row.getCell(34));
		        
		        //筛查区域
		        String district = getCellValue(row.getCell(35));
		        
		        //筛查地点
		        String checkPlace = getCellValue(row.getCell(36));
		        
		        //筛查组
		        String checkGroup = getCellValue(row.getCell(37));
		        
		        System.out.println("errMsg:" + errMsg);
		        
		        //没有错误信息，导入数据
		        errMsgAll += errMsg;
		        if (StringUtils.isEmpty(errMsgAll)) {
		        	Map<String, Object> map = Maps.newHashMap();
		        	map.put("name", name);
		        	map.put("customerId", customerId);
		        	map.put("gender", gender);
		        	map.put("age", age);
		        	map.put("birthday", birthday);
		        	map.put("mobile", mobile);
		        	map.put("checkTime", checkTime);
		        	
		        	String disease = "";
		        	if (StringUtils.isNotEmpty(tnb)) {
		        		if (tnb.equals("是")) {
		        			disease += "糖尿病" + ","; 
			        		map.put("dm", "是");
		        		}
		        		
		        		if (tnb.equals("否")) {
			        		map.put("dm", "否");
		        		}
		        	}
		        	
		        	if (StringUtils.isNotEmpty(gxy)) {
		        		if (gxy.equals("是")) {
		        			disease += "高血压" + ","; 
			        		map.put("htn", "是");
		        		}
		        		
		        		if (gxy.equals("否")) {
			        		map.put("htn", "否");
		        		}
		        	}
		        	
		        	if (StringUtils.isNotEmpty(gxz)) {
		        		if (gxz.equals("是")) {
		        			disease += "高血脂" + ","; 
			        		map.put("hpl", "是");
		        		}
		        		
		        		if (gxz.equals("否")) {
			        		map.put("hpl", "否");
		        		}
		        	}
		        	
		        	if (StringUtils.isNotEmpty(gxb)) {
		        		if (gxb.equals("是")) {
		        			disease += "冠心病" + ","; 
			        		map.put("cpd", "是");
		        		}
		        		
		        		if (gxb.equals("否")) {
		        			map.put("cpd", "是");
		        		}
		        	}
		        	
		        	if (StringUtils.isNotEmpty(disease)) {
		        		disease = disease.substring(0, disease.length() - 1);
		        	}
		        	map.put("disease", disease);
		        	
		        	if (StringUtils.isNotEmpty(familyHistory)) {
		        		if ("是".equals(familyHistory)) {
			    			familyHistory = "有";
			    		} else if ("否".equals(familyHistory)) {
			    			familyHistory = "无";
			    		} 
		        	} else {
		        		familyHistory = "";
		        	}
		        	map.put("familyHistory", familyHistory);
		        	map.put("recordDate", recordDate);
		        	map.put("height", height);
		        	map.put("weight", weight);
		        	map.put("BMI", BMI);
		        	map.put("waistline", waistline);
		        	map.put("highPressure", highPressure);
		        	map.put("lowPressure", lowPressure);
		        	map.put("pulse", pulse);
		        	map.put("temperature", temperature);
		        	map.put("oxygen", oxygen);
		        	map.put("hipline", hipline);
		        	map.put("fatContent", fatContent);
//		        	map.put("tc", tc);
//		        	map.put("tg", tg);
//		        	map.put("ldl", ldl);
//		        	map.put("hdl", hdl);
		        	map.put("tizhi", tizhi);
		        	map.put("eyeCheck", eyeCheck);
		        	map.put("nationality", nationality);
		        	map.put("householdRegistrationType", householdRegistrationType);
		        	map.put("contactName", contactName);
		        	map.put("contactMobile", contactMobile);
		        	map.put("address", address);
		        	map.put("district", district);
		        	map.put("checkPlace", checkPlace);
		        	map.put("checkGroup", checkGroup);
		        	
		        	//计算腰臀比
		        	Double WHR = (double) 0;
		        	if (waistline != null && hipline != null) {
		        		WHR = ParamUtils.doubleScale((waistline / hipline), 2);
		        	}
		        	map.put("WHR", WHR);
		        	
		        	//计算血糖分数
//		        	int riskScore = healthCheckService.computeRiskScore(map);
//		        	map.put("riskScore", riskScore);
		        	
		        	//空腹血糖
		        	Double bloodGlucoseVal = (double) 0;
		        	if (StringUtils.isNotEmpty(bloodGlucose)) {
		        		map.put("bloodGlucoseStr", bloodGlucose);
		    			if (bloodGlucose.indexOf("<")!=-1) {
		    				bloodGlucoseVal = 1.11;
		    			}else if (bloodGlucose.indexOf(">")!=-1) {
		    				bloodGlucoseVal = 33.3;
		    			} else {
		    				bloodGlucoseVal = Double.parseDouble(bloodGlucose);
		    			}
		    		}
		        	map.put("bloodGlucose", bloodGlucoseVal);
		        	
		        	//糖后两小时
		        	Double bloodGlucose2hVal = (double) 0;
		        	if (StringUtils.isNotEmpty(bloodGlucose2h)) {
		        		map.put("bloodGlucose2hStr", bloodGlucose2h);
		    			if (bloodGlucose2h.indexOf("<")!=-1) {
		    				bloodGlucose2hVal = 1.11;
		    			}else if (bloodGlucose2h.indexOf(">")!=-1) {
		    				bloodGlucose2hVal = 33.3;
		    			} else {
		    				bloodGlucose2hVal = Double.parseDouble(bloodGlucose2h);
		    			}
		    		}
		        	map.put("bloodGlucose2h", bloodGlucose2hVal);
		        	
		        	//随机血糖
		        	Double bloodGlucoseRandomVal = (double) 0;
		        	if (StringUtils.isNotEmpty(bloodGlucoseRandom)) {
		        		map.put("bloodGlucoseRandomStr", bloodGlucoseRandom);
		    			if (bloodGlucoseRandom.indexOf("<")!=-1) {
		    				bloodGlucoseRandomVal = 1.11;
		    			}else if (bloodGlucoseRandom.indexOf(">")!=-1) {
		    				bloodGlucoseRandomVal = 33.3;
		    			} else {
		    				bloodGlucoseRandomVal = Double.parseDouble(bloodGlucoseRandom);
		    			}
		    		}
		        	map.put("bloodGlucoseRandom", bloodGlucoseRandomVal);
		        	
		        	//血糖情况
		        	/*String bloodSugarCondition = "";
		        	String OGTTTest = "";
		        	if (bloodGlucoseVal >= 6.1 || bloodGlucose2hVal >= 7.8 || bloodGlucoseRandomVal >= 11.1){
		        		bloodSugarCondition = "血糖异常";
		        		OGTTTest = "需要";
			   		} else if (riskScore==0 && bloodGlucoseVal==0 && bloodGlucose2hVal == 0 && bloodGlucoseRandomVal==0) {
			   			bloodSugarCondition = "";
			   			OGTTTest = "";
			   		} else if (riskScore<25) {
			   			bloodSugarCondition = "正常";
			   			OGTTTest = "不需要";
			   		} else if (riskScore>=25) {
			   			bloodSugarCondition = "糖尿病高危";
			   			OGTTTest = "需要";
			   		}
		        	map.put("bloodSugarCondition", bloodSugarCondition);
		        	map.put("OGTTTest", OGTTTest);*/
		        	
		        	//血压情况
		        	String bloodPressureCondition = "";
		        	String bloodPressureTest = "";
	           		if (highPressure == null && lowPressure == null) {
	           			bloodPressureCondition = "";
	           			bloodPressureTest = "";
	           		} else if(highPressure < 130 && lowPressure < 85){
	           			bloodPressureCondition = "正常";
	        			bloodPressureTest = "不需要";
	        		} else if(highPressure >= 130 || lowPressure >= 85){
	        			bloodPressureCondition = "血压异常";
	        			bloodPressureTest = "需要";
	        		}
	           		map.put("bloodPressureCondition", bloodPressureCondition);
	           		map.put("bloodPressureTest", bloodPressureTest);
		        	
		        	//血脂情况
	           		Double tgNumber = (double) 0;
		        	if (StringUtils.isNotEmpty(tg)) {
		        		map.put("tgStr", tg);
		    			if(tg.indexOf(">") != -1){
		    				tgNumber = 5.65;
		    			}else if(tg.indexOf("<") != -1){
		    				tgNumber = 0.57;
		    			}else{
		    				tgNumber = Double.parseDouble(tg);
		    			}
		    		}
		        	map.put("tg", tgNumber);
		        	
		        	Double tcNumber = (double) 0;
		    		if (StringUtils.isNotEmpty(tc)) {
		    			map.put("tcStr", tc);
		    			if(tc.indexOf(">") != -1){
		    				tcNumber = 10.36;
		    			}else if(tc.indexOf("<") != -1){
		    				tcNumber = 2.59;
		    			}else{
		    				tcNumber = Double.parseDouble(tc);
		    			}	
		    		}
		    		map.put("tc", tcNumber);
		    		
		    		Double hdlNumber = (double) 0;
		    		if (StringUtils.isNotEmpty(hdl)) {
		    			map.put("hdlStr",hdl);
		    			if(hdl.indexOf(">") != -1){
		    				hdlNumber = 2.59;
		    			}else if(hdl.indexOf("<") != -1){
		    				hdlNumber = 0.39;
		    			}else{
		    				hdlNumber = Double.parseDouble(hdl);
		    			}
		    		}
		    		map.put("hdl", hdlNumber);
		    		
		    		if (StringUtils.isNotEmpty(ldl)) {
		    			map.put("ldlStr", ldl);
		    			map.put("ldl", ParamUtils.getDoubleValue(ldl));
		    		}
		        	
		    		String bloodLipidCondition = "";
		    		String bloodLipidTest = "";
		        	if(StringUtils.isNotEmpty(tc) && StringUtils.isNotEmpty(tg) && StringUtils.isNotEmpty(hdl)){
		    			if(tcNumber>=5.2 || tgNumber>=1.7 || hdlNumber<1){
		    				bloodLipidCondition = "血脂异常";
		    				bloodLipidTest = "需要";
		    			}
		    			
		    			if(tcNumber<5.2 && tgNumber<1.7 && hdlNumber>=1){
		    				bloodLipidCondition = "正常";
		    				bloodLipidTest = "不需要";
		    			}
		        	}
		        	map.put("bloodLipidCondition", bloodLipidCondition);
		        	map.put("bloodLipidTest", bloodLipidTest);
		        	
		        	System.out.println("map:" + map);
		        	
		        }
    		}
    		
        }catch (Exception e) {
        	
        }
        
	}
	
	/*public void uploadFile() {
		MultipartHttpServletRequest mhsq = (MultipartHttpServletRequest) request;
		MultipartFile csFile = mhsq.getFile("csFile");
		if (csFile == null) {
			return Message.data("error");
		}
		
		File dir=new File("E://temp");
	    if(!dir.exists()){
	    	dir.mkdirs();
	    }
	    
        try { 
        	File newFile = new File(dir+"/test.xlsx");
        	csFile.transferTo(newFile);
        	File newFile = new File("E:/temp/test.csv");
        	
        	LineNumberReader reader = new LineNumberReader(new FileReader(newFile));
  		    String str = null;
  		    reader.readLine();
  		    int k = 0;
  		    String errMsgAll = "";
  		    while ((str = reader.readLine()) != null) {
 				String[] datas = str.split(",");
 				String errMsg = "";
 				for (int i=0; i < 1; i++) {
 					//姓名
 	    			String name = datas[0];
 	    			if (StringUtils.isEmpty(name)) {
 	    				errMsg += "姓名不能为空;";
 	    			}
 	    			
 	    			//身份证号
 	    			String customerId = datas[1];
 	    			String gender = "";
 	    			int age = 0;
 	    			String birthday = "";
 	    			if (StringUtils.isEmpty(customerId)) {
 	    				errMsg += "身份证号不能为空;";
 	    			} else {
 	    				boolean valid = IdcardValidator.isValidatedAllIdcard(customerId);
 	    		        if (!valid) {
 	    		        	errMsg += "身份证号非法;";
 	    		        } else {
 	    		        	gender = getGenderByIdCard(customerId);
 	    		        	age = getAgeByIdCard(customerId);
 	    		        	birthday = getBirthdayByIdCard(customerId);
 	    		        }
 	    			}
 	    			
 	    			//手机号
 			        String mobile = datas[2];
 			        if (StringUtils.isEmpty(mobile)) {
 			        	errMsg += "手机号不能为空;";
 			        } else {
 			        	boolean mobileValid = isMobileNum(mobile);
 				        if (!mobileValid) {
 				        	errMsg += "手机号非法;";
 				        }
 			        }
 			        
 			        //检测时间
 			        String checkTime = datas[3];
 			        if (StringUtils.isEmpty(checkTime)) {
 			        	errMsg += "检测时间不能为空;";
 			        }
 			        
 			        //糖尿病
 			        String tnb = datas[4];
 			        if (StringUtils.isNotEmpty(tnb) && (!tnb.equals("是") && !tnb.equals("否"))) {
 			        	errMsg += "请规范填写糖尿病;";
 			        } 
 			        
 			        //高血压
 			        String gxy = datas[5];
 			        if (StringUtils.isNotEmpty(gxy) && (!gxy.equals("是") && !gxy.equals("否"))) {
 			        	errMsg += "请规范填写高血压;";
 			        }
 			        
 			        //高血脂
 			        String gxz = datas[6];
 			        if (StringUtils.isNotEmpty(gxz) && (!gxz.equals("是") && !gxz.equals("否"))) {
 			        	errMsg += "请规范填写高血脂;";
 			        }
 			        
 			        //冠心病
 			        String gxb = datas[7];
 			        if (StringUtils.isNotEmpty(gxb) && (!gxb.equals("是") && !gxb.equals("否"))) {
 			        	errMsg += "请规范填写冠心病;";
 			        }
 			        
 			        //家族史
 			        String familyHistory = datas[8];
 			        if (StringUtils.isEmpty(familyHistory)) {
 			        	errMsg += "家族史不能为空;";
 			        } else if (!familyHistory.equals("有") && !familyHistory.equals("无")) {
 			        	errMsg += "请规范填写家族史;";
 			        }
 			        
 			        //建档日期
 			        String recordDate = datas[9];
 			        if (StringUtils.isEmpty(recordDate)) {
 			        	errMsg += "建档日期不能为空;";
 			        }
 			        
 			        //身高
 			        String height = datas[10];
 			        if (StringUtils.isNotEmpty(height)) {
 			        	boolean heightIsNum = isNumeric(height);
 			        	if (!heightIsNum || Double.parseDouble(height) <= 120) {
 			        		errMsg += "请规范填写身高;";
 			        	}
 			        } 
 			        
 			        //体重
 			        String weight = datas[11];
 			        
 			        //BMI
 			        String BMI = datas[12];
 			        if (StringUtils.isNotEmpty(BMI)) {
 			        	BMI = ParamUtils.doubleScale(Double.parseDouble(BMI), 1) + "";
 			        }
 			        
 			        //腰围
 			        String waistline = datas[13];
 			        
 			        //收缩压
 			        String highPressure = datas[14];
 			        
 			        //舒张压
 			        String lowPressure = datas[15];
 			        
 			        //脉率
 			        String pulse = datas[16];
 			        
 			        //体温
 			        String temperature = datas[17];
 			        
 			        //血氧
 			        String oxygen = datas[18];
 			        
 			        //臀围
 			        String hipline = datas[19];
 			        
 			        //体脂率
 			        String fatContent = datas[20];
 			        if (StringUtils.isNotEmpty(fatContent)) {
 			        	fatContent = ParamUtils.doubleScale(Double.parseDouble(fatContent), 1) + "";
 			        }
 			        
 			        //空腹血糖
 			        String bloodGlucose = datas[21];
 			        
 			        //餐后2h血糖
 			        String bloodGlucose2h = datas[22];
 			        
 			        //随机血糖
 			        String bloodGlucoseRandom = datas[23];
 			        
 			        //总胆固醇
 			        String tc = datas[24];
 			        
 			        //甘油三酯
 			        String tg = datas[25];
 			        
 			        //血清低密度脂蛋白
 			        String ldl = datas[26];
 			        
 			        //血清高密度脂蛋白
 			        String hdl = datas[27];
 			        
 			        //中医体质辨识结果
 			        String tizhi = datas[28];
 			        
 			        //中医眼象辨识
 			        String eyeCheck = datas[29];
 			        if (StringUtils.isNotEmpty(eyeCheck)) {
 			        	if (!eyeCheck.equals("已检测") && !eyeCheck.equals("未检测")) {
 			        		errMsg += "请规范填写家族史;";
 			        	}
 			        }
 			        
 			        //民族
 			        String nationality = datas[30];
 			        
 			        //常驻类型
 			        String householdRegistrationType = datas[31];
 			        if (StringUtils.isNotEmpty(householdRegistrationType)) {
 			        	if (!householdRegistrationType.equals("户籍") && !householdRegistrationType.equals("未户籍")) {
 			        		errMsg += "请规范填写常驻类型;";
 			        	}
 			        }
 			        
 			        //联系人姓名
 			        String contactName = datas[32];
 			        
 			        //联系人电话
 			        String contactMobile = datas[33];
 			        
 			        //地址
 			        String address = datas[34];
 			        
 			        //筛查区域
 			        String district = datas[35];
 			        
 			        //筛查地点
 			        String checkPlace = datas[36];
 			        
 			        //筛查组
 			        String checkGroup = datas[37];
 			        
 			        System.out.println("errMsg:" + errMsg);
 			        
 			        //没有错误信息，导入数据
 			        errMsgAll += errMsg;
 			        if (StringUtils.isEmpty(errMsgAll)) {
 			        	Map<String, Object> map = Maps.newHashMap();
 			        	map.put("name", name);
 			        	map.put("customerId", customerId);
 			        	map.put("gender", gender);
 			        	map.put("age", age);
 			        	map.put("birthday", birthday);
 			        	map.put("mobile", mobile);
 			        	map.put("checkTime", checkTime);
 			        	
 			        	String disease = "";
 			        	if (StringUtils.isNotEmpty(tnb)) {
 			        		if (tnb.equals("是")) {
 			        			disease += "糖尿病" + ","; 
 				        		map.put("dm", "是");
 			        		}
 			        		
 			        		if (tnb.equals("否")) {
 				        		map.put("dm", "否");
 			        		}
 			        	}
 			        	
 			        	if (StringUtils.isNotEmpty(gxy)) {
 			        		if (gxy.equals("是")) {
 			        			disease += "高血压" + ","; 
 				        		map.put("htn", "是");
 			        		}
 			        		
 			        		if (gxy.equals("否")) {
 				        		map.put("htn", "否");
 			        		}
 			        	}
 			        	
 			        	if (StringUtils.isNotEmpty(gxz)) {
 			        		if (gxz.equals("是")) {
 			        			disease += "高血脂" + ","; 
 				        		map.put("hpl", "是");
 			        		}
 			        		
 			        		if (gxz.equals("否")) {
 				        		map.put("hpl", "否");
 			        		}
 			        	}
 			        	
 			        	if (StringUtils.isNotEmpty(gxb)) {
 			        		if (gxb.equals("是")) {
 			        			disease += "冠心病" + ","; 
 				        		map.put("cpd", "是");
 			        		}
 			        		
 			        		if (gxb.equals("否")) {
 			        			map.put("cpd", "是");
 			        		}
 			        	}
 			        	
 			        	disease = disease.substring(0, disease.length() - 1);
 			        	map.put("disease", disease);
 			        	
 			        	if (StringUtils.isNotEmpty(familyHistory)) {
 			        		if ("是".equals(familyHistory)) {
 				    			familyHistory = "有";
 				    		} else if ("否".equals(familyHistory)) {
 				    			familyHistory = "无";
 				    		} 
 			        	} else {
 			        		familyHistory = "";
 			        	}
 			        	map.put("familyHistory", familyHistory);
 			        	map.put("recordDate", recordDate);
 			        	map.put("height", height);
 			        	map.put("weight", weight);
 			        	map.put("BMI", BMI);
 			        	map.put("waistline", waistline);
 			        	map.put("highPressure", highPressure);
 			        	map.put("lowPressure", lowPressure);
 			        	map.put("pulse", pulse);
 			        	map.put("temperature", temperature);
 			        	map.put("oxygen", oxygen);
 			        	map.put("hipline", hipline);
 			        	map.put("fatContent", fatContent);
// 			        	map.put("tc", tc);
// 			        	map.put("tg", tg);
// 			        	map.put("ldl", ldl);
// 			        	map.put("hdl", hdl);
 			        	map.put("tizhi", tizhi);
 			        	map.put("eyeCheck", eyeCheck);
 			        	map.put("nationality", nationality);
 			        	map.put("householdRegistrationType", householdRegistrationType);
 			        	map.put("contactName", contactName);
 			        	map.put("contactMobile", contactMobile);
 			        	map.put("address", address);
 			        	map.put("district", district);
 			        	map.put("checkPlace", checkPlace);
 			        	map.put("checkGroup", checkGroup);
 			        	
 			        	//计算腰臀比
 			        	Double WHR = (double) 0;
 			        	if ((StringUtils.isNotEmpty(waistline) && isNumeric(waistline)) && (StringUtils.isNotEmpty(hipline) && isNumeric(hipline))) {
 			        		WHR = ParamUtils.doubleScale((Double.parseDouble(waistline) / Double.parseDouble(hipline)), 2);
 			        	}
 			        	map.put("WHR", WHR);
 			        	
 			        	//计算血糖分数
 			        	int riskScore = healthCheckService.computeRiskScore(map);
 			        	map.put("riskScore", riskScore);
 			        	
 			        	//空腹血糖
 			        	Double bloodGlucoseVal = (double) 0;
 			        	if (StringUtils.isNotEmpty(bloodGlucose)) {
 			        		map.put("bloodGlucoseStr", bloodGlucose);
 			    			if (bloodGlucose.indexOf("<")!=-1) {
 			    				bloodGlucoseVal = 1.11;
 			    			}else if (bloodGlucose.indexOf(">")!=-1) {
 			    				bloodGlucoseVal = 33.3;
 			    			} else {
 			    				bloodGlucoseVal = Double.parseDouble(bloodGlucose);
 			    			}
 			    		}
 			        	map.put("bloodGlucose", bloodGlucoseVal);
 			        	
 			        	//糖后两小时
 			        	Double bloodGlucose2hVal = (double) 0;
 			        	if (StringUtils.isNotEmpty(bloodGlucose2h)) {
 			        		map.put("bloodGlucose2hStr", bloodGlucose2h);
 			    			if (bloodGlucose2h.indexOf("<")!=-1) {
 			    				bloodGlucose2hVal = 1.11;
 			    			}else if (bloodGlucose2h.indexOf(">")!=-1) {
 			    				bloodGlucose2hVal = 33.3;
 			    			} else {
 			    				bloodGlucose2hVal = Double.parseDouble(bloodGlucose2h);
 			    			}
 			    		}
 			        	map.put("bloodGlucose2h", bloodGlucose2hVal);
 			        	
 			        	//随机血糖
 			        	Double bloodGlucoseRandomVal = (double) 0;
 			        	if (StringUtils.isNotEmpty(bloodGlucoseRandom)) {
 			        		map.put("bloodGlucoseRandomStr", bloodGlucoseRandom);
 			    			if (bloodGlucoseRandom.indexOf("<")!=-1) {
 			    				bloodGlucoseRandomVal = 1.11;
 			    			}else if (bloodGlucoseRandom.indexOf(">")!=-1) {
 			    				bloodGlucoseRandomVal = 33.3;
 			    			} else {
 			    				bloodGlucoseRandomVal = Double.parseDouble(bloodGlucoseRandom);
 			    			}
 			    		}
 			        	map.put("bloodGlucoseRandom", bloodGlucoseRandomVal);
 			        	
 			        	//血糖情况
 			        	String bloodSugarCondition = "";
 			        	String OGTTTest = "";
 			        	if (bloodGlucoseVal >= 6.1 || bloodGlucose2hVal >= 7.8 || bloodGlucoseRandomVal >= 11.1){
 			        		bloodSugarCondition = "血糖异常";
 			        		OGTTTest = "需要";
 				   		} else if (riskScore==0 && bloodGlucoseVal==0 && bloodGlucose2hVal == 0 && bloodGlucoseRandomVal==0) {
 				   			bloodSugarCondition = "";
 				   			OGTTTest = "";
 				   		} else if (riskScore<25) {
 				   			bloodSugarCondition = "正常";
 				   			OGTTTest = "不需要";
 				   		} else if (riskScore>=25) {
 				   			bloodSugarCondition = "糖尿病高危";
 				   			OGTTTest = "需要";
 				   		}
 			        	map.put("bloodSugarCondition", bloodSugarCondition);
 			        	map.put("OGTTTest", OGTTTest);
 			        	
 			        	//血压情况
 			        	String bloodPressureCondition = "";
 			        	String bloodPressureTest = "";
 		           		if (StringUtils.isEmpty(highPressure) && StringUtils.isEmpty(lowPressure)) {
 		           			bloodPressureCondition = "";
 		           			bloodPressureTest = "";
 		           		} else if(Double.parseDouble(highPressure) < 130 && Double.parseDouble(lowPressure) < 85){
 		           			bloodPressureCondition = "正常";
 		        			bloodPressureTest = "不需要";
 		        		} else if(Double.parseDouble(highPressure) >= 130 || Double.parseDouble(lowPressure) >= 85){
 		        			bloodPressureCondition = "血压异常";
 		        			bloodPressureTest = "需要";
 		        		}
 		           		map.put("bloodPressureCondition", bloodPressureCondition);
 		           		map.put("bloodPressureTest", bloodPressureTest);
 			        	
 			        	//血脂情况
 		           		Double tgNumber = (double) 0;
 			        	if (StringUtils.isNotEmpty(tg)) {
 			        		map.put("tgStr", tg);
 			    			if(tg.indexOf(">") != -1){
 			    				tgNumber = 5.65;
 			    			}else if(tg.indexOf("<") != -1){
 			    				tgNumber = 0.57;
 			    			}else{
 			    				tgNumber = Double.parseDouble(tg);
 			    			}
 			    		}
 			        	map.put("tg", tgNumber);
 			        	
 			        	Double tcNumber = (double) 0;
 			    		if (StringUtils.isNotEmpty(tc)) {
 			    			map.put("tcStr", tc);
 			    			if(tc.indexOf(">") != -1){
 			    				tcNumber = 10.36;
 			    			}else if(tc.indexOf("<") != -1){
 			    				tcNumber = 2.59;
 			    			}else{
 			    				tcNumber = Double.parseDouble(tc);
 			    			}	
 			    		}
 			    		map.put("tc", tcNumber);
 			    		
 			    		Double hdlNumber = (double) 0;
 			    		if (StringUtils.isNotEmpty(hdl)) {
 			    			map.put("hdlStr",hdl);
 			    			if(hdl.indexOf(">") != -1){
 			    				hdlNumber = 2.59;
 			    			}else if(hdl.indexOf("<") != -1){
 			    				hdlNumber = 0.39;
 			    			}else{
 			    				hdlNumber = Double.parseDouble(hdl);
 			    			}
 			    		}
 			    		map.put("hdl", hdlNumber);
 			    		
 			    		if (StringUtils.isNotEmpty(ldl)) {
 			    			map.put("ldlStr", ldl);
 			    			map.put("ldl", ParamUtils.getDoubleValue(ldl));
 			    		}
 			        	
 			    		String bloodLipidCondition = "";
 			    		String bloodLipidTest = "";
 			        	if(StringUtils.isNotEmpty(tc) && StringUtils.isNotEmpty(tg) && StringUtils.isNotEmpty(hdl)){
 			    			if(tcNumber>=5.2 || tgNumber>=1.7 || hdlNumber<1){
 			    				bloodLipidCondition = "血脂异常";
 			    				bloodLipidTest = "需要";
 			    			}
 			    			
 			    			if(tcNumber<5.2 && tgNumber<1.7 && hdlNumber>=1){
 			    				bloodLipidCondition = "正常";
 			    				bloodLipidTest = "不需要";
 			    			}
 			        	}
 			        	map.put("bloodLipidCondition", bloodLipidCondition);
 			        	map.put("bloodLipidTest", bloodLipidTest);
 			        	
 			        	System.out.println("map:" + map);
 				}
  		    }
        	
        	Workbook wb = null;
            try {
            	wb = new XSSFWorkbook(newFile);
            } catch (Exception ex) {
            	wb = new HSSFWorkbook(new FileInputStream(newFile));
            }
            Sheet sheet = wb.getSheetAt(0);
    		int rows = sheet.getPhysicalNumberOfRows();
    		String errMsgAll = "";
    		List<Map<String, Object>> list = Lists.newArrayList();
    		for (int i = 1; i < rows; i++) {
    			String errMsg = "";
    			Row row = sheet.getRow(i);
    			
    			
		        	
		        }
    		}
    		
  		    }
        }catch (Exception e) {
        	
        }
        
	}*/
	
	public static String getGenderByIdCard(String customerId) {
		String gender;  
		if (Integer.parseInt(customerId.substring(16).substring(0, 1)) % 2 == 0) {// 判断性别  
			gender = "女";  
        } else {  
        	gender = "男";  
        }
		return gender;
	}
	
	public static int getAgeByIdCard(String customerId) {
		int age = 0;   
		
		String year = customerId.substring(6,10);// 得到年份  
	    String month = customerId.substring(10,12);// 得到月份  
		Date date = new Date();// 得到当前的系统时间  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
        String fyear = format.format(date).substring(0, 4);// 当前年份  
        String fmonth = format.format(date).substring(5, 7);// 月份  
       
        if (Integer.parseInt(month) <= Integer.parseInt(fmonth)) { // 当前月份大于用户出身的月份表示已过生日
            age = Integer.parseInt(fyear) - Integer.parseInt(year) + 1;  
        } else {// 当前用户还没过生日
            age = Integer.parseInt(fyear) - Integer.parseInt(year);  
        }  
		return age;
	}
	
	public static String getBirthdayByIdCard(String customerId) {
		String year = customerId.substring(6,10);// 得到年份  
	    String month = customerId.substring(10,12);// 得到月份  
	    String day = customerId.substring(12,14);// 得到月份  
	    String birthday = year + "-" + month + "-" + day;   
		
		return birthday;
	}
	
	public static boolean isMobileNum(String telNum){
		Pattern p = Pattern.compile("^1\\d{10}$");
		Matcher m = p.matcher(telNum);
		return m.matches();
	}

	
	 /*public boolean isNumeric(String str){
		 Pattern pattern = Pattern.compile("[0-9]*");
         Matcher isNum = pattern.matcher(str);
         if( !isNum.matches() ){
             return false;
         }
		 return true;
	 }*/
	
	public static boolean isNumeric(String str){
		for (int i = 0; i < str.length(); i++){
			System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	private String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		}
		return cell.getStringCellValue();
	}
	
	private Integer getCellIntegerValue(XSSFCell cell) {
		if (cell == null) {
			return null;
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			double dvalue = cell.getNumericCellValue();
			Integer ivalue = (int)dvalue;
			return ivalue;
		} else {
			try {
				Integer ivalue = Integer.parseInt(cell.getStringCellValue());
				return ivalue;
			} catch (Exception e) {}
		}
		return null;
	}

	private Double getCellDoubleValue(XSSFCell cell) {
		if (cell == null) {
			return null;
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			double dvalue = cell.getNumericCellValue();
			return dvalue;
		} else {
			try {
				Double dvalue = Double.parseDouble(cell.getStringCellValue());
				return dvalue;
			} catch (Exception e) {}
		}
		return null;
	}
	
}
