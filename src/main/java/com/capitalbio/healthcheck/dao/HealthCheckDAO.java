package com.capitalbio.healthcheck.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.mongo.MongoDBFactory;
import com.capitalbio.common.util.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
@SuppressWarnings("unchecked")
public class HealthCheckDAO extends MongoBaseDAO{

	protected static Logger logger = Logger.getLogger(HealthCheckDAO.class.getName());
	protected DB db = MongoDBFactory.getDB();
	
	public List<Map<String, Object>> getAgeList() {
		DBCollection coll = db.getCollection("ageList");
		
		DBObject sort = new BasicDBObject();
		sort.put("seq", 1);
		
		DBCursor cursor = coll.find().sort(sort);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.remove("_id").toString());
        	list.add(obj);
        }
        return list;
		
	}
	
	public List<Map<String, Object>> getAgeListDoc() {
		DBCollection coll = db.getCollection("agelistDoc");
		
		DBObject sort = new BasicDBObject();
		sort.put("seq", 1);
		
		DBCursor cursor = coll.find().sort(sort);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.remove("_id").toString());
        	list.add(obj);
        }
        return list;
		
	}
	
	public Map<String, Object> getHealthCheckData(String startTime, String endTime) {
		DBCollection coll = db.getCollection("healthcheck");
		DBObject keys = new BasicDBObject("checkDate", 1);
		DBObject condition = new BasicDBObject("checkDate", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		DBObject initial = new BasicDBObject("count", 0);
		String reduce = "function(doc, out){out.count++;}";
		String finalize = "function(out) {return out;}";
		BasicDBList dblist = (BasicDBList) coll.group(keys, condition, initial, reduce, finalize);

		if (dblist != null) {
			Map<String, Object> map = Maps.newHashMap();
			for (int i = 0; i < dblist.size(); i++) {
				DBObject obj = (DBObject) dblist.get(i);
				map.put(obj.get("checkDate").toString(), obj.get("count"));
			}
			return map;
		}
		return null;
	}
	
	public Map<String, Object> getBloodSugarData(Date startTime, Date endTime) {
		DBCollection coll = db.getCollection("bloodSugar");
		DBObject keys = new BasicDBObject("testTime", 1);
		DBObject condition = new BasicDBObject("testTime", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		DBObject initial = new BasicDBObject("count", 0);
		String reduce = "function(doc, out){out.count++;}";
		String finalize = "function(out) {return out;}";
		BasicDBList dblist = (BasicDBList) coll.group(keys, condition, initial, reduce, finalize);
		if (dblist != null) {
			Map<String, Object> map = Maps.newHashMap();
			for (int i = 0; i < dblist.size(); i++) {
				DBObject obj = (DBObject) dblist.get(i);
				map.put(DateUtil.dateToString((Date) (obj.get("testTime"))), obj.get("count"));
			}
			return map;
		}
		return null;
	}
	
	public double getHcDataOfMonth(String startTime, String endTime) {
		DBCollection coll = db.getCollection("healthcheck");
		BasicDBObject query = new BasicDBObject("checkDate", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		int count = coll.find(query).count();
		return count;
	}
	
	public double getBsDataOfMonth(Date startTime, Date endTime) {
		DBCollection coll = db.getCollection("bloodSugar");
		BasicDBObject query = new BasicDBObject("testTime", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		int count = coll.find(query).count();
		return count;
	}
	
	public double getHcCount(String startTime, String endTime) {
		DBCollection coll = db.getCollection("healthcheck");
		DBObject query = new BasicDBObject("checkDate", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		int count = coll.find(query).count();
		
		return count;
	}
	
	public double getBsCount(Date startTime, Date endTime) {
		DBCollection coll = db.getCollection("bloodSugar");
		DBObject query = new BasicDBObject("testTime", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		int count = coll.find(query).count();
		
		return count;
	}
	/*************************************************************************************************************************************************************************************/
	/**
	 * 筛查进度->血压检测人数->舒张压值或收缩压值不为空
	 * @return
	 */
	public int findLowHighPressureNotEmpty() {
		int total = 0;
		String code = "db.getCollection('healthcheck').aggregate(["+
				"{"+
					"'$match':{" +
						"'$or':[" +
							"{'highPressure':{'$ne':null}}," +
							"{'lowPressure':{'$ne':null}}" +
								"]" +
							"}" +
						"},"+
					"{"+
        "'$group':{_id:'','count':{$sum:1}}"+
    	"}"+
    	"])";
		BasicDBObject o = db.doEval(code);
		Map<String,Object> result_m = o.toMap();
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			total = (new Double(d)).intValue();
			
		}
		return total;
	}
	/**
	 * 筛查进度->血压检测人数->舒张压值或收缩压值不为空2
	 * @return
	 */
	public int findLowHighPressureNotEmpty2(String district) {
		
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBList orList = new BasicDBList();
		BasicDBObject highPressureDBO = new BasicDBObject("highPressure", new BasicDBObject("$ne",null));
		BasicDBObject lowPressureDBO = new BasicDBObject("lowPressure",new BasicDBObject("$ne",null));
		orList.add(lowPressureDBO);
		orList.add(highPressureDBO);
		BasicDBObject query2 = new BasicDBObject("$or",orList);
		
		BasicDBObject matchDBO = null;
		if (StringUtils.isNotEmpty(district)) {
			BasicDBList list1 = new BasicDBList();
			BasicDBObject query1 = new BasicDBObject("district", district);
			list1.add(query1);
			list1.add(query2);
			matchDBO = new BasicDBObject("$match", new BasicDBObject("$and",list1));
		} else {
			matchDBO = new BasicDBObject("$match", query2);
		}
		aggreList.add(matchDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		
		int total = 0;
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			total = (int) dbo.get("count");
		}
		return total;
	}
	/*************************************************************************************************************************************************************************************/
	/**
	 * 筛查进度->血脂检测人数->总胆固醇值TC、甘油三酯值TG、高密度脂蛋白值HDL-C、低密度脂蛋白值LDL-C，以上4项任一不为空
	 * @return
	 */
	public int findTcTgHdlLdlNotEmpty() {
		int total = 0;
		String code = "db.getCollection('healthcheck').aggregate([" +
                           "{" +
                               "'$match':{" +
                                   "'$or':[" +
                                       "{'tc':{'$ne':null}}," +
                                       "{'tg':{'$ne':null}}," +
                                       "{'hdl':{'$ne':null}}," +
                                       "{'ldl':{'$ne':null}}" +                
                                       "]" +
                                   "}"+
                           "}," +
                           "{" +
                               "'$group':{_id:'','count':{$sum:1}}" +
                           "}" +
                       "])";
		BasicDBObject o = db.doEval(code);
		Map<String,Object> result_m = o.toMap();
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			total = (new Double(d)).intValue();
			
		}
		return total;
	}
	
	/**
	 * 筛查进度->血脂检测人数->总胆固醇值TC、甘油三酯值TG、高密度脂蛋白值HDL-C、低密度脂蛋白值LDL-C，以上4项任一不为空2
	 * @return
	 */
	public int findTcTgHdlLdlNotEmpty2(String district) {
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBList orList = new BasicDBList();
		BasicDBObject tcDBO = new BasicDBObject("tc",new BasicDBObject("$ne",null));
		BasicDBObject tgDBO = new BasicDBObject("tg",new BasicDBObject("$ne",null));
		BasicDBObject hdlDBO = new BasicDBObject("hdl",new BasicDBObject("$ne",null));
		BasicDBObject ldlDBO = new BasicDBObject("ldl",new BasicDBObject("$ne",null));
		orList.add(tcDBO);
		orList.add(tgDBO);
		orList.add(hdlDBO);
		orList.add(ldlDBO);
		
		BasicDBObject orDBO = new BasicDBObject("$or",orList);
		BasicDBObject matchDBO = null;
		if (StringUtils.isNotEmpty(district)) {
			BasicDBList list = new BasicDBList();
			BasicDBObject districtDBO = new BasicDBObject("district", district);
			list.add(districtDBO);
			list.add(orDBO);
			matchDBO = new BasicDBObject("$match", new BasicDBObject("$and",list));
		} else {
			matchDBO = new BasicDBObject("$match", orDBO);
		}
		aggreList.add(matchDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		int total = 0;
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			total = (int) dbo.get("count");
		}
		return total;
	}
	/*************************************************************************************************************************************************************************************/
	/**
	 * 筛查进度->OGTT检测人数->OGTT（0h）和OGTT（2h）任一项目不为空
	 * @return
	 */
	public int findOh2hNotEmpty() {
		int total = 0;
		String code = "db.getCollection('healthcheck').aggregate([" +
		                                           "{" + 
		                                               "'$match':{" +
		                                                   "'$or':[" +
		                                                       "{'ogtt':{'$ne':null}}," +
		                                                       "{'ogtt2h':{'$ne':null}}" +             
		                                                       "]" +
		                                                   "}" +
		                                           "}," +
		                                           "{" +
		                                               "'$group':{_id:'','count':{$sum:1}}" +
		                                           "}" +
		                                       "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			total = (new Double(d)).intValue();
			
		}
		return total;
	}
	
	/**
	 * 筛查进度->OGTT检测人数->OGTT（0h）和OGTT（2h）任一项目不为空2
	 * @return
	 */
	public int findOh2hNotEmpty2(String district) {
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBList orList = new BasicDBList();
		BasicDBObject ogttDBO = new BasicDBObject("ogtt",new BasicDBObject("$ne",null));
		BasicDBObject ogtt2hDBO = new BasicDBObject("ogtt2h",new BasicDBObject("$ne",null));
		orList.add(ogttDBO);
		orList.add(ogtt2hDBO);
		BasicDBObject orDBO = new BasicDBObject("$or",orList);
		
		BasicDBObject matchDBO = null;
		if (StringUtils.isNotEmpty(district)) {
			BasicDBList list = new BasicDBList();
			BasicDBObject districtDBO = new BasicDBObject("district", district);
			list.add(districtDBO);
			list.add(orDBO);
			matchDBO = new BasicDBObject("$match", new BasicDBObject("$and",list));
		} else {
			matchDBO = new BasicDBObject("$match", orDBO);
		}
		aggreList.add(matchDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		int total = 0;
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			total = (int) dbo.get("count");
		}
		return total;
	}
	/*************************************************************************************************************************************************************************************/
	/**
	 * 肥胖筛查情况->腰臀比->男性＞0.9  女性＞0.8为中心型肥胖；反之则为正常
	 * @return
	 */
	public Map<String,Object> findFatNumberByWHRAndSex() {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String code = "db.getCollection('healthcheck').aggregate([" +
		                                           "{" +
		                                               "'$project':{" +
		                                                       "'WHRisFat':{" +
		                                                               "'$cond':[" +
		                                                                   "{" +
		                                                                       "$or:"+
		                                                                       "[" +
		                                                                           "{$and:[{$gt: ['$WHR', 0.8 ]},{$eq:['$sex','女']}]},"+
		                                                                           "{$and:[{$gt: ['$WHR', 0.9 ]},{$eq:['$sex','男']}]}"+
		                                                                       "]" +
		                                                                   "},'fat'," +
		                                                                   "'common'" +
		                                                               "]" +
		                                                           "}" +
		                                                   "}" +
		                                           "}," +
		                                           "{" +
		                                               "'$group':{_id:'$WHRisFat','count':{$sum:1}}"+
		                                           "}" +
		                                       "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			returnMap.put((String)m.get("_id"), (new Double(d)).intValue());
		}
		return returnMap;
	}
	
	/**
	 * 肥胖筛查情况->腰臀比->男性＞0.9  女性＞0.8为中心型肥胖；反之则为正常2
	 * @return
	 */
	public Map<String,Object> findFatNumberByWHRAndSex2(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		
		BasicDBObject gtWHRDBO1 = new BasicDBObject("$gt",Arrays.asList("$WHR",0.8));
		BasicDBObject eqSEXDBO1 = new BasicDBObject("$eq",Arrays.asList("$gender","女"));
		BasicDBObject andDBO1 = new BasicDBObject("$and",Arrays.asList(gtWHRDBO1,eqSEXDBO1));
		
		BasicDBObject gtWHRDBO2 = new BasicDBObject("$gt",Arrays.asList("$WHR",0.9));
		BasicDBObject eqSEXDBO2 = new BasicDBObject("$eq",Arrays.asList("$gender","男"));
		BasicDBObject andDBO2 = new BasicDBObject("$and",Arrays.asList(gtWHRDBO2,eqSEXDBO2));
		
		BasicDBObject orDBO = new BasicDBObject("$or",Arrays.asList(andDBO1,andDBO2));
		
		BasicDBObject condDBO = new BasicDBObject("$cond",Arrays.asList(orDBO,"fat","common"));
		
		BasicDBObject projectDBO = new BasicDBObject("$project",new BasicDBObject("WHRisFat",condDBO));
		aggreList.add(projectDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","$WHRisFat").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put((String)dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	/*************************************************************************************************************************************************************************************/
	/**
	 * 血糖筛查情况->血糖情况人群分布->初筛数据->正常\已登记糖尿病\糖尿病高危\血糖异常36-39
	 * @return
	 */
	public Map<String, Object> findBloodSugarConditionPeopleDistributionHeadthCheck() {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String code = "db.getCollection('healthcheck').aggregate([" +
		                                           "{" +
		                                               "'$group':{_id:'$bloodSugarCondition','count':{$sum:1}}" +
		                                           "}" +
		                                       "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			if(m.get("_id") == null || m.get("_id") ==""){
				returnMap.put("-", (new Double(d)).intValue());
			}else{
				returnMap.put((String)m.get("_id"), (new Double(d)).intValue());
			}
		}
		return returnMap;
	}
	
	/**
	 * 血糖筛查情况->血糖情况人群分布->初筛数据->正常\已登记糖尿病\糖尿病高危\血糖异常36-39  2
	 * @return
	 */
	public Map<String, Object> findBloodSugarConditionPeopleDistributionHeadthCheck2(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","$bloodSugarCondition").append("count", new BasicDBObject("$sum",1)));
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
	/*************************************************************************************************************************************************************************************/
	/**
	 * 血糖筛查情况->血糖情况人群分布->精筛数据->糖尿病高危\新发现糖尿病\新发现糖尿病前期40-42
	 * @return
	 */
	public Map<String, Object> findBloodSugarConditionPeopleDistributionHeadthCheckDetail() {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String code = "db.getCollection('healthcheckDetail').aggregate([" + 
		                                                 "{" +
		                                                        "$addFields:{" +
		                                                             "'diabetesSituation':{" +
		                                                                 "'$cond':[" +
		                                                                     "{" +
		                                                                         "$and:[" +
		                                                                                 "{$lt: ['$ogtt', 6.1 ]},{$lt:['$ogtt2h',7.8]}" +
		                                                                             "]" +         
		                                                                     "}," +
		                                                                     "'糖尿病高危'," + 
		                                                                     "{" +
		                                                                          "'$cond':[" +
		                                                                                 "{" +
		                                                                                     "$and:[" +
		                                                                                             "{$gte: ['$ogtt', 7 ]},{$gte:['$ogtt2h',11.1]}"+
		                                                                                         "]" +         
		                                                                                 "}," +
		                                                                                 "'新发现糖尿病'," + 
		                                                                                 "{" +
		                                                                                     "'$cond':[" +
		                                                                                         "{" +
		                                                                                             "$or:["+
		                                                                                                     "{$and:[{$gte: ['$ogtt', 6.1 ]},{$lt:['$ogtt',7]}]},"+
		                                                                                                     "{$and:[{$gte: ['$ogtt2h', 7.8 ]},{$lt:['$ogtt2h',11.1]}]}" +
		                                                                                                 "]" +         
		                                                                                         "}," +
		                                                                                         "'新发现糖尿病前期'," + 
		                                                                                         "'-'" +
		                                                                                      "]" +
		                                                                                 "}" +
		                                                                           "]" +
		                                                                     "}" +
		                                                                  "]" +    
		                                                             "}" +
		                                                        "}" +
		                                                 "}," +
		                                                 "{" +
		                                                     "'$group':{_id:'$diabetesSituation','count':{$sum:1}}" +
		                                                 "}" +
		                                             "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			if(m.get("_id") == null || m.get("_id") ==""){
				returnMap.put("-", (new Double(d)).intValue());
			}else{
				returnMap.put((String)m.get("_id"), (new Double(d)).intValue());
			}
		}
		return returnMap;
	}
	
	/**
	 * 血糖筛查情况->血糖情况人群分布->精筛数据->糖尿病高危\新发现糖尿病\新发现糖尿病前期40-42   2
	 * @return
	 */
	public Map<String, Object> findBloodSugarConditionPeopleDistributionHeadthCheckDetail2(String district) {
		
		/*String code = "db.getCollection('healthcheckDetail').aggregate([" + 
                "{" +
                       "$addFields:{" +
                            "'diabetesSituation':{" +
                                "'$cond':[" +
                                    "{" +
                                        "$and:[" +
                                                "{$lt: ['$ogtt', 6.1 ]},{$lt:['$ogtt2h',7.8]}" +
                                            "]" +         
                                    "}," +
                                    "'糖尿病高危'," + 
                                    "{" +
                                         "'$cond':[" +
                                                "{" +
                                                    "$and:[" +
                                                            "{$gte: ['$ogtt', 7 ]},{$gte:['$ogtt2h',11.1]}"+
                                                        "]" +         
                                                "}," +
                                                "'新发现糖尿病'," + 
                                                "{" +
                                                    "'$cond':[" +
                                                        "{" +
                                                            "$or:["+
                                                                    "{$and:[{$gte: ['$ogtt', 6.1 ]},{$lt:['$ogtt',7]}]},"+
                                                                    "{$and:[{$gte: ['$ogtt2h', 7.8 ]},{$lt:['$ogtt2h',11.1]}]}" +
                                                                "]" +         
                                                        "}," +
                                                        "'新发现糖尿病前期'," + 
                                                        "'-'" +
                                                     "]" +
                                                "}" +
                                    "}" +
                                 "]" +    
                            "}" +
                       "}" +
                "}," +
                "{" +
                    "'$group':{_id:'$diabetesSituation','count':{$sum:1}}" +
                "}" +
            "])";*/
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		
		 /* "'$cond':[" +
          "{" +
              "$or:["+
                      "{$and:[{$gte: ['$ogtt', 6.1 ]},{$lt:['$ogtt',7]}]},"+
                      "{$and:[{$gte: ['$ogtt2h', 7.8 ]},{$lt:['$ogtt2h',11.1]}]}" +
                  "]" +         
          "}," +
          "'新发现糖尿病前期'," + 
          "'-'" +
       	"]" +*/
		
		BasicDBObject gteOgttDBO1 = new BasicDBObject("$gte",Arrays.asList("$ogtt",6.1));
		BasicDBObject lteOgttDBO1 = new BasicDBObject("$lt",Arrays.asList("$ogtt",7));
		BasicDBObject andDBO1 = new BasicDBObject("$and",Arrays.asList(gteOgttDBO1,lteOgttDBO1));
		
		BasicDBObject gteOgttDBO2 = new BasicDBObject("$gte",Arrays.asList("$ogtt2h",7.8));
		BasicDBObject lteOgttDBO2 = new BasicDBObject("$lt",Arrays.asList("$ogtt2h",11.1));
		BasicDBObject andDBO2 = new BasicDBObject("$and",Arrays.asList(gteOgttDBO2,lteOgttDBO2));
		
		BasicDBObject orDBO1 = new BasicDBObject("$or",Arrays.asList(andDBO1,andDBO2));
		BasicDBObject condDBO1 = new BasicDBObject("$cond",Arrays.asList(orDBO1,"新发现糖尿病前期","-"));
		
		/*"'$cond':[" +
        "{" +
            "$and:[" +
                    "{$gte: ['$ogtt', 7 ]},{$gte:['$ogtt2h',11.1]}"+
                "]" +         
        "}," +
        "'新发现糖尿病'," + 
        "{" +
            "'$cond':[" +
                "{" +
                    "$or:["+
                            "{$and:[{$gte: ['$ogtt', 6.1 ]},{$lt:['$ogtt',7]}]},"+
                            "{$and:[{$gte: ['$ogtt2h', 7.8 ]},{$lt:['$ogtt2h',11.1]}]}" +
                        "]" +         
                "}," +
                "'新发现糖尿病前期'," + 
                "'-'" +
             "]" +
        "}" +*/
		
		BasicDBObject gteOgttDBO3 = new BasicDBObject("$gte",Arrays.asList("$ogtt",7));
		BasicDBObject gteOgtt2hDBO1 = new BasicDBObject("$gte",Arrays.asList("$ogtt2h",11.1));
		BasicDBObject andDBO3 = new BasicDBObject("$and",Arrays.asList(gteOgttDBO3,gteOgtt2hDBO1));
		BasicDBObject condDBO2 = new BasicDBObject("$cond",Arrays.asList(andDBO3,"新发现糖尿病",condDBO1));
		
		 /*"'$cond':[" +
         "{" +
             "$and:[" +
                     "{$lt: ['$ogtt', 6.1 ]},{$lt:['$ogtt2h',7.8]}" +
                 "]" +         
         "}," +
         "'糖尿病高危'," + 
         "{" +
              "'$cond':[" +
                     "{" +
                         "$and:[" +
                                 "{$gte: ['$ogtt', 7 ]},{$gte:['$ogtt2h',11.1]}"+
                             "]" +         
                     "}," +
                     "'新发现糖尿病'," + 
                     "{" +
                         "'$cond':[" +
                             "{" +
                                 "$or:["+
                                         "{$and:[{$gte: ['$ogtt', 6.1 ]},{$lt:['$ogtt',7]}]},"+
                                         "{$and:[{$gte: ['$ogtt2h', 7.8 ]},{$lt:['$ogtt2h',11.1]}]}" +
                                     "]" +         
                             "}," +
                             "'新发现糖尿病前期'," + 
                             "'-'" +
                          "]" +
                     "}" +
         "}" +
      	"]" +*/
		
		BasicDBObject ltOgttDBO1 = new BasicDBObject("$lt",Arrays.asList("$ogtt",6.1));
		BasicDBObject ltOgtt2hDBO1 = new BasicDBObject("$lt",Arrays.asList("$ogtt2h",7.8));
		BasicDBObject andDBO4 = new BasicDBObject("$and",Arrays.asList(ltOgttDBO1,ltOgtt2hDBO1));
		BasicDBObject condDBO3 = new BasicDBObject("$cond",Arrays.asList(andDBO4,"糖尿病高危",condDBO2));
		
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("diabetesSituation",condDBO3));
		aggreList.add(addFieldsDBO);
		
		
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","$diabetesSituation").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put((String)dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	/*************************************************************************************************************************************************************************************/
	/**
	 * 血糖筛查情况->血糖情况年龄分布->初筛数据->已登记糖尿病\糖尿病高危\血糖异常43-45
	 * @return
	 */
	public Map<Object, Object> findBloodSugarConditionAgeDistributionHeadthCheck() {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		String code = "db.getCollection('healthcheck').aggregate([" + 
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
			                                                "'then':'50-55',"+                                                
			                                                "'else':{"+
			                                                         "'$cond':{"+
			                                                            "'if':{'$in':['$age',[55,56,57,58,59]]},"+
			                                                            "'then':'55-60',"+
			                                                            "'else':{"+
			                                                                     "'$cond':{"+
			                                                                        "'if':{'$in':['$age',[60,61,62,63,64]]},"+
			                                                                        "'then':'60-65',"+
			                                                                        "'else':{"+
			                                                                                 "'$cond':{"+
			                                                                                    "'if':{'$in':['$age',[65,66,67,68,69]]},"+
			                                                                                    "'then':'65-70',"+
			                                                                                    "'else':{"+
			                                                                                            "'$cond':{"+
			                                                                                                "'if':{'$in':['$age',[70,71,72,73,74]]},"+
			                                                                                                "'then':'70-75',"+
			                                                                                                "'else':{"+
			                                                                                                       "'$cond':{"+
			                                                                                                            "'if':{'$in':['$age',[75,76,77,78,79]]},"+
			                                                                                                            "'then':'75-80',"+
			                                                                                                            "'else':{"+
			                                                                                                                    "'$cond':{"+
			                                                                                                                        "'if':{'$in':['$age',[80,81,82,83,84]]},"+
			                                                                                                                        "'then':'80-85',"+
			                                                                                                                       "'else':{"+
			                                                                                                                               "'$cond':{"+
			                                                                                                                                    "'if':{'$in':['$age',[85,86,87,88,89]]},"+
			                                                                                                                                    "'then':'85-90',"+
			                                                                                                                                    "'else':{"+
			                                                                                                                                            "'$cond':{"+
			                                                                                                                                                "'if':{'$in':['$age',[90,91,92,93,94]]},"+
			                                                                                                                                                "'then':'90-95',"+ 
			                                                                                                                                                "'else':{"+
			                                                                                                                                                      "'$cond':{"+
			                                                                                                                                                        "'if':{'$in':['$age',[30,31,32,33,34]]},"+
			                                                                                                                                                        "'then':'30-35',"+
			                                                                                                                                                        "'else':{"+
			                                                                                                                                                            "'$cond':{"+
			                                                                                                                                                            "'if':{'$in':['$age',[25,26,27,28,29]]},"+
			                                                                                                                                                            "'then':'25-30',"+
			                                                                                                                                                            "'else':{"+
			                                                                                                                                                                    "'$cond':{"+
			                                                                                                                                                                    "'if':{'$in':['$age',[20,21,22,23,24]]},"+
			                                                                                                                                                                    "'then':'20-25',"+
			                                                                                                                                                                    "'else':{"+
			                                                                                                                                                                            "'$cond':{"+
			                                                                                                                                                                            "'if':{'$in':['$age',[95,96,97,98,99]]},"+
			                                                                                                                                                                            "'then':'95-100',"+
			                                                                                                                                                                            "'else':'-'"+
			                                                                                                                                                                            "}"+
			                                                                                                                                                                        "}"+
			                                                                                                                                                                    "}"+ 
			                                                                                                                                                                "}"+
			                                                                                                                                                            "}"+  
			                                                                                                                                                            "}"+
			                                                                                                                                                        "}"+   
			                                                                                                                                                  "}"+
			                                                                                                                                                "}"+ 
			                                                                                                                                        "}"+
			                                                                                                                                    "}"+ 
			                                                                                                                            "}"+
			                                                                                                                        "}"+
			                                                                                                                "}"+
			                                                                                                            "}"+ 
			                                                                                                    "}"+
			                                                                                                "}"+ 
			                                                                                        "}"+
			                                                                                    "}"+
			                                                                            "}"+
			                                                                        "}"+
			                                                                "}"+
			                                                            "}"+
			                                                    "}"+
			                                                "}"+  
			                                            "}"+
			                                        "}"+ 
			                                "}"+
			                            "}"+                    
			                    "}"+
			                "}"+            
			            "}"+
			        "}"+
			"},"+
			"{"+
			    "'$group':{_id:{'bs':'$bloodSugarCondition','ages':'$ages'},'count':{$sum:1}}"+
			"}" +
		    "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			returnMap.put(m.get("_id"), (new Double(d)).intValue());
		}
		return returnMap;
	}
	public Map<Object, Object>  findBloodSugarConditionAgeDistributionHeadthCheck2(String district, List<Map<String,Object>> ageList) {
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		BasicDBObject condDBO = null;
		for (Map<String,Object> ageMap:ageList) {
			List<Integer> inList = new ArrayList<Integer>();
			Integer start = (Integer)ageMap.get("start");
			String title = (String)ageMap.get("title");
			if (start == null) {
				start = 0;
			}
			Integer end = (Integer)ageMap.get("end");
			if (end == null) {
				end = 100;
			}
			for (int i = start; i <= end; i++) {
				inList.add(i);
			}
			BasicDBObject inAgeDBO = new BasicDBObject("$in",Arrays.asList("$age",inList));
			if (condDBO == null) {
				condDBO = new BasicDBObject("$cond",Arrays.asList(inAgeDBO,title,"-"));
			} else {
				condDBO = new BasicDBObject("$cond",Arrays.asList(inAgeDBO,title,condDBO));
			}
		}
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bs","$bloodSugarCondition").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		DBCollection coll = db.getCollection("customer");
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		for(DBObject dbo : result){
			returnMap.put(dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;

	}
	/**
	 * 血糖筛查情况->血糖情况年龄分布->初筛数据->已登记糖尿病\糖尿病高危\血糖异常43-45  2
	 * @param district 筛查区域
	 * @return
	 */
//	public Map<Object, Object> findBloodSugarConditionAgeDistributionHeadthCheck2(String district) {
//		Map<Object,Object> returnMap = new HashMap<Object,Object>();
//		DBCollection coll = db.getCollection("customer");
//		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
//		
//		if (StringUtils.isNotEmpty(district)) {
//			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
//			aggreList.add(districtOBO);
//		}
//		
//		/*[60,61,62,63,64]*/
//		List<String> inList11 = new ArrayList<String>();
//		inList11.add("60");
//		inList11.add("61"); 
//		inList11.add("62"); 
//		inList11.add("63"); 
//		inList11.add("64");
//		inList11.add("65");
//		
//		BasicDBObject inAgeDBO11 = new BasicDBObject("$in",Arrays.asList("$age",inList11));
//		BasicDBObject condDBO11 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO11,"60-65","-"));
//		/*[55,56,57,58,59]*/
//		List<String> inList12 = new ArrayList<String>();
//		inList12.add("55");
//		inList12.add("56"); 
//		inList12.add("57"); 
//		inList12.add("58"); 
//		inList12.add("59"); 
//		
//		BasicDBObject inAgeDBO12 = new BasicDBObject("$in",Arrays.asList("$age",inList12));
//		BasicDBObject condDBO12 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO12,"55-59",condDBO11));
//		/*[50,51,52,53,54]*/
//		List<String> inList13 = new ArrayList<String>();
//		inList13.add("50");
//		inList13.add("51"); 
//		inList13.add("52"); 
//		inList13.add("53"); 
//		inList13.add("54"); 
//		
//		BasicDBObject inAgeDBO13 = new BasicDBObject("$in",Arrays.asList("$age",inList13));
//		BasicDBObject condDBO13 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO13,"50-54",condDBO12));
//		/*[45,46,47,48,49]*/
//		List<String> inList14 = new ArrayList<String>();
//		inList14.add("45");
//		inList14.add("46"); 
//		inList14.add("47"); 
//		inList14.add("48"); 
//		inList14.add("49"); 
//		
//		BasicDBObject inAgeDBO14 = new BasicDBObject("$in",Arrays.asList("$age",inList14));
//		BasicDBObject condDBO14 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO14,"45-49",condDBO13));
//		/*[40,41,42,43,44]*/
//		List<String> inList15 = new ArrayList<String>();
//		inList15.add("40");
//		inList15.add("41"); 
//		inList15.add("42"); 
//		inList15.add("43"); 
//		inList15.add("44"); 
//		
//		BasicDBObject inAgeDBO15 = new BasicDBObject("$in",Arrays.asList("$age",inList15));
//		BasicDBObject condDBO15 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO15,"40-44",condDBO14));
//		/*[35,36,37,38,39]*/
//		List<String> inList16 = new ArrayList<String>();
//		inList16.add("35");
//		inList16.add("36"); 
//		inList16.add("37"); 
//		inList16.add("38"); 
//		inList16.add("39"); 
//		
//		BasicDBObject inAgeDBO16 = new BasicDBObject("$in",Arrays.asList("$age",inList16));
//		BasicDBObject condDBO16 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO16,"35-39",condDBO15));
//		
//		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO16));
//		aggreList.add(addFieldsDBO);
//		
//		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bs","$bloodSugarCondition").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
//		aggreList.add(groupDBO);
//		
//		Iterable<DBObject> result = coll.aggregate(aggreList).results();
//		for(DBObject dbo : result){
//			returnMap.put(dbo.get("_id"), dbo.get("count"));
//		}
//		return returnMap;
//		
//	}
	/*************************************************************************************************************************************************************************************/
	/**
	 * 血糖筛查情况->血糖情况年龄分布->精筛数据->糖尿病高危\新发现糖尿病\新发现糖尿病前期46-48
	 * @return
	 */
	public Map<Object, Object> findBloodSugarConditionAgeDistributionHeadthCheckDetail() {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		String code = "db.getCollection('healthcheckDetail').aggregate([" + 
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
			                                                "'then':'50-55',"+                                                
			                                                "'else':{"+
			                                                         "'$cond':{"+
			                                                            "'if':{'$in':['$age',[55,56,57,58,59]]},"+
			                                                            "'then':'55-60',"+
			                                                            "'else':{"+
			                                                                     "'$cond':{"+
			                                                                        "'if':{'$in':['$age',[60,61,62,63,64]]},"+
			                                                                        "'then':'60-65',"+
			                                                                        "'else':{"+
			                                                                                 "'$cond':{"+
			                                                                                    "'if':{'$in':['$age',[65,66,67,68,69]]},"+
			                                                                                    "'then':'65-70',"+
			                                                                                    "'else':{"+
			                                                                                            "'$cond':{"+
			                                                                                                "'if':{'$in':['$age',[70,71,72,73,74]]},"+
			                                                                                                "'then':'70-75',"+
			                                                                                                "'else':{"+
			                                                                                                       "'$cond':{"+
			                                                                                                            "'if':{'$in':['$age',[75,76,77,78,79]]},"+
			                                                                                                            "'then':'75-80',"+
			                                                                                                            "'else':{"+
			                                                                                                                    "'$cond':{"+
			                                                                                                                        "'if':{'$in':['$age',[80,81,82,83,84]]},"+
			                                                                                                                        "'then':'80-85',"+
			                                                                                                                       "'else':{"+
			                                                                                                                               "'$cond':{"+
			                                                                                                                                    "'if':{'$in':['$age',[85,86,87,88,89]]},"+
			                                                                                                                                    "'then':'85-90',"+
			                                                                                                                                    "'else':{"+
			                                                                                                                                            "'$cond':{"+
			                                                                                                                                                "'if':{'$in':['$age',[90,91,92,93,94]]},"+
			                                                                                                                                                "'then':'90-95',"+ 
			                                                                                                                                                "'else':{"+
			                                                                                                                                                      "'$cond':{"+
			                                                                                                                                                        "'if':{'$in':['$age',[30,31,32,33,34]]},"+
			                                                                                                                                                        "'then':'30-35',"+
			                                                                                                                                                        "'else':{"+
			                                                                                                                                                            "'$cond':{"+
			                                                                                                                                                            "'if':{'$in':['$age',[25,26,27,28,29]]},"+
			                                                                                                                                                            "'then':'25-30',"+
			                                                                                                                                                            "'else':{"+
			                                                                                                                                                                    "'$cond':{"+
			                                                                                                                                                                    "'if':{'$in':['$age',[20,21,22,23,24]]},"+
			                                                                                                                                                                    "'then':'20-25',"+
			                                                                                                                                                                    "'else':{"+
			                                                                                                                                                                            "'$cond':{"+
			                                                                                                                                                                            "'if':{'$in':['$age',[95,96,97,98,99]]},"+
			                                                                                                                                                                            "'then':'95-100',"+
			                                                                                                                                                                            "'else':'-'"+
			                                                                                                                                                                            "}"+
			                                                                                                                                                                        "}"+
			                                                                                                                                                                    "}"+ 
			                                                                                                                                                                "}"+
			                                                                                                                                                            "}"+  
			                                                                                                                                                            "}"+
			                                                                                                                                                        "}"+   
			                                                                                                                                                  "}"+
			                                                                                                                                                "}"+ 
			                                                                                                                                        "}"+
			                                                                                                                                    "}"+ 
			                                                                                                                            "}"+
			                                                                                                                        "}"+
			                                                                                                                "}"+
			                                                                                                            "}"+ 
			                                                                                                    "}"+
			                                                                                                "}"+ 
			                                                                                        "}"+
			                                                                                    "}"+
			                                                                            "}"+
			                                                                        "}"+
			                                                                "}"+
			                                                            "}"+
			                                                    "}"+
			                                                "}"+  
			                                            "}"+
			                                        "}"+ 
			                                "}"+
			                            "}"+                    
			                    "}"+
			                "}"+            
			            "}"+
			        "}"+
			"},"+
			"{" +
			            "$addFields:{" +
			                 "'diabetesSituation':{" +
			                     "'$cond':[" +
			                         "{" +
			                             "$and:[" +
			                                     "{$lt: ['$ogtt', 6.1 ]},{$lt:['$ogtt2h',7.8]}"+
			                                 "]" +         
			                         "}," +
			                         "'糖尿病高危'," + 
			                         "{" +
			                              "'$cond':[" +
			                                     "{" +
			                                         "$and:[" +
			                                                 "{$gte: ['$ogtt', 7 ]},{$gte:['$ogtt2h',11.1]}" +
			                                             "]" +         
			                                     "}," +
			                                     "'新发现糖尿病'," + 
			                                     "{" +
			                                         "'$cond':[" +
			                                             "{" +
			                                                 "$or:[" +
			                                                          "{$and:[{$gte: ['$ogtt', 6.1 ]},{$lt:['$ogtt',7]}]}," +
			                                                          "{$and:[{$gte: ['$ogtt2h', 7.8 ]},{$lt:['$ogtt2h',11.1]}]}" +
			                                                     "]" +         
			                                             "}," +
			                                             "'新发现糖尿病前期'," + 
			                                             "'-'" +
			                                          "]" +
			                                     "}" +
			                               "]" +
			                         "}" +
			                      "]" +    
			                 "}" +
			            "}" +
			     "}," +
			"{"+
			    "'$group':{_id:{'ds':'$diabetesSituation','ages':'$ages'},'count':{$sum:1}}"+
			"}" +
		    "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			returnMap.put(m.get("_id"), (new Double(d)).intValue());
		}
		return returnMap;
	}
	
	/**
	 * 血糖筛查情况->血糖情况年龄分布->精筛数据->糖尿病高危\新发现糖尿病\新发现糖尿病前期46-48 2
	 * @return
	 */
	public Map<Object, Object> findBloodSugarConditionAgeDistributionHeadthCheckDetail2(String district) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		
		/*['$age',[95,96,97,98,99]*/
		List<Integer> inList1 = new ArrayList<Integer>();
		inList1.add(95);
		inList1.add(96); 
		inList1.add(97); 
		inList1.add(98); 
		inList1.add(99); 
			
		BasicDBObject inAgeDBO1 = new BasicDBObject("$in",Arrays.asList("$age",inList1));
		BasicDBObject condDBO1 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO1,"95-100","-"));
		
		/*[20,21,22,23,24]*/
		List<Integer> inList2 = new ArrayList<Integer>();
		inList2.add(20);
		inList2.add(21); 
		inList2.add(22); 
		inList2.add(23); 
		inList2.add(24); 
		
		BasicDBObject inAgeDBO2 = new BasicDBObject("$in",Arrays.asList("$age",inList2));
		BasicDBObject condDBO2 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO2,"20-25",condDBO1));
		
		/*[25,26,27,28,29]*/
		List<Integer> inList3 = new ArrayList<Integer>();
		inList3.add(25);
		inList3.add(26); 
		inList3.add(27); 
		inList3.add(28); 
		inList3.add(29); 
		
		BasicDBObject inAgeDBO3 = new BasicDBObject("$in",Arrays.asList("$age",inList3));
		BasicDBObject condDBO3 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO3,"25-30",condDBO2));
		
		/*[30,31,32,33,34]*/
		List<Integer> inList4 = new ArrayList<Integer>();
		inList4.add(30);
		inList4.add(31); 
		inList4.add(32); 
		inList4.add(33); 
		inList4.add(34); 
		
		BasicDBObject inAgeDBO4 = new BasicDBObject("$in",Arrays.asList("$age",inList4));
		BasicDBObject condDBO4 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO4,"30-35",condDBO3));
		
		/*[90,91,92,93,94]*/
		List<Integer> inList5 = new ArrayList<Integer>();
		inList5.add(90);
		inList5.add(91); 
		inList5.add(92); 
		inList5.add(93); 
		inList5.add(94); 
		
		BasicDBObject inAgeDBO5 = new BasicDBObject("$in",Arrays.asList("$age",inList5));
		BasicDBObject condDBO5 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO5,"90-95",condDBO4));
		
		/*[85,86,87,88,89]*/
		List<Integer> inList6 = new ArrayList<Integer>();
		inList6.add(85);
		inList6.add(86); 
		inList6.add(87); 
		inList6.add(88); 
		inList6.add(89); 
		
		BasicDBObject inAgeDBO6 = new BasicDBObject("$in",Arrays.asList("$age",inList6));
		BasicDBObject condDBO6 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO6,"85-90",condDBO5));
		
		/*[80,81,82,83,84]*/
		List<Integer> inList7 = new ArrayList<Integer>();
		inList7.add(80);
		inList7.add(81); 
		inList7.add(82); 
		inList7.add(83); 
		inList7.add(84); 
		
		BasicDBObject inAgeDBO7 = new BasicDBObject("$in",Arrays.asList("$age",inList7));
		BasicDBObject condDBO7 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO7,"80-85",condDBO6));
		
		/*[75,76,77,78,79]*/
		List<Integer> inList8 = new ArrayList<Integer>();
		inList8.add(75);
		inList8.add(76); 
		inList8.add(77); 
		inList8.add(78); 
		inList8.add(79); 
		
		BasicDBObject inAgeDBO8 = new BasicDBObject("$in",Arrays.asList("$age",inList8));
		BasicDBObject condDBO8 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO8,"75-80",condDBO7));
		/*[70,71,72,73,74]*/
		List<Integer> inList9 = new ArrayList<Integer>();
		inList9.add(70);
		inList9.add(71); 
		inList9.add(72); 
		inList9.add(73); 
		inList9.add(74); 
		
		BasicDBObject inAgeDBO9 = new BasicDBObject("$in",Arrays.asList("$age",inList9));
		BasicDBObject condDBO9 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO9,"70-75",condDBO8));
		/*[65,66,67,68,69]*/
		List<Integer> inList10 = new ArrayList<Integer>();
		inList10.add(65);
		inList10.add(66); 
		inList10.add(67); 
		inList10.add(68); 
		inList10.add(69); 
		
		BasicDBObject inAgeDBO10 = new BasicDBObject("$in",Arrays.asList("$age",inList10));
		BasicDBObject condDBO10 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO10,"65-70",condDBO9));
		/*[60,61,62,63,64]*/
		List<Integer> inList11 = new ArrayList<Integer>();
		inList11.add(60);
		inList11.add(61); 
		inList11.add(62); 
		inList11.add(63); 
		inList11.add(64); 
		
		BasicDBObject inAgeDBO11 = new BasicDBObject("$in",Arrays.asList("$age",inList11));
		BasicDBObject condDBO11 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO11,"60-65",condDBO10));
		/*[55,56,57,58,59]*/
		List<Integer> inList12 = new ArrayList<Integer>();
		inList12.add(55);
		inList12.add(56); 
		inList12.add(57); 
		inList12.add(58); 
		inList12.add(59); 
		
		BasicDBObject inAgeDBO12 = new BasicDBObject("$in",Arrays.asList("$age",inList12));
		BasicDBObject condDBO12 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO12,"55-60",condDBO11));
		/*[50,51,52,53,54]*/
		List<Integer> inList13 = new ArrayList<Integer>();
		inList13.add(50);
		inList13.add(51); 
		inList13.add(52); 
		inList13.add(53); 
		inList13.add(54); 
		
		BasicDBObject inAgeDBO13 = new BasicDBObject("$in",Arrays.asList("$age",inList13));
		BasicDBObject condDBO13 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO13,"50-55",condDBO12));
		/*[45,46,47,48,49]*/
		List<Integer> inList14 = new ArrayList<Integer>();
		inList14.add(45);
		inList14.add(46); 
		inList14.add(47); 
		inList14.add(48); 
		inList14.add(49); 
		
		BasicDBObject inAgeDBO14 = new BasicDBObject("$in",Arrays.asList("$age",inList14));
		BasicDBObject condDBO14 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO14,"45-50",condDBO13));
		/*[40,41,42,43,44]*/
		List<Integer> inList15 = new ArrayList<Integer>();
		inList15.add(40);
		inList15.add(41); 
		inList15.add(42); 
		inList15.add(43); 
		inList15.add(44); 
		
		BasicDBObject inAgeDBO15 = new BasicDBObject("$in",Arrays.asList("$age",inList15));
		BasicDBObject condDBO15 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO15,"40-45",condDBO14));
		/*[35,36,37,38,39]*/
		List<Integer> inList16 = new ArrayList<Integer>();
		inList16.add(35);
		inList16.add(36); 
		inList16.add(37); 
		inList16.add(38); 
		inList16.add(39); 
		
		BasicDBObject inAgeDBO16 = new BasicDBObject("$in",Arrays.asList("$age",inList16));
		BasicDBObject condDBO16 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO16,"35-40",condDBO15));
		
		BasicDBObject addFieldsDBO1 = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO16));
		aggreList.add(addFieldsDBO1);
		
		
		BasicDBObject gteOgttDBO1 = new BasicDBObject("$gte",Arrays.asList("$ogtt",6.1));
		BasicDBObject lteOgttDBO1 = new BasicDBObject("$lt",Arrays.asList("$ogtt",7));
		BasicDBObject andDBO1 = new BasicDBObject("$and",Arrays.asList(gteOgttDBO1,lteOgttDBO1));
		
		BasicDBObject gteOgttDBO2 = new BasicDBObject("$gte",Arrays.asList("$ogtt2h",7.8));
		BasicDBObject lteOgttDBO2 = new BasicDBObject("$lt",Arrays.asList("$ogtt2h",11.1));
		BasicDBObject andDBO2 = new BasicDBObject("$and",Arrays.asList(gteOgttDBO2,lteOgttDBO2));
		
		BasicDBObject orDBO1 = new BasicDBObject("$or",Arrays.asList(andDBO1,andDBO2));
		BasicDBObject condDBO111 = new BasicDBObject("$cond",Arrays.asList(orDBO1,"新发现糖尿病前期","-"));
		
		BasicDBObject gteOgttDBO3 = new BasicDBObject("$gte",Arrays.asList("$ogtt",7));
		BasicDBObject gteOgtt2hDBO1 = new BasicDBObject("$gte",Arrays.asList("$ogtt2h",11.1));
		BasicDBObject andDBO3 = new BasicDBObject("$and",Arrays.asList(gteOgttDBO3,gteOgtt2hDBO1));
		BasicDBObject condDBO222 = new BasicDBObject("$cond",Arrays.asList(andDBO3,"新发现糖尿病",condDBO111));
		
		BasicDBObject ltOgttDBO1 = new BasicDBObject("$lt",Arrays.asList("$ogtt",6.1));
		BasicDBObject ltOgtt2hDBO1 = new BasicDBObject("$lt",Arrays.asList("$ogtt2h",7.8));
		BasicDBObject andDBO4 = new BasicDBObject("$and",Arrays.asList(ltOgttDBO1,ltOgtt2hDBO1));
		BasicDBObject condDBO333 = new BasicDBObject("$cond",Arrays.asList(andDBO4,"糖尿病高危",condDBO222));
		
		
		BasicDBObject addFieldsDBO2 = new BasicDBObject("$addFields",new BasicDBObject("diabetesSituation",condDBO333));
		aggreList.add(addFieldsDBO2);
		
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("ds","$diabetesSituation").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put(dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	/*************************************************************************************************************************************************************************************/
	/**
	 * 血压筛查情况->血压情况人群分布->初筛数据->正常\已登记高血压\血压异常51-53
	 * @return
	 */
	public Map<String, Object> findBloodPressureConditionPeopleDistributionHeadthCheck() {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String code = "db.getCollection('healthcheck').aggregate([" +
		                                           "{" +
		                                               "'$group':{_id:'$bloodPressureCondition','count':{$sum:1}}" +
		                                           "}" +
		                                       "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			if(m.get("_id") == null || m.get("_id") ==""){
				returnMap.put("-", (new Double(d)).intValue());
			}else{
				returnMap.put((String)m.get("_id"), (new Double(d)).intValue());
			}
		}
		return returnMap;
	}
	
	/**
	 * 血压筛查情况->血压情况人群分布->初筛数据->正常\已登记高血压\血压异常51-53 2
	 * @return
	 */
	public Map<String, Object> findBloodPressureConditionPeopleDistributionHeadthCheck2(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject groupDistrict = new BasicDBObject("$match",new BasicDBObject("district",district));
			aggreList.add(groupDistrict);
		}
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","$bloodPressureCondition").append("count", new BasicDBObject("$sum",1)));
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
	/*************************************************************************************************************************************************************************************/
	/**
	 * 血压筛查情况->血压情况人群分布->精筛数据->血压异常\新发现高血压54-55
	 * @return
	 */
	public Map<String, Object> findBloodPressureConditionPeopleDistributionHeadthCheckDetail() {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String code = "db.getCollection('healthcheckDetail').aggregate([" +
		                                                 "{" +
		                                                        "$addFields:{" +
		                                                             "'bloodPressureConditionCharge':{" +
		                                                                 "'$cond':[" +
		                                                                     "{" +
		                                                                         "$and:[" +
		                                                                                 "{$lt: ['$highPressure2', 140 ]},{$lt:['$lowPressure2',90]}" +
		                                                                             "]" +         
		                                                                     "}," +
		                                                                     "'血压异常'," + 
		                                                                     "{" +
		                                                                          "'$cond':[" +
		                                                                                 "{" +
		                                                                                     "$or:[" +
		                                                                                             "{$gte: ['$highPressure2', 140 ]},{$gte:['$lowPressure2',90]}" +
		                                                                                         "]" +         
		                                                                                 "}," +
		                                                                                 "'新发现高血压'," + 
		                                                                                 "'-'" +
		                                                                           "]" +
		                                                                     "}" +
		                                                                  "]" +    
		                                                             "}" +
		                                                        "}" +
		                                                 "}," +
		                                                 "{" +
		                                                     "'$group':{_id:'$bloodPressureConditionCharge','count':{$sum:1}}" +
		                                                 "}" +
		                                             "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			if(m.get("_id") == null || m.get("_id") ==""){
				returnMap.put("-", (new Double(d)).intValue());
			}else{
				returnMap.put((String)m.get("_id"), (new Double(d)).intValue());
			}
		}
		return returnMap;
	}
	
	/**
	 * 血压筛查情况->血压情况人群分布->精筛数据->血压异常\新发现高血压54-55  2
	 * @return
	 */
	public Map<String, Object> findBloodPressureConditionPeopleDistributionHeadthCheckDetail2(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		if (district != null) {
			BasicDBObject groupDistrict = new BasicDBObject("$match",new BasicDBObject("district",district));
			aggreList.add(groupDistrict);
		}
		
		BasicDBObject gteHighPressure21 = new BasicDBObject("$gte",Arrays.asList("$highPressure",130));
		BasicDBObject gteLowPressure21 = new BasicDBObject("$gte",Arrays.asList("$lowPressure",85));
		BasicDBObject orDBO1 = new BasicDBObject("$or",Arrays.asList(gteHighPressure21,gteLowPressure21));
		
		BasicDBObject condDBO1 = new BasicDBObject("$cond",Arrays.asList(orDBO1,"新发现高血压","-"));
		
		BasicDBObject ltHighPressure21 = new BasicDBObject("$lt",Arrays.asList("$highPressure",130));
		BasicDBObject ltLowPressure21 = new BasicDBObject("$lt",Arrays.asList("$lowPressure",85));
		BasicDBObject andDBO1 = new BasicDBObject("$and",Arrays.asList(ltHighPressure21,ltLowPressure21));
		
		BasicDBObject condDBO2 = new BasicDBObject("$cond",Arrays.asList(andDBO1,"血压异常",condDBO1));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("bloodPressureConditionCharge",condDBO2));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","$bloodPressureConditionCharge").append("count", new BasicDBObject("$sum",1)));
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
	/*************************************************************************************************************************************************************************************/
	/**
	 * 血压筛查情况->血压情况年龄分布->初筛数据->正常\已登记高血压\血压异常56-58
	 * @return
	 */
	public Map<Object, Object> findBloodPressureConditionAgeDistributionHeadthCheck() {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		String code = "db.getCollection('healthcheck').aggregate([" + 
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
			                                                "'then':'50-55',"+                                                
			                                                "'else':{"+
			                                                         "'$cond':{"+
			                                                            "'if':{'$in':['$age',[55,56,57,58,59]]},"+
			                                                            "'then':'55-60',"+
			                                                            "'else':{"+
			                                                                     "'$cond':{"+
			                                                                        "'if':{'$in':['$age',[60,61,62,63,64]]},"+
			                                                                        "'then':'60-65',"+
			                                                                        "'else':{"+
			                                                                                 "'$cond':{"+
			                                                                                    "'if':{'$in':['$age',[65,66,67,68,69]]},"+
			                                                                                    "'then':'65-70',"+
			                                                                                    "'else':{"+
			                                                                                            "'$cond':{"+
			                                                                                                "'if':{'$in':['$age',[70,71,72,73,74]]},"+
			                                                                                                "'then':'70-75',"+
			                                                                                                "'else':{"+
			                                                                                                       "'$cond':{"+
			                                                                                                            "'if':{'$in':['$age',[75,76,77,78,79]]},"+
			                                                                                                            "'then':'75-80',"+
			                                                                                                            "'else':{"+
			                                                                                                                    "'$cond':{"+
			                                                                                                                        "'if':{'$in':['$age',[80,81,82,83,84]]},"+
			                                                                                                                        "'then':'80-85',"+
			                                                                                                                       "'else':{"+
			                                                                                                                               "'$cond':{"+
			                                                                                                                                    "'if':{'$in':['$age',[85,86,87,88,89]]},"+
			                                                                                                                                    "'then':'85-90',"+
			                                                                                                                                    "'else':{"+
			                                                                                                                                            "'$cond':{"+
			                                                                                                                                                "'if':{'$in':['$age',[90,91,92,93,94]]},"+
			                                                                                                                                                "'then':'90-95',"+ 
			                                                                                                                                                "'else':{"+
			                                                                                                                                                      "'$cond':{"+
			                                                                                                                                                        "'if':{'$in':['$age',[30,31,32,33,34]]},"+
			                                                                                                                                                        "'then':'30-35',"+
			                                                                                                                                                        "'else':{"+
			                                                                                                                                                            "'$cond':{"+
			                                                                                                                                                            "'if':{'$in':['$age',[25,26,27,28,29]]},"+
			                                                                                                                                                            "'then':'25-30',"+
			                                                                                                                                                            "'else':{"+
			                                                                                                                                                                    "'$cond':{"+
			                                                                                                                                                                    "'if':{'$in':['$age',[20,21,22,23,24]]},"+
			                                                                                                                                                                    "'then':'20-25',"+
			                                                                                                                                                                    "'else':{"+
			                                                                                                                                                                            "'$cond':{"+
			                                                                                                                                                                            "'if':{'$in':['$age',[95,96,97,98,99]]},"+
			                                                                                                                                                                            "'then':'95-100',"+
			                                                                                                                                                                            "'else':'-'"+
			                                                                                                                                                                            "}"+
			                                                                                                                                                                        "}"+
			                                                                                                                                                                    "}"+ 
			                                                                                                                                                                "}"+
			                                                                                                                                                            "}"+  
			                                                                                                                                                            "}"+
			                                                                                                                                                        "}"+   
			                                                                                                                                                  "}"+
			                                                                                                                                                "}"+ 
			                                                                                                                                        "}"+
			                                                                                                                                    "}"+ 
			                                                                                                                            "}"+
			                                                                                                                        "}"+
			                                                                                                                "}"+
			                                                                                                            "}"+ 
			                                                                                                    "}"+
			                                                                                                "}"+ 
			                                                                                        "}"+
			                                                                                    "}"+
			                                                                            "}"+
			                                                                        "}"+
			                                                                "}"+
			                                                            "}"+
			                                                    "}"+
			                                                "}"+  
			                                            "}"+
			                                        "}"+ 
			                                "}"+
			                            "}"+                    
			                    "}"+
			                "}"+            
			            "}"+
			        "}"+
			"},"+
			"{"+
			    "'$group':{_id:{'bpc':'$bloodPressureCondition','ages':'$ages'},'count':{$sum:1}}"+
			"}" +
		    "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			returnMap.put(m.get("_id"), (new Double(d)).intValue());
		}
		return returnMap;
	}
	
	/**
	 * 血压筛查情况->血压情况年龄分布->初筛数据->正常\已登记高血压\血压异常56-58 2
	 * @return
	 */
	public Map<Object, Object>  findBloodPressureConditionAgeDistributionHeadthCheck2(String district, List<Map<String,Object>> ageList) {
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		
		BasicDBObject condDBO = ageDistribution(ageList);
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bpc","$bloodPressureCondition").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		DBCollection coll = db.getCollection("customer");
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		for(DBObject dbo : result){
			returnMap.put(dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;

	}
//	public Map<Object, Object> findBloodPressureConditionAgeDistributionHeadthCheck2(String district) {
//		Map<Object,Object> returnMap = new HashMap<Object,Object>();
//		DBCollection coll = db.getCollection("customer");
//		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
//		
//		if (district != null) {
//			BasicDBObject groupDistrict = new BasicDBObject("$match",new BasicDBObject("district",district));
//			aggreList.add(groupDistrict);
//		}
//		
//		/*[60,61,62,63,64]*/
//		List<String> inList11 = new ArrayList<String>();
//		inList11.add("60");
//		inList11.add("61"); 
//		inList11.add("62"); 
//		inList11.add("63"); 
//		inList11.add("64");
//		inList11.add("65");
//		
//		BasicDBObject inAgeDBO11 = new BasicDBObject("$in",Arrays.asList("$age",inList11));
//		BasicDBObject condDBO11 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO11,"60-65","-"));
//		/*[55,56,57,58,59]*/
//		List<String> inList12 = new ArrayList<String>();
//		inList12.add("55");
//		inList12.add("56"); 
//		inList12.add("57"); 
//		inList12.add("58"); 
//		inList12.add("59"); 
//		
//		BasicDBObject inAgeDBO12 = new BasicDBObject("$in",Arrays.asList("$age",inList12));
//		BasicDBObject condDBO12 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO12,"55-59",condDBO11));
//		/*[50,51,52,53,54]*/
//		List<String> inList13 = new ArrayList<String>();
//		inList13.add("50");
//		inList13.add("51"); 
//		inList13.add("52"); 
//		inList13.add("53"); 
//		inList13.add("54"); 
//		
//		BasicDBObject inAgeDBO13 = new BasicDBObject("$in",Arrays.asList("$age",inList13));
//		BasicDBObject condDBO13 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO13,"50-54",condDBO12));
//		/*[45,46,47,48,49]*/
//		List<String> inList14 = new ArrayList<String>();
//		inList14.add("45");
//		inList14.add("46"); 
//		inList14.add("47"); 
//		inList14.add("48"); 
//		inList14.add("49"); 
//		
//		BasicDBObject inAgeDBO14 = new BasicDBObject("$in",Arrays.asList("$age",inList14));
//		BasicDBObject condDBO14 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO14,"45-49",condDBO13));
//		/*[40,41,42,43,44]*/
//		List<String> inList15 = new ArrayList<String>();
//		inList15.add("40");
//		inList15.add("41"); 
//		inList15.add("42"); 
//		inList15.add("43"); 
//		inList15.add("44"); 
//		
//		BasicDBObject inAgeDBO15 = new BasicDBObject("$in",Arrays.asList("$age",inList15));
//		BasicDBObject condDBO15 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO15,"40-44",condDBO14));
//		/*[35,36,37,38,39]*/
//		List<String> inList16 = new ArrayList<String>();
//		inList16.add("35");
//		inList16.add("36"); 
//		inList16.add("37"); 
//		inList16.add("38"); 
//		inList16.add("39"); 
//		
//		BasicDBObject inAgeDBO16 = new BasicDBObject("$in",Arrays.asList("$age",inList16));
//		BasicDBObject condDBO16 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO16,"35-39",condDBO15));
//		
//		BasicDBObject addFieldsDBO1 = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO16));
//		aggreList.add(addFieldsDBO1);
//		
//		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bpc","$bloodPressureCondition").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
//		aggreList.add(groupDBO);
//		
//		Iterable<DBObject> result = coll.aggregate(aggreList).results();
//		for(DBObject dbo : result){
//			returnMap.put(dbo.get("_id"), dbo.get("count"));
//		}
//		return returnMap;
//	}
	/*************************************************************************************************************************************************************************************/
	/**
	 * 血压筛查情况->血压情况年龄分布->精筛数据->血压异常\新发现高血压59-60
	 * @return
	 */
	public Map<Object, Object> findBloodPressureConditionAgeDistributionHeadthCheckDetail() {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		String code = "db.getCollection('healthcheckDetail').aggregate([" + 
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
			                                                "'then':'50-55',"+                                                
			                                                "'else':{"+
			                                                         "'$cond':{"+
			                                                            "'if':{'$in':['$age',[55,56,57,58,59]]},"+
			                                                            "'then':'55-60',"+
			                                                            "'else':{"+
			                                                                     "'$cond':{"+
			                                                                        "'if':{'$in':['$age',[60,61,62,63,64]]},"+
			                                                                        "'then':'60-65',"+
			                                                                        "'else':{"+
			                                                                                 "'$cond':{"+
			                                                                                    "'if':{'$in':['$age',[65,66,67,68,69]]},"+
			                                                                                    "'then':'65-70',"+
			                                                                                    "'else':{"+
			                                                                                            "'$cond':{"+
			                                                                                                "'if':{'$in':['$age',[70,71,72,73,74]]},"+
			                                                                                                "'then':'70-75',"+
			                                                                                                "'else':{"+
			                                                                                                       "'$cond':{"+
			                                                                                                            "'if':{'$in':['$age',[75,76,77,78,79]]},"+
			                                                                                                            "'then':'75-80',"+
			                                                                                                            "'else':{"+
			                                                                                                                    "'$cond':{"+
			                                                                                                                        "'if':{'$in':['$age',[80,81,82,83,84]]},"+
			                                                                                                                        "'then':'80-85',"+
			                                                                                                                       "'else':{"+
			                                                                                                                               "'$cond':{"+
			                                                                                                                                    "'if':{'$in':['$age',[85,86,87,88,89]]},"+
			                                                                                                                                    "'then':'85-90',"+
			                                                                                                                                    "'else':{"+
			                                                                                                                                            "'$cond':{"+
			                                                                                                                                                "'if':{'$in':['$age',[90,91,92,93,94]]},"+
			                                                                                                                                                "'then':'90-95',"+ 
			                                                                                                                                                "'else':{"+
			                                                                                                                                                      "'$cond':{"+
			                                                                                                                                                        "'if':{'$in':['$age',[30,31,32,33,34]]},"+
			                                                                                                                                                        "'then':'30-35',"+
			                                                                                                                                                        "'else':{"+
			                                                                                                                                                            "'$cond':{"+
			                                                                                                                                                            "'if':{'$in':['$age',[25,26,27,28,29]]},"+
			                                                                                                                                                            "'then':'25-30',"+
			                                                                                                                                                            "'else':{"+
			                                                                                                                                                                    "'$cond':{"+
			                                                                                                                                                                    "'if':{'$in':['$age',[20,21,22,23,24]]},"+
			                                                                                                                                                                    "'then':'20-25',"+
			                                                                                                                                                                    "'else':{"+
			                                                                                                                                                                            "'$cond':{"+
			                                                                                                                                                                            "'if':{'$in':['$age',[95,96,97,98,99]]},"+
			                                                                                                                                                                            "'then':'95-100',"+
			                                                                                                                                                                            "'else':'-'"+
			                                                                                                                                                                            "}"+
			                                                                                                                                                                        "}"+
			                                                                                                                                                                    "}"+ 
			                                                                                                                                                                "}"+
			                                                                                                                                                            "}"+  
			                                                                                                                                                            "}"+
			                                                                                                                                                        "}"+   
			                                                                                                                                                  "}"+
			                                                                                                                                                "}"+ 
			                                                                                                                                        "}"+
			                                                                                                                                    "}"+ 
			                                                                                                                            "}"+
			                                                                                                                        "}"+
			                                                                                                                "}"+
			                                                                                                            "}"+ 
			                                                                                                    "}"+
			                                                                                                "}"+ 
			                                                                                        "}"+
			                                                                                    "}"+
			                                                                            "}"+
			                                                                        "}"+
			                                                                "}"+
			                                                            "}"+
			                                                    "}"+
			                                                "}"+  
			                                            "}"+
			                                        "}"+ 
			                                "}"+
			                            "}"+                    
			                    "}"+
			                "}"+            
			            "}"+
			        "}"+
			"},"+
			
			
			 "{" +
			 "$addFields:{" +
			      "'bloodPressureConditionCharge':{" +
			          "'$cond':[" +
			              "{" +
			                  "$and:[" +
			                          "{$lt: ['$highPressure2', 140 ]},{$lt:['$lowPressure2',90]}" +
			                      "]" +         
			              "}," +
			              "'血压异常'," + 
			              "{" +
			                   "'$cond':[" +
			                          "{" +
			                              "$or:[" +
			                                      "{$gte: ['$highPressure2', 140 ]},{$gte:['$lowPressure2',90]}" +
			                                  "]" +         
			                          "}," +
			                          "'新发现高血压'," + 
			                          "'-'" +
			                    "]" +
			              "}" +
			           "]" +    
			      "}" +
			 "}" +
			"}," +
			"{"+
			    "'$group':{_id:{'bpc':'$bloodPressureConditionCharge','ages':'$ages'},'count':{$sum:1}}"+
			"}" +
		    "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			returnMap.put(m.get("_id"), (new Double(d)).intValue());
		}
		return returnMap;
	}
	
	/**
	 * 血压筛查情况->血压情况年龄分布->精筛数据->血压异常\新发现高血压59-60 2
	 * @return
	 */
	public Map<Object, Object> findBloodPressureConditionAgeDistributionHeadthCheckDetail2(String district) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		if (district != null) {
			BasicDBObject groupDistrict = new BasicDBObject("$match",new BasicDBObject("district",district));
			aggreList.add(groupDistrict);
		}
		
		/*['$age',[95,96,97,98,99]*/
		List<Integer> inList1 = new ArrayList<Integer>();
		inList1.add(95);
		inList1.add(96); 
		inList1.add(97); 
		inList1.add(98); 
		inList1.add(99); 
			
		BasicDBObject inAgeDBO1 = new BasicDBObject("$in",Arrays.asList("$age",inList1));
		BasicDBObject condDBO1 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO1,"95-100","-"));
		
		/*[20,21,22,23,24]*/
		List<Integer> inList2 = new ArrayList<Integer>();
		inList2.add(20);
		inList2.add(21); 
		inList2.add(22); 
		inList2.add(23); 
		inList2.add(24); 
		
		BasicDBObject inAgeDBO2 = new BasicDBObject("$in",Arrays.asList("$age",inList2));
		BasicDBObject condDBO2 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO2,"20-25",condDBO1));
		
		/*[25,26,27,28,29]*/
		List<Integer> inList3 = new ArrayList<Integer>();
		inList3.add(25);
		inList3.add(26); 
		inList3.add(27); 
		inList3.add(28); 
		inList3.add(29); 
		
		BasicDBObject inAgeDBO3 = new BasicDBObject("$in",Arrays.asList("$age",inList3));
		BasicDBObject condDBO3 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO3,"25-30",condDBO2));
		
		/*[30,31,32,33,34]*/
		List<Integer> inList4 = new ArrayList<Integer>();
		inList4.add(30);
		inList4.add(31); 
		inList4.add(32); 
		inList4.add(33); 
		inList4.add(34); 
		
		BasicDBObject inAgeDBO4 = new BasicDBObject("$in",Arrays.asList("$age",inList4));
		BasicDBObject condDBO4 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO4,"30-35",condDBO3));
		
		/*[90,91,92,93,94]*/
		List<Integer> inList5 = new ArrayList<Integer>();
		inList5.add(90);
		inList5.add(91); 
		inList5.add(92); 
		inList5.add(93); 
		inList5.add(94); 
		
		BasicDBObject inAgeDBO5 = new BasicDBObject("$in",Arrays.asList("$age",inList5));
		BasicDBObject condDBO5 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO5,"90-95",condDBO4));
		
		/*[85,86,87,88,89]*/
		List<Integer> inList6 = new ArrayList<Integer>();
		inList6.add(85);
		inList6.add(86); 
		inList6.add(87); 
		inList6.add(88); 
		inList6.add(89); 
		
		BasicDBObject inAgeDBO6 = new BasicDBObject("$in",Arrays.asList("$age",inList6));
		BasicDBObject condDBO6 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO6,"85-90",condDBO5));
		
		/*[80,81,82,83,84]*/
		List<Integer> inList7 = new ArrayList<Integer>();
		inList7.add(80);
		inList7.add(81); 
		inList7.add(82); 
		inList7.add(83); 
		inList7.add(84); 
		
		BasicDBObject inAgeDBO7 = new BasicDBObject("$in",Arrays.asList("$age",inList7));
		BasicDBObject condDBO7 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO7,"80-85",condDBO6));
		
		/*[75,76,77,78,79]*/
		List<Integer> inList8 = new ArrayList<Integer>();
		inList8.add(75);
		inList8.add(76); 
		inList8.add(77); 
		inList8.add(78); 
		inList8.add(79); 
		
		BasicDBObject inAgeDBO8 = new BasicDBObject("$in",Arrays.asList("$age",inList8));
		BasicDBObject condDBO8 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO8,"75-80",condDBO7));
		/*[70,71,72,73,74]*/
		List<Integer> inList9 = new ArrayList<Integer>();
		inList9.add(70);
		inList9.add(71); 
		inList9.add(72); 
		inList9.add(73); 
		inList9.add(74); 
		
		BasicDBObject inAgeDBO9 = new BasicDBObject("$in",Arrays.asList("$age",inList9));
		BasicDBObject condDBO9 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO9,"70-75",condDBO8));
		/*[65,66,67,68,69]*/
		List<Integer> inList10 = new ArrayList<Integer>();
		inList10.add(65);
		inList10.add(66); 
		inList10.add(67); 
		inList10.add(68); 
		inList10.add(69); 
		
		BasicDBObject inAgeDBO10 = new BasicDBObject("$in",Arrays.asList("$age",inList10));
		BasicDBObject condDBO10 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO10,"65-70",condDBO9));
		/*[60,61,62,63,64]*/
		List<Integer> inList11 = new ArrayList<Integer>();
		inList11.add(60);
		inList11.add(61); 
		inList11.add(62); 
		inList11.add(63); 
		inList11.add(64); 
		
		BasicDBObject inAgeDBO11 = new BasicDBObject("$in",Arrays.asList("$age",inList11));
		BasicDBObject condDBO11 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO11,"60-65",condDBO10));
		/*[55,56,57,58,59]*/
		List<Integer> inList12 = new ArrayList<Integer>();
		inList12.add(55);
		inList12.add(56); 
		inList12.add(57); 
		inList12.add(58); 
		inList12.add(59); 
		
		BasicDBObject inAgeDBO12 = new BasicDBObject("$in",Arrays.asList("$age",inList12));
		BasicDBObject condDBO12 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO12,"55-60",condDBO11));
		/*[50,51,52,53,54]*/
		List<Integer> inList13 = new ArrayList<Integer>();
		inList13.add(50);
		inList13.add(51); 
		inList13.add(52); 
		inList13.add(53); 
		inList13.add(54); 
		
		BasicDBObject inAgeDBO13 = new BasicDBObject("$in",Arrays.asList("$age",inList13));
		BasicDBObject condDBO13 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO13,"50-55",condDBO12));
		/*[45,46,47,48,49]*/
		List<Integer> inList14 = new ArrayList<Integer>();
		inList14.add(45);
		inList14.add(46); 
		inList14.add(47); 
		inList14.add(48); 
		inList14.add(49); 
		
		BasicDBObject inAgeDBO14 = new BasicDBObject("$in",Arrays.asList("$age",inList14));
		BasicDBObject condDBO14 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO14,"45-50",condDBO13));
		/*[40,41,42,43,44]*/
		List<Integer> inList15 = new ArrayList<Integer>();
		inList15.add(40);
		inList15.add(41); 
		inList15.add(42); 
		inList15.add(43); 
		inList15.add(44); 
		
		BasicDBObject inAgeDBO15 = new BasicDBObject("$in",Arrays.asList("$age",inList15));
		BasicDBObject condDBO15 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO15,"40-45",condDBO14));
		/*[35,36,37,38,39]*/
		List<Integer> inList16 = new ArrayList<Integer>();
		inList16.add(35);
		inList16.add(36); 
		inList16.add(37); 
		inList16.add(38); 
		inList16.add(39); 
		
		BasicDBObject inAgeDBO16 = new BasicDBObject("$in",Arrays.asList("$age",inList16));
		BasicDBObject condDBO16 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO16,"35-40",condDBO15));
		
		BasicDBObject addFieldsDBO1 = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO16));
		aggreList.add(addFieldsDBO1);
		
		
		BasicDBObject gteHighPressure21 = new BasicDBObject("$gte",Arrays.asList("$highPressure",130));
		BasicDBObject gteLowPressure21 = new BasicDBObject("$gte",Arrays.asList("$lowPressure",85));
		BasicDBObject orDBO1 = new BasicDBObject("$or",Arrays.asList(gteHighPressure21,gteLowPressure21));
		
		BasicDBObject condDBO111 = new BasicDBObject("$cond",Arrays.asList(orDBO1,"新发现高血压","-"));
		
		BasicDBObject ltHighPressure21 = new BasicDBObject("$lt",Arrays.asList("$highPressure",130));
		BasicDBObject ltLowPressure21 = new BasicDBObject("$lt",Arrays.asList("$lowPressure",85));
		BasicDBObject andDBO1 = new BasicDBObject("$and",Arrays.asList(ltHighPressure21,ltLowPressure21));
		
		BasicDBObject condDBO222 = new BasicDBObject("$cond",Arrays.asList(andDBO1,"血压异常",condDBO111));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("bloodPressureConditionCharge",condDBO222));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bpc","$bloodPressureConditionCharge").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put(dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	/*************************************************************************************************************************************************************************************/
	/**
	 * 血脂筛查情况->血脂情况人群分布->初筛数据->正常\已登记高血脂\血脂异常61-63
	 * @return
	 */
	public Map<String, Object> findBloodLipidConditionPeopleDistributionHeadthCheck() {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String code = "db.getCollection('healthcheck').aggregate([" + 
		                                           "{" +
		                                               "'$group':{_id:'$bloodLipidCondition','count':{$sum:1}}" +
		                                           "}" +
		                                       "])";
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			if(m.get("_id") == null || m.get("_id") ==""){
				returnMap.put("-", (new Double(d)).intValue());
			}else{
				returnMap.put((String)m.get("_id"), (new Double(d)).intValue());
			}
		}
		return returnMap;
	}
	
	/**
	 * 血脂筛查情况->血脂情况人群分布->初筛数据->正常\已登记高血脂\血脂异常61-63 2
	 * @return
	 */
	public Map<String, Object> findBloodLipidConditionPeopleDistributionHeadthCheck2(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject groupDistrict = new BasicDBObject("$match",new BasicDBObject("district",district));
			aggreList.add(groupDistrict);
		}
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","$bloodLipidCondition").append("count", new BasicDBObject("$sum",1)));
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
	/*************************************************************************************************************************************************************************************/
	/**
	 * 血脂筛查情况->血脂情况人群分布->精筛数据->血脂异常\新发现高血脂64-65
	 * @return
	 */
	public Map<String, Object> findBloodLipidConditionPeopleDistributionHeadthCheckDetail() {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String code ="db.getCollection('healthcheckDetail').aggregate([" +
		                                                 "{" +
		                                                        "$addFields:{" +
		                                                             "'bloodLipidConditionCharge':{" +
		                                                                 "'$cond':[" +
		                                                                     "{" +
		                                                                         "$and:[" +
		                                                                                 "{$lt: ['$tc', 6.2 ]},{$lt:['$tg',2.3]},{$gt:['$hdl',1]}" +
		                                                                             "]" +         
		                                                                     "}," +
		                                                                     "'血脂异常'," + 
		                                                                     "{" +
		                                                                          "'$cond':[" +
		                                                                                 "{" +
		                                                                                     "$or:[" +
		                                                                                             "{$gte: ['$tc', 6.2 ]},{$gte:['$tg',2.3]},{$lte:['$hdl',1]}" +
		                                                                                         "]" +         
		                                                                                 "}," +
		                                                                                 "'新发现高血脂'," + 
		                                                                                 "'-'"+
		                                                                           "]" +
		                                                                     "}" +
		                                                                  "]" +    
		                                                             "}" +
		                                                        "}" +
		                                                 "}," +
		                                                 "{" +
		                                                     "'$group':{_id:'$bloodLipidConditionCharge','count':{$sum:1}}" +
		                                                 "}" +
		                                             "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			if(m.get("_id") == null || m.get("_id") ==""){
				returnMap.put("-", (new Double(d)).intValue());
			}else{
				returnMap.put((String)m.get("_id"), (new Double(d)).intValue());
			}
		}
		return returnMap;
	}
	
	/**
	 * 血脂筛查情况->血脂情况人群分布->精筛数据->血脂异常\新发现高血脂64-65 2
	 * @return
	 */
	public Map<String, Object> findBloodLipidConditionPeopleDistributionHeadthCheckDetail2(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		if (district != null) {
			BasicDBObject groupDistrict = new BasicDBObject("$match",new BasicDBObject("district",district));
			aggreList.add(groupDistrict);
		}
		
		BasicDBObject gteTc1 = new BasicDBObject("$gte",Arrays.asList("$tc",5.2));
		BasicDBObject gteTg1 = new BasicDBObject("$gte",Arrays.asList("$tg",1.7));
		BasicDBObject lteHdl1 = new BasicDBObject("$lte",Arrays.asList("$hdl",1));
		BasicDBObject orDBO1 = new BasicDBObject("$or",Arrays.asList(gteTc1,gteTg1,lteHdl1));
		
		BasicDBObject condDBO1 = new BasicDBObject("$cond",Arrays.asList(orDBO1,"新发现高血脂","-"));
		
		BasicDBObject ltTc1 = new BasicDBObject("$lt",Arrays.asList("$tc",5.2));
		BasicDBObject ltTg1 = new BasicDBObject("$lt",Arrays.asList("$tg",1.7));
		BasicDBObject gtHdl1 = new BasicDBObject("$gt",Arrays.asList("$hdl",1));
		BasicDBObject andDBO1 = new BasicDBObject("$and",Arrays.asList(ltTc1,ltTg1,gtHdl1));
		
		BasicDBObject condDBO2 = new BasicDBObject("$cond",Arrays.asList(andDBO1,"血脂异常",condDBO1));
		
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("bloodLipidConditionCharge",condDBO2));
		aggreList.add(addFieldsDBO);
		
		
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","$bloodLipidConditionCharge").append("count", new BasicDBObject("$sum",1)));
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
	/*************************************************************************************************************************************************************************************/
	/**
	 * 血脂筛查情况->血脂情况人群分布->初筛数据->正常\已登记高血脂\血脂异常66-68
	 * @return
	 */
	public Map<Object, Object> findBloodLipidConditionAgeDistributionHeadthCheck() {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		String code = "db.getCollection('healthcheck').aggregate([" + 
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
			                                                "'then':'50-55',"+                                                
			                                                "'else':{"+
			                                                         "'$cond':{"+
			                                                            "'if':{'$in':['$age',[55,56,57,58,59]]},"+
			                                                            "'then':'55-60',"+
			                                                            "'else':{"+
			                                                                     "'$cond':{"+
			                                                                        "'if':{'$in':['$age',[60,61,62,63,64]]},"+
			                                                                        "'then':'60-65',"+
			                                                                        "'else':{"+
			                                                                                 "'$cond':{"+
			                                                                                    "'if':{'$in':['$age',[65,66,67,68,69]]},"+
			                                                                                    "'then':'65-70',"+
			                                                                                    "'else':{"+
			                                                                                            "'$cond':{"+
			                                                                                                "'if':{'$in':['$age',[70,71,72,73,74]]},"+
			                                                                                                "'then':'70-75',"+
			                                                                                                "'else':{"+
			                                                                                                       "'$cond':{"+
			                                                                                                            "'if':{'$in':['$age',[75,76,77,78,79]]},"+
			                                                                                                            "'then':'75-80',"+
			                                                                                                            "'else':{"+
			                                                                                                                    "'$cond':{"+
			                                                                                                                        "'if':{'$in':['$age',[80,81,82,83,84]]},"+
			                                                                                                                        "'then':'80-85',"+
			                                                                                                                       "'else':{"+
			                                                                                                                               "'$cond':{"+
			                                                                                                                                    "'if':{'$in':['$age',[85,86,87,88,89]]},"+
			                                                                                                                                    "'then':'85-90',"+
			                                                                                                                                    "'else':{"+
			                                                                                                                                            "'$cond':{"+
			                                                                                                                                                "'if':{'$in':['$age',[90,91,92,93,94]]},"+
			                                                                                                                                                "'then':'90-95',"+ 
			                                                                                                                                                "'else':{"+
			                                                                                                                                                      "'$cond':{"+
			                                                                                                                                                        "'if':{'$in':['$age',[30,31,32,33,34]]},"+
			                                                                                                                                                        "'then':'30-35',"+
			                                                                                                                                                        "'else':{"+
			                                                                                                                                                            "'$cond':{"+
			                                                                                                                                                            "'if':{'$in':['$age',[25,26,27,28,29]]},"+
			                                                                                                                                                            "'then':'25-30',"+
			                                                                                                                                                            "'else':{"+
			                                                                                                                                                                    "'$cond':{"+
			                                                                                                                                                                    "'if':{'$in':['$age',[20,21,22,23,24]]},"+
			                                                                                                                                                                    "'then':'20-25',"+
			                                                                                                                                                                    "'else':{"+
			                                                                                                                                                                            "'$cond':{"+
			                                                                                                                                                                            "'if':{'$in':['$age',[95,96,97,98,99]]},"+
			                                                                                                                                                                            "'then':'95-100',"+
			                                                                                                                                                                            "'else':'-'"+
			                                                                                                                                                                            "}"+
			                                                                                                                                                                        "}"+
			                                                                                                                                                                    "}"+ 
			                                                                                                                                                                "}"+
			                                                                                                                                                            "}"+  
			                                                                                                                                                            "}"+
			                                                                                                                                                        "}"+   
			                                                                                                                                                  "}"+
			                                                                                                                                                "}"+ 
			                                                                                                                                        "}"+
			                                                                                                                                    "}"+ 
			                                                                                                                            "}"+
			                                                                                                                        "}"+
			                                                                                                                "}"+
			                                                                                                            "}"+ 
			                                                                                                    "}"+
			                                                                                                "}"+ 
			                                                                                        "}"+
			                                                                                    "}"+
			                                                                            "}"+
			                                                                        "}"+
			                                                                "}"+
			                                                            "}"+
			                                                    "}"+
			                                                "}"+  
			                                            "}"+
			                                        "}"+ 
			                                "}"+
			                            "}"+                    
			                    "}"+
			                "}"+            
			            "}"+
			        "}"+
			"},"+
			"{"+
			    "'$group':{_id:{'blc':'$bloodLipidCondition','ages':'$ages'},'count':{$sum:1}}"+
			"}" +
		    "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			returnMap.put(m.get("_id"), (new Double(d)).intValue());
		}
		return returnMap;
	}
	
	/**
	 * 血脂筛查情况->血脂情况人群分布->初筛数据->正常\已登记高血脂\血脂异常66-68 2
	 * @return
	 */
	public Map<Object, Object>  findBloodLipidConditionAgeDistributionHeadthCheck2(String district, List<Map<String,Object>> ageList) {
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		
		BasicDBObject condDBO = ageDistribution(ageList);
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("blc","$bloodLipidCondition").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		DBCollection coll = db.getCollection("customer");
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		for(DBObject dbo : result){
			returnMap.put(dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;

	}
//	public Map<Object, Object> findBloodLipidConditionAgeDistributionHeadthCheck2(String district) {
//		Map<Object,Object> returnMap = new HashMap<Object,Object>();
//		DBCollection coll = db.getCollection("customer");
//		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
//		
//		if (district != null) {
//			BasicDBObject groupDistrict = new BasicDBObject("$match",new BasicDBObject("district",district));
//			aggreList.add(groupDistrict);
//		}
//		
//		/*[60,61,62,63,64]*/
//		List<String> inList11 = new ArrayList<String>();
//		inList11.add("60");
//		inList11.add("61"); 
//		inList11.add("62"); 
//		inList11.add("63"); 
//		inList11.add("64");
//		inList11.add("65");
//		
//		BasicDBObject inAgeDBO11 = new BasicDBObject("$in",Arrays.asList("$age",inList11));
//		BasicDBObject condDBO11 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO11,"60-65","-"));
//		/*[55,56,57,58,59]*/
//		List<String> inList12 = new ArrayList<String>();
//		inList12.add("55");
//		inList12.add("56"); 
//		inList12.add("57"); 
//		inList12.add("58"); 
//		inList12.add("59"); 
//		
//		BasicDBObject inAgeDBO12 = new BasicDBObject("$in",Arrays.asList("$age",inList12));
//		BasicDBObject condDBO12 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO12,"55-59",condDBO11));
//		/*[50,51,52,53,54]*/
//		List<String> inList13 = new ArrayList<String>();
//		inList13.add("50");
//		inList13.add("51"); 
//		inList13.add("52"); 
//		inList13.add("53"); 
//		inList13.add("54"); 
//		
//		BasicDBObject inAgeDBO13 = new BasicDBObject("$in",Arrays.asList("$age",inList13));
//		BasicDBObject condDBO13 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO13,"50-54",condDBO12));
//		/*[45,46,47,48,49]*/
//		List<String> inList14 = new ArrayList<String>();
//		inList14.add("45");
//		inList14.add("46"); 
//		inList14.add("47"); 
//		inList14.add("48"); 
//		inList14.add("49"); 
//		
//		BasicDBObject inAgeDBO14 = new BasicDBObject("$in",Arrays.asList("$age",inList14));
//		BasicDBObject condDBO14 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO14,"45-49",condDBO13));
//		/*[40,41,42,43,44]*/
//		List<String> inList15 = new ArrayList<String>();
//		inList15.add("40");
//		inList15.add("41"); 
//		inList15.add("42"); 
//		inList15.add("43"); 
//		inList15.add("44"); 
//		
//		BasicDBObject inAgeDBO15 = new BasicDBObject("$in",Arrays.asList("$age",inList15));
//		BasicDBObject condDBO15 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO15,"40-44",condDBO14));
//		/*[35,36,37,38,39]*/
//		List<String> inList16 = new ArrayList<String>();
//		inList16.add("35");
//		inList16.add("36"); 
//		inList16.add("37"); 
//		inList16.add("38"); 
//		inList16.add("39"); 
//		
//		BasicDBObject inAgeDBO16 = new BasicDBObject("$in",Arrays.asList("$age",inList16));
//		BasicDBObject condDBO16 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO16,"35-39",condDBO15));
//		
//		BasicDBObject addFieldsDBO1 = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO16));
//		aggreList.add(addFieldsDBO1);
//		
//		
//		
//		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("blc","$bloodLipidCondition").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
//		aggreList.add(groupDBO);
//		
//		Iterable<DBObject> result = coll.aggregate(aggreList).results();
//		for(DBObject dbo : result){
//			returnMap.put(dbo.get("_id"), dbo.get("count"));
//		}
//		return returnMap;
//	}
	/*************************************************************************************************************************************************************************************/
	/**
	 * 血脂筛查情况->血脂情况人群分布->初筛数据->新发现高血脂\血脂异常69-70
	 * @return
	 */
	public Map<Object, Object> findBloodLipidConditionAgeDistributionHeadthCheckDetail() {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		String code = "db.getCollection('healthcheckDetail').aggregate([" + 
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
			                                                "'then':'50-55',"+                                                
			                                                "'else':{"+
			                                                         "'$cond':{"+
			                                                            "'if':{'$in':['$age',[55,56,57,58,59]]},"+
			                                                            "'then':'55-60',"+
			                                                            "'else':{"+
			                                                                     "'$cond':{"+
			                                                                        "'if':{'$in':['$age',[60,61,62,63,64]]},"+
			                                                                        "'then':'60-65',"+
			                                                                        "'else':{"+
			                                                                                 "'$cond':{"+
			                                                                                    "'if':{'$in':['$age',[65,66,67,68,69]]},"+
			                                                                                    "'then':'65-70',"+
			                                                                                    "'else':{"+
			                                                                                            "'$cond':{"+
			                                                                                                "'if':{'$in':['$age',[70,71,72,73,74]]},"+
			                                                                                                "'then':'70-75',"+
			                                                                                                "'else':{"+
			                                                                                                       "'$cond':{"+
			                                                                                                            "'if':{'$in':['$age',[75,76,77,78,79]]},"+
			                                                                                                            "'then':'75-80',"+
			                                                                                                            "'else':{"+
			                                                                                                                    "'$cond':{"+
			                                                                                                                        "'if':{'$in':['$age',[80,81,82,83,84]]},"+
			                                                                                                                        "'then':'80-85',"+
			                                                                                                                       "'else':{"+
			                                                                                                                               "'$cond':{"+
			                                                                                                                                    "'if':{'$in':['$age',[85,86,87,88,89]]},"+
			                                                                                                                                    "'then':'85-90',"+
			                                                                                                                                    "'else':{"+
			                                                                                                                                            "'$cond':{"+
			                                                                                                                                                "'if':{'$in':['$age',[90,91,92,93,94]]},"+
			                                                                                                                                                "'then':'90-95',"+ 
			                                                                                                                                                "'else':{"+
			                                                                                                                                                      "'$cond':{"+
			                                                                                                                                                        "'if':{'$in':['$age',[30,31,32,33,34]]},"+
			                                                                                                                                                        "'then':'30-35',"+
			                                                                                                                                                        "'else':{"+
			                                                                                                                                                            "'$cond':{"+
			                                                                                                                                                            "'if':{'$in':['$age',[25,26,27,28,29]]},"+
			                                                                                                                                                            "'then':'25-30',"+
			                                                                                                                                                            "'else':{"+
			                                                                                                                                                                    "'$cond':{"+
			                                                                                                                                                                    "'if':{'$in':['$age',[20,21,22,23,24]]},"+
			                                                                                                                                                                    "'then':'20-25',"+
			                                                                                                                                                                    "'else':{"+
			                                                                                                                                                                            "'$cond':{"+
			                                                                                                                                                                            "'if':{'$in':['$age',[95,96,97,98,99]]},"+
			                                                                                                                                                                            "'then':'95-100',"+
			                                                                                                                                                                            "'else':'-'"+
			                                                                                                                                                                            "}"+
			                                                                                                                                                                        "}"+
			                                                                                                                                                                    "}"+ 
			                                                                                                                                                                "}"+
			                                                                                                                                                            "}"+  
			                                                                                                                                                            "}"+
			                                                                                                                                                        "}"+   
			                                                                                                                                                  "}"+
			                                                                                                                                                "}"+ 
			                                                                                                                                        "}"+
			                                                                                                                                    "}"+ 
			                                                                                                                            "}"+
			                                                                                                                        "}"+
			                                                                                                                "}"+
			                                                                                                            "}"+ 
			                                                                                                    "}"+
			                                                                                                "}"+ 
			                                                                                        "}"+
			                                                                                    "}"+
			                                                                            "}"+
			                                                                        "}"+
			                                                                "}"+
			                                                            "}"+
			                                                    "}"+
			                                                "}"+  
			                                            "}"+
			                                        "}"+ 
			                                "}"+
			                            "}"+                    
			                    "}"+
			                "}"+            
			            "}"+
			        "}"+
			"},"+
 			"{" +
		           "$addFields:{" +
		                "'bloodLipidConditionCharge':{" +
		                    "'$cond':[" +
		                        "{" +
		                            "$and:[" +
		                                    "{$lt: ['$tc', 6.2 ]},{$lt:['$tg',2.3]},{$gt:['$hdl',1]}" +
		                                "]" +         
		                        "}," +
		                        "'血脂异常'," + 
		                        "{" +
		                             "'$cond':[" +
		                                    "{" +
		                                        "$or:[" +
		                                                "{$gte: ['$tc', 6.2 ]},{$gte:['$tg',2.3]},{$lte:['$hdl',1]}" +
		                                            "]" +         
		                                    "}," +
		                                    "'新发现高血脂'," + 
		                                    "'-'" +
		                              "]" +
		                        "}" +
		                     "]" +    
		                "}" +
		           "}" + 
		    "}," +
			"{"+
			    "'$group':{_id:{'bpc':'$bloodLipidConditionCharge','ages':'$ages'},'count':{$sum:1}}"+
			"}" +
		    "])";
		
		BasicDBObject o = db.doEval(code);
		
		Map<String,Object> result_m = o.toMap();
		
		Map<String,Object> retval_m = (Map<String, Object>) result_m.get("retval");
		
		List<Map<String,Object>> batch_list = (List<Map<String, Object>>) retval_m.get("_batch");
		for(Map<String,Object> m : batch_list){
			double d = ((Double)m.get("count"));
			returnMap.put(m.get("_id"), (new Double(d)).intValue());
		}
		return returnMap;
	}
	
	/**
	 * 血脂筛查情况->血脂情况人群分布->初筛数据->新发现高血脂\血脂异常69-70 2
	 * @return
	 */
	public Map<Object, Object> findBloodLipidConditionAgeDistributionHeadthCheckDetail2(String district) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
//		DBCollection coll = db.getCollection("healthcheckDetail");
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (district != null) {
			BasicDBObject groupDistrict = new BasicDBObject("$match",new BasicDBObject("district",district));
			aggreList.add(groupDistrict);
		}
		
		/*['$age',[95,96,97,98,99]*/
		List<Integer> inList1 = new ArrayList<Integer>();
		inList1.add(95);
		inList1.add(96); 
		inList1.add(97); 
		inList1.add(98); 
		inList1.add(99); 
			
		BasicDBObject inAgeDBO1 = new BasicDBObject("$in",Arrays.asList("$age",inList1));
		BasicDBObject condDBO1 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO1,"95-100","-"));
		
		/*[20,21,22,23,24]*/
		List<Integer> inList2 = new ArrayList<Integer>();
		inList2.add(20);
		inList2.add(21); 
		inList2.add(22); 
		inList2.add(23); 
		inList2.add(24); 
		
		BasicDBObject inAgeDBO2 = new BasicDBObject("$in",Arrays.asList("$age",inList2));
		BasicDBObject condDBO2 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO2,"20-25",condDBO1));
		
		/*[25,26,27,28,29]*/
		List<Integer> inList3 = new ArrayList<Integer>();
		inList3.add(25);
		inList3.add(26); 
		inList3.add(27); 
		inList3.add(28); 
		inList3.add(29); 
		
		BasicDBObject inAgeDBO3 = new BasicDBObject("$in",Arrays.asList("$age",inList3));
		BasicDBObject condDBO3 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO3,"25-30",condDBO2));
		
		/*[30,31,32,33,34]*/
		List<Integer> inList4 = new ArrayList<Integer>();
		inList4.add(30);
		inList4.add(31); 
		inList4.add(32); 
		inList4.add(33); 
		inList4.add(34); 
		
		BasicDBObject inAgeDBO4 = new BasicDBObject("$in",Arrays.asList("$age",inList4));
		BasicDBObject condDBO4 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO4,"30-35",condDBO3));
		
		/*[90,91,92,93,94]*/
		List<Integer> inList5 = new ArrayList<Integer>();
		inList5.add(90);
		inList5.add(91); 
		inList5.add(92); 
		inList5.add(93); 
		inList5.add(94); 
		
		BasicDBObject inAgeDBO5 = new BasicDBObject("$in",Arrays.asList("$age",inList5));
		BasicDBObject condDBO5 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO5,"90-95",condDBO4));
		
		/*[85,86,87,88,89]*/
		List<Integer> inList6 = new ArrayList<Integer>();
		inList6.add(85);
		inList6.add(86); 
		inList6.add(87); 
		inList6.add(88); 
		inList6.add(89); 
		
		BasicDBObject inAgeDBO6 = new BasicDBObject("$in",Arrays.asList("$age",inList6));
		BasicDBObject condDBO6 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO6,"85-90",condDBO5));
		
		/*[80,81,82,83,84]*/
		List<Integer> inList7 = new ArrayList<Integer>();
		inList7.add(80);
		inList7.add(81); 
		inList7.add(82); 
		inList7.add(83); 
		inList7.add(84); 
		
		BasicDBObject inAgeDBO7 = new BasicDBObject("$in",Arrays.asList("$age",inList7));
		BasicDBObject condDBO7 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO7,"80-85",condDBO6));
		
		/*[75,76,77,78,79]*/
		List<Integer> inList8 = new ArrayList<Integer>();
		inList8.add(75);
		inList8.add(76); 
		inList8.add(77); 
		inList8.add(78); 
		inList8.add(79); 
		
		BasicDBObject inAgeDBO8 = new BasicDBObject("$in",Arrays.asList("$age",inList8));
		BasicDBObject condDBO8 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO8,"75-80",condDBO7));
		/*[70,71,72,73,74]*/
		List<Integer> inList9 = new ArrayList<Integer>();
		inList9.add(70);
		inList9.add(71); 
		inList9.add(72); 
		inList9.add(73); 
		inList9.add(74); 
		
		BasicDBObject inAgeDBO9 = new BasicDBObject("$in",Arrays.asList("$age",inList9));
		BasicDBObject condDBO9 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO9,"70-75",condDBO8));
		/*[65,66,67,68,69]*/
		List<Integer> inList10 = new ArrayList<Integer>();
		inList10.add(65);
		inList10.add(66); 
		inList10.add(67); 
		inList10.add(68); 
		inList10.add(69); 
		
		BasicDBObject inAgeDBO10 = new BasicDBObject("$in",Arrays.asList("$age",inList10));
		BasicDBObject condDBO10 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO10,"65-70",condDBO9));
		/*[60,61,62,63,64]*/
		List<Integer> inList11 = new ArrayList<Integer>();
		inList11.add(60);
		inList11.add(61); 
		inList11.add(62); 
		inList11.add(63); 
		inList11.add(64); 
		
		BasicDBObject inAgeDBO11 = new BasicDBObject("$in",Arrays.asList("$age",inList11));
		BasicDBObject condDBO11 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO11,"60-65",condDBO10));
		/*[55,56,57,58,59]*/
		List<Integer> inList12 = new ArrayList<Integer>();
		inList12.add(55);
		inList12.add(56); 
		inList12.add(57); 
		inList12.add(58); 
		inList12.add(59); 
		
		BasicDBObject inAgeDBO12 = new BasicDBObject("$in",Arrays.asList("$age",inList12));
		BasicDBObject condDBO12 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO12,"55-60",condDBO11));
		/*[50,51,52,53,54]*/
		List<Integer> inList13 = new ArrayList<Integer>();
		inList13.add(50);
		inList13.add(51); 
		inList13.add(52); 
		inList13.add(53); 
		inList13.add(54); 
		
		BasicDBObject inAgeDBO13 = new BasicDBObject("$in",Arrays.asList("$age",inList13));
		BasicDBObject condDBO13 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO13,"50-55",condDBO12));
		/*[45,46,47,48,49]*/
		List<Integer> inList14 = new ArrayList<Integer>();
		inList14.add(45);
		inList14.add(46); 
		inList14.add(47); 
		inList14.add(48); 
		inList14.add(49); 
		
		BasicDBObject inAgeDBO14 = new BasicDBObject("$in",Arrays.asList("$age",inList14));
		BasicDBObject condDBO14 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO14,"45-50",condDBO13));
		/*[40,41,42,43,44]*/
		List<Integer> inList15 = new ArrayList<Integer>();
		inList15.add(40);
		inList15.add(41); 
		inList15.add(42); 
		inList15.add(43); 
		inList15.add(44); 
		
		BasicDBObject inAgeDBO15 = new BasicDBObject("$in",Arrays.asList("$age",inList15));
		BasicDBObject condDBO15 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO15,"40-45",condDBO14));
		/*[35,36,37,38,39]*/
		List<Integer> inList16 = new ArrayList<Integer>();
		inList16.add(35);
		inList16.add(36); 
		inList16.add(37); 
		inList16.add(38); 
		inList16.add(39); 
		
		BasicDBObject inAgeDBO16 = new BasicDBObject("$in",Arrays.asList("$age",inList16));
		BasicDBObject condDBO16 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO16,"35-40",condDBO15));
		
		BasicDBObject addFieldsDBO1 = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO16));
		aggreList.add(addFieldsDBO1);
		
		
		BasicDBObject gteTc1 = new BasicDBObject("$gte",Arrays.asList("$tc",5.2));
		BasicDBObject gteTg1 = new BasicDBObject("$gte",Arrays.asList("$tg",1.7));
		BasicDBObject lteHdl1 = new BasicDBObject("$lte",Arrays.asList("$hdl",1));
		BasicDBObject orDBO1 = new BasicDBObject("$or",Arrays.asList(gteTc1,gteTg1,lteHdl1));
		
		BasicDBObject condDBO111 = new BasicDBObject("$cond",Arrays.asList(orDBO1,"新发现高血脂","-"));
		
		BasicDBObject ltTc1 = new BasicDBObject("$lt",Arrays.asList("$tc",5.2));
		BasicDBObject ltTg1 = new BasicDBObject("$lt",Arrays.asList("$tg",1.7));
		BasicDBObject gtHdl1 = new BasicDBObject("$gt",Arrays.asList("$hdl",1));
		BasicDBObject andDBO111 = new BasicDBObject("$and",Arrays.asList(ltTc1,ltTg1,gtHdl1));
		
		BasicDBObject condDBO222 = new BasicDBObject("$cond",Arrays.asList(andDBO111,"血脂异常",condDBO111));
		
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("bloodLipidConditionCharge",condDBO222));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bpc","$bloodLipidConditionCharge").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put(dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
		
	}
	/*************************************************************************************************************************************************************************************/

	/**
	 * 建档人群年龄分布
	 * @return
	 */
	public Map<String, Object> findRecordCountByAge(String district, List<Map<String, Object>> ageList) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
	
		BasicDBObject condDBO = ageDistribution(ageList);
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("ages","$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			Map<String, Object> id = (Map<String, Object>) dbo.get("_id");
			returnMap.put(id.get("ages").toString(), dbo.get("count"));
		}
