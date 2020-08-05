package com.capitalbio.pdf.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 数字处理类
 * @author wang
 *
 */
public class NumUtil {
	
	private NumUtil(){}
	
//	private static NumUtil numUtil;
	
	public static NumUtil getInstance(){
		return new NumUtil();
	}
	
	/**
	 * 返回double类型小数点后decNum位，参数值".00"
	 * @param decNum
	 * @param num
	 * @return
	 */
	public Double getDecimal(String decNum,Object num){
		DecimalFormat format=new DecimalFormat(decNum);
		return Double.valueOf(format.format(num));
	}
	
	/**
	 * 对double类型的数字 四舍五入后  取整数
	 * @param num
	 * @return
	 */
	public static String dealDoubleNumber(double num) {
		DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
		double rintNum = Math.rint(num);
		
		return decimalFormat.format(rintNum);
	}
	
	public String getBigDecimal(double num){
		BigDecimal bigDecimal=new BigDecimal(num);
		return bigDecimal.toPlainString();
	}
	 public static boolean isNumber(String str){  
	        String reg = "^[0-9]+(.[0-9]+)?$";  
	        return str.matches(reg);  
	    }  
	 
	 public static String addZeroToSingleDigit(int num){
		 if (num > 9 || num <1) {
			 return num+"";
		 } else {
			 
			 DecimalFormat df=new DecimalFormat("00");
		     String resultStr = df.format(num);
			 return resultStr;
		 }
		 
	 }
	 
	 /**
	     * 提供精确的加法运算。
	     * @param v1 被加数
	     * @param v2 加数
	     * @return 两个参数的和
	     */
	    public static double add(Object v1,Object v2){
	    	
	        BigDecimal b1 = new BigDecimal(v1.toString());
	        BigDecimal b2 = new BigDecimal(v2.toString());
	    	
	        return b1.add(b2).doubleValue();
	    }
	    /**
	     * 提供精确的减法运算。
	     * @param v1 被减数
	     * @param v2 减数
	     * @return 两个参数的差
	     */
	    public static double sub(Object v1,Object v2){
	    	BigDecimal b1 = new BigDecimal(v1.toString());
	        BigDecimal b2 = new BigDecimal(v2.toString());
	        return b1.subtract(b2).doubleValue();
	    } 
	    /**
	     * 提供精确的乘法运算。
	     * @param v1 被乘数
	     * @param v2 乘数
	     * @return 两个参数的积
	     */
	    public static double mul(Object v1,Object v2){
	    	BigDecimal b1 = new BigDecimal(v1.toString());
	        BigDecimal b2 = new BigDecimal(v2.toString());
	        return b1.multiply(b2).doubleValue();
	    }
	 
	 
	    /**
	     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
	     * 定精度，以后的数字四舍五入。
	     * @param v1 被除数
	     * @param v2 除数
	     * @param scale 表示表示需要精确到小数点以后几位。
	     * @return 两个参数的商
	     */
	    public static double div(Object v1,Object v2,int scale){
	        if(scale<0){
	            throw new IllegalArgumentException(
	                "The scale must be a positive integer or zero");
	        }
	        BigDecimal b1 = new BigDecimal(v1.toString());
	        BigDecimal b2 = new BigDecimal(v2.toString());
	        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	    }
	 
	    /**
	     * 提供精确的小数位四舍五入处理。
	     * @param v 需要四舍五入的数字
	     * @param scale 小数点后保留几位
	     * @return 四舍五入后的结果
	     */
	    public static double round(Object v,int scale){
	        if(scale<0){
	            throw new IllegalArgumentException(
	                "The scale must be a positive integer or zero");
	        }
	        BigDecimal b = new BigDecimal(v.toString());
	        BigDecimal one = new BigDecimal("1");
	        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	    }
}