package com.capitalbio.common.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomUtil {
    public static String getRandomSix(){  
        Random rad=new Random();  
          
        String result  = rad.nextInt(1000000) +"";  
          
        if(result.length()!=6){  
            return getRandomSix();  
        }  
        return result;  
    }  

	
	public static int randomInt(int min, int max) {
		long num = Math.round(Math.random()*(max-min)+min);
		return (int)num;
	}
	
	public static double randomDouble(double min, double max) {
		Random ra =new Random();
		double num = ra.nextDouble() * (max-min)+min;
		
		BigDecimal bg = new BigDecimal(num);
		double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		return f1;
	}
	
	private static long random(long begin, long end) {
		long rtn = begin + (long) (Math.random() * (end - begin));
		return rtn;
	}


	/**
	 * 获取随机日期
	 * 
	 * @param beginDate
	 *            起始日期，格式为：yyyy-MM-dd
	 * @param endDate
	 *            结束日期，格式为：yyyy-MM-dd
	 * @return
	 */

	public static Date randomDate(String beginDate, String endDate) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start = format.parse(beginDate+ " 00:00:00");// 构造开始日期
			Date end = format.parse(endDate+" 23:59:59");// 构造结束日期
			// getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
			if (start.getTime() >= end.getTime()) {
				return null;
			}
			long date = random(start.getTime(), end.getTime());

			return new Date(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
