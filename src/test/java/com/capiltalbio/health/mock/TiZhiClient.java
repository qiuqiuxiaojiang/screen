package com.capiltalbio.health.mock;

import java.util.HashMap;
import java.util.Map;

import com.capitalbio.common.util.JsonUtil;

public class TiZhiClient {
	public String mockTiZhi(String host, String[] args) throws Exception {
		String url = "";
		if (host.endsWith("/")) {
			url = host + "restWeixin/repstUserQuestion/repst/saveQuestionnaireApp";
		} else {
			url = host+"/restWeixin/repstUserQuestion/repst/saveQuestionnaireApp";
		}
		if (args.length < 5) {
			return "命令格式：java -jar runmock.jar host tizhi idCode answerStr physiqueResult";
		}
		String idCode = processValue(args[2]);
		String answerStr = processValue(args[3]);
		String physiqueResult = processValue(args[4]);
		
		Map<String, Object> mapTiZhi = new HashMap<String,Object>();
		mapTiZhi.put("idCode", idCode);
		mapTiZhi.put("answerStr", answerStr);
		mapTiZhi.put("physiqueResult", physiqueResult);
		Map<String, Object> mapSaveData = saveTiZhiData(url, mapTiZhi, null);
		
		return mapSaveData.get("msg").toString();
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
	
	private static Map<String,Object> saveTiZhiData(String url,Map<String, Object> map,Map<String, String> mapHeader) {
		Map<String, Object> mapResult = new HashMap<String,Object>();
		try {
			String sendRequest = HttpClientUtil.post1(url, map, mapHeader);
			Map<String, Object> mapResponse = JsonUtil.jsonToMap(sendRequest);
			if (mapResponse == null || mapResponse.size() < 1) {
				mapResult.put("msg", "保存数据失败！");
			}
			String message = mapResponse.get("msg").toString();
			if (message != null) {
				mapResult.put("msg", message);
			} else {
				mapResult.put("msg", "保存数据失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			mapResult.put("msg", "保存数据失败！");
		}
		return mapResult;
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		
		String url = "http://localhost:8080/com.bioeh.sp.hm.customer.mserverWeixin/restWeixin/repstUserQuestion/repst/saveQuestionnaireApp";
    	Map<String, Object> map = new HashMap<String,Object>();
    	map.put("answerStr", "1");
    	map.put("idCode", "411122199209068279");
		try {
			Map<String, Object> saveData = saveTiZhiData(url, map, null);
			System.out.println(saveData.get("msg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
