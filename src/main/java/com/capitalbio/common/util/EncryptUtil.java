package com.capitalbio.common.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.capitalbio.auth.util.Constant;

public class EncryptUtil {
	public static Logger logger = Logger.getLogger(EncryptUtil.class);

	/**
	 * 将byte[]转为各种进制的字符串
	 * @param bytes byte[]
	 * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
	 * @return 转换后的字符串
	 */
	public static String binary(byte[] bytes, int radix){
		return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
	}
	/**
	 * base 64 encode
	 * @param bytes 待编码的byte[]
	 * @return 编码后的base 64 code
	 */
	public static String base64Encode(byte[] bytes){
//		return Base64.getEncoder().encodeToString(bytes);
//		return Base64.encodeBase64String(bytes);
		return Base64.encodeBase64URLSafeString(bytes);
	}
	
	/**
	 * base 64 decode
	 * @param base64Code 待解码的base 64 code
	 * @return 解码后的byte[]
	 * @throws Exception
	 */
	public static byte[] base64Decode(String base64Code) throws Exception{
		return StringUtils.isEmpty(base64Code) ? null : Base64.decodeBase64(base64Code);
	}
	/**
	 * AES加密
	 * @param content 待加密的内容
	 * @param encryptKey 加密密钥
	 * @return 加密后的byte[]
	 * @throws Exception
	 */
	public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(encryptKey.getBytes());
        kgen.init(128, random);

		Cipher cipher = Cipher.getInstance("AES");
//		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Arrays.copyOf(encryptKey.getBytes(), 16), "AES"));
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
		
		return cipher.doFinal(content.getBytes("utf-8"));
	}
	
	/**
	 * AES解密
	 * @param encryptBytes 待解密的byte[]
	 * @param decryptKey 解密密钥
	 * @return 解密后的String
	 * @throws Exception
	 */
	public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(decryptKey.getBytes());
        kgen.init(128, random);
        
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
		byte[] decryptBytes = cipher.doFinal(encryptBytes);
		
		return new String(decryptBytes);
	}

	
	/**
	 * AES加密为base 64 code
	 * @param content 待加密的内容
	 * @param encryptKey 加密密钥
	 * @return 加密后的base 64 code
	 * @throws Exception
	 */
	public static String aesEncrypt(String content, String encryptKey) throws Exception {
		return base64Encode(aesEncryptToBytes(content, encryptKey));
	}
	
	public static String encryptByKey(String content) {
		try {
//			String encryptKey = "123";
			String encryptKey = Constant.SECRET_KEY;
			
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			byte[] keyBytes = Arrays.copyOf(encryptKey.getBytes(), 16);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"));
			
			byte[] encryptBytes = cipher.doFinal(content.getBytes("utf-8"));
			
			String encryptValue = base64Encode(encryptBytes);
			return encryptValue;
		} catch (Exception e) {
			logger.debug("encrypt error", e);
		}
		return "";
		
	}

	public static String encrypt(String content) {
		try {
//			String encryptKey = "123";
			String encryptKey = Constant.SECRET_KEY;
			String encryptValue = base64Encode(aesEncryptToBytes(content, encryptKey));
			return encryptValue;
		} catch (Exception e) {
			logger.debug("encrypt error", e);
		}
		return "";
	}
	


	
	public static String decryptByKey(String content) {
		try {
//			String decryptKey = "123";
			String decryptKey = Constant.SECRET_KEY;
			byte[] encryptBytes = base64Decode(content);
			
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Arrays.copyOf(decryptKey.getBytes(), 16), "AES"));
			byte[] decryptBytes = cipher.doFinal(encryptBytes);
			
			String decryptValue = new String(decryptBytes);
			return decryptValue;
		} catch (Exception e) {
			logger.debug("decrypt error", e);
		}
		return null;
		
	}
	
	public static String decrypt(String content) {
		try {
//			String decryptKey = "123";
			String decryptKey = Constant.SECRET_KEY;
			String decryptValue = aesDecryptByBytes(base64Decode(content), decryptKey);
			return decryptValue;
		} catch (Exception e) {
			logger.debug("decrypt error", e);
		}
		return null;
	}
	
	
	public static void main(String[] args) throws Exception {
//		System.out.println(EncryptUtil.decrypt("a07ywje3hxITA-UyMskUgkX5WKKBQ5FIGZRIN8kdwcg"));
//		System.out.println(EncryptUtil.decryptByKey("a07ywje3hxITA-UyMskUgkX5WKKBQ5FIGZRIN8kdwcg"));
		System.out.println(EncryptUtil.encrypt("18321993715"));
		System.out.println(EncryptUtil.encryptByKey("18321993715"));
//		System.out.println(EncryptUtil.encrypt("13547838287"));
//		System.out.println(EncryptUtil.decrypt("JO8PKlUAshUw-hJJAFyoIg"));
//		String key = "123";
//		String src = "110110198601010165";
//		String enc = EncryptUtil.encryptAESECB(src, key);
//		System.out.println(enc);
	
//		String enc = EncryptUtil.encryptByKey("120225198412070032");
//		String dec = EncryptUtil.decryptByKey(enc);
//		long ti = (new Date()).getTime();
//		String token = MD5Util.MD5Encode(enc+ti+"123");
//		System.out.println(enc);
//		System.out.println(dec);
		
		
//		System.out.println(EncryptUtil.decryptByKey("XYC0WT138QLSNaRLz/cvanKKFWM26xuoWfq3nGKWjj4="));
//		
//		System.out.println(MD5Util.MD5Encode("XYC0WT138QLSNaRLz/cvanKKFWM26xuoWfq3nGKWjj4="+1504159094+Constant.SECRET_KEY));
		
//		System.out.println(ti);
//		System.out.println(token);
//		System.out.println(EncryptUtil.decrypt("TFRbmnl4NZOzSIF4ybyIkCYW1vKgSPzxZWXyMarrZRU="));
//		String content = "110108198711037116";
//		System.out.println("加密前：" + content);
//
//		String key = Constant.SECRET_KEY;
//		System.out.println("加密密钥和解密密钥：" + key);
//		
//		String encrypt = aesEncrypt(content, key);
//		System.out.println("加密后：" + encrypt);
//		
//		String decrypt = aesDecrypt("5qh8jml2IIxe9SPlQSnD3Ss8jUB57GFlK9/2YqcebTA", key);
//		if(decrypt==null){
//			System.out.println("解密失败" );
//		}else{
//			System.out.println("解密后：" + decrypt);
//
//		}

	}


}

