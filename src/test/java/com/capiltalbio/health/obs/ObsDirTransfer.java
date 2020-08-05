package com.capiltalbio.health.obs;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.model.CopyObjectResult;

public class ObsDirTransfer {
	private String url = "mongodb://fuxin:Z5mO8!DZYJBEYZjTyK8v@172.16.0.16:8635,172.16.0.120:8635/fuxin?authSource=fuxin&replicaSet=replica";
//	private String url = "mongodb://localhost:27017";
	private String dbName = "fuxin";
	String endPoint = "obs.cn-north-1.myhwclouds.com";
	String accessId = "IJKASCHXETEZY2EIERWV";
	String accessKey = "g2yPYc70xQDqYsJb3Nv0zhlqgoN2BgzyYQfLM9k1";
	String bucketName = "obs-fuxin";
	
	public static void main(String[] args) throws Exception {
		ObsDirTransfer odt = new ObsDirTransfer();
		odt.transferObsFile();
	}
	
	public void transferObsFile() {
		DB db = getDB();
		DBCollection coll = db.getCollection("eyeRecord");
		DBCursor cursor = coll.find()
				.sort(new BasicDBObject("visitTime", 1))
				.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		int count = 0;
		String[] cols = new String[]{"lf","ll","lr","lu","ld","rf","rl","rr","ru","rd"};
		String[] eyeImageCols = new String[]{"LLeft","RDown","LDown","LN","LRight","LUp","RRight","RN","RUp","RLeft"};
		String[] fileCols = new String[]{"pdfFile", "parsePdfFile", "zipFile"};
		ObsClient obsClient = getObsClient();
		Map<String,String> fileIdMap = Maps.newHashMap();
		while (cursor.hasNext()) {
			DBObject eyeRecord = cursor.next();
			count++;
			String userId = (String)eyeRecord.get("userId");
			System.out.println("begin "+count+":"+userId);
			fileIdMap.clear();
			for (int i = 0; i < cols.length; i++) {
				String col = cols[i];
				List<Map<String,Object>> fileList = (List<Map<String, Object>>) eyeRecord.get(col);
				if (fileList == null || fileList.size() == 0) {
					continue;
				}
				for (Map<String,Object> fileMap:fileList) {
					String fileId = (String)fileMap.get("fileId");
					if (StringUtils.isEmpty(fileId)) {
						continue;
					}
					String newFileId = "eye/"+fileId;
					boolean suc = copyFile(fileId, newFileId, obsClient);
					if (suc) {
						fileIdMap.put(fileId, newFileId);
						fileMap.put("fileId", newFileId);
					}
				}
			}
			for (int i = 0; i < fileCols.length; i++) {
				String fileId = (String)eyeRecord.get(fileCols[i]);
				if (StringUtils.isEmpty(fileId)) {
					continue;
				}
				String newFileId = "eye/"+fileId;
				boolean suc = copyFile(fileId, newFileId, obsClient);
				if (suc) {
					eyeRecord.put(fileCols[i], newFileId);
				}
				
			}
			Map<String,Object> eyeImageMap = (Map<String,Object>)eyeRecord.get("eyeImage");
			if (eyeImageMap != null) {
				for (int i = 0; i < eyeImageCols.length; i++) {
					String fileId = (String)eyeImageMap.get(eyeImageCols[i]);
					if (StringUtils.isEmpty(fileId)) {
						continue;
					}
					String newFileId = (String)fileIdMap.get(fileId);
					if (newFileId != null) {
						eyeImageMap.put(eyeImageCols[i], newFileId);
					}
				}
			}
			coll.save(eyeRecord);
			System.out.println("end "+count+": "+userId);
		}
	}
	
	private boolean copyFile(String fileId, String destFileId, ObsClient obsClient) {
		if (fileId.startsWith("eye/")) {
			System.out.println("  "+ fileId+" skip");
			return false;
		}
		try {
			CopyObjectResult result = obsClient.copyObject(bucketName, fileId, bucketName, destFileId);
			System.out.println("  "+fileId+" :"+result.getEtag());
			return true;
		} catch (Exception e) {
			System.out.println("  "+fileId+" : not exist");
			return false;
		}
	}
	
	private ObsClient getObsClient() {
		ObsConfiguration conf = new ObsConfiguration();
		conf.setEndPoint(endPoint);
		conf.setMaxConnections(3000);
		conf.setMaxErrorRetry(3);
		ObsClient obsClient = new ObsClient(accessId, accessKey, conf);
		return obsClient;
	}
	
	public DB getDB() {
		try{
			String sURI = String.format(url);
			MongoClientURI uri = new MongoClientURI(sURI);
			MongoClient mongoClient = new MongoClient(uri);
			DB db = mongoClient.getDB(dbName);
			return db;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
