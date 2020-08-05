package com.capiltalbio.health.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.capitalbio.auth.util.Constant;
import com.capitalbio.auth.util.HttpUtils;
import com.capitalbio.auth.util.JwtUtil;
import com.capitalbio.common.util.EncryptUtil;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.common.util.MD5Util;
import com.capitalbio.common.util.RandomUtil;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class HealthCheckClient {
	String apikey = "fcba709d-4ff8-46cf-9d7c-21f384b7228e";
//	String host = "http://101.200.176.128:18072/chongqing";
//	String host = "http://117.78.43.154/chongqing";
//	String host = "http://59.110.44.175:8081/shanghai/";
//	String host = "http://114.115.233.33:8080/screening";
	String host = "http://localhost:8080/screen";
	String authHost = "http://114.115.233.33:8080/chs_fx/rest/";
	
	public void uploadCustomer() throws Exception{
		
		String url = host+"/healthcheck/uploadCustomer.json";
		Map<String,Object> map = Maps.newHashMap();
		String token = JwtUtil.createJWT(Constant.JWT_ID, "11111111111111111", Constant.JWT_TTL);
		map.put("token", token);
//		map.put("apikey", apikey);
		map.put("customerId", "11111111111111111");
		map.put("name", "李四");
		map.put("cardNo", "11111111111111111");
		map.put("birthday", "2017-01-01");
		map.put("age", 1);
		map.put("sex", "女");
		map.put("married", "否");
		map.put("nation", "汉");
		map.put("phone", "123456777");
		map.put("fixLinePhone", "2222222");
		map.put("vocation", "");
		map.put("address", "XXXXXX");
		map.put("issuingAuthority", "北京市公安局");
		map.put("idValiddate", "2027-01-01");
		map.put("archivesDate", "2017-02-09");
		sendRequest(url, map);
		
	}

	
	public void uploadCheck() throws Exception{
		
		String url = host+"/healthcheck/uploadCheck.json";
		TreeMap<String,Object> map = new TreeMap<String,Object>();
		
//		map.put("apikey", apikey);
		map.put("customerId", "120225198412070032");
		map.put("checkTime", 1496419200000L);
		map.put("temperature", 36.9);
		map.put("bloodGlucose", 0.0);
		map.put("height", 0.0);
		map.put("weight", 0.0);
		map.put("bmi", 0.0);
		map.put("highPressure", 0);
		map.put("lowPressure", 0);
		map.put("mailv", 81);
		map.put("pulse", 0);
		map.put("oxygen", 96);
		map.put("fatContent", 0.0);
		map.put("waistline", 0.0);
		String token = JwtUtil.createJWT(Constant.JWT_ID, "120225198412070032", Constant.JWT_TTL);
		map.put("token", token);
//		String token = AuthUtil.genToken(map);
//		map.put("token", token);
		sendRequest(url, map);
		
	}

	public Map<String,Object> mockInfo(int no) {
//		int no = RandomUtil.randomInt(1, 1000)+1000;
		
		String customerId = "410526196001233785";
//		String customerId = "120225198412070" + (""+(no+1000)).substring(1);
		String encCustomerId = EncryptUtil.encryptByKey(customerId);
		Date checkTime = RandomUtil.randomDate("2017-10-01", "2017-10-12");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> map = Maps.newHashMap();
		map.put("customerId", encCustomerId);
		map.put("checkTime", sdf.format(checkTime));
		map.put("name","张三"+no);
//		int sexNo = RandomUtil.randomInt(1, 2);
//		map.put("sex",sexNo==1?"男":"女");
//		map.put("age",RandomUtil.randomInt(12, 80));
		map.put("contact","13033333333");
//		map.put("riskScore",4.5);
		map.put("temperature", RandomUtil.randomDouble(35, 40));
		int weight = RandomUtil.randomInt(45, 90);
		int height = RandomUtil.randomInt(150, 180);
		map.put("height", height);
		map.put("weight", weight);
		double bmi = ((double)weight/((double)height*(double)height/10000));
		bmi = new BigDecimal(bmi).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		map.put("BMI", bmi);
		map.put("highPressure", RandomUtil.randomInt(80,180));
		map.put("lowPressure", RandomUtil.randomInt(60,120));
		map.put("pulse", RandomUtil.randomInt(40,100));
		map.put("oxygen", RandomUtil.randomInt(90,100));
		map.put("fatContent", RandomUtil.randomInt(10,30));
		map.put("waistline", RandomUtil.randomInt(60,130));
		map.put("bloodGlucose", RandomUtil.randomDouble(3, 8));
		int bloodRandom = RandomUtil.randomInt(0, 1);
		if (bloodRandom > 0) {
			map.put("bloodGlucose2h", RandomUtil.randomDouble(5, 12));
		}
		int eyeCheckNo = RandomUtil.randomInt(0, 2);
		if (eyeCheckNo > 0) {
			map.put("eyeCheck", eyeCheckNo==1?"已检测":"未检测");
		}
		int familyHistoryNo = RandomUtil.randomInt(0, 2);
		if (familyHistoryNo > 0) {
			map.put("familyHistory", familyHistoryNo==1?"已检测":"未检测");
		}
		String[] diseases = {"糖尿病","冠心病","高血压","高血脂"};
		int diseaseNo = RandomUtil.randomInt(0, 4);
		if (diseaseNo > 0) {
			map.put("disease", diseases[diseaseNo-1]);
		}
		String[] tizhis = {"平和质","气虚质","阳虚质","阴虚质","痰湿质","湿热质","气郁质","血瘀质","特禀质"};
		int tizhiNo = RandomUtil.randomInt(0, 9);
		if (tizhiNo > 0) {
			map.put("tizhi", tizhis[tizhiNo-1]);
		}
		
		map.put("checkPlace","小区"+RandomUtil.randomInt(1, 4));
		map.put("checkGroup","李四");
		Long sysTime = System.currentTimeMillis();
		map.put("sysTime", sysTime);
		String token = MD5Util.MD5Encode(encCustomerId+sysTime+Constant.SECRET_KEY);
		map.put("token", token);

		return  map;
	}
	
	public void uploadInfo(int no) throws Exception{
		
		String url = host+"/healthcheck/uploadInfo.json";
		Map<String,Object> map = new HashMap<String,Object>();
		String customerId = "410526199009173785";
//		String customerId = "120225198412070032";
		String encCustomerId = EncryptUtil.encryptByKey(customerId);
		map.put("customerId", encCustomerId);
		map.put("checkTime", "2017-08-16 12:30:35");
		map.put("name","李四");
		map.put("sex","男");
		map.put("age",18);
		map.put("contact","13033333333");
//		map.put("riskScore",4.5);
		map.put("temperature", 36.9);
		map.put("bloodGlucose", 0.0);
		map.put("height", 180);
		map.put("weight", 80);
		map.put("BMI", 24);
		map.put("highPressure", 120);
		map.put("lowPressure", 90);
		map.put("pulse", 60);
		map.put("oxygen", 96);
		map.put("fatContent", 20);
		map.put("waistline", 110);
		map.put("bloodGlucose", 6.1);
		map.put("bloodGlucose2h", 6.5);
		map.put("eyeCheck", "已检测");
		map.put("bloodKnown", "不了解");
		map.put("familyHistory", "无");
		map.put("disease", "高血压");
		map.put("tizhi","平和质"); 
		map.put("checkPlace","XX小区");
		map.put("checkGroup","ABC");

		Long sysTime = System.currentTimeMillis();
		map.put("sysTime", sysTime);
		String token = MD5Util.MD5Encode(encCustomerId+sysTime+Constant.SECRET_KEY);
		map.put("token", token);
//		Map<String,Object> map = mockInfo(no);
		sendRequest(url, map);
		
	}

	private String sendRequest(String url, Map<String, Object> map)
			throws Exception {
		HttpPost httpPost = new HttpPost(url);
		String json = JsonUtil.mapToJson(map);
//		System.out.println(json);
		StringEntity se = new StringEntity(json, "UTF-8");
		httpPost.setEntity(se);
		httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
//		httpPost.addHeader(HTTP.CONTENT_ENCODING, "gzip");
		se.setContentType("text/json");
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
				"application/json"));
		HttpResponse response = new DefaultHttpClient().execute(httpPost);
		int code = response.getStatusLine().getStatusCode();
		System.out.println("code:" + code);
		if (code == 200) {
			String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
			printMessage(result);
			Map<String,Object> retmap = JsonUtil.jsonToMap(result);
			int retcode = (Integer)retmap.get("code");
			if (retcode == 0) {
				String data = (String)retmap.get("data");
				printMessage(data);
				return data;
			}
		} else {
			printMessage("HTTP error code:" + code);
		}
		return null;
	}
	protected void printMessage(String msg) {
		System.out.print("*****************");
		System.out.print(msg);
		System.out.println("*****************");
	}

	public static void main(String[] args) throws Exception{
		HealthCheckClient hcc = new HealthCheckClient();
//		Map<String,Object> map = hcc.applyToken();
//		String userId = (String)map.get("userId");
//		String token = (String)map.get("token");
//		hcc.uploadHealthCheck(userId, token);
		
		hcc.updateHealthCheck("healthcheck");
//		hcc.updateHealthCheck("customer");
	}
	
	public void uploadHealthCheck(String userId, String token) throws Exception{
		
		String url = host+"/healthcheck/uploadHealthCheck.json";
		Map<String,Object> map = new HashMap<String,Object>();
		String customerId = "410526199009173789";
		map.put("customerId", customerId);
		map.put("checkTime", "2017-08-16 12:30:35");
		map.put("name","李四");
		map.put("gender","男");//gender
		map.put("age",18);
		map.put("birthday","2018-06-06");
		map.put("contact","13033333333");
		map.put("temperature", 36.9);
		map.put("bloodGlucose", 0.0);
		map.put("height", 180);
		map.put("weight", 80);
		map.put("BMI", 24);
		map.put("highPressure", 120);
		map.put("lowPressure", 90);
		map.put("pulse", 60);
		map.put("oxygen", 96);
		map.put("fatContent", 20);
		map.put("waistline", 110);
		map.put("bloodGlucose", 6.1);
		map.put("bloodGlucose2h", 6.5);
		map.put("eyeCheck", "已检测");
		map.put("bloodKnown", "不了解");
		map.put("familyHistory", "无");
		map.put("disease", "高血压");
		map.put("tizhi","平和质"); 
		map.put("checkPlace","XX小区");
		map.put("checkGroup","ABC");

//		Map<String,Object> map = mockInfo(no);
		sendRequest(url, map, userId, token);
	}
	
	public Map<String, Object> applyToken() {
		String webSignature = JwtUtil.createJWT(Constant.JWT_ID, "healthcheck", Constant.JWT_TTL);
		String getUrl = authHost + "/dm/getTokenBySignature?webSignature=" + webSignature;
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				
				Map<String,Object> map = JsonUtil.jsonToMap(result);
				Integer retCode = (Integer)map.get("code");
				if (retCode == 0) {
					Map<String, Object> dataMap = (Map<String,Object>)map.get("data");
					return dataMap;
				} else {
					printMessage("request error, retCode:" + retCode + ", msg: " + map.get("msg") + ", debugInfo:" + map.get("debugInfo"));
				}
				
			} else {
				printMessage("HTTP error code:"+code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	private String sendRequest(String url, Map<String, Object> map, String userId, String token)
			throws Exception {
		HttpPost httpPost = new HttpPost(url);
		String json = JsonUtil.mapToJson(map);
		System.out.println(json);
		StringEntity se = new StringEntity(json, "UTF-8");
		httpPost.setEntity(se);
		httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
//		httpPost.addHeader(HTTP.CONTENT_ENCODING, "gzip");
		httpPost.addHeader("userId",userId);
		httpPost.addHeader("token",token);
		se.setContentType("text/json");
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
				"application/json"));
		HttpResponse response = new DefaultHttpClient().execute(httpPost);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
			printMessage(result);
			Map<String,Object> retmap = JsonUtil.jsonToMap(result);
			int retcode = (Integer)retmap.get("code");
			if (retcode == 0) {
				String data = (String)retmap.get("data");
				printMessage(data);
				return data;
			}
		} else {
			printMessage("HTTP error code:" + code);
		}
		return null;
	}
	
	
	public void updateHealthCheck(String collName) throws Exception {
		ExportDataClientFuxin fxClient = new ExportDataClientFuxin();
		HealthCheckService hcService = new HealthCheckService();
		List<Map<String, Object>> datas = getDataBetweenTwoDates(collName, "", "");
		int i = 0;
		
		List<Map<String, Object>> list = Lists.newArrayList();
		for (Map<String, Object> data : datas) {
			String disease = "";
			String tnb = "";
			Double bloodGlucose = null;
			Double bloodGlucose2h = null;
			Double bloodGlucoseRandom = null;
			Integer riskScore = null;
			
			String gxy = "";
			Integer highPressure = null;
			Integer lowPressure = null;
			
			String gxz = "";
			Double tc = null;
			Double tg = null;
			Double hdl = null;
			
			Double BMI = null;
			
			if (data.get("disease") != null && data.get("disease") != "") {
				disease = data.get("disease").toString();
				if(disease.contains("糖尿病")) {
					tnb = "糖尿病";
				} else if (disease.contains("高血压")) {
					gxy = "高血压";
				} else if (disease.contains("高血脂")) {
					gxz = "高血脂";
				}
			}
			
			if (data.get("bloodGlucose") != null && data.get("bloodGlucose") != "") {
				bloodGlucose = Double.parseDouble(data.get("bloodGlucose").toString());
			}
			
			if (data.get("bloodGlucose2h") != null && data.get("bloodGlucose2h") != "") {
				bloodGlucose2h = Double.parseDouble(data.get("bloodGlucose2h").toString());
			}
			
			if (data.get("bloodGlucoseRandom") != null && data.get("bloodGlucoseRandom") != "") {
				bloodGlucoseRandom = Double.parseDouble(data.get("bloodGlucoseRandom").toString());
			}
			
			if (data.get("riskScore") != null && data.get("riskScore") != "") {
				riskScore = Integer.parseInt(data.get("riskScore").toString());
			}
			
			if (data.get("highPressure") != null && data.get("highPressure") != "") {
				highPressure = Integer.parseInt(data.get("highPressure").toString());
			}
			
			if (data.get("lowPressure") != null && data.get("lowPressure") != "") {
				lowPressure = Integer.parseInt(data.get("lowPressure").toString());
			}
			
			if (data.get("tc") != null && data.get("tc") != "") {
				tc = Double.parseDouble(data.get("tc").toString());
			}
			
			if (data.get("tg") != null && data.get("tg") != "") {
				tg = Double.parseDouble(data.get("tg").toString());
			}
			
			if (data.get("hdl") != null && data.get("hdl") != "") {
				hdl = Double.parseDouble(data.get("hdl").toString());
			}
			
			if (data.get("BMI") != null && data.get("BMI") != "") {
				BMI = Double.parseDouble(data.get("BMI").toString());
			}
			
			Map<String, Object> bloodGlucoseCon = hcService.bloodGlucoseCon(tnb, bloodGlucose, bloodGlucose2h, bloodGlucoseRandom, riskScore);
			
			Map<String, Object> bloodPressureCon = hcService.bloodPressureCon(gxy, highPressure, lowPressure);
			
			Map<String, Object> bloodLipidCon = hcService.bloodLipidCon(gxz, tc, tg, hdl);
			
			String fatCon = hcService.getFatCon(BMI);
			
			Map<String, Object> classifyResult = hcService.classifyResult(bloodGlucoseCon, bloodPressureCon, bloodLipidCon, fatCon);
			
			//if (data.get("classifyResult") != null && data.get("classifyResult") != "" && !data.get("classifyResult").equals(classifyResult)) {
				i ++;
				/*System.out.println(i + "=========" + data.get("uniqueId") + "===========" + data.get("classifyResult") + "===========" + classifyResult
						+ "====OGTT====" + data.get("OGTTTest") + ","  + bloodGlucoseCon.get("OGTTTest")
						+ "====bloodLipidTest====" + data.get("bloodLipidTest") + ","  + bloodLipidCon.get("bloodLipidTest")
						+ "====bloodPressureTest====" + data.get("bloodPressureTest") + ","  + bloodPressureCon.get("bloodPressureTest")); */
				Map<String, Object> exportMap = Maps.newHashMap();
				exportMap.putAll(bloodGlucoseCon);
				exportMap.putAll(bloodPressureCon);
				exportMap.putAll(bloodLipidCon);
				exportMap.put("classifyResult", classifyResult);
				exportMap.put("classifyResult1", data.get("classifyResult"));
				exportMap.put("fatCon", fatCon);
				exportMap.put("uniqueId", data.get("uniqueId"));
				list.add(exportMap);
				
				data.putAll(bloodGlucoseCon);
				data.putAll(bloodPressureCon);
				data.putAll(bloodLipidCon);
				data.putAll(classifyResult);
				
				data.put("collName", collName);
				
				String url = host + "/healthcheck/updateHcData.json";
				String id = sendRequest(url, data);
				System.out.println("id:" + id);
			//}
		}
		
		exportExcel(list);
	}
	
	
	public void exportExcel(List<Map<String, Object>> list) throws IOException {
		Workbook workbook = new XSSFWorkbook();
        // 生成一个表格
        Sheet sheet = workbook.createSheet();
        /**创建标题行*/
        Row row = sheet.createRow(0);
        // 存储标题在Excel文件中的序号
        
        String[] titles = {"唯一标识", "血糖情况", "OGTT检测", "dmRisk", "dmTag", "dmRiskType", "血压情况",
        		"血压检测", "血脂情况", "血脂检测", "肥胖情况", "初筛人群结果分类", "初筛人群结果分类修改前"};
        for (int i = 0; i < titles.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles[i]);
        }
        
        for (int i = 0; i < list.size(); i++) {
        	/**设置表格内容从第行开始*/
        	Row valueRow = sheet.createRow(i+1);
        	Map<String, Object> rowMap = list.get(i);
//        	for (int j = 0; j < rowMap.size(); j++) {
        		Cell cell1 = valueRow.createCell(0);
        		if (rowMap.get("uniqueId") != "" && rowMap.get("uniqueId") != null) {
        			cell1.setCellValue(rowMap.get("uniqueId").toString());
        		}
        	   
        		Cell cell2 = valueRow.createCell(1);
        		if (rowMap.get("bloodSugarCondition") != "" && rowMap.get("bloodSugarCondition") != null) {
        			cell2.setCellValue(rowMap.get("bloodSugarCondition").toString());
        		}
        	   	
        		Cell cell3 = valueRow.createCell(2);
        		if (rowMap.get("OGTTTest") != "" && rowMap.get("OGTTTest") != null) {
        			cell3.setCellValue(rowMap.get("OGTTTest").toString());
        		}
        		
        		Cell cell4 = valueRow.createCell(3);
        		if (rowMap.get("dmRisk") != "" && rowMap.get("dmRisk") != null) {
        			cell4.setCellValue(rowMap.get("dmRisk").toString());
        		}
        	   	
        		Cell cell5 = valueRow.createCell(4);
        		if (rowMap.get("dmTag") != "" && rowMap.get("dmTag") != null) {
        			cell5.setCellValue(rowMap.get("dmTag").toString());
        		}
        	   	
        		Cell cell6 = valueRow.createCell(5);
        		if (rowMap.get("dmRiskType") != "" && rowMap.get("dmRiskType") != null) {
        			cell6.setCellValue(rowMap.get("dmRiskType").toString());
        		}
        	   
        		Cell cell7 = valueRow.createCell(6);
        		if (rowMap.get("bloodPressureCondition") != "" && rowMap.get("bloodPressureCondition") != null) {
        			cell7.setCellValue(rowMap.get("bloodPressureCondition").toString());
        		}
        	   
        		Cell cell8 = valueRow.createCell(7);
        		if (rowMap.get("bloodPressureTest") != "" && rowMap.get("bloodPressureTest") != null) {
        			cell8.setCellValue(rowMap.get("bloodPressureTest").toString());
        		}
        	   	
        		Cell cell9 = valueRow.createCell(8);
        		if (rowMap.get("bloodLipidCondition") != "" && rowMap.get("bloodLipidCondition") != null) {
        			cell9.setCellValue(rowMap.get("bloodLipidCondition").toString());
        		}
        	   	
        		Cell cell10 = valueRow.createCell(9);
        		if (rowMap.get("bloodLipidTest") != "" && rowMap.get("bloodLipidTest") != null) {
        			cell10.setCellValue(rowMap.get("bloodLipidTest").toString());
        		}
        		
        		Cell cell11 = valueRow.createCell(10);
        		if (rowMap.get("fatCon") != "" && rowMap.get("fatCon") != null) {
        			cell11.setCellValue(rowMap.get("fatCon").toString());
        		}
        	   	
        		Cell cell12 = valueRow.createCell(11);
        		if (rowMap.get("classifyResult") != "" && rowMap.get("classifyResult") != null) {
        			cell12.setCellValue(rowMap.get("classifyResult").toString());
        		}
        		
        		Cell cell13 = valueRow.createCell(12);
        		if (rowMap.get("classifyResult1") != "" && rowMap.get("classifyResult1") != null) {
        			cell13.setCellValue(rowMap.get("classifyResult1").toString());
        		}
			}
	        File file = new File("e:/人群分类结果20190923.xlsx");
	        OutputStream outputStream = new FileOutputStream(file);
	        
	        workbook.write(outputStream);
	        outputStream.close();
	        workbook.close();
	        System.out.println("修改结束====================");
		}
	
	
	/**  
	 * <p>Title: getDataBetweenTwoDates</p>  
	 * <p>Description: 获取两个日期之间的数据</p>  
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 * @throws Exception  
	 */  
	public List<Map<String, Object>> getDataBetweenTwoDates(String collName, String startTime, String endTime) throws Exception {
		String url = host + "/healthcheck/getCustomerData.json";
//		CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("startTime", startTime));
		params.add(new BasicNameValuePair("endTime", endTime));
		params.add(new BasicNameValuePair("collName", collName));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		httpPost.setEntity(entity);
		HttpResponse response = HttpUtils.getClient().execute(httpPost);
		int code = response.getStatusLine().getStatusCode();
		if(code == 200){
			String result = EntityUtils.toString(response.getEntity());
			List<Map<String, Object>> list = JsonUtil.jsonToList(result);
			//System.out.println("list:" + list);
			System.out.println("共检索到 " + ( null!=list ? list.size() :0) + "条数据");
			return list;
		}else{
			System.out.println("code:"+code);
		}
		return null;
	}
       
}
