package com.capitalbio.common.service;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.FilesManageDao;
import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.log.ServiceLog;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.util.ImageUtil;
import com.capitalbio.common.util.MD5Util;
import com.google.common.collect.Maps;
import com.mongodb.gridfs.GridFSDBFile;

@Service
public class FilesManageService extends BaseService {

	@Autowired FilesManageDao filesManageDao;
	
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return filesManageDao;
	}

	@Override
	public String getCollName() {
		return "fs";
	}

	@ServiceLog
	public String uploadFileToFs(File file,Map<String,Object> map,String uploadType) throws Exception{
		String fileName = file.getName();
		String ext = getExtension(fileName);
	    String upExt = ext.toUpperCase();
	    byte[] bytes = null;
	    if ("JPG".equals(upExt) || "GIF".equals(upExt) || "PNG".equals(upExt) || "BMP".equals(upExt) || "JPEG".equals(upExt)) {
	    	BufferedImage image =ImageIO.read(file); 
			BufferedImage parseBi=ImageUtil.rize(image,150,100);
	        Graphics graphics = parseBi.getGraphics(); 
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        ImageIO.write(parseBi,ext, out); 
	        //释放资源  
	        graphics.dispose();
	        bytes=out.toByteArray();
	    }
		
		return filesManageDao.uploadFilesToFs(file,fileName, map, ext, uploadType, bytes);
	}

	@ServiceLog
	public String uploadFileToFs(File file,String username,String uploadType) throws Exception{
		Map<String,Object> inputFile = Maps.newHashMap();
		String fileName = file.getName();
		inputFile.put("filename", fileName);
		inputFile.put("username",username);
		inputFile.put("uploadDate",new Date());
		String ext = getExtension(fileName);
	    inputFile.put("contentType", ext);
	    inputFile.put("uploadType",uploadType);
	    String upExt = ext.toUpperCase();
	    byte[] bytes = null;
	    if ("JPG".equals(upExt) || "GIF".equals(upExt) || "PNG".equals(upExt) || "BMP".equals(upExt) || "JPEG".equals(upExt)) {
	    	BufferedImage image =ImageIO.read(file); 
			BufferedImage parseBi=ImageUtil.rize(image,150,100);
	        Graphics graphics = parseBi.getGraphics(); 
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        ImageIO.write(parseBi,ext, out); 
	        //释放资源  
	        graphics.dispose();
	        bytes=out.toByteArray();
	        inputFile.put("imgByte",bytes);
	    }
		
		return filesManageDao.uploadFilesToFs(file,fileName, username, ext, uploadType, bytes);
	}
	
	@ServiceLog
	public String uploadFileToFs(InputStream inputStream,String fileName,String uploadType) throws Exception{
		String ext = getExtension(fileName);
	    String upExt = ext.toUpperCase();
	    byte[] bytes = null;
	    if ("JPG".equals(upExt) || "GIF".equals(upExt) || "PNG".equals(upExt) || "BMP".equals(upExt) || "JPEG".equals(upExt)) {
	    	BufferedImage image =ImageIO.read(inputStream); 
			BufferedImage parseBi=ImageUtil.rize(image,150,100);
	        Graphics graphics = parseBi.getGraphics(); 
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        ImageIO.write(parseBi,ext, out); 
	        //释放资源  
	        graphics.dispose();
	        bytes=out.toByteArray();
	    }
		
		return filesManageDao.uploadFilesToFs(inputStream,fileName, ext, uploadType, bytes);
	}
	
	private String getExtension(String filename) {
		int index = filename.lastIndexOf(".");
		if (index >= 0 && index < filename.length()) {
			String ext = filename.substring(index+1);
			return ext;
		}
		return "";
	}

	@ServiceLog
	public String uploadFileToFs(byte[] bytes, String fileName,String username,String uploadType) throws Exception{
		Map<String,Object> inputFile = Maps.newHashMap();
		inputFile.put("filename", fileName);
		inputFile.put("username",username);
		inputFile.put("uploadDate",new Date());
		String ext = getExtension(fileName);
	    inputFile.put("contentType", ext);
	    inputFile.put("uploadType",uploadType);
	    String upExt = ext.toUpperCase();
	    byte[] imgBytes = null;
	    if ("JPG".equals(upExt) || "GIF".equals(upExt) || "PNG".equals(upExt) || "BMP".equals(upExt) || "JPEG".equals(upExt)) {
	    	BufferedImage image =ImageIO.read(new ByteArrayInputStream(bytes)); 
			BufferedImage parseBi=ImageUtil.rize(image,150,100);
	        Graphics graphics = parseBi.getGraphics(); 
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        ImageIO.write(parseBi,ext, out); 
	        //释放资源  
	        graphics.dispose();
	        imgBytes=out.toByteArray();
	        inputFile.put("imgByte",imgBytes);
	    }
		
		return filesManageDao.uploadFilesToFs(bytes,fileName, username, ext, uploadType, imgBytes);
	}

	
