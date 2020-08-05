package com.capiltalbio.health.client;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.capitalbio.common.dao.MongoBaseDAO;


import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.common.util.RandomUtil;



public class RandomCustomerData {

	public static Map<String,Object> randomCustomerData(Integer num) throws Exception{
		MongoBaseDAO mongoBaseDAO = new MongoBaseDAO();
		String[] diseaseArr = {"是","否"};
		String[] familyHistoryArr = {"是","否"};
		String[] checkPlaceArr = {"海州区","阜蒙县"};
		String[] genderArr = {"男","女"};
		String[] householdRegistrationTypeArr = {"户籍","非户籍"};
		String[] tizhiArr = {"平和质","气虚质","阳虚质","阴虚质","痰湿质","湿热质","气郁质","血瘀质","特禀质"};
		String[] eyeCheckArr = {"已检测","未检测"};
		String[] bloodSugarConditionArr = {"糖尿病患者","血糖异常人群","糖尿病高风险人群", "正常", ""};
		String[] bloodPressureConditionArr = {"", "正常", "血压异常人群", "高血压患者"};
		String[] bloodLipidConditionArr = {"血脂异常患者", "血脂异常高风险人群","", "正常"};
		String[] needArr = {"需要","不需要"};
		for (int i = 0; i < num; i++) 
		{
			Map<String, Object> map = new HashMap<String,Object>();
			String birthday = RandomUtil.randomInt(1917, 2017) + "-"+ RandomUtil.randomInt(1, 12) +"-"+
			RandomUtil.randomInt(1, 28);
			String recordDate = 2017+ "-"+ RandomUtil.randomInt(1, 10) +"-"+
					RandomUtil.randomInt(1, 28);
			map.put("name", "name"+i);
			map.put("gender", genderArr[RandomUtil.randomInt(0, 1)]);
			map.put("age", RandomUtil.randomInt(1, 100));
			map.put("birthday", birthday);
			map.put("mobile", 1851519+RandomUtil.randomInt(0000, 2852));
			map.put("householdRegistrationType", householdRegistrationTypeArr[RandomUtil.randomInt(0, 1)]);
			map.put("recordDate", recordDate);
			
			map.put("height", RandomUtil.randomDouble(100, 220));
			map.put("weight", RandomUtil.randomDouble(20, 220));
			map.put("BMI", RandomUtil.randomDouble(12, 30));
			map.put("waistline", RandomUtil.randomDouble(12, 100));
			map.put("hipline", RandomUtil.randomDouble(12, 100));
			map.put("WHR", RandomUtil.randomDouble(0.1, 2));
			map.put("fatContent", RandomUtil.randomDouble(0.1, 80));
			map.put("highPressure", RandomUtil.randomDouble(50, 210));
			map.put("lowPressure", RandomUtil.randomDouble(20, 160));
			map.put("temperature", RandomUtil.randomDouble(35, 41));
			map.put("oxygen", RandomUtil.randomDouble(80, 99));
			map.put("pulse", RandomUtil.randomDouble(40, 200));
			map.put("bloodGlucose", RandomUtil.randomDouble(1.11, 33.3));
			map.put("ogtt", RandomUtil.randomDouble(1.11, 33.3));
			map.put("bloodGlucose2h", RandomUtil.randomDouble(1.11, 33.3));
			map.put("ogtt2h", RandomUtil.randomDouble(1.11, 33.3));
			map.put("bloodGlucoseRandom", RandomUtil.randomDouble(1.11, 33.3));
			map.put("tg", RandomUtil.randomDouble(0.57, 5.65));
			map.put("tc", RandomUtil.randomDouble(2.59, 10.36));
			map.put("hdl", RandomUtil.randomDouble(0.39, 2.59));
			map.put("ldl", RandomUtil.randomDouble(0.39, 2.59));
			
			map.put("dm", diseaseArr[RandomUtil.randomInt(0, 1)]);
			map.put("htn", diseaseArr[RandomUtil.randomInt(0, 1)]);
			map.put("cpd", diseaseArr[RandomUtil.randomInt(0, 1)]);
			map.put("hpl", diseaseArr[RandomUtil.randomInt(0, 1)]);
			
			map.put("tizhi", tizhiArr[RandomUtil.randomInt(0, 8)]);
			map.put("eyeCheck", eyeCheckArr[RandomUtil.randomInt(0, 1)]);
			map.put("bloodSugarCondition", bloodSugarConditionArr[RandomUtil.randomInt(0, 2)]);
			map.put("bloodLipidCondition", bloodLipidConditionArr[RandomUtil.randomInt(0, 3)]);
			map.put("bloodPressureCondition", bloodPressureConditionArr[RandomUtil.randomInt(0, 2)]);
			map.put("OGTTTest", needArr[RandomUtil.randomInt(0, 1)]);
			map.put("bloodLipidTest", needArr[RandomUtil.randomInt(0, 1)]);
			map.put("bloodPressureTest", needArr[RandomUtil.randomInt(0, 1)]);
			map.put("geneTest", needArr[RandomUtil.randomInt(0, 1)]);
			map.put("classifyResult", map.get("bloodSugarCondition")+","+map.get("bloodLipidCondition")+","
			+map.get("bloodPressureCondition"));
			
			map.put("riskScore", RandomUtil.randomInt(1, 35));
			
			map.put("familyHistory", familyHistoryArr[RandomUtil.randomInt(0, 1)]);
			
			map.put("checkDate", "2018-11-09");
			map.put("checkTime", new Date());
			
			map.put("checkTag", "是");
			if (map.get("height") != null || map.get("weight") != null || map.get("waistline") != null 
					|| map.get("hipline")!= null || map.get("fatContent") != null
					|| map.get("highPressure") != null || map.get("lowPressure") != null
					|| map.get("temperature") != null || map.get("oxygen") != null
					|| map.get("pulse") != null) {
				map.put("examTag", "是");
			}
			if (map.get("bloodGlucose") != null || map.get("bloodGlucose2h") != null || map.get("bloodGlucoseRandom") != null) {
				map.put("bloodSugarTag", "是");
			}
			if (map.get("tg") != null || map.get("tgStr") != null 
					|| map.get("tc") != null || map.get("tcStr") != null
					|| map.get("hdl") != null || map.get("ldl") != null
					) {
				map.put("bloodFatTag", "是");
			}
			if (map.get("highPressure") != null || map.get("lowPressure") != null ) {
				map.put("pressureTag", "是");
			}
			map.put("district", checkPlaceArr[RandomUtil.randomInt(0, 1)]);
			
			map.put("checkDate2", "2020-03-06");
			map.put("highPressure2", RandomUtil.randomInt(95, 150));
			map.put("lowPressure2", RandomUtil.randomInt(60, 110));
			
			map.put("checkDate3", "2020-03-07");
			map.put("highPressure3", RandomUtil.randomInt(95, 150));
			map.put("lowPressure3", RandomUtil.randomInt(60, 110));
			
			map.put("checkDate4", "2020-03-08");
			map.put("highPressure4", RandomUtil.randomInt(95, 150));
			map.put("lowPressure4", RandomUtil.randomInt(60, 110));
			
			map.put("ogtt", RandomUtil.randomDouble(1, 10));
			map.put("ogtt2h", RandomUtil.randomDouble(5, 15));
			
			map.put("tgDetail", RandomUtil.randomDouble(1, 5));
			map.put("tcDetail", RandomUtil.randomDouble(1, 6));
			map.put("hdlDetail", RandomUtil.randomDouble(0.1, 2));
			
//			mongoBaseDAO.saveData("healthcheck", map);
			
			sendRequest("http://localhost:8080/screen/healthcheck/saveDataRandom.htm", map);
		}
		
		
		return null;
		
	}
	
	private static String sendRequest(String url, Map<String, Object> map)
			throws Exception {
		HttpPost httpPost = new HttpPost(url);
		String json = JsonUtil.mapToJson(map);
		System.out.println(json);
		StringEntity se = new StringEntity(json, "UTF-8");
		httpPost.setEntity(se);
		httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
//		httpPost.addHeader(HTTP.CONTENT_ENCODING, "gzip");
//		httpPost.addHeader("userId",userId);
//		httpPost.addHeader("token",token);
		se.setContentType("text/json");
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
				"application/json"));
		HttpResponse response = new DefaultHttpClient().execute(httpPost);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
			Map<String,Object> retmap = JsonUtil.jsonToMap(result);
			int retcode = (Integer)retmap.get("code");
			if (retcode == 0) {
				String data = (String)retmap.get("data");
				return data;
			}
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		randomCustomerData(20);
		System.out.println("SUCCESS!");
		
	}
}
