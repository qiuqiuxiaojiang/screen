package com.capitalbio.common.dao;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.capitalbio.common.model.Page;
import com.capitalbio.common.mongo.MongoDBFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

@Repository
public class FilesManageDao extends MongoBaseDAO {

	public String uploadFilesToFs(File file, String fileName, Map<String,Object> map, String ext, String uploadType, byte[] bytes) throws Exception{
		//保存文件到gridfs
		DB db = MongoDBFactory.getDB();
	    GridFS myFS = new GridFS(db,"fs");               
	    GridFSInputFile inputFile = myFS.createFile(file);
	    ObjectId oid = new ObjectId();
	    inputFile.put("_id",oid);

		inputFile.put("filename", fileName);
		for (Map.Entry<String, Object> entry:map.entrySet()) {
			inputFile.put(entry.getKey(), entry.getValue());
		}
//		inputFile.put("username",username);
		inputFile.put("uploadDate",new Date());
	    inputFile.put("contentType", ext);
	    inputFile.put("uploadType",uploadType);
	    if (bytes != null) {
	    	inputFile.put("imgByte", bytes);
	    }

	    inputFile.save();
	    return oid.toString();
	  }

	public String uploadFilesToFs(File file, String fileName, String username, String ext, String uploadType, byte[] bytes) throws Exception{
		//保存文件到gridfs
		DB db = MongoDBFactory.getDB();
	    GridFS myFS = new GridFS(db,"fs");               
	    GridFSInputFile inputFile = myFS.createFile(file);
	    ObjectId oid = new ObjectId();
	    inputFile.put("_id",oid);

		inputFile.put("filename", fileName);
		inputFile.put("username",username);
		inputFile.put("uploadDate",new Date());
	    inputFile.put("contentType", ext);
	    inputFile.put("uploadType",uploadType);
	    if (bytes != null) {
	    	inputFile.put("imgByte", bytes);
	    }

	    inputFile.save();
	    return oid.toString();
	  }
	
	public String uploadFilesToFs(InputStream stream, String fileName, String ext, String uploadType, byte[] bytes) throws Exception{
		//保存文件到gridfs
		DB db = MongoDBFactory.getDB();
	    GridFS myFS = new GridFS(db,"fs");               
	    GridFSInputFile inputFile = myFS.createFile(stream);
	    ObjectId oid = new ObjectId();
	    inputFile.put("_id",oid);

		inputFile.put("filename", fileName);
		inputFile.put("uploadDate",new Date());
	    inputFile.put("contentType", ext);
	    inputFile.put("uploadType",uploadType);
	    if (bytes != null) {
	    	inputFile.put("imgByte", bytes);
	    }

	    inputFile.save();
	    return oid.toString();
	  }
	
	public String uploadFilesToFs(byte[] bytes, String fileName, String username, String ext, String uploadType, byte[] smallBytes) throws Exception{
		//保存文件到gridfs
		DB db = MongoDBFactory.getDB();
	    GridFS myFS = new GridFS(db,"fs");               
	    GridFSInputFile inputFile = myFS.createFile(bytes);
	    ObjectId oid = new ObjectId();
	    
	    inputFile.put("_id",oid);
		inputFile.put("filename", fileName);
		inputFile.put("username",username);
		inputFile.put("uploadDate",new Date());
	    inputFile.put("contentType", ext);
	    inputFile.put("uploadType",uploadType);
	    if (smallBytes != null) {
	    	inputFile.put("imgByte", smallBytes);
	    }
	    inputFile.save();
	    return oid.toString();
	  }
	
	public List<GridFSDBFile> allUserFileInfo(String collName, Map<String,Object> userMap){
		GridFS gridFS= new GridFS(db,collName);
		DBObject query=new BasicDBObject(userMap);
		DBObject sort=new BasicDBObject("uploadDate", -1);
		List<GridFSDBFile> gridFSDBFileList = gridFS.find(query,sort);
		//gridFS.remove(query);
		return gridFSDBFileList;
	}
	
	public List<GridFSDBFile> allFileInfo(String collName,Page page,Map<String,Object> query){
		GridFS gridFS= new GridFS(db,collName);
		DBObject queryObj=new BasicDBObject(query);
		DBObject sort=new BasicDBObject("uploadDate", -1);
		List<GridFSDBFile> gridFSDBFileList = gridFS.find(queryObj,sort);
		return gridFSDBFileList;
	}
	
//	public void delFileById(String id,String gridcllection){
//		GridFS gridFS= new GridFS(db,gridcllection);
//		ObjectId objId = new ObjectId(id);
//		gridFS.remove(objId);
//	}
	
	public void deleteFileByQuery(String collName, Map<String,Object> query) {
		GridFS gridFS = new GridFS(db, collName);
		gridFS.remove(new BasicDBObject(query));
	}
	
