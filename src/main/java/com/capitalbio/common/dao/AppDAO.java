package com.capitalbio.common.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Repository
public class AppDAO extends MongoBaseDAO{
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAppByAppid(String appid) {
		DBCollection coll = db.getCollection("app");
		DBObject query = new BasicDBObject();
		query.put("appid", appid);
		DBObject dbobj=coll.findOne(query);
		if(dbobj!=null){
			Map<String, Object> obj=dbobj.toMap();
			obj.put("id", obj.get("_id").toString());
			return obj;
		}else{
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAppByApikey(String apikey) {
		DBCollection coll = db.getCollection("app");
		DBObject query = new BasicDBObject();
		query.put("apikey", apikey);
		DBObject dbobj=coll.findOne(query);
		if(dbobj!=null){
			Map<String, Object> obj=dbobj.toMap();
			obj.put("id", obj.get("_id").toString());
			return obj;
		}else{
			return null;
		}
	}

}
