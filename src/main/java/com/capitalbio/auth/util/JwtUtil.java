package com.capitalbio.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.capitalbio.common.util.DateUtils;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.common.util.redis.DataDictUtil;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;

public class JwtUtil {
	private static Logger logger = Logger.getLogger(JwtUtil.class);
	
	JedisTemplate template = RedisUtils.getTemplate();
	
	public static SecretKey generalKey(){
		try {
			String stringKey = Constant.JWT_SECRET;
			byte[] encodedKey = stringKey.getBytes();
			SecretKeySpec key = new SecretKeySpec(encodedKey,  "AES");
		    return key;
		} catch (Exception e) {
			
		}
		return null;
	}

	/**
	 * 创建jwt
	 * @param id
	 * @param subject
	 * @param ttlMillis
	 * @return
	 * @throws Exception
	 */
	public static String createJWT(String id, String subject, long ttlMillis) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		SecretKey key = generalKey();
		JwtBuilder builder = Jwts.builder()
			.setId(id)
			.setIssuedAt(now)
			.setSubject(subject)
		    .signWith(signatureAlgorithm, key);
		if (ttlMillis >= 0) {
		    long expMillis = nowMillis + ttlMillis;
		    Date exp = new Date(expMillis);
		    builder.setExpiration(exp);
		}
		return builder.compact();
	}
	
	/**
	 * 解密jwt
	 * @param jwt
	 * @return
	 * @throws Exception
	 */
	public static Claims parseJWT(String jwt) throws Exception{
		SecretKey key = generalKey();
		Claims claims = Jwts.parser()         
		   .setSigningKey(key)
		   .parseClaimsJws(jwt).getBody();
		return claims;
	}
	
	/**
	 * 验证jwt的有效性，此处使用的subject是用户名
	 * 
	 * @param jwt
	 * @param username
	 * @return
	 */
	public static boolean valid(String jwt, String username) {
		try {
			Claims claims = parseJWT(jwt);
			String parseUsername = claims.getSubject();
			if (parseUsername.equals(username)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e);
		}
		return false;
	} 
	
	/**
	 * 验证请求中的token有效性，返回用户名
	 * @param request
	 * @return
	 */
	public static String checkRequest(HttpServletRequest request) {
//		return "temp";
		try {
			String jwt = request.getParameter("token");
			Claims claims = parseJWT(jwt);
			String parseUsername = claims.getSubject();
			return parseUsername;
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e);
		}
		return null;
	}

	public static String checkRequest(String token) {
		try {
			Claims claims = parseJWT(token);
			String parseUsername = claims.getSubject();
			return parseUsername;
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e);
		}
		return null;
	}

	/**
     * token的验证
     * 验证token有效性质
     * @param token 
     * @param uniqueId 加密的身份证
     * @return  
     * @throws Exception
     */
    public static boolean validTime(String token,String uniqueId) throws Exception{
    	try {
    		Claims claims = parseJWT(token);
    		String parseUsername = claims.getSubject();
    		if (!parseUsername.equals(uniqueId)) {
    			return false;
    		}
    		Long time=claims.getExpiration().getTime();
    		if(time-System.currentTimeMillis()>Constant.VALDAT_JWT_TTL)   {
    			//超时
    			return false;
    		}
    		return false;
    	} catch (Exception e) {
    		e.printStackTrace();
    		logger.debug(e);
    	}
    	return false;
    }
    

    public static String getJwtSubject(String webSignature) throws Exception {
        return parseJWT(webSignature).getSubject();
    }
    
    public static void main(String[] args) throws Exception {
    	String jwt1 = JwtUtil.createJWT(Constant.JWT_ID, "xxx", Constant.JWT_TTL);
    	String jwt = "eyIyMDE5MDEyMzE2MjMxODQwNyI6ImhkdG0iLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJiaW8iOiJmdXhpbiIsImlzcyI6ImF1dGgwIiwicGx0bSI6IjIwMTkwMTIzMTYyMzE4NDA3In0.sefVV0ZwX6Is5eB8k7sPEtYX_x83L4DjjaZlN0grUOc";
//    	String jwt = "eyIyMDE5MDEyMzE1MDAwMDQ2OCI6ImhkdG0iLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJiaW8iOiJmdXhpbiIsImlzcyI6ImF1dGgwIiwicGx0bSI6IjIwMTkwMTIzMTUwMDAwNDY4In0.b09yK6wxNOS7QteJxIpjWJh2BvQe1qqEVWqiGyRfpS8";
    	System.out.println(jwt1);
    	Claims claims = JwtUtil.parseJWT(jwt);
    	System.out.println(claims);
    }
}