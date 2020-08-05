package com.capitalbio.auth.util;

public class Constant {
	public static final String SECRET_KEY="bioehEquipmentASEjk170222";
//	public static final String MD5_KEY="bioehEquipmentdj1723";
	/**
	 * jwt
	 */
	public static final String JWT_ID = "jwt";
//	public static final String JWT_SECRET = "bioehEquipmentASEjk170222";
	public static final String JWT_SECRET = "bioeh-e28f-45f5-8414-d9ce660e800b";
	public static final int JWT_EXPIRE = 20*60*1000;
//	public static final int JWT_TTL = 60*60*1000;  //millisecond
	public static final int JWT_REFRESH_INTERVAL = 55*60*1000;  //millisecond
	public static final int JWT_REFRESH_TTL = 12*60*60*1000;  //millisecond
	
	/**
	 * 8小时的验证
	 */
	public static final int JWT_TTL = 8*60*60*1000;  
	/**
	 * 服务校正时间，多1小时误差
	 */
	public static final int VALDAT_JWT_TTL = (8+1)*60*60*1000;  
	
	public static final String DATA_SOURCE_CS = "初筛"; 
	
	public static final String DATA_SOURCE_JS = "精筛"; 

	
}