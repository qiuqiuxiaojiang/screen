package com.capitalbio.pdf.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * 字符串工具类
 * @author letianwang-n
 *
 */
public class StringUtil{

	public static String parseParamsToStr(Map<String,Object> params){
		Map<String, Object> m=params;
		if(m.size()==0){
			return "";
		}
    	Iterator iterator=m.entrySet().iterator();
    	Map<String, Object> result=new HashMap<String, Object>();
    	StringBuffer str=new StringBuffer();
    	while (iterator.hasNext()) {
    		Map.Entry entry =(Map.Entry) iterator.next();
			String[] values=(String[]) m.get(entry.getKey());
			if(values.length==1){
				str.append(entry.getKey().toString()+"="+values[0]+"&");
			}else{
				str.append(entry.getKey().toString().replace("[]", "")+"=");
				for(String s:values){
					str.append(s+";");
				}
				str.append("&");
			}
		}
    	return str.substring(0,str.lastIndexOf("&"));
	}
	
	/**
	 * 判断后一个字符串中的每一个字母是否在前一个字符串中出现
	 * @param front
	 * @param back
	 * @return
	 */
	public static boolean strEqualsArray(String front,String back){
		for(int i=0;i<back.length();i++){
			String tmp=back.substring(i, i+1);
			if(front.indexOf(tmp)==-1){
				return false;
			}
		}
		return true;
	}
	/**
	 * 判断后一个字符串中的每一个字母是否在前一个字符串中出现
	 * @param front
	 * @param back
	 * @return
	 */
	public static boolean strEqualsArrays(String front,String back){
		String [] f=front.split("/");
		if(back.contains(".")){
			String [] b=back.split("\\.");
			for(int i=0;i<b.length;i++){
				if(!f[0].equals(b[i]) && !f[1].equals(b[i])){
					return false;
				}
			}
		}else{
			if(!f[0].equals(back) && !f[1].equals(back) && !(f[0]+f[0]).equals(back) && !(f[1]+f[1]).equals(back) && !(f[0]+f[1]).equals(back) && !(f[1]+f[0]).equals(back)){
				return false;
			}
		}
		return true;
	}
	/**
	 * 转换字符A变T，C变G
	 * @return
	 */
	public static String change(String str){
		String change="";
		if("A".equals(str)){
			change = "T";
		}else if("T".equals(str)){
			change = "A";
		}else if("C".equals(str)){
			change = "G";
		}else if("G".equals(str)){
			change = "C";
		}
		return change;
	}
	
	/* 
	* @return true---是Windows操作系统
	*/
	public static boolean isWindowsOS(){
	    boolean isWindowsOS = false;
	    String osName = System.getProperty("os.name");
	    if(osName.toLowerCase().indexOf("windows")>-1){
	      isWindowsOS = true;
	    }
	    return isWindowsOS;
	 }
	/**
	 * 返回数组
	 * @param str 字符串
	 * @param string 要截取的字符 多个
	 * @return
	 */
	public static String[] splitByMoreStr(String str,String string){
		if(str.indexOf(string)==-1){
			//没有这个字符串
			String[] array = {str};
			return array;
		}
		//存在这个字符串
		List<String> listTmp = substringByMoreStr(str, string);
		String[] strings = new String[listTmp.size()];
		listTmp.toArray(strings);
		return strings;
	}
	/**
	 * 使用递归 多次分割
	 * @param str 字符串
	 * @param string 要截取的字符 多个
	 * @return
	 */
	private static List<String> substringByMoreStr(String str,String string){
		List<String> resultList = new ArrayList<String>();
		int index = str.indexOf(string);
		String subFirst = str.substring(0, index);
		String subSecond = str.substring(index+string.length(), str.length());
		if(subFirst.indexOf(string)!=-1){
			List<String> listFirst = substringByMoreStr(subFirst,string);
			resultList.addAll(listFirst);
		}else{
			resultList.add(subFirst);
		}
		if(subSecond.indexOf(string)!=-1){
			List<String> listSecond = substringByMoreStr(subSecond,string);
			resultList.addAll(listSecond);
		}else{
			resultList.add(subSecond);
		}
		return resultList;
	}
	
	
	public static void main(String[] args) {
	
//		System.out.println(StringUtil.strEqualsArrays("A/C","C"));
		String str="是高2+度保守2+的依2+赖于Zn2+的蛋白";
		String string="2\\+";
//		List<String> list = substringByMoreStr( str,string);
//		String[] strings = new String[list.size()];
//		list.toArray(strings);
//		for(int i=0;i<strings.length;i++){
//			System.out.println(strings[i]);
//		}
		System.out.println(str.indexOf(string));
		String[] split = str.split(string);
		for(int i=0;i<split.length;i++){
			System.out.println(split[i]);
		}
	}
}
