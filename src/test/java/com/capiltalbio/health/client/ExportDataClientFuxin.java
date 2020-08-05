package com.capiltalbio.health.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capiltalbio.health.utils.HttpsUtil;
import com.capitalbio.auth.util.Constant;
import com.capitalbio.auth.util.HttpUtils;
import com.capitalbio.auth.util.JwtUtil;
import com.capitalbio.common.util.DateUtil;
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
public class ExportDataClientFuxin {
	private final String fuxinScreeningStartTime = "2019-03-28";
	private Logger logger = LoggerFactory.getLogger(getClass()); 
//	String fuxinScreening = "http://114.116.74.14:8080/screening";
	/** funxinUnifiedCertificationSystem
	 * 阜新统一身份认证系统*/  
	String  fuxinUnifiedCertificationSystem = "https://fxdm.bioeh.com/chs_fx/rest";
	String  fuxinScreening = "https://fxdm.bioeh.com/screening";
//	String  fuxinScreening = "http://localhost:8080/screen";
	static String outputFilePath="E:/temp/fuxin/";
	
	/** fileExpandedName
	 * 文件扩展名：.xlsx*/  
	static String fileExpandedName = ".xlsx";
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		String fuxinStartTime = "2020-04-20";
		String endTime = "2020-04-20";
		outputFilePath += fuxinStartTime; 
		
//		String dateToString = DateUtil.dateToString(new Date(System.currentTimeMillis() - 1000*60*60*24));
//		String fuxinStartTime = dateToString;
//		String endTime = dateToString;
//		outputFilePath += fuxinStartTime; 
		
//		String fuxinStartTime = "2020-01-19";//2019-03-28  2019-05-20
//		String endTime = "2020-04-25";
//		outputFilePath += fuxinStartTime + "至" + endTime ; 
		
