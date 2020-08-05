package com.capitalbio.common.util;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;

public class OSSUtil {
	private static OSSClient client;
	private static String transOss = null;
	/**
	 * 获取阿里云OSS文件服务器连接
	 * @return
	 */
	public static OSSClient connSingleClient() {
		if (client != null) {
			return client;
		}
		
		String http = "http://";
		String ossEndpoint = getOssEndpoint();
		String accessId = PropertyUtils.getProperty("access_id");
		String accessKey = PropertyUtils.getProperty("access_key");
		//String bucketName = getBucketName();
		
		ClientConfiguration conf = new ClientConfiguration();
		// 设置OSSClient使用的最大连接数，默认1024
		conf.setMaxConnections(3000);
		// 设置失败请求重试次数，默认3次
		conf.setMaxErrorRetry(3);
		
		client = new OSSClient(http + ossEndpoint, accessId, accessKey, conf);
		/*boolean isExit = client.doesBucketExist(bucketName);
		if (!isExit) {
			client.createBucket(bucketName);
			//设置bucket的访问权限， Private权限
			client.setBucketAcl(bucketName, CannedAccessControlList.Private);
		}*/
		
		return client;
	}
	
	public static OSSClient connMulClient() {
		String http = "http://";
		String ossEndpoint = getOssEndpoint();
		String accessId = PropertyUtils.getProperty("access_id");
		String accessKey = PropertyUtils.getProperty("access_key");
		//String bucketName = getBucketName();
		
		ClientConfiguration conf = new ClientConfiguration();
		// 设置HTTP最大连接数为1024
		conf.setMaxConnections(1024);

		// 设置TCP连接超时为5000毫秒
		conf.setConnectionTimeout(5000);

		// 设置最大的重试次数为3
		conf.setMaxErrorRetry(3);

		// 设置Socket传输数据超时的时间为2000毫秒
		conf.setSocketTimeout(2000);
		
		OSSClient client = new OSSClient(http + ossEndpoint, accessId, accessKey, conf);
		
		return client;
	}
	
	public static OSSData ossData(String objectKey) {
		String ossEndpoint = getOssEndpoint();
		String bucketName = getBucketName();
		StringBuffer url = new StringBuffer();
		url.append("http://");
		url.append(bucketName).append(".").append(ossEndpoint).append("/");
		url.append(objectKey);
		
		OSSData ossData = new OSSData();
		ossData.setObjectKey(objectKey);
		ossData.setUrl(url.toString());
		
		return ossData;
	}
	
	public void closeClient() {
		if (client != null) {
			client.shutdown();
		}
	}
	public static String getBucketName() {
		return PropertyUtils.getProperty("bucketname");
	}
	
	public static String getOssEndpoint() {
		return PropertyUtils.getProperty("oss_endpoint");
	}
	
	public static boolean oss() {
		String filePosition = PropertyUtils.getProperty("filePosition");
		if ("oss".equals(filePosition)) {
			return true;
		}
		return false;
	}
//	public static boolean paramsIsExist() {
//		if (transOss == null) {
//			String bucketname = getBucketName();
//			String endpoint = getOssEndpoint();
//			String accessId = PropertyUtils.getProperty("access_id");
//			String accessKey = PropertyUtils.getProperty("access_key");
//			if (StringUtils.isEmpty(bucketname) || StringUtils.isEmpty(endpoint)
//					|| StringUtils.isEmpty(accessId) || StringUtils.isEmpty(accessKey)) {
//				transOss = "false";
//			} else {
//				transOss = "true";
//			}
//		}
//		if ("true".equals(transOss)) {
//			return true;
//		}
//		return false;
//	}
	
	/**
	 * 将oss相关字段追加_oss标识
	 * @param fileName
	 * @return
	 */
//	public static String ossFileName(String fileName) {
//		if (StringUtils.isEmpty(fileName)) {
//			return "";
//		}
//		
//		return fileName + "_oss";
//	}
	
	public static String getContentType(String fileName) {
	    if (fileName.endsWith(".bmp")) {
	      return "image/bmp";
	    }
	    if (fileName.endsWith(".gif")) {
	      return "image/gif";
	    }
	    if (fileName.endsWith(".jpeg") ||
	        fileName.endsWith(".jpg")) {
	      return "image/jpeg";
	    }
	    if (fileName.endsWith(".png")) {
		      return "image/png";
		}
	    if (fileName.endsWith(".html")) {
	      return "text/html";
	    }
	    if (fileName.endsWith(".txt")) {
	      return "text/plain";
	    }
	    if (fileName.endsWith(".vsd")) {
	      return "application/vnd.visio";
	    }
	    if (fileName.endsWith(".pptx") ||
	        fileName.endsWith(".ppt")) {
	      return "application/vnd.ms-powerpoint";
	    }
	    if (fileName.endsWith(".docx") ||
	        fileName.endsWith(".doc")) {
	      return "application/msword";
	    }
	    if (fileName.endsWith(".xml")) {
	      return "text/xml";
	    } else {
			return "application/octet-stream";
		}
	  }
}
