package com.capiltalbio.health.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.capitalbio.common.util.JsonUtil;
import com.google.common.collect.Lists;

/**  
 * ClassName: ExportDataClient <br/>  
 * @author guohuiyang@ccapitalbio.com 杨国辉  
 * @version 1.0 
 * Create Time: 2019-01-23 10:15:20<br/>  
 * Description: TODO 按照开始时间和结束时间导出阜新（海州区和阜蒙县）地区的初筛数据
 *  导出一个人的最新一条数据，数据文件存储在Mongodb数据库表的customer表中。
 *  MongoDB数据库表结构由，董伟设计。 
 *  customer：被筛查人员最新的一条数据
 *  healthcheck：被筛查人员每天最新的一条数据
 *  healthCheckHistory：被筛查人员的所有历史数据
 */ 
public class ExportDataClient {
	String host = "http://114.116.74.14:8080/screening/";
	
	public static void main(String[] args) throws Exception {
		ExportDataClient ec = new ExportDataClient();
		ec.exportCustomer("2018-12-15", "2018-12-29");
	}
	
	/**
	 * 
	 * @param startTime：开始日期
	 * @param endTime：截止日期
	 * @throws Exception
	 */
	public void exportCustomer(String startTime, String endTime) throws Exception{
		List<Map<String, Object>> datas = getData(startTime, endTime);
		System.out.println("datas:" + datas.size());
		List<String> titles = getTitles();
		
		Workbook workbook = new XSSFWorkbook();
        // 生成一个表格
        Sheet sheet = workbook.createSheet();
        
        // 设置表格默认列宽度为15个字节
       // sheet.setDefaultColumnWidth((short) 15);
        /*
         * 创建标题行
         */
        Row row = sheet.createRow(0);
        // 存储标题在Excel文件中的序号
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
        }
        
        String[] names = {"uniqueId","customerId", "mobile", "name", "gender", "age", "birthday", "nationality", "householdRegistrationType", 
        		"contactName", "contactMobile", "address", "remarks", "disease", "familyHistory", "height", "weight", "BMI", "waistline", "highPressure", 
        		"lowPressure", "pulse", "temperature", "oxygen", "hipline", "WHR", "fatContent", "bloodGlucose", "bloodGlucose2h",
        		"bloodGlucoseRandom", "tc", "tg", "ldl", "hdl", "tizhi", "eyeCheck", "riskScore", "bloodSugarCondition",
        		"bloodLipidCondition", "bloodPressureCondition", "classifyResult", "checkDate", "OGTTTest", "bloodLipidTest", 
        		"bloodPressureTest", "geneTest"};
        Map<String, Object> tokenMap = applyToken();
        for (int r = 0; r < datas.size(); r ++) {
        	 Row valueRow = sheet.createRow(r+1);
        	 Map<String, Object> data = datas.get(r);
        	 
        	 if (data != null) {
        		 if (data.get("uniqueId") != null) {
        			 
        			 if (tokenMap == null) {
        				 tokenMap = applyToken();
        			 }
        			 Map<String,Object> secretMap = getUserinfoByUniqueId(data.get("uniqueId").toString(), tokenMap.get("token").toString(), tokenMap.get("userId").toString());
        			 if (secretMap != null) {
        				 data.putAll(secretMap);
        			 }
        			 
        			 for (int j = 0; j < names.length; j++) {
                		 setCellValue(j, names[j], valueRow, data);
                	 }
        		 }
        	 }
        }
        /*
         * 写入到文件中
         */
        
