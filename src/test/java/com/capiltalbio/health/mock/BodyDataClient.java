package com.capiltalbio.health.mock;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.capitalbio.common.util.JsonUtil;

public class BodyDataClient {
	public String mockBodyData(String host, String[] args) throws Exception {
		String url = "";
		if (host.endsWith("/")) {
			url = host+"bodyData/uploadData.json";
		} else {
			url = host+"/bodyData/uploadData.json";
		}
		if (args.length < 12) {
			return "命令格式：java -jar runmock.jar host bodyData idCode measureTime height weight temperature waistline hipline oxygen bmp highPressure lowPressure";
		}
		String idCode = processValue(args[2]);
		String measureTime = processValue(args[3]);
		String height = processValue(args[4]);
		String weight = processValue(args[5]);
		String temperature = processValue(args[6]);
		String waistline = processValue(args[7]);
		String hipline = processValue(args[8]);
		String oxygen = processValue(args[9]);
		String bpm = processValue(args[10]);
		String highPressure = processValue(args[11]);
		String lowPressure = processValue(args[12]);
		
		Map<String,Object> bodyDataMap = new HashMap<String,Object>();
		Map<String, Object> memberMap = new HashMap<String,Object>();
		memberMap.put("IdCode", idCode);
		bodyDataMap.put("Member", memberMap);
		bodyDataMap.put("MeasureTime", measureTime);
		Map<String,Object> heightMap = new HashMap<String,Object>();
		heightMap.put("Height", height);
		heightMap.put("Weight", weight);
		bodyDataMap.put("Height", heightMap);
		Map<String,Object> temperatureMap = new HashMap<String,Object>();
		temperatureMap.put("Temperature", temperature);
		bodyDataMap.put("Temperature", temperatureMap);
		Map<String,Object> whrMap = new HashMap<String,Object>();
		whrMap.put("Waistline", waistline);
		whrMap.put("Hipline", hipline);
		bodyDataMap.put("Whr", whrMap);
		Map<String,Object> boMap = new HashMap<String,Object>();
		boMap.put("Oxygen", oxygen);
		boMap.put("Bpm", bpm);
		bodyDataMap.put("Bo", boMap);
		
		Map<String,Object> bloodPressureMap = new HashMap<String,Object>();
		bloodPressureMap.put("HighPressure", highPressure);
		bloodPressureMap.put("LowPressure", lowPressure);
		bodyDataMap.put("BloodPressure", bloodPressureMap);
		
		return sendRequest(url, bodyDataMap);
	}
	
	private String processValue(String value) {
		if (value == null) {
			return "";
		}
		if (value.equals("null")) {
			return null;
		}
		if (value.equals("blank")) {
			return "";
		}
		return value;
	}
	private String sendRequest(String url, Map<String, Object> map)
			throws Exception {
		HttpPost httpPost = new HttpPost(url);
		String json = JsonUtil.mapToJson(map);
		System.out.println(json);
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
			return "返回结果："+result;
		} else {
			System.out.println("HTTP error code:" + code);
			return "访问结果：HTTP ERROR "+code;
		}
	}

}
