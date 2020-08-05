package com.capiltalbio.health.client;

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
import com.google.common.collect.Maps;

public class TaskClient {

	
	public static void main(String[] args) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		map.put("checkDate", "2020-03-12");
		map.put("uniqueId", "1000");
		sendRequest("http://localhost:8080/screen/healthcheck/testTask.htm", map);
	}
	
	
	private static String sendRequest(String url, Map<String, Object> map)
			throws Exception {
		HttpPost httpPost = new HttpPost(url);
		String json = JsonUtil.mapToJson(map);
		System.out.println(json);
		StringEntity se = new StringEntity(json, "UTF-8");
		httpPost.setEntity(se);
		httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
		se.setContentType("text/json");
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
				"application/json"));
		HttpResponse response = new DefaultHttpClient().execute(httpPost);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
			Map<String,Object> retmap = JsonUtil.jsonToMap(result);
			int retcode = (Integer)retmap.get("code");
			if (retcode == 0) {
				String data = (String)retmap.get("data");
				return data;
			}
		}
		return null;
	}
}
