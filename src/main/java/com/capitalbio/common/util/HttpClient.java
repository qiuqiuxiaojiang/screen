package com.capitalbio.common.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

public class HttpClient {
	
	private static Log logger = LogFactory.getLog(HttpClient.class);

	public static JSONObject  post(String url,Map<String, Object> map,String token,String userId){
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("token", token);
		httpPost.setHeader("userId", userId);
		
		String json = JsonUtil.mapToJson(map);
		System.out.println(json);
		StringEntity entity = new StringEntity(json, "UTF-8");
		
		httpPost.setEntity(entity);
		httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
		try {
			HttpResponse response = new DefaultHttpClient().execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity());
				logger.info("post===" + result);
				return JSONObject.parseObject(result);
			}
		} catch (Exception e) {
			logger.error("post==",e);
		} 
		return null;
	}
	
	public static JSONObject  post(String url,Map<String, Object> map){
		HttpPost httpPost = new HttpPost(url);
		
		String json = JsonUtil.mapToJson(map);
		System.out.println(json);
		StringEntity entity = new StringEntity(json, "UTF-8");
		
		httpPost.setEntity(entity);
		httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
		try {
			HttpResponse response = new DefaultHttpClient().execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity());
				logger.info("post===" + result);
				return JSONObject.parseObject(result);
			}
		} catch (Exception e) {
			logger.error("post==",e);
		} 
		return null;
	}
	
	/**
	 * url 参数自己拼装
	 * @param url
	 * @param token
	 * @param userId
	 * @return
	 */
	public static JSONObject  get(String url,String token,String userId){
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("token", token);
		httpGet.setHeader("userId", userId);
		
		try {
			HttpResponse response = new DefaultHttpClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity());
				logger.info("get===" + result);
				return JSONObject.parseObject(result);
			}
		} catch (Exception e) {
			logger.error("get==",e);
		} 
		return null;
	}
	
	/**
	 * url 参数自己拼装
	 * @param url
	 * @param token
	 * @param userId
	 * @return
	 */
	public static JSONObject get1(String url, String username, String password){
		List<NameValuePair> params = Lists.newArrayList();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		String str = "";
		try {
			//转换为键值对
			str = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
			System.out.println(str);
			//创建Get请求
			HttpGet httpGet = new HttpGet(url+"?"+str);
			HttpResponse response = new DefaultHttpClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity());
				logger.info("get===" + result);
				return JSONObject.parseObject(result);
			}
		} catch (Exception e) {
			logger.error("get==",e);
		} 
		return null;
	}
	
	public static JSONObject  put(String url,Map<String, Object> map,String token,String userId){
		HttpPut httpPut = new HttpPut(url);
		httpPut.setHeader("token", token);
		httpPut.setHeader("userId", userId);
		
		String json = JsonUtil.mapToJson(map);
		System.out.println(json);
		StringEntity entity = new StringEntity(json, "UTF-8");
		
		httpPut.setEntity(entity);
		httpPut.addHeader(HTTP.CONTENT_TYPE, "application/json");
		try {
			HttpResponse response = new DefaultHttpClient().execute(httpPut);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity());
				logger.info("put===" + result);
				return JSONObject.parseObject(result);
			}
		} catch (Exception e) {
			logger.error("put==",e);
		} 
		return null;
	}
	public static void main(String[] args) {
		/*HttpClient.get("http://59.110.44.175:8082/chs_fx/rest/wx/getUserInfo?userId=1474542363345", 
				"16078893-291d-4b85-9a35-18873395e154", "1474542363345");
		Map<String,Object> map = Maps.newHashMap();
		map.put("oldPassword", "123456");
		map.put("newPassword", "654321");
		HttpClient.put("http://59.110.44.175:8082/chs_fx/rest/wx/updatePassword", map, "16078893-291d-4b85-9a35-18873395e154", "1474542363345");*/
		
//		JSONObject object = HttpClient.get("http://59.110.44.175:8082/chs_fx/rest/wx/verifyMobileAndId?mobile=18612031089&id=43052319881215862X");
//		System.out.print(object);
	}
}
