package com.capitalbio.common.cache;


public class TokenCache {
	
	/*private static Map<String, Integer> overTime = new HashMap<String, Integer>();
	
	static {
		overTime.put("1", 10);
		overTime.put("2", 100);
		overTime.put("3", 200);
	}*/
	
	private BaseCache tokenCache;
	private static TokenCache instance;
	private static Object lock = new Object();
	public TokenCache() {
		tokenCache = new BaseCache("token",-1);
	}

	public TokenCache(int refreshPeriod) {
		tokenCache = new BaseCache("token", refreshPeriod);
	}
	
	public static TokenCache getInstance(){
		if (instance == null){
			synchronized( lock ){
				if (instance == null){
					instance = new TokenCache();
				}
			}
		}
		return instance;
	}

	public static TokenCache getInstance(int refreshPeriod){
		if (instance == null){
			synchronized( lock ){
				if (instance == null){
					instance = new TokenCache(refreshPeriod);
				}
			}
		}
		return instance;
	}
	
//	public void putToken(String token, Map<String,Object> user) {
//		tokenCache.put(token, user);
//	}
	
	public void putToken(String token, String user) {
		tokenCache.put(token, user);
	}

	public void removeToken(String token) {
		tokenCache.remove(token);
	}
	
//	@SuppressWarnings("unchecked")
//	public Map<String,Object> getToken(String token) {
//		try {
//			return (Map<String,Object>)tokenCache.get(token);
//		} catch (Exception e) {
//		}
//		return null;
//	}

	public String getToken(String token) {
		try {
			return (String)tokenCache.get(token);
		} catch (Exception e) {
		}
		return null;
	}

	public void removeAllToken() {
		tokenCache.removeAll();
	}
}


