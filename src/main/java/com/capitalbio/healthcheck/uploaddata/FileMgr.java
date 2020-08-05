package com.capitalbio.healthcheck.uploaddata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;

public class FileMgr {
	/**
	 * 记录上次导入数据时间
	 */
	public void downLastImportDate(){
		String filePathStr = getPath();
		String fileName = filePathStr+"\\importTime.txt";
		String content = Util.formatDate(new Date(), "");
		
		try {
		   File file = new File(fileName);
		   if (!file.exists()) {
			   file.createNewFile();
		   }
		   
		   FileWriter writer = new FileWriter(fileName, true);  
		   writer.write(content+"\n");  
		   writer.close();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}
	
	/**
	 * 读取记录最后一次导出数据日期文件的最后一行
	 * @return
	 */
	public String readLastLine() {
		String fileName = getPath()+"\\importTime.txt";
		String charset = "utf-8";
		File file = new File(fileName);
		  if (!file.exists() || file.isDirectory() || !file.canRead()) {  
		    return "";  
		  }  
		  RandomAccessFile raf = null;  
		  try {  
		    raf = new RandomAccessFile(file, "r");  
		    long len = raf.length();  
		    if (len == 0L) {  
		      return "";  
		    } else {  
		      long pos = len - 1;  
		      while (pos > 0) {  
		        pos--;  
		        raf.seek(pos);  
		        if (raf.readByte() == '\n') {  
		          break;  
		        }  
		      }  
		      if (pos == 0) {  
		        raf.seek(0);  
		      }  
		      byte[] bytes = new byte[(int) (len - pos)];  
		      raf.read(bytes);  
		      if (charset == null) {  
		        return new String(bytes).trim();  
		      } else {  
		        return new String(bytes, charset).trim();  
		      }  
		    }  
		  } catch (Exception e) {  
		  } finally {  
		    if (raf != null) {  
		      try {  
		        raf.close();  
		      } catch (Exception e2) {  
		      }  
		    }  
		  }  
		  return null;  
		}
	
	/**
	 * 获取项目运行路径
	 * @return
	 */
	public static String getPath() {
		String relativelyPath = System.getProperty("user.dir"); 
		String url = relativelyPath;
		
		return url;
	}
	
	public static void writeLog(String log) {
		String rootPath = getPath();
		String date = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
		StringBuffer path = new StringBuffer();
		path.append(rootPath).append("\\logs");
		
		try {
			File fileDir = new File(path.toString());
			if (!fileDir.exists()) {
				fileDir.mkdir();
			}
			
			String filePath = path.append("\\sendData_").append(date).append(".log").toString();
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			
            Long filelength = file.length();  
            byte[] filecontent = new byte[filelength.intValue()];
            FileInputStream in = new FileInputStream(file);  
            in.read(filecontent);  
            in.close();  
            String strToal = new String(filecontent);
            
            String dateTime = Util.formatDate(new Date(), "");
            
            strToal = strToal + (dateTime + " " + log);
            in.close();
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(strToal+"\r\n");
            out.flush();
            out.close();
		} catch (Exception e) {
			System.out.print(e.getMessage());
			e.printStackTrace();
		}
		
	}
}
