package com.capitalbio.auth.service;

import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.capitalbio.auth.util.HttpUtils;
import com.capitalbio.common.util.JsonUtil;
import com.google.common.collect.Maps;

@Service
public class GeneService {
	
	private Logger logger = LoggerFactory.getLogger(getClass()); 
	@Value("${gene.url}")
	private String url;
	
	public Map<String,Object> getGeneInfo(String serviceCode, String aspId) {
		
		String getUrl = url + "/screening/getGeneInfo?serviceCode=" + serviceCode + "&aspId=" + aspId;
		Map<String,Object> returnMap = Maps.newHashMap();
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			//httpGet.addHeader("token", token);
			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				logger.debug("result:" + result);
				returnMap = JsonUtil.jsonToMap(result);
				return returnMap;
			} else {
				logger.debug("HTTP error code:"+code);
				returnMap.put("msg", "认证系统访问错误");
				returnMap.put("code", "1");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnMap.put("msg", "1");
		}
		return returnMap;
	}

}