       /* String tempDir = PropertyUtils.getProperty("system.temp.dir");
		File dir = new File(tempDir, "data");
	    if(!dir.exists()){
	    	dir.mkdirs();
	    }
	    
		String uuid =  UUID.randomUUID().toString();
		File newFile = new File(dir, uuid + ".xlsx");
        File file = new File(newFile.getAbsolutePath());*/
        File file = new File("C:/Users/xiaoyanzhang/Desktop/20190117.xlsx");
        OutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getData(String startTime, String endTime) throws Exception {
		String url = host + "/healthcheck/getCustomerData.json";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("startTime", startTime));
		params.add(new BasicNameValuePair("endTime", endTime));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		httpPost.setEntity(entity);
		HttpResponse response = httpClient.execute(httpPost);
		int code = response.getStatusLine().getStatusCode();
		if(code == 200){
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("result:"+result);
			List<Map<String, Object>> list = JsonUtil.jsonToList(result);
			System.out.println(list);
			return list;
		}else{
			System.out.println("code:"+code);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> applyToken() throws Exception {
		String url = host + "/auth/applyToken.json";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		httpPost.setEntity(entity);
		HttpResponse response = httpClient.execute(httpPost);
		int code = response.getStatusLine().getStatusCode();
		if(code == 200){
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("applyToken==result:"+result);
			Map<String, Object> map = JsonUtil.jsonToMap(result);
			System.out.println(map);
			return map;
		}else{
			System.out.println("applyToken==code:"+code);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getUserinfoByUniqueId(String uniqueId, String token, String userId) throws Exception {
		String url = host + "/auth/getUserinfoByUniqueId.json";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uniqueId", uniqueId));
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("userId", userId));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		httpPost.setEntity(entity);
		HttpResponse response = httpClient.execute(httpPost);
		int code = response.getStatusLine().getStatusCode();
		if(code == 200){
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("getUserinfoByUniqueId==result:"+result);
			if (StringUtils.isNotEmpty(result)) {
				Map<String, Object> map = JsonUtil.jsonToMap(result);
				System.out.println(map);
				return map;
			}
			
		}else{
			System.out.println("getUserinfoByUniqueId==code:"+code);
		}
		return null;
	}
	
	public List<String> getTitles() {
		List<String> titles = Lists.newArrayList();
		titles.add("UniqueId");
		titles.add("身份证号");
		titles.add("移动电话");
		titles.add("姓名");
		titles.add("性别");
		titles.add("年龄");
		titles.add("出生日期");
		titles.add("民族");
		titles.add("常住类型");
		titles.add("联系人姓名");
		titles.add("联系人电话");
		titles.add("地址");
		titles.add("备注信息");
		titles.add("已患有哪种疾病");
		titles.add("糖尿病家族史");
		titles.add("身高cm");
		titles.add("体重kg");
		titles.add("体质指数（BMI）");
		titles.add("腰围cm");
		titles.add("收缩压mmHg");
		titles.add("舒张压mmHg");
		titles.add("脉率次/分钟");
		titles.add("体温℃");
		titles.add("血氧（%）");
		titles.add("臀围（cm）");
		titles.add("腰臀比");
		titles.add("体脂率%");
		titles.add("空腹血糖（mmol/L）");
		titles.add("餐后2h血糖（mmol/L）");
		titles.add("随机血糖（mmol/L）");
		titles.add("总胆固醇（mmol/L）");
		titles.add("甘油三酯（mmol/L）");
		titles.add("低密度脂蛋白（mmol/L）");
		titles.add("高密度脂蛋白（mmol/L）");
		titles.add("中医体质辨识结果");
		titles.add("眼象信息");
		titles.add("糖尿病危险因素评估分数");
		titles.add("血糖情况");
		titles.add("血脂情况");
		titles.add("血压情况");
		titles.add("人群分类结果");
		titles.add("检测时间");
		titles.add("OGTT检测");
		titles.add("血脂检测");
		titles.add("血压检测");
		titles.add("基因检测");
		return titles;
	}
	
	public void setCellValue(int i, String name, Row row, Map<String, Object> rowMap) {
		Cell cell = row.createCell(i);
	   	if (rowMap.get(name) != null) {
	   		cell.setCellValue(rowMap.get(name).toString());
	   	}
	}

}
