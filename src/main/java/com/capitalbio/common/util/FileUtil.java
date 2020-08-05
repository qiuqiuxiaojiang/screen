package com.capitalbio.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.pdf.codec.Base64.InputStream;

public class FileUtil {
	// 删除文件夹
	// param folderPath 文件夹完整绝对路径

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}
	
	public static List<String> LocalFileToList(String name) {
		List<String> list =new ArrayList<String>();
		
		BufferedReader br = null;
		try {
			InputStream resourceAsStream = (InputStream) FileUtil.class.getClassLoader().getResourceAsStream(name);
			br = new BufferedReader(new InputStreamReader(resourceAsStream,"UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/***
	 * hpo文件：hpoid，英文名称，中文名称
	 * 
	 * @return hpoid，英文名称，中文名称的组合
	 * 
	 */
	public static Map<String, Object> LocalFileToMap(String name) {
		Map<String, Object> subclassMap = new LinkedHashMap<String, Object>();
		BufferedReader br = null;
		try {
			InputStream resourceAsStream = (InputStream) FileUtil.class.getClassLoader().getResourceAsStream(name);
			br = new BufferedReader(new InputStreamReader(resourceAsStream,"UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] split = line.split("\t",2);
				subclassMap.put(split[0], split[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return subclassMap;
	}
}
