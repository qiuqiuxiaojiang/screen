package com.capitalbio.common.util;

public class XssCleanUtil {
	
	 public static String xssClean(String value) { 
        if (value == null || value.isEmpty()) { 
            return value; 
        } 
//	        value = StringUtil.UrlDecode(value, "UTF-8");
//	        value = HtmlUtils.htmlEscape(value);
         
        value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        value = value.replaceAll("'", "& #39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
         
        StringBuilder sb = new StringBuilder(value.length() + 16); 
        for (int i = 0; i < value.length(); i++) { 
            char c = value.charAt(i); 
            switch (c) { 
            case '>': 
                sb.append("＞");// 转义大于号  
                break; 
            case '<': 
                sb.append("＜");// 转义小于号  
                break; 
            case '\'': 
                sb.append("＇");// 转义单引号  
                break; 
            case '\"': 
                sb.append("＂");// 转义双引号  
                break; 
            case '&': 
                sb.append("＆");// 转义&  
                break; 
            default: 
                sb.append(c); 
                break; 
            } 
        } 
        
        return sb.toString(); 
    }

}
