package com.capitalbio.healthcheck.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
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
public class CustomerDAO extends MongoBaseDAO{

	protected static Logger logger = Logger.getLogger(HealthCheckDAO.class.getName());
	protected DB db = MongoDBFactory.getDB();
	
	/**
	 * 建档人数年龄分布
	 * @return
	 */
	public Map<Object, Object> findRecordCountByAge() {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		String code = "db.getCollection('customer').aggregate(["+
				"{"+
			    "'$addFields':{"+
			        "'ages':{"+
			            "'$cond':{"+
			                "'if':{'$in':['$age',[35,36,37,38,39]]},"+
			                "'then':'35-40',"+
			                "'else':{"+
			                        "'$cond':{"+
			                            "'if':{'$in':['$age',[40,41,42,43,44]]},"+
			                            "'then':'40-45',"+
			                            "'else':{"+
			                                     "'$cond':{"+
			                                        "'if':{'$in':['$age',[45,46,47,48,49]]},"+
			                                        "'then':'45-50',"+                                       
			                                        "'else':{"+
			                                             "'$cond':{"+
			                                                "'if':{'$in':['$age',[50,51,52,53,54]]},"+
			                                                "'then':'50-55', "+                                               
			                                                "'else':{"+
			                                                         "'$cond':{"+
			                                                            "'if':{'$in':['$age',[55,56,57,58,59]]},"+
			                                                            "'then':'55-60',"+
			                                                            "'else':{"+
			                                                                     "'$cond':{"+
			                                                                        "'if':{'$in':['$age',[60,61,62,63,64]]},"+
			                                                                        "'then':'60-65',"+
                                                                                                "'else':'-'"+
			                                                                        "}"+
			                                                               " }"+
			                                                            "}"+
			                                                    "}"+
			                                                "} "+
			                                            "}"+
			                                        "}"+
			                               " }"+
			                            "} "+                  
			                   " }"+
			                "} "+           
			            "}"+
			        "}"+
			    "},"+
				"{"+
				    "'$group':{_id:{'ages':'$ages'},'count':{$sum:1}}"+
				"}"+
		    "])";
		
		BasicDBObject o = db.doEval(code);
		@SuppressWarnings("unchecked")
		Map<String,Object> result_m = o.toMap();
		@SuppressWarnings("unchecked")
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			returnMap.put(m.get("_id"), (new Double(d)).intValue());
		}
		return returnMap;
	}
	
	/**
	 * 建档人数年龄分布
	 * @return
	 */
	public Map<String, Object> findRecordCountByAge2(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
	
		/*[60,61,62,63,64]*/
		List<String> inList1 = new ArrayList<String>();
		inList1.add("60");
		inList1.add("61"); 
		inList1.add("62"); 
		inList1.add("63"); 
		inList1.add("64"); 
		inList1.add("65"); 
		BasicDBObject inAgeDBO1 = new BasicDBObject("$in", Arrays.asList("$age",inList1));
		BasicDBObject condDBO1 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO1,"60-65","-"));
		
		/*[55,56,57,58,59]*/
		List<String> inList2 = new ArrayList<String>();
		inList2.add("55");
		inList2.add("56"); 
		inList2.add("57"); 
		inList2.add("58"); 
		inList2.add("59"); 
		BasicDBObject inAgeDBO2 = new BasicDBObject("$in",Arrays.asList("$age",inList2));
		BasicDBObject condDBO2 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO2,"55-60",condDBO1));
		
		/*[50,51,52,53,54]*/
		List<String> inList3 = new ArrayList<String>();
		inList3.add("50");
		inList3.add("51"); 
		inList3.add("52"); 
		inList3.add("53"); 
		inList3.add("54"); 
		BasicDBObject inAgeDBO3 = new BasicDBObject("$in",Arrays.asList("$age",inList3));
		BasicDBObject condDBO3 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO3,"50-55",condDBO2));
		
		/*[45,46,47,48,49]*/
		List<String> inList4 = new ArrayList<String>();
		inList4.add("45");
		inList4.add("46"); 
		inList4.add("47"); 
		inList4.add("48"); 
		inList4.add("49"); 
		BasicDBObject inAgeDBO4 = new BasicDBObject("$in",Arrays.asList("$age",inList4));
		BasicDBObject condDBO4 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO4,"45-50",condDBO3));
		
		/*[40,41,42,43,44]*/
		List<String> inList5 = new ArrayList<String>();
		inList5.add("40");
		inList5.add("41"); 
		inList5.add("42"); 
		inList5.add("43"); 
		inList5.add("44"); 
		BasicDBObject inAgeDBO5 = new BasicDBObject("$in",Arrays.asList("$age",inList5));
		BasicDBObject condDBO5 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO5,"40-45",condDBO4));
		
		/*[35,36,37,38,39]*/
		List<String> inList6 = new ArrayList<String>();
		inList6.add("35");
		inList6.add("36"); 
		inList6.add("37"); 
		inList6.add("38"); 
		inList6.add("39"); 
		BasicDBObject inAgeDBO6 = new BasicDBObject("$in",Arrays.asList("$age",inList6));
		BasicDBObject condDBO6 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO6,"35-40",condDBO5));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO6));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("ages","$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			Map<String, Object> id = (Map<String, Object>) dbo.get("_id");
			returnMap.put(id.get("ages").toString(), dbo.get("count"));
		}
		System.out.println("returnMap:" + returnMap);
		return returnMap;
	}
	
	/**
	 * 血糖筛查情况
	 * @return
	 */
	public Map<String, Object> findBloodSugarCondition() {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String code = "db.getCollection('customer').aggregate(["
							+ "{"
								+ "'$group':{_id:'$bloodSugarCondition','count':{$sum:1}}"
							+ "}"
						+ "])";
		
		BasicDBObject o = db.doEval(code);
		@SuppressWarnings("unchecked")
		Map<String,Object> result_m = o.toMap();
		@SuppressWarnings("unchecked")
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			if (m.get("_id") != null && m.get("_id") != "") {
				returnMap.put(m.get("_id").toString(), (new Double(d)).intValue());
			}
		}
		return returnMap;
	}
	
	/**
	 * 血糖筛查情况
	 * @return
	 */
	public Map<String, Object> findBloodSugarCondition2(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		if (district != null) {
			BasicDBObject groupDistrict = new BasicDBObject("$match",new BasicDBObject("district",district));
			aggreList.add(groupDistrict);
		}
		
		BasicDBObject groupDBO = new BasicDBObject("$group", new BasicDBObject("_id", "$bloodSugarCondition").append("count", new BasicDBObject("$sum", 1)));
		aggreList.add(groupDBO);
		Iterable<DBObject> results = coll.aggregate(aggreList).results();
		
		for(DBObject dbo : results){
			if(dbo.get("_id") == null || dbo.get("_id") ==""){
				returnMap.put("-", dbo.get("count"));
			}else{
				returnMap.put((String)dbo.get("_id"), dbo.get("count"));
			}
		}
		return returnMap;
	}
	
	/**
	 * 血糖情况年龄分布
	 * @return
	 */
	public Map<Object, Object> findBloodSugarByAge(String district) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
	
		/*[60,61,62,63,64]*/
		List<String> inList1 = new ArrayList<String>();
		inList1.add("60");
		inList1.add("61"); 
		inList1.add("62"); 
		inList1.add("63"); 
		inList1.add("64"); 
		inList1.add("65"); 
		BasicDBObject inAgeDBO1 = new BasicDBObject("$in", Arrays.asList("$age",inList1));
		BasicDBObject condDBO1 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO1,"60-65","-"));
		
		/*[55,56,57,58,59]*/
		List<String> inList2 = new ArrayList<String>();
		inList2.add("55");
		inList2.add("56"); 
		inList2.add("57"); 
		inList2.add("58"); 
		inList2.add("59"); 
		BasicDBObject inAgeDBO2 = new BasicDBObject("$in",Arrays.asList("$age",inList2));
		BasicDBObject condDBO2 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO2,"55-60",condDBO1));
		
		/*[50,51,52,53,54]*/
		List<String> inList3 = new ArrayList<String>();
		inList3.add("50");
		inList3.add("51"); 
		inList3.add("52"); 
		inList3.add("53"); 
		inList3.add("54"); 
		BasicDBObject inAgeDBO3 = new BasicDBObject("$in",Arrays.asList("$age",inList3));
		BasicDBObject condDBO3 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO3,"50-55",condDBO2));
		
		/*[45,46,47,48,49]*/
		List<String> inList4 = new ArrayList<String>();
		inList4.add("45");
		inList4.add("46"); 
		inList4.add("47"); 
		inList4.add("48"); 
		inList4.add("49"); 
		BasicDBObject inAgeDBO4 = new BasicDBObject("$in",Arrays.asList("$age",inList4));
		BasicDBObject condDBO4 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO4,"45-50",condDBO3));
		
		/*[40,41,42,43,44]*/
		List<String> inList5 = new ArrayList<String>();
		inList5.add("40");
		inList5.add("41"); 
		inList5.add("42"); 
		inList5.add("43"); 
		inList5.add("44"); 
		BasicDBObject inAgeDBO5 = new BasicDBObject("$in",Arrays.asList("$age",inList5));
		BasicDBObject condDBO5 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO5,"40-45",condDBO4));
		
		/*[35,36,37,38,39]*/
		List<String> inList6 = new ArrayList<String>();
		inList6.add("35");
		inList6.add("36"); 
		inList6.add("37"); 
		inList6.add("38"); 
		inList6.add("39"); 
		BasicDBObject inAgeDBO6 = new BasicDBObject("$in",Arrays.asList("$age",inList6));
		BasicDBObject condDBO6 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO6,"35-40",condDBO5));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO6));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bs","$bloodSugarCondition").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id"), dbo.get("count"));
			}
		}
		System.out.println("returnMap:" + returnMap);
		return returnMap;
	}
	
	/**
	 * 血压筛查情况
	 * @return
	 */
	public Map<String, Object> findBloodPressureCount(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		if (district != null) {
			BasicDBObject groupDistrict = new BasicDBObject("$match",new BasicDBObject("district",district));
			aggreList.add(groupDistrict);
		}
		
		BasicDBObject groupDBO = new BasicDBObject("$group", new BasicDBObject("_id", "$bloodPressureCondition").append("count", new BasicDBObject("$sum", 1)));
		aggreList.add(groupDBO);
		Iterable<DBObject> results = coll.aggregate(aggreList).results();
		
		for(DBObject dbo : results){
			if(dbo.get("_id") == null || dbo.get("_id") ==""){
				returnMap.put("-", dbo.get("count"));
			}else{
				returnMap.put((String)dbo.get("_id"), dbo.get("count"));
			}
		}
		return returnMap;
	}
	
	/**
	 * 血压情况年龄分布
	 * @return
	 */
	public Map<Object, Object> findBloodPressureByAge(String district) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
	
		/*[60,61,62,63,64]*/
		List<String> inList1 = new ArrayList<String>();
		inList1.add("60");
		inList1.add("61"); 
		inList1.add("62"); 
		inList1.add("63"); 
		inList1.add("64"); 
		inList1.add("65"); 
		BasicDBObject inAgeDBO1 = new BasicDBObject("$in", Arrays.asList("$age",inList1));
		BasicDBObject condDBO1 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO1,"60-65","-"));
		
		/*[55,56,57,58,59]*/
		List<String> inList2 = new ArrayList<String>();
		inList2.add("55");
		inList2.add("56"); 
		inList2.add("57"); 
		inList2.add("58"); 
		inList2.add("59"); 
		BasicDBObject inAgeDBO2 = new BasicDBObject("$in",Arrays.asList("$age",inList2));
		BasicDBObject condDBO2 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO2,"55-60",condDBO1));
		
		/*[50,51,52,53,54]*/
		List<String> inList3 = new ArrayList<String>();
		inList3.add("50");
		inList3.add("51"); 
		inList3.add("52"); 
		inList3.add("53"); 
		inList3.add("54"); 
		BasicDBObject inAgeDBO3 = new BasicDBObject("$in",Arrays.asList("$age",inList3));
		BasicDBObject condDBO3 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO3,"50-55",condDBO2));
		
		/*[45,46,47,48,49]*/
		List<String> inList4 = new ArrayList<String>();
		inList4.add("45");
		inList4.add("46"); 
		inList4.add("47"); 
		inList4.add("48"); 
		inList4.add("49"); 
		BasicDBObject inAgeDBO4 = new BasicDBObject("$in",Arrays.asList("$age",inList4));
		BasicDBObject condDBO4 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO4,"45-50",condDBO3));
		
		/*[40,41,42,43,44]*/
		List<String> inList5 = new ArrayList<String>();
		inList5.add("40");
		inList5.add("41"); 
		inList5.add("42"); 
		inList5.add("43"); 
		inList5.add("44"); 
		BasicDBObject inAgeDBO5 = new BasicDBObject("$in",Arrays.asList("$age",inList5));
		BasicDBObject condDBO5 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO5,"40-45",condDBO4));
		
		/*[35,36,37,38,39]*/
		List<String> inList6 = new ArrayList<String>();
		inList6.add("35");
		inList6.add("36"); 
		inList6.add("37"); 
		inList6.add("38"); 
		inList6.add("39"); 
		BasicDBObject inAgeDBO6 = new BasicDBObject("$in",Arrays.asList("$age",inList6));
		BasicDBObject condDBO6 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO6,"35-40",condDBO5));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO6));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bp","$bloodPressureCondition").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id"), dbo.get("count"));
			}
		}
		System.out.println("returnMap:" + returnMap);
		return returnMap;
	}
	
	/**
	 * 血脂筛查情况
	 * @return
	 */
	public Map<String, Object> findBloodLipidCount(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		if (district != null) {
			BasicDBObject groupDistrict = new BasicDBObject("$match",new BasicDBObject("district",district));
			aggreList.add(groupDistrict);
		}
		
		BasicDBObject groupDBO = new BasicDBObject("$group", new BasicDBObject("_id", "$bloodLipidCondition").append("count", new BasicDBObject("$sum", 1)));
		aggreList.add(groupDBO);
		Iterable<DBObject> results = coll.aggregate(aggreList).results();
		
		for(DBObject dbo : results){
			if(dbo.get("_id") == null || dbo.get("_id") ==""){
				returnMap.put("-", dbo.get("count"));
			}else{
				returnMap.put((String)dbo.get("_id"), dbo.get("count"));
			}
		}
		return returnMap;
	}
	
	/**
	 * 血脂情况年龄分布
	 * @return
	 */
	public Map<Object, Object> findBloodLipidByAge(String district) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
	
		/*[60,61,62,63,64]*/
		List<String> inList1 = new ArrayList<String>();
		inList1.add("60");
		inList1.add("61"); 
		inList1.add("62"); 
		inList1.add("63"); 
		inList1.add("64"); 
		inList1.add("65"); 
		BasicDBObject inAgeDBO1 = new BasicDBObject("$in", Arrays.asList("$age",inList1));
		BasicDBObject condDBO1 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO1,"60-65","-"));
		
		/*[55,56,57,58,59]*/
		List<String> inList2 = new ArrayList<String>();
		inList2.add("55");
		inList2.add("56"); 
		inList2.add("57"); 
		inList2.add("58"); 
		inList2.add("59"); 
		BasicDBObject inAgeDBO2 = new BasicDBObject("$in",Arrays.asList("$age",inList2));
		BasicDBObject condDBO2 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO2,"55-60",condDBO1));
		
		/*[50,51,52,53,54]*/
		List<String> inList3 = new ArrayList<String>();
		inList3.add("50");
		inList3.add("51"); 
		inList3.add("52"); 
		inList3.add("53"); 
		inList3.add("54"); 
		BasicDBObject inAgeDBO3 = new BasicDBObject("$in",Arrays.asList("$age",inList3));
		BasicDBObject condDBO3 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO3,"50-55",condDBO2));
		
		/*[45,46,47,48,49]*/
		List<String> inList4 = new ArrayList<String>();
		inList4.add("45");
		inList4.add("46"); 
		inList4.add("47"); 
		inList4.add("48"); 
		inList4.add("49"); 
		BasicDBObject inAgeDBO4 = new BasicDBObject("$in",Arrays.asList("$age",inList4));
		BasicDBObject condDBO4 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO4,"45-50",condDBO3));
		
		/*[40,41,42,43,44]*/
		List<String> inList5 = new ArrayList<String>();
		inList5.add("40");
		inList5.add("41"); 
		inList5.add("42"); 
		inList5.add("43"); 
		inList5.add("44"); 
		BasicDBObject inAgeDBO5 = new BasicDBObject("$in",Arrays.asList("$age",inList5));
		BasicDBObject condDBO5 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO5,"40-45",condDBO4));
		
		/*[35,36,37,38,39]*/
		List<String> inList6 = new ArrayList<String>();
		inList6.add("35");
		inList6.add("36"); 
		inList6.add("37"); 
		inList6.add("38"); 
		inList6.add("39"); 
		BasicDBObject inAgeDBO6 = new BasicDBObject("$in",Arrays.asList("$age",inList6));
		BasicDBObject condDBO6 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO6,"35-40",condDBO5));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO6));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bl","$bloodLipidCondition").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id"), dbo.get("count"));
			}
		}
		System.out.println("returnMap:" + returnMap);
		return returnMap;
	}
	
	public Map<String, Object> findFatCount(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBObject bmiOBO1 = new BasicDBObject("BMI", new BasicDBObject("$exists", true));
		if (StringUtils.isNotEmpty(district)) {
			List<BasicDBObject> aggreList1 = new ArrayList<BasicDBObject>();
			BasicDBObject districtOBO = new BasicDBObject("district", district);
			aggreList1.add(bmiOBO1);
			aggreList1.add(districtOBO);
			BasicDBObject bDB2 = new BasicDBObject("$match",new BasicDBObject("$and", aggreList1));
			aggreList.add(bDB2);
		} else {
			BasicDBObject bDB2 = new BasicDBObject("$match", bmiOBO1);
			aggreList.add(bDB2);
		}
		
		BasicDBObject notNullbDB = new BasicDBObject("$ne", Arrays.asList("$BMI", null));
		BasicDBObject bDB0 = new BasicDBObject("$lte", Arrays.asList("$BMI", 18.4));
		BasicDBObject bDb01 = new BasicDBObject("$and", Arrays.asList(notNullbDB, bDB0));
		BasicDBObject condBDB0 = new BasicDBObject("$cond", Arrays.asList(bDb01, "偏瘦", "-"));
		
		BasicDBObject bDB2 = new BasicDBObject("$gte", Arrays.asList("$BMI", 24));
		BasicDBObject bDB3 = new BasicDBObject("$lt", Arrays.asList("$BMI", 28));
		BasicDBObject bDb4 = new BasicDBObject("$and", Arrays.asList(bDB2, bDB3));
		BasicDBObject condBDB1 = new BasicDBObject("$cond", Arrays.asList(bDb4, "超重人群", condBDB0));
		
		BasicDBObject bDb5 = new BasicDBObject("$gte", Arrays.asList("$BMI", 28));
		BasicDBObject condBDB2 = new BasicDBObject("$cond", Arrays.asList(bDb5, "肥胖人群", condBDB1));
		
		BasicDBObject bDb6 = new BasicDBObject("$gte", Arrays.asList("$BMI", 18.5));
		BasicDBObject bDb7 = new BasicDBObject("$lt", Arrays.asList("$BMI", 24));
		BasicDBObject bDb8 = new BasicDBObject("$and", Arrays.asList(bDb6, bDb7));
		BasicDBObject condBDB3 = new BasicDBObject("$cond", Arrays.asList(bDb8, "正常", condBDB2));
		
		BasicDBObject bDb9 = new BasicDBObject("$addFields", new BasicDBObject("BMIs",condBDB3));
		aggreList.add(bDb9);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","$BMIs").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			if(dbo.get("_id") == null || dbo.get("_id") ==""){
				returnMap.put("-", dbo.get("count"));
			}else{
				returnMap.put((String)dbo.get("_id"), dbo.get("count"));
			}
		}
		return returnMap;
	}
	
	public Map<Object, Object> findFatCountByAge(String district) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBObject bmiOBO1 = new BasicDBObject("BMI", new BasicDBObject("$exists", true));
		if (StringUtils.isNotEmpty(district)) {
			List<BasicDBObject> aggreList1 = new ArrayList<BasicDBObject>();
			BasicDBObject districtOBO = new BasicDBObject("district", district);
			aggreList1.add(bmiOBO1);
			aggreList1.add(districtOBO);
			BasicDBObject bDB2 = new BasicDBObject("$match",new BasicDBObject("$and", aggreList1));
			aggreList.add(bDB2);
		} else {
			BasicDBObject bDB2 = new BasicDBObject("$match", bmiOBO1);
			aggreList.add(bDB2);
		}
		BasicDBObject notNullbDB = new BasicDBObject("$ne", Arrays.asList("$BMI", null));
		BasicDBObject bDB0 = new BasicDBObject("$lte", Arrays.asList("$BMI", 18.4));
		BasicDBObject bDb01 = new BasicDBObject("$and", Arrays.asList(notNullbDB, bDB0));
		BasicDBObject condBDB0 = new BasicDBObject("$cond", Arrays.asList(bDb01, "偏瘦", "-"));
		
		BasicDBObject bDB2 = new BasicDBObject("$gte", Arrays.asList("$BMI", 24));
		BasicDBObject bDB3 = new BasicDBObject("$lt", Arrays.asList("$BMI", 28));
		BasicDBObject bDb4 = new BasicDBObject("$and", Arrays.asList(bDB2, bDB3));
		BasicDBObject condBDB1 = new BasicDBObject("$cond", Arrays.asList(bDb4, "超重人群", condBDB0));
		
		BasicDBObject bDb5 = new BasicDBObject("$gte", Arrays.asList("$BMI", 28));
		BasicDBObject condBDB2 = new BasicDBObject("$cond", Arrays.asList(bDb5, "肥胖人群", condBDB1));
		
		BasicDBObject bDb6 = new BasicDBObject("$gte", Arrays.asList("$BMI", 18.5));
		BasicDBObject bDb7 = new BasicDBObject("$lt", Arrays.asList("$BMI", 24));
		BasicDBObject bDb8 = new BasicDBObject("$and", Arrays.asList(bDb6, bDb7));
		BasicDBObject condBDB3 = new BasicDBObject("$cond", Arrays.asList(bDb8, "正常", condBDB2));
		
		BasicDBObject bDb9 = new BasicDBObject("$addFields", new BasicDBObject("BMIs",condBDB3));
		aggreList.add(bDb9);
		
		/*[60,61,62,63,64]*/
		List<String> inList1 = new ArrayList<String>();
		inList1.add("60");
		inList1.add("61"); 
		inList1.add("62"); 
		inList1.add("63"); 
		inList1.add("64"); 
		inList1.add("65"); 
		BasicDBObject inAgeDBO1 = new BasicDBObject("$in", Arrays.asList("$age",inList1));
		BasicDBObject condDBO1 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO1,"60-65","-"));
		
		/*[55,56,57,58,59]*/
		List<String> inList2 = new ArrayList<String>();
		inList2.add("55");
		inList2.add("56"); 
		inList2.add("57"); 
		inList2.add("58"); 
		inList2.add("59"); 
		BasicDBObject inAgeDBO2 = new BasicDBObject("$in",Arrays.asList("$age",inList2));
		BasicDBObject condDBO2 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO2,"55-59",condDBO1));
		
		/*[50,51,52,53,54]*/
		List<String> inList3 = new ArrayList<String>();
		inList3.add("50");
		inList3.add("51"); 
		inList3.add("52"); 
		inList3.add("53"); 
		inList3.add("54"); 
		BasicDBObject inAgeDBO3 = new BasicDBObject("$in",Arrays.asList("$age",inList3));
		BasicDBObject condDBO3 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO3,"50-54",condDBO2));
		
		/*[45,46,47,48,49]*/
		List<String> inList4 = new ArrayList<String>();
		inList4.add("45");
		inList4.add("46"); 
		inList4.add("47"); 
		inList4.add("48"); 
		inList4.add("49"); 
		BasicDBObject inAgeDBO4 = new BasicDBObject("$in",Arrays.asList("$age",inList4));
		BasicDBObject condDBO4 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO4,"45-49",condDBO3));
		
		/*[40,41,42,43,44]*/
		List<String> inList5 = new ArrayList<String>();
		inList5.add("40");
		inList5.add("41"); 
		inList5.add("42"); 
		inList5.add("43"); 
		inList5.add("44"); 
		BasicDBObject inAgeDBO5 = new BasicDBObject("$in",Arrays.asList("$age",inList5));
		BasicDBObject condDBO5 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO5,"40-44",condDBO4));
		
		/*[35,36,37,38,39]*/
		List<String> inList6 = new ArrayList<String>();
		inList6.add("35");
		inList6.add("36"); 
		inList6.add("37"); 
		inList6.add("38"); 
		inList6.add("39"); 
		BasicDBObject inAgeDBO6 = new BasicDBObject("$in",Arrays.asList("$age",inList6));
		BasicDBObject condDBO6 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO6,"35-39",condDBO5));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO6));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bmi","$BMIs").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id"), dbo.get("count"));
			}
		}
		System.out.println("returnMap:" + returnMap);
		return returnMap;
	}
	
	public Integer getTizhiCount(String tizhi, String district, String disease) {
		DBCollection coll = db.getCollection("customer");
		BasicDBObject regulaQuery = new BasicDBObject();
		regulaQuery.put("$regex", "^" + tizhi + ".*$");
		
		Map<String, Object> query = Maps.newHashMap();
		if (StringUtils.isNotEmpty(district)) {
			query.put("district", district);
		} 
		
		if (StringUtils.isNotEmpty(disease)) {
			if (disease.equals("糖尿病患者")) {
				query.put("bloodSugarCondition", disease);
			} else if (disease.equals("高血压患者")) {
				query.put("bloodPressureCondition", disease);
			} else if (disease.equals("血脂异常患者")) {
				query.put("bloodLipidCondition", disease);
			} else if (disease.equals("肥胖人群")) {
				BasicDBObject fatQuery = new BasicDBObject();
				fatQuery.put("$gte", 28);
				query.put("BMI", fatQuery);
			}
		} 
		
		query.put("tizhi", regulaQuery);
		
		DBObject map=new BasicDBObject(query);
		Integer count = coll.find(map).count();
		
        return count;
	}
	
}
