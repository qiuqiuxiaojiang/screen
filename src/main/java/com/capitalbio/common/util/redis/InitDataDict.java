package com.capitalbio.common.util.redis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.capitalbio.common.util.CommUtil;
import com.capitalbio.common.util.FileUtil;
import com.google.common.collect.Maps;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;


/**  
* <p>Title: InitDataDict</p>  
* <p>Description:数据字典初始化 </p>  
* @author guohuiyang  
* @date 2018年6月28日 下午4:23:52
*/
public class InitDataDict {
	Logger log = LoggerFactory.getLogger(getClass());
	/**Redis工具类模版*/
	JedisTemplate template = RedisUtils.getTemplate();
	String REDIS_BUCKEY=DataDictUtil.getRedisBucket();
	
	/** redis筛查项目信息key **/
	private static String screeningProjectRedisKey = DataDictUtil.getRedisBucket() + CommUtil.getConfigByString("redis.key.screening.project", "redis.key.screening.project");
	/**  
	 * <p>Title: disease</p>  
	 * <p>Description: 疾病名称字典表</p>    
	 */  
	public void disease(){
		String key = REDIS_BUCKEY+"dict:disease";
		String localFilename="dict/disease.dict";
		dictData2Hash(key, localFilename);
	}
	/**  
	 * <p>Title: pastMedicalHistoryOrder</p>  
	 * <p>Description: 既往病疾病名称排序表</p>    
	 */  
	public void pastMedicalHistoryOrder(){
		String key = REDIS_BUCKEY+"dict:past_medical_history_order";
		String localFilename="dict/past_medical_history_order.dict";
		orderData2List(key, localFilename);
	}
	
	/**  
	 * <p>Title: familyMedicalHistoryOrder</p>  
	 * <p>Description: 家族病疾病名称排序表</p>    
	 */  
	public void familyMedicalHistoryOrder(){
		String key = REDIS_BUCKEY+"dict:family_medical_history_order";
		String localFilename="dict/family_medical_history_order.dict";
		orderData2List(key, localFilename);
	}
	
