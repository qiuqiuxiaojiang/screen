package com.capitalbio.healthcheck.service;

public class TizhiParse {
	
	public static int PINGHE = 90;
	public static int QIXU = 80;
	public static int YANGXU = 70;
	public static int YINXU = 60;
	public static int XUEYU = 50;
	public static int TANSHI = 40;
	public static int SHIRE = 30;
	public static int QIYU = 20;
	public static int TEBING = 10;
	
	public static int getTizhiNum(String tizhi) {
		int num = 0;
		if (tizhi.equals("平和质")) {
			num = 90;
		} else if (tizhi.equals("气虚质")) {
			num = 80;
		} else if (tizhi.equals("阳虚质")) {
			num = 70;
		} else if (tizhi.equals("阴虚质")) {
			num = 60;
		} else if (tizhi.equals("血瘀质")) {
			num = 50;
		} else if (tizhi.equals("痰湿质")) {
			num = 40;
		} else if (tizhi.equals("湿热质")) {
			num = 30;
		} else if (tizhi.equals("气郁质")) {
			num = 20;
		} else if (tizhi.equals("特禀质")) {
			num = 10;
		} 
		return num;
	}

}
