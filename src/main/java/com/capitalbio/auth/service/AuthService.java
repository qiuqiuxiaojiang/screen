package com.capitalbio.auth.service;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.capitalbio.auth.util.Constant;
import com.capitalbio.auth.util.HttpUtils;
import com.capitalbio.auth.util.JwtUtil;
import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.common.util.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
@SuppressWarnings("unchecked")
public class AuthService {
	private Logger logger = LoggerFactory.getLogger(getClass()); 
	@Value("${auth.url}")
	private String url;
	
	@Value("${doctor.url}")
	private String doctorUrl;
	
	@Value("${redis.key.screening.project}")
	private String subject;
	
	public Map<String,Object> requestInfo(String customerId, String token, String userId) {
		
		String getUrl = url + "/wx/getUserInfoById?id="+customerId;
		Map<String,Object> returnMap = Maps.newHashMap();
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			httpGet.addHeader("token", token);
			httpGet.addHeader("userId", userId);
			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				logger.debug("result:" + result);
				Map<String,Object> map = JsonUtil.jsonToMap(result);
				Integer retCode = (Integer)map.get("code");
				Map<String, Object> dataMap = (Map<String,Object>)map.get("data");
				if (retCode == 0 && dataMap != null && dataMap.size() > 0) {
					returnMap.put("uniqueId", String.valueOf(dataMap.get("userId")));
					returnMap.putAll(dataMap);
					return returnMap;
				}
				returnMap.put("msg", map.get("msg"));
			} else {
				logger.debug("HTTP error code:"+code);
				returnMap.put("msg", "认证系统访问错误");
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnMap.put("msg", "认证系统访问错误");
		}
		return returnMap;
	}