//		System.out.println("returnMap:" + returnMap);
		return returnMap;
	}
	
	/**
	 * db.getCollection('customer').aggregate([
     *		{
     *   		'$group':{_id:'$gender',count:{$sum:1}}
     * 		}
	 *	]);
	 * 
	 * 建档人群性别分布
	 * @return
	 */
	public Map<String, Object> findRecordCountByGender(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject groupDistrict = new BasicDBObject("$match",new BasicDBObject("district",district));
			aggreList.add(groupDistrict);
		}
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","$gender").append("count", new BasicDBObject("$sum",1)));
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
	
	/**
	 * 各性别糖尿病分布
	 * @return
	 */
	public Map<Object, Object> findBloodSugarConditionPeopleDistributionByGender(String district) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bs","$bloodSugarCondition").append("gender", "$gender")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id"), dbo.get("count"));
			}
		}
		return returnMap;
	}
	
	
	public Integer findBloodSugarConditionPeopleDistributionByTizhi(Map<String, Object> queryMap, String tizhi) {
		DBCollection coll = db.getCollection("customer");
		BasicDBObject regulaQuery = new BasicDBObject();
		regulaQuery.put("$regex", "^" + tizhi + ".*$");
		
//		query.put("bloodSugarCondition", disease);
		queryMap.put("tizhi", regulaQuery);
		
		DBObject map=new BasicDBObject(queryMap);
		Integer count = coll.find(map).count();
		
        return count;
	}
	
	
	/**
	 * 各性别高血压分布
	 * @return
	 */
	public Map<Object, Object> findBloodPressureConditionPeopleDistributionByGender(String district) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bp","$bloodPressureCondition").append("gender", "$gender")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id"), dbo.get("count"));
			}
		}
		return returnMap;
	}
	
	/**
	 * 各性别血脂情况分布
	 * @return
	 */
	public Map<Object, Object> findBloodLipidConditionPeopleDistributionByGender(String district) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bl","$bloodLipidCondition").append("gender", "$gender")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id"), dbo.get("count"));
			}
		}
		return returnMap;
	}
	
	/**
	 * 体质指数人群分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findFatPeopleDistribution(String district) {
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
	
	
	public Map<Object, Object> findFatPeopleDistributionByAge(String district, List<Map<String, Object>> ageList) {
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
		BasicDBObject condBDB0 = new BasicDBObject("$cond", Arrays.asList(bDb01, "偏瘦人群", "-"));
		
		BasicDBObject bDB2 = new BasicDBObject("$gte", Arrays.asList("$BMI", 24));
		BasicDBObject bDB3 = new BasicDBObject("$lt", Arrays.asList("$BMI", 28));
		BasicDBObject bDb4 = new BasicDBObject("$and", Arrays.asList(bDB2, bDB3));
		BasicDBObject condBDB1 = new BasicDBObject("$cond", Arrays.asList(bDb4, "超重人群", condBDB0));
		
		BasicDBObject bDb5 = new BasicDBObject("$gte", Arrays.asList("$BMI", 28));
		BasicDBObject condBDB2 = new BasicDBObject("$cond", Arrays.asList(bDb5, "肥胖人群", condBDB1));
		
		BasicDBObject bDb6 = new BasicDBObject("$gte", Arrays.asList("$BMI", 18.5));
		BasicDBObject bDb7 = new BasicDBObject("$lt", Arrays.asList("$BMI", 24));
		BasicDBObject bDb8 = new BasicDBObject("$and", Arrays.asList(bDb6, bDb7));
		BasicDBObject condBDB3 = new BasicDBObject("$cond", Arrays.asList(bDb8, "正常人群", condBDB2));
		
		BasicDBObject bDb9 = new BasicDBObject("$addFields", new BasicDBObject("BMIs",condBDB3));
		aggreList.add(bDb9);
		
		BasicDBObject condDBO = ageDistribution(ageList);
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bmi","$BMIs").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id"), dbo.get("count"));
			}
		}
//		System.out.println("returnMap:" + returnMap);
		return returnMap;
	}
	
	public Map<Object, Object> findFatPeopleDistributionByGender(String district) {
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
		BasicDBObject condBDB0 = new BasicDBObject("$cond", Arrays.asList(bDb01, "偏瘦人群", "-"));
		
		BasicDBObject bDB2 = new BasicDBObject("$gte", Arrays.asList("$BMI", 24));
		BasicDBObject bDB3 = new BasicDBObject("$lt", Arrays.asList("$BMI", 28));
		BasicDBObject bDb4 = new BasicDBObject("$and", Arrays.asList(bDB2, bDB3));
		BasicDBObject condBDB1 = new BasicDBObject("$cond", Arrays.asList(bDb4, "超重人群", condBDB0));
		
		BasicDBObject bDb5 = new BasicDBObject("$gte", Arrays.asList("$BMI", 28));
		BasicDBObject condBDB2 = new BasicDBObject("$cond", Arrays.asList(bDb5, "肥胖人群", condBDB1));
		
		BasicDBObject bDb6 = new BasicDBObject("$gte", Arrays.asList("$BMI", 18.5));
		BasicDBObject bDb7 = new BasicDBObject("$lt", Arrays.asList("$BMI", 24));
		BasicDBObject bDb8 = new BasicDBObject("$and", Arrays.asList(bDb6, bDb7));
		BasicDBObject condBDB3 = new BasicDBObject("$cond", Arrays.asList(bDb8, "正常人群", condBDB2));
		
		BasicDBObject bDb9 = new BasicDBObject("$addFields", new BasicDBObject("BMIs",condBDB3));
		aggreList.add(bDb9);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("type","$BMIs").append("gender", "$gender")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id"), dbo.get("count"));
			}
		}
		return returnMap;
	}
	
	
	/**
	 * 
	 * @param tizhi 体质
	 * @param district 筛查区域
	 * @param disease 疾病：糖尿病患者/高血压患者/血脂异常患者/肥胖人群
	 * @param gender 性别
	 * @param age1 年龄
	 * @param age2 年龄
	 * @param type 空：初筛统计  js：精筛统计
	 * @return
	 */
	public Integer getTizhiCount(String tizhi, String district, String disease, String gender, String age1, String age2, String type) {
		DBCollection coll = db.getCollection("customer");
		BasicDBObject regulaQuery = new BasicDBObject();
		regulaQuery.put("$regex", "^" + tizhi + ".*$");
		
		Map<String, Object> query = Maps.newHashMap();
		if (StringUtils.isNotEmpty(district)) {
			query.put("district", district);
		} 
		
		if (StringUtils.isNotEmpty(gender)) {
			query.put("gender", gender);
		}
		
		
		if (StringUtils.isNotEmpty(type)) {
			BasicDBObject jsDbo = new BasicDBObject();
			jsDbo.put("$exists", true);
			query.put("classifyResultJs", jsDbo);
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
		
		if (StringUtils.isNotEmpty(age1) && StringUtils.isNotEmpty(age2)) {
			DBObject ageQuery = new BasicDBObject("$gte", age1).append("$lte", age2);
			query.put("age", ageQuery);
		}
		
		query.put("tizhi", regulaQuery);
		
		DBObject map=new BasicDBObject(query);
		Integer count = coll.find(map).count();
		
        return count;
	}
	
	/**
	 * 血压分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findBloodPressureDistribution(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		/*BasicDBObject dbo17 = new BasicDBObject("$gt", Arrays.asList("$lowPressure", 0));
		BasicDBObject dbo18 = new BasicDBObject("$lt", Arrays.asList("$lowPressure", 90));
		BasicDBObject dbo19 = new BasicDBObject("$gte", Arrays.asList("$highPressure", 140));
		BasicDBObject dbo20 = new BasicDBObject("$and", Arrays.asList(dbo17, dbo18, dbo19));
		BasicDBObject condDBO4 = new BasicDBObject("$cond", Arrays.asList(dbo20, "单纯收缩压高血压", "-"));*/
		
		BasicDBObject dbo10 = new BasicDBObject("$gte", Arrays.asList("$highPressure", 120));
		BasicDBObject dbo11 = new BasicDBObject("$lte", Arrays.asList("$highPressure", 139));
		BasicDBObject dbo12 = new BasicDBObject("$and", Arrays.asList(dbo10, dbo11));
		
		BasicDBObject dbo13 = new BasicDBObject("$gte", Arrays.asList("$lowPressure", 80));
		BasicDBObject dbo14 = new BasicDBObject("$lte", Arrays.asList("$lowPressure", 89));
		BasicDBObject dbo15 = new BasicDBObject("$and", Arrays.asList(dbo13, dbo14));
		
		BasicDBObject dbo16 = new BasicDBObject("$or", Arrays.asList(dbo12, dbo15));
		BasicDBObject condDBO3 = new BasicDBObject("$cond", Arrays.asList(dbo16, "正常高值", "-"));
		
		BasicDBObject dbo5 = new BasicDBObject("$gt", Arrays.asList("$highPressure", 0));
		BasicDBObject dbo6 = new BasicDBObject("$gt", Arrays.asList("$lowPressure", 0));
		BasicDBObject dbo7 = new BasicDBObject("$lt", Arrays.asList("$highPressure", 120));
		BasicDBObject dbo8 = new BasicDBObject("$lt", Arrays.asList("$lowPressure", 80));
		BasicDBObject dbo9 = new BasicDBObject("$and", Arrays.asList(dbo5, dbo6, dbo7, dbo8));
		BasicDBObject condDBO2 = new BasicDBObject("$cond", Arrays.asList(dbo9, "正常血压", condDBO3));
		
		BasicDBObject dbo1 = new BasicDBObject("$eq", Arrays.asList("$htn", "是"));
		BasicDBObject dbo2 = new BasicDBObject("$gte", Arrays.asList("$highPressure", 140));
		BasicDBObject dbo3 = new BasicDBObject("$gte", Arrays.asList("$lowPressure", 90));
		BasicDBObject dbo4 = new BasicDBObject("$or", Arrays.asList(dbo1, dbo2, dbo3));
		BasicDBObject condDBO1 = new BasicDBObject("$cond", Arrays.asList(dbo4, "高血压", condDBO2));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("BloodPressure",condDBO1));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","$BloodPressure").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id").toString(), dbo.get("count"));
			}
		}
		return returnMap;
	}
	
	
	/**
	 * 高血压分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findHighBloodPressureDistribution(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		BasicDBObject dbo1 = new BasicDBObject("$gte", Arrays.asList("$lowPressure", 90));
		BasicDBObject dbo2 = new BasicDBObject("$lte", Arrays.asList("$lowPressure", 99));
		BasicDBObject dbo3 = new BasicDBObject("$and", Arrays.asList(dbo1, dbo2));
		
		BasicDBObject dbo4 = new BasicDBObject("$gte", Arrays.asList("$highPressure", 140));
		BasicDBObject dbo5 = new BasicDBObject("$lte", Arrays.asList("$highPressure", 159));
		BasicDBObject dbo6 = new BasicDBObject("$and", Arrays.asList(dbo4, dbo5));
		
		BasicDBObject dbo7 = new BasicDBObject("$or", Arrays.asList(dbo3, dbo6));
		BasicDBObject condDBO1 = new BasicDBObject("$cond", Arrays.asList(dbo7, "轻度", "-"));
		
		
		BasicDBObject dbo8 = new BasicDBObject("$gte", Arrays.asList("$lowPressure", 100));
		BasicDBObject dbo9 = new BasicDBObject("$lte", Arrays.asList("$lowPressure", 109));
		BasicDBObject dbo10 = new BasicDBObject("$and", Arrays.asList(dbo8, dbo9));
		
		BasicDBObject dbo11 = new BasicDBObject("$gte", Arrays.asList("$highPressure", 160));
		BasicDBObject dbo12 = new BasicDBObject("$lte", Arrays.asList("$highPressure", 179));
		BasicDBObject dbo13 = new BasicDBObject("$and", Arrays.asList(dbo11, dbo12));
		
		BasicDBObject dbo14 = new BasicDBObject("$or", Arrays.asList(dbo10, dbo13));
		BasicDBObject condDBO2 = new BasicDBObject("$cond", Arrays.asList(dbo14, "中度", condDBO1));
		
		BasicDBObject dbo15 = new BasicDBObject("$gte", Arrays.asList("$lowPressure", 110));
		BasicDBObject dbo16 = new BasicDBObject("$gte", Arrays.asList("$highPressure", 180));
		BasicDBObject dbo17 = new BasicDBObject("$or", Arrays.asList(dbo15, dbo16));
		BasicDBObject condDBO3 = new BasicDBObject("$cond", Arrays.asList(dbo17, "重度", condDBO2));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("highBloodPressure",condDBO3));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","$highBloodPressure").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id").toString(), dbo.get("count"));
			}
		}
		return returnMap;
	}
	
	/**
	 * 各性别冠心病分布
	 * @param district
	 * @return
	 */
	public Map<String, Object> findCoronaryHeartDiseaseByGender(String district) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		
		BasicDBObject dbo1 = new BasicDBObject("$eq", Arrays.asList("$gender", "女"));
		BasicDBObject condDBO1 = new BasicDBObject("$cond", Arrays.asList(dbo1, "女非冠心病", "-"));
		
		BasicDBObject dbo2 = new BasicDBObject("$eq", Arrays.asList("$cpd", "是"));
		BasicDBObject dbo3 = new BasicDBObject("$and", Arrays.asList(dbo1, dbo2));
		BasicDBObject condDBO2 = new BasicDBObject("$cond", Arrays.asList(dbo3, "女冠心病", condDBO1));
		
		BasicDBObject dbo4 = new BasicDBObject("$eq", Arrays.asList("$gender", "男"));
		BasicDBObject condDBO3 = new BasicDBObject("$cond", Arrays.asList(dbo4, "男非冠心病", condDBO2));
		
		BasicDBObject dbo5 = new BasicDBObject("$eq", Arrays.asList("$cpd", "是"));
		BasicDBObject dbo6 = new BasicDBObject("$and", Arrays.asList(dbo4, dbo5));
		BasicDBObject condDBO4 = new BasicDBObject("$cond", Arrays.asList(dbo6, "男冠心病", condDBO3));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("gxb",condDBO4));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id", "$gxb").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id").toString(), dbo.get("count"));
			}
		}
		return returnMap;
	}
	
	
	/**
	 * 各年龄段冠心病分布
	 * @param district
	 * @return
	 */
	public Map<Object, Object> findCoronaryHeartDiseaseByAge(String district) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}
		
		BasicDBObject dbo1 = new BasicDBObject("$eq", Arrays.asList("$cpd", "是"));
		BasicDBObject condDBO = new BasicDBObject("$cond", Arrays.asList(dbo1, "冠心病", "非冠心病"));
		
		BasicDBObject addFieldsDBO1 = new BasicDBObject("$addFields",new BasicDBObject("gxb",condDBO));
		aggreList.add(addFieldsDBO1);
		
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
		
		BasicDBObject addFieldsDBO2 = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO6));
		aggreList.add(addFieldsDBO2);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("gxb","$gxb").append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			
			if (dbo.get("_id") != null && dbo.get("_id") != "") {
				returnMap.put(dbo.get("_id"), dbo.get("count"));
			}
		}
		return returnMap;
	}
	
	/*public List<Map<String,Object>> getCustomerData(String startTime, String endTime) {
		DBCollection coll = db.getCollection("healthcheck");
		DBObject query = new BasicDBObject();
		if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
			 query = new BasicDBObject("checkDate", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		}
		DBCursor cursor = coll.find(query);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.remove("_id").toString());
        	list.add(obj);
        }
        return list;
	}*/
	
	public List<Map<String,Object>> getCustomerData(String collName, String startTime, String endTime) {
		DBCollection coll = db.getCollection(collName);
		DBObject query = new BasicDBObject();
		if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
			 query = new BasicDBObject("checkDate", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		}
		DBCursor cursor = coll.find(query);
		List<Map<String,Object>> list = Lists.newArrayList();
        while (cursor.hasNext()) {
        	Map<String,Object> obj=(Map<String,Object>) cursor.next();
        	obj.put("id", obj.remove("_id").toString());
        	list.add(obj);
        }
        return list;
	}
	
	public List<Map<String,Object>> getCustomerDataPage(Page page, Map<String, Object> queryMap, Map<String, Object> sortMap, String collName) {
        DBCollection coll = db.getCollection(collName);
		DBObject query = new BasicDBObject("checkDate", new BasicDBObject("$gte", queryMap.get("startTime")).append("$lte", queryMap.get("endTime")));
		
		if (queryMap.get("district") != null) {
			query.put("district", queryMap.get("district"));
		}
		
		DBCursor cursor = coll.find(query);
        
		int skipCount=page.getFirst();
		int showCount=page.getNumPerPage();
		
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
	
	public long getCustomerCount(Map<String, Object> queryMap, String collName) {
        DBCollection coll = db.getCollection(collName);
		DBObject query = new BasicDBObject("checkDate", new BasicDBObject("$gte", queryMap.get("startTime")).append("$lte", queryMap.get("endTime")));
		
		if (queryMap.get("district") != null) {
			query.put("district", queryMap.get("district"));
		}
		
		long count = coll.find(query).count();
        return count;
	}
	
	/**
	 	* 	 db.getCollection('eyeRecord').aggregate([
	 	*		{
	 	*			"$match": {
		*				$and: [{
		*						'uniqueId': {
		*							'$exists': true
		*						}
		*				}, {
		*					'uniqueId': {
		*						'$ne': null
		*					}
		*				}, {
		*					'uniqueId': {
		*						'$ne': ''
		*					}
		*				}
		*			]
		*		}
		*	}, {
		*		"$sort": {
		*			utime: -1
		*		}
		*	}, {
		*		"$group": {
		*			"_id": {
		*				syndrome: "$analysisResult.syndrome",
		*				uniqueId: "$uniqueId"
		*			},
		*			uniqueId: {
		*				$first: "$uniqueId"
		*			},
		*			syndrome: {
		*				$first: "$analysisResult.syndrome"
		*			}
		*		}
		*
		*	}, {
		*		"$group": {
		*			"_id": "$syndrome",
		*			"count": {
		*				"$sum": 1
		*			}
		*
		*		}
		*	}, {
		*		"$sort": {
		*			"count":-1
		*		}
		*	}
		* ])

	 * @return
	 */
	public Map<String, Object> countBySyndrome() {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("eyeRecord");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		Map<String, Object> matchMap = Maps.newHashMap();
		matchMap.put("uniqueId", new BasicDBObject("$exists", true)
				.append("$ne", "")
				.append("$ne", null));
		
		matchMap.put("analysisResult.syndrome", new BasicDBObject("$exists", true)
				.append("$ne", "")
				.append("$ne", null));
		
		BasicDBObject matchDbo = new BasicDBObject("$match", matchMap);
		aggreList.add(matchDbo);
		
		BasicDBObject sortDbo = new BasicDBObject("$sort", new BasicDBObject("utime", -1));
		aggreList.add(sortDbo);
		
		Map<String, Object> groupMap = Maps.newHashMap();
		groupMap.put("syndrome", "$analysisResult.syndrome");
		groupMap.put("uniqueId", "$uniqueId");
		
		BasicDBObject groupDBO1 = new BasicDBObject("$group",new BasicDBObject("_id", new BasicDBObject(groupMap))
			.append("uniqueId", new BasicDBObject("$first", "$uniqueId"))
			.append("syndrome", new BasicDBObject("$first", "$analysisResult.syndrome")));
		aggreList.add(groupDBO1);
		
		BasicDBObject groupDBO2 = new BasicDBObject("$group",new BasicDBObject("_id","$syndrome")
			.append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO2);
		
//		BasicDBObject sortDbo2 = new BasicDBObject("$sort", new BasicDBObject("count", -1));
//		aggreList.add(sortDbo2);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			if(dbo.get("_id") != null && dbo.get("_id") != ""){
				String name = dbo.get("_id").toString();
				if (name.contains("。")) {
					returnMap.put(name.substring(0, name.length()-1), dbo.get("count"));
				}
			}
		}
		return returnMap;
	}
	
	/**
	 * 血糖筛查情况->血糖情况人群分布->精筛数据->糖尿病患者/糖尿病前期人群/正常人群
	 * @return
	 */
	public Map<String, Object> findBloodSugarConditionPeopleDistributionHeadthCheckDetail(String district) {
		
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		/*if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}*/
		BasicDBObject matchDBO = hcDetailMatchMap(district, "");
		aggreList.add(matchDBO);
		
		String str = "diabetesSituation";
		BasicDBObject bloodSugarCondDBO = bloodSugarDistributionHealthCheckDetail(str);
		aggreList.add(bloodSugarCondDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id", "$" + str).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put((String)dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	
	/**
	 * 血糖筛查情况->血糖情况年龄分布->精筛数据->糖尿病患者/糖尿病前期人群/正常人群
	 * @return
	 */
	public Map<Object, Object> findBloodSugarConditionAgeDistributionHealthCheckDetail(String district, List<Map<String, Object>> ageList) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		/*if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}*/
		
		BasicDBObject matchDBO = hcDetailMatchMap(district, "");
		aggreList.add(matchDBO);
		
		BasicDBObject condDBO = ageDistribution(ageList);
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO));
		aggreList.add(addFieldsDBO);
		
		String str = "diabetesSituation";
		BasicDBObject bloodSugarCondDBO = bloodSugarDistributionHealthCheckDetail(str);
		aggreList.add(bloodSugarCondDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("ds", "$" + str).append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put(dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	
	//精筛数据--血糖情况人群分布
	public BasicDBObject ageDistribution(List<Map<String, Object>> ageList) {
		/*[60,61,62,63,64]
		List<Integer> inList1 = new ArrayList<Integer>();
		inList1.add(60);
		inList1.add(61); 
		inList1.add(62); 
		inList1.add(63); 
		inList1.add(64); 
		inList1.add(65); 
		BasicDBObject inAgeDBO1 = new BasicDBObject("$in", Arrays.asList("$age",inList1));
		BasicDBObject condDBO1 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO1,"60-65","-"));
		
		[55,56,57,58,59]
		List<Integer> inList2 = new ArrayList<Integer>();
		inList2.add(55);
		inList2.add(56); 
		inList2.add(57); 
		inList2.add(58); 
		inList2.add(59); 
		BasicDBObject inAgeDBO2 = new BasicDBObject("$in",Arrays.asList("$age",inList2));
		BasicDBObject condDBO2 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO2,"55-59",condDBO1));
		
		[50,51,52,53,54]
		List<Integer> inList3 = new ArrayList<Integer>();
		inList3.add(50);
		inList3.add(51); 
		inList3.add(52); 
		inList3.add(53); 
		inList3.add(54); 
		BasicDBObject inAgeDBO3 = new BasicDBObject("$in",Arrays.asList("$age",inList3));
		BasicDBObject condDBO3 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO3,"50-54",condDBO2));
		
		[45,46,47,48,49]
		List<Integer> inList4 = new ArrayList<Integer>();
		inList4.add(45);
		inList4.add(46); 
		inList4.add(47); 
		inList4.add(48); 
		inList4.add(49); 
		BasicDBObject inAgeDBO4 = new BasicDBObject("$in",Arrays.asList("$age",inList4));
		BasicDBObject condDBO4 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO4,"45-49",condDBO3));
		
		[40,41,42,43,44]
		List<Integer> inList5 = new ArrayList<Integer>();
		inList5.add(40);
		inList5.add(41); 
		inList5.add(42); 
		inList5.add(43); 
		inList5.add(44); 
		BasicDBObject inAgeDBO5 = new BasicDBObject("$in",Arrays.asList("$age",inList5));
		BasicDBObject condDBO5 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO5,"40-44",condDBO4));
		
		[35,36,37,38,39]
		List<Integer> inList6 = new ArrayList<Integer>();
		inList6.add(35);
		inList6.add(36); 
		inList6.add(37); 
		inList6.add(38); 
		inList6.add(39); 
		BasicDBObject inAgeDBO6 = new BasicDBObject("$in",Arrays.asList("$age",inList6));
		BasicDBObject condDBO6 = new BasicDBObject("$cond",Arrays.asList(inAgeDBO6,"35-39",condDBO5));
		BasicDBObject addFieldsDBO2 = new BasicDBObject("$addFields",new BasicDBObject(ages,condDBO6));
		return addFieldsDBO2;*/
		
		
		BasicDBObject condDBO = null;
		for (Map<String,Object> ageMap:ageList) {
			List<Integer> inList = new ArrayList<Integer>();
			Integer start = (Integer)ageMap.get("start");
			String title = (String)ageMap.get("title");
			if (start == null) {
				start = 0;
			}
			Integer end = (Integer)ageMap.get("end");
			if (end == null) {
				end = 100;
			}
			for (int i = start; i <= end; i++) {
				inList.add(i);
			}
			BasicDBObject inAgeDBO = new BasicDBObject("$in",Arrays.asList("$age",inList));
			if (condDBO == null) {
				condDBO = new BasicDBObject("$cond",Arrays.asList(inAgeDBO,title,"-"));
			} else {
				condDBO = new BasicDBObject("$cond",Arrays.asList(inAgeDBO,title,condDBO));
			}
		}
		return condDBO;
	}
	
	//建档人群年龄分布
	public BasicDBObject ageDistributionDoc(List<Map<String, Object>> ageList) {
		BasicDBObject condDBO = null;
		for (Map<String,Object> ageMap:ageList) {
			List<Integer> inList = new ArrayList<Integer>();
			Integer start = (Integer)ageMap.get("start");
			String title = (String)ageMap.get("title");
			if (start == null) {
				start = 0;
			}
			Integer end = (Integer)ageMap.get("end");
			if (end == null) {
				end = 100;
			}
			for (int i = start; i <= end; i++) {
				inList.add(i);
			}
			BasicDBObject inAgeDBO = new BasicDBObject("$in",Arrays.asList("$age",inList));
			if (condDBO == null) {
				condDBO = new BasicDBObject("$cond",Arrays.asList(inAgeDBO,title,"-"));
			} else {
				condDBO = new BasicDBObject("$cond",Arrays.asList(inAgeDBO,title,condDBO));
			}
		}
		return condDBO;
	}
	
	//精筛数据--血糖情况人群分布
	public BasicDBObject bloodSugarDistributionHealthCheckDetail(String str) {
		BasicDBObject dbo1 = new BasicDBObject("$gt",Arrays.asList("$ogtt", 0));
		BasicDBObject dbo2 = new BasicDBObject("$lt",Arrays.asList("$ogtt", 6.1));
		BasicDBObject dbo3 = new BasicDBObject("$and",Arrays.asList(dbo1, dbo2));
		
		BasicDBObject dbo4 = new BasicDBObject("$gt",Arrays.asList("$ogtt2h", 0));
		BasicDBObject dbo5 = new BasicDBObject("$lt",Arrays.asList("$ogtt2h", 7.8));
		BasicDBObject dbo6 = new BasicDBObject("$and",Arrays.asList(dbo4, dbo5));
		
		BasicDBObject dbo7 = new BasicDBObject("$and",Arrays.asList(dbo3, dbo6));
		BasicDBObject dbo8 = new BasicDBObject("$cond",Arrays.asList(dbo7,"正常人群","-"));
		
		BasicDBObject dbo9 = new BasicDBObject("$gt",Arrays.asList("$ogtt", 0));
		BasicDBObject dbo10 = new BasicDBObject("$lt",Arrays.asList("$ogtt", 7));
		BasicDBObject dbo11 = new BasicDBObject("$and",Arrays.asList(dbo9, dbo10));
		
		BasicDBObject dbo12 = new BasicDBObject("$gte",Arrays.asList("$ogtt2h", 7.8));
		BasicDBObject dbo13 = new BasicDBObject("$lt",Arrays.asList("$ogtt2h", 11.1));
		BasicDBObject dbo14 = new BasicDBObject("$and",Arrays.asList(dbo12, dbo13));
		BasicDBObject dbo15 = new BasicDBObject("$and",Arrays.asList(dbo11, dbo14));
		
		BasicDBObject dbo16 = new BasicDBObject("$gte",Arrays.asList("$ogtt", 6.1));
		BasicDBObject dbo17 = new BasicDBObject("$lt",Arrays.asList("$ogtt", 7));
		BasicDBObject dbo18 = new BasicDBObject("$and",Arrays.asList(dbo16, dbo17));
		
		BasicDBObject dbo19 = new BasicDBObject("$gt",Arrays.asList("$ogtt2h", 0));
		BasicDBObject dbo20 = new BasicDBObject("$lt",Arrays.asList("$ogtt2h", 7.8));
		BasicDBObject dbo21 = new BasicDBObject("$and",Arrays.asList(dbo19, dbo20));
		BasicDBObject dbo22 = new BasicDBObject("$and",Arrays.asList(dbo18, dbo21));
		
		BasicDBObject dbo23 = new BasicDBObject("$or",Arrays.asList(dbo15, dbo22));
		BasicDBObject dbo24 = new BasicDBObject("$cond",Arrays.asList(dbo23,"糖尿病前期人群",dbo8));
		
		BasicDBObject dbo25 = new BasicDBObject("$gt",Arrays.asList("$ogtt2h", 0));
		BasicDBObject dbo26 = new BasicDBObject("$gte",Arrays.asList("$ogtt", 7));
		BasicDBObject dbo27 = new BasicDBObject("$and",Arrays.asList(dbo25,dbo26));
		
		BasicDBObject dbo28 = new BasicDBObject("$gt",Arrays.asList("$ogtt", 0));
		BasicDBObject dbo29 = new BasicDBObject("$gte",Arrays.asList("$ogtt2h", 11.1));
		BasicDBObject dbo30 = new BasicDBObject("$and",Arrays.asList(dbo28,dbo29));
		
		BasicDBObject dbo31 = new BasicDBObject("$or",Arrays.asList(dbo27,dbo30));
		BasicDBObject dbo32 = new BasicDBObject("$cond",Arrays.asList(dbo31,"新发现糖尿病患者",dbo24));
		
//		BasicDBObject dbo33 = new BasicDBObject("$eq",Arrays.asList("$dm", "是"));
//		BasicDBObject dbo35 = new BasicDBObject("$cond",Arrays.asList(dbo33,"已登记糖尿病患者",dbo32));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject(str,dbo32));
		return addFieldsDBO;
	}
	
	
	/**
	 * 血压筛查情况->血压情况人群分布->精筛数据->已登记高血压患者/新发现高血压人群/正常人群/高血压前期人群
	 * @return
	 */
	public Map<String, Object> findBloodPressureConditionPeopleDistributionHealthCheck(String district) {
		
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		/*if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}*/

		BasicDBObject matchDBO = hcDetailMatchMap(district, "");
		aggreList.add(matchDBO);
		
		String str = "bloodPressureConditionCharge";
		BasicDBObject bloodPressureCondDBO = bloodPressureDistributionHealthCheckDetail(str);
		aggreList.add(bloodPressureCondDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id", "$" + str).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put((String)dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	
	
	/**
	 * 血压筛查情况->血压情况年龄分布->精筛数据->已登记高血压患者/新发现高血压人群/正常人群/高血压前期人群
	 * @return
	 */
	public Map<Object, Object> findBloodPressureConditionAgeDistributionHealthCheckDetail(String district, List<Map<String, Object>> ageList) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		/*if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}*/
		
		BasicDBObject matchDBO = hcDetailMatchMap(district, "");
		aggreList.add(matchDBO);
		
		BasicDBObject condDBO = ageDistribution(ageList);
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO));
		aggreList.add(addFieldsDBO);
		
		String str = "bloodPressureConditionCharge";
		BasicDBObject bloodSugarCondDBO = bloodPressureDistributionHealthCheckDetail(str);
		aggreList.add(bloodSugarCondDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bp", "$" + str).append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put(dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	
	//精筛数据--血压情况人群分布  
	public BasicDBObject bloodPressureDistributionHealthCheckDetail(String str) {
		BasicDBObject normalDBO1 = new BasicDBObject("$lt",Arrays.asList("$highPressure2", 140));
		BasicDBObject normalDBO2 = new BasicDBObject("$lt",Arrays.asList("$lowPressure2", 90));
		BasicDBObject normalDBO3 = new BasicDBObject("$lt",Arrays.asList("$highPressure3", 140));
		BasicDBObject normalDBO4 = new BasicDBObject("$lt",Arrays.asList("$lowPressure3", 90));
		BasicDBObject normalDBO5 = new BasicDBObject("$lt",Arrays.asList("$highPressure4", 140));
		BasicDBObject normalDBO6 = new BasicDBObject("$lt",Arrays.asList("$lowPressure4", 90));
		BasicDBObject normalDBO7 = new BasicDBObject("$and",Arrays.asList(normalDBO1, normalDBO2, normalDBO3, normalDBO4, normalDBO5, normalDBO6));
		BasicDBObject dbo1 = new BasicDBObject("$cond",Arrays.asList(normalDBO7,"正常人群","高血压前期人群"));
		
		BasicDBObject newHypertensionDBO1 = new BasicDBObject("$gte",Arrays.asList("$highPressure2", 140));
		BasicDBObject newHypertensionDBO2 = new BasicDBObject("$gte",Arrays.asList("$lowPressure2", 90));
		BasicDBObject newHypertensionDBO3 = new BasicDBObject("$or",Arrays.asList(newHypertensionDBO1, newHypertensionDBO2));
		
		BasicDBObject newHypertensionDBO4 = new BasicDBObject("$gte",Arrays.asList("$highPressure3", 140));
		BasicDBObject newHypertensionDBO5 = new BasicDBObject("$gte",Arrays.asList("$lowPressure3", 90));
		BasicDBObject newHypertensionDBO6 = new BasicDBObject("$or",Arrays.asList(newHypertensionDBO4, newHypertensionDBO5));
		
		BasicDBObject newHypertensionDBO7 = new BasicDBObject("$gte",Arrays.asList("$highPressure4", 140));
		BasicDBObject newHypertensionDBO8 = new BasicDBObject("$gte",Arrays.asList("$lowPressure4", 90));
		BasicDBObject newHypertensionDBO9 = new BasicDBObject("$or",Arrays.asList(newHypertensionDBO7, newHypertensionDBO8));
		
		BasicDBObject newHypertensionDBO10 = new BasicDBObject("$and",Arrays.asList(newHypertensionDBO3, newHypertensionDBO6, newHypertensionDBO9));
		BasicDBObject dbo2 = new BasicDBObject("$cond",Arrays.asList(newHypertensionDBO10, "新发现高血压人群", dbo1));
		
		BasicDBObject otherDBO1 = new BasicDBObject("$eq",Arrays.asList("$checkDate2", null));
		BasicDBObject otherDBO2 = new BasicDBObject("$lte",Arrays.asList("$highPressure2", 0));
		BasicDBObject otherDBO3 = new BasicDBObject("$lte",Arrays.asList("$lowPressure2", 0));
		BasicDBObject otherDBO4 = new BasicDBObject("$eq",Arrays.asList("$checkDate3", null));
		BasicDBObject otherDBO5 = new BasicDBObject("$lte",Arrays.asList("$highPressure3", 0));
		BasicDBObject otherDBO6 = new BasicDBObject("$lte",Arrays.asList("$lowPressure3", 0));
		BasicDBObject otherDBO7 = new BasicDBObject("$eq",Arrays.asList("$checkDate4", null));
		BasicDBObject otherDBO8 = new BasicDBObject("$lte",Arrays.asList("$highPressure4", 0));
		BasicDBObject otherDBO9 = new BasicDBObject("$lte",Arrays.asList("$lowPressure4", 0));
		BasicDBObject otherDBO10 = new BasicDBObject("$eq",Arrays.asList("$checkDate2", "$checkDate3"));
		BasicDBObject otherDBO11 = new BasicDBObject("$eq",Arrays.asList("$checkDate3", "$checkDate4"));
		BasicDBObject otherDBO12 = new BasicDBObject("$eq",Arrays.asList("$checkDate2", "$checkDate4"));
		BasicDBObject otherDBO13 = new BasicDBObject("$or",Arrays.asList(otherDBO1, otherDBO2, otherDBO3, otherDBO4, otherDBO5, otherDBO6,otherDBO7, 
				otherDBO8, otherDBO9, otherDBO10, otherDBO11, otherDBO12));
		BasicDBObject dbo3 = new BasicDBObject("$cond",Arrays.asList(otherDBO13, "-", dbo2));
		
//		BasicDBObject hypertendionDBO = new BasicDBObject("$eq",Arrays.asList("$htn", "是"));
//		BasicDBObject dbo4 = new BasicDBObject("$cond",Arrays.asList(hypertendionDBO, "已登记高血压患者", dbo3));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject(str,dbo3));
		return addFieldsDBO;
	}
	
	
	//精筛数据--血脂情况人群分布--已登记血脂异常患者\新发现血脂异常患者\正常人群  
	public BasicDBObject bloodLipidConditionPeopleDistributionHealthCheck(String str) {
		BasicDBObject normalDBO1 = new BasicDBObject("$lt",Arrays.asList("$tcDetail", 5.2));
		BasicDBObject normalDBO2 = new BasicDBObject("$lt",Arrays.asList("$tgDetail", 1.7));
		BasicDBObject normalDBO3 = new BasicDBObject("$gte",Arrays.asList("$hdlDetail", 1));
		BasicDBObject normalDBO4 = new BasicDBObject("$and",Arrays.asList(normalDBO1, normalDBO2, normalDBO3));
		BasicDBObject dbo1 = new BasicDBObject("$cond",Arrays.asList(normalDBO4,"正常人群","-"));
		
		BasicDBObject newHighLipidDBO1 = new BasicDBObject("$gte",Arrays.asList("$tcDetail", 5.2));
		BasicDBObject newHighLipidDBO2 = new BasicDBObject("$gte",Arrays.asList("$tgDetail", 1.7));
		BasicDBObject newHighLipidDBO3 = new BasicDBObject("$lt",Arrays.asList("$hdlDetail", 1));
		BasicDBObject newHighLipidDBO4 = new BasicDBObject("$or",Arrays.asList(newHighLipidDBO1, newHighLipidDBO2,newHighLipidDBO3));
		BasicDBObject dbo2 = new BasicDBObject("$cond",Arrays.asList(newHighLipidDBO4,"新发现血脂异常患者",dbo1));
		
		BasicDBObject otherDBO1 = new BasicDBObject("$lte",Arrays.asList("$tcDetail", 0));
		BasicDBObject otherDBO2 = new BasicDBObject("$lte",Arrays.asList("$tgDetail", 0));
		BasicDBObject otherDBO3 = new BasicDBObject("$lte",Arrays.asList("$hdlDetail", 0));
		BasicDBObject otherDBO4 = new BasicDBObject("$or",Arrays.asList(otherDBO1, otherDBO2, otherDBO3));
		BasicDBObject dbo3 = new BasicDBObject("$cond",Arrays.asList(otherDBO4, "-", dbo2));
		
//		BasicDBObject highLipidDBO = new BasicDBObject("$eq",Arrays.asList("$hpl", "是"));
//		BasicDBObject dbo4 = new BasicDBObject("$cond",Arrays.asList(highLipidDBO, "已登记血脂异常患者", dbo3));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject(str,dbo3));
		return addFieldsDBO;
	}
	
	/**
	 * 血脂筛查情况--精筛数据--血脂情况人群分布--已登记血脂异常患者\新发现血脂异常患者\正常人群 
	 * @return
	 */
	public Map<String, Object> findBloodLipidConditionPeopleDistributionHealthCheck(String district) {
		
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		/*if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}*/
		
		BasicDBObject matchDBO = hcDetailMatchMap(district, "");
		aggreList.add(matchDBO);
		
		String str = "bloodLipidConditionCharge";
		BasicDBObject bloodLipidCondDBO = bloodLipidConditionPeopleDistributionHealthCheck(str);
		aggreList.add(bloodLipidCondDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id", "$" + str).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put((String)dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	
	
	/**
	 * 血脂筛查情况->精筛数据--血脂情况年龄分布--已登记血脂异常患者\新发现血脂异常患者\正常人群 
	 * @return
	 */
	public Map<Object, Object> findBloodLipidConditionAgePeopleDistributionHealthCheck(String district, List<Map<String, Object>> ageList) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		/*if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("$match", new BasicDBObject("district", district));
			aggreList.add(districtOBO);
		}*/
		
		BasicDBObject matchDBO = hcDetailMatchMap(district, "");
		aggreList.add(matchDBO);
		
		BasicDBObject condDBO = ageDistribution(ageList);
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO));
		aggreList.add(addFieldsDBO);
		
		String str = "bloodLipidConditionCharge";
		BasicDBObject bloodLipidCondDBO = bloodLipidConditionPeopleDistributionHealthCheck(str);
		aggreList.add(bloodLipidCondDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("bl", "$" + str).append("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put(dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	
	
	/**
	 * 体质筛查情况->精筛数据->体质与代谢疾病（糖尿病）患病率的关系
	 * @return
	 */
	public Map<String, Object> findTizhiConditionByDiabetesHealthCheckDetail(String district, String tizhi) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
/*		BasicDBObject tizhiOBO = new BasicDBObject("tizhi", new BasicDBObject("$regex", "^" + tizhi + ".*$"));
		BasicDBObject jsDbo = new BasicDBObject("classifyResultJs", new BasicDBObject("$exists", true));
		BasicDBObject matchDBO = null;
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBList list1 = new BasicDBList();
			BasicDBObject districtOBO = new BasicDBObject("district", district);
			list1.add(districtOBO);
			list1.add(tizhiOBO);
			list1.add(jsDbo);
			matchDBO = new BasicDBObject("$match", new BasicDBObject("$and", list1));
		} else {
			matchDBO = new BasicDBObject("$match", tizhiOBO);
		}
		aggreList.add(matchDBO);*/
		BasicDBObject matchDBO = hcDetailMatchMap(district, tizhi);
		aggreList.add(matchDBO);
		
		BasicDBObject diabetesDBO1 = new BasicDBObject("$eq",Arrays.asList("$dm", "是"));
		
		BasicDBObject diabetesDBO2 = new BasicDBObject("$gt",Arrays.asList("$ogtt2h", 0));
		BasicDBObject diabetesDBO3 = new BasicDBObject("$gte",Arrays.asList("$ogtt", 7));
		BasicDBObject diabetesDBO4 = new BasicDBObject("$and",Arrays.asList(diabetesDBO2, diabetesDBO3));
		
		BasicDBObject diabetesDBO5 = new BasicDBObject("$gt",Arrays.asList("$ogtt", 0));
		BasicDBObject diabetesDBO6 = new BasicDBObject("$gte",Arrays.asList("$ogtt2h", 11.1));
		BasicDBObject diabetesDBO7 = new BasicDBObject("$and",Arrays.asList(diabetesDBO5, diabetesDBO6));
		
		BasicDBObject diabetesDBO8 = new BasicDBObject("$or",Arrays.asList(diabetesDBO4, diabetesDBO7));
		BasicDBObject diabetesDBO9 = new BasicDBObject("$or",Arrays.asList(diabetesDBO8, diabetesDBO1));
		BasicDBObject diabetesDbo = new BasicDBObject("$cond",Arrays.asList(diabetesDBO9, "糖尿病患者", "-"));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("dm", diabetesDbo));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id", "$dm").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put((String)dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	
	
	/**
	 * 体质筛查情况->精筛数据->体质与代谢疾病（高血压）患病率的关系
	 * @return
	 */
	public Map<String, Object> findTizhiConditionByHypertensionHealthCheckDetail(String district, String tizhi) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		/*BasicDBObject tizhiOBO = new BasicDBObject("tizhi", new BasicDBObject("$regex", "^" + tizhi + ".*$"));
		BasicDBObject matchDBO = null;
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBList list1 = new BasicDBList();
			BasicDBObject districtOBO = new BasicDBObject("district", district);
			list1.add(districtOBO);
			list1.add(tizhiOBO);
			matchDBO = new BasicDBObject("$match", new BasicDBObject("$and", list1));
		} else {
			matchDBO = new BasicDBObject("$match", tizhiOBO);
		}
		aggreList.add(matchDBO);*/
		
		BasicDBObject matchDBO = hcDetailMatchMap(district, tizhi);
		aggreList.add(matchDBO);
		
		BasicDBObject diagnoseHtnDBO1 = new BasicDBObject("$eq",Arrays.asList("$htn", "是"));
		
		BasicDBObject diagnoseHtnDBO2 = new BasicDBObject("$ne",Arrays.asList("$checkDate2", null));
		BasicDBObject diagnoseHtnDBO3 = new BasicDBObject("$gt",Arrays.asList("$highPressure2", 0));
		BasicDBObject diagnoseHtnDBO4 = new BasicDBObject("$gt",Arrays.asList("$lowPressure2", 0));
		BasicDBObject diagnoseHtnDBO5 = new BasicDBObject("$ne",Arrays.asList("$checkDate3", null));
		BasicDBObject diagnoseHtnDBO6 = new BasicDBObject("$gt",Arrays.asList("$highPressure3", 0));
		BasicDBObject diagnoseHtnDBO7 = new BasicDBObject("$gt",Arrays.asList("$lowPressure3", 0));
		BasicDBObject diagnoseHtnDBO8 = new BasicDBObject("$ne",Arrays.asList("$checkDate4", null));
		BasicDBObject diagnoseHtnDBO9 = new BasicDBObject("$gt",Arrays.asList("$highPressure4", 0));
		BasicDBObject diagnoseHtnDBO10 = new BasicDBObject("$gt",Arrays.asList("$lowPressure4", 0));
		BasicDBObject diagnoseHtnDBO11 = new BasicDBObject("$and",Arrays.asList(diagnoseHtnDBO2, diagnoseHtnDBO3, diagnoseHtnDBO4, diagnoseHtnDBO5, diagnoseHtnDBO6,diagnoseHtnDBO7, diagnoseHtnDBO8, diagnoseHtnDBO9, diagnoseHtnDBO10));
		
		BasicDBObject diagnoseHtnDBO12 = new BasicDBObject("$gte",Arrays.asList("$highPressure2", 140));
		BasicDBObject diagnoseHtnDBO13 = new BasicDBObject("$gte",Arrays.asList("$lowPressure2", 90));
		BasicDBObject diagnoseHtnDBO14 = new BasicDBObject("$or",Arrays.asList(diagnoseHtnDBO12, diagnoseHtnDBO13));
		
		BasicDBObject diagnoseHtnDBO15 = new BasicDBObject("$gte",Arrays.asList("$highPressure3", 140));
		BasicDBObject diagnoseHtnDBO16 = new BasicDBObject("$gte",Arrays.asList("$lowPressure3", 90));
		BasicDBObject diagnoseHtnDBO17 = new BasicDBObject("$or",Arrays.asList(diagnoseHtnDBO15, diagnoseHtnDBO16));
		
		BasicDBObject diagnoseHtnDBO18 = new BasicDBObject("$gte",Arrays.asList("$highPressure4", 140));
		BasicDBObject diagnoseHtnDBO19 = new BasicDBObject("$gte",Arrays.asList("$lowPressure4", 90));
		BasicDBObject diagnoseHtnDBO20 = new BasicDBObject("$or",Arrays.asList(diagnoseHtnDBO18, diagnoseHtnDBO19));
		
		BasicDBObject diagnoseHtnDBO21 = new BasicDBObject("$and",Arrays.asList(diagnoseHtnDBO11, diagnoseHtnDBO14, diagnoseHtnDBO17, diagnoseHtnDBO20));
		BasicDBObject diagnoseHtnDBO22 = new BasicDBObject("$or",Arrays.asList(diagnoseHtnDBO21, diagnoseHtnDBO1));
		BasicDBObject diagnoseHtnDBO = new BasicDBObject("$cond",Arrays.asList(diagnoseHtnDBO22, "高血压患者", "-"));
		
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("htn", diagnoseHtnDBO));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id", "$htn").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put((String)dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	
	
	/**
	 * 体质筛查情况->精筛数据->体质与代谢疾病（血脂异常患者）患病率的关系
	 * @return
	 */
	public Map<String, Object> findTizhiConditionByHplHealthCheckDetail(String district, String tizhi) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		/*BasicDBObject tizhiOBO = new BasicDBObject("tizhi", new BasicDBObject("$regex", "^" + tizhi + ".*$"));
		BasicDBObject matchDBO = null;
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBList list1 = new BasicDBList();
			BasicDBObject districtOBO = new BasicDBObject("district", district);
			list1.add(districtOBO);
			list1.add(tizhiOBO);
			matchDBO = new BasicDBObject("$match", new BasicDBObject("$and", list1));
		} else {
			matchDBO = new BasicDBObject("$match", tizhiOBO);
		}
		aggreList.add(matchDBO);*/
		
		BasicDBObject matchDBO = hcDetailMatchMap(district, tizhi);
		aggreList.add(matchDBO);
		
		BasicDBObject diagnoseHplDBO1 = new BasicDBObject("$eq",Arrays.asList("$hpl", "是"));
		
		BasicDBObject diagnoseHplDBO2 = new BasicDBObject("$gt",Arrays.asList("$hdlDetail", 0));
		BasicDBObject diagnoseHplDBO3 = new BasicDBObject("$lt",Arrays.asList("$hdlDetail", 1));
		BasicDBObject diagnoseHplDBO4 = new BasicDBObject("$and",Arrays.asList(diagnoseHplDBO2, diagnoseHplDBO3));
		
		BasicDBObject diagnoseHplDBO5 = new BasicDBObject("$gte",Arrays.asList("$tcDetail", 5.2));
		BasicDBObject diagnoseHplDBO6 = new BasicDBObject("$gte",Arrays.asList("$tgDetail", 1.7));
		BasicDBObject diagnoseHplDBO7 = new BasicDBObject("$or",Arrays.asList(diagnoseHplDBO4, diagnoseHplDBO5, diagnoseHplDBO6));
		BasicDBObject diagnoseHplDBO8 = new BasicDBObject("$or",Arrays.asList(diagnoseHplDBO1, diagnoseHplDBO7));
		
		BasicDBObject hplDBO = new BasicDBObject("$cond",Arrays.asList(diagnoseHplDBO8, "血脂异常患者", "-"));
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("hpl", hplDBO));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id", "$hpl").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put((String)dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	
	public BasicDBObject hcDetailMatchMap(String district, String tizhi){
		BasicDBList list1 = new BasicDBList();
		BasicDBObject jsDbo = new BasicDBObject("classifyResultJs", new BasicDBObject("$exists", true));
		list1.add(jsDbo);
		
		if(StringUtils.isNotEmpty(tizhi)) {
			BasicDBObject tizhiOBO = new BasicDBObject("tizhi", new BasicDBObject("$regex", "^" + tizhi + ".*$"));
			list1.add(tizhiOBO);
		}
		
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject districtOBO = new BasicDBObject("district", district);
			list1.add(districtOBO);
		} 
		
		BasicDBObject matchDBO = new BasicDBObject("$match", new BasicDBObject("$and", list1));
		return matchDBO;
	}
	
	/**
	 * 初筛统计->高血压检测总人数
	 * @return
	 */
	public int findHtnCount(String district) {
		
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBList orList = new BasicDBList();
		BasicDBObject highPressureDBO = new BasicDBObject("highPressure", new BasicDBObject("$gte",140));
		BasicDBObject lowPressureDBO = new BasicDBObject("lowPressure",new BasicDBObject("$gte",90));
		orList.add(lowPressureDBO);
		orList.add(highPressureDBO);
		BasicDBObject query2 = new BasicDBObject("$or",orList);
		
		BasicDBObject matchDBO = null;
		if (StringUtils.isNotEmpty(district)) {
			BasicDBList list1 = new BasicDBList();
			BasicDBObject query1 = new BasicDBObject("district", district);
			list1.add(query1);
			list1.add(query2);
			matchDBO = new BasicDBObject("$match", new BasicDBObject("$and",list1));
		} else {
			matchDBO = new BasicDBObject("$match", query2);
		}
		aggreList.add(matchDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		
		int total = 0;
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			total = (int) dbo.get("count");
		}
		return total;
	}
	
	//精筛血糖情况人数统计
	public int healthcheckDetailBloodSugarCount(String district) {
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBObject dbo1 = new BasicDBObject("ogtt",new BasicDBObject("$ne",null));
		BasicDBObject dbo2 = new BasicDBObject("ogtt2h",new BasicDBObject("$ne",null));
		BasicDBObject dbo3 = new BasicDBObject("$and",Arrays.asList(dbo1, dbo2));
		
		//BasicDBObject dbo4 = new BasicDBObject("dm", "是");
		//BasicDBObject dbo5 = new BasicDBObject("$or",Arrays.asList(dbo3, dbo4));
		
		BasicDBObject dbo7 = new BasicDBObject("classifyResultJs", new BasicDBObject("$exists",true));
		BasicDBObject dbo8 = null;
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject dbo6 = new BasicDBObject("district", district);
//			dbo8 = new BasicDBObject("$and",Arrays.asList(dbo5, dbo6, dbo7));
			dbo8 = new BasicDBObject("$and",Arrays.asList(dbo3, dbo6, dbo7));
		} else {
//			dbo8 = new BasicDBObject("$and",Arrays.asList(dbo5, dbo7)); 
			dbo8 = new BasicDBObject("$and",Arrays.asList(dbo3, dbo7)); 
		}
		BasicDBObject matchDBO = new BasicDBObject("$match", dbo8);
		aggreList.add(matchDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		int total = 0;
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			total = (int) dbo.get("count");
		}
		return total;
	}
	
	
	//精筛血压情况人数统计
	public int healthcheckDetailBloodPressureCount(String district) {
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBObject htnDBO1 = new BasicDBObject("checkDate2",new BasicDBObject("$ne",null));
		BasicDBObject htnDBO2 = new BasicDBObject("highPressure2",new BasicDBObject("$ne",null));
		BasicDBObject htnDBO3 = new BasicDBObject("lowPressure2",new BasicDBObject("$ne",null));
		BasicDBObject htnDBO4 = new BasicDBObject("checkDate3",new BasicDBObject("$ne",null));
		BasicDBObject htnDBO5 = new BasicDBObject("highPressure3",new BasicDBObject("$ne",null));
		BasicDBObject htnDBO6 = new BasicDBObject("lowPressure3",new BasicDBObject("$ne",null));
		BasicDBObject htnDBO7 = new BasicDBObject("checkDate4",new BasicDBObject("$ne",null));
		BasicDBObject htnDBO8 = new BasicDBObject("highPressure4",new BasicDBObject("$ne",null));
		BasicDBObject htnDBO9 = new BasicDBObject("lowPressure4",new BasicDBObject("$ne",null));
		BasicDBObject dbo3 = new BasicDBObject("$and",Arrays.asList(htnDBO1, htnDBO2, htnDBO3, htnDBO4, htnDBO5, htnDBO6, htnDBO7, htnDBO8, htnDBO9));
		
		//BasicDBObject dbo4 = new BasicDBObject("htn", "是");
		//BasicDBObject dbo5 = new BasicDBObject("$or",Arrays.asList(dbo3, dbo4));
		
		BasicDBObject dbo7 = new BasicDBObject("classifyResultJs", new BasicDBObject("$exists",true));
		BasicDBObject dbo8 = null;
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject dbo6 = new BasicDBObject("district", district);
			dbo8 = new BasicDBObject("$and",Arrays.asList(dbo3, dbo6, dbo7));
		} else {
			dbo8 = new BasicDBObject("$and",Arrays.asList(dbo3, dbo7)); 
		}
		BasicDBObject matchDBO = new BasicDBObject("$match", dbo8);
		aggreList.add(matchDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		int total = 0;
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			total = (int) dbo.get("count");
		}
		return total;
	}
	
	//精筛血压情况人数统计
	public int healthcheckDetailBloodLipidCount(String district) {
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBObject hplDBO1 = new BasicDBObject("tcDetail",new BasicDBObject("$ne",null));
		BasicDBObject hplDBO2 = new BasicDBObject("tgDetail",new BasicDBObject("$ne",null));
		BasicDBObject hplDBO3 = new BasicDBObject("hdlDetail",new BasicDBObject("$ne",null));
		BasicDBObject dbo3 = new BasicDBObject("$and",Arrays.asList(hplDBO1, hplDBO2, hplDBO3));
		
		BasicDBObject dbo4 = new BasicDBObject("hpl", "是");
		BasicDBObject dbo5 = new BasicDBObject("$or",Arrays.asList(dbo3, dbo4));
		
		BasicDBObject dbo7 = new BasicDBObject("classifyResultJs", new BasicDBObject("$exists",true));
		BasicDBObject dbo8 = null;
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject dbo6 = new BasicDBObject("district", district);
			dbo8 = new BasicDBObject("$and",Arrays.asList(dbo5, dbo6, dbo7));
		} else {
			dbo8 = new BasicDBObject("$and",Arrays.asList(dbo5, dbo7)); 
		}
		BasicDBObject matchDBO = new BasicDBObject("$match", dbo8);
		aggreList.add(matchDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		int total = 0;
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			total = (int) dbo.get("count");
		}
		return total;
	}
	
	
	/**
	 * 获取最新一条随访初筛记录
	 * @param userId
	 * @return
	 */
	public Map<String,Object> getLatestVisitRecord(Map<String, Object> params) {
		DBObject queryObj = new BasicDBObject();
		 
		queryObj.put("uniqueId", params.get("uniqueId"));
		DBCollection coll = db.getCollection("healthcheck");
		DBCursor cursor = coll.find(queryObj).sort(new BasicDBObject("checkDate", -1));
		
		if (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Map<String,Object> map = obj.toMap();
			map.put("id", map.remove("_id").toString());
			return map;
		}
		return null;

	}
	
	
	//已做并发症人数统计
	public int healthcheckDetailComplicationCount(String district) {
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBObject matchDBO = getComplicationCount(district);
		aggreList.add(matchDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id", "").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		int total = 0;
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			total = (int) dbo.get("count");
		}
		return total;
	}
	
	//未做并发症人数统计
	public int healthcheckDetailNoComplicationCount(String district) {
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBObject matchDBO = getNoComplicationCount(district);
		aggreList.add(matchDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id","").append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		int total = 0;
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			total = (int) dbo.get("count");
		}
		return total;
	}
	
	/**
	 * 血糖筛查情况->血糖情况年龄分布->精筛数据->已做并发症人数
	 * @return
	 */
	public Map<Object, Object> healthcheckDetailComplicationCountByAge(String district, List<Map<String, Object>> ageList) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBObject matchDBO = getComplicationCount(district);
		aggreList.add(matchDBO);
		
		BasicDBObject condDBO = ageDistribution(ageList);
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put(dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	
	/**
	 * 血糖筛查情况->血糖情况年龄分布->精筛数据->已做并发症人数
	 * @return
	 */
	public Map<Object, Object> healthcheckDetailNoComplicationCountByAge(String district, List<Map<String, Object>> ageList) {
		Map<Object,Object> returnMap = new HashMap<Object,Object>();
		DBCollection coll = db.getCollection("customer");
		List<BasicDBObject> aggreList = new ArrayList<BasicDBObject>();
		
		BasicDBObject matchDBO = getNoComplicationCount(district);
		aggreList.add(matchDBO);
		
		BasicDBObject condDBO = ageDistribution(ageList);
		BasicDBObject addFieldsDBO = new BasicDBObject("$addFields",new BasicDBObject("ages",condDBO));
		aggreList.add(addFieldsDBO);
		
		BasicDBObject groupDBO = new BasicDBObject("$group",new BasicDBObject("_id",new BasicDBObject("ages", "$ages")).append("count", new BasicDBObject("$sum",1)));
		aggreList.add(groupDBO);
		
		Iterable<DBObject> result = coll.aggregate(aggreList).results();
		for(DBObject dbo : result){
			returnMap.put(dbo.get("_id"), dbo.get("count"));
		}
		return returnMap;
	}
	
	public BasicDBObject getComplicationCount(String district) {
		BasicDBObject comDBO1 = new BasicDBObject("malb",new BasicDBObject("$ne",null));
		BasicDBObject comDBO2 = new BasicDBObject("ucr",new BasicDBObject("$ne",null));
		BasicDBObject comDBO3 = new BasicDBObject("dryLeft",new BasicDBObject("$ne",null));
		BasicDBObject comDBO4 = new BasicDBObject("dryRight",new BasicDBObject("$ne",null));
		BasicDBObject comDBO5 = new BasicDBObject("chapLeft",new BasicDBObject("$ne",null));
		BasicDBObject comDBO6= new BasicDBObject("chapRight",new BasicDBObject("$ne",null));
		BasicDBObject comDBO7 = new BasicDBObject("peelLeft",new BasicDBObject("$ne",null));
		BasicDBObject comDBO8 = new BasicDBObject("peelRight",new BasicDBObject("$ne",null));
		BasicDBObject comDBO9 = new BasicDBObject("cornLeft",new BasicDBObject("$ne",null));
		BasicDBObject comDBO10 = new BasicDBObject("cornRight",new BasicDBObject("$ne",null));
		BasicDBObject comDBO11 = new BasicDBObject("malLeft",new BasicDBObject("$ne",null));
		BasicDBObject comDBO12 = new BasicDBObject("malRight",new BasicDBObject("$ne",null));
		BasicDBObject comDBO13 = new BasicDBObject("callusLeft",new BasicDBObject("$ne",null));
		BasicDBObject comDBO14 = new BasicDBObject("callusRight",new BasicDBObject("$ne",null));
		BasicDBObject comDBO15 = new BasicDBObject("fungalLeft",new BasicDBObject("$ne",null));
		BasicDBObject comDBO16 = new BasicDBObject("fungalRight",new BasicDBObject("$ne",null));
		BasicDBObject comDBO17 = new BasicDBObject("ulcerLeft",new BasicDBObject("$ne",null));
		BasicDBObject comDBO18 = new BasicDBObject("ulcerRight",new BasicDBObject("$ne",null));
		BasicDBObject comDBO19 = new BasicDBObject("feelLeftFirst",new BasicDBObject("$ne",null));
		BasicDBObject comDBO20 = new BasicDBObject("feelRightFirst",new BasicDBObject("$ne",null));
		BasicDBObject comDBO21 = new BasicDBObject("feelLeftSecond",new BasicDBObject("$ne",null));
		BasicDBObject comDBO22 = new BasicDBObject("feelRightSecond",new BasicDBObject("$ne",null));
		BasicDBObject comDBO23 = new BasicDBObject("feelLeftThird",new BasicDBObject("$ne",null));
		BasicDBObject comDBO24 = new BasicDBObject("feelRightThird",new BasicDBObject("$ne",null));
		BasicDBObject comDBO25 = new BasicDBObject("feelLeftResult",new BasicDBObject("$ne",null));
		BasicDBObject comDBO26 = new BasicDBObject("feelRightResult",new BasicDBObject("$ne",null));
		BasicDBObject comDBO27 = new BasicDBObject("fundus",new BasicDBObject("$ne",""));
		BasicDBObject dbo1 = new BasicDBObject("$or",Arrays.asList(comDBO1, comDBO2, comDBO3,
				comDBO4, comDBO5, comDBO6, comDBO7, comDBO8, comDBO9, comDBO10, comDBO11,
				comDBO12, comDBO13, comDBO14, comDBO15, comDBO16, comDBO17, comDBO18, comDBO19,
				comDBO20, comDBO21, comDBO22, comDBO23, comDBO24, comDBO25, comDBO26, comDBO27));
		
		BasicDBObject dbo2 = new BasicDBObject("classifyResultJs", new BasicDBObject("$exists",true));
		BasicDBObject dbo4 = null;
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject dbo3 = new BasicDBObject("district", district);
			dbo4 = new BasicDBObject("$and",Arrays.asList(dbo1, dbo2, dbo3));
		} else {
			dbo4 = new BasicDBObject("$and",Arrays.asList(dbo1, dbo2)); 
		}
		BasicDBObject matchDBO = new BasicDBObject("$match", dbo4);
		return matchDBO;
	}
	
	public BasicDBObject getNoComplicationCount(String district) {
		BasicDBObject comDBO1 = new BasicDBObject("malb",new BasicDBObject("$eq",null));
		BasicDBObject comDBO2 = new BasicDBObject("ucr",new BasicDBObject("$eq",null));
		BasicDBObject comDBO3 = new BasicDBObject("dryLeft",new BasicDBObject("$eq",null));
		BasicDBObject comDBO4 = new BasicDBObject("dryRight",new BasicDBObject("$eq",null));
		BasicDBObject comDBO5 = new BasicDBObject("chapLeft",new BasicDBObject("$eq",null));
		BasicDBObject comDBO6= new BasicDBObject("chapRight",new BasicDBObject("$eq",null));
		BasicDBObject comDBO7 = new BasicDBObject("peelLeft",new BasicDBObject("$eq",null));
		BasicDBObject comDBO8 = new BasicDBObject("peelRight",new BasicDBObject("$eq",null));
		BasicDBObject comDBO9 = new BasicDBObject("cornLeft",new BasicDBObject("$eq",null));
		BasicDBObject comDBO10 = new BasicDBObject("cornRight",new BasicDBObject("$eq",null));
		BasicDBObject comDBO11 = new BasicDBObject("malLeft",new BasicDBObject("$eq",null));
		BasicDBObject comDBO12 = new BasicDBObject("malRight",new BasicDBObject("$eq",null));
		BasicDBObject comDBO13 = new BasicDBObject("callusLeft",new BasicDBObject("$eq",null));
		BasicDBObject comDBO14 = new BasicDBObject("callusRight",new BasicDBObject("$eq",null));
		BasicDBObject comDBO15 = new BasicDBObject("fungalLeft",new BasicDBObject("$eq",null));
		BasicDBObject comDBO16 = new BasicDBObject("fungalRight",new BasicDBObject("$eq",null));
		BasicDBObject comDBO17 = new BasicDBObject("ulcerLeft",new BasicDBObject("$eq",null));
		BasicDBObject comDBO18 = new BasicDBObject("ulcerRight",new BasicDBObject("$eq",null));
		BasicDBObject comDBO19 = new BasicDBObject("feelLeftFirst",new BasicDBObject("$eq",null));
		BasicDBObject comDBO20 = new BasicDBObject("feelRightFirst",new BasicDBObject("$eq",null));
		BasicDBObject comDBO21 = new BasicDBObject("feelLeftSecond",new BasicDBObject("$eq",null));
		BasicDBObject comDBO22 = new BasicDBObject("feelRightSecond",new BasicDBObject("$eq",null));
		BasicDBObject comDBO23 = new BasicDBObject("feelLeftThird",new BasicDBObject("$eq",null));
		BasicDBObject comDBO24 = new BasicDBObject("feelRightThird",new BasicDBObject("$eq",null));
		BasicDBObject comDBO25 = new BasicDBObject("feelLeftResult",new BasicDBObject("$eq",null));
		BasicDBObject comDBO26 = new BasicDBObject("feelRightResult",new BasicDBObject("$eq",null));
		BasicDBObject comDBO27 = new BasicDBObject("fundus",new BasicDBObject("$eq",""));
		BasicDBObject comDBO28 = new BasicDBObject("classifyResultJs",new BasicDBObject("$exists", true));
		BasicDBObject dbo1 = new BasicDBObject("$and",Arrays.asList(comDBO1, comDBO2, comDBO3,
				comDBO4, comDBO5, comDBO6, comDBO7, comDBO8, comDBO9, comDBO10, comDBO11,
				comDBO12, comDBO13, comDBO14, comDBO15, comDBO16, comDBO17, comDBO18, comDBO19,
				comDBO20, comDBO21, comDBO22, comDBO23, comDBO24, comDBO25, comDBO26, comDBO27, comDBO28));
		
		BasicDBObject comDBO29 = new BasicDBObject("bloodSugarCondition", "糖尿病患者");
		
		BasicDBObject comDBO30 = new BasicDBObject("ogtt2h",new BasicDBObject("$gt", 0));
		BasicDBObject comDBO31 = new BasicDBObject("ogtt",new BasicDBObject("$gte", 7));
		BasicDBObject comDBO32 = new BasicDBObject("$and",Arrays.asList(comDBO30, comDBO31));
		
		BasicDBObject comDBO33 = new BasicDBObject("ogtt2h",new BasicDBObject("$gt", 11.1));
		BasicDBObject comDBO34 = new BasicDBObject("ogtt",new BasicDBObject("$gte", 0));
		BasicDBObject comDBO35 = new BasicDBObject("$and",Arrays.asList(comDBO33, comDBO34));
		BasicDBObject comDBO36 = new BasicDBObject("$or",Arrays.asList(comDBO32, comDBO35));
		
		BasicDBObject comDBO37 = new BasicDBObject("bloodSugarCondition",new BasicDBObject("$ne", "糖尿病患者"));
		BasicDBObject comDBO38 = new BasicDBObject("bloodGlucose",new BasicDBObject("$gte", 7));
		BasicDBObject comDBO39 = new BasicDBObject("bloodGlucose2h",new BasicDBObject("$gte", 11.1));
		BasicDBObject comDBO40 = new BasicDBObject("bloodGlucoseRandom",new BasicDBObject("$gte", 11.1));
		BasicDBObject comDBO41 = new BasicDBObject("$or",Arrays.asList(comDBO38, comDBO39, comDBO40));
		BasicDBObject comDBO42 = new BasicDBObject("$and",Arrays.asList(comDBO37, comDBO41));
		
		BasicDBObject dbo2 = new BasicDBObject("$or",Arrays.asList(comDBO29, comDBO36, comDBO42));
		
		BasicDBObject dbo4 = null;
		if (StringUtils.isNotEmpty(district)) {
			BasicDBObject dbo3 = new BasicDBObject("district", district);
			dbo4 = new BasicDBObject("$and",Arrays.asList(dbo1, dbo2, dbo3));
		} else {
			dbo4 = new BasicDBObject("$and",Arrays.asList(dbo1, dbo2)); 
		}
		BasicDBObject matchDBO = new BasicDBObject("$match", dbo4);
		return matchDBO;
	}
	
}
