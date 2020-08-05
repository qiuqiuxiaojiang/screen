package com.capitalbio.common.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json 工具类
 * http://www.json.cn/json/wiki.html
 *
 */
public class JsonUtil1 { 

	/**
	 * 转换json 字符串为map
	 * @param jsonStr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> json2Map(String jsonStr){
		ObjectMapper om = new ObjectMapper();
		Map<String, Object> map = null;
		try {
			map = om.readValue(jsonStr, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 将一个map转换成JSON对象
	 * 	 对象（object） 是一个无序的“‘名称/值’对”集合。一个对象以“{”（左括号）开始，“}”（右括号）结束。
	 * 		 每个“名称”后跟一个“:”（冒号）；“‘名称/值’ 对”之间使用“,”（逗号）分隔。
	 *		 数组（array） 是值（value）的有序集合。
	 * 		一个数组以“[”（左中括号）开始，“]”（右中括号）结束。
	 * 		值之间使用“,”（逗号）分隔。
	 * @param param
	 * @return
	 */
	public static String map2Json(Map<String,Object> map) {
		ObjectMapper om = new ObjectMapper();

		String json="";
		try {
			json = om.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 将一个list转换成json串，转换后格式
	 * [{key:'value'},{key:'value'},{key:'value'}...]
	 * 数组：数组在js中是中括号“[]”括起来的内容，数据结构为 ["java","javascript","vb",...]，取值方式和所有语言中一样，使用索引获取，字段值的类型可以是 数字、字符串、数组、对象几种。
	 * @param param
	 * @return
	 */
	
	public static String list2Json(List<Map<String,Object>> list) {
		ObjectMapper om = new ObjectMapper();
		String json = "";
		try {
			json = om.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		//System.out.println(json);
		return json;
	}
	
	/**
	 * 将一个array类型的json串转换成list
	 * @param jsonStr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String,Object>> jsonToList(String jsonStr){
		ObjectMapper om = new ObjectMapper();
		List<Map<String, Object>> list = null;
		try {
			list = om.readValue(jsonStr, List.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	
	public static void main(String[] args) {
		String jsonss=" ";
		System.out.println(jsonss.trim().length()==0);
		
		List<Map<String,Object>> json=new ArrayList<Map<String,Object>>();
		Map<String, Object> map= new HashMap<String, Object>();
		Map<String, Object> map1= new HashMap<String, Object>();
		map.put("1", "12");
		map.put("name", "24");
		json.add(map);
		map.put("2", "12");
		map.put("name", "24");
		json.add(map);
		System.out.println(list2Json(json));
		map1.put("height", map);
		String mapToJson = map2Json(map1);
		json2Map(mapToJson);
		System.out.println("mapToJson>>>" + mapToJson);
//		
		Map<String, Object> json2Map = json2Map("{\"bmi\":{\"1\":\"13.0\",\"2\":\"17.0\",\"3\":\"14.0\",\"4\":\"8.0\"}}");
		Set<Entry<String, Object>> entrySet = json2Map.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey();
			System.out.println(">>>"+key);
			HashMap<String, Object> value = (HashMap<String, Object>) entry.getValue();
			Object object = value.get("1");
			System.out.println(object);
		}
		
//		List<Map<String,Object>> json=new ArrayList<Map<String,Object>>();
//		json.add(map);
//		Map<String, Object> map11= new HashMap<String, Object>();
//		map1.put("fuhe", json);
//		String map2Json = map2Json(map1);
//		System.out.println("map2Json" +map2Json);
//		String list2Json = list2Json(json);
//		System.out.println(list2Json);
	}
}