	/**  
	 * <p>Title: disease</p>  
	 * <p>Description: 亲属关系字典表</p>    
	 */  
	public void familyRelation(){
		String key = REDIS_BUCKEY+"dict:family_relation";
		String localFilename="dict/family_relation.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**  
	 * <p>Title: disease</p>  
	 * <p>Description: 家人模块中的家人关系字典表</p>    
	 */  
	public void familyModule(){
		String key = REDIS_BUCKEY+"dict:family_member_relation";
		String localFilename="dict/family_module.dict";
		familyModule(key, localFilename);
	}
	
	/**  
	 * <p>Title: familyMedicalHistoryOrder</p>  
	 * <p>Description: 亲属关系排序表</p>    
	 */  
	public void familyRelationOrder(){
		String key = REDIS_BUCKEY+"dict:family_relation_order";
		String localFilename="dict/family_relation_order.dict";
		orderData2List(key, localFilename);
	}
	
	/**  
	 * <p>Title: disease</p>  
	 * <p>Description: 过敏源字典表</p>    
	 */  
	public void allergen(){
		String key = REDIS_BUCKEY+"dict:allergen";
		String localFilename="dict/allergen.dict";
		dictData2Hash(key, localFilename);
	}
	

	/**
	 * <p>Title: allergen_order</p>
	 * <p>Description: 过敏源排序表</p>
	 */
	public void allergenOrder(){
		String key = REDIS_BUCKEY+"dict:allergen_order";
		String localFilename="dict/allergen_order.dict";
		orderData2List(key, localFilename);
	}
	
	public void medication(){
		String key = REDIS_BUCKEY+"dict:medication";
		String localFilename="dict/medication.dict";
		dictData2Hash(key, localFilename);
	}
	

	/**
	 * <p>Title: allergen_order</p>
	 * <p>Description: 用药排序表</p>
	 */
	public void medicationOrder(){
		String key = REDIS_BUCKEY+"dict:medication_order";
		String localFilename="dict/medication_order.dict";
		orderData2List(key, localFilename);
	}
	
	/**
	 * <p>Title: familyMedicalHistoryOrder</p>
	 * <p>Description: 手术部位字典表</p>
	 */
	public void surgicalSite(){
		String key = REDIS_BUCKEY+"dict:surgical_site";
		String localFilename="dict/surgical_site.dict";
		dictData2Hash(key, localFilename);
	}
	/**
	 * <p>Title: familyMedicalHistoryOrder</p>
	 * <p>Description: 手术部位排序表</p>
	 */
	public void surgicalSiteOrder(){
		String key = REDIS_BUCKEY+"dict:surgical_site_order";
		String localFilename="dict/surgical_site_order.dict";
		orderData2List(key, localFilename);
	}
	/**
	 * <p>Title: gender</p>
	 * <p>Description: 性别字典表</p>
	 */
	public void gender(){
		String key = REDIS_BUCKEY+"dict:gb:gender";
		String localFilename="dict/gender.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: profession</p>
	 * <p>Description: 职业字典表</p>
	 */
	public void profession(){
		String key = REDIS_BUCKEY+"dict:gb:profession";
		String localFilename="dict/profession.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: district</p>
	 * <p>Description: 行政区字典表</p>
	 */
	public void district(){
		String key = REDIS_BUCKEY+"dict:gb:district";
		String localFilename="dict/district.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: nationality</p>
	 * <p>Description: 民族字典表</p>
	 */
	public void nationality(){
		String key = REDIS_BUCKEY+"dict:gb:nationality";
		String localFilename="dict/nationality.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: idType</p>
	 * <p>Description: 证件类型字典表</p>
	 */
	public void idType(){
		String key = REDIS_BUCKEY+"dict:idType";
		String localFilename="dict/idType.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: idTypeOrder</p>
	 * <p>Description: 证件类型排序字典表</p>
	 */
	public void idTypeOrder(){
		String key = REDIS_BUCKEY+"dict:idType_order";
		String localFilename="dict/idType_order.dict";
		orderData2List(key, localFilename);
	}
	
	/**
	 * <p>Title: subsidiary</p>
	 * <p>Description: 所属机构字典表</p>
	 */
	public void subsidiary(){
		String key = REDIS_BUCKEY+"dict:subsidiary";
		String localFilename="dict/subsidiary.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: subsidiaryOrder</p>
	 * <p>Description: 所属机构排序字典表</p>
	 */
	public void subsidiaryOrder(){
		String key = REDIS_BUCKEY+"dict:subsidiary_order";
		String localFilename="dict/subsidiary_order.dict";
		orderData2List(key, localFilename);
	}
	
	/**
	 * <p>Title: subsidiary</p>
	 * <p>Description: 用户注册默认的所属机构字典</p>
	 */
	public void subsidiaryDefault(){
		String key = REDIS_BUCKEY+"dict:subsidiary_default";
		String localFilename="dict/subsidiary_default.dict";
		dictDataKey(key, localFilename);
	}
	
	/**
	 * <p>Title: educationBackground</p>
	 * <p>Description: 学历字典表</p>
	 */
	public void educationBackground(){
		String key = REDIS_BUCKEY+"dict:gb:education_background";
		String localFilename="dict/education_background.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: degree</p>
	 * <p>Description: 学位字典表</p>
	 */
	public void degree(){
		String key = REDIS_BUCKEY+"dict:gb:degree";
		String localFilename="dict/degree.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: bloodType</p>
	 * <p>Description: 血型字典表</p>
	 */
	public void bloodType(){
		String key = REDIS_BUCKEY+"dict:ga:blood_type";
		String localFilename="dict/blood_type.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: maritalStatus</p>
	 * <p>Description: 婚姻状况字典表</p>
	 */
	public void maritalStatus(){
		String key = REDIS_BUCKEY+"dict:gb:marital_status";
		String localFilename="dict/marital_status.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: geneTesingOrg</p>
	 * <p>Description: 基因检测机构字典表</p>
	 */
	public void geneTesingOrg(){
		String key = REDIS_BUCKEY+"dict:gene_tesing_org";
		String localFilename="dict/gene_tesing_org.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: geneTesingCategory</p>
	 * <p>Description: 基因检测分类字典表</p>
	 */
	public void geneTesingCategory(){
		String key = REDIS_BUCKEY+"dict:gene_tesing_category";
		String localFilename="dict/gene_tesing_category.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: geneTesingReportType</p>
	 * <p>Description: 基因检测报告类型分类字典表</p>
	 */
	public void geneTesingReportType(){
		String key = REDIS_BUCKEY+"dict:gene_tesing_report_type";
		String localFilename="dict/gene_tesing_report_type.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: geneTesingReportTypeContrast</p>
	 * <p>Description: 基因检测分类与报告类型对照字典表</p>
	 */
	public void geneTesingReportTypeContrast(){
		String key = REDIS_BUCKEY+"dict:gene_tesing_report_type_contrast";
		String localFilename="dict/gene_tesing_report_type_contrast.dict";
		dictData2Hash(key, localFilename);
	}
	
	/**
	 * <p>Title: indicator_all</p>
	 * <p>Description: 全部身体指标字典表</p>
	 */
	public void indicatorAll(){
		String key = REDIS_BUCKEY+"dict:indicator_all";
		String localFilename="dict/Indicator.dict";
		indicatorAll(key, localFilename);
	}
	
	/**
	 * <p>Title: indicator_all</p>
	 * <p>Description: 全部身体指标字典表</p>
	 */
	public void sport(){
		String key = REDIS_BUCKEY+"dict:sport";
		String localFilename="dict/sport.dict";
		sportAll(key, localFilename);
	}
	
	/**
	 * <p>Title: wine</p>
	 * <p>Description: 全部饮酒字典表</p>
	 */
	public void wine(){
		String key = REDIS_BUCKEY+"dict:wine";
		String localFilename="dict/wine.dict";
		wine(key, localFilename);
	}
	
	/**
	 * <p>Title: airCat</p>
	 * <p>Description: airCat监测项目信息</p>
	 */
	public void airCat(){
		String key = REDIS_BUCKEY+"dict:airCat";
		String localFilename="dict/aircat.dict";
		airCat(key, localFilename);
	}
	
	/**
	 * <p>Title: 社区慢病筛查项目</p>
	 * <p>Description: airCat监测项目信息</p>
	 */
	public void screeningProject(){
		String localFilename="dict/screeningProject.dict";
		screeningProject(screeningProjectRedisKey, localFilename);
	}
	
	/**
	 * <p>Title: orderData2Rpush</p>
	 * <p>
	 * Description: 将一个或多个值 value 插入到列表 key 的表尾(最右边)。 如果有多个 value 值，那么各个 value
	 * 值按从左到右的顺序依次插入到表尾： 比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，
	 * 等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。
	 * </p>
	 * @param key Redis 键名
	 * @param localFilename 本地文件名称 
	 */ 
	public void orderData2List(String key,String localFilename){
		/**如果key存在就删除*/
		if(template.exists(key)){
			template.del(key);
		}
		List<String> list = FileUtil.LocalFileToList(localFilename);
		String[] strings = new String[list.size()];
		list.toArray(strings);
		template.rpush(key, strings);
		List<String> lrange = template.lrange(key, 0, template.llen(key).intValue());
		log.info("Redis 键名：" + key + " value:"+lrange.toString());
	}
	
	/**  
	 * <p>Title: dictData2Hash</p>  
	 * <p>Description: HGET key field ；返回哈希表 key 中给定域 field 的值。</p>  
	 * @param key Redis 键名
	 * @param localFilename 本地文件名称 
	 */  
	public void dictData2Hash(String key,String localFilename){
		/**如果key存在就删除*/
		if(template.exists(key)){
			template.del(key);
		}
		Map<String, Object> map = FileUtil.LocalFileToMap(localFilename);
		Set<Entry<String, Object>> entrySet = map.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			template.hset(key, entry.getKey(), entry.getValue().toString());
		}
		Map<String, String> hgetAll = template.hgetAll(key);
		log.info("Redis 键名：" + key + " value:"+hgetAll.toString());
	}
	
	/**  
	 * <p>Title: 家人模块中家人关系</p>  
	 * @param key Redis 键名
	 * @param localFilename 本地文件名称 
	 */  
	public void familyModule(String key,String localFilename){
		/**如果key存在就删除*/
		if(template.exists(key)){
			template.del(key);
		}
		Map<String, Object> map = FileUtil.LocalFileToMap(localFilename);
		Set<Entry<String, Object>> entrySet = map.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			template.hset(key, entry.getKey(), entry.getValue().toString());
		}
		Map<String, String> hgetAll = template.hgetAll(key);
		log.info("Redis 键名：" + key + " value:"+hgetAll.toString());
	}
	
	/**  
	 * <p>Title: dictData2Hash</p>  
	 * <p>Description:  key value ；返回哈希表 key 中给定域 field 的值。</p>  
	 * @param key Redis 键名
	 * @param localFilename 本地文件名称 
	 */  
	public void dictDataKey(String key,String localFilename){
		/**如果key存在就删除*/
		if(template.exists(key)){
			template.del(key);
		}
		List<String> list = FileUtil.LocalFileToList(localFilename);
		for (String str : list) {
			template.set(key, str);
		}
		String val = template.get(key);
		log.info("Redis 键名：" + key + " value:"+val);
	}
	
	/**  
	 * <p>Title: initMsgCode</p>  
	 * <p>Description: 初始化msg字段 接口调用返回的消息。用于展示给用户的提示信息</p>    
	 */  
	public void msgCode(){
		JedisTemplate template = RedisUtils.getTemplate();
		String configFilename ="MsgCode.properties";
		String KEY = REDIS_BUCKEY+"Msg";
		Properties prop = new Properties();
		try{
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(configFilename));
            Set<Object> keyset = prop.keySet();
            Iterator<Object> it = keyset.iterator();
            log.info("##################### Init Msg Code #####################");
            while(it.hasNext()){
                String fieldName = (String)it.next();
                String value = null;
                value = prop.getProperty(fieldName);
                log.info("KEY=" + KEY + "&&fieldName=" +fieldName +"&&value=" + value);
                template.hset(KEY, fieldName, value);
            }
        }catch(Exception exp){
            throw new RuntimeException("MsgCode配置文件：" + configFilename + "不存在！\r\n");
        }
	}
	
	/**  
	 * <p>Title: initDebugInfoCode</p>  
	 * <p>Description: debug_info字段接口调用错误描述。用于APP开发人员调试接口</p>    
	 */  
	public void debugInfoCode(){
		JedisTemplate template = RedisUtils.getTemplate();
		String configFilename ="DebugInfoCode.properties";
		String KEY = REDIS_BUCKEY+"DebugInfo";
		Properties prop = new Properties();
		try{
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(configFilename));
            Set<Object> keyset = prop.keySet();
            Iterator<Object> it = keyset.iterator();
            log.info("##################### Init DebugInfo Code #####################");
            while(it.hasNext()){
                String fieldName = (String)it.next();
                String value = null;
                value = prop.getProperty(fieldName);
                log.info("KEY=" + KEY + "&&fieldName=" +fieldName +"&&value=" + value);
                template.hset(KEY, fieldName, value);
            }
        }catch(Exception exp){
            throw new RuntimeException("DebugInfoCode配置文件：" + configFilename + "不存在！\r\n");
        }
	}
	/**  
	 * <p>Title: dictData2Hash</p>  
	 * <p>Description: HGET key field ；返回哈希表 key 中给定域 field 的值。</p>  
	 * @param key Redis 键名
	 * @param localFilename 本地文件名称 
	 */  
	public void indicatorAll(String key,String localFilename){
		/**如果key存在就删除*/
		if(template.exists(key)){
			template.del(key);
		}
		Map<String, Object> map = FileUtil.LocalFileToMap(localFilename);
		Set<Entry<String, Object>> entrySet = map.entrySet();
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		Map<String, Object> indexMap = null;
		int i=0;
 		for (Entry<String, Object> entry : entrySet) {
 			i++;
 			if(i!=1){
 				String[] indexInfo = entry.getValue().toString().split("\t",9);
				indexMap = Maps.newHashMap();
				indexMap.put("code", indexInfo[0]);
				indexMap.put("name", indexInfo[1]);
				indexMap.put("maxValue", indexInfo[3]);
				indexMap.put("minValue", indexInfo[2]);
				indexMap.put("unit", indexInfo[4]);
				/*String[] scales = indexInfo[5].split(",");
				Double[] dScales = new Double[scales.length];
				for(int j=0;j<scales.length;j++){
					dScales[j] = Double.parseDouble(scales[j]);
				}
				indexMap.put("scaleList", dScales);*/
				indexMap.put("isNeedOtherField", Integer.parseInt(indexInfo[7]));
				indexMap.put("precision", Integer.parseInt(indexInfo[8]));
				resultList.add(indexMap);
 			}
		}
 		JSONObject json = new JSONObject();
 		json.put("r", resultList);
 		template.set(key,json.toJSONString());
		String getAll = template.get(key);
		log.info("Redis 键名：" + key + " value:"+getAll);
	}
	/**  
	 * <p>Title: dictData2Hash</p>  
	 * <p>Description: HGET key field ；返回哈希表 key 中给定域 field 的值。</p>  
	 * @param key Redis 键名
	 * @param localFilename 本地文件名称 
	 */  
	public void sportAll(String key,String localFilename){
		/**如果key存在就删除*/
		if(template.exists(key)){
			template.del(key);
		}
		Map<String, Object> map = FileUtil.LocalFileToMap(localFilename);
		Set<Entry<String, Object>> entrySet = map.entrySet();
		int i=0;
 		for (Entry<String, Object> entry : entrySet) {
 			i++;
 			if(i!=1){
 				String[] indexInfo = entry.getValue().toString().split("\t",2);
 				if(indexInfo!=null&&indexInfo.length==2){
 					template.hset(key, entry.getKey()+"_met", indexInfo[1]);
 				}
 			}
		}
 		Map<String, String> getAll = template.hgetAll(key);
		log.info("Redis 键名：" + key + " value:"+JSONObject.toJSONString(getAll));
	}
	/**  
	 * <p>Title: dictData2Hash</p>  
	 * <p>Description: HGET key field ；返回哈希表 key 中给定域 field 的值。</p>  
	 * @param key Redis 键名
	 * @param localFilename 本地文件名称 
	 */  
	public void wine(String key,String localFilename){
		/**如果key存在就删除*/
		if(template.exists(key)){
			template.del(key);
		}
		Map<String, Object> map = FileUtil.LocalFileToMap(localFilename);
		Set<Entry<String, Object>> entrySet = map.entrySet();
		int i=0;
		JSONObject json = null;
 		for (Entry<String, Object> entry : entrySet) {
 			i++;
 			if(i!=1){
 				String[] indexInfo = entry.getValue().toString().split("\t",5);
 				if(indexInfo!=null&&indexInfo.length==4){
 					json = new JSONObject();
 					json.put("name", indexInfo[0]);
 					json.put("imageUrl", indexInfo[1]);
 					json.put("unit", indexInfo[2]);
 					json.put("unitScale", indexInfo[3]);
 					template.hset(key, String.valueOf(i-1), json.toString());
 				}
 			}
		}
 		Map<String, String> getAll = template.hgetAll(key);
		log.info("Redis 键名：" + key + " value:"+JSONObject.toJSONString(getAll));
	}
	/**  
	 * <p>Title: dictData2Hash</p>  
	 * <p>Description: HGET key field ；返回哈希表 key 中给定域 field 的值。</p>  
	 * @param key Redis 键名
	 * @param localFilename 本地文件名称 
	 */  
	public void airCat(String key,String localFilename){
		/**如果key存在就删除*/
		if(template.exists(key)){
			template.del(key);
		}
		Map<String, Object> map = FileUtil.LocalFileToMap(localFilename);
		Set<Entry<String, Object>> entrySet = map.entrySet();
 		for (Entry<String, Object> entry : entrySet) {
 			String[] indexInfo = entry.getValue().toString().split("\t",1);
			if(indexInfo!=null){
				template.set(key, indexInfo[0]);
			}
		}
 		if(template.exists(key+"Detail")){
			template.del(key+"Detail");
		}
 		String getAll = template.get(key);
 		JSONArray rules = JSONArray.parseArray(getAll);
		for(int i = 0;i<rules.size();i++){
			JSONObject json = rules.getJSONObject(i);
			template.hset(key+"Detail", json.getString("monitoringItemsCategory").toString(), json.toString());
		}
		log.info("Redis 键名：" + key + " value:"+JSONObject.toJSONString(getAll));
	}
	
	/**  
	 * <p>Title: screeningProject</p>  
	 * <p>Description: HGET key field ；返回哈希表 key 中给定域 field 的值。</p>  
	 * @param key Redis 键名
	 * @param localFilename 本地文件名称 
	 */
	public void screeningProject(String key,String localFilename){
		/**如果key存在就删除*/
		if(template.exists(key)){
			template.del(key);
		}
		Map<String, Object> map = FileUtil.LocalFileToMap(localFilename);
		Set<Entry<String, Object>> entrySet = map.entrySet();
 		for (Entry<String, Object> entry : entrySet) {
 			String fieldName = entry.getKey().toString();
 			String jsonValue = entry.getValue().toString();
 			template.hset(key, fieldName, jsonValue);
		}
		log.info("Redis 键名：" + key + " value:"+JSONObject.toJSONString(map));
	}
	
	/**  
	 * <p>Title: systemMaintenanceModeStatus</p>  
	 * <p>Description: 系统维护模式</p>  
	 * @param key
	 * @param localFilename  
	 * @author guohuiyang  
	 * Create Time: 2019-12-03 09:18:49<br/>  
	 */  
	public void systemMaintenanceModeStatus(){
		/**关闭系统维护模式*/
		String OFF = "OFF";
		/**开启系统维护模式*/
		String ON  = "ON";
		/**默认：关闭系统维护模式*/
		String DEFAULT_STATUS = "OFF";
		String key = REDIS_BUCKEY+"system_maintenance_mode";
		String localFilename="dict/system_maintenance_mode";
		dictData2Hash(key, localFilename);
	}
	
	
	
	public static void main(String[] args) {
		InitDataDict init=new InitDataDict();
//		init.surgicalSite();
//		init.surgicalSiteOrder();
//		init.familyMedicalHistoryOrder();
//		init.familyRelation();
//		init.familyRelationOrder();
//		init.allergen();
//		init.allergenOrder();
//		init.medication();
//		init.medicationOrder();
//		init.msgCode();
//		init.debugInfoCode();
//		init.gender();
//		init.profession();
//		init.district();
//		init.nationality();
//		init.idType();
//		init.idTypeOrder();
//		init.subsidiary();
//		init.subsidiaryOrder();
//		init.subsidiaryDefault();
//		init.educationBackground();
//		init.degree();
//		init.bloodType();
//		init.geneTesingOrg();
//		init.geneTesingCategory();
//		init.geneTesingReportType();
//		init.geneTesingReportTypeContrast();
//		init.indicatorAll();
//		init.sport();
//		init.wine();
//		init.airCat();
		init.screeningProject("dict:screening_project", "dict/screeningProject.dict");
	}
}
