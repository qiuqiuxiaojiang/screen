package com.capiltalbio.health.client;

import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.capitalbio.auth.util.HttpUtils;
import com.capitalbio.common.util.JsonUtil;
import com.google.common.collect.Maps;

public class AuthClient {
	String url = "http://59.110.44.175:8082/chs_fx/rest";
	public Map<String,Object> requestInfo(String customerId) {
		String getUrl = url + "/wx/getUserInfoById?id="+customerId;
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			httpGet.addHeader("token", "d872a259-2352-4556-a658-051049fecf38");
			httpGet.addHeader("userId", "dm1");
			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				Map<String,Object> map = JsonUtil.jsonToMap(result);
				System.out.println(result);
				System.out.println(map);
			} else {
				System.out.println(code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Map<String,Object> infoMap = Maps.newHashMap();
		infoMap.put("uniqueId", "1");
		infoMap.put("phone","13333333333");
		return infoMap;
	}

	public static void main(String[] args) throws Exception {
		AuthClient ac = new AuthClient();
		ac.requestInfo("120223197806260165");
	}
}
