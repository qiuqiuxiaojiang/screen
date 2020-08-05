package com.capitalbio.statement.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.mongo.MongoDBFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
@Repository
public class ProjectProcessDao extends MongoBaseDAO{
	
	protected DB db = MongoDBFactory.getDB();
	
	//String reduce = "function(obj,pre){pre.count = pre.count + parseInt(obj.total)}";
	
//	public Map<String, Object> getTz(String collection) {
//		DBCollection coll = db.getCollection(collection);
//		
//		DBObject key = new BasicDBObject("type", true);
//		BasicDBObject cond = new BasicDBObject();
//		BasicDBObject initial = new BasicDBObject("count",0);  
//		String reduce = "function(obj,pre){pre.count = pre.count + parseInt(obj.total)}";
//		BasicDBList dblist = (BasicDBList)coll.group(key, cond, initial, reduce); 
//		
//		List<Object> datas = Lists.newArrayList();
//		List<String> names = Lists.newArrayList();
//		if (dblist != null) {
//			
//			for (int i = 0; i < dblist.size(); i++) {
//				DBObject obj = (DBObject) dblist.get(i);
//				
//				//map.put(obj.get("type").toString(), obj.get("count"));
//				datas.add(obj.get("count"));
//				names.add(obj.get("type").toString());
//			}
//			Map<String, Object> map = Maps.newHashMap();
//			map.put("datas", datas);
//			map.put("names", names);
//			return map;
//		}
//		return null;
//	}
	
	public Map<String, Object> getTzByDisease(String collection) {
		DBCollection coll = db.getCollection(collection);
		
		DBObject key = new BasicDBObject("disease", true);
		BasicDBObject cond = new BasicDBObject();
		BasicDBObject initial = new BasicDBObject("cou",0);  
		String reduce = "function(obj,pre){pre.cou = pre.cou + parseInt(obj.count)}";
		BasicDBList dblist = (BasicDBList)coll.group(key, cond, initial, reduce); 
		
		if (dblist != null) {
			Map<String, Object> map = Maps.newHashMap();
			for (int i = 0; i < dblist.size(); i++) {
				DBObject obj = (DBObject) dblist.get(i);
				map.put(obj.get("disease").toString(), obj.get("cou"));
			}
			return map;
		}
		return null;
	}
	
	public List<Map<String,Object>> getDayTotal(Map<String, Object> queryMap, String colName) {
		DBCollection coll = db.getCollection(colName);
		DBObject query = new BasicDBObject();
		query.putAll(queryMap);
		query.put("checkPlace", new BasicDBObject("$ne", "-"));
		DBCursor cursor = coll.find(query);
		List<Map<String,Object>> list = Lists.newArrayList();
	    while (cursor.hasNext()) {
	    	Map<String,Object> obj=(Map<String,Object>) cursor.next();
	    	obj.put("id", obj.remove("_id").toString());
	    	list.add(obj);
	    }
	    return list;
	}
	
	public List<String> getCheckPlace(Map<String, Object> queryMap, String colName) {
		DBCollection coll = db.getCollection(colName);
		DBObject query = new BasicDBObject();
		query.putAll(queryMap);
		query.put("checkPlace", new BasicDBObject("$ne", "-"));
		DBCursor cursor = coll.find(query);
		List<String> list = Lists.newArrayList();
	    while (cursor.hasNext()) {
	    	Map<String,Object> obj=(Map<String,Object>) cursor.next();
	    	//obj.put("id", obj.remove("_id").toString());
	    	
	    	list.add(obj.get("checkPlace").toString());
	    }
	    return list;
	}
	
	public Map<String,Object> totalOfCheckplace(Map<String, Object> queryMap, String colName) {
		DBCollection coll = db.getCollection(colName);
		DBObject query = new BasicDBObject();
		query.putAll(queryMap);
		query.put("checkPlace", new BasicDBObject("$ne", "-"));
		DBObject dbobj = coll.findOne(query);
		Map<String,Object> map = Maps.newHashMap();
		if(dbobj!=null){
			Map<String, Object> obj=dbobj.toMap();
			obj.put("id", obj.remove("_id").toString());
			return obj;
		}else{
			return null;
		}
	}
	
	public Map<String,Object> queryMap(String colName, Map<String,Object> queryMap) {
		DBCollection coll = db.getCollection(colName);
		DBObject query = new BasicDBObject();
		query.putAll(queryMap);
		DBCursor cursor=coll.find(query);
		
		Map<String, Object> map = Maps.newHashMap();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.remove("_id").toString());
        	
        	map.put(obj.get("tizhi").toString(), obj.get("count"));
        }
        return map;
	}
	
	
	public Object getReportAgeTotal(String collection) {
		DBCollection coll = db.getCollection(collection);
		
		DBObject key = new BasicDBObject();
		BasicDBObject cond = new BasicDBObject("age", new BasicDBObject("$ne", "-"));
		BasicDBObject initial = new BasicDBObject("cou",0);  
		String reduce = "function(obj,pre){pre.cou = pre.cou + parseInt(obj.count)}";
		BasicDBList dblist = (BasicDBList)coll.group(key, cond, initial, reduce); 
		//[ { "cou" : 31.0}]
		Object count = 0;
		if (dblist != null) {
			for (int i = 0; i < dblist.size(); i++) {
				DBObject obj = (DBObject) dblist.get(i);
				count = obj.get("cou");
			}
		}
		return count;
	}
	
	
	public Map<String,Object> getDataByAge(Map<String, Object> queryMap, String colName) {
		DBCollection coll = db.getCollection(colName);
		DBObject query = new BasicDBObject();
		query.putAll(queryMap);
		query.put("age", new BasicDBObject("$ne", "-"));
		DBCursor cursor = coll.find(query);
		Map<String, Object> map = Maps.newHashMap();
		while (cursor.hasNext()) {
	    	Map<String,Object> obj=(Map<String,Object>) cursor.next();
	    	
	    	map.put(obj.get("age").toString(), obj.get("count"));
	    }
	    return map;
	}
	
	
}
