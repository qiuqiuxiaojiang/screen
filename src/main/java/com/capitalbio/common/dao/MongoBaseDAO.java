package com.capitalbio.common.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import com.capitalbio.common.model.Page;
import com.capitalbio.common.mongo.MongoDBFactory;
import com.capitalbio.common.util.ContextUtils;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * MongoDB存储基础DAO
 * @author wdong
 *
 */
public class MongoBaseDAO {
	protected DB db = MongoDBFactory.getDB();
	
	public long count(String collName) {
		DBCollection coll = db.getCollection(collName);
		return coll.count();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> queryPage(String collName, Page page) throws Exception{
		DBCollection coll = db.getCollection(collName);
		int skipCount=page.getFirst();
		int showCount=page.getNumPerPage();
		DBCursor cursor=coll.find().skip(skipCount).limit(showCount);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.remove("_id").toString());
        	list.add(obj);
        }
        return list;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> queryOne(String colName, Map<String,Object> queryMap) {
		DBCollection coll = db.getCollection(colName);
		DBObject query = new BasicDBObject();
		query.putAll(queryMap);
		DBObject dbobj=coll.findOne(query);
		if(dbobj!=null){
			Map<String, Object> obj=dbobj.toMap();
			obj.put("id", obj.remove("_id").toString());
			return obj;
		}else{
			return null;
		}
		
	}

	
	@SuppressWarnings("unchecked")
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
	
	public String saveData(String colName, Map<String,Object> map) {
		DBCollection coll = db.getCollection(colName);
		ObjectId oid = (ObjectId)map.get("_id");
		if (oid == null) {
			String id = (String)map.remove("id");
			oid = getObjectId(id);
		}
		DBObject dbObj= new BasicDBObject(map);
		String userId = (String)ContextUtils.getUserId();
		if (oid == null) {
			oid = new ObjectId();
			dbObj.put("ctime", new Date());
			if (StringUtils.isNoneEmpty(userId)) {
				dbObj.put("cuser", userId);
			}
		} else {
			dbObj.put("utime", new Date());
			if (StringUtils.isNoneEmpty(userId)) {
				dbObj.put("uuser", userId);
			}
		}
		dbObj.put("_id", oid);

		coll.save(dbObj);
		return oid.toString();
	}
	
	public String saveData1(String colName, Map<String,Object> map) {
		DBCollection coll = db.getCollection(colName);
		ObjectId oid = (ObjectId)map.get("_id");
		if (oid == null) {
			String id = (String)map.remove("id");
			oid = getObjectId(id);
		}
		DBObject dbObj= new BasicDBObject(map);
		dbObj.put("_id", oid);

		coll.save(dbObj);
		return oid.toString();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getData(String colName, String id) {
		DBCollection coll = db.getCollection(colName);
		DBObject query = new BasicDBObject();
		ObjectId oid = getObjectId(id);
		if (oid == null) {
			return null;
		}
		query.put("_id", oid);
		DBObject dbobj=coll.findOne(query);
		if(dbobj!=null){
			Map<String, Object> obj=dbobj.toMap();
			obj.put("id", obj.remove("_id").toString());
			return obj;
		}else{
			return null;
		}
	}
	
	public ObjectId getObjectId(String id) {
		ObjectId oid = null;
		try {
			if (ObjectId.isValid(id)) {
				oid = new ObjectId(id);
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return oid;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getDataByQuery(String colName, Map<String,Object> queryMap) {
		DBCollection coll = db.getCollection(colName);
		DBObject query = new BasicDBObject();
		query.putAll(queryMap);
		DBObject dbobj=coll.findOne(query);
		if(dbobj!=null){
			Map<String, Object> obj=dbobj.toMap();
			obj.put("id", obj.remove("_id").toString());
			return obj;
		}else{
			return null;
		}
		
	}

	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> queryList(String colName, Map<String,Object> queryMap) {
		DBCollection coll = db.getCollection(colName);
		DBObject query = new BasicDBObject();
		query.putAll(queryMap);
		DBCursor cursor=coll.find(query);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.get("_id").toString());
        	list.add(obj);
        }
        return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> queryList(String colName, Map<String,Object> queryMap, Map<String, Object> sortMap) {
		DBCollection coll = db.getCollection(colName);
		DBObject query = new BasicDBObject();
		query.putAll(queryMap);
		
		DBCursor cursor=coll.find(query);
		
		if (sortMap != null) {
			DBObject sort=new BasicDBObject();
			sort.putAll(sortMap);
			cursor = cursor.sort(sort);
		}
		
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.get("_id").toString());
        	list.add(obj);
        }
        return list;
	}

//	public void deleteData(String collName, String id) {
//		DB db = MongoDBFactory.getDB();
//		DBCollection coll = db.getCollection(collName);
//		DBObject query = new BasicDBObject();
//		query.put("_id",new ObjectId(id));
//		coll.findAndRemove(query);
//	}
//	
	public int deleteData(String collName, String id) {
		DB db = MongoDBFactory.getDB();
		DBCollection coll = db.getCollection(collName);
		DBObject query = new BasicDBObject();
		ObjectId oid = getObjectId(id);
		if (oid != null) {
			query.put("_id", oid);
			WriteResult wr = coll.remove(query);
			return wr.getN();
		}
		return 0;
	}

	public String newId() {
		return new ObjectId().toString();
	}
	
	public void deleteDataByParams(String colName, Map<String, Object> params){
		DBCollection coll = db.getCollection(colName);
		DBObject query = new BasicDBObject();
		query.putAll(params);
		coll.remove(query);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findPageBySort(String collName, Page page, Map<String,Object> query,Map<String,Object> sortMap) throws Exception{
		DBCollection coll = db.getCollection(collName);
		int skipCount=page.getFirst();
		int showCount=page.getNumPerPage();
		DBObject map=new BasicDBObject(query);
		DBCursor cursor=coll.find(map);
		if (sortMap != null) {
			DBObject sort=new BasicDBObject();
			sort.putAll(sortMap);
			cursor = cursor.sort(sort);
		}
		cursor=cursor.skip(skipCount).limit(showCount);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.get("_id").toString());
        	obj.remove(obj.get("_id"));
        	list.add(obj);
        }
        return list;
	}
	
	public long countByQuery(String collName,Map<String,Object> query) {
		DBCollection coll = db.getCollection(collName);
		DBObject dbObj= new BasicDBObject(query);
		return coll.count(dbObj);
	}
	
	public void setDB(DB db) {
		this.db = db;
	}
}
