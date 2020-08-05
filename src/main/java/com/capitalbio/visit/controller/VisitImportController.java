/**
 * 
 */
package com.capitalbio.visit.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.auth.util.HttpUtils;
import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.healthcheck.dao.CustomerDAO;
import com.google.common.collect.Maps;

/**
 * @author wdong
 *
 */
@Controller
@RequestMapping("/visitImport")
public class VisitImportController {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired 
	AuthService authService;
	
	@Autowired CustomerDAO customerDao;

	/**
	 * 导入用户数据
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="customerVisit")
	@ResponseBody
	@ControllerLog
	public Message customerVisit(@RequestBody Map<String,Object> customerIds, HttpServletResponse response) {
		long startTime = System.currentTimeMillis();
		Map<String, Object> datas = authService.applyToken();
		String token = (String) datas.get("token");
		String userId = (String)datas.get("userId");
		
		try {
		    int rows = 0;
		    for (String key : customerIds.keySet()) {
		        String customerId = (String)customerIds.get(key);
		    	Thread.sleep(100);
		    	Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId);
				if (secretMap == null) {
					logger.info("====customerId:"+customerId+"== secretMap is null");
					continue;
				}
				String uniqueId = (String)secretMap.get("uniqueId");
				if (StringUtils.isEmpty(uniqueId)) {
					logger.info("====customerId:"+customerId+"== uniqueid is null");
					continue;
				}
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("uniqueId", uniqueId);
				Map<String, Object> customer = customerDao.getDataByQuery("customer", params);
				if (customer == null) {
					logger.info("====customerId:"+customerId+"== customer is null");
					continue;
				}
				customer.put("visit", "是");
				customerDao.saveData("customer", customer);
				
				List<Map<String, Object>> hcs = customerDao.queryList("healthcheck", params);
				if (hcs != null && hcs.size() > 0) {
					for (Map<String, Object> hc : hcs) {
						hc.put("visit", "是");
						customerDao.saveData("healthcheck", hc);
					}
				}
				logger.info("====customerId:"+customerId+"== 更新成功！");
				rows ++;
		    }
		    
		    long endTime = System.currentTimeMillis();
		    System.out.println("导入成功,共导入："+rows+",耗时："+(endTime-startTime)/1000);
		    logger.info("导入成功,共导入："+rows+",耗时："+(endTime-startTime)/1000);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return Message.success();
	}
	
	public static String errorLineStr() {
		String errorFilePath = "D:\\project\\screen\\screen\\src\\main\\webapp\\static\\error.js";
		
		StringBuffer lineStr = new StringBuffer();
		File file = new File(errorFilePath);
		BufferedReader reader = null;
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "utf-8");
			reader = new BufferedReader(read);
			String tempStr = "";
			StringBuffer tempSbuf = new StringBuffer(",");
            while ((tempStr = reader.readLine()) != null) {
                tempSbuf.append(tempStr);
            }
            
            if (tempSbuf.length() > 0) {
            	tempStr = tempSbuf.toString();
            	
            	String[] arrStr = tempStr.split(",");
            	for (String str : arrStr) {
            		String[] arrLine = str.split("行");
            		String lineStrin = arrLine[0];
            		if (lineStrin != null)
            			lineStr.append(lineStrin.trim()).append(",");
            	}
            }
            
            reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lineStr.toString();
	}
	
	private String getRootPath(HttpServletRequest request) {
		return request.getSession().getServletContext().getRealPath("/")+"/static/";
	}
	
	public static Map<String, Object> parseExcel() {
		String errorLine = errorLineStr();
		BufferedInputStream in = null;
		XSSFWorkbook wb = null;
		Map<String, Object> customers = new HashMap<String, Object>();
		String filePath = "D:\\project\\screen\\screen\\src\\main\\webapp\\static\\customer.xlsx";
		try {
			in = new BufferedInputStream(new FileInputStream(new File(filePath)));

		    wb = new XSSFWorkbook(in);
		    XSSFSheet st = wb.getSheetAt(0);
		    for (int rowIndex = 4; rowIndex <= st.getLastRowNum(); rowIndex++) {
		    	XSSFRow row = st.getRow(rowIndex);
		    	XSSFCell cell = row.getCell(1);
		    	if (cell == null ) continue;
		    	if (errorLine.indexOf(","+(rowIndex+1)+",") >= 0) {
		    		System.out.println((rowIndex+1)+"格式错误");
		    		continue;
		    	}
		    	
		    	String customerId = cell.getStringCellValue();
		    	customers.put(customerId, customerId);
		    }
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (wb != null) wb.close();
				if (in != null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return customers;
	}
	
	public static Map<String,Object> uploadCustomerData(Map<String,Object> map) {
		
		String postUrl = "http://localhost:8080/screen/visitImport/customerVisit.htm";
		try {
			HttpPost httpPost = new HttpPost(postUrl);
			String json = JsonUtil.mapToJson(map);
			StringEntity se = new StringEntity(json, "UTF-8");
			httpPost.setEntity(se);
			httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			
			HttpResponse response = HttpUtils.getClient().execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			Map<String,Object> returnMap = Maps.newHashMap();
					
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				Map<String,Object> retmap = JsonUtil.jsonToMap(result);
				return retmap;
			} else {
				returnMap.put("msg", "更新数据异常");
				return returnMap;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,Object> returnMap = Maps.newHashMap();
		returnMap.put("msg", "网络错误");
		return returnMap;
	}

	public static void main(String[] args) {
		Map<String, Object> customerIds = parseExcel();
		uploadCustomerData(customerIds);
	}
}
