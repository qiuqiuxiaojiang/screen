package com.capitalbio.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 日期时间工具类
 */
public class DateUtils {

	/**
	 * 一天的毫秒数
	 */
	public static final long DAY_MS = 24 * 60 * 60 * 1000L;

	/**
	 * 日期格式，年-月-日
	 */
	public static final String PATTERN_DAY = "yyyy-MM-dd";

	/**
	 * 日期格式，月-日
	 */
	public static final String PATTERN_MONRH_DAY = "MM-dd";
	/**
	 * 日期格式，年-日
	 */
	public static final String PATTERN_YYMM = "yyyy-MM";

	/**
	 * 日期格式，年月日
	 */
	public static final String PATTERN_YYYYMMDD = "yyyyMMdd";

	/**
	 * 日期格式，年月日时分秒
	 */
	public static final String PATTERN_FULL = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 日期格式，年月日时分秒
	 */
	public static final String PATTERN_FULL2 = "yyyyMMddHHmmss";
	
	/**
	 * 日期格式，年月日时分
	 */
	public static final String PATTERN_FULL3 = "yyyy-MM-dd HH:mm";

	/**
	 * 分钟最小值
	 */
	public static final String MINUTE_MIN = " 00:00:00";

	/**
	 * 分钟最大值
	 */
	public static final String MINUTE_MAX = " 23:59:59";

	public static final DateUtil dateUtil = new DateUtil();

	/**
	 * 默认的Date常量 当前时间
	 */
	public static Date DEFAULT_DATE_VALUE;
	
	public static DateUtil getInstance() {
		return dateUtil;
	}

	private static Map<String, ThreadLocal<SimpleDateFormat>> threadLocalMap = new ConcurrentHashMap<String, ThreadLocal<SimpleDateFormat>>();

	private static SimpleDateFormat osdf = new SimpleDateFormat(PATTERN_FULL);

	public static SimpleDateFormat getSimpleDateFormat(String pattern) {
		ThreadLocal<SimpleDateFormat> threadLocal = threadLocalMap.get(pattern);
		if (threadLocal == null) {
			threadLocal = new ThreadLocal<SimpleDateFormat>();
			threadLocalMap.put(pattern, threadLocal);
		}
		SimpleDateFormat sdf = threadLocal.get();
		if (sdf == null) {
			sdf = new SimpleDateFormat(pattern);
			threadLocal.set(sdf);
			// System.out.println("new" + "|" + Thread.currentThread().getId());
		}
		// System.out.println(sdf + "|" + Thread.currentThread().getId());
		return sdf;
	}

	public static String formatDateWrong(Date date, String pattern) {
		if (date != null) {
			// SimpleDateFormat sdf = getSimpleDateFormat(pattern);
			return osdf.format(date);
		} else {
			return "";
		}
	}

