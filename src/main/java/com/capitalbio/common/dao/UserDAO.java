package com.capitalbio.common.dao;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.stereotype.Repository;

import com.capitalbio.common.mongo.MongoDBFactory;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
@SuppressWarnings("unchecked")
public class UserDAO extends MongoBaseDAO{

	protected static Logger logger = Logger.getLogger(UserDAO.class.getName());
	protected DB db = MongoDBFactory.getDB();
	
	public Map<String, Object> getUserByName(String username) {
		DBCollection t_user = db.getCollection("user");
		DBObject query = new BasicDBObject();
		query.put("username", username);
		DBObject user = t_user.findOne(query);
		if(user != null){
			return user.toMap();
		}
		return null;
		
	}
	
	public Map<String, Object> getUser(Map<String, Object> obj) {
		DBCollection t_user = db.getCollection("user");
		DBObject query = new BasicDBObject();
		query.put("username", obj.get("username"));
		query.put("password", obj.get("password"));
		DBObject user = t_user.findOne(query);
		if(user != null){
			return user.toMap();
		}
		return null;
		
	}
	
	public Map<String, Object> getUser(String username) {
		DBCollection coll = db.getCollection("user");
		DBObject query = new BasicDBObject();
		query.put("username", username);
		DBObject dbobj=coll.findOne(query);
		if(dbobj!=null){
			Map<String, Object> obj=dbobj.toMap();
			obj.put("id", obj.remove("_id").toString());
			return obj;
		}else{
			return null;
		}
	}
	
	public Map<String, Object> getUserByMobile(String mobile) {
		DBCollection coll = db.getCollection("user");
		DBObject query = new BasicDBObject();
		query.put("mobile", mobile);
		DBObject dbobj=coll.findOne(query);
		if(dbobj!=null){
			Map<String, Object> obj=dbobj.toMap();
			obj.put("id", obj.remove("_id").toString());
			return obj;
		}else{
			return null;
		}
	}

	public Map<String,Object> getRole(String role) {
		DBCollection coll = db.getCollection("role");
		DBObject query = new BasicDBObject();
		query.put("rolekey", role);
		DBObject dbobj=coll.findOne(query);
		if(dbobj!=null){
			Map<String, Object> obj=dbobj.toMap();
			obj.put("id", obj.remove("_id").toString());
			return obj;
		}else{
			return null;
		}
		
	}


	
	public void saveUserLoginInfo(Map<String, Object> loginInfo){
		DBCollection t_logininfo = db.getCollection("loginInfo");
		DBObject query = new BasicDBObject(loginInfo);
		if(loginInfo != null){
			if(loginInfo.get("id")==null){
				query.put("id", query.get("_id"));
			}
			t_logininfo.save(query);
		}
	}
	
	
	public List<Map<String, Object>> getLoginfo(String username, String startTime, String endTime) {
		DBCollection coll = db.getCollection("loginInfo");
		DBObject query = new BasicDBObject("logintime", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		query.put("username", username);
		query.put("loginState", "fail");
		
		DBObject sort = new BasicDBObject();
		sort.put("logintime", -1);
		
		DBCursor cursor = coll.find(query).sort(sort);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.remove("_id").toString());
        	list.add(obj);
        }
        return list;
		
	}
}
