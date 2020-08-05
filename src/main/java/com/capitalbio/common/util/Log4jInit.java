package com.capitalbio.common.util;


import java.io.File;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class Log4jInit extends HttpServlet {

	static Logger logger = Logger.getLogger(Log4jInit.class);  
	  
    public Log4jInit() {  
    }  
  
    
    public void init(){
    	//读取项目的路径
    	String prefix = this.getServletConfig().getServletContext().getRealPath("/");
    	//读取log4j相对路径
    	String file = this.getServletConfig().getInitParameter("log4j");
    	String filePath = prefix + file;
    	
    	ResourceBundle config=ResourceBundle.getBundle("config");
		String path = config.getString("system.temp.dir");
		if (path == null) {
			path = new File(prefix, "logs").getAbsolutePath();
		}
		File fileNew = new File(path);
		if (!fileNew.exists()) {
			fileNew.mkdirs();
		}
    	
    	System.setProperty("path", fileNew.getAbsolutePath());
    	DOMConfigurator.configure(filePath);//加载xml文件
    	

    }
    public static void toPrint(String content) {  
        System.out.println(content);  
    }
}
