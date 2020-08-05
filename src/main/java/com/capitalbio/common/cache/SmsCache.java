package com.capitalbio.common.cache;


public class SmsCache {
	private BaseCache smsCache;
	private static SmsCache instance;
	private static Object lock = new Object();
	public SmsCache() {
		smsCache = new BaseCache("sms",600);
	}
	public static SmsCache getInstance(){
		if (instance == null){
			synchronized( lock ){
				if (instance == null){
					instance = new SmsCache();
				}
			}
		}
		return instance;
	}
	
	public void putMessage(String mobile, String code) {
		smsCache.put(mobile, code);
	}
	
	public void removeMessage(String mobile) {
		smsCache.remove(mobile);
	}
	
	public String getMessage(String mobile) {
		try {
			return (String)smsCache.get(mobile);
		} catch (Exception e) {
		}
		return null;
	}
	
	public void removeAllMessage() {
		smsCache.removeAll();
	}
}


