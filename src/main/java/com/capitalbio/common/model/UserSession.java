package com.capitalbio.common.model;

import java.util.HashMap;
import java.util.Map;

public class UserSession {
	@SuppressWarnings("rawtypes")
	private static final ThreadLocal SESSION_MAP = new ThreadLocal();
	
	protected UserSession(){
		
	}
	
	/**
	 * 获得线程中保存的属性
	 * @param attribute
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object get(String attribute){
		Map map = (Map) SESSION_MAP.get();
		/*System.out.println("usersession1:"+map.toString());
		System.out.println("usersession:"+map.containsKey("user"));*/
		return map.get(attribute);
	}
	
	/**
	 * 获得线程中保存的属性，使用指定类型进行转型
	 * @param attribute
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T>T get(String attribute,Class<T> clazz){
		return (T) get(attribute);
	}
	
	public static void remove(String attribute){
		SESSION_MAP.remove();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void set(String attribute,Object value){
		Map map = (Map) SESSION_MAP.get();
		
		if(map == null){
			map = new HashMap();
			SESSION_MAP.set(map);
		}
		map.put(attribute, value);
	}
	

}