		ExportDataClientFuxin ec = new ExportDataClientFuxin();
		ec.exportCustomer(fuxinStartTime, endTime);
//		System.out.println("筛查数据："+outputFilePath);
		long end = System.currentTimeMillis();
		System.out.println("耗时：" + (end-start)/1000+"秒");
	}
	
	/**
	 * 
	 * @param startTime：开始日期 阜新地区的开始筛查时间为2019-03-28
	 * @param endTime：截止日期
	 * @throws Exception
	 */
	public void exportCustomer(String startTime, String endTime) throws Exception{
		/**筛查地点：阜新市*/
		List<Map<String, Object>> fuxinshiListMap = Lists.newArrayList();
		/**筛查地点：阜蒙县*/
		List<Map<String, Object>> fumengxianListMap = Lists.newArrayList();
		/**筛查地点：彰武县*/
		List<Map<String, Object>> zhangwuxianListMap = Lists.newArrayList();
		/**筛查地点：海州区*/
		List<Map<String, Object>> haizhouquListMap = Lists.newArrayList();
		/**筛查地点：空*/
		List<Map<String, Object>> emptyListMap = Lists.newArrayList();
		
		String fuxinshiScreenDataFilename = outputFilePath + "阜新市" + fileExpandedName; 
		String fuxinshiHaizhouquScreenDataFilename = outputFilePath + "阜新市海州区" + fileExpandedName; 
		String fuxinshiFumengxianScreenDataFilename = outputFilePath + "阜新市阜蒙县" + fileExpandedName; 
		String fuxinshiZhangwuxianScreenDataFilename = outputFilePath + "阜新市彰武县" + fileExpandedName; 
		String fuxinshiEmptyDistrictScreenDataFilename = outputFilePath + "阜新市区域为空" + fileExpandedName; 
		
		long start = System.currentTimeMillis();
		List<Map<String, Object>> screenDataList = getDataBetweenTwoDates(startTime, endTime);
		long end = System.currentTimeMillis();
		System.out.println("筛查端数据检索耗时：" + (end-start)/1000 + "秒");
        Map<String, Object> tokenMap = applyTokenByChsFx();
//        for (int r = 0; r < screenDataList.size(); r ++) {
        for (int r = 0; r < screenDataList.size(); r ++) {
        	System.out.println("r=============:" + r);
        	Thread.sleep(100);
        	 Map<String, Object> data = screenDataList.get(r);
        	 if (data != null) {
        		 if (data.get("uniqueId") != null) {
        			 
        			 if (tokenMap == null) {
        				 tokenMap = applyTokenByChsFx();
        			 }
        			 if(null!=data.get("uniqueId") && null!=tokenMap.get("token") && null!=tokenMap.get("userId")) {
        				 Map<String,Object> secretMap = getUserinfoByUniqueId(data.get("uniqueId").toString(), tokenMap.get("token").toString(), tokenMap.get("userId").toString());
        				 if (secretMap != null) {
        					 data.putAll(secretMap);
        				 }
        				 
        				 if(null==data.get("district") || data.get("district") == "") {
        					 Map<String, Object> customer = getCustomer(data.get("uniqueId").toString());
        					 if (customer != null) {
        						 data.put("district", customer.get("district"));
        					 }
        				 }
        				 
        				 if(null!=data.get("district") && !"".equals(data.get("district").toString())) {
        					 if("海州区".equals(data.get("district").toString())) {
        						 haizhouquListMap.add(data);
        					 }else if("阜蒙县".equals(data.get("district").toString())) {
        						 fumengxianListMap.add(data);
        					 }else if("彰武县".equals(data.get("district").toString())) {
        						 zhangwuxianListMap.add(data);
        					 }
        				 }else {
        					 emptyListMap.add(data);
        				 }
        				 fuxinshiListMap.add(data);
        			 }
        		 }
        	 }
        }

        if(fuxinshiListMap.size()>0) {
        	exportExcelByMap(fuxinshiListMap, fuxinshiScreenDataFilename);
        	System.out.println("[筛查数据]：" + fuxinshiScreenDataFilename);
        }
        if(haizhouquListMap.size()>0) {
        	exportExcelByMap(haizhouquListMap, fuxinshiHaizhouquScreenDataFilename);
        	System.out.println("[筛查数据]：" + fuxinshiHaizhouquScreenDataFilename);
        	
        }
		if(fumengxianListMap.size()>0) {
			exportExcelByMap(fumengxianListMap, fuxinshiFumengxianScreenDataFilename);
			System.out.println("[筛查数据]：" + fuxinshiFumengxianScreenDataFilename);
		}
		
		if(zhangwuxianListMap.size()>0) {
			exportExcelByMap(zhangwuxianListMap, fuxinshiZhangwuxianScreenDataFilename);
			System.out.println("[筛查数据]：" + fuxinshiZhangwuxianScreenDataFilename);
		}
		if(emptyListMap.size()>0) {
			exportExcelByMap(emptyListMap, fuxinshiEmptyDistrictScreenDataFilename);
			System.out.println("[筛查数据]：" + fuxinshiEmptyDistrictScreenDataFilename);
		}
	}
	
	/**  
	 * <p>Title: getDataBetweenTwoDates</p>  
	 * <p>Description: 获取两个日期之间的数据</p>  
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 * @throws Exception  
	 */  
	public List<Map<String, Object>> getDataBetweenTwoDates(String startTime, String endTime) throws Exception {
		String url = fuxinScreening + "/healthcheck/getCustomerData.json";
//		CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("startTime", startTime));
		params.add(new BasicNameValuePair("endTime", endTime));
		params.add(new BasicNameValuePair("collName", "healthcheck"));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		httpPost.setEntity(entity);
		HttpResponse response = HttpUtils.getClient().execute(httpPost);
//		HttpResponse response = HttpsUtil.httpsPost(httpPost);
		int code = response.getStatusLine().getStatusCode();
		if(code == 200){
			String result = EntityUtils.toString(response.getEntity());
			List<Map<String, Object>> list = JsonUtil.jsonToList(result);
			//System.out.println("list:" + list);
			System.out.println("共检索到 " + ( null!=list ? list.size() :0) + "条数据");
			return list;
		}else{
			System.out.println("code11:"+code);
		}
		return null;
	}
	
	/**
	 * 获取筛查人员信息
	 * @param uniqueId 唯一标识
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getCustomer(String uniqueId) throws Exception {
		String url = fuxinScreening + "/healthcheck/getCustomer.json";
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uniqueId", uniqueId));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		httpPost.setEntity(entity);
//		HttpResponse response = HttpUtils.getClient().execute(httpPost);
		HttpResponse response = HttpsUtil.httpsPost(httpPost);
		int code = response.getStatusLine().getStatusCode();
		if(code == 200){
			String result = EntityUtils.toString(response.getEntity());
			if (StringUtils.isNotEmpty(result)) {
				Map<String, Object> map = JsonUtil.jsonToMap(result);
				System.out.println("map:" + map);
				return map;
			}
			
		}else{
			System.out.println("code:"+code);
		}
		return null;
	}
	
	/**  
	 * <p>Title: applyTokenByChs_fx</p>  
	 * <p>Description: 获取网络签名-认证系统</p>  
	 * @return
	 * @throws Exception  
	 * @author guohuiyang  
	 * Create Time: 2019-04-19 11:02:36<br/>  
	 */  
	@SuppressWarnings("unchecked")
	private Map<String, Object> applyTokenByChsFx() throws Exception {
		String webSignature = JwtUtil.createJWT(Constant.JWT_ID, "healthcheck", Constant.JWT_TTL);
		String getUrl = fuxinUnifiedCertificationSystem + "/dm/getTokenBySignature?webSignature=" + webSignature;
		
		try {
			HttpGet httpGet = new HttpGet(getUrl);
//			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			HttpResponse response = HttpsUtil.httpsGet(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				System.out.println("[申请网络签名] "+result);
				
				Map<String,Object> map = JsonUtil.jsonToMap(result);
				Integer retCode = (Integer)map.get("code");
				if (retCode == 0) {
					Map<String, Object> dataMap = (Map<String,Object>)map.get("data");
					return dataMap;
				} else {
					logger.debug("request error, retCode:" + retCode + ", msg: " + map.get("msg") + ", debugInfo:" + map.get("debugInfo"));
				}
				
			} else {
				logger.debug("HTTP error code:"+code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getUserinfoByUniqueId(String uniqueId, String token, String userId) throws Exception {
		String getUrl = fuxinUnifiedCertificationSystem + "/wx/getUserInfo?userId=" + uniqueId;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			httpGet.addHeader("token", token);
			httpGet.addHeader("userId", userId);
//			CloseableHttpResponse response = httpclient.execute(httpGet);
			HttpResponse response = HttpsUtil.httpsGet(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				try {
					String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
					logger.debug("requestInfoByUniqueId--result=="+result);
					Map<String,Object> map = JsonUtil.jsonToMap(result);
					Integer retCode = (Integer)map.get("code");
					Map<String, Object> dataMap = (Map<String,Object>)map.get("data");
					if (retCode == 0 && dataMap != null) {
						dataMap.put("customerId", dataMap.get("id"));
						dataMap.put("mobile", dataMap.get("mobile"));
						dataMap.put("name", dataMap.get("name"));
						dataMap.put("gender", dataMap.get("gender"));
						dataMap.put("age", dataMap.get("age"));
						dataMap.put("birthday", dataMap.get("birthday"));
						dataMap.put("nationality", dataMap.get("nationality"));
						dataMap.put("householdRegistrationType", dataMap.get("householdRegistrationType"));
						dataMap.put("contactName", dataMap.get("contactName"));
						dataMap.put("contactMobile", dataMap.get("contactMobile"));
						dataMap.put("address", dataMap.get("address"));
						dataMap.put("remarks", dataMap.get("remarks"));
					}
					return dataMap;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**  
	 * <p>Title: getTitles</p>  
	 * <p>Description: 获取表头</p>  
	 * @return  
	 * @author guohuiyang  
	 * Create Time: 2019-06-19 10:06:25<br/>  
	 */  
	private List<String> getTitles() {
		List<String> titles = Lists.newArrayList();
//		titles.add("id");
		titles.add("编号");
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
		titles.add("血清低密度脂蛋白（mmol/L）");
		titles.add("血清高密度脂蛋白（mmol/L）");
		titles.add("中医体质辨识结果");
		titles.add("眼象信息");
		titles.add("糖尿病危险因素评估分数");
		titles.add("血糖情况");
		titles.add("血脂情况");
		titles.add("血压情况");
		titles.add("人群分类结果");
		titles.add("血糖异常人群细分");
		titles.add("检测时间");
		titles.add("OGTT检测");
		titles.add("血脂检测");
		titles.add("血压检测");
		titles.add("基因检测");
		titles.add("筛查地点");
		return titles;
	}
	
//	public void setCellValue(int i, String name, Row row, Map<String, Object> rowMap) {
//		Cell cell = row.createCell(i);
//	   	if (rowMap.get(name) != null) {
//	   		cell.setCellValue(rowMap.get(name).toString());
//	   	}
//	}
	
	public void exportExcelByMap(List<Map<String, Object>> listMap, String filename) throws Exception{
		List<String> titles = getTitles();
		
		Workbook workbook = new XSSFWorkbook();
        // 生成一个表格
        Sheet sheet = workbook.createSheet();
        /**创建标题行*/
        Row row = sheet.createRow(0);
        // 存储标题在Excel文件中的序号
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
        }
        String[] names = {/*"ids",*/"uniqueId","customerId", "mobile", "name", "gender", "age", "birthday", "nationality", "householdRegistrationType", 
        		"contactName", "contactMobile", "address", "remarks", "disease", "familyHistory", "height", "weight", "BMI", "waistline", "highPressure", 
        		"lowPressure", "pulse", "temperature", "oxygen", "hipline", "WHR", "fatContent", "bloodGlucose", "bloodGlucose2h",
        		"bloodGlucoseRandom", "tc", "tg", "ldl", "hdl", "tizhi", "eyeCheck", "riskScore", "bloodSugarCondition",
        		"bloodLipidCondition", "bloodPressureCondition", "classifyResult","dmTag", "checkDate", "OGTTTest", "bloodLipidTest", 
        		"bloodPressureTest", "geneTest","district"};
        
        for (int i = 0; i < listMap.size(); i++) {
        	/**设置表格内容从第行开始*/
        	Row valueRow = sheet.createRow(i+1);
        	Map<String, Object> rowMap = listMap.get(i);
        	for (int j = 0; j < names.length; j++) {
        		String rowName = names[j];
        		Cell cell = valueRow.createCell(j);
        	   	if (rowMap.get(rowName) != null) {
        	   		cell.setCellValue(rowMap.get(rowName).toString());
        	   	}
			}
			
		}
        File file = new File(filename);
        OutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
	}
	
	// 导入15W条数据测试
	/*public void importDataTest() throws IOException {
		File newFile = new File("C:/Users/xiaoyanzhang/Desktop/2019-03-28至2019-06-18-阜新市.xlsx");
    	
    	FileInputStream input = new FileInputStream(newFile);
    	XSSFWorkbook wb = new XSSFWorkbook(input);
		XSSFSheet sheet = wb.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();
		
		String[] names = {"uniqueId","customerId", "mobile", "name", "gender", "age", "birthday", "nationality", "householdRegistrationType", 
	        		"contactName", "contactMobile", "address", "remarks", "disease", "familyHistory", "height", "weight", "BMI", "waistline", "highPressure", 
	        		"lowPressure", "pulse", "temperature", "oxygen", "hipline", "WHR", "fatContent", "bloodGlucose", "bloodGlucose2h",
	        		"bloodGlucoseRandom", "tc", "tg", "ldl", "hdl", "tizhi", "eyeCheck", "riskScore", "bloodSugarCondition",
	        		"bloodLipidCondition", "bloodPressureCondition", "classifyResult","dmTag", "checkDate", "OGTTTest", "bloodLipidTest", 
	        		"bloodPressureTest", "geneTest","district"};
		 
		for (int i = 1; i < rows; i++) {
			Map<String, Object> obj = Maps.newHashMap();
			XSSFRow row = sheet.getRow(i);
			
			obj.put("", value)
			//姓名
			String name = getCellValue(row.getCell(0));
			obj.put("", value)
		}
	}*/
	

	private String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		}
		return cell.getStringCellValue();
	}
}
