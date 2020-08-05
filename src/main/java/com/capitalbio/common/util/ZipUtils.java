package com.capitalbio.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 压缩工具类
 */
public class ZipUtils {
	private static Logger logger = LoggerFactory.getLogger(ZipUtils.class); 
	
	/**
	 * 压缩多个文件到指定的压缩文件
	 * @param zipFilePath 压缩文件的绝对路径
	 * @param zipedFilePaths 
	 * @return 压缩文件的绝对路径
	 */
	public static String zipFiles(String zipFilePath,String... zipedFilePaths) {
		try {
			ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFilePath))); // 压缩文件的绝对路径

			for(String path : zipedFilePaths) {
				File file = new File(path);
				
				zipOut.putNextEntry(new ZipEntry(file.getName())); // 每个被压缩文件名称
				
				if(!file.isDirectory()) IOUtils.copy(new FileInputStream(file), zipOut);
				
				zipOut.closeEntry(); // 关闭当前zip entry且将流重置到写下个文件的位置
			}
			zipOut.flush(); // 刷流到硬盘上
			zipOut.close(); // 关闭流
			
			return zipFilePath; 
		} catch (Exception e) {
			logger.error("打zip包出错! ",e);
			return null;
		}
	}
 
	     
	    /**
	     * 压缩文件夹内的文件 
	     * @param destDir 压缩文件目录
	     * @param fromDir 需要压缩的文件夹名
	     * @param 文件后缀
	     * @return zip文件路径
	     */
	    public static String zipDirectory(String destDir,String fromDir,String... suffix){ 
	    	try{
		        File zipDir = new File(fromDir); 
		        
		        String zipFileName = zipDir.getName();
		        if(ArrayUtils.isNotEmpty(suffix)) {
		        	for(String s : suffix) {
		        		zipFileName += s;
		        	}
		        }else {
		        	zipFileName = zipDir.getName() + ".zip"; 
		        }
		        zipFileName = destDir + File.separator + zipFileName;
		        
		        ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFileName))); 
		        
	            recursionZipDir(zipDir.getPath(), zipDir , zipOut); 
	            
	            zipOut.flush();
	            zipOut.close(); 
	            return zipFileName;
	        }catch(Exception e){ 
	            logger.error("压缩异常!",e);
	            return null;
	        } 
	    } 

	    /**
	     * 由doZip调用,递归完成目录文件读取 
	     * @param zipDir 要压缩的文件夹
	     * @param zipOut 压缩输出流
	     */
	    public static void recursionZipDir(String basePath,File zipDir , ZipOutputStream zipOut) throws IOException{ 
	    	if(zipDir.isDirectory()) {
	    		File[] zipFiles = zipDir.listFiles(); // 文件夹下的文件
	    		String relativePath = null;
	    		if(zipFiles.length ==0 ) { // 仅仅创建文件夹
	    			relativePath = URLDecoder.decode(StringUtils.removeStartIgnoreCase(zipDir.getPath(), basePath + File.separator) , "UTF-8");
	    			zipOut.putNextEntry(new ZipEntry(relativePath)); // ZipEntry判断是否为目录isDirectory()方法中,目录以"/"结尾.
	    			logger.debug(relativePath);
		            zipOut.closeEntry();
	    		}else {
	    			for(File zipFile : zipFiles) { 
		                if(zipFile.isDirectory()){ 
		                    recursionZipDir(basePath,zipFile , zipOut); 
		                } else { 
		                	relativePath = URLDecoder.decode(StringUtils.removeStartIgnoreCase(zipFile.getPath(), basePath + File.separator),"UTF-8");
		                	logger.debug(relativePath);
		                    zipOut.putNextEntry(new ZipEntry(relativePath)); 
		                    IOUtils.copy(new FileInputStream(zipFile), zipOut);
		                    zipOut.closeEntry(); 
		                }
		            }
	    		}
	    	}
	    }

	    /**
	     * 解压指定zip文件 
	     * @param unZipFileName 需要解压的zip文件名 
	     */
	    public static String unZip(String unzipDir,String unZipFileName){ 
	        try{ 
	        	ZipInputStream zipIn = new ZipInputStream (new BufferedInputStream(new FileInputStream(unZipFileName))); 
	        	ZipEntry zipEntry = null;
	            while((zipEntry = zipIn.getNextEntry()) != null){ 
	            	File file = new File(unzipDir + File.separator + zipEntry.getName()); 
	                if(zipEntry.isDirectory()){ 
	                    file.mkdirs(); 
	                } else { 
	                    File parent = file.getParentFile(); 
	                    if(!parent.exists()) { 
	                        parent.mkdirs(); // 如果指定文件的目录不存在,则创建之. 
	                    }
	                    FileOutputStream fileOut = new FileOutputStream(file); 
	                    IOUtils.copy(zipIn, fileOut);
	                    fileOut.flush();
	                    fileOut.close(); 
	                }
	                zipIn.closeEntry();     
	            }
	            return unzipDir;
	        }catch(IOException e){ 
	            logger.error("解压异常!",e);
	            return null;
	        } 
	    } 


}