	public static String formatDateMan(Date date, String pattern) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.format(date);
		} else {
			return "";
		}
	}

	/**
	 * 格式化日期（yyyy-MM-dd）
	 * @param date  日期
	 * @return 格式化日期字符串
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat sdf = getSimpleDateFormat(PATTERN_DAY);
		if (date != null) {
			return sdf.format(date);
		} else {
			return sdf.format(DEFAULT_DATE_VALUE);
		}
	}

	/**
	 * 格式化日期（指定格式）
	 * 
	 * @param date
	 *            日期
	 * @return 格式化日期字符串
	 */
	public static String formatDate(Date date, String pattern) {
		if (date != null) {
			SimpleDateFormat sdf = getSimpleDateFormat(pattern);
			return sdf.format(date);
		} else {
			return "";
		}
	}

	/**
	 * 格式化时间（yyyy-MM-dd HH:mm:ss）
	 * 
	 * @param date
	 *            时间
	 * @return 格式化时间字符串
	 */
	public static String formatTime(Date date) {
		if (date != null) {
			return formatDate(date, PATTERN_FULL);
		} else {
			return "";
		}
	}

	/**
	 * 将以字符串形式表示的日期转换为Date类型
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param pattern
	 *            格式
	 * @return 日期对象
	 * @throws Exception
	 */
	public static Date parseDate(String dateStr, String pattern) {
		try {
			SimpleDateFormat sdf = getSimpleDateFormat(pattern);
			return sdf.parse(dateStr);
		} catch (Exception e) {
			return DEFAULT_DATE_VALUE;
		}
	}

	/**
	 * 计算2个日期的差值（根据不同的时间单位计算出不同时间单位的差值）
	 * 
	 * @param start
	 * @param end
	 * @param unit
	 *            时间单位
	 * @return 时间差值（单位与unit参数单位相同.当end>start返回正值，否则返回负值）
	 */
	public static long between(Date start, Date end, TimeUnit unit) {

		if (start == null || end == null || unit == null) {
			return 0;
		}

		long duration = end.getTime() - start.getTime();
		return unit.convert(duration, TimeUnit.MILLISECONDS);
	}
	
	/**  
	 * <p>Title: getDifferenceBetweenTwoTimes</p>  
	 * <p>Description: 获取两个时间的时间戳差，单位秒</p>  
	 * @param startTime
	 * @param endTime
	 * @return  
	 * @author guohuiyang  
	 * Create Time: 2019-10-25 14:37:06<br/>  
	 */  
	public static long getDifferenceBetweenTwoTimes(Date startTime, Date endTime, TimeUnit unit){
		long duration = endTime.getTime() - startTime.getTime();
		return unit.convert(duration, unit);
	}
	
	/**  
	 * <p>Title: getDifferenceBetweenTwoTimes</p>  
	 * <p>Description: 获取两个时间的时间戳差，单位小时</p>  
	 * @param startTime
	 * @param endTime
	 * @return  
	 * @author guohuiyang  
	 * Create Time: 2019-10-25 14:47:29<br/>  
	 */  
	public static double getDifferenceBetweenTwoTimes(Date startTime, Date endTime){
		long duration = endTime.getTime() - startTime.getTime();
		TimeUnit unit = TimeUnit.MILLISECONDS;
		double convert = unit.convert(duration, unit);
		return convert / (1000 * 60 * 60);
	}

	/**
	 * 计算2个日期的天数
	 * 
	 * @param start
	 * @param end
	 * @param unit
	 *            时间单位
	 * @return 时间差值（单位与unit参数单位相同.当end>start返回正值，否则返回负值）
	 */
	public static long betweenDays(Date start, Date end, TimeUnit unit) {

		if (start == null || end == null) {
			return 0;
		}
		if (null == unit) {
			unit = TimeUnit.MILLISECONDS;
		}
		long duration = end.getTime() - start.getTime();
		return duration / (1000 * 60 * 60 * 24);
	}

	/**
	 * 计算时间是否是在指定的时间范围内
	 * 
	 * @author lierlin
	 * @param date
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isBetween(Date date, Date start, Date end) {
		if ((date.getTime() > start.getTime())
				&& (date.getTime() < end.getTime())) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 计算时间是否是在指定的时间范围内 包含边界值
	 * 
	 * @author lierlin
	 * @param date
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isBetweenAndEquals(Date date, Date start, Date end) {
	  if ((date.getTime() >= start.getTime())
	      && (date.getTime() <= end.getTime())) {
	    return true;
	  } else {
	    return false;
	  }
	}

	/**
     * 获取某日期区间的所有日期  日期正序
     *
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @param dateFormat 日期格式
     * @return 区间内所有日期
     */
    public static List<Date> getPerDaysByStartAndEndDate(String startDate, String endDate, String dateFormat) {
      DateFormat format = new SimpleDateFormat(dateFormat);
      try {
          Date sDate = format.parse(startDate);
          Date eDate = format.parse(endDate);
          long start = sDate.getTime();
          long end = eDate.getTime();
          if (start > end) {
              return null;
          }
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(sDate);
          List<Date> res = new ArrayList<Date>();
          while (end >= start) {
              res.add(calendar.getTime());
              calendar.add(Calendar.DAY_OF_MONTH, 1);
              start = calendar.getTimeInMillis();
          }
          return res;
      } catch (ParseException e) {
      }

      return null;
  }
    
    /**
     * 获取某日期区间的所有月份  月份正序
     *
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @param dateFormat 日期格式
     * @return 区间内所有月份
     */
    public static List<Date> getPerMonthsByStartAndEndDate(Date sDate, Date eDate) {
      try {
        long start = sDate.getTime();
        long end = eDate.getTime();
        if (start > end) {
          return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sDate);
        List<Date> res = new ArrayList<Date>();
        while (end >= start) {
          res.add(calendar.getTime());
          calendar.add(Calendar.MONTH, 1);
          start = calendar.getTimeInMillis();
        }
        return res;
      } catch (Exception e) {
      }
      return null;
    }
    
    
	/**
	 * 获取一天中的最早时间 00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstOfADay(String date) throws Exception {
		return getFirstOfADay(parseDate(date, DateUtils.PATTERN_DAY));
	}

	/**
	 * 获取一天中的最晚时间 23:59:59
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEndOfADay(String date) throws Exception {
		return getEndOfADay(parseDate(date, DateUtils.PATTERN_DAY));
	}

	/**
	 * 获取一天中的最早时间 00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstOfADay(Date date) throws Exception {
		SimpleDateFormat sdf = getSimpleDateFormat(PATTERN_DAY);
		return getSimpleDateFormat(PATTERN_FULL).parse(
				sdf.format(date) + MINUTE_MIN);
	}

	/**
	 * 获取一天中的最晚时间 23:59:59
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEndOfADay(Date date) throws Exception {
		SimpleDateFormat sdf = getSimpleDateFormat(PATTERN_DAY);
		return getSimpleDateFormat(PATTERN_FULL).parse(
				sdf.format(date) + MINUTE_MAX);
	}

	/**
	 * 获取一周中的最早时间 00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstOfAWeek(Date date) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return getFirstOfADay(calendar.getTime());
	}

	/**
	 * 获取一周中的最晚时间 23:59:59
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEndOfAWeek(Date date) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendar.add(Calendar.DATE, 7); // 依照中国的习惯，获取到周末以后必须往后推一个星期
		return getEndOfADay(calendar.getTime());
	}

	/**
	 * 获取一个月中的最早时间 00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstOfAMonth(Date date) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, 1);
		return getFirstOfADay(calendar.getTime());
	}

	/**
	 * 获取一个月中的最晚时间 23:59:59
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEndOfAMonth(Date date) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, 1);
		calendar.roll(Calendar.DATE, -1);
		return getEndOfADay(calendar.getTime());
	}

	/**
	 * 获取上个月的最早时间 00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstOfLastMonth(Date date) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		return getFirstOfAMonth(calendar.getTime());
	}

	/**
	 * 获取上个月中的最晚时间 23:59:59
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEndOfLastMonth(Date date) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		return getEndOfAMonth(calendar.getTime());
	}

	/**
	 * 按时间和偏移量获取季度名称
	 * 
	 * @param date
	 *            时间
	 * @param changeNum
	 *            季度偏移量，0表示本季度，-1表示上季度
	 * @return
	 */
	public static String getQuarterName(Date date, int changeNum) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, changeNum * 3);
		return getQuarterName(calendar.getTime());
	}

	public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }
	
	public static String getStrDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_FULL2);
		String str = sdf.format(date);
		return str;
	}
	
	/**
	 * 按时间获取季度名称
	 * 
	 * @param date
	 * @return
	 */
	public static String getQuarterName(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		switch (month) {
		case 0:
		case 1:
		case 2: {
			return year + " 第一季度";
		}
		case 3:
		case 4:
		case 5: {
			return year + " 第二季度";
		}
		case 6:
		case 7:
		case 8: {
			return year + " 第三季度";
		}
		case 9:
		case 10:
		case 11: {
			return year + " 第四季度";
		}
		default: {
			return year + "";
		}
		}
	}

	/**
	 * 获取两个时间的差的天数
	 */
	public static int dateSub(Date date1, Date date2) {
		return (int) Math.ceil((date1.getTime() - date2.getTime())
				/ (24 * 60 * 60 * 1000.0));
	}
	public static int getDateMin(Date endDate, Date nowDate) {
		 
	    long nm = 1000 * 60;
	    // long ns = 1000;
	    // 获得两个时间的毫秒时间差异
	    long diff = endDate.getTime() - nowDate.getTime();
	    // 计算差多少分钟
	    long min = diff / nm;
	    return Integer.parseInt(min+"");
	}

	/**
	 * 根据天数差值获取新的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateByAddDays(Date date, int days) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.add(Calendar.DAY_OF_MONTH, days);
		return calender.getTime();
	}
	
	/**
	 * 根据天数差值获取新的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateByAddMinute(Date date, int minutes) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.add(Calendar.MINUTE, minutes);
		return calender.getTime();
	}

	/**
	 * 根据当前日期和天数差值获取新的日期
	 * 
	 * @param days
	 * @return
	 */
	public static Date getDateByAddDays(int days) {
		Date date = new Date();
		return getDateByAddDays(date, days);
	}

	/**
	 * 根据天数差值获取新的日期格式化字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateStrByAddDays(Date date, int days) {
		Date newDate = getDateByAddDays(date, days);
		return formatDate(newDate);
	}

	/**
	 * 根据当前日期和天数差值获取新的日期格式化字符串
	 * 
	 * @param days
	 * @return
	 */
	public static String getDateStrByAddDays(int days) {
		Date newDate = getDateByAddDays(days);
		return formatDate(newDate);
	}

	public static List<String> getDateStrs(Date startDate, Date endDate,
			String pattern) {
		List<String> dates = new ArrayList<String>();
		for (int i = 0;; i++) {
			Date date = DateUtils.getDateByAddDays(startDate, i);
			if (date.compareTo(endDate) <= 0) {
				dates.add(DateUtils.formatDate(date, pattern));
			} else {
				break;
			}
		}
		return dates;
	}

	public static Date parseUTCDate(String utcTime) {
		utcTime = utcTime.replace("Z", " UTC");
		return parseDate(utcTime, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}

	public static Date getDateAddMonth(Date date, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, month);
		return calendar.getTime();
	}

	/**
	 * 比较两个时间大小
	 * 开始时间大于结束时间返回1
	 * 开始时间小于结束时间返回-1
	 * 开始时间等于结束时间返回0
	 * （需JDK8以上）
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static int dateCompareTo(Date startDate,Date endDate) throws Exception{
		LocalDateTime localDateTime1 = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
		LocalDateTime localDateTime2 =  LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
		if(localDateTime1.isBefore(localDateTime2)){
			return 1;
		}else if(localDateTime1.isAfter(localDateTime2)){
			return -1;
		}else if(localDateTime1.equals(localDateTime2)){
			return 0;
		}else {
			throw new Exception("时间比较错误");
		}
	}
	
	public static  int getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance(); 
        if (cal.before(birthDay)) { //出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(birthDay); 
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);  
        int age = yearNow - yearBirth;   //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;//当前日期在生日之前，年龄减一
            }else{
                age--;//当前月份在生日之前，年龄减一
            } 
        } 
        return age; 
	}
	
	public static void main(String[] args) {
	  try {
        Date start = parseDate("2019-10-15", PATTERN_DAY);
        Date end = parseDate("2019-10-21", PATTERN_DAY);
        long between = between(start, end, null);
        long betweenDays = betweenDays(start, end, null);
        System.out.println(between);
        System.out.println(betweenDays);
    } catch (Exception e) {
      e.printStackTrace();
    }
    }
	
}

class Tt extends Thread {

	long t;

	public Tt(long _t) {
		this.t = _t;
	}

	@Override
	public void run() {
		final int SIZE = 100;
		Date d = new Date(t);
		System.out.println("t_st|" + Thread.currentThread().getId());
		long st = System.currentTimeMillis();
		String[] strsw = new String[SIZE];
		for (int i = 0; i < SIZE; i++) {
			strsw[i] = DateUtils.formatDateWrong(d, DateUtils.PATTERN_FULL);
		}
		long w = System.currentTimeMillis();
		String[] strsn = new String[SIZE];
		for (int i = 0; i < SIZE; i++) {
			strsn[i] = DateUtils.formatDate(d, DateUtils.PATTERN_FULL);
		}
		long n = System.currentTimeMillis();
		String[] strsm = new String[SIZE];
		for (int i = 0; i < SIZE; i++) {
			strsm[i] = DateUtils.formatDateMan(d, DateUtils.PATTERN_FULL);
		}
		long m = System.currentTimeMillis();

		// 判断正确错误 :是否一个数组内所有值都跟第一个相同
		boolean bw = true, bn = true, bm = true;
		for (int i = 1; i < SIZE; i++) {
			if (bw && !strsw[i].equals(strsw[0])) {
				bw = false;
			}
			if (bn && !strsn[i].equals(strsn[0])) {
				bn = false;
			}
			if (bm && !strsm[i].equals(strsm[0])) {
				bm = false;
			}
		}
		System.out.println("w is right:" + bw);
		System.out.println("n is right:" + bn);
		System.out.println("m is right:" + bm);
		// 速度
		System.out.println("w use time:" + (w - st));
		System.out.println("n use time:" + (n - w));
		System.out.println("m use time:" + (m - n));

	}
}