	public GridFSDBFile getFileByFs(Map<String,Object> map,String gridFs){
		GridFS gridFS= new GridFS(db,gridFs);
		DBObject query=new BasicDBObject(map);
		GridFSDBFile gridFSDBFile =(GridFSDBFile)gridFS.findOne(query);
		if (gridFSDBFile != null) {
			return gridFSDBFile;
		}
		return null;
	}

	public GridFSDBFile getFileById(String collName, String id){
		GridFS gridFS= new GridFS(db,collName);
		ObjectId oid = new ObjectId(id);
		GridFSDBFile gridFSDBFile =(GridFSDBFile)gridFS.find(oid);
		return gridFSDBFile;
	}

//    public void removeData(String colName,Map<String,Object> removeMap){
//    	DB db = MongoDBFactory.getDB();
//		DBCollection coll = db.getCollection(colName);
//		DBObject query = new BasicDBObject();
//		query.putAll(removeMap);
//		coll.findAndRemove(query);
//    }
    
    /*public List<Map<String,Object>> allFiles(String collName, Map<String,Object> query){
		GridFS gridFS= new GridFS(db,collName);
		DBObject queryObj=new BasicDBObject(query);
		DBObject sort=new BasicDBObject("uploadDate", -1);
		//List<GridFSDBFile> gridFSDBFileList = gridFS.find(queryObj,sort);
		List<Map<String,Object>> list=Lists.newArrayList();
		DBCursor cursor=gridFS.getFileList(queryObj).sort(sort);
		while(cursor.hasNext()){
			GridFSDBFile grid=(GridFSDBFile) cursor.next();
        	Map<String,Object> gridMap=gridFSToMap(grid);
        	list.add(gridMap);
		}
		return list;
	}*/
    
	public List<Map<String,Object>> allFileList(String collName,Page page,Map<String,Object> query,Map<String,Object> sortMap){
		GridFS gridFS= new GridFS(db,collName);
		DBObject queryObj=new BasicDBObject(query);
		DBObject sort=new BasicDBObject();
		if(sortMap!=null){
			sort.putAll(sortMap);
		}else{
			sort.put("uploadDate", -1);
		}
		int skipCount=page.getFirst();
		int showCount=page.getNumPerPage();
		DBCursor gridFSDBFileList = gridFS.getFileList(queryObj).sort(sort).skip(skipCount).limit(showCount);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (gridFSDBFileList.hasNext()) {
        	GridFSDBFile grid=(GridFSDBFile) gridFSDBFileList.next();
        	Map<String,Object> gridMap=gridFSToMap(grid);
        	list.add(gridMap);
        }
		return list;
	}
    
    public long count(String collName,Map<String,Object> queryMap) {
		GridFS gridFS= new GridFS(db,collName);
		DBObject query=new BasicDBObject(queryMap);
		return gridFS.getFileList(query).count();
	}
    
    public List<String> getFileTypeList(String collName, Map<String,Object> query){
		GridFS gridFS= new GridFS(db,collName);
		DBObject queryObj=new BasicDBObject(query);
		List<String> list=Lists.newArrayList();
		DBCursor cursor=gridFS.getFileList(queryObj);
		while(cursor.hasNext()){
			GridFSDBFile grid=(GridFSDBFile) cursor.next();
			String contentType=grid.getContentType();
        	if(!list.contains(contentType)){
        		list.add(contentType);
        	}
		}
		return list;
	}
    
    private Map<String,Object> gridFSToMap(GridFSDBFile grid){
    	Map<String,Object> gridMap=Maps.newHashMap();
    	gridMap.put("id",grid.getId().toString());
    	gridMap.put("filename", grid.getFilename());
    	gridMap.put("contentType", grid.getContentType());
    	gridMap.put("uploadDate",grid.getUploadDate());
    	gridMap.put("uploadType", grid.get("uploadType"));
    	gridMap.put("length", grid.getLength());
    	gridMap.put("ctime", grid.get("ctime"));
    	gridMap.put("utime", grid.get("utime"));
    	gridMap.put("username", grid.get("username"));
    	gridMap.put("md5", grid.get("md5"));
    	return gridMap;
    }
    
    public List<Map<String,Object>> userAllFiles(String collName,Map<String,Object> query){
		GridFS gridFS= new GridFS(db,collName);
		DBObject queryObj=new BasicDBObject(query);
		DBCursor gridFSDBFileList = gridFS.getFileList(queryObj);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (gridFSDBFileList.hasNext()) {
        	GridFSDBFile grid=(GridFSDBFile) gridFSDBFileList.next();
        	Map<String,Object> gridMap=gridFSToMap(grid);
        	list.add(gridMap);
        }
		return list;
	}
    
}
