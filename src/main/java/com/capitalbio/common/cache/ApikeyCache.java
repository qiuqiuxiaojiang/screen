package com.capitalbio.common.cache;

import java.util.Map;

public class ApikeyCache {
	private BaseCache apikeyCache;
	private static ApikeyCache instance;
	private static Object lock = new Object();
	public ApikeyCache() {
		apikeyCache = new BaseCache("token",-1);
	}
	public static ApikeyCache getInstance(){
		if (instance == null){
			synchronized( lock ){
				if (instance == null){
					instance = new ApikeyCache();
				}
			}
		}
		return instance;
	}
	
	public void putApikey(String apikey, Map<String,Object> app) {
		apikeyCache.put(apikey, app);
	}
	
	public void removeApikey(String apikey) {
		apikeyCache.remove(apikey);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getApikey(String apikey) {
		try {
			return (Map<String,Object>)apikeyCache.get(apikey);
		} catch (Exception e) {
		}
		return null;
	}
	
	public void removeAll() {
		apikeyCache.removeAll();
	}
}


