package com.capitalbio.common.util.redis;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.capitalbio.common.util.CommUtil;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;


public class RedisUtil {

	static JedisTemplate template = RedisUtils.getTemplate();
	/***
	 * 删除模糊查询的结果
	 * @param key
	 * @return
	 */
	public static boolean delVague(String key){
		try {
			Set<String> keys = template.keys(key);
			if (null != keys && keys.size() > 0) {
				for (String string : keys) {
					template.del(string);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/***
	 * 插入字符串类型键值，并设置过期时间
	 * @param key
	 * @param value
	 * @param time
	 * @return
	 */
	public static boolean set(String key,String value,int time){
		try {
			template.set(key, value);
			template.expire(key, time);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 模糊校验该键是否存在且是否过期且只存在唯一一个
	 * 不存在 或者 过期时返回false
	 * @param key
	 * @return
	 */
	public static boolean checkKeyVague(String key){
		try {
			Set<String> keys = template.keys(key);
			if (null != keys && keys.size() == 1) {
				for (String string : keys) {
					//当 key 不存在时，返回 -2 。 当 key 存在但没有设置剩余生存时间时，返回 -1 。 
					//否则，以秒为单位，返回 key 的剩余生存时间。
					Long ttl = template.ttl(string);
					if (ttl != -1 && ttl != -2) {
						return true;
					}
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 模糊校验该键是否存在且是否过期
	 * 
	 * 不存在 或者 过期时返回key
	 * 返回key
	 * @param key
	 * @return
	 */
	public static String getKeyByKeyVague(String key){
		try {
			Set<String> keys = template.keys(key);
			if (null != keys && keys.size() == 1) {
				for (String string : keys) {
					//当 key 不存在时，返回 -2 。 当 key 存在但没有设置剩余生存时间时，返回 -1 。 
					//否则，以秒为单位，返回 key 的剩余生存时间。
					Long ttl = template.ttl(string);
					if (ttl != -1 && ttl != -2) {
						return string;
					}
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据key获取value
	 * 
	 * 返回value
	 * @param key
	 * @return
	 */
	public static String getStringValueByKey(String key){
		try {
			String value = template.get(key);
			Long ttl = template.ttl(key);
			if (ttl != -1 && ttl != -2 && null != value && StringUtils.isNotEmpty(value)) {
				return value;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		JedisTemplate template = RedisUtils.getTemplate();
		String REDIS_BUCKET=CommUtil.getConfigByString("redis.bucket", "fuxingzh");
		String key = REDIS_BUCKET + ":wxId_userId";
		template.set(key, "token");
		template.set("userId", "token");
		template.set( REDIS_BUCKET + ":test1", "token");
		template.set( REDIS_BUCKET + ":test2", "token");
		String string = template.get(REDIS_BUCKET + ":wxId*");
		Set<String> keys = template.keys(REDIS_BUCKET + ":wxId_*");
		Set<String> keys1 = template.keys(REDIS_BUCKET + "*_userId*");
		Set<String> keys2 = template.keys( "*_userId*");
		
		
		Boolean del3 = template.del(key);
		Boolean del = template.del(REDIS_BUCKET + ":wxId*");
		Boolean del2 = template.del(REDIS_BUCKET + "*userId*");
		System.out.println(1);
		
	}
}
