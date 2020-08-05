package com.capitalbio.eye.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.util.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@SuppressWarnings("unchecked")
@Repository
public class EyeRecordDao extends MongoBaseDAO{

	/**
	 * 查询所有数据
	 */
	public List<Map<String,Object>> queryAll(String collName) throws Exception{
		DBCollection coll = db.getCollection(collName);
		DBCursor cursor=coll.find();
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.remove("_id").toString());
        	list.add(obj);
        }
        return list;
	}
	
	/**
	 * 分页查询数据
	 * @param collName
	 * @param page
	 * @param query
	 * @param sortMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> queryPage(String collName, Page page, Map<String,Object> query,Map<String,Object> sortMap) throws Exception{
		DBCollection coll = db.getCollection(collName);
		int skipCount=page.getFirst();
		int showCount=page.getNumPerPage();
		DBObject map=new BasicDBObject(query);
		DBObject sort=new BasicDBObject(sortMap);
		DBCursor cursor=coll.find(map).sort(sort).skip(skipCount).limit(showCount);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.get("_id").toString());
        	obj.remove(obj.get("_id"));
        	if (obj.get("name") != null && !"".equals(obj.get("name"))) {
        		String username=obj.get("name").toString();
            	Map<String,Object> userMap=getUserByName(username);
            	if(userMap!=null){
            		obj.put("user", userMap);
            		Map<String,Object> resultQuery=Maps.newHashMap();
                	resultQuery.put("userId",userMap.get("id").toString());
                	List<Map<String,Object>> resultList=queryList("analysisResult", resultQuery);
                	obj.put("resultList", resultList);
            	}
        	}
        	list.add(obj);
        }
        return list;
	}
	
	public List<Map<String,Object>> queryRecordPage(String collName, Page page, Map<String,Object> query) {
		DBCollection coll = db.getCollection(collName);
		int skipCount=page.getFirst();
		int showCount=page.getNumPerPage();
		
		DBObject map=new BasicDBObject(query);
		if (query.get("isTrans") == null) {
			map.put("isTrans", new BasicDBObject("$exists",false));
		}
		
		DBObject sort=new BasicDBObject("utime", -1);
		DBCursor cursor=coll.find(map).sort(sort).skip(skipCount).limit(showCount);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.remove("_id").toString());
        	list.add(obj);
        }
        return list;
	}
	
	public List<Map<String,Object>> queryRecordFsPage(String collName, Page page, Map<String,Object> query) {
		DBCollection coll = db.getCollection(collName);
		int skipCount=page.getFirst();
		int showCount=page.getNumPerPage();
		
		DBObject map=new BasicDBObject(query);
		if (query.get("zipFile_local") == null) {
			map.put("zipFile_local", new BasicDBObject("$exists",false));
		}
		
		DBObject sort=new BasicDBObject("utime", -1);
		DBCursor cursor=coll.find(map).sort(sort).skip(skipCount).limit(showCount);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.remove("_id").toString());
        	list.add(obj);
        }
        return list;
	}
	
	
	/**
	 * 查询某一时间段内上传过数据的用户ID
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<String> queryUserByVisitTime(Date startTime, Date endTime) {
		DBObject queryObj = new BasicDBObject();
		BasicDBObject dateQuery = new BasicDBObject();
		if (startTime != null) {
			dateQuery.put("$gte",startTime);
		}
		if (endTime != null) {
			dateQuery.put("$lte",endTime);
		}
		if (!dateQuery.isEmpty()) {
			queryObj.put("visitTime", dateQuery);
		}
		DBCollection coll = db.getCollection("eyeRecord");
		DBObject keys = new BasicDBObject();
		keys.put("userId", 1);
		DBCursor cursor = coll.find(queryObj, keys);
		List<String> userIdList = Lists.newArrayList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			String userId = (String)obj.get("userId");
			userIdList.add(userId);
		}
		return userIdList;
	}

	public List<Map<String,Object>> queryUserByUTime(Date startTime, Date endTime) {
		DBObject queryObj = new BasicDBObject();
		BasicDBObject dateQuery = new BasicDBObject();
		if (startTime != null) {
			dateQuery.put("$gte",startTime);
		}
		if (endTime != null) {
			dateQuery.put("$lte",endTime);
		}
		if (!dateQuery.isEmpty()) {
			queryObj.put("utime", dateQuery);
		}
		DBCollection coll = db.getCollection("eyeRecord");
		DBObject keys = new BasicDBObject();
		keys.put("userId", 1);
		DBCursor cursor = coll.find(queryObj);
		List<Map<String,Object>> recordList = Lists.newArrayList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Map<String,Object> map = obj.toMap();
			map.put("id", map.remove("_id").toString());
			
			recordList.add(map);
		}
		return recordList;
	}
	
	/**
	 * 查询某一时间段内所有报告不为空的用户ID
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<String> queryReportUserByVisitTime(Date startTime, Date endTime) {
		DBObject queryObj = new BasicDBObject();
		BasicDBObject dateQuery = new BasicDBObject();
		if (startTime != null) {
			dateQuery.put("$gte",startTime);
		}
		if (endTime != null) {
			dateQuery.put("$lte",endTime);
		}
		if (!dateQuery.isEmpty()) {
			queryObj.put("visitTime", dateQuery);
		}
		queryObj.put("reportFileName", new BasicDBObject("$ne",null));
		DBCollection coll = db.getCollection("eyeRecord");
		DBObject keys = new BasicDBObject();
		keys.put("userId", 1);
		DBCursor cursor = coll.find(queryObj, keys);
		List<String> userIdList = Lists.newArrayList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			String userId = (String)obj.get("userId");
			userIdList.add(userId);
		}
		return userIdList;
	}

	/**
	 * 查询一个用户一段时间内的目诊记录
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Map<String,Object>> queryRecordByVisitTime(String userId, Date startTime, Date endTime) {
		DBObject queryObj = new BasicDBObject();
		 
		BasicDBObject dateQuery = new BasicDBObject();
		if (startTime != null) {
			dateQuery.put("$gte",startTime);
		}
		if (endTime != null) {
			dateQuery.put("$lte",endTime);
		}
		if (!dateQuery.isEmpty()) {
			queryObj.put("visitTime", dateQuery);
		}
		queryObj.put("userId", userId);
		DBCollection coll = db.getCollection("eyeRecord");
		DBCursor cursor = coll.find(queryObj);
		List<Map<String,Object>> list = Lists.newArrayList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Map<String,Object> map = obj.toMap();
			map.put("id", map.remove("_id").toString());
			list.add(map);
		}
		return list;
	}

	/**
	 * 获取用户ID的列表
	 * @param queryParams
	 * @return
	 */
	public List<String> queryUserByInfo(Map<String,Object> queryParams) {
		DBObject queryObj = new BasicDBObject(queryParams);
		 DBCollection coll = db.getCollection("basicInfo");
		DBObject keys = new BasicDBObject();
		keys.put("userId", 1);
		DBCursor cursor = coll.find(queryObj, keys);
		List<String> userIdList = Lists.newArrayList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			String userId = (String)obj.get("userId");
			userIdList.add(userId);
		}
		return userIdList;
	}

	/**
	 * 根据唯一标识获取最新一条目诊记录
	 * @param uniqueIdId
	 * @return
	 */
	public Map<String,Object> getLatestRecordByUniqueId(String uniqueIdId) {
		DBObject queryObj = new BasicDBObject();
		 
		queryObj.put("uniqueId", uniqueIdId);
		DBCollection coll = db.getCollection("eyeRecord");
		DBCursor cursor = coll.find(queryObj).sort(new BasicDBObject("visitTime", -1));
		
		if (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Map<String,Object> map = obj.toMap();
			map.put("id", map.remove("_id").toString());
			return map;
		}
		return null;

	}

	/**
	 * 获取最新一条目诊记录
	 * @param userId
	 * @return
	 */
	public Map<String,Object> getLatestRecord(String userId) {
		DBObject queryObj = new BasicDBObject();
		 
		queryObj.put("userId", userId);
		DBCollection coll = db.getCollection("eyeRecord");
		DBCursor cursor = coll.find(queryObj).sort(new BasicDBObject("visitTime", -1));
		
		if (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Map<String,Object> map = obj.toMap();
			map.put("id", map.remove("_id").toString());
			return map;
		}
		return null;

	}

	/**
	 * 获取最新一条目诊记录
	 * @param cardId
	 * @return
	 */
	public Map<String,Object> getRecordByCardId(String cardId) {
		DBObject queryObj = new BasicDBObject();
		 
		queryObj.put("cardId", cardId);
		DBCollection coll = db.getCollection("eyeRecord");
		DBCursor cursor = coll.find(queryObj).sort(new BasicDBObject("visitTime", -1));
		
		if (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Map<String,Object> map = obj.toMap();
			map.put("id", map.remove("_id").toString());
			return map;
		}
		return null;

	}

	/**
	 * 获取目诊记录列表
	 * @param cardId
	 * @return
	 */
	public List<Map<String,Object>> getRecordListByCardId(String cardId) {
		DBObject queryObj = new BasicDBObject();
		 
		queryObj.put("cardId", cardId);
		DBCollection coll = db.getCollection("eyeRecord");
		DBCursor cursor = coll.find(queryObj).sort(new BasicDBObject("visitTime", -1));
		List<Map<String,Object>> list = Lists.newArrayList();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Map<String,Object> map = obj.toMap();
			map.put("id", map.remove("_id").toString());
			list.add(map);
		}
		return list;

	}

	public long count(String collName,Map<String,Object> query) {
		DBCollection coll = db.getCollection(collName);
		DBObject queryMap=new BasicDBObject(query);
		return coll.count(queryMap);
	}
	
	private Map<String,Object> getUserByName(String username){
		Map<String,Object> query=Maps.newHashMap();
		query.put("name",username);
		Map<String,Object> userMap=getDataByQuery("basicInfo", query);
		return userMap;
	}
	
	public Map<String, Object> getEyeRecordData(Date startTime, Date endTime) {
		DBCollection coll = db.getCollection("eyeRecord");
		DBObject keys = new BasicDBObject("visitTime", 1);
		DBObject condition = new BasicDBObject("visitTime", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		DBObject initial = new BasicDBObject("count", 0);
		String reduce = "function(doc, out){out.count++;}";
		String finalize = "function(out) {return out;}";
		BasicDBList dblist = (BasicDBList) coll.group(keys, condition, initial, reduce, finalize);
		if (dblist != null) {
			Map<String, Object> map = Maps.newHashMap();
			for (int i = 0; i < dblist.size(); i++) {
				DBObject obj = (DBObject) dblist.get(i);
				map.put(DateUtil.dateToString((Date) obj.get("visitTime")), obj.get("count"));
			}
			return map;
		}
		return null;
	}
	
	public double getErDataOfMonth(Date startTime, Date endTime) {
		DBCollection coll = db.getCollection("eyeRecord");
		BasicDBObject query = new BasicDBObject("visitTime", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		int count = coll.find(query).count();
		return count;
	}
	
	public double getErCount(Date startTime, Date endTime) {
		DBCollection coll = db.getCollection("eyeRecord");
		DBObject query = new BasicDBObject("visitTime", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		int count = coll.find(query).count();
		
		return count;
		
	}
	
	public List<Map<String,Object>> queryListBySort(Map<String,Object> queryMap) {
		DBCollection coll = db.getCollection("eyeRecord");
		DBObject query = new BasicDBObject();
		query.putAll(queryMap);
		DBCursor cursor=coll.find(query).sort(new BasicDBObject("utime", -1));
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.remove("_id").toString());
        	list.add(obj);
        }
        return list;
	}

}
