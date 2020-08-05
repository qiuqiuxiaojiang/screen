package com.capitalbio.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

public class DateUtil {
	
	/**
	 * 日期格式，年-月-日
	 */
	public static final String PATTERN_DAY = "yyyy-MM-dd";

	/**
	 * 获取当前日期（String）
	 * 
	 * @return
	 */
	public static String getNowDate(Date date){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(date);
		return time;
	}
	
	/**
	 * 获取当前日期前N天的日期
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getDate(int day) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -day);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sdf.format(calendar.getTime());
		return dateString;
	}
	
	/**
	 * 将String日期转换为Date
	 * 
	 * @param time
	 * @return
	 * @throws Exception
	 */
	public static Date stringToDate(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date=sdf.parse(time);
			return date;
		} catch (Exception e) {}
		return null;
	}

	
	/**
	 * 将String日期转换为Date
	 * 
	 * @param time
	 * @return
	 * @throws Exception
	 */
	public static Date stringToDate(String time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			Date date=sdf.parse(time);
			return date;
		} catch (Exception e) {}
		return null;
	}

	/**
	 * 将String时间转换为Date
	 * 
	 * @param datetime
	 * @return
	 * @throws Exception
	 */
	public static Date stringToDateTime(String datetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date=sdf.parse(datetime);
			return date;
		} catch (Exception e) {}
		return null;
		
	}
	
	/**
	 * 获取当前日期（String）
	 * 
	 * @return
	 */
	public static String getDateStr(Date date){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = sdf.format(date);
		return time;
	}
	
	/**
	 * date转String
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		String str=sdf.format(date);  
		return str;
	}

	/**
	 * datetime转String
	 * 
	 * @param date
	 * @return
	 */
	public static String datetimeToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		String str=sdf.format(date);  
		return str;
	}

	/**
	 * 获取当前月份的开始日期
	 * 
	 * @return
	 */
	public static String getCurrentMonthStartTime() {
        Calendar c = Calendar.getInstance();
        String str = null;
        try {
            c.set(Calendar.DATE, 1);
            str = dateToString(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
	
	/**
	 * 获取当前年的开始日期
	 * 
	 * @return
	 */
	public static String getCurrentYearStartTime() {
         Calendar c = Calendar.getInstance();
         String str = null;
         try {
             c.set(Calendar.MONTH, 0);
             c.set(Calendar.DATE, 1);
             str = dateToString(c.getTime());
         } catch (Exception e) {
             e.printStackTrace();
         }
         return str;
    }
	
	/**
	 * 获取当前季度的开始时间
	 * 
	 * @return
	 */
	public static String getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        String str = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 4);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
            str = dateToString(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
	
	/**
	 * 获取当月天数
	 * 
	 * @return
	 */
	public static int getCurrentMonthDay() {  
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }

	/**
	 * 今天是一个月中第几天
	 * 
	 * @return
	 */
	public static int getDayOfMonth() {
		Date date=new Date();  
        Calendar ca=Calendar.getInstance();  
        ca.setTime(date);  
        int a=ca.get(Calendar.DAY_OF_MONTH);  
        return a;
	}
	
	public static String getBeforeMonth(int n) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		c.setTime(new Date());
		c.add(Calendar.MONTH, -n);
		Date m = c.getTime();
		String month = sdf.format(m);
		return month;
	}
	
	/**
	 * 获取某月的第一天
	 * @param n
	 * @throws ParseException 
	 */
	public static String getFirstTime(String datadate) throws ParseException {
		Date date = null;
        String day_first = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = format.parse(datadate);
        
        //创建日历
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        day_first = sdf.format(calendar.getTime());
        return day_first;
	}
	
	/**
	 * 获取某月的最后一天
	 * @param n
	 * @throws ParseException 
	 */
	public static String getLastTime(String datadate) throws ParseException {
		Date date = null;
        String day_last = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = format.parse(datadate);
        //创建日历
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);    //加一个月
        calendar.set(Calendar.DATE, 1);     //设置为该月第一天
        calendar.add(Calendar.DATE, -1);    //再减一天即为上个月最后一天
        day_last = sdf.format(calendar.getTime());
        return day_last;
	}

	/**
	 * 获取两个时间天的差值，忽略时分秒
	 *
	 * @param date1 时间1
	 * @param date2 时间2
	 * @return
	 */
	public static int betweenDays(Date date1, Date date2) {
	    date1 = getZeroDate(date1);
	    date2 = getZeroDate(date2);

	    Calendar calendar1 = Calendar.getInstance();
	    calendar1.setTime(date1);
	    Calendar calendar2 = Calendar.getInstance();
	    calendar2.setTime(date2);

	    return (int) (Math.abs(calendar1.getTimeInMillis() - calendar2.getTimeInMillis()) / (1000 * 60 * 60 * 24)) + 1;
	}

	/**
	 * 获取当前时间天，消除时分秒
	 *  例如：2017-03-20 12:15:32
	 *  结果：2017-03-20 00:00:00
	 *
	 * @param date
	 * @return
	 */
	public static Date getZeroDate(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}
	
	/**
	 * 获取两个日期之间的所有日期（年月日）
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<String> getDays(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<String> list = Lists.newArrayList();
		try {
			Date date1 = stringToDate(startTime);
			Date date2 = stringToDate(endTime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date1);
			while (calendar.getTime().before(date2)) {
				list.add((sdf.format(calendar.getTime())));
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			list.add(endTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取两个日期间的所有月份
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<String> getMonths(String startTime, String endTime) {
		List<String> result = Lists.newArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		try {
			Calendar min = Calendar.getInstance();
			Calendar max = Calendar.getInstance();
			
			min.setTime(sdf.parse(startTime));
			min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
			
			max.setTime(sdf.parse(endTime));
			max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
			
			Calendar curr = min;
			while (curr.before(max)) {
				result.add(sdf.format(curr.getTime()));
				curr.add(Calendar.MONTH, 1);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static Date getNowMonday() {
		Calendar cal = Calendar.getInstance();    
               
        cal.add(Calendar.DAY_OF_MONTH, -1); //解决周日会出现 并到下一周的情况    
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);    
		cal.set(Calendar.AM_PM, Calendar.AM);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 1);
        return cal.getTime();    
	}
	
	public static Date getLastMonday() {
		Calendar calendar = Calendar.getInstance();
		int dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK)-1;
		int offset1=1-dayOfWeek;
		calendar.add(Calendar.DATE, offset1-7);
		calendar.set(Calendar.AM_PM, Calendar.AM);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 1);
		return calendar.getTime();
	}
	
	public static Date getLastSunday() {
		Calendar calendar = Calendar.getInstance();
		int dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK)-1;
		int offset2=7-dayOfWeek;
		calendar.add(Calendar.DATE, offset2-7);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}
	
	/**
	 * 清除时分秒毫秒数据，只取日期
	 * @param date
	 * @return
	 */
	public static Date getDate(Date date) {
		Calendar c = Calendar.getInstance();
		if (date != null) {
			c.setTime(date);
		}
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/*public static int betweenDays(Date fDate, Date oDate) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.set(2016, 12, 28);
		calendar2.set(2017, 01, 5);
	    long milliseconds1 = calendar1.getTimeInMillis();
	    long milliseconds2 = calendar2.getTimeInMillis();
	    long diff = milliseconds2 - milliseconds1;
	    long diffSeconds = diff / 1000;
	    long diffMinutes = diff / (60 * 1000);
	    long diffHours = diff / (60 * 60 * 1000);
	    long diffDays = diff / (24 * 60 * 60 * 1000);

        return day2 - day1;
    }*/
	
	/*public static void main(String[] args) {
	    Calendar calendar1 = Calendar.getInstance();
	    Calendar calendar2 = Calendar.getInstance();
	    calendar1.set(2016, 12, 28);
	    calendar2.set(2017, 01, 5);
	    long milliseconds1 = calendar1.getTimeInMillis();
	    long milliseconds2 = calendar2.getTimeInMillis();
	    long diff = milliseconds2 - milliseconds1;
	    long diffSeconds = diff / 1000;
	    long diffMinutes = diff / (60 * 1000);
	    long diffHours = diff / (60 * 60 * 1000);
	    long diffDays = diff / (24 * 60 * 60 * 1000);
	    System.out.println("\nThe Date Different Example");
	    System.out.println("Time in milliseconds: " + diff + " milliseconds.");
	    System.out.println("Time in seconds: " + diffSeconds + " seconds.");
	    System.out.println("Time in minutes: " + diffMinutes + " minutes.");
	    System.out.println("Time in hours: " + diffHours + " hours.");
	    System.out.println("Time in days: " + diffDays + " days.");
	}*/
	
	public static Date rollDate(Date date, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
		int offset = days + dayOfYear;
		if (offset < 0) {
			c.roll(Calendar.YEAR, -1);
		} else if (offset > 365) {
			c.roll(Calendar.YEAR, 1);
		}
 
		c.roll(Calendar.DAY_OF_YEAR, days);
		Date returnDate = c.getTime();
		return returnDate;
	}
	

	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse("2020-01-01");
		System.out.println(DateUtil.rollDate(date, -30));
		System.out.println(DateUtil.rollDate(date, 30));
		System.out.println(DateUtil.rollDate(date, 364));
	}
}
