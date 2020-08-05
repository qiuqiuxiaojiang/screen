package com.capitalbio.common.mongo;

import org.apache.log4j.LogManager;

import com.capitalbio.common.util.PropertyUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * MongoDB工厂类
 *
 */
public class MongoDBFactory {
	

	private MongoDBFactory(){}
	
	private static class MongoClientHolder {   
		public static String dbname = null;
		public static MongoClient mongoClient = null;
		static {
			
			try{
				String url = PropertyUtils.getProperty("mongo.url");
//				dbname = PropertyUtils.getProperty("mongo.dbname");
				String sURI = String.format(url);
				MongoClientURI uri = new MongoClientURI(sURI);
				mongoClient = new MongoClient(uri);
				dbname = PropertyUtils.getProperty("mongo.dbname");
				
//				LogManager.getLogger("org.mongodb.driver.connection").setLevel(org.apache.log4j.Level.OFF);
//				LogManager.getLogger("org.mongodb.driver.management").setLevel(org.apache.log4j.Level.OFF);
				LogManager.getLogger("org.mongodb.driver.cluster").setLevel(org.apache.log4j.Level.OFF);
//				LogManager.getLogger("org.mongodb.driver.protocol.insert").setLevel(org.apache.log4j.Level.OFF);
//				LogManager.getLogger("org.mongodb.driver.protocol.query").setLevel(org.apache.log4j.Level.OFF);
//				LogManager.getLogger("org.mongodb.driver.protocol.update").setLevel(org.apache.log4j.Level.OFF);
				LogManager.getLogger("org.apache.http.wire").setLevel(org.apache.log4j.Level.OFF);

			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		public static MongoClient getMongoClient () {
			return mongoClient;
		}
		
		public static String getDbname() {
			return dbname;
		}
    }     
    
    public static MongoClient getMongoClient() {     
        return MongoDBFactory.MongoClientHolder.getMongoClient();     
    }
    
    
    

    @SuppressWarnings("deprecation")
	public static DB getDB() {     
    	String dbname = MongoDBFactory.MongoClientHolder.getDbname();
        return MongoDBFactory.MongoClientHolder.getMongoClient().getDB(dbname);
    }
    
    @SuppressWarnings("deprecation")
	public static DB getDB(String dbname) {     
        return MongoDBFactory.MongoClientHolder.getMongoClient().getDB(dbname);
    }

    
    
    
}