//	public void delFileById(String id){
//		filesManageDao.delFileById(id,getCollName());
//	}
	
	public void delFileByQuery(Map<String, Object> query){
		filesManageDao.deleteFileByQuery(getCollName(), query);
	}
	
	public void deleteFileByQuery(Map<String,Object> query) {
		filesManageDao.deleteFileByQuery(getCollName(),query);
	}
	
	public GridFSDBFile getFileByFs(Map<String,Object> query,String gridFS){
		return filesManageDao.getFileByFs(query,gridFS);
	}
	
	public GridFSDBFile getFileById(String id){
		return filesManageDao.getFileById(getCollName(),id);
	}
	
	
	public List<Map<String,Object>> allFiles(String gridFS,Map<String,Object> queryMap,Page page) throws Exception{
		if (page.isAutoCount()) {
			long totalCount = filesManageDao.count("fs",queryMap);
			page.setTotalCount(totalCount);
		}
		return filesManageDao.allFileList(gridFS,page,queryMap,null);
	}
	
	public List<GridFSDBFile> allFileInfo(String gridFS,Page page,String type,String filename){
		Map<String,Object> query=Maps.newHashMap();
		if(StringUtils.isNotEmpty(type)){
			query.put("uploadType", type);
		}
		if(StringUtils.isNotEmpty(filename)){
			query.put("filename",filename);
		}
		return filesManageDao.allFileInfo(gridFS,page,query);
	}
	
	public List<Map<String,Object>> allFileList(String gridFS,Page page,Map<String,Object> queryMap,Map<String,Object> sortMap){
		if (page.isAutoCount()) {
			long totalCount = filesManageDao.count("fs",queryMap);
			page.setTotalCount(totalCount);
		}
		return filesManageDao.allFileList(gridFS,page,queryMap,sortMap);
	}
	
	public long count(String collName,Map<String,Object> queryMap){
		return filesManageDao.count(collName,queryMap);
	}
	
	public List<String> getFileTypeList(String gridFS,String username){
		Map<String,Object> query=Maps.newHashMap();
		query.put("username",username);
		return filesManageDao.getFileTypeList(gridFS,query);
	}
	
	public List<Map<String,Object>> userAllFiles(String gridFS,Map<String,Object> queryMap){
		return filesManageDao.userAllFiles(gridFS,queryMap);
	}
	
	public String fileIsExist(byte[] uploadFileByte,Map<String,Object> query) throws Exception{
        String MD5Content=MD5Util.getMD5String(uploadFileByte);//获得文件的md5值
        List<Map<String,Object>> fileList=userAllFiles("fs",query);
        for (Map<String, Object> file : fileList) {
			if(((String)file.get("md5")).equals(MD5Content)){
				String fileId=file.get("id").toString();
				String filename=file.get("filename").toString();
				Map<String,Object> queryFileInfoMap=Maps.newHashMap();
				queryFileInfoMap.put("fileId", new ObjectId(fileId));
				Map<String,Object> fileInfoMap=getMongoBaseDAO().getDataByQuery(getCollName(), queryFileInfoMap);
				String fileInfoId=fileInfoMap.get("id").toString();
				return filename+","+fileId+","+fileInfoId;
			}
		}
		return "";
	}
	
	public List<GridFSDBFile> allUserFileInfo(String collName, Map<String,Object> userMap) {
		return filesManageDao.allUserFileInfo(collName, userMap);
	}
	public byte[] getBytesFromFile(File file) {  
        byte[] ret = null;  
        try {  
            FileInputStream in = new FileInputStream(file);  
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);  
            byte[] b = new byte[1024];  
            int n;  
            while ((n = in.read(b)) != -1) {  
                out.write(b, 0, n);  
            }  
            in.close();  
            out.close();  
            ret = out.toByteArray();  
        } catch (IOException e) {  
            // log.error("helper:get bytes from file process error!");  
            e.printStackTrace();  
        }  
        return ret;  
	}  
	
	
}
