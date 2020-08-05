package com.capitalbio.common.util;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

public class PinyinUtils {

    public static void main(String[] args) throws Exception{
        String str = "核前群";
        System.out.println(PinyinHelper.convertToPinyinString(str, ""));
        System.out.println(PinyinHelper.getShortPinyin(str));
        System.out.println(getPYFirst(str));
        System.out.println(getPY(str));
//        System.out.println(getPYFirstAllEn(str));
//        System.out.println(getPY(str,"'"));
//        System.out.println(getPY(str,"."));
    }
    /**
     * 返回字符串的拼音大写首字母，返回完整的英文字符串
     * @param str
     * @return
     */
    public static String getPYFirst(String str) {
    	if (str==null||str.length() == 0) {
    		return "";
    	}
    	String s = "";
    	try {
    		s = PinyinHelper.getShortPinyin(str).toUpperCase();
    	} catch (Exception e) {}
    	return s;
    }
    
    /**
     * 返回字符串的拼音(不带声调)，返回完整的英文字符串
     * @param str
     * @return
     */
    public static String getPY(String str) {
    	if (str==null||str.length() == 0) {
    		return "";
    	}
    	String s = "";
    	try {
    		s = PinyinHelper.convertToPinyinString(str, "", PinyinFormat.WITHOUT_TONE);
    	} catch (Exception e) {}
    	return s;
    	
    }

}
