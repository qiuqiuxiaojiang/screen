package com.capitalbio.common.util;

import java.util.Comparator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class MapComparator implements Comparator<Map<String, Object>>{

	@Override
	public int compare(Map<String, Object> map1, Map<String, Object> map2) {
		if ((map1.get("seq") != null || map1.get("seq") != "") && (map2.get("seq") != null || map2.get("seq") != "") ) {
			
			if (isInteger(map1.get("seq").toString()) && isInteger(map2.get("seq").toString())) {
				int value1 = Integer.parseInt(map1.get("seq").toString());
				int value2 = Integer.parseInt(map2.get("seq").toString());
				int value = value1 - value2;
				if (value > 0) {
					return 1;
				} else if (value == 0) {
					return 0;
				} else {
					return -1;
				}
			}
		}
		return 0;
	}
	
	public static boolean isInteger(String str) {  
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
        return pattern.matcher(str).matches();  
	}

}
