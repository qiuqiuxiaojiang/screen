package com.capitalbio.healthcheck.dao;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Repository;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.mongo.MongoDBFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@Repository
public class CommunitysDAO extends MongoBaseDAO{
	
	protected static Logger logger = Logger.getLogger(HealthCheckDAO.class.getName());
	protected DB db = MongoDBFactory.getDB();

	/**
	 * 获取指定项目街道信息
	 * @return
	 */
	public List<String> getStreets(Long itemId) {
		DBCollection coll = db.getCollection("communitys");
		BasicDBObject query = new BasicDBObject();
		query.put("itemId", itemId);
		List<String> distinct = coll.distinct("street", query);
		System.out.println("distinct:" + distinct);
		return distinct;
	}
}
