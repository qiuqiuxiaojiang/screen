package com.capitalbio.healthcheck.uploaddata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Util {
	public Util(){};
	private static Util util;
	
	public static Util getInstance(String fileName){
		if(util==null){
			util=new Util(fileName);
		}
		return util;
	}
	static Properties prop = new Properties();
	/**
	 * 根据提供的key获取对应的�??
	 * @param key
	 * @return
	 */
	public String getValue(String key){
		Set keyValue = prop.keySet();
		for (Iterator it = keyValue.iterator(); it.hasNext();) {
			if(((String) it.next()).equals(key)){
				return prop.getProperty(key);
			}
		}
		return null;
	}

	/**
	 * 初始化本类时�?要传入url参数来初始化�?个配置文�?
	 * @param url
	 * @throws IOException 
	 */
	public Util(String filename){
		File file = new File(filename);
		 try {
			 InputStream in = new FileInputStream(file);
			 prop = new Properties();
			 prop.load(in);
        } catch (IOException ex) {
            ex.fillInStackTrace();
        }
		
		/*ClassPathResource cr = new ClassPathResource(filename);
		prop = new Properties();
        try {
        	prop.load(cr.getInputStream());
        } catch (IOException ex) {
            ex.fillInStackTrace();
        }*/
	}
	
	
	/**
	 * 将一个map转换成JSON对象
	 * @param param
	 * @return
	 */
	public static String parseMapToJson(Map<String, Object> param){
		Set<Map.Entry<String, Object>> entrys = param.entrySet();
		Map.Entry<String, Object> entry = null;
	    String key = "";
	    Object value;
	    StringBuffer jsonBuffer = new StringBuffer();
	    jsonBuffer.append("{");    
	    for(Iterator<Map.Entry<String, Object>> it = entrys.iterator();it.hasNext();){
	    	entry =  (Map.Entry<String, Object>)it.next();
	    	key = entry.getKey();
	        value = entry.getValue() == null ? "" : entry.getValue();
	        jsonBuffer.append("\"").append(key).append("\"");
	        if (value instanceof String || value == null) {
	        	jsonBuffer.append(":\"").append(value).append("\"");
	        }
	        else {
	        	jsonBuffer.append(":").append(value).append("");
	        }
	        	
	        if(it.hasNext()){
	             jsonBuffer.append(",");
	        }
	    }
	    jsonBuffer.append("}");
	    return jsonBuffer.toString();
	}

	
	
	/**
	 * 格式化日期
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDate(Date date, String format){
		if (date == null) {
			return "";
		}
		if (format == null || format.length() <= 0) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat dateFormat=new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
}
