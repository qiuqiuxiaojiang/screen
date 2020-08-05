package com.capitalbio.common.util.redis;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;

public class InitErrorCode  {
	Logger logger = LoggerFactory.getLogger(getClass());
	private static final String CONFIG_FILENAME="ErrorCode.properties";

	public void setErrorCode2Redis(){
		JedisTemplate template = RedisUtils.getTemplate();
		String KEY = "ErrorCode";
		Properties prop = new Properties();
		try{
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILENAME));
            Set<Object> keyset = prop.keySet();
            Iterator<Object> it = keyset.iterator();
            logger.info("##################### Init Error Code #####################");
            while(it.hasNext()){
                String fieldName = (String)it.next();
                String value = null;
                value = prop.getProperty(fieldName);
                logger.info("KEY=" + KEY + "&&fieldName=" +fieldName +"&&value=" + value);
                template.hset(KEY, fieldName, value);
            }
        }catch(Exception exp){
            throw new RuntimeException("ErrorCode配置文件：" + CONFIG_FILENAME + "不存在！\r\n");
        }
		
	}
}
