package com.capitalbio.common.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.capitalbio.common.mongo.MongoDBFactory;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class MenuDAO extends MongoBaseDAO{
	DB db = MongoDBFactory.getDB();
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getMenu(Map<String,Object> queryMap) {
		DBCollection coll = db.getCollection("menu");
		DBObject query = new BasicDBObject();
		query.putAll(queryMap);
		DBCursor cursor=coll.find(query).sort(new BasicDBObject("seq",1)); //-1降序  1升序
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.get("_id").toString());
        	obj.remove(obj.get("_id"));
        	list.add(obj);
        }
        return list;
	}
}
