package com.capitalbio.pdf.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.capitalbio.healthcheck.uploaddata.FileMgr;
import com.capitalbio.healthcheck.uploaddata.Util;

/**
 * 获取项目中文件路径 工具类
 * 
 * @author admin
 *
 */
public class ProjectPathUtil {

	/**
	 * 获取生成pdf所需的文件路径
	 * 
	 * @return
	 */
	public static Map<String, Object> getPathGeneratePdf() {

		Map<String, Object> mapResult = new HashMap<String, Object>();
		
		
		/** 编译文件路径 **/
		File webClassesDir = getWebRootDir();
		/** 模板文件路径 **/
		File templateDir = new File(webClassesDir, "template");
		/** 项目根路径 **/
		File webRootDir = webClassesDir.getParentFile().getParentFile();
		File staticDir = new File(webRootDir,"src");
		staticDir = new File(staticDir,"main");
		staticDir = new File(staticDir,"webapp");
		staticDir = new File(staticDir,"static");
		/** 生成pdf文件路径 **/
		File pdfDir = new File(staticDir,"pdf");
		mapResult.put("webClassesDir", webClassesDir);
		mapResult.put("templateDir", templateDir);
		mapResult.put("webRootDir", webRootDir);
		mapResult.put("pdfDir", pdfDir);
		mapResult.put("staticDir", staticDir);
		return mapResult;
	}

	/**
	 * 获取项目的编译文件路径
	 * 
	 * @return
	 */
	public static File getWebRootDir() {
		String path = ProjectPathUtil.class.getClassLoader().getResource("").getPath();
		File file = new File(path);

		return file;
	}
}