//	public Map<String,Object> requestInfo(String customerId, String token, String userId) {
//		
//		String getUrl = url + "/wx/getUserInfoById?id="+customerId;
//		try {
//			HttpGet httpGet = new HttpGet(getUrl);
//			httpGet.addHeader("token", token);
//			httpGet.addHeader("userId", userId);
//			HttpResponse response = HttpUtils.getClient().execute(httpGet);
//			int code = response.getStatusLine().getStatusCode();
//			logger.debug("code:" + code);
//			if (code == 200) {
//				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
//				logger.debug("result:" + result);
//				Map<String,Object> map = JsonUtil.jsonToMap(result);
//				Integer retCode = (Integer)map.get("code");
//				Map<String, Object> dataMap = (Map<String,Object>)map.get("data");
//				if (retCode == 0 && dataMap != null && dataMap.size() > 0) {
//					dataMap.put("uniqueId", String.valueOf(dataMap.get("userId")));
//					return dataMap;
//				}
//				return null;
//			} else {
//				logger.debug("HTTP error code:"+code);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public List<String> queryInfoByName(String name) {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		String getUrl = url + "/dm/getUserInfoByName?name="+name;
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			httpGet.addHeader("token", token);
			httpGet.addHeader("userId", userId);
			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			logger.debug("code:" + code);
			List<String> returnList = Lists.newArrayList();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				logger.debug("result:" + result);
				Map<String,Object> map = JsonUtil.jsonToMap(result);
				Integer retCode = (Integer)map.get("code");
				Map<String,Object> dataMap = (Map<String,Object>)map.get("data");
				if (retCode == 0 && dataMap != null &&dataMap.size() > 0) {
					List<Map<String, Object>> dataList = (List<Map<String,Object>>)dataMap.get("r");
					if (dataList != null && dataList.size() > 0) {
						for (Map<String,Object> idMap:dataList) {
							Object userIdObj = idMap.remove("userId");
							if (userIdObj != null) {
								String uniqueId = String.valueOf(userIdObj);
								returnList.add(uniqueId);
							}
						}
						return returnList;
					}
				}
				return null;
			} else {
				logger.debug("HTTP error code:"+code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	public Map<String,Object> newDocInfo(Map<String,Object> secretMap, String token, String userId) {
//		String token = (String) ContextUtils.getSession().getAttribute("token");
//		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		String postUrl = url+"/dm/saveUserInfo";
		try {
			HttpPost httpPost = new HttpPost(postUrl);
			String json = JsonUtil.mapToJson(secretMap);
			StringEntity se = new StringEntity(json, "UTF-8");
			httpPost.setEntity(se);
			httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
			httpPost.addHeader("token", token);
			httpPost.addHeader("userId", userId);
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			
			HttpResponse response = HttpUtils.getClient().execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			Map<String,Object> returnMap = Maps.newHashMap();
					
			logger.debug("code-newDocInfo:" + code);
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				logger.debug(result);
				Map<String,Object> retmap = JsonUtil.jsonToMap(result);
				Map<String,Object> dataMap = (Map<String,Object>)retmap.get("data");
				
				if (dataMap != null && dataMap.size() > 0) {
					if (dataMap.get("userId")!=null) {
						String uniqueId = String.valueOf(dataMap.get("userId"));
						returnMap.put("uniqueId", uniqueId);
						returnMap.putAll(dataMap);
					}
				}
				returnMap.put("msg", retmap.get("msg"));
				return returnMap;
			} else {
				returnMap.put("msg", "网络错误");
				logger.debug("request error, HTTP code:" + code);
				return returnMap;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,Object> returnMap = Maps.newHashMap();
		returnMap.put("msg", "网络错误");
		return returnMap;
	}
	
	public Map<String,Object> updateDocInfo(Map<String, Object> secretMap, String token, String userId) {
//		String token = (String) ContextUtils.getSession().getAttribute("token");
//		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		String postUrl = url+"/wx/updateUserInfo";
		try {
			HttpPut httpPut = new HttpPut(postUrl);
			String json = JsonUtil.mapToJson(secretMap);
			StringEntity se = new StringEntity(json, "UTF-8");
			httpPut.setEntity(se);
			httpPut.addHeader(HTTP.CONTENT_TYPE, "application/json");
			httpPut.addHeader("token", token);
			httpPut.addHeader("userId", userId);
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			
			Map<String,Object> returnMap = Maps.newHashMap();
			HttpResponse response = HttpUtils.getClient().execute(httpPut);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				logger.debug(result);
				Map<String,Object> retmap = JsonUtil.jsonToMap(result);
				
				String code1 = retmap.get("code").toString();
				if (code1.equals("0")) {
					Map<String,Object> dataMap = (Map<String,Object>)retmap.get("data");
					if (dataMap != null && dataMap.size() > 0) {
						if (dataMap.get("userId")!=null) {
							String uniqueId = String.valueOf(dataMap.get("userId"));
							returnMap.put("uniqueId", uniqueId);
							returnMap.putAll(dataMap);
						}
					}
				}else {
					returnMap.put("msg", retmap.get("msg"));
				}
				
				return returnMap;
			} else {
				returnMap.put("msg", "网络错误");
				logger.debug("request error, HTTP code:" + code);
				return returnMap;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,Object> returnMap = Maps.newHashMap();
		returnMap.put("msg", "网络错误");
		return returnMap;
	}
	
	/**
	 * @param userId 登录用户id
	 * @param token
	 * @param uId：筛查用户id
	 * @return
	 */
	public Map<String,Object> requestInfoByUniqueId(String uId, String token, String userId) {
//		String token = (String) ContextUtils.getSession().getAttribute("token");
//		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		String getUrl = url + "/wx/getUserInfo?userId=" + uId;
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			httpGet.addHeader("token", token);
			httpGet.addHeader("userId", userId);
			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				logger.debug("requestInfoByUniqueId--result=="+result);
				Map<String,Object> map = JsonUtil.jsonToMap(result);
				Integer retCode = (Integer)map.get("code");
				Map<String, Object> dataMap = (Map<String,Object>)map.get("data");
				if (retCode == 0 && dataMap != null) {
					dataMap.put("customerId", dataMap.get("id"));
					dataMap.put("mobile", dataMap.get("mobile"));
					dataMap.put("name", dataMap.get("name"));
					dataMap.put("gender", dataMap.get("gender"));
					dataMap.put("age", dataMap.get("age"));
					dataMap.put("birthday", dataMap.get("birthday"));
					dataMap.put("nationality", dataMap.get("nationality"));
					dataMap.put("householdRegistrationType", dataMap.get("householdRegistrationType"));
					dataMap.put("contactName", dataMap.get("contactName"));
					dataMap.put("contactMobile", dataMap.get("contactMobile"));
					dataMap.put("address", dataMap.get("address"));
					dataMap.put("remarks", dataMap.get("remarks"));
				}
				return dataMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public boolean verifyToken(String userId, String token) {
		String getUrl = url + "/dm/verifyToken?token=" + token + "&userId=" + userId;
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			httpGet.addHeader("token", token);
			httpGet.addHeader("userId", userId);
			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				Map<String,Object> map = JsonUtil.jsonToMap(result);
				Integer retCode = (Integer)map.get("code");
				Map<String, Object> dataMap = (Map<String,Object>)map.get("data");
				if (retCode == 0 && dataMap != null) {
					String valid = String.valueOf(dataMap.get("userExists"));
					if (valid.equals("1")) {
						return true;
					}
				}
				return false;
			} else {
				logger.debug("HTTP error code:"+code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public JSONObject saveStuffInfo(Map<String,Object> secretMap) {
		String postUrl = url+"/dm/saveStuffInfo";
		try {
			HttpPost httpPost = new HttpPost(postUrl);
			String json = JsonUtil.mapToJson(secretMap);
			StringEntity se = new StringEntity(json, "UTF-8");
			httpPost.setEntity(se);
			httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			
			HttpResponse response = HttpUtils.getClient().execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				logger.debug(result);
				return JSONObject.parseObject(result);
			} else {
				logger.debug("request error, HTTP code:" + code);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject login(String username, String password) {
		String getUrl = url + "/dm/login?username=" + username + "&password=" + password;
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				return JSONObject.parseObject(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public JSONObject updatePassword(Map<String, Object> params) {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		String postUrl = url+"/dm/updatePassword";
		try {
			HttpPut httpPut = new HttpPut(postUrl);
			String json = JsonUtil.mapToJson(params);
			StringEntity se = new StringEntity(json, "UTF-8");
			httpPut.setEntity(se);
			httpPut.addHeader(HTTP.CONTENT_TYPE, "application/json");
			httpPut.addHeader("token", token);
			httpPut.addHeader("userId", userId);
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			
			HttpResponse response = HttpUtils.getClient().execute(httpPut);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				logger.debug(result);
				return JSONObject.parseObject(result);
			} else {
				logger.debug("request error, HTTP code:" + code);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public JSONObject isValidUsername(String username) {
		String getUrl = url + "/dm/isValidUsername?username=" + username;
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				return JSONObject.parseObject(result);
			} else {
				logger.debug("HTTP error code:"+code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject delStuffInfoByUsername(String username) {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		String getUrl = url + "/dm/delStuffInfoByUsername?username=" + username;
		try {
			HttpDelete httpDelete = new HttpDelete(getUrl);
			
			httpDelete.addHeader(HTTP.CONTENT_TYPE, "application/json");
			httpDelete.addHeader("token", token);
			httpDelete.addHeader("userId", userId);
			
			HttpResponse response = HttpUtils.getClient().execute(httpDelete);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				return JSONObject.parseObject(result);
			} else {
				logger.debug("HTTP error code:"+code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public  Map<String, Object> applyToken() {
		String webSignature = JwtUtil.createJWT(Constant.JWT_ID, "healthcheck", Constant.JWT_TTL);
		String getUrl = url + "/dm/getTokenBySignature?webSignature=" + webSignature;
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				
				Map<String,Object> map = JsonUtil.jsonToMap(result);
				Integer retCode = (Integer)map.get("code");
				if (retCode == 0) {
					Map<String, Object> dataMap = (Map<String,Object>)map.get("data");
					return dataMap;
				} else {
					logger.debug("request error, retCode:" + retCode + ", msg: " + map.get("msg") + ", debugInfo:" + map.get("debugInfo"));
				}
				
			} else {
				logger.debug("HTTP error code:"+code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void pushUniqueIdAndDistrict(String uniqueId, String district) {
		String postUrl = doctorUrl;
		try {
			HttpPost httpPost = new HttpPost(postUrl);
			Map<String, Object> params = Maps.newHashMap();
			params.put("uniqueId", uniqueId);
			params.put("region", district);
			String json = JsonUtil.mapToJson(params);
			
			StringEntity se = new StringEntity(json, "UTF-8");
			httpPost.setEntity(se);
			httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			
			HttpResponse response = HttpUtils.getClient().execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				logger.debug(result);
				System.out.println("result:" + result);
			} else {
				logger.debug("request error, HTTP code:" + code);
				System.out.println("request error, HTTP code:" + code);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public  JSONObject applyTokenChms() {
		String webSignature = JwtUtil.createJWT(Constant.JWT_ID, subject, Constant.JWT_TTL);
		String getUrl = url + "/dm/getTokenBySignature?webSignature=" + webSignature;
		try {
			HttpGet httpGet = new HttpGet(getUrl);
			HttpResponse response = HttpUtils.getClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				JSONObject object = JSONObject.parseObject(result);
				/*Integer retCode = (Integer)map.get("code");
				if (retCode == 0) {
					Map<String, Object> dataMap = (Map<String,Object>)map.get("data");
					return dataMap;
				} else {
					logger.debug("request error, retCode:" + retCode + ", msg: " + map.get("msg") + ", debugInfo:" + map.get("debugInfo"));
				}*/
				return object;
				
			} else {
				logger.debug("HTTP error code:"+code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		AuthService auth = new AuthService();
		Map<String, Object> applyToken = auth.applyToken();
		System.out.println(applyToken);
//		auth.pushUniqueIdAndDistrict("100", "海州区");
	}
	
}
