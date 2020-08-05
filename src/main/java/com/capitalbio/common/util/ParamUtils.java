package com.capitalbio.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ParamUtils {
	public static String getParamValue(Map<String,Object> params, String key) {
		Object val = params.get(key);
		if (val == null) {
			return null;
		}
		if (val instanceof String[]) {
			String[] values = (String[])val;
			if (values.length == 0) {
				return "";
			}else if (values.length == 1) {
				return values[0];
			} else {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < values.length; i++) {
					sb.append(values[i]).append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				return sb.toString();
			}
		} else if (val instanceof String) {
			return (String)val;
		} else {
			return String.valueOf(val);
		}
	}
	
	public static Object getProperValue(String paramValue, String type) {
		if (type.equals("int")) {
			return getIntValue(paramValue);
		} else if (type.equals("double")) {
			return getDoubleValue(paramValue);
		} else if (type.equals("date")) {
			return getDate(paramValue);
		} else {
			return paramValue;
		}
	}

	public static long getLong(String value) {
		try {
			long lvalue = Long.parseLong(value);
			return lvalue;
		} catch (Exception e) {}
		return 0;
	}

	public static Integer getIntValue(String value) {
		try {
			Integer ivalue = new Integer(value);
			return ivalue;
		} catch (Exception e) {}
		return null;
	}

	public static Double getDoubleValue(String value) {
		try {
			Double dvalue = new Double(value);
			return dvalue;
		} catch (Exception e) {}
		return null;
	}
	
	public static Date getDate(String value) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(value);
			return date;
		} catch (Exception e) {}
		return null;
	}
	
	public static String getDate2(String value) {
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
			String dateStr = new SimpleDateFormat("yyyyMMdd").format(date);
			return dateStr;
		} catch (Exception e) {}
		return null;
	}
	
	public static Date getDateTime(String value) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = sdf.parse(value);
			return date;
		} catch (Exception e) {}
		return null;
		
	}
	
	public static Date parseDate(String value, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			Date date = sdf.parse(value);
			return date;
		} catch (Exception e) {}
		return null;
		
	}
	
	public static String getDateNow(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(date);
		return time;
	}

	
	public static double doubleScale(double dvalue, int scale) {
		BigDecimal b = new BigDecimal(dvalue);
		double rvalue = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		return rvalue;
	}
	
	public static String decimalFormat(Double num) {
		DecimalFormat df = new DecimalFormat("0.000");
		String data = df.format(num);
		//System.out.println(data);
		//return Double.parseDouble(data);
		return data;
	}
	
	public static Double decimalFormat(Double num, String str) {
		DecimalFormat df = new DecimalFormat(str);
		String data = df.format(num);
		//System.out.println(data);
		return Double.parseDouble(data);
		//return data;
	}
	
	public static String decimalFormat1(Double num, String str) {
		DecimalFormat df = new DecimalFormat(str);
		String data = df.format(num);
		//System.out.println(data);
		return data;
	}
	
	public static  Date getMonday(int limit) {
		Calendar c = Calendar.getInstance();
		c.roll(Calendar.WEEK_OF_YEAR, limit);
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0) {
			day_of_week = 7;
		}
		c.add(Calendar.DATE, -day_of_week + 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	public static  Date getSunday(int limit) {
		Calendar c = Calendar.getInstance();
		c.roll(Calendar.WEEK_OF_YEAR, limit);
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0) {
			day_of_week = 7;
		}
		c.add(Calendar.DATE, -day_of_week + 7);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}
	
	public static  Date getMonthFirst(int limit) {
		Calendar c = Calendar.getInstance();
		c.roll(Calendar.MONTH, limit);
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH,1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	public static  Date getMonthLast(int limit) {
		Calendar c = Calendar.getInstance();
		c.roll(Calendar.MONTH, limit);
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}
	
	public static Date getLastYear(int limit) {
		Calendar c = Calendar.getInstance();
		c.roll(Calendar.YEAR, limit);
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH,1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
		
	}
	
	public static String getTimeBefore() {
		Long time = System.currentTimeMillis();//获得系统当前时间的毫秒数

//		System.out.println("获取当前系统时间为："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));//转换成标准年月日的形式

		time = time - 30*1000*60;//在当前系统时间的基础上计算前30分钟
//		System.out.println("三十分钟后的时间为："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
	}

	
	public static void main(String[] args) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date date = getLastYear(-2);
//		System.out.println(sdf.format(date));
//		date = getLastYear(-1);
//		System.out.println(sdf.format(date));

//		date = getMonthFirst(-1);
//		System.out.println(sdf.format(date));
//		date = getMonthLast(-1);
//		System.out.println(sdf.format(date));
//
//		date = getMonthFirst(-3);
//		System.out.println(sdf.format(date));
//		date = getMonthLast(-3);
//		System.out.println(sdf.format(date));
//
//		date = getMonthFirst(-2);
//		System.out.println(sdf.format(date));
//		date = getMonthLast(-2);
//		System.out.println(sdf.format(date));

//		Date date = getMonday(0);
//		System.out.println(sdf.format(date));
//		date = getMonday(-1);
//		System.out.println(sdf.format(date));
//		date = getMonday(-3);
//		System.out.println(sdf.format(date));
//		date = getMonday(-8);
//		System.out.println(sdf.format(date));
//		date = getSunday(0);
//		System.out.println(sdf.format(date));
//		date = getSunday(-1);
//		System.out.println(sdf.format(date));
//		date = getSunday(-3);
//		System.out.println(sdf.format(date));
//		date = getSunday(-8);
//		System.out.println(sdf.format(date));
		
		
//		getTimeBefore();
		
		getDate2("2015-01-01");
	}

}
