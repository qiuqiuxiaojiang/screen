package com.capitalbio.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Properties;

/**
 * 公用工具类
 * @author guohuiyang
 * @version 2017/05/11
 */
public class CommUtil {
	
	/** 配置文件名 */
	private static final String CONFIG_FILE_NAME = "application.properties";
	
	/** 配置map */
	private static HashMap<String,Object[]> propsMap = new HashMap<String,Object[]>();
	
	/**
	 * 从配置文件中取得 String 值，若无则返回默认值
	 * @param keyName 属性名
	 * @param defaultValue 默认值
	 * @return 属性值
	 */
	public static String getConfigByString(String keyName,String defaultValue){
		String value = getConfig(keyName);
		if(value != null && value.length() > 0){
			return value.trim();
		}else{
			return defaultValue;
		}
	}
	
	public static String getConfigByString(String keyName){
		String value = getConfig(keyName);
		if(value != null && value.length() > 0){
			return value.trim();
		}
		return "";
	}
	
	/**
	 * 从配置文件中取得 int 值，若无（或解析异常）则返回默认值
	 * @param keyName 属性名
	 * @param defaultValue 默认值
	 * @return 属性值
	 */
	public static int getConfigByInt(String keyName,int defaultValue){
		String value = getConfig(keyName);
		if(value != null && value.length() > 0){
			try {
				int parseValue = Integer.parseInt(value.trim());
				return parseValue;
			} catch (Exception e) {
				return defaultValue;
			}
		}else{
			return defaultValue;
		}
	}
	
	/**
	 * 从配置文件中取得 long 值，若无（或解析异常）则返回默认值
	 * @param keyName 属性名
	 * @param defaultValue 默认值(单位：毫秒)
	 * @return 属性值
	 */
	public static long getConfigByLong(String keyName,long defaultValue) {
		String value = getConfig(keyName);
		if(value != null && value.length() > 0){
			try {
				long parseValue = Long.parseLong(value.trim());
				return parseValue;
			} catch (Exception e) {
				return defaultValue;
			}
		}else{
			return defaultValue;
		}
	}
	
	/**
	 * 从配置文件中取得 boolean 值，若无则返回默认值
	 * @param keyName 属性名
	 * @param defaultValue 默认值
	 * @return 属性值
	 */
	public static boolean getConfigByBoolean(String keyName,boolean defaultValue){
		String value = getConfig(keyName);
		if(value != null && value.length() > 0){
			return Boolean.parseBoolean(value.trim());
		}else{
			return defaultValue;
		}
	}
	
	/**
	 * 从配置文件中读取字符串的值
	 * 配置文件查找顺序：
	 * 		1-项目根路径
	 * 		2-src/main/resources
	 * @param keyName 属性名
	 * @return 属性值
	 */
	private static String getConfig(String keyName) {
		Properties props = null;
		boolean bIsNeedLoadCfg = false;

		String filePath = CONFIG_FILE_NAME;
		File cfgFile = new File(filePath);
		if(!cfgFile.exists()){
			try{
				filePath = CommUtil.class.getClassLoader().getResource(CONFIG_FILE_NAME).getPath();
			}catch (Exception e) {
				return null;
			}
			cfgFile = new File(filePath);
			if(!cfgFile.exists()){
				return null;
			}
		}
		
		Object[] arrs = propsMap.get(filePath);
		if(arrs == null){
			bIsNeedLoadCfg = true ;
		}else{
			Long lastModify = (Long)arrs[0];
			if(lastModify.longValue() != cfgFile.lastModified()){
				bIsNeedLoadCfg = true;
			}else{
				props = (Properties)arrs[1];
			}
		}
		
		if(bIsNeedLoadCfg == true){
			FileInputStream fis = null;
			try{
				fis = new FileInputStream(cfgFile);
				props = new Properties();		
				props.load(fis);
				propsMap.put(filePath, new Object[]{cfgFile.lastModified(),props});
			}catch (Exception e) {
				return "";
			}finally{
				try{
					if(fis != null){
						fis.close();
					}
				}catch(Exception e){;}
			}
		}
		return props.getProperty(keyName, "");
	}
	
	/**
	 * 将异常的堆栈信息转为字符串
	 * @param e 异常
	 * @return 异常的字符串描述
	 */
	public static String getExpStack(Exception e) {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(bo);
		e.printStackTrace(pw);
		pw.flush();
		pw.close();
		return bo.toString();
	}
	/**
	 * double型数据保留指定小数位
	 * @param val
	 * @param num
	 * @return
	 */
	public static  double getDecimal(Double val,int num){
		String index = "";
		if(num==0){
			index = "#";
		}else if(num == 1){
			index = "#.0";
		}else if(num == 2){
			index = "#.00";
		}
		DecimalFormat df = new DecimalFormat(index);
		return Double.parseDouble(df.format(val));
	}
	/**
	 * 将Double型转成可展示的字符串
	 * 如：25.0--25  25.00--25
	 * @param d
	 * @return
	 */
	public static String getDoubleToStr(Double value){
		String str = String.valueOf(value);
		String[] strs = str.split("\\.");
		if(strs[1].equals("00") || strs[1].equals("0")){
			str = str.substring(0,str.indexOf("."));
		}
		return str;
	}
	/**
	 * 将Double型转成指定保留小数位的可展示的字符串
	 * 如：25.0--25  25.00--25
	 * @param d
	 * @return
	 */
	public static String getDoubleToStr(Double value,int precision){
		value = getDecimal(getDecimal(value, 3), precision);
		String str = String.valueOf(value);
		String[] strs = str.split("\\.");
		if(strs[1].equals("00") || strs[1].equals("0")){
			str = str.substring(0,str.indexOf("."));
		}
		return str;
	}
	
	public static void main(String[] args) {
		String filePath = CommUtil.class.getClassLoader().getResource(CONFIG_FILE_NAME).getPath();
		System.out.println(filePath);
		File file = new File(filePath);
		System.out.println(file.exists());
	}
}
